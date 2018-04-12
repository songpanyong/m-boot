package com.guohuai.mmp.publisher.investor.holdapartincome;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.guohuai.basic.component.ext.web.PageResp;
import com.guohuai.component.util.BigDecimalUtil;
import com.guohuai.component.util.DateUtil;
import com.guohuai.component.util.StringUtil;

@Service
@Transactional
public class PartIncomeService {
	
	@Value("${investor.info.kickstar:true}")
	private boolean kickstar = true;

	@Autowired
	private PartIncomeDao daoPartIncome;

	@PersistenceContext
	private EntityManager em;

	
	/**
	 * 按计息日，产品统计分仓合计所得总收益，计息总份额
	 * @param productOid
	 * @param incomeDate
	 * @return
	 */
	public List<Object[]> calcSumInterest(String productOid, Date incomeDate){
		return this.daoPartIncome.calcSumInterest(productOid, incomeDate);
	}
	
	/**
	 * 根据产品和确认收益日获取分仓明细列表
	 * @param productOid
	 * @param confirmDate
	 * @return
	 */
	public List<PartIncomeEntity> getByPoidAndConfirmDate(String productOid, Date confirmDate){
		return this.daoPartIncome.getByPoidAndConfirmDate(productOid, confirmDate);
	}

	public void saveBatch(List<PartIncomeEntity> partIncomeList) {
		this.daoPartIncome.save(partIncomeList);
		
	}


	public PartIncomeEntity queryApartIncome(String investorOid, String tradeOrderOid, String incomeDate) {
		return this.daoPartIncome.queryApartIncome(investorOid, tradeOrderOid, incomeDate);
		
	}

	public PartIncomeEntity queryApartIncomeInHis(String investorOid, String tradeOrderOid, String incomeDate) {
		StringBuilder sb = new StringBuilder();
		
		sb.append("SELECT t1.* FROM T_MONEY_PUBLISHER_INVESTOR_INCOME").append(incomeDate.replace("-", ""))
		.append(" t1, T_MONEY_INVESTOR_TRADEORDER t2 WHERE t1.orderOid = t2.oid and  t1.confirmDate = '").append(incomeDate).append("'")
		.append(" and t1.investorOid = '").append(investorOid).append("' and t2.orderCode = '").append(tradeOrderOid).append("'");
		

		@SuppressWarnings("unchecked")
		List<PartIncomeEntity> list = em.createNativeQuery(sb.toString(), PartIncomeEntity.class).getResultList();
		if (!list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}
	
	/** 我的活期奖励阶段收益明细 */
	public PageResp<MyLevelHoldIncomeRep> queryHoldApartIncomeAndLevel(String userOid, String productOid, String level,
			int startLine, int rows) {

		PageResp<MyLevelHoldIncomeRep> pagesRep = new PageResp<MyLevelHoldIncomeRep>();
		List<Object[]> list = this.daoPartIncome.queryHoldApartIncomeAndLevel(userOid, productOid, level, startLine,
				rows);
		if (list != null && list.size() > 0) {
			String nextLevelstartDate = null;
			Object rewardOidObj = list.get(0)[5];
			if (!StringUtils.isEmpty(rewardOidObj)) {
				nextLevelstartDate = this.daoPartIncome.queryNextLevelStartDate(productOid,
						rewardOidObj.toString());
			}
			List<MyLevelHoldIncomeRep> recordRows = new ArrayList<MyLevelHoldIncomeRep>();
			for (Object[] arr : list) {
				MyLevelHoldIncomeRep queryRep = new MyLevelHoldIncomeRep();
				// 单位净值
				BigDecimal netUnitShare = BigDecimalUtil.parseFromObject(arr[3]);
				// 当前金额=持有份额*单位净值
				queryRep.setValue(netUnitShare.multiply(BigDecimalUtil.parseFromObject(arr[0])));
				// 投资金额=投资份额*单位净值
				queryRep.setInvestAmt(netUnitShare.multiply(BigDecimalUtil.parseFromObject(arr[1])));
				// 投资日期
				if (!StringUtils.isEmpty(arr[2])) {
					queryRep.setInvestDate(DateUtil.parseDate(arr[2].toString(), DateUtil.datetimePattern));
				}
				// 下一次升档日期=投资日期+下一次升档开始天数
				//（最后一个等级的时候，没有下次升档时间，返回给界面null，界面判断显示文字）
				if (!StringUtils.isEmpty(nextLevelstartDate)) {
					queryRep.setUpLevelDate(DateUtil.addDays(queryRep.getInvestDate(), Integer.parseInt(nextLevelstartDate)));
				}

				recordRows.add(queryRep);
			}
			pagesRep.setRows(recordRows);
		}
		pagesRep.setTotal(this.daoPartIncome.counntHoldApartIncomeAndLevel(userOid, productOid, level));

		return pagesRep;
	}
	
	
	public PageResp<HoldApartIncomeQueryRep> mng(Specification<PartIncomeEntity> spec, Pageable pageable) {
		Page<PartIncomeEntity> cas = this.daoPartIncome.findAll(spec, pageable);
		PageResp<HoldApartIncomeQueryRep> pagesRep = new PageResp<HoldApartIncomeQueryRep>();
		if (cas != null && cas.getContent() != null && cas.getTotalElements() > 0) {
			for (PartIncomeEntity en : cas) {
				HoldApartIncomeQueryRep queryRep = new HoldApartIncomeQueryRep();
				queryRep.setPhoneNum(this.kickstar ? StringUtil.kickstarOnPhoneNum(en.getInvestorBaseAccount().getPhoneNum()) : en.getInvestorBaseAccount().getPhoneNum());
				queryRep.setAccureVolume(en.getAccureVolume());
				queryRep.setBaseAmount(en.getBaseAmount());
				queryRep.setRewardAmount(en.getRewardAmount());
				
				BigDecimal baseAmount=en.getBaseAmount()==null?new BigDecimal(0):en.getBaseAmount();
				BigDecimal rewardAmount=en.getRewardAmount()==null?new BigDecimal(0):en.getRewardAmount();
				BigDecimal couponAmount=en.getCouponAmount()==null?new BigDecimal(0):en.getCouponAmount();
				
				queryRep.setIncomeAmount(baseAmount.setScale(2, BigDecimal.ROUND_DOWN)
						.add(rewardAmount.setScale(2, BigDecimal.ROUND_DOWN))
						.add(couponAmount.setScale(2, BigDecimal.ROUND_DOWN)));
				queryRep.setConfirmDate(en.getConfirmDate());
				queryRep.setCouponAmount(en.getCouponAmount());
				
				pagesRep.getRows().add(queryRep);
			}	
		}
		pagesRep.setTotal(cas.getTotalElements());
		return pagesRep;
	}

}

 