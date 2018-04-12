package com.guohuai.mmp.publisher.investor.interest.result;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface InterestResultDao extends JpaRepository<InterestResultEntity, String>, JpaSpecificationExecutor<InterestResultEntity> {

	@Query(value = "SELECT SUM(a.successAllocateIncome),MAX(a.createTime),b.spvOid "
			+ "FROM T_MONEY_PUBLISHER_INVESTOR_INTEREST_RESULT a "
			+ "LEFT JOIN T_GAM_PRODUCT b ON a.productOid = b.oid "
			+ "WHERE a.status = 'ALLOCATED' "
			+ "GROUP BY b.spvOid", nativeQuery = true)
	List<Object[]> getTotalInterestAmount();

}
