package com.guohuai.mmp.platform.reserved.account.cashflow;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ReservedAccountCashFlowDao extends JpaRepository<ReservedAccountCashFlowEntity, String>, JpaSpecificationExecutor<ReservedAccountCashFlowEntity> {
	

	

}
