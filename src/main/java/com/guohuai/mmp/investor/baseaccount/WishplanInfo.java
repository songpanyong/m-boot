package com.guohuai.mmp.investor.baseaccount;

import java.math.BigDecimal;
import java.math.BigInteger;

@lombok.Data
public class WishplanInfo {

	//心愿计划的累计投资次数
	private Integer planTotalInvestCount;
	
	//心愿计划的当日投资次数
	private Integer planTodayInvestCount;
	
	//心愿计划的累计赎回次数
	private Integer planTotalRedeemCount;
	
	//心愿计划的当日赎回次数
	private Integer planTodayRedeemCount;
	
	//心愿计划的当日投资总额
	private BigDecimal planTodayInvestAmount;
	
	//心愿计划的当日赎回总额
	private BigDecimal planTodayRedeemAmount;
	
	//心愿计划累计赎回总额
	private BigDecimal planTotalRedeemAmount;
	
	//心愿计划累计还本总额
	private BigDecimal planTotalRepayLoan;
}
