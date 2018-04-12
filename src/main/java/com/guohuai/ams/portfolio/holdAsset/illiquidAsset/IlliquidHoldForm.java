package com.guohuai.ams.portfolio.holdAsset.illiquidAsset;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

import javax.persistence.Id;

import lombok.Data;

/**
 * 投资组合持仓非现金类资产
 * @author star.zhu
 * 2016年12月28日
 */
@Data
public class IlliquidHoldForm implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private String oid;
	
	// 关联标的
	private String illiquidOid;
	private String illiquidName;
	
	// 关联投资组合
	private String portfolioOid;
	private String portfolioName;
	
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
