package com.guohuai.ams.portfolio20.invest.losses;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author created by Arthur
 * @date 2017年2月20日 - 下午6:33:45
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioInvestLossesResp implements Serializable {

	private static final long serialVersionUID = -410440402786135054L;

	public PortfolioInvestLossesResp(PortfolioInvestLossesEntity entity) {
		super();
		this.oid = entity.getOid();
		this.portfolioOid = entity.getPortfolio().getOid();
		if (null != entity.getLiquidAsset()) {
			this.assetName = entity.getLiquidAsset().getName();
			this.assetType = entity.getLiquidAsset().getType();
			this.type = "LIQUID";
		}
		if (null != entity.getIlliquidAsset()) {
			this.assetName = entity.getIlliquidAsset().getName();
			this.assetType = entity.getIlliquidAsset().getType();
			this.type = "ILLIQUID";
		}
		this.investCapital = entity.getInvestCapital();
		this.orderDate = entity.getOrderDate();
		this.investIncome = entity.getInvestIncome();
		this.holdShare = entity.getHoldShare();
		this.selloutShare = entity.getSelloutShare();
		this.selloutPrice = entity.getSelloutPrice();
		this.losses = entity.getLosses();
	}

	private String oid;
	private String type;

	//投资组合
	private String portfolioOid;
	private String liquidAssetOid;
	private String illiquidAssetOid;
	private String illiquidAssetRepaymentOid;
	private String orderOid;

	//资产
	private String assetName;
	private String assetType;

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
