package com.guohuai.mmp.investor.bankorder;

import lombok.NoArgsConstructor;

@lombok.Data
@NoArgsConstructor
public class DepositShortBankOrderReq {
	

	/**
	 * 支付流水号
	 */
	private String orderCode;
	
	
	/**
	 * 对账结果OID
	 */
	private String crOid;
	
	

}
