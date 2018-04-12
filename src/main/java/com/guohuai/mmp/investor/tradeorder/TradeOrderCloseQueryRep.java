package com.guohuai.mmp.investor.tradeorder;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.guohuai.basic.component.ext.web.BaseResp;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@lombok.Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class TradeOrderCloseQueryRep   extends BaseResp{

	/**
	 * 赎回订单UUID
	 */
	private String closeOrderOid;
	/**
	 * 申购订单UUID
	 */
	private String tradeOrderOid;

	/**
	 * 产品UUID
	 */
	private String productOid;

	/**
	 * 产品名称
	 */
	String productName;

	/**
	 * 手机号
	 */
	private String phoneNum;

	/**
	 * 赎回订单号
	 */
	private String orderCode;

	/**
	 * 赎回份额
	 */
	private BigDecimal orderVolume;

	/**
	 * 赎回时间
	 */
	private Timestamp orderTime;
	/**
	 * 基础收益率
	 */
	private BigDecimal baseIncomeRatio;
	private String baseIncomeRatioDisp;
	/**
	 * 奖励收益率
	 */
	private BigDecimal rewardIncomeRatio;
	private String rewardIncomeRatioDisp;
	/**
	 * 总收益率
	 */
	private BigDecimal incomeRatio;
	private String incomeRatioDisp;
	/**
	 * 持仓天数
	 */
	private Long holdDays;
	private String holdDaysDisp;
}
