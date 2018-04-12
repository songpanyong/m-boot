package com.guohuai.mmp.ope.nocard;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface NoCardDao extends JpaRepository<NoCard, String>, JpaSpecificationExecutor<NoCard> {

	NoCard findByUserOid(String userOid);
	
}
