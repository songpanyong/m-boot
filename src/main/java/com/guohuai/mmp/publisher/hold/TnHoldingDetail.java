package com.guohuai.mmp.publisher.hold;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.sql.Timestamp;

import com.guohuai.ams.label.LabelResp;
import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.file.FileResp;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/** 我的持有中定期产品详情 */
@lombok.Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class TnHoldingDetail extends BaseResp{
	
	/**
	 * 投资额
	 */
	private BigDecimal investVolume;
	
	/**
	 * 预计回款金额
	 */
	private BigDecimal payAmount;
	
	/** 
	 * 预计收益开始区间
	 */
	private BigDecimal expectIncome;
	
	/**
	 * 预计收益结束区间
	 */
	private BigDecimal expectIncomeExt;
	
	/** 
	 * 预期年化收益率 开始
	 */
	private String expAror;
	
	/** 
	 * 预期年化收益率 结束
	 */
	private String expArorExt;
	
	/**
	 * 募集开始日期
	 */
	private Date raiseStartDate;
	
	/**
	 * 募集结束日期
	 */
	private Date raiseEndDate;
	
	/**
	 * 存续期开始日期
	 */
	private Date setupDate;
	
	/**
	 * 存续期结束日期
	 */
	private Date durationPeriodEndDate;
	
	/** 
	 * 还本付息日期 
	 */
	private Date repayDate;
	
	/**  存续期   */
	private Integer durationPeriodDays ;
	
	
	/** 最近一次的购买时间 */
	private Timestamp latestOrderTime;
	
	/** 产品名称 */
	private String productName;
	
	private List<FileResp> files;// 附件
	private List<FileResp> investFiles;// 投资协议书
	private List<FileResp> serviceFiles;// 信息服务协议
	private String incomeCalcBasis;//收益计算基础

}
