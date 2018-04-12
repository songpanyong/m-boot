package com.guohuai.mmp.jiajiacai.wishplan.plan.form;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BasePlanForm {
	
	/** 投资者OID */
	@NotNull
	private String uid;
	/** product oid */
	@NotNull
	private String productOid;
	/** 转入金额 */
	@NotNull
	private BigDecimal amount;
	/** 计划OID */
	@NotNull
	private String planOid;
	/** 投资期限 以天为单位 */
	@NotNull
	private int duration;
	/**接口返回的orderid*/
	private String orderOid;
	
	private String status;
	
}
