package com.guohuai.mmp.platform.accment;

import java.math.BigDecimal;

@lombok.Data
public class PublisherTradeRequest {
//	userOid		Y	发行人ID
//	userType		Y	用户类型,传T2 
//	orderType		Y	交易类别
//	收款,传58
//	放款,传57
//	balance		Y	交易额
//	fee		Y	费率 传0
//	voucher		Y	代金券 传0
//	remark		Y	交易用途
//	orderNo		Y	定单号
//	systemSource		Y	来源系统类型
//	requestNo		Y	请求流水号
//	orderDesc		Y	订单描述
	
	private String  memeberId;
	private String  userType;
	private String orderType;
	private BigDecimal balance;
	private String remark;
	private String orderCode;
	private String systemSource;
	private String requestNo;
	private String  orderDesc;
}
