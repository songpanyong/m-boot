package com.guohuai.mmp.investor.sonaccount;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guohuai.basic.component.ext.web.BaseController;
import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.component.exception.AMPException;
import com.guohuai.mmp.investor.bank.BankAddReq;
import com.guohuai.mmp.investor.bank.BankService;
import com.guohuai.mmp.investor.bank.BindBankCardApplyReq;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountDao;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountEntity;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountService;


@Service
@Transactional
public class SonAccountBindCardService  extends BaseController{
	
	@Autowired
    private BankService bankService;
	
	@Autowired
	InvestorBaseAccountService investorBaseAccountService;
	
	@Autowired
	InvestorBaseAccountDao investorBaseAccountDao;

	/** 子账号的绑卡申请  */
	public BaseResp sonBindApply(SonBindBankCardApplyReq req) {
		InvestorBaseAccountEntity sonAccount = this.investorBaseAccountService.findByMemberId(req.getInvestorOid());
		if(sonAccount!=null&&sonAccount.getRealName()!=null){
			BindBankCardApplyReq ibareq = new BindBankCardApplyReq();
			ibareq.setBankName(req.getBankName());
			ibareq.setCardNo(req.getCardNo());
			ibareq.setCertificateNo(sonAccount.getIdNum());
			ibareq.setPhone(req.getPhone());
			ibareq.setRealName(sonAccount.getRealName());
			
			BaseResp rep = this.bankService.bindApply(ibareq,req.getInvestorOid());
			return rep;
		}else{
			throw new AMPException("该子账户不存在或者未实名");
		}
		
		
	}
	
	/** 子账号绑卡  */
	public BaseResp sonAddCard(SonAddCardReq req) {
		BankAddReq bareq = new BankAddReq();
		bareq.setCardOrderId(req.getCardOrderId());
		bareq.setSmsCode(req.getSmsCode());
		BaseResp rep = this.bankService.add(bareq,req.getInvestorOid() );
		return rep;
	}

	/**
	 * 
	 * 账户的解绑卡
	 * */
	public BaseResp removeBank() {
		BaseResp rep = new BaseResp();
		String investorOid = super.getLoginUser();
		InvestorBaseAccountEntity baseAccount =this.investorBaseAccountDao.findByOid(investorOid);
		if(baseAccount!=null){
				if(baseAccount.getMarkId()!=null){
					//当前登录为子账号
					String operator = baseAccount.getMarkId();//主账号为操作者
				BaseResp br = 	this.bankService.syncRemoveSettleBank(investorOid, operator);
				
				}else{
					//当前登录为主账号
				BaseResp br = 	this.bankService.syncRemoveSettleBank(investorOid, investorOid);
			
				}	
				
				return rep;
		}else{
			throw new AMPException("当前账户不存在！");
		}
		
	}

	
}
