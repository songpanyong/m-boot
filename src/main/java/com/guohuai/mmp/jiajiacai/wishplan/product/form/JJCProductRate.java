package com.guohuai.mmp.jiajiacai.wishplan.product.form;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JJCProductRate {
	
	private String oid;
	private String name;
	private float rate;
	private BigDecimal productMoneyValume;
	
}
