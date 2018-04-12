package com.guohuai.mmp.platform.accment;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@lombok.Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class UserQueryReq {
//	参数名	参数值	必须	描述
//	userType	投资人账户:T1、发行人账户:T2、平台账户:T3	Y	用户类型
//	systemUid		Y	用户业务系统id
//	systemSource		Y	来源系统类型
//	phone			手机号
	
	/**
	 * 会员ID
	 */
	private String systemUid;
	
	
	/**
	 * 用户类型
	 */
	private String userType;
	
	/**
	 * phone
	 */
	private String phone;
	
	
}
