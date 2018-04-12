package com.guohuai.mmp.investor.sonaccount;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class SonBasicAccountRep   {

	private String id;
	private String nickName;
	private String relation;
	/** 获得可用余额*/
	private BigDecimal  ApplyAvailableBalance;
	
	/**余额*/
	private BigDecimal Balance;
	/**
	 * 子账户资产总额
	 */
	private BigDecimal CapitalAmount = BigDecimal.ZERO;
	
	/** 子账户累计收益总额 */
	private BigDecimal TotalIncomeAmount = BigDecimal.ZERO;
	
	/** 判断是否是当前账号在登录  */
	Boolean isLogin = false;
	
	private Timestamp createTime;

	

	
	
}
