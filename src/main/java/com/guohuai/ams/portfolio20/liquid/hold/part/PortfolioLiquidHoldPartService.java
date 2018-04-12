package com.guohuai.ams.portfolio20.liquid.hold.part;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guohuai.ams.portfolio20.liquid.hold.PortfolioLiquidHoldEntity;
import com.guohuai.ams.portfolio20.liquid.hold.part.redeem.PortfolioLiquidHoldPartRedeemEntity;
import com.guohuai.ams.portfolio20.liquid.hold.part.redeem.PortfolioLiquidHoldPartRedeemService;
import com.guohuai.ams.portfolio20.order.MarketOrderEntity;
import com.guohuai.basic.common.DateUtil;
import com.guohuai.basic.common.StringUtil;

@Service
public class PortfolioLiquidHoldPartService {

	@Autowired
	private PortfolioLiquidHoldPartDao portfolioLiquidHoldPartDao;
	@Autowired
	private PortfolioLiquidHoldPartRedeemService portfolioLiquidHoldPartRedeemService;

	@Transactional
	public PortfolioLiquidHoldPartEntity newHoldPart(PortfolioLiquidHoldEntity hold, MarketOrderEntity order) {
		PortfolioLiquidHoldPartEntity p = new PortfolioLiquidHoldPartEntity();
		p.setOid(StringUtil.uuid());
		p.setHold(hold);
		p.setLiquidAsset(order.getLiquidAsset());
		p.setPortfolio(order.getPortfolio());
		p.setMarketOrder(order);
		p.setHoldAmount(order.getOrderAmount());
		p.setHoldShare(order.getOrderAmount());
		p.setInvestAmount(order.getOrderAmount());
		p.setInvestShare(order.getOrderAmount());
		p.setFreezeHoldAmount(BigDecimal.ZERO);
		p.setUnitNet(BigDecimal.ONE);
		p.setPriceRatio(BigDecimal.ONE);
		p.setInvestDate(new Timestamp(System.currentTimeMillis()));
		p.setValueDate(DateUtil.addDays(new java.sql.Date(System.currentTimeMillis()), 1));
		p.setHoldState(PortfolioLiquidHoldPartEntity.HOLD_STATE_HOLDING);
		p = this.portfolioLiquidHoldPartDao.save(p);
		return p;
	}

	public List<PortfolioLiquidHoldPartEntity> findHoldingHold(PortfolioLiquidHoldEntity hold) {
		return this.portfolioLiquidHoldPartDao.findByHoldAndHoldState(hold, PortfolioLiquidHoldPartEntity.HOLD_STATE_HOLDING);
	}

	@Transactional
	public List<PortfolioLiquidHoldPartEntity> freeze(MarketOrderEntity order, PortfolioLiquidHoldEntity hold) {
		List<PortfolioLiquidHoldPartEntity> parts = this.findHoldingHold(hold);

		// 剩余需要冻结的金额
		BigDecimal leftShares = order.getOrderAmount();

		for (Iterator<PortfolioLiquidHoldPartEntity> iterator = parts.iterator(); iterator.hasNext();) {
			PortfolioLiquidHoldPartEntity part = iterator.next();

			// 该分仓可以冻结的金额
			BigDecimal usefulAmount = part.getHoldAmount().subtract(part.getFreezeHoldAmount()).setScale(2, RoundingMode.HALF_UP);

			// 該分倉有可用余额, 则继续操作
			if (usefulAmount.signum() > 0) {
				// 该分仓操作冻结的金额, 取 需要冻结的金额和该分仓可操作冻结的份额中较小的一个
				BigDecimal freezeShares = leftShares.compareTo(usefulAmount) >= 0 ? usefulAmount : leftShares;
				part.setFreezeHoldAmount(part.getFreezeHoldAmount().add(freezeShares).setScale(2, RoundingMode.HALF_UP));
				leftShares = leftShares.subtract(freezeShares);

				// 记录分仓赎回明细
				this.portfolioLiquidHoldPartRedeemService.frezee(order, part, freezeShares);

				this.portfolioLiquidHoldPartDao.save(part);

				// 如果全部金额都冻结完成了， 直接终止循环
				if (leftShares.signum() <= 0) {
					break;
				}
			}

		}
		return parts;
	}

	@Transactional
	public void unfreeze(PortfolioLiquidHoldPartRedeemEntity redeem) {
		PortfolioLiquidHoldPartEntity part = redeem.getParts();
		part.setFreezeHoldAmount(part.getFreezeHoldAmount().subtract(redeem.getRedeemShare()));
		this.portfolioLiquidHoldPartDao.save(part);
	}

	// 返回值是实际赎回的本金和预期的偏差
	@Transactional
	public BigDecimal subHoldPart(MarketOrderEntity order, PortfolioLiquidHoldPartEntity part, PortfolioLiquidHoldPartRedeemEntity redeem) {
		// 再算一次本金和收益
		part.setHoldAmount(part.getHoldAmount().subtract(redeem.getRedeemAmount()));
		part.setHoldShare(part.getHoldShare().subtract(redeem.getRedeemShare()));
		part.setInvestAmount(part.getInvestAmount().subtract(redeem.getRedeemCapital()));
		part.setInvestShare(part.getInvestShare().subtract(redeem.getRedeemCapital()));
		part.setFreezeHoldAmount(part.getFreezeHoldAmount().subtract(redeem.getRedeemShare()));

		BigDecimal x = BigDecimal.ZERO;
		if (part.getHoldShare().signum() <= 0) {
			// 份额已经赎清了， 还有本金留存
			if (part.getInvestAmount().signum() > 0) {
				x = part.getInvestAmount();
			}
			part.setHoldState(PortfolioLiquidHoldPartEntity.HOLD_STATE_CLOSED);
		}
		if (order.getForceClose().equals("YES")) {
			part.setHoldState(PortfolioLiquidHoldPartEntity.HOLD_STATE_CLOSED);
		}

		this.portfolioLiquidHoldPartDao.save(part);
		return x;
	}

	@Transactional
	public void passFreeze(MarketOrderEntity order, PortfolioLiquidHoldEntity hold) {
		List<PortfolioLiquidHoldPartEntity> parts = this.findHoldingHold(hold);
		// 剩余需要解冻的金额
		BigDecimal leftShares = order.getOrderAmount();

		for (Iterator<PortfolioLiquidHoldPartEntity> iterator = parts.iterator(); iterator.hasNext();) {
			PortfolioLiquidHoldPartEntity part = iterator.next();

			// 该分仓可以冻结的金额
			BigDecimal usefulAmount = part.getHoldAmount().subtract(part.getFreezeHoldAmount()).setScale(2, RoundingMode.HALF_UP);

			// 該分倉有可用余额, 则继续操作
			if (usefulAmount.signum() > 0) {
				// 该分仓操作冻结的金额, 取 需要冻结的金额和该分仓可操作冻结的份额中较小的一个
				//BigDecimal freezeShares = leftShares.compareTo(usefulAmount) >= 0 ? usefulAmount : leftShares;
				part.setFreezeHoldAmount(part.getFreezeHoldAmount().subtract(leftShares).setScale(2, RoundingMode.HALF_UP));
				part.setHoldAmount(part.getHoldAmount().subtract(leftShares));
				if (usefulAmount.signum() == 0) {
					part.setHoldState(PortfolioLiquidHoldPartEntity.HOLD_STATE_CLOSED);
				}

				this.portfolioLiquidHoldPartDao.save(part);

				// 如果全部金额都冻结完成了， 直接终止循环
				if (leftShares.signum() <= 0) {
					break;
				}
			}

		}
	}

	@Transactional
	public void estimate(PortfolioLiquidHoldPartEntity part, BigDecimal profit) {
		part.setHoldAmount(part.getHoldAmount().add(profit));
		part.setHoldShare(part.getHoldShare().add(profit));
		if (part.getInvestShare().signum() != 0) {
			part.setPriceRatio(part.getHoldShare().divide(part.getInvestShare(), 8, RoundingMode.HALF_UP));
		}
		this.portfolioLiquidHoldPartDao.save(part);
	}

}
