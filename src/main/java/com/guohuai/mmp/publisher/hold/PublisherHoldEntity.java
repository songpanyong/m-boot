package com.guohuai.mmp.publisher.hold;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.guohuai.ams.portfolio.entity.PortfolioEntity;
import com.guohuai.ams.product.Product;
import com.guohuai.component.persist.UUID;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountEntity;
import com.guohuai.mmp.publisher.baseaccount.PublisherBaseAccountEntity;
import com.guohuai.mmp.sys.SysConstant;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 发行人-持有人手册
 * 
 * @author yuechao
 *
 */
@Entity
@Table(name = "T_MONEY_PUBLISHER_HOLD")
@lombok.Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
public class PublisherHoldEntity extends UUID implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 5925816784463398291L;

	/** 持仓状态--待确认 */
	public static final String PUBLISHER_HOLD_HOLD_STATUS_toConfirm = "toConfirm";
	/** 持仓状态--持有中 */
	public static final String PUBLISHER_HOLD_HOLD_STATUS_holding = "holding";
	/** 持仓状态--已到期 */
	public static final String PUBLISHER_HOLD_HOLD_STATUS_expired = "expired";
	/** 持仓状态--结算中 */
	public static final String PUBLISHER_HOLD_HOLD_STATUS_closing = "closing";
	/** 持仓状态--已结算 */
	public static final String PUBLISHER_HOLD_HOLD_STATUS_closed = "closed";
	/** 持仓状态--退款中 */
	public static final String PUBLISHER_HOLD_HOLD_STATUS_refunding = "refunding";
	/** 持仓状态--已退款 */
	public static final String PUBLISHER_HOLD_HOLD_STATUS_refunded = "refunded";

	/** 类型--发行人 */
	public static final String PUBLISHER_accountType_SPV = "SPV";
	/** 类型--投资人 */
	public static final String PUBLISHER_accountType_INVESTOR = "INVESTOR";



	/**
	 * 所属理财产品
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "productOid", referencedColumnName = "oid")
	private Product product;

	/**
	 * 所属资产池
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "assetpoolOid", referencedColumnName = "oid")
	private PortfolioEntity portfolio;

	/**
	 * 所属发行人
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "publisherOid", referencedColumnName = "oid")
	private PublisherBaseAccountEntity publisherBaseAccount;

	/**
	 * 所属投资人
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "investorOid", referencedColumnName = "oid")
	private InvestorBaseAccountEntity investorBaseAccount;

	/**
	 * 总份额
	 */
	private BigDecimal totalVolume = SysConstant.BIGDECIMAL_defaultValue;

	/**
	 * 持有份额
	 */
	private BigDecimal holdVolume = SysConstant.BIGDECIMAL_defaultValue;

	/**
	 * 待确认投资份额
	 */
	private BigDecimal toConfirmInvestVolume = SysConstant.BIGDECIMAL_defaultValue;

	/**
	 * 待确认投赎回份额
	 */
	private BigDecimal toConfirmRedeemVolume = SysConstant.BIGDECIMAL_defaultValue;

	/**
	 * 可赎回份额
	 */
	private BigDecimal redeemableHoldVolume = SysConstant.BIGDECIMAL_defaultValue;

	/**
	 * 赎回锁定份额
	 */
	private BigDecimal lockRedeemHoldVolume = SysConstant.BIGDECIMAL_defaultValue;
	
	/**
	 * 体验金份额
	 */
	private BigDecimal expGoldVolume = BigDecimal.ZERO;

	/**
	 * 累计投资份额
	 */
	private BigDecimal totalInvestVolume = SysConstant.BIGDECIMAL_defaultValue;
	
	/**
	 * 累计使用卡券金额
	 */
	private BigDecimal totalVoucherAmount = SysConstant.BIGDECIMAL_defaultValue;

	/**
	 * 可计息份额
	 */
	private BigDecimal accruableHoldVolume = SysConstant.BIGDECIMAL_defaultValue;

	/**
	 * 最新市值
	 */
	private BigDecimal value = SysConstant.BIGDECIMAL_defaultValue;

	/**
	 * 累计收益
	 */
	private BigDecimal holdTotalIncome = SysConstant.BIGDECIMAL_defaultValue;

	/**
	 * 累计基础收益
	 */
	private BigDecimal totalBaseIncome = SysConstant.BIGDECIMAL_defaultValue;
	/**
	 * 累计奖励收益
	 */
	private BigDecimal totalRewardIncome = SysConstant.BIGDECIMAL_defaultValue;
	/**
	 * 累计加息收益
	 */
	private BigDecimal totalCouponIncome = SysConstant.BIGDECIMAL_defaultValue;
	/**
	 * 昨日收益
	 */
	private BigDecimal holdYesterdayIncome = SysConstant.BIGDECIMAL_defaultValue;
	/**
	 * 昨日基础收益
	 */
	private BigDecimal yesterdayBaseIncome = SysConstant.BIGDECIMAL_defaultValue;
	/**
	 * 昨天奖励收益
	 */
	private BigDecimal yesterdayRewardIncome = SysConstant.BIGDECIMAL_defaultValue;
	/**
	 * 昨天加息收益
	 */
	private BigDecimal yesterdayCouponIncome = SysConstant.BIGDECIMAL_defaultValue;
	/**
	 * 收益金额
	 */
	private BigDecimal incomeAmount = SysConstant.BIGDECIMAL_defaultValue;

	/**
	 * 可赎回收益
	 */
	private BigDecimal redeemableIncome = SysConstant.BIGDECIMAL_defaultValue;

	/**
	 * 锁定收益
	 */
	private BigDecimal lockIncome = SysConstant.BIGDECIMAL_defaultValue;

	/**
	 * 收益确认日期
	 */
	private Date confirmDate;

	/**
	 * 预期收益
	 */
	private BigDecimal expectIncome = SysConstant.BIGDECIMAL_defaultValue;
	private BigDecimal expectIncomeExt = SysConstant.BIGDECIMAL_defaultValue;

	/**
	 * 投资人类型
	 */
	private String accountType;

	/**
	 * 单个产品最大持有份额
	 */
	private BigDecimal maxHoldVolume = SysConstant.BIGDECIMAL_defaultValue;

	/**
	 * 
	 * 单日赎回份额
	 */
	private BigDecimal dayRedeemVolume = SysConstant.BIGDECIMAL_defaultValue;

	/**
	 * 
	 * 单日投资份额
	 */
	private BigDecimal dayInvestVolume = SysConstant.BIGDECIMAL_defaultValue;
	
	
	/**
	 * 单日赎回次数
	 */
	private Integer dayRedeemCount = 0; 
	
	/**
	 * 定期产品期号
	 */
	private String productAlias;
	
	/**
	 * 最近一次投资时间
	 */
	private Timestamp latestOrderTime;
	
	/**
	 * 持仓状态
	 */
	private String holdStatus;

	private Timestamp updateTime;
	private Timestamp createTime;
	/**
	 * wishplan
	 */
	private String wishplanOid;
}
