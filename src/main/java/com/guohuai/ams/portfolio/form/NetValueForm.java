package com.guohuai.ams.portfolio.form;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

import lombok.Data;

/**
 * 投资组合净值明细要素
 * @author star.zhu
 * 2016年12月26日
 */
@Data
public class NetValueForm implements Serializable {

	private static final long serialVersionUID = 1L;

	private String oid;

	// 投资组合OID
	private String portfolioOid;
	// 投资组合名称
	private String portfolioName;
	// 上一个校准日
	private Date lastBaseDate;
	// 净申购
	private BigDecimal purchase;
	// 净赎回
	private BigDecimal redemption;
	// 净收益
	private BigDecimal profit;
	// 昨日净申赎
	private BigDecimal lastOrders;
	// 昨日份额
	private BigDecimal lastShares;
	// 昨日单位净值
	private BigDecimal lastNav;
	// 净值校准日
	private Date netValueDate;
	// 单位净值
	private BigDecimal net;
	// 持有份额
	private BigDecimal share;
	// 当前估值
	private BigDecimal valuations;
	// 当前净值
	private BigDecimal netValue;
	// 净值增长率
	private BigDecimal netYield;
	// 状态
	private String state;

	private String creater;
	private Timestamp createTime;
}
