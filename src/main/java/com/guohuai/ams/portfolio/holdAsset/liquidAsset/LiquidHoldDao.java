package com.guohuai.ams.portfolio.holdAsset.liquidAsset;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface LiquidHoldDao
		extends JpaRepository<LiquidHoldEntity, String>, JpaSpecificationExecutor<LiquidHoldEntity> {

	@Query(value = "SELECT * FROM T_GAM_PORTFOLIO_LIQUID_ASSET a WHERE a.illiquid.oid = ?1 AND a.portfolio.oid = ?2", nativeQuery = true)
	public LiquidHoldEntity findLiquidHoldInfo(String liquidAssetOid, String portfolioOid);
}
