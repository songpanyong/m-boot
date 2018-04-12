package com.guohuai.ams.portfolio.form;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

import lombok.Data;

/**
 * 投资组合校准记录要素
 * @author star.zhu
 * 2016年12月26日
 */
@Data
public class AdjustForm implements Serializable {

	private static final long serialVersionUID = 1L;

	private String oid;

	// 校准日期
	private Date adjustDate;
	// 校准类型
	private String type;
	// 校准金额
	private BigDecimal amount;
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
	// 审核时间
	private Timestamp auditTime;
	// 审核意见
	private String auditMark;
}
