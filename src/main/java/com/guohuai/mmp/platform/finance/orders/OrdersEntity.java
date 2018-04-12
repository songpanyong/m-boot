package com.guohuai.mmp.platform.finance.orders;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.guohuai.basic.component.ext.hibernate.UUID;
import com.guohuai.mmp.platform.finance.check.PlatformFinanceCheckEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 平台-财务-本地数据
 * 
 * @author suzhicheng
 *
 */
@Entity
@Table(name = "T_MONEY_ORDERS")
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Data
public class OrdersEntity extends UUID implements Serializable {
	/**
	* 1.1 渠道对账单下载
	* 1.2 渠道对账单标准化
	* 1.3 本地交易记录准备
	* 1.4 轧帐
	* 1.5 平帐
	*/
	private static final long serialVersionUID = 5572358693898559645L;
	
	/** 尚未轧账 */
	public static final String ORDER_checkStatus_no = "no";
	/** 异常*/
	public static final String ORDER_checkStatus_exception = "exception";
	/** 状态不一致--待回调成功 */
	public static final String ORDER_checkStatus_notifyOk = "notifyOk";
	/** 状态不一致--待回调失败*/
	public static final String ORDER_checkStatus_notifyFail = "notifyFail";
	/** 长款(补单) */
	public static final String ORDER_checkStatus_long = "long";
	/** 作废 */
	public static final String ORDER_checkStatus_short = "short";
	/** 一致 */
	public static final String ORDER_checkStatus_equal = "equal";
	/** 成功到失败 */
	public static final String ORDER_checkStatus_okTofail = "okTofail";
	/** 失败到成功 */
	public static final String ORDER_checkStatus_failToOk = "failToOk";
	
	/** 用户类型--投资者 */
	public static final String ORDER_userType_investor = "investor";
	/** 用户类型--发行人 */
	public static final String ORDER_userType_spv = "spv";
	
	/**
	 * 所属对账批次
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "checkOid", referencedColumnName = "oid")
	private PlatformFinanceCheckEntity platformFinanceCheck;

	/**
	 * 订单号
	 */
	private String orderCode;
	/**
	 * 支付流水号
	 */
	private String iPayNo;
	/**
	 * 用户类型
	 */
	private String userType;
	/**
	 * 订单类型
	 */
	private String orderType;
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
	 * 用户
	 */
	private String investorOid;
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
	
	
}
