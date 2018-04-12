package com.guohuai.mmp.investor.bankorder;

import java.math.BigDecimal;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;

import org.hibernate.validator.constraints.NotEmpty;

import lombok.NoArgsConstructor;

@lombok.Data
@NoArgsConstructor
public class DepositBankOrderReq {
	/**
	 * 金额
	 */
	@Digits(integer = 18, fraction = 2, message = "金额格式错误")
	@DecimalMin(inclusive = false, value = "0", message = "金额必须大于0")
	private BigDecimal orderAmount;

	@NotEmpty(message = "支付流水号不能为空")
	private String payNo;

	@NotEmpty(message = "短信验证码不能为空")
	private String smsCode;
	
	private String investorOid;

}
