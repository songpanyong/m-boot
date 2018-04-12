package com.guohuai.mmp.investor.baseaccount.check;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.DateUtil;
import com.guohuai.mmp.investor.baseaccount.detailcheck.DetailCheckService;
import com.guohuai.mmp.job.lock.JobLockEntity;
import com.guohuai.mmp.job.lock.JobLockService;
import com.guohuai.mmp.job.log.JobLogEntity;
import com.guohuai.mmp.job.log.JobLogFactory;
import com.guohuai.mmp.job.log.JobLogService;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class CheckScheduleService {

	@Autowired
	private CheckService checkService;
	@Autowired
	private DetailCheckService detailCheckService;
	@Autowired
	private JobLockService jobLockService;
	@Autowired
	private JobLogService jobLogService;
	
	public void platformCheck() {
		if (this.jobLockService.getRunPrivilege(JobLockEntity.JOB_jobId_cronCheck)) {
			JobLogEntity jobLog = JobLogFactory.getInstance(JobLockEntity.JOB_jobId_cronCheck);
			
			String currentCheckTime = DateUtil.getCurrStrDate();
			try {
				// 平台余额对账
				this.checkService.generateCheckOrders(currentCheckTime);
				// 资金明细对账
				this.detailCheckService.generateDetailCheck(currentCheckTime);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				jobLog.setJobMessage(AMPException.getStacktrace(e));
				jobLog.setJobStatus(JobLogEntity.JOB_jobStatus_failed);
			}
			jobLog.setBatchEndTime(DateUtil.getSqlCurrentDate());
			this.jobLogService.saveEntity(jobLog);
			this.jobLockService.resetJob(JobLockEntity.JOB_jobId_cronCheck);
		} 
	}
}
