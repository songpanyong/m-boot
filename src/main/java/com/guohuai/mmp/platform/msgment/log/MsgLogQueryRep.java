package com.guohuai.mmp.platform.msgment.log;

import java.sql.Timestamp;


import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 */
@lombok.Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class MsgLogQueryRep {

	

	/** 接口名称 */
	private String interfaceName;

	/** 接口返回码 */
	private Integer errorCode;

	/** 接口错误消息 */
	private String errorMessage;

	/** 已发送次数 */
	private Integer sendedTimes;
	
	/**
	 * 最多发送次数
	 */
	private Integer limitSendTimes;
	
	/**
	 * 下次调用时间
	 */
	private Timestamp nextNotifyTime;

	/** 发送消息内容 */
	private String sendObj;

	private Timestamp createTime;

	private Timestamp updateTime;
	
	/**object oid, etc. wish plan oid*/
//	private String objectOid;
}
