package com.guohuai.mmp.investor.sonaccount;

import com.guohuai.basic.component.ext.web.BaseResp;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@lombok.Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class SonBindReq extends BaseResp{
	
	/**   子账户的id   */
	private String sMemberId;
	
	/**  昵称 (手机号)  */
	private String nickname;
	
	/**   关系                */
	private String relation;
}
