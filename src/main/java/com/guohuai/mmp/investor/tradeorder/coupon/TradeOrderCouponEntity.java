package com.guohuai.mmp.investor.tradeorder.coupon;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.guohuai.basic.component.ext.hibernate.UUID;
import com.guohuai.mmp.investor.bankorder.InvestorBankOrderEntity;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountEntity;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderEntity;
import com.guohuai.mmp.sys.SysConstant;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@lombok.Data
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
@Entity
@Table(name="T_MONEY_INVESTOR_TRADEORDER_COUPON")
public class TradeOrderCouponEntity extends UUID implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/** 卡券类型- 抵用券*/
	public static final String TRADEORDERCOUPON_type_coupon = "coupon";
	/** 卡券类型- 加息券*/
	public static final String TRADEORDERCOUPON_type_rateCoupon = "rateCoupon";
	/** 卡券类型- 体验金*/
	public static final String TRADEORDERCOUPON_type_tasteCoupon = "tasteCoupon";
	/** 卡券类型- 红包*/
	public static final String TRADEORDERCOUPON_type_redEnvelope = "redEnvelopeCoupon";
	

	/** 关联投资者 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="investorOid", referencedColumnName="oid")
	private InvestorBaseAccountEntity investorBaseAccount;
	
	/** 关联委托单 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="orderOid", referencedColumnName="oid")
	private InvestorTradeOrderEntity investorTradeOrder;
	
	/** 投资者银行委托单 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="bankOrderOid", referencedColumnName="oid")
	private InvestorBankOrderEntity investorBankOrder;
	
	/** 卡券金额 */
	private BigDecimal couponAmount = SysConstant.BIGDECIMAL_defaultValue;
	
	/** 卡券编号 */
	private String coupons;
	
	/** 卡券类型 */
	private String couponType;
	
	/** 加息收益率-年  需除100	 */
	private BigDecimal additionalInterestRate = SysConstant.BIGDECIMAL_defaultValue;
	
	/** 加息收益率- 活期是复利日利率   定期是单利日利率	 */
	private BigDecimal dAdditionalInterestRate = SysConstant.BIGDECIMAL_defaultValue;
	
	/** 加息有效天数 */
	private Integer affectiveDays;
	
	/** createTime */
	private Timestamp createTime;
	
	/** updateTime */
	private Timestamp updateTime;
}
