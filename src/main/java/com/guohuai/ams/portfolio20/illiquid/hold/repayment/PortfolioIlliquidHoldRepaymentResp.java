package com.guohuai.ams.portfolio20.illiquid.hold.repayment;

import java.math.BigDecimal;
import java.sql.Date;

import lombok.Data;

@Data
public class PortfolioIlliquidHoldRepaymentResp {

	public PortfolioIlliquidHoldRepaymentResp(PortfolioIlliquidHoldRepaymentEntity p) {
		this.oid = p.getOid();
		this.illiquidAssetOid = p.getHold().getIlliquidAsset().getOid();
		this.illiquidAssetName = p.getHold().getIlliquidAsset().getName();
		this.portfolioOid = p.getHold().getPortfolio().getOid();
		this.portfolioName = p.getHold().getPortfolio().getName();
		this.issue = p.getIssue();
		this.repaymentType = p.getRepaymentType();
		this.intervalDays = p.getIntervalDays();
		this.startDate = p.getStartDate();
		this.endDate = p.getEndDate();
		this.dueDate = p.getDueDate();
		this.principalPlan = p.getPrincipalPlan();
		this.interestPlan = p.getInterestPlan();
		this.repaymentPlan = p.getRepaymentPlan();
		this.principal = p.getPrincipal();
		this.interest = p.getInterest();
		this.repayment = p.getRepayment();
		this.lastIssue = p.getLastIssue();
		this.state = p.getState();

	}

	private String oid;

	private String illiquidAssetOid;
	private String illiquidAssetName;
	private String portfolioOid;
	private String portfolioName;

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
	// 还款日
	private Date dueDate;
	// 预计还款本金
	private BigDecimal principalPlan;
	// 预计还款利息
	private BigDecimal interestPlan;
	// 预计还款总额
	private BigDecimal repaymentPlan;
	// 实际还款本金
	private BigDecimal principal;
	// 实际还款利息
	private BigDecimal interest;
	// 实际还款总额
	private BigDecimal repayment;
	// 是否最后一期
	private String lastIssue;
	// 还款状态
	private String state;

	private boolean paidable = false;

}
