package com.guohuai.mmp.publisher.product.statistics;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface PublisherProductStatisticsDao extends JpaRepository<PublisherProductStatisticsEntity, String>,
		JpaSpecificationExecutor<PublisherProductStatisticsEntity> {

	
	
	
	@Query(value = "SELECT productOid, IFNULL(SUM(orderAmount), 0) FROM T_MONEY_INVESTOR_TRADEORDER WHERE orderType IN ('invest', 'noPayInvest')"
			+ " AND orderStatus = 'confirmed' AND DATE_FORMAT(orderTime, '%Y%d%m') = DATE_SUB(CURRENT_DATE(), INTERVAL 1 DAY)"
			+ " GROUP BY productOid ", nativeQuery = true)
	public List<Object[]> ys();
	
	
	@Query(value = "select * from T_MONEY_PUBLISHER_PRODUCT_STATISTICS where productOid = ? order by investDate limit 1", nativeQuery = true)
	public PublisherProductStatisticsEntity getLatest(String productOid);
	
	@Query(value = "select * from T_MONEY_PUBLISHER_PRODUCT_STATISTICS where investDate = DATE_SUB(CURRENT_DATE(), INTERVAL 1 DAY) order by totalInvestAmount limit 5", nativeQuery = true)
	public List<PublisherProductStatisticsEntity> getTopFive();
	
	
	@Query(value = "select * from T_MONEY_PUBLISHER_PRODUCT_STATISTICS where publisherOid = ?1 and investDate = DATE_SUB(CURRENT_DATE(), INTERVAL 1 DAY) order by totalInvestAmount limit 5", nativeQuery = true)
	public List<PublisherProductStatisticsEntity> getTopFive(String publisherOid);
}	



