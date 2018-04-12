package com.guohuai.mmp.publisher.baseaccount;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.mmp.sys.SysConstant;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@lombok.Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PublisherBaseAccountRep extends BaseResp {
	
	/**
	 * 发行人OID
	 */
	private String baseAccountOid;
	
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
	 * 累计充值总额
	 */
	private BigDecimal totalDepositAmount = SysConstant.BIGDECIMAL_defaultValue;

	/**
	 * 累计提现总额
	 */
	private BigDecimal totalWithdrawAmount = SysConstant.BIGDECIMAL_defaultValue;

	/**
	 * 累计借款总额
	 */
	private BigDecimal totalLoanAmount = SysConstant.BIGDECIMAL_defaultValue;

	/**
	 * 累计还款总额
	 */
	private BigDecimal totalReturnAmount = SysConstant.BIGDECIMAL_defaultValue;

	/**
	 * 累计付息总额
	 */
	private BigDecimal totalInterestAmount = SysConstant.BIGDECIMAL_defaultValue;

	/**
	 * 逾期次数
	 */
	private Integer overdueTimes = SysConstant.INTEGER_defaultValue;
	
	private Timestamp createTime;


}
