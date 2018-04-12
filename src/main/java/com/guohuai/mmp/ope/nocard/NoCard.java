package com.guohuai.mmp.ope.nocard;

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
 * 新注册未绑卡表
 */
@Entity
@Table(name = "T_OPE_NOCARD")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@DynamicInsert
@DynamicUpdate
public class NoCard extends UUID implements Serializable {
	
	private static final long serialVersionUID = -5794992759519180709L;
	
	public static final String NOCARD_COMMON_IS = "is"; // 是
	public static final String NOCARD_COMMON_NO = "no"; // 否
	
	/** 所属用户 */
	private String userOid;
	/** 姓名 */
	private String name;
	/** 手机 */
	private String phone;
	/** 注册渠道 */
	private String source;
	/** 注册时间 */
	private Timestamp registerTime;
	/** 最终反馈 */
	private String lastFeedback;
	/** 是否反馈 */
	private String isFeedback;
	/** 是否已绑卡 */
	private String isBind;
	/** 首次绑卡成功时间 */
	private Timestamp bindSuccessTime;
	/** 操作人 */
	private String operator;
	
	private Timestamp createTime;
	private Timestamp updateTime;
}
