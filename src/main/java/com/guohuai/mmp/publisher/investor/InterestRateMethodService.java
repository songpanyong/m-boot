package com.guohuai.mmp.publisher.investor;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guohuai.ams.duration.fact.income.IncomeAllocate;
import com.guohuai.ams.duration.fact.income.IncomeAllocateDao;
import com.guohuai.ams.product.Product;
import com.guohuai.ams.product.ProductService;
import com.guohuai.mmp.platform.baseaccount.statistics.PlatformStatisticsService;
import com.guohuai.mmp.publisher.baseaccount.statistics.PublisherStatisticsService;
import com.guohuai.mmp.publisher.hold.PublisherHoldEntity;
import com.guohuai.mmp.publisher.hold.PublisherHoldService;
import com.guohuai.mmp.publisher.investor.interest.result.InterestResultEntity;
import com.guohuai.mmp.publisher.investor.interest.result.InterestResultService;
import com.guohuai.mmp.publisher.product.rewardincomepractice.PracticeService;
import com.guohuai.mmp.serialtask.InterestParams;
import com.guohuai.mmp.serialtask.SerialTaskEntity;
import com.guohuai.mmp.serialtask.SerialTaskReq;
import com.guohuai.mmp.serialtask.SerialTaskRequireNewService;
import com.guohuai.mmp.serialtask.SerialTaskService;
import com.guohuai.mmp.sys.SysConstant;


@Service
@Transactional
public class InterestRateMethodService {
	Logger logger = LoggerFactory.getLogger(InterestRateMethodService.class);
	@Autowired
	private PracticeService practiceService;
	@Autowired
	private PublisherHoldService holdService;
	@Autowired
	private InterestResultService interestResultService;
	@Autowired
	private InterestRateMethodServiceNew interestRateMethodServiceNew;
	@Autowired
	private IncomeAllocateDao incomeAllocateDao;
	@Autowired
	private ProductService productService;
	@Autowired
	private PublisherStatisticsService publisherStatisticsService;
	@Autowired
	private PlatformStatisticsService platformStatisticsService;
	@Autowired
	private SerialTaskService serialTaskService;
	@Autowired
	private SerialTaskRequireNewService serialTaskRequireNewService;
	
	
	
	public void interest(final String incomeAllocateOid, final String productOid) {
		InterestParams params = new InterestParams();
		params.setIncomeAllocateOid(incomeAllocateOid);
		params.setProductOid(productOid);
		
		SerialTaskReq<InterestParams> req = new SerialTaskReq<InterestParams>();
		req.setTaskCode(SerialTaskEntity.TASK_taskCode_interest);
		req.setTaskParams(params);
		serialTaskService.createSerialTask(req);
	}
	
//	public void interestDo(String incomeAllocateOid, String productOid, String taskOid) {
//		IncomeAllocate incomeAllocate = incomeAllocateDao.findOne(incomeAllocateOid);
//		Product product = productService.findByOid(productOid);
//		Date incomeDate = incomeAllocate.getBaseDate();
//		BigDecimal fpBaseAmount = incomeAllocate.getLeftAllocateBaseIncome();
//		BigDecimal fpRewardAmount = incomeAllocate.getLeftAllocateRewardIncome();
//		BigDecimal fpAmount = fpBaseAmount.add(fpRewardAmount);
//		BigDecimal fpRate = incomeAllocate.getRatio();
//		
//		//收益分配日志
//		InterestResultEntity result = interestResultService.createEntity(product, incomeAllocate, incomeDate);
//		
//		BigDecimal totalVolume = incomeAllocate.getCapital();
//		//待计息份额 = 本金份额 + 最后计息基数
//		if (null == totalVolume || SysConstant.BIGDECIMAL_defaultValue.compareTo(totalVolume) == 0) {
//			logger.info("{}===待计息从份额为零", product.getCode());
//			result.setLeftAllocateIncome(fpAmount);
//			result.setStatus(InterestResultEntity.RESULT_status_ALLOCATED);
//			result.setAnno("待计息份额为零");
//			this.interestResultService.saveEntity(result);
//			interestResultService.send(result);
//			return;
//		}
//		
//		
//		//单位净值
//		BigDecimal netUnitAmount = product.getNetUnitShare();
//		
//		String lastOid = "0";
//		int arithmometer = 1;
//		while(true) {
//			List<PublisherHoldEntity> holdList = holdService.findByProductHoldStatus(product, PublisherHoldEntity.PUBLISHER_HOLD_HOLD_STATUS_holding, lastOid, incomeDate);
//			if (holdList.isEmpty()) {
//				break;
//			}
//			for (PublisherHoldEntity hold : holdList) {
//				
//				lastOid = hold.getOid();
//				logger.info("计息holdOid={}", hold.getOid());
//				try {
//					InterestRep iRep = this.interestRateMethodServiceNew.processOneItem(product, incomeDate, hold, netUnitAmount, fpRate, incomeAllocate);
//					if (iRep.isResult()) {
//						
//						result.setSuccessAllocateIncome(result.getSuccessAllocateIncome().add(iRep.getAmount()));
//						result.setSuccessAllocateRewardIncome(result.getSuccessAllocateRewardIncome().add(iRep.getRewardAmount()));
//						result.setSuccessAllocateBaseIncome(result.getSuccessAllocateBaseIncome().add(iRep.getBaseAmount()));
//						result.setSuccessAllocateInvestors(result.getSuccessAllocateInvestors() + 1);
//						
//					} else {
//						result.setFailAllocateInvestors(result.getFailAllocateInvestors() + 1);
//					}
//					logger.info("InterestRep getBaseAmount={} getRewardAmount={} getAmount{}", iRep.getBaseAmount(), iRep.getRewardAmount(), iRep.getAmount());
//				} catch(Exception e) {
//					e.printStackTrace();
//					logger.error("计息异常", e);
//					logger.warn("interest error: {}", e.getMessage());
//				}
//				arithmometer++;
//				if (arithmometer > 100) {
//					arithmometer = 1;
//					serialTaskRequireNewService.updateTime(taskOid);
//				}
//			}
//		}
//		result.setLeftAllocateIncome(fpAmount.subtract(result.getSuccessAllocateIncome()));
//		result.setLeftAllocateBaseIncome(fpBaseAmount.subtract(result.getSuccessAllocateBaseIncome()));
//		result.setLeftAllocateRewardIncome(fpRewardAmount.subtract(result.getSuccessAllocateRewardIncome()));
//		if (0 == result.getFailAllocateInvestors().intValue()) {
//			result.setStatus(InterestResultEntity.RESULT_status_ALLOCATED);
//		} else {
//			result.setStatus(InterestResultEntity.RESULT_status_ALLOCATEFAIL);
//		}
//		
//		/** 平台统计 */
//		this.platformStatisticsService.updateStatistics4TotalInterestAmount(result.getSuccessAllocateIncome());
//		
//		/** 发行人统计 */
//		publisherStatisticsService.increaseTotalInterestAmount(product.getPublisherBaseAccount(), result.getSuccessAllocateIncome());
//		
//		/** 复利，重置后面的收益试算和计算快照  */
////		this.practiceService.practiceAgain(productOid, incomeDate);
//		
//		/** 发送计息结果 */
//		this.interestResultService.saveEntity(result);
//		
//		interestResultService.send(result);
//	}
}
