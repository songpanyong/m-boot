package com.guohuai.cache;

import com.guohuai.component.util.StringUtil;

public class CacheKeyConstants {
	
	/**
	 * 平台redis key
	 */
	public static final String PLATFORM_CACHE_KEY = "m:platform";
	public static final String getPlatformCacheHkey() {
		return PLATFORM_CACHE_KEY;
	}
	
	/** 投资者前缀 */
	public static final String INVESTOR_CACHK_KEY = "m:i:a:";
	public static String getInvestorHKey(String userOid) {
		return INVESTOR_CACHK_KEY + userOid;
	}
	
	/** 产品的缓存前缀 */
	public static final String PRODUCT_CACHE_KEY = "m:p:";
	public static String getProductHKey(String productOid) {
		return PRODUCT_CACHE_KEY + productOid;
	}
	/** 同步产品标签 */
	public static final String BATCH_NO_syncProductLabel = "syncProductLabel:";
	public static String getSyncProductLabel() {
		return BATCH_NO_syncProductLabel + StringUtil.uuid();
	}
	/** 同步产品最大可售份额 */
	public static final String BATCH_NO_syncProductMaxSaleVolume = "syncProductMaxSaleVolume:";
	public static String getSyncProductMaxSaleVolume() {
		return BATCH_NO_syncProductMaxSaleVolume + StringUtil.uuid();
	}
	/** 产品上架时同步 */
	public static final String BATCH_NO_syncProduct4Upshelf = "syncProduct4Upshelf:";
	public static String getSyncProduct4Upshelf() {
		return BATCH_NO_syncProduct4Upshelf + StringUtil.uuid();
	}
	
	
	
	/** 投资者合仓 缓存前缀 */
	public static final String INVESTOR_PRODUCT_CACHE_KEY = "m:i:p:";
	public static String getHoldHKey(String userOid, String productOid) {
		return INVESTOR_PRODUCT_CACHE_KEY + userOid + ":" + productOid;
	}
	/**
	 * wish plan
	 * @param userOid
	 * @param productOid
	 * @param wishplanOid
	 * @return
	 */
	public static String getHoldHKeyWishplan(String userOid, String productOid, String wishplanOid) {
		return INVESTOR_PRODUCT_CACHE_KEY + userOid + ":" + productOid + ":" + wishplanOid;
	}
	/** 投资者合仓 缓存前缀索引 */
	public static final String INVESTOR_HOLD_INDEX = "m:h:i:";
	public static String getHoldIndexKey(String userOid) {
		return INVESTOR_HOLD_INDEX + userOid;
	}
	
	
	/** 渠道缓存前缀 */
	public static final String CHANNEL_CACHE_KEY = "m:c:";
	public static String getChannelHKey(String cid, String ckey, String productOid) {
		return CacheKeyConstants.CHANNEL_CACHE_KEY + cid + ":" + ckey + ":" + productOid;
	}
	
	
	
	/** spv的缓存前缀 */
	public static final String SPVHOLD_CACHE_KEY = "m:spv:p:";
	public static String getSpvHKey(String productOid) {
		return SPVHOLD_CACHE_KEY + productOid;
	}
	public static final String BATCH_NO_syncSpvHoldTotalVolume = "syncSpvHoldTotalVolume:";
	public static String getSyncSpvHoldTotalVolume() {
		return BATCH_NO_syncSpvHoldTotalVolume + StringUtil.uuid();
	}
	
	
//	/** 体验金--投资*/
//	public static final String BATCH_NO_expGoldInvest = "expGoldInvest:";
//	public static String getExpGoldInvest(String orderCode) {
//		return BATCH_NO_expGoldInvest + orderCode;
//	}
	
	/** 批次号-投资 */
	public static final String BATCH_NO_INVEST = "INVEST:";
	public static final String getInvestBatchNo(String orderCode) {
		return BATCH_NO_INVEST + orderCode;
	}
	public static final String BATCH_NO_INVEST_CB = "INVEST:CB:";
	public static final String getInvestCBBatchNo(String orderCode) {
		return BATCH_NO_INVEST_CB + orderCode;
	}
	
	/** 批次号-投资确认 */
	public static final String BATCH_NO_INVEST_CONFIRM = "INVEST_CONFIRM:";
	public static final String getInvestConfirmBatchNo(String orderCode) {
		return BATCH_NO_INVEST_CONFIRM + orderCode;
	}
	
	/** 批次号-赎回 */
	public static final String BATCH_NO_redeem = "redeem";
	public static final String getRedeem(String orderCode) {
		return BATCH_NO_redeem + orderCode;
	}

	
	/** 批次号--投资单作废 */
	public static final String BATCH_NO_investAbandon = "investAbandon:";
	public static final String getInvestAbandon() {
		return BATCH_NO_investAbandon + StringUtil.uuid(); 
	}
	
	/** 批次号--投资单退款 */
	public static final String BATCH_NO_refund = "refund:";
	public static final String getRefund() {
		return BATCH_NO_refund + StringUtil.uuid(); 
	}
	
	
	/** 批次号-投资赎回 */
	public static final String BATCH_NO_REDEEM_INVEST = "REDEEM_INVEST:";
	/** 批次号-体验金平仓 */
	public static final String BATCH_NO_flatExpGold = "flatExpGold:";
	public static String getFlatExpGoldBatchNo() {
		return BATCH_NO_flatExpGold + StringUtil.uuid();
	}
	
	/** 每日重置 */
	public static final String BATCH_NO_resetToday = "resetToday:";
	public static String getResetTodayBatchNo() {
		return BATCH_NO_resetToday + StringUtil.uuid();
	}
}
