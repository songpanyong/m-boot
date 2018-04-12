package com.guohuai.mmp.investor.tradeorder;

import java.math.BigDecimal;
import java.sql.Timestamp;
import com.guohuai.component.persist.UUID;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 投资人-交易委托单
 * 
 * @author yuechao
 *
 */
@lombok.Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class InvestorTradeOrderRep extends UUID {
	/**
	* 
	*/
	private static final long serialVersionUID = 4333179226422640561L;

	private String orderCode;
	private String investorOid;
	private String orderType;
	private String orderStatus;
	private BigDecimal orderAmount;
	private Timestamp orderTime;
	private String productType;
	
}
