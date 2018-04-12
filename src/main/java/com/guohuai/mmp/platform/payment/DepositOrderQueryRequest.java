package com.guohuai.mmp.platform.payment;

import lombok.Data;

@Data
public class DepositOrderQueryRequest {
//	userOid		String	Y	用户ID
//	orderNo		String	Y	订单号
//	tradeType		String	Y	交易类型,01充值，02提现，此接口传01
	private String investorOid, iPayNo, orderType;
}
