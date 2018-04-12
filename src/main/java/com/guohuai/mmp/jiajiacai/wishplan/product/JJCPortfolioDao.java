package com.guohuai.mmp.jiajiacai.wishplan.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.guohuai.mmp.jiajiacai.wishplan.product.entity.JJCPortfolioEntity;

public interface JJCPortfolioDao extends JpaRepository<JJCPortfolioEntity,String>{

	/**
	 *  根据产品ID查询资管系统中的资产组合要素表 T_GAM_PORTFOLIO 中，各种资产的比例
	 */
	@Query(value = "SELECT p.oid,f.liquidRate,f.illiquidRate,f.cashRate FROM t_gam_product p JOIN t_gam_portfolio f ON p.assetPoolOid = f.oid WHERE p.oid=:oid", nativeQuery = true)
	public JJCPortfolioEntity getPortfolioAssetRatio(@Param("oid") String oid);
	
}
