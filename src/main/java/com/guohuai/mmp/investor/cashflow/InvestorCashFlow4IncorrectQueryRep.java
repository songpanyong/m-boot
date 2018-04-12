package com.guohuai.mmp.investor.cashflow;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
@lombok.Builder
@lombok.Data
@NoArgsConstructor
@AllArgsConstructor
public class InvestorCashFlow4IncorrectQueryRep {

	/** 投资账号 */
	String phone;
	
	/** 交易方向 */
	String direction;
	
	/** 交易类型 */
	String orderType;
	
	/** 订单时间 */
	Timestamp orderTime;
	
	/** 订单金额 */
	BigDecimal orderAmt;
	
	/** 记账来源 */
	String doCheckType;

}
