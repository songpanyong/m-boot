package com.guohuai.mmp.platform.finance.orders;

import java.sql.Timestamp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface OrdersDao extends JpaRepository<OrdersEntity, String>, JpaSpecificationExecutor<OrdersEntity> {

	/**
	 * 提取投资者的充值订单记录
	 */
	@Query(value = "INSERT INTO T_MONEY_ORDERS (checkOid, orderCode, iPayNo, userType,  orderType,"
			+ " orderAmount, voucher, fee,  investorOid, phoneNum, realName, orderStatus, checkStatus, orderTime) "
			+ " SELECT ?3, t1.orderCode, t1.orderCode, 'investor', "
			+ " CASE "
			+ " WHEN t1.`orderType` = 'deposit' THEN 'deposit'"
			+ " WHEN t1.orderType = 'depositLong' THEN 'deposit'  "
			+ " WHEN t1.orderType = 'redEnvelope' THEN 'redEnvelope' "
			+ " ELSE 'xx' END orderType, "
			+ " t1.`orderAmount`, "
			+ " 0, IF(t1.feePayer = 'user', t1.fee, 0), t1.`investorOid`, t2.phoneNum, t2.realName, t1.`orderStatus`, 'no', t1.orderTime  "
			+ " FROM `t_money_investor_bankorder` t1, `t_money_investor_baseaccount` t2 "
			+ " WHERE  t1.orderTime between ?1 and ?2 and t1.investorOid = t2.oid  and t1.orderType in ('deposit', 'depositLong', 'redEnvelope') and t1.orderStatus not in ('abandoned') ", nativeQuery = true)
	@Modifying
	int performInvestorsDepositOrders(Timestamp beginTime, Timestamp endTime, String checkOid);
	
	/**
	 * 提取投资者的提现订单记录
	 */
	@Query(value = "INSERT INTO T_MONEY_ORDERS (checkOid, orderCode, iPayNo, userType,  orderType,"
			+ " orderAmount, voucher, fee,  investorOid, phoneNum, realName, orderStatus, frozenStatus, checkStatus, orderTime) "
			+ " SELECT ?3, t1.orderCode, t1.orderCode, 'investor', "

			+ " CASE "
			+ " WHEN t1.orderType = 'withdraw' THEN 'withdraw' "
			+ " WHEN t1.orderType = 'withdrawLong' THEN 'withdraw' ELSE 'xx' END orderType, "

			+ " t1.`orderAmount`, "
			+ " 0, IF(t1.feePayer = 'user', t1.fee, 0), t1.`investorOid`, t2.phoneNum, t2.realName, t1.`orderStatus`, t1.frozenStatus, 'no', t1.orderTime  "
			+ " FROM `t_money_investor_bankorder` t1, `t_money_investor_baseaccount` t2 "
			+ " WHERE  t1.orderTime between ?1 and ?2 and t1.investorOid = t2.oid "
			+ " and t1.orderType in ('withdraw', 'withdrawLong')  and t1.orderStatus not in ('abandoned')", nativeQuery = true)
	@Modifying
	int performInvestorsWithdrawOrders(Timestamp beginTime, Timestamp endTime, String checkOid);
	

	/**
	 * 提取投资者的投资和赎回相关订单
	 */
	@Query(value = "INSERT INTO T_MONEY_ORDERS (checkOid, orderCode, iPayNo, userType,  " + " orderType,  "
			+ " orderAmount, voucher, fee,  investorOid, phoneNum, realName, orderStatus, checkStatus) "
			+ " SELECT ?3, t1.`orderCode`, t1.orderCode, 'investor',  "
			+ " CASE WHEN t1.`orderType` = 'cash' THEN 'redeem'  " + " WHEN t1.orderType = 'cashFailed' THEN 'redeem'  "
			+ " WHEN t1.orderType = 'dividend' THEN 'redeem' ELSE 'invest' END orderType,  " + " t1.payAmount,  "
			+ " t1.orderAmount - t1.payAmount, 0, t1.investorOid, t2.phoneNum, t2.realName, orderStatus, 'no'  "
			+ " FROM  `t_money_investor_tradeorder` t1, `t_money_investor_baseaccount` t2   "
			+ " WHERE t1.investorOid = t2.oid  and t1.orderTime between ?1 and ?2", nativeQuery = true)
	@Modifying
	int performInvestorInvestAndRedeemOrders(Timestamp beginTime, Timestamp endTime, String checkOid);

	/**
	 * 提取发行人充值订单
	 */
	@Query(value = "INSERT INTO T_MONEY_ORDERS (checkOid, orderCode, iPayNo, userType, orderType, "
			+ " orderAmount,  "
			+ " voucher, fee,  investorOid, phoneNum, realName, orderStatus, checkStatus, orderTime)  "
			+ " SELECT ?3, t1.orderCode, t1.orderCode, 'spv',  "
			+ " CASE WHEN t1.`orderType` = 'deposit' THEN 'deposit'  "
			+ " WHEN t1.orderType = 'depositLong' THEN 'deposit'  "
			+ " ELSE 'xx' END orderType, t1.`orderAmount`, "
			+ " 0, t1.`fee`, t1.`publisherOid`, t2.phone, t2.realName, t1.`orderStatus`, 'no', t1.orderTime  "
			+ " FROM T_MONEY_PUBLISHER_BANKORDER t1, `t_money_publisher_baseaccount` t2  "
			+ " WHERE  t1.publisherOid = t2.oid and t1.orderTime between ?1 and ?2 "
			+ " and t1.orderStatus not in ('abandoned') and t1.orderType in ('deposit', 'depositLong') ", nativeQuery = true)
	@Modifying
	int performPublishersDepositOrders(Timestamp beginTime, Timestamp endTime, String checkOid);
	
	
	
	/**
	 * 提取发行人提现订单
	 */
	@Query(value = "INSERT INTO T_MONEY_ORDERS (checkOid, orderCode, iPayNo, userType, orderType, "
			+ " orderAmount,  "
			+ " voucher, fee,  investorOid, phoneNum, realName, orderStatus, frozenStatus, checkStatus, orderTime)  "
			+ " SELECT ?3, t1.orderCode, t1.orderCode, 'spv',  "
			+ " CASE WHEN t1.orderType = 'withdraw' THEN 'withdraw'  "
			+ " WHEN t1.orderType = 'withdrawLong' THEN 'withdraw' ELSE 'xx' END orderType, t1.`orderAmount`, "
			+ " 0, t1.`fee`, t1.`publisherOid`, t2.phone, t2.realName, t1.`orderStatus`, t1.frozenStatus, 'no', t1.orderTime  "
			+ " FROM T_MONEY_PUBLISHER_BANKORDER t1, `t_money_publisher_baseaccount` t2  "
			+ " WHERE  t1.publisherOid = t2.oid and t1.orderTime between ?1 and ?2 "
			+ " and t1.orderStatus not in ('abandoned') and t1.orderType in ('withdraw', 'withdrawLong') and t1.payStatus != 'noPay' ", nativeQuery = true)
	@Modifying
	int performPublishersWithdrawOrders(Timestamp beginTime, Timestamp endTime, String checkOid);
	

	@Query(value = "update T_MONEY_ORDERS a, T_MONEY_CHECK_COMPAREDATA b set a.checkStatus = 'equal', b.checkStatus = 'equal' "
			+ " where a.orderType = b.buzzOrderType and  a.orderStatus = b.buzzOrderStatus and a.orderAmount = b.tradeAmount and a.investorOid = b.investorOid"
			+ " and a.voucher = b.voucher and a.fee = b.fee  "
			+ " and a.userType = b.buzzUserType and a.orderCode = b.orderCode and a.checkStatus = 'no' "
			+ " and b.checkStatus = 'no' and a.checkOid = ?1 and b.checkOid = ?1 ", nativeQuery = true)
	@Modifying
	public int filterRightOrders(String checkOid);

	@Query(value = "update T_MONEY_ORDERS a, T_MONEY_CHECK_COMPAREDATA b set a.checkStatus = 'notifyOk', b.checkStatus = 'notifyOk' "
			+ " where a.orderType = b.buzzOrderType and  a.orderStatus = 'toPay' and b.buzzOrderStatus = 'paySuccess' "
			+ " and a.orderAmount = b.tradeAmount and a.investorOid = b.investorOid"
			+ " and a.voucher = b.voucher and a.fee = b.fee  "
			+ " and a.userType = b.buzzUserType and a.orderCode = b.orderCode and a.checkStatus = 'no' and b.checkStatus = 'no' "
			+ " and a.orderType = 'deposit' and b.orderType = 'deposit' and a.checkOid = ?1 and b.checkOid = ?1 ", nativeQuery = true)
	@Modifying
	public int filterDepositNotifyOk(String checkOid);
	
	@Query(value = "update T_MONEY_ORDERS a, T_MONEY_CHECK_COMPAREDATA b set a.checkStatus = 'okTofail', b.checkStatus = 'okTofail' "
			+ " where a.orderType = b.buzzOrderType and  a.orderStatus = 'paySuccess' and b.buzzOrderStatus = 'payFailed' "
			+ " and a.orderAmount = b.tradeAmount and a.investorOid = b.investorOid"
			+ " and a.voucher = b.voucher and a.fee = b.fee  "
			+ " and a.userType = b.buzzUserType and a.orderCode = b.orderCode and a.checkStatus = 'no' and b.checkStatus = 'no' "
			+ " and a.orderType = 'deposit' and b.orderType = 'deposit' and a.checkOid = ?1 and b.checkOid = ?1 ", nativeQuery = true)
	@Modifying
	public int filterDepositOK2Fail(String checkOid);
	
	
	@Query(value = "update T_MONEY_ORDERS a, T_MONEY_CHECK_COMPAREDATA b set a.checkStatus = 'failToOk', b.checkStatus = 'failToOk' "
			+ " where a.orderType = b.buzzOrderType and  a.orderStatus = 'payFailed' and b.buzzOrderStatus = 'paySuccess' "
			+ " and a.orderAmount = b.tradeAmount and a.investorOid = b.investorOid"
			+ " and a.voucher = b.voucher and a.fee = b.fee  "
			+ " and a.userType = b.buzzUserType and a.orderCode = b.orderCode and a.checkStatus = 'no' and b.checkStatus = 'no' "
			+ " and a.orderType = 'deposit' and b.orderType = 'deposit' and a.checkOid = ?1 and b.checkOid = ?1 ", nativeQuery = true)
	@Modifying
	public int filterDepositFail2OK(String checkOid);
	
	
	
	
	@Query(value = "update T_MONEY_ORDERS a, T_MONEY_CHECK_COMPAREDATA b set a.checkStatus = 'notifyOk', b.checkStatus = 'notifyOk' "
			+ " where a.orderType = b.buzzOrderType "
			+ " and  a.orderStatus = 'toPay' and b.buzzOrderStatus = 'paySuccess' "
			+ " and a.orderAmount = b.tradeAmount and a.investorOid = b.investorOid"
			+ " and a.voucher = b.voucher and a.fee = b.fee  "
			+ " and a.userType = b.buzzUserType and a.orderCode = b.orderCode and a.checkStatus = 'no' and b.checkStatus = 'no' "
			+ " and a.orderType = 'withdraw' and b.orderType = 'withdraw' and a.checkOid = ?1 and b.checkOid = ?1 ", nativeQuery = true)
	@Modifying
	public int filterWithdrawNotifyOk(String checkOid);

	@Query(value = "update T_MONEY_ORDERS a, T_MONEY_CHECK_COMPAREDATA b set a.checkStatus = 'notifyFail', b.checkStatus = 'notifyFail' "
			+ " where a.orderType = b.buzzOrderType and  a.orderStatus = 'toPay' and b.buzzOrderStatus = 'payFailed' "
			+ " and a.orderAmount = b.tradeAmount and a.investorOid = b.investorOid"
			+ " and a.voucher = b.voucher and a.fee = b.fee  "
			+ " and a.userType = b.buzzUserType and a.orderCode = b.orderCode and a.checkStatus = 'no' and b.checkStatus ='no' and a.orderType = 'deposit'  "
			+ " and b.orderType = 'deposit' and a.checkOid = ?1 and b.checkOid = ?1 ", nativeQuery = true)
	@Modifying
	public int filterDepositNotifyFail(String checkOid);
	
	@Query(value = "update T_MONEY_ORDERS a, T_MONEY_CHECK_COMPAREDATA b set a.checkStatus = 'okTofail', b.checkStatus = 'okTofail' "
			+ " where a.orderType = b.buzzOrderType and  a.orderStatus = 'paySuccess' and b.buzzOrderStatus = 'payFailed' "
			+ " and a.orderAmount = b.tradeAmount and a.investorOid = b.investorOid"
			+ " and a.voucher = b.voucher and a.fee = b.fee  "
			+ " and a.userType = b.buzzUserType and a.orderCode = b.orderCode and a.checkStatus = 'no' and b.checkStatus = 'no' "
			+ " and a.orderType = 'withdraw' and b.orderType = 'withdraw' and a.checkOid = ?1 and b.checkOid = ?1 ", nativeQuery = true)
	@Modifying
	public int filterWithdrawOK2Fail(String checkOid);
	
	@Query(value = "update T_MONEY_ORDERS a, T_MONEY_CHECK_COMPAREDATA b set a.checkStatus = 'notifyFail', b.checkStatus = 'notifyFail' "
			+ " where a.orderType = b.buzzOrderType and  a.orderStatus = 'toPay' and b.buzzOrderStatus = 'payFailed' and a.orderAmount = b.tradeAmount and a.investorOid = b.investorOid"
			+ " and a.voucher = b.voucher and a.fee = b.fee  "
			+ " and a.userType = b.buzzUserType and a.orderCode = b.orderCode and a.checkStatus = 'no' and b.checkStatus ='no' and a.orderType = 'withdraw'  "
			+ " and b.orderType = 'withdraw' and a.checkOid = ?1 and b.checkOid = ?1 ", nativeQuery = true)
	@Modifying
	public int filterWithdrawNotifyFail(String checkOid);

	@Query(value = "delete from T_MONEY_CHECK_COMPAREDATA where orderType in ('abpay', 'abcollect', 'offsetPositive', 'offsetNegative', 'redeem', 'cash', 'cashFailed', 'clearRedeem', 'invest') and checkOid = ?1 ", nativeQuery = true)
	@Modifying
	int deleteUnfitOrdersInRemote(String checkOid);

	@Query(value = "delete from T_MONEY_ORDERS where orderType in ('abpay', 'abcollect', 'offsetPositive', 'offsetNegative', 'redeem', 'cash', 'cashFailed', 'clearRedeem', 'invest') and checkOid = ?1 ", nativeQuery = true)
	@Modifying
	int deleteUnfitOrders(String checkOid);

	@Query(value = "update T_MONEY_CHECK_COMPAREDATA b set b.checkStatus = 'long' "
			+ " where b.orderCode not in (select a.orderCode from T_MONEY_ORDERS a where a.checkStatus = 'no' and a.checkOid = ?1 ) and b.checkOid = ?1 and b.checkStatus = 'no'", nativeQuery = true)
	@Modifying
	int filterDepositAndWithdrawAndInvestAndRedeemLong(String checkOid);

	@Query(value = "update T_MONEY_ORDERS b set b.checkStatus = 'short' "
			+ " where b.orderCode not in (select a.orderCode from T_MONEY_CHECK_COMPAREDATA a where a.checkStatus = 'no' and a.checkOid = ?1) and b.checkOid = ?1 and b.checkStatus = 'no'", nativeQuery = true)
	@Modifying
	int filterDepositAndWithdrawAndInvestAndRedeemShort(String checkOid);

	@Query(value = "INSERT INTO t_money_check_comparedata_result (`oid`, `checkOid`, `orderCode`,"
			+ "`iPayNo`, `userType`, `orderType`,"
			+ "`orderAmount`,`voucher`,`fee`,`investorOid`, phoneNum, realName, `orderStatus`, frozenStatus,`checkStatus`, orderTime, "
			+ "`outerOrderCode`,`outerUserType`,`buzzUserType`,`outerOrderType`,`buzzOrderType`,"
			+ "`tradeAmount`, outerVoucher, outerFee, outerInvestorOid, outerPhoneNum, outerRealName, `outerOrderStatus`,`buzzOrderStatus`,outerCheckStatus,"
			+ " outerOrderTime, reconciliationStatus, `dealStatus`)"
			+ " SELECT  REPLACE(UUID(), '-', ''),  ?1, t1.`orderCode`," + " t1.`iPayNo`, t1.`userType`, t1.`orderType`,"
			+ " t1.`orderAmount`, t1.`voucher`, t1.`fee`, t1.`investorOid`, t1.phoneNum, t1.realName, t1.`orderStatus`, t1.frozenStatus, t1.`checkStatus`, t1.orderTime, "
			+ " t2.`orderCode`, t2.`userType`, t2.`buzzUserType`, t2.`orderType`, t2.`buzzOrderType`,"
			+ " t2.`tradeAmount`, t2.`voucher`, t2.`fee`, t2.`investorOid`, t2.phoneNum, t2.realName, t2.`orderStatus`, t2.buzzOrderStatus, t2.`checkStatus`, "
			+ " t2.orderTime, t2.reconciliationStatus, 'dealt' "
			+ " FROM `t_money_orders` t1, `t_money_check_comparedata` t2 "
			+ " WHERE t1.`iPayNo` = t2.`orderCode` and t1.checkOid = ?1 and t2.checkOid = ?1 and t1.checkStatus = 'equal' and t2.checkStatus = 'equal' ", nativeQuery = true)
	@Modifying
	int mergeNormalData(String checkOid);

	@Query(value = "INSERT INTO t_money_check_comparedata_result ( " + " `oid`, `checkOid`, `orderCode`,"
			+ "`iPayNo`, `userType`, `orderType`,"
			+ "`orderAmount`,`voucher`,`fee`,`investorOid`, phoneNum, realName, `orderStatus`, frozenStatus, `checkStatus`, orderTime, "
			+ "`outerOrderCode`,`outerUserType`,`buzzUserType`,`outerOrderType`,`buzzOrderType`,"
			+ "`tradeAmount`,outerVoucher, outerFee, outerInvestorOid, outerPhoneNum, outerRealName, `outerOrderStatus`,`buzzOrderStatus`, outerCheckStatus,"
			+ " reconciliationStatus, outerOrderTime, `dealStatus`)"
			+ " SELECT  REPLACE(UUID(), '-', ''),  ?1, t1.`orderCode`," + " t1.`iPayNo`, t1.`userType`, t1.`orderType`,"
			+ " t1.`orderAmount`, t1.`voucher`, t1.`fee`, t1.`investorOid`, t1.phoneNum, t1.realName, t1.`orderStatus`, t1.frozenStatus, t1.`checkStatus`, t1.orderTime, "
			+ " t2.`orderCode`, t2.`userType`, t2.`buzzUserType`, t2.`orderType`, t2.`buzzOrderType`,"
			+ " t2.`tradeAmount`, t2.`voucher`, t2.`fee`, t2.`investorOid`, t1.phoneNum, t2.realName, t2.`orderStatus`, t2.buzzOrderStatus, t2.`checkStatus`, "
			+ " t2.reconciliationStatus, t2.orderTime, 'toDeal' "
			+ " FROM `t_money_orders` t1, `t_money_check_comparedata` t2 "
			+ " WHERE t1.`iPayNo` = t2.`orderCode` and t1.checkOid = ?1 and t2.checkOid = ?1 "
			+ " and t1.checkStatus in ('notifyFail', 'notifyOk') and t2.checkStatus in ('notifyFail', 'notifyOk') ", nativeQuery = true)
	@Modifying
	int mergeNotifyData(String checkOid);
	
	@Query(value = "INSERT INTO t_money_check_comparedata_result (`oid`, `checkOid`, `orderCode`,"
			+ "`iPayNo`, `userType`, `orderType`,"
			+ "`orderAmount`,`voucher`,`fee`,`investorOid`, phoneNum, realName, `orderStatus`, frozenStatus,`checkStatus`, orderTime, "
			+ "`outerOrderCode`,`outerUserType`,`buzzUserType`,`outerOrderType`,`buzzOrderType`,"
			+ "`tradeAmount`, outerVoucher, outerFee, outerInvestorOid, outerPhoneNum, outerRealName, `outerOrderStatus`,`buzzOrderStatus`,outerCheckStatus,"
			+ " outerOrderTime, reconciliationStatus, `dealStatus`)"
			+ " SELECT  REPLACE(UUID(), '-', ''),  ?1, t1.`orderCode`," + " t1.`iPayNo`, t1.`userType`, t1.`orderType`,"
			+ " t1.`orderAmount`, t1.`voucher`, t1.`fee`, t1.`investorOid`, t1.phoneNum, t1.realName, t1.`orderStatus`, t1.frozenStatus, t1.`checkStatus`, t1.orderTime, "
			+ " t2.`orderCode`, t2.`userType`, t2.`buzzUserType`, t2.`orderType`, t2.`buzzOrderType`,"
			+ " t2.`tradeAmount`, t2.`voucher`, t2.`fee`, t2.`investorOid`, t2.phoneNum, t2.realName, t2.`orderStatus`, t2.buzzOrderStatus, t2.`checkStatus`, "
			+ " t2.orderTime, t2.reconciliationStatus, 'toDeal' "
			+ " FROM `t_money_orders` t1, `t_money_check_comparedata` t2 "
			+ " WHERE t1.`iPayNo` = t2.`orderCode` and t1.checkOid = ?1 and t2.checkOid = ?1 and t1.checkStatus = 'okTofail' and t2.checkStatus = 'okTofail' ", nativeQuery = true)
	@Modifying
	int mergeOk2FailData(String checkOid);
	
	
	@Query(value = "INSERT INTO t_money_check_comparedata_result (`oid`, `checkOid`, `orderCode`,"
			+ "`iPayNo`, `userType`, `orderType`,"
			+ "`orderAmount`,`voucher`,`fee`,`investorOid`, phoneNum, realName, `orderStatus`, frozenStatus,`checkStatus`, orderTime, "
			+ "`outerOrderCode`,`outerUserType`,`buzzUserType`,`outerOrderType`,`buzzOrderType`,"
			+ "`tradeAmount`, outerVoucher, outerFee, outerInvestorOid, outerPhoneNum, outerRealName, `outerOrderStatus`,`buzzOrderStatus`,outerCheckStatus,"
			+ " outerOrderTime, reconciliationStatus, `dealStatus`)"
			+ " SELECT  REPLACE(UUID(), '-', ''),  ?1, t1.`orderCode`," + " t1.`iPayNo`, t1.`userType`, t1.`orderType`,"
			+ " t1.`orderAmount`, t1.`voucher`, t1.`fee`, t1.`investorOid`, t1.phoneNum, t1.realName, t1.`orderStatus`, t1.frozenStatus, t1.`checkStatus`, t1.orderTime, "
			+ " t2.`orderCode`, t2.`userType`, t2.`buzzUserType`, t2.`orderType`, t2.`buzzOrderType`,"
			+ " t2.`tradeAmount`, t2.`voucher`, t2.`fee`, t2.`investorOid`, t2.phoneNum, t2.realName, t2.`orderStatus`, t2.buzzOrderStatus, t2.`checkStatus`, "
			+ " t2.orderTime, t2.reconciliationStatus, 'toDeal' "
			+ " FROM `t_money_orders` t1, `t_money_check_comparedata` t2 "
			+ " WHERE t1.`iPayNo` = t2.`orderCode` and t1.checkOid = ?1 and t2.checkOid = ?1 and t1.checkStatus = 'failToOk' and t2.checkStatus = 'failToOk' ", nativeQuery = true)
	@Modifying
	int mergeFail2OkData(String checkOid);
	
	
	
	

	@Query(value = "INSERT INTO t_money_check_comparedata_result (`oid`, `checkOid`, "
			+ "`outerOrderCode`,`outerUserType`,`buzzUserType`,`outerOrderType`,`buzzOrderType`,"
			+ "`tradeAmount`,outerVoucher, outerFee, outerInvestorOid, outerPhoneNum, outerRealName, `outerOrderStatus`,`buzzOrderStatus`,outerCheckStatus,"
			+ " reconciliationStatus, outerOrderTime, dealStatus) SELECT  REPLACE(UUID(), '-', ''),  ?1, "
			+ " t2.`orderCode`, t2.`userType`, t2.`buzzUserType`, t2.`orderType`, t2.`buzzOrderType`,"
			+ " t2.`tradeAmount`, t2.`voucher`, t2.`fee`, t2.`investorOid`, t2.phoneNum, t2.realName, t2.`orderStatus`, t2.buzzOrderStatus, t2.`checkStatus`, "
			+ " t2.reconciliationStatus, t2.orderTime, 'toDeal' "
			+ " FROM `t_money_check_comparedata` t2 where t2.orderCode not in (select t1.orderCode from t_money_orders t1 where t1.checkOid = ?1)"
			+ " and t2.checkOid = ?1 ", nativeQuery = true)
	@Modifying
	int mergeLongData(String checkOid);

	@Query(value = "INSERT INTO t_money_check_comparedata_result ( " + " `oid`, `checkOid`, `orderCode`,"
			+ " `iPayNo`, `userType`, `orderType`,"
			+ " `orderAmount`,`voucher`,`fee`,`investorOid`, phoneNum, realName, `orderStatus`, frozenStatus, `checkStatus`,"
			+ "  orderTime, dealStatus)" + "  SELECT  REPLACE(UUID(), '-', ''),  ?1, t1.`orderCode`,"
			+ "  t1.`iPayNo`, t1.`userType`, t1.`orderType`,"
			+ "  t1.`orderAmount`, t1.`voucher`, t1.`fee`, t1.`investorOid`, t1.phoneNum, t1.realName, t1.`orderStatus`, t1.frozenStatus, t1.`checkStatus`,"
			+ "  t1.orderTime, 'toDeal' "
			+ "  FROM `t_money_orders` t1 where t1.orderCode not in (select t2.orderCode from t_money_check_comparedata t2 where t2.checkOid = ?1)"
			+ "  and t1.checkOid = ?1 ", nativeQuery = true)
	@Modifying
	int mergeShortData(String checkOid);

	@Query(value = "delete from T_MONEY_ORDERS where checkOid = ?1 ", nativeQuery = true)
	@Modifying
	int deleteByCheckOid(String checkOid);

	@Query(value = "select count(*) from t_money_check_comparedata_result where checkOid = ?1 and dealStatus = 'toDeal' ", nativeQuery = true)
	int getWrongCount(String checkOid);

	@Query(value = "INSERT INTO t_money_check_comparedata_result ( " + " `oid`, `checkOid`, `orderCode`,"
			+ "`iPayNo`, `userType`, `orderType`,"
			+ "`orderAmount`,`voucher`,`fee`,`investorOid`, phoneNum, realName, `orderStatus`, frozenStatus, `checkStatus`, orderTime, "
			+ "`outerOrderCode`,`outerUserType`,`buzzUserType`,`outerOrderType`,`buzzOrderType`,"
			+ "`tradeAmount`,outerVoucher, outerFee, outerInvestorOid, outerPhoneNum, outerRealName, `outerOrderStatus`,`buzzOrderStatus`,outerCheckStatus,"
			+ " reconciliationStatus, outerOrderTime, `dealStatus`)"
			+ " SELECT  REPLACE(UUID(), '-', ''),  ?1, t1.`orderCode`," + " t1.`iPayNo`, t1.`userType`, t1.`orderType`,"
			+ " t1.`orderAmount`, t1.`voucher`, t1.`fee`, t1.`investorOid`, t1.phoneNum, t1.realName, t1.`orderStatus`, t1.frozenStatus, 'exception', t1.orderTime, "
			+ " t2.`orderCode`, t2.`userType`, t2.`buzzUserType`, t2.`orderType`, t2.`buzzOrderType`,"
			+ " t2.`tradeAmount`, t2.`voucher`, t2.`fee`, t2.`investorOid`, t2.phoneNum, t2.realName, t2.`orderStatus`, t2.buzzOrderStatus, 'exception', "
			+ " t2.reconciliationStatus, t2.orderTime, 'toDeal' "
			+ " FROM `t_money_orders` t1, `t_money_check_comparedata` t2 "
			+ " WHERE t1.`iPayNo` = t2.`orderCode` and t1.checkOid = ?1 and t2.checkOid = ?1 and t1.checkStatus = 'no' and t2.checkStatus = 'no' ", nativeQuery = true)
	@Modifying
	int mergeException(String checkOid);

	// @Query(value = "DELETE FROM T_MONEY_CHECK_COMPAREDATA "
	// + " WHERE buzzDate = ?1", nativeQuery = true)
	// @Modifying
	// public void deleteByBuzzDate(Date buzzDate);
	//
	//
	// public PlatformFinanceCompareDataEntity findByOrderCode(String
	// orderCode);
	//
	// @Query(value = "SELECT * FROM T_MONEY_CHECK_COMPAREDATA "
	// + " WHERE buzzDate = ?1 AND checkStatus = 'no' ", nativeQuery = true)
	// public List<PlatformFinanceCompareDataEntity>
	// findByCheckDateAndCheckStatus(String checkDate);
}
