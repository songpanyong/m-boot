package com.guohuai.mmp.investor.bankorder.apply;

import java.math.BigDecimal;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.StringUtil;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountEntity;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountService;
import com.guohuai.mmp.platform.payment.DepositApplyRep;
import com.guohuai.mmp.platform.payment.DepositApplyRequest;
import com.guohuai.mmp.platform.payment.PayParam;
import com.guohuai.mmp.platform.payment.Payment;
import com.guohuai.mmp.publisher.baseaccount.PublisherBaseAccountEntity;
import com.guohuai.mmp.publisher.baseaccount.loginacc.PublisherLoginAccService;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class InvestorDepositApplyService {

	
	@Autowired
	private InvestorDepositApplyDao investorDepositApplyDao;
	@Autowired
	private InvestorBaseAccountService investorBaseAccountService;
	@Autowired
	private PublisherLoginAccService publisherLoginAccService;
	@Autowired
	private Payment paymentServiceImpl;
	
	@Value("${depositamount}")
	String depositAmount ;
	
	public BaseResp depositApply4SPV(ApplyReq req, String investorOid) {
		PublisherBaseAccountEntity baseAccount = this.publisherLoginAccService.findByLoginAcc(investorOid);
		
		DepositApplyRequest ireq = new DepositApplyRequest();
		ireq.setMemberId(baseAccount.getMemberId());
		ireq.setOrderAmount(req.getOrderAmount());
		ireq.setSystemSource(PayParam.SystemSource.MIMOSA.toString());
		ireq.setRequestNo(StringUtil.uuid());
		DepositApplyRep irep = this.paymentServiceImpl.validPay(ireq);
		
		
		InvestorDepositApplyEntity depositApply = new InvestorDepositApplyEntity();
		depositApply.setInvestorOid(investorOid);
		depositApply.setOrderAmount(req.getOrderAmount());
		depositApply.setPayNo(irep.getPayNo());
		depositApply.setErrorCode(irep.getErrorCode());
		depositApply.setErrorMessage(JSONObject.toJSONString(irep));
		depositApply.setSendObj(JSONObject.toJSONString(ireq));
		this.saveEntity(depositApply);
		return irep;
	}

	public BaseResp depositApply(ApplyReq req, String investorOid) {
		
		int a = req.getOrderAmount().compareTo(new BigDecimal(depositAmount));
		if(a>=0){
			InvestorBaseAccountEntity baseAccount = this.investorBaseAccountService.findOne(investorOid);
			
			DepositApplyRequest ireq = new DepositApplyRequest();//充值申请
			ireq.setMemberId(baseAccount.getMemberId());
			ireq.setOrderAmount(req.getOrderAmount());
			ireq.setSystemSource(PayParam.SystemSource.MIMOSA.toString());
			ireq.setRequestNo(StringUtil.uuid());
			DepositApplyRep irep = this.paymentServiceImpl.validPay(ireq);
			
			
			InvestorDepositApplyEntity depositApply = new InvestorDepositApplyEntity();
			depositApply.setInvestorOid(investorOid);
			depositApply.setOrderAmount(req.getOrderAmount());
			depositApply.setPayNo(irep.getPayNo());
			depositApply.setErrorCode(irep.getErrorCode());
			depositApply.setErrorMessage(JSONObject.toJSONString(irep));
			depositApply.setSendObj(JSONObject.toJSONString(ireq));
			this.saveEntity(depositApply);
			log.info("先锋返回-预支付{}",JSON.toJSONString(irep));
			return irep;
		}else{
			throw new AMPException("充值金额最低"+depositAmount+"元");
		}
		
	}

	private InvestorDepositApplyEntity saveEntity(InvestorDepositApplyEntity entity) {
		return investorDepositApplyDao.save(entity);
	}

	public InvestorDepositApplyEntity findByPayNoAndOrderAmountAndInvestorOid(String payNo,
			BigDecimal orderAmount, String investorOid) {
		InvestorDepositApplyEntity entity = this.investorDepositApplyDao
				.findByPayNoAndOrderAmountAndInvestorOid(payNo, orderAmount, investorOid);
		if (null == entity) {
			// error.define[30073]=金额与支付流水号不符(CODE:30073)
			throw new AMPException(30073);
		}
		return entity;
	}
	
	
	
	
	
}
