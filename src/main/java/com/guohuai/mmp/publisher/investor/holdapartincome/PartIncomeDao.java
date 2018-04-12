package com.guohuai.mmp.publisher.investor.holdapartincome;

import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface PartIncomeDao extends JpaRepository<PartIncomeEntity, String>, JpaSpecificationExecutor<PartIncomeEntity>{

	/**
	 * 按计息日，产品统计分仓合计所得总收益，计息总份额
	 * @param productOid 产品oid
	 * @param incomeDate 收益日 
	 * @return
	 */
	@Query(value = "SELECT SUM(incomeAmount) AS totalAmount, holdOid FROM T_MONEY_PUBLISHER_INVESTOR_INCOME WHERE productOid =?1 AND confirmDate =?2 GROUP BY holdOid", nativeQuery = true)
	public List<Object[]> calcSumInterest(String productOid, Date incomeDate);
	
	@Query(value = "FROM PartIncomeEntity WHERE product.oid =?1 AND confirmDate =?2", nativeQuery = true)
	public List<PartIncomeEntity> getByPoidAndConfirmDate(String productOid, Date confirmDate);
	
	@Query(value = "select * from T_MONEY_PUBLISHER_INVESTOR_INCOME where investorOid = ?1 and productOid = ?2 and rewardRuleOid = ?3 and confirmDate = ?4", nativeQuery = true)
	public List<PartIncomeEntity> findApartIncomeByRewardOid(String investorOid, String productOid, String rewardRuleOid, String incomeDate);
	
	@Query(value = "select t1.* from T_MONEY_PUBLISHER_INVESTOR_INCOME t1, T_MONEY_INVESTOR_TRADEORDER t2"
			+ " where t1.orderOid = t2.oid and t1.investorOid = ?1 and t2.orderCode = ?2 and confirmDate = ?3", nativeQuery = true)
	public PartIncomeEntity queryApartIncome(String investorOid, String tradeOrderOid, String incomeDate);
	
	/** 我的产品某阶段收益明细信息（持有份额，投资份额，创建时间，单位净值，升档日期天数） */
	@Query(value = " SELECT C.holdVolume,C.investVolume,C.createTime,D.netUnitShare,E.startDate,E.oid "
					+" FROM T_MONEY_INVESTOR_BASEACCOUNT A  "
					+" INNER JOIN T_MONEY_PUBLISHER_INVESTOR_INCOME B ON A.oid = B.investorOid  "
					+" INNER JOIN T_MONEY_PUBLISHER_HOLDAPART C ON B.holdApartOid=C.oid "
					+" INNER JOIN T_GAM_PRODUCT D ON B.productOid=D.oid "
					+" INNER JOIN T_GAM_INCOME_REWARD E ON B.rewardRuleOid=E.oid "
					+" WHERE A.oid=?1 AND B.productOid=?2 "
					+" AND B.confirmDate = ( "
					+" SELECT MAX(F2.confirmDate)   "
					+" 	 FROM T_MONEY_INVESTOR_BASEACCOUNT F1   "
					+" 	 INNER JOIN T_MONEY_PUBLISHER_INVESTOR_LEVELINCOME F2 ON F1.oid=F2.investorOid   "
					+" 	 WHERE F1.oid = ?1 AND F2.productOid = ?2 "
				    +" )  "
					+" AND E.level=?3 "
					+" ORDER BY C.createTime DESC "
					+ " limit ?4,?5 ", nativeQuery = true)
	public List<Object[]> queryHoldApartIncomeAndLevel(String investorOid, String productOid, String level,int startLine, int endLine);
	
	/** 查询某奖励阶段的下一阶段开始时间 */
	@Query(value = " SELECT A.startDate " //
			+ " FROM T_GAM_INCOME_REWARD A "//
			+ " WHERE A.productOid=?1 "//
			+ " AND A.startDate>( "//
			+ "  SELECT startDate FROM T_GAM_INCOME_REWARD WHERE oid=?2) "
			+ " ORDER BY A.startDate ASC LIMIT 1 ", nativeQuery = true)
	public String queryNextLevelStartDate(String productOid, String rewardOid);
	
	/** 我的产品某阶段收益明细信息（持有份额，投资份额，创建时间，单位净值，升档日期天数） */
	@Query(value = " SELECT COUNT(*) "
					+" FROM T_MONEY_INVESTOR_BASEACCOUNT A  "
					+" INNER JOIN T_MONEY_PUBLISHER_INVESTOR_INCOME B ON A.oid = B.investorOid  "
					+" INNER JOIN T_MONEY_PUBLISHER_HOLDAPART C ON B.holdApartOid=C.oid "
					+" INNER JOIN T_GAM_PRODUCT D ON B.productOid=D.oid "
					+" INNER JOIN T_GAM_INCOME_REWARD E ON B.rewardRuleOid=E.oid "
					+" WHERE A.oid=?1 AND B.productOid=?2 "
					+" AND B.confirmDate = ( "
					+" SELECT MAX(F2.confirmDate)   "
					+" 	 FROM T_MONEY_INVESTOR_BASEACCOUNT F1   "
					+" 	 INNER JOIN T_MONEY_PUBLISHER_INVESTOR_LEVELINCOME F2 ON F1.oid=F2.investorOid   "
					+" 	 WHERE F1.oid = ?1 AND F2.productOid = ?2 "
				    +" )  "
					+" AND E.level=?3 ", nativeQuery = true)
	public int counntHoldApartIncomeAndLevel(String investorOid, String productOid, String level);
	
	@Query(value="select t2.oid,t2.code,t2.name,SUM(t1.incomeAmount),SUM(t1.baseAmount),"
			+ " SUM(t1.holdVolume),SUM(t1.rewardAmount), t1.confirmDate ,t1.createTime FROM t_money_publisher_investor_income t1,"
			+ " t_gam_product t2 WHERE t1.productOid = t2.oid AND  t1.investorOid = ?1 AND "
			+ " t1.wishplanOid IS NULL  GROUP BY t1.confirmDate,t1.holdOid ORDER BY t1.confirmDate DESC ",nativeQuery = true)
	public List<Object[]> findInvestorIncome(String investorOid);
}
