package com.guohuai.mmp.jiajiacai.wishplan.plan.service;

import java.math.BigDecimal;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guohuai.ams.calendar.TradeCalendarTService;
import com.guohuai.component.util.DateUtil;
import com.guohuai.mmp.jiajiacai.caculate.StringUtils;
import com.guohuai.mmp.jiajiacai.common.constant.InvestTypeEnum;
import com.guohuai.mmp.jiajiacai.common.constant.PlanStatus;
import com.guohuai.mmp.jiajiacai.wishplan.plan.dao.PlanMonthDao;
import com.guohuai.mmp.jiajiacai.wishplan.plan.entity.PlanMonthEntity;
import com.guohuai.mmp.jiajiacai.wishplan.plan.form.MonthlyInvestForm;
import com.guohuai.mmp.jiajiacai.wishplan.planlist.PlanListDao;

@Service
public class PlanMonthService {

	@Autowired
	private PlanMonthDao planMonthDao;
	
	@Autowired
	private TradeCalendarTService tradeCalendarService;
	@Autowired
	private PlanListDao planListDao;
	
	@Transactional
	public PlanMonthEntity investMonth(MonthlyInvestForm form) {
		PlanMonthEntity entity = new PlanMonthEntity();
		entity.setOid(StringUtils.uuid());
		entity.setUid(form.getUid());
	
		entity.setCid(form.getCid());
		entity.setCkey(form.getCkey());
//		entity.setPlanType(form.getPlanType());
		entity.setMonthAmount(form.getMothAmount());
		
		entity.setPlanMonthCount(form.getPlanMonthCount());
		
		entity.setStartInvestDate(DateUtil.addTimestampDays(form.getStartInvestDate(), 0));
		entity.setMonthInvestDate(form.getMothInvestDate());
		if (!InvestTypeEnum.MonthSalaryInvest.getCode().equals(form.getPlanType())) {
			entity.setEndTime(DateUtil.addTimestampMonths(entity.getStartInvestDate(), form.getPlanMonthCount()));
		}
		//The bank
		entity.setInvestorBankOid(form.getInvestorBankOid());
		//create time
        entity.setCreateTime(DateUtil.getSqlCurrentDate());				
//        entity.setUpdateTime(DateUtil.getSqlCurrentDate());				
				
		entity.setStatus(PlanStatus.READY.getCode());
		
//		entity.setExpectedAmount(form.getExpectedAmount());
		entity.setExpectedRate(form.getExpectedRate());
		entity.setPlanType(form.getPlanType());	
		entity.setPlanTarget(form.getPlanTarget());
		
		/** 增加心愿计划的批次 */
		String planName = this.planListDao.findPlanNameByType(form.getPlanType());
		entity.setPlanBatch(planName+""+DateUtil.currentTime());
		
		PlanMonthEntity result = planMonthDao.save(entity);
		return result;
	}
	
	public List<PlanMonthEntity> queryByUid(String uid) {
		List<PlanMonthEntity> list = planMonthDao.findByUidOrderByCreateTimeDesc(uid);
		return list;
	}
	
	public PlanMonthEntity queryByOid(String oid) {
		return  planMonthDao.findByOid(oid);
	}
	
	public List<PlanMonthEntity> querySuccessPlanMonthByUid(String uid, String type) {
		return planMonthDao.querySuccessPlanMonth(uid, type);
	}
	
	
	@Transactional
	public int alterPlan(String oid, int dateNumber, BigDecimal amount) {
		return  planMonthDao.updatePlan(oid, dateNumber, amount);
	}
	
	@Transactional
	public int stopPlan(String oid) {
		return  planMonthDao.updateStatus(oid, PlanStatus.STOP.getCode());
	}
	
	public List<String> findPlanByUidStatusType(String uid, String status, String type) {
		List<String> list = planMonthDao.findPlanByUidTypeStatus(uid, status, type);
		return list;
	}
	
	public List<String> findPlanByUidTypeStatuses(String uid, List<String> statuses, String type) {
		List<String> list = planMonthDao.findPlanByUidTypeStatuses(uid, statuses, type);
		return list;
	}
	
	public List<PlanMonthEntity> queryOverdueCurrentProduct() {
//		Date nextdt = tradeCalendarService.nextTrade(new  Date(DateUtil.getCurrDate().getTime()), 1);
//		List<PlanMonthEntity> list = planMonthDao.queryOverdueNextTrade(new Timestamp(nextdt.getTime()));
//		Date nextdt = tradeCalendarService.nextTrade(new  Date(DateUtil.getCurrDate().getTime()), 1);
		List<PlanMonthEntity> list = planMonthDao.queryOverdueNow();
		return list;
	}
	
	@Transactional
	public int updateStatus(String oid, String status) {
		return  planMonthDao.updateStatus(oid, status);
	}
	
	@Transactional
	public int redeemSalaryplan(String oid) {
		return  planMonthDao.redeemSalaryplan(oid);
	}
	
//	@Transactional
//	public int transferBalance(String oid) {
//		return  planMonthDao.transferBalance(oid);
//	}
	
	
}
