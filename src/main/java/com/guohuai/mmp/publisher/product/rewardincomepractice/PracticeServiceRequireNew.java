

package com.guohuai.mmp.publisher.product.rewardincomepractice;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guohuai.ams.product.Product;
import com.guohuai.ams.product.reward.ProductIncomeReward;
import com.guohuai.ams.product.reward.ProductIncomeRewardCacheService;
import com.guohuai.ams.product.reward.ProductIncomeRewardService;
//import com.guohuai.component.util.DateUtil;
//import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderEntity;
//import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderService;
import com.guohuai.mmp.publisher.hold.PublisherHoldEntity;
import com.guohuai.mmp.publisher.holdapart.snapshot.SnapshotEntity;
import com.guohuai.mmp.publisher.holdapart.snapshot.SnapshotService;


@Service
@Transactional
public class PracticeServiceRequireNew {
	
//	private static final Logger logger = LoggerFactory.getLogger(PracticeServiceRequireNew.class);
	@Autowired
	private ProductIncomeRewardService productIncomeRewardService;
	@Autowired
	private ProductIncomeRewardCacheService rewardCacheService;
	@Autowired
	private SnapshotService snapshotService;
	@Autowired
	private PracticeDao practiceDao;

	
	
	@Transactional(value = TxType.REQUIRES_NEW)
	public BigDecimal processOneHold(Product product, Date incomeDate, Map<ProductIncomeReward, PracticeEntity> map, PublisherHoldEntity hold) {
		BigDecimal totalVolume = BigDecimal.ZERO;
//		List<InvestorTradeOrderEntity> apartList = investorTradeOrderService.findInterestableApart(hold, incomeDate);
//		for (InvestorTradeOrderEntity apart : apartList) {
//			
//			SnapshotEntity snapshotEntity = snapshotService.findByOrderAndSnapShotDate(apart.getOid(), incomeDate);
//			if (null == snapshotEntity) {
//				logger.info("practice.holdApartOid={},没有计息快照",  apart.getOid());
//				continue;
//			}
//		
//			//奖励收益
//			ProductIncomeReward reward = rewardCacheService.getRewardEntity(product.getOid(), DateUtil.daysBetween(incomeDate, apart.getBeginAccuralDate()) + 1);
//			BigDecimal rewardAmount = BigDecimal.ZERO;
//			
//			totalVolume = totalVolume.add(snapshotEntity.getSnapshotVolume());
//			if (null != reward) { // 奖励金额
//				rewardAmount = productIncomeRewardService.getRewardAmount(reward, snapshotEntity.getSnapshotVolume());
//				PracticeEntity practiceEntity = map.get(reward);
//				practiceEntity.setTotalRewardIncome(rewardAmount.add(practiceEntity.getTotalRewardIncome()));
//				practiceEntity.setTotalHoldVolume(snapshotEntity.getSnapshotVolume().add(practiceEntity.getTotalHoldVolume()));
//			}
//		}
		List<SnapshotEntity> snapShotList = snapshotService.findByHoldOidAndSnapShotDate(hold.getOid(), incomeDate);
		for (SnapshotEntity snapshotEntity : snapShotList) {
			ProductIncomeReward reward = rewardCacheService.getRewardEntity(product.getOid(), snapshotEntity.getHoldDays());
			BigDecimal rewardAmount = BigDecimal.ZERO;
			
			totalVolume = totalVolume.add(snapshotEntity.getSnapshotVolume());
			if (null != reward) { // 奖励金额
				rewardAmount = productIncomeRewardService.getRewardAmount(reward, snapshotEntity.getSnapshotVolume());
				PracticeEntity practiceEntity = map.get(reward);
				practiceEntity.setTotalRewardIncome(rewardAmount.add(practiceEntity.getTotalRewardIncome()));
				practiceEntity.setTotalHoldVolume(snapshotEntity.getSnapshotVolume().add(practiceEntity.getTotalHoldVolume()));
			}
		}
		return totalVolume;
	}
	
	
	@Transactional(value = TxType.REQUIRES_NEW)
	public void delByProductAndTDate(String productOid, Date sqlDate){
		this.practiceDao.delByProductAndTDate(productOid, sqlDate);
	}
	

	
	

	
	
}
