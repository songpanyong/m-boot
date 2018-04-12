package com.guohuai.ams.liquidAsset;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface LiquidAssetDao extends JpaRepository<LiquidAsset, String>, JpaSpecificationExecutor<LiquidAsset> {

	@Modifying
	@Query("update LiquidAsset set dailyProfit=?2, weeklyYield=?3,profitDate=?4 where oid = ?1")
	public void liquidAssetYield(String oid, BigDecimal dailyProfit, BigDecimal weeklyYield, Date profitDate);

	@Query(value = "select * from T_GAM_LIQUID_ASSET where sn = ?1 and state != ?2", nativeQuery = true)
	public LiquidAsset findBySnAndState(String sn, String liquidStateInvalid);

	@Query(value = " from LiquidAsset a where a.state = ?1 and a.type = ?2 order by a.name asc")
	public List<LiquidAsset> findByStateAndType(String state, String type);

	@Query(value = "update LiquidAsset a set a.applyAmount = a.applyAmount + ?2 where oid = ?1")
	@Modifying
	public int incrApplyAmount(String oid, BigDecimal value);

	@Query(value = "update LiquidAsset a set a.applyAmount = a.applyAmount - ?2 where oid = ?1")
	@Modifying
	public int decrApplyAmount(String oid, BigDecimal value);

	@Query(value = "update LiquidAsset a set a.holdShare = a.holdShare + ?2 where oid = ?1")
	@Modifying
	public int incrHoldShare(String oid, BigDecimal value);

	@Query(value = "update LiquidAsset a set a.holdShare = a.holdShare - ?2 where oid = ?1")
	@Modifying
	public int decrHoldShare(String oid, BigDecimal value);

	@Query(value = "update LiquidAsset a set a.lockupShare = a.lockupShare + ?2 where oid = ?1")
	@Modifying
	public int incrLockupShare(String oid, BigDecimal value);

	@Query(value = "update LiquidAsset a set a.lockupShare = a.lockupShare - ?2 where oid = ?1")
	@Modifying
	public int decrLockupShare(String oid, BigDecimal value);
}
