package com.guohuai.mmp.platform.finance.result;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Data
public class PlatformFinanceCompareDataResultRep implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1956892210090284690L;
	/** crOid */
	private String crOid;
	
	/** 对账Oid */
	private String checkOid;
	
	
	/**
	 * 订单号
	 */
	private String orderCode;
	/**
	 * 支付订单号
	 */
	private String iPayNo;
	
	/**
	 * 订单类型
	 */
	private String orderType;
	private String orderTypeDisp;
	
	/**
	 * 订单金额
	 */
	private BigDecimal orderAmount;
	/**
	 * 卡券金额
	 */
	private BigDecimal voucher;

	/**
	 * 手续费
	 */
	private BigDecimal fee;
	
	/**
	 * 订单状态
	 */
	private String orderStatus;
	private String  orderStatusDisp;
	
	/**
	 * 订单时间
	 */
	private Timestamp orderTime;
	
	/**
	 * 用户
	 */
	private String investorOid;
	
	/**
	 * 投资者账号
	 */
	private String phoneNum;
	
	/**
	 * 投资者姓名
	 */
	private String realName;
	
	/**
	 * 用户类型
	 */
	private String userType;
	private String userTypeDisp;
	
	/**
	 * 对账状态
	 */
	private String checkStatus;
	private String checkStatusDisp;
	
	

	/**
	 *  外部订单号
	 */
	private String outerOrderCode;
	
	/**
	 * 外部订单类型 
	 */
	private String outerOrderType;
	/**
	 * 业务订单类型
	 */
	private String buzzOrderType;
	private String buzzOrderTypeDisp;
	
	
	/**
	 * 外部交易金额
	 */
	private BigDecimal tradeAmount;
	
	/**
	 * 外部卡券金额
	 */
	private BigDecimal outerVoucher;

	/**
	 * 外部手续费
	 */
	private BigDecimal outerFee;
	

	/**
	 * 外部订单状态
	 */
	private String outerOrderStatus;

	/**
	 * 业务订单状态
	 */
	private String buzzOrderStatus;
	private String buzzOrderStatusDisp;

	/** 外部订单时间 */
	private Timestamp outerOrderTime;

	
	/** 外部投资人 */
	private String outerInvestorOid;
	
	/**
	 * 外部投资者账号
	 */
	private String outerPhoneNum;
	
	/**
	 * 外部投资者姓名
	 */
	private String outerRealName;
	
	/**
	 * 外部用户类型
	 */
	private String outerUserType;

	/**
	 * 业务用户类型
	 */
	private String buzzUserType;
	private String buzzUserTypeDisp;
	
	/**
	 * 三方支付对账状态
	 */
	private String reconciliationStatus;
	

	/** 比对状态 */
	private String outerCheckStatus;
	private String outerCheckStatusDisp;

	/** 处理结果 */
	private String dealStatus;
	private String dealStatusDisp;

}
