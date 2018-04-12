package com.guohuai.ams.liquidAsset;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

import lombok.Data;

@Data
public class LiquidAssetResp {

	public LiquidAssetResp(LiquidAsset liquidAsset){
		super();
		this.oid = liquidAsset.getOid();
		this.sn = liquidAsset.getSn();
		this.name = liquidAsset.getName();
		this.type = liquidAsset.getType();
		this.operationMode = liquidAsset.getOperationMode();
		this.confirmDays = liquidAsset.getConfirmDays();
		this.perfBenchmark = liquidAsset.getPerfBenchmark();
		this.incomeSchedule = liquidAsset.getIncomeSchedule();
		this.exchangeCd = liquidAsset.getExchangeCd();
		this.managerName = liquidAsset.getManagerName();
		this.managementCompany = liquidAsset.getManagementCompany();
		this.managementFullName = liquidAsset.getManagementFullName();
		this.custodian = liquidAsset.getCustodian();
		this.custodianFullName = liquidAsset.getCustodianFullName();
		this.investField = liquidAsset.getInvestField();
		this.investTarget = liquidAsset.getInvestTarget();
		this.holdPorpush = liquidAsset.getHoldPorpush();
		this.riskLevel = liquidAsset.getRiskLevel();
		this.valueDate = liquidAsset.getValueDate();
//		this.profitStartDate = liquidAsset.getProfitStartDate();
//		this.profitDeadlineDate = liquidAsset.getProfitDeadlineDate();
		this.dailyProfit = liquidAsset.getDailyProfit();
		this.weeklyYield = liquidAsset.getWeeklyYield();
		this.dividendType = liquidAsset.getDividendType();
		this.baseAmount = liquidAsset.getBaseAmount();
		this.baseYield = liquidAsset.getBaseYield();
		this.yield = liquidAsset.getYield();
		this.contractDays = liquidAsset.getContractDays();
		this.state = liquidAsset.getState();
		this.applyAmount = liquidAsset.getApplyAmount();
		this.holdShare = liquidAsset.getHoldShare();
		this.lockupShare = liquidAsset.getLockupShare();
		this.price = liquidAsset.getPrice();
		this.dayProfit = liquidAsset.getDayProfit();
		this.totalPfofit = liquidAsset.getTotalPfofit();
		this.valuations = liquidAsset.getValuations();
		this.netValue = liquidAsset.getNetValue();
		this.lastValueDate = liquidAsset.getLastValueDate();
		this.creator = liquidAsset.getCreator();
		this.operator = liquidAsset.getOperator();
		this.createTime = liquidAsset.getCreateTime();
		this.updateTime = liquidAsset.getUpdateTime();
	}
	
	private String oid;
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
	/**
	 * 万份收益日（收益开始日）
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
	 * 持有份额fenxian
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
