package com.guohuai.mmp.investor.sonoperate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface SonOperateDao extends JpaRepository<SonOperateEntity, Long>, JpaSpecificationExecutor<SonOperateEntity> {
	@Query(value = "SELECT b.realName FROM t_money_investor_son_operate AS a, t_money_investor_baseaccount AS b WHERE a.pid=b.memberId AND a.bankorderoid= ?1", nativeQuery = true)
	public String getOperateName(String oid);
}
