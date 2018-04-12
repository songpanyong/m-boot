package com.guohuai.ams.portfolio20.order;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

import lombok.Data;

@Data
public class MarketOrderResp {

	public MarketOrderResp(MarketOrderEntity entity) {
		super();
		this.oid = entity.getOid();
		this.portfolioOid = entity.getPortfolio().getOid();
		this.portfolioName  = entity.getPortfolio().getName();
		if ("LIQUID".equals(entity.getType())) {
			this.assetName = entity.getLiquidAsset().getName();
			this.type = entity.getType();
			this.assetType = entity.getLiquidAsset().getType();
		} else if ("ILLIQUID".equals(entity.getType())) {
			this.assetName = entity.getIlliquidAsset().getName();
			this.type = entity.getType();
			this.assetType = entity.getIlliquidAsset().getType();
		}
		this.orderState = entity.getOrderState();
		this.dealType = entity.getDealType();
		this.orderAmount = entity.getOrderAmount();
		this.tradeShare = entity.getTradeShare();
		this.capital = entity.getCapital();
		this.income = entity.getIncome();
		this.orderDate = entity.getOrderDate();
		this.auditor = entity.getAuditor();
		this.auditTime = entity.getAuditTime();
		this.auditMark = entity.getAuditMark();
	}
	
	private String oid;
	private String portfolioOid;
	private String portfolioName;
	private String assetName;
	private String type;
	private String assetType;
	
	// 订单状态(待审核: CREATE;通过: PASS;驳回: FAIL;已删除: DELETE)
	private String orderState;
	// 交易类型
	private String dealType;
	// 交易金额
	private BigDecimal orderAmount;
	// 交易份额
	private BigDecimal tradeShare;
	// 交易本金
	private BigDecimal capital;
	// 交易收益
	private BigDecimal income;
	//交易日期
	private Date orderDate;
	//审核人 
	private String auditor;
	// 审批时间
	private Timestamp auditTime;
	//审核意见 
	private String auditMark;
	// 估值方式
	private String exceptWay;
	// 是否强制平仓
	private String forceClose;
}
