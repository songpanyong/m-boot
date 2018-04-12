package com.guohuai.ams.portfolio20.net.correct;

import java.math.BigDecimal;
import java.sql.Date;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author created by Arthur
 * @date 2017年2月20日 - 下午1:33:04
 */
@Data
@NoArgsConstructor
public class PortfolioNetCorrectPrepareResp {
	public PortfolioNetCorrectPrepareResp(PortfolioNetCorrectEntity entity){
		super();
		this.lastShare = entity.getLastShare();
		this.lastNav = entity.getLastNav();
		this.lastNet = entity.getLastNet();
		this.netDate =entity.getNetDate();
		this.share = entity.getShare();
		this.nav =entity.getNav();

		this.net = entity.getLastNet();
		this.netYield = entity.getNetYield();

		this.net = entity.getNet();

		this.chargeAmount = entity.getChargeAmount();
		this.withdrawAmount = entity.getWithdrawAmount();
		this.tradeAmount = entity.getTradeAmount();
	}

	// 有未审核的净值校准订单, 请审核后再次操作.
	private boolean correcting;
	// 当前日期的所有数据已经校准完成, 请明日再执行此操作.
	private boolean allCorrected;
	// 历史是否有执行过校准操作, true 表示有, 日期不可选, 用下面的日期, false 表示未执行校准操作, 日期可选, 只能是昨日之前的数据
	private boolean corrected;

	// 最多可校准此日期的数据
	private Date maxCorrectDate;
	// 本次操作, 应校准此日期的数据
	private Date currentCorrectDate;
	// 净值校准日
	private Date netDate;
	// 份额
	private BigDecimal share;
	// 单位净值
	private BigDecimal nav;
	// 净值
	private BigDecimal net;
	// 净值增长率
	private BigDecimal netYield;


	// 前一净值校准日
	private Date lastNetDate;

	// 昨日份额
	private BigDecimal lastShare;
	// 昨日单位净值
	private BigDecimal lastNav;
	// 净值
	private BigDecimal lastNet;

	// 净充值
	private BigDecimal chargeAmount;
	// 净提现
	private BigDecimal withdrawAmount;
	// 净交易
	private BigDecimal tradeAmount;

}
