package com.guohuai.mmp.platform.channel.statistics;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guohuai.ams.channel.ChannelService;
import com.guohuai.component.util.DateUtil;

@Service
public class PlatformChannelStatisticsService {

	Logger logger = LoggerFactory.getLogger(PlatformChannelStatisticsService.class);

	@Autowired
	private PlatformChannelStatisticsDao platformChannelStatisticsDao;
	@Autowired
	private ChannelService channelService;
	
	
	public void ys() {
		List<Object[]> list = platformChannelStatisticsDao.ys();
		List<PlatformChannelYsPojo> pojos = new ArrayList<PlatformChannelYsPojo>();
		for (int i = 0; i < list.size(); i++) {
				Object[] objArr = list.get(i);
				PlatformChannelYsPojo pojo = new PlatformChannelYsPojo();
				pojo.setChannelOid((String)objArr[0]);
				pojo.setTodayInvestAmount((BigDecimal)objArr[1]);
		}
		for (PlatformChannelYsPojo pojo : pojos) {
			PlatformChannelStatisticsEntity latestEn = this.platformChannelStatisticsDao.getLatest(pojo.getChannelOid());
			PlatformChannelStatisticsEntity entity = new PlatformChannelStatisticsEntity();
			entity.setChannel(channelService.getOne(pojo.getChannelOid()));
			entity.setTodayInvestAmount(pojo.getTodayInvestAmount());
			if (null == latestEn) {
				entity.setTotalInvestAmount(pojo.getTodayInvestAmount());
			} else {
				entity.setTotalInvestAmount(latestEn.getTotalInvestAmount().add(pojo.getTodayInvestAmount()));
			}
			entity.setInvestDate(DateUtil.getBeforeDate());
			saveEntity(entity);
		}
	}

	private PlatformChannelStatisticsEntity saveEntity(PlatformChannelStatisticsEntity entity) {
		return this.platformChannelStatisticsDao.save(entity);
	}
	
	public List<PlatformChannelStatisticsEntity> getTopFive() {
		List<PlatformChannelStatisticsEntity> topFiveList = this.platformChannelStatisticsDao.getTopFive();
		return topFiveList;
	}
	
	
}
