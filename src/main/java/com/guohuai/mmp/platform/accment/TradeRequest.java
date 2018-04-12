package com.guohuai.mmp.platform.accment;

import java.math.BigDecimal;


import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


/**
 * 交易 用于申购、赎回
 * @author yuechao
 *
 */
@lombok.Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class TradeRequest {
	
	/**
	 * 会员ID
	 */
	private String userOid;
	
	/**
	 * 发行人会员id
	 */
	private String publisherMemeberId;
	
	/**
	 * 用户类型
	 */
	private String userType;
	
	/**
	 * 交易类别
	 */
	private String orderType;
	
	
	/**
	 * 优惠惠金额
	 */
	private BigDecimal voucher;
	
	/**
	 * 交易额
	 */
	private BigDecimal balance;
	
	/**
	 * 交易用途
	 */
	private String remark;
	
	/**
	 * 定单号
	 */
	private String orderNo;
	
	/**
	 * 支付订单号
	 */
	private String iPayNo;
	
	/**
	 * 订单时间
	 */
	private String orderTime;
	
	private String originBranch;
}
