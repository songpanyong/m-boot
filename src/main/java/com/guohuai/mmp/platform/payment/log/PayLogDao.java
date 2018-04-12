package com.guohuai.mmp.platform.payment.log;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;


public interface PayLogDao extends JpaRepository<PayLogEntity, String>, JpaSpecificationExecutor<PayLogEntity> {
	
	@Query(value = "select * from T_MONEY_PAY_LOG "
			+ "where sendedTimes < limitSendTimes and nextNotifyTime < sysdate() and oid > ?1 "
			+ " and errorCode != 0 order by oid limit 2000", nativeQuery = true)
	List<PayLogEntity> getResendEntities(String lastOid);
	
//	@Query(value = "from PayLogEntity where iPayNo = ?1 and errorCode = 0 and handleType = 'applyCall' ")
//  取消必须成功，因为不成功的请求也需要处理回调	
	@Query(value = "from PayLogEntity where iPayNo = ?1 and handleType = 'applyCall' ") 
	PayLogEntity getSuccessPayAplly(String orderCode);
	
	@Query(value = "from PayLogEntity where orderCode = ?1 and errorCode = 0 and handleType = 'applyCall' ")
	PayLogEntity getSuccessPayApllyByOrderCode(String orderCode);
	
	@Query(value = "select * from T_MONEY_PAY_LOG where iPayNo = ?1 and errorCode = 0 and handleType = 'notify' limit 1", nativeQuery = true)
	PayLogEntity getPaySuccessNotify(String iPayNo);

	PayLogEntity findByOrderCodeAndHandleType(String orderCode, String handleType);

}
