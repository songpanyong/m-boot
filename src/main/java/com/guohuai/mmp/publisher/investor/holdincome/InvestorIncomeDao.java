package com.guohuai.mmp.publisher.investor.holdincome;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface InvestorIncomeDao extends JpaRepository<InvestorIncomeEntity, String>, JpaSpecificationExecutor<InvestorIncomeEntity>{
	
	@Query(value = "select * from T_MONEY_PUBLISHER_INVESTOR_HOLDINCOME where investorOid = ?1 and confirmDate = ?2", nativeQuery = true)
	List<InvestorIncomeEntity> findByInvestorOidAndConfirmDate(String investorOid, String incomeDate);
	

	
//	@Query(value = "select * from T_MONEY_PUBLISHER_INVESTOR_HOLDINCOME where investorOid =  ?1 "
//			+ " and date_format(confirmDate, '%Y%m') = ?2 order by confirmDate", nativeQuery = true)
	@Query(value = "select * from T_MONEY_PUBLISHER_INVESTOR_INCOME where investorOid =  ?1 "
			+ " and date_format(confirmDate, '%Y%m') = ?2 order by confirmDate", nativeQuery = true)
	List<InvestorIncomeEntity> queryIncomeByYearMonth(String investorOid, String yearMonth);

	/**
	 * 获取用户收益金额
	 * @param checkTimeStart
	 * @param checkTimeEnd
	 * @param investorOid
	 * @param productType
	 * @return
	 */
	@Query(value = "SELECT IFNULL(SUM(a.incomeAmount), 0) FROM T_MONEY_PUBLISHER_INVESTOR_HOLDINCOME a, "
			+ " T_GAM_PRODUCT b "
			+ " WHERE a.productOid = b.oid "
			+ " AND a.confirmDate > ?1 "
			+ " AND a.confirmDate < ?2 "
			+ " AND a.investorOid = ?3 "
			+ " AND b.type = ?4 ", nativeQuery = true)
	public BigDecimal getCheckIncomeOfAcc(String checkTimeStart, String checkTimeEnd, String investorOid, String productType);


	@Query(value = "select oid from T_MONEY_PUBLISHER_INVESTOR_HOLDINCOME where oid > ?1 and isClosedToBalance = 'toClose' order by oid limit 2000", nativeQuery = true)
	List<String> queryToCloseHoldIncome(String lastOid);


	@Query(value="select * from  T_MONEY_PUBLISHER_INVESTOR_HOLDINCOME t where t.wishplanOid=?1",nativeQuery = true)
	List<InvestorIncomeEntity> findBywishplanOid(String oid);



	/** 查询非薪增长计划在已经结清下的收益和 */
	@Query(value="select  IFNULL(SUM(t.incomeAmount), 0),confirmDate,createTime from T_MONEY_PUBLISHER_INVESTOR_HOLDINCOME t where t.wishplanOid = ?1 group by t.productOid order by t.createTime desc" ,nativeQuery = true)
	 public List<Object[]> findNotSalaryBywishplanOid(String oid);
}
