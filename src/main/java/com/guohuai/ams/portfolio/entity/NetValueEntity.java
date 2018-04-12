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
 * 投资组合净值明细
 * @author star.zhang
 * 2017年02月10日
 */
@Data
@Entity
@Table(name = "T_GAM_PORTFOLIO_NETVALUE")
public class NetValueEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 净值校准状态
	 */
	public static final String PORTFOLIO_NETVALUE_STATE_create = "CREATE";//新建
	public static final String PORTFOLIO_NETVALUE_STATE_pretrial = "PRETRIAL";//审核中
	public static final String PORTFOLIO_NETVALUE_STATE_duration = "DURATION";//审核通过
	public static final String PORTFOLIO_NETVALUE_STATE_reject = "REJECT";//驳回
	
	@Id
	private String oid;
	
	// 关联投资组合
	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "portfolioOid", referencedColumnName = "oid")
	//投资者组合id
	private PortfolioEntity portfolio;
	// 净值校准日
	private Date netValueDate;
	// 单位净值
	private BigDecimal unitNet;
	// 持有份额
	private BigDecimal holdShare;
	//当前估值
	private BigDecimal valuations;
	// 当前净值
	private BigDecimal nowValue;
	//昨日净值
	private BigDecimal yestValue;
	// 净值增长率
	private BigDecimal netYield;
	// 审核人
	private String auditor;
	// 审核状态
	private String auditState;
	// 审核意见
	private String auditMark;
	// 审核时间
	private Timestamp auditTime;
	
	private String creater;
	
	private Timestamp createTime;
}
