package com.guohuai.mmp.investor.bankorder;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.guohuai.basic.component.exception.GHException;
import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.DateUtil;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountService;
import com.guohuai.mmp.investor.baseaccount.statistics.InvestorStatisticsService;
import com.guohuai.mmp.investor.cashflow.InvestorCashFlowService;
import com.guohuai.mmp.investor.tradeorder.coupon.TradeOrderCouponEntity;
import com.guohuai.mmp.investor.tradeorder.coupon.TradeOrderCouponService;
import com.guohuai.mmp.jiajiacai.wishplan.plan.service.PlanMonthScheduleService;
import com.guohuai.mmp.platform.SeqGeneratorService;
import com.guohuai.mmp.platform.accment.AccParam;
import com.guohuai.mmp.platform.baseaccount.statistics.PlatformStatisticsService;
import com.guohuai.mmp.platform.finance.result.PlatformFinanceCompareDataResultNewService;
import com.guohuai.mmp.platform.msgment.MailService;
import com.guohuai.mmp.platform.msgment.MsgService;
import com.guohuai.mmp.platform.msgment.RechargeSuccessMailReq;
import com.guohuai.mmp.platform.msgment.RechargeSuccessMsgReq;
import com.guohuai.mmp.platform.payment.ApiDepositRequest;
import com.guohuai.mmp.platform.payment.DepositOrderQueryRequest;
import com.guohuai.mmp.platform.payment.DepositOrderQueryResponse;
import com.guohuai.mmp.platform.payment.DepositRequest;
import com.guohuai.mmp.platform.payment.DepositbfRequest;
import com.guohuai.mmp.platform.payment.OrderNotifyReq;
import com.guohuai.mmp.platform.payment.PayHtmlRep;
import com.guohuai.mmp.platform.payment.PayParam;
import com.guohuai.mmp.platform.payment.Payment;
import com.guohuai.mmp.platform.payment.log.PayInterface;
import com.guohuai.mmp.platform.payment.log.PayLogEntity;
import com.guohuai.mmp.platform.payment.log.PayLogReq;
import com.guohuai.mmp.platform.payment.log.PayLogService;
import com.guohuai.mmp.platform.tulip.TulipService;
import com.guohuai.mmp.sys.CodeConstants;
import com.guohuai.tulip.platform.facade.obj.MyCouponRep;

import lombok.extern.slf4j.Slf4j;


@Service
@Transactional
@Slf4j
public class InvestorDepositBankOrderService {
	
	@Autowired
	private InvestorBaseAccountService investorBaseAccountService;
	@Autowired
	private InvestorCashFlowService investorCashFlowService;
	@Autowired
	private InvestorStatisticsService investorStatisticsService;
	@Autowired
	private InvestorBankOrderDao investorBankOrderDao;
	@Autowired
	private Payment paymentServiceImpl;
	@Autowired
	private PlatformStatisticsService platformStatisticsService;
	@Autowired
	private MsgService msgService;
	@Autowired
	private MailService mailService;
	@Autowired
	private InvestorBankOrderService investorBankOrderService;
	@Autowired
	private SeqGeneratorService seqGeneratorService;
	@Autowired
	private TulipService tulipeService;
	@Autowired
	private PayLogService payLogService;
	@Autowired
	private PlatformFinanceCompareDataResultNewService platformFinanceCompareDataResultNewService;
	@Autowired
	private TradeOrderCouponService tradeOrderCouponService;
	
	@Autowired
	private PlanMonthScheduleService planMothService;
	/**
	 *  充值--投资人
	 */
	@Transactional(value = TxType.REQUIRES_NEW)
	public BankOrderRep deposit(DepositBankOrderReq bankOrderReq, String orderCode) {
		BankOrderRep bankOrderRep = new BankOrderRep();
		/** 创建订单 */
		InvestorBankOrderEntity bankOrder = investorBankOrderService.findByOrderCodeAndOrderStatusAndOrderType(orderCode,
				InvestorBankOrderEntity.BANKORDER_orderStatus_submitted, InvestorBankOrderEntity.BANKORDER_orderType_deposit);
	
		DepositRequest ireq = new DepositRequest();
		ireq.setMemberId(bankOrder.getInvestorBaseAccount().getMemberId());
		ireq.setOrderCode(bankOrder.getOrderCode());
		ireq.setIPayNo(bankOrder.getOrderCode());
		//ireq.setIPayNo(seqGeneratorService.getSeqNo(CodeConstants.PAYMENT_depositPayNo));
		ireq.setPayNo(bankOrderReq.getPayNo());
		ireq.setOrderAmount(bankOrder.getOrderAmount());
		ireq.setOrderTime(DateUtil.format(bankOrder.getOrderTime(), DateUtil.fullDatePattern));
		ireq.setSmsCode(bankOrderReq.getSmsCode());
		ireq.setUserType(PayParam.UserType.INVESTOR.toString());
		
		BaseResp baseRep = this.paymentServiceImpl.depositPay(ireq);
		
		if (0 != baseRep.getErrorCode()) {
			//throw new AMPException(baseRep.getErrorMessage());
			bankOrderRep.setErrorCode(-1);
			bankOrderRep.setErrorMessage(baseRep.getErrorMessage());
		}
		bankOrderRep.setBankOrderOid(bankOrder.getOid());
		bankOrderRep.setOrderStatus(InvestorBankOrderEntity.BANKORDER_orderStatus_toPay);
		
		return bankOrderRep;
	}
	
	
	/**
	 *  充值--投资人
	 */
	@Transactional(value = TxType.REQUIRES_NEW)
	public BankOrderRep depositbf(DepositBankOrderbfReq bankOrderReq, String orderCode) {
		BankOrderRep bankOrderRep = new BankOrderRep();
		/** 创建订单 */
		InvestorBankOrderEntity bankOrder = investorBankOrderService.findByOrderCodeAndOrderStatusAndOrderType(orderCode,
				InvestorBankOrderEntity.BANKORDER_orderStatus_submitted, InvestorBankOrderEntity.BANKORDER_orderType_deposit);
	
		DepositbfRequest ireq = new DepositbfRequest();
		ireq.setMemberId(bankOrder.getInvestorBaseAccount().getMemberId());
		ireq.setOrderCode(bankOrder.getOrderCode());
		ireq.setIPayNo(bankOrder.getOrderCode());
		//ireq.setIPayNo(seqGeneratorService.getSeqNo(CodeConstants.PAYMENT_depositPayNo));
		ireq.setOrderAmount(bankOrder.getOrderAmount());
		ireq.setOrderTime(DateUtil.format(bankOrder.getOrderTime(), DateUtil.fullDatePattern));
		
		
		BaseResp baseRep = this.paymentServiceImpl.depositbfPay(ireq);
		
		if (0 != baseRep.getErrorCode()) {
			bankOrderRep.setErrorCode(-1);
			bankOrderRep.setErrorMessage(baseRep.getErrorMessage());
			log.info("代扣失败原因:{}", baseRep.getErrorMessage());
		}
		bankOrderRep.setBankOrderOid(bankOrder.getOid());
		bankOrderRep.setOrderStatus(InvestorBankOrderEntity.BANKORDER_orderStatus_toPay);
		
		return bankOrderRep;
	}
	
	@Transactional(value = TxType.REQUIRES_NEW)
	public PayHtmlRep apiDeposit(ApiDepositBankOrderReq bankOrderReq, String orderCode) {
		
		/** 创建订单 */
		InvestorBankOrderEntity bankOrder = investorBankOrderService.findByOrderCodeAndOrderStatusAndOrderType(orderCode,
				InvestorBankOrderEntity.BANKORDER_orderStatus_submitted, InvestorBankOrderEntity.BANKORDER_orderType_deposit);
	
		ApiDepositRequest ireq = new ApiDepositRequest();
		ireq.setMemberId(bankOrder.getInvestorBaseAccount().getMemberId());
		ireq.setOrderCode(bankOrder.getOrderCode());
		ireq.setIPayNo(bankOrder.getOrderCode());
		//ireq.setIPayNo(seqGeneratorService.getSeqNo(CodeConstants.PAYMENT_depositPayNo));
		ireq.setOrderAmount(bankOrder.getOrderAmount());
		ireq.setOrderTime(DateUtil.format(bankOrder.getOrderTime(), DateUtil.fullDatePattern));
		ireq.setReturnUrl(bankOrderReq.getReturnUrl());
		
		PayHtmlRep baseRep = this.paymentServiceImpl.apiDepositPay(ireq);
		
		if (0 != baseRep.getErrorCode()) {
			throw new AMPException(baseRep.getErrorMessage());
		}
		
		baseRep.setOrderStatus(InvestorBankOrderEntity.BANKORDER_orderStatus_toPay);
		return baseRep;
	}
	
	
	/**
	 * 充值回调--投资人
	 */
	@Transactional(value = TxType.REQUIRES_NEW)
	public void depositCallBack(OrderNotifyReq ireq) {
		
		InvestorBankOrderEntity bankOrder = this.investorBankOrderDao.findByOrderCodeAndOrderStatusAndOrderType(
				ireq.getOrderCode(), InvestorBankOrderEntity.BANKORDER_orderStatus_toPay,
				InvestorBankOrderEntity.BANKORDER_orderType_deposit);
        //Call back withhold by wish plan
		String wishplanOid = bankOrder.getWishplanOid();
		if (PayParam.ReturnCode.RC0000.toString().equals(ireq.getReturnCode())) {
			notifyOk(bankOrder);
			if (wishplanOid != null) {
				planMothService.withHoldCallback(wishplanOid, true);
			}
		} else {
			notifyFail(bankOrder);
			if (wishplanOid != null) {
				planMothService.withHoldCallback(wishplanOid, false);
			}
		}
		
	}
	
	private void notifyFail(InvestorBankOrderEntity bankOrder) {
		bankOrder.setOrderStatus(InvestorBankOrderEntity.BANKORDER_orderStatus_payFailed);
		bankOrder.setCompleteTime(DateUtil.getSqlCurrentDate());
		investorBankOrderService.saveEntity(bankOrder);
	}
	
	/**
	 * 充值短款
	 */
	public BaseResp depositShort(DepositShortBankOrderReq ireq) {
		log.info("depositShort--ireq:{}", JSONObject.toJSONString(ireq));
		int i = platformFinanceCompareDataResultNewService.updateDealStatusDealingByOid(ireq.getCrOid());
		log.info("depositShort--orderCode={}, start update={}", ireq.getOrderCode(), i == 1);
		
		InvestorBankOrderEntity bankOrder = this.investorBankOrderService.findByOrderCode(ireq.getOrderCode());
		bankOrder.setOrderStatus(InvestorBankOrderEntity.BANKORDER_orderStatus_abandoned);
		investorBankOrderService.saveEntity(bankOrder);
		
		i = platformFinanceCompareDataResultNewService.updateDealStatusDealtByOid(ireq.getCrOid());
		log.info("depositShort--orderCode={}, end update={}", ireq.getOrderCode(), i == 1);
		return new BaseResp();
	}
	

	/**
	 * 充值长款
	 */
	public BaseResp depositLong(DepositLongBankOrderReq ireq) {
		log.info("depositLong--ireq:{}", JSONObject.toJSONString(ireq));
		int i = platformFinanceCompareDataResultNewService.updateDealStatusDealingByOid(ireq.getCrOid());
		log.info("depositLong--orderCode={}, start update={}", ireq.getOrderCode(), i == 1);
		
		/** 记录业务订单 */
		InvestorBankOrderEntity bankOrder = this.investorBankOrderService.createDepositLongBankOrder(ireq);
		if (InvestorBankOrderEntity.BANKORDER_orderStatus_paySuccess.equals(ireq.getOrderStatus())) {
			bankOrder.setOrderStatus(InvestorBankOrderEntity.BANKORDER_orderStatus_paySuccess);
			bankOrder.setCompleteTime(DateUtil.getSqlCurrentDate());
			investorBankOrderService.saveEntity(bankOrder);
			
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
		
		i = platformFinanceCompareDataResultNewService.updateDealStatusDealtByOid(ireq.getCrOid());
		log.info("depositLong--orderCode={}, end update={}", ireq.getOrderCode(), i == 1);
		
		return new BaseResp();
	}

	private void notifyOk(InvestorBankOrderEntity bankOrder) {
		/** 创建<<投资人-资金变动明细>> */
		investorCashFlowService.createCashFlow(bankOrder);

		/** 更新<<投资人-基本账户-统计>>.<<累计充值总额>><<累计充值次数>><<当日充值次数>><<当日充值总额>> */
		investorStatisticsService.updateStatistics4Deposit(bankOrder);
		/** 更新<<平台-统计>>.<<累计交易总额>><<投资人充值总额>> */
//		platformStatisticsService.updateStatistics4InvestorDeposit(bankOrder.getOrderAmount());
		
		/** 更新<<投资人-基本账户>>.<<余额>> */
		investorBaseAccountService.updateBalance(bankOrder.getInvestorBaseAccount());
		
		/** 运营充值事件 */
		tulipeService.onRecharge(bankOrder);
		//Avoid duplicated call msg and mail
		if (bankOrder.getWishplanOid() == null) {
			RechargeSuccessMsgReq msgReq = new RechargeSuccessMsgReq();
			msgReq.setPhone(bankOrder.getInvestorBaseAccount().getPhoneNum());
			msgReq.setOrderAmount(bankOrder.getOrderAmount());
			msgService.rechargesuccess(msgReq);

			RechargeSuccessMailReq mailReq = new RechargeSuccessMailReq();
			mailReq.setUserOid(bankOrder.getInvestorBaseAccount().getOid());
			mailReq.setOrderAmount(bankOrder.getOrderAmount());
			mailService.rechargesuccess(mailReq);
		}
		bankOrder.setOrderStatus(InvestorBankOrderEntity.BANKORDER_orderStatus_paySuccess);
		bankOrder.setCompleteTime(DateUtil.getSqlCurrentDate());
		investorBankOrderService.saveEntity(bankOrder);
	}
	
	public BaseResp crDepositFail2Ok(NotifyReq notifyReq) {
		log.info("crDepositFail2Ok--noitfyReq:{}", JSONObject.toJSONString(notifyReq));
		int i = platformFinanceCompareDataResultNewService.updateDealStatusDealingByOid(notifyReq.getCrOid());
		log.info("crDepositFail2Ok--orderCode={}, start update={}", notifyReq.getOrderCode(), i == 1);

		BaseResp rep = new BaseResp();
		InvestorBankOrderEntity bankOrder = this.investorBankOrderDao.findByOrderCode(notifyReq.getOrderCode());
		if (!InvestorBankOrderEntity.BANKORDER_orderStatus_payFailed.equals(bankOrder.getOrderStatus())) {
			throw new AMPException("订单非支付失败，不能改充值单订单状态");
		}

		bankOrder.setOrderStatus(InvestorBankOrderEntity.BANKORDER_orderStatus_paySuccess);
		bankOrder.setCompleteTime(DateUtil.getSqlCurrentDate());
		investorBankOrderService.saveEntity(bankOrder);

		this.notifyOk(bankOrder);
	
		
		i = platformFinanceCompareDataResultNewService.updateDealStatusDealtByOid(notifyReq.getCrOid());
		log.info("crDepositFail2Ok--orderCode={}, end update={}", notifyReq.getOrderCode(), i == 1);
		return rep;
	}
	
	/**
	 * 实时对账:充值订单状态由失败改为成功
	 */
	public BaseResp depositFail2Ok(String orderCode) {
		BaseResp rep = new BaseResp();
		InvestorBankOrderEntity bankOrder = this.investorBankOrderDao.findByOrderCode(orderCode);
		if (!InvestorBankOrderEntity.BANKORDER_orderStatus_payFailed.equals(bankOrder.getOrderStatus())) {
			throw new AMPException("订单非支付失败，不能改充值单订单状态");
		}
		PayLogEntity log = this.payLogService.getSuccessPayApllyByOrderCode(bankOrder.getOrderCode());
		
		DepositOrderQueryRequest ireq = new DepositOrderQueryRequest();
		ireq.setInvestorOid(bankOrder.getInvestorBaseAccount().getOid());
		ireq.setIPayNo(log.getIPayNo());
		ireq.setOrderType(PayParam.OrderType.DEPOSIT.toString());
		DepositOrderQueryResponse irep = this.paymentServiceImpl.depositConfirm(ireq);
		if (irep.getOrderStatus().equals(InvestorBankOrderEntity.BANKORDER_orderStatus_paySuccess)) {
			bankOrder.setOrderStatus(InvestorBankOrderEntity.BANKORDER_orderStatus_paySuccess);
			bankOrder.setCompleteTime(DateUtil.getSqlCurrentDate());
			investorBankOrderService.saveEntity(bankOrder);
			
			this.notifyOk(bankOrder);
		} else {
			rep.setErrorCode(-1);
			rep.setErrorMessage("结算支付未成功，冲正失败");
		}
		return rep;
	}
	
	/**
	 * 充值冲正
	 */
	public BaseResp correctDepositBankOrder(String orderCode) {
		BaseResp rep = new BaseResp();
		InvestorBankOrderEntity bankOrder = this.investorBankOrderDao.findByOrderCode(orderCode);
		if (!InvestorBankOrderEntity.BANKORDER_orderStatus_payFailed.equals(bankOrder.getOrderStatus())) {
			throw new AMPException("订单非支付失败，不能补充值单");
		}
		PayLogEntity log = this.payLogService.getSuccessPayApllyByOrderCode(bankOrder.getOrderCode());
		
		DepositOrderQueryRequest ireq = new DepositOrderQueryRequest();
		ireq.setInvestorOid(bankOrder.getInvestorBaseAccount().getOid());
		//ireq.setIPayNo(log.getIPayNo());
		ireq.setIPayNo(bankOrder.getOrderCode());
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
	
	public BaseResp depositOk2Fail(NotifyReq notifyReq) {
		log.info("depositOk2Fail--noitfyReq:{}", JSONObject.toJSONString(notifyReq));
		int i = platformFinanceCompareDataResultNewService.updateDealStatusDealingByOid(notifyReq.getCrOid());
		log.info("depositOk2Fail--orderCode={}, start update={}", notifyReq.getOrderCode(), i == 1);
		
		BaseResp rep = new BaseResp();
		InvestorBankOrderEntity bankOrder = this.investorBankOrderDao.findByOrderCode(notifyReq.getOrderCode());
		if (!InvestorBankOrderEntity.BANKORDER_orderStatus_paySuccess.equals(bankOrder.getOrderStatus())) {
			throw new AMPException("订单非支付成功，不能修改充值单订单状态");
		}
		
		bankOrder.setOrderStatus(InvestorBankOrderEntity.BANKORDER_orderStatus_payFailed);
		this.investorBankOrderService.saveEntity(bankOrder);
		
		/** 创建<<投资人-资金变动明细>> */
		investorCashFlowService.deleteByOrderCode(bankOrder.getOid());

		/** 更新<<投资人-基本账户-统计>>.<<累计充值总额>><<累计充值次数>><<当日充值次数>><<当日充值总额>> */
		investorStatisticsService.updateStatistics4DepositOk2Fail(bankOrder);
		
		/** 更新<<投资人-基本账户>>.<<余额>> */
		investorBaseAccountService.updateBalance(bankOrder.getInvestorBaseAccount());
		
		i = platformFinanceCompareDataResultNewService.updateDealStatusDealtByOid(notifyReq.getCrOid());
		log.info("depositOk2Fail--orderCode={}, end update={}", notifyReq.getOrderCode(), i == 1);
		return rep;
	}
	
	/**
	 * 红包短款
	 */
	public BaseResp redEnvelopeShort(RedEnvelopeShortBankOrderReq ireq) {
		platformFinanceCompareDataResultNewService.updateDealStatusDealingByOid(ireq.getCrOid());
		
		InvestorBankOrderEntity bankOrder = this.investorBankOrderService.findByOrderCode(ireq.getOrderCode());
		
		TradeOrderCouponEntity coupon = this.tradeOrderCouponService.findByBankOrder(bankOrder.getOid());
		
		MyCouponRep rep = this.tulipeService.resetCoupon(coupon.getCoupons());
		
		if (0 == rep.getErrorCode()) {
			bankOrder.setOrderStatus(InvestorBankOrderEntity.BANKORDER_orderStatus_abandoned);
			investorBankOrderService.saveEntity(bankOrder);
		} else {
			throw GHException.getException(rep.getErrorMessage() + "(" + rep.getErrorCode() + ")");
		}
		
		platformFinanceCompareDataResultNewService.updateDealStatusDealtByOid(ireq.getCrOid());
		return new BaseResp();
	}
	
	public BaseResp notifyDepositOk(NotifyReq notifyReq) {
		log.info("notifyDepositOk--noitfyReq:{}", JSONObject.toJSONString(notifyReq));
		int i = platformFinanceCompareDataResultNewService.updateDealStatusDealingByOid(notifyReq.getCrOid());
		log.info("notifyDepositOk--orderCode={}, start update={}", notifyReq.getOrderCode(), i == 1);
		
		
		
		OrderNotifyReq ireq = new OrderNotifyReq();
		ireq.setOrderCode(notifyReq.getOrderCode());
		ireq.setReturnCode(PayParam.ReturnCode.RC0000.toString());
		boolean flag = paymentServiceImpl.tradeCallback(ireq);
		
		i = platformFinanceCompareDataResultNewService.updateDealStatusDealtByOid(notifyReq.getCrOid());
		log.info("notifyDepositOk--orderCode={}, end update={}", notifyReq.getOrderCode(), i == 1);
		return new BaseResp();
	}
	
	public BaseResp notifyDepositFail(NotifyReq notifyReq) {
		log.info("notifyDepositFail--noitfyReq:{}", JSONObject.toJSONString(notifyReq));
		int i = platformFinanceCompareDataResultNewService.updateDealStatusDealingByOid(notifyReq.getCrOid());
		log.info("notifyDepositFail--orderCode={}, start update={}", notifyReq.getOrderCode(), i == 1);

		OrderNotifyReq ireq = new OrderNotifyReq();
		ireq.setOrderCode(notifyReq.getOrderCode());
		ireq.setReturnCode(PayParam.ReturnCode.RC9999.toString());
		boolean flag = this.paymentServiceImpl.tradeCallback(ireq);

		i = platformFinanceCompareDataResultNewService.updateDealStatusDealtByOid(notifyReq.getCrOid());
		log.info("notifyDepositFail--orderCode={}, start update={}", notifyReq.getOrderCode(), i == 1);
		return new BaseResp();
	}
}
