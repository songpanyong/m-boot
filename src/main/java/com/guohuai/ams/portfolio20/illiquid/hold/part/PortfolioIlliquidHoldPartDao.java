package com.guohuai.ams.portfolio20.illiquid.hold.part;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.guohuai.ams.portfolio20.illiquid.hold.PortfolioIlliquidHoldEntity;

public interface PortfolioIlliquidHoldPartDao extends JpaRepository<PortfolioIlliquidHoldPartEntity, String>, JpaSpecificationExecutor<PortfolioIlliquidHoldPartEntity> {

	@Query("from PortfolioIlliquidHoldPartEntity p where p.hold = ?1 and p.holdState = ?2 order by p.investDate asc")
	public List<PortfolioIlliquidHoldPartEntity> findByHoldAndHoldState(PortfolioIlliquidHoldEntity hold, String state);
	
}
