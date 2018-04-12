package com.guohuai.mmp.platform.publisher.offset;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

import com.guohuai.mmp.sys.SysConstant;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@lombok.Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class PublisherOffsetQueryRep {
	
	/**
	 * 轧差OID
	 */
	private String offsetOid;
	
	/**
	 * 轧差日期
	 */
	private Date offsetDate;
	
	/**
	 * 轧差批次号
	 */
	private String offsetCode;
	
	/**
	 * 净头寸
	 */
	private BigDecimal netPosition;
	
	/**
	 * 清算状态
	 */
	private String clearStatus;
	private boolean isClearTimeArr;
	private String clearStatusDisp;
	
	/**
	 * 交收状态
	 */
	private String confirmStatus;
	private String confirmStatusDisp;
	
	/**
	 * 结算状态
	 */
	private String closeStatus;
	private String closeStatusDisp;
	
	/**
	 * 申购金额
	 */
	private BigDecimal buyAmount;



	/**
	 * 赎回金额
	 */
	private BigDecimal redeemAmount;
	
	
	/**
	 * 待结算赎回订单笔数
	 */
	private Integer toCloseRedeemAmount;
	
	/**
	 * 结算人
	 */
	private String closeMan;
	
	/**
	 * SPV OID
	 */
	String spvOid;
	
	/**
	 * SPV名称
	 */
	String spvName;
	
	
	Timestamp createTime;
	Timestamp updateTime;
	
}
