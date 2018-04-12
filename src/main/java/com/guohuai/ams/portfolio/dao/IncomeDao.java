package com.guohuai.ams.portfolio.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.guohuai.ams.portfolio.entity.IncomeEntity;

public interface IncomeDao extends JpaRepository<IncomeEntity, String>, JpaSpecificationExecutor<IncomeEntity> {

}
