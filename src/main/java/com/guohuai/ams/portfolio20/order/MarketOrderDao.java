package com.guohuai.ams.portfolio20.order;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface MarketOrderDao extends JpaRepository<MarketOrderEntity, String>, JpaSpecificationExecutor<MarketOrderEntity> {

	@Query(value = "update T_GAM_ASSET_ORDER set orderState = 'PASS' where oid = ?1 and orderState = 'SUBMIT'", nativeQuery = true)
	@Modifying
	public int pass(String oid);

	@Query(value = "update T_GAM_ASSET_ORDER set orderState = 'FAIL' where oid = ?1 and orderState = 'SUBMIT'", nativeQuery = true)
	@Modifying
	public int fail(String oid);
	
	@Query(value = "select * from T_GAM_ASSET_ORDER where type = ?1 and portfolioOid = ?2 and orderState = 'PASS' and auditTime BETWEEN ?3 AND ?4", nativeQuery = true)
	public List<MarketOrderEntity> getPortfolioTypeRecord(String type, String portfolioOid, Timestamp startTime, Timestamp endTime);
	
	@Query(value = "select * from T_GAM_ASSET_ORDER where portfolioOid = ?1 and orderState = 'PASS' and auditTime BETWEEN ?2 AND ?3", nativeQuery = true)
	public List<MarketOrderEntity> getPortfolioRecord(String assetPoolOid, Timestamp startTime, Timestamp endTime);
}
