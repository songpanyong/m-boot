package com.guohuai.mmp.investor.tradeorder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.basic.component.ext.web.PageResp;
import com.guohuai.mmp.sys.SysConstant;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@lombok.Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class TradeOrderListRep extends BaseResp {
	
	private PageResp<MyOrdersRep> pageResp;

	private BigDecimal yesterdayAllIncome = SysConstant.BIGDECIMAL_defaultValue;	// 昨日总收益
	private BigDecimal allValue = SysConstant.BIGDECIMAL_defaultValue;	// 在投总金额
	private BigDecimal expGoldAmount = SysConstant.BIGDECIMAL_defaultValue;	// 
	private List<String> redeemableProductOids = new ArrayList<String>();  // 可赎回产品
}
