package com.guohuai.ams.portfolio20.estimate.illiquid;

import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.guohuai.ams.portfolio.entity.PortfolioEntity;

/**
 * @author created by Arthur
 * @date 2017年2月24日 - 下午5:31:59
 */
public interface PortfolioIlliquidEstimateDao extends JpaRepository<PortfolioIlliquidEstimateEntity, String>, JpaSpecificationExecutor<PortfolioIlliquidEstimateEntity> {

	public List<PortfolioIlliquidEstimateEntity> findByPortfolioAndEstimateDate(PortfolioEntity portfolio, Date estimateDate);

}
