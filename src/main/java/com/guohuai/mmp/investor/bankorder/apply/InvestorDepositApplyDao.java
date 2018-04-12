package com.guohuai.mmp.investor.bankorder.apply;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface InvestorDepositApplyDao extends JpaRepository<InvestorDepositApplyEntity, String>,
		JpaSpecificationExecutor<InvestorDepositApplyEntity> {

	InvestorDepositApplyEntity findByPayNoAndOrderAmountAndInvestorOid(String payNo, BigDecimal orderAmount,
			String investorOid);
	
	InvestorDepositApplyEntity findByPayNoAndInvestorOid(String payNo, String investorOid);
}
