package com.guohuai.ams.portfolio20.liquid.hold.part;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.guohuai.ams.portfolio20.liquid.hold.PortfolioLiquidHoldEntity;

public interface PortfolioLiquidHoldPartDao extends JpaRepository<PortfolioLiquidHoldPartEntity, String>, JpaSpecificationExecutor<PortfolioLiquidHoldPartEntity> {

	@Query("from PortfolioLiquidHoldPartEntity p where p.hold = ?1 and p.holdState = ?2 order by p.investDate asc")
	public List<PortfolioLiquidHoldPartEntity> findByHoldAndHoldState(PortfolioLiquidHoldEntity hold, String holdState);
	
}
