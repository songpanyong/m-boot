package com.guohuai.mmp.platform.payment;

import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.mmp.investor.bankorder.ResendSmsReq;
import com.guohuai.mmp.platform.accment.UserBindCardApplyRep;
import com.guohuai.mmp.platform.accment.UserBindCardApplyRequest;
import com.guohuai.mmp.platform.accment.UserBindCardConfirmRequest;
import com.guohuai.mmp.platform.accment.UserUnBindCardRequest;
import com.guohuai.settlement.api.SettlementCallBackApi;

public interface Payment extends SettlementCallBackApi {
	
	
	/**
	 * 订单查询
	 */
	public BaseResp queryPay(QueryPayRequest ireq);
	
	/**
	 * 充值申请，获取验证码
	 */
	public DepositApplyRep validPay(DepositApplyRequest ireq);
	
	/**
	 * 充值
	 */
	public BaseResp depositPay(DepositRequest ireq);
	
	/**
	 * 无码充值
	 */
	public BaseResp depositbfPay(DepositbfRequest ireq);
	
	/**
	 * 充值
	 */
	public BaseResp bfpay(DepositbfRequest ireq, boolean isLog);
	
	/**
	 * 充值
	 */
	public PayHtmlRep apiDepositPay(ApiDepositRequest ireq);
	
	/**
	 * API充值
	 */
	public PayHtmlRep apiPay(ApiDepositRequest ireq, boolean isLog);
	
	/**
	 * 提现
	 */
	public BaseResp withdrawPay(WithdrawRequest ireq);
	
	/**
	 * 提现
	 */
	public BaseResp payee(WithdrawRequest ireq, boolean isLog);

	/**
	 * 充值
	 */
	public BaseResp pay(DepositRequest ireq, boolean isLog);
	
	/**
	 * 绑卡申请 
	 */
	public UserBindCardApplyRep bindCardApply(UserBindCardApplyRequest ireq);
	
	/**
	 * 绑卡确认
	 */
	public BaseResp bindCardConfirm(UserBindCardConfirmRequest ireq);
	
	/**
	 * 绑定银行卡
	 * @author wq
	 */
	public UserBindCardApplyRep bindBankCard(UserBindCardApplyRequest ireq);
	
	/**
	 * 发行人绑定银行卡
	 * @author wrw
	 * 
	 * */
	public UserBindCardApplyRep spBindBankCard(UserBindCardApplyRequest ireq);
	
	public BaseResp unbindCard(UserUnBindCardRequest ireq);
	
	public boolean tradeCallback(OrderNotifyReq ireq);

	public DepositOrderQueryResponse depositConfirm(DepositOrderQueryRequest ireq);
	
	public BaseResp withdrawPass(WithdrawAuditRequest ireq);
	
	public BaseResp withdrawReject(WithdrawAuditRequest ireq);	
	/**
	 * 重发短信
	 * @param req
	 * @return
	 */
	public BaseResp resendSms(ResendSmsReq req,String uid);
}
