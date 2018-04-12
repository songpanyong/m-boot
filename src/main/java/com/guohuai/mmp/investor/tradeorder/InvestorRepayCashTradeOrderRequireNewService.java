package com.guohuai.mmp.investor.tradeorder;

import java.math.BigDecimal;
import java.util.List;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guohuai.mmp.investor.tradeorder.coupon.TradeOrderCouponEntity;
import com.guohuai.mmp.investor.tradeorder.coupon.TradeOrderCouponService;
import com.guohuai.mmp.publisher.hold.PublisherHoldEntity;
import com.guohuai.mmp.publisher.hold.PublisherHoldService;

@Service
@Transactional
public class InvestorRepayCashTradeOrderRequireNewService {
	
	@Autowired
	private PublisherHoldService publisherHoldService;
	@Autowired
	private InvestorTradeOrderService investorTradeOrderService;
	@Autowired
	private InvestorInvestTradeOrderExtService investorInvestTradeOrderExtService;
	@Autowired
	private InvestorInvestTradeOrderService investorInvestTradeOrderService;
	@Autowired
	private TradeOrderCouponService tradeOrderCouponService;
	

	@Transactional(value = TxType.REQUIRES_NEW)
	public void processItem(String holdOid) {
		PublisherHoldEntity hold = publisherHoldService.findByOid(holdOid);
		/** 解锁分仓可赎回状态 */
		this.investorTradeOrderService.unlockRedeemByHold(hold);
		
		/** 更新合仓可赎回 */
		hold.setRedeemableHoldVolume(hold.getLockRedeemHoldVolume());
		hold.setLockRedeemHoldVolume(BigDecimal.ZERO);
		this.publisherHoldService.saveEntity(hold);
		
		List<InvestorTradeOrderEntity> investOrders = this.investorTradeOrderService.findByPublisherHold(hold);
		for (InvestorTradeOrderEntity investOrder : investOrders) {
			RedeemTradeOrderReq ireq = new RedeemTradeOrderReq();
			ireq.setUid(hold.getInvestorBaseAccount().getOid());
			if (InvestorTradeOrderEntity.TRADEORDER_usedCoupons_no.equals(investOrder.getUsedCoupons())) {
				ireq.setOrderAmount(investOrder.getHoldVolume());
				ireq.setPayAmount(investOrder.getHoldVolume());
			} else {
				TradeOrderCouponEntity coupon = this.tradeOrderCouponService.findByInvestorTradeOrder(investOrder);
				if (TradeOrderCouponEntity.TRADEORDERCOUPON_type_coupon.equals(coupon.getCouponType())) {
					ireq.setOrderAmount(investOrder.getHoldVolume());
					ireq.setPayAmount(investOrder.getHoldVolume().subtract(coupon.getCouponAmount()));
				} else {
					ireq.setPayAmount(investOrder.getHoldVolume());
				}
			}
			
			ireq.setProductOid(hold.getProduct().getOid());
			/** The order type of origin branch, the default is plain */
			ireq.setPlanRedeemOid(investOrder.getWishplanOid());
			investorInvestTradeOrderExtService.cashFailOrder(ireq);
		}
		
		
	}
	
	@Transactional(value = TxType.REQUIRES_NEW)
	public void processCashItem(String holdOid) {
		PublisherHoldEntity hold = publisherHoldService.findByOid(holdOid);
		/** 解锁分仓可赎回状态 */
		this.investorTradeOrderService.unlockRedeemByHold(hold);
		
		/** 更新合仓可赎回 */
		hold.setRedeemableHoldVolume(hold.getLockRedeemHoldVolume());
		hold.setLockRedeemHoldVolume(BigDecimal.ZERO);
		this.publisherHoldService.saveEntity(hold);
		
		RedeemTradeOrderReq ireq = new RedeemTradeOrderReq();
		ireq.setUid(hold.getInvestorBaseAccount().getOid());
		ireq.setOrderAmount(hold.getRedeemableHoldVolume());
		ireq.setProductOid(hold.getProduct().getOid());
		ireq.setPlanRedeemOid(hold.getWishplanOid());
		investorInvestTradeOrderExtService.cashOrder(ireq);
	}
	
	@Transactional(value = TxType.REQUIRES_NEW)
	public void updateExpectedRevenue(String holdOid) {
		PublisherHoldEntity hold = publisherHoldService.findByOid(holdOid);
		BigDecimal holdExpectIncome = BigDecimal.ZERO;
		BigDecimal holdExpectIncomeExt = BigDecimal.ZERO;
		
		List<InvestorTradeOrderEntity>  orderList = this.investorTradeOrderService.findByPublisherHold(hold);
		for (InvestorTradeOrderEntity orderEntity : orderList) {
			BigDecimal expectIncome = investorInvestTradeOrderService.getExpectIncome(orderEntity, orderEntity.getProduct().getExpAror());
			BigDecimal expectIncomeExt = investorInvestTradeOrderService.getExpectIncome(orderEntity, orderEntity.getProduct().getExpArorSec());
			holdExpectIncome = holdExpectIncome.add(expectIncome);
			holdExpectIncomeExt = holdExpectIncomeExt.add(expectIncomeExt);
			orderEntity.setExpectIncome(expectIncome);
			orderEntity.setExpectIncomeExt(expectIncomeExt);
		}
		this.investorTradeOrderService.batchUpdate(orderList);
		
		
		hold.setExpectIncome(holdExpectIncome);
		hold.setExpectIncomeExt(holdExpectIncomeExt);
		this.publisherHoldService.saveEntity(hold);
	}



	


}
