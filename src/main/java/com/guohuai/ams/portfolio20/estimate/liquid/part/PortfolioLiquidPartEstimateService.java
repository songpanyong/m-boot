package com.guohuai.ams.portfolio20.estimate.liquid.part;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guohuai.ams.liquidAsset.LiquidAsset;
import com.guohuai.ams.liquidAsset.yield.LiquidAssetYield;
import com.guohuai.ams.portfolio20.estimate.liquid.PortfolioLiquidEstimateEntity;
import com.guohuai.ams.portfolio20.liquid.hold.PortfolioLiquidHoldEntity;
import com.guohuai.ams.portfolio20.liquid.hold.part.PortfolioLiquidHoldPartEntity;
import com.guohuai.ams.portfolio20.liquid.hold.part.PortfolioLiquidHoldPartService;
import com.guohuai.basic.common.DateUtil;
import com.guohuai.component.util.StringUtil;

/**
 * @author created by Arthur
 * @date 2017年2月24日 - 上午10:40:27
 */
@Service
public class PortfolioLiquidPartEstimateService {

	@Autowired
	private PortfolioLiquidPartEstimateDao portfolioLiquidPartEstimateDao;
	@Autowired
	private PortfolioLiquidHoldPartService portfolioLiquidHoldPartService;

	/**
	 * 
	 * @param liquidEstimate
	 *            合仓
	 * @param profit
	 *            日收益率
	 * @param estimateDate
	 *            估值日期
	 */
	@Transactional
	public BigDecimal batchEstimate(PortfolioLiquidEstimateEntity liquidEstimate, BigDecimal profitRate, Date estimateDate) {

		// 估值的总增长额
		BigDecimal increase = BigDecimal.ZERO;

		List<PortfolioLiquidHoldPartEntity> parts = this.portfolioLiquidHoldPartService.findHoldingHold(liquidEstimate.getHold());
		// 没有分仓持仓了, 不需要进行估值
		if (null == parts || parts.size() == 0) {
			return BigDecimal.ZERO;
		}

		LiquidAsset asset = liquidEstimate.getAsset();

		/*
		// 昨日估值
		List<PortfolioLiquidPartEstimateEntity> lastPartEstimates = this.portfolioLiquidPartEstimateDao.findByHoldAndEstimateDate(liquidEstimate.getHold(), DateUtil.addDays(estimateDate, -1));
		// key = 分仓id
		Map<String, PortfolioLiquidPartEstimateEntity> lastPartEstimatesMap = new HashMap<String, PortfolioLiquidPartEstimateEntity>();
		if (null != lastPartEstimates && lastPartEstimates.size() > 0) {
			for (PortfolioLiquidPartEstimateEntity e : lastPartEstimates) {
				lastPartEstimatesMap.put(e.getHoldPart().getOid(), e);
			}
		}
		*/

		// 今日估值, 通过此列表进行是否重复估值的判断
		List<PortfolioLiquidPartEstimateEntity> currentPartEstimates = this.portfolioLiquidPartEstimateDao.findByHoldAndEstimateDate(liquidEstimate.getHold(), estimateDate);
		Map<String, PortfolioLiquidPartEstimateEntity> currentPartEstimatesMap = new HashMap<String, PortfolioLiquidPartEstimateEntity>();
		if (null != currentPartEstimates && currentPartEstimates.size() > 0) {
			for (PortfolioLiquidPartEstimateEntity e : currentPartEstimates) {
				currentPartEstimatesMap.put(e.getHoldPart().getOid(), e);
			}
		}

		for (PortfolioLiquidHoldPartEntity part : parts) {

			if (DateUtil.lt(estimateDate, part.getValueDate())) {
				// 未到分仓起息日
				continue;
			}

			// 判断今日是否做过估值, 如果做过, 做增量更新处理

			// 如果没有做过估值, 做新增处理

			PortfolioLiquidPartEstimateEntity pe = null;

			if (currentPartEstimatesMap.containsKey(part.getOid())) {
				pe = currentPartEstimatesMap.get(part.getOid());
			} else {
				pe = new PortfolioLiquidPartEstimateEntity();
				pe.setOid(StringUtil.uuid());
				pe.setHoldPart(part);
				pe.setHold(liquidEstimate.getHold());
				pe.setHoldEstimate(liquidEstimate);
				pe.setAsset(asset);
				pe.setPortfolio(liquidEstimate.getHold().getPortfolio());
				pe.setPortfolioEstimate(liquidEstimate.getPortfolioEstimate());

			}

			BigDecimal profit = BigDecimal.ZERO;
			BigDecimal basic = BigDecimal.ZERO;

			//货币基金, 按照日复利方式, 计算收益
			if (LiquidAsset.TYPE_CASH_FUND.equals(asset.getType())) {
				profit = part.getHoldAmount().multiply(profitRate).setScale(2, RoundingMode.HALF_UP);
				basic = part.getHoldAmount();
			}

			// 协定存款, 没有复利, 每次都以分仓的持仓本金计算收益
			if (LiquidAsset.TYPE_AGREEMENT_DEPOSIT.equals(asset.getType())) {
				profit = part.getInvestAmount().multiply(profitRate).setScale(2, RoundingMode.HALF_UP);
				basic = part.getInvestAmount();
			}

			// 重新计算估值时的增长
			BigDecimal incr = profit;
			if (currentPartEstimatesMap.containsKey(part.getOid())) {
				PortfolioLiquidPartEstimateEntity currentPartEstimate = currentPartEstimatesMap.get(part.getOid());
				incr = profit.subtract(currentPartEstimate.getProfit());
			}

			pe.setLastEstimate(part.getHoldAmount());
			pe.setLastPriceRatio(part.getPriceRatio());
			pe.setLastUnitNet(part.getUnitNet());
			pe.setBasic(basic);
			pe.setEstimate(part.getHoldAmount().add(profit));
			if (part.getInvestAmount().signum() == 0) {
				pe.setPriceRatio(BigDecimal.ZERO);
			} else {
				pe.setPriceRatio(part.getHoldAmount().add(profit).divide(part.getInvestAmount(), 8, RoundingMode.HALF_UP));
			}
			pe.setUnitNet(part.getUnitNet());
			pe.setProfit(profit);
			pe.setProfitRate(profitRate);
			pe.setEstimateDate(estimateDate);
			pe.setEstimateTime(new Timestamp(System.currentTimeMillis()));

			this.portfolioLiquidPartEstimateDao.save(pe);

			this.portfolioLiquidHoldPartService.estimate(part, incr);

			increase = increase.add(incr);

		}

		return increase;

	}

	@Transactional
	public BigDecimal flushEstimate(PortfolioLiquidHoldEntity hold, LiquidAssetYield... yields) {
		BigDecimal estimate = BigDecimal.ZERO;
		Date minDate = null;
		Map<String, LiquidAssetYield> yieldsMap = new HashMap<String, LiquidAssetYield>();
		for (LiquidAssetYield yield : yields) {
			yieldsMap.put(yield.getProfitDate().toString(), yield);
			if (null == minDate) {
				minDate = yield.getProfitDate();
			} else {
				minDate = new Date(Math.min(yield.getProfitDate().getTime(), minDate.getTime()));
			}
		}

		List<PortfolioLiquidHoldPartEntity> parts = this.portfolioLiquidHoldPartService.findHoldingHold(hold);
		
		return estimate;
	}

}
