package com.guohuai.ams.portfolio.holdAsset.valuations.liquidValuations;

import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LiquidValuationsDao
		extends JpaRepository<LiquidValuationsEntity, String>, JpaSpecificationExecutor<LiquidValuationsEntity> {

	public LiquidValuationsEntity findByLiquidHoldOidAndValueDate(String liquidHoldOid, Date valueDate);
	
	public List<LiquidValuationsEntity> findByPortfolioOidAndValueDate(String portfolioOid, Date valueDate);
}
