package com.guohuai.mmp.jiajiacai.caculate;

import java.math.BigDecimal;
import java.util.HashMap;

public class JJCUtility {

	public static String keep2Decimal(float f) {
		float r = (float) (Math.round(f * 100) / 100.00);
		return String.format("%.2f", r);
	}

	public static BigDecimal bigKeep2Decimal(BigDecimal input) {
		return new BigDecimal(keep2Decimal(input.floatValue()));
	}
	
	public static String keep4Decimal(float f) {
		float r = (float) (Math.round(f * 10000) / 10000.0000);
		return String.format("%.4f", r);
	}

	public static BigDecimal bigKeep4Decimal(BigDecimal input) {
		return new BigDecimal(keep4Decimal(input.floatValue()));
	}
	/**
	 * 避免28天的2月，避免6年多一月
	 * 
	 * @param days
	 * @return
	 */
	public static int days2month(int days) {
		return (days + 2 - (days / 365) * 5) / 30;
	}

	private static final HashMap<String, String> typeMap = new HashMap<String, String>() {
		private static final long serialVersionUID = 1L;
		{
			put("ONCE_EDU", "助学成长计划");
			put("MONTH_EDU", "助学成长计划");
			put("MONTH_SALARY", "薪增长计划");
			put("ONCE_TOUR", "家庭旅游计划");
			put("MONTH_TOUR", "家庭旅游计划");
		}
	};

	public static String plantype2Str(String key) {
		return typeMap.get(key);
	}

}
