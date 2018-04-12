package com.guohuai.mmp.platform.msgment.log;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;


public interface MsgLogDao extends JpaRepository<MsgLogEntity, String>, JpaSpecificationExecutor<MsgLogEntity> {
	
	@Query(value = "select * from T_MONEY_MSG_LOG "
			+ "where sendedTimes < limitSendTimes and nextNotifyTime < sysdate() and oid > ?1 "
			+ " and errorCode != 0 order by oid limit 2000", nativeQuery = true)
	List<MsgLogEntity> getResendEntities(String lastOid);
	
	
	@Query(value = "SELECT * FROM T_MONEY_MSG_LOG "
			+ "WHERE  objectOid = ?1  AND interfaceName IN (?2) AND createTime > ?3 AND createTime < ?4 "
			+ "ORDER BY createTime DESC LIMIT 3", nativeQuery = true)
	List<MsgLogEntity> getMsgLogByObject(String objectOid, List<String> names, Timestamp start,  Timestamp end);

}
