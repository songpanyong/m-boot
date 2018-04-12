package com.guohuai.mmp.schedule;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.DateUtil;
import com.guohuai.mmp.job.JobEnum;
import com.guohuai.mmp.job.lock.JobLockService;
import com.guohuai.mmp.job.log.JobLogEntity;
import com.guohuai.mmp.job.log.JobLogFactory;
import com.guohuai.mmp.job.log.JobLogService;
import com.guohuai.mmp.platform.baseaccount.statistics.history.PlatformStatisticsHistoryService;
import com.guohuai.mmp.platform.channel.statistics.PlatformChannelStatisticsService;
import com.guohuai.mmp.publisher.baseaccount.statistics.history.PublisherStatisticsHistoryService;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class YsService {
	
	@Autowired
	private JobLockService jobLockService;
	@Autowired
	private JobLogService jobLogService;
	@Autowired
	private PlatformChannelStatisticsService platformChannelStatisticsService;
	@Autowired
	private PublisherStatisticsHistoryService publisherStatisticsHistoryService;
	@Autowired
	private PlatformStatisticsHistoryService platformStatisticsHistoryService;
	

	public void ys() {
		if (this.jobLockService.getRunPrivilege(JobEnum.JOB_jobId_yesterdayStatistics.getJobId())) {
			this.ysLog();
		}
	}
	
	public void ysLog() {
		JobLogEntity jobLog =  JobLogFactory.getInstance(JobEnum.JOB_jobId_yesterdayStatistics.getJobId());
		try {
			ysDo();
			
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			jobLog.setJobMessage(AMPException.getStacktrace(e));
			jobLog.setJobStatus(JobLogEntity.JOB_jobStatus_failed);
		}
		
		jobLog.setBatchEndTime(DateUtil.getSqlCurrentDate());
		this.jobLogService.saveEntity(jobLog);
		this.jobLockService.resetJob(JobEnum.JOB_jobId_yesterdayStatistics.getJobId());
	}

	public void ysDo() {
		platformChannelStatisticsService.ys();
		platformStatisticsHistoryService.cp2His();
		publisherStatisticsHistoryService.cp2His();
		
		
	}
}
