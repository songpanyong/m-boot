package com.guohuai.mmp.schedule;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guohuai.ams.product.Product;
import com.guohuai.ams.product.ProductDao;
import com.guohuai.cache.service.CachePlatformService;
import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.DateUtil;
import com.guohuai.component.util.DecimalUtil;
import com.guohuai.mmp.investor.baseaccount.statistics.InvestorStatisticsService;
import com.guohuai.mmp.job.lock.JobLockEntity;
import com.guohuai.mmp.job.lock.JobLockService;
import com.guohuai.mmp.job.log.JobLogEntity;
import com.guohuai.mmp.job.log.JobLogFactory;
import com.guohuai.mmp.job.log.JobLogService;
import com.guohuai.mmp.publisher.baseaccount.statistics.PublisherStatisticsService;
import com.guohuai.mmp.publisher.hold.PublisherHoldService;
import com.guohuai.mmp.serialtask.SerialTaskEntity;
import com.guohuai.mmp.serialtask.SerialTaskReq;
import com.guohuai.mmp.serialtask.SerialTaskService;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author yuechao
 *
 */
@Service
@Transactional
@Slf4j
public class ResetTodayService {

	
	@Autowired
	private JobLockService jobLockService;
	@Autowired
	private InvestorStatisticsService investorStatisticsService;
	@Autowired
	private PublisherHoldService publisherHoldService;
	@Autowired
	private PublisherStatisticsService publisherStatisticsService;
	@Autowired
	private JobLogService jobLogService;
	@Autowired
	private SerialTaskService serialTaskService;
	@Autowired
	private CachePlatformService cachePlatformService;
	@Autowired
	private ProductDao productDao;
	
	public void resetToday() {
		if (this.jobLockService.getRunPrivilege(JobLockEntity.JOB_jobId_resetToday)) {
			this.resetTodayLog();
		}
	}
	
	public void resetTodayLog() {
		JobLogEntity jobLog =  JobLogFactory.getInstance(JobLockEntity.JOB_jobId_resetToday);
		try {
			resetTodayDo();
			
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			jobLog.setJobMessage(AMPException.getStacktrace(e));
			jobLog.setJobStatus(JobLogEntity.JOB_jobStatus_failed);
		}
		
		jobLog.setBatchEndTime(DateUtil.getSqlCurrentDate());
		this.jobLogService.saveEntity(jobLog);
		this.jobLockService.resetJob(JobLockEntity.JOB_jobId_resetToday);
	}

	public void resetTodayDo() {
		
		log.info("resetWithdrawDayLimit start");
		cachePlatformService.resetWithdrawDayLimit();
		log.info("resetWithdrawDayLimit end");

		SerialTaskReq<String> req = new SerialTaskReq<String>();
		req.setTaskCode(SerialTaskEntity.TASK_taskCode_resetToday);
		serialTaskService.createSerialTask(req);

	}
	
	public void resetTodayDb() {

		List<Product> ps = this.productDao.queryT0Products();

		for (Product p : ps) {
			log.info("<<-----" + p.getOid() + "剩余赎回金额每日还原、上一个交易日产品规模(基于占比算)计算 start----->>");
			
				p.setDailyNetMaxRredeem(p.getNetMaxRredeemDay());
				// 赎回占比开关
				if (p.getIsPreviousCurVolume().equals(Product.YES)) {
					// 上一个交易日产品当前规模(基于占比算)
					p.setPreviousCurVolume(DecimalUtil.zoomIn(p.getCurrentVolume().multiply(p.getPreviousCurVolumePercent()), 100));
				}
				this.productDao.save(p);

			
			log.info("<<-----" + p.getOid() + "剩余赎回金额每日还原、上一个交易日产品规模(基于占比算)计算 end----->>");
		}

		/** 重围投资者今日持仓数据 */
		publisherHoldService.resetToday();
		/** 重置投资者今日统计数据 */
		this.investorStatisticsService.resetToday();
		/** 重置发行人每日统计数据 */
		publisherStatisticsService.resetToday();
	}

	
}
