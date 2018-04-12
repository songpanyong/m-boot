package com.guohuai.mmp.platform.accountingnotify;

import java.math.BigDecimal;
import java.sql.Date;

import com.guohuai.basic.component.ext.web.BaseResp;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@lombok.Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class AccountingNotifyRep  extends BaseResp{

	private Date busDate;
	private String notifyType;
	private BigDecimal costFee;
	private String notifyTypeName;
	private String productOid;
	private String notifyStatus;
	
}
