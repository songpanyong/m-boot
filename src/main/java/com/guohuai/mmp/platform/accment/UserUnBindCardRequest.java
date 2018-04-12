package com.guohuai.mmp.platform.accment;

import lombok.Data;

@Data
public class UserUnBindCardRequest {
	/**
	 * 解绑卡
userOid		Y	会员ID
requestNo		Y	请求流水号:表示一次交互 uuid
cardNo		Y	银行卡号
systemSource	默认 mimosa	Y	来源系统类型
	 */
	
	private String memberId;
	
	private String requestNo;
	
	private String cardNo;
	
	private String systemSource;
}
