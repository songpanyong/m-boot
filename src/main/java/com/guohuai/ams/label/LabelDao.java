package com.guohuai.ams.label;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;


public interface LabelDao extends JpaRepository<LabelEntity, String>, JpaSpecificationExecutor<LabelEntity> {

	/**
	 * 根据产品ID查询可用的基本标签
	 * @param productId
	 * @return
	 */
	@Query(value="SELECT count(*) FROM T_MONEY_PLATFORM_LABEL a  INNER JOIN T_MONEY_PLATFORM_LABEL_PRODUCT b ON a.oid=b.labelOid"
			+ " WHERE a.isOk='yes' AND b.productOid=?1  and a.labelCode='8' ", nativeQuery = true)
	public Integer findLabelByProductId(String productId);
	/**
	 * 获取产品渠道已上架的体验金数量
	 * @param channelOid
	 * @return
	 */
	@Query(value="SELECT count(*) FROM T_MONEY_PLATFORM_LABEL a  INNER JOIN T_MONEY_PLATFORM_LABEL_PRODUCT b ON a.oid=b.labelOid INNER JOIN T_GAM_PRODUCT_CHANNEL c ON b.productOid=c.productOid" 
			+ " WHERE a.isOk='yes' AND c.channelOid=?1  AND a.labelCode='8' AND c.marketState='ONSHELF'", nativeQuery = true)
	public Integer findLabel4ProductChannel(String channelOid);
	
	/**
	 * 根据产品ID查询可用标签Code
	 * @return
	 */
	@Query(value="SELECT labelCode,labelName "
			+ "FROM T_MONEY_PLATFORM_LABEL a "
			+ "INNER JOIN T_MONEY_PLATFORM_LABEL_PRODUCT b ON a.oid=b.labelOid "
			+ "WHERE a.isOk='yes' AND b.productOid=?1", nativeQuery = true)
	public List<Object[]> findLabelCodeByProductId(String productId);
	
}
