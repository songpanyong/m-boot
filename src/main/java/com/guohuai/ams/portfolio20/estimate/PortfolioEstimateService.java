package com.guohuai.ams.portfolio20.estimate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.guohuai.ams.liquidAsset.LiquidAsset;
import com.guohuai.ams.liquidAsset.yield.LiquidAssetYield;
import com.guohuai.ams.order.SPVOrder;
import com.guohuai.ams.order.SPVOrderDao;
import com.guohuai.ams.portfolio.entity.PortfolioEntity;
import com.guohuai.ams.portfolio.service.PortfolioService;
import com.guohuai.ams.portfolio20.estimate.illiquid.PortfolioIlliquidEstimateService;
import com.guohuai.ams.portfolio20.estimate.liquid.PortfolioLiquidEstimateService;
import com.guohuai.ams.portfolio20.liquid.hold.PortfolioLiquidHoldEntity;
import com.guohuai.ams.portfolio20.liquid.hold.PortfolioLiquidHoldService;
import com.guohuai.ams.portfolio20.order.MarketOrderDao;
import com.guohuai.ams.portfolio20.order.MarketOrderEntity;
import com.guohuai.basic.common.DateUtil;
import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.StringUtil;
import com.guohuai.mmp.job.JobEnum;
import com.guohuai.mmp.job.lock.JobLockService;
import com.guohuai.mmp.job.log.JobLogEntity;
import com.guohuai.mmp.job.log.JobLogFactory;
import com.guohuai.mmp.job.log.JobLogService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author created by Arthur
 * @date 2017年2月24日 - 下午6:02:39
 */
@Service
@Slf4j
public class PortfolioEstimateService {

	@Autowired
	private PortfolioEstimateDao portfolioEstimateDao;
	@Autowired
	private SPVOrderDao spvOrderDao;
	@Autowired
	MarketOrderDao marketOrderDao;
	@Autowired
	private PortfolioService portfolioService;

	@Autowired
	private PortfolioLiquidEstimateService portfolioLiquidEstimateService;
	@Autowired
	private PortfolioIlliquidEstimateService portfolioIlliquidEstimateService;

	@Autowired
	private PortfolioLiquidHoldService portfolioLiquidHoldService;
	@Autowired
	private JobLockService jobLockService;
	@Autowired
	private JobLogService jobLogService;

//	@Transactional
	public void batchEstimate() {
		if (this.jobLockService.getRunPrivilege(JobEnum.JOB_jobId_portfolioEstimate.getJobId())) {
			JobLogEntity jobLog = JobLogFactory.getInstance(JobEnum.JOB_jobId_portfolioEstimate.getJobId());
			try {
				// 查询所有资产池
				List<PortfolioEntity> portfolios = this.portfolioService.findActivePortfolio();

				Date maxDate = DateUtil.addDays(new java.sql.Date(System.currentTimeMillis()), -1);
				int i = 0;
				for (PortfolioEntity portfolio : portfolios) {
					Date sdate = null;
					if (null == portfolio.getDimensionsDate()) {
						sdate = new java.sql.Date(portfolio.getAuditTime().getTime());
					} else {
						sdate = DateUtil.addDays(portfolio.getDimensionsDate(), 1);
					}
					while (DateUtil.le(sdate, maxDate)) {
						try {
							this.estimate(portfolio, sdate);
						} catch (Exception e) {
							System.out.println("=========估值计算异常开始 ['" + portfolio.getOid() + "', '" + sdate + "']=========");
							e.printStackTrace();
							System.out.println("=========估值计算异常结束 ['" + portfolio.getOid() + "', '" + sdate + "']=========");
						}
						sdate = DateUtil.addDays(sdate, 1);
					}
					i++;
				}
				System.out.println("=========估值计算结束, 共[" + portfolios.size() + "]个, 成功计算[" + i + "]个.=========");

			} catch (Exception e) {
				log.error(e.getMessage(), e);
				jobLog.setJobMessage(AMPException.getStacktrace(e));
				jobLog.setJobStatus(JobLogEntity.JOB_jobStatus_failed);
			}
			jobLog.setBatchEndTime(new Timestamp(System.currentTimeMillis()));
			this.jobLogService.saveEntity(jobLog);
			this.jobLockService.resetJob(JobEnum.JOB_jobId_portfolioEstimate.getJobId());
		}

	}

	@Transactional
	public void estimate(PortfolioEntity portfolio, Date estimateDate) {
		PortfolioEstimateEntity e = this.portfolioEstimateDao.findByPortfolioAndEstimateDate(portfolio, estimateDate);
		if (null == e) {
			e = new PortfolioEstimateEntity();
			e.setOid(StringUtil.uuid());
			e.setPortfolio(portfolio);
			e.setLiquidEstimate(portfolio.getLiquidDimensions());
			e.setIlliquidEstimate(portfolio.getIlliquidDimensions());
			e.setCashEstimate(portfolio.getCashPosition());
			e.setManageChargefee(BigDecimal.ZERO);
			e.setTrusteeChargefee(BigDecimal.ZERO);
			e.setChargefee(BigDecimal.ZERO);
			e.setEstimateDate(estimateDate);
			e.setEstimateTime(new Timestamp(System.currentTimeMillis()));
			e = this.portfolioEstimateDao.save(e);
		}

		BigDecimal liquidIncrease = this.portfolioLiquidEstimateService.batchEstimate(e, estimateDate);
		BigDecimal illiquidIncrease = this.portfolioIlliquidEstimateService.batchEstimate(e, estimateDate);

		e.setLiquidEstimate(e.getLiquidEstimate().add(liquidIncrease));
		e.setIlliquidEstimate(e.getIlliquidEstimate().add(illiquidIncrease));

		BigDecimal estimate = portfolio.getCashPosition().add(e.getLiquidEstimate()).add(e.getIlliquidEstimate());
		BigDecimal manageChargefee = BigDecimal.ZERO;
		if (null != portfolio.getManageRate()) {
		  	//BigDecimal manageRate = portfolio.getManageRate().divide(new BigDecimal(portfolio.getCalcBasis()), 8, RoundingMode.HALF_UP);
			manageChargefee = estimate.multiply(portfolio.getManageRate()).divide(new BigDecimal(portfolio.getCalcBasis()), 4, RoundingMode.HALF_UP);
		}
		BigDecimal trusteeChargefee = BigDecimal.ZERO;
		if (null != portfolio.getTrusteeRate()) {
			//BigDecimal trusteeRate = portfolio.getTrusteeRate().divide(new BigDecimal(portfolio.getCalcBasis()), 8, RoundingMode.HALF_UP);
			trusteeChargefee = estimate.multiply(portfolio.getTrusteeRate()).divide(new BigDecimal(portfolio.getCalcBasis()), 4, RoundingMode.HALF_UP);
		}

		BigDecimal manageChargefeeIncr = manageChargefee.subtract(e.getManageChargefee());
		BigDecimal trusteeChargefeeIncr = trusteeChargefee.subtract(e.getTrusteeChargefee());

		e.setManageChargefee(manageChargefee);
		e.setTrusteeChargefee(trusteeChargefee);
		e.setChargefee(manageChargefee.add(trusteeChargefee));
		e.setEstimateTime(new Timestamp(System.currentTimeMillis()));

		this.portfolioEstimateDao.save(e);

		this.portfolioService.estimate(portfolio.getOid(), manageChargefeeIncr.add(trusteeChargefeeIncr), liquidIncrease, illiquidIncrease, estimateDate);

		// this.portfolioService.countingChargefee(portfolio.getOid(),
		// manageChargefeeIncr.add(trusteeChargefeeIncr));
	}

	@Transactional
	public void flushEstimate(LiquidAsset asset, LiquidAssetYield... yields) {
		List<PortfolioLiquidHoldEntity> holds = this.portfolioLiquidHoldService.findHoldingHold(asset);
		for (LiquidAssetYield yield : yields) {
			System.out.println(yield);
		}
		System.out.println(holds);
	}

	/**
	 * 累计计提费用明细列表
	 * @param spec
	 * @param pageable
	 * @return
	 */
	public Page<PortfolioEstimateEntity> getChargefeeList(Specification<PortfolioEstimateEntity> spec,
			Pageable pageable) {
		return this.portfolioEstimateDao.findAll(spec, pageable);
	}

	/**
	 * 最新估值日和累计计提费用
	 * @param portfolioOid
	 * @return
	 */
	public JSONObject theNew(String portfolioOid) {
		
		JSONObject object = new JSONObject();
		// 最新估值日
		Date lastEstimateDate = null;
		// 累计计提费用
		Date totalChargefee = null;
		
		lastEstimateDate = this.portfolioEstimateDao.getLastEstimateDate(portfolioOid);
		totalChargefee = this.portfolioEstimateDao.getTotalChargefee(portfolioOid);
		
		object.put("lastEstimateDate", lastEstimateDate);
		object.put("totalChargefee", totalChargefee);
		return object;
	}
	
	
//	@Transactional
	public void batchEstimateTest() {

		JobLogEntity jobLog = JobLogFactory.getInstance(JobEnum.JOB_jobId_portfolioEstimate.getJobId());
		try {
			// 查询所有资产池
			List<PortfolioEntity> portfolios = this.portfolioService.findActivePortfolio();

			Date maxDate = DateUtil.addDays(new java.sql.Date(System.currentTimeMillis()), -1);
			int i = 0;
			for (PortfolioEntity portfolio : portfolios) {
				if (portfolio.getOid() != "aca861fa15ab4fc6bec69ad1dee9b74b") {
					continue;
				}
				Date sdate = null;
				if (null == portfolio.getDimensionsDate()) {
					sdate = new java.sql.Date(portfolio.getAuditTime().getTime());
				} else {
					sdate = DateUtil.addDays(portfolio.getDimensionsDate(), 1);
				}
				while (DateUtil.le(sdate, maxDate)) {
					try {
						this.estimate(portfolio, sdate);
					} catch (Exception e) {
						System.out
								.println("=========估值计算异常开始 ['" + portfolio.getOid() + "', '" + sdate + "']=========");
						e.printStackTrace();
						System.out
								.println("=========估值计算异常结束 ['" + portfolio.getOid() + "', '" + sdate + "']=========");
					}
					sdate = DateUtil.addDays(sdate, 1);
				}
				i++;
			}
			System.out.println("=========估值计算结束, 共[" + portfolios.size() + "]个, 成功计算[" + i + "]个.=========");

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			jobLog.setJobMessage(AMPException.getStacktrace(e));
			jobLog.setJobStatus(JobLogEntity.JOB_jobStatus_failed);
		}
		jobLog.setBatchEndTime(new Timestamp(System.currentTimeMillis()));
		this.jobLogService.saveEntity(jobLog);
		this.jobLockService.resetJob(JobEnum.JOB_jobId_portfolioEstimate.getJobId());
	}

}
