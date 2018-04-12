package com.guohuai.mmp.platform.publisher.dividend.offset;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface DividendOffsetDao extends JpaRepository<DividendOffsetEntity, String>, JpaSpecificationExecutor<DividendOffsetEntity> {

	DividendOffsetEntity findByDividendDate(Date dividendDate);

	@Query(value = "select * from T_MONEY_PLATFORM_DIVIDEND_OFFSET where productOid = ?1 and dividendDate = ?2", nativeQuery = true)
	DividendOffsetEntity findByDividendDateAndProductOid(String productOid, Date incomeDate);
	
	
//	@Query(value = "insert into T_MONEY_INVESTOR_TRADEORDER (oid, investorOid, publisherOid, dividendOffsetOid, "
//		+ "  productOid, orderCode, "
//		+ "  orderAmount, orderVolume, orderStatus, orderTime, completeTime, orderType, createMan, "
//		+ "  checkStatus, publisherClearStatus, publisherConfirmStatus, publisherCloseStatus,"
//		+ "	 holdVolume, totalIncome, totalBaseIncome, totalRewardIncome, totalCouponIncome, yesterdayBaseIncome, yesterdayRewardIncome, yesterdayIncome, yesterdayCouponIncome,"
//		+ "	 remainderBaseIncome, remainderRewardIncome, remainderCouponIncome, toConfirmIncome, incomeAmount, expectIncome, expectIncomeExt, value)  "
//		+ "  select replace(UUID(), '-', ''), investorOid, ?2, ?3, "
//		+ "  ?1, concat(?5, nextval(date_format(sysdate(), '%Y%m%d'))), "
//		+ "  incomeAmount, incomeAmount, 'confirmed', sysdate(), sysdate(), 'dividend', 'platform', "
//		+ "  'no', 'cleared', 'confirmed', 'toClose',"
//		+ "  0, 0, 0, 0, 0, 0, 0, 0, 0, 0,0,0,0,0,0,0,0"
//		+ "  from T_MONEY_PUBLISHER_INVESTOR_HOLDINCOME where productOid = ?1 and confirmDate = ?4 and incomeAmount > 0", nativeQuery = true)
//	@Modifying
//	int createDividendOrders(String productOid, String publisherOid, String dividendOffsetOid, Date incomeDate, String seqEnv);
	
	
	
	@Query(value = "insert into T_MONEY_INVESTOR_TRADEORDER (oid, investorOid, publisherOid, dividendOffsetOid, "
			+ "  productOid, orderCode, "
			+ "  orderAmount, orderVolume, payAmount, orderStatus, orderTime, completeTime, orderType, createMan, "
			+ "  checkStatus, publisherClearStatus, publisherConfirmStatus, publisherCloseStatus,"
			+ "	 holdVolume, totalIncome, totalBaseIncome, totalRewardIncome, totalCouponIncome, yesterdayBaseIncome, yesterdayRewardIncome, yesterdayIncome, yesterdayCouponIncome,"
			+ "	 remainderBaseIncome, remainderRewardIncome, remainderCouponIncome, toConfirmIncome, incomeAmount, expectIncome, expectIncomeExt, value, wishplanOid)  "
			+ "  select orderOid, investorOid, publisherOid, dividendOffsetOid, "
			+ "  productOid, orderCode, "
			+ "  orderAmount, orderAmount, orderAmount, 'confirmed', sysdate(), sysdate(), 'dividend', 'platform', "
			+ "  'no', 'cleared', 'confirmed', 'toClose',"
			+ "  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, wishplanOid"
			+ "  from t_money_dividend_orders_md ", nativeQuery = true)
	@Modifying
	int createDividendOrders();
	
	
	@Query(value = "insert into t_money_dividend_orders_md (orderOid, investorOid, publisherOid, dividendOffsetOid, productOid,  orderCode, orderAmount, wishplanOid)  "
			+ "  select replace(UUID(), '-', ''), investorOid, ?2, ?3, "
			+ "  ?1, concat(?5, nextval(date_format(sysdate(), '%Y%m%d'))), "
			+ "  incomeAmount, wishplanOid"
			+ "  from T_MONEY_PUBLISHER_INVESTOR_HOLDINCOME where productOid = ?1 and confirmDate = ?4 and incomeAmount > 0", nativeQuery = true)
	@Modifying
	int insertIntoOrdersMd(String productOid, String publisherOid, String dividendOffsetOid, Date incomeDate, String seqEnv);
	
	@Query(value = "delete from t_money_dividend_orders_md", nativeQuery = true)
	@Modifying
	int deleteOrdersMd();
	
	
	@Query(value = "INSERT INTO T_MONEY_PLATFORM_DIVIDEND_OFFSET (oid, productOid, dividendDate, dividendAmount, toCloseDividendNumber, dividendCloseStatus)"
			+ " VALUES (?1, ?2, ?3, ?4, ?5, 'toClose')", nativeQuery = true)
	@Modifying
	int createDividendOffset(String dividendOid, String productOid, Date incomeDate, BigDecimal dividendAmount,
			int toCloseDividendNumber);

	List<DividendOffsetEntity> findByDividendCloseStatus(String dividendCloseStatus);
	
	@Query(value = "update T_MONEY_PLATFORM_DIVIDEND_OFFSET set dividendCloseStatus = ?2, message = ?3 where oid = ?1", nativeQuery = true)
	@Modifying
	int updateCloseStatus4Close(String oid, String dividendCloseStatus, String msg);
	
	@Query(value = "UPDATE T_MONEY_PLATFORM_DIVIDEND_OFFSET SET toCloseDividendNumber = toCloseDividendNumber - 1, "
			+ " dividendCloseStatus = IF(toCloseDividendNumber = 0, 'closed', dividendCloseStatus), updateTime = SYSDATE() where oid = ?1", nativeQuery = true)
	@Modifying
	int decreaseToCloseDividendNumber(String dividendOffsetOid);
	



}
