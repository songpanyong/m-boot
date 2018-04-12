package com.guohuai.ams.portfolio20.estimate.liquid.part;

import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.guohuai.ams.portfolio20.liquid.hold.PortfolioLiquidHoldEntity;

/**
 * @author created by Arthur
 * @date 2017年2月24日 - 上午10:40:07
 */
public interface PortfolioLiquidPartEstimateDao extends JpaRepository<PortfolioLiquidPartEstimateEntity, String>, JpaSpecificationExecutor<PortfolioLiquidPartEstimateEntity> {

	public List<PortfolioLiquidPartEstimateEntity> findByHoldAndEstimateDate(PortfolioLiquidHoldEntity hold, Date estimateDate);

}
