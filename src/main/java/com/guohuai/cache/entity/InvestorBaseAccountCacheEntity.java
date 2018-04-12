package com.guohuai.cache.entity;

import java.math.BigDecimal;

@lombok.Data
public class InvestorBaseAccountCacheEntity {
	
	public static String[] zoomArr = new String[] { "balance", "withdrawFrozenBalance", "rechargeFrozenBalance", "applyAvailableBalance", "withdrawAvailableBalance"};
	
	/**
	 * 用户OID
	 */
	private String userOid;
	
	/**
	 * 新手否
	 */
	private String isFreshman;
	
	
	/**
	 * 余额
	 */
	private BigDecimal balance;
	
	/**
	 * 提现冻结
	 */
	private BigDecimal withdrawFrozenBalance;
	
	/**
	 * 充值冻结
	 */
	private BigDecimal rechargeFrozenBalance;
	
	/**
	 * 申购冻结
	 */
	private BigDecimal applyAvailableBalance;
	
	/**
	 * 提现可用
	 */
	private BigDecimal withdrawAvailableBalance;
	
	/**
	 * 月提现次数
	 */
	private Integer monthWithdrawCount;
	
	/**
	 * 状态
	 */
	private String status;

}
