package com.guohuai.mmp.jiajiacai.wishplan.plan.form;

import java.sql.Timestamp;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 按月定投的支付参数
 * @author Administrator
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthPlanForm extends BasePlanForm{
	
	/** 转入日期 */
	@NotNull
	@ApiModelProperty(value="dateNumber" ,required=true)
	private int dateNumber;
	/** 首次转入日期 */
	@NotNull
	@ApiModelProperty(value="inputTime" ,required=true)
	private Timestamp inputTime;
	
	private String planType;
}
