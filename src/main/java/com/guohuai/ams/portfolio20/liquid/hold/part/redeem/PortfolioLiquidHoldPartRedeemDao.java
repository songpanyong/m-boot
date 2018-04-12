package com.guohuai.ams.portfolio20.liquid.hold.part.redeem;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.guohuai.ams.portfolio20.order.MarketOrderEntity;

public interface PortfolioLiquidHoldPartRedeemDao extends JpaRepository<PortfolioLiquidHoldPartRedeemEntity, String>, JpaSpecificationExecutor<PortfolioLiquidHoldPartRedeemEntity>{

	public List<PortfolioLiquidHoldPartRedeemEntity> findByMarketOrder(MarketOrderEntity order);
	
}
