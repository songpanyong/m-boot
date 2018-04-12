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
@Table(name = "t_plan_month_invest")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicInsert
@DynamicUpdate
public class PlanMonthEntity implements Serializable {
	
	private static final long serialVersionUID = 7644481711548905139L;
	
	public static final int PLAN_status_created = 0;
	public static final int PLAN_status_continued = 1;
	public static final int PLAN_status_overdue = 2;
	public static final int PLAN_status_completed = 3;
	public static final int PLAN_status_stop = 4;
	
	@Id
	private String oid;
	
	/**investor OID */
	@NotNull
	private String uid;

	private BigDecimal totalDepositAmount;
	
	private BigDecimal monthAmount;
	/**The plan month count*/
	private int planMonthCount;
	/**The actual invest count */
	private int totalInvestCount;
	
//	private String planListOid;
	
	private Timestamp startInvestDate;
//	private Timestamp lastInvestDate;
	private Timestamp endTime;
	/** The pay day on month */ 
	private int  monthInvestDate;
	private Timestamp updateTime;
	private Timestamp createTime;
	
	/** T_MONEY_INVESTOR_BANK*/
	private String investorBankOid;
	private String status;
	/** The credit*/
	private int monthOverdueCount;
	private int totalOverdueCount;
	
	private String planType;
	
	private BigDecimal expectedAmount;
	
	private BigDecimal expectedRate;
	//1 means transfered to balance
//	private int transferBalance; 
	
	private String cid;
	
	private String ckey;
	
	private int lastInvestMonth;
	
	private BigDecimal income;
	
	private String planTarget;
	/** 心愿计划批次 */
	private String planBatch;
}
