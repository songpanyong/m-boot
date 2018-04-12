package com.guohuai.mmp.publisher.hold;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@lombok.Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class HoldingTnDetail {
	/** 
	 * 产品ID 
	 */
	private String productOid;

	/** 
	 * 产品名称 
	 */
	private String productName;
	/**
	 * 投资金额
	 */
	private BigDecimal investAmount;
	
	/**
	 * 预计年化收益率
	 */
	private String expYearRate;
	
	/**
	 * 到期日
	 */
	private Date durationPeriodEndDate;
	
	private String status;
	private String statusDisp;
	
	/** 最近一次的订单时间 */
	private Timestamp lastOrderTime;
	
	private String orderCode;
}
