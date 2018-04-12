package com.guohuai.cache.entity;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

@lombok.Data
@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
@lombok.Builder
public class HoldCacheEntity {

	public static String[] zoomArr = new String[] { "toConfirmInvestVolume", "toConfirmRedeemVolume",
			"redeemableHoldVolume", "totalInvestVolume", "totalVoucherAmount", "dayRedeemVolume", "dayInvestVolume", "maxHoldVolume",
			"lockRedeemHoldVolume", "holdVolume", "totalVolume", "accruableHoldVolume", "value", "expGoldVolume",
			"holdYesterdayIncome", "holdTotalIncome", "expectIncome", "expectIncomeExt"};
	/** 所属理财产品 */
	private String productOid;
	/** 用户OID */
	private String userOid;
	
	private String wishplanOid;
	/**  */
	private String accountType;
	/** 待确认投资份额 */
	private BigDecimal toConfirmInvestVolume;
	/** 待确认赎回份额(赎回在途份额) */
	private BigDecimal toConfirmRedeemVolume;
	/** 可赎回份额 */
	private BigDecimal redeemableHoldVolume;
	/** 累计投资份额 */
	private BigDecimal totalInvestVolume;
	
	/**
	 * 累计使用卡券金额
	 */
	private BigDecimal totalVoucherAmount;
	/** 单日赎回份额 */
	private BigDecimal dayRedeemVolume;
	/** 单日投资份额 */
	private BigDecimal dayInvestVolume;
	/**
	 * 单日赎回次数
	 */
	private Integer dayRedeemCount; 
	
	/**
	 * 昨日收益
	 */
	private BigDecimal holdYesterdayIncome;
	/**
	 * 累计收益
	 */
	private BigDecimal holdTotalIncome;
	
	/** 最大持有额度 */
	private BigDecimal maxHoldVolume;
	/** 锁定可赎回份额 */
	private BigDecimal lockRedeemHoldVolume;
	/** 持有份额 */
	private BigDecimal holdVolume;
	/** 总份额 */
	private BigDecimal totalVolume;
	/** 可计息份额 */
	private BigDecimal accruableHoldVolume;
	/**
	 * 预期收益起始区间
	 */
	private BigDecimal expectIncome;
	
	/**
	 * 预期收益闭区间
	 */
	private BigDecimal expectIncomeExt;
	
	/**
	 * 体验金金额
	 */
	private BigDecimal expGoldVolume;
	/** 持有状态/ */
	private String holdStatus;
	/** 市值 */
	private BigDecimal value;
	/**
	 * 收益确认日
	 */
	private Date incomeDate;
	
	/**
	 * 最近一次投资时间
	 */
	private Timestamp latestOrderTime;
}
