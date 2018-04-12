package com.guohuai.mmp.publisher.baseaccount;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface PublisherBaseAccountDao extends JpaRepository<PublisherBaseAccountEntity, String>, JpaSpecificationExecutor<PublisherBaseAccountEntity> {

	@Query(value = "update PublisherBaseAccountEntity set basicBalance = ?2, "
			+ " collectionSettlementBalance = ?3, availableAmountBalance = ?4, "
			+ " frozenAmountBalance = ?5, withdrawAvailableAmountBalance = ?6 where oid = ?1")
	@Modifying
	public int updateBalance(String baseAccountOid, 
			BigDecimal basicBalance, BigDecimal collectionSettlementBalance, 
			BigDecimal availableAmountBalance, BigDecimal frozenAmountBalance, BigDecimal withdrawAvailableAmountBalance);
	

	@Query("from PublisherBaseAccountEntity s where s.status = ?1 order by s.updateTime desc")
	public List<PublisherBaseAccountEntity> findByStatus(String status);
	

	@Query(value="SELECT oid,corperateOid FROM T_MONEY_PUBLISHER_BASEACCOUNT",nativeQuery=true)
	public List<Object[]> findOneOid();

	@Query(value = "select * from T_MONEY_PUBLISHER_BASEACCOUNT order by createTime desc limit 10 ", nativeQuery = true)
	public List<PublisherBaseAccountEntity> getLatestTen();

	
	public PublisherBaseAccountEntity findByPhone(String phone);
}
