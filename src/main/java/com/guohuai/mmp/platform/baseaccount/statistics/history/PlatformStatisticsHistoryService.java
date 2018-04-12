package com.guohuai.mmp.platform.baseaccount.statistics.history;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guohuai.component.util.BeanUtil;
import com.guohuai.component.util.DateUtil;
import com.guohuai.component.util.StringUtil;
import com.guohuai.mmp.platform.StaticProperties;
import com.guohuai.mmp.platform.baseaccount.statistics.PlatformStatisticsDao;
import com.guohuai.mmp.platform.baseaccount.statistics.PlatformStatisticsEntity;

@Service

public class PlatformStatisticsHistoryService {

	Logger logger = LoggerFactory.getLogger(PlatformStatisticsHistoryService.class);

	@Autowired
	private PlatformStatisticsDao platformStatisticsDao;
	@Autowired
	private PlatformStatisticsHistoryDao platformStatisticsHistoryDao;


	@Transactional
	public void cp2His() {
		Date date = StaticProperties.isIs15() ? DateUtil.getSqlDate() : DateUtil.getBeforeDate();
		List<PlatformStatisticsEntity> entityList = this.platformStatisticsDao.findAll();

		List<PlatformStatisticsHistoryEntity> newList = new ArrayList<PlatformStatisticsHistoryEntity>();
		for (PlatformStatisticsEntity entity : entityList) {
			PlatformStatisticsHistoryEntity dest = new PlatformStatisticsHistoryEntity();
			BeanUtil.copy(dest, entity);
			dest.setOid(StringUtil.uuid());
			dest.setConfirmDate(date);
			dest.setCreateTime(DateUtil.getSqlCurrentDate());
			dest.setUpdateTime(DateUtil.getSqlCurrentDate());
			newList.add(dest);
		}
		
		// 保存新数据
		this.platformStatisticsHistoryDao.save(newList);
	}


	public List<PlatformStatisticsHistoryEntity> getLatest30UserCurve() {
		List<PlatformStatisticsHistoryEntity> list = this.platformStatisticsHistoryDao.getLatest30UserCurve();
		Collections.sort(list, new Comparator<PlatformStatisticsHistoryEntity>() {

			@Override
			public int compare(PlatformStatisticsHistoryEntity o1, PlatformStatisticsHistoryEntity o2) {
				return o1.getConfirmDate().compareTo(o2.getConfirmDate());
			}
			
		});
		return list;
	}
}
