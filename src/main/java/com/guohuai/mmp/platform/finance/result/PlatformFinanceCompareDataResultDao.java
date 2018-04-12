package com.guohuai.mmp.platform.finance.result;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface PlatformFinanceCompareDataResultDao extends JpaRepository<PlatformFinanceCompareDataResultEntity, String>, JpaSpecificationExecutor<PlatformFinanceCompareDataResultEntity>{

	@Query(value = "DELETE FROM T_MONEY_CHECK_COMPAREDATA_RESULT WHERE checkOid = ?1 ", nativeQuery = true)
	@Modifying
	public int deleteByCheckOid(String checkOid);
	
	@Query(value = "UPDATE T_MONEY_CHECK_COMPAREDATA_RESULT "
			+ "SET dealStatus=?2 "
			+ "where oid=?1 ", nativeQuery = true)
	@Modifying
	public void updateDealStatusByOid(String oid, String dealStatus);
	
	@Query(value = "SELECT count(*) FROM T_MONEY_CHECK_COMPAREDATA_RESULT "
			+ "where checkOid=?1 AND dealStatus = 'dealing' ", nativeQuery = true)
	public Long countByCheckOid(String checkOid);
	
	@Query(value = "UPDATE T_MONEY_CHECK_COMPAREDATA_RESULT "
			+ "SET dealStatus = 'dealing' "
			+ "where (orderCode = ?1 or checkOrderCode = ?1)  and dealStatus = 'toDeal' ", nativeQuery = true)
	@Modifying
	public int updateDealStatusDealingByOrderCode(String orderCode);
	
	@Query(value = "UPDATE T_MONEY_CHECK_COMPAREDATA_RESULT "
			+ "SET dealStatus = 'dealt' "
			+ "where (orderCode = ?1 or checkOrderCode = ?1)  and dealStatus = 'dealing' ", nativeQuery = true)
	@Modifying
	public int updateDealStatusDealtByOrderCode(String orderCode);
	
	

	@Query(value = "UPDATE T_MONEY_CHECK_COMPAREDATA_RESULT "
			+ " SET dealStatus = 'dealing' "
			+ " where oid = ?1 and dealStatus = 'toDeal' ", nativeQuery = true)
	@Modifying
	public int updateDealStatusDealingByOid(String crOid);
	
	@Query(value = "UPDATE T_MONEY_CHECK_COMPAREDATA_RESULT "
			+ " SET dealStatus = 'dealt' "
			+ " where oid = ?1  and dealStatus = 'dealing' ", nativeQuery = true)
	@Modifying
	public int updateDealStatusDealtByOid(String crOid);

}
