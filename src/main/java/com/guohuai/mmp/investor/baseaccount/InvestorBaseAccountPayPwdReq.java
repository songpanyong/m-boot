package com.guohuai.mmp.investor.baseaccount;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
@lombok.Data
public class InvestorBaseAccountPayPwdReq {

	private String investorOid;
	
	@NotBlank(message = "交易密码不能为空！")
	@Length(min = 6, max = 6, message = "交易密码位数不正确！")
	private String payPwd;
	
	// 用户手机号
	private String userAcc;
	
	private String vericode;
	
	// 旧交易密码
	private String oldPayPwd;
	
}
