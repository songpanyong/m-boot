package com.guohuai.mmp.investor.baseaccount;

import com.guohuai.basic.component.ext.web.BaseResp;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
@lombok.Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class InvestorBaseAccountIsRegistRep extends BaseResp {

	private String phoneNum;
	
	private boolean regist;

	private String investorOid;
	
	private boolean isAuthority;
}
