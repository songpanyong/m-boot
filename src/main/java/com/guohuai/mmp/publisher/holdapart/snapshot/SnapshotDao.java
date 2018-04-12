package com.guohuai.mmp.publisher.holdapart.snapshot;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.guohuai.ams.product.Product;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountEntity;


public interface SnapshotDao extends JpaRepository<SnapshotEntity, String>, JpaSpecificationExecutor<SnapshotEntity> {

	
	
	@Query(value = "select * from T_MONEY_PUBLISHER_INVESTOR_HOLD_SNAPSHOT where orderOid = ?1 and snapShotDate = ?2", nativeQuery = true)
	SnapshotEntity findByOrderAndSnapShotDate(String orderOid, Date incomeDate);

	@Modifying
	@Query(value = "update T_MONEY_PUBLISHER_INVESTOR_HOLD_SNAPSHOT set snapshotVolume = snapshotVolume + ?2 "
			+ " where orderOid = ?1 and snapShotDate > ?3 ", nativeQuery = true)
	int increaseSnapshotVolume(String orderOid, BigDecimal holdIncomeVolume, Date incomeDate);
	
	@Query(value = "select * from T_MONEY_PUBLISHER_INVESTOR_HOLD_SNAPSHOT where holdOid = ?1 and snapShotDate = ?2 and snapShotVolume > 0", nativeQuery = true)
	List<SnapshotEntity> findByHoldOidAndSnapShotDate(String holdOid, Date incomeDate);

	
//	/**
//	 * 复利无奖励收益
//	 * 
//	 * @param productOid
//	 * @param baseIncomeRatio
//	 * @param incomeDate
//	 * @return
//	 */
//	@Modifying
//	@Query(value = "UPDATE t_money_publisher_investor_hold_snapshot a SET "
//			+ " a.baseIncome = TRUNCATE(a.snapshotVolume*?2, ?4), "
//			+ " a.rewardIncome = 0, "
//			+ " a.couponIncome = TRUNCATE(IF(a.additionalInterestRate>0,IF(a.affectiveDays>0,a.snapshotVolume*a.additionalInterestRate*a.affectiveDays/100/?5,0),0), ?4),"
//			+ " a.holdIncome = a.baseIncome + a.couponIncome"
//			+ " WHERE a.productOid = ?1 AND a.snapShotDate = ?3", nativeQuery = true)
//	int distributeInterestCompoundWithoutRewardIncome(String productOid, BigDecimal baseIncomeRatio, Date incomeDate, int digit, int incomeCalcBasis);
	
	@Modifying
	@Query(value = "UPDATE t_money_publisher_investor_hold_snapshot a SET a.mdBaseIncome = TRUNCATE(a.snapshotVolume * ?2, 4),"
			+ " a.mdRewardIncome = 0, "
			+ " a.mdCouponIncome = TRUNCATE(IF(a.additionalInterestRate > 0, IF(a.affectiveDays > 0, a.snapshotVolume * a.additionalInterestRate * a.affectiveDays,0), 0), 4),"
			+ " a.baseIncome = truncate(a.mdBaseIncome + a.remainderBaseIncome, 2), "
			+ " a.rewardIncome = truncate(a.mdRewardIncome + a.remainderRewardIncome, 2), "
			+ " a.couponIncome = truncate(a.mdCouponIncome + a.remainderCouponIncome, 2), "
			+ " a.latestRemainderBaseIncome = a.mdBaseIncome + a.remainderBaseIncome - a.baseIncome,"
			+ " a.latestRemainderRewardIncome = a.mdRewardIncome + a.remainderRewardIncome - a.rewardIncome, "
			+ " a.latestRemainderCouponIncome = a.mdCouponIncome + a.remainderCouponIncome - a.couponIncome,"
			+ " a.holdIncome = a.baseIncome + a.rewardIncome + a.couponIncome "
			+ " WHERE a.productOid = ?1 AND a.snapShotDate = ?3 ", nativeQuery = true)
	int remainderIncomeWithoutRewardIncome(String productOid, BigDecimal baseIncomeRatio, Date incomeDate);

	/**
	 * 复利有奖励收益
	 * 
	 * @param productOid
	 * @param baseIncomeRatio
	 * @param rewardIncomeRatio
	 * @param incomeDate
	 * @return
	 */
//	@Modifying
//	@Query(value = "UPDATE t_money_publisher_investor_hold_snapshot a SET a.baseIncome=TRUNCATE(a.snapshotVolume*?2,?4),"
//			+ " a.rewardIncomeRatio=(SELECT b.dratio FROM T_GAM_INCOME_REWARD b WHERE b.productOid=a.productOid AND a.holdDays BETWEEN b.startDate AND b.endDate LIMIT 1),"
//			+ " a.rewardIncome=TRUNCATE(IF(a.rewardIncomeRatio>0,a.snapshotVolume*a.rewardIncomeRatio,0),?4),"
//			+ " a.couponIncome=TRUNCATE(IF(a.additionalInterestRate>0,IF(a.affectiveDays>0,a.snapshotVolume*a.additionalInterestRate*a.affectiveDays/100/?5,0),0),?4),"
//			+ " a.holdIncome=a.baseIncome+a.rewardIncome+a.couponIncome,"
//			+ " a.rewardRuleOid=(SELECT oid FROM T_GAM_INCOME_REWARD b WHERE b.productOid=a.productOid AND a.holdDays BETWEEN b.startDate AND b.endDate LIMIT 1)"
//			+ " WHERE a.productOid=?1 AND a.snapShotDate=?3", nativeQuery = true)
//	int distributeInterestCompoundWithRewardIncome(String productOid, BigDecimal baseIncomeRatio, Date incomeDate, int digit, int incomeCalcBasis);
	
	
	@Modifying
	@Query(value = "UPDATE t_money_publisher_investor_hold_snapshot a SET "
			+ " a.mdBaseIncome = TRUNCATE(a.snapshotVolume * ?2, 4),"
			+ " a.rewardIncomeRatio = (SELECT b.dratio FROM T_GAM_INCOME_REWARD b WHERE b.productOid = a.productOid AND a.holdDays BETWEEN b.startDate AND b.endDate LIMIT 1),"
			+ " a.mdRewardIncome = TRUNCATE(IF(a.rewardIncomeRatio > 0,a.snapshotVolume * a.rewardIncomeRatio, 0), 4),"
			+ " a.mdCouponIncome = TRUNCATE(IF(a.additionalInterestRate > 0, IF(a.affectiveDays > 0, a.snapshotVolume * a.additionalInterestRate * a.affectiveDays,0), 0), 4),"
			+ " a.baseIncome = truncate(a.mdBaseIncome + a.remainderBaseIncome, 2), "
			+ " a.rewardIncome = truncate(a.mdRewardIncome + a.remainderRewardIncome, 2), "
			+ " a.couponIncome = truncate(a.mdCouponIncome + a.remainderCouponIncome, 2), "
			+ " a.latestRemainderBaseIncome = a.mdBaseIncome + a.remainderBaseIncome - a.baseIncome,"
			+ " a.latestRemainderRewardIncome = a.mdRewardIncome + a.remainderRewardIncome - a.rewardIncome,"
			+ " a.latestRemainderCouponIncome = a.mdCouponIncome + a.remainderCouponIncome - a.couponIncome,"
			+ " a.holdIncome = a.baseIncome + a.rewardIncome + a.couponIncome,"
			+ " a.rewardRuleOid=(SELECT oid FROM T_GAM_INCOME_REWARD b WHERE b.productOid=a.productOid AND a.holdDays BETWEEN b.startDate AND b.endDate LIMIT 1)"
			+ " WHERE a.productOid = ?1 AND a.snapShotDate = ?3 ", nativeQuery = true)
	int remainderIncome(String productOid, BigDecimal baseIncomeRatio, Date incomeDate);
	
	@Modifying
	@Query(value = "UPDATE t_money_publisher_investor_hold_snapshot a SET "
			+ " a.mdBaseIncome = TRUNCATE(a.snapshotVolume * ?2, 4),"
			+ " a.mdCouponIncome = TRUNCATE(IF(a.additionalInterestRate > 0, IF(a.affectiveDays > 0, a.snapshotVolume * a.additionalInterestRate * a.affectiveDays,0), 0), 4),"
			+ " a.baseIncome = truncate(a.mdBaseIncome + a.remainderBaseIncome, 2), "
			+ " a.couponIncome = truncate(a.mdCouponIncome + a.remainderCouponIncome, 2), "
			+ " a.latestRemainderBaseIncome = a.mdBaseIncome + a.remainderBaseIncome - a.baseIncome,"
			+ " a.latestRemainderCouponIncome = a.mdCouponIncome + a.remainderCouponIncome - a.couponIncome,"
			+ " a.holdIncome = a.baseIncome + a.couponIncome"
			+ " WHERE a.productOid = ?1 AND a.snapShotDate = ?3 ", nativeQuery = true)
	int remainderNoRewardIncome(String productOid, BigDecimal baseIncomeRatio, Date incomeDate);
	
	/**
	 * 一次性付息无奖励收益
	 */
	@Modifying
	@Query(value = "UPDATE t_money_publisher_investor_hold_snapshot a SET  "
			+ " a.mdBaseIncome = TRUNCATE(a.snapshotVolume * ?2 * ?4 / ?5, 4),"
			+ " a.mdRewardIncome = 0,"
			+ " a.mdCouponIncome=TRUNCATE(IF(a.additionalInterestRate>0,IF(a.affectiveDays>0,a.snapshotVolume*a.additionalInterestRate*a.affectiveDays,0),0), 4),"
			+ " a.baseIncome = truncate(a.mdBaseIncome + a.remainderBaseIncome, 2), "
			+ " a.rewardIncome = truncate(a.mdRewardIncome + a.remainderRewardIncome, 2), "
			+ " a.couponIncome = truncate(a.mdCouponIncome + a.remainderCouponIncome, 2), "
			+ " a.latestRemainderBaseIncome = a.mdBaseIncome + a.remainderBaseIncome - a.baseIncome,"
			+ " a.latestRemainderRewardIncome = a.mdRewardIncome + a.remainderRewardIncome - a.rewardIncome, "
			+ " a.latestRemainderCouponIncome = a.mdCouponIncome + a.remainderCouponIncome - a.couponIncome,"
			+ " a.holdIncome = a.baseIncome + a.rewardIncome + a.couponIncome "
			+ " WHERE a.productOid = ?1 AND a.snapShotDate = ?3", nativeQuery = true)
	int distributeInterestSingleWithoutRewardIncome(String productOid, BigDecimal baseIncomeRatio, Date incomeDate,
			int holdDays, int incomeCalcBasis);

	/**
	 * 一次性付息有奖励收益
	 */
	@Modifying
	@Query(value = "UPDATE t_money_publisher_investor_hold_snapshot a SET "
			+ " a.mdBaseIncome = TRUNCATE(a.snapshotVolume * ?2 * ?4 / ?5, 4),"
			+ " a.mdRewardIncome = TRUNCATE(IF(a.rewardIncomeRatio>0,a.snapshotVolume*a.rewardIncomeRatio,0), 4),"
			+ " a.rewardIncomeRatio = (SELECT b.dratio FROM T_GAM_INCOME_REWARD b WHERE b.productOid=a.productOid AND a.holdDays BETWEEN b.startDate AND b.endDate LIMIT 1), "
			+ " a.rewardRuleOid = (SELECT oid FROM T_GAM_INCOME_REWARD b WHERE b.productOid=a.productOid AND a.holdDays BETWEEN b.startDate AND b.endDate LIMIT 1), "
			+ " a.mdCouponIncome = TRUNCATE(IF(a.additionalInterestRate>0,IF(a.affectiveDays>0,a.snapshotVolume*a.additionalInterestRate*a.affectiveDays,0),0), 4), "
			+ " a.baseIncome = truncate(a.mdBaseIncome + a.remainderBaseIncome, 2), "
			+ " a.rewardIncome = truncate(a.mdRewardIncome + a.remainderRewardIncome, 2), "
			+ " a.couponIncome = truncate(a.mdCouponIncome + a.remainderCouponIncome, 2), "
			+ " a.latestRemainderBaseIncome = a.mdBaseIncome + a.remainderBaseIncome - a.baseIncome,"
			+ " a.latestRemainderRewardIncome = a.mdRewardIncome + a.remainderRewardIncome - a.rewardIncome, "
			+ " a.latestRemainderCouponIncome = a.mdCouponIncome + a.remainderCouponIncome - a.couponIncome,"
			+ " a.holdIncome = a.baseIncome + a.rewardIncome + a.couponIncome "
			+ " WHERE a.productOid=?1 AND a.snapShotDate=?3", nativeQuery = true)
	int distributeInterestSingleWithRewardIncome(String productOid, BigDecimal baseIncomeRatio, Date incomeDate,
			int holdDays, int incomeCalcBasis);

	/**
	 * 清除临时表数据
	 * 
	 * @return
	 */
	@Modifying
	@Query(value = "TRUNCATE TABLE t_money_publisher_investor_hold_snapshot_tmp", nativeQuery = true)
	int truncateSnapshotTmp();

	/**
	 * 将快照表中的计算完收益的数据以投资者维度插入临时表中
	 */
	@Modifying
	@Query(value = "INSERT INTO t_money_publisher_investor_hold_snapshot_tmp (oid, holdOid, productOid, investorOid, snapshotVolume,"
			+ " totalSnapshotVolume, holdVolume, snapShotDate, "
			+ " baseIncome, rewardIncome, holdIncome, lockHoldIncome, redeemableHoldIncome, couponIncome, wishplanOid)"
	+ " SELECT REPLACE(UUID(),'-','') AS oid, a.holdOid, a.productOid, a.investorOid, IFNULL(SUM(a.snapshotVolume), 0) AS snapshotVolume, "
	+ " sum(a.totalSnapshotVolume), IFNULL(SUM(a.holdVolume), 0), a.snapShotDate, "
	+ " SUM(IFNULL(a.baseIncome,0)) baseIncome, "
	+ " SUM(IFNULL(a.rewardIncome,0)) AS rewardIncome,"
	+ " SUM(IFNULL(a.holdIncome,0)) AS holdIncome," 
	+ " SUM(IF(a.beginRedeemDate IS NULL, a.holdIncome, IF(UNIX_TIMESTAMP(DATE(SYSDATE())) < UNIX_TIMESTAMP(a.beginRedeemDate), a.holdIncome,0))) lockHoldIncome," 
	+ " SUM(IF(a.beginRedeemDate IS NULL, 0, IF(UNIX_TIMESTAMP(DATE(SYSDATE())) >= UNIX_TIMESTAMP(a.beginRedeemDate), a.holdIncome,0))) redeemableHoldIncome,"
	+ " SUM(IFNULL(a.couponIncome, 0)) AS  couponIncome,"
	+ " a.wishplanOid"
	+ " FROM t_money_publisher_investor_hold_snapshot a" 
	+ " WHERE  a.productOid = ?1 AND a.snapShotDate=?2" 
	+ " GROUP BY a.investorOid, a.wishplanOid", nativeQuery = true)
	int insertIntoSnapshotTmp(String productOid, Date incomeDate);

	/**
	 * 根据订单表分发收益
	 */
	@Modifying
	@Query(value = "UPDATE T_MONEY_INVESTOR_TRADEORDER a,t_money_publisher_investor_hold_snapshot b SET "
			+ " a.holdVolume = a.holdVolume+b.holdIncome/?3, " + "a.totalIncome=a.totalIncome+b.holdIncome, "
			+ " a.totalBaseIncome=a.totalBaseIncome+b.baseIncome, "
			+ " a.totalRewardIncome=a.totalRewardIncome + b.rewardIncome, " 
			+ " a.totalCouponIncome=a.totalCouponIncome + b.couponIncome, " 
			+ " a.yesterdayBaseIncome=b.baseIncome, "
			+ " a.yesterdayRewardIncome=b.rewardIncome, " 
			+ " a.yesterdayCouponIncome=b.couponIncome, " 
			+ " a.yesterdayIncome=b.holdIncome, "
			+ " a.remainderBaseIncome = b.latestRemainderBaseIncome, "
			+ " a.remainderRewardIncome = b.latestRemainderRewardIncome, "
			+ " a.remainderCouponIncome = b.latestRemainderCouponIncome, "
			+ " a.value=a.value+b.holdIncome, " + "a.confirmDate=b.snapShotDate "
			+ "WHERE a.productOid=b.productOid AND a.oid=b.orderOid AND b.snapShotDate=?2 AND (a.confirmDate != b.snapShotDate OR a.confirmDate IS NULL) "
			+ "AND b.productOid=?1", nativeQuery = true)
	int distributeOrderInterestAndPrincipal(String productOid, Date incomeDate, BigDecimal netUnitShare);
	
	
	@Modifying
	@Query(value = "UPDATE T_MONEY_INVESTOR_TRADEORDER a,t_money_publisher_investor_hold_snapshot b SET "
			+ " a.totalIncome=a.totalIncome+b.holdIncome, "
			+ " a.totalBaseIncome=a.totalBaseIncome+b.baseIncome, "
			+ " a.totalRewardIncome=a.totalRewardIncome + b.rewardIncome, " 
			+ " a.totalCouponIncome=a.totalCouponIncome + b.couponIncome, " 
			+ " a.yesterdayBaseIncome=b.baseIncome, "
			+ " a.yesterdayRewardIncome=b.rewardIncome, " 
			+ " a.yesterdayCouponIncome=b.couponIncome, " 
			+ " a.yesterdayIncome=b.holdIncome, "
			+ " a.remainderBaseIncome = b.latestRemainderBaseIncome, "
			+ " a.remainderRewardIncome = b.latestRemainderRewardIncome, "
			+ " a.remainderCouponIncome = b.latestRemainderCouponIncome, "
			+ " a.confirmDate=b.snapShotDate "
			+ "WHERE a.productOid=b.productOid AND a.oid=b.orderOid AND b.snapShotDate=?2 AND (a.confirmDate != b.snapShotDate OR a.confirmDate IS NULL) "
			+ "AND b.productOid=?1", nativeQuery = true)
	int distributeOrderInterest(String productOid, Date incomeDate);

	/**
	 * 是否已经根据持有人手册表分发收益
	 * 
	 * @param productOid
	 * @param incomeDate
	 * @return
	 */
	@Query(value = "SELECT COUNT(*) FROM t_money_publisher_hold b WHERE b.productOid=?1 AND b.confirmDate=?2 LIMIT 1", nativeQuery = true)
	int hasdistributedHoldInterest(String productOid, Date incomeDate);

	/**
	 * 根据持有人手册表分发收益
	 */
	@Modifying
	@Query(value = "UPDATE t_money_publisher_hold a,t_money_publisher_investor_hold_snapshot_tmp b SET "
			+ " a.totalVolume=a.totalVolume+b.holdIncome/?3, a.holdVolume = a.holdVolume + b.holdIncome/?3, "
			+ " a.lockRedeemHoldVolume=a.lockRedeemHoldVolume + b.lockHoldIncome/?3, "
			+ " a.redeemableHoldVolume=a.redeemableHoldVolume + b.redeemableHoldIncome/?3, "
			+ " a.accruableHoldVolume=a.accruableHoldVolume+b.holdIncome/?3, "
			+ " a.holdTotalIncome=a.holdTotalIncome+b.holdIncome, "
			+ " a.totalBaseIncome=a.totalBaseIncome+b.baseIncome, "
			+ " a.totalRewardIncome=a.totalRewardIncome+b.rewardIncome, "
			+ " a.totalCouponIncome=a.totalCouponIncome+b.couponIncome, "
			+ " a.yesterdayBaseIncome=b.baseIncome, "
			+ " a.yesterdayRewardIncome=b.rewardIncome, " 
			+ " a.yesterdayCouponIncome=b.couponIncome, "
			+ " a.holdYesterdayIncome=b.holdIncome, "
			+ " a.value=a.value+b.holdIncome, " + "a.confirmDate=b.snapShotDate "
			+ " WHERE a.productOid=b.productOid AND a.oid=b.holdOid AND b.snapShotDate=?2 "
			+ " AND b.productOid=?1", nativeQuery = true)
	int distributeHoldInterestAndPrincipal(String productOid, Date incomeDate, BigDecimal netUnitShare);
	
	
	/**
	 * 根据持有人手册表分发收益
	 */
	@Modifying
	@Query(value = "UPDATE t_money_publisher_hold a,t_money_publisher_investor_hold_snapshot_tmp b SET "
			+ " a.holdTotalIncome=a.holdTotalIncome+b.holdIncome, "
			+ " a.totalBaseIncome=a.totalBaseIncome+b.baseIncome, "
			+ " a.totalRewardIncome=a.totalRewardIncome+b.rewardIncome, "
			+ " a.totalCouponIncome=a.totalCouponIncome+b.couponIncome, "
			+ " a.yesterdayBaseIncome=b.baseIncome, "
			+ " a.yesterdayRewardIncome=b.rewardIncome, " 
			+ " a.yesterdayCouponIncome=b.couponIncome, "
			+ " a.holdYesterdayIncome=b.holdIncome, "
			+ " a.confirmDate = b.snapShotDate "
			+ " WHERE a.productOid=b.productOid AND a.oid=b.holdOid AND b.snapShotDate=?2 "
			+ " AND b.productOid=?1", nativeQuery = true)
	int distributeHoldInterest(String productOid, Date incomeDate);

	/**
	 * 收益更新到投资统计信息(收益结转)
	 */
	@Modifying
	@Query(value = "UPDATE T_MONEY_INVESTOR_STATISTICS a, t_money_publisher_investor_hold_snapshot_tmp b SET "
			+ " a.totalIncomeAmount = a.totalIncomeAmount+b.holdIncome, "
			+ " a.t0YesterdayIncome = IF('PRODUCTTYPE_02' = ?3, a.t0YesterdayIncome + b.holdIncome, a.t0YesterdayIncome), "
			+ " a.tnTotalIncome = IF('PRODUCTTYPE_01' = ?3, a.tnTotalIncome + b.holdIncome, a.tnTotalIncome), "
			+ " a.t0TotalIncome = IF('PRODUCTTYPE_02' = ?3, a.t0TotalIncome + b.holdIncome, a.t0TotalIncome), "
			+ " a.t0CapitalAmount = IF('PRODUCTTYPE_02' = ?3, a.t0CapitalAmount + b.holdIncome, a.t0CapitalAmount), "
			+ " a.tnCapitalAmount = IF('PRODUCTTYPE_01' = ?3, a.tnCapitalAmount + b.holdIncome, a.tnCapitalAmount), "
			+ " a.incomeConfirmDate = b.snapShotDate "
			+ " WHERE a.investorOid = b.investorOid AND b.snapShotDate = ?2 "
			+ " AND b.productOid = ?1 AND b.wishplanOid IS NULL", nativeQuery = true)
	int distributeInterestToInvestorStatistic(String productOid, Date incomeDate, String productType);
	
	/**
	 * wish plan
	 */
	/*
	@Modifying 
	@Query(value = "UPDATE T_MONEY_INVESTOR_STATISTICS a, t_money_publisher_investor_hold_snapshot_tmp b, t_plan_product_entity p"
				+ " SET a.wishplanIncome = a.wishplanIncome + (SELECT SUM(b.holdIncome) AS income"
				+ " FROM t_money_publisher_investor_hold_snapshot_tmp b WHERE b.wishplanOid IS NOT NULL" 
				+ " AND b.productOid = ?1 AND b.snapShotDate = ?2 AND a.investorOid = b.investorOid)"
			    + " WHERE b.wishplanOid = p.oid  AND p.planType = 'MONTH_SALARY' AND a.investorOid = b.investorOid", nativeQuery = true)
	public int updateWishplanAmountByProductOid(String productOid, Date incomeDate);
	*/
	@Modifying 
	@Query(value = "UPDATE T_MONEY_INVESTOR_STATISTICS a,"
			+ " (SELECT SUM(b.holdIncome) AS income, b.investorOid as investorOid" 
			+ " FROM t_money_publisher_investor_hold_snapshot_tmp b, t_plan_product_entity p WHERE b.wishplanOid = p.oid" 
			+ " AND p.planType = 'MONTH_SALARY' AND b.productOid = ?1 AND b.snapShotDate = ?2"
			+ " GROUP BY b.investorOid) t" 
			+ " SET a.wishplanIncome = a.wishplanIncome + t.income"
			+ " WHERE a.investorOid = t.investorOid", nativeQuery = true)
	public int updateWishplanAmountByProductOid(String productOid, Date incomeDate);
	
	/**
	 * 结转
	 * @param productOid
	 * @param incomeDate
	 * @return
	 
	@Modifying
	@Query(value = "UPDATE t_plan_product_entity a, t_money_publisher_investor_hold_snapshot_tmp b SET a.incomeVolume = a.incomeVolume + (SELECT SUM(b.holdIncome) AS income " + 
						"FROM t_money_publisher_investor_hold_snapshot_tmp b " + 
						"WHERE b.wishplanOid IS NOT NULL AND b.productOid = ?1 AND b.snapShotDate = ?2 AND a.oid = b.wishplanOid) WHERE a.oid = b.wishplanOid", nativeQuery = true)
	public int wishplanIncomeVolumeByProductOid(String productOid, Date incomeDate);
	*/
	
	/**
	 * 结转
	 * @param productOid
	 * @param incomeDate
	 * @return
	 */
	@Modifying
	@Query(value = "UPDATE t_plan_product_entity a, t_money_publisher_investor_hold_snapshot_tmp b "
			      + "SET a.incomeVolume = a.incomeVolume + b.holdIncome " 	 
                  + "WHERE b.productOid = ?1 AND b.snapShotDate = ?2 AND a.oid = b.wishplanOid", nativeQuery = true)
	public int wishplanIncomeVolumeByProductOid(String productOid, Date incomeDate);
	
	/**
	 * 红利
	 * @param productOid
	 * @param incomeDate
	 * @return
	@Modifying
	@Query(value = "UPDATE t_plan_product_entity a, t_money_publisher_investor_hold_snapshot_tmp b SET a.income = a.income + (SELECT SUM(b.holdIncome) AS income " + 
			"FROM t_money_publisher_investor_hold_snapshot_tmp b " + 
			"WHERE b.wishplanOid IS NOT NULL AND b.productOid = ?1 AND b.snapShotDate = ?2 AND a.oid = b.wishplanOid) WHERE a.oid = b.wishplanOid", nativeQuery = true)
	public int wishplanIncomeByProductOid(String productOid, Date incomeDate);
	*/
	@Modifying
	@Query(value = "UPDATE t_plan_product_entity a, t_money_publisher_investor_hold_snapshot_tmp b "
			+ "SET a.income = a.income + b.holdIncome " 
            + "WHERE b.productOid = ?1 AND b.snapShotDate = ?2 AND a.oid = b.wishplanOid", nativeQuery = true)
	public int wishplanIncomeByProductOid(String productOid, Date incomeDate);
	/**
	 * 收益更新到投资统计信息(现金分红)
	 */
	@Modifying
	@Query(value = "UPDATE T_MONEY_INVESTOR_STATISTICS a, t_money_publisher_investor_hold_snapshot_tmp b SET "
			+ " a.totalIncomeAmount = a.totalIncomeAmount + b.holdIncome, "
			+ " a.t0YesterdayIncome = a.t0YesterdayIncome + b.holdIncome, "
			+ " a.t0TotalIncome = a.t0TotalIncome + b.holdIncome, "
			+ " a.totalRedeemAmount = a.totalRedeemAmount + b.holdIncome, "
			+ " a.incomeConfirmDate = b.snapShotDate "
			+ " WHERE a.investorOid = b.investorOid AND b.snapShotDate = ?2 "
			+ " AND b.productOid = ?1 AND b.wishplanOid IS NULL", nativeQuery = true)
	int distributeInterestCashToInvestorStatistic(String productOid, Date incomeDate);


	/**
	 * 是否已经发放收益到投资者收益明细（订单粒度）
	 */
	@Query(value = "SELECT COUNT(*) FROM T_MONEY_PUBLISHER_INVESTOR_INCOME b WHERE b.productOid=?1 AND b.confirmDate=?2 LIMIT 1", nativeQuery = true)
	int hasDistributedInterestToInvestorIncome(String productOid, Date incomeDate);

	/**
	 * 发放收益到投资者收益明细（订单粒度）
	 * 
	 * @param productOid
	 * @param incomeDate
	 * @param incomeOid
	 * @return
	 */
	@Modifying
	@Query(value = "INSERT INTO T_MONEY_PUBLISHER_INVESTOR_INCOME( oid,holdOid,productOid,investorOid,incomeOid,holdIncomeOid,"
			+ " rewardRuleOid,levelIncomeOid,"
			+ " totalSnapshotVolume, holdVolume, remainderBaseIncome, remainderRewardIncome, remainderCouponIncome, mdBaseIncome, mdRewardIncome, mdCouponIncome, "
			+ " latestRemainderBaseIncome, latestRemainderRewardIncome, latestRemainderCouponIncome, "
			+ " orderOid,incomeAmount,baseAmount,rewardAmount,accureVolume,confirmDate,couponAmount, wishplanOid) "
			+ " SELECT REPLACE(UUID(),'-','') oid,b.holdOid,b.productOid,b.investorOid,?3,null,"
			+ " b.rewardRuleOid,null,"
			+ " b.totalSnapshotVolume, b.holdVolume, b.remainderBaseIncome, b.remainderRewardIncome, b.remainderCouponIncome, b.mdBaseIncome, b.mdRewardIncome, b.mdCouponIncome, "
			+ " b.latestRemainderBaseIncome, b.latestRemainderRewardIncome, b.latestRemainderCouponIncome, "
			+ " b.orderOid,b.holdIncome,b.baseIncome,b.rewardIncome,b.snapshotVolume,b.snapShotDate,b.couponIncome, b.wishplanOid FROM t_money_publisher_investor_hold_snapshot b "
			+ "WHERE b.productOid = ?1 AND b.snapShotDate=?2", nativeQuery = true)
	int distributeInterestToInvestorIncome(String productOid, Date incomeDate, String incomeOid);
	
	
	

	/**
	 * 是否已经发放收益到投资者阶梯奖励收益明细(投资者阶梯奖励收益粒度)
	 * 
	 * @param productOid
	 * @param incomeDate
	 * @return
	 */
	@Query(value = "SELECT COUNT(*) FROM T_MONEY_PUBLISHER_INVESTOR_LEVELINCOME b WHERE b.productOid=?1 AND b.confirmDate=?2 LIMIT 1", nativeQuery = true)
	int hasDistributedInterestToInvestorLevelIncome(String productOid, Date incomeDate);

	/**
	 * 发放收益到投资者阶梯奖励收益明细(投资者阶梯奖励收益粒度)
	 */
	@Modifying
	@Query(value = "INSERT INTO T_MONEY_PUBLISHER_INVESTOR_LEVELINCOME(oid,holdOid,productOid,rewardRuleOid,investorOid,holdIncomeOid,"
			+ " incomeAmount,baseAmount,rewardAmount,couponAmount, "
			+ " accureVolume, totalSnapshotVolume, holdVolume,confirmDate, wishplanOid) "
			+ " SELECT REPLACE(UUID(),'-','') oid,holdOid,productOid,rewardRuleOid,investorOid,holdIncomeOid,"
			+ " IFNULL(SUM(incomeAmount),0),"
			+ " IFNULL(SUM(baseAmount),0),"
			+ " IFNULL(SUM(rewardAmount),0),"
			+ " IFNULL(SUM(couponAmount),0),"
			+ " IFNULL(SUM(accureVolume),0), IFNULL(SUM(totalSnapshotVolume),0), IFNULL(SUM(holdVolume),0), confirmDate, wishplanOid "
			+ " FROM T_MONEY_PUBLISHER_INVESTOR_INCOME b "
			+ " WHERE b.productOid=?1 AND b.confirmDate=?2 "
			+ " GROUP BY b.investorOid,b.rewardRuleOid", nativeQuery = true)
	int distributeInterestToInvestorLevelIncome(String productOid, Date incomeDate);

	/**
	 * 是否已经发放收益到投资者合仓收益明细（投资者合仓粒度）
	 */
	@Query(value = "SELECT COUNT(*) FROM T_MONEY_PUBLISHER_INVESTOR_HOLDINCOME b WHERE b.productOid=?1 AND b.confirmDate=?2 LIMIT 1", nativeQuery = true)
	int hasDistributedInterestToInvestorHoldIncome(String productOid, Date incomeDate);
	
	

	/**
	 * 发放收益到投资者合仓收益明细（投资者合仓粒度）
	 * 
	 * @param productOid
	 * @param incomeDate
	 * @return
	 */
//	@Modifying
//	@Query(value = "INSERT INTO T_MONEY_PUBLISHER_INVESTOR_HOLDINCOME(oid,holdOid,productOid,incomeOid,investorOid,incomeAmount,"
//			+ "baseAmount,rewardAmount,accureVolume,confirmDate) "
//			+ "SELECT REPLACE(UUID(),'-','') oid,holdOid,productOid,incomeOid,investorOid,TRUNCATE(IFNULL(SUM(incomeAmount),0),2),"
//			+ "TRUNCATE(IFNULL(SUM(baseAmount),0),2),TRUNCATE(IFNULL(SUM(rewardAmount),0),2),TRUNCATE(IFNULL(SUM(accureVolume),0),2),confirmDate "
//			+ "FROM T_MONEY_PUBLISHER_INVESTOR_INCOME b "
//			+ "WHERE b.productOid=?1 AND b.confirmDate=?2 " + "GROUP BY b.investorOid", nativeQuery = true)
//	int distributeInterestToInvestorHoldIncome(String productOid, Date incomeDate);
	
	
	@Modifying
	@Query(value = "INSERT INTO T_MONEY_PUBLISHER_INVESTOR_HOLDINCOME(oid,holdOid,productOid,incomeOid,investorOid,incomeAmount, "
	+ " baseAmount,rewardAmount,accureVolume, totalSnapshotVolume, holdVolume, confirmDate,couponAmount, wishplanOid)"
	+ " SELECT REPLACE(UUID(), '-', ''), holdOid, productOid, ?3, investorOid, holdIncome," 
	+ " baseIncome, rewardIncome, snapShotVolume, totalSnapshotVolume, holdVolume, ?2, couponIncome, wishplanOid"
	+ " FROM t_money_publisher_investor_hold_snapshot_tmp WHERE  productOid = ?1 AND snapShotDate = ?2", nativeQuery = true)
	int distributeInterestToInvestorHoldIncome(String productOid, Date incomeDate, String incomeOid);

	/**
	 * 是否已经再次更新投资者收益明细的引用holdIncomeOid，levelIncomeOid
	 * 
	 * @param productOid
	 * @param incomeDate
	 * @return
	 */
	@Query(value = "SELECT COUNT(*) FROM T_MONEY_PUBLISHER_INVESTOR_INCOME b WHERE b.productOid=?1 AND b.confirmDate=?2 and b.holdIncomeOid is not null and b.levelIncomeOid is not null LIMIT 1", nativeQuery = true)
	int hasReupdatedInvestorIncomeWithRewardIncome(String productOid, Date incomeDate);

	/**
	 * 再次更新投资者收益明细的引用holdIncomeOid，levelIncomeOid
	 * 
	 * @param productOid
	 * @param incomeDate
	 * @return
	 */
	@Modifying
	@Query(value = "UPDATE T_MONEY_PUBLISHER_INVESTOR_INCOME a SET "
			+ "a.holdIncomeOid=(SELECT b.oid FROM T_MONEY_PUBLISHER_INVESTOR_HOLDINCOME b WHERE b.productOid=a.productOid AND b.investorOid=a.investorOid AND b.wishplanOid=a.wishplanOid AND b.confirmDate=a.confirmDate), "
			+ "a.levelIncomeOid=(SELECT c.oid FROM T_MONEY_PUBLISHER_INVESTOR_LEVELINCOME c WHERE c.productOid=a.productOid AND c.investorOid=a.investorOid AND c.wishplanOid=a.wishplanOid AND c.rewardRuleOid=a.rewardRuleOid AND c.confirmDate=a.confirmDate) "
			+ "WHERE a.productOid = ?1 AND a.confirmDate=?2", nativeQuery = true)
	int reupdateInvestorIncomeWithRewardIncome(String productOid, Date incomeDate);

	/**
	 * 是否已经再次更新投资者收益明细的引用holdIncomeOid
	 * 
	 * @param productOid
	 * @param incomeDate
	 * @return
	 */
	@Query(value = "SELECT COUNT(*) FROM T_MONEY_PUBLISHER_INVESTOR_INCOME b WHERE b.productOid=?1 AND b.confirmDate=?2 and b.holdIncomeOid is not null LIMIT 1", nativeQuery = true)
	int hasReupdatedInvestorIncomeWithoutRewardIncome(String productOid, Date incomeDate);

	/**
	 * 再次更新投资者收益明细的引用holdIncomeOid
	 * 
	 * @param productOid
	 * @param incomeDate
	 * @return
	 */
	@Modifying
	@Query(value = "UPDATE T_MONEY_PUBLISHER_INVESTOR_INCOME a SET "
			+ "a.holdIncomeOid=(SELECT b.oid FROM T_MONEY_PUBLISHER_INVESTOR_HOLDINCOME b WHERE b.productOid=a.productOid AND b.investorOid=a.investorOid AND b.wishplanOid=a.wishplanOid AND b.confirmDate=a.confirmDate) "
			+ "WHERE a.productOid = ?1 AND a.confirmDate=?2", nativeQuery = true)
	int reupdateInvestorIncomeWithoutRewardIncome(String productOid, Date incomeDate);
	
	/**
	 * 更新阶梯收益明细的引用holdIncomeOid
	 * @param productOid
	 * @param incomeDate
	 * @return
	 */
	@Modifying
	@Query(value = "UPDATE T_MONEY_PUBLISHER_INVESTOR_LEVELINCOME a SET "
			+ "a.holdIncomeOid=(SELECT b.oid FROM T_MONEY_PUBLISHER_INVESTOR_HOLDINCOME b WHERE b.productOid=a.productOid AND b.investorOid=a.investorOid AND b.wishplanOid=a.wishplanOid AND b.confirmDate=a.confirmDate) "
			+ "WHERE a.productOid = ?1 AND a.confirmDate=?2", nativeQuery = true)
	int reupdateLevelIncomeWithoutRewardIncome(String productOid, Date incomeDate);

	/**
	 * 获取已分派收益信息
	 * 
	 * @param productOid
	 * @param incomeDate
	 * @return
	 */

	@Query(value = "SELECT TRUNCATE(IFNULL(SUM(b.yesterdayIncome),0),2),TRUNCATE(IFNULL(SUM(b.yesterdayRewardIncome),0),2),"
			+ " TRUNCATE(IFNULL(SUM(b.yesterdayBaseIncome),0),2),COUNT(DISTINCT b.investorOid),TRUNCATE(IFNULL(SUM(b.yesterdayCouponIncome),0),2) FROM T_MONEY_INVESTOR_TRADEORDER b "
			+ " WHERE b.productOid=?1 AND b.confirmDate=?2", nativeQuery = true)
	List<Object[]> getDistributedInterestInfo(String productOid, Date incomeDate);

	
	/**
	 * 试算有奖励收益
	 * 
	 * @param productOid
	 * @param incomeDate
	 * @return
	 */
	@Modifying
	@Query(value = "INSERT INTO T_MONEY_PUBLISHER_PRODUCT_REWARDINCOMEPRACTICE(oid,productOid,rewardRuleOid,"
			+ " totalHoldVolume,totalRewardIncome,totalCouponIncome,tDate) "
			+ " SELECT REPLACE(UUID(),'-',''), ?1, a.rewardRuleOid,"
			+ " SUM(a.snapshotVolume), SUM(a.rewardIncome), SUM(a.couponIncome), ?2 "
			+ " FROM t_money_publisher_investor_hold_snapshot a  "
			+ "WHERE a.productOid = ?1 AND a.snapShotDate=?2 " + "GROUP BY a.rewardRuleOid", nativeQuery = true)
	int practiceDistributeInterestWithRewardIncome(String productOid, Date incomeDate);
	
	/**
	 * 含奖励收益--试算汇总
	 */
	@Modifying
	@Query(value = "INSERT INTO T_MONEY_PUBLISHER_PRODUCT_REWARDINCOMEPRACTICE(oid,productOid,"
			+ " totalHoldVolume, totalRewardIncome, totalCouponIncome, tDate) "
			+ " SELECT REPLACE(UUID(),'-','') oid, ?1,"
			+ " IFNULL(SUM(a.snapshotVolume), 0),"
			+ " IFNULL(SUM(IFNULL(a.mdRewardIncome, 0)), 0),"
			+ " IFNULL(SUM(IFNULL(a.mdCouponIncome, 0)), 0),"
			+ " ?2 FROM t_money_publisher_investor_hold_snapshot a  "
			+ "WHERE a.productOid = ?1 AND a.snapShotDate = ?2 ", nativeQuery = true)
	int practiceSummary(String productOid, Date incomeDate);
	

	/**
	 * 试算没有奖励收益
	 * 
	 * @param productOid
	 * @param incomeDate
	 * @return
	 */
	@Modifying
	@Query(value = "INSERT INTO T_MONEY_PUBLISHER_PRODUCT_REWARDINCOMEPRACTICE(oid, productOid, rewardRuleOid,"
			+ " totalHoldVolume,totalRewardIncome,totalCouponIncome,tDate) "
			+ " SELECT REPLACE(UUID(),'-',''), ?1, null,"
			+ " IFNULL(SUM(a.snapshotVolume),0), null,"
			+ " TRUNCATE(IFNULL(SUM(a.couponIncome),0),2), ?2 FROM t_money_publisher_investor_hold_snapshot a  "
			+ "WHERE a.productOid = ?1 AND a.snapShotDate=?2 ", nativeQuery = true)
	int practiceDistributeInterestWithoutRewardIncome(String productOid, Date incomeDate);

	/**
	 * 获取大于指定快照日期之后已拍的快照日期
	 * 
	 * @param productOid
	 * @param incomeDate
	 * @return
	 */
	@Query(value = "SELECT a.snapShotDate FROM t_money_publisher_investor_hold_snapshot a WHERE " + "a.productOid = ?1 "
			+ "AND a.snapShotDate>?2 " + "GROUP BY a.snapShotDate " + "ORDER BY a.snapShotDate", nativeQuery = true)
	List<Date> getAfterIncomeDate(String productOid, Date incomeDate);

	/**
	 * 重新同步在派发收益日期之后已经拍过快照的数据
	 */
	@Modifying
	@Query(value = "UPDATE t_money_publisher_investor_hold_snapshot a,t_money_publisher_investor_hold_snapshot b "
			+ "SET b.totalSnapshotVolume = b.totalSnapshotVolume + a.mdBaseIncome + a.mdRewardIncome + a.mdCouponIncome,"
			+ " b.holdVolume = b.holdVolume + a.holdIncome,"
			+" b.remainderBaseIncome=a.latestRemainderBaseIncome,b.remainderRewardIncome=a.latestRemainderRewardIncome,b.remainderCouponIncome=a.latestRemainderCouponIncome,"
			+ " b.snapshotVolume = b.snapshotVolume + a.mdBaseIncome + a.mdRewardIncome + a.mdCouponIncome "
			+ " WHERE a.productOid = b.productOid AND a.orderOid=b.orderOid AND a.snapShotDate=?2 "
			+ " AND b.snapShotDate = ?3 AND b.productOid = ?1", nativeQuery = true)
	int reupdateAfterIncomeDateSnapshot(String productOid, Date incomeDate, Date afterIncomeDate);
	
	@Modifying
	@Query(value = "UPDATE t_money_publisher_investor_hold_snapshot a,t_money_publisher_investor_hold_snapshot b "
			+ " SET b.remainderBaseIncome = a.latestRemainderBaseIncome, b.remainderRewardIncome = a.latestRemainderRewardIncome,"
			+ " b.remainderCouponIncome = a.latestRemainderCouponIncome"
			+ " WHERE a.productOid = b.productOid AND a.orderOid=b.orderOid AND a.snapShotDate=?2 "
			+ " AND b.snapShotDate = ?3 AND b.productOid = ?1", nativeQuery = true)
	int reupdateAfterIncomeDateCashSnapshot(String productOid, Date incomeDate, Date afterIncomeDate);
	
	List<SnapshotEntity> findByInvestorBaseAccountAndProductAndSnapShotDate(InvestorBaseAccountEntity investorBaseAccount, Product product, Date snapShotDate);
	
	@Query(value = "select IFNULL(sum(incomeAmount), 0) from T_MONEY_PUBLISHER_INVESTOR_HOLDINCOME where productOid = ?1 and confirmDate = ?2 and incomeAmount > 0", nativeQuery = true)
	BigDecimal getTotalIncomeByProductOidAndIncomeDate(String productOid, Date incomeDate);
	@Query(value = "select count(*) from T_MONEY_PUBLISHER_INVESTOR_HOLDINCOME where productOid = ?1 and confirmDate = ?2 and incomeAmount > 0", nativeQuery = true)
	int getTotalDividendNumberByProductOidAndIncomeDate(String productOid, Date incomeDate);
	
	/**
	 * wishplan IncomeVolume By trade order
	 * @param productOid
	 * @return
	 */
	@Modifying
	@Query(value = "UPDATE t_plan_product_entity a, t_money_investor_tradeorder b "
			      + "SET a.incomeVolume = b.totalIncome " 	 
                  + "WHERE a.productOid = ?1 AND b.oid = a.orderOid AND a.status = 'SUCCESS'", nativeQuery = true)
	public int wishplanIncomeVolumeByOrder(String productOid);
	
	/**
	 * wishplan Income By trade order
	 * @param productOid
	 * @return
	 */
	@Modifying
	@Query(value = "UPDATE t_plan_product_entity a, t_money_investor_tradeorder b "
			      + "SET a.income = b.totalIncome " 	 
                  + "WHERE a.productOid = ?1 AND b.oid = a.orderOid AND a.status = 'SUCCESS'", nativeQuery = true)
	public int wishplanIncomeByOrder(String productOid);
}
