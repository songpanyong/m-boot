package com.guohuai.mmp.investor.sonaccount;

import java.math.BigDecimal;

@lombok.Data
public class AccountAmountSort {

	private String oid;
	private String nickname;
	
	
	private BigDecimal  CapitalAmount = BigDecimal.ZERO;
	
	private Boolean isSonAccount;
	
	
}
