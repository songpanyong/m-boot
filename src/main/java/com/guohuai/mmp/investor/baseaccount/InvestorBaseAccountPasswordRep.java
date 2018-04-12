package com.guohuai.mmp.investor.baseaccount;

import com.guohuai.basic.component.ext.web.BaseResp;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@lombok.Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class InvestorBaseAccountPasswordRep extends BaseResp {

	private String investorOid;

	public InvestorBaseAccountPasswordRep(InvestorBaseAccountEntity account) {
		super();
		this.investorOid = account.getOid();
	}
	
}
