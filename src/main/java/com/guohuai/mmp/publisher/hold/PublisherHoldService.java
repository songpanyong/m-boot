package com.guohuai.mmp.publisher.hold;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.guohuai.ams.duration.fact.income.IncomeAllocate;
import com.guohuai.ams.duration.fact.income.IncomeAllocateService;
import com.guohuai.ams.portfolio.entity.PortfolioEntity;
import com.guohuai.ams.portfolio.service.PortfolioService;
import com.guohuai.ams.product.Product;
import com.guohuai.ams.product.ProductDao;
import com.guohuai.ams.product.ProductDecimalFormat;
import com.guohuai.ams.product.ProductService;
import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.basic.component.ext.web.PageResp;
import com.guohuai.cache.entity.HoldCacheEntity;
import com.guohuai.cache.entity.ProductCacheEntity;
import com.guohuai.cache.service.CacheHoldService;
import com.guohuai.cache.service.CacheProductService;
import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.BigDecimalUtil;
import com.guohuai.component.util.DateUtil;
import com.guohuai.component.util.DecimalUtil;
import com.guohuai.component.util.StringUtil;
import com.guohuai.component.web.view.Pages;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountEntity;
import com.guohuai.mmp.investor.tradeorder.FlatWareTotalRep;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderEntity;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderService;
import com.guohuai.mmp.job.lock.JobLockEntity;
import com.guohuai.mmp.job.lock.JobLockService;
import com.guohuai.mmp.job.log.JobLogEntity;
import com.guohuai.mmp.job.log.JobLogFactory;
import com.guohuai.mmp.job.log.JobLogService;
import com.guohuai.mmp.platform.StaticProperties;
import com.guohuai.mmp.publisher.baseaccount.PublisherBaseAccountDao;
import com.guohuai.mmp.publisher.baseaccount.PublisherBaseAccountEntity;
import com.guohuai.mmp.publisher.investor.InterestFormula;
import com.guohuai.mmp.publisher.product.client.ProductClientService;
import com.guohuai.mmp.publisher.product.client.ProductPeriodicDetailResp;
import com.guohuai.mmp.serialtask.SerialTaskEntity;
import com.guohuai.mmp.serialtask.SerialTaskReq;
import com.guohuai.mmp.serialtask.SerialTaskRequireNewService;
import com.guohuai.mmp.serialtask.SerialTaskService;
import com.guohuai.mmp.serialtask.UnlockAccrualParams;
import com.guohuai.mmp.serialtask.UnlockRedeemParams;
import com.guohuai.mmp.sys.SysConstant;

/**
 * 持有人手册
 * 
 */
@Service
@Transactional
public class PublisherHoldService {

	private static final Logger logger = LoggerFactory.getLogger(PublisherHoldService.class);

	
	@Value("${investor.info.kickstar:true}")
	private boolean kickstar = true;
	@Autowired
	private PublisherHoldDao publisherHoldDao;
	@Autowired
	private ProductService productService;
	@Autowired
	private PortfolioService portfolioService;
	@Autowired
	private PublisherHoldServiceNew newService;
	@Autowired
	private PublisherBaseAccountDao publisherBaseAccountDao;
	@Autowired
	private JobLockService jobLockService;
	@Autowired
	private IncomeAllocateService incomeAllocateService;
	@Autowired
	private InvestorTradeOrderService investorTradeOrderService;
	@Autowired
	private CacheHoldService cacheHoldService;
	@Autowired
	private CacheProductService cacheProductService;
	@Autowired
	private SerialTaskService serialTaskService;
	@Autowired
	private SerialTaskRequireNewService serialTaskRequireNewService;
	@Autowired
	private JobLogService jobLogService;
	@Autowired
	private ProductClientService productClientService;
	@Autowired
	private ProductDao productDao;

	/**
	 * 投资
	 * 
	 * @param tradeOrder
	 * @return
	 */
	public PublisherHoldEntity invest(InvestorTradeOrderEntity orderEntity) {
//		PublisherHoldEntity hold = this.publisherHoldDao.findByInvestorBaseAccountAndProduct(orderEntity.getInvestorBaseAccount(), orderEntity.getProduct());
		PublisherHoldEntity hold = this.publisherHoldDao.findByInvestorBaseAccountAndProductAndWishplanOid(orderEntity.getInvestorBaseAccount(), orderEntity.getProduct(), orderEntity.getWishplanOid());
		Product product = orderEntity.getProduct();
		
		//合仓
		if (null == hold) {
			hold = new PublisherHoldEntity();
			hold.setProduct(product); // 所属理财产品
			hold.setPublisherBaseAccount(orderEntity.getPublisherBaseAccount()); // 所属发行人
			hold.setInvestorBaseAccount(orderEntity.getInvestorBaseAccount()); // 所属投资人
			hold.setPortfolio(product.getPortfolio());
			hold.setTotalVolume(orderEntity.getOrderVolume()); // 总份额
			hold.setToConfirmInvestVolume(orderEntity.getOrderVolume()); // 待确认份额
			hold.setAccountType(PublisherHoldEntity.PUBLISHER_accountType_INVESTOR);
			hold.setRedeemableHoldVolume(BigDecimal.ZERO);// 可赎回份额
			hold.setLockRedeemHoldVolume(BigDecimal.ZERO);// 赎回锁定份额
			hold.setAccruableHoldVolume(BigDecimal.ZERO);
			hold.setValue(orderEntity.getOrderAmount()); // 最新市值
			hold.setHoldStatus(PublisherHoldEntity.PUBLISHER_HOLD_HOLD_STATUS_toConfirm);
			hold.setExpectIncome(orderEntity.getExpectIncome());
			hold.setExpectIncomeExt(orderEntity.getExpectIncomeExt());
			hold.setDayInvestVolume(orderEntity.getOrderVolume());
			hold.setTotalInvestVolume(orderEntity.getOrderVolume());
			hold.setTotalVoucherAmount(this.investorTradeOrderService.getVoucherAmount(orderEntity));
			hold.setMaxHoldVolume(orderEntity.getOrderVolume());
			hold.setLatestOrderTime(orderEntity.getOrderTime());
			//wishplan
			hold.setWishplanOid(orderEntity.getWishplanOid());
			this.saveEntity(hold);
			/** 统计产品投资人数和投资次数 */
			this.productService.updatePurchasePeopleNumAndPurchaseNum(product.getOid());
			
		} else {
			hold.setTotalVolume(hold.getTotalVolume().add(orderEntity.getOrderVolume())); // 总份额
			hold.setToConfirmInvestVolume(hold.getToConfirmInvestVolume().add(orderEntity.getOrderVolume())); // 待确认份额
			hold.setTotalVoucherAmount(hold.getTotalVoucherAmount().add(this.investorTradeOrderService.getVoucherAmount(orderEntity)));
			hold.setTotalInvestVolume(hold.getTotalInvestVolume().add(orderEntity.getOrderVolume()));
			hold.setDayInvestVolume(hold.getDayInvestVolume().add(orderEntity.getOrderVolume()));
			hold.setValue(hold.getValue().add(orderEntity.getOrderAmount()));

			hold.setExpectIncome(hold.getExpectIncome().add(orderEntity.getExpectIncome()));
			
			hold.setExpectIncomeExt(hold.getExpectIncomeExt().add(orderEntity.getExpectIncomeExt()));
			hold.setMaxHoldVolume(hold.getMaxHoldVolume().add(orderEntity.getOrderVolume()));
			hold.setLatestOrderTime(orderEntity.getOrderTime());
			saveEntity(hold);
			/** 统计产品投资次数 */
			this.productService.updatePurchaseNum(product.getOid());

		}

		return hold;
	}

	public void redeemDayRules(InvestorTradeOrderEntity orderEntity) {
		if (InvestorTradeOrderEntity.TRADEORDER_orderType_normalRedeem.equals(orderEntity.getOrderType()) ||
				InvestorTradeOrderEntity.TRADEORDER_orderType_clearRedeem.equals(orderEntity.getOrderType())) {
			// 产品单人单日赎回上限等于0时，表示无上限
			if (DecimalUtil.isGoRules(orderEntity.getProduct().getSingleDailyMaxRedeem())) {
				int i = this.publisherHoldDao.redeem4DayRedeemVolume(orderEntity.getOrderVolume(), orderEntity.getInvestorBaseAccount(), orderEntity.getProduct(), orderEntity.getProduct().getSingleDailyMaxRedeem());
				if (i < 1) {
					// error.define[30032]=超过产品单人单日赎回上限(CODE:30032)
					throw AMPException.getException(30032);
				}
			}
			if (DecimalUtil.isGoRules(orderEntity.getProduct().getSingleDayRedeemCount())) {
				int i = this.publisherHoldDao.updateDayRedeemCount(orderEntity.getProduct().getSingleDayRedeemCount(), orderEntity.getProduct().getOid(), orderEntity.getInvestorBaseAccount().getOid());
				if (i < 1) {
					throw new AMPException("超过单日赎回次数");
				}
			}
		}
	}
	
	/**
	 *  赎回锁定
	 */
	public void redeemLock(InvestorTradeOrderEntity orderEntity) {
//		PublisherHoldEntity hold = this.findByInvestorBaseAccountAndProduct(orderEntity.getInvestorBaseAccount(), orderEntity.getProduct());
		PublisherHoldEntity hold = this.findByInvestorBaseAccountAndProductAndWishplanOid(orderEntity.getInvestorBaseAccount(), orderEntity.getProduct(), orderEntity.getWishplanOid());
		if (InvestorTradeOrderEntity.TRADEORDER_orderType_expGoldRedeem.equals(orderEntity.getOrderType())) {
			
			
			hold.setToConfirmRedeemVolume(hold.getToConfirmRedeemVolume().add(orderEntity.getOrderAmount()));
			hold.setExpGoldVolume(hold.getExpGoldVolume().subtract(orderEntity.getOrderAmount()));
			if (!DecimalUtil.isValOutGreatThanOrEqualZero(hold.getExpGoldVolume())) {
				throw new AMPException("可赎回体验金不足");
			}
			
		} else {
			hold.setToConfirmRedeemVolume(hold.getToConfirmRedeemVolume().add(orderEntity.getOrderAmount()));
			hold.setRedeemableHoldVolume(hold.getRedeemableHoldVolume().subtract(orderEntity.getOrderAmount()));
			if (!DecimalUtil.isValOutGreatThanOrEqualZero(hold.getRedeemableHoldVolume())) {
				throw new AMPException("可赎回金额不足");
			}
			this.saveEntity(hold);
		}
	}

	/**
	 * 赎回
	 */
	public FlatWareTotalRep normalRedeem(InvestorTradeOrderEntity orderEntity) {
		String holdStatus = getFlatWareHoldStatus(orderEntity);

//		PublisherHoldEntity hold = this.findByInvestorBaseAccountAndProduct(orderEntity.getInvestorBaseAccount(), orderEntity.getProduct());
		PublisherHoldEntity hold = this.findByInvestorBaseAccountAndProductAndWishplanOid(orderEntity.getInvestorBaseAccount(), orderEntity.getProduct(), orderEntity.getWishplanOid());
		
		
		hold.setToConfirmRedeemVolume(hold.getToConfirmRedeemVolume().subtract(orderEntity.getOrderAmount()));
		hold.setTotalVolume(hold.getTotalVolume().subtract(orderEntity.getOrderAmount()));
		if (hold.getMaxHoldVolume().compareTo(orderEntity.getOrderAmount()) >= 0) {
			hold.setMaxHoldVolume(hold.getMaxHoldVolume().subtract(orderEntity.getOrderAmount()));
		} else {
			hold.setMaxHoldVolume(BigDecimal.ZERO);
		}
		if (hold.getAccruableHoldVolume().compareTo(orderEntity.getOrderAmount()) >= 0) {
			hold.setAccruableHoldVolume(hold.getAccruableHoldVolume().subtract(orderEntity.getOrderAmount()));
		} else {
			hold.setAccruableHoldVolume(BigDecimal.ZERO);
		}
		
		// error.define[20005]=赎回份额异常(CODE:20005)
		DecimalUtil.isValOutGreatThanOrEqualZero(hold.getToConfirmRedeemVolume(), 20005);
		hold.setHoldVolume(hold.getHoldVolume().subtract(orderEntity.getOrderAmount()));
		hold.setValue(hold.getValue().subtract(orderEntity.getOrderAmount()));
		hold.setHoldStatus(holdStatus);
		// error.define[20005]=赎回份额异常(CODE:20005)
		DecimalUtil.isValOutGreatThanOrEqualZero(hold.getValue(), 20005);
		DecimalUtil.isValOutGreatThanOrEqualZero(hold.getTotalVolume(), 20005);
		DecimalUtil.isValOutGreatThanOrEqualZero(hold.getHoldVolume(), 20005);
		this.saveEntity(hold);
		
		// 分仓处理
		FlatWareTotalRep rep = null;
		if (orderEntity.getWishplanOid() != null) { 
			investorTradeOrderService.wishplanflatWare(orderEntity);
		} else {
			investorTradeOrderService.flatWare(orderEntity);
		}
		return rep;

	}

	/**
	 * 废单：扣除总份额和投资待确认份额
	 */
	public void abandonInvestOrder(InvestorTradeOrderEntity orderEntity) {

		int i = this.publisherHoldDao.invest4Abandon(orderEntity.getOrderVolume(), orderEntity.getProduct().getNetUnitShare(), orderEntity.getInvestorBaseAccount(), orderEntity.getProduct());
		if (i < 1) {
			// error.define[20017]=废单份额异常(CODE:20017)
			throw AMPException.getException(20017);
		}
	}

	public int redeem4Refuse(InvestorTradeOrderEntity orderEntity) {
		// 暂时在份额确认之前不能再赎回，直接从锁定份额里面扣除就可以了
		int i = this.publisherHoldDao.redeem4Refuse(orderEntity.getOrderVolume(), orderEntity.getInvestorBaseAccount(), orderEntity.getProduct());
		if (i < 1) {
			// error.define[20017]=废单份额异常(CODE:20017)
			throw AMPException.getException(20017);
		}
		return i;
	}

	public void redeem4RefuseOfDayRedeemVolume(InvestorTradeOrderEntity tradeOrder) {
		// 单人单日产品赎回上限为0，表示无上限
		if (null != tradeOrder.getProduct().getSingleDailyMaxRedeem() && tradeOrder.getProduct().getSingleDailyMaxRedeem().compareTo(BigDecimal.ZERO) != 0) {
			int i = this.publisherHoldDao.redeem4RefuseOfDayRedeemVolume(tradeOrder.getOrderVolume(), tradeOrder.getInvestorBaseAccount(), tradeOrder.getProduct());
			if (i < 1) {
				// error.define[30037]=废赎回单之单日最大赎回份额异常(CODE:30037)
				throw AMPException.getException(30037);
			}
		}
	}

	public void invest4AbandonOfDayInvestVolume(InvestorTradeOrderEntity orderEntity) {
		int i = this.publisherHoldDao.invest4AbandonOfDayInvestVolume(orderEntity.getPublisherHold().getOid(), orderEntity.getOrderVolume());
		if (i < 1) {
			// error.define[30037]=废赎回单之单日最大赎回份额异常(CODE:30037)
			throw AMPException.getException(30037);
		}
	}

	public void unlockRedeem() {

		if (this.jobLockService.getRunPrivilege(JobLockEntity.JOB_jobId_unlockRedeem)) {
			UnlockRedeemParams params = new UnlockRedeemParams();
			params.setRedeemBaseDate(StaticProperties.isIs24() ? DateUtil.getSqlDate() : DateUtil.getAfterDate());

			SerialTaskReq<UnlockRedeemParams> req = new SerialTaskReq<UnlockRedeemParams>();
			req.setTaskCode(SerialTaskEntity.TASK_taskCode_unlockRedeem);
			req.setTaskParams(params);
			serialTaskService.createSerialTask(req);
		}
	}

	/**
	 * 根据分仓更新合仓可赎回份额
	 */
	public void unlockRedeemDo(String taskOid, Date cur) {
		JobLogEntity jobLog = JobLogFactory.getInstance(JobLockEntity.JOB_jobId_unlockRedeem);
		try {
			String lastOid = "0";

			int arithmometer = 1;
			while (true) {
				List<InvestorTradeOrderEntity> list = this.investorTradeOrderService.findByBeforeBeginRedeemDateInclusive(cur, lastOid);
				if (list.isEmpty()) {
					break;
				}
				for (InvestorTradeOrderEntity entity : list) {
					lastOid = entity.getOid();
					unlockRedeemItem(entity);
					arithmometer++;
					if (arithmometer > 100) {
						arithmometer = 1;
						serialTaskRequireNewService.updateTime(taskOid);
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
		this.jobLockService.resetJob(JobLockEntity.JOB_jobId_unlockRedeem);
	}

	/**
	 * 解锁计息
	 */
	public void unlockAccrual() {
		if (this.jobLockService.getRunPrivilege(JobLockEntity.JOB_jobId_unlockAccrual)) {
			UnlockAccrualParams params = new UnlockAccrualParams();
			params.setAccrualBaseDate(StaticProperties.isIs24() ? DateUtil.getSqlDate() : DateUtil.getAfterDate());

			SerialTaskReq<UnlockAccrualParams> req = new SerialTaskReq<UnlockAccrualParams>();
			req.setTaskCode(SerialTaskEntity.TASK_taskCode_unlockAccrual);
			req.setTaskParams(params);
			serialTaskService.createSerialTask(req);
		}
	}

	public void unlockAccrualDo(String taskOid, Date cur) {

		JobLogEntity jobLog = JobLogFactory.getInstance(JobLockEntity.JOB_jobId_unlockAccrual);

		try {
			String lastOid = "0";

			int arithmometer = 1;
			while (true) {
				List<InvestorTradeOrderEntity> list = this.investorTradeOrderService.findByBeforeBeginAccuralDateInclusive(cur, lastOid);
				if (list.isEmpty()) {
					break;
				}
				for (InvestorTradeOrderEntity entity : list) {
					lastOid = entity.getOid();
					unlockAccrualItem(entity);
					arithmometer++;
					if (arithmometer > 100) {
						arithmometer = 1;
						serialTaskRequireNewService.updateTime(taskOid);
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
		this.jobLockService.resetJob(JobLockEntity.JOB_jobId_unlockAccrual);

	}

	private void unlockAccrualItem(InvestorTradeOrderEntity entity) {
		this.newService.unlockAccrualItem(entity);

	}

	private void unlockRedeemItem(InvestorTradeOrderEntity entity) {
		this.newService.unlockRedeemItem(entity);
	}

	public PublisherHoldEntity saveEntity(PublisherHoldEntity hold) {
		return this.publisherHoldDao.save(hold);
	}

	public PageResp<HoldQueryRep> holdMng(Specification<PublisherHoldEntity> spec, Pageable pageable) {
		Page<PublisherHoldEntity> cas = this.publisherHoldDao.findAll(spec, pageable);
		PageResp<HoldQueryRep> pagesRep = new PageResp<HoldQueryRep>();
		if (cas != null && cas.getContent() != null && cas.getTotalElements() > 0) {
			for (PublisherHoldEntity hold : cas) {
				HoldQueryRep queryRep = new HoldQueryRep();
				
				queryRep.setHoldOid(hold.getOid()); // 持仓OID
				Product product = hold.getProduct();
				if (null != product) {
					queryRep.setProductOid(product.getOid()); // 产品OID
					queryRep.setProductCode(product.getCode()); // 产品编号
					queryRep.setProductName(product.getName()); // 产品名称
					queryRep.setExpAror(DecimalUtil.zoomOut(product.getExpAror(), 100).toString()
							+ "-" + DecimalUtil.zoomOut(product.getExpArorSec(), 100).toString()); // 预期收益率
				}
				
				if (PublisherHoldEntity.PUBLISHER_accountType_INVESTOR.equals(hold.getAccountType())) {
					queryRep.setInvestorOid(hold.getInvestorBaseAccount().getOid());
					queryRep.setPhoneNum(this.kickstar ? StringUtil.kickstarOnPhoneNum(hold.getInvestorBaseAccount().getPhoneNum()) : hold.getInvestorBaseAccount().getPhoneNum());
				}
				
				if (PublisherHoldEntity.PUBLISHER_accountType_SPV.equals(hold.getAccountType())) {
					queryRep.setInvestorOid(hold.getPublisherBaseAccount().getOid()); // 发行人OID
					queryRep.setPhoneNum(hold.getPublisherBaseAccount().getPhone());
				}
				queryRep.setTotalVolume(hold.getTotalVolume()); // 持仓总份额
				queryRep.setHoldVolume(hold.getHoldVolume()); // 持有份额
				queryRep.setToConfirmRedeemVolume(hold.getToConfirmRedeemVolume()); // 当前价值
				queryRep.setToConfirmInvestVolume(hold.getToConfirmInvestVolume()); // 待确认
				queryRep.setTotalInvestVolume(hold.getTotalInvestVolume()); // 累计投资
				queryRep.setRedeemableHoldVolume(hold.getRedeemableHoldVolume()); // 可赎回份额
				queryRep.setLockRedeemHoldVolume(hold.getLockRedeemHoldVolume()); // 赎回锁定份额
				queryRep.setAccruableHoldVolume(hold.getAccruableHoldVolume()); // 可计息份额
				queryRep.setValue(hold.getValue()); // 最新市值
				queryRep.setExpGoldVolume(hold.getExpGoldVolume()); //体验金
				
				queryRep.setHoldTotalIncome(hold.getHoldTotalIncome()); // 累计收益
				queryRep.setTotalBaseIncome(hold.getTotalBaseIncome());
				queryRep.setTotalRewardIncome(hold.getTotalRewardIncome());
				queryRep.setTotalCouponIncome(hold.getTotalCouponIncome());
				
				queryRep.setHoldYesterdayIncome(hold.getHoldYesterdayIncome()); // 昨日收益
				queryRep.setYesterdayBaseIncome(hold.getYesterdayBaseIncome());
				queryRep.setYesterdayRewardIncome(hold.getYesterdayRewardIncome());
				queryRep.setYesterdayCouponIncome(hold.getYesterdayCouponIncome());
				
				queryRep.setConfirmDate(hold.getConfirmDate()); // 收益确认日期
				
				queryRep.setExpectIncome(hold.getExpectIncome() + "~" + hold.getExpectIncomeExt()); // 预期收益
				
				queryRep.setAccountType(hold.getAccountType());
				queryRep.setAccountTypeDisp(this.accountTypeEn2Ch(hold.getAccountType()));
				queryRep.setDayInvestVolume(hold.getDayInvestVolume()); // 今日投资份额
				queryRep.setDayRedeemVolume(hold.getDayRedeemVolume()); // 今日赎回份额
				queryRep.setDayRedeemCount(hold.getDayRedeemCount());

				queryRep.setMaxHoldVolume(hold.getMaxHoldVolume()); // 单个产品最大持有份额
				queryRep.setHoldStatus(hold.getHoldStatus()); // 持仓状态
				queryRep.setHoldStatusDisp(holdStatusEn2Ch(hold.getHoldStatus())); // 持仓状态disp
				
				queryRep.setLatestOrderTime(hold.getLatestOrderTime()); // 计息份额

				queryRep.setProductAlias(hold.getProductAlias()); //产品分期号
				
				pagesRep.getRows().add(queryRep);
			}
		}
		pagesRep.setTotal(cas.getTotalElements());
		return pagesRep;
	}

	public HoldDetailRep detail(String holdOid) {
		HoldDetailRep detailRep = new HoldDetailRep();
		PublisherHoldEntity hold = this.findByOid(holdOid);
		detailRep.setProductOid(hold.getProduct().getOid()); // 产品OID
		detailRep.setProductCode(hold.getProduct().getCode()); // 产品编号
		detailRep.setProductName(hold.getProduct().getName()); // 产品名称
		detailRep.setExpAror(hold.getProduct().getExpAror().toString() + hold.getProduct().getExpArorSec().toString()); // 预期收益率
		detailRep.setLockPeriod(hold.getProduct().getLockPeriodDays()); // 锁定期
		detailRep.setTotalHoldVolume(hold.getTotalVolume()); // 持仓总份额
		detailRep.setRedeemableHoldVolume(hold.getRedeemableHoldVolume()); // 可赎回份额
		detailRep.setLockRedeemHoldVolume(hold.getLockRedeemHoldVolume()); // 赎回锁定份额
		detailRep.setValue(hold.getValue()); // 最新市值
		detailRep.setHoldTotalIncome(hold.getHoldTotalIncome()); // 累计收益
		detailRep.setHoldYesterdayIncome(hold.getHoldYesterdayIncome()); // 昨日收益
		detailRep.setExpectIncome(hold.getExpectIncome()); // 预期收益
		detailRep.setLastConfirmDate(hold.getConfirmDate()); // 份额确认日期
		detailRep.setHoldStatus(hold.getHoldStatus()); // 持仓状态
		detailRep.setHoldStatusDisp(holdStatusEn2Ch(hold.getHoldStatus())); // 持仓状态disp
		return detailRep;
	}

	public PublisherHoldEntity findByOid(String holdOid) {
		PublisherHoldEntity hold = this.publisherHoldDao.findOne(holdOid);
		if (null == hold) {
			throw new AMPException("持仓不存在");
		}
		return hold;
	}
	
	private String accountTypeEn2Ch(String accountType) {
		if (PublisherHoldEntity.PUBLISHER_accountType_SPV.equals(accountType)) {
			return "发行人";
		}
		if (PublisherHoldEntity.PUBLISHER_accountType_INVESTOR.equals(accountType)) {
			return "投资人";
		}
		return accountType;

	}

	private String holdStatusEn2Ch(String holdStatus) {
		if (PublisherHoldEntity.PUBLISHER_HOLD_HOLD_STATUS_toConfirm.equals(holdStatus)) {
			return "待确认";
		}
		if (PublisherHoldEntity.PUBLISHER_HOLD_HOLD_STATUS_holding.equals(holdStatus)) {
			return "持有中";
		}

		if (PublisherHoldEntity.PUBLISHER_HOLD_HOLD_STATUS_expired.equals(holdStatus)) {
			return "已到期";
		}
		if (PublisherHoldEntity.PUBLISHER_HOLD_HOLD_STATUS_closing.equals(holdStatus)) {
			return "结算中";
		}

		if (PublisherHoldEntity.PUBLISHER_HOLD_HOLD_STATUS_closed.equals(holdStatus)) {
			return "已结算";
		}

		if (PublisherHoldEntity.PUBLISHER_HOLD_HOLD_STATUS_refunding.equals(holdStatus)) {
			return "退款中";
		}

		if (PublisherHoldEntity.PUBLISHER_HOLD_HOLD_STATUS_refunded.equals(holdStatus)) {
			return "已退款";
		}

		return holdStatus;
	}

	/**
	 * 获取指定产品下面的所有持有人
	 */
	public List<PublisherHoldEntity> findByProduct(Product product, String lastOid) {
		return this.publisherHoldDao.findByProduct(product.getOid(), lastOid);
	}

	public List<PublisherHoldEntity> clearingHold(String productOid, String accountType, String lastOid) {
		return this.publisherHoldDao.clearingHold(productOid, accountType, lastOid);
	}

	public List<PublisherHoldEntity> findByProductHoldStatus(Product product, String holdStatus, String lastOid, Date incomeDate) {
		return this.publisherHoldDao.findByProductAndHoldStatus(product.getOid(), holdStatus, lastOid, PublisherHoldEntity.PUBLISHER_accountType_INVESTOR, incomeDate);
	}

	public int updateHold4Interest(String holdOid, BigDecimal holdIncomeVolume, BigDecimal holdIncomeAmount, BigDecimal holdLockIncomeVolume, BigDecimal holdLockIncomeAmount, BigDecimal netUnitAmount, Date incomeDate, BigDecimal holdBaseAmount, BigDecimal holdRewardAmount) {

		int i = this.publisherHoldDao.updateHold4Interest(holdOid, holdIncomeVolume, holdIncomeAmount, holdLockIncomeVolume, holdLockIncomeAmount, netUnitAmount, incomeDate, holdBaseAmount, holdRewardAmount);
		if (i < 1) {
			throw new AMPException("计息失败");
		}
		return i;

	}

	public int updateHold4InterestTn(String holdOid, BigDecimal holdIncomeAmount, BigDecimal holdLockIncomeAmount, Date incomeDate) {

		int i = this.publisherHoldDao.updateHold4InterestTn(holdOid, holdIncomeAmount, holdLockIncomeAmount, incomeDate);
		if (i < 1) {
			throw new AMPException("计息失败");
		}
		return i;

	}

	@Transactional
	public PublisherHoldEntity getPortfolioSpvHold(PortfolioEntity portfolio, PublisherBaseAccountEntity spv) {
		List<PublisherHoldEntity> list = this.publisherHoldDao.findByPortfolioAndSPV(portfolio, spv);

		if (null != list && list.size() > 0) {
			return list.get(0);
		} else {
			PublisherHoldEntity e = new PublisherHoldEntity();
			e.setPortfolio(portfolio);
			e.setPublisherBaseAccount(spv);

			List<Product> products = this.productService.getProductListByPortfolioOid(portfolio.getOid());
			if (null != products && products.size() > 0) {
				e.setProduct(products.get(0));
			}
			e.setAccountType(PublisherHoldEntity.PUBLISHER_accountType_SPV);
			e.setHoldStatus(PublisherHoldEntity.PUBLISHER_HOLD_HOLD_STATUS_holding);
			return this.publisherHoldDao.save(e);
		}
	}

	public int checkSpvHold4Invest(InvestorTradeOrderEntity tradeOrder) {
		return this.checkSpvHold4Invest(tradeOrder, false);
	}

	/**
	 * 检验SPV仓位
	 * 
	 * @param tradeOrder
	 * @return
	 */
	public int checkSpvHold4Invest(InvestorTradeOrderEntity tradeOrder, boolean isRecovery) {
		BigDecimal orderVolume = tradeOrder.getOrderVolume();
		if (isRecovery) {
			orderVolume = orderVolume.negate();
		}
		int i = this.publisherHoldDao.checkSpvHold4Invest(tradeOrder.getProduct(), PublisherHoldEntity.PUBLISHER_accountType_SPV, orderVolume);
		if (i < 1) {
			// error.define[30011]=产品可投金额不足(CODE:30011)
			throw new AMPException(30011);
		}
		return i;

	}

	/**
	 * //更新SPV持仓
	 * 
	 * @param product
	 * @param orderVolume
	 * @return
	 */
	public int update4InvestConfirm(Product product, BigDecimal orderVolume) {
		int i = this.publisherHoldDao.update4InvestConfirm(product, PublisherHoldEntity.PUBLISHER_accountType_SPV, orderVolume);
		if (i < 1) {
			// error.define[30024]=针对SPV持仓份额确认失败(CODE:30024)
			throw new AMPException(30024);
		}
		return i;
	}
	/**
	 * 废单:定期产品
	 * 增加总份额
	 * @return
	 */
	public int update4InvestConfirmAbandon(InvestorTradeOrderEntity orderEntity) {
		int i = this.publisherHoldDao.update4InvestConfirmAbandon(orderEntity.getProduct(), PublisherHoldEntity.PUBLISHER_accountType_SPV, orderEntity.getOrderVolume());
		if (i < 1) {
			// error.define[30024]=针对SPV持仓份额确认失败(CODE:30024)
			throw new AMPException(30024);
		}
		return i;
	}
	public int update4RedeemConfirm(Product product, BigDecimal orderVolume) {
		int i = this.publisherHoldDao.update4RedeemConfirm(product, PublisherHoldEntity.PUBLISHER_accountType_SPV, orderVolume);
		if (i < 1) {
			// error.define[30024]=针对SPV持仓份额确认失败(CODE:30024)
			throw new AMPException(30024);
		}

		return i;
	}

	public HoldDetailRep getHoldByAssetPoolOid(String assetPoolOid) {

		HoldDetailRep detailRep = new HoldDetailRep();

		PublisherHoldEntity hold = null;
		PortfolioEntity portfolio = this.portfolioService.getByOid(assetPoolOid);
		if (portfolio != null && portfolio.getSpvEntity() != null) {
			PublisherBaseAccountEntity spv = this.publisherBaseAccountDao.findOne(portfolio.getSpvEntity().getOid());

			List<PublisherHoldEntity> list = this.publisherHoldDao.findByPortfolioAndSPV(portfolio, spv);

			if (null != list && list.size() > 0) {
				hold = list.get(0);
				if (hold.getProduct() != null) {
					detailRep.setProductOid(hold.getProduct().getOid()); // 产品OID
					detailRep.setProductCode(hold.getProduct().getCode()); // 产品编号
					detailRep.setProductName(hold.getProduct().getName()); // 产品名称
					detailRep.setExpAror(hold.getProduct().getExpAror().toString() + "~" + hold.getProduct().getExpArorSec().toString()); // 预期收益率
					detailRep.setLockPeriod(hold.getProduct().getLockPeriodDays()); // 锁定期
				}

				detailRep.setTotalHoldVolume(hold.getTotalVolume()); // 持仓总份额
																		// 本金余额(持有总份额)
																		// totalHoldVolume
																		// decimal(16,4)
				detailRep.setRedeemableHoldVolume(hold.getRedeemableHoldVolume()); // 可赎回份额
				detailRep.setLockRedeemHoldVolume(hold.getLockRedeemHoldVolume()); // 赎回锁定份额
				detailRep.setValue(hold.getValue()); // 最新市值
				detailRep.setHoldTotalIncome(hold.getHoldTotalIncome()); // 累计收益
				detailRep.setHoldYesterdayIncome(hold.getHoldYesterdayIncome()); // 昨日收益
				detailRep.setExpectIncome(hold.getExpectIncome()); // 预期收益
				detailRep.setLastConfirmDate(hold.getConfirmDate()); // 份额确认日期
				detailRep.setHoldStatus(hold.getHoldStatus()); // 持仓状态
				detailRep.setHoldStatusDisp(holdStatusEn2Ch(hold.getHoldStatus())); // 持仓状态disp

			}

		}

		return detailRep;
	}

	/**
	 * 检查是否超过单个产品最大持仓
	 */
	public void checkMaxHold4Invest(InvestorTradeOrderEntity orderEntity) {
		if (DecimalUtil.isGoRules(orderEntity.getProduct().getMaxHold())) { // 等于0，表示无限制
//			PublisherHoldEntity hold = this.publisherHoldDao.findByInvestorBaseAccountAndProduct(orderEntity.getInvestorBaseAccount(), orderEntity.getProduct());
			PublisherHoldEntity hold = this.publisherHoldDao.findByInvestorBaseAccountAndProductAndWishplanOid(orderEntity.getInvestorBaseAccount(), orderEntity.getProduct(), orderEntity.getWishplanOid());
			if (null == hold) {
				if (orderEntity.getOrderVolume().compareTo(orderEntity.getProduct().getMaxHold()) > 0) {
					// error.define[30031]=份额已超过所购产品最大持仓(CODE:30031)
					throw new AMPException(30031);
				}
			} else {
				int i = this.publisherHoldDao.checkMaxHold4Invest(orderEntity.getInvestorBaseAccount(), orderEntity.getProduct(), orderEntity.getProduct().getMaxHold(), orderEntity.getOrderVolume());
				if (i < 1) {
					// error.define[30031]=份额已超过所购产品最大持仓(CODE:30031)
					throw new AMPException(30031);
				}
			}
		}
	}
	/**
	 * 定期产品废单操作
	 * @param publisherHold
	 * @param redeemableHoldVolume
	 * @param lockRedeemHoldVolume
	 * @param accruableHoldVolume
	 * @param orderVolume
	 * @return
	 */
	public int updateHold4ConfirmAbandon(PublisherHoldEntity publisherHold,  BigDecimal lockRedeemHoldVolume, BigDecimal accruableHoldVolume, BigDecimal orderVolume) {
		int i = this.publisherHoldDao.updateHold4ConfirmAbandon(publisherHold.getOid(),  lockRedeemHoldVolume, accruableHoldVolume, orderVolume);
		if (i < 1) {
			throw new AMPException("定期废单异常");
		}
		return i;

	}
	public int updateHold4Confirm(PublisherHoldEntity publisherHold, BigDecimal redeemableHoldVolume, BigDecimal lockRedeemHoldVolume, BigDecimal accruableHoldVolume, BigDecimal orderVolume) {
		int i = this.publisherHoldDao.updateHold4Confirm(publisherHold.getOid(), redeemableHoldVolume, lockRedeemHoldVolume, accruableHoldVolume, orderVolume);
		if (i < 1) {
			throw new AMPException("份额确认异常");
		}
		return i;

	}

	public int updateHold4ExpGoldConfirm(PublisherHoldEntity publisherHold, BigDecimal accruableHoldVolume, BigDecimal orderVolume) {
		int i = this.publisherHoldDao.updateHold4ExpGoldConfirm(publisherHold.getOid(), accruableHoldVolume, orderVolume);
		if (i < 1) {
			throw new AMPException("份额确认异常");
		}
		return i;

	}

	/**
	 * 废单：解锁SPV锁定份额
	 */
	public int updateSpvHold4InvestAbandon(InvestorTradeOrderEntity orderEntity) {
		int i = this.publisherHoldDao.updateSpvHold4InvestAbandon(orderEntity.getProduct(), PublisherHoldEntity.PUBLISHER_accountType_SPV, orderEntity.getOrderVolume());
		if (i < 1) {
			// error.define[30035]=废申购单时SPV持仓锁定份额异常(CODE:30035)
			throw new AMPException(30035);
		}
		return i;

	}

	/**
	 * 废单:扣除投资人最大持仓份额
	 */
	public void updateMaxHold4InvestAbandon(InvestorBaseAccountEntity investorBaseAccount, Product product, BigDecimal orderVolume) {
		if (DecimalUtil.isGoRules(product.getMaxHold())) { // 等于0，表示无限制
			int i = this.publisherHoldDao.updateMaxHold4InvestAbandon(investorBaseAccount, product, orderVolume);
			if (i < 1) {
				// error.define[30036]=废申购单时最大持仓份额异常(CODE:30036)
				throw new AMPException(30036);
			}

		}

	}
	/**
	 * 付息锁定
	 * 
	 * @param tradeOrder
	 */
	public void repayInterestLock(InvestorTradeOrderEntity tradeOrder) {

		if (this.publisherHoldDao.repayInterestLock(tradeOrder.getOrderAmount(), tradeOrder.getInvestorBaseAccount(), tradeOrder.getProduct()) <= 0) {
			throw AMPException.getException(20008);
		}
	}


	/**
	 * 我的活期----基线
	 */
	public MyHoldT0QueryRep queryMyT0HoldProList(String investorOid) {
		MyHoldT0QueryRep rep = new MyHoldT0QueryRep();
		Pages<HoldingT0Detail> holdingDetails = new Pages<HoldingT0Detail>();
		Pages<ToConfirmT0Detail> toConfirmDetails = new Pages<ToConfirmT0Detail>();

		//List<HoldCacheEntity> holds = cacheHoldService.findByInvestorOid(investorOid);
		List<PublisherHoldEntity> holds = this.findByInvestorOid(investorOid);
		Date incomeDate = DateUtil.getBeforeDate();
		for (PublisherHoldEntity cacheHold : holds) {

			//ProductCacheEntity cacheProduct = cacheProductService.getProductCacheEntityById(cacheHold.getProductOid());
			if (Product.TYPE_Producttype_01.equals(cacheHold.getProduct().getType().getOid())) {
				continue;
			}
			rep.setT0CapitalAmount(rep.getT0CapitalAmount().add(cacheHold.getTotalVolume()));
			if (null != cacheHold.getConfirmDate() && DateUtil.daysBetween(incomeDate, cacheHold.getConfirmDate()) == 0) {
				rep.setT0YesterdayIncome(rep.getT0YesterdayIncome().add(cacheHold.getHoldYesterdayIncome()));
			}

			rep.setTotalIncomeAmount(rep.getTotalIncomeAmount().add(cacheHold.getHoldTotalIncome()));

			if (cacheHold.getHoldVolume().compareTo(BigDecimal.ZERO) > 0) {
				HoldingT0Detail detail = new HoldingT0Detail();
				detail.setLastOrderTime(cacheHold.getLatestOrderTime());
				detail.setProductOid(cacheHold.getProduct().getOid());
				detail.setProductName(cacheHold.getProduct().getName());
				detail.setValue(cacheHold.getHoldVolume());
				if (null != cacheHold.getConfirmDate() && DateUtil.daysBetween(incomeDate, cacheHold.getConfirmDate()) == 0) {
					detail.setYesterdayIncome(cacheHold.getHoldYesterdayIncome());
				}
				detail.setHoldTotalIncome(cacheHold.getHoldTotalIncome());
				detail.setToConfirmRedeemVolume(cacheHold.getToConfirmRedeemVolume());
				holdingDetails.add(detail);
			}

			if (cacheHold.getToConfirmInvestVolume().compareTo(BigDecimal.ZERO) > 0) {
				ToConfirmT0Detail toConfirmDetail = new ToConfirmT0Detail();
				toConfirmDetail.setLastOrderTime(cacheHold.getLatestOrderTime());
				toConfirmDetail.setProductName(cacheHold.getProduct().getName());
				toConfirmDetail.setToConfirmInvestVolume(cacheHold.getToConfirmInvestVolume());
				toConfirmDetails.add(toConfirmDetail);
			}
		}
		
		/** 对持有中的产品进行排序  */
		if(holdingDetails.getRows().size()>1){
			Collections.sort(holdingDetails.getRows(),new Comparator<HoldingT0Detail>(){

				@Override
				public int compare(HoldingT0Detail o1, HoldingT0Detail o2) {
					 int flag = o1.getLastOrderTime().compareTo(o2.getLastOrderTime());
					 return -flag;
				}
				
			});
		}
		
		/** 对申请中的产品进行排序  */
		if(toConfirmDetails.getRows().size()>1){
			Collections.sort(toConfirmDetails.getRows(),new Comparator<ToConfirmT0Detail>(){

				@Override
				public int compare(ToConfirmT0Detail o1, ToConfirmT0Detail o2) {
					 int flag = o1.getLastOrderTime().compareTo(o2.getLastOrderTime());
					 return -flag;
				}
				
			});
		}
		
		
		holdingDetails.setTotal(holdingDetails.getRows().size());
		toConfirmDetails.setTotal(toConfirmDetails.getRows().size());
		rep.setToConfirmDetails(toConfirmDetails);
		rep.setHoldingDetails(holdingDetails);

		return rep;
	}

	
	/**
	 * 查询活期产品详情（累计收益、昨日收益、总市值）
	 */
	public MyHoldQueryRep queryT0HoldingDetail(String userOid, String productOid) {
		MyHoldQueryRep rep = new MyHoldQueryRep();

		HoldCacheEntity cacheHold = this.cacheHoldService.getHoldCacheEntityByUidAndProductId(userOid, productOid);
		ProductCacheEntity cacheProduct = this.cacheProductService.getProductCacheEntityById(productOid);
		Date incomeDate = DateUtil.getBeforeDate();
		BigDecimal expAror = cacheProduct.getExpAror(); // 年化收益率
		BigDecimal expArorSec = cacheProduct.getExpArorSec(); // 年化收益率区间
		BigDecimal incomeCalcBasis = new BigDecimal(cacheProduct.getIncomeCalcBasis()); // 收益计算基础
		if (null != cacheHold.getIncomeDate() && DateUtil.daysBetween(incomeDate, cacheHold.getIncomeDate()) == 0) {
			rep.setYesterdayIncome(cacheHold.getHoldYesterdayIncome());// 昨日收益
		}
		rep.setTotalIncome(cacheHold.getHoldTotalIncome());// 累计收益
		rep.setMinRredeem(cacheProduct.getMinRredeem());// 单笔赎回最低金额
		rep.setMaxRredeem(cacheProduct.getMaxRredeem());// 单笔赎回最大金额
		rep.setAdditionalRredeem(cacheProduct.getAdditionalRredeem());// 单笔赎回递增金额
		rep.setSingleDailyMaxRedeem(cacheProduct.getSingleDailyMaxRedeem());// 产品--单人单日赎回上限
		rep.setDailyNetMaxRredeem(cacheProduct.getDailyNetMaxRredeem());// 产品--剩余赎回金额
		rep.setNetMaxRredeemDay(cacheProduct.getNetMaxRredeemDay());// 产品--单日净赎回上限
		rep.setSingleDayRedeemCount(cacheProduct.getSingleDayRedeemCount());
		rep.setDayRedeemCount(cacheHold.getDayRedeemCount());
		rep.setTotalValue(cacheHold.getHoldVolume());// 持有金额
		rep.setDayRedeemVolume(cacheHold.getDayRedeemVolume());// 投资者--今日赎回金额
		rep.setRedeemableHoldVolume(cacheHold.getRedeemableHoldVolume());// 可赎回金额

		// 预期收益率
		BigDecimal expArorMid = expArorSec == null ? expAror : (expAror.add(expArorSec).divide(new BigDecimal("2.0")));

		if (incomeCalcBasis.compareTo(SysConstant.BIGDECIMAL_defaultValue) != 0) {
			rep.setMillionIncome(InterestFormula.compound(new BigDecimal("10000"), expArorMid, incomeCalcBasis.intValue()));
		}

		// 折线图最近日期
		Date lastedDate = DateUtil.getBeforeDate();

		// 实际收益分配的产品收益率
		Page<IncomeAllocate> pcas = this.incomeAllocateService.getProductIncomeAllocate(productOid, 30);
		if (pcas != null && pcas.getContent() != null && pcas.getTotalElements() > 0) {
			for (IncomeAllocate ia : pcas) {
				// 年化收益率走势 单位（%）
				rep.getExpArorList().add(0, new MyCurrProTendencyChartRep(DateUtil.format(ia.getBaseDate(), "MM-dd"), ProductDecimalFormat.multiply(ia.getRatio())));

				// 万份收益走势 单位（元）
				rep.getMillionIncomeList().add(0, new MyCurrProTendencyChartRep(DateUtil.format(ia.getBaseDate(), "MM-dd"), ia.getWincome()));
				lastedDate = ia.getBaseDate();
			}
		}

		int size = rep.getExpArorList().size();
		if (size < 30) {
			for (int i = 0; i < 30 - size; i++) {
				lastedDate = DateUtil.addSQLDays(lastedDate, -1);
				// 年化收益率走势 单位（%）,补足30条数据
				rep.getExpArorList().add(0, new MyCurrProTendencyChartRep(DateUtil.format(lastedDate, "MM-dd"), ProductDecimalFormat.multiply(expArorMid)));
				// 万份收益走势 单位（元）,补足30条数据
				rep.getMillionIncomeList().add(0, new MyCurrProTendencyChartRep(DateUtil.format(lastedDate, "MM-dd"), rep.getMillionIncome()));
			}
		}

		return rep;
	}

	/**
	 * 查询定期 我的持有中 产品详情
	 */
	public TnHoldingDetail queryTnHoldingDetail(String userOid, String productOid) {
		TnHoldingDetail rep = new TnHoldingDetail();
		
		HoldCacheEntity holdCacheEntity = this.cacheHoldService.getHoldCacheEntityByUidAndProductId(userOid, productOid);
		rep.setInvestVolume(holdCacheEntity.getTotalVolume());
		rep.setPayAmount(holdCacheEntity.getTotalVolume().add(holdCacheEntity.getExpectIncome()));
		rep.setExpectIncome(holdCacheEntity.getExpectIncome());
		rep.setExpectIncomeExt(holdCacheEntity.getExpectIncomeExt());

		ProductCacheEntity productCacheEntity = this.cacheProductService.getProductCacheEntityById(productOid);
		if (null != productCacheEntity.getRewardInterest() && BigDecimal.ZERO.compareTo(productCacheEntity.getRewardInterest()) != 0) {
			rep.setExpAror(DecimalUtil.zoomOut(productCacheEntity.getExpAror(), 100, 2).toString() + "%+"
					 + DecimalUtil.zoomOut(productCacheEntity.getRewardInterest(), 1, 2).toString());
			rep.setExpAror(DecimalUtil.zoomOut(productCacheEntity.getExpAror(), 100, 2).toString() + "%+"
					 + DecimalUtil.zoomOut(productCacheEntity.getRewardInterest(), 1, 2).toString());
		} else {
			rep.setExpAror(DecimalUtil.zoomOut(productCacheEntity.getExpAror(), 100, 2).toString());// 预计收益率
			rep.setExpArorExt(DecimalUtil.zoomOut(productCacheEntity.getExpArorSec(), 100, 2).toString());
		}

		rep.setRaiseStartDate(productCacheEntity.getRaiseStartDate());
		rep.setRaiseEndDate(productCacheEntity.getRaiseEndDate());
		rep.setSetupDate(productCacheEntity.getSetupDate());
		rep.setDurationPeriodEndDate(productCacheEntity.getDurationPeriodEndDate());
		rep.setRepayDate(productCacheEntity.getRepayDate());

		return rep;
	}
	
	/**
	 * 查询定期 我的持有中 产品详情
	 */
	public TnHoldingDetail queryTnHoldingDetailer(String userOid, String productOid) {
		TnHoldingDetail rep = new TnHoldingDetail();

		/** 家加财新增----增加定期产品的存续期  */
		Product product =  this.productService.getProductByOid(productOid);
		rep.setDurationPeriodDays(product.getDurationPeriodDays());
		rep.setProductName(product.getName());//增加产品名称
		ProductPeriodicDetailResp resp = this.productClientService.periodicDdetail(product.getOid(),userOid);
		rep.setIncomeCalcBasis(resp.getIncomeCalcBasis());
		rep.setFiles(resp.getFiles());
		rep.setInvestFiles(resp.getInvestFiles());
		rep.setServiceFiles(resp.getServiceFiles());
		
		HoldCacheEntity holdCacheEntity = this.cacheHoldService.getHoldCacheEntityByUidAndProductId(userOid, productOid);
		rep.setInvestVolume(holdCacheEntity.getTotalVolume());
		rep.setPayAmount(holdCacheEntity.getTotalVolume().add(holdCacheEntity.getExpectIncome()));
		rep.setExpectIncome(holdCacheEntity.getExpectIncome());
		rep.setExpectIncomeExt(holdCacheEntity.getExpectIncomeExt());
		/** 增加最近一次的购买时间  */
			rep.setLatestOrderTime(holdCacheEntity.getLatestOrderTime());
			
		ProductCacheEntity productCacheEntity = this.cacheProductService.getProductCacheEntityById(productOid);
		if (null != productCacheEntity.getRewardInterest() && BigDecimal.ZERO.compareTo(productCacheEntity.getRewardInterest()) != 0) {
			rep.setExpAror(DecimalUtil.zoomOut(productCacheEntity.getExpAror(), 100, 2).toString() + "%+"
					 + DecimalUtil.zoomOut(productCacheEntity.getRewardInterest(), 1, 2).toString() + "%");
			rep.setExpAror(DecimalUtil.zoomOut(productCacheEntity.getExpAror(), 100, 2).toString() + "%+"
					 + DecimalUtil.zoomOut(productCacheEntity.getRewardInterest(), 1, 2).toString() + "%");
		} else {
			rep.setExpAror(DecimalUtil.zoomOut(productCacheEntity.getExpAror(), 100, 2).toString() + "%");// 预计收益率
			rep.setExpArorExt(DecimalUtil.zoomOut(productCacheEntity.getExpArorSec(), 100, 2).toString() + "%");
		}

		rep.setRaiseStartDate(productCacheEntity.getRaiseStartDate());
		rep.setRaiseEndDate(productCacheEntity.getRaiseEndDate());
		rep.setSetupDate(productCacheEntity.getSetupDate());
		rep.setDurationPeriodEndDate(productCacheEntity.getDurationPeriodEndDate());
		rep.setRepayDate(productCacheEntity.getRepayDate());

		return rep;
	}

	/**
	 * 查询我的已结清定期产品详情
	 * 
	 * @param userOid
	 * @param proOid
	 * @return
	 */
	public MyClosedRegularInfoQueryRep closedregularinfo(String userOid, String proOid) {
		MyClosedRegularInfoQueryRep rep = new MyClosedRegularInfoQueryRep();
		List<Object[]> list = this.publisherHoldDao.myClosedregularDetail(userOid, proOid);
		if (list != null && list.size() > 0) {
			Object[] obj = list.get(0);
			rep.setInvestAmt(BigDecimalUtil.parseFromObject(obj[0]));// 投资金额
			rep.setTotalIncome(BigDecimalUtil.parseFromObject(obj[1]));// 累计收益
			try {
				if (obj[2] != null) {
					rep.setSDate(DateUtil.parse(obj[2].toString()));// 计息开始日
				}
				if (obj[3] != null) {
					rep.setEDate(DateUtil.parse(obj[3].toString()));// 计息截止日
				}
				if (obj[4] != null) {
					rep.setDayNum(Integer.parseInt(obj[4].toString()));// 总期限
				}
			} catch (Exception e) {
			}
		}
		Product product = this.productDao.findByOid(proOid);
		rep.setProductName(product.getName());//增加产品名称
		ProductPeriodicDetailResp resp = this.productClientService.periodicDdetail(product.getOid(),userOid);
		rep.setIncomeCalcBasis(resp.getIncomeCalcBasis());
		rep.setFiles(resp.getFiles());
		rep.setInvestFiles(resp.getInvestFiles());
		rep.setServiceFiles(resp.getServiceFiles());
		
		return rep;
	}

	public int resetToday() {
		int i = this.publisherHoldDao.resetToday();
		return i;

	}

	/** 发行人下投资人质量分析（某个投资金额范围内的投资人个数） */
	public List<Object[]> analyseInvestor(String publisherOid) {
		return this.publisherHoldDao.analyseInvestor(publisherOid);
	}


	public int countByPublisherBaseAccountAndInvestorBaseAccount(InvestorBaseAccountEntity investorBaseAccount, PublisherBaseAccountEntity publisherBaseAccount) {
		return publisherHoldDao.countByPublisherBaseAccountAndInvestorBaseAccount(investorBaseAccount.getOid(), publisherBaseAccount.getOid());
	}

	/** 平台下-投资人质量分析（某个投资金额范围内的投资人个数） */
	public List<Object[]> analysePlatformInvestor() {
		return this.publisherHoldDao.analysePlatformInvestor();
	}

	public BaseResp getMaxHoldVol(String investorOid, String productOid) {
		MaxHoldVolRep rep = new MaxHoldVolRep();

		BigDecimal maxHoldVol = this.publisherHoldDao.findMaxHoldVol(investorOid, productOid);
		rep.setMaxHoldVol(maxHoldVol == null ? BigDecimal.ZERO : maxHoldVol);
		return rep;
	}

	/**
	 * 查看总持仓份额是否等于可赎回份额,并且是全部赎回
	 * 
	 * @param product
	 * @param investorOid
	 * @return
	 */
	public void update4MinRedeem(InvestorTradeOrderEntity tradeOrder) {
		Product product = tradeOrder.getProduct();

		int i = 1;
		if (product.getMinRredeem() != null) {//如果是全部赎回
			i = this.publisherHoldDao.update4MinRedeem(tradeOrder.getProduct(), tradeOrder.getInvestorBaseAccount(), tradeOrder.getOrderAmount());
		}

		if (i < 1) {
			if (null != product.getMinRredeem() && product.getMinRredeem().compareTo(BigDecimal.ZERO) != 0) {
				if (tradeOrder.getOrderVolume().compareTo(product.getMinRredeem()) < 0) {
					// error.define[30013]=不满足单笔赎回下限(CODE:30013)
					throw new AMPException(30013);
				}
			}

			if (null != product.getAdditionalRredeem() && product.getAdditionalRredeem().compareTo(BigDecimal.ZERO) != 0) {
				if (null != product.getMinRredeem() && product.getMinRredeem().compareTo(BigDecimal.ZERO) != 0) {
					if (tradeOrder.getOrderVolume().subtract(product.getMinRredeem()).remainder(product.getAdditionalRredeem()).compareTo(BigDecimal.ZERO) != 0) {
						// error.define[30039]=不满足赎回追加份额(CODE:30039)
						throw new AMPException(30039);
					}
				} else {
					if (tradeOrder.getOrderVolume().remainder(product.getAdditionalRredeem()).compareTo(BigDecimal.ZERO) != 0) {
						// error.define[30039]=不满足赎回追加份额(CODE:30039)
						throw new AMPException(30039);
					}
				}
			}
		}
	}

	public void batchUpdate(List<PublisherHoldEntity> holds) {
		this.publisherHoldDao.save(holds);
	}

//	public PublisherHoldEntity findByProductAndInvestorBaseAccount(Product product, InvestorBaseAccountEntity investorBaseAccount) {
//		return this.publisherHoldDao.findByInvestorBaseAccountAndProduct(investorBaseAccount, product);
//	}

	public List<PublisherHoldEntity> findByInvestorOid(String investorOid) {
		return this.publisherHoldDao.findByInvestorOid(investorOid);
	}

	/**
	 * 批量获取持仓列表
	 * 
	 * @param lastOid
	 * @return
	 */
	public List<PublisherHoldEntity> getHoldByBatch(String lastOid) {
		return this.publisherHoldDao.getHoldByBatch(lastOid);
	}

	/**
	 * 获取SPV持仓列表
	 */
	public List<PublisherHoldEntity> getSPVHold() {
		return this.publisherHoldDao.getSPVHold();
	}

	/**
	 * 我的定期
	 */
	public MyHoldTnQueryRep queryMyTnHoldProList(String investorOid) {
		MyHoldTnQueryRep rep = new MyHoldTnQueryRep();
		//Pages<HoldingTnDetail> holdingTnDetails = new Pages<HoldingTnDetail>();
		Pages<HoldingTnDetail> holdingTnDetails = new Pages<HoldingTnDetail>();
		Pages<ClosedTnDetail> closedTnDetails = new Pages<ClosedTnDetail>();
		Pages<ToConfirmTnDetail> toConfirmTnDetails = new Pages<ToConfirmTnDetail>();
		rep.setHoldingTnDetails(holdingTnDetails);
		rep.setClosedTnDetails(closedTnDetails);
		rep.setToConfirmTnDetails(toConfirmTnDetails);
		//List<HoldCacheEntity> holds = this.cacheHoldService.findByInvestorOid(investorOid);
		List<PublisherHoldEntity> holds = this.findByInvestorOid(investorOid);
		Date incomeDate = DateUtil.getBeforeDate();
		for (PublisherHoldEntity hold : holds) {
			
			//ProductCacheEntity cacheProduct = this.cacheProductService.getProductCacheEntityById(hold.getProductOid());
			if (Product.TYPE_Producttype_02.equals(hold.getProduct().getType().getOid())) {
				continue;
			}
			rep.setTnCapitalAmount(rep.getTnCapitalAmount().add(hold.getTotalVolume()));
			rep.setTotalIncomeAmount(rep.getTotalIncomeAmount().add(hold.getHoldTotalIncome()));
			if (null != hold.getConfirmDate() && DateUtil.daysBetween(incomeDate, hold.getConfirmDate()) == 0) {
				rep.setTnYesterdayIncome(rep.getTnYesterdayIncome().add(hold.getHoldYesterdayIncome()));
			}

			/** 申请中 */
			if (PublisherHoldEntity.PUBLISHER_HOLD_HOLD_STATUS_toConfirm.equals(hold.getHoldStatus())) {
				ToConfirmTnDetail toConfirmTnDetail = new ToConfirmTnDetail();
				toConfirmTnDetail.setLastOrderTime(hold.getLatestOrderTime());
				toConfirmTnDetail.setProductOid(hold.getProduct().getOid());
				toConfirmTnDetail.setProductName(hold.getProduct().getName());
				toConfirmTnDetail.setToConfirmInvestAmount(hold.getToConfirmInvestVolume());
				toConfirmTnDetail.setSetupDate(hold.getProduct().getSetupDate());
				
				toConfirmTnDetails.add(toConfirmTnDetail);
				continue;
			}
			/** 退款中 */
			if (PublisherHoldEntity.PUBLISHER_HOLD_HOLD_STATUS_refunding.equals(hold.getHoldStatus())) {
				
				ToConfirmTnDetail toConfirmTnDetail = new ToConfirmTnDetail();
			
					toConfirmTnDetail.setLastOrderTime(hold.getLatestOrderTime());
				
				
				toConfirmTnDetail.setProductOid(hold.getProduct().getOid());
				toConfirmTnDetail.setProductName(hold.getProduct().getName());
				toConfirmTnDetail.setRefundAmount(hold.getTotalInvestVolume().add(hold.getHoldTotalIncome()));
				toConfirmTnDetail.setStatus(PublisherHoldEntity.PUBLISHER_HOLD_HOLD_STATUS_refunding);
				toConfirmTnDetail.setStatusDisp("退款中");
				toConfirmTnDetails.add(toConfirmTnDetail);
				continue;
			}
			/** 已结清 */
			if (PublisherHoldEntity.PUBLISHER_HOLD_HOLD_STATUS_closed.equals(hold.getHoldStatus())) {
				ClosedTnDetail closedTnDetail = new ClosedTnDetail();
		
				closedTnDetail.setLastOrderTime(hold.getLatestOrderTime());
						
				closedTnDetail.setProductOid(hold.getProduct().getOid());
				closedTnDetail.setProductName(hold.getProduct().getName());
				closedTnDetail.setOrderAmount(hold.getTotalInvestVolume().add(hold.getHoldTotalIncome()));
				closedTnDetail.setSetupDate(hold.getProduct().getSetupDate());
				closedTnDetail.setRepayDate(hold.getProduct().getRepayDate());
				closedTnDetail.setStatus(PublisherHoldEntity.PUBLISHER_HOLD_HOLD_STATUS_closed);
				closedTnDetail.setStatusDisp("已结清");
				closedTnDetails.add(closedTnDetail);
				continue;
			}
			/** 已退款 */
			if (PublisherHoldEntity.PUBLISHER_HOLD_HOLD_STATUS_refunded.equals(hold.getHoldStatus())) {
				ClosedTnDetail closedTnDetail = new ClosedTnDetail();
				
				
					closedTnDetail.setLastOrderTime(hold.getLatestOrderTime());
				
				
				closedTnDetail.setProductOid(hold.getProduct().getOid());
				closedTnDetail.setProductName(hold.getProduct().getName());
				closedTnDetail.setOrderAmount(hold.getTotalInvestVolume().add(hold.getHoldTotalIncome()).subtract(hold.getTotalVoucherAmount()));
				closedTnDetail.setSetupDate(hold.getProduct().getSetupDate());
				closedTnDetail.setRepayDate(hold.getProduct().getRepayDate());
				closedTnDetail.setRaiseFailDate(hold.getProduct().getRaiseFailDate());
				closedTnDetail.setStatus(PublisherHoldEntity.PUBLISHER_HOLD_HOLD_STATUS_refunded);
				closedTnDetail.setStatusDisp("已退款");
				closedTnDetails.add(closedTnDetail);
				continue;
			}

			if (PublisherHoldEntity.PUBLISHER_HOLD_HOLD_STATUS_holding.equals(hold.getHoldStatus())) {
				/** 申请中 */
				if (Product.STATE_Raising.equals(hold.getProduct().getState()) || Product.STATE_Raiseend.equals(hold.getProduct().getState())) {
					ToConfirmTnDetail toConfirmTnDetail = new ToConfirmTnDetail();
					
					
						toConfirmTnDetail.setLastOrderTime(hold.getLatestOrderTime());
					
					
					toConfirmTnDetail.setProductOid(hold.getProduct().getOid());
					toConfirmTnDetail.setProductName(hold.getProduct().getName());
					toConfirmTnDetail.setToConfirmInvestAmount(hold.getToConfirmInvestVolume());
					toConfirmTnDetail.setAcceptedAmount(hold.getHoldVolume());
					toConfirmTnDetail.setSetupDate(hold.getProduct().getSetupDate());
					toConfirmTnDetail.setStatus(PublisherHoldEntity.PUBLISHER_HOLD_HOLD_STATUS_toConfirm);
					toConfirmTnDetail.setStatusDisp("申请中");
					toConfirmTnDetails.add(toConfirmTnDetail);
					continue;
				}
				/** 持有中 */
				if (Product.STATE_Durationing.equals(hold.getProduct().getState())) {
					HoldingTnDetail holdingTnDetail = new HoldingTnDetail();
					holdingTnDetail.setProductOid(hold.getProduct().getOid());
					holdingTnDetail.setProductName(hold.getProduct().getName());
					if (null != hold.getProduct().getRewardInterest() && BigDecimal.ZERO.compareTo(hold.getProduct().getRewardInterest()) != 0) {
						holdingTnDetail.setExpYearRate(DecimalUtil.zoomOut(hold.getProduct().getExpAror(), 100, 2).toString() + "%+"
								 + DecimalUtil.zoomOut(hold.getProduct().getRewardInterest(), 1, 2).toString() + "%");
					} else {
						holdingTnDetail.setExpYearRate(DecimalUtil.zoomOut(hold.getProduct().getExpAror(), 100, 2).toString() + "%");
					}
					
					
					holdingTnDetail.setLastOrderTime(hold.getLatestOrderTime());
					
					
					holdingTnDetail.setInvestAmount(hold.getTotalInvestVolume().add(hold.getHoldTotalIncome()));
					holdingTnDetail.setDurationPeriodEndDate(hold.getProduct().getDurationPeriodEndDate());
					holdingTnDetail.setStatus(PublisherHoldEntity.PUBLISHER_HOLD_HOLD_STATUS_holding);
					holdingTnDetail.setStatusDisp("持有中");
					holdingTnDetails.add(holdingTnDetail);
				}
				/** 还本付息中 */
				if (Product.STATE_Durationend.equals(hold.getProduct().getState())) {
					HoldingTnDetail holdingTnDetail = new HoldingTnDetail();
					holdingTnDetail.setProductOid(hold.getProduct().getOid());
					holdingTnDetail.setProductName(hold.getProduct().getName());
					if (null != hold.getProduct().getRewardInterest() && BigDecimal.ZERO.compareTo(hold.getProduct().getRewardInterest()) != 0) {
						holdingTnDetail.setExpYearRate(DecimalUtil.zoomOut(hold.getProduct().getExpAror(), 100, 2).toString() + "%+"
								 +  DecimalUtil.zoomOut(hold.getProduct().getRewardInterest(), 1, 2).toString() + "%");
					} else {
						holdingTnDetail.setExpYearRate(DecimalUtil.zoomOut(hold.getProduct().getExpAror(), 100, 2).toString() + "%");
					}
					
					
					holdingTnDetail.setLastOrderTime(hold.getLatestOrderTime());
					holdingTnDetail.setInvestAmount(hold.getTotalVolume());
					holdingTnDetail.setDurationPeriodEndDate(hold.getProduct().getDurationPeriodEndDate());
					holdingTnDetail.setStatus(Product.PRODUCT_repayLoanStatus_repaying);
					holdingTnDetail.setStatusDisp("还本付息中");
					holdingTnDetails.add(holdingTnDetail);
				}
			}
		}
		/** 对持有中的产品进行排序  */
		if(holdingTnDetails.getRows().size()>1){
			Collections.sort(holdingTnDetails.getRows(),new Comparator<HoldingTnDetail>(){

				@Override
				public int compare(HoldingTnDetail o1, HoldingTnDetail o2) {
					 int flag = o1.getLastOrderTime().compareTo(o2.getLastOrderTime());
					 return -flag;
				}
				
			});
		}
		/** 对申请中的产品进行排序  */
		if(toConfirmTnDetails.getRows().size()>1){
			Collections.sort(toConfirmTnDetails.getRows(),new Comparator<ToConfirmTnDetail>(){

				@Override
				public int compare(ToConfirmTnDetail o1, ToConfirmTnDetail o2) {
					 int flag = o1.getLastOrderTime().compareTo(o2.getLastOrderTime());
					 return -flag;
				}
				
			});
		}
		/** 对已结清的产品进行排序  */
		if(closedTnDetails.getRows().size()>1){
			Collections.sort(closedTnDetails.getRows(),new Comparator<ClosedTnDetail>(){

				@Override
				public int compare(ClosedTnDetail o1, ClosedTnDetail o2) {
					 int flag = o1.getLastOrderTime().compareTo(o2.getLastOrderTime());
					 return -flag;
				}
				
			});
		}
		
		holdingTnDetails.setTotal(holdingTnDetails.getRows().size());
		closedTnDetails.setTotal(closedTnDetails.getRows().size());
		toConfirmTnDetails.setTotal(toConfirmTnDetails.getRows().size());
		return rep;
	}

//	public PublisherHoldEntity findByInvestorBaseAccountAndProduct(InvestorBaseAccountEntity investorBaseAccount, Product product) {
//		return this.publisherHoldDao.findByInvestorBaseAccountAndProduct(investorBaseAccount, product);
//	}
	//Added for whish plan
	public PublisherHoldEntity findByInvestorBaseAccountAndProductAndWishplanOid(InvestorBaseAccountEntity investorBaseAccount, Product product, String wishplanOid) {
		return this.publisherHoldDao.findByInvestorBaseAccountAndProductAndWishplanOid(investorBaseAccount, product, wishplanOid);
	}

	public List<PublisherHoldEntity> findByProductPaged(Product product, String accountType, String lastOid) {
		return this.publisherHoldDao.findByProductPaged(product.getOid(), accountType, lastOid);
	}

	public List<PublisherHoldEntity> findByProductPaged(String productOid, String accountType, String lastOid) {
		return this.publisherHoldDao.findByProductPaged(productOid, accountType, lastOid);
	}

	/**
	 * 获取含有体验金的用户
	 */
	public List<PublisherHoldEntity> getAllExpHolds(String lastOid) {

		return this.publisherHoldDao.getAllExpHolds(lastOid);
	}

	public String getFlatWareHoldStatus(InvestorTradeOrderEntity orderEntity) {
		if (InvestorTradeOrderEntity.TRADEORDER_orderType_cash.equals(orderEntity.getOrderType())) {
			return PublisherHoldEntity.PUBLISHER_HOLD_HOLD_STATUS_closed;
		}
		if (InvestorTradeOrderEntity.TRADEORDER_orderType_cashFailed.equals(orderEntity.getOrderType())) {
			return PublisherHoldEntity.PUBLISHER_HOLD_HOLD_STATUS_refunded;
		}
		if (InvestorTradeOrderEntity.TRADEORDER_orderType_normalRedeem.equals(orderEntity.getOrderType())) {
			return PublisherHoldEntity.PUBLISHER_HOLD_HOLD_STATUS_holding;
		}
		if (InvestorTradeOrderEntity.TRADEORDER_orderType_expGoldRedeem.equals(orderEntity.getOrderType())) {
			return PublisherHoldEntity.PUBLISHER_HOLD_HOLD_STATUS_holding;
		}
		if (InvestorTradeOrderEntity.TRADEORDER_orderType_clearRedeem.equals(orderEntity.getOrderType())) {
			return PublisherHoldEntity.PUBLISHER_HOLD_HOLD_STATUS_holding;
		}
		if (InvestorTradeOrderEntity.TRADEORDER_orderType_reRedeem.equals(orderEntity.getOrderType())) {
			return PublisherHoldEntity.PUBLISHER_HOLD_HOLD_STATUS_holding;
		}
		throw new AMPException("订单类型异常");

	}

	
	/**
	 * 从订单表中查询我的定期的每条记录
	 * 
	 * 
	 * */
	
	public MyHoldTnQueryRep queryMyTnHoldProLists(String userOid) {

		MyHoldTnQueryRep rep = new MyHoldTnQueryRep();
		Pages<HoldingTnDetail> holdingTnDetails = new Pages<HoldingTnDetail>();
		Pages<ClosedTnDetail> closedTnDetails = new Pages<ClosedTnDetail>();
		Pages<ToConfirmTnDetail> toConfirmTnDetails = new Pages<ToConfirmTnDetail>();
		rep.setToConfirmTnDetails(toConfirmTnDetails);
		rep.setClosedTnDetails(closedTnDetails);
		rep.setHoldingTnDetails(holdingTnDetails);
		List<InvestorTradeOrderEntity> list = this.investorTradeOrderService.findByInvestorOid(userOid,
				"PRODUCTTYPE_01");
		Date incomeDate = DateUtil.getBeforeDate();
		if (list != null && list.size() > 0) {
			for (InvestorTradeOrderEntity tradeOrder : list) {
				rep.setTotalIncomeAmount(rep.getTotalIncomeAmount().add(tradeOrder.getTotalIncome()));
				if (null != tradeOrder.getConfirmDate()
						&& DateUtil.daysBetween(incomeDate, tradeOrder.getConfirmDate()) == 0) {
					rep.setTnYesterdayIncome(rep.getTnYesterdayIncome().add(tradeOrder.getYesterdayIncome()));
				}

				// 获取申请中的1
				if (tradeOrder.getHoldStatus().equals(InvestorTradeOrderEntity.TRADEORDER_holdStatus_toConfirm)) {
					ToConfirmTnDetail toConfirm = new ToConfirmTnDetail();
					toConfirm.setOrderCode(tradeOrder.getOrderCode());// 获取订单号
					toConfirm.setProductOid(tradeOrder.getProduct().getOid());// 产品id
					toConfirm.setProductName(tradeOrder.getProduct().getName());// 产品名称
					toConfirm.setLastOrderTime(tradeOrder.getOrderTime());// 订单时间
					toConfirm.setToConfirmInvestAmount(tradeOrder.getOrderAmount());// 待确认金额
					toConfirm.setAcceptedAmount(BigDecimal.ZERO);// 已经确认份额
					toConfirm.setSetupDate(tradeOrder.getProduct().getSetupDate());// 预计产品成立日
					toConfirm.setStatus(InvestorTradeOrderEntity.TRADEORDER_holdStatus_toConfirm);
					toConfirm.setStatusDisp("申请中");
					toConfirmTnDetails.add(toConfirm);
					rep.setTnCapitalAmount(rep.getTnCapitalAmount().add(toConfirm.getToConfirmInvestAmount())
							.add(toConfirm.getAcceptedAmount()));// 总投资份额
					continue;

				}
				
				

				if (tradeOrder.getHoldStatus().equals(InvestorTradeOrderEntity.TRADEORDER_holdStatus_holding)) {

					// 申请中3
					if ((tradeOrder.getProduct().getState().equals(Product.STATE_Raising)
							|| tradeOrder.getProduct().getState().equals(Product.STATE_Raiseend))) {
						ToConfirmTnDetail toConfirm = new ToConfirmTnDetail();

						if (tradeOrder.getPublisherConfirmStatus() != null && tradeOrder.getPublisherConfirmStatus()
								.equals(InvestorTradeOrderEntity.TRADEORDER_publisherConfirmStatus_confirmed)) {
							toConfirm.setAcceptedAmount(tradeOrder.getOrderAmount().add(tradeOrder.getTotalIncome()));
							toConfirm.setToConfirmInvestAmount(BigDecimal.ZERO);
						} else {
							toConfirm.setAcceptedAmount(BigDecimal.ZERO);
							toConfirm.setToConfirmInvestAmount(
									tradeOrder.getOrderAmount().add(tradeOrder.getTotalIncome()));
						}
						toConfirm.setOrderCode(tradeOrder.getOrderCode());// 获取订单号
						toConfirm.setProductOid(tradeOrder.getProduct().getOid());// 产品id
						toConfirm.setProductName(tradeOrder.getProduct().getName());// 产品名称
						toConfirm.setLastOrderTime(tradeOrder.getOrderTime());// 订单时间
						toConfirm.setSetupDate(tradeOrder.getProduct().getSetupDate());// 预计产品成立日

						toConfirm.setStatus(InvestorTradeOrderEntity.TRADEORDER_holdStatus_toConfirm);
						toConfirm.setStatusDisp("申请中");
						toConfirmTnDetails.add(toConfirm);
						rep.setTnCapitalAmount(rep.getTnCapitalAmount().add(toConfirm.getToConfirmInvestAmount())
								.add(toConfirm.getAcceptedAmount()));// 总投资份额

						continue;
					}

					/** 持有中1 */
					if (Product.STATE_Durationing.equals(tradeOrder.getProduct().getState())) {
						HoldingTnDetail holdingTnDetail = new HoldingTnDetail();
						holdingTnDetail.setProductOid(tradeOrder.getProduct().getOid());
						holdingTnDetail.setProductName(tradeOrder.getProduct().getName());
						holdingTnDetail.setLastOrderTime(tradeOrder.getOrderTime());

						if (null != tradeOrder.getProduct().getRewardInterest()
								&& BigDecimal.ZERO.compareTo(tradeOrder.getProduct().getRewardInterest()) != 0) {
							holdingTnDetail.setExpYearRate(
									DecimalUtil.zoomOut(tradeOrder.getProduct().getExpAror(), 100, 2).toString() + "%+"
											+ DecimalUtil.zoomOut(tradeOrder.getProduct().getRewardInterest(), 1, 2)
													.toString()
											+ "%");
						} else {
							holdingTnDetail.setExpYearRate(
									DecimalUtil.zoomOut(tradeOrder.getProduct().getExpAror(), 100, 2).toString() + "%");
						}

						holdingTnDetail.setInvestAmount(tradeOrder.getOrderAmount().add(tradeOrder.getTotalIncome()));
						holdingTnDetail.setDurationPeriodEndDate(tradeOrder.getProduct().getDurationPeriodEndDate());
						holdingTnDetail.setStatus(InvestorTradeOrderEntity.TRADEORDER_holdStatus_holding);
						holdingTnDetail.setStatusDisp("持有中");
						holdingTnDetail.setLastOrderTime(tradeOrder.getOrderTime());
						holdingTnDetails.add(holdingTnDetail);
						rep.setTnCapitalAmount(rep.getTnCapitalAmount().add(holdingTnDetail.getInvestAmount()));// 总投资份额

					}
					/** 还本付息中2 */
					if (Product.STATE_Durationend.equals(tradeOrder.getProduct().getState())) {
						HoldingTnDetail holdingTnDetail = new HoldingTnDetail();
						holdingTnDetail.setProductOid(tradeOrder.getProduct().getOid());
						holdingTnDetail.setProductName(tradeOrder.getProduct().getName());
						if (null != tradeOrder.getProduct().getRewardInterest()
								&& BigDecimal.ZERO.compareTo(tradeOrder.getProduct().getRewardInterest()) != 0) {
							holdingTnDetail.setExpYearRate(
									DecimalUtil.zoomOut(tradeOrder.getProduct().getExpAror(), 100, 2).toString() + "%+"
											+ DecimalUtil.zoomOut(tradeOrder.getProduct().getRewardInterest(), 1, 2)
													.toString()
											+ "%");
						} else {
							holdingTnDetail.setExpYearRate(
									DecimalUtil.zoomOut(tradeOrder.getProduct().getExpAror(), 100, 2).toString() + "%");
						}

						holdingTnDetail.setOrderCode(tradeOrder.getOrderCode());
						holdingTnDetail.setInvestAmount(tradeOrder.getOrderAmount().add(tradeOrder.getTotalIncome()));
						holdingTnDetail.setDurationPeriodEndDate(tradeOrder.getProduct().getDurationPeriodEndDate());
						holdingTnDetail.setStatus(Product.PRODUCT_repayLoanStatus_repaying);
						holdingTnDetail.setStatusDisp("还本付息中");
						holdingTnDetail.setLastOrderTime(tradeOrder.getOrderTime());
						holdingTnDetails.add(holdingTnDetail);
						rep.setTnCapitalAmount(rep.getTnCapitalAmount().add(holdingTnDetail.getInvestAmount()));// 总投资份额
					}
				}

				// 获取定期产品已经结清的1
				if (tradeOrder.getHoldStatus().equals(InvestorTradeOrderEntity.TRADEORDER_holdStatus_closed)) {
					ClosedTnDetail closedTnDetail = new ClosedTnDetail();
					closedTnDetail.setLastOrderTime(tradeOrder.getOrderTime());
					closedTnDetail.setOrderAmount(tradeOrder.getOrderAmount().add(tradeOrder.getTotalIncome()));
					closedTnDetail.setOrderCode(tradeOrder.getOrderCode());// 获取订单号
					closedTnDetail.setProductOid(tradeOrder.getProduct().getOid());// 产品id
					closedTnDetail.setProductName(tradeOrder.getProduct().getName());// 产品名称
					closedTnDetail.setRepayDate(tradeOrder.getProduct().getRepayDate());// 还本付息日
					closedTnDetail.setSetupDate(tradeOrder.getProduct().getSetupDate());// 产品成立日
					closedTnDetail.setStatus(InvestorTradeOrderEntity.TRADEORDER_holdStatus_closed);
					closedTnDetail.setStatusDisp("已结清");
					closedTnDetails.add(closedTnDetail);
					continue;
				}
				/** 已经退款的 2 */
				if (tradeOrder.getHoldStatus().equals(InvestorTradeOrderEntity.TRADEORDER_holdStatus_refunded)) {
					ClosedTnDetail closedTnDetail = new ClosedTnDetail();
					closedTnDetail.setLastOrderTime(tradeOrder.getOrderTime());
					closedTnDetail.setOrderAmount(tradeOrder.getOrderAmount().add(tradeOrder.getTotalIncome()));
					closedTnDetail.setOrderCode(tradeOrder.getOrderCode());// 获取订单号
					closedTnDetail.setProductOid(tradeOrder.getProduct().getOid());// 产品id
					closedTnDetail.setProductName(tradeOrder.getProduct().getName());// 产品名称
					closedTnDetail.setRaiseFailDate(tradeOrder.getProduct().getRaiseFailDate());// 退款日
					closedTnDetail.setSetupDate(tradeOrder.getProduct().getSetupDate());// 产品成立日
					closedTnDetail.setStatus(InvestorTradeOrderEntity.TRADEORDER_holdStatus_refunded);
					closedTnDetail.setStatusDisp("已退款");
					closedTnDetails.add(closedTnDetail);

					continue;
				}
			}
		}
		holdingTnDetails.setTotal(holdingTnDetails.getRows().size());
		closedTnDetails.setTotal(closedTnDetails.getRows().size());
		toConfirmTnDetails.setTotal(toConfirmTnDetails.getRows().size());
		return rep;
	}

	public MyHoldT0QueryRep queryMyT0HoldProLists(String userOid) {
		MyHoldT0QueryRep rep = new MyHoldT0QueryRep();
		Pages<HoldingT0Detail> holdingDetails = new Pages<HoldingT0Detail>();
		Pages<ToConfirmT0Detail> toConfirmDetails = new Pages<ToConfirmT0Detail>();
		Date incomeDate = DateUtil.getBeforeDate();
		//从合仓表中获取用户购买的产品的所有订单
		List<InvestorTradeOrderEntity> list = this.investorTradeOrderService.findByInvestorOid(userOid,
				"PRODUCTTYPE_02");
		
		if(list != null && list.size() > 0){
			for(InvestorTradeOrderEntity tradeOrder : list){
				
				
				if (null != tradeOrder.getConfirmDate() && DateUtil.daysBetween(incomeDate, tradeOrder.getConfirmDate()) == 0) {
					rep.setT0YesterdayIncome(rep.getT0YesterdayIncome().add(tradeOrder.getYesterdayIncome()));
				}
				rep.setTotalIncomeAmount(rep.getTotalIncomeAmount().add(tradeOrder.getTotalIncome()));
				
				//申请中
				if(tradeOrder.getHoldStatus().equals(InvestorTradeOrderEntity.TRADEORDER_holdStatus_toConfirm)){
					ToConfirmT0Detail t0ConfirmDetail = new ToConfirmT0Detail();
					t0ConfirmDetail.setProductName(tradeOrder.getProduct().getName());
					t0ConfirmDetail.setOrderCode(tradeOrder.getOrderCode());
					t0ConfirmDetail.setLastOrderTime(tradeOrder.getOrderTime());
					t0ConfirmDetail.setToConfirmInvestVolume(tradeOrder.getOrderAmount());
					
					toConfirmDetails.add(t0ConfirmDetail);
					rep.setT0CapitalAmount(rep.getT0CapitalAmount().add(t0ConfirmDetail.getToConfirmInvestVolume()));
					continue;
				}
				
				if(tradeOrder.getHoldStatus().equals(InvestorTradeOrderEntity.TRADEORDER_holdStatus_holding)){
					HoldingT0Detail holdingT0Detail = new HoldingT0Detail();
					holdingT0Detail.setOrderCode(tradeOrder.getOrderCode());//订单号
					holdingT0Detail.setLastOrderTime(tradeOrder.getOrderTime());//投资时间
					holdingT0Detail.setProductOid(tradeOrder.getProduct().getOid());//产品id
					holdingT0Detail.setProductName(tradeOrder.getProduct().getName());//产品名称
					holdingT0Detail.setHoldTotalIncome(tradeOrder.getTotalIncome());//持有的收益
					holdingT0Detail.setValue(tradeOrder.getHoldVolume());//持有的本息和
								
					if (null != tradeOrder.getConfirmDate() && DateUtil.daysBetween(incomeDate, tradeOrder.getConfirmDate()) == 0) {
						holdingT0Detail.setYesterdayIncome(tradeOrder.getYesterdayIncome());
					}
				
					holdingDetails.add(holdingT0Detail);
					
					rep.setT0CapitalAmount(rep.getT0CapitalAmount().add(holdingT0Detail.getValue()));
					continue;
				}
				
				//持有中，当份额确认后，赎回状态有原来的holding变为partHolding
				if(tradeOrder.getHoldStatus().equals(InvestorTradeOrderEntity.TRADEORDER_holdStatus_partHolding)){
					
						HoldingT0Detail holdingT0Detail = new HoldingT0Detail();
						holdingT0Detail.setValue(tradeOrder.getHoldVolume());//持有的本息和
						if(holdingT0Detail.getValue().compareTo(new BigDecimal(0)) > 0){
						holdingT0Detail.setOrderCode(tradeOrder.getOrderCode());//订单号
						holdingT0Detail.setLastOrderTime(tradeOrder.getOrderTime());//投资时间
						holdingT0Detail.setProductOid(tradeOrder.getProduct().getOid());//产品id
						holdingT0Detail.setProductName(tradeOrder.getProduct().getName());//产品名称
						holdingT0Detail.setHoldTotalIncome(tradeOrder.getTotalIncome());//持有的收益
						holdingT0Detail.setValue(tradeOrder.getHoldVolume());//持有的本息和
						
						if (null != tradeOrder.getConfirmDate() && DateUtil.daysBetween(incomeDate, tradeOrder.getConfirmDate()) == 0) {
							holdingT0Detail.setYesterdayIncome(tradeOrder.getYesterdayIncome());
						}
						holdingDetails.add(holdingT0Detail);
						rep.setT0CapitalAmount(rep.getT0CapitalAmount().add(holdingT0Detail.getValue()));
						
					}
						continue;
				}
			}
		}
				
		holdingDetails.setTotal(holdingDetails.getRows().size());
		toConfirmDetails.setTotal(toConfirmDetails.getRows().size());
		rep.setToConfirmDetails(toConfirmDetails);
		rep.setHoldingDetails(holdingDetails);

		return rep;
		
	}
	
	

}
