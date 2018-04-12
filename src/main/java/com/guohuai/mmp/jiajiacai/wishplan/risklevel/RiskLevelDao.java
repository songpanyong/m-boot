package com.guohuai.mmp.jiajiacai.wishplan.risklevel;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RiskLevelDao extends JpaRepository<RiskLevelEntity,String> {

	List<RiskLevelEntity> queryByUserOid(String userOid);
	
	@Transactional
	@Modifying
	@Query(value = "update T_RISKLEVEL_ENTITY r set r.riskLevel=:riskLevel where r.oid=:oid", nativeQuery = true)
	int updateEntityByOid(@Param("oid") String oid, @Param("riskLevel") String riskLevel);

	RiskLevelEntity findByUserOid(String investorOid);
	
	@Query(value = "SELECT riskLevel FROM T_RISKLEVEL_ENTITY where userOid = ?1", nativeQuery = true)
	String selectRiskLevel(String userOid);

}
