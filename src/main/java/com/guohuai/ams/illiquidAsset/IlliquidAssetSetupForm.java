package com.guohuai.ams.illiquidAsset;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class IlliquidAssetSetupForm {

	private String oid;
	private Date restEndDate;
	private Date restStartDate;
	private Date setDate;
	
	private List<IlliquidAssetSetupForm.RepaymentPlan> plans;
	
	
	
	@Data
	public static class RepaymentPlan {
		
		private int issue;
		private int intervalDays;
		private Date dueDate;
		private Date startDate;
		private Date endDate;
		private BigDecimal principal;
		private BigDecimal interest;
		private BigDecimal repayment;
	}

}
