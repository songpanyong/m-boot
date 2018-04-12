package com.guohuai.mmp.jiajiacai.wishplan.planlist;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PlanListDao extends JpaRepository<PlanListEntity,String>{
	
	PlanListEntity findByName(String name);

	PlanListEntity findByOid(String oid);
	
	PlanListEntity findByPlanType(String type);
	
	@Query(value = "SELECT name FROM T_PLANLIST_ENTITY WHERE planType=?1", nativeQuery = true)
	public String findPlanNameByType(String planType);

}
