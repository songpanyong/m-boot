package com.guohuai.ams.illiquidAsset.project;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface IlliquidAssetProjectDao extends JpaRepository<IlliquidAssetProject, String>,JpaSpecificationExecutor<IlliquidAssetProject>{

	@Query(value = "from IlliquidAssetProject p where p.illiquidAsset.oid = ?1")
	List<IlliquidAssetProject> findByIlliquidAssetOid(String illiquidAssetOid);

}
