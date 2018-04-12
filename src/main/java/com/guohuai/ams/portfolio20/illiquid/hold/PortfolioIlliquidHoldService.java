package com.guohuai.ams.portfolio20.illiquid.hold;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guohuai.ams.illiquidAsset.IlliquidAsset;
import com.guohuai.ams.portfolio.entity.PortfolioEntity;
import com.guohuai.ams.portfolio20.illiquid.hold.part.PortfolioIlliquidHoldPartEntity;
import com.guohuai.ams.portfolio20.illiquid.hold.part.PortfolioIlliquidHoldPartService;
import com.guohuai.ams.portfolio20.illiquid.hold.part.repayment.PortfolioIlliquidHoldPartRepaymentService;
import com.guohuai.ams.portfolio20.illiquid.hold.repayment.PortfolioIlliquidHoldRepaymentEntity;
import com.guohuai.ams.portfolio20.order.MarketOrderEntity;
import com.guohuai.basic.common.DateUtil;
import com.guohuai.component.util.StringUtil;

@Service
public class PortfolioIlliquidHoldService {

	@Autowired
	private PortfolioIlliquidHoldDao portfolioIlliquidHoldDao;

	@Autowired
	private PortfolioIlliquidHoldPartService portfolioIlliquidHoldPartService;
	@Autowired
	private PortfolioIlliquidHoldPartRepaymentService portfolioIlliquidHoldPartRepaymentService;

	public PortfolioIlliquidHoldEntity findHoldingHold(PortfolioEntity portfolio, IlliquidAsset illiquidAsset) {
		return this.portfolioIlliquidHoldDao.findForHolding(portfolio, illiquidAsset);
	}
//	@Transactional
	public List<PortfolioIlliquidHoldEntity> findHoldingHold(PortfolioEntity portfolio) {
		return this.portfolioIlliquidHoldDao.findForHolding(portfolio);
	}

	@Transactional
	public PortfolioIlliquidHoldEntity newHold(PortfolioEntity portfolio, IlliquidAsset illiquidAsset, String exceptWay, String operator) {

		PortfolioIlliquidHoldEntity h = new PortfolioIlliquidHoldEntity();
		h.setOid(StringUtil.uuid());
		h.setIlliquidAsset(illiquidAsset);
		h.setPortfolio(portfolio);
		h.setInvestDate(new java.sql.Date(System.currentTimeMillis()));
		h.setValueDate(DateUtil.addDays(h.getInvestDate(), 1));
		h.setExpectValue(BigDecimal.ZERO);
		h.setHoldShare(BigDecimal.ZERO);
		h.setHoldIncome(BigDecimal.ZERO);
		h.setNatValue(BigDecimal.ZERO);
		h.setLockupCapital(BigDecimal.ZERO);
		h.setLockupIncome(BigDecimal.ZERO);
		h.setTotalPfofit(BigDecimal.ZERO);
		h.setNewPfofit(BigDecimal.ZERO);
		h.setExceptWay(exceptWay);
		h.setHoldState(PortfolioIlliquidHoldEntity.HOLDSTATE_HOLDING);
		h.setCreator(operator);
		h.setCreateTime(new Timestamp(System.currentTimeMillis()));
		h.setOperator(operator);
		h.setUpdateTime(new Timestamp(System.currentTimeMillis()));
		h = this.portfolioIlliquidHoldDao.save(h);
		return h;
	}

	@Transactional
	public PortfolioIlliquidHoldEntity mergeHold(PortfolioIlliquidHoldEntity hold, PortfolioIlliquidHoldPartEntity part) {
		hold.setExpectValue(hold.getExpectValue().add(part.getExpectValue()).setScale(2, RoundingMode.HALF_UP));
		hold.setHoldShare(hold.getHoldShare().add(part.getHoldShare()).setScale(2, RoundingMode.HALF_UP));
		hold.setNatValue(hold.getExpectValue().divide(hold.getHoldShare(), 8, RoundingMode.HALF_UP));
		hold = this.portfolioIlliquidHoldDao.save(hold);
		return hold;
	}

	@Transactional
	public Map<String, BigDecimal> freeze(MarketOrderEntity order, PortfolioIlliquidHoldEntity hold) {

		Map<String, BigDecimal> result = this.portfolioIlliquidHoldPartService.freeze(order, hold);

		hold.setLockupCapital(hold.getLockupCapital().add(result.get("ACTUAL_FREEZE_CAPITAL")));
		hold.setLockupIncome(hold.getLockupIncome().add(result.get("ACTUAL_FREEZE_INCOME")));

		hold = this.portfolioIlliquidHoldDao.save(hold);
		return result;
	}

	@Transactional
	public Map<String, BigDecimal> failRepayment(PortfolioIlliquidHoldEntity hold, MarketOrderEntity order) {

		Map<String, BigDecimal> map = this.portfolioIlliquidHoldPartRepaymentService.unfrezee(order);
		hold.setLockupCapital(hold.getLockupCapital().subtract(map.get("ACTUAL_UNFREEZE_CAPITAL")));
		hold.setLockupIncome(hold.getLockupIncome().subtract(map.get("ACTUAL_UNFREEZE_INCOME")));
		hold = this.portfolioIlliquidHoldDao.save(hold);
		return map;
	}

	@Transactional
	public Map<String, BigDecimal> subHold(PortfolioIlliquidHoldEntity hold, MarketOrderEntity order) {
		return this.subHold(hold, order, false);
	}

	@Transactional
	public Map<String, BigDecimal> subHold(PortfolioIlliquidHoldEntity hold, MarketOrderEntity order, boolean forceClose) {

		Map<String, BigDecimal> map = this.portfolioIlliquidHoldPartRepaymentService.subHoldPart(order, forceClose);
		hold.setLockupCapital(hold.getLockupCapital().subtract(map.get("ACTUAL_REPAYMENT_CAPITAL")));
		hold.setLockupIncome(hold.getLockupIncome().subtract(map.get("ACTUAL_REPAYMENT_INCOME")));
		hold.setHoldShare(hold.getHoldShare().subtract(map.get("ACTUAL_REPAYMENT_CAPITAL")));
		hold.setHoldIncome(hold.getHoldIncome().subtract(map.get("ACTUAL_REPAYMENT_INCOME")));
		hold.setExpectValue(hold.getExpectValue().subtract(map.get("ACTUAL_REPAYMENT_CAPITAL")).subtract(map.get("ACTUAL_REPAYMENT_INCOME")));
		hold.setNatValue(hold.getHoldShare().signum() <= 0 ? BigDecimal.ZERO : hold.getExpectValue().divide(hold.getHoldShare(), 8, RoundingMode.HALF_UP));

		if (MarketOrderEntity.DEALTYPE_REPAYMENT.equals(order.getDealType())) {
			if (PortfolioIlliquidHoldRepaymentEntity.LAST_ISSUE_YES.equals(order.getIlliquidAssetRepayment().getLastIssue())) {
				hold.setHoldState(PortfolioIlliquidHoldEntity.HOLDSTATE_CLOSED);
			}
		}

		if (MarketOrderEntity.DEALTYPE_TRANSFER.equals(order.getDealType()) || MarketOrderEntity.DEALTYPE_OVERDUETRANS.equals(order.getDealType()) || MarketOrderEntity.DEALTYPE_REFUND.equals(order.getDealType()) || MarketOrderEntity.DEALTYPE_CANCELLATE.equals(order.getDealType()) || MarketOrderEntity.DEALTYPE_OVERDUECANCELLATE.equals(order.getDealType())) {
			hold.setHoldState(PortfolioIlliquidHoldEntity.HOLDSTATE_CLOSED);
		}

		if (MarketOrderEntity.DEALTYPE_SELLOUT.equals(order.getDealType()) || MarketOrderEntity.DEALTYPE_REPAYMENT.equals(order.getDealType())) {
			if (hold.getHoldShare().signum() <= 0 || forceClose) {
				hold.setHoldState(PortfolioIlliquidHoldEntity.HOLDSTATE_CLOSED);
			}
		}

		if (PortfolioIlliquidHoldEntity.HOLDSTATE_CLOSED.equals(hold.getHoldState())) {
			map.put("LEFT_HOLD_SHARE", hold.getHoldShare());
			map.put("LEFT_HOLD_INCOME", hold.getHoldIncome());
		} else {
			map.put("LEFT_HOLD_SHARE", BigDecimal.ZERO);
			map.put("LEFT_HOLD_INCOME", BigDecimal.ZERO);
		}

		/*
		 * if (hold.getHoldShare().signum() <= 0 &&
		 * hold.getHoldIncome().signum() <= 0) {
		 * hold.setHoldState(PortfolioIlliquidHoldEntity.HOLDSTATE_CLOSED); }
		 */
		return map;
	}

	@Transactional
	public void estimate(PortfolioIlliquidHoldEntity hold, BigDecimal estimate, Date date) {
		hold.setExpectValue(hold.getExpectValue().add(estimate));
		hold.setHoldIncome(hold.getHoldIncome().add(estimate));
		hold.setTotalPfofit(hold.getTotalPfofit().add(estimate));
		if (null != hold.getNewValueDate() && DateUtil.eq(date, hold.getNewValueDate())) {
			hold.setNewPfofit(hold.getNewPfofit().add(estimate));
		} else {
			hold.setNewPfofit(estimate);
		}
		hold.setNewValueDate(date);
		if (hold.getHoldShare().signum() == 0) {
			hold.setNatValue(BigDecimal.ZERO);
		} else {
			hold.setNatValue(hold.getExpectValue().divide(hold.getHoldShare(), 8, RoundingMode.HALF_UP));
		}
		if (hold.getExpectValue().signum() == 0) {
			hold.setHoldState(PortfolioIlliquidHoldEntity.HOLDSTATE_CLOSED);
		}
		this.portfolioIlliquidHoldDao.save(hold);
	}

	public PortfolioIlliquidHoldEntity findByOid(String oid) {
		return this.portfolioIlliquidHoldDao.findOne(oid);
	}

	public List<PortfolioIlliquidHoldEntity> findByAsset(IlliquidAsset asset) {
		return this.portfolioIlliquidHoldDao.findByIlliquidAssetAndHoldState(asset, PortfolioIlliquidHoldEntity.HOLDSTATE_HOLDING);
	}

}
