package com.guohuai.ams.portfolio20.estimate;

import java.sql.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.guohuai.ams.portfolio.entity.PortfolioEntity;

/**
 * @author created by Arthur
 * @date 2017年2月24日 - 下午6:02:17
 */
public interface PortfolioEstimateDao extends JpaRepository<PortfolioEstimateEntity, String>, JpaSpecificationExecutor<PortfolioEstimateEntity> {

	public PortfolioEstimateEntity findByPortfolioAndEstimateDate(PortfolioEntity portfolio, Date estimateDate);

	@Query("select max(estimateDate) from PortfolioEstimateEntity cr where cr.portfolio.oid=?1")
	@Modifying
	public Date getLastEstimateDate(String portfolioOid);
	
	@Query("select sum(chargefee) from PortfolioEstimateEntity cr where cr.portfolio.oid=?1")
	@Modifying
	public Date getTotalChargefee(String portfolioOid);
	
	@Query(value = "select * from t_gam_portfolio_estimate where portfolioOid=?1 and estimateDate =?2 limit 1", nativeQuery = true)
	public PortfolioEstimateEntity getCashEstimate(String portfolioOid, Date estimateDate);

}
