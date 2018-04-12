package com.guohuai.ams.portfolio20.estimate.illiquid.part;

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
import com.guohuai.ams.portfolio20.estimate.illiquid.PortfolioIlliquidEstimateEntity;
import com.guohuai.ams.portfolio20.illiquid.hold.part.PortfolioIlliquidHoldPartEntity;
import com.guohuai.ams.portfolio20.illiquid.hold.part.PortfolioIlliquidHoldPartService;
import com.guohuai.basic.common.DateUtil;
import com.guohuai.component.util.StringUtil;

/**
 * @author created by Arthur
 * @date 2017年2月24日 - 上午10:34:08
 */
@Service
public class PortfolioIlliquidPartEstimateService {

	@Autowired
	private PortfolioIlliquidPartEstimateDao portfolioIlliquidPartEstimateDao;
	@Autowired
	private PortfolioIlliquidHoldPartService portfolioIlliquidHoldPartService;

	@Transactional
	public BigDecimal batchEstimate(PortfolioIlliquidEstimateEntity illiquidEstimate, BigDecimal profitRateX, int contractDays, Date estimateDate) {
		// 估值的总增长额
		BigDecimal increase = BigDecimal.ZERO;

		List<PortfolioIlliquidHoldPartEntity> parts = this.portfolioIlliquidHoldPartService.findHoldingHold(illiquidEstimate.getHold());
		// 没有分仓持仓了, 不需要进行估值
		if (null == parts || parts.size() == 0) {
			return BigDecimal.ZERO;
		}

		IlliquidAsset asset = illiquidEstimate.getAsset();

		// 今日估值, 通过此列表进行是否重复估值的判断
		List<PortfolioIlliquidPartEstimateEntity> currentPartEstimates = this.portfolioIlliquidPartEstimateDao.findByHoldAndEstimateDate(illiquidEstimate.getHold(), estimateDate);
		Map<String, PortfolioIlliquidPartEstimateEntity> currentPartEstimatesMap = new HashMap<String, PortfolioIlliquidPartEstimateEntity>();
		if (null != currentPartEstimates && currentPartEstimates.size() > 0) {
			for (PortfolioIlliquidPartEstimateEntity e : currentPartEstimates) {
				currentPartEstimatesMap.put(e.getHoldPart().getOid(), e);
			}
		}

		for (PortfolioIlliquidHoldPartEntity part : parts) {

			if (DateUtil.lt(estimateDate, part.getValueDate())) {
				// 未到分仓起息日
				continue;
			}

			// 判断今日是否做过估值, 如果做过, 做增量更新处理

			// 如果没有做过估值, 做新增处理

			PortfolioIlliquidPartEstimateEntity pe = null;

			if (currentPartEstimatesMap.containsKey(part.getOid())) {
				pe = currentPartEstimatesMap.get(part.getOid());
			} else {
				pe = new PortfolioIlliquidPartEstimateEntity();
				pe.setOid(StringUtil.uuid());
				pe.setHoldPart(part);
				pe.setHold(illiquidEstimate.getHold());
				pe.setHoldEstimate(illiquidEstimate);
				pe.setAsset(asset);
				pe.setPortfolio(illiquidEstimate.getHold().getPortfolio());
				pe.setPortfolioEstimate(illiquidEstimate.getPortfolioEstimate());
				pe.setLastHoldShare(part.getHoldShare());
				pe.setLastHoldIncome(part.getHoldIncome());
				pe.setLastEstimate(part.getHoldShare().add(part.getHoldIncome()));
				if (pe.getLastHoldShare().signum() == 0) {
					pe.setLastUnitNet(BigDecimal.ZERO);
				} else {
					pe.setLastUnitNet(pe.getLastEstimate().divide(pe.getLastHoldShare(), 8, RoundingMode.HALF_UP));
				}
				pe.setHoldShare(part.getHoldShare());
				pe.setHoldIncome(part.getHoldIncome());
				pe.setEstimate(part.getHoldShare().add(part.getHoldIncome()));
				if (pe.getHoldShare().signum() == 0) {
					pe.setUnitNet(BigDecimal.ZERO);
				} else {
					pe.setUnitNet(pe.getEstimate().divide(pe.getHoldShare(), 8, RoundingMode.HALF_UP));
				}
				pe.setProfit(BigDecimal.ZERO);
				pe.setProfitRate(BigDecimal.ZERO);
				pe.setLifeState(asset.getLifeState());
				pe.setEstimateDate(estimateDate);
				pe.setEstimateTime(new Timestamp(System.currentTimeMillis()));
			}

			BigDecimal profit = BigDecimal.ZERO;

			BigDecimal profitRate = BigDecimal.ZERO;
			if (profitRateX.signum() != 0) {
				if (contractDays == 365 || contractDays == 360) {

				} else {
					contractDays = 360;
				}
				profitRate = profitRateX.divide(new BigDecimal(contractDays), 8, RoundingMode.HALF_UP);
			}

			if (PortfolioIlliquidHoldPartEntity.EXCEPT_WAY_AMORTISED_COST.equals(part.getExceptWay())) {
				profit = pe.getHoldShare().multiply(profitRate).setScale(2, RoundingMode.HALF_UP);
			}
			BigDecimal incr = profit.subtract(pe.getProfit());

			pe.setHoldIncome(pe.getHoldIncome().add(incr));
			pe.setEstimate(pe.getEstimate().add(incr));
			if (pe.getHoldShare().signum() == 0) {
				pe.setUnitNet(BigDecimal.ZERO);
			} else {
				pe.setUnitNet(pe.getEstimate().divide(pe.getHoldShare(), 8, RoundingMode.HALF_UP));
			}
			pe.setProfit(profit);
			pe.setProfitRate(profitRate);
			pe.setLifeState(asset.getLifeState());
			pe.setEstimateTime(new Timestamp(System.currentTimeMillis()));
			pe.setExceptWay(part.getExceptWay());

			this.portfolioIlliquidPartEstimateDao.save(pe);
			
			this.portfolioIlliquidHoldPartService.estimate(part, incr, estimateDate);

			increase = increase.add(incr);

		}

		return increase;
	}

}
