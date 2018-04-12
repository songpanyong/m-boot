package com.guohuai.mmp.jiajiacai.wishplan.plan.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.guohuai.mmp.jiajiacai.wishplan.plan.entity.BackPlanInvestEntity;

public interface BackPlanInvestDao extends JpaRepository<BackPlanInvestEntity, String> ,JpaSpecificationExecutor<BackPlanInvestEntity> {

}
