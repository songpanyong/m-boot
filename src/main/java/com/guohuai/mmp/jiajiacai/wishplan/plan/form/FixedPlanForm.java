package com.guohuai.mmp.jiajiacai.wishplan.plan.form;

import java.sql.Timestamp;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author Administrator
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FixedPlanForm extends BasePlanForm{
	/** 转入日期 */
	@NotNull
	private Timestamp inputTime;
	
}
