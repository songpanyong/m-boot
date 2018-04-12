package com.guohuai.mmp.sms;

public class SMSTypeEnum {

	/**
	 * 外部短信类型
	 * @author xjj
	 *
	 */
	public enum smstypeEnum {  
        // 注册，快速登录，忘记登录密码，重置交易密码，其他
		regist, login, forgetlogin, forgetpaypwd, normal,bindcard;  
    }  
	
	public static boolean checkSMSType(String smsType) {
		for (smstypeEnum smstypeEnum : SMSTypeEnum.smstypeEnum.values()) {
			if (smstypeEnum.toString().equals(smsType)) {
				return true;
			}
		}
		return false;
	}
	
}
