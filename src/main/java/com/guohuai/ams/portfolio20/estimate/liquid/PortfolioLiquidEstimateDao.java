package com.guohuai.ams.portfolio20.estimate.liquid;

import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.guohuai.ams.portfolio.entity.PortfolioEntity;
import com.guohuai.ams.portfolio20.liquid.hold.PortfolioLiquidHoldEntity;

/**
 * @author created by Arthur
 * @date 2017年2月24日 - 下午12:28:59
 */
public interface PortfolioLiquidEstimateDao extends JpaRepository<PortfolioLiquidEstimateEntity, String>, JpaSpecificationExecutor<PortfolioLiquidEstimateEntity> {

	public List<PortfolioLiquidEstimateEntity> findByPortfolioAndEstimateDate(PortfolioEntity portfolio, Date estimateDate);

	public PortfolioLiquidEstimateEntity findByHoldAndEstimateDate(PortfolioLiquidHoldEntity hold, Date estimateDate);

}
