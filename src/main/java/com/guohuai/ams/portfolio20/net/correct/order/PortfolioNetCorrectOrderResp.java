package com.guohuai.ams.portfolio20.net.correct.order;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author created by Arthur
 * @date 2017年2月20日 - 下午4:16:30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioNetCorrectOrderResp {
	public PortfolioNetCorrectOrderResp (PortfolioNetCorrectOrderEntity entity){
		super();
		this.oid = entity.getOid();
		this.portfolioOid = entity.getPortfolio().getOid();
		this.portfolioName = entity.getPortfolio().getName();
		this.netDate = entity.getNetDate();
		this.share = entity.getShare();
		this.chargeAmount = entity.getChargeAmount();
		this.nav =entity.getNav();
		this.net = entity.getNet();
		this.tradeAmount = entity.getTradeAmount();
		this.withdrawAmount = entity.getWithdrawAmount();
		this.netYield = entity.getNetYield();
		this.orderState = entity.getOrderState();
	}

	private String portfolioOid;
	private String portfolioName;
	
	private String oid;

	// 基准日
	private Date netDate;

	// 份额
	private BigDecimal share;
	// 单位净值
	private BigDecimal nav;
	// 总资产净值
	private BigDecimal net;

	// 	净充值
	private BigDecimal chargeAmount;
	// 净提现
	private BigDecimal withdrawAmount;
	// 净交易
	private BigDecimal tradeAmount;
	// 净值增长率
	private BigDecimal netYield;

	// 申请人
	private String creator;
	// 申请时间
	private Timestamp createTime;

	// 审核人
	private String auditor;
	// 审核时间
	private Timestamp auditTime;
	// 审核状态
	private String orderState;
	// 审核意见
	private String auditMark;

}
