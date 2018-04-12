package com.guohuai.mmp.platform.publisher.dividend.offset;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;


import lombok.Data;

@Data
public class DividendOffsetQueryRep {
	
	private String dividendOffsetOid;
	/**
	 * 产品名称
	 */
	private String productName;
	
	/**
	 * 红利日期
	 */
	private Date dividendDate;

	/**
	 * 红利金额
	 */
	private BigDecimal dividendAmount;

	/**
	 * 待结算红利订单笔数
	 */
	private Integer toCloseDividendNumber;
	
	/**
	 * 结算响应信息
	 */
	private String message;
	
	/**
	 * 红利结算状态
	 */
	private String dividendCloseStatus;
	private String dividendCloseStatusDisp;

	private Timestamp updateTime;
	private Timestamp createTime;
}
