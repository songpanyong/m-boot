package com.guohuai.mmp.jiajiacai.wishplan.plan.dao;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.guohuai.mmp.jiajiacai.wishplan.plan.entity.PlanInvestEntity;

public interface PlanInvestDao
		extends JpaRepository<PlanInvestEntity, String>, JpaSpecificationExecutor<PlanInvestEntity> {

	List<PlanInvestEntity> findByUid(String uid);

	List<PlanInvestEntity> findByUidOrderByCreateTimeDesc(String uid);

	@Query("SELECT e FROM PlanInvestEntity e where e.uid=?1 AND e.planType IN ('ONCE_EDU', 'ONCE_TOUR') ORDER BY createTime Desc")
	List<PlanInvestEntity> findOncePlanByUidOrderByCreateTimeDesc(String uid);

	@Query(value = "SELECT e.oid as oid, e.planType as planType, e.depositAmount as depositAmount, e.status as status, e.createTime as createTime, e.endTime as endTime, e.planTarget as planTarget FROM t_plan_invest e where e.uid = ?1 AND e.planType IN ('ONCE_EDU', 'ONCE_TOUR') AND e.`status` IN (?2) UNION SELECT m.oid as oid, m.planType as planType, m.totalDepositAmount as depositAmount, m.status as status, m.createTime as createTime, m.endTime as endTime, m.planTarget as planTarget  FROM t_plan_month_invest m WHERE m.uid= ?1 AND m.`status` IN (?2) ORDER BY createTime Desc LIMIT ?3 OFFSET ?4", nativeQuery = true)
	List<Object[]> findPlanSByUidByStatusOrderByCreateTimeDesc(String uid, List<String> status, int limit, int offset);

	@Query("SELECT e FROM PlanInvestEntity e where e.uid=?1 AND e.planType=?2 AND e.status IN (?3)")
	List<PlanInvestEntity> findSuccessByUidTypeDesc(String uid, String type, List<String> statusList);
	
	@Query("SELECT e FROM PlanInvestEntity e where e.uid=?1 AND e.planType IN (?2) AND e.status IN (?3)")
	List<PlanInvestEntity> findSuccessByUidTypeStatus(String uid, List<String> typeList, List<String> statusList);

	@Query("SELECT e FROM PlanInvestEntity e where e.monthOid=?1 AND e.status IN (?2)")
	List<PlanInvestEntity> findSuccessByMonthDesc(String monthOid, List<String> statusList);

	PlanInvestEntity findByOid(String oid);

	@Transactional
	@Modifying
	@Query("update PlanInvestEntity e set e.status=:status where e.oid=:oid")
	public int updateStatus(@Param("oid") String oid, @Param("status") String status);

	@Modifying
	@Query("update PlanInvestEntity e set e.status=:status, e.endTime = now() where e.oid=:oid")
	public int complete(@Param("oid") String oid, @Param("status") String status);

	@Query("SELECT e FROM PlanInvestEntity e where e.status='SUCCESS' AND e.endTime < now()")
	List<PlanInvestEntity> queryOverdueCurrentProduct();

	public List<PlanInvestEntity> findByStatus(String status);

	@Query(value = "SELECT * FROM t_plan_invest e where e.status=?1 ORDER BY e.createTime DESC LIMIT ?2", nativeQuery = true)
	public List<PlanInvestEntity> findStatusesLimit(String status, int limit);

	@Transactional
	@Modifying
	@Query(value = "UPDATE t_plan_invest SET balance = balance - ?1  WHERE oid = ?2 and balance >= ?1", nativeQuery = true)
	public int subtractBalance(BigDecimal balance, String oid);

	@Transactional
	@Modifying
	@Query(value = "UPDATE t_plan_invest SET balance = balance + ?1 WHERE oid = ?2", nativeQuery = true)
	public int addBalance(BigDecimal balance, String oid);

	@Transactional
	@Modifying
	@Query(value = "UPDATE t_plan_invest SET balance = balance + ?1, status = ?2  WHERE oid = ?3", nativeQuery = true)
	public int updateBalanceStatus(BigDecimal balance, String status, String oid);

	@Query(value = "SELECT balance FROM t_plan_invest WHERE oid = ?1", nativeQuery = true)
	BigDecimal queryBalanceByOid(String oid);

	@Transactional
	@Modifying
	@Query(value = "UPDATE t_plan_invest SET withholdCount = withholdCount + 1  WHERE oid = ?1", nativeQuery = true)
	public int addWithholdCount(String oid);

	List<PlanInvestEntity> findByMonthOidAndStatus(String planMonthOid, String status);

	List<PlanInvestEntity> findByMonthOid(String planMonthOid);

	@Query("SELECT e FROM PlanInvestEntity e where e.status='SUCCESS' AND e.endTime < now()")
	List<PlanInvestEntity> queryOverdue();

	@Query(value = "SELECT p.* FROM t_plan_invest p WHERE p.monthOid=?1 AND p.status IN ('SUCCESS', 'DEPOSITED') ORDER BY p.createTime DESC limit 1", nativeQuery = true)
	public PlanInvestEntity findLatestDeposit(String planMonthOid);

	@Query(value = "SELECT l.name FROM T_PLANLIST_ENTITY l, t_plan_invest i  WHERE i.oid=?1 AND l.planType=i.planType", nativeQuery = true)
	public String findPlanName(String oid);

	@Query(value = "SELECT endTime FROM t_plan_invest WHERE oid=?1", nativeQuery = true)
	public Timestamp findPlanEndtime(String oid);

	@Query("SELECT e FROM PlanInvestEntity e where e.status IN ('SUCCESS', 'REDEEMING') AND e.planType IN ('ONCE_EDU', 'ONCE_TOUR') AND e.endTime < now() ORDER BY e.createTime DESC")
	List<PlanInvestEntity> findOverdueNow();

	@Query(value = "SELECT * FROM t_plan_invest e where e.status=?1 AND e.planType IN ('ONCE_EDU', 'ONCE_TOUR') ORDER BY e.updateTime DESC limit ?2", nativeQuery = true)
	List<PlanInvestEntity> findOncePlanByStatus(String status, int limit);

	@Query("SELECT e FROM PlanInvestEntity e where e.monthOid = ?1 AND e.status IN (?2)")
	List<PlanInvestEntity> findByMonthOidAndStatusList(String planMonthOid, List<String> statusList);

	@Modifying
	@Transactional
	@Query(value = "UPDATE T_MONEY_INVESTOR_STATISTICS a SET a.wishplanIncome = a.wishplanIncome + ?2"
			+ " WHERE a.investorOid = ?1", nativeQuery = true)
	public int updateWishplanAmountByInvestorOid(String investorOid, BigDecimal income);

	@Query(value = "SELECT status FROM t_plan_invest WHERE oid=?1", nativeQuery = true)
	public String findPlanStatus(String oid);
	@Query(value = "select count(*) from t_plan_invest t where t.uid = ?1 and t.monthOid  is NULL and t.status <> 'FAILURE' ",nativeQuery = true)
	public int queryTotalInvestInfo(String investorOid);
	
	@Query(value = "SELECT COUNT(*),IFNULL(SUM(t.depositAmount),0) FROM t_plan_invest t WHERE "
			+ "  (CASE WHEN  NOW()>DATE_FORMAT(NOW(),'%Y-%m-%d 15:00:00')  THEN t.createTime >DATE_FORMAT(NOW(),'%Y-%m-%d 15:00:00') "
			+ " ELSE t.createTime >DATE_FORMAT(DATE_SUB(CURRENT_DATE(),INTERVAL 1 DAY),'%Y-%m-%d 15:00:00') END ) AND t.uid=?1 AND  "
			+ " t.monthOid IS NULL AND t.status <> 'failure' ", nativeQuery = true)
	public List<Object[]> queryTodayInvestCount(String investorOid);

	@Query(value = "select * from t_plan_invest t where t.uid = ?1 and t.monthOid is  null and"
			+ "  t.status ='complete' and t.planType =?2", nativeQuery = true)
	public List<PlanInvestEntity> findCompleteByUid(String uid,String planType );
	
	/**
	 * Once once
	 * @param planOid
	 * @return
	 */
	@Query(value = "SELECT h.incomeAmount FROM T_MONEY_PUBLISHER_INVESTOR_HOLDINCOME h"
			        + " WHERE h.confirmDate = adddate(curdate(),-1) AND h.wishplanOid = ?1", nativeQuery = true)
	BigDecimal onceYesterdayIncome(String planOid);
	
	/**
	 * 查询月定投心愿计划已经划扣的记录
	 * */
	@Query(value = "SELECT * FROM t_plan_invest p WHERE p.monthOid=?1 AND p.status IN ('SUCCESS', 'DEPOSITED') ", nativeQuery = true)
	public List<PlanInvestEntity> findDeductInfoByOid(String oid);

	@Query(value="SELECT oid,planType,depositAmount,createTime,status,planBatch FROM (SELECT t1.oid AS oid,t1.planType AS planType ,t1.totalDepositAmount"
			+ "  AS depositAmount,t1.createTime AS createTime,t1.status AS status ,t1.uid  AS uid ,t1.planBatch AS planBatch FROM t_plan_month_invest t1 where t1.totalDepositAmount > 0 "
			+ " UNION ALL SELECT t2.oid,t2.planType ,t2.depositAmount,t2.createTime,t2.status ,t2.uid ,t2.planBatch FROM t_plan_invest t2  WHERE  t2.monthOid IS NULL) a "
			+ " WHERE a.status<>'failure' and  a.uid =?1 and if(?2 is null or ?2 ='',0=0,a.planType=?2) and if(?3 is null or ?3 ='' ,0=0,a.createTime>?3)"
			+ " and if(?4 is null or ?4 ='', 0=0,a.createTime<=?4) and if(?5 is null or ?5='' ,0=0,a.planBatch like ?5)" ,nativeQuery = true)
	public List<Object[]> findPlanByCondition(String investorOid, String planName, Timestamp investTimeStart,
			Timestamp investTimeEnd,String planBatch);
	
	/**
	 * The complete invest and income
	 * @param uid
	 * @return
	 */
	@Query(value = "SELECT e.oid as oid, e.depositAmount as depositAmount, e.balance as income "
			      + "FROM t_plan_invest e WHERE e.uid = ?1 AND e.planType IN ('ONCE_EDU', 'ONCE_TOUR') AND e.`status`='COMPLETE' "
			      + "UNION SELECT m.oid as oid, m.totalDepositAmount as depositAmount, m.income as income "
			      + "FROM t_plan_month_invest m WHERE m.uid= ?1 AND m.`status`='COMPLETE' ", nativeQuery = true)
	List<Object[]> findPlanSByUidByStatusByComplete(String uid);

	@Query(value="select * from t_plan_invest t where t.monthOid = ?1 and t.status <>'failure' "
			+ " order by t.createTime asc limit 1",nativeQuery = true)
	public PlanInvestEntity findByMonthOidAsc(String oid);




}
