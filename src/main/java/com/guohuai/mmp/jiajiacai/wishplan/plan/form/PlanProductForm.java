package com.guohuai.mmp.jiajiacai.wishplan.plan.form;
import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.validation.constraints.NotNull;

import com.guohuai.mmp.investor.tradeorder.TradeOrderReq;

import lombok.NoArgsConstructor;

@NoArgsConstructor
@lombok.Data
public class PlanProductForm extends TradeOrderReq{	
	
	@NotNull
	private int investDuration;
//	@NotNull
//	private int tourDuration;
//	@NotNull
//	private int numberPeople;
//	@NotNull
//	private String hotelLevel;
//	@NotNull
//	private String destination;
//	@NotNull
    private float expectedRate;
    
    private BigDecimal expectedAmount;
     
    @NotNull
	private String planListOid;
	
	private Timestamp endTime;
	
	private String planOid;
	private String planType;
	private String monthOid;
	//The plan target
	private String planTarget;
}
