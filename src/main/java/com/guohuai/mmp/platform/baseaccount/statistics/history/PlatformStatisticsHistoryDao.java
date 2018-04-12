package com.guohuai.mmp.platform.baseaccount.statistics.history;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface PlatformStatisticsHistoryDao extends JpaRepository<PlatformStatisticsHistoryEntity, String>,
		JpaSpecificationExecutor<PlatformStatisticsHistoryEntity> {
	
	
	@Query(value = "select * from T_MONEY_PLATFORM_STATISTICS_HISTORY order by confirmDate desc limit 30", nativeQuery = true)
	List<PlatformStatisticsHistoryEntity> getLatest30UserCurve();


}
