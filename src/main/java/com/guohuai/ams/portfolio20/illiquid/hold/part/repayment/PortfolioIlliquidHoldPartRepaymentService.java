package com.guohuai.ams.portfolio20.illiquid.hold.part.repayment;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guohuai.ams.portfolio20.illiquid.hold.part.PortfolioIlliquidHoldPartEntity;
import com.guohuai.ams.portfolio20.illiquid.hold.part.PortfolioIlliquidHoldPartService;
import com.guohuai.ams.portfolio20.order.MarketOrderEntity;
import com.guohuai.basic.common.StringUtil;

@Service
public class PortfolioIlliquidHoldPartRepaymentService {

	@Autowired
	private PortfolioIlliquidHoldPartRepaymentDao portfolioIlliquidHoldPartRepaymentDao;
	@Autowired
	private PortfolioIlliquidHoldPartService portfolioIlliquidHoldPartService;

	@Transactional
	public PortfolioIlliquidHoldPartRepaymentEntity freeze(MarketOrderEntity order, PortfolioIlliquidHoldPartEntity part, BigDecimal freezeCapital, BigDecimal freezeIncome) {
		PortfolioIlliquidHoldPartRepaymentEntity repayment = new PortfolioIlliquidHoldPartRepaymentEntity();
		repayment.setOid(StringUtil.uuid());
		repayment.setHoldPart(part);
		repayment.setRepayment(order.getIlliquidAssetRepayment());
		repayment.setOrder(order);
		repayment.setRepaymentCapital(freezeCapital);
		repayment.setRepaymentIncome(freezeIncome);

		repayment = this.portfolioIlliquidHoldPartRepaymentDao.save(repayment);
		return repayment;
	}

	@Transactional
	public Map<String, BigDecimal> unfrezee(MarketOrderEntity order) {
		BigDecimal unfreezeCapital = BigDecimal.ZERO;
		BigDecimal unfreezeIncome = BigDecimal.ZERO;

		List<PortfolioIlliquidHoldPartRepaymentEntity> repayments = this.portfolioIlliquidHoldPartRepaymentDao.findByOrder(order);
		if (null != repayments && repayments.size() > 0) {
			for (PortfolioIlliquidHoldPartRepaymentEntity repayment : repayments) {
				this.portfolioIlliquidHoldPartService.unfreeze(repayment);
				unfreezeCapital = unfreezeCapital.add(repayment.getRepaymentCapital());
				unfreezeIncome = unfreezeIncome.add(repayment.getRepaymentIncome());
			}
		}

		Map<String, BigDecimal> map = new HashMap<String, BigDecimal>();
		map.put("ACTUAL_UNFREEZE_CAPITAL", unfreezeCapital);
		map.put("ACTUAL_UNFREEZE_INCOME", unfreezeIncome);

		return map;
	}

	@Transactional
	public Map<String, BigDecimal> subHoldPart(MarketOrderEntity order) {
		return this.subHoldPart(order, false);
	}

	@Transactional
	public Map<String, BigDecimal> subHoldPart(MarketOrderEntity order, boolean forceClose) {
		BigDecimal subCapital = BigDecimal.ZERO;
		BigDecimal subIncome = BigDecimal.ZERO;

		List<PortfolioIlliquidHoldPartRepaymentEntity> repayments = this.portfolioIlliquidHoldPartRepaymentDao.findByOrder(order);
		if (null != repayments && repayments.size() > 0) {
			for (PortfolioIlliquidHoldPartRepaymentEntity repayment : repayments) {
				this.portfolioIlliquidHoldPartService.subHoldPart(order, repayment.getHoldPart(), repayment, forceClose);
				subCapital = subCapital.add(repayment.getRepaymentCapital());
				subIncome = subIncome.add(repayment.getRepaymentIncome());
			}
		}

		Map<String, BigDecimal> map = new HashMap<String, BigDecimal>();
		map.put("ACTUAL_REPAYMENT_CAPITAL", subCapital);
		map.put("ACTUAL_REPAYMENT_INCOME", subIncome);

		return map;
	}

}
