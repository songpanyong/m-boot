package com.guohuai.mmp.investor.tradeorder;

import java.sql.Date;
import java.sql.Timestamp;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guohuai.ams.product.Product;
import com.guohuai.calendar.TradeCalendarService;
import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.DateUtil;
import com.guohuai.mmp.investor.tradeorder.coupon.TradeOrderCouponEntity;
import com.guohuai.mmp.investor.tradeorder.coupon.TradeOrderCouponService;
import com.guohuai.mmp.platform.tulip.TulipService;

@Service
@Transactional
public class OrderDateService {
	
	@Autowired
	private TulipService tulipService;
	@Autowired
	private TradeCalendarService tradeCalendarService;
	@Autowired
	private TradeOrderCouponService tradeOrderCouponService;
	/**
	 * 本金计息截止日期
	 */
	public Date getCorpusAccrualEndDate(InvestorTradeOrderEntity orderEntity) {
		Date corpusAccrualEndDate = null;
		if (InvestorTradeOrderEntity.TRADEORDER_orderType_expGoldInvest.equals(orderEntity.getOrderType())) {
			boolean isT = DateUtil.isT(orderEntity.getOrderTime());
			Date baseDate = DateUtil.getSqlDate(orderEntity.getOrderTime().getTime());

			TradeOrderCouponEntity coupon = tradeOrderCouponService.findByInvestorTradeOrder(orderEntity);
			
			if (!isT) {
				corpusAccrualEndDate = DateUtil.addSQLDays(baseDate, 
						this.tulipService.getCouponDetail(coupon.getCoupons()).getValidPeriod() + 1);
			} else {
				corpusAccrualEndDate = DateUtil.addSQLDays(baseDate, 
						this.tulipService.getCouponDetail(coupon.getCoupons()).getValidPeriod());
			}
		}
		return corpusAccrualEndDate;
	}
	
	
	
	/**
	 * 获取体验金、冲销单开始起息日
	 */
	public Date getDirectlyBeginAccuralDate(InvestorTradeOrderEntity orderEntity) {
		boolean isT = DateUtil.isT(orderEntity.getOrderTime());
		Date baseDate = DateUtil.getSqlDate(orderEntity.getOrderTime().getTime());
		
		Date beginAccuralDate = baseDate;
		if (!isT) {
			beginAccuralDate = DateUtil.addSQLDays(baseDate, 1);
		}
		return beginAccuralDate;
	}
	
	/**
	 *  产品详情显示调用
	 */
	public Date getBeginAccuralDate(Product product) {
		return this.getNormalBeginAccuralDate(product, new Timestamp(System.currentTimeMillis()));
	}
	
	/**
	 * 
	 */
	public Date getBeginAccuralDate(InvestorTradeOrderEntity orderEntity) {
		if (0 == orderEntity.getProduct().getInterestsFirstDays()) {
			return this.getDirectlyBeginAccuralDate(orderEntity);
		} else {
			return this.getNormalBeginAccuralDate(orderEntity.getProduct(), orderEntity.getOrderTime());
		}
	}
	
	public Date getNormalBeginAccuralDate(Product product, Timestamp orderTime) {
		Date beginAccuralDate = null;
		
		boolean isT = DateUtil.isT(orderTime);
		int interestsFirstDays = product.getInterestsFirstDays();
		if (Product.Product_dateType_T.equals(product.getInvestDateType())) {
			boolean isTrade = this.tradeCalendarService.isTrade(new java.sql.Date(orderTime.getTime()));
			if (isTrade) {
				if (!isT) {
					interestsFirstDays = interestsFirstDays + 1;
				}
			} else {
				interestsFirstDays = interestsFirstDays + 1;
			}
			beginAccuralDate = tradeCalendarService
					.nextTrade(new Date(orderTime.getTime()), interestsFirstDays);
		} else {
			if (!isT) {
				interestsFirstDays = interestsFirstDays + 1;
			}
			beginAccuralDate = DateUtil.addSQLDays(new Date(orderTime.getTime()), interestsFirstDays);
		}
		return beginAccuralDate;
	}
	
	
	public Date getConfirmDate(InvestorTradeOrderEntity orderEntity) {
		return this.getConfirmDate(orderEntity.getProduct(), orderEntity.getOrderTime());
	}
	
	
	public Date getConfirmDate(Product product, Timestamp orderTime) {
		Date confirmDate = null;
		
		boolean isT = DateUtil.isT(orderTime);
		
		int purchaseConfirmDays = product.getPurchaseConfirmDays();
		
		if (Product.Product_dateType_T.equals(product.getInvestDateType())) {
			boolean isTrade = this.tradeCalendarService.isTrade(new java.sql.Date(orderTime.getTime()));
			if (isTrade) {
				if (!isT) {
					purchaseConfirmDays = purchaseConfirmDays + 1;
				}
			} else {
				purchaseConfirmDays = purchaseConfirmDays + 1;
			}
			
			confirmDate = tradeCalendarService.nextTrade(new java.sql.Date(orderTime.getTime()), purchaseConfirmDays);
		} else {
			if (!isT) {
				purchaseConfirmDays = purchaseConfirmDays + 1;
			}
			confirmDate = DateUtil.addSQLDays(new java.sql.Date(orderTime.getTime()), purchaseConfirmDays);
		}
		return confirmDate;
	}
	
	
	public Date getBeginRedeemDate(InvestorTradeOrderEntity orderEntity) {
		if (InvestorTradeOrderEntity.TRADEORDER_orderType_invest.equals(orderEntity.getOrderType())) {
			return this.getNormalBeginRedeemDate(orderEntity);
		}
		
		if (InvestorTradeOrderEntity.TRADEORDER_orderType_expGoldInvest.equals(orderEntity.getOrderType())) {
			return this.getGoldBeginRedeemDate(orderEntity);
		}
		
		if (InvestorTradeOrderEntity.TRADEORDER_orderType_writeOff.equals(orderEntity.getOrderType())) {
			return this.getDirectlyBeginRedeemDate(orderEntity);
		}
		if (InvestorTradeOrderEntity.TRADEORDER_orderType_reInvest.equals(orderEntity.getOrderType())) {
			return this.getDirectlyBeginRedeemDate(orderEntity);
		}
		
		
		throw new AMPException("订单类型不存在，获取开始赎回日异常");
	}
	/**
	 * 定期，开始赎回日直接为空
	 * 活期，在申购确认日的基础上进行累加
	 */
	private Date getNormalBeginRedeemDate(InvestorTradeOrderEntity orderEntity) {
		Date beginRedeemDate = null;
		/** 定期不存在赎回 */
		if (Product.TYPE_Producttype_01.equals(orderEntity.getProduct().getType().getOid())) {
			return beginRedeemDate;
		}
		int lockPeriodDays = orderEntity.getProduct().getLockPeriodDays();
		if (orderEntity.getProduct().getLockPeriodDays() == 0) {
			beginRedeemDate = getConfirmDate(orderEntity.getProduct(), orderEntity.getOrderTime());
		} else {
			if (Product.Product_dateType_T.equals(orderEntity.getProduct().getInvestDateType())) {
				beginRedeemDate = this.tradeCalendarService.nextTrade(getConfirmDate(orderEntity.getProduct(), orderEntity.getOrderTime()), lockPeriodDays);
			} else {
				beginRedeemDate = DateUtil.addSQLDays(getConfirmDate(orderEntity.getProduct(), orderEntity.getOrderTime()), lockPeriodDays);
			}
		}
		return beginRedeemDate;
	}
	
	/**
	 * 获取体验金开始赎回日
	 */
	public Date getGoldBeginRedeemDate(InvestorTradeOrderEntity orderEntity) {
		boolean isT = DateUtil.isT(orderEntity.getOrderTime());
		Date baseDate = DateUtil.getSqlDate(orderEntity.getOrderTime().getTime());
		
		TradeOrderCouponEntity coupon = tradeOrderCouponService.findByInvestorTradeOrder(orderEntity);

		Date beginRedeemDate = null;
		if (!isT) {
			beginRedeemDate = DateUtil.addSQLDays(baseDate, this.tulipService.getCouponDetail(coupon.getCoupons()).getValidPeriod() + 1);
		} else {
			beginRedeemDate = DateUtil.addSQLDays(baseDate, this.tulipService.getCouponDetail(coupon.getCoupons()).getValidPeriod());
		}
		return beginRedeemDate;
	}
	
	/**
	 * 获取开始赎回日
	 * 冲销单
	 */
	public Date getDirectlyBeginRedeemDate(InvestorTradeOrderEntity orderEntity) {
		boolean isT = DateUtil.isT(orderEntity.getOrderTime());
		Date baseDate = DateUtil.getSqlDate(orderEntity.getOrderTime().getTime());
		
		Date beginRedeemDate = baseDate;
		if (!isT) {
			beginRedeemDate = DateUtil.addSQLDays(baseDate, 1);
		}
		return beginRedeemDate;
	}
	
	public Date getRedeemConfirmDate(InvestorTradeOrderEntity orderEntity) {
		Date confirmDate = null;
		
		Product product = orderEntity.getProduct();
		Timestamp orderTime = orderEntity.getOrderTime();
		
		boolean isT = DateUtil.isT(orderEntity.getOrderTime());
		
		int redeemConfirmDays = product.getRedeemConfirmDays();
		
		if (Product.Product_dateType_T.equals(product.getInvestDateType())) {
			boolean isTrade = this.tradeCalendarService.isTrade(new java.sql.Date(orderTime.getTime()));
			if (isTrade) {
				if (!isT) {
					redeemConfirmDays = redeemConfirmDays + 1;
				}
			} else {
				redeemConfirmDays = redeemConfirmDays + 1;
			}
			
			confirmDate = tradeCalendarService.nextTrade(new java.sql.Date(orderTime.getTime()), redeemConfirmDays);
		} else {
			if (!isT) {
				redeemConfirmDays = redeemConfirmDays + 1;
			}
			confirmDate = DateUtil.addSQLDays(new java.sql.Date(orderTime.getTime()), redeemConfirmDays);
		}
		return confirmDate;
	}
}
