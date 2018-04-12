package com.guohuai.mmp.jiajiacai.wishplan.plan.rep;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class InstalmentRep {
	private int year; //分期付款的年份
	private int month; //分期付款的月份
	private String status; //分期付款对应的状态
}
