package com.guohuai.mmp.publisher.hold;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@lombok.Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class ClosedTnDetail {
	
	/** 
	 * 产品ID 
	 */
	private String productOid;

	/** 
	 * 产品名称 
	 */
	private String productName;
	/**
	 * 本息金额
	 */
	private BigDecimal orderAmount;
	
	/**
	 * 还本付息日
	 */
	private Date repayDate;
	
	/**
	 * 产品成立日
	 */
	private Date setupDate;
	
	/**
	 *  募集宣告失败日期
	 */
	private Date raiseFailDate;
	
	private String status;
	private String statusDisp;
	
	/** 最近一次的订单时间 */
	private Timestamp lastOrderTime;
	
	private String orderCode;
	
}
