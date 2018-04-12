package com.guohuai.mmp.investor.sonoperate;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guohuai.mmp.investor.bankorder.InvestorBankOrderEntity;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountDao;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountEntity;

@Service
@Transactional
public class SonOperateService {
	@Autowired
	private InvestorBaseAccountDao investorBaseAccountDao;
	@Autowired
	private SonOperateDao sonOperateDao;
	
	public void addOperate(InvestorBankOrderEntity iboe,String operate){
		//qi修改 判断是否是主账号切换到子账号操作
		InvestorBaseAccountEntity isae = this.investorBaseAccountDao.findByMemberId(iboe.getInvestorBaseAccount().getOid());
		if (null != isae.getMarkId() && isae.getMarkId() != "") {
			SonOperateEntity soe = new SonOperateEntity();
			soe.setAction(operate);
			soe.setPid(isae.getMarkId());
			soe.setSid(isae.getMemberId());
			soe.setBankorderoid(iboe.getOid());
			this.sonOperateDao.save(soe);
		}
	}

}
