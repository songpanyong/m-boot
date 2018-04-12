package com.guohuai.mmp.sms.notify;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface SMSNotifyDao extends JpaRepository<SMSNotifyEntity, String>, JpaSpecificationExecutor<SMSNotifyEntity> {
	
	
	@Query(value = "SELECT t.* FROM T_MONEY_SMS_NOTIFY t WHERE t.errorCode != 0 AND t.notifyStatus = 'toConfirm' AND t.notifyTimes <= 20 ORDER BY t.createTime ASC LIMIT 200;", nativeQuery = true)
	public List<SMSNotifyEntity> getFailedNotify();
	
}
