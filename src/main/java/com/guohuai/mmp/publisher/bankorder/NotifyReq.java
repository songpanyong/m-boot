package com.guohuai.mmp.publisher.bankorder;

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
	
	

}
