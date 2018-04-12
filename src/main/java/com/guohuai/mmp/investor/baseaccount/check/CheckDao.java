package com.guohuai.mmp.investor.baseaccount.check;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountEntity;

public interface CheckDao extends JpaRepository<CheckEntity, String>, JpaSpecificationExecutor<CheckEntity> {

	@Query(value = "FROM CheckEntity WHERE investorBaseAccount = ?1 AND checkTime = ?2")
	public CheckEntity findByAccAndChTime(InvestorBaseAccountEntity account, String checkTime);
	
	@Query(value = "SELECT t.checkTime FROM T_MONEY_INVESTOR_CHECK t ORDER BY t.checkTime DESC LIMIT 1", nativeQuery = true)
	public String getMaxCheckTime();
	
	@Query(value = "SELECT IFNULL(SUM(a.moneyAmount), 0), SUM(a.capitalAmount) FROM T_MONEY_INVESTOR_CHECK a WHERE a.checkTime = ?1 ", nativeQuery = true)
	public List<Object[]> getSumAmt(String checkTime);
	
	@Query(value = "SELECT t.checkTime FROM T_MONEY_INVESTOR_CHECK t WHERE t.investorOid = ?1 ORDER BY t.checkTime DESC LIMIT 1", nativeQuery = true)
	public String getUserMaxCheckTime(String investorOid);
	
	/**
	 * 资金总额 = 累计已充值 - 已成功提现 + 定期累计收益 + 活期累计收益 + 卡券红包金额
	 */
	@Query(value = "SELECT IFNULL(depositAmount, 0) as depositAmount, IFNULL(withdrawAmont, 0) as withdrawAmont, IFNULL(tnInterest, 0) as tnInterest, IFNULL(t0Interest, 0) as t0Interest, "
			+ " IFNULL(couponAmount, 0) + IFNULL(redEnvelope, 0) as couponAmount, "  
			+ " IFNULL(depositAmount, 0) - IFNULL(withdrawAmont, 0) + IFNULL(tnInterest, 0) + IFNULL(t0Interest, 0) + IFNULL(couponAmount, 0) + "
			+ " IFNULL(redEnvelope, 0) AS total FROM "

			+ " (SELECT t0.oid AS investorOid FROM T_MONEY_INVESTOR_BASEACCOUNT t0) a0 "
			
			+ " LEFT  OUTER  JOIN "
			+ " (SELECT SUM(t1.orderAmount) AS depositAmount, t1.investorOid FROM  "
					+ " T_MONEY_INVESTOR_BANKORDER t1 WHERE t1.orderStatus = 'done' AND t1.orderType = 'deposit' GROUP BY t1.investorOid) a1 "
			+ " ON (a0.investorOid = a1.investorOid) "

			+ " LEFT  OUTER  JOIN "
			+ " (SELECT SUM(t1.orderAmount) AS withdrawAmont, t1.investorOid FROM  "
					+ " T_MONEY_INVESTOR_BANKORDER t1 WHERE t1.orderStatus = 'done' AND t1.orderType = 'withdraw' "
					+ " GROUP BY t1.investorOid) a2 "
			+ " ON (a0.investorOid = a2.investorOid) "

			+ " LEFT OUTER JOIN "
			+ " (SELECT SUM(t3.holdTotalIncome) AS tnInterest, t3.investorOid FROM " 
					+ " T_MONEY_PUBLISHER_HOLD t3, t_gam_product t4 " 
					+ " WHERE t3.productOid = t4.oid AND t4.type = 'PRODUCTTYPE_01' GROUP BY t3.investorOid) a3 "
			+ " ON (a0.investorOid = a3.investorOid) "

			+ " LEFT OUTER JOIN " 
			+ " (SELECT SUM(t5.holdTotalIncome) AS t0Interest, t5.investorOid FROM " 
					+ " T_MONEY_PUBLISHER_HOLD t5, t_gam_product t6 " 
					+ " WHERE t5.productOid = t6.oid AND t6.type = 'PRODUCTTYPE_02' GROUP BY t5.investorOid) a4 " 
			+ " ON (a0.investorOid = a4.investorOid) "
			
			+ " LEFT OUTER JOIN "
			+ " (SELECT SUM(t7.couponAmount) AS couponAmount, t7.investorOid FROM "
					+ " T_MONEY_INVESTOR_TRADEORDER_COUPON t7, T_MONEY_INVESTOR_TRADEORDER t8 "
					+ " WHERE t7.orderOid = t8.oid AND t7.couponType in ('coupon') "
					+ " AND t8.orderStatus IN ( 'confirmed', 'accepted' ) GROUP BY t7.investorOid) a5 " 
			+ " ON (a0.investorOid = a5.investorOid) "
			
			+ " LEFT OUTER JOIN "
			+ " (SELECT SUM(t1.orderAmount) AS redEnvelope, t1.investorOid FROM "
					+ " T_MONEY_INVESTOR_BANKORDER t1 WHERE t1.orderStatus = 'done' AND t1.orderType = 'redEnvelope' "
					+ " GROUP BY t1.investorOid) a6 "
			+ " ON (a0.investorOid = a6.investorOid) "

			+ " WHERE a0.investorOid = ?1 ", nativeQuery = true)
	public List<Object[]> getCheckMoneyData(String investorOid);
	
	/**
	 * 资产总额 = 可提现余额 + 提现申请中金额 + 活期持有 + 活期申购冻结 + 定期持有 + 定期申购冻结
	 */
	@Query(value = "SELECT IFNULL(applyAvailableBalance, 0) as applyAvailableBalance,  IFNULL(withdrawFrozenBalance, 0) as withdrawFrozenBalance, "
			+ " IFNULL(t0Amount, 0) as t0Amount, IFNULL(t0LockAmount, 0) as t0LockAmount, IFNULL(tnAmount, 0) as tnAmount, "
			+ " IFNULL(tnLockAmount, 0) as tnLockAmount, "
			+ " IFNULL(applyAvailableBalance, 0) + IFNULL(withdrawFrozenBalance, 0) + IFNULL(t0Amount, 0) + IFNULL(t0LockAmount, 0) + IFNULL(tnAmount, 0) + "
			+ " IFNULL(tnLockAmount, 0) AS total FROM " 
			
			+ " (SELECT t1.applyAvailableBalance, t1.oid AS investorOid FROM T_MONEY_INVESTOR_BASEACCOUNT t1) a1 "
			
			+ " LEFT OUTER JOIN " 
			+ " (SELECT t1.withdrawFrozenBalance, t1.oid AS investorOid FROM T_MONEY_INVESTOR_BASEACCOUNT t1) a2 "
			+ " ON (a1.investorOid = a2.investorOid) "
			
			+ " LEFT OUTER JOIN " 
			+ " (SELECT SUM(t3.holdVolume) AS t0Amount, t3.investorOid FROM " 
					+ " t_money_investor_tradeorder t3, t_gam_product t4 "
					+ " WHERE t3.productOid = t4.oid AND t4.type = 'PRODUCTTYPE_02' AND t3.orderType IN ('invest') AND t3.orderStatus = 'confirmed' "
					+ " GROUP BY t3.investorOid) a3 "
			+ " ON (a1.investorOid = a3.investorOid) "
			
			+ " LEFT  OUTER JOIN "
			+ " (SELECT SUM(t3.holdVolume) AS t0LockAmount, t3.investorOid FROM " 
					+ " t_money_investor_tradeorder t3, t_gam_product t4 " 
					+ " WHERE t3.productOid = t4.oid AND t4.type = 'PRODUCTTYPE_02' AND t3.orderType IN ('invest') AND t3.orderStatus = 'accepted' "
					+ " GROUP BY t3.investorOid) a4 "
			+ " ON (a1.investorOid = a4.investorOid) "
			
			+ " LEFT OUTER JOIN "
			+ " (SELECT SUM(t3.holdVolume) AS tnAmount, t3.investorOid FROM " 
					+ " t_money_investor_tradeorder t3, t_gam_product t4 "
					+ " WHERE t3.productOid = t4.oid AND t4.type = 'PRODUCTTYPE_01' AND t3.orderType IN ('invest') AND t3.orderStatus = 'confirmed' "
					+ " GROUP BY t3.investorOid) a5 "
			+ " ON (a1.investorOid = a5.investorOid) "
					
			+ " LEFT OUTER JOIN "
			+ " (SELECT SUM(t3.holdVolume) AS tnLockAmount, t3.investorOid FROM " 
					+ " t_money_investor_tradeorder t3, t_gam_product t4 "
					+ " WHERE t3.productOid = t4.oid AND t4.type = 'PRODUCTTYPE_01' AND t3.orderType IN ('invest') AND t3.orderStatus = 'accepted' "
					+ " GROUP BY t3.investorOid) a6 "
			+ " ON (a1.investorOid = a6.investorOid) "

			+ " WHERE a1.investorOid =  ?1 ", nativeQuery = true)
	public List<Object[]> getCheckCapitalData(String investorOid);
}
