package com.guohuai.mmp.platform.payment;

import java.io.Serializable;
import java.math.BigDecimal;

@lombok.Data
public class DepositApplyRequest implements Serializable {
	
	
	/**
	 *  充值申请
		userOid		Y	会员ID
		requestNo		Y	请求流水号
		systemSource	默认 mimosa	Y	来源系统类型
		amount		Y	金额
	 */
	
	private static final long serialVersionUID = 3421418125883889708L;
	
	/**
	 * 会员ID
	 */
	private String memberId;

	/**
	 * 订单金额
	 */
	private BigDecimal orderAmount;
	
	private String systemSource;
	private String requestNo;
	
}
