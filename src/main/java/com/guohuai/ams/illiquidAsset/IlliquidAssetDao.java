package com.guohuai.ams.illiquidAsset;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface IlliquidAssetDao extends JpaRepository<IlliquidAsset, String>, JpaSpecificationExecutor<IlliquidAsset> {

	@Query(value = "update IlliquidAsset a set a.applyAmount = a.applyAmount + ?2 where oid = ?1")
	@Modifying
	public int incrApplyAmount(String oid, BigDecimal value);

	@Query(value = "update IlliquidAsset a set a.applyAmount = a.applyAmount - ?2 where oid = ?1")
	@Modifying
	public int decrApplyAmount(String oid, BigDecimal value);

	@Query(value = "update IlliquidAsset a set a.holdShare = a.holdShare + ?2 where oid = ?1")
	@Modifying
	public int incrHoldShare(String oid, BigDecimal value);

	@Query(value = "update IlliquidAsset a set a.holdShare = a.holdShare - ?2 where oid = ?1")
	@Modifying
	public int decrHoldShare(String oid, BigDecimal value);

	@Query(value = "update IlliquidAsset a set a.holdIncome = a.holdIncome + ?2 where oid = ?1")
	@Modifying
	public int incrHoldIncome(String oid, BigDecimal value);

	@Query(value = "update IlliquidAsset a set a.holdIncome = a.holdIncome - ?2 where oid = ?1")
	@Modifying
	public int decrHoldIncome(String oid, BigDecimal value);

	@Query(value = "update IlliquidAsset a set a.lockupCapital = a.lockupCapital + ?2 where oid = ?1")
	@Modifying
	public int incrLockupCapital(String oid, BigDecimal value);

	@Query(value = "update IlliquidAsset a set a.lockupCapital = a.lockupCapital - ?2 where oid = ?1")
	@Modifying
	public int decrLockupCapital(String oid, BigDecimal value);

	@Query(value = "update IlliquidAsset a set a.lockupIncome = a.lockupIncome + ?2 where oid = ?1")
	@Modifying
	public int incrLockupIncome(String oid, BigDecimal value);

	@Query(value = "update IlliquidAsset a set a.lockupIncome = a.lockupIncome - ?2 where oid = ?1")
	@Modifying
	public int decrLockupIncome(String oid, BigDecimal value);

	public List<IlliquidAsset> findByLifeStateIn(String[] states);

	@Query(value = "update IlliquidAsset a set a.lifeState = ?2 where a.oid = ?1")
	@Modifying
	public int updateLifeState(String oid, String lifeState);

}
