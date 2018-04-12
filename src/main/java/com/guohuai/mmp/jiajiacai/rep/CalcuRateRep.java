package com.guohuai.mmp.jiajiacai.rep;

import java.util.List;

import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.mmp.investor.sonaccount.AccountListRep;
import com.guohuai.mmp.jiajiacai.wishplan.question.InvestMessageForm;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@lombok.Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class CalcuRateRep extends BaseResp{

	private List<InvestMessageForm> list ;


}
