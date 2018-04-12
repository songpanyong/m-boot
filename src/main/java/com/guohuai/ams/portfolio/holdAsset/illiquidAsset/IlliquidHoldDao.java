package com.guohuai.ams.portfolio.holdAsset.illiquidAsset;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface IlliquidHoldDao
		extends JpaRepository<IlliquidHoldEntity, String>, JpaSpecificationExecutor<IlliquidHoldEntity> {

	@Query(value = "SELECT * FROM T_GAM_PORTFOLIO_ILLIQUID_ASSET a WHERE a.illiquid.oid = ?1 AND a.portfolio.oid = ?2", nativeQuery = true)
	public IlliquidHoldEntity findIlliquidHoldInfo(String illiquidAssetOid, String portfolioOid);
}
