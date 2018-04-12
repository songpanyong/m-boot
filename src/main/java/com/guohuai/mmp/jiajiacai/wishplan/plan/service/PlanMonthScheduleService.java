package com.guohuai.mmp.jiajiacai.wishplan.plan.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guohuai.account.api.request.RedeemToBasicRequest;
import com.guohuai.cache.entity.InvestorBaseAccountCacheEntity;
import com.guohuai.cache.service.CacheInvestorService;
import com.guohuai.component.util.DateUtil;
import com.guohuai.mmp.investor.bankorder.BankOrderRep;
import com.guohuai.mmp.investor.bankorder.DepositBankOrderbfReq;
import com.guohuai.mmp.investor.bankorder.InvestorBankOrderExtService;
import com.guohuai.mmp.investor.tradeorder.TradeOrderRep;
import com.guohuai.mmp.jiajiacai.common.constant.Constant;
import com.guohuai.mmp.jiajiacai.common.constant.InvestTypeEnum;
import com.guohuai.mmp.jiajiacai.common.constant.PlanStatus;
import com.guohuai.mmp.jiajiacai.common.exception.BaseException;
import com.guohuai.mmp.jiajiacai.common.service.ThirdNotifyService;
import com.guohuai.mmp.jiajiacai.ebaoquan.EbaoquanRecord;
import com.guohuai.mmp.jiajiacai.ebaoquan.EbaoquanRecordService;
import com.guohuai.mmp.jiajiacai.wishplan.plan.dao.PlanMonthDao;
import com.guohuai.mmp.jiajiacai.wishplan.plan.entity.PlanInvestEntity;
import com.guohuai.mmp.jiajiacai.wishplan.plan.entity.PlanMonthEntity;
import com.guohuai.mmp.jiajiacai.wishplan.plan.form.PlanProductForm;
import com.guohuai.mmp.jiajiacai.wishplan.product.JJCProductService;
import com.guohuai.mmp.jiajiacai.wishplan.product.form.JJCProductRate;
import com.guohuai.mmp.jiajiacai.wishplan.question.InvestMessageForm;
import com.guohuai.mmp.platform.accment.Accment;
import com.guohuai.mmp.platform.msgment.MsgParam;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PlanMonthScheduleService {

	@Autowired
	private PlanMonthDao planMonthDao;

	@Autowired
	private PlanProductService planProductService;

	@Autowired
	private JJCProductService productService;

	@Autowired
	private CacheInvestorService cacheInvestorService;

	
	private int processCount;
	
	@Autowired
	private PlanInvestService planInvestService;
	
	@Autowired
	private Accment accmentService;
	
	@Autowired
	private InvestorBankOrderExtService investorBankOrderExtService;

	@Autowired
	private ThirdNotifyService thirdNotifyService;
	
	@Autowired
	private EbaoquanRecordService baoquanService;
	
	/*
	@Transactional
	public PlanMonthEntity investMonth(MonthlyInvestForm form) {
		PlanMonthEntity entity = new PlanMonthEntity();
		entity.setOid(StringUtils.uuid());
		entity.setUid(form.getUid());

//		entity.setPlanListOid(form.getPlanListOid());
		entity.setMonthAmount(form.getMothAmount());

		entity.setPlanMonthCount(form.getPlanMonthCount());

		Timestamp ts = Timestamp.valueOf(form.getStartInvestDate());
		entity.setStartInvestDate(ts);
		entity.setMonthInvestDate(form.getMothInvestDate());
		// The bank
		entity.setInvestorBankOid(form.getInvestorBankOid());
		// create time
		entity.setCreateTime(DateUtil.getSqlCurrentDate());
		// entity.setUpdateTime(DateUtil.getSqlCurrentDate());

		entity.setStatus(PlanStatus.READY.getCode());

//		entity.setExpectedAmount(form.getExpectedAmount());
		entity.setPlanType(form.getPlanType());
		PlanMonthEntity result = planMonthDao.save(entity);
		return result;
	}
   */
	public List<PlanMonthEntity> queryByUid(String uid) {
		List<PlanMonthEntity> list = planMonthDao.findByUid(uid);
		return list;
	}

	public PlanMonthEntity queryByOid(String oid) {
		return planMonthDao.findByOid(oid);
	}

	public void scheduleProcess() throws BaseException {
		int today = DateUtil.getTodayOnMonth();
		int yesterday = DateUtil.getYesterdayOnMonth();
		int beforeyesterday = DateUtil.getBeforeYesterdayOnMonth();
		int thisMonth = DateUtil.getYearMonthFromDate();
		List<Integer> dueDays = new ArrayList<Integer>();
		dueDays.add(today);
		dueDays.add(yesterday);
		dueDays.add(beforeyesterday);
		Timestamp end = DateUtil.firstDayOfNextMonth();
//		List<PlanMonthEntity> list = planMonthDao.findByMonthInvestDateStatusMonth(dueDay, PlanStatus.READY.getCode(), thisMonth);
		List<PlanMonthEntity> list = planMonthDao.findMonthPlanByDateStatusEnd(dueDays, PlanStatus.READY.getCode(), thisMonth, end);
		processCount = 0;
		for (PlanMonthEntity pme : list) {
			try {
				processOneMonthPlan(pme);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				e.printStackTrace();
			}
		}
	}

	private InvestMessageForm getMaxProfitProduct(BigDecimal moneyVolum, Timestamp time, String type, String uid) {
		// Get the max profit from product.
		int duration = 0;
		boolean onlyOpen = true;
		if (!InvestTypeEnum.MonthSalaryInvest.getCode().contentEquals(type)) {
			duration = DateUtil.getTimeRemainDays(time);
			//Fixed the one different day bug.
			duration += 1;
			onlyOpen = false;
		}
		JJCProductRate productRate = productService.getRateByDuration(duration, onlyOpen, uid);
//		if (productRate.getOid() == null) {
//			throw new BaseException(ErrorMessage.PRODUCT_NOT_EXIST);
//		}
		InvestMessageForm form = null;
		if (!InvestTypeEnum.MonthSalaryInvest.getCode().contentEquals(type)) {
			form = planProductService.caculateInvestProfitByFixed(duration, moneyVolum.intValue(),
					productRate.getRate());
		} else {
			form = new InvestMessageForm();
		}
		form.setRate(productRate.getRate());
		form.setProductOid(productRate.getOid());
		form.setProductMoneyValume(productRate.getProductMoneyValume());
		return form;
	}

	private int processOneMonthPlan(PlanMonthEntity pme) {

		InvestMessageForm investForm = getMaxProfitProduct(pme.getMonthAmount(), pme.getEndTime(), pme.getPlanType(), pme.getUid());
//		if (investForm == null) {
//			throw new BaseException(ErrorMessage.PRODUCT_NOT_EXIST);
//		}
//		PlanMonthDeposit monthDeposit = monthDepositService.investMonth(pme, investForm);
//		if (monthDeposit == null) {
//			throw new BaseException(ErrorMessage.PRAMETER_ERROR);
//		}
		PlanProductForm tradeOrderReq = planInvestService.generateOrderReq(pme, investForm);
		tradeOrderReq.setUid(pme.getUid());
		tradeOrderReq.setMonthOid(pme.getOid());
		log.info("月定投计划 uid={}, money={}, productid={}, startTime={}", tradeOrderReq.getUid(),
				tradeOrderReq.getMoneyVolume(), tradeOrderReq.getProductOid(), DateUtil.getSqlCurrentDate());
		
//		tradeOrderReq.setInvestDuration(tradeOrderReq.getInvestDuration() / 30);
		PlanInvestEntity investPlan = planInvestService.planInsert(tradeOrderReq, pme.getPlanType(), null);
		
		int thisMonth = DateUtil.getYearMonthFromDate();
		planMonthDao.updateInvestMonth(pme.getOid(), thisMonth);
		
//		if (investPlan == null) {
//			throw new BaseException(ErrorMessage.PRAMETER_ERROR);
//		}
		
		int balanceResult = checkBalanceEnough(pme.getUid(), pme.getMonthAmount());
		//划扣
		if (balanceResult < 0) {
			withhold(investPlan);
			planInvestService.updateStatus(investPlan.getOid(), PlanStatus.TODEPOSIT.getCode());
		} else {
			int result = depositRedeemBalace(investPlan);
			if (result == 0) {
				baoquanRecord(pme);
				thirdNotifyService.callMsgMail(MsgParam.msgbalancemonthbuysuccess, investPlan, investPlan.getUid());
			}
		}
		return processCount++;

	}
    /*
	private static PlanProductForm initPlanProductFrom(PlanMonthEntity pme, InvestMessageForm investForm,
			PlanMonthDeposit monthDeposit) throws BaseException {
		PlanProductForm tradeOrderReq = new PlanProductForm();
		tradeOrderReq.setUid(pme.getUid());
		tradeOrderReq.setMoneyVolume(pme.getMonthAmount());
		if (tradeOrderReq.getUid() == null) {
			throw new BaseException(ErrorMessage.USER_NOT_LOGIN);
		}
		log.info("计划购买开始 uid={}, money={}, productid={}, startTime={}", tradeOrderReq.getUid(),
				tradeOrderReq.getMoneyVolume(), tradeOrderReq.getProductOid(), DateUtil.getSqlCurrentDate());
		if (monthDeposit == null) {
			throw new BaseException(ErrorMessage.PRAMETER_ERROR);
		}
		tradeOrderReq.setProductOid(investForm.getProductOid());
		tradeOrderReq.setPlanOid(monthDeposit.getOid());
		tradeOrderReq.setPlanType(pme.getPlanType());
		// hard code
		tradeOrderReq.setCid("12306");
		tradeOrderReq.setCkey("12306");
		tradeOrderReq.setPlanRedeemOid(investForm.getPlanRedeemOid());
		tradeOrderReq.setOriginBranch(PlanProductForm.TRADEORDER_originBranch_whishMiddle);
		return tradeOrderReq;
	}
    
	
	public void salarySchedule() throws BaseException {
		int dueDay = Integer.parseInt(DateUtil.getCurrentDay());
		List<PlanMonthEntity> list = planMonthDao.findByMonthInvestDateAndStatus(dueDay, PlanStatus.READY.getCode());
		processCount = 0;
		for (PlanMonthEntity pme : list) {
			if (pme.getPlanType().contentEquals(InvestTypeEnum.MonthSalaryInvest.getCode())
					&& checkInvestOnMonth(pme)) {
				// setEndTime(pme);
				processOneMonthPlan(pme);
			}
		}
	}
    */
	@Transactional
	public void updatePlanInvest(PlanMonthEntity entity, BigDecimal detaInvestAmount, BigDecimal detaExpectedAmount) {
		BigDecimal depo = entity.getTotalDepositAmount();
		if (depo != null) {
			entity.setTotalDepositAmount(depo.add(detaInvestAmount));
		} else {
			entity.setTotalDepositAmount(detaInvestAmount);
		}
		if (detaExpectedAmount != null) {
			BigDecimal exp = entity.getExpectedAmount();
			if (exp != null) {
				entity.setExpectedAmount(exp.add(detaExpectedAmount));
			} else {
				entity.setExpectedAmount(detaExpectedAmount);
			}
		}
		entity.setTotalInvestCount(entity.getTotalInvestCount() + 1);
		planMonthDao.save(entity);
	}

//	public boolean checkInvestOnMonth(PlanMonthEntity entity) {
//		if (entity.getLastInvestDate() == null) {
//			return true;
//		}
//		int dueMoth = Integer.parseInt(DateUtil.getCurrentMonth());
//		int lastInvestMonth = DateUtil.getMonthFromDate(entity.getLastInvestDate());
//		return (dueMoth != lastInvestMonth);
//	}

	public int checkBalanceEnough(String investorOid, BigDecimal amount) {
		InvestorBaseAccountCacheEntity cache = cacheInvestorService.getInvestorByInvestorOid(investorOid);// 账户资金的实体
		return cache.getBalance().compareTo(amount);
	}
	/*
	public void buyProductByPlan(PlanMonthEntity pme, PlanMonthDeposit monthDeposit, BigDecimal retainValume,
			InvestMessageForm investForm) throws BaseException {

		if (investForm.getMoneyValume().compareTo(retainValume) > 0) {
			onceBuy(pme, investForm, monthDeposit, retainValume);
		} else {
			onceBuy(pme, investForm, monthDeposit, investForm.getMoneyValume());
			retainValume.subtract(investForm.getMoneyValume());
			InvestMessageForm currentInvestForm = getMaxProfitProduct(retainValume, pme.getEndTime(),
					pme.getPlanType());
			buyProductByPlan(pme, monthDeposit, retainValume, currentInvestForm);
		}
	}

	public void onceBuy(PlanMonthEntity pme, InvestMessageForm investForm, PlanMonthDeposit monthDeposit,
			BigDecimal moneyValume) throws BaseException {
		PlanProductForm tradeOrderReq = initPlanProductFrom(pme, investForm, monthDeposit);
		PlanProductEntity planEntity = planProductService.planInvest(tradeOrderReq);
		if (planEntity == null) {
			throw new BaseException(ErrorMessage.PLAN_NOT_EXIST);
		}
		TradeOrderRep rep = null;
		try {
			rep = investorInvestTradeOrderExtService.normalInvest(tradeOrderReq);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			rep = new TradeOrderRep();
			log.error(e.getMessage(), e);
			rep.setErrorCode(-1);
			rep.setErrorMessage(e.getMessage());
		}
		if (rep.getErrorCode() == 0) {
			planProductService.updateStatus(planEntity.getOid(), PlanStatus.SUCCESS.getCode(), rep.getTradeOrderOid());
			updateDeposit(monthDeposit, moneyValume);
		} else {
			planProductService.updateStatus(planEntity.getOid(), PlanStatus.FAILURE.getCode(), rep.getTradeOrderOid());
		}

		log.info("定时计划购买成功 uid={}, planId={}, money={}, endTime={}", tradeOrderReq.getUid(), planEntity.getOid(),
				tradeOrderReq.getMoneyVolume(), DateUtil.getSqlCurrentDate());
	}

	public void updateMonthStatus(PlanMonthEntity pme, PlanMonthDeposit monthDeposit) {
		updatePlanInvest(pme, monthDeposit.getDepositAmount(), monthDeposit.getExpectedAmount());
		monthDeposit.setStatus(PlanStatus.SUCCESS.getCode());
		planMonthDepositDao.save(monthDeposit);
	}

	@Transactional
	public void updateDeposit(PlanMonthDeposit entity, BigDecimal curentValume) {
		BigDecimal depo = entity.getActualDeposit();
		if (depo != null) {
			entity.setActualDeposit(depo.add(curentValume));
		} else {
			entity.setActualDeposit(curentValume);
		}
		planMonthDepositDao.save(entity);
	}
    */
	public void scheduleOverdueProcess() throws BaseException {
		List<PlanInvestEntity> list = planInvestService.queryByStatus(PlanStatus.TODEPOSIT.getCode());
		processCount = 0;
		for (PlanInvestEntity pmd : list) {
			if (checkValidate(pmd)) {
				try {
					overdueMonthDeposit(pmd);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public void overdueMonthDeposit(PlanInvestEntity investPlan) throws BaseException {
		int balanceResult = checkBalanceEnough(investPlan.getUid(), investPlan.getDepositAmount());
		//划扣
		if (balanceResult < 0) {
			withhold(investPlan);
		} else {
			depositRedeemBalace(investPlan);
			thirdNotifyService.callMsgMail(MsgParam.msgbalancemonthbuysuccess, investPlan, investPlan.getUid());
		}
	}
	
	public boolean checkValidate(PlanInvestEntity pie) {
		long difDay = DateUtil.daysBetween(pie.getCreateTime());
		if (difDay > 2) {
			planInvestService.updateStatus(pie.getOid(), PlanStatus.FAILURE.getCode());
			return false;
		}
		/*
		if (pie.getWithholdCount() >= 9) {
			planInvestService.updateStatus(pie.getOid(), PlanStatus.FAILURE.getCode());
			return false;
		}
		*/
		return true;
	}
	
	/**
	 * depositRedeemBalace
	 * @param pie
	 * @return
	 */
	public int depositRedeemBalace(PlanInvestEntity pie) {
		RedeemToBasicRequest req = new RedeemToBasicRequest();
		req.setBalance(pie.getDepositAmount());
		req.setUserOid(pie.getUid());
		req.setOpposition(true);
		int result = -1;
		try {
			result = accmentService.redeem2basic(req);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		TradeOrderRep rep = new TradeOrderRep();
		if (result == 0) {
			rep.setErrorCode(0);
			String tradeOrderOid = planInvestService.depositSucess(pie);
			rep.setTradeOrderOid(tradeOrderOid);
			if (null != pie.getMonthOid()) {
				updatePlanInvest(planMonthDao.findByOid(pie.getMonthOid()), pie.getDepositAmount(), pie.getExpectedAmount());
			}
			log.info("月定投划扣成功 uid={}, planId={}, money={}, endTime={}", pie.getUid(), pie.getOid(),
					pie.getDepositAmount(), DateUtil.getSqlCurrentDate());
			return 0;
		} else {
			rep.setErrorCode(-1);
			planInvestService.updateStatus(pie.getOid(), PlanStatus.TODEPOSIT.getCode());
			log.info("月定投划扣失败 uid={}, planId={}, money={}, endTime={}", pie.getUid(), pie.getOid(),
					pie.getDepositAmount(), DateUtil.getSqlCurrentDate());
			return -1;
		}
	}
	
	/**
	 * withhold
	 * @param pie
	 */
	private void withhold(PlanInvestEntity pie) {
		DepositBankOrderbfReq req = new DepositBankOrderbfReq();
		req.setOrderAmount(pie.getDepositAmount());
		req.setWishplanOid(pie.getOid());
		BankOrderRep rep = investorBankOrderExtService.depositbf(req, pie.getUid());
		planInvestService.addWithholdCount(pie.getOid());
		if (rep.getErrorCode() == 0) {
			log.info("代扣请求成功 oid={}, money={}", pie.getOid(), pie.getDepositAmount());
		} else {
			log.info("代扣失败 oid={}, money={}, failTime={}", pie.getOid(), pie.getDepositAmount(),  pie.getWithholdCount() + 1);
			if (DateUtil.getCurrentHour() >= Constant.LATEST_WITHHOLD_TIME) {
				parseErrorCode(rep.getErrorMessage(), pie); 
			}
		}
		
	}
	
	public void parseErrorCode(String input, PlanInvestEntity pie) {
		Pattern pattern = Pattern.compile("CODE:\\d{5,}");
		Matcher matcher = pattern.matcher(input);
	
		if(matcher.find()) {
			if(matcher.group().length() == 10){
				log.info("matcher={}", matcher.group());
				String strCode = matcher.group().substring(5);
				int code = Integer.parseInt(strCode);
			    if (code == Constant.BALANCE_LESS_CODE) {
			    	thirdNotifyService.callMsgMail(MsgParam.msgfirstbymonthbuyfail, pie, pie.getUid());
			    } else if (code == Constant.UNSUPPORT_CARD_CODE) {
			    	thirdNotifyService.callMsgMail(MsgParam.msgDeductFailForSystemNoSupportCard, pie, pie.getUid());
			    } else if (code == Constant.UNSUPPORT_CHANNLE_CODE) {
			    	thirdNotifyService.callMsgMail(MsgParam.msgDeductFailForChannelNoSupportCard, pie, pie.getUid());
			    } else if (code == Constant.UNSUPPORT_ONLINE_CODE) {
			    	thirdNotifyService.callMsgMail(MsgParam.msgDeductFailForOnlineBanking, pie, pie.getUid());
			    } else {
			    	thirdNotifyService.callMsgMail(MsgParam.msgDeductFailForOtherReason, pie, pie.getUid());
			    }
			}
		} else {
			thirdNotifyService.callMsgMail(MsgParam.msgDeductFailForOtherReason, pie, pie.getUid());
		}
	}
	public void withHoldCallback(String wishplanOid, boolean isOk) {
//		if (planInvestService.checkDeposited(wishplanOid)) {
//			log.info("已代扣 wishplanOid={}", wishplanOid);
//			return;
//		}
		PlanInvestEntity pie = planInvestService.queryByOid(wishplanOid);
		if (isOk) {
			depositRedeemBalace(pie);
		    //Call third msg and mail.
			thirdNotifyService.callMsgMail(MsgParam.msgbymonthbuysuccess, pie, pie.getUid());
			log.info("代扣回调成功 oid={}, money={}", pie.getOid(), pie.getDepositAmount());
		} else {
//			if (DateUtil.getCurrentHour() >= Constant.LATEST_WITHHOLD_TIME) {
//				thirdNotifyService.callMsgMail(MsgParam.msgfirstbymonthbuyfail, pie, pie.getUid());
//			}
			log.info("代扣失败 uid={}, money={}", pie.getUid(), pie.getDepositAmount());
		}
	}
	
	public void withholdCurrent(PlanMonthEntity pme) {
		
		int today = DateUtil.getTodayOnMonth();
		if (today == pme.getMonthInvestDate() && nearSchedule()) {
			processOneMonthPlan(pme);
		}
	}
	
	static final int NEAR_SCHEDULE_MINUTES = 10;
	private boolean nearSchedule() {
		String t1 = DateUtil.getPlanMonthStrDate() + " 10:05:00";
		String t2 = DateUtil.getPlanMonthStrDate() + " 14:05:00";
		String t3 = DateUtil.getPlanMonthStrDate() + " 21:05:00";
		if ((0 < DateUtil.getTimeRemainMinutes(t1) && DateUtil.getTimeRemainMinutes(t1) < NEAR_SCHEDULE_MINUTES)
			|| (0 < DateUtil.getTimeRemainMinutes(t2) && DateUtil.getTimeRemainMinutes(t2) < NEAR_SCHEDULE_MINUTES)
			|| (0 < DateUtil.getTimeRemainMinutes(t3) && DateUtil.getTimeRemainMinutes(t3) <NEAR_SCHEDULE_MINUTES)) {
			return false;
		} else {
			return true;
		}
	}
	/**
	 * First
	 * @param pie
	 */
	private void baoquanRecord(PlanMonthEntity pme) {
		//Record ebaoquan
		if (baoquanService.checkRecord(pme.getOid(), EbaoquanRecord.EBAOQUAN_TYPE_WISHPLAN_MONTH)) {
			baoquanService.eBaoquanRecord(EbaoquanRecord.EBAOQUAN_TYPE_WISHPLAN_MONTH, pme);
		}
	}

}
