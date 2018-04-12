package com.guohuai.mmp.platform.msgment;

import java.math.BigDecimal;

import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * 回款提醒	receivedpayments	["产品名称","金额"]	回款提醒	您投资的{1}理财产品本次回款{2}元，如需详情请查看资金记录。
 * @author yuechao
 *
 */

@Data
@EqualsAndHashCode(callSuper = false)
public class ReceivedPaymentsMailReq extends MailReq {
	/**
	 * 产品名称
	 */
	private String productName;
	/**
	 * 订单金额
	 */
	private BigDecimal orderAmount;
}
