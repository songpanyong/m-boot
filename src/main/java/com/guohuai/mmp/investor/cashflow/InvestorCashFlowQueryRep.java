package com.guohuai.mmp.investor.cashflow;

import java.math.BigDecimal;
import java.sql.Timestamp;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@lombok.Builder
@lombok.Data
@NoArgsConstructor
@AllArgsConstructor
public class InvestorCashFlowQueryRep {

	
	/**
	 * 交易金额
	 */
	private BigDecimal tradeAmount;

	/**
	 * 交易类型
	 */
	private String tradeType;
	
	private String tradeTypeDisp;

	private Timestamp updateTime;

	private Timestamp createTime;

}
