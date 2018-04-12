package com.guohuai.mmp.jiajiacai.wishplan.plan.rep;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import com.guohuai.basic.component.ext.web.BaseResp;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@lombok.Builder
@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class MonthPlanByOidRep extends BaseResp {
	private String planOid; //心愿计划ID
	private String planName; //心愿计划名称
	private String planTarget; //心愿目标
	private String investType; //投资方式
	private int investDuration; //投资期限
	private Timestamp addTime; //加入时间
	private Timestamp finishTime; //完成时间
	private List<InstalmentRep> instalment;
	private BigDecimal monthAmount; //每月转入金额
	private int monthTime; //每月转入时间
	private String bankCardInfo; //转入绑定银行卡信息
	private String planStatus; //计划状态
	private String totalAmount; //投资金额/累计投入
	private int totalMonths; //累计已投期数
	private String principalAndInterest; //本息合计金额
	private int transferBalance; //是否可转仅限薪增长使用
	private String redeemStatus; //判断赎回的状态
	private String orderCode;//订单号
}
