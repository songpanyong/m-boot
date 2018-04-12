package com.guohuai.mmp.investor.bankorder;

import java.math.BigDecimal;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;

import org.hibernate.validator.constraints.NotEmpty;

import lombok.NoArgsConstructor;

@lombok.Data
@NoArgsConstructor
public class DepositApplyReq {
	/**
	 * 金额
	 */
	@Digits(integer = 18, fraction = 2, message = "金额格式错误")
	@DecimalMin(inclusive = false, value = "0", message = "金额必须大于0")
	private BigDecimal orderAmount;

	@NotEmpty(message = "银行名称不能为空")
	private String bankName;

	@NotEmpty(message = "银行卡号不能为空")
	private String cardNo;
	
	@NotEmpty(message = "银行预留手机号不能为空")
	private String phone;
	
	@NotEmpty(message = "用户ID不能为空")
	private String investorOid;
}
