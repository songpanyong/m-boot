package com.guohuai.mmp.publisher.investor;

import java.math.BigDecimal;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guohuai.ams.duration.fact.income.IncomeAllocate;
import com.guohuai.ams.duration.fact.income.IncomeAllocateDao;
import com.guohuai.ams.duration.fact.income.IncomeEvent;
import com.guohuai.ams.duration.fact.income.IncomeEventDao;
import com.guohuai.component.util.DateUtil;
import com.guohuai.component.util.StringUtil;
import com.guohuai.mmp.jiajiacai.caculate.JJCUtility;

@Service
@Transactional
public class InterestRequireNew {
	
	@Autowired
	private IncomeEventDao incomeEventDao;
	@Autowired
	private IncomeAllocateDao incomeAllocateDao;
	
	
	@Transactional(value = TxType.REQUIRES_NEW)
	public IncomeAllocate newAllocate(InterestReq ireq) {
		IncomeEvent incomeEvent = new IncomeEvent();
		incomeEvent.setOid(StringUtil.uuid());
		incomeEvent.setPortfolio(ireq.getProduct().getPortfolio());
		incomeEvent.setBaseDate(ireq.getIncomeDate());
		incomeEvent.setAllocateIncome(ireq.getIncomeAmount().add(ireq.getIncomeCouponAmount()));// 总分配收益
		//Truncated
		incomeEvent.setAllocateIncome(JJCUtility.bigKeep4Decimal(incomeEvent.getAllocateIncome()));
		incomeEvent.setCreator("system");
		incomeEvent.setCreateTime(DateUtil.getSqlCurrentDate());
		incomeEvent.setDays(1);
		incomeEvent.setStatus(IncomeEvent.STATUS_Create);
		incomeEventDao.save(incomeEvent);
		
		
		IncomeAllocate incomeAllocate = new IncomeAllocate();
		incomeAllocate.setOid(StringUtil.uuid());
		incomeAllocate.setIncomeEvent(incomeEvent);
		incomeAllocate.setProduct(ireq.getProduct());
		incomeAllocate.setAllocateIncomeType(ireq.getIncomeType());
		incomeAllocate.setBaseDate(incomeEvent.getBaseDate());
		incomeAllocate.setCapital(ireq.getTotalInterestedVolume());// 产品可计息规模
		incomeAllocate.setAllocateIncome(ireq.getIncomeAmount());
		//Truncated
		incomeAllocate.setAllocateIncome(JJCUtility.bigKeep4Decimal(incomeAllocate.getAllocateIncome()));
		
		incomeAllocate.setLeftAllocateBaseIncome(ireq.getIncomeAmount());
		incomeAllocate.setLeftAllocateRewardIncome(BigDecimal.ZERO);
		incomeAllocate.setLeftAllocateCouponIncome(ireq.getIncomeCouponAmount());// 剩余加息收益
		incomeAllocate.setRewardIncome(BigDecimal.ZERO);// 奖励收益
		incomeAllocate.setCouponIncome(ireq.getIncomeCouponAmount());// 加息收益
		incomeAllocate.setRatio(ireq.getRatio());// 年化收益
		
//		incomeAllocate.setWincome(InterestFormula.compound(new BigDecimal(10000),
//				ireq.getProduct().getRecPeriodExpAnYield(), ireq.getProduct().getIncomeCalcBasis())); // 万份收益
		incomeAllocate.setWincome(InterestFormula.compound(new BigDecimal(10000),
				ireq.getRatio(), ireq.getProduct().getIncomeCalcBasis())); // 万份收益
		
		incomeAllocate.setDays(1);// 收益分配天数
		incomeAllocate.setSuccessAllocateIncome(BigDecimal.ZERO);// 成功分配基础收益
		incomeAllocate.setSuccessAllocateRewardIncome(BigDecimal.ZERO);
		incomeAllocate.setSuccessAllocateCouponIncome(BigDecimal.ZERO);//成功分配加息收益
		incomeAllocate.setLeftAllocateIncome(incomeAllocate.getAllocateIncome().add(incomeAllocate.getRewardIncome()).add(incomeAllocate.getCouponIncome()));// 剩余收益
		//Truncated
		incomeAllocate.setLeftAllocateIncome(JJCUtility.bigKeep4Decimal(incomeAllocate.getLeftAllocateIncome()));
				
		incomeAllocate.setSuccessAllocateInvestors(0);// 成功分配投资者数
		incomeAllocate.setFailAllocateInvestors(0);// 失败分配投资者数
		
		incomeAllocateDao.save(incomeAllocate);
		
		incomeEvent.setStatus(IncomeEvent.STATUS_Allocating);
		incomeEvent.setAuditor("system");
		incomeEvent.setAuditTime(DateUtil.getSqlCurrentDate());
		incomeEventDao.save(incomeEvent);
		
		return incomeAllocate;
	}
}
