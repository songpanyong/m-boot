package com.guohuai.mmp.platform.msgment;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * 提现到账	withdrawsuccess	["时间","金额"]		【掌悦理财】您于{1}申请的{2}元提现已转入您指定的银行帐号，具体到账时间请参照各银行规定。
 * @author yuechao
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class WithdrawSuccessMsgReq extends MsgReq {

	/**
	 * 订单完成时间
	 */
	private Timestamp completeTime;
	
	private BigDecimal realAmount;
}
