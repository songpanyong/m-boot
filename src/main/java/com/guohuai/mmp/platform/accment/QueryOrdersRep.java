package com.guohuai.mmp.platform.accment;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class QueryOrdersRep {
//	orderCode		String	订单号
//	orderType		String	订单类型，申购invest，赎回redeem，提现withdraw 可用金放款abpay，收款abcollect，充值deposit， 冲正offsetPositive，冲负offsetNegative，红包redEnvelope
//	userType		String	用户类型，投资人账户:T1、发行人账户:T2、平台账户:T3 
//	orderAmount		BigDecimal	订单金额
//	investorOid		String	投资人id
//	buzzDate		String	订单日期
//	countNum		long	分页偏移量，默认0
//	orderStatus		String	订单状态
//	errorMessage		String	失败详情
//	returnCode		String	结果码:0000交易成功,其它为交易失败
	/**
	 * 订单号
	 */
	private String orderCode;
	/**
	 * 订单类型
	 */
	private String orderType;
	/**
	 * 用户类型
	 */
	private String userType;
	/**
	 * 交易金额
	 */
	private BigDecimal tradeAmount;
	/**
	 * 手续费
	 */
	private BigDecimal fee;
	
	/**
	 * 卡券金额
	 */
	private BigDecimal voucher;
	/**
	 * 用户
	 */
	private String investorOid;
	/**
	 * 投资者账号
	 */
	private String phoneNum;
	
	/**
	 * 投资者姓名
	 */
	private String realName;
	
	/**
	 * 三方对账状态
	 */
	private String reconciliationStatus;
	
	/**
	 * 订单时间(来源于业务)
	 */
	private String buzzDate;
	
	/**
	 * 偏移量
	 */
	private long countNum;
	/**
	 * 订单状态
	 */
	private String orderStatus;
	
	
}
