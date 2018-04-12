package com.guohuai.ams.product;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import com.guohuai.ams.channel.Channel;
import com.guohuai.ams.product.productChannel.ProductChannel;
import com.guohuai.mmp.publisher.baseaccount.PublisherBaseAccountEntity;

public interface ProductDao extends JpaRepository<Product, String>, JpaSpecificationExecutor<Product> {

	/**
	 * 根据产品编号获取产品实体
	 * 
	 * @param productCode
	 * @return {@link Product}
	 */
	public Product findByCode(String code);

	public List<Product> findByOidIn(List<String> oids);

	public List<Product> findByState(String state);

	/**
	 * SPV订单审核确定调整 募集总份额(产品可售头寸)
	 * @param oid
	 * @param orderAmount
	 * @return
	 */
	@Query("update Product set raisedTotalNumber = raisedTotalNumber+?2, updateTime = sysdate() where oid = ?1 ")
	@Modifying
	public int adjustRaisedTotalNumber(String oid, BigDecimal orderAmount);

	/**
	 * 废单：解锁产品锁定已募份额 
	 */
	@Query("update Product set lockCollectedVolume = lockCollectedVolume - ?2  where oid = ?1 and lockCollectedVolume >= ?2")
	@Modifying
	public int update4InvestAbandon(String productOid, BigDecimal orderVolume);
	/**
	 * 废单：定期产品废单处理
	 */
	@Query("UPDATE Product SET maxSaleVolume = maxSaleVolume + ?2, "
			+ "currentVolume = currentVolume - ?2, collectedVolume = collectedVolume - ?2  "
			+ "WHERE oid = ?1  AND currentVolume >= ?2 AND collectedVolume >= ?2")
	@Modifying
	public int update4InvestConfirmAbandon(String productOid, BigDecimal orderVolume);
	/**
	 * 投资减少可售份额，增加锁定金额
	 * @param oid
	 * @param orderVolume
	 * @return
	 */
	@Query("update Product set lockCollectedVolume = lockCollectedVolume + ?2  where oid = ?1 and maxSaleVolume - lockCollectedVolume >= ?2")
	@Modifying
	public int update4Invest(String oid, BigDecimal orderVolume);
	
	/**
	 * 份额确认之后解除锁定份额
	 */
	@Query("update Product set lockCollectedVolume = lockCollectedVolume - ?2, maxSaleVolume = maxSaleVolume - ?2, "
			+ "currentVolume = currentVolume + ?2, collectedVolume = collectedVolume + ?2  where oid = ?1  and maxSaleVolume >= ?2 and lockCollectedVolume >= ?2")
	@Modifying
	public int update4InvestConfirm(String productOid, BigDecimal orderVolume);
	
	/**
	 * 减少单日赎回份额
	 * @param oid
	 * @param orderVolume
	 * @return
	 */
	@Query("update Product set dailyNetMaxRredeem = dailyNetMaxRredeem - ?2  where oid = ?1 and dailyNetMaxRredeem >= ?2")
	@Modifying
	public int update4Redeem(String productOid, BigDecimal orderVolume);
	
	@Query("update Product set dailyNetMaxRredeem = dailyNetMaxRredeem + ?2  where oid = ?1")
	@Modifying
	public int update4RedeemRefuse(String productOid, BigDecimal orderVolume);
	
	/**
	 * 赎回确认
	 * @param oid
	 * @param orderVolume
	 * @return
	 */
	@Query("update Product set  currentVolume = currentVolume - ?2  where oid = ?1 and currentVolume >= ?2")
	@Modifying
	public int update4RedeemConfirm(String productOid, BigDecimal orderVolume);
	
	@Query(value = "update Product set purchaseNum = purchaseNum + 1 where oid = ?1")
	@Modifying
	public int updatePurchaseNum(String productOid);
	
	@Query(value = "update Product set purchasePeopleNum = purchasePeopleNum + 1, purchaseNum = purchaseNum + 1 where oid = ?1")
	@Modifying
	public int updatePurchasePeopleNumAndPurchaseNum(String productOid);
	
	/**
	 * 资产池收益分配成功发送更新产品currentVolume
	 * @param oid
	 * @param incomeAllocateSuccesAmount
	 * @return
	 */
	@Query("update Product set currentVolume = currentVolume+?2, basicRatio = ?3, updateTime = sysdate() where oid = ?1 ")
	@Modifying
	public int incomeAllocateAdjustCurrentVolume(String oid, BigDecimal incomeAllocateSuccesAmount, BigDecimal basicRatio);
	
	@Query("update Product set  basicRatio = ?2, updateTime = sysdate() where oid = ?1 ")
	@Modifying
	public int incomeAllocateAdjustCurrentVolume(String oid, BigDecimal basicRatio);
	
	@Query("update Product set state = 'CLEARED', clearedTime = sysdate() where oid = ?1 and currentVolume = 0 ")
	@Modifying
	public int update4Liquidation(String oid);
	
	/**
	 * 清结算之前 需发放收益活期产品
	 */
	@Query(value = "select * from T_GAM_PRODUCT where and spvOid = ?1 and  state in ('DURATIONING', 'CLEARING') and type ='PRODUCTTYPE_02' ", nativeQuery = true)
	public List<Product> needIncomeBeforeOffset(String spvOid);
	
	/**
	 * 活期产品 存续期、清盘中 发放收益
	 */
	@Query(value = "select * from T_GAM_PRODUCT where state in ('DURATIONING', 'CLEARING') and type ='PRODUCTTYPE_02' ", nativeQuery = true)
	public List<Product> findProductT04Snapshot();
	
	/**
	 *定期产品 募集期 发放收益
	 */
	@Query(value = "select * from T_GAM_PRODUCT where raiseEndDate >= ?1 and recPeriodExpAnYield > 0 and type ='PRODUCTTYPE_01' ", nativeQuery = true)
	public List<Product> findProductTn4Snapshot(Date incomeDate);
	
//	@Query(value = "select * from T_GAM_PRODUCT where state in ('RAISING', 'RAISEEND') and recPeriodExpAnYield > 0 and type ='PRODUCTTYPE_01' ", nativeQuery = true)
//	public List<Product> findProductTn4Interest();
	
	/**
	 * 活期产品新建轧差批次
	 */
	@Query(value = "select * from T_GAM_PRODUCT where spvOid = ?1 and state in ('DURATIONING', 'CLEARING') and type ='PRODUCTTYPE_02' ", nativeQuery = true)
	public List<Product> findProductT04NewOffset(String spvOid);
	/**
	 * 定期产品新建轧差批次
	 */
	@Query(value = "select * from T_GAM_PRODUCT where spvOid = ?1 and state in ('RAISING', 'RAISEEND') and type ='PRODUCTTYPE_01' ", nativeQuery = true)
	public List<Product> findProductTn4NewOffset(String spvOid);
	
	@Query(value = "from Product where isDeleted = 'NO' and state != 'CLEARED' ")
	public List<Product> findByProduct4Contract();
	
	@Query("from Product where repayLoanStatus = 'toRepay' and repayDate >= ?1")
	public List<Product> getRepayLoanProduct(Date repayDate);
	
	@Query("from Product where repayInterestStatus = 'toRepay' and repayDate >= ?1")
	public List<Product> getRepayInterestProduct(Date repayDate);
	
//	@Query("update Product set fastRedeemLeft = fastRedeemLeft - ?2 where oid = ?1 and fastRedeemLeft >= ?2 ")
//	@Modifying
//	public int updateFastRedeemLeft(String productOid, BigDecimal orderVolume);
	
//	@Query("update Product set fastRedeemLeft = fastRedeemMax where fastRedeemStatus = 'YES' and state != 'CLEARED' and isDeleted = 'NO' ")
//	@Modifying
//	public int resetFastRedeemLeft();
//	
//	@Query(value = "update Product set fastRedeemStatus = ?3,fastRedeemLeft = ?2 + fastRedeemLeft - fastRedeemMax, fastRedeemMax = ?2,operator = ?4,updateTime = sysdate()  where oid = ?1 and fastRedeemMax-fastRedeemLeft <= ?2")
//	@Modifying
//	public int updateFastRedeemMax(String oid,BigDecimal fastRedeemMax,String fastRedeemStatus,String operator);
	
	
	@Query(value = "update Product set repayLoanStatus = ?2, repayInterestStatus = ?3 "
			+ "  where oid = ?1 and repayLoanStatus = 'repaying' and  repayInterestStatus = 'repaying' ")
	@Modifying
	public int updateRepayStatus(String oid, String repayLoanStatus, String repayInterestStatus);
	
	@Query(value = "from Product where state ='Durationend' and repayLoanStatus = 'toRepay' and repayDate < ?1 and overdueStatus != 'yes' ")
	public List<Product> getOverdueProduct(Date curDate);
	
	@Query(value = "update Product set repayLoanStatus = 'repaying', repayInterestStatus = 'repaying' "
			+ " where oid = ?1 and repayLoanStatus in ('toRepay', 'repayFailed') and repayInterestStatus in ('toRepay', 'repayFailed') ")
	@Modifying
	public int repayLock(String productOid);
	
	/**
	 * 可售份额申请生效
	 * @param oid
	 * @param applyAmount 申请份额
	 * @param holdTotalVolume 发行人持有份额
	 * @return
	 */
	@Query("update Product set maxSaleVolume = maxSaleVolume + ?2  where oid = ?1  and ?3-maxSaleVolume >= ?2 ")
	@Modifying
	public int updateMaxSaleVolume(String productOid, BigDecimal applyAmount, BigDecimal holdTotalVolume);
	
	/**
	 * 查询有标签的产品所有列表
	 * @param channeOid
	 * @param labelCode
	 * @param nowDate
	 * @param offset
	 * @param rows
	 * @return
	 */
	@Query(value = "SELECT p.oid productOid,p.incomeCalcBasis,d.oid type,p.expAror,p.expArorSec,c.oid channelOid,p.code productCode,p.name productName,p.fullName productFullName, "
			+"p.currentVolume currentVolume,p.collectedVolume collectedVolume,p.lockCollectedVolume lockCollectedVolume,p.investMin investMin,p.lockPeriodDays lockPeriodDays, "
			+"p.durationPeriodDays durationPeriod,p.raisedTotalNumber raisedTotalNumber,p.maxSaleVolume maxSaleVolume,p.state state,p.netUnitShare netUnitShare,p.purchaseNum purchaseNum, p.rewardInterest "
			+ " FROM T_GAM_PRODUCT p "
			+ " INNER JOIN T_GAM_PRODUCT_CHANNEL pc ON pc.productOid = p.oid AND pc.marketState = '"+ProductChannel.MARKET_STATE_Onshelf+"'"
			+ " INNER JOIN T_MONEY_PLATFORM_CHANNEL c ON c.oid = pc.channelOid AND c.oid = ?1 AND c.channelStatus = '"+Channel.CHANNEL_STATUS_ON+"' AND c.deleteStatus = '"+Channel.CHANNEL_DELESTATUS_NO+"'"
			+ " INNER JOIN T_GAM_DICT d ON d.oid = p.type "
			+ " INNER JOIN T_MONEY_PLATFORM_LABEL_PRODUCT pl ON pl.productOid = p.oid "
			+ " INNER JOIN T_MONEY_PLATFORM_LABEL l ON l.oid = pl.labelOid AND l.labelCode = ?2 "
			+ " WHERE p.isDeleted = '"+Product.NO+"' AND p.isOpenPurchase = '"+Product.YES+"'"
			+" AND ((d.oid = '"+Product.TYPE_Producttype_02+"' AND p.state = '"+Product.STATE_Durationing+"' AND p.setupDate <= ?3 ) "
			+" OR (d.oid = '"+Product.TYPE_Producttype_01+"' AND p.state = '"+Product.STATE_Raising+"' AND p.raiseStartDate <= ?3 AND p.raiseEndDate >= ?3 )) "
			+ " ORDER BY pc.rackTime DESC ", nativeQuery = true)
	public List<Object[]> queryLabelProducts(String channeOid, String labelCode, Date nowDate);
	
	
	/**
	 * 获取在售中的产品
	 * @return
	 */
	@Query(value = "SELECT * FROM T_GAM_PRODUCT t1, T_GAM_PRODUCT_CHANNEL t2, T_MONEY_PLATFORM_CHANNEL t3,T_MONEY_PLATFORM_LABEL_PRODUCT t4, "
			 + " T_MONEY_PLATFORM_LABEL t5 WHERE t1.oid = t2.productOid AND t2.channelOid = t3.oid " 
			 + " AND t2.marketState = 'ONSHELF' AND t1.oid =t4.productOid AND t4.labelOid = t5.oid AND t5.labelCode = ?1 AND t1.type = 'PRODUCTTYPE_02'  AND t1.state IN ('DURATIONING') limit 1" 
			 , nativeQuery = true)
	public Product findOnSaleTyjProducts(String labelCode);
	
	/**
	 * 活期产品--基线
	 */
	@Query(value = "select * from T_GAM_PRODUCT t1, T_GAM_PRODUCT_CHANNEL t2, T_MONEY_PLATFORM_CHANNEL t3 where t1.oid = t2.productOid and t2.channelOid = t3.oid and t2.channelOid = ?1 and t2.marketState = 'ONSHELF' and t1.type = 'PRODUCTTYPE_02' and t1.state in ( 'CLEARING', 'DURATIONING', 'CLEARED' ) ORDER BY t2.updateTime DESC limit 2", nativeQuery = true)
	public List<Product> getT0Product(String channelOid);
	
	/**
	 * 体验金产品
	 */
	@Query(value = "select * from T_GAM_PRODUCT t1, T_GAM_PRODUCT_CHANNEL t2, T_MONEY_PLATFORM_CHANNEL t3,T_MONEY_PLATFORM_LABEL_PRODUCT t4, "
			+ " T_MONEY_PLATFORM_LABEL t5 where t1.oid = t2.productOid and t2.channelOid = t3.oid and t2.channelOid = ?1 "
			+ " and t2.marketState = 'ONSHELF' and t1.oid =t4.productOid and t4.labelOid=t5.oid and t5.labelCode='8' and t1.type = 'PRODUCTTYPE_02' "
			+ " and t1.state in ( 'CLEARING', 'DURATIONING', 'CLEARED' ) ORDER BY t2.updateTime DESC limit 1", nativeQuery = true)
	public Product getTyjProduct(String channelOid);
	
	
	/**
	 * 获取活期产品列表--基线
	 */
	@Query(value = "select * from T_GAM_PRODUCT t1, T_GAM_PRODUCT_CHANNEL t2, T_MONEY_PLATFORM_CHANNEL t3 where t1.oid = t2.productOid and t2.channelOid = t3.oid and t2.channelOid = ?1 and t2.marketState = 'ONSHELF' and t1.type = 'PRODUCTTYPE_02' and t1.state in ( 'CLEARING', 'DURATIONING', 'CLEARED' )", nativeQuery = true)
	public List<Product> getT0ProductByChannelOid(String channelOid);
	
	/**
	 * 获取活期产品列表(排除体验金)--基线
	 */
	@Query(value = "select * from T_GAM_PRODUCT t1, T_GAM_PRODUCT_CHANNEL t2, "
			+ " T_MONEY_PLATFORM_CHANNEL t3,T_MONEY_PLATFORM_LABEL t4,T_MONEY_PLATFORM_LABEL_PRODUCT t5 "
			+ " where t1.oid = t2.productOid and t2.channelOid = t3.oid and t2.channelOid = ?1 "
			+ " and t2.marketState = 'ONSHELF' and t1.type = 'PRODUCTTYPE_02' "
			+ " and t1.state in ( 'CLEARING', 'DURATIONING', 'CLEARED' ) "
			+ " and t4.oid=t5.labelOid and t1.oid=t5.productOid and t4.isOk='yes' "
			+ " and t4.labelCode <>'8' ", nativeQuery = true)
	public List<Product> getT0ProductByChannelOidExceptTyj(String channelOid);
	
	/**
	 * 获取活期产品列表(排除体验金和心愿计划的产品)--基线
	 *根据用户的风险评测结果来筛选出在风险评测的范围内的活期产品
	 */
	@Query(value = "select * from T_GAM_PRODUCT t1, T_GAM_PRODUCT_CHANNEL t2, "
			+ " T_MONEY_PLATFORM_CHANNEL t3,T_MONEY_PLATFORM_LABEL t4,T_MONEY_PLATFORM_LABEL_PRODUCT t5 "
			+ " where t1.oid = t2.productOid and t2.channelOid = t3.oid and t2.channelOid = ?1 "
			+ " and t2.marketState = 'ONSHELF' and t1.type = 'PRODUCTTYPE_02' "
			+ " and t1.state in ( 'CLEARING', 'DURATIONING', 'CLEARED' ) "
			+ " and t4.oid=t5.labelOid and t1.oid=t5.productOid and t4.isOk='yes' "
			+ " and t4.labelCode not in ('8','11') "
			+"  and t1.riskLevel in ?2 order by t1.weightValue asc, t1.expAror desc" , nativeQuery = true)
	public List<Product> getT0ProductByChannelOidExceptTyj(String channelOid,List<String> riskString);
	
	
	
	/**
	 * APP和PC获取活期产品(排除体验金)--基线
	 */
	@Query(value = "select * from T_GAM_PRODUCT t1, T_GAM_PRODUCT_CHANNEL t2, "
			+ " T_MONEY_PLATFORM_CHANNEL t3,T_MONEY_PLATFORM_LABEL t4,T_MONEY_PLATFORM_LABEL_PRODUCT t5 "
			+ " where t1.oid = t2.productOid and t2.channelOid = t3.oid and t2.channelOid = ?1 "
			+ " and t2.marketState = 'ONSHELF' and t1.type = 'PRODUCTTYPE_02' "
			+ " and t1.state in ( 'CLEARING', 'DURATIONING', 'CLEARED' ) "
			+ " and t4.oid=t5.labelOid and t1.oid=t5.productOid and t4.isOk='yes' "
			+ " and t4.labelCode <>'8' ORDER BY t2.updateTime DESC limit 1", nativeQuery = true)
	public List<Product> getT0ProductByChannelOidExceptTyj4APPAndPC(String channelOid);
	
	/**
	 * 新手标
	 */
	@Query(value = "SELECT t1.* FROM T_GAM_PRODUCT t1 , T_MONEY_PLATFORM_LABEL_PRODUCT t2, T_MONEY_PLATFORM_LABEL t3, T_GAM_PRODUCT_CHANNEL t4, "
			+ " T_MONEY_PLATFORM_CHANNEL t5 WHERE t1.oid = t2.productOid " 
			+  " AND t2.labelOid = t3.oid AND t4.productOid = t1.oid AND t4.channelOid = t5.oid AND t5.oid = ?1 AND t4.marketState = 'ONSHELF' "
			+ " AND t3.labelCode = '1' AND t1.state in ('RAISING') order by t4.rackTime LIMIT 1", nativeQuery = true)
	public Product getNewBieProduct(String channelOid);
	
	
	@Query("update Product set auditState = 'Locking'  where oid = ?1 and  auditState = 'REVIEWED' ")
	@Modifying
	public int lockProduct(String productOid);
	
	
	@Query("update Product set auditState = 'REVIEWED'  where oid = ?1 and  auditState = 'Locking' ")
	@Modifying
	public int unLockProduct(String productOid);
	
	@Query(value = "update Product set repayInterestStatus = 'repayed' "
			+ "  where oid = ?1 and  repayInterestStatus = 'repaying' ")
	@Modifying
	public int repayInterestOk(String productOid);
	
	@Query(value = "update Product set repayInterestStatus = 'repaying' "
			+ " where oid = ?1 and repayInterestStatus in ('toRepay', 'repayFailed') ")
	@Modifying
	public int repayInterestLock(String productOid);
	
	@Query(value = "update Product set repayLoanStatus = 'repaying' "
			+ " where oid = ?1 and repayLoanStatus in ('toRepay', 'repayFailed') and  repayInterestStatus = 'repayed' ")
	@Modifying
	public int repayLoanLock(String productOid);
	
	@Query(value = "update Product set repayLoanStatus = ?2 "
			+ " where oid = ?1 and repayLoanStatus in ('repaying') and  repayInterestStatus = 'repayed' ")
	@Modifying
	public int repayLoanEnd(String productOid, String repayLoanStatus);
	
	@Query(value = "from Product WHERE state in ('RAISING', 'RAISEEND', 'DURATIONING', 'DURATIONEND', 'CLEARING') and publisherBaseAccount = ?1")
	public List<Product> productAmount(PublisherBaseAccountEntity ba);
	
	@Query(value = "select count(*) from T_GAM_PRODUCT where state = 'CLEARED' and spvOid = ?1 ", nativeQuery = true)
	public int closedProductAmount(String publsiherOid);
	
	@Query(value = "from Product WHERE state in ('RAISING', 'RAISEEND', 'DURATIONING', 'DURATIONEND', 'CLEARING') ")
	public List<Product> productAmount();
	
	@Query(value = "select count(*) from T_GAM_PRODUCT where state = 'CLEARED' ", nativeQuery = true)
	public int closedProductAmount();
	
	@Query(value = "select * from T_GAM_PRODUCT order by createTime desc limit 10", nativeQuery = true)
	public List<Product> getLatestTen();

	@Query(value = "update Product set previousCurVolume = previousCurVolume - ?2 where oid = ?1 and previousCurVolume - ?2 >= 0 ")
	@Modifying
	public int updatePreviousCurVolume(String oid, BigDecimal orderVolume);
	
	@Query(value = "select * from T_GAM_PRODUCT where type = 'PRODUCTTYPE_02' and state in ('DURATIONING', 'CLEARING') ", nativeQuery = true)
	public List<Product> queryT0Products();
	@Query(value = "from Product WHERE portfolio.oid = ?1")
	public Product findByPortfolio(String oid);
	
	/**家加财新增代码---通过id查询产品实体*/
	public Product findByOid(String Oid);

	@Query(value="select * from T_GAM_PRODUCT t where t.oid = ?1 and t.state in('DURATIONING','DURATIONEND','CLEARING','CLEARED') ",nativeQuery = true)
	public Product findByOidAndStatus(String productOid);
}	

