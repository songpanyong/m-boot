package com.guohuai.mmp.publisher.holdapart.closedetails;

import java.math.BigDecimal;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.guohuai.ams.product.reward.ProductIncomeReward;
import com.guohuai.ams.product.reward.ProductIncomeRewardCacheService;
import com.guohuai.component.util.DateUtil;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderEntity;




@Service
@Transactional
public class CloseDetailsService {

	
	@Autowired
	CloseDetailsDao closeDetailsDao;
	@Autowired
	ProductIncomeRewardCacheService productIncomeRewardCacheService;

	public CloseDetailsEntity createCloseDetails(InvestorTradeOrderEntity investOrder, BigDecimal volume,InvestorTradeOrderEntity redeemOrder) {
		long holdDays = DateUtil.daysBetween(DateUtil.getSqlDate(), investOrder.getBeginAccuralDate()) + 1;
		ProductIncomeReward w = this.productIncomeRewardCacheService.getRewardEntity(investOrder.getProduct().getOid(), (int)holdDays);
		
		CloseDetailsEntity entity = new CloseDetailsEntity();
		entity.setChangeVolume(volume);
		entity.setRedeemOrder(redeemOrder);
		entity.setInvestOrder(investOrder);
		entity.setChangeDirection(CloseDetailsEntity.DETAIL_changeDirection_out);
		entity.setHoldDays(holdDays);
		entity.setBasicRatio(investOrder.getProduct().getBasicRatio());
		entity.setRewardIncomeRatio(new BigDecimal(0));
		if (null != w) {
			entity.setRewardIncomeRatio(w.getRatio());
		}
		entity.setIncomeRatio(entity.getBasicRatio().add(entity.getRewardIncomeRatio()));
		entity.setProduct(investOrder.getProduct());
		entity.setInvestorBaseAccount(investOrder.getInvestorBaseAccount());
		return this.saveEntity(entity);
	}

	private CloseDetailsEntity saveEntity(CloseDetailsEntity entity) {
		return this.closeDetailsDao.save(entity);
	}
	
	public Page<CloseDetailsEntity> findAll(Specification<CloseDetailsEntity> spec, Pageable pageable){
		return this.closeDetailsDao.findAll(spec, pageable);
	}
	
}
