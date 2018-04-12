package com.guohuai.mmp.ope.nobuy;

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
 * 充值未购买列表
 */
@Entity
@Table(name = "T_OPE_NOBUY")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@DynamicInsert
@DynamicUpdate
public class NoBuy extends UUID implements Serializable {
	
	private static final long serialVersionUID = -5794992759519180709L;
	
	public static final String NOBUY_COMMON_IS = "is"; // 是
	public static final String NOBUY_COMMON_NO = "no"; // 否
	
	/** 所属用户 */
	private String userOid;
	/** 姓名 */
	private String name;
	/** 手机 */
	private String phone;
	/** 注册渠道 */
	private String source;
	/** 首充成功时间 */
	private Timestamp rechargeTime;
	/** 最终反馈 */
	private String lastFeedback;
	/** 是否反馈 */
	private String isFeedback;
	/** 是否已绑卡 */
	private String isBuy;
	/** 首次购买时间 */
	private Timestamp buyTime;
	/** 操作人 */
	private String operator;
	
	private Timestamp createTime;
	private Timestamp updateTime;
}
