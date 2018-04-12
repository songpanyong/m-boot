package com.guohuai.mmp.publisher.hold;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@lombok.Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class ToConfirmT0Detail {
	

	/** 
	 * 产品名称 
	 */
	private String productName;
	
	
	/**
	 * 投资金额
	 */
	private BigDecimal toConfirmInvestVolume;
	
	/**
	 * 最近一次购买的时间
	 * */
	private Timestamp lastOrderTime;
	
	/**  订单号 */
	private String orderCode;
	
	
}
