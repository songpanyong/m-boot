package com.guohuai.ams.liquidAsset.yield;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import lombok.Data;

@Data
public class LiquidAssetYieldForm {
	/**
	 * 标的oid
	 */
	@NotNull
	@NotEmpty
	private String oid;
	
	private List<Profit> profits = new ArrayList<Profit>();
	
	@Data
	public static class Profit {
		@NotNull
		@NotEmpty
		private BigDecimal dailyProfit;
		@NotNull
		@NotEmpty
		private BigDecimal weeklyYield;
		@NotNull
		@NotEmpty
		private Date profitDate;
	}
	
}
