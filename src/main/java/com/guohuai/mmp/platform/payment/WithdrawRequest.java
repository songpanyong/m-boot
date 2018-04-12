package com.guohuai.mmp.platform.payment;

import java.io.Serializable;
import java.math.BigDecimal;

@lombok.Data
public class WithdrawRequest implements Serializable {
	private static final long serialVersionUID = -112765746294580721L;

	/**
	 * 会员ID
	 */
	private String memberId;
	/**
	 * 来源系统类型
	 */
	private String systemSource;
	/**
	 * 订单号
	 */
	private String orderCode;
	
	/**
	 * mimosa支付流水号
	 */
	private String iPayNo;

	/**
	 * 交易类别
	 */
	private String type;
	/**
	 * 金额
	 */
	private BigDecimal orderAmount;

	/**
	 * 手续费
	 */
	private BigDecimal fee;

	/**
	 * 请求流水号
	 */
	private String requestNo;
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
	 * 用户类型
	 */
	private String userType;

}