package com.guohuai.mmp.platform.publisher.product.offset;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@lombok.Builder
@lombok.Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductOffsetMoneyRep {
	String productOid;
	
	String productCode;
	String productName;
	
	/**
	 * 申购金额
	 */
	private BigDecimal investAmount;

	/**
	 * 赎回金额
	 */
	private BigDecimal redeemAmount;

	/**
	 * 净头寸
	 */
	private BigDecimal netPosition;
	
	/**
	 * 投资组合oid，用于页面跳转
	 */
	private String portfolioOid;
	
	/**
	 * 投资组合名称
	 */
	private String portfolioName;
	
	/**
	 * 投资组合比例展示
	 */
	private String portfolioRatio;
	
	/**
	 * 可用金额
	 */
	private BigDecimal availableAmount;
	
	/**
	 * 现金类
	 */
	private BigDecimal cashCategory;
	
	/**
	 * 非现金类
	 */
	private BigDecimal nonCashCategory;
	
	/**
	 * 小计
	 */
	private BigDecimal subtotal;
	
	/**
	 * 昨日投资组合可用金额
	 */
	private BigDecimal yesterdayCashEstimate;
}
