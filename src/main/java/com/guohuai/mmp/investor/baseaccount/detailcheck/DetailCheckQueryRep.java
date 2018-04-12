package com.guohuai.mmp.investor.baseaccount.detailcheck;

import java.math.BigDecimal;

import lombok.EqualsAndHashCode;

@lombok.Data
@EqualsAndHashCode(callSuper = false)
@lombok.Builder
public class DetailCheckQueryRep {

	String oid, investOid, phone, checkTime, checkStatus;
	
	/** 资金账户余额 */
	BigDecimal balance = BigDecimal.ZERO;
	
	/** 资金变动明细重算金额 */
	BigDecimal recorrectBalance = BigDecimal.ZERO;

}
