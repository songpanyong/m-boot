package com.guohuai.mmp.platform.tulip;

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
import com.guohuai.mmp.platform.tulip.log.TulipLogEntity;
import com.guohuai.mmp.platform.tulip.log.TulipLogService;

import lombok.extern.slf4j.Slf4j;

/**
 * 推广平台-重新发送失败的请求
 * 
 */
@Service
@Slf4j
@Transactional
public class TulipResendService {

	@Autowired
	private TulipLogService tulipLogService;
	@Autowired
	private JobLockService jobLockService;
	@Autowired
	private JobLogService jobLogService;
	@Autowired
	private TulipResendRequireNewService tulipResendRequireNewService;
	
	
	
	public void reSendTulipMessage() {
		if (this.jobLockService.getRunPrivilege(JobLockEntity.JOB_jobId_tulipResend)) {
			reSendTulipMessageLog();
		}
	}
	
	public void reSendTulipMessageLog() {
		JobLogEntity jobLog = JobLogFactory.getInstance(JobLockEntity.JOB_jobId_tulipResend);
		try {
			
			reSendTulipMessageDo();
			
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			jobLog.setJobMessage(AMPException.getStacktrace(e));
			jobLog.setJobStatus(JobLogEntity.JOB_jobStatus_failed);
		}
		jobLog.setBatchEndTime(DateUtil.getSqlCurrentDate());
		this.jobLogService.saveEntity(jobLog);
		this.jobLockService.resetJob(JobLockEntity.JOB_jobId_tulipResend);
	}
	
	
	/** 将发送推广平台失败的请求重新发送 */
	public void reSendTulipMessageDo() {

		String lastOid = "0";
		while (true) {
			List<TulipLogEntity> list = tulipLogService.getResendEntities(lastOid);
			if (list.isEmpty()) {
				break;
			}
			
			for (TulipLogEntity en : list) {
				tulipResendRequireNewService.reSendTulipMessageDo(en.getOid());
				lastOid = en.getOid();
			}
		}

	}

	


}
