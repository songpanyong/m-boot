package com.guohuai.ams.liquidAsset;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class LiquidAssetForm implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8280010601845615803L;
	/**
	 * 
	 */
	private String oid;
	/**
	 * 标的代码
	 */
	@NotNull(message = "标的代码不能为空")
	private String sn;
	/**
	 * 标的名称
	 */
	@NotNull(message = "标的名称不能为空")
	private String name;
	/**
	 * 标的代码
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
	/**
	 * 万份收益日（日期）
	 */
	private Date dailyProfitDate;
	/**
	 * 收益开始日期
	 */
	private Date profitStartDate;
	/**
	 * 收益截止日
	 */
	private Date profitDeadlineDate;
	/**
	 * 万份收益
	 */
	private BigDecimal dailyProfit;
	/**
	 * 7日年化收益率
	 */
	private BigDecimal weeklyYield;
	/**
	 * 分红方式
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
	 * 持有份额
	 */
	private BigDecimal holdAmount;
	/**
	 * 冻结份额
	 */
	private BigDecimal lockupAmount;
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
