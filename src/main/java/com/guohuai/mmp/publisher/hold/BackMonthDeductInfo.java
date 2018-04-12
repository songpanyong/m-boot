package com.guohuai.mmp.publisher.hold;

import java.math.BigDecimal;
import java.sql.Timestamp;

@lombok.Data
public class BackMonthDeductInfo {


	//划扣的月份
	private String deductMonth;
	//设置的转入时间
	private String monthInvestTime;
	//实际转入时间
	private String realInvestTime;
	//实际划扣金额(从银行卡)
	private String realInvestAmountfromCard;
	//实际从余额金额
	private String realInvestAmountfromBalance;
	//实际划扣次数
	private String realDeductCount;
	//划扣状态描述
	private String deductStatusDesc;
	//备注说明
	private String investDesc;
	//是否是从银行卡划扣
	private Boolean isCardDeduct;
	
}
