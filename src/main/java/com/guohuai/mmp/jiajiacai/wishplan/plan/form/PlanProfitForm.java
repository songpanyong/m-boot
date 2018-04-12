package com.guohuai.mmp.jiajiacai.wishplan.plan.form;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanProfitForm {
	
    private String planListName;
    
    private String planType;

	private String depositAmount;
	
	private String incomeAmount;
	
	private Timestamp createTime;
	
	private String planOid;
    
}
