package com.guohuai.mmp.investor.bankorder;

import java.math.BigDecimal;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.DateUtil;
import com.guohuai.mmp.investor.bankorder.apply.InvestorDepositApplyService;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountEntity;
import com.guohuai.mmp.investor.orderlog.OrderLogReq;
import com.guohuai.mmp.investor.orderlog.OrderLogService;
import com.guohuai.mmp.investor.sonoperate.SonOperateService;
import com.guohuai.mmp.ope.api.OpeSelectApiService;
import com.guohuai.mmp.platform.payment.OrderNotifyReq;
import com.guohuai.mmp.platform.payment.PayHtmlRep;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class InvestorBankOrderExtService {
	
	@Autowired
	private InvestorWithdrawBankOrderService investorWithdrawBankOrderService;
	@Autowired
	private InvestorDepositBankOrderService investorDepositBankOrderService;
	@Autowired
	private InvestorBankOrderService investorBankOrderService;
	@Autowired
	private SonOperateService sonOperateService;
	@Autowired
	private InvestorDepositApplyService investorDepositApplyService;
	@Autowired
	private OpeSelectApiService opeSelectApiService;
	@Autowired
	private OrderLogService orderLogService;
	
	@Value("${withdrawamount}")
	private String widthdrawAmount;
	
	public BankOrderRep withdraw(WithdrawBankOrderReq bankOrderReq, String uid) {
		BankOrderRep rep = new BankOrderRep(); 
		int a = bankOrderReq.getOrderAmount().compareTo(new BigDecimal(widthdrawAmount));
		if(a>=0){
			InvestorBankOrderEntity bankOrder = investorBankOrderService.createWithdrawNormalBankOrder(bankOrderReq, uid);
			this.sonOperateService.addOperate(bankOrder,"withdraw"); //qi修改 增加主子账户操作记录
			try {
				rep = investorWithdrawBankOrderService.withdraw(bankOrder.getOrderCode());
			} catch (Exception e) {
				
				log.error(e.getMessage(), e);
				rep.setErrorCode(-1);
				rep.setErrorMessage(e.getMessage());
				rep.setOrderStatus(InvestorBankOrderEntity.BANKORDER_orderStatus_submitFailed);
				
			}
			this.investorBankOrderService.withdrawThen(bankOrder.getOrderCode(), rep);
			log.info("先锋返回-预支付{}",JSON.toJSONString(rep));
			return rep;
		}else{
			throw new AMPException("提现金额最低"+ widthdrawAmount+"元");
		}
	}
	
	
	public BankOrderRep writeOffOrder(InvestorBankOrderEntity bankOrder) {
		BankOrderRep rep = new BankOrderRep();

		OrderLogReq orderLog = new OrderLogReq();
		
		try {
			rep = investorWithdrawBankOrderService.notifyFail(bankOrder);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			rep.setErrorCode(-1);
			rep.setErrorMessage(AMPException.getStacktrace(e));
			orderLog.setErrorCode(-1);
			orderLog.setErrorMessage(AMPException.getStacktrace(e));
			
		}
		
		orderLog.setOrderCode(bankOrder.getOrderCode());
		orderLog.setOrderType(bankOrder.getOrderType());
		orderLog.setOrderStatus("writeOffOrder");
		orderLogService.createOrderLog(orderLog);

		return rep;
	}
	
	@Transactional
	public BankOrderRep deposit(DepositBankOrderReq bankOrderReq, String investorOid) {
		log.error("DepositBankOrderReq：{}|===|investorOid:{}",bankOrderReq,investorOid);
		investorDepositApplyService.findByPayNoAndOrderAmountAndInvestorOid(bankOrderReq.getPayNo(),
				bankOrderReq.getOrderAmount(), investorOid);
		InvestorBankOrderEntity bankOrder = investorBankOrderService.createDepositBankOrder(bankOrderReq, investorOid);
		this.sonOperateService.addOperate(bankOrder,"deposit"); //qi修改 增加主子账户操作记录
		BankOrderRep rep = new BankOrderRep();
		try {
			rep = investorDepositBankOrderService.deposit(bankOrderReq, bankOrder.getOrderCode());
		} catch (AMPException ampException) {
			log.info(ampException.getMessage(), ampException);
			rep.setOrderStatus(InvestorBankOrderEntity.BANKORDER_orderStatus_submitFailed);
			rep.setErrorCode(ampException.getCode());
			rep.setErrorMessage(ampException.getMessage());
		} catch (Exception e) {
			log.info(e.getMessage(), e);
			rep.setOrderStatus(InvestorBankOrderEntity.BANKORDER_orderStatus_submitFailed);
			rep.setErrorCode(-1);
			rep.setErrorMessage(AMPException.getStacktrace(e));
		}
		
		investorBankOrderService.depositThen(bankOrder.getOrderCode(), rep);
		saveFailRecharge(bankOrder, rep);	// 生成充值未成功表
		log.info("先锋返回-确认支付{}",JSON.toJSONString(rep));
		return rep;
	}
	
	
	@Transactional
	public BankOrderRep depositbf(DepositBankOrderbfReq bankOrderReq, String investorOid) {
		
		InvestorBankOrderEntity bankOrder = investorBankOrderService.createDepositBankOrderbf(bankOrderReq, investorOid);
		BankOrderRep rep = new BankOrderRep();
		try {
			rep = investorDepositBankOrderService.depositbf(bankOrderReq, bankOrder.getOrderCode());
		} catch (AMPException ampException) {
			log.info(ampException.getMessage(), ampException);
			rep.setOrderStatus(InvestorBankOrderEntity.BANKORDER_orderStatus_submitFailed);
			rep.setErrorCode(ampException.getCode());
			rep.setErrorMessage(ampException.getMessage());
		} catch (Exception e) {
			log.info(e.getMessage(), e);
			rep.setOrderStatus(InvestorBankOrderEntity.BANKORDER_orderStatus_submitFailed);
			rep.setErrorCode(-1);
			rep.setErrorMessage(AMPException.getStacktrace(e));
		}
		
		investorBankOrderService.depositThen(bankOrder.getOrderCode(), rep);
		saveFailRecharge(bankOrder, rep);	// 生成充值未成功表
		log.info("先锋返回-代扣{}",JSON.toJSONString(rep));
		return rep;
	}
	
	@Transactional
	public PayHtmlRep apiDeposit(ApiDepositBankOrderReq bankOrderReq, String uid) {
		PayHtmlRep rep = new PayHtmlRep();
		InvestorBankOrderEntity bankOrder = investorBankOrderService.createDepositBankOrder(bankOrderReq, uid);
		try {
			rep = investorDepositBankOrderService.apiDeposit(bankOrderReq, bankOrder.getOrderCode());
		} catch (AMPException ampException) {
			log.info(ampException.getMessage(), ampException);
			rep.setOrderStatus(InvestorBankOrderEntity.BANKORDER_orderStatus_submitFailed);
			rep.setErrorCode(ampException.getCode());
			rep.setErrorMessage(ampException.getMessage());
		} catch (Exception e) {
			log.info(e.getMessage(), e);
			rep.setOrderStatus(InvestorBankOrderEntity.BANKORDER_orderStatus_submitFailed);
			rep.setErrorCode(-1);
			rep.setErrorMessage(AMPException.getStacktrace(e));
		}
		investorBankOrderService.depositThen(bankOrder.getOrderCode(), rep);
		saveFailRecharge(bankOrder, rep);	// 生成充值未成功表
		return rep;
	}
	
	// 生成充值未成功表
	private void saveFailRecharge(InvestorBankOrderEntity bankOrder, BaseResp rep) {
		try{
//			OpeUserInfoRep user = opeUserCenterApi.getLoginUserInfo(bankOrder.getInvestorBaseAccount().getUserOid());
			InvestorBaseAccountEntity account = bankOrder.getInvestorBaseAccount();
			opeSelectApiService.createFailRecharge(account.getOid(), account.getPhoneNum(), account.getRealName(), account.getChannelid(), rep.getErrorMessage());
		}catch (Exception e) {
			log.error("用户充值时，生成充值为成功表失败，失败原因："+e.getMessage());
		}
	}


	/**
	 * 体现回调
	 */
	@Transactional(value = TxType.REQUIRES_NEW)
	public boolean withdrawCallBack(OrderNotifyReq ireq) {
		
		OrderLogReq orderLog = new  OrderLogReq();
		boolean retFlag = true;
		try {
			retFlag = this.investorWithdrawBankOrderService.withdrawCallBack(ireq);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			retFlag = false;
			
			orderLog.setErrorCode(-1);
			orderLog.setErrorMessage(AMPException.getStacktrace(e));
		}
		orderLog.setOrderCode(ireq.getOrderCode());
		orderLog.setOrderType(InvestorBankOrderEntity.BANKORDER_orderType_withdraw);
		orderLog.setOrderStatus("withdrawCallBack");
		orderLogService.createOrderLog(orderLog);
		return retFlag;
	}
	
	/**
	 * 充值回调--投资人
	 */
	@Transactional
	public boolean depositCallBack(OrderNotifyReq ireq) {
		
		OrderLogReq orderLog = new  OrderLogReq();
		boolean retFlag = true;
		try {
			log.info("orderCode={} deal start, time={}", ireq.getOrderCode(), DateUtil.getSqlCurrentDate());
			this.investorDepositBankOrderService.depositCallBack(ireq);
			log.info("orderCode={} deal start, time={}", ireq.getOrderCode(), DateUtil.getSqlCurrentDate());
			
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			retFlag = false;
		
			orderLog.setErrorCode(-1);
			orderLog.setErrorMessage(AMPException.getStacktrace(e));
		}
		orderLog.setOrderCode(ireq.getOrderCode());
		orderLog.setOrderType(InvestorBankOrderEntity.BANKORDER_orderType_deposit);
		orderLog.setOrderStatus("depositCallBack");
		orderLogService.createOrderLog(orderLog);
		
		return retFlag;
	}



	
	
}
