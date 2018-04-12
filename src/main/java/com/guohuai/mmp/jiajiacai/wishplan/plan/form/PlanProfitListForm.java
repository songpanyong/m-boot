package com.guohuai.mmp.jiajiacai.wishplan.plan.form;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanProfitListForm {
	
		
		/** 用户持有的累计投资总额 */
	    private String totalDepositAmount;
	    
	    /** 用户持有的累计总本息 */
		private String totalExpectedAmount;	
		
		/** 用户心愿计划的投资总额 */
		private String totalholdInvestAmount;
		
		
		/** 用户心愿计划总投资金额和本息 */
		private String totalholdAmountIncome;
		
		private List<PlanProfitForm> profitList;
		
		protected int errorCode;
		protected String errorMessage;
}
