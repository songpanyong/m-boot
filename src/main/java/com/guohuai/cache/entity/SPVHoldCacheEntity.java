package com.guohuai.cache.entity;

import java.math.BigDecimal;

@lombok.Data
@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
@lombok.Builder
public class SPVHoldCacheEntity {

	public static String[] zoomArr = new String[] {"lockRedeemHoldVolume",
			"totalVolume" };

	/** 所属理财产品 */
	String productOid;
	/** 锁定可赎回份额 */
	BigDecimal lockRedeemHoldVolume;
	/** 总份额 */
	BigDecimal totalVolume;
}
