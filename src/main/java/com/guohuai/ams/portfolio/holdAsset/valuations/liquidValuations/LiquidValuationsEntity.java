package com.guohuai.ams.portfolio.holdAsset.valuations.liquidValuations;

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

import com.guohuai.ams.portfolio.holdAsset.liquidAsset.LiquidHoldEntity;

import lombok.Data;

/**
 * 投资组合持仓现金类资产每日估值
 * @author star.zhu
 * 2016年12月28日
 */
@Data
@Entity
@Table(name = "T_GAM_LIQUID_ASSET_VALUATIONS")
public class LiquidValuationsEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private String oid;
	
	// 关联持仓记录
	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "liquidHoldOid", referencedColumnName = "oid")
	private LiquidHoldEntity liquidHold;

	// 投资组合OID
	private String portfolioOid;
	// 估值日
	private Date valueDate;
	// 万份收益
	private BigDecimal dailyProfit;
	// 7日年化收益率
	private BigDecimal weeklyYield;
	// 持有份额
	private BigDecimal holdAmount;
	// 冻结份额
	private BigDecimal lockupAmount;
	// 当日收益
	private BigDecimal dayProfit;
	// 当日单价
	private BigDecimal price;
	// 累计收益
	private BigDecimal totalProfit;
	// 当前估值
	private BigDecimal valuations;
	// 当前净值
	private BigDecimal netValue;
	
	private String creater;
	private Timestamp createTime;
}
