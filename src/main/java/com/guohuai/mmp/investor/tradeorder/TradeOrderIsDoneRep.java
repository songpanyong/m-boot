package com.guohuai.mmp.investor.tradeorder;

import java.sql.Date;

import com.guohuai.basic.component.ext.web.BaseResp;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@lombok.Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class TradeOrderIsDoneRep extends BaseResp {
	
	/**
	 * 预期收益起始日
	 */
	private Date beginInterestDate;
	/**
	 * 预期收益到账日
	 */
	private Date interestArrivedDate;
	
	/**
	 * 赎回到账日
	 */
	private Date redeemArrivedDate;
}
