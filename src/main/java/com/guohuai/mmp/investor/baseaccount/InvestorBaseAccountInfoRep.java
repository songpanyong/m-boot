package com.guohuai.mmp.investor.baseaccount;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.guohuai.basic.component.ext.web.BaseResp;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
@lombok.Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class InvestorBaseAccountInfoRep extends BaseResp {

	private String phoneNum;
	
	private String realName;
	
	private String status;
	
	private BigDecimal balance;	

	private Timestamp createTime;
	//首次投资时间
	private Timestamp firstInvestTime; 
	
}
