package com.guohuai.cache.entity;

import java.math.BigDecimal;
import java.sql.Date;

import com.guohuai.ams.product.Product;

@lombok.Data
@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
@lombok.Builder
public class ProductCacheEntity {
	public static String[] zoomArr = new String[] { "netUnitShare", "investMin", "investAdditional", "investMax",
			"minRredeem", "maxRredeem", "additionalRredeem", "netMaxRredeemDay", "dailyNetMaxRredeem", "maxHold",
			"singleDailyMaxRedeem", "maxSaleVolume", "lockCollectedVolume", "collectedVolume",
			"currentVolume", "previousCurVolume" };
	/** 产品Oid */
	String productOid;
	/**
	 * 产品名称 
	 */
	String name;
	/** 开放申购期 */
	String isOpenPurchase;
	/** 开放赎回期 */
	String isOpenRemeed;
	/** 开市时间 */
	String dealStartTime;
	/** 闭市时间 */
	String dealEndTime;
	
	
	/** 单位份额净值 */
	BigDecimal netUnitShare;
	/** 单笔投资最低份额 */
	BigDecimal investMin;
	/** 单笔投资追加份额 */
	BigDecimal investAdditional;
	/** 单笔投资最高份额 */
	BigDecimal investMax;
	/** 单笔赎回最低下限 */
	BigDecimal minRredeem;
	/** 单笔赎回最高份额 */
	BigDecimal maxRredeem;
	/** 单笔赎回追加份额 */
	BigDecimal additionalRredeem;
	/** 单日净赎回上限 */
	BigDecimal netMaxRredeemDay;
	/** 剩余赎回金额 */
	BigDecimal dailyNetMaxRredeem;
	/** 单人持有上限 */
	BigDecimal maxHold;
	/** 单人单日赎回上限 */
	BigDecimal singleDailyMaxRedeem;
	/** 最高可售份额(申请的) */
	BigDecimal maxSaleVolume;
	/** 锁定已募集份额 */
	BigDecimal lockCollectedVolume;
	/** 当前份额(投资者持有份额) */
	BigDecimal currentVolume;
	
	
	
	/** 产品标签 */
	String productLabel;
	/** 产品状态 */
	String state;
	/** 产品类型 */
	String type;
	/** 是否屏蔽赎回确认 */
	String isOpenRedeemConfirm;
	/** 发行人Oid */
	String spvOid;
	
	/**
	 * 已募规模
	 */
	private BigDecimal collectedVolume;

	
	
	private Integer singleDayRedeemCount; // 单人单日赎回次数
	
	/**
	 * 募集开始日期
	 */
	private Date raiseStartDate;
	
	/**
	 * 募集结束日期
	 */
	private Date raiseEndDate;
	
	/**
	 * 产品成立日期(存续期开始日期)
	 */
	private Date setupDate;
	
	/**
	 * 存续期结束
	 */
	private Date durationPeriodEndDate;
	
	/**
	 * 还本付息日
	 */
	private Date repayDate;
	/**
	 *  募集宣告失败日期
	 */
	private Date raiseFailDate;
	
	/**
	 * 预期收益率
	 */
	private BigDecimal expAror;
	private BigDecimal expArorSec;
	/**
	 * 平台奖励收益
	 */
	private BigDecimal rewardInterest;
	
	
	/**
	 * 还本付息状态
	 */
	private String repayLoanStatus;
	
	/**
	 * 收益 计息基数
	 */
	private String incomeCalcBasis;
	
	/**
	 * 系统按每个工作日处理赎回申请，赎回金额不得超过金猪宝（上一个交易日）在投总金额的20%
	 */
	
	/**
	 * 上一个交易日产品当前规模(基于占比算)
	 */
	private BigDecimal previousCurVolume = BigDecimal.ZERO;
	
	/**
	 * 赎回占上一交易日规模百分比
	 */
	private BigDecimal previousCurVolumePercent = BigDecimal.ZERO;
	
	/**
	 * 赎回占比开关
	 */
	private String isPreviousCurVolume = Product.NO;
}
