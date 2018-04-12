package com.guohuai.mmp.investor.bankorder;

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
 * 投资人-银行委托单
 * 
 * @author yuechao
 *
 */
@Entity
@Table(name = "T_MONEY_INVESTOR_BANKORDER")
@lombok.Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
public class InvestorBankOrderEntity extends UUID {
	/**
	* 
	*/
	private static final long serialVersionUID = -5183984182066265200L;

	/** 交易类型--充值 */
	public static final String BANKORDER_orderType_deposit = "deposit";
	/** 交易类型--充值长款 */
	public static final String BANKORDER_orderType_depositLong = "depositLong";
	/** 交易类型--提现 */
	public static final String BANKORDER_orderType_withdraw = "withdraw";
	/** 交易类型--提现长款 */
	public static final String BANKORDER_orderType_withdrawLong = "withdrawLong";
	/** 交易类型--红包 */
	public static final String BNAKORDER_orderType_redEnvelope = "redEnvelope";
	/** 交易类型--账户之间转入 */
	public static final String BNAKORDER_orderType_ROLLIN = "rollIn";
	/** 交易类型--账户之间转出 */
	public static final String BNAKORDER_orderType_ROLLOUT = "rollOut";

	
	/** 手续费支付方--平台 */
	public static final String BANKORDER_feePayer_platform = "platform";
	/** 手续费支付方--用户 */
	public static final String BANKORDER_feePayer_user = "user";

	/** 是否使用了卡券-是 */
	public static final String BANKORDER_usedCoupons_yes = "yes";
	/** 是否使用了卡券-否 */
	public static final String BANKORDER_usedCoupons_no = "no";
	
	/** 订单状态--已申请 */
	public static final String BANKORDER_orderStatus_submitted = "submitted";
	/** 订单状态--申请失败 */
	public static final String BANKORDER_orderStatus_submitFailed = "submitFailed";
	/** 订单状态--待支付 */
	public static final String BANKORDER_orderStatus_toPay = "toPay";
	/** 订单状态--支付失败 */
	public static final String BANKORDER_orderStatus_payFailed = "payFailed";
	/** 订单状态--支付成功 */
	public static final String BANKORDER_orderStatus_paySuccess = "paySuccess";
	public static final String BANKORDER_orderStatus_done = "done";
	/** 作废 */
	public static final String BANKORDER_orderStatus_abandoned = "abandoned";
	
	
	
	/** 订单状态--尚未结算 */
	public static final String BANKORDER_payStatus_noPay = "noPay";
	/** 订单状态--待支付 */
	public static final String BANKORDER_payStatus_toPay = "toPay";
	/** 订单状态--支付失败 */
	public static final String BANKORDER_payStatus_payFailed = "payFailed";
	/** 订单状态--支付成功 */
	public static final String BANKORDER_payStatus_paySuccess = "paySuccess";

	
	/** 已冻结 */
	public static final String BANKORDER_frozenStatus_frozened = "frozened";
	/** 解冻中 */
	public static final String BANKORDER_frozenStatus_toIceOut = "toIceOut";
	/** 已解冻 */
	public static final String BANKORDER_frozenStatus_iceOut = "iceOut";
	
	
	/**
	 * 所属投资人
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "investorOid", referencedColumnName = "oid")
	private InvestorBaseAccountEntity investorBaseAccount;

	/**
	 * 订单号
	 */
	private String orderCode;

	/**
	 * 交易类型
	 */
	private String orderType;

	/**
	 * 手续费支付方
	 */
	private String feePayer;

	/**
	 * 手续费
	 */
	private BigDecimal fee = SysConstant.BIGDECIMAL_defaultValue;

	/**
	 * 订单金额
	 */
	private BigDecimal orderAmount = SysConstant.BIGDECIMAL_defaultValue;
	
	/**
	 * N是否使用了卡券
	 */
	private String usedCoupons = InvestorBankOrderEntity.BANKORDER_usedCoupons_no;

	/**
	 * 订单状态
	 */
	private String orderStatus;
	
	/**
	 * 结算状态
	 */
	private String payStatus;
	
	/**
	 * 冻结状态
	 */
	private String frozenStatus;
	
	
	/**
	 * 订单时间
	 */
	private Timestamp orderTime;

	/**
	 * 订单创建时间
	 */
	private Timestamp iceOutTime;

	
	
	/**
	 * 订单完成时间
	 */
	private Timestamp completeTime;
	
	private Timestamp createTime;

	private Timestamp updateTime;
	
	private String wishplanOid;
}
