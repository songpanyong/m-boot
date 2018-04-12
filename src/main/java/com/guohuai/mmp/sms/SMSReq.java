package com.guohuai.mmp.sms;

import org.hibernate.validator.constraints.NotBlank;

import com.guohuai.basic.component.ext.web.parameter.validation.Enumerations;

@lombok.Data
public class SMSReq {

	@NotBlank(message = "手机号码不能为空！")
	String phone;

	@Enumerations(values = { "regist", "login", "forgetlogin", "forgetpaypwd", "normal","bindcard"}, message = "短信类型参数有误！")
	String smsType;
	
	String veriCode;
	
	String[] values;
	
	/** 图形验证码 */
	String imgvc;
}
