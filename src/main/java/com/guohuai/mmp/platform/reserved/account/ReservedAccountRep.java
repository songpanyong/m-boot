package com.guohuai.mmp.platform.reserved.account;

import com.guohuai.basic.component.ext.web.BaseResp;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@lombok.Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class ReservedAccountRep extends BaseResp {
	
	String retHtml;
}
