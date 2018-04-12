package com.guohuai.mmp.jiajiacai.wishplan.plan.dao;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.guohuai.mmp.jiajiacai.wishplan.plan.entity.PlanMonthEntity;



public interface PlanMonthDao extends JpaRepository<PlanMonthEntity, String>,  JpaSpecificationExecutor<PlanMonthEntity>{

	public List<PlanMonthEntity> findByStatus(String status);
	
	@Query(value = "SELECT * FROM t_plan_month_invest e where e.status=?1 ORDER BY e.updateTime DESC LIMIT ?2", nativeQuery = true)
	public List<PlanMonthEntity> findByStatusLimit(String status, int limit);
//	
	List<PlanMonthEntity> findByUid(String uid);
	
	List<PlanMonthEntity> findByUidOrderByCreateTimeDesc(String uid);
	
	PlanMonthEntity findByOid(String oid);
	
	@Modifying
	@Transactional
	@Query("update PlanMonthEntity e set e.status = ?2 where e.oid = ?1")
	int updateStatus(String oid, String status);
	
	
	@Modifying
	@Query("update PlanMonthEntity e set e.status = 'STOP', e.endTime = sysdate() where e.oid = ?1")
	int stopAndEndtime(String oid);
	
	@Modifying
	@Query("update PlanMonthEntity e set e.monthInvestDate = ?2, e.monthAmount = ?3 where e.oid = ?1")
	int updatePlan(String oid, int dateNumber, BigDecimal amount);
	
	
//	@Modifying
//	@Query("update PlanMonthEntity e set e.lastInvestDate = ?2, e.totalDepositAmount = ?3, e.expectedAmount = ?4, e.totalInvestCount = ?5, where e.oid = ?1")
//	int updatePlanInvest(String oid, Timestamp time,  BigDecimal investAmount, BigDecimal expectedAmount, int investCount);
	
//	PlanEntity findByPlanOidAndUid(String planOid,String uid);
//	
//	@Modifying
//	@Query("update PlanEntity e set e.status=:status, e.orderOid=:orderOid where e.oid=:oid")
//	public int updateStatus(@Param("oid")String oid, @Param("status") String status, @Param("orderOid") String orderOid);
	List<PlanMonthEntity> findByMonthInvestDate(int dueDay);
	
//	@Query(value = "SELECT * FROM t_plan_month_invest e WHERE e.monthInvestDate=?1 AND e.status=?2 AND e.lastInvestMonth<?3", nativeQuery = true)
//	List<PlanMonthEntity> findByMonthInvestDateStatusMonth(int dueDay, String status, int thisMonth);
//	
//	@Query(value = "SELECT * FROM t_plan_month_invest e WHERE e.monthInvestDate IN (?1) "
//			+ "AND e.status=?2 AND e.lastInvestMonth<?3 AND ((e.endTime IS NULL) OR (DATE_FORMAT(e.endTime ,'%Y%m') >=  ?3)", nativeQuery = true)
//	List<PlanMonthEntity> findMonthPlanByDateStatus(List<Integer> dueDays, String status, int thisMonth);
	
	
//	@Query(value = "SELECT * FROM t_plan_month_invest e WHERE e.monthInvestDate IN (?1) "
//			+ "AND e.status=?2 AND e.lastInvestMonth<?3 AND e.endTime >= ?4", nativeQuery = true)
//	List<PlanMonthEntity> findMonthPlanByDateStatusEnd(List<Integer> dueDays, String status, int thisMonth, Timestamp endTime);
//	
	@Query(value = "SELECT * FROM t_plan_month_invest e WHERE e.monthInvestDate IN (?1) "
			+ "AND e.status=?2 AND e.lastInvestMonth<?3 AND ((e.endTime IS NULL) OR (e.endTime >= ?4)) ORDER BY e.monthInvestDate DESC", nativeQuery = true)
	List<PlanMonthEntity> findMonthPlanByDateStatusEnd(List<Integer> dueDays, String status, int thisMonth, Timestamp endTime);
	
	
	@Modifying
	@Transactional
	@Query("update PlanMonthEntity e set e.lastInvestMonth = ?2 where e.oid = ?1")
	int updateInvestMonth(String oid, int thisMonth);
	
	@Query(value = "SELECT oid FROM t_plan_month_invest WHERE uid =?1 AND status !=?2 AND planType =?3 ", nativeQuery = true)
	List<String> findPlanByUidTypeStatus(String uid, String status, String planType);
	
	@Query(value = "SELECT oid FROM t_plan_month_invest WHERE uid =?1 AND status IN (?2) AND planType =?3 ", nativeQuery = true)
	List<String> findPlanByUidTypeStatuses(String uid, List<String> statuses, String planType);
	
	@Query("SELECT e FROM PlanMonthEntity e where e.status='SUCCESS' AND e.endTime < now() AND (e.planType='MONTH_EDU' OR e.planType='MONTH_TOUR')")
	List<PlanMonthEntity> queryOverdueCurrentProduct();
	
	
	@Query("SELECT e FROM PlanMonthEntity e where e.status IN ('READY', 'STOP') AND e.endTime < ?1 AND (e.planType='MONTH_EDU' OR e.planType='MONTH_TOUR')")
	List<PlanMonthEntity> queryOverdueNextTrade(Timestamp endtime);
	
	@Query("SELECT e FROM PlanMonthEntity e where e.status IN ('READY', 'STOP') AND e.endTime < now() AND (e.planType='MONTH_EDU' OR e.planType='MONTH_TOUR')")
	List<PlanMonthEntity> queryOverdueNow();
	
	
	@Modifying
	@Query("update PlanMonthEntity e set e.endTime = now(), e.status='REDEEMING' where e.oid = ?1")
	int redeemSalaryplan(String oid);
	
//	@Modifying
//	@Query("update PlanMonthEntity e set e.endTime = now(), e.transferBalance=1 where e.oid = ?1")
//	int transferBalance(String oid);
//	
	@Transactional
	@Modifying
	@Query(value = "UPDATE t_plan_month_invest SET income = income + ?1 WHERE oid = ?2", nativeQuery = true)
	public int addIncome(BigDecimal income, String oid);
	
	@Query("SELECT e FROM PlanMonthEntity e where e.totalInvestCount > 0 AND e.uid = ?1 AND e.planType = ?2")
	List<PlanMonthEntity> querySuccessPlanMonth(String uid, String type);
	
	/**
	 * queryAllSuccessPlanMonth
	 * @param uid
	 * @return
	 */
	@Query("SELECT e FROM PlanMonthEntity e WHERE e.totalInvestCount > 0 AND e.status <> 'complete' AND e.uid = ?1")
	List<PlanMonthEntity> queryAllSuccessPlanMonth(String uid);

	@Query(value = "select count(*) from t_plan_month_invest t where t.uid = ?1 and t.status <> 'FAILURE' ",nativeQuery = true)
	public int queryTotalInvestCount(String investorOid);

	/**
	 * 按月定投的当日投资次数和投资金额
	 * */
	@Query(value = "SELECT COUNT(*),IFNULL(SUM(t1.orderAmount),0) FROM t_money_investor_tradeorder t1 ,t_plan_invest t2 WHERE "
			+ " (CASE WHEN  NOW()>DATE_FORMAT(NOW(),'%Y-%m-%d 15:00:00')  THEN t1.orderTime >DATE_FORMAT(NOW(),'%Y-%m-%d 15:00:00') "
			+ " ELSE t1.orderTime >DATE_FORMAT(DATE_SUB(CURRENT_DATE(),INTERVAL 1 DAY),'%Y-%m-%d 15:00:00') END ) AND t1.wishplanOid = t2.oid"
			+ " AND t2.monthOid IS NOT NULL AND  t2.uid = ?1 AND t1.orderType='wishInvest'  " ,nativeQuery = true)
	public List<Object[]> queryTodayInvestInfo(String investorOid);
	
	@Query(value = "select * from t_plan_month_invest t where t.uid =?1 and t.status ='complete' and t.totalDepositAmount > 0 and t.planType=?2",nativeQuery =true)
	public List<PlanMonthEntity> findCompleteByUid(String uid,String planType);
	
	/**
	 * Query the DEPOSITED invest oid
	 * @param investorOid
	 * @return
	 */
	@Query(value = "SELECT oid FROM t_plan_invest WHERE monthOid = ?1 AND status IN ('SUCCESS', 'DEPOSITED', 'REDEEMING','COMPLETE')" ,nativeQuery = true)
	public List<String> queryInvestOidBySalary(String monthOid);
	
	/**
	 * Salary invest
	 * @param planOid
	 * @return
	 */
	@Query(value = "SELECT SUM(h.incomeAmount) from T_MONEY_PUBLISHER_INVESTOR_HOLDINCOME h"
			       + " WHERE h.confirmDate = adddate(curdate(),-1) AND h.wishplanOid IN"
			       + " (SELECT p.oid FROM t_plan_product_entity p WHERE p.planOid = ?1 AND p.status IN ('SUCCESS', 'TOREDEEM', 'REDEEMING') )", nativeQuery = true)
	BigDecimal yesterdayIncome(String planOid);

	/**
	 * Education month, tour month 
	 * @param monthOid
	 * @return
	 */
	@Query(value = "SELECT h.incomeAmount FROM T_MONEY_PUBLISHER_INVESTOR_HOLDINCOME h"
			        + " WHERE h.confirmDate = adddate(curdate(),-1) AND h.wishplanOid = ?1", nativeQuery = true)
	BigDecimal completeYesterdayIncome(String monthOid);

	@Query(value="select t.endTime from t_plan_month_invest t where t.oid = ?1",nativeQuery = true)
	public Timestamp findPlanEndtime(String planOid);
	
}
