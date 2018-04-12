package com.guohuai.mmp.investor.bankorder;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface InvestorBankOrderDao
		extends JpaRepository<InvestorBankOrderEntity, String>, JpaSpecificationExecutor<InvestorBankOrderEntity> {

	/**
	 * 根据<<订单号>>查询<<投资人-银行委托单>>
	 * 
	 * @param orderCode
	 * @return
	 */
	public InvestorBankOrderEntity findByOrderCodeAndOrderStatusAndOrderType(String orgCode, String orderStatus,
			String orderType);

	public InvestorBankOrderEntity findByOrderCode(String orderCode);

	// 获取用户充值对账金额
	@Query(value = "SELECT IFNULL(SUM(t.orderAmount), 0) FROM T_MONEY_INVESTOR_BANKORDER t "
			+ " WHERE t.orderTime > ?1 " + " AND t.orderTime < ?2 " + " AND t.orderType = ?3 "
			+ " AND t.orderStatus = ?4 " + " AND t.investorOid = ?5 ", nativeQuery = true)
	public BigDecimal getCheckBankOrder4Acc(String checkTimeStart, String checkTimeEnd, String orderType,
			String orderStatus, String investOid);

	// 提现申请中
	@Query(value = "SELECT IFNULL(SUM(t.orderAmount), 0) FROM T_MONEY_INVESTOR_BANKORDER t "
			+ " WHERE t.orderTime > ?1 " + " AND t.orderTime < ?2 " + " AND t.orderType = 'withdraw' "
			+ " AND t.orderStatus NOT IN ('submitFailed', 'payFailed', 'done') "
			+ " AND t.investorOid = ?3 ", nativeQuery = true)
	public BigDecimal getWithdrawApplyAmt4Acc(String checkTimeStart, String checkTimeEnd, String investOid);

	// 提现金额，包括申请中和已完成
	@Query(value = "SELECT IFNULL(SUM(t.orderAmount), 0) FROM T_MONEY_INVESTOR_BANKORDER t "
			+ " WHERE t.orderTime > ?1 " + " AND t.orderTime < ?2 " + " AND t.orderType = 'withdraw' "
			+ " AND t.orderStatus NOT IN ('submitFailed', 'payFailed') "
			+ " AND t.investorOid = ?3 ", nativeQuery = true)
	public BigDecimal getWithdrawAmt4Acc(String checkTimeStart, String checkTimeEnd, String investOid);

	// 提现手续费
	@Query(value = "SELECT IFNULL(SUM(t.fee), 0) FROM T_MONEY_INVESTOR_BANKORDER t " + " WHERE t.orderTime > ?1 "
			+ " AND t.orderTime < ?2 " + " AND t.orderType = 'withdraw' "
			+ " AND t.orderStatus IN ('submitted', 'toPay', 'done') " + " AND t.feePayer = 'user') "
			+ " AND t.investorOid = ?3 ", nativeQuery = true)
	public BigDecimal getFeeAmt4Acc(String checkTimeStart, String checkTimeEnd, String investOid);

	// 作废订单
	@Query(value = "UPDATE T_MONEY_INVESTOR_BANKORDER SET orderStatus='abandoned',updateTime=now()  where orderCode=?1", nativeQuery = true)
	@Modifying
	public int updateBankOrderToAbandonedByOrderCode(String orderCode);

	// 提现 充值(作废)
	@Query(value = "SELECT a.orderCode,a.orderStatus,a.orderType,"
			+ "IF(a.orderType='withdraw' AND feePayer='user',a.orderAmount-a.fee,a.orderAmount) as orderAmount,"
			+ "b.memberId,a.orderTime,'' as productType "
			+ "FROM T_MONEY_INVESTOR_BANKORDER a,T_MONEY_INVESTOR_BASEACCOUNT b "
			+ "WHERE a.investorOid=b.oid AND a.orderTime BETWEEN ?1 AND ?2 "
			+ "AND a.orderCode > ?3 AND a.orderStatus IN ('toPay','payFailed','done') "
			+ "AND a.orderType IN('deposit','withdraw','redEnvelope') " 
			+ "ORDER BY a.orderCode LIMIT 2000 ", nativeQuery = true)
	public List<Object[]> findBankOrderByOrderTime(String beginTime, String endTime, String orderBankCode);

	@Query(value = "SELECT SUM(a.orderAmount),MAX(a.completeTime) "
			+ "FROM T_MONEY_INVESTOR_BANKORDER a "
			+ "WHERE a.orderStatus = 'done' and a.orderType in ('deposit','depositLong')", nativeQuery = true)
	public List<Object[]> getInvestorDepositAmount();
	@Query(value = "SELECT SUM(a.orderAmount),MAX(a.completeTime) "
			+ "FROM T_MONEY_INVESTOR_BANKORDER a "
			+ "WHERE a.orderStatus = 'done' and a.orderType in ('withdraw','withdrawLong')", nativeQuery = true)
	public List<Object[]> getInvestorWithdrawAmount();
	
	@Query(value = "update T_MONEY_INVESTOR_BANKORDER set frozenStatus = 'toIceOut', payStatus = 'toPay', iceOutTime = ?2 where oid = ?1 and frozenStatus in ('frozened') and payStatus = 'noPay' and orderStatus = 'toPay' ", nativeQuery = true)
	@Modifying
	public int updatePassFrozenStatusToIceOut(String bankOrderOid, Timestamp iceOutTime);
	
	@Query(value = "update T_MONEY_INVESTOR_BANKORDER set frozenStatus = 'toIceOut', iceOutTime = ?2 where oid = ?1 and frozenStatus in ('frozened') and payStatus = 'noPay' and orderStatus = 'toPay' ", nativeQuery = true)
	@Modifying
	public int updateFrozenStatusToIceOut(String bankOrderOid, Timestamp iceOutTime);
	
	
	@Query(value = "update T_MONEY_INVESTOR_BANKORDER set frozenStatus = 'iceOut'  where oid = ?1 and frozenStatus in ('toIceOut') ", nativeQuery = true)
	@Modifying
	public int updateFrozenStatusIceOut(String bankOrderOid);
	
	/**查询主账户转入到所有子账户的累计金额*/
	@Query(value = "select COUNT(*) , IFNULL(sum(t.orderAmount),0)from T_MONEY_INVESTOR_BANKORDER t where t.investorOid = ?1 and t.orderType = 'rollOut' and orderStatus = 'paySuccess'" ,nativeQuery = true)
	public List<Object[]> queryTransferToSonAccount(String investorOid);
	
	/**查询当前子账户向主账户的转账累计金额*/
	@Query(value = "select count(*) , IFNULL(sum(t.orderAmount),0) from T_MONEY_INVESTOR_BANKORDER t where t.investorOid =? and t.orderType = 'rollIn' and orderStatus = 'paySuccess'",nativeQuery = true)
	public List<Object[]> queryTransferFromPAccount(String investorOid);
}
