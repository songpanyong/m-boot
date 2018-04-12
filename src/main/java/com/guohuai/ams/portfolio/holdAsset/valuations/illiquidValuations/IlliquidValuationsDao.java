package com.guohuai.ams.portfolio.holdAsset.valuations.illiquidValuations;

import java.sql.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface IlliquidValuationsDao
		extends JpaRepository<IlliquidValuationsEntity, String>, JpaSpecificationExecutor<IlliquidValuationsEntity> {

	public IlliquidValuationsEntity findByIlliquidHoldOidAndValueDate(String illiquidHoldOid, Date valueDate);
}
