package com.guohuai.mmp.ope.schedule;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guohuai.mmp.job.lock.JobLockEntity;
import com.guohuai.mmp.job.lock.JobLockService;

@Service
public class OpeScheduleService {
	private Logger logger = LoggerFactory.getLogger(OpeScheduleService.class);
	@Autowired
	private OpeService opeService;
	@Autowired
	private JobLockService jobLockService;
	
    public void scheduler() {
		if (this.jobLockService.getRunPrivilege(JobLockEntity.JOB_jobId_opeschedule)) {
			logger.info("<-------运营查询：扫描注册用户开始---->");
			try {
				opeService.collectNoCardSchedule();					// 扫描注册用户
			}catch (Exception e) {
				logger.info("运营查询：扫描注册用户出错，错误信息："+e.getMessage());
			}
			logger.info("<-------运营查询：扫描注册用户结束---->");
			
			logger.info("<-------运营查询：扫描绑卡用户开始---->");
			try {
				opeService.collectBindCardSchedule();				// 扫描绑卡用户
			}catch (Exception e) {
				logger.info("运营查询：扫描绑卡用户出错，错误信息："+e.getMessage());
			}
			logger.info("<-------运营查询：扫描绑卡用户结束---->");
			
			logger.info("<-------运营查询：扫描充值日志开始---->");
			try {
				opeService.collectFailRechargeSchedule();	// 扫描充值日志
			}catch (Exception e) {
				logger.info("运营查询：扫描充值日志出错，错误信息："+e.getMessage());
			}
			logger.info("<-------运营查询：扫描充值日志结束---->");
			
			logger.info("<-------运营查询：扫描交易订单开始---->");
			try {
				opeService.collectNoBuySchedule();					// 扫描交易订单
			}catch (Exception e) {
				logger.info("运营查询：扫描交易订单出错，错误信息："+e.getMessage());
			}
			logger.info("<-------运营查询：扫描交易订单结束---->");
			
			this.jobLockService.resetJob(JobLockEntity.JOB_jobId_opeschedule);
		}
	}
	
}
