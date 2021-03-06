package com.guohuai.mmp.investor.bankorder;

import java.math.BigDecimal;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;

import lombok.NoArgsConstructor;

@lombok.Data
@NoArgsConstructor
public class DepositBankOrderbfReq {
	/**
	 * 金额
	 */
	@Digits(integer = 18, fraction = 2, message = "金额格式错误")
	@DecimalMin(inclusive = false, value = "0", message = "金额必须大于0")
	private BigDecimal orderAmount;
	
	private String wishplanOid;

}
