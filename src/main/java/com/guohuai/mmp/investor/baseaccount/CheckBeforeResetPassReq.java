package com.guohuai.mmp.investor.baseaccount;

import javax.validation.constraints.NotNull;

import com.guohuai.basic.component.ext.web.BaseResp;

@lombok.Data
public class CheckBeforeResetPassReq extends BaseResp{

	private String investorOid;
	@NotNull
	private String loginPassword;
	
	private String idCardNum;
	private String bankCardNum;
	
}
