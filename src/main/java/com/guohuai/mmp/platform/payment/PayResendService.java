package com.guohuai.mmp.platform.payment;

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
import com.guohuai.mmp.platform.payment.log.PayLogEntity;
import com.guohuai.mmp.platform.payment.log.PayLogService;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class PayResendService {

	@Autowired
	private PayLogService payLogService;
	@Autowired
	private JobLockService jobLockService;
	@Autowired
	private JobLogService jobLogService;
	@Autowired
	private PayResendServiceRequireNew payResendServiceRequireNew;
	
	
	public void resend() {
		if (this.jobLockService.getRunPrivilege(JobLockEntity.JOB_jobId_payResend)) {
			resendLog();
		}
	}
	
	public void resendLog() {
		JobLogEntity jobLog = JobLogFactory.getInstance(JobLockEntity.JOB_jobId_payResend);
		try {
			resendDo();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			jobLog.setJobMessage(AMPException.getStacktrace(e));
			jobLog.setJobStatus(JobLogEntity.JOB_jobStatus_failed);
		}
		jobLog.setBatchEndTime(DateUtil.getSqlCurrentDate());
		this.jobLogService.saveEntity(jobLog);
		this.jobLockService.resetJob(JobLockEntity.JOB_jobId_payResend);
	}

	public void resendDo() throws Exception {

		String lastOid = "0";
		while (true) {

			List<PayLogEntity> entities = payLogService.getResendEntities(lastOid);
			if (entities.isEmpty()) {
				break;
				
			}
			lastOid = payResendServiceRequireNew.requireNew(lastOid, entities);
		}

	}

}
