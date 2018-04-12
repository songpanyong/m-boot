
package com.guohuai.cache.service;

import java.math.BigDecimal;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.guohuai.ams.label.LabelEnum;
import com.guohuai.ams.product.Product;
import com.guohuai.cache.CacheKeyConstants;
import com.guohuai.cache.entity.ProductCacheEntity;
import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.DateUtil;
import com.guohuai.component.util.DecimalUtil;
import com.guohuai.component.util.HashRedisUtil;
import com.guohuai.component.util.StringUtil;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderEntity;
import com.guohuai.mmp.platform.redis.RedisSyncService;

@Service
public class CacheProductService {
	Logger logger = LoggerFactory.getLogger(CacheProductService.class);
	@Autowired
	private RedisTemplate<String, String> redis;
	@Autowired
	private RedisExecuteLogExtService redisExecuteLogExtService;
	@Autowired
	private RedisSyncService redisSyncService;
	
	
	/**
	 * 投资校验产品
	 * @param tradeOrderReq
	 */
	public void checkProduct4Invest(InvestorTradeOrderEntity orderEntity){
		ProductCacheEntity product = getProductCacheEntityById(orderEntity.getProduct().getOid());
		if (Product.NO.equals(product.getIsOpenPurchase())) {
			// error.define[30020]=申购开关已关闭(CODE:30020)
			throw new AMPException(30020);
		}
		
		if (Product.TYPE_Producttype_01.equals(product.getType())) {
			if (!Product.STATE_Raising.equals(product.getState())) {
				// error.define[30017]=定期产品非募集期不能投资(CODE:30017)
				throw new AMPException(30017);
			}
		}
		
		if (Product.TYPE_Producttype_02.equals(product.getType())) {
			if (!Product.STATE_Durationing.equals(product.getState())) {
				// error.define[30055]=活期产品非存续期不能投资(CODE:30055)
				throw new AMPException(30055);
			}
			String productLabels = product.getProductLabel();
			if (InvestorTradeOrderEntity.TRADEORDER_orderType_expGoldInvest.equals(orderEntity.getOrderType())) {
				if (!StringUtil.isEmpty(productLabels) && !productLabels.contains(LabelEnum.tiyanjin.toString())) {
					throw new AMPException("非体验金产品不能投资");
				}
			} else {
				if (!StringUtil.isEmpty(productLabels) && productLabels.contains(LabelEnum.tiyanjin.toString())) {
					// error.define[30072]=体验金产品不能投资(CODE:30072)
					throw new AMPException(30072);
				}
			}
		}

		// 投资份额需要大于0
		if (orderEntity.getOrderAmount().compareTo(BigDecimal.ZERO) <= 0) {
			// error.define[30040]=金额不能小于等于0(CODE:30040)
			throw new AMPException(30040);
		}

		if (DecimalUtil.isGoRules(product.getInvestMin())) {
			if (orderEntity.getOrderAmount().compareTo(product.getInvestMin()) < 0) {
				// error.define[30008]=不能小于产品投资最低金额(CODE:30008)
				throw new AMPException(30008);
			}
		}
		if (DecimalUtil.isGoRules(product.getInvestMax())) {
			if (orderEntity.getOrderAmount().compareTo(product.getInvestMax()) > 0) {
				// error.define[30009]=已超过产品投资最高金额(CODE:30009)
				throw new AMPException(30009);
			}
		}
		if (DecimalUtil.isGoRules(product.getInvestAdditional())) {
			if (DecimalUtil.isGoRules(product.getInvestMin())) {
				if (orderEntity.getOrderAmount().subtract(product.getInvestMin()).remainder(product.getInvestAdditional()).compareTo(BigDecimal.ZERO) != 0) {
					// error.define[30010]=不满足产品投资追加金额(CODE:30010)
					throw new AMPException(30010);
				}
			} else {
				if (orderEntity.getOrderAmount().remainder(product.getInvestAdditional()).compareTo(BigDecimal.ZERO) != 0) {
					// error.define[30010]=不满足产品投资追加金额(CODE:30010)
					throw new AMPException(30010);
				}
			}
		}
	}
	
	/**
	 * 赎回校验产品
	 */
	public void checkProduct4Redeem(InvestorTradeOrderEntity orderEntity) {
		if (InvestorTradeOrderEntity.TRADEORDER_orderType_normalRedeem.equals(orderEntity.getOrderType())) {
			ProductCacheEntity product = getProductCacheEntityById(orderEntity.getProduct().getOid());
			
			if (Product.TYPE_Producttype_01.equals(product.getProductOid())) {
				// error.define[30060]=非活期产品不能赎回(CODE:30060)
				throw new AMPException(30060);
			}
			
			// 投资份额需要大于0
			if (orderEntity.getOrderVolume().compareTo(BigDecimal.ZERO) <= 0) {
				// error.define[30040]=份额不能小于等于0(CODE:30040)
				throw new AMPException(30040);
			}

			if (DecimalUtil.isGoRules(product.getMaxRredeem())) {
				if (orderEntity.getOrderVolume().compareTo(product.getMaxRredeem()) > 0) {
					// error.define[30038]=不满足赎回最高份额条件(CODE:30038)
					throw new AMPException(30038);
				}
			}
			
			if (Product.NO.equals(product.getIsOpenRemeed())) {
				// error.define[30021]=赎回开关已关闭(CODE:30021)
				throw new AMPException(30021);
			}
			
			if (Product.NO.equals(product.getIsOpenRedeemConfirm())) {
				// error.define[30033]=屏蔽赎回确认处于打开状态(CODE:30033)
				throw new AMPException(30033);
			}
		}
		
	}
	/**
	 * 锁定产品可售份额
	 */
	public void updateProduct4LockCollectedVolume(InvestorTradeOrderEntity orderEntity) {
		BigDecimal orderVolume = orderEntity.getOrderAmount();
		ProductCacheEntity product = getProductCacheEntityById(orderEntity.getProduct().getOid());

		BigDecimal valOut = redisExecuteLogExtService.hincrByBigDecimal(CacheKeyConstants.getProductHKey(orderEntity.getProduct().getOid()), 
				"lockCollectedVolume", orderVolume);
		if (null == product.getMaxSaleVolume() || product.getMaxSaleVolume().subtract(valOut).compareTo(BigDecimal.ZERO) < 0) {
			redisSyncService.saveEntityRefProductRequireNew(orderEntity.getInvestorBaseAccount().getOid(), 
					orderEntity.getProduct().getOid(), 
					orderEntity.getProduct().getPortfolio().getOid());
			// error.define[30011]=产品可投金额不足(CODE:30011)
			throw new AMPException(30011);

		}
	}
	
	/**
	 * 
	 * @param orderVolume
	 * @param productOid
	 * @return
	 */
	public boolean checkProductVolume(BigDecimal orderVolume, String productOid) {
		ProductCacheEntity product = getProductCacheEntityById(productOid);
		if (null == product.getMaxSaleVolume()
				|| product.getMaxSaleVolume().subtract(product.getLockCollectedVolume()).compareTo(orderVolume) < 0) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * 根据产品Oid从redis中获取产品
	 */
	public ProductCacheEntity getProductCacheEntityById(String productOid) {
		Map<String, String> mapProduct = HashRedisUtil.hgetall(redis, CacheKeyConstants.getProductHKey(productOid));
		ProductCacheEntity product = JSONObject.parseObject(JSONObject.toJSONString(mapProduct),
				ProductCacheEntity.class);
		logger.info("ProductCacheEntity.zoomIn.key={}{}", CacheKeyConstants.PRODUCT_CACHE_KEY, productOid);
		DecimalUtil.zoomIn(product);
		return product;
	}
	
	/**
	 * 产品单日净赎回上限 
	 */
	public void update4Redeem(InvestorTradeOrderEntity orderEntity) {
		
		if (InvestorTradeOrderEntity.TRADEORDER_orderType_normalRedeem.equals(orderEntity.getOrderType())) {
			String hkey = CacheKeyConstants.getProductHKey(orderEntity.getProduct().getOid());
			
			// 产品单日赎回上限为零或null，则表示无上限
			if (DecimalUtil.isGoRules(orderEntity.getProduct().getNetMaxRredeemDay())) {
				
				BigDecimal valOut = redisExecuteLogExtService.hincrByBigDecimal(hkey,
						"dailyNetMaxRredeem", orderEntity.getOrderVolume().negate());
				if (valOut.compareTo(BigDecimal.ZERO) < 0) {
					// error.define[30014]=赎回超出产品单日净赎回上限(CODE:30014)
					throw new AMPException(30014);
				}
			}
			
			if (Product.YES.equals(orderEntity.getProduct().getIsPreviousCurVolume())) {
				BigDecimal valOut = redisExecuteLogExtService.hincrByBigDecimal(hkey,
						"previousCurVolume", orderEntity.getOrderVolume().negate());
				if (valOut.compareTo(BigDecimal.ZERO) < 0) {
					throw new AMPException("赎回超出上一个交易日产品规模(基于占比算)");
				}
			}
		}
		
	}

	/** 
	 * 校验产品交易时间 
	 */
	public void isInDealTime(String productOid) {
		ProductCacheEntity product = getProductCacheEntityById(productOid);
		// 交易时间

		if (!"000000".equals(product.getDealStartTime()) && DateUtil.isLessThanDealTime(product.getDealStartTime())) {
			// error.define[30048]=非交易时间不接收订单(CODE:30048)
			throw AMPException.getException(30048);
		}

		if (!"000000".equals(product.getDealEndTime()) && DateUtil.isGreatThanDealTime(product.getDealEndTime())) {
			// error.define[30048]=非交易时间不接收订单(CODE:30048)
			throw AMPException.getException(30048);
		}
	}
	
	
	
	
	/**
	 * 定期产品期号编码规则：定期产品名+期数
    定期产品名称：根据系统定义定期产品名称；
    期数：以200个用户为一期（该定期产品下，涵盖全部期数用户是不能重复的），期号用数字编码自动顺序，格式0000。如果期号大于9999，请自动增加期号。
如，定期产品定投宝，期数为第三期；
那么，期号应该展示为“定投宝0003”。
	 * @param product
	 * @return
	 */
	public String getProductAlias(Product product) {

		Long productAliasCounter = HashRedisUtil.hincrByLong(redis, CacheKeyConstants.getProductHKey(product.getOid()),
				"productAliasCounter", 1);
		Long productAlias;
		if (((productAliasCounter - 1) % 200) == 0) {
			productAlias = HashRedisUtil.hincrByLong(redis, CacheKeyConstants.getProductHKey(product.getOid()),
					"productAlias", 1);
		} else {
			productAlias = Long
					.parseLong(HashRedisUtil.hget(redis, CacheKeyConstants.getProductHKey(product.getOid()), "productAlias"));
		}
		String tmp = String.valueOf(productAlias);
		while (tmp.length() < 3) {
			tmp = "0" + tmp;
		}
		return product.getName() + tmp;
	}
}
