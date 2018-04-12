package com.guohuai.mmp.platform.baseaccount.statistics;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.guohuai.component.persist.UUID;
import com.guohuai.mmp.platform.baseaccount.PlatformBaseAccountEntity;
import com.guohuai.mmp.sys.SysConstant;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 平台-基本账户-统计
 * 
 * @author yuechao
 *
 */
@Entity
@Table(name = "T_MONEY_PLATFORM_STATISTICS")
@lombok.Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
public class PlatformStatisticsEntity extends UUID implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4962497564579911579L;
	
	/**
	 * 所属平台
	 */
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "platformOid", referencedColumnName = "oid")
	PlatformBaseAccountEntity platformBaseAccount;
	
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
	 * 累计交易总额(充值+提现+投资+赎回)
	 */
	private BigDecimal totalTradeAmount = SysConstant.BIGDECIMAL_defaultValue;
	
	/**
	 * 发行人充值总额
	 */
	private BigDecimal publisherTotalDepositAmount = SysConstant.BIGDECIMAL_defaultValue;
	
	/**
	 * 发行人提现总额
	 */
	private BigDecimal publisherTotalWithdrawAmount = SysConstant.BIGDECIMAL_defaultValue;
	
	
	
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
	
//	/**
//	 * 投资人数
//	 */
//	private Integer investorAmount = SysConstant.INTEGER_defaultValue;
//	
//	/**
//	 * 持仓人
//	 */
//	private Integer investorHoldAmount = SysConstant.INTEGER_defaultValue;
	
	/**
	 * 逾期次数
	 */
	private Integer overdueTimes = SysConstant.INTEGER_defaultValue;
	
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
//	
//	/**
//	 * 封闭期产品数
//	 */
//	private Integer sealProductAmount = SysConstant.INTEGER_defaultValue;
	
	/**
	 * 发行人数(系统中存在的发行人数)
	 */
	private Integer publisherAmount = SysConstant.INTEGER_defaultValue;
	
	/**
	 * 实名认证人数
	 */
	private Integer verifiedInvestorAmount = SysConstant.INTEGER_defaultValue;
	
//	/**
//	 * 活跃投资人数(待产品提供活跃规则)
//	 */
//	private Integer activeInvestorAmount = SysConstant.INTEGER_defaultValue;
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
	
	private Timestamp updateTime;
	private Timestamp createTime;
}
