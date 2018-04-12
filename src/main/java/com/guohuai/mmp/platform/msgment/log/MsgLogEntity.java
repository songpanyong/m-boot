package com.guohuai.mmp.platform.msgment.log;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.guohuai.component.persist.UUID;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 
 * 消息通知日志
 *
 */
@Entity
@Table(name = "T_MONEY_MSG_LOG")
@lombok.Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
public class MsgLogEntity extends UUID {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1125580834556322202L;

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
	private String objectOid;
}
