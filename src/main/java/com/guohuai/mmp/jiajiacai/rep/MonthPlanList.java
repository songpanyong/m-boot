package com.guohuai.mmp.jiajiacai.rep;

import java.math.BigDecimal;

import java.sql.Timestamp;
import java.util.Date;

@lombok.Data
public class MonthPlanList {

		//购买的心愿计划的id
		private String oid;
		
		//心愿计划的名称
		private String planListName;
		
		//心愿计划的类型 
		private String planType;
		
		//加入计划的时间
		private Timestamp createTime;
		
		//累计投资金额
		private BigDecimal depositAmount;
		
		//投资期限
		private String investDuration;
		
		//月定投金额
		private BigDecimal monthInvset;
		
		//每月几号进行转入
		private String transferDay;
		
		//首次转入时间
		private Date firstTransferTime;
		
		//模拟年化收益率
		private String expectRate;
		
		//预计到期收益
		private String expectedInterest ;
		
		//预计到期的本息和
		private String  expectAmount ;
		
		//昨日收益
		private BigDecimal holdYesterdayIncome;
				
		//实时累计收益
		private BigDecimal realInterest;
		//实时累计本息和
		private BigDecimal realAmount;
		//账户类型
		private String  accountType;

		//计划的状态
		private String status;
		//计划的状态描述
		private String statusDesc;
		//心愿目标
		private String planTarget;
}
