package com.guohuai.mmp.publisher.bankorder;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface PublisherBankOrderDao extends JpaRepository<PublisherBankOrderEntity, String>, JpaSpecificationExecutor<PublisherBankOrderEntity> {
	
	/**
	 * 根据<<订单号>>查询<<投资人-银行委托单>>
	 * @param orderCode
	 * @return
	 */
	public PublisherBankOrderEntity findByOrderCodeAndOrderStatusAndOrderType(String orgCode, String bankOrderOrderStatusSubmitted, String bankOrderOrderTypeDeposit);
	
	
	public PublisherBankOrderEntity findByOidAndOrderStatusAndOrderType(String oid, String bankOrderOrderStatusSubmitted, String bankOrderOrderTypeDeposit);


	public PublisherBankOrderEntity findByOrderCode(String orderCode);

	@Query(value = "SELECT SUM(a.orderAmount),MAX(a.completeTime),a.publisherOid "
			+ "FROM T_MONEY_PUBLISHER_BANKORDER a "
			+ "WHERE a.orderStatus = 'done' and a.orderType in ('deposit','depositLong')"
			+ "GROUP BY a.publisherOid", nativeQuery = true)
	public List<Object[]> getPublisherDepositAmount();
	@Query(value = "SELECT SUM(a.orderAmount),MAX(a.completeTime),a.publisherOid "
			+ "FROM T_MONEY_PUBLISHER_BANKORDER a "
			+ "WHERE a.orderStatus = 'done' and a.orderType in ('withdraw','withdrawLong') "
			+ "GROUP BY a.publisherOid", nativeQuery = true)
	public List<Object[]> getPublisherWithdrawAmount();
}
