package com.guohuai.ams.portfolio20.illiquid.hold.part;

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
import com.guohuai.ams.portfolio.entity.PortfolioEntity;
import com.guohuai.ams.portfolio20.illiquid.hold.PortfolioIlliquidHoldEntity;
import com.guohuai.ams.portfolio20.order.MarketOrderEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "T_GAM_PORTFOLIO_ILLIQUID_HOLD_PART")
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioIlliquidHoldPartEntity implements Serializable {

	private static final long serialVersionUID = 2041327925676725217L;

	// 持仓状态 - 持仓中
	public static final String HOLDSTATE_HOLDING = "HOLDING";
	// 持仓状态 - 已平仓
	public static final String HOLDSTATE_CLOSED = "CLOSED";

	public static final String EXCEPT_WAY_BOOK_VALUE = "BOOK_VALUE";
	public static final String EXCEPT_WAY_AMORTISED_COST = "AMORTISED_COST";

	@Id
	private String oid;

	// 合仓
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "holdOid", referencedColumnName = "oid")
	private PortfolioIlliquidHoldEntity hold;

	// 标的
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "illiquidAssetOid", referencedColumnName = "oid")
	private IlliquidAsset illiquidAsset;

	// 投资组合
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "portfolioOid", referencedColumnName = "oid")
	private PortfolioEntity portfolio;

	//订单
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "orderOid", referencedColumnName = "oid")
	private MarketOrderEntity order;

	// 估值
	private BigDecimal expectValue;
	// 持有份额
	private BigDecimal holdShare;
	// 剩余收益
	private BigDecimal holdIncome;
	// 冻结本金
	private BigDecimal lockupCapital;
	// 东i额收益
	private BigDecimal lockupIncome;
	// 单位净值
	private BigDecimal unitNet;
	// 建仓时间
	private Date investDate;
	// 起息日
	private Date valueDate;
	// [统计]累计收益
	private BigDecimal totalPfofit;
	// [统计]最新估值日
	private Date newValueDate;
	// [统计]最新估值日收益
	private BigDecimal newProfit;
	// 持仓状态
	private String holdState;
	// 估值方式
	private String exceptWay;

}
