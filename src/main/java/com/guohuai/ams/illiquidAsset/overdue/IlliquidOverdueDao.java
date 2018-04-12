package com.guohuai.ams.illiquidAsset.overdue;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface IlliquidOverdueDao extends JpaRepository<IlliquidOverdue, String>,JpaSpecificationExecutor<IlliquidOverdue>{

	@Query(value = "from IlliquidOverdue p where p.illiquidAsset.oid = ?1 and p.overdueEndDate is null ORDER BY updateTime DESC")
	List<IlliquidOverdue> getLastOverdueByAssetOid(String oid);
}
