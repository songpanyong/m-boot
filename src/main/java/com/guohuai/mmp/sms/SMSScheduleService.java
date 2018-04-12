package com.guohuai.mmp.sms;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.Collections3;
import com.guohuai.component.util.DateUtil;
import com.guohuai.mmp.job.lock.JobLockEntity;
import com.guohuai.mmp.job.lock.JobLockService;
import com.guohuai.mmp.job.log.JobLogEntity;
import com.guohuai.mmp.job.log.JobLogFactory;
import com.guohuai.mmp.job.log.JobLogService;
import com.guohuai.mmp.sms.notify.SMSNotifyEntity;
import com.guohuai.mmp.sms.notify.SMSNotifyService;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class SMSScheduleService {

	@Autowired
	private SMSNotifyService sMSNotifyService;
	@Autowired
	private SMSUtils sMSUtils;
	@Autowired
	private JobLockService jobLockService;
	@Autowired
	private JobLogService jobLogService;
	
	public void smsReSend() {
		if (this.jobLockService.getRunPrivilege(JobLockEntity.JOB_jobId_SMSSendShedule)) {
			List<SMSNotifyEntity> notifys = this.sMSNotifyService.getFailedNotify();
			if (!Collections3.isEmpty(notifys)) {

				JobLogEntity jobLog = JobLogFactory.getInstance(JobLockEntity.JOB_jobId_SMSSendShedule);
				
				try {
					for (SMSNotifyEntity notify : notifys) {
						this.sMSUtils.reSendSMS(notify);
					}
				} catch (Exception e) {
					log.error(e.getMessage(), e);
					jobLog.setJobMessage(AMPException.getStacktrace(e));
					jobLog.setJobStatus(JobLogEntity.JOB_jobStatus_failed);
				}
				jobLog.setBatchEndTime(DateUtil.getSqlCurrentDate());
				this.jobLogService.saveEntity(jobLog);
			}
			this.jobLockService.resetJob(JobLockEntity.JOB_jobId_SMSSendShedule);
		} 
	}
}
