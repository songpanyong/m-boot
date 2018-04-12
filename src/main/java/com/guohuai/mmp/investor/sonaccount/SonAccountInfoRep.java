package com.guohuai.mmp.investor.sonaccount;

import java.math.BigDecimal;

import com.guohuai.basic.component.ext.web.BaseResp;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@lombok.Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class SonAccountInfoRep extends BaseResp {

	String investorOid;
	String userAcc;
	boolean userPwd;
	boolean paypwd;
	String sceneid;
	String status;
	String source;
	String channelid;
	String createTime;
	boolean islogin;
	String  isAdult;
	BigDecimal applyAvailableBalance;
	
	String name;
	String fullName;
	String idNumb;
	String fullIdNumb;
	String bankName;
	String bankCardNum;
	String fullBankCardNum;
	String bankPhone;
	String markId;
	BigDecimal balance;
	Boolean haveSonAccount;
	String isMaster;
	String riskLevel;
	String nickName;
	/**  主子账户的关系   */
	String relation;
}
