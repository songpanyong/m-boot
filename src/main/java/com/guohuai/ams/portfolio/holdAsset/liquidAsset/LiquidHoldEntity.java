package com.guohuai.ams.portfolio.holdAsset.liquidAsset;

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

import lombok.Data;

/**
 * 投资组合持仓现金类资产
 * @author star.zhu
 * 2016年12月28日
 */
@Data
@Entity
@Table(name = "T_GAM_PORTFOLIO_LIQUID_ASSET")
public class LiquidHoldEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private String oid;
	
	// 关联标的
	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "liquidAssetOid", referencedColumnName = "oid")
	private LiquidAsset liquid;
	
	// 关联投资组合
	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "portfolioOid", referencedColumnName = "oid")
	private PortfolioEntity portfolio;
	
	// 投资日
	private Date investDate;
	// 起息日
	private Date valueDate;
	// 持有份额
	private BigDecimal holdShare;
	// 冻结份额
	private BigDecimal lockupAmount;
	// 当前单价
	private BigDecimal price;
	// 当日收益
	private BigDecimal dayProfit;
	// 累计收益
	private BigDecimal totalProfit;
	// 当前估值
	private BigDecimal valuations;
	// 当前净值
	private BigDecimal netValue;
	// 最新估值日
	private Date lastValueDate;
	
	private String creater;
	private Timestamp createTime;
	private String operator;
	private Timestamp updateTime;
}
