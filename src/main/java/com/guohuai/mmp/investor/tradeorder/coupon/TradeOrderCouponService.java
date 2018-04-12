package com.guohuai.mmp.investor.tradeorder.coupon;

import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.guohuai.basic.common.StringUtil;
import com.guohuai.basic.component.exception.GHException;
import com.guohuai.component.util.DateUtil;
import com.guohuai.mmp.investor.bankorder.InvestorBankOrderEntity;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderEntity;

import antlr.StringUtils;


@Service
@Transactional
public class TradeOrderCouponService {
	
	
	@Autowired
	private TradeOrderCouponDao tradeOrderCouponDao;

	
	public TradeOrderCouponEntity findByInvestorTradeOrder(InvestorTradeOrderEntity entity){
		return this.tradeOrderCouponDao.findByInvestorTradeOrder(entity);
	}
	
	public TradeOrderCouponEntity findByInvestorBankOrder(InvestorBankOrderEntity entity){
		return this.tradeOrderCouponDao.findByInvestorBankOrder(entity);
	}
	
	public TradeOrderCouponEntity findByBankOrder(String bankOrderOid){
		TradeOrderCouponEntity tradeOrderCoupon = this.tradeOrderCouponDao.findByBankOrder(bankOrderOid);
		if (null == tradeOrderCoupon) {
			// error.define[80035]=该订单未使用红包卡券!(CODE:80035)
			throw GHException.getException(80035);
		}
		
		if (StringUtil.isEmpty(tradeOrderCoupon.getCoupons())) {
			// error.define[80036]=红包卡券订单记录为空!(CODE:80036)
			throw GHException.getException(80036);
		}
		return tradeOrderCoupon;
	}
	
	public TradeOrderCouponEntity findByCoupons(String coupons){
		return this.tradeOrderCouponDao.findByCoupons(coupons);
	}
	
	/**
	 * 新增
	 * @param entity
	 * @return
	 */
	public TradeOrderCouponEntity saveEntity(TradeOrderCouponEntity entity){
		entity.setCreateTime(DateUtil.getSqlCurrentDate());
		return this.updateEntity(entity);
	}
	
	/**
	 * 修改
	 * @param entity
	 * @return
	 */
	private TradeOrderCouponEntity updateEntity(TradeOrderCouponEntity entity) {
		entity.setUpdateTime(DateUtil.getSqlCurrentDate());
		return this.tradeOrderCouponDao.save(entity);
	}
	
//	/** 卡券类型- 抵用券*/
//	public static final String TRADEORDERCOUPON_type_coupon = "coupon";
//	/** 卡券类型- 加息券*/
//	public static final String TRADEORDERCOUPON_type_rateCoupon = "rateCoupon";
//	/** 卡券类型- 体验金*/
//	public static final String TRADEORDERCOUPON_type_tasteCoupon = "tasteCoupon";
	
	public String couponTypeEn2Ch(String couponType) {
		if (TradeOrderCouponEntity.TRADEORDERCOUPON_type_coupon.equals(couponType)) {
			return "抵用券";
		}
		
		if (TradeOrderCouponEntity.TRADEORDERCOUPON_type_rateCoupon.equals(couponType)) {
			return "加息券";
		}
		
		if (TradeOrderCouponEntity.TRADEORDERCOUPON_type_tasteCoupon.equals(couponType)) {
			return "体验金";
		}
		
		if (TradeOrderCouponEntity.TRADEORDERCOUPON_type_redEnvelope.equals(couponType)) {
			return "红包";
		}
		
		return couponType;
	}
	
	public Page<TradeOrderCouponEntity> findPage(Specification<TradeOrderCouponEntity> spec, Pageable pageable){
		return this.tradeOrderCouponDao.findAll(spec, pageable);
	}

	/**
	 * 统计卡券
	 * @return
	 */
	public List<Object[]> getCouponStatistics() {
		return this.tradeOrderCouponDao.getCouponStatistics();
	}
	
	/**
	 * 卡券-使用红包
	 * @param bankOrder
	 * @param couponId
	 * @return
	 */
	public TradeOrderCouponEntity saveBankOrderCoupon(InvestorBankOrderEntity bankOrder, String couponId) {
		TradeOrderCouponEntity coupon = new TradeOrderCouponEntity();
		coupon.setInvestorBaseAccount(bankOrder.getInvestorBaseAccount());
		coupon.setInvestorBankOrder(bankOrder);
		coupon.setCoupons(couponId);
		coupon.setCouponAmount(bankOrder.getOrderAmount());
		coupon.setCouponType(TradeOrderCouponEntity.TRADEORDERCOUPON_type_redEnvelope);
		return this.saveEntity(coupon);
	}
	
}
