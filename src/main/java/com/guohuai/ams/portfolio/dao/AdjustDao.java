package com.guohuai.ams.portfolio.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.guohuai.ams.portfolio.entity.AdjustEntity;

public interface AdjustDao extends JpaRepository<AdjustEntity, String>, JpaSpecificationExecutor<AdjustEntity> {

}
