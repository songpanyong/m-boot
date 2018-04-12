package com.guohuai.ams.portfolio20.order;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class MarketOrderForm {
	private String oid;
	private String portfolioOid;
	private String liquidAssetOid;
	private String illiquidAssetOid;
	private String illiquidAssetRepaymentOid;
	private BigDecimal orderAmount;
	private BigDecimal orderShare;
	private BigDecimal orderCapital;
	private BigDecimal orderIncome;
	private String exceptWay;
	private String forceClose;
}
