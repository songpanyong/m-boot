package com.guohuai.mmp.platform.msgment;

import java.math.BigDecimal;

import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * 
 * 
 * 提前还款提醒	prepayment	["产品名称","金额","金额","金额","金额"]	提前还款提醒	您投资的{1}发生提前还款,
 * 总额{2}元,其中提前还款本金{3}元,提前还款利息{4}元,提前还款补偿金{5}元，本次投资已回款结束。
 * @author yuechao
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PrePaymentReq extends MailReq {
	/**
	 * 产品名称
	 */
	private String productName;
	
	/**
	 * 总额
	 */
	private BigDecimal totalAmount;
	
	/**
	 * 本金
	 */
	private BigDecimal corpusAmount;
	
	/**
	 * 补偿金
	 */
	private BigDecimal compensateAmount;
	
	
}
