package com.guohuai.mmp.investor.bankorder;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@lombok.Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class MyBankOrderRep {

	/** 订单流水号 */
	private String orderCode;
	/** 订单类型 */
	private String orderType;
	private String orderTypeDisp;
	/** 订单金额 */
	private BigDecimal orderAmount;
	/** 订单时间 */
	private Timestamp orderTime;
	/** 订单状态 */
	private String orderStatus;
	private String orderStatusDisp;
	/**
	 * 手续费
	 */
	private BigDecimal fee;
	
	/**
	 * qi修改
	 * 操作人
	 */
	private String operator;
}
