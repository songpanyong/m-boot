package com.guohuai.mmp.ope.time;

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
 * 运营查询时间表
 */
@Entity
@Table(name = "T_OPE_SELECTTIME")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@DynamicInsert
@DynamicUpdate
public class OpeTime extends UUID implements Serializable {
	
	private static final long serialVersionUID = -7539401579065831064L;

	public static final String OPETIME_NAME_NOCARDTIME = "nocardtime"; // 未绑卡
	public static final String OPETIME_NAME_BINDTIME = "bindtime"; // 绑卡
	public static final String OPETIME_NAME_FAILRECHARGETIME = "failrechargetime"; // 购买失败
	public static final String OPETIME_NAME_NOBUYTIME = "nobuytime"; // 未购买
	
	public static final String OPETIME_NAME_TOTALLOAN = "totalloantime"; // 平台统计-累计借款总额
	public static final String OPETIME_NAME_TOTALRETURN = "totalreturntime"; // 平台统计-累计还款总额
	public static final String OPETIME_NAME_TOTALINTEREST = "totalinteresttime"; // 平台统计-累计付息总额
	public static final String OPETIME_NAME_PUBLISHERDEPOSIT = "publisherdeposittime"; // 平台统计-发行人充值总额
	public static final String OPETIME_NAME_PUBLISHERWITHDRAW = "publisherwithdrawtime"; // 平台统计-发行人提现总额
	public static final String OPETIME_NAME_INVESTORDEPOSIT = "investordeposittime"; // 平台统计-投资人充值总额
	public static final String OPETIME_NAME_INVESTORWITHDRAW = "investorwithdrawtime"; // 平台统计-投资人提现总额
	public static final String OPETIME_NAME_COUPON = "coupontime"; // 平台统计-卡券
	

	private String name;
	private Long time;
	
	private Timestamp createTime;
	private Timestamp updateTime;
}
