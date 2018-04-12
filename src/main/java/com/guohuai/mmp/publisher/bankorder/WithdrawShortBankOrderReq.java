package com.guohuai.mmp.publisher.bankorder;

import lombok.NoArgsConstructor;

@lombok.Data
@NoArgsConstructor
public class WithdrawShortBankOrderReq {
	

	/**
	 * 支付流水号
	 */
	private String orderCode;
	
	
	/**
	 * 对账结果OID
	 */
	private String crOid;
	
	

}
