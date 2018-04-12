package com.guohuai.ams.portfolio.form;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

import lombok.Data;

/**
 * 投资组合收益分配要素
 * @author star.zhu
 * 2016年12月26日
 */
@Data
public class IncomeForm implements Serializable {

	private static final long serialVersionUID = 1L;

	private String oid;
	
	// 收益分配类型
	private String type;
	// 分配收益
	private BigDecimal income;
	// 收益分配日
	private Date incomeDate;
	// 基础收益
	private BigDecimal baseIncome;
	// 奖励收益
	private BigDecimal rewardIncome;
	// 实际发放基础收益
	private BigDecimal factBaseIncome;
	// 实际发放奖励收益
	private BigDecimal factRewardIncome;
	// 收益率
	private BigDecimal yield;
	// 状态
	private String state;
	
	private String creater;
	private Timestamp createTime;
	private String operator;
	private Timestamp operatTime;
}
