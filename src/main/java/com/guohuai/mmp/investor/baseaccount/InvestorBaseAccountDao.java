package com.guohuai.mmp.investor.baseaccount;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface InvestorBaseAccountDao extends JpaRepository<InvestorBaseAccountEntity, String>, JpaSpecificationExecutor<InvestorBaseAccountEntity> {
	
	
	
	/**
	 * 判断余额是否足够 
	 */
	@Modifying
	@Query(value = "update T_MONEY_INVESTOR_BASEACCOUNT set updateTime = sysdate() where oid = ?2 and balance >= ?1", nativeQuery = true)
	public int balanceEnough(BigDecimal orderAmount, String oid);

	@Query("UPDATE InvestorBaseAccountEntity  SET balance = ?2 WHERE oid = ?1")
	@Modifying
	public int updateBalance(String baseAccountOid, BigDecimal balance);
	
	
	@Query("UPDATE InvestorBaseAccountEntity  SET balance = ?2, rechargeFrozenBalance = ?3, withdrawFrozenBalance = ?4, withdrawAvailableBalance = ?5, applyAvailableBalance = ?6 WHERE oid = ?1")
	@Modifying
	public int updateBalance(String baseAccountOid, 
			BigDecimal balance, BigDecimal rechargeFrozenBalance, 
			BigDecimal withdrawFrozenBalance, BigDecimal withdrawAvailableBalance, BigDecimal applyAvailableBalance);
	
	/**
	 * 提现回调
	 */
	@Query("UPDATE InvestorBaseAccountEntity  SET onWayBalance = onWayBalance - ?2 WHERE oid = ?1 and onWayBalance >= ?2 ")
	@Modifying
	public int update4WithdrawCBBalance(String baseAccountOid, BigDecimal amount);
	
	/**
	 * 提现
	 */
	@Query("UPDATE InvestorBaseAccountEntity  SET balance = balance - ?2, onWayBalance = onWayBalance + ?2 WHERE oid = ?1 and balance >= ?2 ")
	@Modifying
	public int update4WithdrawBalance(String baseAccountOid, BigDecimal amount);
	
	/**
	 * 充值
	 */
	@Query("UPDATE InvestorBaseAccountEntity  SET balance = balance + ?2, onWayBalance = onWayBalance - ?2 WHERE oid = ?1 and onWayBalance >= ?2 ")
	@Modifying
	public int update4WriteOffBalance(String baseAccountOid, BigDecimal amount);
	
	public List<InvestorBaseAccountEntity> findByMemberIdIn(String[] memberIds);
		
	public InvestorBaseAccountEntity findByMemberId(String memberId);
	
	public InvestorBaseAccountEntity findByOwner(String owner);

	public InvestorBaseAccountEntity findByPhoneNum(String phoneNum);
	
	public InvestorBaseAccountEntity findByUid(String uid);
	
	@Query("UPDATE InvestorBaseAccountEntity  SET balance = balance + ?1 WHERE owner = 'platform' and status = 'normal' ")
	@Modifying
	public int borrowFromPlatform(BigDecimal amount);
	
	@Query("UPDATE InvestorBaseAccountEntity  SET balance = balance - ?1 WHERE owner = 'platform' and status = 'normal' and balance >= ?1 ")
	@Modifying
	public int payToPlatform(BigDecimal amount);
	
	@Query(value = "select * from T_MONEY_INVESTOR_BASEACCOUNT where oid > ?1 and owner = 'investor' order by oid limit 2000", nativeQuery = true)
	public List<InvestorBaseAccountEntity> query4Bonus(String lastOid);
	
	@Query("UPDATE InvestorBaseAccountEntity  SET isFreshman = 'no' WHERE oid = ?1 and isFreshman = 'yes' ")
	@Modifying
	public int updateFreshman(String oid);
	
	@Query(value = "select * from T_MONEY_INVESTOR_BASEACCOUNT where owner = 'investor' and oid > ?1 order by oid limit 2000 ", nativeQuery = true)
	public List<InvestorBaseAccountEntity> findAll(String lastOid);

	@Query(value = "SELECT * from T_MONEY_INVESTOR_BASEACCOUNT WHERE createTime < ?1", nativeQuery = true)
	public List<InvestorBaseAccountEntity> getAccByCreateTime(String createTime);

	@Query(value = "SELECT * from T_MONEY_INVESTOR_BASEACCOUNT WHERE oid != 'superaccount' ", nativeQuery = true)
	public List<InvestorBaseAccountEntity> getInvestorsNotSuperaccount();

	@Query(value = "select count(*) from T_MONEY_INVESTOR_BASEACCOUNT WHERE realName IS NOT NULL AND realName !=''", nativeQuery = true)
	public long getAllVerifiedCount();
	
	@Query("UPDATE InvestorBaseAccountEntity  SET status = 'forbidden' WHERE oid = ?1 ")
	@Modifying
	public int forbiddenUser(String investorOid);
	
	@Query("UPDATE InvestorBaseAccountEntity  SET status = 'normal' WHERE oid = ?1 ")
	@Modifying
	public int normalUser(String investorOid);

	public InvestorBaseAccountEntity findByIdNum(String idCard);
	/**通过用户id来查询*/
	public InvestorBaseAccountEntity findByOid(String userId);
	
	
	@Query(value = "SELECT phoneNum from T_MONEY_INVESTOR_BASEACCOUNT WHERE oid = ?1", nativeQuery = true)
	public String findPhoneByOid(String oid);
	
	
	/**  更新子账户的交易密码 *//*
	@Query(value="UPDATE T_MONEY_INVESTOR_BASEACCOUNT SET payPwd = ?2 , paySalt = ?3 WHERE oid =?1",nativeQuery = true)
	@Modifying
	public int updatepayPwd(String investorOid,String payPwd,String paySalt);
*/
	
}
