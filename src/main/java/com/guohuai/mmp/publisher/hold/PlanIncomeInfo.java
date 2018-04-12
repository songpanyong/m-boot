package com.guohuai.mmp.publisher.hold;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

@lombok.Data
public class PlanIncomeInfo {

	/** 计划名称 */
	private String planName;
	
	/** 计划类型*/
	private String planType;
	
	/** 心愿计划批次 */
	private String planBatch;
	
	/** 投资时间 */
	private Timestamp investTime;
	
	/** 收益金额 */
	private String income;
	/** 累计投资金额 */
	private BigDecimal totalInvestAmount;
	
	/** 收益确认日 */
	private String confirmDate;
	
	/**收益发放日期 */
	private String createTime ;
	
}
