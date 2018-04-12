package com.guohuai.mmp.platform.payment;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.guohuai.basic.common.StringUtil;
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
import com.guohuai.settlement.api.SettlementCallBackApi;
import com.guohuai.settlement.api.SettlementSdk;
import com.guohuai.settlement.api.request.ElementValidationRequest;
import com.guohuai.settlement.api.request.InteractiveRequest;
import com.guohuai.settlement.api.request.OrderRequest;
import com.guohuai.settlement.api.request.QueryOrdersRequest;
import com.guohuai.settlement.api.request.WriterOffOrderRequest;
import com.guohuai.settlement.api.response.BaseResponse;
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
public class PaymentIsolationService implements Payment {
	@Override
	public boolean tradeCallback(OrderResponse orderResponse) {
		
		return true;
	}

	@Override
	public BaseResponse writerOffOrder(WriterOffOrderRequest req) {
		
		return new BaseResponse();
	}

	@Override
	public boolean changeOrderStatus(InteractiveRequest req) {
		
		return true;
	}

	@Override
	public List<UserAccountInfoResponse> getUserAccountInfo(String[] memberId) {
		
		return null;
	}

	@Override
	public boolean notifyChangeAccountBalance(InteractiveRequest req) {
		
		return true;
	}

	@Override
	public BaseResp queryPay(QueryPayRequest ireq) {
		return new BaseResp();
	}

	@Override
	public DepositApplyRep validPay(DepositApplyRequest ireq) {
		DepositApplyRep irep = new DepositApplyRep();
		irep.setPayNo("12580");
		return irep;
	}

	@Override
	public BaseResp depositPay(DepositRequest ireq) {
		return this.pay(ireq, true);
	}

	@Override
	public BaseResp depositbfPay(DepositbfRequest ireq) {
		return this.bfpay(ireq, true);
	}

	@Override
	public BaseResp bfpay(DepositbfRequest ireq, boolean isLog) {
		return new BaseResp();
	}

	@Override
	public PayHtmlRep apiDepositPay(ApiDepositRequest ireq) {
		return this.apiPay(ireq, true);
	}

	@Override
	public PayHtmlRep apiPay(ApiDepositRequest ireq, boolean isLog) {
		return new PayHtmlRep();
	}

	@Override
	public BaseResp withdrawPay(WithdrawRequest ireq) {
		return this.payee(ireq, true);
	}

	@Override
	public BaseResp payee(WithdrawRequest ireq, boolean isLog) {
		return new BaseResp();
	}

	@Override
	public BaseResp pay(DepositRequest ireq, boolean isLog) {
		return new BaseResp();
	}

	@Override
	public UserBindCardApplyRep bindCardApply(UserBindCardApplyRequest ireq) {
		UserBindCardApplyRep irep = new UserBindCardApplyRep();
		irep.setCardOrderId("12580");
		return irep;
	}

	@Override
	public BaseResp bindCardConfirm(UserBindCardConfirmRequest ireq) {
		return new BaseResp();
	}
	
	@Override
	public UserBindCardApplyRep bindBankCard(UserBindCardApplyRequest ireq) {
		UserBindCardApplyRep irep = new UserBindCardApplyRep();
		return irep;
	}

	@Override
	public BaseResp unbindCard(UserUnBindCardRequest ireq) {
		return new BaseResp();
	}

	@Override
	public boolean tradeCallback(OrderNotifyReq ireq) {
		
		return false;
	}

	@Override
	public DepositOrderQueryResponse depositConfirm(DepositOrderQueryRequest ireq) {
		return new DepositOrderQueryResponse();
	}

	@Override
	public QueryOrdersResponse queryHoldingOrders(QueryOrdersRequest req) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public BaseResp withdrawPass(WithdrawAuditRequest ireq) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BaseResp withdrawReject(WithdrawAuditRequest ireq) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public BaseResp resendSms(ResendSmsReq req,String uid) {
		return new BaseResp();
	}

	@Override
	public UserBindCardApplyRep spBindBankCard(UserBindCardApplyRequest ireq) {
		
		return null;
	}

}
