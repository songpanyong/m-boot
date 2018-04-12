package com.guohuai.ams.portfolio.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;

/**
 * 投资组合估值明细要素
 * @author star.zhu
 * 2016年12月26日
 */
@Data
@Entity
@Table(name = "T_GAM_PORTFOLIO_VALUATION")
public class ValuationEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private String oid;
	
	// 关联投资组合
	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "portfolioOid", referencedColumnName = "oid")
	private PortfolioEntity portfolio;
	
	// 估值日
	private Date valueDate;
	// 实际现金类资产占比
	private BigDecimal liquidFactRate;
	// 实际非现金类资产占比
	private BigDecimal illiquidFactRate;
	// 实际现金存款占比
	private BigDecimal cashFactRate;
	// 冻结金额
	private BigDecimal freezeCash;
	// 在途金额
	private BigDecimal transitCash;
	// 应付托管费
	private BigDecimal trusteeFee;
	// 应付管理费
	private BigDecimal manageFee;
	// 偏离损益
	private BigDecimal deviationValue;
	// 收益基准日
	private Date baseDate;
	// 账户现金
	private BigDecimal cashPosition;
	// 未分配收益
	private BigDecimal unDistributeProfit;
	// 应付费金
	private BigDecimal payFeigin;
	// 状态
	private String state;
	// 确认收益
	private BigDecimal confirmProfit;
	// 实现收益
	private BigDecimal factProfit;
	// 每日定时任务状态
	private String scheduleState;
	// 每日收益分配状态
	private String incomeState;
	// SPV累计提取费金
	private BigDecimal drawedChargefee;
	// SPV累计计提费金
	private BigDecimal countintChargefee;
	// SPV持有的基子单位净值
	private BigDecimal nav;
	// SPV持有的基子份额
	private BigDecimal shares;
	// 总资产净值
	private BigDecimal netValue;
	// 总规模（估值）
	private BigDecimal scale;
	
	// 审核人
	private String auditor;
	// 审核时间
	private Timestamp auditTime;
	// 审核状态
	private String auditState;
	// 审核意见
	private String auditMark;
	
	private String creater;
	private Timestamp createTime;
	private String operator;
	private Timestamp operatTime;
}
