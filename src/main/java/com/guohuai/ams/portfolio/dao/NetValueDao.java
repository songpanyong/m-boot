package com.guohuai.ams.portfolio.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.guohuai.ams.portfolio.entity.NetValueEntity;

public interface NetValueDao extends JpaRepository<NetValueEntity, String>, JpaSpecificationExecutor<NetValueEntity> {

}
