package com.guohuai.mmp.investor.baseaccount.log;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface CouponLogDao extends JpaRepository<CouponLogEntity, String>, JpaSpecificationExecutor<CouponLogEntity> {
	@Query(value = "select oid from T_MONEY_COUPON_LOG "
			+ "where sendedTimes < limitSendTimes and nextNotifyTime < sysdate()"
			+ " and status='FAILED' limit 2000", nativeQuery = true)
	List<String> getCouponLogEntity();
}
