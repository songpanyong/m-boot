package com.guohuai.mmp.platform.baseaccount.statistics;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurvePojo {
	private Date date;
	
	private Long number;
}
