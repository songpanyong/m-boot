package com.guohuai.mmp.investor.tradeorder;

import com.guohuai.component.util.StringUtil;

public class TradeUtil {
//	/** 发行人结算状态--待结算 */
//	public static final String TRADEORDER_publisherCloseStatus_toClose = "toClose";
//	/** 发行人结算状态--结算中 */
//	public static final String TRADEORDER_publisherCloseStatus_closing = "closing";
//	/** 发行人结算状态--已结算 */
//	public static final String TRADEORDER_publisherCloseStatus_closed = "closed";
//	/** 发行人结算状态--结算申请失败 */
//	public static final String TRADEORDER_publisherCloseStatus_closeSubmitFailed = "closeSubmitFailed";
//	/** 发行人结算状态--结算支付失败 */
//	public static final String TRADEORDER_publisherCloseStatus_closePayFailed = "closePayFailed";
	public static String publisherCloseStatusEn2Ch(String publisherCloseStatus) {
		
		if (InvestorTradeOrderEntity.TRADEORDER_publisherCloseStatus_toClose.equals(publisherCloseStatus)) {
			return "待结算";
		}
		if (InvestorTradeOrderEntity.TRADEORDER_publisherCloseStatus_closing.equals(publisherCloseStatus)) {
			return "结算中";
		}
		if (InvestorTradeOrderEntity.TRADEORDER_publisherCloseStatus_closed.equals(publisherCloseStatus)) {
			return "已结算";
		}
		if (InvestorTradeOrderEntity.TRADEORDER_publisherCloseStatus_closeToPay.equals(publisherCloseStatus)) {
			return "结算待支付";
		}
		if (InvestorTradeOrderEntity.TRADEORDER_publisherCloseStatus_closeSubmitFailed.equals(publisherCloseStatus)) {
			return "结算申请失败";
		}
		if (InvestorTradeOrderEntity.TRADEORDER_publisherCloseStatus_closePayFailed.equals(publisherCloseStatus)) {
			return "结算支付失败";
		}
		return publisherCloseStatus;
	}
	
//	/** 发行人交收状态--待交收 */
//	public static final String TRADEORDER_publisherConfirmStatus_toConfirm = "toConfirm";
//	/** 发行人交收状态--交收中 */
//	public static final String TRADEORDER_publisherConfirmStatus_confirming = "confirming";
//	/** 发行人交收状态--已交收 */
//	public static final String TRADEORDER_publisherConfirmStatus_confirmed = "confirmed";
//	public static final String TRADEORDER_publisherConfirmStatus_confirmFailed = "confirmFailed";
	public static String publisherConfirmStatusEn2Ch(String publisherConfirmStatus) {
		if (InvestorTradeOrderEntity.TRADEORDER_publisherConfirmStatus_toConfirm.equals(publisherConfirmStatus)) {
			return "待交收";
		}
		if (InvestorTradeOrderEntity.TRADEORDER_publisherConfirmStatus_confirming.equals(publisherConfirmStatus)) {
			return "交收中";
		}
		if (InvestorTradeOrderEntity.TRADEORDER_publisherConfirmStatus_confirmed.equals(publisherConfirmStatus)) {
			return "已交收 ";
		}
		if (InvestorTradeOrderEntity.TRADEORDER_publisherConfirmStatus_confirmFailed.equals(publisherConfirmStatus)) {
			return "交收失败";
		}
		
		
		return publisherConfirmStatus;
	}


//	/** 发行人清算状态--待清算 */
//	public static final String TRADEORDER_publisherClearStatus_toClear = "toClear";
//	/** 发行人清算状态--清算中 */
//	public static final String TRADEORDER_publisherClearStatus_clearing = "clearing";
//	/** 发行人清算状态--已清算 */
//	public static final String TRADEORDER_publisherClearStatus_cleared = "cleared";
	public static String publisherClearStatusEn2Ch(String publisherClearStatus) {
		if (InvestorTradeOrderEntity.TRADEORDER_publisherClearStatus_toClear.equals(publisherClearStatus)) {
			return "待清算";
		}
		if (InvestorTradeOrderEntity.TRADEORDER_publisherClearStatus_clearing.equals(publisherClearStatus)) {
			return "清算中";
		}
		if (InvestorTradeOrderEntity.TRADEORDER_publisherClearStatus_cleared.equals(publisherClearStatus)) {
			return "已清算";
		}
		
		return publisherClearStatus;
	}

	public static String investorCloseStatusEn2Ch(String investorCloseStatus) {
		if (null == investorCloseStatus) {
			return StringUtil.EMPTY;
		}
		if (InvestorTradeOrderEntity.TRADEORDER_investorCloseStatus_toClose.equals(investorCloseStatus)) {
			return "待结算";
		}
		if (InvestorTradeOrderEntity.TRADEORDER_investorCloseStatus_closing.equals(investorCloseStatus)) {
			return "结算中";
		}
		if (InvestorTradeOrderEntity.TRADEORDER_investorCloseStatus_closed.equals(investorCloseStatus)) {
			return "已结算";
		}
		if (InvestorTradeOrderEntity.TRADEORDER_investorCloseStatus_closeSubmitFailed.equals(investorCloseStatus)) {
			return "结算申请失败";
		}
		if (InvestorTradeOrderEntity.TRADEORDER_investorCloseStatus_closePayFailed.equals(investorCloseStatus)) {
			return "结算支付失败";
		}
		
		return investorCloseStatus;
	}
	
	public static String investorClearStatusEn2Ch(String investorClearStatus) {
		
		if (null == investorClearStatus) {
			return StringUtil.EMPTY;
		}
		
		if (InvestorTradeOrderEntity.TRADEORDER_investorClearStatus_toClear.equals(investorClearStatus)) {
			return "待清算";
		}
		if (InvestorTradeOrderEntity.TRADEORDER_investorClearStatus_clearing.equals(investorClearStatus)) {
			return "清算中";
		}
		if (InvestorTradeOrderEntity.TRADEORDER_investorClearStatus_cleared.equals(investorClearStatus)) {
			return "已清算";
		}
		
		return investorClearStatus;
	}
	
	
//	/** 交易类型--投资 */
//	public static final String TRADEORDER_orderType_invest = "invest";
//	/** 交易类型--普赎 */
//	public static final String TRADEORDER_orderType_normalRedeem = "normalRedeem";
//	/** 交易类型--快赎 */
//	public static final String TRADEORDER_orderType_fastRedeem = "fastRedeem";
//	/** 交易类型--清盘 */
//	public static final String TRADEORDER_orderType_clearRedeem = "clearRedeem";
//	/** 交易类型--还本/付息 */
//	public static final String TRADEORDER_orderType_cash = "cash";
//	/** 交易类型--还本 */
//	public static final String TRADEORDER_orderType_repayLoan = "repayLoan";
//	/** 交易类型--付息 */
//	public static final String TRADEORDER_orderType_repayInterest = "repayInterest";
//	/** 交易类型--退款 */
//	public static final String TRADEORDER_orderType_refund = "refund";
//	/** 交易类型--买卖(平台) */
//	public static final String TRADEORDER_orderType_buy = "buy";
//	/** 交易类型--冲销单 */
//	public static final String TRADEORDER_orderType_writeOff = "writeOff";
	public static String orderTypeEn2Ch(String orderType) {
		if (InvestorTradeOrderEntity.TRADEORDER_orderType_invest.equals(orderType)) {
			return "申购";
		}
		if (InvestorTradeOrderEntity.TRADEORDER_orderType_normalRedeem.equals(orderType)) {
			return "赎回";
		}
		if (InvestorTradeOrderEntity.TRADEORDER_orderType_fastRedeem.equals(orderType)) {
			return "快赎";
		}
		if (InvestorTradeOrderEntity.TRADEORDER_orderType_clearRedeem.equals(orderType)) {
			return "清盘赎回";
		}
		if (InvestorTradeOrderEntity.TRADEORDER_orderType_cash.equals(orderType)) {
			return "还本/付息";
		}
		if (InvestorTradeOrderEntity.TRADEORDER_orderType_cashFailed.equals(orderType)) {
			return "募集失败退款";
		}
		if (InvestorTradeOrderEntity.TRADEORDER_orderType_repayLoan.equals(orderType)) {
			return "还本";
		}
		if (InvestorTradeOrderEntity.TRADEORDER_orderType_repayInterest.equals(orderType)) {
			return "付息";
		}
		if (InvestorTradeOrderEntity.TRADEORDER_orderType_refund.equals(orderType)) {
			return "退款";
		}
		if (InvestorTradeOrderEntity.TRADEORDER_orderType_buy.equals(orderType)) {
			return "买卖";
		}
		if (InvestorTradeOrderEntity.TRADEORDER_orderType_writeOff.equals(orderType)) {
			return "冲销单";
		}
		if (InvestorTradeOrderEntity.TRADEORDER_orderType_expGoldRedeem.equals(orderType)) {
			return "赎回(体验金)";
		}
		if (InvestorTradeOrderEntity.TRADEORDER_orderType_expGoldInvest.equals(orderType)) {
			return "申购(体验金)";
		}
		
		if (InvestorTradeOrderEntity.TRADEORDER_orderType_reInvest.equals(orderType)) {
			return "申购";
		}
		
		if (InvestorTradeOrderEntity.TRADEORDER_orderType_reRedeem.equals(orderType)) {
			return "赎回";
		}
		if (InvestorTradeOrderEntity.TRADEORDER_orderType_dividend.equals(orderType)) {
			return "现金分红";
		}
		//Added for wishplan
		if (InvestorTradeOrderEntity.TRADEORDER_orderType_wishInvest.equals(orderType)) {
			return "心愿购买";
		}
		if (InvestorTradeOrderEntity.TRADEORDER_orderType_wishRedeem.equals(orderType)) {
			return "心愿赎回";
		}
		

		return orderType;
	}

	
//	/** 订单状态--已提交 */
//	public static final String TRADEORDER_orderStatus_submitted = "submitted";
//	/** 订单状态--提交失败 */
//	public static final String TRADEORDER_orderStatus_refused = "refused";
//	/** 订单状态--待支付 */
//	public static final String TRADEORDER_orderStatus_toPay = "toPay";
//	/** 订单状态--支付回调失败 */
//	public static final String TRADEORDER_orderStatus_payFailed = "payFailed";
//	/** 订单状态--已支付 */
//	public static final String TRADEORDER_orderStatus_paySuccess = "paySuccess";
//	/** 订单状态--已过期 */
//	public static final String TRADEORDER_orderStatus_payExpired = "payExpired";
//	/** 订单状态--已受理 */
//	public static final String TRADEORDER_orderStatus_accepted = "accepted";
//	/** 订单状态--确认 */
//	public static final String TRADEORDER_orderStatus_confirmed = "confirmed";
//	/** 订单状态--成交 */
//	public static final String TRADEORDER_orderStatus_done = "done";
//	/** 订单状态--已退款 */
//	public static final String TRADEORDER_orderStatus_refunded = "refunded";
//	/** 订单状态--已作废 */
//	public static final String TRADEORDER_orderStatus_abandoned = "abandoned";
	public static String orderStatusEn2Ch(String orderStatus) {
		if (InvestorTradeOrderEntity.TRADEORDER_orderStatus_submitted.equals(orderStatus)) {
			return "已提交";
		}
		if (InvestorTradeOrderEntity.TRADEORDER_orderStatus_refused.equals(orderStatus)) {
			return "交易关闭";
		}
		if (InvestorTradeOrderEntity.TRADEORDER_orderStatus_toPay.equals(orderStatus)) {
			return "待支付";
		}
		if (InvestorTradeOrderEntity.TRADEORDER_orderStatus_payFailed.equals(orderStatus)) {
			return "支付失败";
		}
		if (InvestorTradeOrderEntity.TRADEORDER_orderStatus_paySuccess.equals(orderStatus)) {
			return "已支付";
		}
		if (InvestorTradeOrderEntity.TRADEORDER_orderStatus_payExpired.equals(orderStatus)) {
			return "已过期";
		}
		if (InvestorTradeOrderEntity.TRADEORDER_orderStatus_accepted.equals(orderStatus)) {
			return "已受理";
		}
		if (InvestorTradeOrderEntity.TRADEORDER_orderStatus_confirmed.equals(orderStatus)) {
			return "已确认";
		}
		if (InvestorTradeOrderEntity.TRADEORDER_orderStatus_done.equals(orderStatus)) {
			return "已成交";
		}
		if (InvestorTradeOrderEntity.TRADEORDER_orderStatus_refunded.equals(orderStatus)) {
			return "已退款";
		}
		if (InvestorTradeOrderEntity.TRADEORDER_orderStatus_abandoned.equals(orderStatus)) {
			return "已作废";
		}

		return orderStatus;
	}
	
	public static String createManEn2Ch(String createMan) {
		if (InvestorTradeOrderEntity.TRADEORDER_createMan_investor.equals(createMan)) {
			return "投资人";
		} else if (InvestorTradeOrderEntity.TRADEORDER_createMan_platform.equals(createMan)) {
			return "平台";
		} else if (InvestorTradeOrderEntity.TRADEORDER_createMan_publisher.equals(createMan)) {
			return "发行人";
		}
		return createMan;
	}
	

	public static String contractStatusEn2Ch(String contractStatus) {
		if (InvestorTradeOrderEntity.TRADEORDER_contractStatus_toHtml.equals(contractStatus)) {
			return "等待生成html";
		} 
		if (InvestorTradeOrderEntity.TRADEORDER_contractStatus_htmlOK.equals(contractStatus)) {
			return "生成html成功";
		}  
		if (InvestorTradeOrderEntity.TRADEORDER_contractStatus_htmlFail.equals(contractStatus)) {
			return "生成html失败";
		}
		if (InvestorTradeOrderEntity.TRADEORDER_contractStatus_pdfOK.equals(contractStatus)) {
			return "生成PDF成功";
		}
		return contractStatus;
	}
	
//	/** 可赎回状态-可以 */
//	public static final String TRADEORDER_redeemStatus_yes = "yes";
//	/** 可赎回状态-不可以 */
//	public static final String TRADEORDER_redeemStatus_no = "no";
	public static String redeemStatusEn2Ch(String redeemStatus) {
		if (InvestorTradeOrderEntity.TRADEORDER_redeemStatus_yes.equals(redeemStatus)) {
			return "是";
		}
		if (InvestorTradeOrderEntity.TRADEORDER_redeemStatus_no.equals(redeemStatus)) {
			return "否";
		}
		return redeemStatus;
	}
	
	
	
//	/** 可计息状态-可以 */
//	public static final String TRADEORDER_accrualStatus_yes = "yes";
//	/** 可计息状态-不可以 */
//	public static final String TRADEORDER_accrualStatus_no = "no";
	public static String accrualStatusEn2Ch(String accrualStatus) {
		if (InvestorTradeOrderEntity.TRADEORDER_accrualStatus_yes.equals(accrualStatus)) {
			return "是";
		}
		if (InvestorTradeOrderEntity.TRADEORDER_accrualStatus_no.equals(accrualStatus)) {
			return "否";
		}
		return accrualStatus;
	}
//	/** 持有状态-待确认 */
//	public static final String TRADEORDER_holdStatus_toConfirm="toConfirm";
//	/** 持有状态-持有中 */
//	public static final String TRADEORDER_holdStatus_holding="holding";
//	/** 持有状态-已到期 */
//	public static final String TRADEORDER_holdStatus_expired="expired";
//	/** 持有状态-部分持有 */
//	public static final String TRADEORDER_holdStatus_partHolding="partHolding";
//	/** 持有状态-已结算 */
//	public static final String TRADEORDER_holdStatus_closed="closed";
//	/** 持有状态-已作废 */
//	public static final String TRADEORDER_holdStatus_abandoned="abandoned";
	public static String holdStatusEn2Ch(String holdStatus) {
		if (InvestorTradeOrderEntity.TRADEORDER_holdStatus_toConfirm.equals(holdStatus)) {
			return "待确认";
		}
		if (InvestorTradeOrderEntity.TRADEORDER_holdStatus_holding.equals(holdStatus)) {
			return "持有中";
		}
		if (InvestorTradeOrderEntity.TRADEORDER_holdStatus_expired.equals(holdStatus)) {
			return "已到期 ";
		}
		if (InvestorTradeOrderEntity.TRADEORDER_holdStatus_partHolding.equals(holdStatus)) {
			return "部分持有";
		}
		if (InvestorTradeOrderEntity.TRADEORDER_holdStatus_closed.equals(holdStatus)) {
			return "已结算";
		}
		if (InvestorTradeOrderEntity.TRADEORDER_holdStatus_abandoned.equals(holdStatus)) {
			return "已作废";
		}
		return holdStatus;
	}
	
	public static String viewStatusEn2Ch(String viewStatus, Long holdDays) {
		if (TradeOrderQueryJZRep.TRADEORDER_viewStatus_accepted.equals(viewStatus)) {
			return "已受理";
		}
		if (TradeOrderQueryJZRep.TRADEORDER_viewStatus_work.equals(viewStatus)) {
			return "今日起息";
		}
		if (TradeOrderQueryJZRep.TRADEORDER_viewStatus_holding.equals(viewStatus)) {
			return "已持有 "+holdDays+"天";
		}
		if (TradeOrderQueryJZRep.TRADEORDER_viewStatus_over.equals(viewStatus)) {
			return "已结束";
		}
		if (TradeOrderQueryJZRep.TRADEORDER_viewStatus_fail.equals(viewStatus)) {
			return "订单失败";
		}
		return viewStatus;
	}
}
