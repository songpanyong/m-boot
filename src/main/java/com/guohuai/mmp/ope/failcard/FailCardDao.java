package com.guohuai.mmp.ope.failcard;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FailCardDao extends JpaRepository<FailCard, String>, JpaSpecificationExecutor<FailCard> {

	FailCard findByUserOid(String userOid);
	
}
