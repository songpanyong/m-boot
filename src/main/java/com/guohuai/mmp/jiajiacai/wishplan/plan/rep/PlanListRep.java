package com.guohuai.mmp.jiajiacai.wishplan.plan.rep;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@lombok.Builder
@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PlanListRep {
	private String planOid; //心愿计划ID
	private String planName; //心愿计划名称
	private String planTarget; //心愿目标
	private String investType; //投资方式
	private BigDecimal totalAmount; //投资金额/累计投入
	private Timestamp addTime; //加入时间
	private String planStatus; //计划状态
}
