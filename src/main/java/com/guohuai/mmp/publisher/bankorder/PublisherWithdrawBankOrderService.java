package com.guohuai.mmp.publisher.bankorder;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.cache.service.CachePlatformService;
import com.guohuai.component.util.DateUtil;
import com.guohuai.mmp.investor.bankorder.BankOrderRep;
import com.guohuai.mmp.investor.bankorder.InvestorBankOrderEntity;
import com.guohuai.mmp.investor.bankorder.WithdrawLongBankOrderReq;
import com.guohuai.mmp.investor.bankorder.WithdrawShortBankOrderReq;
import com.guohuai.mmp.platform.SeqGeneratorService;
import com.guohuai.mmp.platform.baseaccount.statistics.PlatformStatisticsService;
import com.guohuai.mmp.platform.finance.result.PlatformFinanceCompareDataResultNewService;
import com.guohuai.mmp.platform.payment.OrderNotifyReq;
import com.guohuai.mmp.platform.payment.PayParam;
import com.guohuai.mmp.platform.payment.Payment;
import com.guohuai.mmp.platform.payment.WithdrawRequest;
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
public class PublisherWithdrawBankOrderService {
	
	@Autowired
	private PublisherBaseAccountService publisherBaseAccountService;
	@Autowired
	private PublisherBankOrderService publisherBankOrderService;
	@Autowired
	private PublisherCashFlowService publisherCashFlowService;
	@Autowired
	private PublisherStatisticsService publisherStatisticsService;
	@Autowired
	private PlatformStatisticsService platformStatisticsService;
	@Autowired
	private CachePlatformService cachePlatformService;
	@Autowired
	private Payment paymentServiceImpl;
	@Autowired
	private SeqGeneratorService seqGeneratorService;
	@Autowired
	private PlatformFinanceCompareDataResultNewService platformFinanceCompareDataResultNewService;
	@Autowired
	private PayLogService payLogService;
	
	/**
	 *  体现
	 */
	@Transactional
	public BaseResp withdraw(BankOrderWithdrawReq bankOrderReq, String investorOid) {
		BankOrderRep bankOrderRep = new BankOrderRep();
		
		/** 判断<<投资人-基本账户>>.<<余额>>是否足够提现 */
		PublisherBaseAccountEntity baseAccount = publisherBaseAccountService.findByLoginAcc(investorOid);
		publisherBaseAccountService.balanceEnough(bankOrderReq.getOrderAmount(), baseAccount.getOid());
		
		PublisherBankOrderEntity bankOrder = publisherBankOrderService.createWithdrawBankOrder(bankOrderReq, baseAccount);
		
		/** 手续费/月提现次数  */
	
		/** 平台单日提现限额 */
		this.cachePlatformService.isWithdrawDayLimit(bankOrder.getOrderAmount());
		
		WithdrawRequest ireq = new WithdrawRequest();
		ireq.setMemberId(bankOrder.getPublisherBaseAccount().getMemberId());
		ireq.setOrderCode(bankOrder.getOrderCode());
		ireq.setIPayNo(seqGeneratorService.getSeqNo(CodeConstants.PAYMENT_debitWithdrawPayNo));
//		if (feeRep.getPayer().equals(InvestorBankOrderEntity.BANKORDER_feePayer_user)) {
//			ireq.setOrderAmount(bankOrder.getOrderAmount().subtract(feeRep.getFee()));
//			ireq.setFee(feeRep.getFee());
//		} else {
//			ireq.setOrderAmount(bankOrder.getOrderAmount());
//			ireq.setFee(BigDecimal.ZERO);
//		}
		ireq.setOrderAmount(bankOrder.getOrderAmount());
		ireq.setOrderTime(DateUtil.format(bankOrder.getOrderTime(), DateUtil.fullDatePattern));
		ireq.setUserType(PayParam.UserType.SPV.toString());

		BaseResp irep = this.paymentServiceImpl.withdrawPay(ireq);
		
		if (0 != irep.getErrorCode()) {
			bankOrderRep.setErrorCode(irep.getErrorCode());
			bankOrderRep.setErrorMessage(irep.getErrorMessage());
			bankOrder.setOrderStatus(PublisherBankOrderEntity.BANKORDER_orderStatus_submitFailed);
			bankOrder.setCompleteTime(DateUtil.getSqlCurrentDate());
			publisherBankOrderService.saveEntity(bankOrder);
		} else {
			this.publisherBaseAccountService.updateBalance(bankOrder.getPublisherBaseAccount());
			bankOrder.setOrderStatus(PublisherBankOrderEntity.BANKORDER_orderStatus_toPay);
			publisherBankOrderService.saveEntity(bankOrder);
		}
		
//		bankOrder.setFee(feeRep.getFee());
//		bankOrder.setFeePayer(feeRep.getPayer());
		
		bankOrderRep.setBankOrderOid(bankOrder.getOid());
		

		return bankOrderRep;
	}
	
	
	/**
	 * 提现短款
	 */
	public BaseResp withdrawShort(WithdrawShortBankOrderReq ireq) {
		platformFinanceCompareDataResultNewService.updateDealStatusDealingByOid(ireq.getCrOid());
		
		PublisherBankOrderEntity bankOrder = this.publisherBankOrderService.findByOrderCode(ireq.getOrderCode());
		bankOrder.setOrderStatus(InvestorBankOrderEntity.BANKORDER_orderStatus_abandoned);
		publisherBankOrderService.saveEntity(bankOrder);
		
		platformFinanceCompareDataResultNewService.updateDealStatusDealtByOid(ireq.getCrOid());
		return new BaseResp();
	}
	

	/**
	 * 提现长款
	 */
	public BaseResp withdrawLong(WithdrawLongBankOrderReq ireq) {
		platformFinanceCompareDataResultNewService.updateDealStatusDealingByOid(ireq.getCrOid());
		
		/** 记录业务订单 */
		PublisherBankOrderEntity bankOrder = this.publisherBankOrderService.createWithdrawLongBankOrder(ireq);
		if (InvestorBankOrderEntity.BANKORDER_orderStatus_paySuccess.equals(ireq.getOrderStatus())) {
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
	

	public boolean withdrawCallback(OrderNotifyReq ireq) {
		PublisherBankOrderEntity bankOrder = publisherBankOrderService.findByOrderCode(ireq.getOrderCode());
		if (PayParam.ReturnCode.RC0000.toString().equals(ireq.getReturnCode())) {
			this.notifyOk(bankOrder);
		} else {
			this.notifyFail(bankOrder);
		}
		return true;
	}
	
	private void notifyOk(PublisherBankOrderEntity bankOrder) {
		/** 创建<<发行人-资金变动明细>> */
		this.publisherCashFlowService.createCashFlow(bankOrder);
		/** 更新<<发行人-基本账户>>.<<余额>> */
		this.publisherBaseAccountService.updateBalance(bankOrder.getPublisherBaseAccount());
		/** 更新<<发行人-统计>>.<<累计提现总额>> */
//		this.publisherStatisticsService.updateStatistics4Withdraw(bankOrder);
		/** 更新<<平台-统计>>.<<累计交易总额>><<发行人提现总额>> */
//		this.platformStatisticsService.updateStatistics4PublisherWithdraw(bankOrder.getOrderAmount());
		
		bankOrder.setOrderStatus(PublisherBankOrderEntity.BANKORDER_orderStatus_done);
		bankOrder.setCompleteTime(DateUtil.getSqlCurrentDate());
		publisherBankOrderService.saveEntity(bankOrder);
		
	}


	public boolean notifyFail(PublisherBankOrderEntity bankOrder) {
		
		bankOrder.setOrderStatus(PublisherBankOrderEntity.BANKORDER_orderStatus_payFailed);
		bankOrder.setCompleteTime(DateUtil.getSqlCurrentDate());
		publisherBankOrderService.saveEntity(bankOrder);
		/** 更新<<发行人-基本账户>>.<<余额>> */
		this.publisherBaseAccountService.updateBalance(bankOrder.getPublisherBaseAccount());
		return true;
	}
	
}
