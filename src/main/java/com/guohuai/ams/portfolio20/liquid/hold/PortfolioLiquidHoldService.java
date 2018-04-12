package com.guohuai.ams.portfolio20.liquid.hold;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guohuai.ams.liquidAsset.LiquidAsset;
import com.guohuai.ams.portfolio.entity.PortfolioEntity;
import com.guohuai.ams.portfolio20.liquid.hold.part.PortfolioLiquidHoldPartEntity;
import com.guohuai.ams.portfolio20.liquid.hold.part.PortfolioLiquidHoldPartService;
import com.guohuai.ams.portfolio20.liquid.hold.part.redeem.PortfolioLiquidHoldPartRedeemService;
import com.guohuai.ams.portfolio20.order.MarketOrderEntity;
import com.guohuai.basic.common.DateUtil;
import com.guohuai.basic.common.StringUtil;
import com.guohuai.basic.component.exception.GHException;

@Service
public class PortfolioLiquidHoldService {

	@Autowired
	private PortfolioLiquidHoldDao portfolioLiquidHoldDao;
	@Autowired
	private PortfolioLiquidHoldPartService portfolioLiquidHoldPartService;
	@Autowired
	private PortfolioLiquidHoldPartRedeemService portfolioLiquidHoldPartRedeemService;

	public PortfolioLiquidHoldEntity findHoldingHold(PortfolioEntity portfolio, LiquidAsset liquidAsset) {
		return this.portfolioLiquidHoldDao.findForHolding(portfolio, liquidAsset);
	}

	public List<PortfolioLiquidHoldEntity> findHoldingHold(PortfolioEntity portfolio) {
		return this.portfolioLiquidHoldDao.findForHolding(portfolio);
	}

	public List<PortfolioLiquidHoldEntity> findHoldingHold(LiquidAsset liquidAsset) {
		return this.portfolioLiquidHoldDao.findForHolding(liquidAsset);
	}

	@Transactional
	public PortfolioLiquidHoldEntity newHold(PortfolioEntity portfolio, LiquidAsset liquidAsset, String operator) {
		PortfolioLiquidHoldEntity h = new PortfolioLiquidHoldEntity();
		h.setOid(StringUtil.uuid());
		h.setPortfolio(portfolio);
		h.setLiquidAsset(liquidAsset);
		h.setInvestDate(new java.sql.Date(System.currentTimeMillis()));
		h.setValueDate(DateUtil.addDays(h.getInvestDate(), 1));
		h.setHoldAmount(BigDecimal.ZERO);
		h.setHoldShare(BigDecimal.ZERO);
		h.setInvestAmount(BigDecimal.ZERO);
		h.setInvestCome(BigDecimal.ZERO);
		h.setTotalPfofit(BigDecimal.ZERO);
		h.setNewPfofit(BigDecimal.ZERO);
		h.setLockupAmount(BigDecimal.ZERO);
		h.setHoldState(PortfolioLiquidHoldEntity.HOLD_STATE_HOLDING);
		h.setCreator(operator);
		h.setOperator(operator);
		h.setCreateTime(new Timestamp(System.currentTimeMillis()));
		h.setUpdateTime(new Timestamp(System.currentTimeMillis()));

		h = this.portfolioLiquidHoldDao.save(h);

		return h;
	}

	@Transactional
	public PortfolioLiquidHoldEntity mergeHold(PortfolioLiquidHoldEntity hold, PortfolioLiquidHoldPartEntity part) {
		hold.setHoldAmount(hold.getHoldAmount().add(part.getHoldAmount()));
		hold.setHoldShare(hold.getHoldShare().add(part.getHoldShare()));
		hold.setInvestAmount(hold.getInvestAmount().add(part.getInvestAmount()));
		hold = this.portfolioLiquidHoldDao.save(hold);
		return hold;
	}

	@Transactional
	public PortfolioLiquidHoldEntity freeze(MarketOrderEntity order, PortfolioLiquidHoldEntity hold) {
		if (hold.getHoldShare().subtract(hold.getLockupAmount()).subtract(order.getTradeShare()).signum() >= 0) {
			hold.setLockupAmount(hold.getLockupAmount().add(order.getTradeShare()));
			this.portfolioLiquidHoldPartService.freeze(order, hold);
			hold = this.portfolioLiquidHoldDao.save(hold);
			return hold;
		} else {
			throw new GHException("可用份额不足");
		}
	}

	@Transactional
	public PortfolioLiquidHoldEntity failRedeem(PortfolioLiquidHoldEntity hold, MarketOrderEntity order) {
		hold.setLockupAmount(hold.getLockupAmount().subtract(order.getTradeShare()));
		hold = this.portfolioLiquidHoldDao.save(hold);
		this.portfolioLiquidHoldPartRedeemService.unfrezee(order);
		return hold;

	}

	@Transactional
	public Map<String, BigDecimal> subHold(PortfolioLiquidHoldEntity hold, MarketOrderEntity order) {
		Map<String, BigDecimal> map = new HashMap<String, BigDecimal>();
		hold.setLockupAmount(hold.getLockupAmount().subtract(order.getTradeShare()));
		hold.setHoldAmount(hold.getHoldAmount().subtract(order.getOrderAmount()));
		hold.setHoldShare(hold.getHoldShare().subtract(order.getTradeShare()));

		BigDecimal capital = this.portfolioLiquidHoldPartRedeemService.subHoldPart(order);
		// 本次赎回总收益， 即投资损益
		BigDecimal income = order.getOrderAmount().subtract(capital);

		hold.setInvestAmount(hold.getInvestAmount().subtract(capital));
		hold.setInvestCome(hold.getInvestCome().subtract(income));

		if (order.getForceClose().equals("YES")) {
			hold.setHoldState(PortfolioLiquidHoldEntity.HOLD_STATE_CLOSED);
		} else {
			// 份额已经赎清了, 设置为已平仓
			if (hold.getHoldShare().signum() <= 0) {
				hold.setHoldState(PortfolioLiquidHoldEntity.HOLD_STATE_CLOSED);
			}
		}

		map.put("CAPITAL", capital);
		map.put("INCOME", income);

		if (order.getForceClose().equals("YES")) {
			map.put("LEFT_OFFSET_CAPITAL", hold.getInvestAmount());
			map.put("LEFT_OFFSET_INCOME", hold.getInvestCome());
		} else {
			map.put("LEFT_OFFSET_CAPITAL", BigDecimal.ZERO);
			map.put("LEFT_OFFSET_INCOME", BigDecimal.ZERO);
		}

		hold = this.portfolioLiquidHoldDao.save(hold);

		return map;
	}

	@Transactional
	public void estimate(PortfolioLiquidHoldEntity hold, BigDecimal estimate, Date date) {
		hold.setHoldAmount(hold.getHoldAmount().add(estimate));
		hold.setHoldShare(hold.getHoldShare().add(estimate));
		hold.setInvestCome(hold.getInvestCome().add(estimate));
		hold.setTotalPfofit(hold.getTotalPfofit().add(estimate));
		if (null != hold.getNewValueDate() && DateUtil.eq(date, hold.getNewValueDate())) {
			hold.setNewPfofit(hold.getNewPfofit().add(estimate));
		} else {
			hold.setNewPfofit(estimate);
		}
		hold.setNewValueDate(date);
		this.portfolioLiquidHoldDao.save(hold);
	}

	public PortfolioLiquidHoldEntity findByOid(String oid) {
		return this.portfolioLiquidHoldDao.findOne(oid);
	}
}
