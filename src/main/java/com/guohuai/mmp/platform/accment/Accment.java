package com.guohuai.mmp.platform.accment;

import java.math.BigDecimal;

import com.guohuai.account.api.request.RedeemToBasicRequest;
import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.component.web.view.RowsRep;

public interface Accment {
	
	/**
	 * 新增用户
	 */
	public void addUser(CreateUserReq ireq);
	
//	/**
//	 * 创建子账户	createAccount
//	 */
//	
//	public String createAccount(CreateAccRequest ireq);
	
	/**
	 * 会员账户交易	trade
	 */
	public BaseResp trade(TradeRequest ireq);
	public BaseResp trade(TradeRequest ireq, boolean isLog);
	
//	/**
//	 * 平台转账	transferAccount
//	 */
//	public BaseResp transferAccount(TransferAccRequest ireq);
//	
//	public BaseResp transferAccount(TransferAccRequest ireq, boolean isLog);
	
//	/**
//	 * 平台、发行人账户调账	enterAccount
//	 */
//	public BaseResp enterAccout(EnterAccRequest ireq);
//	
//	public BaseResp enterAccout(EnterAccRequest ireq, boolean isLog);
	
//	public List<UserQueryRep> queryUser(UserQueryIRequest ireq);
	
//	/**
//	 * 查询平台用户
//	 */
//	public UserQueryIRep queryPlatformUser();
//	/**
//	 * 查询平台用户下相关账户
//	 */
//	public AccountQueryIRep accountQueryList(AccountQueryIRequest ireq);
	
	/**
	 * 结算
	 */
	public BaseResp close(CloseRequest ireq);

	

//	public BaseResp tradepublish(TpIntegratedRequest tpIReq);
//	
//	public BaseResp tradepublish(TpIntegratedRequest tpIReq, boolean isLog);

	/**
	 * 查询用户余额
	 */
	public UserBalanceRep queryBalance(String memberId);
	
	/**
	 * 查询发行人余额
	 */
	public PublisherBalanceRep queryPublisherBalance(String memberId);


	/**
	 * 发行人收款、付款
	 */
	public BaseResp publisherTrade(PublisherTradeRequest ireq);

	public BaseResp batchPay(BatchPayRequest ireq);

	public BaseResp transfer(TransferRequest ireq);

	public RowsRep<QueryOrdersRep> queryOrders(QueryOrdersRequest ireq);
	
	/**
	 * 
	 * @param userOid
	 * @param amount
	 * @return
	 */
	public int redeem2basic(RedeemToBasicRequest req);

}
