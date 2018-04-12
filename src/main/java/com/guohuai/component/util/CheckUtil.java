package com.guohuai.component.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.guohuai.component.exception.AMPException;

/**
 * check util
 * @author Jeffrey.Wong
 * 2015年7月18日下午2:02:36
 */
public class CheckUtil {
	
	/**
	 * 校验参数是否为空
	 * @param param
	 * @param errorCode
	 */
	public static void checkParams(String param, int errorCode){
		if (param == null || "".equals(param)) {
			throw new AMPException(errorCode);
		}
	}
	
	/**
	 * 校验手机号
	 * @param mobile 手机号
	 * @param isNull 是否判断非空
	 * @param errorCode1 非空提示
	 * @param errorCode2 手机号格式错误提示
	 */
	public static void isMobileNO(String mobile, boolean isNull, int errorCode1, int errorCode2){
		if (isNull) {
			CheckUtil.checkParams(mobile, errorCode1);
		}
		Pattern p = Pattern.compile("^1[3|4|5|6|7|8|9][0-9]{9}$");
		Matcher m = p.matcher(mobile);
		if (!m.matches()) {
			throw new AMPException(errorCode2);
		}
	}
	
	/**
	 * 校验登录密码
	 * @param password 原始密码
	 * @param begLen 密码长度开始
	 * @param endLen 密码长度结束
	 * @param isNull 是否判断非空
	 * @param errorCode1 非空提示
	 * @param errorCode2 校验提示
	 */
	public static void checkloginPwd(String password, int begLen, int endLen, boolean isNull, int errorCode1, int errorCode2) {
		if (isNull) {
			CheckUtil.checkParams(password, errorCode1);
		}
		Pattern p = Pattern.compile("^([A-Z]|[a-z]|[0-9]|[`~!@#$%^&*()-_+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]){" + begLen + "," + endLen + "}$");
		Matcher m = p.matcher(password);
		if (!m.matches()) {
			throw new AMPException(errorCode2);
		}
	}
	
	/**
	 * 
	 * 交易数字类型
	 * @param param 校验字段
	 * @param begLen 密码长度开始
	 * @param endLen 密码长度结束
	 * @param isNull 是否判断非空
	 * @param errorCode1 非空提示
	 * @param errorCode2 校验提示
	 */
	public static void checkNum(String param, int begLen, int endLen, boolean isNull, int errorCode1, int errorCode2) {
		if (isNull) {
			CheckUtil.checkParams(param, errorCode1);
		}
		Pattern p = Pattern.compile("^\\d{" + begLen + "," + endLen + "}$");
		Matcher m = p.matcher(param);
		if (!m.matches()) {
			throw new AMPException(errorCode2);
		}
	}
	
}
