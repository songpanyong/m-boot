package com.guohuai.mmp.investor.cashflow;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface InvestorCashFlowDao extends JpaRepository<InvestorCashFlowEntity, String>, JpaSpecificationExecutor<InvestorCashFlowEntity> {
	
	
	@Query(value = "INSERT INTO T_MONEY_INVESTOR_CASHFLOW (oid, investorOid, orderOid, tradeAmount, tradeType, wishplanOid)"
			+ "	select replace(uuid(), '-', ''), investorOid, orderOid, orderAmount, 'dividend', wishplanOid from t_money_dividend_orders_md  ", nativeQuery = true)
	@Modifying
	int createDividendCashFlow();
	
	InvestorCashFlowEntity findByOrderOid(String orderOid);
}
