package com.guohuai.mmp.backstage.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.guohuai.mmp.backstage.SonAccountRelateEntity;
import com.guohuai.mmp.investor.sonaccount.SonAccountEntity;

public interface SonAccountRelateDao extends JpaRepository<SonAccountRelateEntity, Long>, JpaSpecificationExecutor<SonAccountRelateEntity> {

}
