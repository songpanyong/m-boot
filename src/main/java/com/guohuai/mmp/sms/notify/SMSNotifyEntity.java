package com.guohuai.mmp.sms.notify;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.guohuai.component.persist.UUID;
import com.guohuai.mmp.sys.SysConstant;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 短信通知
 * @author M1
 *
 */
@Entity
@Table(name = "T_MONEY_SMS_NOTIFY")
@lombok.Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
public class SMSNotifyEntity extends UUID implements Serializable {
	
	private static final long serialVersionUID = 2425452922159934702L;
	
	/** 通知状态--待确认 */
	public static final String NOTIFY_notifyStatus_toConfirm = "toConfirm";
	/** 通知状态--已确认 */
	public static final String NOTIFY_notifyStatus_confirmed = "confirmed";
	
	/**
	 * 短信发送类型
	 */
	private String smsSendTypes;
	
	/**
	 * 通知内容	
	 */
	private String notifyContent;
	
	/**
	 * 通知状态	
	 */
	private String notifyStatus;
	
	/**
	 * 错误代码
	 */
	private int errorCode = SysConstant.INTEGER_defaultValue;
	
	/**
	 * 错误消息
	 */
	private String errorMessage;
	
	/**
	 * 通知时间	
	 */
	private Timestamp notifyTime;
	
	/**
	 * 回复时间	
	 */
	private Timestamp notifyConfirmedTime;
	
	/**
	 * 通知次数	
	 */
	private int notifyTimes = SysConstant.INTEGER_defaultValue;
	
	/**
	 * 序列
	 */
	private long seqId;
	
	private Timestamp createTime;
	private Timestamp updateTime;
	
}
