package com.guohuai.ams.portfolio20.estimate.illiquid.part;

import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.guohuai.ams.portfolio20.illiquid.hold.PortfolioIlliquidHoldEntity;

/**
 * @author created by Arthur
 * @date 2017年2月24日 - 上午10:32:46
 */
public interface PortfolioIlliquidPartEstimateDao extends JpaRepository<PortfolioIlliquidPartEstimateEntity, String>, JpaSpecificationExecutor<PortfolioIlliquidPartEstimateEntity> {

	public List<PortfolioIlliquidPartEstimateEntity> findByHoldAndEstimateDate(PortfolioIlliquidHoldEntity hold, Date estimateDate);

}
