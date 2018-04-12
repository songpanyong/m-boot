package com.guohuai.mmp.investor.baseaccount;

import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.fastjson.JSONObject;

public class InvestorBaseAccountPayPwdRedisUtil {

private static final Charset UTF8CHARSET=Charset.forName("utf8");
	
//交易密码在redis中的键为字符串常量和用户id的拼接
	public static final String USER_PAYPWD_REDIS_KEY = "p:a:y:pwd:";
	
	private static final Logger logger = LoggerFactory.getLogger(InvestorBaseAccountPayPwdRedisUtil.class);
	//redis的set方法进行保存
	public static InvestorBaseAccountRedisInfo set(RedisTemplate<String,String> redis,final String key,final InvestorBaseAccountRedisInfo value){
		final String json = JSONObject.toJSONString(value);
		return redis.execute(new RedisCallback<InvestorBaseAccountRedisInfo>(){

			@Override
			public InvestorBaseAccountRedisInfo doInRedis(RedisConnection connection) throws DataAccessException {
				// TODO Auto-generated method stub
				logger.info("user.redis={},val={}", key, json);
				connection.set((USER_PAYPWD_REDIS_KEY + key).getBytes(UTF8CHARSET), json.getBytes(UTF8CHARSET));
				return value;
			}
			
		});
	}
	//redis的get方法进行获取,这里的Key为用户的id
	public static InvestorBaseAccountRedisInfo get(RedisTemplate<String,String> redis,final String key){
		return redis.execute(new RedisCallback<InvestorBaseAccountRedisInfo>() {
			@Override
			public InvestorBaseAccountRedisInfo doInRedis(RedisConnection connection)
					throws DataAccessException {
				byte [] data = connection.get((USER_PAYPWD_REDIS_KEY + key).getBytes(UTF8CHARSET));
				if (data == null) {
					return null;
				}
				return JSONObject.parseObject(new String(data, UTF8CHARSET), InvestorBaseAccountRedisInfo.class);
			}
		});
	}
	
}
