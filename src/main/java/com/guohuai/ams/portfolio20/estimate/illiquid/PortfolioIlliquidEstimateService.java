package com.guohuai.ams.portfolio20.estimate.illiquid;
/**
 * @author created by Arthur
 * @date 2017年2月24日 - 下午5:32:18
 */

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guohuai.ams.illiquidAsset.IlliquidAsset;
import com.guohuai.ams.portfolio20.estimate.PortfolioEstimateEntity;
import com.guohuai.ams.portfolio20.estimate.illiquid.part.PortfolioIlliquidPartEstimateService;
import com.guohuai.ams.portfolio20.illiquid.hold.PortfolioIlliquidHoldEntity;
import com.guohuai.ams.portfolio20.illiquid.hold.PortfolioIlliquidHoldService;
import com.guohuai.basic.common.DateUtil;
import com.guohuai.component.util.StringUtil;

@Service
public class PortfolioIlliquidEstimateService {

	@Autowired
	private PortfolioIlliquidEstimateDao portfolioIlliquidEstimateDao;
	@Autowired
	private PortfolioIlliquidHoldService portfolioIlliquidHoldService;
	@Autowired
	private PortfolioIlliquidPartEstimateService portfolioIlliquidPartEstimateService;

	@Transactional
	public BigDecimal batchEstimate(PortfolioEstimateEntity estimate, Date estimateDate) {

		// 估值的总增长额
		BigDecimal increase = BigDecimal.ZERO;

		List<PortfolioIlliquidHoldEntity> holds = this.portfolioIlliquidHoldService.findHoldingHold(estimate.getPortfolio());

		// 没有分仓持仓了, 不需要进行估值
		if (null == holds || holds.size() == 0) {
			return BigDecimal.ZERO;
		}

		// 今日估值, 通过此列表进行是否重复估值的判断
		List<PortfolioIlliquidEstimateEntity> currentEstimates = this.portfolioIlliquidEstimateDao.findByPortfolioAndEstimateDate(estimate.getPortfolio(), estimateDate);
		Map<String, PortfolioIlliquidEstimateEntity> currentEstimatesMap = new HashMap<String, PortfolioIlliquidEstimateEntity>();
		if (null != currentEstimates && currentEstimates.size() > 0) {
			for (PortfolioIlliquidEstimateEntity e : currentEstimates) {
				currentEstimatesMap.put(e.getHold().getOid(), e);
			}
		}

		for (PortfolioIlliquidHoldEntity hold : holds) {

			IlliquidAsset asset = hold.getIlliquidAsset();

			PortfolioIlliquidEstimateEntity he = null;
			if (currentEstimatesMap.containsKey(hold.getOid())) {
				he = currentEstimatesMap.get(hold.getOid());
			} else {
				he = new PortfolioIlliquidEstimateEntity();
				he.setOid(StringUtil.uuid());
				he.setHold(hold);
				he.setAsset(hold.getIlliquidAsset());
				he.setPortfolio(estimate.getPortfolio());
				he.setPortfolioEstimate(estimate);
				he.setLastHoldShare(hold.getHoldShare());
				he.setLastHoldIncome(hold.getHoldIncome());
				he.setLastEstimate(hold.getHoldShare().add(hold.getHoldIncome()));
				if (he.getLastHoldShare().signum() == 0) {
					he.setLastUnitNet(BigDecimal.ZERO);
				} else {
					he.setLastUnitNet(he.getLastEstimate().divide(he.getLastHoldShare(), 8, RoundingMode.HALF_UP));
				}
				he.setHoldShare(hold.getHoldShare());
				he.setHoldIncome(hold.getHoldIncome());
				he.setEstimate(hold.getHoldShare().add(hold.getHoldIncome()));
				if (he.getHoldShare().signum() == 0) {
					he.setUnitNet(BigDecimal.ZERO);
				} else {
					he.setUnitNet(he.getEstimate().divide(he.getHoldShare(), 8, RoundingMode.HALF_UP));
				}
				he.setProfit(BigDecimal.ZERO);
				he.setProfitRate(BigDecimal.ZERO);
				he.setLifeState(asset.getLifeState());
				he.setEstimateDate(estimateDate);
				he.setEstimateTime(new Timestamp(System.currentTimeMillis()));
			}

			he = this.portfolioIlliquidEstimateDao.save(he);

			BigDecimal profitRate = BigDecimal.ZERO;

			if (null != asset.getCollectStartDate() && null != asset.getCollectEndDate()) {
				if (DateUtil.ge(estimateDate, asset.getCollectStartDate()) && DateUtil.lt(estimateDate, asset.getCollectEndDate())) {
					// 计算募集期收益
					profitRate = asset.getCollectIncomeRate() == null ? BigDecimal.ZERO : asset.getCollectIncomeRate();
				}
			}

			/**
			if (IlliquidAsset.ILLIQUIDASSET_LIFESTATE_COLLECTING.equals(asset.getLifeState())) {
				profitRate = asset.getCollectIncomeRate() == null ? BigDecimal.ZERO : asset.getCollectIncomeRate();
			}
			*/

			if (null != asset.getRestStartDate() && null != asset.getRestEndDate()) {
				// 计算存续期收益
				if (DateUtil.ge(estimateDate, asset.getRestStartDate()) && DateUtil.lt(estimateDate, asset.getRestEndDate())) {
					profitRate = asset.getExpAror() == null ? BigDecimal.ZERO : asset.getExpAror();
				}
			}

			/**
			if (IlliquidAsset.ILLIQUIDASSET_LIFESTATE_VALUEDATE.equals(asset.getLifeState())) {
				// 计算存续期收益
				profitRate = asset.getExpAror() == null ? BigDecimal.ZERO : asset.getExpAror();
			}
			*/

			if (IlliquidAsset.ILLIQUIDASSET_LIFESTATE_OVERDUE.equals(asset.getLifeState())) {
				// 计算逾期收益
				profitRate = asset.getOverdueRate() == null ? BigDecimal.ZERO : asset.getOverdueRate();
			}

			BigDecimal profit = this.portfolioIlliquidPartEstimateService.batchEstimate(he, profitRate, asset.getContractDays(), estimateDate);

			he.setHoldIncome(he.getHoldIncome().add(profit));
			he.setEstimate(he.getEstimate().add(profit));
			if (he.getHoldShare().signum() == 0) {
				he.setUnitNet(BigDecimal.ZERO);
			} else {
				he.setUnitNet(he.getEstimate().divide(he.getHoldShare(), 8, RoundingMode.HALF_UP));
			}
			he.setProfit(he.getProfit().add(profit));
			he.setProfitRate(profitRate);
			he.setLifeState(asset.getLifeState());
			he.setEstimateTime(new Timestamp(System.currentTimeMillis()));

			this.portfolioIlliquidEstimateDao.save(he);

			this.portfolioIlliquidHoldService.estimate(hold, profit, estimateDate);

			increase = increase.add(profit);
		}

		return increase;
	}

}
