package com.guohuai.cache.service;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.guohuai.ams.label.LabelEnum;
import com.guohuai.ams.label.LabelService;
import com.guohuai.ams.product.Product;
import com.guohuai.ams.switchcraft.SwitchService;
import com.guohuai.cache.CacheKeyConstants;
import com.guohuai.cache.entity.InvestorBaseAccountCacheEntity;
import com.guohuai.cache.entity.ProductCacheEntity;
import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.HashRedisUtil;
import com.guohuai.mmp.investor.bankorder.FeeRep;
import com.guohuai.mmp.investor.bankorder.InvestorBankOrderEntity;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountDao;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountEntity;
import com.guohuai.mmp.investor.baseaccount.statistics.InvestorStatisticsService;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderEntity;

@Service
public class CacheInvestorService {
	Logger logger = LoggerFactory.getLogger(CacheInvestorService.class);
	
	@Autowired
	private RedisTemplate<String, String> redis;
	@Autowired
	private CacheProductService cacheProductService;
	@Autowired
	private SwitchService switchService;
	@Autowired
	private LabelService labelService;
	@Autowired
	private InvestorBaseAccountDao investorBaseAccountDao;
	@Autowired
	private InvestorStatisticsService investorStatisticsService;

	
	public InvestorBaseAccountCacheEntity getInvestorByInvestorOid(String investorOid) {
		InvestorBaseAccountCacheEntity cache = HashRedisUtil.hgetall(redis, CacheKeyConstants.getInvestorHKey(investorOid), InvestorBaseAccountCacheEntity.class);
		return cache;
	}

	public void isNewbie(InvestorTradeOrderEntity orderEntity) {
		
		ProductCacheEntity cache = this.cacheProductService.getProductCacheEntityById(orderEntity.getProduct().getOid());
		if (Product.TYPE_Producttype_01.equals(cache.getType()) && labelService.isProductLabelHasAppointLabel(cache.getProductLabel(), LabelEnum.newbie.toString())) {
			int i = this.investorBaseAccountDao.updateFreshman(orderEntity.getInvestorBaseAccount().getOid());
			if (i < 1) {
				// error.define[30077]=新手产品只提供初次购买(CODE:30077)
				throw new AMPException(30077);
			}
		}
	}
	
	
	
	/**
	 * 手续费/月提现次数 
	 */
	public FeeRep getFee(InvestorBankOrderEntity bankOrder) {
		FeeRep rep = new FeeRep();
		// 月提现次数
//		long withdrawTimes = this.redisExecuteLogExtService.hincrByLong(CacheKeyConstants.getInvestorHKey(bankOrder.getInvestorBaseAccount().getOid()),
//				"monthWithdrawCount", 1);
		int freeWithdrawTimes = switchService.getWithdrawTimes();
		BigDecimal fee = switchService.getFee(bankOrder.getOrderTime());
		
		/** 增加提现次数 */
		investorStatisticsService.increaseMonthWithdrawCount(bankOrder);
		boolean isFree = investorStatisticsService.isFree(bankOrder.getInvestorBaseAccount(), freeWithdrawTimes);
		
		
		if (!isFree) {
			if (bankOrder.getOrderAmount().compareTo(fee) <= 0) {
				// error.define[30078]=订单金额不足以抵扣提现手续费(CODE:30078)
				throw new AMPException(30078);
			}
			rep.setPayer(InvestorBankOrderEntity.BANKORDER_feePayer_user);
			
		} else {
			rep.setPayer(InvestorBankOrderEntity.BANKORDER_feePayer_platform);
		}
		rep.setFee(fee);
		return rep;
	}

	public void isInvestorBaseAccountNormal(String investorOid) {
		InvestorBaseAccountCacheEntity investorCache = this.getInvestorByInvestorOid(investorOid);
		if (InvestorBaseAccountEntity.BASEACCOUNT_status_forbidden.equals(investorCache.getStatus())) {
			throw new AMPException("账户已冻结,请联系客服");
		}
		
	}


	
}
