package com.guohuai.ams.portfolio20.illiquid.hold;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.guohuai.ams.illiquidAsset.IlliquidAsset;
import com.guohuai.ams.portfolio.entity.PortfolioEntity;

public interface PortfolioIlliquidHoldDao extends JpaRepository<PortfolioIlliquidHoldEntity, String>, JpaSpecificationExecutor<PortfolioIlliquidHoldEntity> {

	@Query("from PortfolioIlliquidHoldEntity h where h.portfolio = ?1 and h.illiquidAsset = ?2 and h.holdState = '" + PortfolioIlliquidHoldEntity.HOLDSTATE_HOLDING + "'")
	public PortfolioIlliquidHoldEntity findForHolding(PortfolioEntity portfolio, IlliquidAsset illiquidAsset);

	@Query("from PortfolioIlliquidHoldEntity h where h.portfolio = ?1 and h.holdState = '" + PortfolioIlliquidHoldEntity.HOLDSTATE_HOLDING + "'")
	public List<PortfolioIlliquidHoldEntity> findForHolding(PortfolioEntity portfolio);

	public List<PortfolioIlliquidHoldEntity> findByIlliquidAssetAndHoldState(IlliquidAsset asset, String holdState);

}
