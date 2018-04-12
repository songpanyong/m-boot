package com.guohuai.mmp.investor.bankorder;


import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.cache.entity.InvestorBaseAccountCacheEntity;
import com.guohuai.cache.service.CacheInvestorService;
import com.guohuai.cache.service.CachePlatformService;
import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.DateUtil;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountEntity;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountService;
import com.guohuai.mmp.investor.baseaccount.statistics.InvestorStatisticsService;
import com.guohuai.mmp.investor.cashflow.InvestorCashFlowService;
import com.guohuai.mmp.platform.SeqGeneratorService;
import com.guohuai.mmp.platform.baseaccount.statistics.PlatformStatisticsService;
import com.guohuai.mmp.platform.finance.result.PlatformFinanceCompareDataResultNewService;
import com.guohuai.mmp.platform.msgment.MailService;
import com.guohuai.mmp.platform.msgment.MsgService;
import com.guohuai.mmp.platform.msgment.WithdrawApplyMailReq;
import com.guohuai.mmp.platform.msgment.WithdrawApplyMsgReq;
import com.guohuai.mmp.platform.msgment.WithdrawSuccessMailReq;
import com.guohuai.mmp.platform.msgment.WithdrawSuccessMsgReq;
import com.guohuai.mmp.platform.payment.OrderNotifyReq;
import com.guohuai.mmp.platform.payment.PayParam;
import com.guohuai.mmp.platform.payment.Payment;
import com.guohuai.mmp.platform.payment.WithdrawAuditRequest;
import com.guohuai.mmp.platform.payment.WithdrawRequest;
import com.guohuai.mmp.platform.payment.log.PayInterface;
import com.guohuai.mmp.platform.payment.log.PayLogEntity;
import com.guohuai.mmp.platform.payment.log.PayLogReq;
import com.guohuai.mmp.platform.payment.log.PayLogService;
import com.guohuai.mmp.platform.tulip.TulipService;
import com.guohuai.mmp.sys.CodeConstants;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class InvestorWithdrawBankOrderService {
	
	@Autowired
	private InvestorBaseAccountService investorBaseAccountService;
	@Autowired
	private InvestorCashFlowService investorCashFlowService;
	@Autowired
	private InvestorStatisticsService investorStatisticsService;
	@Autowired
	private Payment paymentServiceImpl;
	@Autowired
	private TulipService tulipService;
	@Autowired
	private CacheInvestorService cacheInvestorService;
	@Autowired
	private CachePlatformService cachePlatformService;
	@Autowired
	private MsgService msgService;
	@Autowired
	private MailService mailService;
	@Autowired
	private InvestorBankOrderService investorBankOrderService;
	@Autowired
	private SeqGeneratorService seqGeneratorService;
	@Autowired
	private PlatformFinanceCompareDataResultNewService platformFinanceCompareDataResultNewService;
	@Autowired
	private PayLogService payLogService;
	@Autowired
	private InvestorBankOrderDao investorBankOrderDao;
	
	/**
	 *  体现
	 */
	@Transactional(value = TxType.REQUIRES_NEW)
	public BankOrderRep withdraw(String orderCode) {
		log.info("orderCode =" + orderCode  + "start"); 
		BankOrderRep bankOrderRep = new BankOrderRep();
		InvestorBankOrderEntity bankOrder = investorBankOrderService.findByOrderCodeAndOrderStatusAndOrderType(orderCode,
				InvestorBankOrderEntity.BANKORDER_orderStatus_submitted, InvestorBankOrderEntity.BANKORDER_orderType_withdraw);
		
		/** 判断<<投资人-基本账户>>.<<余额>>是否足够提现 */
		investorBaseAccountService.balanceEnoughWithdraw(bankOrder.getInvestorBaseAccount(), bankOrder.getOrderAmount());
		
		/** 判断用户是否正常 */
		this.cacheInvestorService.isInvestorBaseAccountNormal(bankOrder.getInvestorBaseAccount().getOid());
		
		
		/** 手续费/月提现次数  */
		FeeRep feeRep = cacheInvestorService.getFee(bankOrder);
		
		/** 平台单日提现限额 */
		this.cachePlatformService.isWithdrawDayLimit(bankOrder.getOrderAmount());
		
		WithdrawRequest ireq = new WithdrawRequest();
		ireq.setMemberId(bankOrder.getInvestorBaseAccount().getMemberId());
		ireq.setOrderCode(bankOrder.getOrderCode());
		//ireq.setIPayNo(seqGeneratorService.getSeqNo(CodeConstants.PAYMENT_withdrawPayNo));
		ireq.setIPayNo(bankOrder.getOrderCode());
		if (feeRep.getPayer().equals(InvestorBankOrderEntity.BANKORDER_feePayer_user)) {
			ireq.setOrderAmount(bankOrder.getOrderAmount().subtract(feeRep.getFee()));
			ireq.setFee(feeRep.getFee());
		} else {
			ireq.setOrderAmount(bankOrder.getOrderAmount());
			ireq.setFee(BigDecimal.ZERO);
		}
		ireq.setOrderTime(DateUtil.format(bankOrder.getOrderTime(), DateUtil.fullDatePattern));
		ireq.setUserType(PayParam.UserType.INVESTOR.toString());

		BaseResp irep = this.paymentServiceImpl.withdrawPay(ireq);
		
		if (0 != irep.getErrorCode()) {
			//throw new AMPException(irep.getErrorMessage());
			bankOrderRep.setErrorCode(-1);
			bankOrderRep.setErrorMessage(irep.getErrorMessage());
		}
		if (0 == irep.getErrorCode()) {
			this.investorBaseAccountService.updateBalance(bankOrder.getInvestorBaseAccount());
		}
		bankOrder.setFee(feeRep.getFee());
		bankOrder.setFeePayer(feeRep.getPayer());
		investorBankOrderService.saveEntity(bankOrder);
		/**增加在前端显示手续费的字段*/
		bankOrderRep.setFee(ireq.getFee());
		bankOrderRep.setBankOrderOid(bankOrder.getOid());
		bankOrderRep.setOrderStatus(InvestorBankOrderEntity.BANKORDER_orderStatus_toPay);
		
		WithdrawApplyMsgReq msgReq = new WithdrawApplyMsgReq();
		msgReq.setPhone(bankOrder.getInvestorBaseAccount().getPhoneNum());
		msgReq.setOrderAmount(bankOrder.getOrderAmount());
		msgReq.setOrderTime(bankOrder.getOrderTime());
		msgReq.setFee(ireq.getFee());
		msgReq.setPreAmount(msgReq.getOrderAmount().subtract(msgReq.getFee()));
		msgService.withdrawapply(msgReq);
	
		WithdrawApplyMailReq mailReq = new WithdrawApplyMailReq();
		mailReq.setUserOid(bankOrder.getInvestorBaseAccount().getOid());
		mailReq.setOrderAmount(bankOrder.getOrderAmount());
		mailReq.setOrderTime(bankOrder.getOrderTime());
		mailReq.setFee(ireq.getFee());
		mailReq.setPreAmount(mailReq.getOrderAmount().subtract(ireq.getFee()));
		mailService.withdrawapply(mailReq);
		return bankOrderRep;
	}
	
	public boolean notifyWithdrawOk(OrderNotifyReq req) {

		InvestorBankOrderEntity bankOrder = investorBankOrderService.findByOrderCodeAndOrderStatusAndOrderType(
				req.getOrderCode(), InvestorBankOrderEntity.BANKORDER_orderStatus_toPay,
				InvestorBankOrderEntity.BANKORDER_orderType_withdraw);
		if (PayParam.ReturnCode.RC0000.toString().equals(req.getReturnCode())) {
			bankOrder.setOrderStatus(InvestorBankOrderEntity.BANKORDER_orderStatus_paySuccess);
			bankOrder.setPayStatus(InvestorBankOrderEntity.BANKORDER_payStatus_paySuccess);
			bankOrder.setFrozenStatus(InvestorBankOrderEntity.BANKORDER_frozenStatus_iceOut);
			bankOrder.setCompleteTime(DateUtil.getSqlCurrentDate());
			investorBankOrderService.saveEntity(bankOrder);
			
			notifyOk(bankOrder);
		}
		this.investorBaseAccountService.updateBalance(bankOrder.getInvestorBaseAccount());
		
		return true;
	}
	
	public boolean notifyWithdrawFail(OrderNotifyReq req) {
		InvestorBankOrderEntity bankOrder = investorBankOrderService.findByOrderCodeAndOrderStatusAndOrderType(
				req.getOrderCode(), InvestorBankOrderEntity.BANKORDER_orderStatus_toPay,
				InvestorBankOrderEntity.BANKORDER_orderType_withdraw);
		bankOrder.setPayStatus(InvestorBankOrderEntity.BANKORDER_payStatus_payFailed);
		bankOrder.setOrderStatus(InvestorBankOrderEntity.BANKORDER_orderStatus_payFailed);
		bankOrder.setFrozenStatus(InvestorBankOrderEntity.BANKORDER_frozenStatus_iceOut);
		bankOrder.setCompleteTime(DateUtil.getSqlCurrentDate());
		this.investorBankOrderService.saveEntity(bankOrder);
		return true;
	}

	/**
	 * 体现回调
	 */
	@Transactional(value = TxType.REQUIRES_NEW)
	public boolean withdrawCallBack(OrderNotifyReq req) {

		InvestorBankOrderEntity bankOrder = investorBankOrderService.findByOrderCodeAndOrderStatusAndOrderType(
				req.getOrderCode(), InvestorBankOrderEntity.BANKORDER_orderStatus_toPay,
				InvestorBankOrderEntity.BANKORDER_orderType_withdraw);
		if (PayParam.ReturnCode.RC0000.toString().equals(req.getReturnCode())) {
			notifyOk(bankOrder);
		} else {
			notifyFail(bankOrder);
		}
		
		return true;
	}
	
	@Transactional(value = TxType.REQUIRES_NEW)
	public BankOrderRep notifyFail(InvestorBankOrderEntity bankOrder) {
		
		/** 月提现次数minus minus */
		if (DateUtil.isInCurrentMonth(bankOrder.getOrderTime())) {
			this.investorStatisticsService.decreaseMonthWithdrawCount(bankOrder);
		}
		
		bankOrder.setOrderStatus(InvestorBankOrderEntity.BANKORDER_orderStatus_payFailed);
		bankOrder.setCompleteTime(DateUtil.getSqlCurrentDate());
		investorBankOrderService.saveEntity(bankOrder);
		
		this.investorBaseAccountService.updateBalance(bankOrder.getInvestorBaseAccount());
		
		return new BankOrderRep();
	}
	
	/**
	 * 提现短款
	 */
	public BaseResp withdrawShort(WithdrawShortBankOrderReq ireq) {
		log.info("withdrawShort--ireq:{}", JSONObject.toJSONString(ireq));
		int i = platformFinanceCompareDataResultNewService.updateDealStatusDealingByOid(ireq.getCrOid());
		log.info("withdrawShort--orderCode={}, start update={}", ireq.getOrderCode(), i == 1);
		
		InvestorBankOrderEntity bankOrder = this.investorBankOrderService.findByOrderCode(ireq.getOrderCode());
		bankOrder.setOrderStatus(InvestorBankOrderEntity.BANKORDER_orderStatus_abandoned);
		bankOrder.setFrozenStatus(InvestorBankOrderEntity.BANKORDER_frozenStatus_iceOut);
		bankOrder.setPayStatus(InvestorBankOrderEntity.BANKORDER_payStatus_noPay);
		investorBankOrderService.saveEntity(bankOrder);
		
		i = platformFinanceCompareDataResultNewService.updateDealStatusDealtByOid(ireq.getCrOid());
		log.info("withdrawShort--orderCode={}, end update={}", ireq.getOrderCode(), i == 1);
		
		return new BaseResp();
	}
	

	/**
	 * 提现长款
	 */
	public BaseResp withdrawLong(WithdrawLongBankOrderReq ireq) {
		log.info("withdrawLong--ireq:{}", JSONObject.toJSONString(ireq));
		int i = platformFinanceCompareDataResultNewService.updateDealStatusDealingByOid(ireq.getCrOid());
		log.info("withdrawLong--orderCode={}, start update={}", ireq.getOrderCode(), i == 1);
		
		/** 记录业务订单 */
		InvestorBankOrderEntity bankOrder = this.investorBankOrderService.createWithdrawLongBankOrder(ireq);
		if (InvestorBankOrderEntity.BANKORDER_orderStatus_paySuccess.equals(ireq.getOrderStatus())) {
			bankOrder.setOrderStatus(InvestorBankOrderEntity.BANKORDER_orderStatus_paySuccess);
			bankOrder.setPayStatus(InvestorBankOrderEntity.BANKORDER_payStatus_paySuccess);
			bankOrder.setFrozenStatus(InvestorBankOrderEntity.BANKORDER_frozenStatus_iceOut);
			bankOrder.setCompleteTime(DateUtil.getSqlCurrentDate());
			
			investorBankOrderService.saveEntity(bankOrder);
			
			notifyOk(bankOrder);
			this.investorBaseAccountService.updateBalance(bankOrder.getInvestorBaseAccount());
		} else if (InvestorBankOrderEntity.BANKORDER_orderStatus_payFailed.equals(ireq.getOrderStatus())) {
			bankOrder.setFrozenStatus(InvestorBankOrderEntity.BANKORDER_frozenStatus_iceOut);
			bankOrder.setPayStatus(InvestorBankOrderEntity.BANKORDER_payStatus_payFailed);
			bankOrder.setOrderStatus(InvestorBankOrderEntity.BANKORDER_orderStatus_payFailed);
			bankOrder.setCompleteTime(DateUtil.getSqlCurrentDate());
			investorBankOrderService.saveEntity(bankOrder);
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
		log.info("withdrawLong--orderCode={}, start update={}", ireq.getOrderCode(), i == 1);
		
		return new BaseResp();
	}


	private void notifyOk(InvestorBankOrderEntity bankOrder) {
		
		this.investorBaseAccountService.updateBalance(bankOrder.getInvestorBaseAccount());
		
		/** 创建<<投资人-资金变动明细>> */
		investorCashFlowService.createCashFlow(bankOrder);
		
		/** 更新<<投资人-基本账户-统计>>.<<累计提现总额>><<累计提现次数>><<当日提现总额>><<当日提现次数>><<月提现次数>> */
		investorStatisticsService.updateStatistics4Withdraw(bankOrder);
		/** 更新<<平台-统计>>.<<累计交易总额>><<投资人提现总额>> */
//		this.platformStatisticsService.updateStatistics4InvestorWithdraw(bankOrder.getOrderAmount());

		bankOrder.setOrderStatus(InvestorBankOrderEntity.BANKORDER_orderStatus_paySuccess);
		bankOrder.setCompleteTime(DateUtil.getSqlCurrentDate());
		investorBankOrderService.saveEntity(bankOrder);
		
		this.tulipService.onCash(bankOrder);
		
		WithdrawSuccessMsgReq msgReq = new WithdrawSuccessMsgReq();
		msgReq.setPhone(bankOrder.getInvestorBaseAccount().getPhoneNum());
		msgReq.setCompleteTime(bankOrder.getCompleteTime());
		msgReq.setRealAmount(bankOrder.getOrderAmount().subtract(getFeeByFeePayer(bankOrder)));
		msgService.withdrawsuccess(msgReq);
		
		WithdrawSuccessMailReq mailReq = new WithdrawSuccessMailReq();
		mailReq.setUserOid(bankOrder.getInvestorBaseAccount().getOid());
		mailReq.setCompleteTime(bankOrder.getCompleteTime());
		mailReq.setRealAmount(bankOrder.getOrderAmount().subtract(getFeeByFeePayer(bankOrder)));
		mailService.withdrawsuccess(mailReq);
	}
	
	public BaseResp withdrawOk2Fail(NotifyReq notifyReq) {
		log.info("withdrawOk2Fail--noitfyReq:{}", JSONObject.toJSONString(notifyReq));
		int i = platformFinanceCompareDataResultNewService.updateDealStatusDealingByOid(notifyReq.getCrOid());
		log.info("withdrawOk2Fail--orderCode={}, start update={}", notifyReq.getOrderCode(), i == 1);
		BaseResp rep = new BaseResp();
		InvestorBankOrderEntity bankOrder = this.investorBankOrderDao.findByOrderCode(notifyReq.getOrderCode());
		/** 创建<<投资人-资金变动明细>> */
		investorCashFlowService.deleteByOrderCode(bankOrder.getOid());
		
		/** 更新<<投资人-基本账户-统计>>.<<累计提现总额>><<累计提现次数>><<当日提现总额>><<当日提现次数>><<月提现次数>> */
		investorStatisticsService.updateStatistics4WithdrawOK2Fail(bankOrder);
		
		bankOrder.setOrderStatus(InvestorBankOrderEntity.BANKORDER_orderStatus_payFailed);
		bankOrder.setPayStatus(InvestorBankOrderEntity.BANKORDER_orderStatus_payFailed);
		bankOrder.setFrozenStatus(InvestorBankOrderEntity.BANKORDER_frozenStatus_iceOut);
		bankOrder.setCompleteTime(DateUtil.getSqlCurrentDate());
		investorBankOrderService.saveEntity(bankOrder);
		
		i = platformFinanceCompareDataResultNewService.updateDealStatusDealtByOid(notifyReq.getCrOid());
		log.info("withdrawOk2Fail--orderCode={}, start update={}", notifyReq.getOrderCode(), i == 1);
		return rep;
	}
	
	/**
	 * 提现批量通过
	 */
	public BaseResp withdrawBatchPass(List<String> orderCodes) {
		BaseResp rep = new BaseResp();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < orderCodes.size(); i++) {
			String orderCode = orderCodes.get(i);
			BaseResp singleRep = this.withdrawPass(orderCode);
			if (0 != singleRep.getErrorCode()) {
				sb.append(orderCode).append(":").append(singleRep.getErrorMessage()).append(System.getProperty("line.separator"));
			}
		}
		
		if (sb.length() != 0) {
			rep.setErrorCode(-1);
			rep.setErrorMessage(sb.toString());
		}
		return rep;
		
	}
	
	/**
	 * 提现批量拒绝
	 */
	public BaseResp withdrawBatchReject(List<String> orderCodes) {
		BaseResp rep = new BaseResp();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < orderCodes.size(); i++) {
			String orderCode = orderCodes.get(i);
			BaseResp singleRep = this.withdrawReject(orderCode);
			if (0 != singleRep.getErrorCode()) {
				sb.append(orderCode).append(":").append(singleRep.getErrorMessage()).append(System.getProperty("line.separator"));
			}
		}
		
		if (sb.length() != 0) {
			rep.setErrorCode(-1);
			rep.setErrorMessage(sb.toString());
		}
		return rep;
		
	}
	
	public BaseResp withdrawPass(String orderCode) {
		BaseResp irep = new BaseResp();
		InvestorBankOrderEntity bankOrder = this.investorBankOrderService.findByOrderCode(orderCode);
		
		Timestamp iceOutTime = DateUtil.getSqlCurrentDate();
		int i = this.investorBankOrderDao.updatePassFrozenStatusToIceOut(bankOrder.getOid(), iceOutTime);
		if (i < 1) {
			irep.setErrorCode(-1);
			irep.setErrorMessage("订单状态非冻结状态不能审核通过");
			return irep;
		}
		WithdrawAuditRequest ireq = new WithdrawAuditRequest();
		ireq.setOrderCode(bankOrder.getOrderCode());
		ireq.setInvestorOid(bankOrder.getInvestorBaseAccount().getOid());
		ireq.setType(PayParam.Type.WITHDRAW.toString());
		ireq.setUserType(PayParam.UserType.INVESTOR.toString());
		ireq.setIceOutTime(DateUtil.format(iceOutTime, DateUtil.fullDatePattern));
		irep = this.paymentServiceImpl.withdrawPass(ireq);
		
		return irep;
	}
	
	public BaseResp withdrawReject(String orderCode) {
		BaseResp irep = new BaseResp();
		InvestorBankOrderEntity bankOrder = this.investorBankOrderService.findByOrderCode(orderCode);
		
		Timestamp iceOutTime = DateUtil.getSqlCurrentDate();
		int i = this.investorBankOrderDao.updateFrozenStatusToIceOut(bankOrder.getOid(), iceOutTime);
		if (i < 1) {
			irep.setErrorCode(-1);
			irep.setErrorMessage("订单状态非冻结状态不能审核拒绝");
			return irep;
		}
		WithdrawAuditRequest ireq = new WithdrawAuditRequest();
		ireq.setOrderCode(bankOrder.getOrderCode());
		ireq.setInvestorOid(bankOrder.getInvestorBaseAccount().getOid());
		ireq.setType(PayParam.Type.WITHDRAW.toString());
		ireq.setUserType(PayParam.UserType.INVESTOR.toString());
		ireq.setIceOutTime(DateUtil.format(iceOutTime, DateUtil.fullDatePattern));
		irep = this.paymentServiceImpl.withdrawReject(ireq);
		if (0 != irep.getErrorCode()) {
			return irep;
		} else {
			/** 月提现次数minus minus */
			if (DateUtil.isInCurrentMonth(bankOrder.getOrderTime())) {
				this.investorStatisticsService.decreaseMonthWithdrawCount(bankOrder);
			}
			if (bankOrder.getPayStatus().equals(InvestorBankOrderEntity.BANKORDER_payStatus_noPay)) {
				bankOrder.setFrozenStatus(InvestorBankOrderEntity.BANKORDER_frozenStatus_iceOut);
				bankOrder.setOrderStatus(InvestorBankOrderEntity.BANKORDER_orderStatus_abandoned);
			}
			bankOrder.setCompleteTime(DateUtil.getSqlCurrentDate());
			investorBankOrderService.saveEntity(bankOrder);
			
			this.investorBaseAccountService.updateBalance(bankOrder.getInvestorBaseAccount());
			
			i = this.investorBankOrderDao.updateFrozenStatusIceOut(bankOrder.getOid());
			if (i < 1) {
				irep.setErrorCode(-1);
				irep.setErrorMessage("解冻失败");
			}
		}
		return irep;
	}
	
	public BigDecimal getFeeByFeePayer(InvestorBankOrderEntity bankOrder){
		BigDecimal fee =new BigDecimal(0);
		if(bankOrder.getFeePayer().equals(InvestorBankOrderEntity.BANKORDER_feePayer_platform)){
			return fee;
		}else{
			fee = bankOrder.getFee();
			return fee;
		}
	}
}
