package com.guohuai.ams.portfolio20.liquid.hold;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

import javax.persistence.Id;

import com.guohuai.ams.liquidAsset.LiquidAsset;
import com.guohuai.ams.portfolio.entity.PortfolioEntity;

import lombok.Data;


@Data
public class PortfolioLiquidHoldResp  {

	
	public PortfolioLiquidHoldResp(PortfolioLiquidHoldEntity entity) {
		super();
		this.oid = entity.getOid();
		this.portfolioOid = entity.getPortfolio().getOid();
		this.portfolioName  = entity.getPortfolio().getName();
		this.assetName = entity.getLiquidAsset().getName();
		this.assetType = entity.getLiquidAsset().getType();
		this.holdShare=entity.getHoldShare();
		this.holdState = entity.getHoldState();
		this.holdAmount=entity.getHoldAmount();
		this.investAmount = entity.getInvestAmount();
		this.investDate = entity.getInvestDate();
		this.valueDate = entity.getValueDate();
		this.investCome = entity.getInvestCome();
		this.totalPfofit = entity.getTotalPfofit();
		this.newPfofit = entity.getNewPfofit();
		this.newValueDate = entity.getNewValueDate();
		this.liquidAssetOid =entity.getLiquidAsset().getOid();
	}

	@Id
	private String oid;
	private String portfolioOid;
	private String portfolioName;
	private String assetName;
	private String assetType;
	//投资组合
	private PortfolioEntity portfolio;
	// 投资标的
	private String liquidAssetOid;
	private LiquidAsset liquidAsset;
	//建仓日期
	private Date investDate;
	// 起息日
	private Date valueDate;
	// 持有金额
	private BigDecimal holdAmount;	
	// 持有份额
	private BigDecimal holdShare;
	// 投资本金
	private BigDecimal investAmount;
    //投资收益
	private BigDecimal investCome;
	//[统计]累计收益
	private BigDecimal totalPfofit;
	//[统计]最新估值日	lastValueDate
	private Date newValueDate;
	//[统计]最新估值日收益
	private BigDecimal newPfofit;
	//赎回冻结份额
	private BigDecimal lockupAmount;
	//持仓状态
	private String holdState;
	private String creator;
	private String operator;
	private Timestamp createTime;
	private Timestamp updateTime;
}
