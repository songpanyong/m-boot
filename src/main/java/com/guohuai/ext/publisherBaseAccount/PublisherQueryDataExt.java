package com.guohuai.ext.publisherBaseAccount;

import com.guohuai.basic.component.ext.web.PageResp;
import com.guohuai.basic.component.proactive.ProActive;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountQueryRep;
import com.guohuai.mmp.platform.accment.UserBindCardApplyRep;
import com.guohuai.mmp.publisher.baseaccount.PublisherBaseAccountBindApplyReq;
/**
 * 投资人登录后返回登录信息
 * @author gh
 *
 */
public interface PublisherQueryDataExt extends ProActive {
	/**
	 * 返回登录报错信息
	 * @param tradeOrderReq
	 * @return
	 */
	public UserBindCardApplyRep bindCardApplyExt(PublisherBaseAccountBindApplyReq rep);
}
