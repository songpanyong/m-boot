package com.guohuai.mmp.investor.baseaccount.check;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.guohuai.component.persist.UUID;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountEntity;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "T_MONEY_INVESTOR_CHECK")
@lombok.Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
public class CheckEntity extends UUID {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** 对账状态：对账成功 */
	public static final String Check_Status_OK = "ok";
	/** 对账状态：对账失败 */
	public static final String Check_Status_Failed = "failed";
	/** 对账状态：对账忽略 */
	public static final String Check_Status_Ignore = "ignore";
	
	/** 用户状态：已锁定 */
	public static final String Check_USER_Status_IsLock = "isLock";
	/** 用户状态：未锁定 */
	public static final String Check_USER_Status_IsOk = "isOk";
	
	/** 所属投资人 */
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "investorOid", referencedColumnName = "oid")
	private InvestorBaseAccountEntity investorBaseAccount;
	
	/** 资金总额 */
	private BigDecimal moneyAmount = BigDecimal.ZERO;
	
	/** 资产总额 */
	private BigDecimal capitalAmount = BigDecimal.ZERO;
	
	/** 已充值 */
	private BigDecimal recharge = BigDecimal.ZERO;
	
	/** 已成功提现 */
	private BigDecimal withdraw = BigDecimal.ZERO;
	
	/** 定期累计收益 */
	private BigDecimal tnInterest = BigDecimal.ZERO;
	
	/** 活期累计收益 */
	private BigDecimal t0Interest = BigDecimal.ZERO;
	
	/** 卡券红包金额 */
	private BigDecimal couponAmt = BigDecimal.ZERO;
	
	/** 前一日资产总额 */
	private BigDecimal yesterdayCapitalAmt = BigDecimal.ZERO;
	
	/** 申购可用金额： */
	private BigDecimal balance = BigDecimal.ZERO;
	
	/** 提现申请中金额 */
	private BigDecimal applyBalance = BigDecimal.ZERO;
	
	/** 活期申购冻结 */
	private BigDecimal t0ApplyAmt = BigDecimal.ZERO;
	
	/** 活期持有 */
	private BigDecimal t0HoldAmt = BigDecimal.ZERO;
	
	/** 定期申购冻结 */
	private BigDecimal tnApplyAmt = BigDecimal.ZERO;
	
	/** 定期持有 */
	private BigDecimal tnHoldlAmt = BigDecimal.ZERO;
	
	/** 累计补登金额 */
	private BigDecimal allRecorrectAmt = BigDecimal.ZERO;
	
	/** 对账状态 */
	private String checkStatus;
	
	/** 用户状态 */
	private String userStatus;
	
	/** 对账日期 */
	private String checkTime;
	
	private Timestamp updateTime;
	
	private Timestamp createTime;
}
