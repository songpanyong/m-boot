package com.guohuai.ams.portfolio.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;

/**
 * 投资组合收益分配要素
 * @author star.zhu
 * 2016年12月26日
 */
@Data
@Entity
@Table(name = "T_GAM_PORTFOLIO_INCOME")
public class IncomeEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private String oid;
	
	// 关联投资组合
	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "portfolioOid", referencedColumnName = "oid")
	private PortfolioEntity portfolio;
	
	// 产品名称
	private String productName;
	// 产品规模
	private BigDecimal productScale;
	// 收益分配类型
	private String type;
	// 收益分配日
	private Date incomeDate;
	// 分配收益
	private BigDecimal income;
	// 基础收益
	private BigDecimal baseIncome;
	// 奖励收益
	private BigDecimal rewardIncome;
	// 实际发放基础收益
	private BigDecimal factBaseIncome;
	// 实际发放奖励收益
	private BigDecimal factRewardIncome;
	// 收益率
	private BigDecimal yield;
	// 状态
	private String state;
	
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
