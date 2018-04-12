package com.guohuai.mmp.publisher.baseaccount.statistics;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.mmp.sys.SysConstant;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 发行人首页查询
 * 
 */
@lombok.Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class PublisherHomeQueryRep extends BaseResp {

	/**
	 * 累计充值总额
	 */
	private BigDecimal totalDepositAmount = SysConstant.BIGDECIMAL_defaultValue;
	
	/**
	 * 累计提现总额
	 */
	private BigDecimal totalWithdrawAmount = SysConstant.BIGDECIMAL_defaultValue;
	
	/**
	 * 累计借款总额
	 */
	private BigDecimal totalLoanAmount = SysConstant.BIGDECIMAL_defaultValue;
	
	/**
	 * 累计还款总额
	 */
	private BigDecimal totalReturnAmount = SysConstant.BIGDECIMAL_defaultValue;
	
	/**
	 * 累计付息总额
	 */
	private BigDecimal totalInterestAmount = SysConstant.BIGDECIMAL_defaultValue;
	
	/**
	 * 今日定期投资总额
	 */
	private BigDecimal todayTnInvestAmount = SysConstant.BIGDECIMAL_defaultValue;
	
	/**
	 * 今日赎回金额
	 */
	private BigDecimal todayRedeemAmount = SysConstant.BIGDECIMAL_defaultValue;
	
	/**
	 * 今日还本金额
	 */
	private BigDecimal todayRepayInvestAmount = SysConstant.BIGDECIMAL_defaultValue;
	
	/**
	 * 今日付息金额
	 */
	private BigDecimal todayRepayInterestAmount = SysConstant.BIGDECIMAL_defaultValue;
	
	/**
	 * 逾期次数
	 */
	private Integer overdueTimes = SysConstant.INTEGER_defaultValue;
	
	
	/**
	 * 募集期产品数量
	 */
	private Integer raisingNum;
	
	/**
	 * 募集结束产品数量
	 */
	private Integer raiseendNum;
	
	/**
	 * 存续期产品数量
	 */
	private Integer durationingNum;
	
	/**
	 * 存续期结束
	 */
	private Integer durationendNum;
	
	/**
	 * 清盘中
	 */
	private Integer clearingNum;
	
	/**
	 * 已清盘
	 */
	private Integer clearedNum;

	/** 昨日产品投资额Top5 */
	private List<ChartPojo> top5ProductList = new ArrayList<ChartPojo>();

	/** 在售产品募集进度 */
	private List<PublisherRaiseRateRep> raiseRate;

	/** 投资人质量分析 */
	private List<ChartPojo> investorAnalyse = new ArrayList<ChartPojo>();
}
