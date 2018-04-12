package com.guohuai.ams.portfolio20.liquid.hold;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.guohuai.ams.liquidAsset.LiquidAsset;
import com.guohuai.ams.portfolio.entity.PortfolioEntity;

public interface PortfolioLiquidHoldDao extends JpaRepository<PortfolioLiquidHoldEntity, String>, JpaSpecificationExecutor<PortfolioLiquidHoldEntity> {

	@Query("from PortfolioLiquidHoldEntity h where h.portfolio = ?1 and h.liquidAsset = ?2 and h.holdState = '" + PortfolioLiquidHoldEntity.HOLD_STATE_HOLDING + "'")
	public PortfolioLiquidHoldEntity findForHolding(PortfolioEntity portfolio, LiquidAsset liquidAsset);

	@Query("from PortfolioLiquidHoldEntity h where h.portfolio = ?1 and h.holdState = '" + PortfolioLiquidHoldEntity.HOLD_STATE_HOLDING + "'")
	public List<PortfolioLiquidHoldEntity> findForHolding(PortfolioEntity portfolio);

	@Query("from PortfolioLiquidHoldEntity h where h.liquidAsset = ?1 and h.holdState = '" + PortfolioLiquidHoldEntity.HOLD_STATE_HOLDING + "'")
	public List<PortfolioLiquidHoldEntity> findForHolding(LiquidAsset liquidAsset);

}
