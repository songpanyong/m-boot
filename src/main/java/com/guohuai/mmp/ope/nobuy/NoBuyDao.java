package com.guohuai.mmp.ope.nobuy;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface NoBuyDao extends JpaRepository<NoBuy, String>, JpaSpecificationExecutor<NoBuy> {

	NoBuy findByUserOid(String userOid);
	
}
