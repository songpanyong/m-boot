package com.guohuai.ams.portfolio.form;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;

import com.guohuai.ams.product.Product;

import lombok.Data;

/**
 * @author created by Arthur
 * @date 2017年2月25日 - 下午4:00:44
 */
@Data
public class PortfolioStatisticsResp implements Serializable {

	private static final long serialVersionUID = 1728194544136092395L;

	private String oid;
	// 净值基准日
	private Date baseDate;
	// 基准份额
	private BigDecimal shares;
	// 单位净值
	private BigDecimal nav;
	// 总资产净值
	private BigDecimal netValue;
	// 所有者权益
	private BigDecimal equity;
	// 未分配收益
	private BigDecimal payableIncome;
	// 应收投资收益
	private BigDecimal receivableIncome;

	// [估值]总资产估值
	private BigDecimal dimensions;
	// [估值]总资产净值
	private BigDecimal estimate;
	// [估值]最新估值日
	private Date dimensionsDate;
	// [估值]账户现金
	private BigDecimal cashPosition;
	// [估值]现金类资产总规模
	private BigDecimal liquidDimensions;
	// [估值]非现金类资产总规模
	private BigDecimal illiquidDimensions;
	// [估值]偏离损益
	private BigDecimal deviationValue;
	// SPV累计提取费金
	private BigDecimal drawedChargefee;
	// SPV累计计提费金
	private BigDecimal countintChargefee;
	//关联产品  类型
	private String productType;

}
