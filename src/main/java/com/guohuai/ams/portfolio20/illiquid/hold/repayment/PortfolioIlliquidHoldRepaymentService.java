package com.guohuai.ams.portfolio20.illiquid.hold.repayment;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guohuai.ams.illiquidAsset.IlliquidAsset;
import com.guohuai.ams.portfolio20.illiquid.hold.PortfolioIlliquidHoldEntity;
import com.guohuai.ams.portfolio20.illiquid.hold.PortfolioIlliquidHoldService;
import com.guohuai.ams.portfolio20.order.MarketOrderEntity;
import com.guohuai.basic.common.StringUtil;
import com.guohuai.basic.component.repayment.RepaymentPlan;
import com.guohuai.basic.component.repayment.mode.MonthDaysMode;
import com.guohuai.basic.component.repayment.rate.YearYieldRate;
import com.guohuai.component.util.DateUtil;

@Service
public class PortfolioIlliquidHoldRepaymentService {

	@Autowired
	private PortfolioIlliquidHoldRepaymentDao portfolioIlliquidHoldRepaymentDao;
	@Autowired
	private PortfolioIlliquidHoldService portfolioIlliquidHoldService;

	public PortfolioIlliquidHoldRepaymentEntity findByOid(String oid) {
		return this.portfolioIlliquidHoldRepaymentDao.findOne(oid);
	}

	@Transactional
	public void setupInit(IlliquidAsset asset, String operator) {

		List<PortfolioIlliquidHoldEntity> holds = this.portfolioIlliquidHoldService.findByAsset(asset);
		for (PortfolioIlliquidHoldEntity hold : holds) {
			this.setupInit(hold, asset, operator);
		}
	}

	@Transactional
	private List<PortfolioIlliquidHoldRepaymentEntity> setupInit(PortfolioIlliquidHoldEntity hold, IlliquidAsset asset, String operator) {
		List<PortfolioIlliquidHoldRepaymentEntity> repayments = new ArrayList<PortfolioIlliquidHoldRepaymentEntity>();

		Date startDate = asset.getRestStartDate();
		Date endDate = asset.getRestEndDate();
		//原有逻辑 先成立标的 在投资组合审核 按照认购的起息日  先投资组合审核 在标的成立 按照成立标的的起息日
		//wangzhixin update 按照认购的起息日 
		if(null!=hold.getValueDate()&&!"".equals(hold.getValueDate())){
			startDate=hold.getValueDate();
		}
		List<RepaymentPlan.Plan> plans = null;
		
		if ("A_DEBT_SERVICE_DUE".equals(asset.getAccrualType())) {
			plans = RepaymentPlan.aDebtServiceDue(startDate, endDate, hold.getHoldShare(), new YearYieldRate(asset.getExpAror(), asset.getContractDays()));
		} else if ("EACH_INTEREST_RINCIPAL_DUE".equals(asset.getAccrualType())) {
			plans = RepaymentPlan.eachInterestRincipalDue(startDate, endDate, hold.getHoldShare(), new YearYieldRate(asset.getExpAror(), asset.getContractDays()), asset.getAccrualDate(), asset.getAccrualDate() == 360 ? MonthDaysMode.FIXED_30_DAYS : MonthDaysMode.NATURAL_DAYS);
		} else if ("FIXED-PAYMENT_MORTGAGE".equals(asset.getAccrualType())) {
			plans = RepaymentPlan.fixedPaymentMortgage(startDate, endDate, hold.getHoldShare(), new YearYieldRate(asset.getExpAror(), asset.getContractDays()), asset.getAccrualDate(), asset.getAccrualDate() == 360 ? MonthDaysMode.FIXED_30_DAYS : MonthDaysMode.NATURAL_DAYS);
		} else if ("FIXED-BASIS_MORTGAGE".equals(asset.getAccrualType())) {
			plans = RepaymentPlan.fixedBasisMortgage(startDate, endDate, hold.getHoldShare(), new YearYieldRate(asset.getExpAror(), asset.getContractDays()), asset.getAccrualDate(), asset.getAccrualDate() == 360 ? MonthDaysMode.FIXED_30_DAYS : MonthDaysMode.NATURAL_DAYS);
		} else {
			return repayments;
		}

		if (null == plans || plans.size() == 0) {
			return repayments;
		}

		int issue = 1;

		for (int i = 0; i < plans.size(); i++) {
			RepaymentPlan.Plan plan = plans.get(i);
			{
				PortfolioIlliquidHoldRepaymentEntity p = new PortfolioIlliquidHoldRepaymentEntity();
				p.setOid(StringUtil.uuid());
				p.setHold(hold);
				p.setIssue(issue);
				p.setIntervalDays(plan.getIntervalDays());
				p.setRepaymentType(asset.getAccrualType());
				p.setStartDate(new Date(plan.getStartDate().getTime()));
				p.setEndDate(new Date(plan.getEndDate().getTime()));
				p.setDueDate(new Date(plan.getDueDate().getTime()));
				p.setPrincipalPlan(plan.getPrincipal());
				p.setInterestPlan(plan.getInterest());
				p.setRepaymentPlan(plan.getRepayment());
				p.setRepayment(BigDecimal.ZERO);
				p.setInterest(BigDecimal.ZERO);
				p.setRepayment(BigDecimal.ZERO);
				p.setLastIssue(i == plans.size() - 1 ? PortfolioIlliquidHoldRepaymentEntity.LAST_ISSUE_YES : PortfolioIlliquidHoldRepaymentEntity.LAST_ISSUE_NO);
				p.setState(PortfolioIlliquidHoldRepaymentEntity.STATE_UNDUE);
				p.setCreator(operator);
				p.setCreateTime(new Timestamp(System.currentTimeMillis()));
				p.setOperator(operator);
				p.setOperateTime(new Timestamp(System.currentTimeMillis()));
				p = this.portfolioIlliquidHoldRepaymentDao.save(p);
				repayments.add(p);
			}
			issue++;
		}

		return repayments;
	}

	@Transactional
	public List<PortfolioIlliquidHoldRepaymentEntity> mergeRepayment(PortfolioIlliquidHoldEntity hold, MarketOrderEntity order, String operator) {

		// 这里的实现思路是, 根据每期的结束日期, 判断新的计划和老的计划, 是否为同一期

		List<PortfolioIlliquidHoldRepaymentEntity> list = this.portfolioIlliquidHoldRepaymentDao.findByHold(hold);
		LinkedHashMap<String, PortfolioIlliquidHoldRepaymentEntity> hismap = new LinkedHashMap<String, PortfolioIlliquidHoldRepaymentEntity>();
		Date lastRepayment = new java.sql.Date(0);
		if (null != list && list.size() > 0) {
			for (PortfolioIlliquidHoldRepaymentEntity r : list) {
				hismap.put(r.getEndDate().toString(), r);
				if (PortfolioIlliquidHoldRepaymentEntity.STATE_PAID.equals(r.getState())) {
					lastRepayment = r.getEndDate();
				}
			}
		}
		IlliquidAsset asset = hold.getIlliquidAsset();

		Date startDate = new java.sql.Date(Math.max(asset.getRestStartDate().getTime(), DateUtil.addDays(order.getOrderDate(), 1).getTime()));
		Date endDate = asset.getRestEndDate();
		
		List<RepaymentPlan.Plan> plans = null;
		if ("A_DEBT_SERVICE_DUE".equals(asset.getAccrualType())) {
			//wangzhixin update 债权类型标的 投资确认日在起息日之前，则直到起息日当日日终，该标的估值开始按算法进行变更
			//到期还款日取页面的到期还款日
			if(asset.ILLIQUIDASSET_TYPE_ZHAIQUAN.equals(asset.getType())){
						if(asset.getRestStartDate().getTime()>order.getOrderDate().getTime()){
							startDate=asset.getRestStartDate();
						}else{
							startDate=order.getOrderDate();
						}
						endDate=asset.getRestEndDate();
					}    
			plans = RepaymentPlan.aDebtServiceDue(startDate, endDate, hold.getHoldShare(), new YearYieldRate(asset.getExpAror(), asset.getContractDays()));
		} else if ("EACH_INTEREST_RINCIPAL_DUE".equals(asset.getAccrualType())) {
			plans = RepaymentPlan.eachInterestRincipalDue(startDate, endDate, hold.getHoldShare(), new YearYieldRate(asset.getExpAror(), asset.getContractDays()), asset.getAccrualDate(), asset.getAccrualDate() == 360 ? MonthDaysMode.FIXED_30_DAYS : MonthDaysMode.NATURAL_DAYS);
		} else if ("FIXED-PAYMENT_MORTGAGE".equals(asset.getAccrualType())) {
			plans = RepaymentPlan.fixedPaymentMortgage(startDate, endDate, hold.getHoldShare(), new YearYieldRate(asset.getExpAror(), asset.getContractDays()), asset.getAccrualDate(), asset.getAccrualDate() == 360 ? MonthDaysMode.FIXED_30_DAYS : MonthDaysMode.NATURAL_DAYS);
		} else if ("FIXED-BASIS_MORTGAGE".equals(asset.getAccrualType())) {
			plans = RepaymentPlan.fixedBasisMortgage(startDate, endDate, hold.getHoldShare(), new YearYieldRate(asset.getExpAror(), asset.getContractDays()), asset.getAccrualDate(), asset.getAccrualDate() == 360 ? MonthDaysMode.FIXED_30_DAYS : MonthDaysMode.NATURAL_DAYS);
		} else {
			return list;
		}

		if (null == plans || plans.size() == 0) {
			return list;
		}

		List<PortfolioIlliquidHoldRepaymentEntity> result = new ArrayList<PortfolioIlliquidHoldRepaymentEntity>();

		int issue = 1;

		for (int i = 0; i < plans.size(); i++) {
			RepaymentPlan.Plan plan = plans.get(i);
			if (hismap.containsKey(DateUtil.formatDate(plan.getEndDate().getTime()))) {
				PortfolioIlliquidHoldRepaymentEntity p = hismap.get(DateUtil.formatDate(plan.getEndDate().getTime()));
				p.setIssue(issue);
				p.setIntervalDays(Math.max(p.getIntervalDays(), plan.getIntervalDays()));
				p.setStartDate(new Date(Math.min(p.getStartDate().getTime(), plan.getStartDate().getTime())));
				if (!PortfolioIlliquidHoldRepaymentEntity.STATE_PAID.equals(p.getState())) {
					// 只有未还款的, 才重新设置还款计划
					p.setPrincipalPlan(plan.getPrincipal());
					p.setInterestPlan(plan.getInterest());
					p.setRepaymentPlan(plan.getRepayment());
				}
				p.setLastIssue(i == plans.size() - 1 ? PortfolioIlliquidHoldRepaymentEntity.LAST_ISSUE_YES : PortfolioIlliquidHoldRepaymentEntity.LAST_ISSUE_NO);
				p.setOperator(operator);
				p.setOperateTime(new Timestamp(System.currentTimeMillis()));
				p = this.portfolioIlliquidHoldRepaymentDao.save(p);
				result.add(p);
			} else {
				PortfolioIlliquidHoldRepaymentEntity p = new PortfolioIlliquidHoldRepaymentEntity();
				p.setOid(StringUtil.uuid());
				p.setHold(hold);
				p.setIssue(issue);
				p.setIntervalDays(plan.getIntervalDays());
				p.setRepaymentType(asset.getAccrualType());
				p.setStartDate(new Date(plan.getStartDate().getTime()));
				p.setEndDate(new Date(plan.getEndDate().getTime()));
				p.setDueDate(new Date(plan.getDueDate().getTime()));
				p.setPrincipalPlan(plan.getPrincipal());
				p.setInterestPlan(plan.getInterest());
				p.setRepaymentPlan(plan.getRepayment());
				p.setRepayment(BigDecimal.ZERO);
				p.setInterest(BigDecimal.ZERO);
				p.setRepayment(BigDecimal.ZERO);
				p.setLastIssue(i == plans.size() - 1 ? PortfolioIlliquidHoldRepaymentEntity.LAST_ISSUE_YES : PortfolioIlliquidHoldRepaymentEntity.LAST_ISSUE_NO);
				if (DateUtil.ge(lastRepayment, new java.sql.Date(plan.getEndDate().getTime()))) {
					p.setState(PortfolioIlliquidHoldRepaymentEntity.STATE_PAID);
				} else {
					if (DateUtil.ge(order.getOrderDate(), new java.sql.Date(plan.getEndDate().getTime()))) {
						p.setState(PortfolioIlliquidHoldRepaymentEntity.STATE_PAYING);
					} else {
						p.setState(PortfolioIlliquidHoldRepaymentEntity.STATE_UNDUE);
					}
				}
				p.setCreator(operator);
				p.setCreateTime(new Timestamp(System.currentTimeMillis()));
				p.setOperator(operator);
				p.setOperateTime(new Timestamp(System.currentTimeMillis()));
				p = this.portfolioIlliquidHoldRepaymentDao.save(p);
				result.add(p);
			}
			issue++;
		}

		return result;
	}

	@Transactional
	public PortfolioIlliquidHoldRepaymentEntity repaymenting(PortfolioIlliquidHoldRepaymentEntity repayment, MarketOrderEntity order, String operator) {
		repayment.setState(PortfolioIlliquidHoldRepaymentEntity.STATE_AUDIT);
		repayment.setPrincipal(order.getCapital());
		repayment.setInterest(order.getIncome());
		repayment.setRepayment(order.getCapital().add(order.getIncome()));
		repayment.setOperator(operator);
		repayment.setOperateTime(new Timestamp(System.currentTimeMillis()));
		return this.portfolioIlliquidHoldRepaymentDao.save(repayment);
	}

	@Transactional
	public PortfolioIlliquidHoldRepaymentEntity failRepayment(PortfolioIlliquidHoldRepaymentEntity repayment, String operator) {
		repayment.setState(PortfolioIlliquidHoldRepaymentEntity.STATE_PAYING);
		repayment.setPrincipal(BigDecimal.ZERO);
		repayment.setInterest(BigDecimal.ZERO);
		repayment.setRepayment(BigDecimal.ZERO);
		repayment.setOperator(operator);
		repayment.setOperateTime(new Timestamp(System.currentTimeMillis()));
		return this.portfolioIlliquidHoldRepaymentDao.save(repayment);
	}

	@Transactional
	public PortfolioIlliquidHoldRepaymentEntity passRepayment(PortfolioIlliquidHoldRepaymentEntity repayment, String operator) {
		repayment.setState(PortfolioIlliquidHoldRepaymentEntity.STATE_PAID);
		repayment.setOperator(operator);
		repayment.setOperateTime(new Timestamp(System.currentTimeMillis()));
		return this.portfolioIlliquidHoldRepaymentDao.save(repayment);
	}

	public List<PortfolioIlliquidHoldRepaymentEntity> findByHold(String oid) {
		return this.portfolioIlliquidHoldRepaymentDao.findByHoldOid(oid);
	}

}
