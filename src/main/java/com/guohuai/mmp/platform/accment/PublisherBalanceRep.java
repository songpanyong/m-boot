package com.guohuai.mmp.platform.accment;

import java.math.BigDecimal;

import com.guohuai.basic.component.ext.web.BaseResp;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class PublisherBalanceRep extends BaseResp {

	
	/**
	 * 余额 
	 */
	private BigDecimal basicBalance = BigDecimal.ZERO;
	
	/**
	 * 归集清算户余额
	 */
	private BigDecimal collectionSettlementBalance = BigDecimal.ZERO;
	
	/**
	 * 可用金户余额
	 */
	private BigDecimal availableAmountBalance = BigDecimal.ZERO;
	
	/**
	 * 冻结户余额
	 */
	private BigDecimal frozenAmountBalance = BigDecimal.ZERO;
	
	/**
	 * 提现可用金余额
	 */
	private BigDecimal withdrawAvailableAmountBalance = BigDecimal.ZERO;
}
