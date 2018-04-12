package com.guohuai.mmp.jiajiacai.wishplan.plan.form;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;

import com.guohuai.basic.component.ext.web.BaseResp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthsSatisticsForm extends BaseResp{
	
    private String totalDepositAmount;
	
//	private BigDecimal monthAmount;
	/**The plan month count*/
	private int planMonthCount;
	/**The actual invest count */
	private int actualInvestCount;	
	private Timestamp endTime;
	/** The pay day on month */ 
//	private Timestamp updateTime;
	private Timestamp createTime;
	private String status;
	
	private String expectedAmount;
	
	private List<OneYearForm> yearList;
	private List<OneMonthAmountForm> amountList;
	
	private Timestamp firstInvestTime;
	private int monthInvestDate;
	
	private BigDecimal updatedAmount;
	
	private int transferBalance;
}
