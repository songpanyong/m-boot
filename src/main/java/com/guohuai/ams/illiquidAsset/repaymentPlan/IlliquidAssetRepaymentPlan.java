package com.guohuai.ams.illiquidAsset.repaymentPlan;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.guohuai.ams.illiquidAsset.IlliquidAsset;
import com.guohuai.component.persist.UUID;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 非现金类资产预计还款计划
 * 
 * @author Administrator
 *
 */
@Entity
@Table(name = "T_GAM_ILLIQUID_REPAYMENT_PLAN")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
public class IlliquidAssetRepaymentPlan extends UUID implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "illiquidAssetOid", referencedColumnName = "oid")
	private IlliquidAsset illiquidAsset;

	// 期数
	private int issue;

	// 还款方式
	private String repaymentType;

	// 计息天数
	private int intervalDays;

	// 计息起始日
	private Date startDate;

	// 计息截止日
	private Date endDate;

	// 还款本金
	private BigDecimal principal;
	// 还款日期
	private Date dueDate;

	// 还款利息
	private BigDecimal interest;

	// 还款总额
	private BigDecimal repayment;

	private String creator; // 创建者
	private String operator; // 操作者
	private Timestamp createTime;
	private Timestamp operateTime;

}
