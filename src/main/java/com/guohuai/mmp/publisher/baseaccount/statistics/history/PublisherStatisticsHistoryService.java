package com.guohuai.mmp.publisher.baseaccount.statistics.history;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guohuai.component.util.BeanUtil;
import com.guohuai.component.util.DateUtil;
import com.guohuai.component.util.StringUtil;
import com.guohuai.mmp.platform.StaticProperties;
import com.guohuai.mmp.publisher.baseaccount.statistics.PublisherStatisticsDao;
import com.guohuai.mmp.publisher.baseaccount.statistics.PublisherStatisticsEntity;

@Service
@Transactional
public class PublisherStatisticsHistoryService {
	
	@Autowired
	private PublisherStatisticsHistoryDao publisherStatisticsHistoryDao;
	@Autowired
	private PublisherStatisticsDao publisherStatisticsDao;
	
	/**
	 * 数据CP到历史表
	 */
	@Transactional
	public void cp2His() {
		Date date = StaticProperties.isIs15() ? DateUtil.getSqlDate() : DateUtil.getBeforeDate();
		List<PublisherStatisticsEntity> entityList = this.publisherStatisticsDao.findAll();
		
		List<PublisherStatisticsHistoryEntity> newList = new ArrayList<PublisherStatisticsHistoryEntity>();
		for (PublisherStatisticsEntity entity : entityList) {
			PublisherStatisticsHistoryEntity dest = new PublisherStatisticsHistoryEntity();
			BeanUtil.copy(dest, entity);
			dest.setOid(StringUtil.uuid());
			dest.setConfirmDate(date);
			newList.add(dest);
		}
		
		this.publisherStatisticsHistoryDao.save(newList);
	}
	

	
}
