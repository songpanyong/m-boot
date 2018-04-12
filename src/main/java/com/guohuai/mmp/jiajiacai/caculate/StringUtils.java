package com.guohuai.mmp.jiajiacai.caculate;

import java.util.UUID;

public class StringUtils {
	
	public final static String uuid() {
		String uuid = UUID.randomUUID().toString().replace("-", "");
		return uuid;
	}
	
}
