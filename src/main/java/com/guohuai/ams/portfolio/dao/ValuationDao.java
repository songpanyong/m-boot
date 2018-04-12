package com.guohuai.ams.portfolio.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.guohuai.ams.portfolio.entity.ValuationEntity;

public interface ValuationDao
		extends JpaRepository<ValuationEntity, String>, JpaSpecificationExecutor<ValuationEntity> {

}
