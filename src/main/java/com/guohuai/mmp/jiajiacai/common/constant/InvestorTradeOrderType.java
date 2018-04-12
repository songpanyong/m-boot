package com.guohuai.mmp.jiajiacai.common.constant;

public class InvestorTradeOrderType {

	//submitted:已申请，refused:已拒绝，toPay:待支付，payFailed:支付失败，paySuccess:支付成功，
	//payExpired:支付超时，accepted:已受理，confirmed:份额已确认，done:交易成功，refunded:已退款，abandoned:已作废',
	/** 订单状态--已提交 */
	public static final String TRADEORDER_orderStatus_submitted = "submitted";
	/** 订单状态--提交失败 */
	public static final String TRADEORDER_orderStatus_refused = "refused";
	/** 订单状态--待支付 */
	public static final String TRADEORDER_orderStatus_toPay = "toPay";
	/** 订单状态--支付回调失败 */
	public static final String TRADEORDER_orderStatus_payFailed = "payFailed";
	/** 订单状态--已支付 */
	public static final String TRADEORDER_orderStatus_paySuccess = "paySuccess";
	/** 订单状态--已过期 */
	public static final String TRADEORDER_orderStatus_payExpired = "payExpired";
	/** 订单状态--已受理 */
	public static final String TRADEORDER_orderStatus_accepted = "accepted";
	/** 订单状态--已确认 */
	public static final String TRADEORDER_orderStatus_confirmed = "confirmed";
	/** 订单状态--已成交 */
	public static final String TRADEORDER_orderStatus_done = "done";
	
	/** 订单状态--退款中 */
	public static final String TRADEORDER_orderStatus_refunding = "refunding";
	/** 订单状态--已退款 */
	public static final String TRADEORDER_orderStatus_refunded = "refunded";
	
	/** 订单状态--作废中 */
	public static final String TRADEORDER_orderStatus_abandoning = "abandoning";
	/** 订单状态--已作废 */
	public static final String TRADEORDER_orderStatus_abandoned = "abandoned";
	
	
	/** 交易类型--体验金赎回 */
	public static final String TRADEORDER_orderType_expGoldRedeem = "expGoldRedeem";
	/** 交易类型--补赎回单 */
	public static final String TRADEORDER_orderType_reRedeem = "reRedeem";
	/** 交易类型--投资 */
	public static final String TRADEORDER_orderType_invest = "invest";
	/** 交易类型--补投资单 */
	public static final String TRADEORDER_orderType_reInvest = "reInvest";
	/** 交易类型--体验金投资 */
	public static final String TRADEORDER_orderType_expGoldInvest = "expGoldInvest";
	/** 交易类型--普赎 */
	public static final String TRADEORDER_orderType_normalRedeem = "normalRedeem";
	/** 交易类型--快赎 */
	public static final String TRADEORDER_orderType_fastRedeem = "fastRedeem";
	/** 交易类型--清盘赎回 */
	public static final String TRADEORDER_orderType_clearRedeem = "clearRedeem";
	/** 交易类型--还本/付息 */
	public static final String TRADEORDER_orderType_cash = "cash";
	/** 交易类型--募集失败退款 */
	public static final String TRADEORDER_orderType_cashFailed = "cashFailed";
	/** 交易类型--还本 */
	public static final String TRADEORDER_orderType_repayLoan = "repayLoan";
	/** 交易类型--付息 */
	public static final String TRADEORDER_orderType_repayInterest = "repayInterest";
	
	/** 交易类型--红利 */
	public static final String TRADEORDER_orderType_dividend = "dividend";
	// 
	/** 交易类型--退款 */
	public static final String TRADEORDER_orderType_refund = "refund";
	/** 交易类型--买卖(平台) */
	public static final String TRADEORDER_orderType_buy = "buy";
	/** 交易类型--冲销单 */
	public static final String TRADEORDER_orderType_writeOff = "writeOff";

	/** 订单创建人--投资人 */
	public static final String TRADEORDER_createMan_investor = "investor";
	/** 订单创建人--平台 */
	public static final String TRADEORDER_createMan_platform = "platform";
	/** 订单创建人--发行人 */
	public static final String TRADEORDER_createMan_publisher = "publisher";

	/** 三方对账状态--已对 */
	public static final String TRADEORDER_checkStatus_yes = "yes";
	/** 三方对账状态--未对 */
	public static final String TRADEORDER_checkStatus_no = "no";

	/** 合同生成状态--等待生成html */
	public static final String TRADEORDER_contractStatus_toHtml = "toHtml";
	/** 合同生成状态--生成html成功 */
	public static final String TRADEORDER_contractStatus_htmlOK = "htmlOK";
	/** 合同生成状态--生成html失败 */
	public static final String TRADEORDER_contractStatus_htmlFail = "htmlFail";
	/** 合同生成状态--生成PDF成功 */
	public static final String TRADEORDER_contractStatus_pdfOK = "pdfOK";

	/** 发行人清算状态--待清算 */
	public static final String TRADEORDER_publisherClearStatus_toClear = "toClear";
	/** 发行人清算状态--清算中 */
	public static final String TRADEORDER_publisherClearStatus_clearing = "clearing";
	/** 发行人清算状态--已清算 */
	public static final String TRADEORDER_publisherClearStatus_cleared = "cleared";

	/** 发行人交收状态--待交收 */
	public static final String TRADEORDER_publisherConfirmStatus_toConfirm = "toConfirm";
	/** 发行人交收状态--交收中 */
	public static final String TRADEORDER_publisherConfirmStatus_confirming = "confirming";
	/** 发行人交收状态--已交收 */
	public static final String TRADEORDER_publisherConfirmStatus_confirmed = "confirmed";
	public static final String TRADEORDER_publisherConfirmStatus_confirmFailed = "confirmFailed";

	/** 发行人结算状态--待结算 */
	public static final String TRADEORDER_publisherCloseStatus_toClose = "toClose";
	/** 发行人结算状态--结算中 */
	public static final String TRADEORDER_publisherCloseStatus_closing = "closing";
	/** 发行人结算状态--已结算 */
	public static final String TRADEORDER_publisherCloseStatus_closed = "closed";
	/** 发行人结算状态--结算申请失败 */
	public static final String TRADEORDER_publisherCloseStatus_closeSubmitFailed = "closeSubmitFailed";
	/** 发行人结算状态--结算支付失败 */
	public static final String TRADEORDER_publisherCloseStatus_closePayFailed = "closePayFailed";
	/** 投资人结算状态--结算待支付 */
	public static final String TRADEORDER_publisherCloseStatus_closeToPay= "closeToPay";

	/** 投资人清算状态--待清算 */
	public static final String TRADEORDER_investorClearStatus_toClear = "toClear";
	/** 投资人清算状态--清算中 */
	public static final String TRADEORDER_investorClearStatus_clearing = "clearing";
	/** 投资人清算状态--已清算 */
	public static final String TRADEORDER_investorClearStatus_cleared = "cleared";

	/** 投资人结算状态--待结算 */
	public static final String TRADEORDER_investorCloseStatus_toClose = "toClose";
	/** 投资人结算状态--结算中 */
	public static final String TRADEORDER_investorCloseStatus_closing = "closing";
	/** 投资人结算状态--已结算 */
	public static final String TRADEORDER_investorCloseStatus_closed = "closed";
	/** 投资人结算状态--结算申请失败 */
	public static final String TRADEORDER_investorCloseStatus_closeSubmitFailed = "closeSubmitFailed";
	/** 投资人结算状态--结算支付失败 */
	public static final String TRADEORDER_investorCloseStatus_closePayFailed = "closePayFailed";
	
	
	/** 可赎回状态-可以 */
	public static final String TRADEORDER_redeemStatus_yes = "yes";
	/** 可赎回状态-不可以 */
	public static final String TRADEORDER_redeemStatus_no = "no";
	
	/** 可计息状态-可以 */
	public static final String TRADEORDER_accrualStatus_yes = "yes";
	/** 可计息状态-不可以 */
	public static final String TRADEORDER_accrualStatus_no = "no";
	
	/** 是否使用了卡券-是 */
	public static final String TRADEORDER_usedCoupons_yes = "yes";
	/** 是否使用了卡券-否 */
	public static final String TRADEORDER_usedCoupons_no = "no";
	
	
	/** 持有状态-待确认 */
	public static final String TRADEORDER_holdStatus_toConfirm = "toConfirm";
	/** 持有状态-持有中 */
	public static final String TRADEORDER_holdStatus_holding = "holding";
	/** 持有状态-已到期 */
	public static final String TRADEORDER_holdStatus_expired = "expired";
	/** 持有状态-部分持有 */
	public static final String TRADEORDER_holdStatus_partHolding = "partHolding";
	/** 持有状态-已结算 */
	public static final String TRADEORDER_holdStatus_closed = "closed";
	/** 持有状态-已退款 */
	public static final String TRADEORDER_holdStatus_refunded = "refunded";
	/** 持有状态-已作废 */
	public static final String TRADEORDER_holdStatus_abandoned = "abandoned";
	
//	/** 支付状态-待支付 */
//	public static final String TRADEORDER_payStatus_toPay = "toPay";
//	/** 支付状态-申请失败 */
//	public static final String TRADEORDER_payStatus_submitFailed = "submitFailed";
//	/** 支付状态-支付失败 */
//	public static final String TRADEORDER_payStatus_payFailed = "payFailed";
//	/** 支付状态-支付成功 */
//	public static final String TRADEORDER_payStatus_paySuccess = "paySuccess";
//	/** 支付状态-支付超时 */
//	public static final String TRADEORDER_payStatus_payExpired = "payExpired";
	
//	/** 受理状态-待受理 */
//	public static final String TRADEORDER_acceptStatus_toAccept = "toAccept";
//	/** 受理状态-已受理 */
//	public static final String TRADEORDER_acceptStatus_accepted = "accepted";
//	/** 受理状态-受理失败 */
//	public static final String TRADEORDER_acceptStatus_acceptFailed = "acceptFailed";
	
//	
//	/** 退款状态-待退款 */
//	public static final String TRADEORDER_refundStatus_toRefund = "toRefund";
//	/** 退款状态-退款中 */
//	public static final String TRADEORDER_refundStatus_refunding = "refunding";
//	/** 退款状态-已退款 */
//	public static final String TRADEORDER_refundStatus_refunded = "refunded";
//	/** 退款状态-退款失败 */
//	public static final String TRADEORDER_refundStatus_refundFailed = "refundFailed";
	
}
