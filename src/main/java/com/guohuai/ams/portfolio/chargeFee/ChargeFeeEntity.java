package com.guohuai.ams.portfolio.chargeFee;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * 费金要素
 * @author star.zhu
 * 2016年12月26日
 */
@Data
@Entity
@Table(name = "T_GAM_CHARGEFEE")
public class ChargeFeeEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	private String oid;
	
	// 投资组合OID
	private String portfolioOid;
	// 投资组合名称
	private String portfolioName;
	// 分类
	private String classify;
	// 更新日期
	private Date updateDate;
	// 新增费金
	private BigDecimal addFee;
	// 新增托管费
	private BigDecimal addTrusteeFee;
	// 新增管理费
	private BigDecimal addManageFee;
	// 累计托管费
	private BigDecimal trusteeFee;
	// 累计管理费
	private BigDecimal manageFee;
	// 累计计提费金
	private BigDecimal chargeFee;
	// 计提时间
	private Timestamp askDate;
	// 计提人
	private String asker;
	// 计提费金类型
	private String feeType;
	// 计提托管费
	private BigDecimal getTrusteeFee;
	// 计提管理费
	private BigDecimal getManageFee;
	// 计提费金
	private BigDecimal getChargeFee;
	// 摘要
	private String digest;
	// 状态
	private String state;
	// 审核人
	private String auditor;
	// 审核状态
	private String auditState;
	// 审核时间
	private Timestamp auditTime;
	// 审核意见
	private String auditMark;
	
	private String creater;
	private Timestamp createTime;
	private String operator;
	private Timestamp updateTime;
}
