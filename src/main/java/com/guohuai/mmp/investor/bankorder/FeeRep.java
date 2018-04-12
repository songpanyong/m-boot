package com.guohuai.mmp.investor.bankorder;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class FeeRep {
	private BigDecimal fee;
	private String payer;
}
