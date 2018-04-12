package com.guohuai.mmp.publisher.baseaccount;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@lombok.Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PublisherQueryRep {
	
	private String baseAccountOid;
	
	/**
	 * 三方会员账户
	 */
	private String memberId;
	
	/**
	 * 手机号
	 */
	private String phone;
	
	/**
	 * 姓名
	 */
	private String realName;
	
	/**
	 * 证件号
	 */
	private String certificateNo;
	
	/**
	 * 银行名称
	 */
	private String bankName;
	
	/**
	 * 银行账号
	 */
	private String cardNo;

	/**
	 * 余额 
	 */
	private BigDecimal basicBalance = BigDecimal.ZERO;
	
	/**
	 * 归集清算户余额
	 */
	private BigDecimal collectionSettlementBalance = BigDecimal.ZERO;
	
	/**
	 * 可用金户余额
	 */
	private BigDecimal availableAmountBalance = BigDecimal.ZERO;
	
	/**
	 * 冻结户余额
	 */
	private BigDecimal frozenAmountBalance = BigDecimal.ZERO;
	
	/**
	 * 提现可用金余额
	 */
	private BigDecimal withdrawAvailableAmountBalance = BigDecimal.ZERO;

	/**
	 * 状态
	 */
	private String status;
	private String statusDisp;

	private Timestamp updateTime;

	private Timestamp createTime;
	

}
