package com.guohuai.mmp.publisher.bankorder;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.DateUtil;
import com.guohuai.mmp.investor.bankorder.DepositLongBankOrderReq;
import com.guohuai.mmp.investor.bankorder.DepositShortBankOrderReq;
import com.guohuai.mmp.investor.bankorder.InvestorBankOrderEntity;
import com.guohuai.mmp.investor.bankorder.apply.InvestorDepositApplyService;
import com.guohuai.mmp.platform.SeqGeneratorService;
import com.guohuai.mmp.platform.baseaccount.statistics.PlatformStatisticsService;
import com.guohuai.mmp.platform.finance.result.PlatformFinanceCompareDataResultNewService;
import com.guohuai.mmp.platform.payment.DepositOrderQueryRequest;
import com.guohuai.mmp.platform.payment.DepositOrderQueryResponse;
import com.guohuai.mmp.platform.payment.DepositRequest;
import com.guohuai.mmp.platform.payment.OrderNotifyReq;
import com.guohuai.mmp.platform.payment.PayParam;
import com.guohuai.mmp.platform.payment.Payment;
import com.guohuai.mmp.platform.payment.log.PayInterface;
import com.guohuai.mmp.platform.payment.log.PayLogEntity;
import com.guohuai.mmp.platform.payment.log.PayLogReq;
import com.guohuai.mmp.platform.payment.log.PayLogService;
import com.guohuai.mmp.publisher.baseaccount.PublisherBaseAccountEntity;
import com.guohuai.mmp.publisher.baseaccount.PublisherBaseAccountService;
import com.guohuai.mmp.publisher.baseaccount.statistics.PublisherStatisticsService;
import com.guohuai.mmp.publisher.cashflow.PublisherCashFlowService;
import com.guohuai.mmp.sys.CodeConstants;

@Service
@Transactional
public class PublisherDepositBankOrderService {
	
	@Autowired
	private Payment paymentServiceImpl;
	@Autowired
	private PublisherBankOrderService publisherBankOrderService;
	@Autowired
	private PublisherBaseAccountService publisherBaseAccountService;
	@Autowired
	private InvestorDepositApplyService investorDepositApplyService;
	@Autowired
	private PublisherCashFlowService publisherCashFlowService;
	@Autowired
	private PublisherStatisticsService publisherStatisticsService;
	@Autowired
	private PlatformStatisticsService platformStatisticsService;
	@Autowired
	private SeqGeneratorService seqGeneratorService;
	@Autowired
	private PlatformFinanceCompareDataResultNewService platformFinanceCompareDataResultNewService;
	@Autowired
	private PayLogService payLogService;

	/**
	 *  充值--发行人
	 */
	@Transactional
	public BaseResp deposit(BankOrderReq bankOrderReq, String investorOid) {
		
		investorDepositApplyService.findByPayNoAndOrderAmountAndInvestorOid(bankOrderReq.getPayNo(),
				bankOrderReq.getOrderAmount(), investorOid);
		
		BankOrderRep bankOrderRep = new BankOrderRep();
		
		PublisherBaseAccountEntity baseAccount = publisherBaseAccountService.findByLoginAcc(investorOid);
		/** 创建订单 */
		PublisherBankOrderEntity bankOrder = publisherBankOrderService.createDepostBankOrder(bankOrderReq, baseAccount);
		
		DepositRequest ireq = new DepositRequest();
		ireq.setMemberId(bankOrder.getPublisherBaseAccount().getMemberId());
		ireq.setOrderCode(bankOrder.getOrderCode());
		ireq.setIPayNo(seqGeneratorService.getSeqNo(CodeConstants.PAYMENT_debitDepositPayNo));
		ireq.setPayNo(bankOrderReq.getPayNo());
		ireq.setOrderAmount(bankOrder.getOrderAmount());
		ireq.setOrderTime(DateUtil.format(bankOrder.getOrderTime(), DateUtil.fullDatePattern));
		ireq.setSmsCode(bankOrderReq.getSmsCode());
		ireq.setUserType(PayParam.UserType.SPV.toString());
		
		BaseResp baseRep = this.paymentServiceImpl.depositPay(ireq);
		
		if (0 != baseRep.getErrorCode()) {
			bankOrderRep.setErrorCode(baseRep.getErrorCode());
			bankOrderRep.setErrorMessage(baseRep.getErrorMessage());
			bankOrder.setOrderStatus(PublisherBankOrderEntity.BANKORDER_orderStatus_submitFailed);
			bankOrder.setCompleteTime(DateUtil.getSqlCurrentDate());
			publisherBankOrderService.saveEntity(bankOrder);
		} else {
			bankOrder.setOrderStatus(PublisherBankOrderEntity.BANKORDER_orderStatus_toPay);
			publisherBankOrderService.saveEntity(bankOrder);
		}
		bankOrderRep.setBankOrderOid(bankOrder.getOid());
		
		return bankOrderRep;
	}
	
	/**
	 * 充值短款
	 */
	public BaseResp depositShort(DepositShortBankOrderReq ireq) {
		platformFinanceCompareDataResultNewService.updateDealStatusDealingByOid(ireq.getCrOid());
		
		PublisherBankOrderEntity bankOrder = this.publisherBankOrderService.findByOrderCode(ireq.getOrderCode());
		bankOrder.setOrderStatus(PublisherBankOrderEntity.BANKORDER_orderStatus_abandoned);
		publisherBankOrderService.saveEntity(bankOrder);
		
		platformFinanceCompareDataResultNewService.updateDealStatusDealtByOid(ireq.getCrOid());
		return new BaseResp();
	}
	

	/**
	 * 充值长款
	 */
	public BaseResp depositLong(DepositLongBankOrderReq ireq) {
		platformFinanceCompareDataResultNewService.updateDealStatusDealingByOid(ireq.getCrOid());
		
		/** 记录业务订单 */
		PublisherBankOrderEntity bankOrder = this.publisherBankOrderService.createDepositLongBankOrder(ireq);
		if (PublisherBankOrderEntity.BANKORDER_orderStatus_done.equals(ireq.getOrderStatus())) {
			notifyOk(bankOrder);
		} else if (InvestorBankOrderEntity.BANKORDER_orderStatus_payFailed.equals(ireq.getOrderStatus())) {
			notifyFail(bankOrder);
		}
		
		/** 记录支付订单 */
		PayLogReq logReq = new PayLogReq();
		logReq.setErrorCode(0);
		logReq.setErrorMessage(ireq.getMsg());
		logReq.setInterfaceName(PayInterface.tradeCallback.getInterfaceName());
		logReq.setOrderCode(bankOrder.getOrderCode());
		logReq.setIPayNo(ireq.getOrderCode()); // 外部订单号对应 内部支付流水号
		logReq.setSendedTimes(1);
		logReq.setContent(JSONObject.toJSONString(ireq));
		logReq.setHandleType(PayLogEntity.PAY_handleType_notify);
		this.payLogService.createEntity(logReq);
		
		logReq = new PayLogReq();
		logReq.setErrorCode(0);
		logReq.setErrorMessage(ireq.getMsg());
		logReq.setInterfaceName(PayInterface.pay.getInterfaceName());
		logReq.setSendedTimes(1);
		logReq.setContent(JSONObject.toJSONString(ireq));
		logReq.setOrderCode(bankOrder.getOrderCode());
		logReq.setIPayNo(ireq.getOrderCode());
		logReq.setHandleType(PayLogEntity.PAY_handleType_applyCall);
		this.payLogService.createEntity(logReq);
		
		platformFinanceCompareDataResultNewService.updateDealStatusDealtByOid(ireq.getCrOid());
		
		return new BaseResp();
	}
	
	
	public boolean depositCallback(OrderNotifyReq ireq) {
		PublisherBankOrderEntity bankOrder = publisherBankOrderService.findByOrderCode(ireq.getOrderCode());
		
		if (PayParam.ReturnCode.RC0000.toString().equals(ireq.getReturnCode())) {
			notifyOk(bankOrder);
		} else {
			notifyFail(bankOrder);
		}
		
		return true;
	}
	
	private void notifyFail(PublisherBankOrderEntity bankOrder) {
		
		bankOrder.setOrderStatus(PublisherBankOrderEntity.BANKORDER_orderStatus_payFailed);
		bankOrder.setCompleteTime(DateUtil.getSqlCurrentDate());
		publisherBankOrderService.saveEntity(bankOrder);
	}

	private void notifyOk(PublisherBankOrderEntity bankOrder) {
		/** 创建<<发行人-资金变动明细>> */
		publisherCashFlowService.createCashFlow(bankOrder);
		/** 更新<<发行人-基本账户>>.<<账户余额>> */
		this.publisherBaseAccountService.updateBalance(bankOrder.getPublisherBaseAccount());
		/** 更新<<发行人-统计>>.<<累计充值总额>> */
//		publisherStatisticsService.updateStatistics4Deposit(bankOrder);
		/** 更新<<平台-统计>>.<<累计交易总额>><<发行人充值总额>> */
//		this.platformStatisticsService.updateStatistics4PublisherDeposit(bankOrder.getOrderAmount());
		bankOrder.setOrderStatus(PublisherBankOrderEntity.BANKORDER_orderStatus_done);
		bankOrder.setCompleteTime(DateUtil.getSqlCurrentDate());
		publisherBankOrderService.saveEntity(bankOrder);
	}

	/**
	 * 充值冲正
	 */
	public BaseResp correctDepositBankOrder(String orderCode) {
		BaseResp rep = new BaseResp();
		PublisherBankOrderEntity bankOrder = this.publisherBankOrderService.findByOrderCode(orderCode);
		if (!PublisherBankOrderEntity.BANKORDER_orderStatus_payFailed.equals(bankOrder.getOrderStatus())) {
			throw new AMPException("订单非支付失败，不能修正充值单");
		}
		PayLogEntity log = this.payLogService.getSuccessPayApllyByOrderCode(bankOrder.getOrderCode());
		
		DepositOrderQueryRequest ireq = new DepositOrderQueryRequest();
		ireq.setInvestorOid(bankOrder.getPublisherBaseAccount().getOid());
		ireq.setIPayNo(log.getIPayNo());
		ireq.setOrderType(PayParam.OrderType.DEPOSIT.toString());
		DepositOrderQueryResponse irep = this.paymentServiceImpl.depositConfirm(ireq);
		if (irep.getOrderStatus().equals(InvestorBankOrderEntity.BANKORDER_orderStatus_paySuccess)) {
			this.notifyOk(bankOrder);
		} else {
			rep.setErrorCode(-1);
			rep.setErrorMessage("状态支付失败");
		}
		return rep;
	}
	
	
	
}
