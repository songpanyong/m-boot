package com.guohuai.mmp.jiajiacai.wishplan.plan.form;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthFormVO {
	/** product oid */
	@NotNull
	private String productOid;
	/** 转入金额 */
	@NotNull
	private int amount;
	/** 计划OID */
	@NotNull
	private String planOid;
	/** 投资期限 以天为单位 */
	@NotNull
	private int duration;
	
	private String planType;
	private String planName;
}
