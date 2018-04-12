package com.guohuai.mmp.jiajiacai.wishplan.plan.form;
import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Administrator
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyInvestForm {
	/** 投资者OID */
	private String uid;
	/** planLabelOid oid */
//	@NotNull
//	private String planListOid;
	/** Every portion on month */
	@NotNull
	private BigDecimal mothAmount;
	/**The first pay day */
	@NotNull
	private String startInvestDate;
	/**The pay day */
	@NotNull
	private int mothInvestDate; 
	/** The bank id*/
	private String investorBankOid;
	
	/** The plan month count lenth*/
	private int planMonthCount;
	private String planType;
	
	private BigDecimal expectedAmount;
	
	private BigDecimal expectedRate;
//	private int investDuration; 
	
	private String cid;
	
	private String ckey;
	
	private String planTarget;

}
