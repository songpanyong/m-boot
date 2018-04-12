package com.guohuai.mmp.investor.sonaccount;

import com.guohuai.basic.component.ext.web.BaseResp;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@lombok.Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class ChangeToSonRep extends BaseResp {
	
	/**   子账户的昵称   */
	private String nickName;
	
	/**   主、子账号的标识   */
	private String markId;
}
