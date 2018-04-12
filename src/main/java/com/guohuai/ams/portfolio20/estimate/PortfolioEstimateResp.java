package com.guohuai.ams.portfolio20.estimate;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

import lombok.Data;

@Data
public class PortfolioEstimateResp {

	public PortfolioEstimateResp(PortfolioEstimateEntity entity){
		super();
		this.oid = entity.getOid();
		this.liquidEstimate = entity.getLiquidEstimate();
		this.illiquidEstimate = entity.getIlliquidEstimate();
		this.cashEstimate = entity.getCashEstimate();
		this.totalEstimate = entity.getCashEstimate().add(entity.getLiquidEstimate()).add(entity.getIlliquidEstimate());
		this.manageChargefee = entity.getManageChargefee();
		this.trusteeChargefee = entity.getTrusteeChargefee();
		this.chargefee = entity.getChargefee();
		this.estimateDate = entity.getEstimateDate();
		this.estimateTime = entity.getEstimateTime();
	}

	private String oid;
	// 现金类标的估值
	private BigDecimal liquidEstimate;
	// 非现金类标的估值
	private BigDecimal illiquidEstimate;
	// 现金估值
	private BigDecimal cashEstimate;
	// 总资产估值
	private BigDecimal totalEstimate;
	// 计提托管费用
	private BigDecimal manageChargefee;
	// 计提管理费用
	private BigDecimal trusteeChargefee;
	// 计提费用合计	
	private BigDecimal chargefee;
	// 估值日期	
	private Date estimateDate;
	// 估值时间	
	private Timestamp estimateTime;

}
