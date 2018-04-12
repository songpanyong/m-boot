package com.guohuai.mmp.investor.orderlog;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.guohuai.component.persist.UUID;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 订单日志
 * @author yuechao
 *
 */
@Entity
@Table(name = "T_MONEY_ORDER_LOG")
@lombok.Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
public class OrderLogEntity extends UUID implements Serializable {
	
	private static final long serialVersionUID = 2425452922159934702L;
	
	/** 订单状态--投资份额确认 */
	public static final String ORDERLOG_orderStatus_confirmed = "confirmed";
	public static final String ORDERLOG_orderStatus_confirmFailed = "confirmFailed";

	/** 订单类型--投资平仓 */
	public static final String ORDERLOG_orderType_investClose = "investClose";
	
	
	
	/**
	 * 订单类型
	 */
	private String orderType;
	
	/**
	 * 订单号
	 */
	private String tradeOrderOid;
	
	/**
	 * 订单状态	
	 */
	private String orderStatus;
	/**
	 * 关联赎回订单号
	 */
	private String referredOrderCode;
	
	/**
	 * 关联赎回金额
	 */
	private BigDecimal referredOrderAmount;
	
	/**
	 * 错误代码
	 */
	private Integer errorCode;
	
	/**
	 * 错误消息
	 */
	private String errorMessage;
	
	private Timestamp createTime;
	private Timestamp updateTime;
	
}
