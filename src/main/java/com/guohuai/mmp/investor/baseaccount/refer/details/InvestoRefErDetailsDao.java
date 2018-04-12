package com.guohuai.mmp.investor.baseaccount.refer.details;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountEntity;
import com.guohuai.mmp.investor.baseaccount.referee.InvestorRefEreeEntity;

public interface InvestoRefErDetailsDao extends JpaRepository<InvestoRefErDetailsEntity, String>, JpaSpecificationExecutor<InvestoRefErDetailsEntity> {

	/**
	 * 推荐排名统计，前10名
	 * @return
	 */
	@Query(value = "SELECT b.phoneNum, b.realName, COUNT(*) AS total "
			+ "FROM T_MONEY_INVESTOR_BASEACCOUNT_REFER_DETAILS s "
			+ "LEFT JOIN T_MONEY_INVESTOR_BASEACCOUNT_REFEREE r "
			+ "ON s.refereeOid = r.oid "
			+ "LEFT JOIN T_MONEY_INVESTOR_BASEACCOUNT b "
			+ "ON r.investorOid = b.oid "
			+ "GROUP BY s.refereeOid ORDER BY total DESC LIMIT 10", nativeQuery = true)
	public List<Object[]> recommendRankTOP10();	
	
	public List<InvestoRefErDetailsEntity> findByInvestorBaseAccount(InvestorBaseAccountEntity account);
	
	public List<InvestoRefErDetailsEntity> findByInvestorRefEree(InvestorRefEreeEntity investorRefEreeEntity);
}
