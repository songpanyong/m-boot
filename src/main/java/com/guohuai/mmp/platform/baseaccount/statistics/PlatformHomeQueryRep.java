package com.guohuai.mmp.platform.baseaccount.statistics;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.mmp.sys.SysConstant;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 平台首页
 * 
 * @author yuechao
 *
 */
@lombok.Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@lombok.Builder
public class PlatformHomeQueryRep extends BaseResp {

	
	/**
	 * 累计交易总额(充值+提现+投资+赎回)
	 */
	private BigDecimal totalTradeAmount = SysConstant.BIGDECIMAL_defaultValue;
	
	/**
	 * 累计借款总额(用户投资)
	 */
	private BigDecimal totalLoanAmount = SysConstant.BIGDECIMAL_defaultValue;
	
	/**
	 * 累计还款总额(用户赎回)
	 */
	private BigDecimal totalReturnAmount = SysConstant.BIGDECIMAL_defaultValue;
	
	
	/**
	 * 累计付息总额(用户收到的收益)
	 */
	private BigDecimal totalInterestAmount = SysConstant.BIGDECIMAL_defaultValue;
	
	
	/**
	 * 发行人数(系统中存在的发行人数)
	 */
	private Integer publisherAmount = SysConstant.INTEGER_defaultValue;
	/**
	 * 发行人充值总额
	 */
	private BigDecimal publisherTotalDepositAmount = SysConstant.BIGDECIMAL_defaultValue;
	
	/**
	 * 发行人提现总额
	 */
	private BigDecimal publisherTotalWithdrawAmount = SysConstant.BIGDECIMAL_defaultValue;
	
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
	
//	/**
//	 * 发行产品数
//	 */
//	private Integer productAmount = SysConstant.INTEGER_defaultValue;
//	
//	/**
//	 * 已结算产品数
//	 */
//	private Integer closedProductAmount = SysConstant.INTEGER_defaultValue;
//	
//	/**
//	 * 待结算产品数
//	 */
//	private Integer toCloseProductAmount = SysConstant.INTEGER_defaultValue;
//	
//	/**
//	 * 在售产品数
//	 */
//	private Integer onSaleProductAmount = SysConstant.INTEGER_defaultValue;
	
	
	
	/**
	 * 投资人充值总额
	 */
	private BigDecimal investorTotalDepositAmount = SysConstant.BIGDECIMAL_defaultValue;
	
	/**
	 * 投资人提现总额
	 */
	private BigDecimal investorTotalWithdrawAmount = SysConstant.BIGDECIMAL_defaultValue;
	
	/**
	 * 注册人数
	 */
	private Integer registerAmount = SysConstant.INTEGER_defaultValue;
	
	/**
	 * 实名认证人数
	 */
	private Integer verifiedInvestorAmount = SysConstant.INTEGER_defaultValue;
	/**
	 * 代金券数量
	 */
	private Long totalCoupon;
	/**
	 * 代金券金额
	 */
	private BigDecimal totalCouponAmount;
	/**
	 * 体验金数量
	 */
	private Long totalTasteCoupon;
	/**
	 * 体验金金额
	 */
	private BigDecimal totalTasteCouponAmount;
	/**
	 * 加息券数量
	 */
	private Long totalRateCoupon;
	
	/**
	 * 最近一个数人数变化
	 */
	private List<CurvePojo> peopleCurve = new ArrayList<CurvePojo>();
	

	/** 昨日各渠道投资额排名前5 */
	private List<ChartPojo> channelRank = new ArrayList<ChartPojo>();

	/** 昨日产品新增投资排名前五 */
	private List<ChartPojo> proInvestorRank = new ArrayList<ChartPojo>();

	/** 平台交易额占比分析 */
	private List<ChartPojo> tradeAmountAnalyse = new ArrayList<ChartPojo>();

	/** 投资人质量分析 */
	private List<ChartPojo> investorAnalyse = new ArrayList<ChartPojo>();

}
