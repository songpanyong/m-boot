package com.guohuai.mmp.publisher.investor;

import java.math.BigDecimal;
import java.sql.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guohuai.ams.duration.fact.income.IncomeAllocate;
import com.guohuai.ams.duration.fact.income.IncomeAllocateDao;
import com.guohuai.ams.product.Product;
import com.guohuai.ams.product.ProductService;
import com.guohuai.ams.product.reward.ProductIncomeRewardCacheService;
import com.guohuai.mmp.jiajiacai.caculate.JJCUtility;
import com.guohuai.mmp.platform.baseaccount.statistics.PlatformStatisticsService;
import com.guohuai.mmp.publisher.baseaccount.statistics.PublisherStatisticsService;
import com.guohuai.mmp.publisher.holdapart.snapshot.SnapshotService;
import com.guohuai.mmp.publisher.investor.interest.result.InterestResultEntity;
import com.guohuai.mmp.publisher.investor.interest.result.InterestResultService;
import com.guohuai.mmp.sys.SysConstant;

import lombok.extern.slf4j.Slf4j;
/**
 * 
 * @author jeffrey
 *派息优化处理
 */
@Slf4j
@Service
public class InterestDistributionService {
	
	@Autowired
	SnapshotService snapshotService;
	@Autowired
	private IncomeAllocateDao incomeAllocateDao;
	@Autowired
	private ProductService productService;
	@Autowired
	private ProductIncomeRewardCacheService productIncomeRewardCacheService;
	@Autowired
	private InterestResultService interestResultService;
	@Autowired
	private PublisherStatisticsService publisherStatisticsService;
	@Autowired
	private PlatformStatisticsService platformStatisticsService;
	
	
	
	/**
	 * 派息
	 * 
	 * @param incomeOid
	 *            收益分派主键ID
	 * @param productOid
	 *            产品主键ID
	 */
	public void distributeInterestByProduct(String incomeOid, String productOid) {
		long beginTime = System.currentTimeMillis();
		IncomeAllocate incomeAllocate = incomeAllocateDao.findOne(incomeOid);
		Product product = productService.findByOid(productOid);
		Date incomeDate = incomeAllocate.getBaseDate();
		BigDecimal fpRate = incomeAllocate.getRatio();
		BigDecimal baseIncomeRatio = InterestFormula.caclDayInterest(fpRate,
				Integer.parseInt(product.getIncomeCalcBasis()));// 复利，基础万份收益
		BigDecimal netUnitShare = product.getNetUnitShare();
		String incomeDealType = product.getIncomeDealType();
		
		log.info("begin to handle distributeInterestByProduct:incomeOid={},productOid={},incomeDate={}", incomeOid, productOid, incomeDate);
		
		log.info("incomeDate={},productOid={},检查可计息规模 start", incomeDate, product.getOid());
		if(!this.checkPracticeAmount(product, incomeDate, incomeAllocate)){//如果试算规模为0，不进行收益发放
			log.info("incomeDate={},productOid={},计息规模为0，计息结束", incomeDate, product.getOid());
			return;
		}
		log.info("incomeDate={},productOid={},检查可计息规模 success", incomeDate, product.getOid());

		/** 1.在快照表中更新要发放的收益 */
		this.caclInterest(product, incomeDate, incomeAllocate, baseIncomeRatio,fpRate);

		/** 2.订单表分发收益 */ 
		this.snapshotService.distributeOrderInterest(productOid, incomeDate, netUnitShare, incomeDealType);
		
		/** 更新合仓表收益 */
		this.snapshotService.distributeHoldInterest(productOid, incomeDate, netUnitShare, product.getType().getOid(), incomeDealType);
		
		/** 更新投资者统计表 */
		this.snapshotService.distributeInterestToInvestorStatistic(productOid, incomeDate, product.getType().getOid(), incomeDealType);
		
		/** 更新后续快照 */
		this.snapshotService.reupdateAfterIncomeDateAllSnapshot(productOid, incomeDate, netUnitShare);
		
		/** 记录分仓收益 */
		this.snapshotService.distributeInterestToInvestorIncome(productOid, incomeDate, incomeOid);
		
		if (this.productIncomeRewardCacheService.hasRewardIncome(productOid)) {
			/** 记录阶梯收益 */
			this.snapshotService.distributeInterestToInvestorLevelIncome(productOid, incomeDate, netUnitShare);
		}
		
		/** 记录合仓收益 */
		this.snapshotService.distributeInterestToInvestorHoldIncome(productOid, incomeDate, incomeOid, incomeDealType);
		
		/** 现金分红 */
		if (incomeDealType.equals(Product.PRODUCT_incomeDealType_cash)) {
			snapshotService.dividend(product.getOid(), incomeDate, product.getPublisherBaseAccount().getOid());
		}
		
		if (this.productIncomeRewardCacheService.hasRewardIncome(productOid)) {// 有定义奖励收益
			/**  再次更新投资者收益明细的引用holdIncomeOid，levelIncomeOid */
			this.snapshotService.reupdateInvestorIncomeWithRewardIncome(productOid, incomeDate);
		} else {
			/** 再次更新投资者收益明细的引用holdIncomeOid */
			this.snapshotService.reupdateInvestorIncomeWithoutRewardIncome(productOid, incomeDate);
		}

		/** 已发放收益数据 */
		this.updateDistributedInterest(product, incomeDate, incomeAllocate);

		log.info("发放产品oid={},执行总时间：{}秒", productOid, (System.currentTimeMillis() - beginTime) / 1000);
	}

	/**
	 * 基于快照表算出收益
	 */
	private void caclInterest(Product product, Date incomeDate, IncomeAllocate incomeAllocate,
			BigDecimal baseIncomeRatio,BigDecimal fpRate) {
		
		if (Product.TYPE_Producttype_01.equals(product.getType().getOid())
				&& IncomeAllocate.ALLOCATE_INCOME_TYPE_durationIncome.equals(incomeAllocate.getAllocateIncomeType())) {// 定期
			if (this.productIncomeRewardCacheService.hasRewardIncome(product.getOid())) {// 有定义奖励收益
				/** 单利有奖励收益 */
				this.snapshotService.distributeInterestSingleWithRewardIncome(product.getOid(), fpRate,
						incomeDate, product.getDurationPeriodDays(), Integer.parseInt(product.getIncomeCalcBasis()));
			} else {
				/** 单利无奖励收益 */
				this.snapshotService.distributeInterestSingleWithoutRewardIncome(product.getOid(), fpRate,
						incomeDate, product.getDurationPeriodDays(), Integer.parseInt(product.getIncomeCalcBasis()));
			}
		} else {
			if (this.productIncomeRewardCacheService.hasRewardIncome(product.getOid())) {// 有定义奖励收益
				/** 复利有奖励收益 */
				this.snapshotService.remainderIncome(product.getOid(), baseIncomeRatio, incomeDate);
			} else {
				/** 复利无奖励收益 */
				this.snapshotService.remainderIncomeWithoutRewardIncome(product.getOid(), baseIncomeRatio, incomeDate);
			}
		}
		/** 计算投资人合仓收益 */
		this.snapshotService.insertIntoSnapshotTmp(product.getOid(), incomeDate);
		
	}
	
	/**
	 * 试算规模是否大于0
	 */
	private boolean checkPracticeAmount(Product product, Date incomeDate, IncomeAllocate incomeAllocate){
		BigDecimal fpBaseAmount = incomeAllocate.getLeftAllocateBaseIncome();
		BigDecimal fpRewardAmount = incomeAllocate.getLeftAllocateRewardIncome();
		BigDecimal fpAmount = fpBaseAmount.add(fpRewardAmount);
		
		BigDecimal totalVolume = incomeAllocate.getCapital();
		//待计息份额 = 本金份额 + 最后计息基数
		if (null == totalVolume || SysConstant.BIGDECIMAL_defaultValue.compareTo(totalVolume) == 0) {
			//收益分配日志
			InterestResultEntity result = interestResultService.createEntity(product, incomeAllocate, incomeDate);
			result.setLeftAllocateIncome(fpAmount);
			//Truncated
			result.setLeftAllocateIncome(JJCUtility.bigKeep4Decimal(result.getLeftAllocateIncome()));
			result.setStatus(InterestResultEntity.RESULT_status_ALLOCATED);
			result.setAnno("待计息份额为零");
			this.interestResultService.saveEntity(result);
			interestResultService.send(result);
			return false;
		}
		return true;
	}
	
	


	/**
	 * 更新已发放收益数据
	 * 
	 * @param product
	 * @param incomeDate
	 * @param incomeAllocate
	 */
	private void updateDistributedInterest(Product product, Date incomeDate, IncomeAllocate incomeAllocate) {
		BigDecimal fpBaseAmount = incomeAllocate.getLeftAllocateBaseIncome();
		BigDecimal fpRewardAmount = incomeAllocate.getLeftAllocateRewardIncome();
		BigDecimal fpCouponAmount = incomeAllocate.getLeftAllocateCouponIncome();
		BigDecimal fpAmount = fpBaseAmount.add(fpRewardAmount).add(fpCouponAmount);
		InterestResultEntity result = interestResultService.createEntity(product, incomeAllocate, incomeDate);
		Object[] distributedInterest = this.snapshotService.getDistributedInterestInfo(product.getOid(), incomeDate);
		result.setSuccessAllocateRewardIncome(new BigDecimal(distributedInterest[1].toString()));
		result.setSuccessAllocateBaseIncome(new BigDecimal(distributedInterest[2].toString()));
		result.setSuccessAllocateCouponIncome(new BigDecimal(distributedInterest[4].toString()));
		result.setSuccessAllocateIncome(result.getSuccessAllocateBaseIncome().add(result.getSuccessAllocateRewardIncome()).add(result.getSuccessAllocateCouponIncome()));
		result.setSuccessAllocateInvestors(Integer.parseInt(distributedInterest[3].toString()));
		result.setLeftAllocateIncome(fpAmount.subtract(result.getSuccessAllocateIncome()));
		//Truncated
		result.setLeftAllocateIncome(JJCUtility.bigKeep4Decimal(result.getLeftAllocateIncome()));
				
		result.setLeftAllocateBaseIncome(fpBaseAmount.subtract(result.getSuccessAllocateBaseIncome()));
		result.setLeftAllocateRewardIncome(fpRewardAmount.subtract(result.getSuccessAllocateRewardIncome()));
		result.setLeftAllocateCouponIncome(fpCouponAmount.subtract(result.getSuccessAllocateCouponIncome()));
		if (result.getSuccessAllocateInvestors() > 0) {
			result.setStatus(InterestResultEntity.RESULT_status_ALLOCATED);
		} else {
			result.setStatus(InterestResultEntity.RESULT_status_ALLOCATEFAIL);
		}
		/** 发送计息结果 */
		this.interestResultService.saveEntity(result);
		interestResultService.send(result);

		/** 发行人统计 */
//		publisherStatisticsService.increaseTotalInterestAmount(product.getPublisherBaseAccount(),
//				result.getSuccessAllocateIncome());

		/** 平台统计 */
//		this.platformStatisticsService.updateStatistics4TotalInterestAmount(result.getSuccessAllocateIncome());
	}

	

}
