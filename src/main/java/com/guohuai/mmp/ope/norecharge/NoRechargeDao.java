package com.guohuai.mmp.ope.norecharge;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface NoRechargeDao extends JpaRepository<NoRecharge, String>, JpaSpecificationExecutor<NoRecharge> {

	NoRecharge findByUserOid(String userOid);
	
}
