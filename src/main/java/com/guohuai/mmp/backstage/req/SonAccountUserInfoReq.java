package com.guohuai.mmp.backstage.req;

import java.sql.Timestamp;

@lombok.Data
public class SonAccountUserInfoReq {

	/** 子账户的id */
	private  String sid ;
	/**  主账户的注册手机号 */
	private  String phoneNum;
	/**主账户的id */
	private String pid;
	/**   子账户的状态   */
	private String status;
	/** 子账户的创建 时间  */
	private Timestamp createTime;
}
