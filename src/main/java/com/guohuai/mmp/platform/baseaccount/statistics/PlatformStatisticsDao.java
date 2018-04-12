package com.guohuai.mmp.platform.baseaccount.statistics;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.guohuai.mmp.platform.baseaccount.PlatformBaseAccountEntity;

public interface PlatformStatisticsDao extends JpaRepository<PlatformStatisticsEntity, String>, JpaSpecificationExecutor<PlatformStatisticsEntity> {

	PlatformStatisticsEntity findByPlatformBaseAccount(PlatformBaseAccountEntity baseAccount);
	
	/**
	 * 投资人充值回调
	 */
	@Query(value = "update PlatformStatisticsEntity set totalTradeAmount = totalTradeAmount + ?2, "
			+ "investorTotalDepositAmount = investorTotalDepositAmount + ?2 where oid = ?1")
	@Modifying
	int updateStatistics4InvestorDeposit(String oid, BigDecimal orderAmount);
	
	/**
	 * 发行人充值回调
	 */
	@Query(value = "update PlatformStatisticsEntity set totalTradeAmount = totalTradeAmount + ?2, "
			+ "publisherTotalDepositAmount = publisherTotalDepositAmount + ?2 where oid = ?1")
	@Modifying
	int updateStatistics4PublisherDeposit(String oid, BigDecimal orderAmount);
	
	/**
	 * 投资人提现回调
	 */
	@Query(value = "update PlatformStatisticsEntity set totalTradeAmount = totalTradeAmount + ?2, "
			+ "investorTotalWithdrawAmount = investorTotalWithdrawAmount + ?2 where oid = ?1")
	@Modifying
	int updateStatistics4InvestorWithdraw(String oid, BigDecimal orderAmount);
	
	
	@Query(value = "update PlatformStatisticsEntity set totalTradeAmount = totalTradeAmount - ?2, "
			+ "investorTotalWithdrawAmount = investorTotalWithdrawAmount - ?2 where oid = ?1")
	@Modifying
	int updateStatistics4InvestorWithdrawWriteOff(String oid, BigDecimal orderAmount);
	
	
	/**
	 * 发行人提现回调
	 * @param oid
	 * @param orderAmount
	 * @return
	 */
	@Query(value = "update PlatformStatisticsEntity set totalTradeAmount = totalTradeAmount + ?2, "
			+ "publisherTotalWithdrawAmount = publisherTotalWithdrawAmount + ?2 where oid = ?1")
	@Modifying
	int updateStatistics4PublisherWithdraw(String oid, BigDecimal orderAmount);
	
	/**
	 * 用户注册
	 * 增加平台注册人数
	 */
	@Query(value = "update PlatformStatisticsEntity set registerAmount = registerAmount + 1 where oid = ?1")
	@Modifying
	int increaseRegisterAmount(String oid);
	
	/**
	 * 投资人实名认证
	 * 增加平台实名人数
	 */
	@Query(value = "update PlatformStatisticsEntity set verifiedInvestorAmount = verifiedInvestorAmount + 1 where oid = ?1")
	@Modifying
	int increaseVerifiedInvestorAmount(String oid);
	
	/**
	 * 投资单份额确认:累计交易额、累计借款额
	 */
	@Query(value = "update PlatformStatisticsEntity set totalTradeAmount = totalTradeAmount + ?2, "
			+ " totalLoanAmount = totalLoanAmount + ?2 where oid = ?1")
	@Modifying
	int updateStatistics4InvestConfirm(String oid, BigDecimal orderAmount);
	
	/**
	 * 赎回单份额确认:累计交易额、累计还款额
	 */
	@Query(value = "update PlatformStatisticsEntity set totalTradeAmount = totalTradeAmount + ?2, "
			+ " totalReturnAmount = totalReturnAmount + ?2 where oid = ?1")
	@Modifying
	int updateStatistics4RedeemConfirm(String oid, BigDecimal orderAmount);
	
	/**
	 * 累计付息总额
	 */
	@Query(value = "update PlatformStatisticsEntity set "
			+ " totalInterestAmount = totalInterestAmount + ?2 where oid = ?1")
	@Modifying
	int updateStatistics4TotalInterestAmount(String oid, BigDecimal income);
	
	
//	/**
//	 * 发行产品数
//	 */
//	@Query("UPDATE PlatformStatisticsEntity  SET productAmount = productAmount + 1 WHERE oid = ?1")
//	@Modifying
//	public int increaseReleasedProductAmount(String oid);
//	
//	/**
//	 * 在售
//	 */
//	@Query("UPDATE PlatformStatisticsEntity  SET onSaleProductAmount = onSaleProductAmount + 1 WHERE oid = ?1")
//	@Modifying
//	public int increaseOnSaleProductAmount(String oid);
	
	
//	/**
//	 * 封闭期
//	 */
//	@Query("UPDATE PlatformStatisticsEntity  SET sealProductAmount = sealProductAmount + 1 WHERE oid = ?1")
//	@Modifying
//	public int increaseSealProductAmount(String oid);
	
	
	
//	/**
//	 * 待结算
//	 */
//	@Query("UPDATE PlatformStatisticsEntity  SET onSaleProductAmount = onSaleProductAmount - 1, "
//			+ "toCloseProductAmount = toCloseProductAmount + 1 WHERE oid = ?1")
//	@Modifying
//	public int increaseToCloseProductAmount(String oid);
//	
//	/**
//	 * 已结算
//	 */
//	@Query("UPDATE PlatformStatisticsEntity  SET toCloseProductAmount = toCloseProductAmount - 1, "
//			+ "closedProductAmount = closedProductAmount + 1 WHERE oid = ?1")
//	@Modifying
//	public int increaseClosedProductAmount(String oid);

	
	/**
	 * 逾期次数
	 */
	@Query("UPDATE PlatformStatisticsEntity  SET overdueTimes = overdueTimes + ?2 "
			+ " WHERE oid = ?1")
	@Modifying
	int increaseOverdueTimes(String oid, int overdueTimes);
	
	/**
	 * 发行人数
	 */
	@Query("UPDATE PlatformStatisticsEntity  SET publisherAmount = publisherAmount + 1 "
			+ " WHERE oid = ?1")
	@Modifying
	int increasePublisherAmount(String oid);
	
	@Query(value=" SELECT platformOid,totalTradeAmount,totalReturnAmount,oid FROM T_MONEY_PLATFORM_STATISTICS where oid > ?1 ", nativeQuery = true)
	List<Object[]> getPlatformStatisticsByBatch(String lastOid);
}
