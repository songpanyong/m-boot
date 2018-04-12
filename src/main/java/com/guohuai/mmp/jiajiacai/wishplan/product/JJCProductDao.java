package com.guohuai.mmp.jiajiacai.wishplan.product;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.guohuai.mmp.jiajiacai.wishplan.plan.entity.WishplanProduct;


public interface JJCProductDao extends JpaRepository<WishplanProduct,String>{	
	
	@Query(value = "SELECT p.* FROM T_GAM_PRODUCT p, T_GAM_PRODUCT_CHANNEL c, T_MONEY_PLATFORM_LABEL t4, T_MONEY_PLATFORM_LABEL_PRODUCT t5"
			    + " WHERE p.oid NOT IN (?3) AND c.marketState = 'ONSHELF'"
				+ " AND p.TYPE = 'PRODUCTTYPE_01' AND p.investMin <= 1 AND (p.maxSaleVolume - p.lockCollectedVolume > p.investMin) AND p.state='RAISING'" 
				+ " AND p.repayDate <= ?1"
				+ " AND p.riskLevel IN (?2) AND p.oid = c.productOid"
				+ " AND (p.dealStartTime = '' OR (TIME(p.dealEndTime) >= NOW() AND TIME(p.dealStartTime) <= NOW()))"
			    + " AND t4.oid = t5.labelOid and p.oid = t5.productOid and t4.isOk = 'yes' AND t4.labelCode IN ('11')"
				+ " UNION ALL SELECT p.* FROM T_GAM_PRODUCT p, T_GAM_PRODUCT_CHANNEL c, T_MONEY_PLATFORM_LABEL t4, T_MONEY_PLATFORM_LABEL_PRODUCT t5 WHERE c.marketState = 'ONSHELF'" 
				+ " AND p.TYPE='PRODUCTTYPE_02' AND p.investMin <= 1 AND (p.maxSaleVolume - p.lockCollectedVolume > p.investMin)"
				+ " AND p.state='DURATIONING' AND p.riskLevel IN (?2) AND p.oid = c.productOid" 
				+ " AND (p.dealStartTime = '' OR (TIME(p.dealEndTime) >= NOW() AND TIME(p.dealStartTime) <= NOW()))"
				+ " AND t4.oid = t5.labelOid and p.oid = t5.productOid and t4.isOk = 'yes' AND t4.labelCode IN ('11')"
				+ " ORDER BY expAror DESC, riskLevel DESC, type ASC, durationPeriodDays DESC limit 1" , nativeQuery = true)
	WishplanProduct findMaxProductRateListByDate(Date repayDate, List<String> level, List<String> excludes);
/*
	@Query(value = "SELECT p.* FROM T_GAM_PRODUCT p, T_GAM_PRODUCT_CHANNEL c, T_MONEY_PLATFORM_LABEL t4, T_MONEY_PLATFORM_LABEL_PRODUCT t5"
		    + " WHERE p.oid NOT IN (?3) AND c.marketState = 'ONSHELF'"
			+ " AND p.TYPE = 'PRODUCTTYPE_01' AND p.investMin <= 1 AND (p.maxSaleVolume - p.lockCollectedVolume > p.investMin) AND p.state='RAISING'" 
			+ " AND (p.durationPeriodDays + p.foundDays + p.purchaseConfirmDays + p.redeemConfirmDays + p.accrualRepayDays"
			+ " + DATEDIFF(DATE_FORMAT(p.raiseEndDate, \"%y-%m-%d\"), CURDATE()) ) <= ?1"
			+ " AND p.riskLevel IN (?2) AND p.oid = c.productOid"
			+ " AND (p.dealStartTime = '' OR (TIME(p.dealEndTime) >= NOW() AND TIME(p.dealStartTime) <= NOW()))"
		    + " AND t4.oid = t5.labelOid and p.oid = t5.productOid and t4.isOk = 'yes' AND t4.labelCode IN ('11')"
			+ " UNION ALL SELECT p.* FROM T_GAM_PRODUCT p, T_GAM_PRODUCT_CHANNEL c, T_MONEY_PLATFORM_LABEL t4, T_MONEY_PLATFORM_LABEL_PRODUCT t5 WHERE c.marketState = 'ONSHELF'" 
			+ " AND p.TYPE='PRODUCTTYPE_02' AND p.investMin <= 1 AND (p.maxSaleVolume - p.lockCollectedVolume > p.investMin)"
			+ " AND p.state='DURATIONING' AND p.riskLevel IN (?2) AND p.oid = c.productOid" 
			+ " AND (p.dealStartTime = '' OR (TIME(p.dealEndTime) >= NOW() AND TIME(p.dealStartTime) <= NOW()))"
			+ " AND t4.oid = t5.labelOid and p.oid = t5.productOid and t4.isOk = 'yes' AND t4.labelCode IN ('11')"
			+ " ORDER BY expAror DESC, riskLevel DESC, type ASC, durationPeriodDays DESC limit 1" , nativeQuery = true)
   WishplanProduct findMaxProductRateList(int duration, List<String> level, List<String> excludes);
*/
	
	
//	@Query(value = "SELECT p.* FROM T_GAM_PRODUCT p, T_GAM_PRODUCT_CHANNEL c"
//			+ " WHERE c.marketState = 'ONSHELF'"
//			+ " AND p.TYPE='PRODUCTTYPE_02' AND p.investMin <= 1 AND (p.maxSaleVolume - p.lockCollectedVolume > p.investMin)  AND p.dealStartTime = ''"
//			+ " AND p.state='DURATIONING' AND p.riskLevel IN (?1) AND p.oid = c.productOid"
//			+ " AND (p.dealStartTime = '' OR (TIME(p.dealEndTime) >= NOW() AND TIME(p.dealStartTime) <= NOW()))"
//			+ " ORDER BY expAror, riskLevel DESC limit 1" , nativeQuery = true)
//	WishplanProduct findOpenProductRateList(List<String> level);
	
	@Query(value = "SELECT p.* FROM T_GAM_PRODUCT p, T_GAM_PRODUCT_CHANNEL c, T_MONEY_PLATFORM_LABEL t4, T_MONEY_PLATFORM_LABEL_PRODUCT t5"
			+ " WHERE c.marketState = 'ONSHELF' AND p.investMin <= 1"
			+ " AND p.TYPE='PRODUCTTYPE_02' AND (p.maxSaleVolume - p.lockCollectedVolume > p.investMin) AND p.dealStartTime = ''"
			+ " AND p.state='DURATIONING' AND p.riskLevel IN (?1) AND p.oid = c.productOid AND p.oid NOT IN (?2)"
			+ " AND (p.dealStartTime = '' OR (TIME(p.dealEndTime) >= NOW() AND TIME(p.dealStartTime) <= NOW()))"
			+ " AND t4.oid = t5.labelOid and p.oid = t5.productOid and t4.isOk = 'yes' AND t4.labelCode IN ('11')"
			+ " ORDER BY expAror DESC, riskLevel DESC limit 1" , nativeQuery = true)
	WishplanProduct findOpenProductRateListExclude(List<String> level,  List<String> excludes);
	
	@Query(value = "SELECT maxSaleVolume - lockCollectedVolume FROM T_GAM_PRODUCT WHERE oid=?1", nativeQuery = true)
	BigDecimal queryMaxSaleVolume(String oid);
	
	 
	@Query(value = "update T_GAM_PRODUCT set lockCollectedVolume = lockCollectedVolume + ?2  where oid = ?1", nativeQuery = true)
	@Modifying
	public int update4InvestLockVolume(String oid, BigDecimal orderVolume);

}
