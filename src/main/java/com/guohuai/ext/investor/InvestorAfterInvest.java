package com.guohuai.ext.investor;

import com.guohuai.basic.component.proactive.ProActive;
import com.guohuai.basic.component.proactive.SingleProActive;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderEntity;
/**
 * 投资人下单成功后，扩展业务定义
 * @author gh
 *
 */
@SingleProActive
public interface InvestorAfterInvest extends ProActive {
	/**
	 * 投资人正常购买
	 * @param tradeOrderReq
	 * @return
	 */
	public Void normalInvestExt(InvestorTradeOrderEntity orderEntity);
}
