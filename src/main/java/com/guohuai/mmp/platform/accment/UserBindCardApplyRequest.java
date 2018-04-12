package com.guohuai.mmp.platform.accment;

import lombok.Data;

@Data
public class UserBindCardApplyRequest {
	/**
	 * 绑卡申请
	userOid		Y	会员ID
	requestNo		Y	请求流水号:表示一次交互 uuid
	bankName		N	客户银行卡行别,如：平安银行
	realName		Y	客户姓名
	cardNo		Y	客户银行账号
	phone		Y	手机号码
	certificateNo		Y	客户证件号码
	 */
	
	private String memberId;
	
	private String requestNo;
	
	private String phone;
	
	private String realName;
	
	private String certificateNo;
	
	private String bankName;
	
	private String cardNo;
}
