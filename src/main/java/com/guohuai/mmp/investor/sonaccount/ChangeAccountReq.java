package com.guohuai.mmp.investor.sonaccount;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@lombok.Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class ChangeAccountReq {
	
	/**   要切换到子账户 的id  */
	private String userId;
	/**   登录平台              */
	private String platform;
}
