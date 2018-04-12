package com.guohuai.ams.switchcraft.black;

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
@Table(name = "T_ACCT_SWITCH_BLACK")
@lombok.Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
public class SwitchBlackEntity extends UUID implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/** 所属开关 */
	private String switchOid;
	
	/** 用户Oid */
	private String userOid;
	
	/** 用户手机账号 */
	private String userAcc;
	
	/** 操作人 */
	private String operator;
	
	/** 备注 */
	private String note;
	
	private Timestamp updateTime;
	
	private Timestamp createTime;
	
}
