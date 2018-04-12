package com.guohuai.ams.portfolio20.illiquid.hold.part.repayment;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.guohuai.ams.portfolio20.order.MarketOrderEntity;

public interface PortfolioIlliquidHoldPartRepaymentDao extends JpaRepository<PortfolioIlliquidHoldPartRepaymentEntity, String>, JpaSpecificationExecutor<PortfolioIlliquidHoldPartRepaymentEntity> {

	public List<PortfolioIlliquidHoldPartRepaymentEntity> findByOrder(MarketOrderEntity order);

}
