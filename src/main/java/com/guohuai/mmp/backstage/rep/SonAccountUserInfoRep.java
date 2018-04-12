package com.guohuai.mmp.backstage.rep;

import java.math.BigDecimal;
import java.sql.Timestamp;

@lombok.Data
public class SonAccountUserInfoRep {

	private String sid;
	
	private String  nickName;
	
	private String realName;
	
	private String pid;
	
	private String status;
	
	private String statusDesc;
	
	/**  主账户注册的手机号 */
	private String phoneNum ;
	
	/**子账户的可用余额 */
	private BigDecimal applyBalance = new BigDecimal(0);
	/** 子账户的累计投资*/
	private BigDecimal totalInvestAmount =new BigDecimal(0);
	/** 子账户的累计收益 */
	private BigDecimal totalIncomeAmount  = new BigDecimal(0);
	/** 子账户的创建时间 */
	private Timestamp createTime;
	/** 子账户的余额  */
	private BigDecimal balance;
	
	private int relationStatus;
}
