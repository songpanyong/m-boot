package com.guohuai.ams.liquidAsset.yield;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.guohuai.ams.liquidAsset.LiquidAsset;

public interface LiquidAssetYieldDao extends JpaRepository<LiquidAssetYield, String>, JpaSpecificationExecutor<LiquidAssetYield> {

	@Modifying
	@Query("delete from LiquidAssetYield cr where cr.liquidAsset.oid=?1 and cr.profitDate=?2")
	public void deleteByProfitDate(String liquidAssetOid, Date profitDate);

	@Query("select max(profitDate) from LiquidAssetYield cr where cr.liquidAsset.oid=?1")
	public Date findmaxProfitDateByOid(String oid);

	public LiquidAssetYield findByLiquidAssetAndProfitDate(LiquidAsset asset, Date profitDate);

}
