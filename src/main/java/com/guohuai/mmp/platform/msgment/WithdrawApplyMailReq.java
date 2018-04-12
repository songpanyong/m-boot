package com.guohuai.mmp.platform.msgment;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.Data;
import lombok.EqualsAndHashCode;


/**
提现申请提醒	withdrawapply	["时间-精确到分钟","金额"]	提现申请提醒	您于{1}申请的{2}元提现已受理,我们会在1个工作日之内处理。
 * @author yuechao
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class WithdrawApplyMailReq extends MailReq {
	/**
	 * 订单时间
	 */
	private Timestamp orderTime;
	
	/**
	 * 订单金额
	 */
	private BigDecimal orderAmount;
	
	private BigDecimal fee;
	
	private BigDecimal preAmount;
}
