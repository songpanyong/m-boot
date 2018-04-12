package com.guohuai.mmp.schedule;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.DateUtil;
import com.guohuai.mmp.investor.baseaccount.statistics.InvestorStatisticsService;
import com.guohuai.mmp.job.JobEnum;
import com.guohuai.mmp.job.lock.JobLockService;
import com.guohuai.mmp.job.log.JobLogEntity;
import com.guohuai.mmp.job.log.JobLogFactory;
import com.guohuai.mmp.job.log.JobLogService;
import com.guohuai.mmp.serialtask.SerialTaskEntity;
import com.guohuai.mmp.serialtask.SerialTaskReq;
import com.guohuai.mmp.serialtask.SerialTaskService;

/**
 * 
 * @author yuechao
 *
 */
@Service
@Transactional
public class ResetMonthService {

	private static final Logger logger = LoggerFactory.getLogger(ResetMonthService.class);
	@Autowired
	private JobLockService jobLockService;
	@Autowired
	private InvestorStatisticsService investorStatisticsService;
	@Autowired
	private JobLogService jobLogService;
	@Autowired
	private SerialTaskService serialTaskService;
	
	public void resetMonth() {
		if (this.jobLockService.getRunPrivilege(JobEnum.JOB_jobId_resetMonth.getJobId())) {
			this.resetMonthLog();
		}
	}
	
	public void resetMonthLog() {
		JobLogEntity jobLog =  JobLogFactory.getInstance(JobEnum.JOB_jobId_resetMonth.getJobId());
		try {
			resetMonthDo();
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			jobLog.setJobMessage(AMPException.getStacktrace(e));
			jobLog.setJobStatus(JobLogEntity.JOB_jobStatus_failed);
		}
		
		jobLog.setBatchEndTime(DateUtil.getSqlCurrentDate());
		this.jobLogService.saveEntity(jobLog);
		this.jobLockService.resetJob(JobEnum.JOB_jobId_resetMonth.getJobId());
	}

	public void resetMonthDo() {

		SerialTaskReq<String> req = new SerialTaskReq<String>();
		req.setTaskCode(SerialTaskEntity.TASK_taskCode_resetMonth);
		serialTaskService.createSerialTask(req);

	}

	public void resetMonthDb() {
		this.investorStatisticsService.resetMonth();
	}
	

	
}
