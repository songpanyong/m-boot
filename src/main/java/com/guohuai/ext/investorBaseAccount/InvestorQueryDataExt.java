package com.guohuai.ext.investorBaseAccount;

import com.guohuai.basic.component.ext.web.PageResp;
import com.guohuai.basic.component.proactive.ProActive;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountQueryRep;
/**
 * 投资人下单成功后，扩展业务定义
 * @author gh
 *
 */
public interface InvestorQueryDataExt extends ProActive {
	/**
	 * 返回登录报错信息
	 * @param tradeOrderReq
	 * @return
	 */
	public PageResp<InvestorBaseAccountQueryRep> investorBaseAccount(PageResp<InvestorBaseAccountQueryRep> datas);
}
