package com.guohuai.mmp.jiajiacai.wishplan.plan.form;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OneYearForm {
	
	private int year;
	
    private ArrayList<String> statusList;
	
}
