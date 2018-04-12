package com.guohuai.ams.switchcraft;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.guohuai.component.persist.UUID;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "T_ACCT_SWITCH")
@lombok.Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
public class SwitchEntity extends UUID implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/** 状态-待审核 */
	public static final String SWITCH_Status_toApprove = "toApprove";
	/** 状态-拒绝 */
	public static final String SWITCH_Status_refused = "refused";
	/** 状态-通过 */
	public static final String SWITCH_Status_pass = "pass";
	/** 状态-启用 */
	public static final String SWITCH_Status_enable = "enable";
	/** 状态-禁用 */
	public static final String SWITCH_Status_disable = "disable";
	
	/** 白名单状态-启用白名单 */
	public static final String SWITCH_WhiteStatus_white = "white";
	/** 白名单状态-启用黑名单 */
	public static final String SWITCH_WhiteStatus_black = "black";
	/** 白名单状态-禁用 */
	public static final String SWITCH_WhiteStatus_no = "no";
	
	/** 类型-开关 */
	public static final String SWITCH_type_switch = "switch";
	/** 类型-参数配置 */
	public static final String SWITCH_type_configure = "configure";
	/** 收益分配排期通知开关 */
	public static final String SWITCH_code_IncomeDistriNotice = "IncomeDistriNotice";
	
	/** 开关编码--平台余额对账手动执行 */
	public static final String SWITCH_code_DoPlatformCheck = "DoPlatformCheck";
	/** 开关编码--平台余额对账锁定用户 */
	public static final String SWITCH_code_PlatformCheckLock = "PlatformCheckLock";
	
	/** 编码 */
	private String code;
	
	/** 名称 */
	private String name;
	
	/** 类型 switch开关  configure配置 */
	private String type;
	
	/** 内容 */
	private String content;
	
	/** 状态 */
	private String status;
	
	/** 白名单状态 */
	private String whiteStatus;
	
	/** 申请人 */
	private String requester;
	
	/** 审核者 */
	private String approver;
	
	/** 审核意见 */
	private String approveRemark;	
	
	private Timestamp updateTime;
	
	private Timestamp createTime;
	
}
