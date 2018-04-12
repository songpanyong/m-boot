package com.guohuai.ams.product;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;

import com.guohuai.ams.portfolio.entity.PortfolioEntity;
import com.guohuai.ams.portfolio.service.PortfolioService;
import com.guohuai.ams.product.order.salePosition.ProductSalePositionDao;
import com.guohuai.ams.product.order.salePosition.ProductSalePositionOrder;
import com.guohuai.ams.product.order.salePosition.ProductSaleScheduleDao;
import com.guohuai.ams.product.order.salePosition.ProductSaleScheduling;
import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.DateUtil;
import com.guohuai.component.util.DecimalUtil;
import com.guohuai.mmp.investor.tradeorder.InvestorRepayCashTradeOrderService;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderService;
import com.guohuai.mmp.job.lock.JobLockEntity;
import com.guohuai.mmp.job.lock.JobLockService;
import com.guohuai.mmp.job.log.JobLogEntity;
import com.guohuai.mmp.job.log.JobLogFactory;
import com.guohuai.mmp.job.log.JobLogService;
import com.guohuai.mmp.publisher.baseaccount.PublisherBaseAccountDao;
import com.guohuai.mmp.publisher.baseaccount.PublisherBaseAccountEntity;
import com.guohuai.mmp.publisher.hold.PublisherHoldEntity;
import com.guohuai.mmp.publisher.hold.PublisherHoldService;
import com.guohuai.mmp.publisher.product.rewardincomepractice.PracticeService;

@Service
public class ProductSchedService {

	private Logger logger = LoggerFactory.getLogger(ProductSchedService.class);

	@Autowired
	private ProductDao productDao;
	@Autowired
	private ProductSalePositionDao salePositionDao;
	@Autowired
	private ProductSaleScheduleDao productSaleScheduleDao;
	@Autowired
	private ProductService productService;
	@Autowired
	private JobLockService jobLockService;
	@Autowired
	private PortfolioService portfolioService;
	@Autowired
	private PublisherBaseAccountDao publisherBaseAccountDao;
	@Autowired
	private PublisherHoldService publisherHoldService;
	@Autowired
	private JobLogService jobLogService;
	@Autowired
	private InvestorRepayCashTradeOrderService investorRepayCashTradeOrderService;
	@Autowired
	private InvestorTradeOrderService investorTradeOrderService;
	@Autowired
	private PracticeService practiceService;

	/**
	 * 活期: 当<<成立开始日期>>到,募集未开始变为募集中; 定期: 当<<募集开始日期>>到,募集未开始变为募集中; 定期: 当<
	 * <募集結束日期>>到或募集满额,募集中变为募集結束; 定期: 当<<存续期开始日期>>到,募集結束变为存续期; 定期: 当<
	 * <存续期结束日期>>到,存续期变为存续期結束;
	 */
	@Transactional(value = TxType.REQUIRES_NEW)
	public void notstartraiseToRaisingOrRaised() {

		if (this.jobLockService.getRunPrivilege(JobLockEntity.JOB_jobId_scheduleProductState)) {
			this.notstartraiseToRaising();
		}

	}

	private void notstartraiseToRaising() {
		JobLogEntity jobLog = JobLogFactory.getInstance(JobLockEntity.JOB_jobId_scheduleProductState);
		try {
			List<Product> products = new ArrayList<Product>();
			Date now = DateUtil.parseToSqlDate(new SimpleDateFormat(DateUtil.datePattern).format(new java.util.Date()));

			t0ProductStateChange(products, now);

			tnProductStateChange(products, now);
			if (products.size() > 0) {
				this.productDao.save(products);
			
			}

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			jobLog.setJobMessage(AMPException.getStacktrace(e));
			jobLog.setJobStatus(JobLogEntity.JOB_jobStatus_failed);
		}
		jobLog.setBatchEndTime(DateUtil.getSqlCurrentDate());
		this.jobLogService.saveEntity(jobLog);
		this.jobLockService.resetJob(JobLockEntity.JOB_jobId_scheduleProductState);

	}

	//定期: 当<<募集开始日期>>到或<<成立开始日期>>到,募集未开始变为募集中;
	//定期: 当<<募集結束日期>>到或募集满额,募集募集中变为募集結束;
	//定期: 当<<存续期结束日期>>到,存续期变为存续期結束;
	private void tnProductStateChange(List<Product> products, Date now) {
		Specification<Product> periodicSpec = new Specification<Product>() {
			@Override
			public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				In<String> status = cb.in(root.get("state").as(String.class));
				status.value(Product.STATE_Reviewpass).value(Product.STATE_Raising).value(Product.STATE_Durationing);
				return cb.and(cb.equal(root.get("type").get("oid").as(String.class), Product.TYPE_Producttype_01),
						cb.equal(root.get("isDeleted").as(String.class), Product.NO), status);
			}
		};

		periodicSpec = Specifications.where(periodicSpec);
		List<Product> periodics = this.productDao.findAll(periodicSpec);
		if (periodics != null && periodics.size() > 0) {
			for (Product product : periodics) {
				if (Product.STATE_Reviewpass.equals(product.getState())) {
					if (product.getRaiseStartDate() != null && DateUtil.daysBetween(DateUtil.getSqlDate(), product.getRaiseStartDate()) >= 0) {
						product.setState(Product.STATE_Raising);
						product.setUpdateTime(DateUtil.getSqlCurrentDate());
						products.add(product);

					}
				} else if (Product.STATE_Raising.equals(product.getState())) {
					BigDecimal investMin = null == product.getInvestMin() ? BigDecimal.ZERO : product.getInvestMin();
					
					if (Product.RAISE_FULL_FOUND_TYPE_AUTO.equals(product.getRaiseFullFoundType())
							&& product.getCurrentVolume().add(DecimalUtil.null2Zero(investMin)).compareTo(product.getRaisedTotalNumber()) >= 0) { // 募集满额后是否自动触发成立
						
						product.setState(Product.STATE_Durationing);

						product.setSetupDate(DateUtil.getSqlDate());// 产品成立时间（存续期开始时间）
						product.setRaiseEndDate(DateUtil.getBeforeDate());// 产品结束日为成立日前一天
						java.sql.Date durationPeriodEndDate = DateUtil.addSQLDays(product.getSetupDate(),
								product.getDurationPeriodDays() - 1);
						product.setDurationPeriodEndDate(durationPeriodEndDate);// 存续期结束时间
						// 到期最晚还本付息日 指存续期结束后的还本付息最迟发生在存续期后的第X个自然日的23:59:59为止
						java.sql.Date repayDate = DateUtil.addSQLDays(durationPeriodEndDate,
								product.getAccrualRepayDays());
						// 到期最晚还本付息日 指存续期结束后的还本付息最迟发生在存续期后的第X个自然日的23:59:59为止
						product.setRepayDate(repayDate);// 到期还款时间

						products.add(product);
						
						investorRepayCashTradeOrderService.updateExpectedRevenue(product);
						
					} else {
						if (product.getRaiseEndDate() != null
								&& DateUtil.daysBetween(DateUtil.getSqlDate(), product.getRaiseEndDate()) > 0) {// 定期:
							// 募集募集中变为募集結束;
							product.setState(Product.STATE_Raiseend);
							product.setUpdateTime(DateUtil.getSqlCurrentDate());
							products.add(product);
						}
					}

				} else if (Product.STATE_Durationing.equals(product.getState())) { // 定期:
																					// 当<<存续期结束日期>>到,存续期变为存续期結束;
					if (product.getDurationPeriodEndDate() != null
							&& DateUtil.daysBetween(DateUtil.getSqlDate(), product.getDurationPeriodEndDate()) > 0) {
						product.setState(Product.STATE_Durationend);
						product.setUpdateTime(DateUtil.getSqlCurrentDate());
						products.add(product);
						
						
						/**
						 * 存续期快照、试算
						 * @author yuechao
						 */
						investorTradeOrderService.snapshotTnVolume(product, DateUtil.getSqlDate());
						practiceService.processOneItem(product, DateUtil.getSqlDate());
					}
				}
			}
		}
	}

	//活期产品 当<<成立开始日期>>到,存续期未开始变为存续期;
	private void t0ProductStateChange(List<Product> products, Date now) {
		Specification<Product> nostartDurationsSpec = new Specification<Product>() {
			@Override
			public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.and(cb.equal(root.get("type").get("oid").as(String.class), Product.TYPE_Producttype_02), // 活期
						cb.equal(root.get("state").as(String.class), Product.STATE_Reviewpass), // 存续期未开始
						cb.equal(root.get("isDeleted").as(String.class), Product.NO)// 产品未删除
				);
			}
		};
		nostartDurationsSpec = Specifications.where(nostartDurationsSpec);
		List<Product> nostartDurations = this.productDao.findAll(nostartDurationsSpec);
		if (nostartDurations != null && nostartDurations.size() > 0) {
			for (Product product : nostartDurations) {
				if (product.getSetupDate() != null && product.getSetupDate().getTime() <= now.getTime()) {
					product.setState(Product.STATE_Durationing);
					product.setUpdateTime(DateUtil.getSqlCurrentDate());
					products.add(product);

				}
			}
		}
	}
	


	/**
	 * 可售份额排期发放
	 */
	@Transactional(value = TxType.REQUIRES_NEW)
	public void scheduleSendProductMaxSaleVolume() {

		if (this.jobLockService.getRunPrivilege(JobLockEntity.JOB_jobId_scheduleSendProductMaxSaleVolume)) {
			this.sendProductMaxSaleVolume();
		}

	}

	/**
	 * 可售份额排期发放
	 */
	private void sendProductMaxSaleVolume() {
		JobLogEntity jobLog = JobLogFactory.getInstance(JobLockEntity.JOB_jobId_scheduleSendProductMaxSaleVolume);
		try {
			final Date today = DateUtil.formatUtilToSql(DateUtil.getCurrDate());

			Specification<ProductSalePositionOrder> spec = new Specification<ProductSalePositionOrder>() {
				@Override
				public Predicate toPredicate(Root<ProductSalePositionOrder> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
					return cb.and(cb.equal(root.get("productSaleScheduling").get("basicDate").as(Date.class), today), cb.equal(root.get("status").as(String.class), ProductSalePositionOrder.STATUS_PASS));
				}
			};
			spec = Specifications.where(spec);

			List<ProductSalePositionOrder> maxSaleVolumes = this.salePositionDao.findAll(spec, new Sort(new Order(Direction.ASC, "createTime")));
			if (maxSaleVolumes != null && maxSaleVolumes.size() > 0) {
				for (ProductSalePositionOrder sp : maxSaleVolumes) {
					try {
						this.sendApply(sp);
					} catch (Exception e) {
						sp.setStatus(ProductSalePositionOrder.STATUS_DEACTIVE);
						sp.setErrorMessage("发生异常");
						if (e.getClass().equals(AMPException.class)) {
							int errorCode = ((AMPException) e).getCode();
							if (errorCode == 90031) {
								sp.setErrorMessage("数据不正常");
							} else if (errorCode == 90029) {
								sp.setErrorMessage("可申请份额不足");

							} else if (errorCode == 90032) {
								sp.setErrorMessage("更新乐视接口最高可售份额失败");
							}
						}
						salePositionDao.saveAndFlush(sp);
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			jobLog.setJobMessage(AMPException.getStacktrace(e));
			jobLog.setJobStatus(JobLogEntity.JOB_jobStatus_failed);
		}

		jobLog.setBatchEndTime(DateUtil.getSqlCurrentDate());
		this.jobLogService.saveEntity(jobLog);
		this.jobLockService.resetJob(JobLockEntity.JOB_jobId_scheduleSendProductMaxSaleVolume);

	}

	/**
	 * 可售份额申请发送
	 * 
	 * @param oid
	 * @param operator
	 * @throws Exception
	 */
	private void sendApply(ProductSalePositionOrder pspo) throws Exception {
		if (pspo.getProduct() == null || pspo.getProductSaleScheduling() == null || !ProductSalePositionOrder.STATUS_PASS.equals(pspo.getStatus())) {
			throw AMPException.getException(90031);
		}
		ProductSaleScheduling productSaleSchedule = this.productSaleScheduleDao.findOne(pspo.getProductSaleScheduling().getOid());
		BigDecimal newMaxSaleVolume = pspo.getVolume();//申请份额

		Product product = this.productService.getProductByOid(pspo.getProduct().getOid());
		PortfolioEntity portfolio = this.portfolioService.getByOid(product.getPortfolio().getOid());
		if (portfolio != null) {
			PublisherBaseAccountEntity spv = this.publisherBaseAccountDao.findOne(portfolio.getSpvEntity().getOid());

			PublisherHoldEntity hold = this.publisherHoldService.getPortfolioSpvHold(portfolio, spv);
			if (hold != null) {
				BigDecimal holdTotalVolume = hold.getTotalVolume() != null ? hold.getTotalVolume() : BigDecimal.ZERO;
				//更新产品的maxSaleVolume
				int adjust = productDao.updateMaxSaleVolume(pspo.getProduct().getOid(), newMaxSaleVolume, holdTotalVolume);
				if (adjust > 0) {
					Timestamp now = new Timestamp(System.currentTimeMillis());
					pspo.setStatus(ProductSalePositionOrder.STATUS_ACTIVE);
					salePositionDao.saveAndFlush(pspo);

					productSaleSchedule.setApprovalAmount(productSaleSchedule.getApprovalAmount().add(newMaxSaleVolume));// 生效份额
					productSaleSchedule.setSyncTime(now);// 同步时间
					productSaleSchedule.setUpdateTime(now);
					productSaleScheduleDao.save(productSaleSchedule);

				}
			}

		}

	}

}
