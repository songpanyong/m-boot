package com.guohuai.mmp.platform.payment;

import com.guohuai.basic.component.ext.web.BaseResp;

import lombok.EqualsAndHashCode;

@lombok.Data
@EqualsAndHashCode(callSuper = false)
public class DepositApplyRep extends BaseResp {

	/**
	 * 支付流水号
	 */
	private String payNo;

	
	
}
