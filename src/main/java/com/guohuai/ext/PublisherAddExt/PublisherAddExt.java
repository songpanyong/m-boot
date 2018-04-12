package com.guohuai.ext.PublisherAddExt;

import com.guohuai.basic.component.ext.web.PageResp;
import com.guohuai.basic.component.proactive.ProActive;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountQueryRep;
import com.guohuai.mmp.platform.accment.UserBindCardApplyRep;
import com.guohuai.mmp.publisher.baseaccount.BaseAccountAddRep;
import com.guohuai.mmp.publisher.baseaccount.PublisherBaseAccountBindApplyReq;
import com.guohuai.mmp.publisher.baseaccount.PublisherBaseAccountReq;
/**
 * 投资人登录后返回登录信息
 * @author gh
 *
 */
public interface PublisherAddExt extends ProActive {
	/**
	 * 返回登录报错信息
	 * @param tradeOrderReq
	 * @return
	 */
	public BaseAccountAddRep publisherAddExt(PublisherBaseAccountReq rep);
}
