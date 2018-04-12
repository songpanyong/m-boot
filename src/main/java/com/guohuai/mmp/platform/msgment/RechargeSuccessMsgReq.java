package com.guohuai.mmp.platform.msgment;

import java.math.BigDecimal;

import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * 充值成功	rechargesuccess	["金额"]		【掌悦理财】恭喜您成功充值{1}元！如需详情请在平台内查看。
 * @author yuechao
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class RechargeSuccessMsgReq extends MsgReq {
	/**
	 * 订单金额
	 */
	private BigDecimal orderAmount;
}
