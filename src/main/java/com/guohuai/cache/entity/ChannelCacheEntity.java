package com.guohuai.cache.entity;

@lombok.Data
@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
@lombok.Builder
public class ChannelCacheEntity {
	public static String[] zoomArr = new String[] {};
	/** 渠道Oid */
	private String channelOid;
	/** 产品Id */
	private String productOid;

	/**
	 * 渠道--产品状态
	 */
	private String status;

	/**
	 * 渠道--产品上下架状态
	 */
	private String marketState;

	/** cid */
	private String cid;
	/** ckey */
	private String ckey;
	/** 渠道名称 */
	private String channelName;

}
