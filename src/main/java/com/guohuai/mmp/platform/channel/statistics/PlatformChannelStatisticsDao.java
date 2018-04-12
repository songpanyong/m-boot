package com.guohuai.mmp.platform.channel.statistics;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface PlatformChannelStatisticsDao extends JpaRepository<PlatformChannelStatisticsEntity, String>,
		JpaSpecificationExecutor<PlatformChannelStatisticsEntity> {

	
	/** 平台交易额占比分析 */
	@Query(value = "SELECT SUM(A.totalInvestAmount),SUM(A.totalRedeemAmount),SUM(A.totalCashAmount) FROM T_MONEY_PLATFORM_CHANNEL_STATISTICS A WHERE A.investDate=?1 ", nativeQuery = true)
	@Modifying
	public List<Object[]> platformTradeAnalyse(Date investDate);
	
	@Query(value = "SELECT channelOid, IFNULL(SUM(orderAmount), 0) FROM T_MONEY_INVESTOR_TRADEORDER WHERE orderType IN ('invest', 'noPayInvest')"
			+ " AND orderStatus = 'confirmed' AND DATE_FORMAT(orderTime, '%Y%d%m') = DATE_SUB(CURRENT_DATE(), INTERVAL 1 DAY)"
			+ " GROUP BY channelOid ", nativeQuery = true)
	public List<Object[]> ys();
	
	
	@Query(value = "select * from T_MONEY_PLATFORM_CHANNEL_STATISTICS where channelOid = ? order by investDate limit 1", nativeQuery = true)
	public PlatformChannelStatisticsEntity getLatest(String channelOid);
	
	@Query(value = "select * from T_MONEY_PLATFORM_CHANNEL_STATISTICS where investDate = DATE_SUB(CURRENT_DATE(), INTERVAL 1 DAY) order by totalInvestAmount limit 5", nativeQuery = true)
	public List<PlatformChannelStatisticsEntity> getTopFive();
	
	
}
