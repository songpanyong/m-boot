package com.guohuai.ams.portfolio.chargeFee;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

import lombok.Data;

/**
 * 费金要素
 * @author star.zhu
 * 2016年12月26日
 */
@Data
public class ChargeFeeForm implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String oid;
	
	// 投资组合OID
	private String portfolioOid;
	// 投资组合名称
	private String portfolioName;
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
	
	private String creater;
	private Timestamp createTime;
	private String operator;
	private Timestamp updateTime;
}
