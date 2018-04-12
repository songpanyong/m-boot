package com.guohuai.mmp.publisher.hold;


import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guohuai.cache.service.CacheHoldService;
import com.guohuai.component.util.DecimalUtil;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderEntity;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderService;

/**
 * 持有人手册_内部事务类.
 * 
 * @author Jeffrey Wong
 *
 */
@Service
@Transactional
public class PublisherHoldServiceNew {

	private static final Logger logger = LoggerFactory.getLogger(PublisherHoldServiceNew.class);

	@Autowired
	private PublisherHoldDao publisherHoldDao;
	@Autowired
	private InvestorTradeOrderService investorTradeOrderService;

	/**
	 * 根据分仓更新合仓可赎回份额
	 */
	@Transactional(TxType.REQUIRES_NEW)
	public void unlockRedeemItem(InvestorTradeOrderEntity orderEntity) {
		
		int i = 0;
		if (InvestorTradeOrderEntity.TRADEORDER_orderType_expGoldInvest.equals(orderEntity.getOrderType())) {
			logger.info("holdOid={}, totalIncome{}",orderEntity.getPublisherHold().getOid(),
					orderEntity.getTotalIncome().setScale(DecimalUtil.scale, DecimalUtil.roundMode));
			i = this.publisherHoldDao.unlockRedeem(orderEntity.getPublisherHold().getOid(),
					orderEntity.getTotalIncome().setScale(DecimalUtil.scale, DecimalUtil.roundMode));
			
		} else {
			i = this.publisherHoldDao.unlockRedeem(orderEntity.getPublisherHold().getOid(),
					orderEntity.getHoldVolume().setScale(DecimalUtil.scale, DecimalUtil.roundMode));
		}
		
		if (i < 1) {
			logger.info("========解锁可赎回根据分仓更新合仓可赎回份额" + orderEntity.getOid() + "处理失败");
			return;
		}
		this.investorTradeOrderService.unlockRedeem(orderEntity.getOid());

	}
	
	/**
	 * 根据分仓更新合仓可计息份额
	 */
	@Transactional(TxType.REQUIRES_NEW)
	public void unlockAccrualItem(InvestorTradeOrderEntity entity) {
		int i = this.publisherHoldDao.unlockAccrual(entity.getPublisherHold().getOid(), entity.getHoldVolume());
		if (i < 1) {
			logger.info("========根据分仓更新合仓可计息份额" + entity.getOid() + "处理失败");
			return;
		}
		this.investorTradeOrderService.unlockAccrual(entity.getOid());
		
		
	}
}
