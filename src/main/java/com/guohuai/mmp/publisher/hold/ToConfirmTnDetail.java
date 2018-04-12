package com.guohuai.mmp.publisher.hold;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@lombok.Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class ToConfirmTnDetail {
	
	/**
	 * 产品OID
	 */
	private String productOid;
	/** 
	 * 产品名称 
	 */
	private String productName;
	
	/**
	 * 待受理金额
	 */
	private BigDecimal toConfirmInvestAmount;
	
	/**	
	 * 产品预计成立日
	 */
	private Date setupDate;
	
	/**
	 * 已受理金额
	 */
	private BigDecimal acceptedAmount;
	
	/**
	 * 本息退款
	 */
	private BigDecimal refundAmount;
	
	private String status;
	
	private String statusDisp;
	
	/** 最近一次的订单时间 */
	private Timestamp lastOrderTime;
	
	private String orderCode;
	
}
