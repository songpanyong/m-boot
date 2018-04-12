package com.guohuai.cache.service;

import java.math.BigDecimal;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.guohuai.ams.product.Product;
import com.guohuai.basic.common.StringUtil;
import com.guohuai.cache.CacheKeyConstants;
import com.guohuai.cache.entity.SPVHoldCacheEntity;
import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.DecimalUtil;
import com.guohuai.component.util.HashRedisUtil;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderEntity;
import com.guohuai.mmp.platform.redis.RedisSyncService;
import com.guohuai.mmp.publisher.hold.PublisherHoldEntity;

@Service
public class CacheSPVHoldService {
	
	Logger logger = LoggerFactory.getLogger(CacheSPVHoldService.class);
	@Autowired
	private RedisTemplate<String, String> redis;
	@Autowired
	private RedisExecuteLogExtService redisExecuteLogExtService;
	@Autowired
	private RedisSyncService redisSyncService;

	/**
	 * 校验SPV持仓
	 */
	public void checkSpvHold4Invest(InvestorTradeOrderEntity orderEntity) {
		BigDecimal orderVolume = orderEntity.getOrderAmount();

		BigDecimal valOut = redisExecuteLogExtService.hincrByBigDecimal(
				CacheKeyConstants.getSpvHKey(orderEntity.getProduct().getOid()), "lockRedeemHoldVolume", orderVolume);
		SPVHoldCacheEntity spvHold = getSPVHoldCacheEntityByProductId(orderEntity.getProduct().getOid());
		if (spvHold.getTotalVolume().compareTo(valOut) < 0) {
			redisSyncService.saveEntityRefSpvHoldRequireNew(orderEntity.getInvestorBaseAccount().getOid(), 
					orderEntity.getProduct().getOid(), 
					orderEntity.getProduct().getPortfolio().getOid());
			// error.define[30069]=SPV可售份额不足(CODE:30069)
			throw new AMPException(30069);
		}
	}

	/**
	 * 根据产品Oik获取SPV持仓
	 * 
	 * @param productOid
	 * @return
	 */
	private SPVHoldCacheEntity getSPVHoldCacheEntityByProductId(String productOid) {
		Map<String, String> mapSPV = HashRedisUtil.hgetall(redis, CacheKeyConstants.SPVHOLD_CACHE_KEY + productOid);
		SPVHoldCacheEntity svpHold = JSONObject.parseObject(JSONObject.toJSONString(mapSPV), SPVHoldCacheEntity.class);
		logger.info("SPVHoldCacheEntity.zoomIn.key={}{}:{}", CacheKeyConstants.SPVHOLD_CACHE_KEY, productOid);
		DecimalUtil.zoomIn(svpHold);
		return svpHold;
	}



	
	
}
