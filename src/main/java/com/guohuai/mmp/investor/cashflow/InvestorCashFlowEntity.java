package com.guohuai.mmp.investor.cashflow;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.guohuai.component.persist.UUID;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountEntity;
import com.guohuai.mmp.sys.SysConstant;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 投资人-资金变动明细
 * 
 * @author yuechao
 *
 */
@Entity
@Table(name = "T_MONEY_INVESTOR_CASHFLOW")
@lombok.Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
public class InvestorCashFlowEntity extends UUID implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 1622280278625300593L;

	
	/**
	 * 申购(invest expInvest)、赎回(normalRedeem, fastRedeem, normalExpRedeem)、
	 * 还本付息(cash)四种，其中，活动体验金自动申购，体验金到期自动赎回，体验金收入自动转入活期
	 */
	
	/** 资金变动类型--投资 */
	public static final String CASHFLOW_tradeType_invest = "invest";
	/** 资金变动类型--体验金投资 */
	public static final String CASHFLOW_tradeType_expGoldInvest = "expGoldInvest";
	/** 资金变动类型--体验金赎回 */
	public static final String CASHFLOW_tradeType_expGoldRedeem = "expGoldRedeem";
	/** 资金变动类型--普赎 */
	public static final String CASHFLOW_tradeType_normalRedeem = "normalRedeem";
	/** 资金变动类型--清盘赎回 */
	public static final String CASHFLOW_tradeType_clearRedeem = "clearRedeem";
	/** 资金变动类型--现金分红 */
	public static final String CASHFLOW_tradeType_dividend = "dividend";
	/** 资金变动类型--充值 */
	public static final String CASHFLOW_tradeType_deposit = "deposit";
	/** 资金变动类型--提现 */
	public static final String CASHFLOW_tradeType_withdraw = "withdraw";
	/** 资金变动类型--还本/付息 */
	public static final String CASHFLOW_tradeType_cash = "cash";
	/** 资金变动类型--募集失败退款 */
	public static final String CASHFLOW_tradeType_cashFailed = "cashFailed";
	/** 资金变动类型--手续费 */
	public static final String CASHFLOW_tradeType_fee = "fee";
	/** 资金变动类型--红包 */
	public static final String CASHFLOW_tradeType_redEnvelope = "redEnvelope";
	/** 资金变动类型--主账户余额转出至子账户 */
	public static final String CASHFLOW_tradeType_rollOut = "rollOut";
	/** 资金变动类型--子账户从主账户余额中转入 */
	public static final String CASHFLOW_tradeType_rollIn = "rollIn";
	
	/**
	 * 所属投资人
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "investorOid", referencedColumnName = "oid")
	private InvestorBaseAccountEntity investorBaseAccount;
	
	/**
	 * 关联单号
	 */
	private String orderOid;
	
	/**
	 * 交易金额
	 */
	private BigDecimal tradeAmount = SysConstant.BIGDECIMAL_defaultValue;

	/**
	 * 交易类型
	 */
	private String tradeType;

	private Timestamp updateTime;

	private Timestamp createTime;

}
