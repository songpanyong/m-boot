package com.guohuai.mmp.platform.finance.data;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.guohuai.component.persist.UUID;
import com.guohuai.mmp.platform.finance.check.PlatformFinanceCheckEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 远程对账数据
 * 
 * @author suzhicheng
 *
 */
@Entity
@Table(name = "T_MONEY_CHECK_COMPAREDATA")
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Data
public class PlatformFinanceCompareDataEntity extends UUID {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4422317274650884893L;

	/** 尚未轧账 */
	public static final String DATA_checkStatus_no = "no";
	/** 状态不一致--待回调成功 */
	public static final String DATA_checkStatus_notifyOk = "notifyOk";
	/** 状态不一致--待回调失败*/
	public static final String DATA_checkStatus_notifyFail = "notifyFail";
	/** 长款 */
	public static final String DATA_checkStatus_long = "long";
	/** 短款 */
	public static final String DATA_checkStatus_short = "short";
	/** 一致 */
	public static final String DATA_checkStatus_equal = "equal";
	
	/**
	 * 所属对账批次
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "checkOid", referencedColumnName = "oid")
	private PlatformFinanceCheckEntity platformFinanceCheck;

	/** 订单号 */
	private String orderCode;
	/** 订单类型 */
	private String orderType;
	/**
	 * 业务订单类型
	 */
	private String buzzOrderType;

	/**
	 * 用户类型
	 */
	private String userType;

	/**
	 * 业务用户类型
	 */
	private String buzzUserType;

	/** 交易金额 */
	private BigDecimal tradeAmount = BigDecimal.ZERO;

	/**
	 * 手续费
	 */
	private BigDecimal fee;

	/**
	 * 卡券金额
	 */
	private BigDecimal voucher;

	/** 订单状态 */
	private String orderStatus;
	
	/**
	 * 业务订单状态
	 */
	private String buzzOrderStatus;

	/** 订单时间 */
	private Timestamp orderTime;
	/** 投资人 */
	private String investorOid;
	/**
	 * 投资人账号
	 */
	private String phoneNum;
	
	/**
	 * 投资人姓名
	 */
	private String realName;
	
	/**
	 * 三方对账状态
	 */
	private String reconciliationStatus;

	/** 比对状态 */
	private String checkStatus;
	/** 创建时间 */
	private Timestamp createTime;
	/** 修改时间 */
	private Timestamp updateTime;

}
