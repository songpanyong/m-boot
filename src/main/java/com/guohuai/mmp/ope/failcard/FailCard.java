package com.guohuai.mmp.ope.failcard;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.guohuai.component.persist.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 绑卡失败表
 */
@Entity
@Table(name = "T_OPE_FAILCARD")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@DynamicInsert
@DynamicUpdate
public class FailCard extends UUID implements Serializable {

	private static final long serialVersionUID = -8330772878190313121L;

	public static final String FAILCARD_COMMON_IS = "is"; // 是
	public static final String FAILCARD_COMMON_NO = "no"; // 否
	
	/** 所属用户 */
	private String userOid;
	/** 姓名 */
	private String name;
	/** 手机 */
	private String phone;
	/** 注册渠道 */
	private String source;
	/** 绑卡时间 */
	private Timestamp bindTime;
	/** 系统原因 */
	private String systemReason;
	/** 最终反馈 */
	private String lastFeedback;
	/** 是否反馈 */
	private String isFeedback;
	/** 是否已绑卡成功 */
	private String isBind;
	/** 首次绑卡成功时间 */
	private Timestamp bindSuccessTime;
	/** 操作人 */
	private String operator;
	
	private Timestamp createTime;
	private Timestamp updateTime;
}
