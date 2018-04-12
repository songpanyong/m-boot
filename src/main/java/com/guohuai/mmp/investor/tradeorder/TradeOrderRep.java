package com.guohuai.mmp.investor.tradeorder;

import java.sql.Timestamp;

import com.guohuai.basic.component.ext.web.BaseResp;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@lombok.Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class TradeOrderRep extends BaseResp {
	
	private String tradeOrderOid;
	
	private String expectedAmount;
	
	private Timestamp startTime;
	
	private Timestamp endTime;
	
}
