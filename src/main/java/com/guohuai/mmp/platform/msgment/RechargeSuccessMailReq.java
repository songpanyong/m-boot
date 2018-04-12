package com.guohuai.mmp.platform.msgment;

import java.math.BigDecimal;

import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * 充值成功提醒	rechargesuccess	["金额"]	充值成功提醒	恭喜您成功充值{1}元！
 * @author yuechao
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class RechargeSuccessMailReq extends MailReq {
	/**
	 * 订单金额
	 */
	private BigDecimal orderAmount;
}
