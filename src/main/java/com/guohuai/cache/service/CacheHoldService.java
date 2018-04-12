package com.guohuai.cache.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.guohuai.ams.product.Product;
import com.guohuai.cache.CacheKeyConstants;
import com.guohuai.cache.entity.HoldCacheEntity;
import com.guohuai.cache.entity.ProductCacheEntity;
import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.DecimalUtil;
import com.guohuai.component.util.HashRedisUtil;
import com.guohuai.component.util.StrRedisUtil;
import com.guohuai.component.util.ZsetRedisUtil;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderEntity;
import com.guohuai.mmp.platform.redis.RedisSyncService;
@Service
public class CacheHoldService {
	Logger logger = LoggerFactory.getLogger(CacheHoldService.class);
	@Autowired
	private RedisTemplate<String, String> redis;
	@Autowired
	private CacheProductService cacheProductService;
	@Autowired
	private RedisExecuteLogExtService redisExecuteLogExtService;
	@Autowired
	private RedisSyncService redisSyncService;
	
	/**
	 * 单人单日赎回上限
	 */
	public void redeemDayRules(InvestorTradeOrderEntity orderEntity) {
		BigDecimal orderVolume = orderEntity.getOrderAmount();
		Product product = orderEntity.getProduct();
		String hkey = CacheKeyConstants.getHoldHKey(orderEntity.getInvestorBaseAccount().getOid(), orderEntity.getProduct().getOid());
		
		if (DecimalUtil.isGoRules(product.getSingleDailyMaxRedeem())) {
			BigDecimal valOut = redisExecuteLogExtService.hincrByBigDecimal(hkey, "dayRedeemVolume", orderVolume);
			if (valOut.compareTo(product.getSingleDailyMaxRedeem()) > 0) {
				// error.define[30032]=超过产品单人单日赎回上限(CODE:30032)
				throw AMPException.getException(30032);
			}
		}
		if (DecimalUtil.isGoRules(product.getSingleDayRedeemCount())) {
			Long valOut = redisExecuteLogExtService.hincrByLong(hkey, "dayRedeemCount", 1);
			if (valOut > product.getSingleDayRedeemCount()) {
				throw new AMPException("每日最多只能赎回" + product.getSingleDayRedeemCount() + "次");
			}
		}
		
	}
	/**
	 * 校验所购产品最大持仓
	 */
	public void checkMaxHold4Invest(InvestorTradeOrderEntity orderEntity) {
		if (DecimalUtil.isGoRules(orderEntity.getProduct().getMaxHold())) { // 等于0，表示无限制
			String hkey = CacheKeyConstants.getHoldHKey(orderEntity.getInvestorBaseAccount().getOid(),
					orderEntity.getProduct().getOid());
			
			BigDecimal valOut = null;
			if (StrRedisUtil.exists(redis, hkey)) {
				valOut = redisExecuteLogExtService.hincrByBigDecimal(hkey, "maxHoldVolume", orderEntity.getOrderVolume());
			} else {
				valOut = orderEntity.getOrderVolume();
			}
			
			if (valOut.compareTo(orderEntity.getProduct().getMaxHold()) > 0) {
				redisSyncService.saveEntityRefInvestorHoldRequireNew(orderEntity.getInvestorBaseAccount().getOid(), 
						orderEntity.getProduct().getOid());
				// error.define[30031]=份额已超过所购产品最大持仓(CODE:30031)
				throw new AMPException(30031);
			}
		}
	}
	/**
	 * 赎回锁定
	 */
	public void redeemLock(InvestorTradeOrderEntity orderEntity) {
		BigDecimal orderVolume = orderEntity.getOrderAmount();
		String hkey = null;
		if (orderEntity.getWishplanOid() == null) {
			hkey = CacheKeyConstants.getHoldHKey(orderEntity.getInvestorBaseAccount().getOid(),
					orderEntity.getProduct().getOid());
		} else {
			hkey = CacheKeyConstants.getHoldHKeyWishplan(orderEntity.getInvestorBaseAccount().getOid(),
					orderEntity.getProduct().getOid(), orderEntity.getWishplanOid());
		}
	
		BigDecimal valOut = redisExecuteLogExtService.hincrByBigDecimal(hkey, "redeemableHoldVolume", orderVolume.negate());
		if (null == valOut || valOut.compareTo(BigDecimal.ZERO) < 0) {
			// error.define[20004]=赎回锁定份额异常(CODE:20004)
			throw AMPException.getException(20004);
		}
			
		redisExecuteLogExtService.hincrByBigDecimal(hkey, "toConfirmRedeemVolume", orderVolume);
	}

	
	
	
	/**
	 * 产品赎回份额张约束
	 * 查看总持仓份额是否等于可赎回份额,并且是全部赎回
	 */
	public void update4MinRedeem(InvestorTradeOrderEntity orderEntity) {
		if (InvestorTradeOrderEntity.TRADEORDER_orderType_normalRedeem.equals(orderEntity.getOrderType())) {
			BigDecimal orderVolume = orderEntity.getOrderAmount();
			ProductCacheEntity product = cacheProductService.getProductCacheEntityById(orderEntity.getProduct().getOid());
			HoldCacheEntity holdCacheEntity = getHoldCacheEntityByUidAndProductId(
					orderEntity.getInvestorBaseAccount().getOid(), orderEntity.getProduct().getOid());
			
			boolean isRedeemAllFlag = false;
			if (DecimalUtil.isGoRules(product.getMinRredeem())) {// 如果是全部赎回
				if (holdCacheEntity.getRedeemableHoldVolume().compareTo(orderVolume) == 0) {
					isRedeemAllFlag = true;
				}
			}
			if (!isRedeemAllFlag) {
				if (DecimalUtil.isGoRules(product.getMinRredeem())) {
					if (orderVolume.compareTo(product.getMinRredeem()) < 0) {
						// error.define[30013]=不满足单笔赎回下限(CODE:30013)
						throw new AMPException(30013);
					}
				}

				if (DecimalUtil.isGoRules(product.getAdditionalRredeem())) {
					if (DecimalUtil.isGoRules(product.getMinRredeem())) {
						if (orderVolume.subtract(product.getMinRredeem()).remainder(product.getAdditionalRredeem())
								.compareTo(BigDecimal.ZERO) != 0) {
							// error.define[30039]=不满足赎回追加份额(CODE:30039)
							throw new AMPException(30039);
						}
					} else {
						if (orderVolume.remainder(product.getAdditionalRredeem()).compareTo(BigDecimal.ZERO) != 0) {
							// error.define[30039]=不满足赎回追加份额(CODE:30039)
							throw new AMPException(30039);
						}
					}
				}
			}
		}
	}

	public HoldCacheEntity getHoldCacheEntityByUidAndProductId(String uid, String productId) {
		Map<String, String> mapHold = HashRedisUtil.hgetall(redis, CacheKeyConstants.getHoldHKey(uid, productId));
		if (mapHold.isEmpty()) {
			return null;
		}
		HoldCacheEntity hold = JSONObject.parseObject(JSONObject.toJSONString(mapHold), HoldCacheEntity.class);
		logger.info("HoldCacheEntity.zoomIn.key={}{}:{}", CacheKeyConstants.INVESTOR_PRODUCT_CACHE_KEY, uid, productId);
		DecimalUtil.zoomIn(hold);
		return hold;
	}

	/**
	 * 查询用户仓位
	 */
	public List<HoldCacheEntity> findByInvestorOid(String investorOid) {
		String zkey = CacheKeyConstants.getHoldIndexKey(investorOid);
		List<String> list = ZsetRedisUtil.zRange(redis, zkey, 0, -1);
		
		List<HoldCacheEntity>  returnList=new ArrayList<HoldCacheEntity>();
		HoldCacheEntity hold = null;
		for (String productOid : list) {
			
			hold = getHoldCacheEntityByUidAndProductId(investorOid, productOid);
			if (null != hold) {
				returnList.add(hold);
			}
		}
		return returnList;
	}
	
//	/**
//	 * 根据用户查询所有活期产品合仓
//	 */
//	public List<HoldCacheEntity> findByUserOid(String userOid) {
//		String zkey = CacheKeyConstants.getHoldIndexKey(userOid);
//		List<String> list = ZsetRedisUtil.zRange(redis, zkey, 0, -1);
//		
//		List<HoldCacheEntity>  returnList=new ArrayList<HoldCacheEntity>();
//		HoldCacheEntity hold = null;
//		for (String productOid : list) {
//			
//			hold = getHoldCacheEntityByUidAndProductId(userOid, productOid);
//			returnList.add(hold);
//		}
//		return returnList;
//	}
	
//	/**
//	 * 根据用户查询所有定期产品合仓
//	 */
//	public List<HoldCacheEntity> findTnByUserOid(String userOid) {
//		String zkey = CacheKeyConstants.getHoldTnIndexKey(userOid);
//		List<String> list = ZsetRedisUtil.zRange(redis, zkey, 0, -1);
//		
//		List<HoldCacheEntity>  returnList=new ArrayList<HoldCacheEntity>();
//		HoldCacheEntity hold = null;
//		for (String productOid : list) {
//			hold = getHoldCacheEntityByUidAndProductId(userOid, productOid);
//			returnList.add(hold);
//		}
//		return returnList;
//	}
	
	
	
}
