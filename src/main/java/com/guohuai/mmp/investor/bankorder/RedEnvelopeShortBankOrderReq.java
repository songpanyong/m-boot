package com.guohuai.mmp.investor.bankorder;

import org.hibernate.validator.constraints.NotBlank;

import lombok.NoArgsConstructor;

@lombok.Data
@NoArgsConstructor
public class RedEnvelopeShortBankOrderReq {
	

	/** 支付流水号 */
	@NotBlank(message = "支付流水号不能为空！")
	private String orderCode;

	/** 对账结果OID */
	@NotBlank(message = "对账结果OID不能为空！")
	private String crOid;
}
