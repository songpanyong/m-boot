package com.guohuai.mmp.jiajiacai.wishplan.plan.form;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanDepositForm {
	  
    private String depositStatus;

	private BigDecimal depositAmount;
		
	private Timestamp depositTime;
    
}
