package com.guohuai.mmp.investor.bankorder;

import org.hibernate.validator.constraints.NotEmpty;

import lombok.NoArgsConstructor;

@lombok.Data
@NoArgsConstructor
public class ResendSmsReq {
	@NotEmpty(message = "支付流水号不能为空")
	private String payNo;
	
	@NotEmpty(message = "用户不能为空")
	private String investorOid;
}
