package com.guohuai.mmp.jiajiacai.wishplan.plan.rep;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.guohuai.basic.component.ext.web.BaseResp;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@lombok.Builder
@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PlanByOidRep extends BaseResp {
	private String planOid; //心愿计划ID
	private String planName; //心愿计划名称
	private String planTarget; //心愿目标
	private String investType; //投资方式
	private BigDecimal totalAmount; //投资金额/累计投入
	private String principalAndInterest; //本息合计金额
	private int investDuration; //投资期限
	private int completedDays; //已完成天数
	private Timestamp addTime; //加入时间
	private Timestamp finishTime; //完成时间
	private String planStatus; //计划状态
	private int totalDays; //计划总天数
	private String orderCode;//订单号
}
