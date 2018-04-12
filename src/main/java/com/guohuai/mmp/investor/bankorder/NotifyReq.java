package com.guohuai.mmp.investor.bankorder;

import lombok.NoArgsConstructor;

@lombok.Data
@NoArgsConstructor
public class NotifyReq {
	

	/**
	 * 支付流水号
	 */
	private String payNo;
	
	
	/**
	 * 对账结果OID
	 */
	private String crOid;
	
	/**
	 * 订单号
	 */
	private String orderCode;

}
