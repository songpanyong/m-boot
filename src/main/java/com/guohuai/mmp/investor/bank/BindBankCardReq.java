package com.guohuai.mmp.investor.bank;

import java.io.Serializable;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

@lombok.Data
public class BindBankCardReq implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@NotBlank(message = "银行名称不能为空！")
	@Length(min = 1, max = 30, message = "银行名称位数不对！")
	private String bankName;
	
	@NotBlank(message = "银行卡号不能为空！")
	@Length(min = 16, max = 19, message = "银行卡号位数不对！")
	private String cardNo;
	
	@NotBlank(message = "预留手机号不能为空！")
	@Length(min = 11, max = 11, message = "预留手机号位数不对！")
	private String phone;
	
	private String smsCode;
	
	private String userOid;
	
}
