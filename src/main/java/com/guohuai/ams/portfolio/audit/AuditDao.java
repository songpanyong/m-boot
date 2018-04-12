package com.guohuai.ams.portfolio.audit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AuditDao
		extends JpaRepository<AuditEntity, String>, JpaSpecificationExecutor<AuditEntity> {

}
