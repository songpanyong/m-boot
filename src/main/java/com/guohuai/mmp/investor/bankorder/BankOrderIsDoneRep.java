package com.guohuai.mmp.investor.bankorder;

import java.sql.Timestamp;

import com.guohuai.basic.component.ext.web.BaseResp;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@lombok.Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BankOrderIsDoneRep extends BaseResp {
	private Timestamp completeTime;
}
