package com.guohuai.ams.portfolio20.invest.losses;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.guohuai.ams.illiquidAsset.IlliquidAsset;
import com.guohuai.ams.liquidAsset.LiquidAsset;
import com.guohuai.ams.portfolio.entity.PortfolioEntity;
import com.guohuai.ams.portfolio20.illiquid.hold.repayment.PortfolioIlliquidHoldRepaymentEntity;
import com.guohuai.ams.portfolio20.order.MarketOrderEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author created by Arthur
 * @date 2017年2月20日 - 下午6:33:45
 */

@Data
@Table(name = "T_GAM_PORTFOLIO_INVEST_LOSSES")
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioInvestLossesEntity implements Serializable {

	private static final long serialVersionUID = -3319688424922888846L;

	public static final String TYPE_LIQUID = "LIQUID";
	public static final String TYPE_ILLIQUID = "ILLIQUID";

	@Id
	private String oid;
	private String type;

	//投资组合
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "portfolioOid", referencedColumnName = "oid")
	private PortfolioEntity portfolio;

	//关联现金类标的
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "liquidAssetOid", referencedColumnName = "oid")
	private LiquidAsset liquidAsset;

	//关联非现金类标的
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "illiquidAssetOid", referencedColumnName = "oid")
	private IlliquidAsset illiquidAsset;

	//关联非现金类标的还款计划
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "illiquidAssetRepaymentOid", referencedColumnName = "oid")
	private PortfolioIlliquidHoldRepaymentEntity illiquidAssetRepayment;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "orderOid", referencedColumnName = "oid")
	private MarketOrderEntity order;

	// 交易日
	private Date orderDate;
	// 投资本金
	private BigDecimal investCapital;
	// 投资收益
	private BigDecimal investIncome;
	// 持有份额
	private BigDecimal holdShare;
	// 卖出份额
	private BigDecimal selloutShare;
	// 卖出价格
	private BigDecimal selloutPrice;
	// 投资损益
	private BigDecimal losses;

}
