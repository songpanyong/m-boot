package com.guohuai.mmp.publisher.bankorder;

import com.guohuai.basic.component.ext.web.BaseResp;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@lombok.Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BankOrderRep extends BaseResp {
	
	private String bankOrderOid;
}
