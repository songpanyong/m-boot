package com.guohuai.mmp.investor.bankorder;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.NoArgsConstructor;

@lombok.Data
@NoArgsConstructor
public class DepositLongBankOrderReq {
	

	/**
	 * 支付流水号
	 */
	private String orderCode;
	
	/**
	 * 订单金额
	 */
	private BigDecimal orderAmount;
	
	
	/**
	 * 订单状态
	 */
	private String orderStatus;
	
	/**
	 * 订单时间
	 */
	private Timestamp orderTime;
	
	/**
	 * 投资人
	 */
	private String investorOid;
	
	/**
	 * 长款原因
	 */
	private String msg;
	
	/**
	 * 对账结果OID
	 */
	private String crOid;
	
	

}
