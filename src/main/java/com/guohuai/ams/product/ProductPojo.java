package com.guohuai.ams.product;

import java.math.BigDecimal;
import java.util.List;

import com.guohuai.ams.label.LabelRep;

import lombok.Data;

@Data
public class ProductPojo {

	public static final String ProductPojo_showType_double = "double";
	public static final String ProductPojo_showType_single = "single";
	
	/**
	 * 产品OID
	 */
	private String productOid;
	/**
	 * 产品名称
	 */
	private String name;
	
	/**
	 * 产品类型
	 */
	private String type;
	/**
	 * 起投金额 
	 */
	private BigDecimal investMin;
	/**
	 * 预期收益起始
	 */
	private BigDecimal expAror;
	/**
	 * 预期收益结束
	 */
	private BigDecimal expArorSec;
	
	/**
	 * 收益显示
	 */
	private String expArrorDisp;
	/**
	 * 平台 奖励收益
	 */
	private BigDecimal rewardInterest;
	/**
	 * 存续期天数
	 */
	private Integer durationPeriodDays;
	
	/**
	 * 可售规模
	 */
	private BigDecimal maxSaleVolume;
	
	/**
	 * 募集规模
	 */
	private BigDecimal raisedTotalNumber;
	/**
	 * 已募份额
	 */
	private BigDecimal collectedVolume;
	/**
	 * 锁定已募份额
	 */
	private BigDecimal lockCollectedVolume;
	/**
	 * 状态排序号
	 */
	private String stateOrder;
	/**
	 * 状态
	 */
	private String state;
	/**
	 * 状态显示
	 */
	private String stateDisp;
	/**
	 * 单个收益率还是两个
	 */
	private String showType;
	
	private List<LabelRep> labelList;
	
	private BigDecimal tenThousandIncome;
	
	/**
	 * 投资次数
	 */
	private Integer purchaseNum;
	private String rewardYieldRange;//奖励收益率区间
	/**
	 * 最大阶梯收益
	 */
	private BigDecimal maxLevelRatio;
	/**
	 * 收益计算基础
	 */
	private String incomeCalcBasis;
	
	/** 收益方式  */
	private String incomeDealType;
	
	/** 收益方式描述 */
	private String incomeDealTypeDesc;
	
	/** 当前时间是否可购买 */
	private Boolean isInvest;
	
	/** 购买时间--起始时间 */
	private String dealStartTime;
	
	/** 交易结束时间 */
	private String dealEndTime;
	
	/** 权重 */
	private Integer weightValue;

}
