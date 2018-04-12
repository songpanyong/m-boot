package com.guohuai.mmp.investor.baseaccount.statistics;

import java.math.BigDecimal;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@lombok.Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class CapitalDetail {
	
	private String productOid;
	private BigDecimal amount;
	private String productName;

}