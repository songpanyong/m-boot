package com.guohuai.ams.portfolio.holdAsset;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface HoldDetDao extends JpaRepository<HoldDetEntity, String>, JpaSpecificationExecutor<HoldDetEntity> {

	@Query(value = "SELECT * FROM T_GAM_PORTFOLIO_HOLD_DET a WHERE a.portfolio.oid = ?1 AND a.assetOid = ?2"
			+ "ORDER BY a.tradeTime ASC", nativeQuery = true)
	public List<HoldDetEntity> getHoldList(String portfolioOid, String assetOid);
}
