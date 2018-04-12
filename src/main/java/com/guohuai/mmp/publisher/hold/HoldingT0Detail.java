package com.guohuai.mmp.publisher.hold;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@lombok.Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class HoldingT0Detail {
	/** 
	 * 产品ID 
	 */
	private String productOid;

	/** 
	 * 产品名称 
	 */
	private String productName;
	/**
	 * 市值
	 */
	private BigDecimal value;
	
	/**
	 * 昨日收益
	 */
	private BigDecimal yesterdayIncome;
	
	/**
	 * 累计收益
	 */
	private BigDecimal holdTotalIncome;
	
	/**
	 * 冻结金额
	 */
	private BigDecimal toConfirmRedeemVolume;
	
	/**
	 * 最近一次投资时间
	 */
	private Timestamp lastOrderTime;
	
	private String orderCode;
}
