package com.guohuai.mmp.investor.baseaccount.check;

import java.math.BigDecimal;

import com.guohuai.basic.component.ext.web.BaseResp;

import lombok.EqualsAndHashCode;
@lombok.Data
@EqualsAndHashCode(callSuper = false)
@lombok.Builder
public class CheckQueryRep extends BaseResp {

	String oid, investOid, checkTime, phone, checkStatus, userStatus;

	/** 资金总额 */
	BigDecimal moneyAmount = BigDecimal.ZERO;
	
	/** 资产总额 */
	BigDecimal capitalAmount = BigDecimal.ZERO;
	
	/** 已充值 */
	BigDecimal recharge = BigDecimal.ZERO;
	
	/** 已成功提现 */
	BigDecimal withdraw = BigDecimal.ZERO;
	
	/** 定期收益 */
	BigDecimal tnInterest = BigDecimal.ZERO;
	
	/** 活期收益 */
	BigDecimal t0Interest = BigDecimal.ZERO;
	
	/** 卡券红包金额 */
	BigDecimal couponAmt = BigDecimal.ZERO;
	
	/** 前一日资产总额 */
	BigDecimal yesterdayCapitalAmt = BigDecimal.ZERO;
	
	/** 可提现余额 */
	BigDecimal balance = BigDecimal.ZERO;
	
	/** 提现申请中金额 */
	BigDecimal applyBalance = BigDecimal.ZERO;
	
	/** 活期申购冻结 */
	BigDecimal t0ApplyAmt = BigDecimal.ZERO;
	
	/** 活期持有 */
	BigDecimal t0HoldAmt = BigDecimal.ZERO;
	
	/** 定期申购冻结 */
	BigDecimal tnApplyAmt = BigDecimal.ZERO;
	
	/** 定期持有 */
	BigDecimal tnHoldlAmt = BigDecimal.ZERO;
	
}
