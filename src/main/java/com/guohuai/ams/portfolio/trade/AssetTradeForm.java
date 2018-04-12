package com.guohuai.ams.portfolio.trade;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.Data;

/**
 * 投资组合交易资产要素
 * @author star.zhu
 * 2016年12月28日
 */
@Data
public class AssetTradeForm implements Serializable {

	private static final long serialVersionUID = 1L;

	private String oid;
	
	// 关联投资组合
	private String portfolioOid;
	private String portfolioName;

	// 关联标的OID
	private String assetOid;
	// 关联标的名称
	private String assetName;
	// 关联标的类型
	private String assetType;
	// 交易资产类型
	private String classify;
	
	// 交易时间
	private Timestamp tradeTime;
	// 交易份额
	private BigDecimal tradeVolume;
	// 交易金额
	private BigDecimal tradeCapital;
	// 交易单价
	private BigDecimal tradePrice;
	// 交易类型
	private String tradeType;
	// 投资损益
	private BigDecimal investmentIncome;
	// 状态
	private String state;
	// 申请人
	private String asker;
	// 申请份额
	private BigDecimal askVolume;
	// 申请金额
	private BigDecimal askCapital;
	// 申请时间
	private Timestamp askTime;
	// 审核人
	private String auditor;
	// 审核份额
	private BigDecimal auditVolume;
	// 审核金额
	private BigDecimal auditCapital;
	// 审核意见
	private String auditMark;
	// 审核时间
	private Timestamp auditTime;
	// 确认人
	private String confirmer;
	// 确认份额
	private BigDecimal confirmVolume;
	// 确认金额
	private BigDecimal confirmCapital;
	// 确认时间
	private Timestamp confirmTime;
}
