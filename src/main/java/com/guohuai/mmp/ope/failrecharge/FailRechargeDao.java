package com.guohuai.mmp.ope.failrecharge;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FailRechargeDao extends JpaRepository<FailRecharge, String>, JpaSpecificationExecutor<FailRecharge> {

	FailRecharge findByUserOid(String userOid);
	
}
