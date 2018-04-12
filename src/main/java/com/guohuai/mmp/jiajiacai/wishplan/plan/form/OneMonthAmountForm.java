package com.guohuai.mmp.jiajiacai.wishplan.plan.form;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OneMonthAmountForm {
	
	private BigDecimal depositAmount;
	
    private BigDecimal planDepositAmount;
}
