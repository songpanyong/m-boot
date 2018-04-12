package com.guohuai.mmp.jiajiacai.wishplan.plan.dao;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.guohuai.mmp.jiajiacai.wishplan.plan.entity.PlanInvestEntity;
import com.guohuai.mmp.jiajiacai.wishplan.plan.entity.PlanProductEntity;


public interface PlanProductDao extends JpaRepository<PlanProductEntity,String>,JpaSpecificationExecutor<PlanProductEntity>{

	List<PlanProductEntity> findByUidAndStatus(String uid, String status);
	
	List<PlanProductEntity> findByUid(String uid);
	
//	@Modifying
//	@Query("update PlanEntity e set e.status=:status where e.orderOid=:orderOid")
//	String stopPlan(@Param("orderOid") String orderOid,@Param("status") String status);
	
//	@Modifying
//	@Query("update PlanEntity e set e.dateNumber=:dateNumber, e.amount=:amount where e.orderOid=:orderOid")
//	String updatePlan(@Param("orderOid") String orderOid, @Param("dateNumber") int dateNumber, @Param("amount") int amount);
//	
	PlanProductEntity findByOid(String oid);
	
	PlanProductEntity findByPlanOidAndUid(String planOid,String uid);
	
	@Modifying
	@Query("update PlanProductEntity e set e.status=:status, e.orderOid=:orderOid where e.oid=:oid")
	public int updateStatus(@Param("oid")String oid, @Param("status") String status, @Param("orderOid") String orderOid);
    
	List<PlanProductEntity> findByPlanOidAndStatus(String planOid, String status);
	
	
	@Modifying
	@Query("update PlanProductEntity e set e.status=:status where e.oid=:oid")
	public int updateStatus(@Param("oid")String oid, @Param("status") String status);
	
	@Transactional
	@Modifying
	@Query(value = "UPDATE t_plan_product_entity SET income = income + ?1 WHERE oid = ?2", nativeQuery = true)
	public int addIncome(BigDecimal income, String oid);
	
	@Transactional
	@Modifying
	@Query(value = "UPDATE t_plan_product_entity SET income = income + ?1, status = 'COMPLETE'  WHERE oid = ?2", nativeQuery = true)
	public int addIncomeComplete(BigDecimal income, String oid);
	
	@Query(value = "SELECT planOid FROM t_plan_product_entity WHERE oid = ?1", nativeQuery = true)
	public String queryInvestOid(String oid);
	
	@Query(value = "SELECT l.name FROM T_PLANLIST_ENTITY l, t_plan_product_entity p  WHERE p.oid=?1 AND l.planType=p.planType", nativeQuery = true)
	public String findPlanName(String planOid);
	
	
	@Query(value = "SELECT status FROM t_plan_product_entity WHERE planOid = ?1", nativeQuery = true)
	public List<String> queryStatusesByInvest(String investOid);
	
	@Query(value = "SELECT * FROM t_plan_product_entity WHERE planOid = ?1 AND status IN (?2)", nativeQuery = true)
	public List<PlanProductEntity> queryPlanProductByInvestAndStatusList(String planOid, List<String> statuses);
	
	@Query(value = "SELECT * FROM t_plan_product_entity WHERE planOid IN (?1) AND status IN (?2)", nativeQuery = true)
	public List<PlanProductEntity> queryByInvestAndStatus(List<String> planOids, List<String> statuses);
	
	@Query(value = "SELECT COUNT(*) FROM t_plan_product_entity WHERE planOid = ?1 AND status IN (?2)", nativeQuery = true)
	public int countPlanProductByInvestAndStatusList(String investOid, List<String> statuses);
	
	
	@Query(value = "SELECT planType FROM t_plan_product_entity WHERE oid = ?1", nativeQuery = true)
	public String findPlanTypeByOid(String oid);
	
	@Query(value = "SELECT * FROM t_plan_product_entity WHERE status = ?1", nativeQuery = true)
	public List<PlanProductEntity> queryInvestByStatus(String status);

	@Query(value = "SELECT SUM(t1.incomeAmount),t1.confirmDate,t1.createTime "
			+ "  FROM t_money_publisher_investor_income t1 ,t_plan_product_entity t2 ,t_plan_invest t3 WHERE "
			+ "  t3.monthOid=?1 AND t3.oid=t2.planOid AND t2.oid = t1.wishplanOid  AND t2.status<> 'failure' "
			+ " GROUP BY t1.confirmDate ORDER BY t1.createTime asc",nativeQuery = true)
	public List<Object[]> calcuIncomeByPlanOid(String planOid);

	@Query(value="SELECT SUM(t1.incomeAmount),MAX(t1.confirmDate),MAX(t1.createTime) "
			+ " FROM t_money_publisher_investor_income t1 ,t_plan_product_entity t2,t_plan_invest t3"
			+ "  WHERE t2.oid = t1.wishplanOid AND t2.planOid = t3.oid and t3.oid=?1 AND t2.status<> 'failure' ",nativeQuery = true)
	public List<Object[]> calcuCompleteTotalIncome(String planOid);

	@Query(value="SELECT SUM(t1.incomeAmount),MAX(t1.confirmDate),MAX(t1.createTime) "
			+ "  FROM t_money_publisher_investor_income t1 ,t_plan_product_entity t2 ,t_plan_invest t3"
			+ "  WHERE t3.monthOid=?1 AND t3.oid=t2.planOid AND t2.oid = t1.wishplanOid  AND t2.status<> 'failure'  ",nativeQuery = true)
	public List<Object[]> calcuCompleteMonthTotalIncome(String planOid);
	
	@Query(value="select * from t_plan_product_entity t where t.planOid =?1 and t.status<>'failure' order by t.createTime asc",nativeQuery = true)
	public List<PlanProductEntity> findRelateProduct(String planOid);
	@Query(value="select * from t_plan_product_entity t where t.planOid = ?1 and t.status='complete' order by t.createTime desc limit 1",nativeQuery = true)
	public PlanProductEntity findLatestByplanOid(String planOid);



	

	
}
