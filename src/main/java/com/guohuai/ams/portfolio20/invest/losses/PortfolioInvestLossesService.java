package com.guohuai.ams.portfolio20.invest.losses;

import java.math.BigDecimal;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.guohuai.ams.portfolio20.order.MarketOrderEntity;
import com.guohuai.basic.common.StringUtil;

/**
 * @author created by Arthur
 * @date 2017年2月20日 - 下午6:45:52
 */
@Service
public class PortfolioInvestLossesService {

	@Autowired
	private PortfolioInvestLossesDao portfolioInvestLossesDao;

	@Transactional
	public void save(MarketOrderEntity order, BigDecimal investCapital, BigDecimal investIncome, BigDecimal holdShare, BigDecimal selloutShare, BigDecimal selloutPrice, BigDecimal losses) {
		PortfolioInvestLossesEntity l = new PortfolioInvestLossesEntity();
		l.setOid(StringUtil.uuid());
		l.setType(order.getType());
		l.setPortfolio(order.getPortfolio());
		l.setLiquidAsset(order.getLiquidAsset());
		l.setIlliquidAsset(order.getIlliquidAsset());
		l.setIlliquidAssetRepayment(order.getIlliquidAssetRepayment());
		l.setOrder(order);
		l.setOrderDate(order.getOrderDate());
		l.setInvestCapital(investCapital);
		l.setInvestIncome(investIncome);
		l.setHoldShare(holdShare);
		l.setSelloutShare(selloutShare);
		l.setSelloutPrice(selloutPrice);
		l.setLosses(losses);
		this.portfolioInvestLossesDao.save(l);
	}

	public Page<PortfolioInvestLossesEntity> getListByLosses(Specification<PortfolioInvestLossesEntity> spec,
			Pageable pageable) {
		return this.portfolioInvestLossesDao.findAll(spec, pageable);
	}

}
