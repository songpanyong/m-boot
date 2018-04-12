package com.guohuai.mmp.platform.payment;

import java.io.Serializable;
import java.sql.Timestamp;

@lombok.Data
public class WithdrawAuditRequest implements Serializable {
	private static final long serialVersionUID = -112765746294580721L;

	/**
	 * 会员ID
	 */
	private String investorOid;
	
	/**
	 * 订单号
	 */
	private String orderCode;
	
	/**
	 * 交易类别
	 */
	private String type;
	
	/**
	 * 用户类型
	 */
	private String userType;
	
	/**
	 * 订单时间
	 */
	private String iceOutTime;

}