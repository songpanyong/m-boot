package com.guohuai.cache.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.guohuai.component.util.DecimalUtil;
import com.guohuai.component.util.GHUtil;
import com.guohuai.component.util.HashRedisUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RedisExecuteLogExtService {
	@Autowired
	private RedisTemplate<String, String> redis;
	
	
	
	/**
	 * redis执行HINCR命令
	 * @param command
	 * @param batchNo
	 * @param hkey
	 * @param key
	 * @param value
	 * @param backValue
	 */
	public BigDecimal hincrByBigDecimal(String hkey, String key, BigDecimal value) {
		Long valueIn = 0L;
//		Long backValueIn = 0L;
		if (value instanceof BigDecimal) {
			value = DecimalUtil.zoomOut((BigDecimal)value);
			valueIn = ((BigDecimal) value).longValue();
		} 
		

//		RedisExecuteLogEntity entity = saveRedisExecuteLogEntity(RedisExecuteLogEntity.HINCRBY, batchNo, hkey, key,
//				valueIn.toString(), backValueIn.toString());
//		this.redisExecuteLogService.save(entity);
		log.info("==command:{} {} {} {}", "hincry", hkey, key, valueIn);
		BigDecimal valOut = HashRedisUtil.hincr(redis, hkey, key, valueIn);
		log.info("==valOut:{}", valOut);
//		entity.setExecuteSuccessStatus(RedisExecuteLogEntity.EXECUTE_STATUS_SUCCESS);
//		this.redisExecuteLogService.save(entity);
		valOut = DecimalUtil.zoomIn(valOut);
		return valOut;
	}
	
	
	
	public Long hincrByLong(String hkey, String key, Object value) {
		Integer valueIn = 0;
//		Integer backValueIn = 0;

		valueIn = ((Integer) value).intValue();

//		backValueIn = ((Integer) backValue).intValue();
//		RedisExecuteLogEntity entity = saveRedisExecuteLogEntity(RedisExecuteLogEntity.HINCRBY, batchNo, hkey, key,
//				valueIn.toString(), backValueIn.toString());
//		this.redisExecuteLogService.save(entity);
		log.info("==command:{} {} {} {}", "hincry", hkey, key, valueIn);
		Long valOut = HashRedisUtil.hincrByLong(redis, hkey, key, valueIn);
		log.info("==valOut:{}", valOut);
//		entity.setExecuteSuccessStatus(RedisExecuteLogEntity.EXECUTE_STATUS_SUCCESS);
//		this.redisExecuteLogService.save(entity);
		
		return valOut;
	}
	
	

	/**
	 *  redis执行HMSET命令
	 */
	public void redisExecuteHMSET(String batchNo, String hkey, Object obj) {
		DecimalUtil.zoomOut(obj);
		
		log.info("==command:{} {} {} ", "hmset", batchNo, hkey);
		HashRedisUtil.hmset(redis, hkey, GHUtil.obj2Map(obj));
		
	}
	
	
	/**
	 * redis执行HSET命令
	 * @param command
	 * @param batchNo
	 * @param hkey
	 * @param key
	 * @param value
	 * @param backValue
	 */
	public void redisExecuteHSET(String hkey, String key, Object value) {
//		RedisExecuteLogEntity redisLogEntity = saveRedisExecuteLogEntity(RedisExecuteLogEntity.HSET, batchNo, hkey, key,
//				value.toString(), backValue.toString());
//		this.redisExecuteLogService.save(redisLogEntity);
		HashRedisUtil.hset(redis, hkey, key, value.toString());
		log.info("==command:{} {} {} {}", "hset", hkey, key, value.toString());
//		redisLogEntity.setExecuteSuccessStatus(RedisExecuteLogEntity.EXECUTE_STATUS_SUCCESS);
//		this.redisExecuteLogService.save(redisLogEntity);
	}
	
	
	

	
}
