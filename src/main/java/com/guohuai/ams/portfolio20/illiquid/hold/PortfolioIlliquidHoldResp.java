package com.guohuai.ams.portfolio20.illiquid.hold;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

import javax.persistence.Id;

import com.guohuai.ams.illiquidAsset.IlliquidAsset;
import com.guohuai.ams.portfolio.entity.PortfolioEntity;

import lombok.Data;

@Data
public class PortfolioIlliquidHoldResp {

	public PortfolioIlliquidHoldResp(PortfolioIlliquidHoldEntity entity) {
		super();
		this.oid = entity.getOid();
		this.portfolioOid = entity.getPortfolio().getOid();
		this.portfolioName = entity.getPortfolio().getName();
		this.assetName = entity.getIlliquidAsset().getName();
		this.assetType = entity.getIlliquidAsset().getType();
		this.accrualType = entity.getIlliquidAsset().getAccrualType();
		this.investDate = entity.getInvestDate();
		this.valueDate = entity.getValueDate();
		this.expectValue = entity.getExpectValue();
		this.holdShare = entity.getHoldShare();
		this.holdIncome = entity.getHoldIncome();
		this.natValue = entity.getNatValue();
		this.lockupCapital = entity.getLockupCapital();
		this.lockupIncome = entity.getLockupIncome();
		this.totalPfofit = entity.getTotalPfofit();
		this.newPfofit = entity.getNewPfofit();
		this.newValueDate = entity.getNewValueDate();
		this.exceptWay = entity.getExceptWay();
		this.holdState = entity.getHoldState();
		this.lifeState = entity.getIlliquidAsset().getLifeState();
		this.illiquidAssetOid = entity.getIlliquidAsset().getOid();
	}

	@Id
	private String oid;
	private String portfolioOid;
	private String portfolioName;
	private String assetName;
	private String assetType;
	private String accrualType;
	//投资组合
	private PortfolioEntity portfolio;
	// 投资标的
	private String illiquidAssetOid;
	private IlliquidAsset illiquidAsset;
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
	//生命周期状态
	private String lifeState;

	private String creator;
	private Timestamp createTime;
	private String operator;
	private Timestamp updateTime;
}
