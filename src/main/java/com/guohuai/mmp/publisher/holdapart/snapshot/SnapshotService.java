package com.guohuai.mmp.publisher.holdapart.snapshot;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guohuai.ams.label.LabelEnum;
import com.guohuai.ams.label.LabelService;
import com.guohuai.ams.product.Product;
import com.guohuai.ams.product.ProductService;
import com.guohuai.ams.productLabel.ProductLabelService;
import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.DateUtil;
import com.guohuai.component.util.StringUtil;
import com.guohuai.mmp.investor.cashflow.InvestorCashFlowService;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderService;
import com.guohuai.mmp.job.lock.JobLockEntity;
import com.guohuai.mmp.job.lock.JobLockService;
import com.guohuai.mmp.job.log.JobLogEntity;
import com.guohuai.mmp.job.log.JobLogFactory;
import com.guohuai.mmp.job.log.JobLogService;
import com.guohuai.mmp.platform.StaticProperties;
import com.guohuai.mmp.platform.publisher.dividend.offset.DividendOffsetService;
import com.guohuai.mmp.publisher.product.rewardincomepractice.PracticeService;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class SnapshotService {

	@Autowired
	private SnapshotDao snapshotDao;
	@Autowired
	private InvestorTradeOrderService investorTradeOrderService;
	@Autowired
	private JobLockService jobLockService;
	@Autowired
	private JobLogService jobLogService;
	@Autowired
	private ProductService productService;
	@Autowired
	private ProductLabelService productLabelService;
	@Autowired
	private LabelService labelService;
	@Autowired
	private SnapshotServiceRequiresNew snapshotServiceRequiresNew;
	@Autowired
	private PracticeService practiceService;
	@Autowired
	private DividendOffsetService dividendOffsetService;
	@Autowired
	private InvestorCashFlowService investorCashFlowService;
//<<<<<<< HEAD
//	/**
//	 * 实际保存的计息收益精度
//	 */
//    public static int interestPrecisionReserved;
//    @Value("${interest.precision.reserved:8}")
//    public void setInterestPrecisionReserved(int v){
//    	interestPrecisionReserved=v;
//    }
//    
//    /**
//	 * 实际有效的计息收益精度
//	 */
//    public static int interestPrecisionAvailable;
//    @Value("${interest.precision.available:2}")
//    public void setInterestPrecisionAvailable(int v){
//    	interestPrecisionAvailable=v;
//    }
//=======
//	@Value("${precision.snapshot.digit:8}")
//    private int digit;
//>>>>>>> gh_dev1.0.2
	
	public void snapshot() {
		if (this.jobLockService.getRunPrivilege(JobLockEntity.JOB_jobId_snapshot)) {
			this.snapshotDo();
		}
	}

	/**
	 * 计息份额快照
	 */
	public void snapshotDo() {
		
		JobLogEntity jobLog = JobLogFactory.getInstance(JobLockEntity.JOB_jobId_snapshot);
		
		try {
			Date incomeDate = StaticProperties.isIs24() ? DateUtil.getBeforeDate() : DateUtil.getSqlDate();
			/**
			 * 活期产品 募集期、清盘中 发放收益
			 */
			List<Product> productList = productService.findProductT04Snapshot();
			for (Product product : productList) {
				String productLabel = productLabelService.findLabelByProduct(product);
				if (labelService.isProductLabelHasAppointLabel(productLabel, LabelEnum.tiyanjin.toString())) {
					investorTradeOrderService.snapshotTasteCouponVolume(product, incomeDate);
				} else {
					if (Product.PRODUCT_incomeDealType_reinvest.equals(product.getIncomeDealType())) {
						investorTradeOrderService.snapshotT0Volume(product, incomeDate);
					}
					if (Product.PRODUCT_incomeDealType_cash.equals(product.getIncomeDealType())) {
						investorTradeOrderService.snapshotT0CashVolume(product, incomeDate);
					}
					
					if (Product.PRODUCT_redeemWithoutInterest_on.equals(product.getRedeemWithoutInterest())) {
						String lastOid = "0";
						while (true) {
							List<String> oids = this.investorTradeOrderService.findByProductOid(product.getOid(), lastOid);
							if (oids.size() == 0) {
								break;
							}
							for (String orderOid : oids) {
								this.snapshotServiceRequiresNew.flatWare(orderOid, incomeDate);
								lastOid = orderOid;
							}
						}
					}
				}
			}
			/**
			 * 定期产品 募集期、募集结束在募集失败和募集成立之前发放收益
			 */
			productList = productService.findProductTn4Snapshot(incomeDate);
			for (Product product : productList) {
				investorTradeOrderService.snapshotTnVolume(product, incomeDate);
			}

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			jobLog.setJobMessage(AMPException.getStacktrace(e));
			jobLog.setJobStatus(JobLogEntity.JOB_jobStatus_failed);
		}
		jobLog.setBatchEndTime(DateUtil.getSqlCurrentDate());
		this.jobLogService.saveEntity(jobLog);
		
		this.jobLockService.resetJob(JobLockEntity.JOB_jobId_snapshot);
		
	}

	
	public SnapshotEntity findByOrderAndSnapShotDate(String orderOid, Date incomeDate) {
		SnapshotEntity entity = this.snapshotDao.findByOrderAndSnapShotDate(orderOid, incomeDate);

		return entity;
	}

	public int increaseSnapshotVolume(String orderOid, BigDecimal holdIncomeVolume, Date incomeDate) {

		return this.snapshotDao.increaseSnapshotVolume(orderOid, holdIncomeVolume, incomeDate);

	}

	public List<SnapshotEntity> findByHoldOidAndSnapShotDate(String holdOid, Date incomeDate) {
		return this.snapshotDao.findByHoldOidAndSnapShotDate(holdOid, incomeDate);
	}
	
	
	
	
	@Transactional(value=Transactional.TxType.REQUIRES_NEW)
	public int remainderIncomeWithoutRewardIncome(String productOid, BigDecimal baseIncomeRatio, Date incomeDate){
		log.info("incomeDate={},productOid={},复利无奖励收益,在快照表中算出收益 start", incomeDate, productOid);
		int result=this.snapshotDao.remainderIncomeWithoutRewardIncome(productOid, baseIncomeRatio, incomeDate);
		log.info("incomeDate={},productOid={},复利无奖励收益,在快照表中算出收益{}", incomeDate, productOid, result > 0 ? "success" : "fail");
		return result;
	}
	
	
	
	@Transactional(value=Transactional.TxType.REQUIRES_NEW)
	public int remainderIncome(String productOid, BigDecimal baseIncomeRatio,Date incomeDate){
		log.info("incomeDate={},productOid={},复利有奖励收益,在快照表中算出收益 start", incomeDate, productOid);
		int result= this.snapshotDao.remainderIncome(productOid, baseIncomeRatio, incomeDate);
		log.info("incomeDate={},productOid={},复利有奖励收益,在快照表中算出收益{}", incomeDate, productOid, result > 0 ? "success" : "fail");
		return result;
	}
	
	@Transactional(value=Transactional.TxType.REQUIRES_NEW)
	public int remainderNoRewardIncome(String productOid, BigDecimal baseIncomeRatio,Date incomeDate){
		int result= this.snapshotDao.remainderNoRewardIncome(productOid, baseIncomeRatio, incomeDate);
		log.info("结束复利无奖励收益，结果为{}",result>0);
		return result;
	}
	
	
	/**
	 * 一次性付息无奖励收益
	 */
	@Transactional(value=Transactional.TxType.REQUIRES_NEW)
	public int distributeInterestSingleWithoutRewardIncome(String productOid, BigDecimal baseIncomeRatio, Date incomeDate,int holdDays,int incomeCalcBasis){
		log.info("incomeDate={},productOid={},单利无奖励收益,在快照表中算出收益 start", incomeDate, productOid);
		int result= this.snapshotDao.distributeInterestSingleWithoutRewardIncome(productOid, baseIncomeRatio, incomeDate, holdDays, incomeCalcBasis);
		log.info("incomeDate={},productOid={},单利无奖励收益,在快照表中算出收益{}", incomeDate, productOid, result > 0 ? "success" : "fail");
		return result;
	}
	
	/**
	 * 一次性付息有奖励收益
	 * @param productOid
	 * @param baseIncomeRatio
	 * @param rewardIncomeRatio
	 * @param incomeDate
	 * @return
	 */
	@Transactional(value=Transactional.TxType.REQUIRES_NEW)
	public int distributeInterestSingleWithRewardIncome(String productOid, BigDecimal baseIncomeRatio, Date incomeDate,int holdDays,int incomeCalcBasis){
		log.info("incomeDate={},productOid={},单利有奖励收益,在快照表中算出收益 start", incomeDate, productOid);
		int i = this.snapshotDao.distributeInterestSingleWithRewardIncome(productOid, baseIncomeRatio, incomeDate, holdDays, incomeCalcBasis);
		log.info("incomeDate={},productOid={},单利有奖励收益,在快照表中算出收益{}", incomeDate, productOid, i > 0 ? "success" : "fail");
		return i;
	}
	
	/**
	 * 根据订单表分发收益
	 */
	@Transactional(value=Transactional.TxType.REQUIRES_NEW)
	public int distributeOrderInterest(String productOid,Date incomeDate,BigDecimal netUnitShare, String incomeDealType){
		int result = 0;
		log.info("incomeDate={},productOid={},更新分仓收益", incomeDate, productOid);
		/** 收益结转 */
		if (Product.PRODUCT_incomeDealType_reinvest.equals(incomeDealType)) {
			log.info("incomeDate={},productOid={},更新分仓收益,收益结转start", incomeDate, productOid);
			result = this.snapshotDao.distributeOrderInterestAndPrincipal(productOid, incomeDate, netUnitShare);
			log.info("incomeDate={},productOid={},更新分仓收益,收益结转{}", incomeDate, productOid, result > 0 ? "success" : "fail");
		}
		
		/** 现金分红 */
		if (Product.PRODUCT_incomeDealType_cash.equals(incomeDealType)) {
			log.info("incomeDate={},productOid={},更新分仓收益,现金分红start", incomeDate, productOid);
			result = this.snapshotDao.distributeOrderInterest(productOid, incomeDate);
			log.info("incomeDate={},productOid={},更新分仓收益,现金分红{}", incomeDate, productOid, result > 0 ? "success" : "fail");
		}
		return result;
	}
	
	/**
	 * 记录分仓收益
	 */
	@Transactional(value=Transactional.TxType.REQUIRES_NEW)
	public int distributeInterestToInvestorIncome(String productOid,Date incomeDate,String incomeOid){
		int result=0;
		if (this.snapshotDao.hasDistributedInterestToInvestorIncome(productOid, incomeDate)==0) {
			log.info("incomeDate={},productOid={},记录分仓收益start", incomeDate, productOid);
			result= this.snapshotDao.distributeInterestToInvestorIncome(productOid, incomeDate, incomeOid);
			log.info("incomeDate={},productOid={},记录分仓收益{}", incomeDate, productOid, result > 0 ? "success" : "fail");
		}
		
		return result;
	}
	
	/**
	 * 记录阶梯收益
	 */
	@Transactional(value = Transactional.TxType.REQUIRES_NEW)
	public int distributeInterestToInvestorLevelIncome(String productOid,Date incomeDate,BigDecimal netUnitShare){
		int result = 0;
		if (this.snapshotDao.hasDistributedInterestToInvestorLevelIncome(productOid, incomeDate)==0) {
			log.info("incomeDate={},productOid={},记录阶梯收益start", incomeDate, productOid);
			result= this.snapshotDao.distributeInterestToInvestorLevelIncome(productOid, incomeDate);
			log.info("incomeDate={},productOid={},记录阶梯收益{}", incomeDate, productOid, result > 0 ? "success" : "fail");
		}
		return result;
	}
	
	/**
	 * 记录合仓收益
	 */
	@Transactional(value = Transactional.TxType.REQUIRES_NEW)
	public int distributeInterestToInvestorHoldIncome(String productOid,Date incomeDate, String incomeOid, String incomeDealType){
		int result=0;
		if (this.snapshotDao.hasDistributedInterestToInvestorHoldIncome(productOid, incomeDate) ==0) {
			log.info("incomeDate={},productOid={},记录合仓收益start", incomeDate, productOid);
			result= this.snapshotDao.distributeInterestToInvestorHoldIncome(productOid, incomeDate, incomeOid);
			log.info("incomeDate={},productOid={},记录合仓收益{}", incomeDate, productOid, result > 0 ? "success" : "fail");
		}
		return result;
	}
	
	/**
	 * 现金分红
	 */
	@Transactional(value=Transactional.TxType.REQUIRES_NEW)
	public void dividend(String productOid, Date incomeDate, String publisherOid) {
		if (this.snapshotDao.hasDistributedInterestToInvestorHoldIncome(productOid, incomeDate) != 0) {
			log.info("合仓收益已记录");
			if (!dividendOffsetService.isDividend(productOid, incomeDate)) {
				log.info("incomeDate={},productOid={},现金分红批次和订单start", incomeDate, productOid);
				BigDecimal dividendAmount = this.snapshotDao.getTotalIncomeByProductOidAndIncomeDate(productOid, incomeDate);
				
				int toCloseDividendNumber = this.snapshotDao.getTotalDividendNumberByProductOidAndIncomeDate(productOid, incomeDate);
				String dividendOffsetOid = StringUtil.uuid();
				this.dividendOffsetService.createDividendOffset(dividendOffsetOid, productOid, incomeDate, dividendAmount, toCloseDividendNumber);
				if (dividendAmount.compareTo(BigDecimal.ZERO) > 0) {
					dividendOffsetService.deleteOrdersMd();
					dividendOffsetService.insertIntoOrdersMd(productOid, publisherOid, dividendOffsetOid, incomeDate);
					/** 创建现金分红订单 */
					int result = dividendOffsetService.createDividendOrders();
					/** 创建现金分红资金流水 */
					result = investorCashFlowService.createDividendCashFlow();
							
					log.info("incomeDate={},productOid={},现金分红单生成{}", incomeDate, productOid, result > 0 ? "success" : "fail");
				}
				
			}
		}
	}
	
	/**
	 * 再次更新投资者收益明细的引用holdIncomeOid，levelIncomeOid
	 * @param productOid
	 * @param incomeDate
	 * @return
	 */
	@Transactional(value=Transactional.TxType.REQUIRES_NEW)
	public int reupdateInvestorIncomeWithRewardIncome(String productOid,Date incomeDate){
		int result=0;
		if (this.snapshotDao.hasReupdatedInvestorIncomeWithRewardIncome(productOid, incomeDate) == 0) {
			result= this.snapshotDao.reupdateInvestorIncomeWithRewardIncome(productOid, incomeDate);
			this.snapshotDao.reupdateLevelIncomeWithoutRewardIncome(productOid, incomeDate);
		}
		log.info("结束再次更新投资者收益明细的引用holdIncomeOid，levelIncomeOid，结果为{}",result>0);
		return result;
	}
	
	/**
	 * 再次更新投资者收益明细的引用holdIncomeOid
	 * @param productOid
	 * @param incomeDate
	 * @return
	 */
	@Transactional(value=Transactional.TxType.REQUIRES_NEW)
	public int reupdateInvestorIncomeWithoutRewardIncome(String productOid,Date incomeDate){
		int result=0;
		if (this.snapshotDao.hasReupdatedInvestorIncomeWithoutRewardIncome(productOid, incomeDate) == 0) {
			result= this.snapshotDao.reupdateInvestorIncomeWithoutRewardIncome(productOid, incomeDate);
		}
		log.info("结束再次更新投资者收益明细的引用holdIncomeOid，结果为{}",result>0);
		return result;
	}
	
	/**
	 * 根据持有人手册表分发收益
	 */
	@Transactional(value=Transactional.TxType.REQUIRES_NEW)
	public int distributeHoldInterest(String productOid,Date incomeDate,BigDecimal netUnitShare,String productType, String incomeDealType){
		int result = 0;
		log.info("incomeDate={},productOid={},更新合仓收益", incomeDate, productOid);
		if (this.snapshotDao.hasdistributedHoldInterest(productOid, incomeDate)==0) {
			/** 收益结转 */
			if (Product.PRODUCT_incomeDealType_reinvest.equals(incomeDealType)) {
				log.info("incomeDate={},productOid={},更新合仓收益,收益结转start", incomeDate, productOid);
				result= this.snapshotDao.distributeHoldInterestAndPrincipal(productOid, incomeDate, netUnitShare);
				log.info("incomeDate={},productOid={},更新合仓收益,收益结转{}", incomeDate, productOid, result > 0 ? "success" : "fail");
			}
			
			/** 现金分红 */
			if (Product.PRODUCT_incomeDealType_cash.equals(incomeDealType)) {
				log.info("incomeDate={},productOid={},更新合仓收益,现金分红start", incomeDate, productOid);
				result= this.snapshotDao.distributeHoldInterest(productOid, incomeDate);
				log.info("incomeDate={},productOid={},更新合仓收益,现金分红{}", incomeDate, productOid, result > 0 ? "success" : "fail");
			}
		}
		return result;
	}
	
	/**
	 * 重新同步在派发收益日期之后已经拍过快照的全部数据
	 */
	//Commented the the Transactional, avoid repeatly operation, 2018.03.08
//	@Transactional(value=Transactional.TxType.REQUIRES_NEW)
	public void reupdateAfterIncomeDateAllSnapshot(String productOid, Date incomeDate,
			BigDecimal netUnitShare){
		List<Date> snapshotDates = this.snapshotDao.getAfterIncomeDate(productOid, incomeDate);
		Product product = productService.findByOid(productOid);
		for (Date date : snapshotDates) {
			if (Product.PRODUCT_incomeDealType_reinvest.equals(product.getIncomeDealType())) {
				log.info("incomeDate={},productOid={},afterSnapshotDate={}更新后续收益快照,收益结转start", incomeDate, productOid, date);
				int result= this.snapshotServiceRequiresNew.reupdateAfterIncomeDateSnapshot(productOid, incomeDate, date);
				log.info("incomeDate={},productOid={},afterSnapshotDate={}更新后续收益快照,收益结转{}", incomeDate, productOid, result > 0 ? "success" : "fail", date);
			}
			
			if (Product.PRODUCT_incomeDealType_cash.equals(product.getIncomeDealType())) {
				log.info("incomeDate={},productOid={},afterSnapshotDate={}更新后续收益快照,现金分红start", incomeDate, productOid, date);
				int result=this.snapshotServiceRequiresNew.reupdateAfterIncomeDateCashSnapshot(productOid, incomeDate, date);
				log.info("incomeDate={},productOid={},afterSnapshotDate={}更新后续收益快照,现金分红{}", incomeDate, productOid, result > 0 ? "success" : "fail", date);
			}
			
			this.practiceService.processOneItem(product, date);
		}
	}
	
	/**
	 * 收益更新到投资统计信息
	 */
	@Transactional(value = TxType.REQUIRES_NEW)
	public int distributeInterestToInvestorStatistic(String productOid,Date incomeDate,String ptype, String incomeDealType) {
		int result = 0;
		if (Product.PRODUCT_incomeDealType_reinvest.equals(incomeDealType)) {
			log.info("incomeDate={},productOid={}, 更新到投资统计信息,收益结转 start", incomeDate, productOid);
			result = this.snapshotDao.distributeInterestToInvestorStatistic(productOid, incomeDate, ptype);
			log.info("incomeDate={},productOid={}, 更新到投资统计信息,收益结转 {}", incomeDate, productOid, result > 0 ? "success" : "fail");
			
			result = snapshotDao.updateWishplanAmountByProductOid(productOid, incomeDate);
			log.info("incomeDate={},productOid={}, 更新心愿投资统计信息,收益结转 {}", incomeDate, productOid, result > 0 ? "success" : "fail");
			snapshotDao.wishplanIncomeVolumeByOrder(productOid);
//			result = snapshotDao.wishplanIncomeVolumeByProductOid(productOid, incomeDate);
			log.info("incomeDate={},productOid={}, 更新心愿计划收益,收益结转 {}", incomeDate, productOid, result > 0 ? "success" : "fail");
		}
		
		if (Product.PRODUCT_incomeDealType_cash.equals(incomeDealType)) {
			log.info("incomeDate={},productOid={}, 更新到投资统计信息,现金分红 start", incomeDate, productOid);
			result = this.snapshotDao.distributeInterestCashToInvestorStatistic(productOid, incomeDate);
			log.info("incomeDate={},productOid={}, 更新到投资统计信息,现金分红 {}", incomeDate, productOid, result > 0 ? "success" : "fail");
			
			result = snapshotDao.updateWishplanAmountByProductOid(productOid, incomeDate);
			log.info("incomeDate={},productOid={}, 更新心愿投资统计信息,现金分红 {}", incomeDate, productOid, result > 0 ? "success" : "fail");
			
			snapshotDao.wishplanIncomeByOrder(productOid);
//			result = snapshotDao.wishplanIncomeByProductOid(productOid, incomeDate);
			log.info("incomeDate={},productOid={}, 更新心愿计划收益,现金分红 {}", incomeDate, productOid, result > 0 ? "success" : "fail");
		}
		
		return result;
	}
	
	
	/**
	 * 心愿计划收益更新到投资统计信息
	 */
	@Transactional(value = TxType.REQUIRES_NEW)
	public int wishplanInterestToInvestorStatistic(String productOid,Date incomeDate,String ptype, String incomeDealType) {
		int result = 0;
		if (Product.PRODUCT_incomeDealType_reinvest.equals(incomeDealType)) {
			log.info("incomeDate={},productOid={}, 心愿更新到投资统计信息,收益结转 start", incomeDate, productOid);
			result = this.snapshotDao.distributeInterestToInvestorStatistic(productOid, incomeDate, ptype);
			log.info("incomeDate={},productOid={}, 心愿更新到投资统计信息,收益结转 {}", incomeDate, productOid, result > 0 ? "success" : "fail");
		}
		
		if (Product.PRODUCT_incomeDealType_cash.equals(incomeDealType)) {
			log.info("incomeDate={},productOid={}, 心愿更新到投资统计信息,现金分红 start", incomeDate, productOid);
			result = this.snapshotDao.distributeInterestCashToInvestorStatistic(productOid, incomeDate);
			log.info("incomeDate={},productOid={}, 心愿更新到投资统计信息,现金分红 {}", incomeDate, productOid, result > 0 ? "success" : "fail");
		}
		
		return result;
	}
	
	/**
	 * 试算有奖励收益
	 * @param productOid
	 * @param incomeDate
	 * @return
	 */
	@Transactional(value=Transactional.TxType.REQUIRES_NEW)
	public int practiceDistributeInterestWithRewardIncome(String productOid,Date incomeDate){
		return this.snapshotDao.practiceDistributeInterestWithRewardIncome(productOid, incomeDate);
	}
	
	/**
	 * 含奖励收益--试算汇总
	 */
	@Transactional(value=Transactional.TxType.REQUIRES_NEW)
	public int practiceSummary(String productOid, Date incomeDate) {
		return this.snapshotDao.practiceSummary(productOid, incomeDate);
	}
	
	/**
	 * 试算无奖励收益
	 * @param productOid
	 * @param incomeDate
	 * @return
	 */
	@Transactional(value=Transactional.TxType.REQUIRES_NEW)
	public int practiceDistributeInterestWithoutRewardIncome(String productOid,Date incomeDate){
		return this.snapshotDao.practiceDistributeInterestWithoutRewardIncome(productOid, incomeDate);
	}
	
	/**
	 * 获取已分派收益信息
	 * @param productOid
	 * @param incomeDate
	 * @return
	 */
	@Transactional(value=Transactional.TxType.REQUIRES_NEW)
	public Object[] getDistributedInterestInfo(String productOid,Date incomeDate){
		return this.snapshotDao.getDistributedInterestInfo(productOid, incomeDate).get(0);
	}
	
	/**
	 * 将快照表中的计算完收益的数据以投资者维度插入临时表中
	 */
	@Transactional(value=Transactional.TxType.REQUIRES_NEW)
	public int insertIntoSnapshotTmp(String productOid, Date incomeDate){
		log.info("incomeDate={},productOid={},计算投资者合仓收益 start", incomeDate, productOid);
		//Comment out the truncate, 2018-03-10
//		this.snapshotDao.truncateSnapshotTmp();
		int result= this.snapshotDao.insertIntoSnapshotTmp(productOid, incomeDate);
		log.info("incomeDate={},productOid={},计算投资者合仓收益{}", incomeDate, productOid, result > 0 ? "success" : "fail");
		return result;
	}
	
	
	
	
}
