package com.guohuai.ams.illiquidAsset.repaymentPlan;

import java.math.BigDecimal;
import java.util.Date;

import javax.validation.constraints.NotNull;

import com.guohuai.ams.illiquidAsset.IlliquidAsset;
import com.guohuai.basic.component.ext.web.parameter.validation.Enumerations;

import lombok.Data;

@Data
public class IlliquidAssetRepaymentPlanForm {

	@NotNull
	private Date startDate;

	@NotNull
	private Date endDate;

	@NotNull
	private BigDecimal raiseScope;

	@NotNull
	private BigDecimal expAror;

	@Enumerations(values = { "360", "365" })
	private int contractDays;

	private int accrualDate;

	@Enumerations(values = { IlliquidAsset.PAYMENT_METHOD_A_DEBT_SERVICE_DUE,
			IlliquidAsset.PAYMENT_METHOD_EACH_INTEREST_RINCIPAL_DUE, IlliquidAsset.PAYMENT_METHOD_FIXED_BASIS_MORTGAGE,
			IlliquidAsset.PAYMENT_METHOD_FIXED_PAYMENT_MORTGAGE })
	private String accrualType;
}
