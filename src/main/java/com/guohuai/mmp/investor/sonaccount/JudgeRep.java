package com.guohuai.mmp.investor.sonaccount;

import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountIsLoginRep;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@lombok.Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class JudgeRep extends BaseResp {
	
	private Boolean islogin;
	
	private  String InvestorOid;
	
	private Boolean isSon;
	
	private Boolean hasSon;
	
	private String nackName;
	
	private String isAdult;
	
}
