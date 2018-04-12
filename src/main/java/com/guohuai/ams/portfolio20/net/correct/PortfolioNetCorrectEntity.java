package com.guohuai.ams.portfolio20.net.correct;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.guohuai.ams.portfolio.entity.PortfolioEntity;
import com.guohuai.ams.portfolio20.net.correct.order.PortfolioNetCorrectOrderEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "T_GAM_PORTFOLIO_NET_CORRECT")
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioNetCorrectEntity implements Serializable {

	private static final long serialVersionUID = 649529152780277136L;

	@Id
	private String oid;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "portfolioOid", referencedColumnName = "oid")
	private PortfolioEntity portfolio;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "orderOid", referencedColumnName = "oid")
	private PortfolioNetCorrectOrderEntity order;

	// 净值校准日
	private Date netDate;

	// 份额
	private BigDecimal share;
	// 单位净值
	private BigDecimal nav;
	// 净值
	private BigDecimal net;

	// 昨日份额
	private BigDecimal lastShare;
	// 昨日单位净值
	private BigDecimal lastNav;
	// 昨日净值
	private BigDecimal lastNet;

	// 净充值
	private BigDecimal chargeAmount;
	// 净提现
	private BigDecimal withdrawAmount;
	// 净交易
	private BigDecimal tradeAmount;

	// 净值增长率
	private BigDecimal netYield;

}
