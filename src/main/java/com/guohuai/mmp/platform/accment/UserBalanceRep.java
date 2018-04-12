package com.guohuai.mmp.platform.accment;

import java.math.BigDecimal;

import com.guohuai.basic.component.ext.web.BaseResp;

import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = false)
public class UserBalanceRep extends BaseResp {
	
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
	 * wishplan product redeem balance
	 */
	private BigDecimal redeemBalance;

}
