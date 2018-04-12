package com.guohuai.mmp.investor.baseaccount.statistics;

import java.math.BigDecimal;

import com.guohuai.basic.component.ext.web.BaseResp;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/** 我的首页 */
@lombok.Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class MyHomeQueryRep extends BaseResp {

	

	/** 累计收益总额 */
	private BigDecimal totalIncomeAmount = BigDecimal.ZERO;


	/**
	 * 非心愿计划的资产总额
	 */
	private BigDecimal capitalAmount = BigDecimal.ZERO;

	/**
	 * 活期资产总额
	 */
	private BigDecimal t0CapitalAmount = BigDecimal.ZERO;

	/**
	 * 定期资产总额
	 */
	private BigDecimal tnCapitalAmount = BigDecimal.ZERO;
	
	/**
	 * 活期昨日收益额
	 */
	private BigDecimal t0YesterdayIncome = BigDecimal.ZERO;
	
	/**
	 * 余额
	 */
	private BigDecimal balance;
	
	/**
	 * 提现冻结
	 */
	private BigDecimal withdrawFrozenBalance;
	
	/**
	 * 充值冻结
	 */
	private BigDecimal rechargeFrozenBalance;
	
	/**
	 * 申购冻结
	 */
	private BigDecimal applyAvailableBalance;
	
	/**
	 * 提现可用
	 */
	private BigDecimal withdrawAvailableBalance;
	
	/**
	 * 活期产品中持有和申请中数量
	 * */
	private int  holdT0Size;
	
	/**
	 * 定期期产品中持有和申请中的数量
	 * */
	private int  holdTnSize;
	
	/**
	 * 购买的心愿计计划中持有和停止的数量
	 * */
	private int  holdPlanSize;
	

	/** 心愿计划的资产的总和 */
	private BigDecimal PlancapitalAmount = BigDecimal.ZERO;
	/** 心愿计划的投资成本 */
	private BigDecimal PlanInvestAmount = BigDecimal.ZERO;
	
	/** 个人账户资产总和 */
	private BigDecimal allCapitalAmount = BigDecimal.ZERO;
	
	/** 个人收益总和 */
	private BigDecimal allTotalIncomeAmount = BigDecimal.ZERO;
	
	
	
}
