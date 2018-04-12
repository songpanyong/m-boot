package com.guohuai.ams.portfolio20.estimate.illiquid;

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
import com.guohuai.ams.portfolio20.estimate.PortfolioEstimateEntity;
import com.guohuai.ams.portfolio20.illiquid.hold.PortfolioIlliquidHoldEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author created by Arthur
 * @date 2017年2月21日 - 下午5:01:49
 */
@Data
@Entity
@Table(name = "T_GAM_PORTFOLIO_ILLIQUID_HOLD_ESTIMATE")
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioIlliquidEstimateEntity implements Serializable {

	private static final long serialVersionUID = -6092022802267052023L;

	@Id
	private String oid;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "holdOid", referencedColumnName = "oid")
	private PortfolioIlliquidHoldEntity hold;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "illiquidAssetOid", referencedColumnName = "oid")
	private IlliquidAsset asset;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "portfolioOid", referencedColumnName = "oid")
	private PortfolioEntity portfolio;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "portfolioEstimateOid", referencedColumnName = "oid")
	private PortfolioEstimateEntity portfolioEstimate;

	// 前一日持有份额
	private BigDecimal lastHoldShare;

	// 前一日持有收益
	private BigDecimal lastHoldIncome;

	// 前一日估值
	private BigDecimal lastEstimate;

	// 前一日单位净值
	private BigDecimal lastUnitNet;

	// 持有分额
	private BigDecimal holdShare;

	// 持有收益
	private BigDecimal holdIncome;

	// 估值
	private BigDecimal estimate;

	// 单位净值
	private BigDecimal unitNet;

	// 收益
	private BigDecimal profit;

	// 收益率
	private BigDecimal profitRate;
	
	// 标的状态
	private String lifeState;

	// 估值日期
	private Date estimateDate;

	// 估值时间	
	private Timestamp estimateTime;

}
