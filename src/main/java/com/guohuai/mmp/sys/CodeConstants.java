package com.guohuai.mmp.sys;

public class CodeConstants {
	/**
	 * 第三方支付购买前缀 充值
	 */
	public static final String PAYMENT_deposit = "10";
	public static final String PAYMENT_depositPayNo = "101";

	/**
	 * 第三方支付购买前缀 提现
	 */
	public static final String PAYMENT_withdraw = "11";
	public static final String PAYMENT_withdrawPayNo = "111";

	/** 第三方支付购买前缀 购买 */
	public static final String PAYMENT_invest = "12";
	public static final String PAYMENT_investPayNo = "121";
	/**
	 * 第三方支付购买前缀 赎回
	 */
	public static final String PAYMENT_redeem = "13";
	/** 现金分红、赎回、T+0赎回转账*/
	public static final String PAYMENT_redeemPayNo = "131";

	/**
	 * 第三方支付购买前缀 企业充值
	 */
	public static final String PAYMENT_debitDeposit = "14";
	public static final String PAYMENT_debitDepositPayNo = "141";

	/**
	 * 第三方支付购买前缀 企业提现
	 */
	public static final String PAYMENT_debitWithdraw = "15";
	public static final String PAYMENT_debitWithdrawPayNo = "151";
	
	/**
	 * 发行人--收款
	 */
	public static final String PAYMENT_spvCollect = "16";
	public static final String PAYMENT_spvCollectPayNo = "161";
	
	/**
	 * 发行人--付款
	 */
	public static final String PAYMENT_spvPay = "17";
	public static final String PAYMENT_spvPayPayNo = "171";
	/**
	 * 付息
	 */
	public static final String PAYMENT_dividend = "88";
	
	
	/**
	 * 发行人--可用金收款
	 */
	public static final String PAYMENT_spvABCollect = "19";
	public static final String PAYMENT_spvABCollectPayNo = "191";
	
	/**
	 * 发行人--可用金付款
	 */
	public static final String PAYMENT_spvABPay = "20";
	public static final String PAYMENT_spvABPayPayNo = "201";
	
	
	
	
	/**
	 * 备付金--中间户代付
	 */
	public static final String Reserved_create_single_hosting_pay_trade = "19";
	
	/**
	 * 还本
	 */
	public static final String Publisher_repayLoan = "20";
	
	/**
	 * 付息
	 */
	public static final String Publisher_repayInterest = "21";
	
	/**
	 * 发行人--中间户代收
	 */
	public static final String Publisher_create_hosting_collect_trade = "22";
	
	/**
	 * 发行人--中间户代付
	 */
	public static final String Publisher_create_single_hosting_pay_trade = "23";
	
	/**
	 * 投资人 --批量代付--批次号
	 */
	public static final String Investor_batch_pay = "24";
	
	/**
	 * 产品编号
	 */
	public static final String Product_code = "80";
	
	/**
	 * 渠道编号
	 */
	public static final String Channel_code = "25";
	/**
	 * 渠道审批编号
	 */
	public static final String ChannelApprove_code = "26";
	/**
	 * 产品上下架编号
	 */
	public static final String ChannelProduct_code = "27";
	
	/**
	 * 委托单状态变化日志通知
	 */
	public static final String OrderLog_notifyId = "28";
	
	/**
	 * 委托单状态变化日志通知
	 */
	public static final String OrderLog_accoutingNotifyId = "29";
	
	/**
	 * 还本付息单
	 */
	public static final String Publisher_repayCash = "30";
	
	public static final String Superacc_order = "31";
	
	/**
	 * 企业
	 */
	public static final String corAuditOrderNo = "32";
	
	/** SPV订单前缀 */
	public static final String SPV_order = "SPV";
	
	/** 渠道审批编号 */
	public static final String channelApproveCode = "CAC";
	
	/**清盘*/
	public static final String PAYMENT_clearRedeem = "33";
	/** 买卖单 */
	public static final String PAYMENT_buy = "34";
	
	
	/** 红包提现前缀 */
	public static final String INVESTOR_Coupon_withdraw = "35";
	public static final String INVESTOR_Coupon_batchPay = "36";
	
	/**
	 * 投资人 --退款批量代付--批次号
	 */
	public static final String Investor_batch_refund = "37";
	
	
	
	/** 现金前缀 */
	public static final String INVESTOR_redEnvelope = "39";
	public static final String INVESTOR_redEnvelopePayNo = "40";
	
	/** 账户之间转入 */
	public static final String PAYMENT_RollIn = "66";
	public static final String PAYMENT_RollInPayNo = "661";
	/** 账户之间转出 */
	public static final String PAYMENT_RollOut = "68";
	public static final String PAYMENT_RollOutPayNo = "681";
	
	/** 心愿计划 购买 */
	public static final String PAYMENT_wishInvest = "72";
	public static final String PAYMENT_wishInvestPayNo = "721";
	/**
	 * 心愿计划 赎回
	 */
	public static final String PAYMENT_wishRedeem = "76";
	public static final String PAYMENT_wishRedeemPayNo = "761";
	
}
