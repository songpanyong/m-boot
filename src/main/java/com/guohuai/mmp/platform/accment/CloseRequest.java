package com.guohuai.mmp.platform.accment;

import java.math.BigDecimal;


import lombok.Data;

@Data
public class CloseRequest {
	
	private String publisherUserOid;
	private BigDecimal nettingBalance;
	private String requestNo;
	private String systemSource;
	private String orderCode;
	private String orderType;
	private String orderTime;
	private String userType;
	private String remark;
	private String orderDesc;
	
	/**
	 * 申购金额
	 */
	private BigDecimal investAmount;

	/**
	 * 赎回金额
	 */
	private BigDecimal redeemAmount;
	
	
//	publisherUserOid		String	Y	发行人ID
//	nettingBalance		BigDecimal	Y	轧差额
//	requestNo		String	Y	请求流水号
//	systemSource		String	Y	来源系统类型，传mimosa
//	orderNo		String	Y	订单号
//	orderType		String	Y	订单类型，可用金收款：07，可用金放款：08
//	orderCreatTime		String	Y	订单时间YYYY-MM-DD HH:mm:ss
//	userType		String	Y	用户类型传T2，投资人账户:T1、发行人账户:T2、平台账户:T3 
//	remark		String	N	备注
//	orderDesc		String	N	订单描述
//	applyBalance		BigDecimal	N	申购总金额
//	redeemBalance		BigDecimal	Y	赎回总金额
}
