package com.guohuai.mmp.investor.tradeorder;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.guohuai.ams.product.Product;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountEntity;
import com.guohuai.mmp.platform.investor.offset.InvestorOffsetEntity;
import com.guohuai.mmp.platform.publisher.offset.PublisherOffsetEntity;
import com.guohuai.mmp.publisher.hold.PublisherHoldEntity;

public interface InvestorTradeOrderDao
		extends JpaRepository<InvestorTradeOrderEntity, String>, JpaSpecificationExecutor<InvestorTradeOrderEntity> {

	/**
	 * 更新投资人清算状态
	 * 
	 * @param offset
	 *            投资人轧差
	 * @param investorClearStatus
	 *            清算状态
	 * @return
	 */
	@Query("update InvestorTradeOrderEntity set investorClearStatus = ?2 "
			+ " where investorOffset = ?1 and investorClearStatus = 'toClear' and orderStatus in ('accepted', 'done') ")
	@Modifying
	public int updateInvestorClearStatus(InvestorOffsetEntity offset, String investorClearStatus);

	/**
	 * 更新投资人结算状态
	 */
	@Query("update InvestorTradeOrderEntity set investorCloseStatus = ?2 where investorOffset = ?1  and orderStatus in ('accepted', 'done') "
			+ " and investorCloseStatus in ('toClose','closeSubmitFailed','closePayFailed') ")
	@Modifying
	public int updateInvestorCloseStatus(InvestorOffsetEntity offset, String investorCloseStatus);

	@Query("update InvestorTradeOrderEntity set investorCloseStatus = ?2 where investorOffset = ?1  and orderStatus in ('accepted', 'done') "
			+ " and investorCloseStatus = 'toClose' ")
	@Modifying
	public int updateInvestorCloseStatusDirectly(InvestorOffsetEntity offset, String investorCloseStatus);

	/**
	 * 更新超级用户投资人结算状态
	 */
	@Query("update InvestorTradeOrderEntity set investorCloseStatus = ?3 "
			+ " where investorOffset = ?1 and  investorOid = ?2 and investorClearStatus = 'cleared' and orderStatus in ('accepted', 'done') "
			+ " and investorCloseStatus in ('toClose','closeSubmitFailed','closePayFailed') ")
	@Modifying
	public int updatePlatformInvestorCloseStatus(InvestorOffsetEntity offset, String investorOid,
			String investorCloseStatus);

	
	public InvestorTradeOrderEntity findByOrderCode(String orderCode);

	// @Query(value="select * from T_MONEY_INVESTOR_TRADEORDER where oid > ?3
	// and orderStatus in ('accepted', 'done') "
	// + "and investorCloseStatus in
	// ('toClose','closeSubmitFailed','closePayFailed') "
	// + "and investorClearStatus = 'cleared' and investorOffsetOid = ?1 and
	// investorOid != ?2 order by oid limit 300", nativeQuery = true)
	// public List<InvestorTradeOrderEntity> findToCloseOrders(String
	// investorOffsetOid, String investorOid, String oid);
	//
	// @Query(value="select * from T_MONEY_INVESTOR_TRADEORDER where orderStatus
	// in ('accepted', 'done') "
	// + " and investorCloseStatus in
	// ('toClose','closeSubmitFailed','closePayFailed') "
	// + " and investorClearStatus = 'cleared' and investorOffsetOid = ?1 and
	// investorOid = ?2 ", nativeQuery = true)
	// public List<InvestorTradeOrderEntity> findToClosePlatformOrders(String
	// investorOffsetOid, String investorOid);

	@Query(value = "select * from T_MONEY_INVESTOR_TRADEORDER where oid > ?2 and orderStatus in ('accepted', 'confirmed') "
			+ " and publisherCloseStatus in ('toClose') and publisherOffsetOid = ?1 order by oid limit 300", nativeQuery = true)
	public List<InvestorTradeOrderEntity> findToCloseOrders(String publisherOffsetOid, String oid);

	public List<InvestorTradeOrderEntity> findByInvestorOffsetAndOrderType(InvestorOffsetEntity offset,
			String orderType);

	@Query(value = "select * from T_MONEY_INVESTOR_TRADEORDER entity where publisherOffsetOid = ?1 and oid > ?2 "
			+ "  and publisherClearStatus = 'cleared' and publisherConfirmStatus in ('confirmFailed', 'toConfirm')  order by oid limit 2000", nativeQuery = true)
	public List<InvestorTradeOrderEntity> findByOffsetOidAndOid(String offsetOid, String lastOid);

	/**
	 * 查询订单用于生成PDF协议 HTML
	 */
	@Query(value = "select * from T_MONEY_INVESTOR_TRADEORDER "
			+ " where productOid = ?1 and oid > ?2 and publisherConfirmStatus = 'confirmed' "
			+ " and orderType = 'invest' and contractStatus in ('htmlFail', 'toHtml') order by oid limit 2000", nativeQuery = true)
	public List<InvestorTradeOrderEntity> findByProductOid4Contract(String productOid, String lastOid);

	/**
	 * 查询订单用于生成PDF协议 PDF
	 */
	@Query(value = "select * from T_MONEY_INVESTOR_TRADEORDER "
			+ " where productOid = ?1 and oid > ?2 and publisherConfirmStatus = 'confirmed' "
			+ " and orderType = 'invest' and contractStatus in ('htmlOK') order by oid limit 2000", nativeQuery = true)
	public List<InvestorTradeOrderEntity> findByProductOid4PDF(String productOid, String lastOid);

	@Query(value = "update T_MONEY_INVESTOR_TRADEORDER set publisherClearStatus = ?2 "
			+ "where publisherOffsetOid = ?1 and publisherClearStatus = 'toClear' and orderStatus = 'accepted' ", nativeQuery = true)
	@Modifying
	public int updatePublisherClearStatus(String offsetOid, String clearStatus);

	/**
	 * 份额确认
	 */
	@Query(value = "update InvestorTradeOrderEntity set publisherConfirmStatus = 'confirmed', orderStatus = 'confirmed', completeTime = sysdate()  "
			+ " where oid = ?1 and publisherConfirmStatus in ('toConfirm', 'confirmFailed') and publisherClearStatus = 'cleared' ")
	@Modifying
	public int update4Confirm(String oid);

	/**
	 * 结算
	 */
	@Query(value = "update T_MONEY_INVESTOR_TRADEORDER set publisherCloseStatus = ?2  "
			+ " where publisherOffsetOid = ?1  and publisherClearStatus = 'cleared' "
			+ " and publisherCloseStatus in ('closeSubmitFailed', 'closePayFailed', 'toClose')  ", nativeQuery = true)
	@Modifying
	public int updateCloseStatus4Redeem(String pOffsetOid, String closeStatus);

	/**
	 * 结算
	 */
	@Query(value = "update T_MONEY_INVESTOR_TRADEORDER set publisherCloseStatus = ?2  "
			+ " where dividendOffsetOid = ?1  and publisherClearStatus = 'cleared' "
			+ " and publisherCloseStatus in ('closeSubmitFailed', 'closePayFailed', 'toClose')  ", nativeQuery = true)
	@Modifying
	public int updateCloseStatus4Dividend(String dividendOffsetOid, String closeStatus4TradeOrder);

	@Query(value = "update T_MONEY_INVESTOR_TRADEORDER set publisherCloseStatus =  ?2 "
			+ " where publisherOffsetOid = ?1  and publisherClearStatus = 'cleared' "
			+ " and publisherCloseStatus in ('toClose') and orderType = 'invest'  ", nativeQuery = true)
	@Modifying
	public int updateCloseStatus4Invest(String pOffsetOid, String closeStatus);

	@Query(value = "update T_MONEY_INVESTOR_TRADEORDER set publisherCloseStatus = ?2 "
			+ "where publisherOffsetOid = ?1 and publisherClearStatus = 'cleared' and publisherCloseStatus != 'closed' ", nativeQuery = true)
	@Modifying
	public int updateCloseStatus4CloseBack(String pOffsetOid, String closeStatus);

	@Query(value = "update T_MONEY_INVESTOR_TRADEORDER set orderStatus ='refused', "
			+ " publisherClearStatus = null, publisherConfirmStatus = null, publisherCloseStatus = null, investorClearStatus = null, investorCloseStatus = null "
			+ " where orderCode = ?1 and orderType = 'normalRedeem' and publisherClearStatus = 'toClear' and orderStatus = 'accepted' ", nativeQuery = true)
	@Modifying
	public int refuseOrder(String orderCode);

	@Query(value = "select * from T_MONEY_INVESTOR_TRADEORDER "
			+ "where productOid = ?1 and oid > ?2 and orderType = 'normalRedeem' and orderStatus = 'accepted'"
			+ " and publisherClearStatus = 'toClear' order by oid limit 1000", nativeQuery = true)
	public List<InvestorTradeOrderEntity> findByProduct(String productOid, String lastOid);

	@Query(value = "UPDATE T_MONEY_INVESTOR_TRADEORDER SET orderStatus = 'abandoning', holdStatus = 'abandoned', "
			+ " publisherClearStatus = 'toClear' "
			+ " WHERE orderCode = ?1 AND orderType = 'invest' AND orderStatus IN ('accepted')", nativeQuery = true)
	@Modifying
	public int abandonOrder(String orderCode);

	@Query(value = "update T_MONEY_INVESTOR_TRADEORDER set orderStatus = 'abandoning'  "
			+ " where orderCode = ?1 and orderType = 'invest' and orderStatus = 'paySuccess' ", nativeQuery = true)
	@Modifying
	public int refundOrder(String orderCode);

	@Query(value = "select * from T_MONEY_INVESTOR_TRADEORDER where oid > ?1 and orderStatus in ('toRefund', 'refundFailed') order by oid limit 300", nativeQuery = true)
	public List<InvestorTradeOrderEntity> findPage4Refund(String lastOid);

	/** 统计发行人昨日各产品投资金额 */
	@Query(value = "SELECT A.publisherOid,A.productOid,SUM(A.orderAmount) investAmount  "
			+ " FROM T_MONEY_INVESTOR_TRADEORDER A " + " WHERE A.orderType = 'invest'  "
			+ " AND A.orderStatus IN ('paySuccess','accepted','confirmed', 'done') "
			+ " AND A.orderTime BETWEEN ?1 AND ?2  " + " GROUP BY A.publisherOid,A.productOid ", nativeQuery = true)
	public List<Object[]> countPublishersYesterdayInvestAmount(Timestamp startTime, Timestamp endTime);

	/** 统计发行人截止昨日各产品累计投资金额 */
	@Query(value = "SELECT A.publisherOid,A.productOid,SUM(A.orderAmount) investAmount  "
			+ " FROM T_MONEY_INVESTOR_TRADEORDER A " + " WHERE A.orderType = 'invest'  "
			+ " AND A.orderStatus IN ('paySuccess','accepted','confirmed', 'done') " + " AND A.orderTime <= ?1  "
			+ " GROUP BY A.publisherOid,A.productOid ", nativeQuery = true)
	public List<Object[]> countPublishersTotalInvestAmount(Timestamp endTime);

	@Query(value = "select * from T_MONEY_INVESTOR_TRADEORDER where oid > ?1 and orderStatus = 'toPay' and (UNIX_TIMESTAMP(NOW()) - UNIX_TIMESTAMP(createTime))/60 > 15 order by oid limit 2000", nativeQuery = true)
	public List<InvestorTradeOrderEntity> findPage4RecoveryHold(String lastOid);

	/** 统计各渠道昨日投资额 */
	@Query(value = " SELECT A.channelOid,A.orderType,SUM(A.orderAmount) amount "
			+ " FROM T_MONEY_INVESTOR_TRADEORDER A  "
			+ " WHERE A.channelOid is not null and A.orderTime BETWEEN ?1 AND ?2 " + " AND A.orderType='invest' "
			+ " AND A.orderStatus IN ('paySuccess','accepted','confirmed','done') "
			+ " GROUP BY A.channelOid,A.orderType ", nativeQuery = true)
	public List<Object[]> statInvestAmountByChannel(Timestamp startTime, Timestamp endTime);

	/** 统计各渠道截止到昨日累计投资额 */
	@Query(value = " SELECT A.channelOid,A.orderType,SUM(A.orderAmount) amount "
			+ " FROM T_MONEY_INVESTOR_TRADEORDER A  " + " WHERE A.channelOid is not null and A.orderTime <=?1"
			+ " AND A.orderType='invest' " + " AND A.orderStatus IN ('paySuccess','accepted','confirmed','done') "
			+ " GROUP BY A.channelOid,A.orderType ", nativeQuery = true)
	public List<Object[]> statInvestTotalAmountByChannel(Timestamp endTime);

	// public InvestorTradeOrderEntity findByCoupons(String coupons);

	/**
	 * 份额确认（分仓）
	 */
	@Modifying
	@Query(value = "update InvestorTradeOrderEntity set holdStatus = 'holding', redeemStatus = ?2, "
			+ "accrualStatus = ?3, completeTime = sysdate() where oid = ?1")
	public int update4Confirm(String orderOid, String redeemStatus, String accrualStatus);

	/**
	 * 查询可平仓的分仓
	 */
	@Query(value = "from InvestorTradeOrderEntity "
			+ " where investorBaseAccount = ?1 and product = ?2 and holdVolume > 0"
			+ " and holdStatus in ('holding','partHolding', 'refunding') and wishplanOid IS NULL"
			+ " and redeemStatus = 'yes' order by createTime asc")
	public List<InvestorTradeOrderEntity> findApart(InvestorBaseAccountEntity investorBaseAccount, Product product);

	/**
	 * 查询可平仓的分仓
	 */
	@Query(value = "from InvestorTradeOrderEntity "
			+ " where investorBaseAccount = ?1 and product = ?2 and holdVolume > 0"
			+ " and holdStatus in ('holding','partHolding', 'refunding') and wishplanOid IS NULL"
			+ " and redeemStatus = 'yes' order by createTime desc")
	public List<InvestorTradeOrderEntity> findApartDesc(InvestorBaseAccountEntity investorBaseAccount, Product product);

	/**
	 * 定期计息快照
	 */
	@Modifying
	@Query(value = "insert into T_MONEY_PUBLISHER_INVESTOR_HOLD_SNAPSHOT (oid, orderOid, investorOid, productOid, holdOid, holdDays, beginRedeemDate, "
			+ " totalSnapshotVolume, snapshotVolume, holdVolume, "
			+ " remainderBaseIncome, remainderRewardIncome, remainderCouponIncome, snapShotDate, updateTime, createTime, additionalInterestRate, affectiveDays, wishplanOid) "
			+ " select REPLACE(uuid(), '-', ''), a.oid, a.investorOid, a.productOid, a.holdOid, TIMESTAMPDIFF(DAY, a.beginAccuralDate, ?2) + 1, a.beginRedeemDate, "
			+ " a.holdVolume + a.remainderBaseIncome + a.remainderRewardIncome + a.remainderCouponIncome, a.holdVolume + a.remainderBaseIncome + a.remainderRewardIncome + a.remainderCouponIncome, a.holdVolume,"
			+ " a.remainderBaseIncome, a.remainderRewardIncome, a.remainderCouponIncome, ?2, sysdate(), sysdate(), "
			+ " IF (c.setupDate <= ?2, IF (a.usedCoupons = 'yes', b.dAdditionalInterestRate , 0.00), 0.00),"
			+ " IF (c.setupDate <= ?2, IF (a.usedCoupons = 'yes',  IF ( TIMESTAMPDIFF(DAY, c.setupDate, ?2 )  < b.affectiveDays , TIMESTAMPDIFF(DAY, c.setupDate, ?2 ) , b.affectiveDays) , 0.00), 0.00),"
			+ " a.wishplanOid" + " from T_MONEY_INVESTOR_TRADEORDER a "
			+ " LEFT JOIN T_MONEY_INVESTOR_TRADEORDER_COUPON b ON b.orderOid = a.oid"
			+ " LEFT JOIN T_GAM_PRODUCT c on a.productOid = c.oid"
			+ " where a.productOid = ?1 AND a.beginAccuralDate <= ?2 "
			+ " and a.holdStatus in ('holding','partHolding') AND a.holdVolume > 0", nativeQuery = true)
	public int snapshotTnVolume(String productOid, Date incomeDate);

	/**
	 * 活期计息快照,复利
	 */
	@Modifying
	@Query(value = "insert into T_MONEY_PUBLISHER_INVESTOR_HOLD_SNAPSHOT (oid, orderOid, investorOid, productOid, holdOid, holdDays, beginRedeemDate, "
			+ " totalSnapshotVolume, snapshotVolume, holdVolume,"
			+ " remainderBaseIncome, remainderRewardIncome, remainderCouponIncome, snapShotDate, updateTime, createTime, additionalInterestRate, affectiveDays, wishplanOid) "
			+ " select REPLACE(uuid(), '-', ''), a.oid, a.investorOid, a.productOid, a.holdOid, TIMESTAMPDIFF(DAY, a.beginAccuralDate, ?2) + 1, a.beginRedeemDate, "
			+ " a.holdVolume + a.remainderBaseIncome + a.remainderRewardIncome + a.remainderCouponIncome, a.holdVolume + a.remainderBaseIncome + a.remainderRewardIncome + a.remainderCouponIncome, a.holdVolume,"
			+ " a.remainderBaseIncome, a.remainderRewardIncome, a.remainderCouponIncome, ?2, sysdate(), sysdate(), "
			+ "	IF (a.usedCoupons = 'yes', IF ( TIMESTAMPDIFF(DAY, a.beginAccuralDate, ?2 ) + 1 <= b.affectiveDays , b.dAdditionalInterestRate, '0') , NULL),"
			+ " IF (a.usedCoupons = 'yes', IF ( TIMESTAMPDIFF(DAY, a.beginAccuralDate, ?2 ) + 1 <= b.affectiveDays , '1', '0') , NULL),"
			+ " a.wishplanOid"
			+ " from T_MONEY_INVESTOR_TRADEORDER a LEFT JOIN T_MONEY_INVESTOR_TRADEORDER_COUPON b ON b.orderOid = a.oid"
			+ " where productOid = ?1 AND beginAccuralDate <= ?2 "
			+ " and holdStatus in ('holding','partHolding') AND holdVolume > 0", nativeQuery = true)
	public int snapshotT0Volume(String productOid, Date incomeDate);

	/**
	 * 活期计息快照,现金分红
	 */
	@Modifying
	@Query(value = "insert into T_MONEY_PUBLISHER_INVESTOR_HOLD_SNAPSHOT (oid, orderOid, investorOid, productOid, holdOid, holdDays, beginRedeemDate, "
			+ " totalSnapshotVolume, snapshotVolume, holdVolume,"
			+ " remainderBaseIncome, remainderRewardIncome, remainderCouponIncome, snapShotDate, updateTime, createTime, additionalInterestRate, affectiveDays, wishplanOid) "
			+ " select REPLACE(uuid(), '-', ''), a.oid, a.investorOid, a.productOid, a.holdOid, TIMESTAMPDIFF(DAY, a.beginAccuralDate, ?2) + 1, a.beginRedeemDate, "
			+ " a.holdVolume, a.holdVolume, a.holdVolume,"
			+ " a.remainderBaseIncome, a.remainderRewardIncome, a.remainderCouponIncome, ?2, sysdate(), sysdate(), "
			+ "	IF (a.usedCoupons = 'yes', IF ( TIMESTAMPDIFF(DAY, a.beginAccuralDate, ?2 ) + 1 <= b.affectiveDays , b.dAdditionalInterestRate, '0') , NULL),"
			+ " IF (a.usedCoupons = 'yes', IF ( TIMESTAMPDIFF(DAY, a.beginAccuralDate, ?2 ) + 1 <= b.affectiveDays , '1', '0') , NULL),"
			+ " a.wishplanOid"
			+ " from T_MONEY_INVESTOR_TRADEORDER a LEFT JOIN T_MONEY_INVESTOR_TRADEORDER_COUPON b ON b.orderOid = a.oid"
			+ " where productOid = ?1 AND beginAccuralDate <= ?2 "
			+ " and holdStatus in ('holding','partHolding') AND holdVolume > 0", nativeQuery = true)
	public int snapshotT0CashVolume(String productOid, Date incomeDate);

	/**
	 * 计息快照 (体验金利息)
	 */
	@Modifying
	@Query(value = "insert into T_MONEY_PUBLISHER_INVESTOR_HOLD_SNAPSHOT (oid, orderOid, investorOid, productOid, holdOid, holdDays, beginRedeemDate, "
			+ " totalSnapshotVolume, snapshotVolume, holdVolume, "
			+ " remainderBaseIncome, remainderRewardIncome, remainderCouponIncome, snapShotDate, updateTime, createTime, wishplanOid) "
			+ " select REPLACE(uuid(), '-', ''), oid, investorOid, productOid, holdOid, TIMESTAMPDIFF(DAY, beginAccuralDate, ?2) + 1, beginRedeemDate, "
			+ " IF(corpusAccrualEndDate <= ?2, IF(holdVolume >= orderVolume, holdVolume - orderVolume + remainderBaseIncome, holdVolume + remainderBaseIncome), holdVolume + remainderBaseIncome), "
			+ " IF(corpusAccrualEndDate <= ?2, IF(holdVolume >= orderVolume, holdVolume - orderVolume + remainderBaseIncome, holdVolume + remainderBaseIncome), holdVolume + remainderBaseIncome), "
			+ " IF(corpusAccrualEndDate <= ?2, IF(holdVolume >= orderVolume, holdVolume - orderVolume, holdVolume), holdVolume), "
			+ " remainderBaseIncome, remainderRewardIncome, remainderCouponIncome, ?2, sysdate(), sysdate(), wishplanOid"
			+ " from T_MONEY_INVESTOR_TRADEORDER " + " where productOid = ?1 AND beginAccuralDate <= ?2 "
			+ " and holdStatus in ('holding','partHolding') and holdVolume > 0 ", nativeQuery = true)
	public int snapshotTasteCouponVolume(String productOid, Date incomeDate);

	@Query(value = "select * from T_MONEY_INVESTOR_TRADEORDER where beginRedeemDate <= ?1 "
			+ "and redeemStatus = 'no' and oid > ?2 and holdStatus IN ('holding', 'partHolding') order by oid limit 1000 ", nativeQuery = true)
	public List<InvestorTradeOrderEntity> findByBeforerBeginRedeemDateInclusive(Date today, String oid);

	/**
	 * 根据可计算状态，持仓状态，开始起息日查找分仓
	 */
	@Query(value = "select * from T_MONEY_INVESTOR_TRADEORDER where beginAccuralDate <= ?1 and accrualStatus = 'no' and oid > ?2 and holdStatus IN ('holding', 'partHolding') order by oid limit 1000 ", nativeQuery = true)
	public List<InvestorTradeOrderEntity> findByBeforeBeginAccuralDateInclusive(Date date, String oid);

	/**
	 * 更新可赎回状态
	 */
	@Query(value = "update InvestorTradeOrderEntity set redeemStatus = 'yes' where oid = ?1")
	@Modifying
	public int unlockRedeem(String orderOid);

	/**
	 * 更新可计息状态
	 */
	@Query(value = "update InvestorTradeOrderEntity set accrualStatus = 'yes' where oid = ?1")
	@Modifying
	public int unlockAccrual(String orderOid);

	/**
	 * 更新可赎回状态
	 */
	@Query(value = "update InvestorTradeOrderEntity set redeemStatus = 'yes' where publisherHold = ?1")
	@Modifying
	public int unlockRedeemByHold(PublisherHoldEntity hold);

	/**
	 * 查询可计息分仓
	 */
	@Query(value = "from InvestorTradeOrderEntity "
			+ " where publisherHold = ?1 and beginAccuralDate <= ?2 and holdStatus in ('holding','partHolding')")
	public List<InvestorTradeOrderEntity> findInterestableApart(PublisherHoldEntity hold, Date curDate);

	/**
	 * 查询使用体验金且状态为持有中的订单
	 * 
	 * @return
	 */
	@Query(value = "select * from T_MONEY_INVESTOR_TRADEORDER where holdStatus = 'holding' and holdOid = ?1 and confirmDate is not null and  couponType = 'tasteCoupon'  ", nativeQuery = true)
	public List<InvestorTradeOrderEntity> findtTasteCouponHolding(String holdOid);

	@Query(value = "select count(*) from T_MONEY_INVESTOR_TRADEORDER where productOid = ?1 and holdStatus = 'toConfirm' ", nativeQuery = true)
	public int getToConfirmCountByProduct(String productOid);

	// @Query(value = "update InvestorTradeOrderEntity set holdVolume =
	// holdVolume + incomeAmount, incomeAmount = 0 where product = ?1 ")
	// @Modifying
	// public int changeIncomeIntoHoldVolume(Product product);

	/**
	 * 分配收益
	 */
	@Modifying
	@Query(value = "update InvestorTradeOrderEntity set holdVolume = holdVolume + ?2, " + " value = holdVolume * ?4,"
			+ " totalIncome = totalIncome + ?3, " + " yesterdayIncome = ?3, "
			+ " totalBaseIncome = totalBaseIncome + ?6, " + " yesterdayBaseIncome = ?6, "
			+ " totalRewardIncome = totalRewardIncome + ?7, "
			+ " yesterdayRewardIncome = ?7, confirmDate = ?5 where oid = ?1")
	public int updateHoldApart4Interest(String orderOid, BigDecimal incomeVolume, BigDecimal incomeAmount,
			BigDecimal netUnitAmount, Date incomeDate, BigDecimal baseAmount, BigDecimal rewardAmount);

	@Modifying
	// @Query(value = "update InvestorTradeOrderEntity set incomeAmount =
	// incomeAmount + ?2, value = value + ?2,"
	// + " confirmDate = ?3 where oid = ?1")
	@Query(value = "update InvestorTradeOrderEntity set holdVolume = holdVolume + ?2, " + " value = value + ?2,"
			+ " totalIncome = totalIncome + ?2, " + " yesterdayIncome = ?2, "
			+ " totalBaseIncome = totalBaseIncome + ?2, " + " yesterdayBaseIncome = ?2, "
			+ " confirmDate = ?3 where oid = ?1")
	public int updateHoldApart4InterestTn(String orderOid, BigDecimal incomeAmount, Date incomeDate);

	public List<InvestorTradeOrderEntity> findByPublisherOffset(PublisherOffsetEntity offset);

	@Query(value = "SELECT * " + "FROM T_MONEY_INVESTOR_TRADEORDER " + "WHERE DATE_FORMAT(orderTime,'%Y-%m-%d') = ?1 "
			+ "AND orderCode > ?2 " + "AND usedCoupons='no' " + "AND orderType IN('invest','normalRedeem') "
			+ "AND orderStatus IN('payFailed','paySuccess','accepted','confirmed','done') "
			+ "ORDER BY orderCode LIMIT 2000 ", nativeQuery = true)
	public List<InvestorTradeOrderEntity> findInvestorOrderByOrderTime(String date, String orderCode);

	/**
	 * 查询待平仓体验金
	 */
	@Query(value = "select a.* from T_MONEY_INVESTOR_TRADEORDER a, t_money_investor_tradeorder_coupon b where a.oid = b.orderOid and b.couponType = 'tasteCoupon' "
			+ " and a.beginRedeemDate <= ?2 and a.redeemStatus = 'yes' and a.holdVolume >= a.orderAmount and a.oid > ?1 order by a.oid limit 2000", nativeQuery = true)
	public List<InvestorTradeOrderEntity> queryFlatExpGold(String lastOid, Date baseDate);

	@Query(value = "update InvestorTradeOrderEntity set orderStatus = 'abandoned' where orderCode = ?1 and orderStatus = 'abandoning' ")
	@Modifying
	public int updateOrderStatus4Abandon(String orderCode);

	@Query(value = "update InvestorTradeOrderEntity set orderStatus = 'refunded' where orderCode = ?1 and orderStatus = 'refunding' ")
	@Modifying
	public int updateOrderStatus4Refund(String orderCode);

	public List<InvestorTradeOrderEntity> findByPublisherHold(PublisherHoldEntity hold);

	// 赎回，还本付息，退款已结算，confirmed：订单异常
	@Query(value = "SELECT IFNULL(SUM(a.orderAmount), 0) FROM T_MONEY_INVESTOR_TRADEORDER a "
			+ " WHERE a.orderTime > ?1 " + " AND a.orderTime < ?2 " + " AND a.orderType != 'invest' "
			+ " AND a.orderStatus NOT IN ('submitted', 'refused', 'payFailed', 'payExpired') "
			+ " AND a.investorOid = ?3 ", nativeQuery = true)
	public BigDecimal getAmtTradeOrder4Acc(String checkTimeStart, String checkTimeEnd, String investorOid);

	// 用户持有份额
	@Query(value = "SELECT IFNULL(SUM(a.holdVolume), 0) FROM T_MONEY_INVESTOR_TRADEORDER a, " + " T_GAM_PRODUCT b "
			+ " WHERE a.productOid = b.oid " + " AND a.orderTime > ?1 " + " AND a.orderTime < ?2 "
			+ " AND a.orderType = ?3 " + " AND a.holdStatus IN ('holding', 'partHolding') " + " AND a.investorOid = ?4 "
			+ " AND b.type = ?5 ", nativeQuery = true)
	public BigDecimal getHoldingAmtTradeOrder4Acc(String checkTimeStart, String checkTimeEnd, String orderType,
			String investorOid, String productType);

	// 用户持有状态对应金额
	@Query(value = "SELECT IFNULL(SUM(a.orderAmount), 0) FROM T_MONEY_INVESTOR_TRADEORDER a, " + " T_GAM_PRODUCT b "
			+ " WHERE a.productOid = b.oid " + " AND a.orderTime > ?1 " + " AND a.orderTime < ?2 "
			+ " AND a.orderType = ?3 " + " AND a.holdStatus = ?4 " + " AND a.investorOid = ?5 "
			+ " AND b.type = ?6 ", nativeQuery = true)
	public BigDecimal getHoldStatusAmtTradeOrder4Acc(String checkTimeStart, String checkTimeEnd, String orderType,
			String holdStatus, String investorOid, String productType);

	// 用户卡券红包金额
	@Query(value = "SELECT IFNULL(SUM(a.couponAmount), 0) FROM T_MONEY_INVESTOR_TRADEORDER a "
			+ " WHERE a.orderTime > ?1 " + " AND a.orderTime < ?2 " + " AND a.orderType = ?3 "
			+ " AND a.investorOid = ?4 "
			+ " AND a.orderStatus NOT IN ('submitted', 'refused', 'payFailed', 'payExpired') ", nativeQuery = true)
	public BigDecimal getCouponAmtTradeOrder4Acc(String checkTimeStart, String checkTimeEnd, String orderType,
			String investorOid);

	// 申购申请中金额
	@Query(value = "SELECT IFNULL(SUM(a.orderAmount), 0) FROM T_MONEY_INVESTOR_TRADEORDER a "
			+ " WHERE a.orderTime > ?1 " + " AND a.orderTime < ?2 " + " AND a.orderType = 'invest' "
			+ " AND a.investorOid = ?3 "
			+ " AND a.orderStatus IN ('toPay', 'accepted', 'confirmed') ", nativeQuery = true)
	public BigDecimal getApplyingTradeOrder4Acc(String checkTimeStart, String checkTimeEnd, String investorOid);

	// 申购 赎回
	@Query(value = "SELECT a.orderCode,a.orderStatus,a.orderType,IF(a.orderType='invest',a.payAmount,a.orderAmount) AS orderAmount,b.memberId,a.orderTime,c.type "
			+ "FROM T_MONEY_INVESTOR_TRADEORDER a "
			+ "INNER JOIN T_MONEY_INVESTOR_BASEACCOUNT b ON a.investorOid=b.oid "
			+ "LEFT JOIN T_GAM_PRODUCT c ON a.productOid=c.oid " + "WHERE a.orderTime BETWEEN ?1 AND ?2 "
			+ "AND a.orderCode > ?3 "
			+ "AND a.orderType IN('invest','normalRedeem','reInvest','reRedeem','clearRedeem','cashFailed','cash') "
			+ "AND a.orderStatus IN('payFailed','paySuccess','accepted','confirmed','done') "
			+ "ORDER BY a.orderCode LIMIT 2000 ", nativeQuery = true)
	public List<Object[]> findInvestorOrderByOrderTime(String beginTime, String endTime, String orderTradeCode);

	@Query(value = "select oid from T_MONEY_INVESTOR_TRADEORDER where productOid = ?1 and orderType = 'normalRedeem' "
			+ " and orderStatus = 'accepted' and oid > ?2 order by oid limit 1000", nativeQuery = true)
	public List<String> findByProductOid(String productOid, String lastOid);

	@Query(value = "select * from T_MONEY_INVESTOR_TRADEORDER where publisherOid = ?1"
			+ " and orderType in ('normalRedeem', 'dividend', 'cash', 'cashFailed', 'clearRedeem')  and publisherCloseStatus in ('closing') limit 300 ", nativeQuery = true)
	public List<InvestorTradeOrderEntity> getToPayOrders(String publisherOid);

	@Query(value = "SELECT SUM(a.orderAmount),MAX(a.completeTime),a.publisherOid "
			+ "FROM T_MONEY_INVESTOR_TRADEORDER a "
			+ "WHERE a.orderStatus = 'confirmed' AND a.orderType IN ('invest', 'expGoldInvest', 'writeOff') "
			+ "GROUP BY a.publisherOid", nativeQuery = true)
	public List<Object[]> getTotalLoanAmount();

	@Query(value = "SELECT SUM(a.orderAmount),MAX(a.completeTime),a.publisherOid "
			+ "FROM T_MONEY_INVESTOR_TRADEORDER a "
			+ "WHERE a.orderStatus = 'confirmed' AND a.orderType IN ('normalRedeem', 'clearRedeem', 'cash', 'cashFailed', 'expGoldRedeem') "
			+ "GROUP BY a.publisherOid", nativeQuery = true)
	public List<Object[]> getTotalReturnAmount();

	@Query(value = "SELECT SUM(a.orderAmount),a.publisherOid,b.type "
			+ "FROM T_MONEY_INVESTOR_TRADEORDER a LEFT JOIN T_GAM_PRODUCT b ON a.productOid = b.oid "
			+ "WHERE a.orderStatus = 'confirmed' AND a.orderType IN ('invest', 'expGoldInvest', 'writeOff') "
			+ "AND a.completeTime >= ?1 AND a.completeTime <= ?2 "
			+ "GROUP BY a.publisherOid,b.type", nativeQuery = true)
	public List<Object[]> getTotalTLoanAmount(String startTime, String endTime);

	
	@Query(value = "SELECT * from T_MONEY_INVESTOR_TRADEORDER "
				 + "WHERE wishplanOid = ?1 AND holdVolume > 0 AND orderType = 'invest' ", nativeQuery = true)
	public InvestorTradeOrderEntity findTradeOrderByWishplan(String wishplanOid);
	
	@Query(value = "SELECT beginRedeemDate from T_MONEY_INVESTOR_TRADEORDER WHERE oid = ?1", nativeQuery = true)
	public Date findBeginRedeemDateByOid(String oid);
	
	@Query(value = "SELECT COUNT(1) from t_money_publisher_investor_holdincome WHERE wishplanOid = ?1", nativeQuery = true)
	public int findHoldincomeCountByPlanOid(String oid);
	
	/**
	 * 
	 *查询心愿计划的累计赎回次数和累计金额 
	 * */
	@Query(value = "SELECT COUNT(*),IFNULL(SUM(t.orderAmount),0) FROM t_money_investor_tradeorder t "
			+ " WHERE t.orderType ='wishRedeem' AND t.investorOid=?1 AND "
			+ " t.orderStatus='confirmed' " ,nativeQuery =true)
	public List<Object[]> queryWishplanRedeem(String investorOid);

	@Query(value = "SELECT COUNT(*),IFNULL(SUM(t.orderAmount),0)FROM t_money_investor_tradeorder t WHERE"
			+ "  (CASE WHEN  NOW()>DATE_FORMAT(NOW(),'%Y-%m-%d 15:00:00')  THEN t.orderTime >DATE_FORMAT(NOW(),'%Y-%m-%d 15:00:00') "
			+ " ELSE t.orderTime>DATE_FORMAT(DATE_SUB(CURRENT_DATE(),INTERVAL 1 DAY),'%Y-%m-%d 15:00:00') END ) AND t.orderType='wishRedeem'"
			+ "  AND t.investorOid=?1 AND t.orderStatus='confirmed' ",nativeQuery = true)
	public List<Object[]> queryWishplanTodayRedeem(String investorOid);

	@Query(value = "select * from t_money_investor_tradeorder t where t.investorOid = ?1 and t.wishplanOid is not null AND t.orderType='wishRedeem' AND t.orderStatus='confirmed'" ,nativeQuery = true)
	public List<InvestorTradeOrderEntity> findTradeOrderByInvestorOid(String investorOid);

	@Query(value = "select * from t_money_investor_tradeorder t where t.wishplanOid = ?1 and t.orderType = ?2 " , nativeQuery = true)
	public InvestorTradeOrderEntity findByWishplan(String wishplanOid,String ordertype);

	@Query(value ="SELECT COUNT(*) FROM t_money_investor_tradeorder t WHERE t.investorOid = ?1 AND t.orderType='wishInvest' AND t. orderStatus ='confirmed' ",nativeQuery =true)
	public int queryTotalInvestCountByOid(String investorOid);

	@Query(value = "SELECT count(*),IFNULL(SUM(t.orderAmount),0) FROM t_money_investor_tradeorder t WHERE (CASE WHEN  NOW()>DATE_FORMAT(NOW(),'%Y-%m-%d 15:00:00') "
			+ " THEN t.orderTime >DATE_FORMAT(NOW(),'%Y-%m-%d 15:00:00') ELSE t.orderTime>DATE_FORMAT(DATE_SUB(CURRENT_DATE(),INTERVAL 1 DAY),'%Y-%m-%d 15:00:00') END ) "
			+ " AND t.orderType='wishInvest' AND t.orderStatus='confirmed' and t.investorOid =?1",nativeQuery = true)
	public List<Object[]> queryTodayInvestInfo(String investorOid);
	
	@Query(value = "SELECT COUNT(*) FROM T_MONEY_INVESTOR_TRADEORDER "
			 + "WHERE wishplanOid = ?1 AND orderType = ?2", nativeQuery = true)
    public int findTradeOrderByWishplanAndType(String wishplanOid, String type);

	@Query(value = "SELECT * FROM t_money_investor_tradeorder t "
			+ " WHERE t.wishplanOid =?1 AND t.productOid = ?2 AND t.orderType in ?3 and t.orderStatus not in( 'refused','payFailed','payExpired','refunded','abandoned')", nativeQuery = true)
	public InvestorTradeOrderEntity findByWishplanOidAndProductOidAndType(String wishplanOid, String productOid,
			List<String> type);
	@Query(value="SELECT * FROM `t_money_investor_tradeorder` t1 ,`t_gam_product` t2 ,`t_money_publisher_hold` t3 WHERE t1.`investorOid`= ?1 AND t1.`productOid` = t2.`oid` AND t2.`type`= ?2 AND t1.`wishplanOid` IS NULL "
			+ " AND t3.`oid` = t1.`holdOid` ORDER BY t1.createTime desc",nativeQuery = true)
	public List<InvestorTradeOrderEntity> findByInvestorOid(String userOid,String type);
	@Query(value="select t2.oid from t_money_investor_tradeorder t1,t_plan_invest t2 where t1.wishplanOid = t2.oid and t1.orderCode = ?1 limit 1",nativeQuery = true)
	public String findPlanOidByOrdeCode(String orderCode);
	@Query(value="select t2.monthOid from t_money_investor_tradeorder t1,t_plan_invest t2 where t1.wishplanOid = t2.oid and t1.orderCode = ?1 limit 1",nativeQuery = true)
	public String findMonthPlanOidByOrdeCode(String orderCode);
	
}
