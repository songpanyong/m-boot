package com.guohuai.mmp.platform.msgment;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * 提现申请	withdrawapply	["时间","金额"]		【掌悦理财】您于{1}申请的{2}元提现已受理，我们会在1个工作日之内处理。
 * @author yuechao
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class WithdrawApplyMsgReq extends MsgReq {
	/**
	 * 订单时间
	 */
	private Timestamp orderTime;
	
	/**
	 * 订单金额
	 */
	private BigDecimal orderAmount;
	
	/**手续费*/
	private BigDecimal fee;
	
	/** 预计到账金额 */
	private BigDecimal preAmount;
}
