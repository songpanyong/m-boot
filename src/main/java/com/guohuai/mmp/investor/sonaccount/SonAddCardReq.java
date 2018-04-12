package com.guohuai.mmp.investor.sonaccount;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import lombok.NoArgsConstructor;

@lombok.Data
@NoArgsConstructor
public class SonAddCardReq {
		
		@NotBlank(message = "绑卡编号不能为空")
		String cardOrderId;
		
		@NotBlank(message = "验证码不能为空")
		@Length(min = 6, max = 6, message = "验证码位数不对！")
		String smsCode;
		
		String investorOid;
}
