package com.guohuai.mmp.platform.accment;

import java.math.BigDecimal;

@lombok.Data
public class BatchPayDto {
//	参数名	参数值	类型	必须	描述
//	orderNo		String	Y	订单号
//	userOid		String	Y	用户ID
//	orderType		String	Y	订单类型，赎回:02
//	balance		BigDecimal	Y	单据金额
//	voucher		BigDecimal	Y	代金券
//	fee		BigDecimal	Y	手续费
//	orderDesc		String	N	订单描述
//	submitTime		String	Y	订单时间
//	remark		String	N	备注
	
	private String orderCode;
	/**
	 * mimosa支付流水号
	 */
	private String iPayNo;
	
	private String memberId;
	private String orderType;
	private BigDecimal orderAmount;
	
	private String orderDesc;
	private String orderTime;
	private String remark;
	
	/** The order type of origin branch, the default is plain */
	private String originBranch;

}
