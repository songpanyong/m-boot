package com.guohuai.mmp.investor.bank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountEntity;
/**
 * dao for User and debit card relationship
 * @author Jeffrey.Wong
 * 2015年7月4日下午2:21:00
 */
public interface BankDao extends JpaRepository<BankEntity, String>, JpaSpecificationExecutor<BankEntity>{
	public BankEntity findByInvestorBaseAccount(InvestorBaseAccountEntity account);
		
	public BankEntity findByInvestorBaseAccountAndBindStatus(InvestorBaseAccountEntity account, String bindStatus);
	
	public BankEntity findByDebitCardAndBindStatus(String debitCard, String bindStatus);
	
	public BankEntity findByIdCardAndBindStatus(String idCard, String bindStatus);
	
	/**
	 * 获取绑卡成功记录
	 * @param investorOid
	 * @return
	 */
	@Query("FROM BankEntity WHERE investorBaseAccount.oid = ?1 AND bindStatus = 'ok'")
	public BankEntity getOKBankByInvestorOid(String investorOid);
}
