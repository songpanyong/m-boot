package com.guohuai.mmp.platform.payment;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRegistrationRequest {
	
	/**
	 * 用户ID
	 */
	private String memberId;
	
	/**
	 * 系统来源
	 */
	private String systemSource;
	
	/**
	 * 订单号
	 */
	private String orderCode;
	
	/**
	 * 订单金额
	 */
	private BigDecimal orderAmount;
	/**
	 * 手续费
	 */
	private BigDecimal fee;
	/**
	 * 订单类型
	 */
	private String orderType;
	/**
	 * 请求流水号
	 */
	private String requestNo;
	/**
	 * 订单描述
	 */
	private String orderAnno;
	
	/**
	 * 订单时间
	 */
	private String orderTime;
	
	/**
	 * 三方订单完成时间
	 */
	private String tripartiteTime;
	
	/**
	 * 三方支付流水号
	 */
	private String tripartitePayNo;
	/**
	 * 银行卡号
	 */
	private String bankCardNumber;
	
	/**
	 * 支付通道ID
	 */
	private String channelNo;
	
}
