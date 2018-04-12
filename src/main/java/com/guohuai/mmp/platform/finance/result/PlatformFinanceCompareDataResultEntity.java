package com.guohuai.mmp.platform.finance.result;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.guohuai.component.persist.UUID;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "T_MONEY_CHECK_COMPAREDATA_RESULT")
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Data
public class PlatformFinanceCompareDataResultEntity extends UUID {

	/**
	 * 
	 */
	private static final long serialVersionUID = 227955108800580211L;


	/** 对账处理结果-待处理 */
	public static final String RESULT_dealStatus_toDeal = "toDeal";
	/** 对账处理结果-处理中 */
	public static final String RESULT_dealStatus_dealing = "dealing";
	/** 对账处理结果-已处理 */
	public static final String RESULT_dealStatus_dealt = "dealt";
	
	
	public static final String RESULT_userType_investor = "investor";
	public static final String RESULT_userType_spv = "spv";
	
	
	/** 交易类型--充值 */
	public static final String RESULT_orderType_deposit = "deposit";
	/** 交易类型--提现 */
	public static final String RESULT_orderType_withdraw = "withdraw";

	/**
	 * 所属对账批次
	 */
	private String checkOid;
	/**
	 * 订单号
	 */
	private String orderCode;
	
	/**
	 * 支付流水号
	 */
	private String iPayNo;
	
	/**
	 * 订单类型
	 */
	private String orderType;
	
	
	/**
	 * 用户类型
	 */
	private String userType;

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
	 * 订单金额
	 */
	private BigDecimal orderAmount = BigDecimal.ZERO;

	/**
	 * 手续费
	 */
	private BigDecimal fee = BigDecimal.ZERO;

	/**
	 * 卡券金额
	 */
	private BigDecimal voucher = BigDecimal.ZERO;

	/**
	 * 订单状态
	 */
	private String orderStatus;
	
	/**
	 * 冻结状态
	 */
	private String frozenStatus;

	/**
	 * 订单时间
	 */
	private Timestamp orderTime;

	/**
	 * 对账状态
	 */
	private String checkStatus;

	/** 外部订单号 */
	private String outerOrderCode;
	/**
	 * 外部订单类型
	 */
	private String outerOrderType;
	/**
	 * 业务订单类型
	 */
	private String buzzOrderType;

	/**
	 * 外部用户类型
	 */
	private String outerUserType;

	/**
	 * 业务用户类型
	 */
	private String buzzUserType;

	/**
	 * 外部用户
	 */
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
	 * 交易金额
	 */
	private BigDecimal tradeAmount = BigDecimal.ZERO;

	/**
	 * 外部手续费
	 */
	private BigDecimal outerFee = BigDecimal.ZERO;

	/**
	 * 外部卡券金额
	 */
	private BigDecimal outerVoucher;

	/**
	 * 外部订单状态
	 */
	private String outerOrderStatus;
	
	/**
	 * 业务订单状态
	 */
	private String buzzOrderStatus;

	/**
	 * 外部订单时间
	 */
	private Timestamp outerOrderTime;
	
	/**
	 * 三方支付对账状态
	 */
	private String reconciliationStatus;

	/**
	 * 外部对账状态
	 */
	private String outerCheckStatus;

	/** 处理结果 */
	private String dealStatus;

	/** 修改时间 */
	private Timestamp updateTime;
	/** 创建时间 */
	private Timestamp createTime;

}
