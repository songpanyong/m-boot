package com.guohuai.ams.portfolio20.estimate.liquid;

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

import com.guohuai.ams.liquidAsset.LiquidAsset;
import com.guohuai.ams.liquidAsset.yield.LiquidAssetYield;
import com.guohuai.ams.liquidAsset.yield.LiquidAssetYieldService;
import com.guohuai.ams.portfolio20.estimate.PortfolioEstimateEntity;
import com.guohuai.ams.portfolio20.estimate.liquid.part.PortfolioLiquidPartEstimateService;
import com.guohuai.ams.portfolio20.liquid.hold.PortfolioLiquidHoldEntity;
import com.guohuai.ams.portfolio20.liquid.hold.PortfolioLiquidHoldService;
import com.guohuai.basic.component.exception.GHException;
import com.guohuai.component.util.StringUtil;

/**
 * @author created by Arthur
 * @date 2017年2月24日 - 下午12:29:19
 */
@Service
public class PortfolioLiquidEstimateService {

	@Autowired
	private PortfolioLiquidEstimateDao portfolioLiquidEstimateDao;
	@Autowired
	private PortfolioLiquidHoldService portfolioLiquidHoldService;
	@Autowired
	private PortfolioLiquidPartEstimateService portfolioLiquidPartEstimateService;
	@Autowired
	private LiquidAssetYieldService liquidAssetYieldService;

	@Transactional
	public BigDecimal batchEstimate(PortfolioEstimateEntity estimate, Date estimateDate) {

		// 估值的总增长额
		BigDecimal increase = BigDecimal.ZERO;

		List<PortfolioLiquidHoldEntity> holds = this.portfolioLiquidHoldService.findHoldingHold(estimate.getPortfolio());

		// 没有分仓持仓了, 不需要进行估值
		if (null == holds || holds.size() == 0) {
			return BigDecimal.ZERO;
		}

		// 今日估值, 通过此列表进行是否重复估值的判断
		List<PortfolioLiquidEstimateEntity> currentEstimates = this.portfolioLiquidEstimateDao.findByPortfolioAndEstimateDate(estimate.getPortfolio(), estimateDate);
		Map<String, PortfolioLiquidEstimateEntity> currentEstimatesMap = new HashMap<String, PortfolioLiquidEstimateEntity>();
		if (null != currentEstimates && currentEstimates.size() > 0) {
			for (PortfolioLiquidEstimateEntity e : currentEstimates) {
				currentEstimatesMap.put(e.getHold().getOid(), e);
			}
		}

		for (PortfolioLiquidHoldEntity hold : holds) {

			LiquidAsset asset = hold.getLiquidAsset();

			PortfolioLiquidEstimateEntity he = null;
			if (currentEstimatesMap.containsKey(hold.getOid())) {
				he = currentEstimatesMap.get(hold.getOid());
			} else {
				he = new PortfolioLiquidEstimateEntity();
				he.setOid(StringUtil.uuid());
				he.setHold(hold);
				he.setAsset(hold.getLiquidAsset());
				he.setPortfolio(estimate.getPortfolio());
				he.setPortfolioEstimate(estimate);

				he.setLastEstimate(hold.getHoldAmount());
				if (hold.getInvestAmount().signum() == 0) {
					he.setLastPriceRatio(BigDecimal.ZERO);
				} else {
					he.setLastPriceRatio(hold.getHoldAmount().divide(hold.getInvestAmount(), 8, RoundingMode.HALF_UP));
				}
				he.setLastUnitNet(BigDecimal.ONE);

				BigDecimal basic = BigDecimal.ZERO;
				//货币基金, 按照日复利方式, 计算收益
				if (LiquidAsset.TYPE_CASH_FUND.equals(asset.getType())) {
					basic = hold.getHoldAmount();
				}
				// 协定存款, 没有复利, 每次都以分仓的持仓本金计算收益
				if (LiquidAsset.TYPE_AGREEMENT_DEPOSIT.equals(asset.getType())) {
					basic = hold.getInvestAmount();
				}
				he.setBasic(basic);

				he.setEstimate(hold.getHoldAmount());

				BigDecimal priceRatio = BigDecimal.ZERO;
				if (hold.getInvestAmount().signum() != 0) {
					priceRatio = hold.getHoldAmount().divide(hold.getInvestAmount(), 8, RoundingMode.HALF_UP);
				}
				he.setPriceRatio(priceRatio);
				he.setUnitNet(BigDecimal.ONE);
				he.setProfit(BigDecimal.ZERO);
				he.setProfit(BigDecimal.ZERO);
				he.setEstimateDate(estimateDate);
				he.setEstimateTime(new Timestamp(System.currentTimeMillis()));

			}

			he = this.portfolioLiquidEstimateDao.save(he);

			BigDecimal profitRate = BigDecimal.ZERO;
			int contractDays = asset.getContractDays() == 0 ? 360 : asset.getContractDays();
			if (LiquidAsset.TYPE_CASH_FUND.equals(asset.getType())) {
				LiquidAssetYield yield = this.liquidAssetYieldService.findByDate(asset, estimateDate);
				if (null != yield) {
					profitRate = yield.getDailyProfit().multiply(new BigDecimal(contractDays)).divide(new BigDecimal(10000)).divide(new BigDecimal(contractDays), 8, RoundingMode.HALF_UP);
				}
			}
			if (LiquidAsset.TYPE_AGREEMENT_DEPOSIT.equals(asset.getType())) {
				profitRate = asset.getYield().divide(new BigDecimal(contractDays), 8, RoundingMode.HALF_UP);
			}

			BigDecimal profit = this.portfolioLiquidPartEstimateService.batchEstimate(he, profitRate, estimateDate);

			he.setEstimate(he.getEstimate().add(profit));
			he.setProfit(he.getProfit().add(profit));
			if (hold.getInvestAmount().signum() == 0) {
				he.setPriceRatio(BigDecimal.ZERO);
			} else {
				he.setPriceRatio(he.getEstimate().divide(hold.getInvestAmount(), 8, RoundingMode.HALF_UP));
			}
			he.setProfitRate(profitRate);
			he.setEstimateTime(new Timestamp(System.currentTimeMillis()));

			this.portfolioLiquidEstimateDao.save(he);

			this.portfolioLiquidHoldService.estimate(hold, profit, estimateDate);

			increase = increase.add(profit);
		}

		return increase;
	}

	@Transactional
	public BigDecimal flushEstimate(PortfolioLiquidHoldEntity hold, LiquidAssetYield... yields) {
		BigDecimal increase = BigDecimal.ZERO;

		// 要判断资产类型
		LiquidAsset asset = hold.getLiquidAsset();
		if (LiquidAsset.TYPE_AGREEMENT_DEPOSIT.equals(asset.getType())) {
			// 如果资产类型是协定存款, 不需要处理复利, 只是之后所有估值数据都需要更新一遍
			// 此功能暂时先不实现
			throw new GHException("协定存款暂不支持更新收益率.");
		}

		if (LiquidAsset.TYPE_CASH_FUND.equals(asset.getType())) {
			// 如果资产类型是货币基金, 那么需要按日复利方式处理.

			// 1. 调用更新分仓
			for (LiquidAssetYield yield : yields) {
				System.out.println(yield);
			}

		}

		// 如果是货币基金, 基数应该以本日的持仓加上昨日的收益为基准

		// 如果是协定存款, 则以快照的基数为准, 

		return increase;
	}

}
