package com.guohuai.mmp.investor.baseaccount.statistics;

import java.math.BigDecimal;
import java.sql.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountEntity;

public interface InvestorStatisticsDao extends JpaRepository<InvestorStatisticsEntity, String>, JpaSpecificationExecutor<InvestorStatisticsEntity> {
	
	
	/**
	 * 充值
	 */
	@Query("UPDATE InvestorStatisticsEntity a SET a.totalDepositAmount = a.totalDepositAmount + ?2,"
			+ " a.totalDepositCount = a.totalDepositCount + 1,"
			+ " a.todayDepositCount = a.todayDepositCount + 1,"
			+ " a.todayDepositAmount = a.todayDepositAmount + ?2 "
			+ " WHERE a.investorBaseAccount = ?1")
	@Modifying
	public int updateStatistics4Deposit(InvestorBaseAccountEntity baseAccount, BigDecimal orderAmount);
	
	@Query("UPDATE InvestorStatisticsEntity a SET a.totalDepositAmount = a.totalDepositAmount - ?2,"
			+ " a.totalDepositCount = a.totalDepositCount - 1 "
			+ " WHERE a.investorBaseAccount = ?1")
	@Modifying
	public int updateStatistics4DepositOk2Fail(InvestorBaseAccountEntity baseAccount, BigDecimal orderAmount);
	
	/**
	 * 投资人提现回调
	 */
	@Query("UPDATE InvestorStatisticsEntity  SET totalWithdrawAmount = totalWithdrawAmount + ?2,"
			+ " totalWithdrawCount = totalWithdrawCount + 1,"
			+ " todayWithdrawCount = todayWithdrawCount + 1,"
			+ " todayWithdrawAmount = todayWithdrawAmount + ?2 "
			+ "  WHERE investorBaseAccount = ?1")
	@Modifying
	public int updateStatistics4Withdraw(InvestorBaseAccountEntity baseAccount, BigDecimal orderAmount);
	/**
	 * 投资人提现成功改失败
	 */
	@Query("UPDATE InvestorStatisticsEntity  SET totalWithdrawAmount = totalWithdrawAmount - ?2,"
			+ " totalWithdrawCount = totalWithdrawCount - 1"
			+ "  WHERE investorBaseAccount = ?1")
	@Modifying
	public int updateStatistics4WithdrawOK2Fail(InvestorBaseAccountEntity baseAccount, BigDecimal orderAmount);
	
	
	/**
	 * 投资人提现
	 */
	@Query("UPDATE InvestorStatisticsEntity a SET a.monthWithdrawCount = a.monthWithdrawCount + 1  WHERE a.investorBaseAccount = ?1")
	@Modifying
	public int increaseMonthWithdrawCount(InvestorBaseAccountEntity baseAccount);
	
	
	/**
	 * 冲销单
	 */
	@Query("UPDATE InvestorStatisticsEntity a SET a.monthWithdrawCount = a.monthWithdrawCount - 1  WHERE a.investorBaseAccount = ?1 and a.monthWithdrawCount > 0")
	@Modifying
	public int decreaseMonthWithdrawCount(InvestorBaseAccountEntity investorBaseAccount);
	
	
	/**
	 * 活期--赎回
	 */
	@Query("UPDATE InvestorStatisticsEntity a SET a.totalRedeemAmount = a.totalRedeemAmount + ?2,"
			+ " a.totalRedeemCount = a.totalRedeemCount + 1,"
			+ " a.todayRedeemCount = a.todayRedeemCount + 1,"
			+ " a.todayRedeemAmount = a.todayRedeemAmount + ?2,"
			+ " a.t0CapitalAmount = a.t0CapitalAmount - ?2" 
			+ " WHERE a.investorBaseAccount = ?1")
	@Modifying
	public int updateStatistics4T0Redeem(InvestorBaseAccountEntity baseAccount, BigDecimal orderAmount);
	
	/**
	 * 定期--还本
	 * @param oid
	 * @param orderAmount
	 * @return
	 */
	@Query("UPDATE InvestorStatisticsEntity  SET totalRepayLoan = totalRepayLoan + ?2, "
			+ " tnCapitalAmount = tnCapitalAmount - ?2 WHERE investorBaseAccount = ?1")
	@Modifying
	public int repayStatistics(InvestorBaseAccountEntity baseAccount, BigDecimal orderAmount);
	
	/**
	 * 定期--付息
	 * @param oid
	 * @param orderAmount
	 * @return
	 */
	@Query("UPDATE InvestorStatisticsEntity SET tnCapitalAmount = tnCapitalAmount - ?2 WHERE investorBaseAccount = ?1")
	@Modifying
	public int updateStatistics4TnRepayInterest(InvestorBaseAccountEntity baseAccount, BigDecimal orderAmount);
	
	/**
	 * 活期投资
	 */
	@Query("UPDATE InvestorStatisticsEntity  SET totalInvestAmount = totalInvestAmount + ?2, "
			+ " totalInvestCount =  totalInvestCount + 1, "
			+ " todayInvestCount =  todayInvestCount + 1, "
			+ " todayInvestAmount =  todayInvestAmount + ?2, "
			+ " t0CapitalAmount =  t0CapitalAmount + ?2 WHERE  investorBaseAccount = ?1")
	@Modifying
	public int updateStatistics4T0Invest(InvestorBaseAccountEntity baseAccount, BigDecimal orderAmount);
	
	/**
	 * 定期投资
	 */
	@Query("UPDATE InvestorStatisticsEntity SET totalInvestAmount = totalInvestAmount + ?2, "
			+ " totalInvestCount = totalInvestCount + 1, "
			+ " todayInvestCount = todayInvestCount + 1, "
			+ " todayInvestAmount = todayInvestAmount + ?2, "
			+ " tnCapitalAmount = tnCapitalAmount + ?2 WHERE investorBaseAccount = ?1")
	@Modifying
	public int updateStatistics4TnInvest(InvestorBaseAccountEntity baseAccount, BigDecimal orderAmount);
	

	public InvestorStatisticsEntity findByInvestorBaseAccount(InvestorBaseAccountEntity baseAccount);

	
	@Query(value = "update InvestorStatisticsEntity set updateTime = sysdate() where monthWithdrawCount <= ?2 and investorBaseAccount = ?1")
	@Modifying
	public int isFree(InvestorBaseAccountEntity baseAccount, int freeTimes);
	
	
	/**
	 * totalIncomeAmount 累计收益总额
	 * t0YesterdayIncome 活期昨日收益额
	 * t0TotalIncome 活期总收益额
	 * t0CapitalAmount 活期资产总额
	 */
	@Query(value = "UPDATE InvestorStatisticsEntity set totalIncomeAmount = totalIncomeAmount + ?2 + ?3, t0YesterdayIncome = ?2 +?3,"
			+ " t0TotalIncome = t0TotalIncome + ?2 + ?3, t0CapitalAmount = t0CapitalAmount + ?2 + ?3, incomeConfirmDate = ?4 where investorBaseAccount = ?1")
	@Modifying
	public int interestStatistics(InvestorBaseAccountEntity investorBaseAccount, BigDecimal holdIncomeAmount, BigDecimal holdLockIncomeAmount, Date incomeDate);
	
	/**
	 * totalIncomeAmount 累计收益总额
	 * tnTotalIncome 定期总收益额
	 */
	@Query(value = "UPDATE InvestorStatisticsEntity set tnCapitalAmount = tnCapitalAmount + ?2 + ?3, totalIncomeAmount = totalIncomeAmount + ?2 + ?3, tnTotalIncome = tnTotalIncome + ?2 + ?3,"
			+ " incomeConfirmDate = ?4 where investorBaseAccount = ?1")
	@Modifying
	public int interestStatisticsTn(InvestorBaseAccountEntity investorBaseAccount, BigDecimal holdIncomeAmount, BigDecimal holdLockIncomeAmount, Date incomeDate);
	
	
	@Query(value = "UPDATE InvestorStatisticsEntity set todayDepositCount = 0, todayWithdrawCount = 0, "
			+ "todayInvestCount = 0, todayRedeemCount = 0,"
			+ "todayDepositAmount = 0, todayWithdrawAmount = 0, todayInvestAmount = 0, todayRedeemAmount = 0, t0YesterdayIncome = 0")
	@Modifying
	public int resetToday();
	
	@Query("UPDATE InvestorStatisticsEntity SET monthWithdrawCount = 0 ")
	@Modifying
	public int resetMonth();

	
	
}
