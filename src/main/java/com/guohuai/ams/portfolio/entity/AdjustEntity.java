package com.guohuai.ams.portfolio.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;

/**
 * 投资组合校准记录要素
 * @author star.zhu
 * 2016年12月26日
 */
@Data
@Entity
@Table(name = "T_GAM_PORTFOLIO_ADJUST")
public class AdjustEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private String oid;
	
	// 关联投资组合
	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "portfolioOid", referencedColumnName = "oid")
	private PortfolioEntity portfolio;

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
	// 申请时间
	private Timestamp askTime;
	// 审核人
	private String auditor;
	// 审核时间
	private Timestamp auditTime;
	// 审核状态
	private String auditState;
	// 审核意见
	private String auditMark;
}
