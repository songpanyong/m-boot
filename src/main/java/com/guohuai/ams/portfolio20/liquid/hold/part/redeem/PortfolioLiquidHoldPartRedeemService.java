package com.guohuai.ams.portfolio20.liquid.hold.part.redeem;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Iterator;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guohuai.ams.portfolio20.liquid.hold.part.PortfolioLiquidHoldPartEntity;
import com.guohuai.ams.portfolio20.liquid.hold.part.PortfolioLiquidHoldPartService;
import com.guohuai.ams.portfolio20.order.MarketOrderEntity;
import com.guohuai.component.util.StringUtil;

@Service
public class PortfolioLiquidHoldPartRedeemService {
	
	@Autowired
	private PortfolioLiquidHoldPartRedeemDao portfolioLiquidHoldPartRedeemDao;
	@Autowired
	private PortfolioLiquidHoldPartService portfolioLiquidHoldPartService;

	
	@Transactional
	public PortfolioLiquidHoldPartRedeemEntity frezee(MarketOrderEntity order, PortfolioLiquidHoldPartEntity part, BigDecimal freezeShares) {
		
		PortfolioLiquidHoldPartRedeemEntity r = new PortfolioLiquidHoldPartRedeemEntity();
		r.setOid(StringUtil.uuid());
		r.setMarketOrder(order);
		r.setParts(part);
		r.setRedeemShare(freezeShares);
		r.setRedeemAmount(freezeShares.multiply(part.getUnitNet()).setScale(2, RoundingMode.HALF_UP));
		BigDecimal redeemCapital = freezeShares.divide(part.getPriceRatio(), 2, RoundingMode.HALF_UP);
		BigDecimal redeemIncome = freezeShares.subtract(redeemCapital).setScale(2, RoundingMode.HALF_UP);
		r.setRedeemCapital(redeemCapital);
		r.setRedeemIncome(redeemIncome);
		r = this.portfolioLiquidHoldPartRedeemDao.save(r);
		
		return r;
	}
	
	@Transactional
	public void unfrezee(MarketOrderEntity order) {
		List<PortfolioLiquidHoldPartRedeemEntity> redeems = this.portfolioLiquidHoldPartRedeemDao.findByMarketOrder(order);
		for (Iterator<PortfolioLiquidHoldPartRedeemEntity> iterator = redeems.iterator(); iterator.hasNext();) {
			PortfolioLiquidHoldPartRedeemEntity redeem= iterator.next();
			this.portfolioLiquidHoldPartService.unfreeze(redeem);
		}
	}
	
	/**
	 * 減倉
	 * @param order 赎回订单对象
	 * @return 返回本次赎回操作， 总减少的投资本金金额
	 */
	@Transactional
	public BigDecimal subHoldPart(MarketOrderEntity order) {
		List<PortfolioLiquidHoldPartRedeemEntity> redeems = this.portfolioLiquidHoldPartRedeemDao.findByMarketOrder(order);
		BigDecimal xs = BigDecimal.ZERO;
		for (Iterator<PortfolioLiquidHoldPartRedeemEntity> iterator = redeems.iterator(); iterator.hasNext();) {
			PortfolioLiquidHoldPartRedeemEntity redeem= iterator.next();
			
			BigDecimal x = this.portfolioLiquidHoldPartService.subHoldPart(order, redeem.getParts(), redeem);
			xs = xs.add(x).add(redeem.getRedeemCapital());
		}
		return xs;
	}
	
}
