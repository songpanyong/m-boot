package com.guohuai.mmp.investor.baseaccount.check;

import java.math.BigDecimal;

import com.guohuai.basic.component.ext.web.BaseResp;

import lombok.EqualsAndHashCode;
@lombok.Data
@EqualsAndHashCode(callSuper = false)
public class CheckSumAmtRep extends BaseResp {

	/** 资金总额 */
	BigDecimal allMoneyAmount;
	
	/** 资产总额 */
	BigDecimal allCapitalAmount;
}
