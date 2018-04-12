package com.guohuai.ams.portfolio20.estimate.liquid;

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

import com.guohuai.ams.liquidAsset.LiquidAsset;
import com.guohuai.ams.portfolio.entity.PortfolioEntity;
import com.guohuai.ams.portfolio20.estimate.PortfolioEstimateEntity;
import com.guohuai.ams.portfolio20.liquid.hold.PortfolioLiquidHoldEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author created by Arthur
 * @date 2017年2月21日 - 下午5:01:49
 */
@Data
@Entity
@Table(name = "T_GAM_PORTFOLIO_LIQUID_HOLD_ESTIMATE")
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioLiquidEstimateEntity implements Serializable {

	private static final long serialVersionUID = -1190666795331085397L;

	@Id
	private String oid;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "holdOid", referencedColumnName = "oid")
	private PortfolioLiquidHoldEntity hold;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "liquidAssetOid", referencedColumnName = "oid")
	private LiquidAsset asset;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "portfolioOid", referencedColumnName = "oid")
	private PortfolioEntity portfolio;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "portfolioEstimateOid", referencedColumnName = "oid")
	private PortfolioEstimateEntity portfolioEstimate;

	// 前一日估值
	private BigDecimal lastEstimate;

	// 前一日现价率
	private BigDecimal lastPriceRatio;

	// 前一日单位净值
	private BigDecimal lastUnitNet;

	// 估值基数
	private BigDecimal basic;

	// 估值
	private BigDecimal estimate;

	// 现价率
	private BigDecimal priceRatio;

	// 单位净值
	private BigDecimal unitNet;

	// 收益
	private BigDecimal profit;

	// 收益率
	private BigDecimal profitRate;

	// 估值日期
	private Date estimateDate;

	// 估值时间	
	private Timestamp estimateTime;

}
