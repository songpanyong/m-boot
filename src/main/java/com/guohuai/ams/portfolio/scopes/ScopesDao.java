package com.guohuai.ams.portfolio.scopes;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ScopesDao extends JpaRepository<ScopesEntity, String>, JpaSpecificationExecutor<ScopesEntity>  {

	@Query(value = "select * from T_GAM_INVEST_SCOPE where relationOid = ?1", nativeQuery = true)
	@Modifying
	public List<ScopesEntity> findByRelationOid(String relationOid);
	
	@Query(value = "delete from T_GAM_INVEST_SCOPE where relationOid = ?1", nativeQuery = true)
	@Modifying
	public void deleteByPid(String pid);
}
