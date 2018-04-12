package com.guohuai.mmp.publisher.hold;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.guohuai.ams.portfolio.entity.PortfolioEntity;
import com.guohuai.ams.product.Product;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountEntity;
import com.guohuai.mmp.publisher.baseaccount.PublisherBaseAccountEntity;

public interface PublisherHoldDao extends JpaRepository<PublisherHoldEntity, String>, JpaSpecificationExecutor<PublisherHoldEntity> {

//	public PublisherHoldEntity findByInvestorBaseAccountAndProduct(InvestorBaseAccountEntity investorBaseAccount, Product product);
	
	public PublisherHoldEntity findByInvestorBaseAccountAndProductAndWishplanOid(InvestorBaseAccountEntity investorBaseAccount, Product product, String wishplanOid);
	
	/**
	 * 投资后更新持有人
	 * @param oid 持有人名录oid
	 * @param netUnitShare 单位净值
	 * @param volume 投资份额
	 * 增加<<总份额totalVolume>> <<待确认份额toConfirmVolume>><<累计投资份额totalInvestVolume>>
	 * 增加<<市值value>>
	 */
	@Query(value = "update PublisherHoldEntity set "
			+ " totalVolume = totalVolume + ?3, toConfirmInvestVolume = toConfirmInvestVolume + ?3,"
			+ " totalInvestVolume = totalInvestVolume + ?3, "
			+ " dayInvestVolume = dayInvestVolume + ?3, "
			+ " value = totalVolume * ?2, expectIncome = expectIncome + ?4,  expectIncomeExt = expectIncomeExt + ?5 where oid = ?1")
	@Modifying
	public int invest(String holdOid, BigDecimal netUnitShare, BigDecimal volume, BigDecimal expectIncome, BigDecimal expectIncomeExt);
	
	/**
	 * 体验金入仓 
	 */
	@Query(value = "update PublisherHoldEntity set totalInvestVolume = totalInvestVolume + ?3, dayInvestVolume = dayInvestVolume + ?3, "
			+ " totalVolume = totalVolume + ?3, holdVolume = holdVolume + ?3, expGoldVolume = expGoldVolume + ?3, "
			+ " accruableHoldVolume = accruableHoldVolume + ?3,"
			+ " value = totalVolume * ?2 where oid = ?1")
	@Modifying
	public void investGold(String oid, BigDecimal netUnitShare, BigDecimal orderVolume);
	
	/*
	@Query(value = "update PublisherHoldEntity set totalInvestVolume = totalInvestVolume + ?3, "
			+ " totalVolume = totalVolume + ?3, "
			+ " holdVolume = holdVolume + ?3, "
			+ " accruableHoldVolume = accruableHoldVolume + ?4, redeemableHoldVolume = redeemableHoldVolume + ?3,"
			+ " value = totalVolume * ?2 where oid = ?1")
	@Modifying
	public void invest4Super(String oid, BigDecimal netUnitShare, BigDecimal orderVolume, BigDecimal accruableHoldVolume);
	*/
	/**
	 * 通过注册表重新检测，增加可计息份额，减少计息锁定份额
	 * @param oid 持有人主键
	 * @param volume 份额
	 * @return
	 */
	@Query(value = "update PublisherHoldEntity set accruableHoldVolume = accruableHoldVolume + ?2  where oid = ?1 ")
	@Modifying
	public int unlockAccrual(String holdOid, BigDecimal volume);
	
	/**
	 * 赎回锁定期到了
	 * 增加可赎回份额，减少赎回锁定份额
	 * @param oid 持有人主键
	 * @param volume 份额
	 * @return
	 */
	@Query(value = "update PublisherHoldEntity set lockRedeemHoldVolume = lockRedeemHoldVolume - ?2,"
			+ " redeemableHoldVolume = redeemableHoldVolume + ?2 where oid = ?1 and lockRedeemHoldVolume - ?2 >= 0")
	@Modifying
	public int unlockRedeem(String holdOid, BigDecimal volume);
	
	@Query(value = "update T_MONEY_PUBLISHER_HOLD set totalVolume = totalVolume - ?1, "
			+ " maxHoldVolume = IF (maxHoldVolume >= ?1, maxHoldVolume - ?1, 0),"
			+ " accruableHoldVolume = IF(accruableHoldVolume >= ?1, accruableHoldVolume - ?1, 0) , "
			+ " holdVolume = holdVolume - ?1, "
			+ " redeemableHoldVolume = redeemableHoldVolume - ?1, value = totalVolume * ?2"
			+ " where investorOid = ?3 and productOid = ?4 "
			+ "and redeemableHoldVolume >= ?1 and totalVolume >= ?1 and holdVolume >= ?1", nativeQuery = true)
	@Modifying
	public int fastRedeem(BigDecimal volume,BigDecimal netUnitShare,String investorOid, String productOid);
	
	
	@Query(value = "update PublisherHoldEntity set totalVolume = totalVolume - ?1, "
			+ " toConfirmInvestVolume = toConfirmInvestVolume - ?1,"
			+ " totalInvestVolume = totalInvestVolume - ?1, "
			+ " value = totalVolume * ?2 where investorBaseAccount = ?3 and product = ?4 "
			+ " and totalVolume - ?1 >= 0 and toConfirmInvestVolume - ?1 >= 0")
	@Modifying
	public int invest4Abandon(BigDecimal volume, BigDecimal netUnitShare, InvestorBaseAccountEntity investorBaseAccount, Product product);
	
	
	@Query(value = "update PublisherHoldEntity set dayRedeemVolume = dayRedeemVolume + ?1 where investorBaseAccount = ?2 and product = ?3 and dayRedeemVolume + ?1 <= ?4")
	@Modifying
	public int redeem4DayRedeemVolume(BigDecimal orderVolume, InvestorBaseAccountEntity investorBaseAccount, Product product, BigDecimal singleDailyMaxRedeem);
	
	@Query(value = "update PublisherHoldEntity set toConfirmRedeemVolume = toConfirmRedeemVolume - ?1, redeemableHoldVolume = redeemableHoldVolume + ?1 "
			+ "where investorBaseAccount = ?2 and product = ?3 and toConfirmRedeemVolume - ?1 >= 0")
	@Modifying
	public int redeem4Refuse(BigDecimal volume, InvestorBaseAccountEntity investorBaseAccount, Product product);
	
	@Query(value = "update PublisherHoldEntity set dayRedeemVolume = dayRedeemVolume - ?1 "
			+ "where investorBaseAccount = ?2 and product = ?3 and dayRedeemVolume >= ?1")
	@Modifying
	public int redeem4RefuseOfDayRedeemVolume(BigDecimal orderVolume, InvestorBaseAccountEntity investorBaseAccount, Product product);
	
	
	@Query(value = "update PublisherHoldEntity set dayInvestVolume = dayInvestVolume - ?2 "
			+ " where oid = ?1 and dayInvestVolume >= ?2")
	@Modifying
	public int invest4AbandonOfDayInvestVolume(String holdOid, BigDecimal orderVolume);
	
	/**
	 * 付息
	 * @param interest 利息
	 * @param investorBaseAccount 投资人
	 * @param product 产品
	 * @return
	 */
	@Query(value = "update PublisherHoldEntity set lockIncome=lockIncome-?1,value = value - ?1, updateTime = sysdate() where investorOid = ?4 and product= ?5 and lockIncome-?1 >=0 and value - ?1 >=0")
	@Modifying
	public int repayInterest(BigDecimal interest,String investorOid, Product product);	
	
	/**
	 * 获取指定产品下面的所有持有人名录
	 */
	@Query(value = "select * from T_MONEY_PUBLISHER_HOLD where productOid = ?1 and holdStatus = 'holding' "
			+ " and oid > ?2 and accountType = 'INVESTOR' order by oid limit 1000", nativeQuery = true)
	public List<PublisherHoldEntity> findByProduct(String productOid, String lastOid);
	
	@Query(value = "select * from T_MONEY_PUBLISHER_HOLD where productOid = ?1 and holdStatus = 'holding' and redeemableHoldVolume > 0 "
			+ " and oid > ?3 and accountType = ?2 order by oid limit 1000", nativeQuery = true)
	public List<PublisherHoldEntity> clearingHold(String productOid, String accountType, String lastOid);
	
	@Modifying
	@Query(value = "update T_MONEY_PUBLISHER_HOLD set totalVolume = totalVolume + ?2 + ?4,"
			+ " redeemableHoldVolume = redeemableHoldVolume + ?2, "
			+ " lockRedeemHoldVolume = lockRedeemHoldVolume + ?4, "
			+ " holdVolume = holdVolume + ?2 + ?4, "
			+ " holdTotalIncome = holdTotalIncome + ?3 + ?5, holdYesterdayIncome = ?3 + ?5, "
			+ " accruableHoldVolume = accruableHoldVolume + ?2 + ?4, "
			+ " value = totalVolume * ?6, "
			+ " totalBaseIncome = totalBaseIncome + ?8, yesterdayBaseIncome = ?8,"
			+ " totalRewardIncome = totalRewardIncome + ?9, yesterdayRewardIncome = ?9, "
			+ " confirmDate = ?7 where oid = ?1", nativeQuery = true)
	public int updateHold4Interest(String holdOid, BigDecimal holdInterestVolume, BigDecimal holdInterestAmount, 
			BigDecimal holdLockIncomeVolume, BigDecimal holdLockIncomeAmount, BigDecimal netUnitAmount, 
			Date incomeDate, BigDecimal holdInterestBaseAmount, BigDecimal holdInterestRewardAmount);
	
//	@Modifying
//	@Query(value = "update T_MONEY_PUBLISHER_HOLD set value = value + ?2 + ?3, "
//			+ " totalVolume = totalVolume + ?2 + ?3, "
//			+ " holdVolume = holdVolume + ?2 + ?3, "
//			+ " holdTotalIncome = holdTotalIncome + ?2 + ?3, "
//			+ " incomeAmount = incomeAmount + ?2 + ?3,"
//			+ " confirmDate = ?4 where oid = ?1", nativeQuery = true)
	@Modifying
	@Query(value = "update T_MONEY_PUBLISHER_HOLD set totalVolume = totalVolume + ?2 + ?3,"
			+ " lockRedeemHoldVolume = lockRedeemHoldVolume + ?3, "
			+ " holdVolume = holdVolume + ?2 + ?3, "
			+ " holdTotalIncome = holdTotalIncome + ?2 + ?3, holdYesterdayIncome = ?2 + ?3, "
			+ " accruableHoldVolume = accruableHoldVolume + ?2 + ?3, "
			+ " value = value + ?2 + ?3, "
			+ " totalBaseIncome = totalBaseIncome + ?2 + ?3, yesterdayBaseIncome = ?2 + ?3,"
			+ " confirmDate = ?4 where oid = ?1", nativeQuery = true)
	public int updateHold4InterestTn(String holdOid, BigDecimal holdIncomeAmount, BigDecimal holdLockIncomeAmount, Date incomeDate);

	
	
	@Query(value = "select * from T_MONEY_PUBLISHER_HOLD where productOid = ?1 and holdStatus = ?2 and oid > ?3 and accountType = ?4 "
			+ " and (confirmDate < ?5 or confirmDate is null)order by oid limit 1000", nativeQuery = true)
	public List<PublisherHoldEntity> findByProductAndHoldStatus(String productOid, String holdStatus, String lastOid, String accountType, Date incomeDate);
	

	@Query("from PublisherHoldEntity e where e.portfolio = ?1 and e.publisherBaseAccount = ?2 and e.accountType='SPV'")
	public List<PublisherHoldEntity> findByPortfolioAndSPV(PortfolioEntity portfolio, PublisherBaseAccountEntity spv);
	
	/**
	 * spv赎回订单审核确定调整totalVolume
	 * @param oid
	 * @param orderAmount
	 * @return
	 */
	@Query(value = "update PublisherHoldEntity set totalVolume = totalVolume - ?2 where oid = ?1 and totalVolume-lockRedeemHoldVolume >= ?2")
	@Modifying
	public int spvOrderRedeemConfirm(String oid,BigDecimal orderAmount);
	
	/**
	 * spv申购订单审核确定调整totalVolume
	 * @param oid
	 * @param orderAmount
	 * @return
	 */
	@Query(value = "update PublisherHoldEntity set totalVolume = totalVolume + ?2 where oid = ?1 ")
	@Modifying
	public int spvOrderInvestConfirm(String oid,BigDecimal orderAmount);
		
	/**
	 * 投资 增加SPV锁定赎回金额
	 */
	@Query("update PublisherHoldEntity set lockRedeemHoldVolume = lockRedeemHoldVolume + ?3  "
			+ " where product = ?1 and accountType = ?2 and totalVolume - lockRedeemHoldVolume >= ?3")
	@Modifying
	public int checkSpvHold4Invest(Product product, String accountType, BigDecimal orderVolume);
	
	@Query("update PublisherHoldEntity set lockRedeemHoldVolume = lockRedeemHoldVolume - ?3  where product = ?1 "
			+ " and accountType = ?2 and lockRedeemHoldVolume >= ?3 ")
	@Modifying
	public int updateSpvHold4InvestAbandon(Product product, String accountType, BigDecimal orderVolume);

	/**
	 * 赎回份额确认
	 * @param product
	 * @param publisherAccounttypeSpv
	 * @param orderVolume
	 * @return
	 */
	@Query("update PublisherHoldEntity set totalVolume = totalVolume + ?3  where product = ?1 and accountType = ?2")
	@Modifying
	public int update4RedeemConfirm(Product product, String accountType, BigDecimal orderVolume);
	
	/**
	 * //更新SPV持仓
	 * @param product
	 * @param publisherAccounttypeSpv
	 * @param orderVolume
	 * @return
	 */
	@Query("update PublisherHoldEntity set lockRedeemHoldVolume = lockRedeemHoldVolume - ?3, totalVolume = totalVolume - ?3  where product = ?1 and accountType = ?2 and lockRedeemHoldVolume >= ?3")
	@Modifying
	public int update4InvestConfirm(Product product, String publisherAccounttypeSpv,
			BigDecimal orderVolume);
	/**
	 * 废单:定期产品
	 * 增加总份额
	 * @param product
	 * @param publisherAccounttypeSpv
	 * @param orderVolume
	 * @return
	 */
	@Query("UPDATE PublisherHoldEntity SET totalVolume = totalVolume + ?3  "
			+ "WHERE product = ?1 AND accountType = ?2 ")
	@Modifying
	public int update4InvestConfirmAbandon(Product product, String publisherAccounttypeSpv,
			BigDecimal orderVolume);
	
	@Query(value = "select * from T_MONEY_PUBLISHER_HOLD where productOid = ?1 and accountType = 'INVESTOR' order by oid limit ?2, ?3", nativeQuery = true)
	public List<PublisherHoldEntity> queryHoldList(String productOid, int offset, int limit);
	
	@Query(value = "select * from T_MONEY_PUBLISHER_HOLD where productOid = ?1 and investorOid = ?2", nativeQuery = true)
	public List<PublisherHoldEntity> queryHoldList(String productOid, String investorOid);
	
	@Query("update PublisherHoldEntity set updateTime = sysdate()  where product = ?1 and investorBaseAccount = ?2 and redeemableHoldVolume = ?3")
	@Modifying
	public int update4MinRedeem(Product product, InvestorBaseAccountEntity investorBaseAccount, BigDecimal orderAmount);
	
	@Query("update PublisherHoldEntity set maxHoldVolume = maxHoldVolume + ?4  where product = ?2 and investorBaseAccount = ?1 and maxHoldVolume + ?4 <= ?3")
	@Modifying
	public int checkMaxHold4Invest(InvestorBaseAccountEntity investorBaseAccount, Product product, BigDecimal proMaxHoldVolume, BigDecimal orderVolume);
	
	@Query("update PublisherHoldEntity set maxHoldVolume = maxHoldVolume - ?3  where product = ?2 and investorBaseAccount = ?1 and maxHoldVolume >= ?3")
	@Modifying
	public int updateMaxHold4InvestAbandon(InvestorBaseAccountEntity investorBaseAccount, Product product, BigDecimal orderVolume);
	
	@Query(value = "update T_MONEY_PUBLISHER_HOLD set lockRedeemHoldVolume = lockRedeemHoldVolume + ?3, "
			+ " redeemableHoldVolume = redeemableHoldVolume + ?2,"
			+ " accruableHoldVolume = accruableHoldVolume + ?4,"
			+ " maxHoldVolume = maxHoldVolume + ?3, "
			+ " holdVolume = holdVolume + ?5,"
			+ " toConfirmInvestVolume = toConfirmInvestVolume - ?5, "
			+ " holdStatus = if(holdStatus = 'toConfirm', 'holding', holdStatus) "
			+ " where oid = ?1 and toConfirmInvestVolume >= ?5", nativeQuery = true)
	@Modifying
	public int updateHold4Confirm(String holdOid, BigDecimal redeemableHoldVolume,
			BigDecimal lockRedeemHoldVolume, BigDecimal accruableHoldVolume, BigDecimal orderVolume);
	
	@Query(value = "update T_MONEY_PUBLISHER_HOLD set lockRedeemHoldVolume = lockRedeemHoldVolume - ?2, "
			+ " accruableHoldVolume = accruableHoldVolume - ?3,"
			+ " maxHoldVolume = maxHoldVolume - ?2, "
			+ " holdVolume = holdVolume - ?4,"
			+ " totalVolume = totalVolume - ?4,"
			+ " totalInvestVolume = totalInvestVolume - ?4,"
			+ " value = value - ?4 "
			+ " where oid = ?1 and holdVolume >= ?4", nativeQuery = true)
	@Modifying
	public int updateHold4ConfirmAbandon(String holdOid, BigDecimal lockRedeemHoldVolume, BigDecimal accruableHoldVolume, BigDecimal orderVolume);
	
	@Query(value = "update T_MONEY_PUBLISHER_HOLD set expGoldVolume = expGoldVolume + ?3, "
			+ " accruableHoldVolume = accruableHoldVolume + ?2, "
			+ " maxHoldVolume = maxHoldVolume + ?3, "
			+ " holdVolume = holdVolume + ?3,"
			+ " toConfirmInvestVolume = toConfirmInvestVolume - ?3, "
			+ " holdStatus = if(holdStatus = 'toConfirm', 'holding', holdStatus) "
			+ " where oid = ?1 and toConfirmInvestVolume >= ?3" , nativeQuery = true)
	@Modifying
	public int updateHold4ExpGoldConfirm(String holdOid, BigDecimal accruableHoldVolume, BigDecimal orderVolume);
	
	
	@Query("update PublisherHoldEntity set dayRedeemVolume = 0, dayInvestVolume = 0, dayRedeemCount = 0 ")
	@Modifying
	public int resetToday();
	
	/**
	 * 付息锁定
	 * @param interest 利息
	 * @param investorBaseAccount 投资人
	 * @param product 产品
	 * @return
	 */
	@Query(value = "update PublisherHoldEntity set lockIncome = lockIncome + ?1, "
			+ "redeemableIncome = redeemableIncome - ?1 where investorBaseAccount = ?2 and product= ?3 and redeemableIncome-?1 >=0")
	@Modifying
	public int repayInterestLock(BigDecimal amount, InvestorBaseAccountEntity investorBaseAccount, Product product);
	

	
	/**
	 * 查询活期产品详情（）
	 * @param investorOid：用户uid
	 * @param productOid:产品oid
	 * @return
	 */
	@Query(value = " SELECT A.holdTotalIncome, "//累计收益
			+ " A.holdYesterdayIncome, "//昨日收益
			+ " (A.redeemableHoldVolume + A.lockRedeemHoldVolume + A.toConfirmRedeemVolume + A.expGoldVolume) holdVolume, "//持有份额
			+ " A.productOid, "//
			+ " C.incomeCalcBasis, "//收益计算基础
			+ " C.expAror, "//预期年化收益率
			+ " C.expArorSec, "//年化收益率区间
			+ " C.assetPoolOid, "//所属资产池
			+ " C.minRredeem, "//单笔赎回最低下限
			+ " C.maxRredeem, "//单笔赎回追加金额
			+ " C.additionalRredeem, "//单笔赎回最高份额
			+ " C.netUnitShare, "//单位净值
			+ " A.dayRedeemVolume, "//今日赎回金额
			+ " C.singleDailyMaxRedeem, "//单人单日赎回上限
			+ " C.dailyNetMaxRredeem, "//剩余赎回金额
			+ " C.netMaxRredeemDay, "//单日净赎回上限
			+ " A.redeemableHoldVolume "//可赎回份额
			+ " FROM T_MONEY_INVESTOR_BASEACCOUNT B "//
			+ " INNER JOIN T_MONEY_PUBLISHER_HOLD A ON A.investorOid = B.oid "//
			+ " INNER JOIN T_GAM_PRODUCT C ON A.productOid = C.oid "//
			+ " WHERE B.oid=?1 AND A.productOid=?2 ", nativeQuery = true)
	public List<Object[]> findProductDetail(String investorOid, String productOid);
	
	
	/**
	 * 查询我的已结清定期产品详情（投资金额，累计收益，计息开始日/产品成立日，存续期结束时间,存续天数）
	 * */
	@Query(value = "SELECT totalInvestVolume*A.netUnitShare holdAmount,B.holdTotalIncome,A.setupDate,A.durationPeriodEndDate,A.durationPeriodDays "
			+ " FROM T_MONEY_INVESTOR_BASEACCOUNT C "
			+ " INNER JOIN T_MONEY_PUBLISHER_HOLD B  ON B.investorOid = C.oid  "
			+ " INNER JOIN T_GAM_PRODUCT A ON A.oid = B.productOid "
			+ " WHERE C.oid=?1 AND B.productOid=?2 ", nativeQuery = true)
	public List<Object[]> myClosedregularDetail(String investorOid,String productOid);
	
	
	
	/**发行人下投资人质量分析（某个投资金额范围内的投资人个数）*/
	@Query(value = " SELECT A1.LEVEL,COUNT(*) FROM ( "
			+" SELECT A.investorOid, "
			+" CASE "
			+ "	  WHEN SUM(A.totalInvestVolume * B.netUnitShare) <=50000 THEN 1 "
			+"    WHEN SUM(A.totalInvestVolume * B.netUnitShare) BETWEEN 50000 AND 100000 THEN 2  "
			+"    WHEN SUM(A.totalInvestVolume * B.netUnitShare) BETWEEN 100000 AND 200000 THEN 3 "
			+"    ELSE 4 END LEVEL "
			+" FROM "
			+"   T_MONEY_PUBLISHER_HOLD A  "
			+"   INNER JOIN T_GAM_PRODUCT B ON A.productOid = B.oid  "
			+" WHERE A.publisherOid = ?1  "
			+" GROUP BY A.investorOid  "
			+" )A1 "
			+ " GROUP BY A1.LEVEL ASC ", nativeQuery = true)
	public List<Object[]> analyseInvestor(String publisherOid);
	
	public PublisherHoldEntity findByInvestorBaseAccount(InvestorBaseAccountEntity investorBaseAccount);
	
	
	@Query(value = "select count(*) from T_MONEY_PUBLISHER_HOLD where investorOid = ?1 and publisherOid = ?2", nativeQuery = true)
	public int countByPublisherBaseAccountAndInvestorBaseAccount(String investorOid, String publisherOid);
	
	/**平台下-投资人质量分析（某个投资金额范围内的投资人个数）*/
	@Query(value = " SELECT A1.LEVEL, COUNT(*) FROM ( "
			+" SELECT investorOid, "
			+" CASE "
			+ "	  WHEN SUM(totalVolume) <=50000 THEN 1 "
			+"    WHEN SUM(totalVolume) BETWEEN 50000 AND 100000 THEN 2  "
			+"    WHEN SUM(totalVolume) BETWEEN 100000 AND 200000 THEN 3 "
			+"    ELSE 4 END LEVEL "
			+" FROM "
			+"   T_MONEY_PUBLISHER_HOLD GROUP BY investorOid  "
			+" )A1 "
			+ " GROUP BY A1.LEVEL ASC ", nativeQuery = true)
	public List<Object[]> analysePlatformInvestor();
	
	
	@Query("from PublisherHoldEntity e where e.product = ?1 and e.accountType='SPV'")
	public List<PublisherHoldEntity> findSpvHoldByProduct(Product product);
	
	@Query(value = "select maxHoldVolume from T_MONEY_PUBLISHER_HOLD where investorOid = ?1 and productOid = ?2", nativeQuery = true)
	public BigDecimal findMaxHoldVol(String investorOid, String productOid);
	
	@Query(value = "select * from T_MONEY_PUBLISHER_HOLD where investorOid = ?1 and wishplanOid is null", nativeQuery = true)
	public List<PublisherHoldEntity> findByInvestorOid(String investorOid);
	
	/**
	 * 获取指定产品下面的所有持有人名录
	 */
	@Query(value = "select * from T_MONEY_PUBLISHER_HOLD where productOid = ?1 and holdStatus = 'holding' "
			+ " and oid > ?3 and accountType = ?2 order by oid limit 1000", nativeQuery = true)
	public List<PublisherHoldEntity> findByProductPaged(String productOid, String accountType, String lastOid);
	

	@Query(value = "SELECT * FROM T_MONEY_PUBLISHER_HOLD "
			+ " WHERE accountType='INVESTOR' and oid > ?1 order by oid limit 2000", nativeQuery = true)
	public List<PublisherHoldEntity> getHoldByBatch(String lastOid);
	
	
	@Query(value = "SELECT * FROM `T_MONEY_PUBLISHER_HOLD` WHERE accountType='SPV'", nativeQuery = true)
	public List<PublisherHoldEntity> getSPVHold();
	
	/**
	 * 获取含有体验金的用户
	 */
	@Query(value = "select * from T_MONEY_PUBLISHER_HOLD where oid > ?1 and expGoldVolume > 0 order by oid limit 2000 ", nativeQuery = true)
	public List<PublisherHoldEntity> getAllExpHolds(String lastOid);
	
	@Query(value = "update T_MONEY_PUBLISHER_HOLD set dayRedeemCount = dayRedeemCount + 1 where dayRedeemCount + 1 <= ?1 and productOid=?2 and investorOid=?3",nativeQuery = true)
	@Modifying
	public int updateDayRedeemCount(Integer singleDayRedeemCount,String productOid,String investorOid);

	/**
	 * redeemable HoldVolume
	 */
	@Query(value = "select redeemableHoldVolume from T_MONEY_PUBLISHER_HOLD where wishplanOid = ?1", nativeQuery = true)
	public BigDecimal findRedeemableHoldVolume(String wishplanOid);
	
	@Query(value="select t.latestOrderTime from T_MONEY_PUBLISHER_HOLD t where t.wishplanOid = ?1 and t.productOid = ?2",nativeQuery = true)
	public Timestamp findByWishplanOidAndProductOid(String oid, String productOid);

	public BigDecimal findByOid(String oid);

}
