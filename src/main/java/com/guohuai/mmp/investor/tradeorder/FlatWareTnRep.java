package com.guohuai.mmp.investor.tradeorder;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@lombok.Data
@lombok.Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlatWareTnRep {
	BigDecimal tHoldVolume = BigDecimal.ZERO;
	BigDecimal tIncomeAmount = BigDecimal.ZERO;
	
}
