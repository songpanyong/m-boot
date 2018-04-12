package com.guohuai.mmp.platform.baseaccount.statistics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.DateUtil;
import com.guohuai.mmp.job.JobEnum;
import com.guohuai.mmp.job.lock.JobLockService;
import com.guohuai.mmp.job.log.JobLogEntity;
import com.guohuai.mmp.job.log.JobLogFactory;
import com.guohuai.mmp.job.log.JobLogService;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
public class PlatformStatisticsScheduleService {

	@Autowired
	private JobLockService jobLockService;
	@Autowired
	private JobLogService jobLogService;
	@Autowired
	private PlatformStatisticsScheduleBaseService platformStatisticsScheduleBaseService;

	/**
	 * 手动全量统计
	 */
	public BaseResp handStatisticsAllSchedule() {
		BaseResp resp = new BaseResp();
		if (this.jobLockService.getRunPrivilege(JobEnum.JOB_jobId_platFormStatisticsIncrementShedule.getJobId())) {
			this.handTotalLoanAmountStatisticsDo();// 累计借款总额统计
			this.handTotalReturnAmountStatisticsDo();// 累计还款总额统计
			this.handTotalInterestAmountStatisticsDo();// 累计付息总额统计
			this.handPublisherDepositAmountStatisticsDo();// 发行人充值总额统计
			this.handPublisherWithdrawAmountStatisticsDo();// 发行人提现总额统计
			this.handInvestorDepositStatisticsDo();// 投资人充值总额统计
			this.handInvestorWithdrawStatisticsDo();// 投资人提现总额统计
			this.handCouponStatisticsDo();// 卡券统计
			
			this.jobLockService.resetJob(JobEnum.JOB_jobId_platFormStatisticsIncrementShedule.getJobId());
		}else{
			resp.setErrorCode(-1);
			resp.setErrorMessage("请重试");
		}
		
		return resp;
	}
	
	/**
	 * 定时任务增量统计
	 */
	public void statisticsIncrementSchedule() {
		if (this.jobLockService.getRunPrivilege(JobEnum.JOB_jobId_platFormStatisticsIncrementShedule.getJobId())) {
			this.totalLoanAmountStatisticsDo();// 累计借款总额统计
			this.totalReturnAmountStatisticsDo();// 累计还款总额统计
			this.totalInterestAmountStatisticsDo();// 累计付息总额统计
			this.publisherDepositAmountStatisticsDo();// 发行人充值总额统计
			this.publisherWithdrawAmountStatisticsDo();// 发行人提现总额统计
			this.investorDepositStatisticsDo();// 投资人充值总额统计
			this.investorWithdrawStatisticsDo();// 投资人提现总额统计
			this.couponStatisticsDo();// 卡券统计
			
			this.jobLockService.resetJob(JobEnum.JOB_jobId_platFormStatisticsIncrementShedule.getJobId());
		} 
	}

	/**
	 * 定时任务全量统计
	 */
	public void statisticsAllSchedule() {
		if (this.jobLockService.getRunPrivilege(JobEnum.JOB_jobId_platFormStatisticsAllShedule.getJobId())) {
			this.registerStatisticsDo();	// 注册人数统计
			this.verifiedInvestorStatisticsDo();	// 实名统计
			this.publisherStatisticsDo();	// 发行人统计
			this.publisherTotalTLoanStatisticsDo();	//发行人今日借款统计
			
			this.jobLockService.resetJob(JobEnum.JOB_jobId_platFormStatisticsAllShedule.getJobId());
		} 
	}
	
	
	
	private void handTotalLoanAmountStatisticsDo() {
		JobLogEntity jobLog = JobLogFactory.getInstance(JobEnum.JOB_jobId_platFormStatisticsHandAllShedule.getJobId());
		try {
			log.info("*****（全量）平台统计-累计借款总额统计-开始*****");
			this.platformStatisticsScheduleBaseService.handTotalLoanAmountStatisticsDo();		// 累计借款总额统计
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			jobLog.setJobMessage(AMPException.getStacktrace(e));
			jobLog.setJobStatus(JobLogEntity.JOB_jobStatus_failed);
			log.error("*****（全量）平台统计-累计借款总额统计-错误:"+e.getMessage()+"");
		}
		
		jobLog.setBatchEndTime(DateUtil.getSqlCurrentDate());
		this.jobLogService.saveEntity(jobLog);
		log.info("*****（全量）平台统计-累计借款总额统计-结束*****");
	}
	private void handTotalReturnAmountStatisticsDo() {
		JobLogEntity jobLog = JobLogFactory.getInstance(JobEnum.JOB_jobId_platFormStatisticsHandAllShedule.getJobId());
		try {
			log.info("*****（全量）平台统计-累计还款总额统计-开始*****");
			this.platformStatisticsScheduleBaseService.handTotalReturnAmountStatisticsDo();	// 累计还款总额统计
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			jobLog.setJobMessage(AMPException.getStacktrace(e));
			jobLog.setJobStatus(JobLogEntity.JOB_jobStatus_failed);
			log.error("*****（全量）平台统计-累计还款总额统计-错误:"+e.getMessage()+"");
		}

		jobLog.setBatchEndTime(DateUtil.getSqlCurrentDate());
		this.jobLogService.saveEntity(jobLog);
		log.info("*****（全量）平台统计-累计还款总额统计-结束*****");
	}
	private void handTotalInterestAmountStatisticsDo() {
		JobLogEntity jobLog = JobLogFactory.getInstance(JobEnum.JOB_jobId_platFormStatisticsIncrementShedule.getJobId());
		try {
			log.info("*****（全量）平台统计-累计付息总额统计-开始*****");
			this.platformStatisticsScheduleBaseService.handTotalInterestAmountStatisticsDo();	// 累计付息总额统计
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			jobLog.setJobMessage(AMPException.getStacktrace(e));
			jobLog.setJobStatus(JobLogEntity.JOB_jobStatus_failed);
			log.error("*****（全量）平台统计-累计付息总额统计-错误:"+e.getMessage()+"");
		}

		jobLog.setBatchEndTime(DateUtil.getSqlCurrentDate());
		this.jobLogService.saveEntity(jobLog);
		log.info("*****（全量）平台统计-累计付息总额统计-结束*****");
	}
	private void handPublisherDepositAmountStatisticsDo() {
		JobLogEntity jobLog = JobLogFactory.getInstance(JobEnum.JOB_jobId_platFormStatisticsHandAllShedule.getJobId());
		try {
			log.info("*****（全量）平台统计-发行人充值总额统计-开始*****");
			this.platformStatisticsScheduleBaseService.handPublisherDepositAmountStatisticsDo();	// 发行人充值总额统计
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			jobLog.setJobMessage(AMPException.getStacktrace(e));
			jobLog.setJobStatus(JobLogEntity.JOB_jobStatus_failed);
			log.error("*****（全量）平台统计-发行人充值总额统计-错误:"+e.getMessage()+"");
		}

		jobLog.setBatchEndTime(DateUtil.getSqlCurrentDate());
		this.jobLogService.saveEntity(jobLog);
		log.info("*****（全量）平台统计-发行人充值总额统计-结束*****");
	}
	private void handPublisherWithdrawAmountStatisticsDo() {
		JobLogEntity jobLog = JobLogFactory.getInstance(JobEnum.JOB_jobId_platFormStatisticsHandAllShedule.getJobId());
		try {
			log.info("*****（全量）平台统计-发行人提现总额统计-开始*****");
			this.platformStatisticsScheduleBaseService.handPublisherWithdrawAmountStatisticsDo();	// 发行人提现总额统计
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			jobLog.setJobMessage(AMPException.getStacktrace(e));
			jobLog.setJobStatus(JobLogEntity.JOB_jobStatus_failed);
			log.error("*****（全量）平台统计-发行人提现总额统计-错误:"+e.getMessage()+"");
		}

		jobLog.setBatchEndTime(DateUtil.getSqlCurrentDate());
		this.jobLogService.saveEntity(jobLog);
		log.info("*****（全量）平台统计-发行人提现总额统计-结束*****");
	}
	private void handInvestorDepositStatisticsDo() {
		JobLogEntity jobLog = JobLogFactory.getInstance(JobEnum.JOB_jobId_platFormStatisticsHandAllShedule.getJobId());
		try {
			log.info("*****（全量）平台统计-投资人充值总额统计-开始*****");
			this.platformStatisticsScheduleBaseService.handInvestorDepositStatisticsDo();		// 投资人充值总额统计
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			jobLog.setJobMessage(AMPException.getStacktrace(e));
			jobLog.setJobStatus(JobLogEntity.JOB_jobStatus_failed);
			log.error("*****（全量）平台统计-投资人充值总额统计-错误:"+e.getMessage()+"");
		}

		jobLog.setBatchEndTime(DateUtil.getSqlCurrentDate());
		this.jobLogService.saveEntity(jobLog);
		log.info("*****（全量）平台统计-投资人充值总额统计-结束*****");
	}
	private void handInvestorWithdrawStatisticsDo() {
		JobLogEntity jobLog = JobLogFactory.getInstance(JobEnum.JOB_jobId_platFormStatisticsHandAllShedule.getJobId());
		try {
			log.info("*****（全量）平台统计-投资人提现总额统计-开始*****");
			this.platformStatisticsScheduleBaseService.handInvestorWithdrawStatisticsDo();	// 投资人提现总额统计
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			jobLog.setJobMessage(AMPException.getStacktrace(e));
			jobLog.setJobStatus(JobLogEntity.JOB_jobStatus_failed);
			log.error("*****（全量）平台统计-投资人提现总额统计-错误:"+e.getMessage()+"");
		}

		jobLog.setBatchEndTime(DateUtil.getSqlCurrentDate());
		this.jobLogService.saveEntity(jobLog);
		log.info("*****（全量）平台统计-投资人提现总额统计-结束*****");
	}

	private void handCouponStatisticsDo() {
		JobLogEntity jobLog = JobLogFactory.getInstance(JobEnum.JOB_jobId_platFormStatisticsHandAllShedule.getJobId());
		try {
			log.info("*****（全量）平台统计-卡券统计-开始*****");
			this.platformStatisticsScheduleBaseService.handCouponStatisticsDo();		// 卡券统计
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			jobLog.setJobMessage(AMPException.getStacktrace(e));
			jobLog.setJobStatus(JobLogEntity.JOB_jobStatus_failed);
			log.error("*****（全量）平台统计-卡券统计-错误:"+e.getMessage()+"");
		}
		
		jobLog.setBatchEndTime(DateUtil.getSqlCurrentDate());
		this.jobLogService.saveEntity(jobLog);
		log.info("*****（全量）平台统计-卡券统计-结束*****");
	}
	
	
	
	
	
	
	
	
	private void totalLoanAmountStatisticsDo() {
		JobLogEntity jobLog = JobLogFactory.getInstance(JobEnum.JOB_jobId_platFormStatisticsIncrementShedule.getJobId());
		try {
			log.info("*****（增量）平台统计-累计借款总额统计-开始*****");
			this.platformStatisticsScheduleBaseService.totalLoanAmountStatisticsDo();		// 累计借款总额统计
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			jobLog.setJobMessage(AMPException.getStacktrace(e));
			jobLog.setJobStatus(JobLogEntity.JOB_jobStatus_failed);
			log.error("*****（增量）平台统计-累计借款总额统计-错误:"+e.getMessage()+"");
		}
		
		jobLog.setBatchEndTime(DateUtil.getSqlCurrentDate());
		this.jobLogService.saveEntity(jobLog);
		log.info("*****（增量）平台统计-累计借款总额统计-结束*****");
	}
	private void totalReturnAmountStatisticsDo() {
		JobLogEntity jobLog = JobLogFactory.getInstance(JobEnum.JOB_jobId_platFormStatisticsIncrementShedule.getJobId());
		try {
			log.info("*****（增量）平台统计-累计还款总额统计-开始*****");
			this.platformStatisticsScheduleBaseService.totalReturnAmountStatisticsDo();	// 累计还款总额统计
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			jobLog.setJobMessage(AMPException.getStacktrace(e));
			jobLog.setJobStatus(JobLogEntity.JOB_jobStatus_failed);
			log.error("*****（增量）平台统计-累计还款总额统计-错误:"+e.getMessage()+"");
		}

		jobLog.setBatchEndTime(DateUtil.getSqlCurrentDate());
		this.jobLogService.saveEntity(jobLog);
		log.info("*****（增量）平台统计-累计还款总额统计-结束*****");
	}
	private void totalInterestAmountStatisticsDo() {
		JobLogEntity jobLog = JobLogFactory.getInstance(JobEnum.JOB_jobId_platFormStatisticsIncrementShedule.getJobId());
		try {
			log.info("*****（增量）平台统计-累计付息总额统计-开始*****");
			this.platformStatisticsScheduleBaseService.totalInterestAmountStatisticsDo();	// 累计付息总额统计
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			jobLog.setJobMessage(AMPException.getStacktrace(e));
			jobLog.setJobStatus(JobLogEntity.JOB_jobStatus_failed);
			log.error("*****（增量）平台统计-累计付息总额统计-错误:"+e.getMessage()+"");
		}

		jobLog.setBatchEndTime(DateUtil.getSqlCurrentDate());
		this.jobLogService.saveEntity(jobLog);
		log.info("*****（增量）平台统计-累计付息总额统计-结束*****");
	}
	private void publisherDepositAmountStatisticsDo() {
		JobLogEntity jobLog = JobLogFactory.getInstance(JobEnum.JOB_jobId_platFormStatisticsIncrementShedule.getJobId());
		try {
			log.info("*****（增量）平台统计-发行人充值总额统计-开始*****");
			this.platformStatisticsScheduleBaseService.publisherDepositAmountStatisticsDo();	// 发行人充值总额统计
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			jobLog.setJobMessage(AMPException.getStacktrace(e));
			jobLog.setJobStatus(JobLogEntity.JOB_jobStatus_failed);
			log.error("*****（增量）平台统计-发行人充值总额统计-错误:"+e.getMessage()+"");
		}

		jobLog.setBatchEndTime(DateUtil.getSqlCurrentDate());
		this.jobLogService.saveEntity(jobLog);
		log.info("*****（增量）平台统计-发行人充值总额统计-结束*****");
	}
	private void publisherWithdrawAmountStatisticsDo() {
		JobLogEntity jobLog = JobLogFactory.getInstance(JobEnum.JOB_jobId_platFormStatisticsIncrementShedule.getJobId());
		try {
			log.info("*****（增量）平台统计-发行人提现总额统计-开始*****");
			this.platformStatisticsScheduleBaseService.publisherWithdrawAmountStatisticsDo();	// 发行人提现总额统计
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			jobLog.setJobMessage(AMPException.getStacktrace(e));
			jobLog.setJobStatus(JobLogEntity.JOB_jobStatus_failed);
			log.error("*****（增量）平台统计-发行人提现总额统计-错误:"+e.getMessage()+"");
		}

		jobLog.setBatchEndTime(DateUtil.getSqlCurrentDate());
		this.jobLogService.saveEntity(jobLog);
		log.info("*****（增量）平台统计-发行人提现总额统计-结束*****");
	}
	private void investorDepositStatisticsDo() {
		JobLogEntity jobLog = JobLogFactory.getInstance(JobEnum.JOB_jobId_platFormStatisticsIncrementShedule.getJobId());
		try {
			log.info("*****（增量）平台统计-投资人充值总额统计-开始*****");
			this.platformStatisticsScheduleBaseService.investorDepositStatisticsDo();		// 投资人充值总额统计
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			jobLog.setJobMessage(AMPException.getStacktrace(e));
			jobLog.setJobStatus(JobLogEntity.JOB_jobStatus_failed);
			log.error("*****（增量）平台统计-投资人充值总额统计-错误:"+e.getMessage()+"");
		}

		jobLog.setBatchEndTime(DateUtil.getSqlCurrentDate());
		this.jobLogService.saveEntity(jobLog);
		log.info("*****（增量）平台统计-投资人充值总额统计-结束*****");
	}
	private void investorWithdrawStatisticsDo() {
		JobLogEntity jobLog = JobLogFactory.getInstance(JobEnum.JOB_jobId_platFormStatisticsIncrementShedule.getJobId());
		try {
			log.info("*****（增量）平台统计-投资人提现总额统计-开始*****");
			this.platformStatisticsScheduleBaseService.investorWithdrawStatisticsDo();	// 投资人提现总额统计
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			jobLog.setJobMessage(AMPException.getStacktrace(e));
			jobLog.setJobStatus(JobLogEntity.JOB_jobStatus_failed);
			log.error("*****（增量）平台统计-投资人提现总额统计-错误:"+e.getMessage()+"");
		}

		jobLog.setBatchEndTime(DateUtil.getSqlCurrentDate());
		this.jobLogService.saveEntity(jobLog);
		log.info("*****（增量）平台统计-投资人提现总额统计-结束*****");
	}

	private void couponStatisticsDo() {
		JobLogEntity jobLog = JobLogFactory.getInstance(JobEnum.JOB_jobId_platFormStatisticsIncrementShedule.getJobId());
		try {
			log.info("*****（增量）平台统计-卡券统计-开始*****");
			this.platformStatisticsScheduleBaseService.couponStatisticsDo();		// 卡券统计
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			jobLog.setJobMessage(AMPException.getStacktrace(e));
			jobLog.setJobStatus(JobLogEntity.JOB_jobStatus_failed);
			log.error("*****（增量）平台统计-卡券统计-错误:"+e.getMessage()+"");
		}
		
		jobLog.setBatchEndTime(DateUtil.getSqlCurrentDate());
		this.jobLogService.saveEntity(jobLog);
		log.info("*****（增量）平台统计-卡券统计-结束*****");
	}
	
	
	
	
	
	
	
	private void registerStatisticsDo() {
		JobLogEntity jobLog = JobLogFactory.getInstance(JobEnum.JOB_jobId_platFormStatisticsAllShedule.getJobId());
		try {
			log.info("*****（全量）平台统计-注册人数统计-开始*****");
			this.platformStatisticsScheduleBaseService.registerStatisticsDo();	// 注册人数统计
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			jobLog.setJobMessage(AMPException.getStacktrace(e));
			jobLog.setJobStatus(JobLogEntity.JOB_jobStatus_failed);
			log.error("*****（全量）平台统计-注册人数统计-错误:"+e.getMessage()+"");
		}

		jobLog.setBatchEndTime(DateUtil.getSqlCurrentDate());
		this.jobLogService.saveEntity(jobLog);
		log.info("*****（全量）平台统计-注册人数统计-结束*****");
	}

	private void verifiedInvestorStatisticsDo() {
		JobLogEntity jobLog = JobLogFactory.getInstance(JobEnum.JOB_jobId_platFormStatisticsAllShedule.getJobId());
		try {
			log.info("*****（全量）平台统计-实名人数统计-开始*****");
			this.platformStatisticsScheduleBaseService.verifiedInvestorStatisticsDo();	// 实名统计
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			jobLog.setJobMessage(AMPException.getStacktrace(e));
			jobLog.setJobStatus(JobLogEntity.JOB_jobStatus_failed);
			log.error("*****（全量）平台统计-实名人数统计-错误:"+e.getMessage()+"");
		}

		jobLog.setBatchEndTime(DateUtil.getSqlCurrentDate());
		this.jobLogService.saveEntity(jobLog);
		log.info("*****（全量）平台统计-实名人数统计-结束*****");
	}

	private void publisherStatisticsDo() {
		JobLogEntity jobLog = JobLogFactory.getInstance(JobEnum.JOB_jobId_platFormStatisticsAllShedule.getJobId());
		try {
			log.info("*****（全量）平台统计-发行人数统计-开始*****");
			this.platformStatisticsScheduleBaseService.publisherStatisticsDo();	// 发行人统计
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			jobLog.setJobMessage(AMPException.getStacktrace(e));
			jobLog.setJobStatus(JobLogEntity.JOB_jobStatus_failed);
			log.error("*****（全量）平台统计-发行人数统计-错误:"+e.getMessage()+"");
		}

		jobLog.setBatchEndTime(DateUtil.getSqlCurrentDate());
		this.jobLogService.saveEntity(jobLog);
		log.info("*****（全量）平台统计-发行人数统计-结束*****");
	}
	
	private void publisherTotalTLoanStatisticsDo() {
		JobLogEntity jobLog = JobLogFactory.getInstance(JobEnum.JOB_jobId_platFormStatisticsAllShedule.getJobId());
		try {
			log.info("*****（全量）发行人统计-今日借款统计-开始*****");
			this.platformStatisticsScheduleBaseService.publisherTotalTLoanStatisticsDo();	// 发行人统计
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			jobLog.setJobMessage(AMPException.getStacktrace(e));
			jobLog.setJobStatus(JobLogEntity.JOB_jobStatus_failed);
			log.error("*****（全量）发行人统计-今日借款统计-错误:"+e.getMessage()+"");
		}

		jobLog.setBatchEndTime(DateUtil.getSqlCurrentDate());
		this.jobLogService.saveEntity(jobLog);
		log.info("*****（全量）发行人统计-今日借款统计-结束*****");
	}
}
