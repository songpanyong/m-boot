package com.guohuai.ams.portfolio.holdAsset;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.guohuai.ams.portfolio.entity.PortfolioEntity;

import lombok.Data;

/**
 * 投资组合分仓要素
 * @author star.zhu
 * 2016年12月28日
 */
@Data
@Entity
@Table(name = "T_GAM_PORTFOLIO_HOLD_DET")
public class HoldDetEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private String oid;
	
	// 关联投资组合
	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "portfolioOid", referencedColumnName = "oid")
	private PortfolioEntity portfolio;

	// 标的资产OID
	private String assetOid;
	// 标的资产名称
	private String assetName;
	// 交易时间
	private Timestamp tradeTime;
	// 持有份额
	private BigDecimal holdAmount;
	// 交易份额
	private BigDecimal tradeAmount;
	// 买入全价
	private BigDecimal buyValue;
	// 买入净价
	private BigDecimal buyPrice;
	// 卖出全价
	private BigDecimal sellValue;
	// 卖出净价
	private BigDecimal sellPrice;
	// 投资损益
	private BigDecimal deviation;
	// 状态
	private String state;
	
	private String creater;
	private Timestamp createTime;
	private String operator;
	private Timestamp updateTime;
}
