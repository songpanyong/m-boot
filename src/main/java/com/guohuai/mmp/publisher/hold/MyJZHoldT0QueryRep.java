package com.guohuai.mmp.publisher.hold;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.guohuai.ams.label.LabelResp;
import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.component.web.view.Pages;
import com.guohuai.file.FileResp;
import com.guohuai.mmp.publisher.product.client.CurrentProductDetailProfit;
import com.guohuai.mmp.publisher.product.client.ProductDetailIncomeRewardProfit;
import com.guohuai.mmp.sys.SysConstant;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/** 我的持有中活期产品列表 ----金猪*/
@lombok.Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class MyJZHoldT0QueryRep extends BaseResp{

	/**
	 * 活期资产总额
	 */
	private BigDecimal t0CapitalAmount = BigDecimal.ZERO;
	/**
	 * 活期昨日收益额
	 */
	private BigDecimal t0YesterdayIncome = BigDecimal.ZERO;
	
	/** 累计收益总额 */
	private BigDecimal totalIncomeAmount = BigDecimal.ZERO;
	private String productOid;// 产品oid
	private String state;// 产品状态
	private String productName;// 产品名称
	private BigDecimal value;//市值
	private String incomeCalcBasis;//收益计算基础
	private Integer lockPeriodDays;//锁定期
	private Integer interestsFirstDays;// 起息日
	private Date interestsFirstDate;//起息日期
	private Date setupDate;// 成立日期
	
	private List<ProductDetailIncomeRewardProfit> rewardYields;//昨日年化收益率走势  单位（%） :昨日年化收益率+奖励收益率
	private List<CurrentProductDetailProfit> perMillionIncomes;//基准每万份收益 单位（元）
	private List<CurrentProductDetailProfit> annualYields;//基准年化收益率走势  单位（%）
	
	private String annualInterestSec;// 预期年化收益率区间
	private String tenThsPerDayProfit;//万份收益
	private String sevenDayYield;//七日年化收益率
	private String yesterdayYield;//昨日年化收益率
	private String rewardYieldRange;//奖励收益率区间
	private String rewardTenThsProfit;//奖励万份收益
	private String showType;//展示类型
	/**
	 * 时间
	 */
	private Timestamp investTime;
	
	/**
	 * 昨日收益
	 */
	private BigDecimal yesterdayIncome;
	
	/**
	 * 累计收益
	 */
	private BigDecimal holdTotalIncome;
	
	/** 每万份收益 单位（元） */
	private List<MyCurrProTendencyChartRep> millionIncomeList = new ArrayList<MyCurrProTendencyChartRep>();

	/** 年化收益率走势 单位（%） */
	private List<MyCurrProTendencyChartRep> expArorList = new ArrayList<MyCurrProTendencyChartRep>();
	/** 万元收益 */
	private BigDecimal millionIncome = SysConstant.BIGDECIMAL_defaultValue;
}
