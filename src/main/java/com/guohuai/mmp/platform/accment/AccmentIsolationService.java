package com.guohuai.mmp.platform.accment;

import java.math.BigDecimal;

import com.guohuai.account.api.request.RedeemToBasicRequest;
import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.component.web.view.RowsRep;



public class AccmentIsolationService implements Accment {
	
	
	/**
	 * 新增用户
	 */
	@Override
	public void addUser(CreateUserReq ireq) {
		

	}

	/**
	 * 会员账户交易 投资
	 */
	@Override
	public BaseResp trade(TradeRequest ireq) {
		return this.trade(ireq, true);
	}

	@Override
	public BaseResp trade(TradeRequest ireq, boolean isLog) {
		
		return new BaseResp();
	}


	@Override
	public BaseResp close(CloseRequest ireq) {
		return new BaseResp();
	}


	@Override
	public UserBalanceRep queryBalance(String memberId) {
		UserBalanceRep irep = new UserBalanceRep();
		irep.setErrorCode(-1);
		return irep;
	}

	@Override
	public PublisherBalanceRep queryPublisherBalance(String memberId) {
		PublisherBalanceRep irep = new PublisherBalanceRep();
		irep.setErrorCode(-1);
		return irep;
	}

	@Override
	public BaseResp publisherTrade(PublisherTradeRequest ireq) {
		
		return new BaseResp();
	}

	@Override
	public BaseResp batchPay(BatchPayRequest ireq) {

		return new BaseResp();
	}

	@Override
	public BaseResp transfer(TransferRequest ireq) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RowsRep<QueryOrdersRep> queryOrders(QueryOrdersRequest ireq) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int redeem2basic(RedeemToBasicRequest req) {
		// TODO Auto-generated method stub
		return 0;
	}

}
