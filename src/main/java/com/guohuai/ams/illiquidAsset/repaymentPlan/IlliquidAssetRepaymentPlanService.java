package com.guohuai.ams.illiquidAsset.repaymentPlan;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.guohuai.ams.illiquidAsset.IlliquidAsset;
import com.guohuai.ams.illiquidAsset.IlliquidAssetDao;
import com.guohuai.ams.illiquidAsset.IlliquidAssetSetupForm;
import com.guohuai.ams.portfolio20.illiquid.hold.repayment.PortfolioIlliquidHoldRepaymentService;
import com.guohuai.basic.common.StringUtil;
import com.guohuai.basic.component.exception.GHException;
import com.guohuai.basic.component.repayment.RepaymentPlan;
import com.guohuai.basic.component.repayment.mode.MonthDaysMode;
import com.guohuai.basic.component.repayment.rate.YearYieldRate;

@Service
@Transactional
public class IlliquidAssetRepaymentPlanService {

	@Autowired
	private IlliquidAssetRepaymentPlanDao illiquidAssetRepaymentPlanDao;
	@Autowired
	private IlliquidAssetDao illiquidAssetDao;
	@Autowired
	private PortfolioIlliquidHoldRepaymentService portfolioIlliquidHoldRepaymentService;

	/**
	 * 后台分页查询
	 * 
	 * @param spec
	 * @param pageable
	 * @return
	 */
	@Transactional
	public IlliquidAssetRepaymentPlanListResp queryPage(Specification<IlliquidAssetRepaymentPlan> spec, Pageable pageable) {
		Page<IlliquidAssetRepaymentPlan> enchs = this.illiquidAssetRepaymentPlanDao.findAll(spec, pageable);
		IlliquidAssetRepaymentPlanListResp pageResp = new IlliquidAssetRepaymentPlanListResp(enchs);

		return pageResp;
	}

	/**
	 * 生成还款计划
	 * 
	 * @param it
	 */
	@Transactional
	public List<IlliquidAssetRepaymentPlan> repayMentPlanSchedule(IlliquidAsset it, String operator) {

		// 先清空历史的还款计划
		List<IlliquidAssetRepaymentPlan> hplan = this.illiquidAssetRepaymentPlanDao.findByIlliquidAsset(it);
		if (null != hplan && hplan.size() > 0) {
			this.illiquidAssetRepaymentPlanDao.delete(hplan);
		}
		List<RepaymentPlan.Plan> plans = null;
		if (RepaymentPlan.PAYMENT_METHOD_A_DEBT_SERVICE_DUE.equals(it.getAccrualType())) {
			plans = RepaymentPlan.aDebtServiceDue(it.getRestStartDate(), it.getRestEndDate(), it.getPurchaseValue(), new YearYieldRate(it.getExpAror(), it.getContractDays()));
		} else if (RepaymentPlan.PAYMENT_METHOD_EACH_INTEREST_RINCIPAL_DUE.equals(it.getAccrualType())) {
			plans = RepaymentPlan.eachInterestRincipalDue(it.getRestStartDate(), it.getRestEndDate(), it.getPurchaseValue(), new YearYieldRate(it.getExpAror(), it.getContractDays()), it.getAccrualDate(), MonthDaysMode.FIXED_30_DAYS);
		} else if (RepaymentPlan.PAYMENT_METHOD_FIXED_BASIS_MORTGAGE.equals(it.getAccrualType())) {
			plans = RepaymentPlan.fixedBasisMortgage(it.getRestStartDate(), it.getRestEndDate(), it.getPurchaseValue(), new YearYieldRate(it.getExpAror(), it.getContractDays()), it.getAccrualDate(), MonthDaysMode.FIXED_30_DAYS);
		} else if (RepaymentPlan.PAYMENT_METHOD_FIXED_PAYMENT_MORTGAGE.equals(it.getAccrualType())) {
			plans = RepaymentPlan.fixedPaymentMortgage(it.getRestStartDate(), it.getRestEndDate(), it.getPurchaseValue(), new YearYieldRate(it.getExpAror(), it.getContractDays()), it.getAccrualDate(), MonthDaysMode.FIXED_30_DAYS);
		} else {
			throw new GHException("未知的还款计划类型.");
		}

		if (null == plans || plans.size() == 0) {
			throw new GHException("生成还款计划失败.");
		}

		Timestamp current = new Timestamp(System.currentTimeMillis());

		List<IlliquidAssetRepaymentPlan> result = new ArrayList<IlliquidAssetRepaymentPlan>();
		for (RepaymentPlan.Plan plan : plans) {
			IlliquidAssetRepaymentPlan r = new IlliquidAssetRepaymentPlan();
			r.setOid(StringUtil.uuid());
			r.setIlliquidAsset(it);
			r.setIssue(plan.getIssue());
			r.setRepaymentType(it.getAccrualType());
			r.setIntervalDays(plan.getIntervalDays());
			r.setStartDate(new java.sql.Date(plan.getStartDate().getTime()));
			r.setEndDate(new java.sql.Date(plan.getEndDate().getTime()));
			r.setDueDate(new Date(plan.getDueDate().getTime()));
			r.setPrincipal(plan.getPrincipal());
			r.setInterest(plan.getInterest());
			r.setRepayment(plan.getRepayment());
			r.setOperator(operator);
			r.setCreator(operator);
			r.setOperateTime(current);
			r.setCreateTime(current);
			result.add(this.illiquidAssetRepaymentPlanDao.save(r));

		}

		return result;
	}

	public void savePlan(IlliquidAssetSetupForm form, String operator) throws Exception {

		if (null == form.getPlans() || form.getPlans().size() == 0) {
			throw new GHException("未获取到还款计划");
		}

		IlliquidAsset entity = illiquidAssetDao.findOne(form.getOid());
		if (null == entity) {
			throw new GHException("未知的投资标的ID: " + form.getOid());
		}

		// 置为已成立
		entity.setLifeState(IlliquidAsset.ILLIQUIDASSET_LIFESTATE_SETUP);
		entity.setSetDate(new Date(form.getSetDate().getTime()));
		entity.setRestStartDate(new Date(form.getRestStartDate().getTime()));
		entity.setRestEndDate(new Date(form.getRestEndDate().getTime()));

		this.illiquidAssetDao.save(entity);
		List<IlliquidAssetRepaymentPlan> hplan = this.illiquidAssetRepaymentPlanDao.findByIlliquidAsset(entity);
		if (null != hplan && hplan.size() > 0) {
			this.illiquidAssetRepaymentPlanDao.delete(hplan);
		}
		for (IlliquidAssetSetupForm.RepaymentPlan plan : form.getPlans()) {
			IlliquidAssetRepaymentPlan p = new IlliquidAssetRepaymentPlan();
			p.setIlliquidAsset(entity);
			p.setIssue(plan.getIssue());
			p.setRepaymentType(entity.getAccrualType());
			p.setIntervalDays(plan.getIntervalDays());
			p.setStartDate(new Date(plan.getStartDate().getTime()));
			p.setEndDate(new Date(plan.getEndDate().getTime()));
			p.setDueDate(new Date(plan.getDueDate().getTime()));
			p.setPrincipal(plan.getPrincipal());
			p.setInterest(plan.getInterest());
			p.setRepayment(plan.getRepayment());
			p.setCreator(operator);
			p.setOperator(operator);
			p.setCreateTime(new Timestamp(System.currentTimeMillis()));
			p.setOperateTime(new Timestamp(System.currentTimeMillis()));
			this.illiquidAssetRepaymentPlanDao.save(p);
		}

		// 检测持仓列表，需要生成还款计划
		this.portfolioIlliquidHoldRepaymentService.setupInit(entity, operator);
	}

}
