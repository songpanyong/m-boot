package com.guohuai.ams.portfolio20.illiquid.hold.part.repayment;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.guohuai.ams.portfolio20.illiquid.hold.part.PortfolioIlliquidHoldPartEntity;
import com.guohuai.ams.portfolio20.illiquid.hold.repayment.PortfolioIlliquidHoldRepaymentEntity;
import com.guohuai.ams.portfolio20.order.MarketOrderEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "T_GAM_PORTFOLIO_ILLIQUID_HOLD_PART_REPAYMENT")
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioIlliquidHoldPartRepaymentEntity implements Serializable {

	private static final long serialVersionUID = 9129616528616466694L;
	@Id
	private String oid;

	// 分仓
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "holdPartOid", referencedColumnName = "oid")
	private PortfolioIlliquidHoldPartEntity holdPart;

	// 还款计划
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "repaymentOid", referencedColumnName = "oid")
	private PortfolioIlliquidHoldRepaymentEntity repayment;

	// 订单
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "orderOid", referencedColumnName = "oid")
	private MarketOrderEntity order;

	// 还款本金
	private BigDecimal repaymentCapital;
	// 还款利息
	private BigDecimal repaymentIncome;

}
