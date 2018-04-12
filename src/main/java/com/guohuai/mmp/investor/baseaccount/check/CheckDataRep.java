package com.guohuai.mmp.investor.baseaccount.check;

import java.math.BigDecimal;

import lombok.EqualsAndHashCode;

@lombok.Data
@EqualsAndHashCode(callSuper = false)
public class CheckDataRep {

	// 已充值金额
	BigDecimal depositOk = BigDecimal.ZERO;
	
	// 已成功提现
	BigDecimal withdrawOk = BigDecimal.ZERO;
    
 	// 活期收益
 	BigDecimal t0Interest = BigDecimal.ZERO;
 	
 	// 定期收益
	BigDecimal tnInterest = BigDecimal.ZERO;
	
	// 卡券红包金额
    BigDecimal couponAmt = BigDecimal.ZERO;
    
    // 资金总额：已充值-已提现+累计收益(定期累计收益+活期累计收益)+红包金额(包括现金好包)
    BigDecimal moneyAmount = BigDecimal.ZERO;
	
    // 前一日资产总额
    BigDecimal yesterdayCapitalAmt = BigDecimal.ZERO;
    
    // 提现可用金额
 	BigDecimal withdrawAvailableBalance = BigDecimal.ZERO;
    
    // 提现申请中
  	BigDecimal withdrawFrozenBalance = BigDecimal.ZERO;
    
	// 活期持有份额
	BigDecimal t0Hold = BigDecimal.ZERO;
	
	// 活期冻结
	BigDecimal t0ToConfirm = BigDecimal.ZERO;
	
	// 定期持有份额
	BigDecimal tnHold = BigDecimal.ZERO;
	
	// 定期冻结
	BigDecimal tnToConfirm = BigDecimal.ZERO;
	
	// 资产总额：可提现余额+申请提现+定期持有+定期申请+活期持有+活期申请+补登累计
	BigDecimal capitalAmount = BigDecimal.ZERO;
}
