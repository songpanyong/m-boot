package com.guohuai.mmp.jiajiacai.wishplan.plan.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AggregateAmountForm {

	private int aggregateAmount; // 对于按月定投的是 累计投资
	private int expectAmount;  //到期可用
	private int[] number; // 期数 作为投资的月份
	private int[] planAmount; // 每月的投资金额
	private int[] amount; // 每月的投资金额
	private String[] month; // 划款月份
	private String[] status; // 每月扣款状态
}
