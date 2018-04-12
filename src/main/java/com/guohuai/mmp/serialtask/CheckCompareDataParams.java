package com.guohuai.mmp.serialtask;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@lombok.Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class CheckCompareDataParams {
	private String checkOid;
	private String checkDate;
	private String operator;
}
