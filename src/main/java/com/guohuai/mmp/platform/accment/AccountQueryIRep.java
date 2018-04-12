package com.guohuai.mmp.platform.accment;

import com.guohuai.basic.component.ext.web.BaseResp;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@lombok.Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class AccountQueryIRep extends BaseResp {
	private String userOid;
	
}
