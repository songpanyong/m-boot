package com.guohuai.ams.illiquidAsset.repaymentPlan;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;

import com.guohuai.ams.illiquidAsset.IlliquidAsset;

public interface IlliquidAssetRepaymentPlanDao extends JpaRepository<IlliquidAssetRepaymentPlan, String>,JpaSpecificationExecutor<IlliquidAssetRepaymentPlan>{

	
	public List<IlliquidAssetRepaymentPlan> findByIlliquidAsset(IlliquidAsset asset);
	
	@Modifying
	public int deleteByIlliquidAsset(IlliquidAsset asset);
	
}
