package com.guohuai.mmp.investor.baseaccount.detailcheck;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountEntity;

public interface DetailCheckDao extends JpaRepository<DetailCheckEntity, String>, JpaSpecificationExecutor<DetailCheckEntity> {
	
	@Query(value = "SELECT t.checkTime FROM T_MONEY_INVESTOR_DETAIL_CHECK t ORDER BY t.checkTime DESC LIMIT 1", nativeQuery = true)
	public String getMaxCheckTime();
	
	public DetailCheckEntity findByCheckTimeAndInvestorBaseAccount(String checkTime, InvestorBaseAccountEntity account);
	
	/** 
	 * 明细对账数据: 
	 * 累计充值 - 累计提现 - 提现在途中 - 累计申购(不包含卡券) - 申购在途中(不包含卡券) + 赎回 + 卡券 + 现金红包
	 */
	@Query(value = "SELECT IFNULL(depositAmount, 0) AS depositAmount, IFNULL(withdrawAmount, 0)  AS withdrawAmount, " 
 + " IFNULL(withdrawOnWayAmount, 0) AS withdrawOnWayAmount, "
 + " IFNULL(totalInvestAmount, 0) AS totalInvestAmount, " 
 + " IFNULL(totalOnWayInvest, 0) AS totalOnWayInvest, IFNULL(totalRedeemAmount, 0) AS totalRedeemAmount, " 
 + " IFNULL(couponAmount, 0) AS couponAmount, IFNULL(redEnvelope, 0) AS redEnvelope, " 
 + " IFNULL(depositAmount, 0) -  IFNULL(withdrawAmount, 0) - IFNULL(withdrawOnWayAmount, 0) - IFNULL(totalInvestAmount, 0) - IFNULL(totalOnWayInvest, 0) "
 + " + IFNULL(totalRedeemAmount, 0) + IFNULL(couponAmount, 0) + IFNULL(redEnvelope, 0) AS balance FROM "  		
 
+ " (SELECT t0.oid AS investorOid FROM T_MONEY_INVESTOR_BASEACCOUNT t0) a0 " 

+ " LEFT  OUTER  JOIN " 
+ " (SELECT SUM(t1.orderAmount) AS depositAmount, t1.investorOid " 
+ " FROM T_MONEY_INVESTOR_BANKORDER t1 WHERE t1.orderType = 'deposit' AND t1.orderStatus = 'paySuccess' " 
+ " GROUP BY t1.investorOid) a1 " 
+ " ON (a0.investorOid = a1.investorOid) " 

+ " LEFT OUTER JOIN   "
+ " (SELECT SUM(t2.orderAmount) AS withdrawAmount, t2.investorOid FROM  "
+ " T_MONEY_INVESTOR_BANKORDER t2 WHERE t2.orderType = 'withdraw' AND t2.orderStatus IN ('paySuccess')  "
+ " GROUP BY t2.investorOid) a2  "
+ " ON (a0.investorOid = a2.investorOid)  "

+ " LEFT OUTER JOIN   "
+ " (SELECT SUM(t2.orderAmount) AS withdrawOnWayAmount, t2.investorOid FROM  "
+ " T_MONEY_INVESTOR_BANKORDER t2 WHERE t2.orderType = 'withdraw' AND t2.orderStatus IN ('toPay')  "
+ " GROUP BY t2.investorOid) a8  "
+ " ON (a0.investorOid = a8.investorOid)  "

+ " LEFT OUTER JOIN  "
+ " (SELECT SUM(t3.orderAmount) AS totalInvestAmount, t3.investorOid FROM  "
+ " T_MONEY_INVESTOR_TRADEORDER t3 WHERE t3.orderType IN ('invest')   "
+ " AND t3.orderStatus IN ('confirmed')  "
+ " GROUP BY t3.investorOid) a3  "
+ " ON (a0.investorOid = a3.investorOid)  "

+ " LEFT OUTER JOIN  "
+ " (SELECT SUM(t6.orderAmount) AS totalOnWayInvest, t6.investorOid FROM  "
+ " T_MONEY_INVESTOR_TRADEORDER t6 WHERE  t6.orderType in ('invest')  "
+ " AND t6.orderStatus IN ('accepted')  "
+ " GROUP BY t6.investorOid) a7  "
+ " ON (a0.investorOid = a7.investorOid)  "

+ " LEFT OUTER JOIN  "
+ " (SELECT SUM(t3.payAmount) AS totalRedeemAmount, t3.investorOid FROM  "
+ " T_MONEY_INVESTOR_TRADEORDER t3 WHERE t3.orderType IN ('normalRedeem', 'cash', 'cashFailed', 'clearRedeem', 'dividend')  "
+ " AND t3.publisherCloseStatus = 'closed'  "
+ " GROUP BY t3.investorOid) a4  "
+ " ON (a0.investorOid = a4.investorOid)  "

+ " LEFT  OUTER JOIN  "
+ " (SELECT SUM(t4.couponAmount) AS couponAmount, t4.investorOid FROM  "
+ " T_MONEY_INVESTOR_TRADEORDER_COUPON t4, T_MONEY_INVESTOR_TRADEORDER t6  "
+ " WHERE t4.orderOid = t6.oid AND t4.couponType = 'coupon'  "
+ " AND t6.orderStatus IN ('accepted', 'confirmed')  "
+ " GROUP BY t4.investorOid) a5  "
+ " ON (a0.investorOid = a5.investorOid)  "

+ " LEFT OUTER JOIN  "
+ " (SELECT SUM(t5.orderAmount) AS redEnvelope, t5.investorOid FROM  "
+ " T_MONEY_INVESTOR_BANKORDER t5 WHERE t5.orderType = 'redEnvelope'  "
+ " AND t5.orderStatus = 'paySuccess'  "
+ " GROUP BY t5.investorOid) a6  "
+ " ON (a0.investorOid = a6.investorOid)  "

+ " WHERE a0.investorOid = ?1 ", nativeQuery = true)
	public List<Object[]> getMoneyDetailData(String investorOid);
	
}
