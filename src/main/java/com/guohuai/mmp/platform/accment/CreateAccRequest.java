package com.guohuai.mmp.platform.accment;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@lombok.Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class CreateAccRequest {
/**
 * 创建子账户
 * 
 * userOid		Y	会员ID
userType		Y	用户类型,投资人账户:T1、发行人账户:T2、平台账户:T3
accountType		Y	账户类型,01为活期，02为活期利息，03为体验金，04为在途，05为冻结户（冻结户，非冻结状态），06定期户，07产品户，08备付金户，09 超级户，10基本户，11 运营户
 */
	
	/**
	 * 会员ID
	 */
	private String memberId;
	
	
	/**
	 * 用户类型
	 */
	private String userType;
	
	/**
	 * phone
	 */
	private String accountType;
	
	
}
