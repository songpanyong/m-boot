package com.guohuai.ams.portfolio20.illiquid.hold.part;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guohuai.ams.portfolio20.illiquid.hold.PortfolioIlliquidHoldEntity;
import com.guohuai.ams.portfolio20.illiquid.hold.part.repayment.PortfolioIlliquidHoldPartRepaymentEntity;
import com.guohuai.ams.portfolio20.illiquid.hold.part.repayment.PortfolioIlliquidHoldPartRepaymentService;
import com.guohuai.ams.portfolio20.illiquid.hold.repayment.PortfolioIlliquidHoldRepaymentEntity;
import com.guohuai.ams.portfolio20.order.MarketOrderEntity;
import com.guohuai.basic.common.DateUtil;
import com.guohuai.basic.common.StringUtil;

@Service
public class PortfolioIlliquidHoldPartService {

	@Autowired
	private PortfolioIlliquidHoldPartDao portfolioIlliquidHoldPartDao;
	@Autowired
	private PortfolioIlliquidHoldPartRepaymentService portfolioIlliquidHoldPartRepaymentService;

	@Transactional
	public PortfolioIlliquidHoldPartEntity newHoldPart(PortfolioIlliquidHoldEntity hold, MarketOrderEntity order) {
		PortfolioIlliquidHoldPartEntity part = new PortfolioIlliquidHoldPartEntity();
		part.setOid(StringUtil.uuid());
		part.setHold(hold);
		part.setIlliquidAsset(order.getIlliquidAsset());
		part.setPortfolio(order.getPortfolio());
		part.setOrder(order);
		part.setExpectValue(order.getTradeShare());
		part.setHoldShare(order.getTradeShare());
		part.setHoldIncome(BigDecimal.ZERO);
		part.setLockupCapital(BigDecimal.ZERO);
		part.setLockupIncome(BigDecimal.ZERO);
		part.setUnitNet(BigDecimal.ONE);
		part.setInvestDate(order.getOrderDate());
		part.setValueDate(DateUtil.addDays(part.getInvestDate(), 1));
		part.setTotalPfofit(BigDecimal.ZERO);
		part.setNewProfit(BigDecimal.ZERO);
		part.setHoldState(PortfolioIlliquidHoldPartEntity.HOLDSTATE_HOLDING);
		part.setExceptWay(order.getExceptWay());
		part = this.portfolioIlliquidHoldPartDao.save(part);
		return part;
	}

	@Transactional
	public Map<String, BigDecimal> freeze(MarketOrderEntity order, PortfolioIlliquidHoldEntity hold) {
		List<PortfolioIlliquidHoldPartEntity> parts = this.portfolioIlliquidHoldPartDao.findByHoldAndHoldState(hold, PortfolioIlliquidHoldPartEntity.HOLDSTATE_HOLDING);

		BigDecimal capital = order.getCapital();
		BigDecimal income = order.getIncome();

		BigDecimal actualFreezeCapital = BigDecimal.ZERO;
		BigDecimal actualFreezeIncome = BigDecimal.ZERO;

		// 是否有需要冻结的金额
		if (capital.signum() > 0 || income.signum() > 0) {

			x: for (PortfolioIlliquidHoldPartEntity part : parts) {
				// 可冻结本金 = 持仓本金 - 冻结本金
				BigDecimal usefulCapital = part.getHoldShare().subtract(part.getLockupCapital());
				// 可冻结收益 = 持仓收益 - 冻结收益
				BigDecimal usefulIncome = part.getHoldIncome().subtract(part.getLockupIncome());

				BigDecimal freezeCapital = capital.compareTo(usefulCapital) > 0 ? usefulCapital : capital;
				BigDecimal freezeIncome = income.compareTo(usefulIncome) > 0 ? usefulIncome : income;

				if (freezeCapital.signum() > 0 || freezeIncome.signum() > 0) {
					// 冻结本金
					if (freezeCapital.signum() > 0) {
						part.setLockupCapital(part.getLockupCapital().add(freezeCapital).setScale(2, RoundingMode.HALF_UP));
					}
					// 冻结收益
					if (freezeIncome.signum() > 0) {
						part.setLockupIncome(part.getLockupIncome().add(freezeIncome).setScale(2, RoundingMode.HALF_UP));
					}

					this.portfolioIlliquidHoldPartRepaymentService.freeze(order, part, freezeCapital, freezeIncome);

					this.portfolioIlliquidHoldPartDao.save(part);

				}

				capital = capital.subtract(freezeCapital).setScale(2, RoundingMode.HALF_UP);
				income = income.subtract(freezeIncome).setScale(2, RoundingMode.HALF_UP);
				actualFreezeCapital = actualFreezeCapital.add(freezeCapital);
				actualFreezeIncome = actualFreezeIncome.add(freezeIncome);

				if (capital.signum() <= 0 && income.signum() <= 0) {
					break x;
				}
			}
		}

		Map<String, BigDecimal> map = new HashMap<String, BigDecimal>();
		map.put("ACTUAL_FREEZE_CAPITAL", actualFreezeCapital);
		map.put("ACTUAL_FREEZE_INCOME", actualFreezeIncome);
		return map;
	}

	@Transactional
	public void unfreeze(PortfolioIlliquidHoldPartRepaymentEntity repayment) {
		PortfolioIlliquidHoldPartEntity part = repayment.getHoldPart();
		part.setLockupCapital(part.getLockupCapital().subtract(repayment.getRepaymentCapital()).setScale(2, RoundingMode.HALF_UP));
		part.setLockupIncome(part.getLockupIncome().subtract(repayment.getRepaymentIncome()).setScale(2, RoundingMode.HALF_UP));
		this.portfolioIlliquidHoldPartDao.save(part);
	}

	@Transactional
	public void subHoldPart(MarketOrderEntity order, PortfolioIlliquidHoldPartEntity part, PortfolioIlliquidHoldPartRepaymentEntity repayment) {
		this.subHoldPart(order, part, repayment, false);
	}

	@Transactional
	public void subHoldPart(MarketOrderEntity order, PortfolioIlliquidHoldPartEntity part, PortfolioIlliquidHoldPartRepaymentEntity partRepayment, boolean forceClose) {

		if (MarketOrderEntity.DEALTYPE_REPAYMENT.equals(order.getDealType())) {
			if (PortfolioIlliquidHoldRepaymentEntity.LAST_ISSUE_YES.equals(order.getIlliquidAssetRepayment().getLastIssue())) {
				part.setHoldState(PortfolioIlliquidHoldPartEntity.HOLDSTATE_CLOSED);
			}
		}

		if (MarketOrderEntity.DEALTYPE_TRANSFER.equals(order.getDealType()) || MarketOrderEntity.DEALTYPE_OVERDUETRANS.equals(order.getDealType())) {
			part.setHoldState(PortfolioIlliquidHoldPartEntity.HOLDSTATE_CLOSED);
		}

		if (MarketOrderEntity.DEALTYPE_SELLOUT.equals(order.getDealType())) {
			if (part.getHoldShare().signum() <= 0 || forceClose) {
				part.setHoldState(PortfolioIlliquidHoldPartEntity.HOLDSTATE_CLOSED);
			}
		}

		part.setLockupCapital(part.getLockupCapital().subtract(partRepayment.getRepaymentCapital()));
		part.setLockupIncome(part.getLockupIncome().subtract(partRepayment.getRepaymentIncome()));
		part.setHoldShare(part.getHoldShare().subtract(partRepayment.getRepaymentCapital()));
		part.setHoldIncome(part.getHoldIncome().subtract(partRepayment.getRepaymentIncome()));
		part.setExpectValue(part.getExpectValue().subtract(partRepayment.getRepaymentCapital()).subtract(partRepayment.getRepaymentIncome()));

		if (part.getHoldShare().signum() <= 0 && part.getHoldIncome().signum() <= 0) {
			part.setHoldState(PortfolioIlliquidHoldPartEntity.HOLDSTATE_CLOSED);
		}

		this.portfolioIlliquidHoldPartDao.save(part);

	}

	@Transactional
	public void estimate(PortfolioIlliquidHoldPartEntity part, BigDecimal estimate, Date date) {
		part.setExpectValue(part.getExpectValue().add(estimate));
		part.setHoldIncome(part.getHoldIncome().add(estimate));
		part.setTotalPfofit(part.getTotalPfofit().add(estimate));
		if (null != part.getNewValueDate() && DateUtil.eq(date, part.getNewValueDate())) {
			part.setNewProfit(part.getNewProfit().add(estimate));
		} else {
			part.setNewProfit(estimate);
		}
		part.setNewValueDate(date);
		if (part.getHoldShare().signum() != 0) {
			part.setUnitNet(part.getExpectValue().divide(part.getHoldShare(), 8, RoundingMode.HALF_UP));
		} else {
			part.setUnitNet(BigDecimal.ZERO);
		}
		this.portfolioIlliquidHoldPartDao.save(part);
	}

	public List<PortfolioIlliquidHoldPartEntity> findHoldingHold(PortfolioIlliquidHoldEntity hold) {
		return this.portfolioIlliquidHoldPartDao.findByHoldAndHoldState(hold, PortfolioIlliquidHoldPartEntity.HOLDSTATE_HOLDING);
	}
}
