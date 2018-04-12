package com.guohuai.ams.portfolio20.illiquid.hold;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.guohuai.ams.illiquidAsset.IlliquidAsset;
import com.guohuai.ams.portfolio.entity.PortfolioEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "T_GAM_PORTFOLIO_ILLIQUID_HOLD")
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioIlliquidHoldEntity implements Serializable {

	private static final long serialVersionUID = 3007648758548103636L;

	// 持仓状态 - 持仓中
	public static final String HOLDSTATE_HOLDING = "HOLDING";
	// 持仓状态 - 已平仓
	public static final String HOLDSTATE_CLOSED = "CLOSED";

	public static final String EXCEPT_WAY_BOOK_VALUE = "BOOK_VALUE";
	public static final String EXCEPT_WAY_AMORTISED_COST = "AMORTISED_COST";

	@Id
	private String oid;

	// 标的
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "illiquidAssetOid", referencedColumnName = "oid")
	private IlliquidAsset illiquidAsset;

	// 投资组合
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "portfolioOid", referencedColumnName = "oid")
	private PortfolioEntity portfolio;

	// 建仓日期
	private Date investDate;
	// 起息日
	private Date valueDate;
	// 估值
	private BigDecimal expectValue;
	// 持有份额
	private BigDecimal holdShare;
	// 剩余收益
	private BigDecimal holdIncome;
	// 单位净值(估值)
	private BigDecimal natValue;
	// 冻结本金
	private BigDecimal lockupCapital;
	// 冻结收益
	private BigDecimal lockupIncome;
	// [统计]累计收益
	private BigDecimal totalPfofit;
	// [统计]最新估值日
	private Date newValueDate;
	// [统计]最新估值日收益
	private BigDecimal newPfofit;
	// 估值方式
	private String exceptWay;
	// 持仓状态
	private String holdState;

	private String creator;
	private Timestamp createTime;
	private String operator;
	private Timestamp updateTime;

}
