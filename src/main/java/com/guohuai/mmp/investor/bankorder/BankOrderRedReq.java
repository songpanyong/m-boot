package com.guohuai.mmp.investor.bankorder;

import org.hibernate.validator.constraints.NotBlank;

@lombok.Data
public class BankOrderRedReq {
	
	@NotBlank(message = "手机号不能为空！")
	private String phoneNum;
	
	@NotBlank(message = "红包编号不能为空！")
	private String couponId;
}
