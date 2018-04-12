package com.guohuai.ams.portfolio20.illiquid.hold.repayment;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.guohuai.ams.portfolio20.illiquid.hold.PortfolioIlliquidHoldEntity;

public interface PortfolioIlliquidHoldRepaymentDao extends JpaRepository<PortfolioIlliquidHoldRepaymentEntity, String>, JpaSpecificationExecutor<PortfolioIlliquidHoldRepaymentEntity> {

	@Query("from PortfolioIlliquidHoldRepaymentEntity r where r.hold = ?1 order by r.issue asc")
	public List<PortfolioIlliquidHoldRepaymentEntity> findByHold(PortfolioIlliquidHoldEntity hold);

	@Query("from PortfolioIlliquidHoldRepaymentEntity r where r.hold.oid = ?1 order by r.issue asc")
	public List<PortfolioIlliquidHoldRepaymentEntity> findByHoldOid(String holdOid);

	public List<PortfolioIlliquidHoldRepaymentEntity> findByState(String state);

}
