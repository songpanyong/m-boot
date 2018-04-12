package com.guohuai.mmp.platform.accment;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class TransferRequest {
//	publisherUserOid		String	Y	发行人ID
//	requestNo		String	Y	请求批次流水号
//	systemSource		String	Y	来源系统类型，传mimosa
//	orderNo		String	Y	订单号
//	userOid		String	Y	用户ID
//	orderType		String	Y	订单类型，申购：01、赎回:02
//	balance		BigDecimal	Y	单据金额
//	voucher		BigDecimal	Y	代金券
//	fee		BigDecimal	Y	手续费
//	submitTime		String	Y	订单时间YYYY-MM-DD HH:mm:ss
//	userType		String	Y	用户类型，投资人账户:T1、发行人账户:T2、平台账户:T3 
//					
//	remark		String	N	备注
//	orderDesc		String	N	订单描述
	
	private String publisherOid;
	private String requestNo;
	private String  systemSource;
	private String orderCode;
	private String iPayNo;
	private String investorOid;
	private String orderType;
	private BigDecimal  orderAmount;
	private BigDecimal voucher;
	private String orderTime;
	private String userType;
	private String remark;
	private String orderDesc;
	private String originBranch;
}
