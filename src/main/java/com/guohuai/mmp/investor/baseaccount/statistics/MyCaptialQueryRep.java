package com.guohuai.mmp.investor.baseaccount.statistics;

import java.math.BigDecimal;
import java.util.List;

import com.guohuai.basic.component.ext.web.BaseResp;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/** 我的资产（账户余额，活期总资产，定期总资产） */
@lombok.Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class MyCaptialQueryRep extends BaseResp {

	/**
	 * 资产总额
	 */
	private BigDecimal capitalAmount = BigDecimal.ZERO;

	/**
	 * 活期资产总额
	 */
	private BigDecimal t0CapitalAmount = BigDecimal.ZERO;
	private List<CapitalDetail> t0CapitalDetails;

	/**
	 * 定期资产总额
	 */
	private BigDecimal tnCapitalAmount = BigDecimal.ZERO;
	private List<CapitalDetail> tnCapitalDetails;

	/** 申请中资产 */
	private BigDecimal applyAmt = BigDecimal.ZERO;
	private List<CapitalDetail> applyCapitalDetails;
	
	/** 体验金总资产 */
	private BigDecimal experienceCouponAmount = BigDecimal.ZERO;
	
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
	
	/** 心愿计划总的收益 */
	private BigDecimal PlanTotalIncomeAmount = BigDecimal.ZERO;
	/** 心愿计划的资产的总和 */
	private BigDecimal PlancapitalAmount = BigDecimal.ZERO;
	/** 心愿计划的投资成本 */
	private BigDecimal PlanInvestAmount = BigDecimal.ZERO;
	
	/** 个人账户资产总和 */
	private BigDecimal allCapitalAmount = BigDecimal.ZERO;
	
	
}
