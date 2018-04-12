package com.guohuai.mmp.publisher.holdapart.closedetails;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.guohuai.ams.product.Product;
import com.guohuai.component.persist.UUID;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountEntity;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderEntity;
import com.guohuai.mmp.sys.SysConstant;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 发行人-投资人-分仓平仓明细
 * 
 * @author yuechao
 *
 */
@Entity
@Table(name = "T_MONEY_PUBLISHER_INVESTOR_HOLDAPART_CLOSEDETAILS")
@lombok.Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
public class CloseDetailsEntity extends UUID {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -367515237865702143L;

	/** 变动方向--进 */
	public static final String DETAIL_changeDirection_in = "in";
	/** 变动方向--出 */
	public static final String DETAIL_changeDirection_out = "out";

	
	/**
	 * 所属赎回单
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "redeemOrderOid", referencedColumnName = "oid")
	private InvestorTradeOrderEntity redeemOrder;
	
	/**
	 * 所属申购单
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "investOrderOid", referencedColumnName = "oid")
	private InvestorTradeOrderEntity investOrder;
	
	/**
	 * 所属产品
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "productOid", referencedColumnName = "oid")
	private Product product;
	
	/**
	 * 所属投资人
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "investorOid", referencedColumnName = "oid")
	private InvestorBaseAccountEntity investorBaseAccount;
	
	/**
	 * 变动份额
	 */
	BigDecimal changeVolume = SysConstant.BIGDECIMAL_defaultValue;
	
	/**
	 * 变动方向
	 */
	String changeDirection;
	
	/** 基础收益率 */
	BigDecimal basicRatio;
	/** 奖励收益率 */
	BigDecimal rewardIncomeRatio;
	/** 总收益率 */
	BigDecimal incomeRatio;
	/** 持有时间 */
	Long holdDays;
	
	Timestamp updateTime;
	Timestamp createTime;
	
}
