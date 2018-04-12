package com.guohuai.ams.portfolio20.illiquid.hold.repayment;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guohuai.basic.common.DateUtil;
import com.guohuai.component.exception.AMPException;
import com.guohuai.mmp.job.JobEnum;
import com.guohuai.mmp.job.lock.JobLockService;
import com.guohuai.mmp.job.log.JobLogEntity;
import com.guohuai.mmp.job.log.JobLogFactory;
import com.guohuai.mmp.job.log.JobLogService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PortfolioIlliquidHoldRepaymentScheduleService {

	@Autowired
	private JobLockService jobLockService;
	@Autowired
	private JobLogService jobLogService;

	@Autowired
	private PortfolioIlliquidHoldRepaymentDao portfolioIlliquidHoldRepaymentDao;

	@Transactional
	public void updateState(Date sys) {

		if (this.jobLockService.getRunPrivilege(JobEnum.JOB_jobId_updateIlliquidRepaymentState.getJobId())) {
			JobLogEntity jobLog = JobLogFactory.getInstance(JobEnum.JOB_jobId_updateIlliquidRepaymentState.getJobId());
			try {
				List<PortfolioIlliquidHoldRepaymentEntity> list = this.portfolioIlliquidHoldRepaymentDao.findByState(PortfolioIlliquidHoldRepaymentEntity.STATE_UNDUE);

				if (list != null && list.size() > 0) {
					for (PortfolioIlliquidHoldRepaymentEntity r : list) {
						if (null == r.getDueDate()) {
							r.setDueDate(DateUtil.addDays(r.getEndDate(), 1));
						}

						if (DateUtil.ge(sys, r.getDueDate())) {
							r.setState(PortfolioIlliquidHoldRepaymentEntity.STATE_PAYING);
							this.portfolioIlliquidHoldRepaymentDao.save(r);
						}
					}
				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				jobLog.setJobMessage(AMPException.getStacktrace(e));
				jobLog.setJobStatus(JobLogEntity.JOB_jobStatus_failed);
			}
			jobLog.setBatchEndTime(new Timestamp(System.currentTimeMillis()));
			this.jobLogService.saveEntity(jobLog);
			this.jobLockService.resetJob(JobEnum.JOB_jobId_updateIlliquidRepaymentState.getJobId());
		}

	}

}
