package com.guohuai.mmp.platform.accment;


import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@lombok.Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class UserQueryIRequest {
	
	
	/**
	 * 用户查询
	 * systemUid		N	用户业务系统id
userType		N	用户类型, 投资人账户:T1、发行人账户:T2、平台账户:T3
systemSource		N	来源系统类型
	 */
	
	private String systemUid;
	
	private String userType;
	
	private String systemSource;
	
}
