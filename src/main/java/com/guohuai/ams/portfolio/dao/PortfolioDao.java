package com.guohuai.ams.portfolio.dao;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.guohuai.ams.portfolio.entity.PortfolioEntity;

public interface PortfolioDao extends JpaRepository<PortfolioEntity, String>, JpaSpecificationExecutor<PortfolioEntity> {

	@Query(value = "SELECT * FROM T_GAM_PORTFOLIO a WHERE a.state = 'duration' order by a.createTime desc LIMIT 1", nativeQuery = true)
	public PortfolioEntity getLimitOne();

	//逻辑删除
	@Query(value = "update T_GAM_PORTFOLIO set state = 'invalid' where oid = ?1", nativeQuery = true)
	@Modifying
	public void update(String pid);

	//物理删除
	@Query(value = "delete from T_GAM_PORTFOLIO where oid = ?1", nativeQuery = true)
	@Modifying
	public void delete(String pid);

	// 获取所有已成立的资产池id和名称列表
	@Query(value = "SELECT a.oid, a.name FROM T_GAM_PORTFOLIO a WHERE a.state = 'DURATION' order by a.createTime desc", nativeQuery = true)
	public List<Object> findAllNameList();

	public List<PortfolioEntity> findByState(String state);

	@Query("update PortfolioEntity p set p.cashPosition = p.cashPosition + ?2 where p.oid = ?1")
	@Modifying
	public int incrCashPosition(String oid, BigDecimal value);

	@Query("update PortfolioEntity p set p.cashPosition = p.cashPosition - ?2 where p.oid = ?1")
	@Modifying
	public int decrCashPosition(String oid, BigDecimal value);

	// 

	@Query("update PortfolioEntity p set p.dimensions = p.dimensions + ?2 where p.oid = ?1")
	@Modifying
	public int incrDimensions(String oid, BigDecimal value);

	@Query("update PortfolioEntity p set p.dimensions = p.dimensions - ?2 where p.oid = ?1")
	@Modifying
	public int decrDimensions(String oid, BigDecimal value);

	@Query("update PortfolioEntity p set p.liquidDimensions = p.liquidDimensions + ?2 where p.oid = ?1")
	@Modifying
	public int incrLiquidDimensions(String oid, BigDecimal value);

	@Query("update PortfolioEntity p set p.liquidDimensions = p.liquidDimensions - ?2 where p.oid = ?1")
	@Modifying
	public int decrLiquidDimensions(String oid, BigDecimal value);

	@Query("update PortfolioEntity p set p.illiquidDimensions = p.illiquidDimensions + ?2 where p.oid = ?1")
	@Modifying
	public int incrIlliquidDimensions(String oid, BigDecimal value);

	@Query("update PortfolioEntity p set p.illiquidDimensions = p.illiquidDimensions - ?2 where p.oid = ?1")
	@Modifying
	public int decrIlliquidDimensions(String oid, BigDecimal value);

	@Query("update PortfolioEntity p set p.deviationValue = p.deviationValue + ?2 where p.oid = ?1")
	@Modifying
	public int incrDeviationValue(String oid, BigDecimal value);

	@Query("update PortfolioEntity p set p.deviationValue = p.deviationValue - ?2 where p.oid = ?1")
	@Modifying
	public int decrDeviationValue(String oid, BigDecimal value);

	@Query("update PortfolioEntity p set p.freezeCash = p.freezeCash + ?2 where p.oid = ?1")
	@Modifying
	public int incrFreezeCash(String oid, BigDecimal value);

	@Query("update PortfolioEntity p set p.freezeCash = p.freezeCash - ?2 where p.oid = ?1")
	@Modifying
	public int decrFreezeCash(String oid, BigDecimal value);

	@Query("update PortfolioEntity p set p.drawedChargefee = p.drawedChargefee + ?2 where p.oid = ?1")
	@Modifying
	public int incrDrawedChargefee(String oid, BigDecimal value);

	@Query("update PortfolioEntity p set p.drawedChargefee = p.drawedChargefee - ?2 where p.oid = ?1")
	@Modifying
	public int decrDrawedChargefee(String oid, BigDecimal value);

	@Query("update PortfolioEntity p set p.countintChargefee = p.countintChargefee + ?2 where p.oid = ?1")
	@Modifying
	public int incrCountintChargefee(String oid, BigDecimal value);

	@Query("update PortfolioEntity p set p.countintChargefee = p.countintChargefee - ?2 where p.oid = ?1")
	@Modifying
	public int decrCountintChargefee(String oid, BigDecimal value);

	@Query("update PortfolioEntity p set p.dimensionsDate = ?2 where p.oid = ?1")
	@Modifying
	public int updateDimensionsDate(String oid, Date dimensionsDate);
	
	/**
	 * JJC
	 * @param productOid
	 * @return
	 */
	@Query(value = "SELECT * FROM T_GAM_PORTFOLIO a, T_GAM_PRODUCT b WHERE b.oid = ?1 AND a.oid = b.assetPoolOid", nativeQuery = true)
	public PortfolioEntity queryByProductOid(String productOid);
	
	/**
	 * queryByProductOids
	 * @param productOids
	 * @return
	 */
	@Query(value = "SELECT * FROM T_GAM_PORTFOLIO a, T_GAM_PRODUCT b WHERE b.oid IN (?1) AND a.oid = b.assetPoolOid", nativeQuery = true)
	public List<PortfolioEntity> queryByProductOids(List<String> productOids);
	
	public List<PortfolioEntity> findByState(String uid, String type);

}
