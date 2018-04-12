package com.guohuai.mmp.investor.baseaccount.log;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.DateUtil;
import com.guohuai.mmp.job.lock.JobLockEntity;
import com.guohuai.mmp.job.lock.JobLockService;
import com.guohuai.mmp.job.log.JobLogEntity;
import com.guohuai.mmp.job.log.JobLogFactory;
import com.guohuai.mmp.job.log.JobLogService;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class TaskCouponLogService {
	@Autowired
	private CouponLogService couponLogService;
	@Autowired
	private JobLockService jobLockService;
	@Autowired
	private JobLogService jobLogService;
	@Autowired
	private TaskCouponLogServiceRequireNew taskCouponLogServiceRequireNew;
	
	public void taskUseCoupon() {

		if (this.jobLockService.getRunPrivilege(JobLockEntity.JOB_jobId_taskUseCoupon)) {
			taskUseCouponLog();
		}
	}
	
	public void taskUseCouponLog() {
		JobLogEntity jobLog = JobLogFactory.getInstance(JobLockEntity.JOB_jobId_taskUseCoupon);
		try {
			taskUseCouponDo();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			jobLog.setJobMessage(AMPException.getStacktrace(e));
			jobLog.setJobStatus(JobLogEntity.JOB_jobStatus_failed);
		}
		jobLog.setBatchEndTime(DateUtil.getSqlCurrentDate());
		this.jobLogService.saveEntity(jobLog);
		this.jobLockService.resetJob(JobLockEntity.JOB_jobId_taskUseCoupon);
	}
	
	@Transactional
	public void taskUseCouponDo() {
		List<String> oids = couponLogService.getCouponLogEntity();
		for (String oid : oids) {
			taskCouponLogServiceRequireNew.requireNew(oid);
		}
	}

	
}
