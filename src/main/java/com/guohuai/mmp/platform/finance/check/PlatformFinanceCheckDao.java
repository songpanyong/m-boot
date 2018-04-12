package com.guohuai.mmp.platform.finance.check;

import java.sql.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;



public interface PlatformFinanceCheckDao extends JpaRepository<PlatformFinanceCheckEntity, String>, JpaSpecificationExecutor<PlatformFinanceCheckEntity>{
	
	public PlatformFinanceCheckEntity findByCheckDate(Date checkDate);
	
	@Query(value = "UPDATE T_MONEY_PLATFORM_FINANCE_CHECK "
			+ "SET confirmStatus='yes',"
			+ "operator=?2,"
			+ "updateTime=now() "
			+ " where oid=?1", nativeQuery = true)
	@Modifying
	public int checkDataConfirm(String oid, String operator);
	
	@Query(value = "update PlatformFinanceCheckEntity set checkStatus = 'checking',updateTime=now(),operator=?2 where oid = ?1 and checkStatus in ('toCheck', 'checkSuccess','checkFailed') ")
	@Modifying
	public int checking(String pdcOid,String operator);
	
	@Query(value = "update PlatformFinanceCheckEntity set checkDataSyncStatus = 'syncing' where oid = ?1 and checkDataSyncStatus in ('toSync', 'syncFailed','syncOK') ")
	@Modifying
	public int syncing(String pdcOid);
	
	@Query(value = "update PlatformFinanceCheckEntity set checkDataSyncStatus = 'syncFailed' where oid = ?1 and checkDataSyncStatus = 'syncing' ")
	@Modifying
	public int syncFailed(String pdcOid);
	
	@Query(value = "update PlatformFinanceCheckEntity set checkDataSyncStatus = 'syncOK', totalCount = ?2 where oid = ?1 and checkDataSyncStatus = 'syncing' ")
	@Modifying
	public int syncOK(String pdcOid, int totalCount);
	
	
	public PlatformFinanceCheckEntity findByCheckCode(String checkCode);
	
	@Query(value = "update PlatformFinanceCheckEntity set ldataStatus = 'prepareing' where oid = ?1 "
			+ " and ldataStatus in ('toPrepare', 'prepareFailed', 'prepared') and checkDataSyncStatus = 'syncOK'")
	@Modifying
	public int updateLdataStatusPrepareing(String checkOid);
	
	@Query(value = "update PlatformFinanceCheckEntity set ldataStatus = 'prepared' where oid = ?1 "
			+ " and ldataStatus in ('prepareing') and checkDataSyncStatus = 'syncOK'")
	@Modifying
	public int updateLdataStatusPrepared(String checkOid);
	
	
	@Query(value = "update PlatformFinanceCheckEntity set gaStatus = 'gaing' where oid = ?1 "
			+ " and gaStatus in ('toGa', 'gaFailed', 'gaOk') and ldataStatus = 'prepared' and checkDataSyncStatus = 'syncOK' ")
	@Modifying
	public int gaing(String checkOid);
	
	
	@Query(value = "update T_MONEY_PLATFORM_FINANCE_CHECK set gaStatus = 'gaOk', wrongCount = ?2, wrongLeftCount = ?2, checkStatus = IF(wrongCount > 0, 'toCheck', 'checkSuccess')  where oid = ?1 "
			+ " and gaStatus in ('gaing') and ldataStatus = 'prepared' and checkDataSyncStatus = 'syncOK' ", nativeQuery = true)
	@Modifying
	public int gaed(String checkOid, int wrongCount);
	
	
	

}
