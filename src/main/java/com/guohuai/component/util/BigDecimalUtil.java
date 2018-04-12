package com.guohuai.component.util;

import java.math.BigDecimal;

import com.guohuai.component.exception.AMPException;
import com.guohuai.mmp.sys.SysConstant;

/**
 * 单位换算
 * 以元为单位，保留到分
 * @author star.zhu
 * 2016年7月13日
 */
public class BigDecimalUtil {

	public static final BigDecimal init0 	= BigDecimal.ZERO;
	public static final BigDecimal NUM1 	= new BigDecimal("1");
	public static final BigDecimal NUM1_1 	= new BigDecimal("1.1");
	public static final BigDecimal NUM100 	= new BigDecimal("100");
	public static final BigDecimal NUM10000 = new BigDecimal("10000");
	public static final BigDecimal NUM365 	= new BigDecimal("365");
	
	/**
	 * 百分比换算，保留4位百分比小数 xx.xxxx%
	 * @param data
	 * @return
	 */
	public static BigDecimal formatForMul100(BigDecimal data) {
		if (null == data)
			return init0;
		else
			return data.multiply(NUM100).setScale(4, BigDecimal.ROUND_HALF_UP);
	}
	
	/**
	 * 百分比还原，保留4位百分比小数 xx.xxxx%
	 * @param data
	 * @return
	 */
	public static BigDecimal formatForDivide100(BigDecimal data) {
		if (null == data)
			return init0;
		else
			return data.divide(NUM100).setScale(6, BigDecimal.ROUND_HALF_UP);
	}
	
	/**
	 * 万元换算成元，保留到分
	 * @param data
	 * @return
	 */
	public static BigDecimal formatForMul10000(BigDecimal data) {
		if (null == data)
			return init0;
		else
			return data.multiply(NUM10000).setScale(2, BigDecimal.ROUND_HALF_UP);
	}
	
	/**
	 * 元换算成万元，保留到分
	 * @param data
	 * @return
	 */
	public static BigDecimal formatForDivide10000(BigDecimal data) {
		if (null == data)
			return init0;
		else
			return data.divide(NUM10000).setScale(6, BigDecimal.ROUND_HALF_UP);
	}
	
	/** Object对象转化成Bigecimal对象 */
	public static BigDecimal parseFromObject(Object obj) {
		return obj == null ? SysConstant.BIGDECIMAL_defaultValue : new BigDecimal(obj.toString());
	}
	
	/**
	 * 是否大于0
	 * @param amount
	 * @return
	 */
	public static boolean isBigZero(BigDecimal amount) {
		if (null == amount) {
			return false;
		}
		if (BigDecimal.ZERO.compareTo(amount) < 0){
			return true;
		}
		return false;
	}
	
	/**
	 * 截取小数后面几位小数
	 * @param data
	 * @param digit
	 * @return
	 */
	public static BigDecimal intercept(BigDecimal data, int digit) {
		return data == null ? BigDecimal.ZERO : data.setScale(digit, BigDecimal.ROUND_DOWN);
	}
	
	public static BigDecimal parseObj2BigDecimal(Object obj) {
		BigDecimal data = BigDecimal.ZERO;
		if (null == obj) {
			return data;
		}
		try {
			data = (BigDecimal) obj;
		} catch (Exception e) {
			AMPException.getStacktrace(e);
			data = BigDecimal.ZERO;
		}
		return data;
	}
}
