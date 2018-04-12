package com.guohuai.mmp.platform.payment;

import java.io.Serializable;
import java.math.BigDecimal;

@lombok.Data
public class DepositbfRequest implements Serializable {
	private static final long serialVersionUID = -112765746294580721L;
	
	/**
	 * 会员ID
	 */
	private String memberId;
	/**
	 * 订单号
	 */
	private String orderCode;
	
	/**
	 * mimosa支付流水号
	 */
	private String iPayNo;

	/**
	 * 请求流水号
	 */
	private String requestNo;
	/**
	 * 支付方式
	 */
	private String payMethod;
	/**
	 * 交易类别
	 */
	private String type;
	/**
	 * 金额
	 */
	private BigDecimal orderAmount;
	/**
	 * 支付备注
	 */
	private String remark;
	/**
	 * 定单描述
	 */
	private String describe;
	/**
	 * 订单时间
	 */
	private String orderTime;
	/**
	 * 系统 来源
	 */
	private String systemSource;

}