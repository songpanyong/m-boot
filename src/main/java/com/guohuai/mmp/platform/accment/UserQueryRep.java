package com.guohuai.mmp.platform.accment;


import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@lombok.Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class UserQueryRep {
	
	
	/**
	 * 用户查询
	 * 
oid		String	单条记录标识
userType		String	用户类型, 投资人账户:T1、发行人账户:T2、平台账户:T3
systemUid		String	用户业务系统id
userOid		String	用户ID
systemSource		String	来源系统类型
createTime		String	创建时间
	 */
	
	private String oid;
	
	private String userType;
	
	private String investorOid;
	
	private String memberId;
	
	private String systemSource;
	
	private String createTime;
}
