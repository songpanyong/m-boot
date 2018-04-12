package com.guohuai.mmp.platform.payment;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.guohuai.basic.common.StringUtil;
import com.guohuai.basic.component.exception.GHException;
import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.DateUtil;
import com.guohuai.component.util.KeyRedisUtil;
import com.guohuai.component.util.StrRedisUtil;
import com.guohuai.mmp.investor.bankorder.BankOrderRep;
import com.guohuai.mmp.investor.bankorder.InvestorBankOrderEntity;
import com.guohuai.mmp.investor.bankorder.InvestorBankOrderExtService;
import com.guohuai.mmp.investor.bankorder.InvestorBankOrderService;
import com.guohuai.mmp.investor.bankorder.ResendSmsReq;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountService;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderBatchPayService;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderEntity;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderService;
import com.guohuai.mmp.platform.accment.AccParam;
import com.guohuai.mmp.platform.accment.UserBindCardApplyRep;
import com.guohuai.mmp.platform.accment.UserBindCardApplyRequest;
import com.guohuai.mmp.platform.accment.UserBindCardConfirmRequest;
import com.guohuai.mmp.platform.accment.UserUnBindCardRequest;
import com.guohuai.mmp.platform.payment.log.PayInterface;
import com.guohuai.mmp.platform.payment.log.PayLogEntity;
import com.guohuai.mmp.platform.payment.log.PayLogReq;
import com.guohuai.mmp.platform.payment.log.PayLogService;
import com.guohuai.mmp.publisher.bankorder.PublisherBankOrderEntity;
import com.guohuai.mmp.publisher.bankorder.PublisherBankOrderService;
import com.guohuai.mmp.publisher.bankorder.PublisherDepositBankOrderService;
import com.guohuai.mmp.publisher.bankorder.PublisherWithdrawBankOrderService;
import com.guohuai.mmp.sys.CodeConstants;
import com.guohuai.settlement.api.SettlementSdk;
import com.guohuai.settlement.api.request.DepositConfirmRequest;
import com.guohuai.settlement.api.request.ElementValidationRequest;
import com.guohuai.settlement.api.request.InteractiveRequest;
import com.guohuai.settlement.api.request.OrderRequest;
import com.guohuai.settlement.api.request.QueryOrdersRequest;
import com.guohuai.settlement.api.request.WriterOffOrderRequest;
import com.guohuai.settlement.api.response.BaseResponse;
import com.guohuai.settlement.api.response.DepositConfirmResponse;
import com.guohuai.settlement.api.response.ElementValidaResponse;
import com.guohuai.settlement.api.response.OrderResponse;
import com.guohuai.settlement.api.response.QueryOrdersResponse;
import com.guohuai.settlement.api.response.UserAccountInfoResponse;
import com.guohuai.settlement.api.response.WriteOffResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * the third party payment notification implemented service
 */
@Slf4j
public class PaymentServiceImpl implements Payment {
	
	
	@Autowired
	private PayLogService payLogService;
	@Autowired
	private SettlementSdk settlementSdk;
	@Autowired 
	private InvestorBankOrderService investorBankOrderService;
	@Autowired
	private InvestorBankOrderExtService investorBankOrderExtService;
	@Autowired
	private InvestorBaseAccountService investorBaseAccountService;
	@Autowired
	private PublisherBankOrderService publisherBankOrderService;
	@Autowired
	private PublisherDepositBankOrderService publisherDepositBankOrderService;
	@Autowired
	private PublisherWithdrawBankOrderService publisherWithdrawBankOrderService;
	@Autowired
	private InvestorTradeOrderService investorTradeOrderService;
	@Autowired
	private InvestorTradeOrderBatchPayService investorTradeOrderBatchPayService;
	@Autowired
	private RedisTemplate<String, String> redis;
	
	public static final String PAY_orderCode = "p:o:c:";
	
	@Value("${seq.env}")
	String seqEnv;
	
	/**
	 * 订单查询
	 */
	public BaseResp queryPay(QueryPayRequest ireq) {
		BaseResp irep = new BaseResp();
		OrderRequest oreq = new OrderRequest();
		oreq.setOrderNo(ireq.getOrderNo());
		oreq.setUserOid(ireq.getUserOid());

		OrderResponse orep = new OrderResponse();
		try {
			orep = this.settlementSdk.queryPay(oreq);
			this.setIrep(irep, orep);
		} catch (Exception e) {
			irep.setErrorCode(-1);
			irep.setErrorMessage(AMPException.getStacktrace(e));
		}

		return irep;

	}
	
	/**
	 * 充值申请，获取验证码
	 */
	public DepositApplyRep validPay(DepositApplyRequest ireq) {
		DepositApplyRep irep = new DepositApplyRep();
		
		OrderRequest oreq = new OrderRequest();
		oreq.setUserOid(ireq.getMemberId());
		oreq.setAmount(ireq.getOrderAmount());
		oreq.setSystemSource(ireq.getSystemSource());
		oreq.setRequestNo(ireq.getRequestNo());
		
		OrderResponse orep = new OrderResponse();
		log.info(JSONObject.toJSONString(oreq));
		try {
			orep = settlementSdk.validPay(oreq);
		} catch (Exception e) {
			log.error("申请短信接口异常", e);
			irep.setErrorCode(-1);
			irep.setErrorMessage("申请短信接口异常:" + AMPException.getStacktrace(e)); 
			return irep;
		}
		
		if (null == orep) {
			irep.setErrorCode(-1);
			irep.setErrorMessage("申请短信:返回null");
			return irep;
		}
		
		if (!PayParam.ReturnCode.RC0000.toString().equals(orep.getReturnCode())) {
			irep.setErrorCode(-1);
			irep.setErrorMessage(orep.getErrorMessage());
			return irep;
		}
		if (StringUtil.isEmpty(orep.getPayNo())) {
			irep.setErrorCode(-1);
			irep.setErrorMessage("申请短信:支付流水号返回为空");
			return irep;
		}
		irep.setPayNo(orep.getPayNo());
		
		return irep;
	}
	
	/**
	 * 充值
	 */
	public BaseResp depositPay(DepositRequest ireq) {

		ireq.setDescribe("desposit describe");
		ireq.setRemark("desposit remark");
		ireq.setPayMethod(PayParam.PayMethod.MOBILE.toString());
		ireq.setType(PayParam.Type.DEPOSIT.toString());
		ireq.setRequestNo(StringUtil.uuid());
		ireq.setSystemSource(PayParam.SystemSource.MIMOSA.toString());
		return this.pay(ireq, true);
	}
	
	/**
	 * 无码充值
	 */
	public BaseResp depositbfPay(DepositbfRequest ireq) {

		ireq.setDescribe("despositbf describe");
		ireq.setRemark("desposit remark");
		ireq.setPayMethod(PayParam.PayMethod.MOBILE.toString());
		ireq.setType(PayParam.Type.DEPOSIT.toString());
		ireq.setRequestNo(StringUtil.uuid());
		ireq.setSystemSource(PayParam.SystemSource.MIMOSA.toString());
		return this.bfpay(ireq, true);
	}
	
	/**
	 * 充值
	 */
	public BaseResp bfpay(DepositbfRequest ireq, boolean isLog) {
		BaseResp irep = new BaseResp();
		
		OrderRequest oreq = new OrderRequest();
		oreq.setUserOid(ireq.getMemberId());
		oreq.setOrderNo(ireq.getIPayNo());
		oreq.setRequestNo(ireq.getRequestNo());
		oreq.setPaymentMethod(ireq.getPayMethod());
		oreq.setType(ireq.getType());
		oreq.setAmount(ireq.getOrderAmount());
		oreq.setRemark(ireq.getRemark());
		oreq.setDescribe(ireq.getDescribe());
		oreq.setOrderCreateTime(ireq.getOrderTime());
		oreq.setSystemSource(ireq.getSystemSource());
		
		//Fixed the user type bug 2018-0314
		oreq.setUserType(PayParam.UserType.INVESTOR.toString());
		
		OrderResponse orep = new OrderResponse();
		try {
			log.info(JSONObject.toJSONString(oreq));

			log.info("orderCode={}, startTime={}", ireq.getOrderCode(), DateUtil.getSqlCurrentDate());
			orep = settlementSdk.withholding(oreq);
			log.info("orderCode={}, endTime={}", ireq.getOrderCode(), DateUtil.getSqlCurrentDate());
			this.setIrep(irep, orep);
		} catch (Exception e) {
			irep.setErrorCode(-1);
			irep.setErrorMessage(AMPException.getStacktrace(e));
		}
		
		
		if (isLog) {
			writeLog(ireq, irep, PayInterface.pay.getInterfaceName());
		}
		
		return irep;
	}
	
	
	/**
	 * 充值
	 */
	public PayHtmlRep apiDepositPay(ApiDepositRequest ireq) {

		ireq.setDescribe("apidesposit describe");
		ireq.setRemark("desposit remark");
		ireq.setPayMethod(PayParam.PayMethod.PC.toString());
		ireq.setType(PayParam.Type.DEPOSIT.toString());
		ireq.setProductDesc("product desc");
		ireq.setRequestNo(StringUtil.uuid());
		ireq.setSystemSource(PayParam.SystemSource.MIMOSA.toString());
		return this.apiPay(ireq, true);
	}
	
	/**
	 * API充值
	 */
	public PayHtmlRep apiPay(ApiDepositRequest ireq, boolean isLog) {
		PayHtmlRep irep = new PayHtmlRep();
		
		OrderRequest oreq = new OrderRequest();
		oreq.setUserOid(ireq.getMemberId());
		oreq.setOrderNo(ireq.getIPayNo());
		oreq.setRequestNo(ireq.getRequestNo());
		oreq.setPaymentMethod(ireq.getPayMethod());
		oreq.setType(ireq.getType());
		oreq.setAmount(ireq.getOrderAmount());
		oreq.setRemark(ireq.getRemark());
		oreq.setDescribe(ireq.getDescribe());
		oreq.setOrderCreateTime(ireq.getOrderTime());
		oreq.setProdInfo(ireq.getProductDesc());
		oreq.setProdDetailUrl(ireq.getReturnUrl());
		oreq.setSystemSource(ireq.getSystemSource());
		
		OrderResponse orep = new OrderResponse();
		try {
			log.info(JSONObject.toJSONString(oreq));

			log.info("orderCode={}, startTime={}", ireq.getOrderCode(), DateUtil.getSqlCurrentDate());
			orep = settlementSdk.pay(oreq);
			log.info("orderCode={}, endTime={}", ireq.getOrderCode(), DateUtil.getSqlCurrentDate());
			
			
			if (null == orep) {
				irep.setErrorCode(-1);
				irep.setErrorMessage("返回为空");
				
			} else if (!PayParam.ReturnCode.RC0000.toString().equals(orep.getReturnCode())) {
				irep.setErrorCode(-1);
				irep.setErrorMessage(orep.getErrorMessage());
				irep.setRetHtml(orep.getRespHtml());
			} else {
				irep.setRetHtml(orep.getRespHtml());
			}
			log.info(orep.getRespHtml());
		} catch (Exception e) {
			irep.setErrorCode(-1);
			irep.setErrorMessage(AMPException.getStacktrace(e));
		}
		
		
		if (isLog) {
			writeLog(ireq, irep, PayInterface.pay.getInterfaceName());
		}
		
		return irep;
	}
	
	
	
	/**
	 * 提现
	 */
	public BaseResp withdrawPay(WithdrawRequest ireq) {
		
		ireq.setDescribe("withdraw describe");
		ireq.setRemark("withdraw remark");
		ireq.setType(PayParam.Type.WITHDRAW.toString());
		ireq.setSystemSource(PayParam.SystemSource.MIMOSA.toString());
		ireq.setRequestNo(StringUtil.uuid());
		return this.payee(ireq, true);
	}
	
	/**
	 * 提现
	 */
	public BaseResp payee(WithdrawRequest ireq, boolean isLog) {
		BaseResp irep = new BaseResp();
		
		OrderRequest oreq = new OrderRequest();
		oreq.setUserOid(ireq.getMemberId());
		oreq.setOrderNo(ireq.getOrderCode());
		oreq.setAmount(ireq.getOrderAmount());
		oreq.setFee(ireq.getFee());
		oreq.setDescribe(ireq.getDescribe());
		oreq.setRemark(ireq.getRemark());
		oreq.setType(ireq.getType());
		oreq.setSystemSource(ireq.getSystemSource());
		oreq.setRequestNo(ireq.getRequestNo());
		oreq.setOrderCreateTime(ireq.getOrderTime());
		oreq.setUserType(ireq.getUserType());
		
		OrderResponse orep = new OrderResponse();
	
		try {
			log.info(JSONObject.toJSONString(oreq));
			orep = settlementSdk.payee(oreq);
			this.setIrep(irep, orep);
		} catch (Exception e) {
			irep.setErrorCode(-1);
			irep.setErrorMessage(AMPException.getStacktrace(e));
			
		}
		if (isLog) {
			writeLog(ireq, irep, PayInterface.payee.getInterfaceName());
		}
		
		return irep;
	}
	
	@Override
	public BaseResp withdrawPass(WithdrawAuditRequest ireq) {
		
		BaseResp irep = new BaseResp();
		
		OrderRequest oreq = new OrderRequest();
		oreq.setUserOid(ireq.getInvestorOid());
		oreq.setOrderNo(ireq.getOrderCode());
		oreq.setType(ireq.getType());
		oreq.setUserType(ireq.getUserType());
		oreq.setOrderCreateTime(ireq.getIceOutTime());
		
		OrderResponse orep = new OrderResponse();
	
		try {
			log.info(JSONObject.toJSONString(oreq));
			orep = settlementSdk.confirmWthdrawal(oreq);
			this.setIrep(irep, orep);
		} catch (Exception e) {
			irep.setErrorCode(-1);
			irep.setErrorMessage(AMPException.getStacktrace(e));
			
		}
		
		writeLog(ireq, irep, PayInterface.withdrawAudit.getInterfaceName());
		
		
		return irep;
	}
	
	@Override
	public BaseResp withdrawReject(WithdrawAuditRequest ireq) {
		
		BaseResp irep = new BaseResp();
		
		OrderRequest oreq = new OrderRequest();
		oreq.setUserOid(ireq.getInvestorOid());
		oreq.setOrderNo(ireq.getOrderCode());
		oreq.setType(ireq.getType());
		oreq.setUserType(ireq.getUserType());
		oreq.setOrderCreateTime(ireq.getIceOutTime());
		
		OrderResponse orep = new OrderResponse();
	
		try {
			log.info(JSONObject.toJSONString(oreq));
			orep = settlementSdk.unforzenUserWithdrawal(oreq);
			this.setIrep(irep, orep);
		} catch (Exception e) {
			irep.setErrorCode(-1);
			irep.setErrorMessage(AMPException.getStacktrace(e));
			
		}
		
		writeLog(ireq, irep, PayInterface.withdrawReject.getInterfaceName());
		
		
		return irep;
	}

	/**
	 * 充值
	 */
	public BaseResp pay(DepositRequest ireq, boolean isLog) {
		BaseResp irep = new BaseResp();
		
		OrderRequest oreq = new OrderRequest();
		oreq.setUserOid(ireq.getMemberId());
		oreq.setOrderNo(ireq.getIPayNo());
		oreq.setPayNo(ireq.getPayNo());
		oreq.setRequestNo(ireq.getRequestNo());
		oreq.setPaymentMethod(ireq.getPayMethod());
		oreq.setType(ireq.getType());
		oreq.setAmount(ireq.getOrderAmount());
		oreq.setRemark(ireq.getRemark());
		oreq.setDescribe(ireq.getDescribe());
		oreq.setOrderCreateTime(ireq.getOrderTime());
		oreq.setSmsCode(ireq.getSmsCode());
		oreq.setSystemSource(ireq.getSystemSource());
		oreq.setUserType(ireq.getUserType());
		
		OrderResponse orep = new OrderResponse();
		try {
			log.info(JSONObject.toJSONString(oreq));

			log.info("orderCode={}, startTime={}", ireq.getOrderCode(), DateUtil.getSqlCurrentDate());
			orep = settlementSdk.pay(oreq);
			log.info("orderCode={}, endTime={}", ireq.getOrderCode(), DateUtil.getSqlCurrentDate());
			this.setIrep(irep, orep);
		} catch (Exception e) {
			irep.setErrorCode(-1);
			irep.setErrorMessage(AMPException.getStacktrace(e));
		}
		
		
		if (isLog) {
			writeLog(ireq, irep, PayInterface.pay.getInterfaceName());
		}
		
		return irep;
	}
	
	private void writeLog(String content, String orderCode, String iPayNo, String handleType, BaseResp irep, String interfaceName) {
		PayLogReq logReq = new PayLogReq();
		logReq.setErrorCode(irep.getErrorCode());
		logReq.setErrorMessage(irep.getErrorMessage());
		logReq.setInterfaceName(interfaceName);
		logReq.setSendedTimes(1);
		logReq.setContent(content);
		logReq.setOrderCode(orderCode);
		logReq.setIPayNo(iPayNo);
		logReq.setHandleType(handleType);
		this.payLogService.createEntity(logReq);
	}
	
	
	
	private void writeLog(DepositbfRequest ireq, BaseResp irep, String interfaceName) {
		this.writeLog(JSONObject.toJSONString(ireq), ireq.getOrderCode(), ireq.getIPayNo(), PayLogEntity.PAY_handleType_applyCall, irep, interfaceName);
	}
	
	private void writeLog(OrderRegistrationRequest ireq, BaseResp irep, String interfaceName) {
		this.writeLog(JSONObject.toJSONString(ireq), ireq.getOrderCode(), ireq.getOrderCode(), PayLogEntity.PAY_handleType_applyCall, irep, interfaceName);
	}
	
	private void writeLog(WithdrawAuditRequest ireq, BaseResp irep, String interfaceName) {
		this.writeLog(JSONObject.toJSONString(ireq), ireq.getOrderCode(), ireq.getOrderCode(), PayLogEntity.PAY_handleType_withdrawAudit, irep, interfaceName);
	}
	private void writeLog(ApiDepositRequest ireq, BaseResp irep, String interfaceName) {
		this.writeLog(JSONObject.toJSONString(ireq), ireq.getOrderCode(), ireq.getIPayNo(), PayLogEntity.PAY_handleType_applyCall, irep, interfaceName);
	}
	
	

	private void writeLog(DepositRequest ireq, BaseResp irep, String interfaceName) {
		this.writeLog(JSONObject.toJSONString(ireq), ireq.getOrderCode(), ireq.getIPayNo(), PayLogEntity.PAY_handleType_applyCall, irep, interfaceName);
	}
	
	private void writeLog(WithdrawRequest ireq, BaseResp irep, String interfaceName) {
		this.writeLog(JSONObject.toJSONString(ireq), ireq.getOrderCode(), ireq.getIPayNo(), PayLogEntity.PAY_handleType_applyCall, irep, interfaceName);
	}
	
	private void setIrep(BaseResp irep, BaseResponse orep) {
		
		/** orep == null, 当接口返回为NULL时 */
		if (null == orep) {
			irep.setErrorCode(-1);
			irep.setErrorMessage("返回为空");
			return;
		}

		if (AccParam.ReturnCode.RC0000.toString().equals(orep.getReturnCode())) {
			irep.setErrorMessage(orep.getErrorMessage());
		} else {
			irep.setErrorCode(-1);
			irep.setErrorMessage(orep.getErrorMessage());
		}
	}
	
	@Override
	@Transactional
	public boolean tradeCallback(OrderNotifyReq ireq) {
		log.info("mimosa接收回调信息{}",JSONObject.toJSONString(ireq));
		if (!StrRedisUtil.setnx(redis, PAY_orderCode + ireq.getOrderCode(), ireq.getOrderCode())) {
			log.info("getOrderCode={} is dealing ....", ireq.getOrderCode());
			return false;
		}
		/** 设置失效时间 */
		KeyRedisUtil.expire(redis, PAY_orderCode + ireq.getOrderCode(), 10);
		
		boolean flag = true;
		/** 判断申请是否成功 */
		PayLogEntity isHasApplyCallSuccLog = this.payLogService.getSuccessPayAplly(ireq.getOrderCode());

		if (null == isHasApplyCallSuccLog) {
			log.info("getOrderCode={} have not success apply ", ireq.getOrderCode());
			StrRedisUtil.del(redis, PAY_orderCode + ireq.getOrderCode());
			return false;
		}

		/** 判断是否已成功回调 */
		PayLogEntity isHasCallBackSuccLog = this.payLogService.getPaySuccessNotify(ireq.getOrderCode());
		if (null != isHasCallBackSuccLog) {
			log.info("getOrderCode={} already callback ", ireq.getOrderCode());
			StrRedisUtil.del(redis, PAY_orderCode + ireq.getOrderCode());
			return true;
		}
		
		ireq.setOrderCode(isHasApplyCallSuccLog.getOrderCode());
		
		/** 投资人--充值 */
		if (ireq.getOrderCode().startsWith(this.seqEnv  + CodeConstants.PAYMENT_deposit)) {
			try {
				InvestorBankOrderEntity orderEntity = this.investorBankOrderService.findByOrderCode(ireq.getOrderCode());
				if (!InvestorBankOrderEntity.BANKORDER_orderStatus_toPay.equals(orderEntity.getOrderStatus())) {
					log.info("orderCode={} have not toPay ", ireq.getOrderCode());
					StrRedisUtil.del(redis, PAY_orderCode + ireq.getOrderCode());
					return false;
				}
				flag = investorBankOrderExtService.depositCallBack(ireq);
			} catch (Exception e) {
				log.error("投资人充值回调异常", e);
				flag = false;
			}
		} else if (ireq.getOrderCode().startsWith(this.seqEnv  + CodeConstants.PAYMENT_withdraw)) {
			/** 投资人--提现 */ 
			try {
				InvestorBankOrderEntity orderEntity = this.investorBankOrderService.findByOrderCode(ireq.getOrderCode());
				if (!InvestorBankOrderEntity.BANKORDER_orderStatus_toPay.equals(orderEntity.getOrderStatus())) {
					log.info("orderCode={} have not toPay ", ireq.getOrderCode());
					StrRedisUtil.del(redis, PAY_orderCode + ireq.getOrderCode());
					return false;
				}
				flag = this.investorBankOrderExtService.withdrawCallBack(ireq);
			} catch (Exception e) {
				log.error("投资人提现回调异常", e);
				flag = false;
			}
		} else if (ireq.getOrderCode().startsWith(this.seqEnv  + CodeConstants.PAYMENT_debitDeposit)) {
			/** 发行人--充值 */
			try {
				PublisherBankOrderEntity orderEntity = this.publisherBankOrderService.findByOrderCode(ireq.getOrderCode());
				if (!PublisherBankOrderEntity.BANKORDER_orderStatus_toPay.equals(orderEntity.getOrderStatus())) {
					log.info("orderCode={} have not toPay ", ireq.getOrderCode());
					StrRedisUtil.del(redis, PAY_orderCode + ireq.getOrderCode());
					return false;
				}
				flag = this.publisherDepositBankOrderService.depositCallback(ireq);
			} catch (Exception e) {
				log.error("发行人充值回调异常", e);
				flag = false;
			}
			
		} else if (ireq.getOrderCode().startsWith(this.seqEnv  + CodeConstants.PAYMENT_debitWithdraw)) {
			/** 发行人--提现 */
			try {
				PublisherBankOrderEntity orderEntity = this.publisherBankOrderService.findByOrderCode(ireq.getOrderCode());
				if (!PublisherBankOrderEntity.BANKORDER_orderStatus_toPay.equals(orderEntity.getOrderStatus())) {
					log.info("orderCode={} have not toPay ", ireq.getOrderCode());
					StrRedisUtil.del(redis, PAY_orderCode + ireq.getOrderCode());
					return false;
				}
				flag = this.publisherWithdrawBankOrderService.withdrawCallback(ireq);
			} catch (Exception e) {
				log.error("发行人提现回调异常", e);
				flag = false;
			}
		} else if (ireq.getOrderCode().startsWith(this.seqEnv + CodeConstants.PAYMENT_redeem)
				|| ireq.getOrderCode().startsWith(this.seqEnv + CodeConstants.PAYMENT_dividend)) {
			/** 投资人--赎回     现金分红回调*/
			try {
				InvestorTradeOrderEntity orderEntity = this.investorTradeOrderService.findByOrderCode(ireq.getOrderCode());
				if (!InvestorTradeOrderEntity.TRADEORDER_publisherCloseStatus_closeToPay.equals(orderEntity.getPublisherCloseStatus())) {
					log.info("orderCode={} have not closeToPay ", ireq.getOrderCode());
					StrRedisUtil.del(redis, PAY_orderCode + ireq.getOrderCode());
					return false;
				}
				flag = this.investorTradeOrderBatchPayService.notify(ireq);
			} catch (Exception e) {
				log.error("发行人赎回回调异常", e);
				flag = false;
			}
		} else {
			log.info("this.seqEnv配置不统一,当前Env为{},orderCode为{}", this.seqEnv, ireq.getOrderCode());
			return false;
		}
	
		PayLogReq logReq = new PayLogReq();
		logReq.setErrorCode(0);
		logReq.setErrorMessage(ireq.getErrorMessage());
		logReq.setInterfaceName(PayInterface.tradeCallback.getInterfaceName());
		logReq.setOrderCode(ireq.getOrderCode());
		logReq.setIPayNo(ireq.getOrderCode());
		logReq.setSendedTimes(1);
		logReq.setContent(JSONObject.toJSONString(ireq));
		logReq.setHandleType(PayLogEntity.PAY_handleType_notify);
		this.payLogService.createEntity(logReq);
		StrRedisUtil.del(redis, PAY_orderCode + ireq.getOrderCode());
		return flag;

	}
	
	@Override
	public boolean tradeCallback(OrderResponse orderResponse) {
		log.info(JSONObject.toJSONString(orderResponse));
		Assert.notNull(orderResponse, "orderResponse is null");
		if (StringUtil.isEmpty(orderResponse.getOrderNo()) || StringUtil.isEmpty(orderResponse.getReturnCode())) {
			throw new IllegalArgumentException("orderNo is empty or returnCode is empty");
		}

		OrderNotifyReq ireq = new OrderNotifyReq();
		ireq.setOrderCode(orderResponse.getOrderNo());
		ireq.setReturnCode(orderResponse.getReturnCode());
		ireq.setErrorMessage(orderResponse.getErrorMessage());
		return this.tradeCallback(ireq);
	}
	
	/**
	 * 绑卡申请 
	 */
	public UserBindCardApplyRep bindCardApply(UserBindCardApplyRequest ireq) {
		log.info("bind card apply：" + JSON.toJSONString(ireq));
		UserBindCardApplyRep irep = new UserBindCardApplyRep();
		ElementValidationRequest oreq  = new ElementValidationRequest();
		oreq.setUserOid(ireq.getMemberId());
		oreq.setRequestNo(ireq.getRequestNo());
		oreq.setBankName(ireq.getBankName());
		oreq.setRealName(ireq.getRealName());
		oreq.setCardNo(ireq.getCardNo());
		oreq.setPhone(ireq.getPhone());
		oreq.setCertificateNo(ireq.getCertificateNo());
		
		ElementValidaResponse orep = new ElementValidaResponse();
		try {
			
			orep = settlementSdk.bindApply(oreq);
			setIrep(irep, orep);
			if (0 == irep.getErrorCode()) {
				irep.setCardOrderId(orep.getCardOrderId());
			}
		} catch (Exception e) {
			irep.setErrorCode(-1);
			irep.setErrorMessage(AMPException.getStacktrace(e));
		}
		if (irep.getErrorCode() == 9907) {//已经绑卡返回提示
			throw GHException.getException(80034);
		}
		
		if (irep.getErrorCode() != 0) {
			throw new AMPException(irep.getErrorMessage());
		}
		//irep.setErrorCode(irep.getErrorCode());
		//irep.setErrorMessage(irep.getErrorMessage());
		return irep;
	}
	
	/**
	 * 绑卡确认
	 */
	public BaseResp bindCardConfirm(UserBindCardConfirmRequest ireq) {
		log.info("bind card confirm：" + JSON.toJSONString(ireq));
		BaseResp irep = new BaseResp();
		ElementValidationRequest oreq  = new ElementValidationRequest();
		oreq.setUserOid(ireq.getMemberId());
		oreq.setRequestNo(ireq.getRequestNo());
		oreq.setPhone(ireq.getPhone());
		oreq.setSmsCode(ireq.getSmsCode());
		oreq.setCardOrderId(ireq.getCardOrderId());
		
		ElementValidaResponse orep = new ElementValidaResponse();
		try {
			
			orep = settlementSdk.bindConfirm(oreq);
			setIrep(irep, orep);
		} catch (Exception e) {
			irep.setErrorCode(-1);
			irep.setErrorMessage(AMPException.getStacktrace(e));
		}
		
		if (irep.getErrorCode() != 0) {
			throw new AMPException(irep.getErrorMessage());
		}
		return irep;
	}
	
	/**
	 * 绑定银行卡
	 * @author wq 
	 */
	public UserBindCardApplyRep bindBankCard(UserBindCardApplyRequest ireq) {
		log.info("bind card apply：" + JSON.toJSONString(ireq));
		UserBindCardApplyRep irep = new UserBindCardApplyRep();
		ElementValidationRequest oreq  = new ElementValidationRequest();
		oreq.setUserOid(ireq.getMemberId());
		oreq.setRequestNo(ireq.getRequestNo());
		oreq.setBankName(ireq.getBankName());
		oreq.setRealName(ireq.getRealName());
		oreq.setCardNo(ireq.getCardNo());
		oreq.setPhone(ireq.getPhone());
		oreq.setCertificateNo(ireq.getCertificateNo());
		
		ElementValidaResponse orep = new ElementValidaResponse();
		try {
			orep = settlementSdk.bindCard(oreq);
			setIrep(irep, orep);
		} catch (Exception e) {
			irep.setErrorCode(-1);
			irep.setErrorMessage(AMPException.getStacktrace(e));
		}
		if (irep.getErrorCode() == 9907) {//已经绑卡返回提示
			throw GHException.getException(80034);
		}
		if (irep.getErrorCode() != 0) {
			throw new AMPException(irep.getErrorMessage());
		}
		return irep;
	}
	
	public BaseResp unbindCard(UserUnBindCardRequest ireq) {
		log.info("unbind card：" + JSON.toJSONString(ireq));
		BaseResp irep = new BaseResp();
		ElementValidationRequest oreq  = new ElementValidationRequest();
		oreq.setUserOid(ireq.getMemberId());
		oreq.setRequestNo(ireq.getRequestNo());
		oreq.setCardNo(ireq.getCardNo());
		oreq.setSystemSource(ireq.getSystemSource());
		
		ElementValidaResponse orep = new ElementValidaResponse();
		try {
			
			orep = settlementSdk.unlock(oreq);
			setIrep(irep, orep);
		} catch (Exception e) {
			irep.setErrorCode(-1);
			irep.setErrorMessage(AMPException.getStacktrace(e));
		}
		
		if (irep.getErrorCode() != 0) {
			throw new AMPException(irep.getErrorMessage());
		}
		return irep;
	}
	
	@Override
	public BaseResponse writerOffOrder(WriterOffOrderRequest req) {
		log.info(JSONObject.toJSONString(req));
		Assert.notNull(req);
		if (StringUtil.isEmpty(req.getOriginalRedeemOrderCode())) {
			WriteOffResponse rep = new WriteOffResponse();
			rep.setReturnCode("0001");
			rep.setErrorMessage("订单号为空");
			return rep;
		}
		WriteOffOrderNotifyReq ireq = new WriteOffOrderNotifyReq();
		ireq.setIPayNo(req.getOriginalRedeemOrderCode());
		return this.writerOffOrder(ireq);
	}

	

	
	public BaseResponse writerOffOrder(WriteOffOrderNotifyReq ireq) {
		
		WriteOffResponse rep = new WriteOffResponse();
		
		
		try {
			BankOrderRep iRep = new BankOrderRep();
			PayLogEntity log = this.payLogService.getSuccessPayAplly(ireq.getIPayNo());
			if (null == log) {
				rep.setReturnCode("0001");
				rep.setErrorMessage("提现申请不存在");
				return rep;
			}
			ireq.setOrderCode(log.getOrderCode());
			/** 投资人--提现 */
			if (ireq.getIPayNo().startsWith(this.seqEnv  + CodeConstants.PAYMENT_withdrawPayNo)) {
				InvestorBankOrderEntity bankOrder = this.investorBankOrderService.findByOrderCode(ireq.getOrderCode());
				if (!InvestorBankOrderEntity.BANKORDER_orderStatus_toPay.equals(bankOrder.getOrderStatus())) {
					rep.setReturnCode("0001");
					rep.setErrorMessage("订单非待支付，不能撤消");
					return rep;
				}
				if (!InvestorBankOrderEntity.BANKORDER_orderType_withdraw.equals(bankOrder.getOrderType())) {
					rep.setReturnCode("0002");
					rep.setErrorMessage("订单非提现订单");
					return rep;
				}
				
				
				iRep = this.investorBankOrderExtService.writeOffOrder(bankOrder);
			}
			/** 发行人--提现 */
			if (ireq.getIPayNo().startsWith(this.seqEnv  + CodeConstants.PAYMENT_debitWithdrawPayNo)) {
				PublisherBankOrderEntity bankOrder = this.publisherBankOrderService.findByOrderCode(ireq.getOrderCode());
				if (!PublisherBankOrderEntity.BANKORDER_orderStatus_toPay.equals(bankOrder.getOrderStatus())) {
					rep.setReturnCode("0001");
					rep.setErrorMessage("订单非待支付，不能撤消");
					return rep;
				}
				if (!PublisherBankOrderEntity.BANK_ORDER_ORDER_TYPE_withdraw.equals(bankOrder.getOrderType())) {
					rep.setReturnCode("0002");
					rep.setErrorMessage("订单非提现订单");
					return rep;
				}
				
				
				this.publisherWithdrawBankOrderService.notifyFail(bankOrder);
			}
			
			
			if (0 == iRep.getErrorCode()) {
				rep.setReturnCode(PayParam.ReturnCode.RC0000.toString());
				rep.setErrorMessage("撤消成功,用户余额已恢复");
				
				PayLogReq logReq = new PayLogReq();
				logReq.setErrorCode(0);
				logReq.setErrorMessage("支付失败");
				logReq.setInterfaceName(PayInterface.tradeCallback.getInterfaceName());
				logReq.setOrderCode(ireq.getOrderCode());
				logReq.setIPayNo(log.getIPayNo());
				logReq.setSendedTimes(1);
				logReq.setContent(JSONObject.toJSONString(ireq));
				logReq.setHandleType(PayLogEntity.PAY_handleType_notify);
				this.payLogService.createEntity(logReq);
				
				
			} else {
				rep.setReturnCode("0001");
				rep.setErrorMessage(iRep.getErrorMessage());
			}
			
		} catch (Exception e) {
			rep.setReturnCode("0001");
			rep.setErrorMessage(e.getMessage());
		}
		
		return rep;
	}
	
	/**
	 * 充值冲正(由回调失败变成回调成功)
	 */
	@Override
	public boolean changeOrderStatus(InteractiveRequest req) {
		return false;
	}

	@Override
	public List<UserAccountInfoResponse> getUserAccountInfo(String[] arg0) {
		return null;
	}
	
	
	/**
	 * 充值解冻
	 */
	@Override
	public boolean notifyChangeAccountBalance(InteractiveRequest arg0) {
		investorBaseAccountService.updateBalance(investorBaseAccountService.findByMemberId(arg0.getUserOid()));
		return true;
	}

	@Override
	public DepositOrderQueryResponse depositConfirm(DepositOrderQueryRequest ireq) {
		DepositOrderQueryResponse irep = new DepositOrderQueryResponse();
		DepositConfirmRequest oreq = new DepositConfirmRequest();
		oreq.setUserOid(ireq.getInvestorOid());
		oreq.setTradeType(ireq.getOrderType());
		oreq.setOrderNo(ireq.getIPayNo());
		DepositConfirmResponse orep = this.settlementSdk.depositConfirm(oreq);
		if (!PayParam.ReturnCode.RC0000.toString().equals(orep.getReturnCode())) {
			throw new AMPException(orep.getErrorMessage() + "(" + orep.getReturnCode() + ")");
		}
		if ("S".equals(orep.getStatus())) {
			irep.setOrderStatus("done");
		} else {
			irep.setOrderStatus("payFailed");
		}
		return irep;
	}

	@Override
	public QueryOrdersResponse queryHoldingOrders(QueryOrdersRequest req) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public BaseResp resendSms(ResendSmsReq req,String uid) {
		BaseResp irep = new BaseResp();
		OrderRequest orderRequest = new OrderRequest();
		orderRequest.setPayNo(req.getPayNo());
		orderRequest.setUserOid(uid);
		
		OrderResponse orep = new OrderResponse();
		try {
			orep = settlementSdk.reValidPay(orderRequest);
		} catch (Exception e) {
			irep.setErrorCode(-1);
			irep.setErrorMessage(AMPException.getStacktrace(e));
		}
		
		return irep;
	}
	
	/**
	 * 发行人绑定银行卡
	 * @author wrw 
	 */
	public UserBindCardApplyRep spBindBankCard(UserBindCardApplyRequest ireq) {
		log.info("publisher bind card ：" + JSON.toJSONString(ireq));
		UserBindCardApplyRep irep = new UserBindCardApplyRep();
		ElementValidationRequest oreq  = new ElementValidationRequest();
		oreq.setUserOid(ireq.getMemberId());
		oreq.setRequestNo(ireq.getRequestNo());
		oreq.setBankName(ireq.getBankName());
		oreq.setRealName(ireq.getRealName());
		oreq.setCardNo(ireq.getCardNo());
		oreq.setPhone(ireq.getPhone());
		oreq.setCertificateNo(ireq.getCertificateNo());
		
		ElementValidaResponse orep = new ElementValidaResponse();
		try {
			orep = settlementSdk.spBindCard(oreq);
			setIrep(irep, orep);
		} catch (Exception e) {
			irep.setErrorCode(-1);
			irep.setErrorMessage(AMPException.getStacktrace(e));
		}
		if (irep.getErrorCode() == 9907) {//已经绑卡返回提示
			throw GHException.getException(80034);
		}
		if (irep.getErrorCode() != 0) {
			throw new AMPException(irep.getErrorMessage());
		}
		return irep;
	}

}
