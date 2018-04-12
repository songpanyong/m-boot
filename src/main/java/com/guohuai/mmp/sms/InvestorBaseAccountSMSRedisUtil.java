package com.guohuai.mmp.sms;

import java.nio.charset.Charset;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.fastjson.JSONObject;

public class InvestorBaseAccountSMSRedisUtil {

	private static final Charset UTF8CHARSET=Charset.forName("utf8");
	
	//用户在redis中的保存方式为key和用户手机号的拼接
	public static  final  String USER_SMS_REDIS_KEY= "s:m:s:ver";
	
	//设置set方法，将数据保存在redis服务器中
	public static InvestorSMSMessageRedisInfo set(RedisTemplate<String,String>redis,final String key,InvestorSMSMessageRedisInfo value){
		//以json格式的将对象进行保存
		final String json = JSONObject.toJSONString(value);
		return redis.execute(new RedisCallback<InvestorSMSMessageRedisInfo>() {

			@Override
			public InvestorSMSMessageRedisInfo doInRedis(RedisConnection connection) throws DataAccessException {
				connection.set((USER_SMS_REDIS_KEY+key).getBytes(UTF8CHARSET),json.getBytes(UTF8CHARSET));
				return value;
			}
		});
	}
	
	//设置get方法，用于从redis中获取值
	public static InvestorSMSMessageRedisInfo get(RedisTemplate<String,String> redis,final String key){
		
		return redis.execute(new RedisCallback<InvestorSMSMessageRedisInfo>(){

			@Override
			public InvestorSMSMessageRedisInfo doInRedis(RedisConnection connection) throws DataAccessException {
				byte[] b = connection.get((USER_SMS_REDIS_KEY+key).getBytes(UTF8CHARSET));
				if(b==null){
					return null;
				}
				//将字节型的转化为对象格式的
				return JSONObject.parseObject(new String(b,UTF8CHARSET),InvestorSMSMessageRedisInfo.class );
			}});
	}
}
