package com.guohuai.ams.portfolio20.net.correct.order;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.guohuai.ams.portfolio.entity.PortfolioEntity;

public abstract interface PortfolioNetCorrectOrderDao extends JpaRepository<PortfolioNetCorrectOrderEntity, String>, JpaSpecificationExecutor<PortfolioNetCorrectOrderEntity> {

	public abstract List<PortfolioNetCorrectOrderEntity> findByPortfolioAndOrderState(PortfolioEntity portfolio, String orderState);

}
