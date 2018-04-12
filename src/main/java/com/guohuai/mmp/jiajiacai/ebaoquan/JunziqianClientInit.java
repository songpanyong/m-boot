package com.guohuai.mmp.jiajiacai.ebaoquan;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Maps;
import com.junziqian.api.JunziqianClient;

//import rop.thirdparty.com.google.common.collect.Maps;
//import rop.thirdparty.org.apache.commons.lang3.StringUtils;

public class JunziqianClientInit {

	// 请填入服务地址（根据环境的不同选择不同的服务地址），沙箱环境，正式环境
	public final static String SERVICE_URL;

	// 请填入你的APPKey
	public final static String APP_KEY;

	// 请填入你的APPSecret
	public final static String APP_SECRET;

	protected static Map<String, String> props = Maps.newHashMap();

	private static JunziqianClient client;
	

	static {

//		String filePath = "ebaoquan.properties";
		InputStream reader;
		try {
			reader = JunziqianClientInit.class.getClassLoader().getResourceAsStream("ebaoquan.properties");
//			reader = new InputStreamReader(new FileInputStream(new File(filePath)));
			Properties properties = new Properties();
			properties.load(reader);
			Enumeration<?> it = properties.propertyNames();
			while (it.hasMoreElements()) {
				String propName = (String) it.nextElement();
				props.put(propName, properties.getProperty(propName));
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		SERVICE_URL = props.get("services_url");
		APP_KEY = props.get("app_key");
		APP_SECRET = props.get("app_secret");
		if (StringUtils.isBlank(SERVICE_URL)) {
			throw new RuntimeException("SERVICE_URL is null");
		}
		if (StringUtils.isBlank(APP_KEY)) {
			throw new RuntimeException("APP_KEY is null");
		}
		if (StringUtils.isBlank(APP_SECRET)) {
			throw new RuntimeException("APP_SECRET is null");
		}
		client = new JunziqianClient(SERVICE_URL, APP_KEY, APP_SECRET);
		
	}
	
	public static JunziqianClient getClient() {
		return client;
	}
}
