package com.guohuai.mmp.jiajiacai.wishplan.plan.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "t_plan_product_entity")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicInsert
@DynamicUpdate
public class PlanProductEntity implements Serializable {
	
//	public static final String PLAN_status_paySuccess = "paySuccess";
//		
//	public static final String PLAN_status_oder = "order";
//		
//	public static final String PLAN_status_trash = "trash";
		

	private static final long serialVersionUID = 7644481711548905139L;
	@Id
	private String oid;
	/** 投资者OID */
	@NotNull
	private String uid;
	/** 转入金额 */
	@NotNull
	private BigDecimal amount;
	/** 计划OID */
	@NotNull
	private String planOid;
//	/** 投资期限 以天为单位 */
//	@NotNull
//	private int duration;
	
	/** 计划类型 按月定投 一次性购买 */
	@NotNull
	private String planType;
	
//	/** 开始时间 */
//	private Timestamp startTime;
//	/** 到期时间 */
//	private Timestamp endTime;
	
	/** 创建时间 */
	@NotNull
	private Timestamp createTime;
	/** 更新时间 */
	private Timestamp updateTime;
	
	private String status ;
	
	/**接口返回的orderid*/
	private String orderOid;
	
	/**接口返回的productId*/
	private String productOid;
	
//	private int dateNumber;
//	private BigDecimal balance;
	//红利，现金收益
	private BigDecimal income;
	//结转，增加份额, 仅记账
	private BigDecimal incomeVolume;
}
