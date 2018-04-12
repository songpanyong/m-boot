package com.guohuai.mmp.investor.baseaccount;

import com.guohuai.basic.component.ext.web.BaseResp;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
@lombok.Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class InvestorBaseAccountIsLoginRep extends BaseResp {

	boolean islogin;
		
	String investorOid;
}
