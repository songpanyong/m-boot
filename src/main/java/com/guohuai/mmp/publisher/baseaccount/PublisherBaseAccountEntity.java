package com.guohuai.mmp.publisher.baseaccount;

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
 * 发行人-基本账户
 * 
 * @author yuechao
 *
 */
@Entity
@Table(name = "T_MONEY_PUBLISHER_BASEACCOUNT")
@lombok.Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
public class PublisherBaseAccountEntity extends UUID {

	private static final long serialVersionUID = -6301241499172524045L;

	
	
	public static final String PUBLISHER_BASE_ACCOUNT_STATUS_created = "created";
	public static final String PUBLISHER_BASE_ACCOUNT_STATUS_applied = "applied";
	public static final String PUBLISHER_BASE_ACCOUNT_STATUS_confirmed = "confirmed";

	/**
	 * 三方会员账户
	 */
	private String memberId;
	
	/**
	 * 手机号
	 */
	private String phone;
	
	/**
	 * 姓名
	 */
	private String realName;
	
	/**
	 * 证件号
	 */
	private String certificateNo;
	
	/**
	 * 银行名称
	 */
	private String bankName;
	
	/**
	 * 银行账号
	 */
	private String cardNo;

	/**
	 * 余额 
	 */
	private BigDecimal basicBalance = BigDecimal.ZERO;
	
	/**
	 * 归集清算户余额
	 */
	private BigDecimal collectionSettlementBalance = BigDecimal.ZERO;
	
	/**
	 * 可用金户余额
	 */
	private BigDecimal availableAmountBalance = BigDecimal.ZERO;
	
	/**
	 * 冻结户余额
	 */
	private BigDecimal frozenAmountBalance = BigDecimal.ZERO;
	
	/**
	 * 提现可用金余额
	 */
	private BigDecimal withdrawAvailableAmountBalance = BigDecimal.ZERO;

	/**
	 * 状态
	 */
	private String status;

	private Timestamp updateTime;

	private Timestamp createTime;
}
