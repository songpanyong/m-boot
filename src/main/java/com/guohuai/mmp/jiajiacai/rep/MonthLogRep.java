package com.guohuai.mmp.jiajiacai.rep;

import java.util.List;

import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.mmp.jiajiacai.wishplan.plan.form.PlanDepositForm;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@lombok.Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class MonthLogRep extends BaseResp {

	private List<PlanDepositForm> list;
}
