package com.guohuai.ams.portfolio20.liquid.hold.part.redeem;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.guohuai.ams.portfolio20.liquid.hold.part.PortfolioLiquidHoldPartEntity;
import com.guohuai.ams.portfolio20.order.MarketOrderEntity;
import com.guohuai.component.util.BigDecimalUtil;

import lombok.Data;

@Data
@Entity
@Table(name = "T_GAM_PORTFOLIO_LIQUID_HOLD_PART_REDEEM")
@DynamicInsert
@DynamicUpdate
public class PortfolioLiquidHoldPartRedeemEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public PortfolioLiquidHoldPartRedeemEntity() {
		this.redeemShare 	= BigDecimalUtil.init0;
		this.redeemAmount   = BigDecimalUtil.init0;
		this.redeemCapital  = BigDecimalUtil.init0;
		this.redeemIncome   = BigDecimalUtil.init0;
		
	}

	@Id
	private String oid;
	// 关联分仓记录
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "partsOid", referencedColumnName="oid")
	private PortfolioLiquidHoldPartEntity parts;
	
	//关联赎回订单
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "redeemOid", referencedColumnName="oid")
	private MarketOrderEntity marketOrder;
	
	
	// 赎回份额
	private BigDecimal redeemShare;
	// 赎回金额
	private BigDecimal redeemAmount;
	// 赎回本金
	private BigDecimal redeemCapital;
	// 赎回收益
	private BigDecimal redeemIncome;
	
}
