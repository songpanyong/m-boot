package com.guohuai.mmp.jiajiacai.wishplan.plan.form;
import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

import lombok.NoArgsConstructor;

@lombok.Data
@NoArgsConstructor
public class EducationInvestReq {
	/**
	 * 申购金额
	 */
	@Digits(integer = 18, fraction = 2, message = "金额格式错误")
	@NotNull(message = "金额不能为空")
	BigDecimal moneyVolume;

	String cid;
	
	String ckey;

	/** 卡券编号 */
	String couponId;

	/** 卡券类型 */
	String couponType;

	/** 卡券实际抵扣金额 */
	BigDecimal couponDeductibleAmount;

	/** 卡券金额 */
	BigDecimal couponAmount;

	/** 投资者实付金额 */
	BigDecimal payAmouont;

	@NotNull
	int duration;
	
	@NotNull
	String planListOid;
	
	private String planTarget;
}