package com.guohuai.mmp.ope.norecharge;

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
 * 未充值表
 */
@Entity
@Table(name = "T_OPE_NORECHARGE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@DynamicInsert
@DynamicUpdate
public class NoRecharge extends UUID implements Serializable {

	private static final long serialVersionUID = -8330772878190313122L;

	public static final String NORECHARGE_COMMON_IS = "is"; // 是
	public static final String NORECHARGE_COMMON_NO = "no"; // 否
	
	/** 所属用户 */
	private String userOid;
	/** 姓名 */
	private String name;
	/** 手机 */
	private String phone;
	/** 注册渠道 */
	private String source;
	/** 绑卡成功时间 */
	private Timestamp bindTime;
	/** 最终反馈 */
	private String lastFeedback;
	/** 是否反馈 */
	private String isFeedback;
	/** 是否已充值 */
	private String isCharge;
	/** 首次充值成功时间 */
	private Timestamp rechargeSuccessTime;
	/** 操作人 */
	private String operator;
	
	private Timestamp createTime;
	private Timestamp updateTime;
}
