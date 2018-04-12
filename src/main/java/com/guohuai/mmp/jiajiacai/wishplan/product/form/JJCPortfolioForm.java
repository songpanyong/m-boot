package com.guohuai.mmp.jiajiacai.wishplan.product.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 产品
 * @author Administrator
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JJCPortfolioForm {
	
	private String oid;
	private String name;
	private String liquidRate; 		//计划现金类资产占比
	private String illiquidRate; 	//计划非现金类资产占比
	private String cashRate;		//计划现金存款占比
	private String other; 			//其他
}
