package com.guohuai.mmp.backstage.rep;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.guohuai.basic.component.ext.web.BaseResp;

@lombok.Data
public class TransferMoneyRep {

	private String tradeOrder;
	private String pid;
	private String sid;
	private String pRealname;
	private String sRealname;
	private String  parentPhone;
	private String sonPhone;
	private BigDecimal transferAmount;
	private BigDecimal fee;
	private String type;
	private String  tradeStatus;
	private String tradeStatusDesc;
	private String failMessage;
	private Timestamp transferTime;
	
}
