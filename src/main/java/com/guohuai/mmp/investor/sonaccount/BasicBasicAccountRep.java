package com.guohuai.mmp.investor.sonaccount;

import java.math.BigDecimal;

import com.guohuai.basic.component.ext.web.BaseResp;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BasicBasicAccountRep {
	private String phoneNum;
	private String id;
	
	/**
	 * 主账户可用余额
	 */
	private BigDecimal applyAvailableBalance;
	

	/**
	 * 主账户余额
	 */
	private BigDecimal Balance;
	
	
	/**
	 * 主账户资产总额
	 */
	private BigDecimal CapitalAmount = BigDecimal.ZERO;
	
	/** 主账户累计收益总额 */
	private BigDecimal TotalIncomeAmount = BigDecimal.ZERO;

	
	/**
	 * 主、子账户家庭成员总资产
	 */
	private BigDecimal sumCapitalAmount = BigDecimal.ZERO;
	
	/**
	 * 主、子账户家庭成员总收益
	 * */
	private BigDecimal sumTotalIncomeAmount = BigDecimal.ZERO;
	/** 判断是否是当前账号在登录  */
	Boolean isLogin = false;
}
