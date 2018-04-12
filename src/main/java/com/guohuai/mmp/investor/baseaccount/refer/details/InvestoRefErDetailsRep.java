package com.guohuai.mmp.investor.baseaccount.refer.details;

import java.util.Date;

import com.guohuai.component.util.StringUtil;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/** 我的推荐列表 */
@lombok.Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class InvestoRefErDetailsRep {

	public InvestoRefErDetailsRep(InvestoRefErDetailsEntity entity, boolean kickstar) {
		this.phone = kickstar ?  StringUtil.kickstarOnPhoneNum(entity.getInvestorBaseAccount().getPhoneNum()) : entity.getInvestorBaseAccount().getPhoneNum();
		this.date = entity.getCreateTime();
		this.realName = kickstar ? StringUtil.kickstarOnRealname(entity.getInvestorBaseAccount().getRealName()) : entity.getInvestorBaseAccount().getRealName();
	}

	private String phone;
	private String realName;
	private Date date;

}
