package com.guohuai.mmp.jiajiacai.wishplan.question;

import java.math.BigDecimal;

import com.guohuai.basic.component.ext.web.BaseResp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvestMessageForm extends BaseResp {
	/** 计划OID */
	private String planOid;
	/**投资期限 */
	private int duration; 
	/**投资额*/
	private int capital;
	/**到期总收益*/
	private String profit;
	/**按月定投 一次性购买*/
	private String type;
	/** 产品ID */
	private String productOid;
	/** 产品Name */
	private String productName;
	
	private double rate;
	
	private BigDecimal productMoneyValume;
	
	private String planRedeemOid;
}
