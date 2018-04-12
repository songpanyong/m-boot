package com.guohuai.ams.liquidAsset;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.guohuai.component.persist.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 现金类资产要素
 * 
 * @author zudafu
 *
 */

@Entity
@Table(name = "T_GAM_LIQUID_ASSET")
@Data
@Builder
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
public class LiquidAsset extends UUID {

	/**
	 * 现金管理类工具状态 waitPretrial 未审核 pretrial 审核中 collecting 审核通过 reject 驳回 invalid
	 * 作废
	 */
	public static final String LIQUID_STATE_waitPretrial = "waitPretrial";
	public static final String LIQUID_STATE_pretrial = "pretrial";
	public static final String LIQUID_STATE_collecting = "collecting";
	public static final String LIQUID_STATE_reject = "reject";
	public static final String LIQUID_STATE_invalid = "invalid";

	public static final String TYPE_CASH_FUND = "CASHTOOLTYPE_01";
	public static final String TYPE_AGREEMENT_DEPOSIT = "CASHTOOLTYPE_02";

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 标的代码
	 */
	private String sn;
	/**
	 * 标的名称
	 */
	private String name;
	/**
	 * 标的类型
	 */
	private String type;
	/**
	 * 运行模式
	 */
	private String operationMode;
	/**
	 * 申购确认日
	 */
	private int confirmDays;
	/**
	 * 业绩比较基准
	 */
	private String perfBenchmark;
	/**
	 * 收益结转方式
	 */
	private String incomeSchedule;
	/**
	 * 交易所代码
	 */
	private String exchangeCd;
	/**
	 * 基金经理
	 */
	private String managerName;
	/**
	 * 基金管理人编码
	 */
	private String managementCompany;
	/**
	 * 基金管理人名称
	 */
	private String managementFullName;
	/**
	 * 基金托管人编码
	 */
	private String custodian;
	/**
	 * 基金托管人名称
	 */
	private String custodianFullName;
	/**
	 * 投资领域
	 */
	private String investField;
	/**
	 * 投资目标
	 */
	private String investTarget;
	/**
	 * 持有目的
	 */
	private String holdPorpush;
	/**
	 * 风险等级
	 */
	private String riskLevel;
	/**
	 * 起息日
	 */
	private Date valueDate;
//	/**
//	 * 万份收益日（收益开始日）
//	 */
//	private Date profitStartDate;
	/**
	 * 收益日
	 */
	private Date profitDate;
	/**
	 * 万份收益
	 */
	private BigDecimal dailyProfit;
	/**
	 * 7日年化收益率
	 */
	private BigDecimal weeklyYield;
	/**
	 * 分红方式2
	 */
	private String dividendType;
	/**
	 * 基本额度
	 */
	private BigDecimal baseAmount;
	/**
	 * 基本年利率
	 */
	private BigDecimal baseYield;
	/**
	 * 协定存款年利率
	 */
	private BigDecimal yield;
	/**
	 * 合同年天数
	 */
	private int contractDays;
	/**
	 * 状态
	 */
	private String state;

	/**
	 * 申请中金额
	 */
	private BigDecimal applyAmount;

	/**
	 * 持有份额
	 */
	private BigDecimal holdShare;
	/**
	 * 冻结份额
	 */
	private BigDecimal lockupShare;
	/**
	 * 当前单价
	 */
	private BigDecimal price;
	/**
	 * 当日收益
	 */
	private BigDecimal dayProfit;
	/**
	 * 累计收益
	 */
	private BigDecimal totalPfofit;
	/**
	 * 当前估值
	 */
	private BigDecimal valuations;
	/**
	 * 当前净值
	 */
	private BigDecimal netValue;
	/**
	 * 最新估值日
	 */
	private Date lastValueDate;
	/**
	 * creator
	 */
	private String creator;
	/**
	 * operator
	 */
	private String operator;
	/**
	 * createTime
	 */
	private Timestamp createTime;
	/**
	 * updateTime
	 */
	private Timestamp updateTime;
}
