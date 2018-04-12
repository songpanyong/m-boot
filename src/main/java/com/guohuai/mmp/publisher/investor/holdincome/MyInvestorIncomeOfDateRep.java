package com.guohuai.mmp.publisher.investor.holdincome;

import java.math.BigDecimal;
import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/** 我的某日收益 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class MyInvestorIncomeOfDateRep {

	/** 日期 */
	private Date date;

	/** 活期收益 */
	private BigDecimal t0Income = BigDecimal.ZERO;

	/** 定期收益 */
	private BigDecimal tnIncome = BigDecimal.ZERO;
	
	/** 计划收益 */
	private BigDecimal wishplanIncome = BigDecimal.ZERO;

}
