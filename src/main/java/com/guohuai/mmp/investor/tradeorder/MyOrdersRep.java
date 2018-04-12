package com.guohuai.mmp.investor.tradeorder;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.guohuai.basic.component.ext.web.BaseResp;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@lombok.Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class MyOrdersRep extends BaseResp {
	
	private String tradeOrderOid;

	/**
	 * 昨日奖励收益率
	 */
	private BigDecimal rewardIncomeRatio;

	/**
	 * 昨日基础收益率
	 */
	private BigDecimal baseIncomeRatio;

	/**
	 * 产品名称
	 */
	private String productName;
	/** 昨日收益 */
	private BigDecimal yesterdayIncome;

	/** 累计收益 */
	private BigDecimal totalIncome;


	/** 最新市值 */
	private BigDecimal value;
	
	/**
	 * 订单类型
	 */
	private String orderType;

	/**
	 * 订单时间
	 */
	private Timestamp orderTime;

	/**
	 * 昨日收益率
	 */
	private String incomeRatio;
	
	private String viewStatus;
	private String viewStatusDisp;

}
