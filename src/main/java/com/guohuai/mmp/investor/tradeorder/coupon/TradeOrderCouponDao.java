package com.guohuai.mmp.investor.tradeorder.coupon;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.guohuai.mmp.investor.bankorder.InvestorBankOrderEntity;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderEntity;

public interface TradeOrderCouponDao extends JpaRepository<TradeOrderCouponEntity, String>, JpaSpecificationExecutor<TradeOrderCouponEntity>{

	TradeOrderCouponEntity findByInvestorTradeOrder(InvestorTradeOrderEntity entity);
	
	TradeOrderCouponEntity findByInvestorBankOrder(InvestorBankOrderEntity entity);

	TradeOrderCouponEntity findByCoupons(String coupons);

	@Query(value = "SELECT SUM(a.couponAmount),MAX(a.createTime),COUNT(oid),couponType FROM T_MONEY_INVESTOR_TRADEORDER_COUPON a GROUP BY couponType", nativeQuery = true)
	List<Object[]> getCouponStatistics();
	
	@Query(value = "SELECT * FROM T_MONEY_INVESTOR_TRADEORDER_COUPON WHERE bankOrderOid = ?1 AND couponType = 'redEnvelopeCoupon';", nativeQuery = true)
	TradeOrderCouponEntity findByBankOrder(String bankOrderOid);
}
