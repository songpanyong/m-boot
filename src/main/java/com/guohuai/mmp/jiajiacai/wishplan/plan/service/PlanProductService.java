package com.guohuai.mmp.jiajiacai.wishplan.plan.service;

import java.math.BigDecimal;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guohuai.component.util.DateUtil;
import com.guohuai.mmp.jiajiacai.caculate.InvestProfitCaculate;
import com.guohuai.mmp.jiajiacai.caculate.JJCUtility;
import com.guohuai.mmp.jiajiacai.caculate.StringUtils;
import com.guohuai.mmp.jiajiacai.common.constant.InvestTypeEnum;
import com.guohuai.mmp.jiajiacai.common.constant.PlanStatus;
import com.guohuai.mmp.jiajiacai.wishplan.plan.dao.PlanProductDao;
import com.guohuai.mmp.jiajiacai.wishplan.plan.entity.PlanProductEntity;
import com.guohuai.mmp.jiajiacai.wishplan.plan.form.FixedPlanForm;
import com.guohuai.mmp.jiajiacai.wishplan.plan.form.MonthPlanForm;
import com.guohuai.mmp.jiajiacai.wishplan.plan.form.PlanProductForm;
import com.guohuai.mmp.jiajiacai.wishplan.question.InvestMessageForm;

@Service
public class PlanProductService {

	@Autowired
	private PlanProductDao planDao;

	@Transactional
	public
	PlanProductEntity investFixed(FixedPlanForm form) {
		PlanProductEntity entity = new PlanProductEntity();
		entity.setOid(StringUtils.uuid());
		entity.setUid(form.getUid());
//		entity.setProductOid(form.getProductOid());
		entity.setPlanOid(form.getPlanOid());
		entity.setAmount(form.getAmount());
//		entity.setDuration(form.getDuration());
//		entity.setOrderOid(form.getOrderOid());
		entity.setCreateTime(form.getInputTime());
		entity.setPlanType(InvestTypeEnum.FixedInvestment.getCode());
		PlanProductEntity result = planDao.save(entity);
		return result;
	}

	@Transactional
	public
	PlanProductEntity investMonth(MonthPlanForm form) {
		PlanProductEntity entity = new PlanProductEntity();
		entity.setOid(StringUtils.uuid());
		entity.setUid(form.getUid());
		entity.setProductOid(form.getProductOid());
		entity.setPlanOid(form.getPlanOid());
		entity.setAmount(form.getAmount());
//		entity.setDuration(form.getDuration());
//		entity.setOrderOid(form.getOrderOid());
		entity.setCreateTime(form.getInputTime());
//		entity.setDateNumber(form.getDateNumber());
		entity.setPlanType(form.getPlanType());
		PlanProductEntity result = planDao.save(entity);
		return result;
	}

	public List<PlanProductEntity> queryByStatus(String uid, String status) {
		List<PlanProductEntity> list = planDao.findByUidAndStatus(uid, status);
		return list;
	}
	
	public List<PlanProductEntity> queryByUid(String uid) {
		List<PlanProductEntity> list = planDao.findByUid(uid);
		return list;
	}
	
	public PlanProductEntity queryByOid(String oid) {
		PlanProductEntity entity = planDao.findOne(oid);
		return entity;
	}

	public String stopPlanByOrderOid(String orderOid) {
		// TODO 调用支付接口 
		// stop plan
//		String result = planDao.stopPlan(orderOid, PlanStatus.STOP.getCode());
		String result = null;
		return result;
	}

	public String updatePlan(String orderOid, int dateNumber, int amount) {
		// TODO 调用支付接口
		//  update plan
		String result = null;
//		planDao.updatePlan(orderOid, dateNumber, amount);
		return result;
	}
	
	public PlanProductEntity findPlanByPlanOid(String planOid,String uid) {
		return planDao.findByPlanOidAndUid(planOid,uid);
	}

	public InvestMessageForm caculateInvestProfitByFixed(int duration, int capital, float rate) {
		float profit = InvestProfitCaculate.caculateInvestProfitByFixed(duration, capital, rate);
		String profitStr = JJCUtility.keep2Decimal(profit);
		InvestMessageForm form = new InvestMessageForm();
		form.setCapital(capital);
		form.setDuration(duration);
		form.setProfit(profitStr);
		form.setType(InvestTypeEnum.FixedInvestment.getCode());
		return form;
	}

	public InvestMessageForm caculateInvestProfitByMonth(int month, int capital, float rate) {
		float profit = InvestProfitCaculate.caculateInvestProfitByMonth(month, capital,  rate);
		String profitStr = JJCUtility.keep2Decimal(profit);
		InvestMessageForm form = new InvestMessageForm();
		form.setCapital(capital);
		form.setDuration(DateUtil.diffDays4Months(month));
		form.setProfit(profitStr);
//		form.setType(InvestTypeEnum.MonthInvestment.getCode());
		return form;
	}
	
	@Transactional
	public PlanProductEntity planInvest(PlanProductForm form) {
		PlanProductEntity entity = new PlanProductEntity();
		entity.setOid(StringUtils.uuid());
		entity.setUid(form.getUid());
		entity.setProductOid(form.getProductOid());
		entity.setPlanOid(form.getPlanOid());
		entity.setAmount(form.getMoneyVolume());
//		entity.setDuration(form.getDuration());
//		entity.setOrderOid(form.getOrderOid());
//		entity.setInputTime(form.getInputTime());
		entity.setCreateTime(DateUtil.getSqlCurrentDate());
		entity.setPlanType(form.getPlanType());
		entity.setStatus(PlanStatus.READY.getCode());
		PlanProductEntity result = planDao.save(entity);
		return result;
	}
	
	@Transactional
	public int updateStatus(String oid, String status, String orderOid) {
		return planDao.updateStatus(oid, status, orderOid);
	}
	
	
	@Transactional
	public int updateStatus(String oid, String status) {
		return planDao.updateStatus(oid, status);
	}
	
	public List<PlanProductEntity> findByPlanOidAndStatus(String planOid, String status) {
		return planDao.findByPlanOidAndStatus(planOid, status);
	}
		
	public List<PlanProductEntity> queryPlanProductByInvestAndStatusList(String planOid, List<String> statusList) {
		return planDao.queryPlanProductByInvestAndStatusList(planOid, statusList);
	}
	
	/**
	 * count
	 * @param planOid
	 * @param statusList
	 * @return
	 */
	public int countPlanProductByInvestAndStatusList(String planOid, List<String> statusList) {
		return planDao.countPlanProductByInvestAndStatusList(planOid, statusList);
	}
	
	
	@Transactional
	public int addIncome(BigDecimal income, String oid) {
		return planDao.addIncome(income, oid);
	}
	
	public List<PlanProductEntity> queryInvestByStatus(String status) {
		return planDao.queryInvestByStatus(status);
	}
	
}
