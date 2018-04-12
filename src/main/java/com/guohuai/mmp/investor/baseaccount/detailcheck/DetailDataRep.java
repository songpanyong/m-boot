package com.guohuai.mmp.investor.baseaccount.detailcheck;

import java.math.BigDecimal;

import lombok.EqualsAndHashCode;

@lombok.Data
@EqualsAndHashCode(callSuper = false)
public class DetailDataRep {

	// 累计充值 - 累计提现 - 提现在途中 - 累计申购(不包含卡券) - 申购在途中(不包含卡券) + 赎回 + 卡券 + 现金红包

	private BigDecimal depositAmount, withdrawAmount, withdrawOnWayAmount, totalInvestAmount, totalOnWayInvest,
			totalRedeemAmount, couponAmount, redEnvelope, balance;


}
