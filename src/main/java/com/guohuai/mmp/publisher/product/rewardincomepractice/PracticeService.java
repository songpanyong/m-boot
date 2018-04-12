

package com.guohuai.mmp.publisher.product.rewardincomepractice;

import java.sql.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guohuai.ams.product.Product;
import com.guohuai.ams.product.ProductService;
import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.DateUtil;
import com.guohuai.component.util.StringUtil;
import com.guohuai.component.web.view.RowsRep;
import com.guohuai.mmp.job.JobEnum;
import com.guohuai.mmp.job.lock.JobLockService;
import com.guohuai.mmp.job.log.JobLogEntity;
import com.guohuai.mmp.job.log.JobLogFactory;
import com.guohuai.mmp.job.log.JobLogService;
import com.guohuai.mmp.platform.StaticProperties;


@Service
@Transactional
public class PracticeService {
	@Autowired
	PracticeDao practiceDao;
	
	private static final Logger logger = LoggerFactory.getLogger(PracticeService.class);
	
	@Autowired
	private ProductService productService;
	@Autowired
	private JobLockService jobLockService;
	@Autowired
	private JobLogService jobLogService;
	@Autowired
	private PracticeInterestService practiceInterestService;
	
	
	public void practice() {
		if (this.jobLockService.getRunPrivilege(JobEnum.JOB_jobId_practice.getJobId())) {
			practiceLog();
		}
	}
	
	public void practiceLog() {

		JobLogEntity jobLog = JobLogFactory.getInstance(JobEnum.JOB_jobId_practice.getJobId());

		try {
			practiceDo();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			jobLog.setJobMessage(AMPException.getStacktrace(e));
			jobLog.setJobStatus(JobLogEntity.JOB_jobStatus_failed);
		}
		jobLog.setBatchEndTime(DateUtil.getSqlCurrentDate());
		this.jobLogService.saveEntity(jobLog);
		this.jobLockService.resetJob(JobEnum.JOB_jobId_practice.getJobId());
	}
	
	/**
	 * 活期产品收益试算
	 */
	private void practiceDo() {

		Date incomeDate = StaticProperties.isIs24() ? DateUtil.getBeforeDate() : DateUtil.getSqlDate();
		// T0
		List<Product> productList = this.productService.findProductT04Snapshot();
		for (Product product : productList) {
			processOneItem(product, incomeDate);
		}

		// Tn
		productList = productService.findProductTn4Snapshot(incomeDate);
		for (Product product : productList) {
			processOneItem(product, incomeDate);
		}
	}
	
	public void processOneItem(Product product, Date incomeDate) {
		logger.info("productOid={}, incomeDate={} practice start", product.getOid(), incomeDate);
		this.practiceInterestService.practiceInterest(product.getOid(), incomeDate);
		logger.info("practice.productOid={}, incomeDate={} practice end", product.getOid(), incomeDate);
	}

	public PracticeEntity saveEntity(PracticeEntity entity) {
		return this.practiceDao.save(entity);
	}
	
	public BaseResp detail(String productOid) {
		DetailRep rep = new DetailRep();
		RewardIsNullRep isNullRep = this.rewardIsNullRep(this.productService.findByOid(productOid), null);
		rep.setTotalHoldVolume(isNullRep.getTotalHoldVolume());
		rep.setTotalCouponVolume(isNullRep.getTotalCouponIncome());
		return rep;
	}
	
	/**
	 * 某天某个产品的收益是否已试算
	 * true yes 
	 * false no
	 */
	public boolean isPractice(Product product, Date tDate) {
		PracticeEntity entity = this.practiceDao.findRewardIsNull(product.getOid(), tDate);
		if (null == entity) {
			return false;
		} 
		return true;
	}
	
	public RewardIsNullRep rewardIsNullRep(Product product, Date tDate) {
		RewardIsNullRep rep = new RewardIsNullRep();
		
		PracticeEntity entity = null;
		if (null != tDate) {
			entity = this.practiceDao.findRewardIsNull(product.getOid(), tDate);
		} else {
			
			entity = this.practiceDao.findRewardIsNull(product.getOid());
		}
		if (null != entity) {
			rep.setProduct(product);
			rep.setTotalHoldVolume(entity.getTotalHoldVolume());
			rep.setTotalRewardIncome(entity.getTotalRewardIncome());
			rep.setTotalCouponIncome(entity.getTotalCouponIncome());
			rep.setTDate(entity.getTDate());
		} 
		return rep;
	}
	
	
	
	public List<PracticeEntity> findByPrductAfterInterest(Product product, Date incomeDate) {
		return this.practiceDao.findByPrductAfterInterest(product, incomeDate);
	}
	
	/**
	 * 产品--数据分布
	 */
	public RowsRep<PracticeInRep> findByProduct(String productOid, Date tDate) {
		if (StringUtil.isEmpty(productOid)) {
			return new RowsRep<PracticeInRep>();
		}
		Product product = this.productService.findByOid(productOid);
		List<PracticeEntity> list = null;
		if (null != tDate) {
			list = this.practiceDao.findByProductAndTDate(product, tDate);
		} else {
			Date maxDate = this.practiceDao.findMaxTDate(product);
			list = this.practiceDao.findByProductAndTDate(product, maxDate);
		}
		RowsRep<PracticeInRep> rowsRep = new RowsRep<PracticeInRep>();
		if (null != list && !list.isEmpty()) {
			for (PracticeEntity entity : list) {
				PracticeInRep rep = new PracticeInRep();
				
				rep.setTotalHoldVolume(entity.getTotalHoldVolume());
				rep.setValue((long)(Math.floor(entity.getTotalHoldVolume().doubleValue() * product.getNetUnitShare().doubleValue() * 100)));
				rep.setTotalRewardIncome(entity.getTotalRewardIncome());
				rep.setTDate(entity.getTDate());
				rep.setCreateTime(entity.getCreateTime());
				rep.setUpdateTime(entity.getUpdateTime());
				rep.setProductOid(entity.getProduct().getOid());
				if (null != entity.getReward()) {
					rep.setRewardRatio(entity.getReward().getRatio());
					rep.setStartDate(entity.getReward().getStartDate());
					rep.setEndDate(entity.getReward().getEndDate());
					rep.setRewardOid(entity.getReward().getOid());
					rep.setLevel(entity.getReward().getLevel());
				}
						
				rowsRep.add(rep);
			}
		}
		
		return rowsRep;
	}

	public void delete(List<PracticeEntity> entities) {
		this.practiceDao.delete(entities);
	}
	
}
