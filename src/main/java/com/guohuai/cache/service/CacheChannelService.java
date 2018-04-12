package com.guohuai.cache.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.guohuai.ams.product.productChannel.ProductChannel;
import com.guohuai.cache.CacheKeyConstants;
import com.guohuai.cache.entity.ChannelCacheEntity;
import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.HashRedisUtil;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderEntity;

//import lombok.extern.slf4j.Slf4j;

@Service
//@Slf4j
public class CacheChannelService {
	
	@Autowired
	private RedisTemplate<String, String> redis;

	/**
	 * 校验渠道
	 */
	public void checkChannel(InvestorTradeOrderEntity orderEntity) {
		if (InvestorTradeOrderEntity.TRADEORDER_orderType_invest.equals(orderEntity.getOrderType())) {
			ChannelCacheEntity cache = this.getChannel(orderEntity.getChannel().getCid(), orderEntity.getChannel().getCkey(), orderEntity.getProduct().getOid());
			if (!ProductChannel.MARKET_STATE_Onshelf.equals(cache.getMarketState()) || !ProductChannel.STATUS_VALID.equals(cache.getStatus())) {
				throw new AMPException("该产品在该渠道尚未发行或发行已被退回");
			}
		}
	}

	public ChannelCacheEntity getChannel(String cid, String ckey, String productOid) {
		String hkey = CacheKeyConstants.getChannelHKey(cid, ckey, productOid);
		return HashRedisUtil.hgetall(redis, hkey, ChannelCacheEntity.class);
	}

	
}
