package com.guohuai.mmp.investor.sonaccount;


import com.guohuai.basic.component.ext.web.BaseResp;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@lombok.Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class RegistSonAccountRep extends BaseResp{
	
	private String memberId;
	private Boolean isAdult;
}
