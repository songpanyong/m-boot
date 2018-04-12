package com.guohuai.mmp.investor.bank;

import com.guohuai.basic.component.ext.web.BaseResp;

import lombok.EqualsAndHashCode;
@lombok.Data
@EqualsAndHashCode(callSuper=true)
public class BankInfoRep extends BaseResp {

	/** 姓名 */
	String name;
	
	/** 身份证号 */
	String idNumb;
	
	/** 银行名称 */
	String bankName;
	
	/** 银行卡号	 */
	String cardNumb;
	
	/** 银行预留手机号 */
	String phoneNo;
	
	String createTime;
	
	boolean isbind;
}
