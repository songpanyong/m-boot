package com.guohuai.mmp.jiajiacai.wishplan.product.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * 投资组合要素表
 * 
 * @author star.zhu 2016年12月26日
 */
@Data
@Entity
@Table(name = "T_GAM_PORTFOLIO")
public class JJCPortfolioEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 投资组合状态
	 */
	public static final String PORTFOLIO_STATE_create = "CREATE";
	public static final String PORTFOLIO_STATE_pretrial = "PRETRIAL";
	public static final String PORTFOLIO_STATE_duration = "DURATION";
	public static final String PORTFOLIO_STATE_reject = "REJECT";

	@Id
	private String oid;

	// 关联发行人
	private String spvOid;

	// 投资组合名称
	private String name;
	// 计划现金类资产占比
	private BigDecimal liquidRate;
	// 计划非现金类资产占比
	private BigDecimal illiquidRate;
	// 计划现金存款占比
	private BigDecimal cashRate;
	// 实际现金类资产占比
	private BigDecimal liquidFactRate;
	// 实际非现金类资产占比
	private BigDecimal illiquidFactRate;
	// 实际现金存款占比
	private BigDecimal cashFactRate;
	// 管理费率
	private BigDecimal manageRate=new BigDecimal(0);
	// 托管费率
	private BigDecimal trusteeRate=new BigDecimal(0);
	// 费用计算基础
	private int calcBasis;
	// 资管机构名称
	private String organization;
	// 资管计划名称
	private String planName;
	// 托管银行
	private String bank;
	// 托管银行账号
	private String account;
	// 联系人
	private String contact;
	// 联系电话
	private String telephone;

	// SPV持有的基子单位净值
	private BigDecimal nav;
	// SPV持有的基子份额
	private BigDecimal shares;
	// SPV持有的总资产净值
	private BigDecimal netValue;
	// SPV净值校准日期
	private Date baseDate;

	// [估值]投资组合总规模
	private BigDecimal dimensions;
	// [估值]账户现金
	private BigDecimal cashPosition;
	// [估值]现金类资产总规模
	private BigDecimal liquidDimensions;
	// [估值]非现金类资产总规模
	private BigDecimal illiquidDimensions;
	// [估值]偏离损益
	private BigDecimal deviationValue;
	// [估值]冻结现金
	private BigDecimal freezeCash;
	// [估值]最新估值日
	private Date dimensionsDate;

	// SPV累计提取费金
	private BigDecimal drawedChargefee;
	// SPV累计计提费金
	private BigDecimal countintChargefee;

	// 状态
	private String state;

	// 审核人
	private String auditor;
	// 审核时间
	private Timestamp auditTime;
	// 审核意见
	private String auditMark;

	private String creater;
	private Timestamp createTime;
	private String operator;
	private Timestamp updateTime;

}
