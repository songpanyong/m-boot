package com.guohuai.mmp.platform.accment;

import lombok.Data;

@Data
public class UserBindCardConfirmRequest {
	/**
	 * 绑卡确认
	userOid		Y	会员ID
requestNo		Y	请求流水号:表示一次交互 uuid
phone		Y	绑卡手机号
smsCode		Y	短信验证码，申请时发送到绑定手机的验证码
cardOrderId		N	绑卡编号，如果绑卡申请中返回则必输，否则可空
	 */
	
	private String memberId;
	
	private String requestNo;
	
	private String phone;
	
	private String smsCode;
	
	private String cardOrderId;
}
