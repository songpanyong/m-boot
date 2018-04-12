package com.guohuai.mmp.investor.bankorder.apply;

import java.math.BigDecimal;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;

import lombok.Data;


@Data
public class ApplyReq {
	
	@Digits(integer = 18, fraction = 2, message = "金额格式有误")
	@DecimalMin(inclusive = false, value = "0", message = "金额必须大于零")
	private BigDecimal orderAmount;
}
