package com.guohuai.mmp.jiajiacai.wishplan.plan.service;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.guohuai.ams.product.Product;
import com.guohuai.ams.product.ProductDao;
import com.guohuai.ams.product.ProductDecimalFormat;
import com.guohuai.basic.component.ext.web.PageResp;
import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.DateUtil;
import com.guohuai.component.util.StringUtil;
import com.guohuai.mmp.investor.bank.BankDao;
import com.guohuai.mmp.investor.bank.BankEntity;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountDao;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountEntity;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderEntity;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderService;
import com.guohuai.mmp.jiajiacai.caculate.JJCUtility;
import com.guohuai.mmp.jiajiacai.common.constant.InvestTypeEnum;
import com.guohuai.mmp.jiajiacai.common.constant.PlanStatus;
import com.guohuai.mmp.jiajiacai.common.exception.ErrorMessage;
import com.guohuai.mmp.jiajiacai.rep.MonthPlanList;
import com.guohuai.mmp.jiajiacai.rep.QueryOnceInvestInfo;
import com.guohuai.mmp.jiajiacai.wishplan.plan.dao.BackPlanInvestDao;
import com.guohuai.mmp.jiajiacai.wishplan.plan.dao.PlanInvestDao;
import com.guohuai.mmp.jiajiacai.wishplan.plan.dao.PlanMonthDao;
import com.guohuai.mmp.jiajiacai.wishplan.plan.dao.PlanProductDao;
import com.guohuai.mmp.jiajiacai.wishplan.plan.entity.BackPlanInvestEntity;
import com.guohuai.mmp.jiajiacai.wishplan.plan.entity.PlanInvestEntity;
import com.guohuai.mmp.jiajiacai.wishplan.plan.entity.PlanMonthEntity;
import com.guohuai.mmp.jiajiacai.wishplan.plan.entity.PlanProductEntity;
import com.guohuai.mmp.jiajiacai.wishplan.plan.form.DepositProfit;
import com.guohuai.mmp.jiajiacai.wishplan.plan.form.PlanFormVO;
import com.guohuai.mmp.jiajiacai.wishplan.plan.form.PlanProfitForm;
import com.guohuai.mmp.jiajiacai.wishplan.plan.form.PlanProfitListForm;
import com.guohuai.mmp.jiajiacai.wishplan.plan.rep.InstalmentRep;
import com.guohuai.mmp.jiajiacai.wishplan.plan.rep.MonthPlanByOidRep;
import com.guohuai.mmp.jiajiacai.wishplan.planlist.PlanListEntity;
import com.guohuai.mmp.jiajiacai.wishplan.planlist.PlanListService;
import com.guohuai.mmp.platform.msgment.MsgUtil;
import com.guohuai.mmp.platform.msgment.log.MsgLogDao;
import com.guohuai.mmp.platform.msgment.log.MsgLogEntity;
import com.guohuai.mmp.publisher.hold.BackMonthDeductInfo;
import com.guohuai.mmp.publisher.hold.MonthPageResp;
import com.guohuai.mmp.publisher.hold.PlanIncomeInfo;
import com.guohuai.mmp.publisher.hold.PlanPageResp;
import com.guohuai.mmp.publisher.hold.PlanRelateProductInfo;
import com.guohuai.mmp.publisher.hold.PublisherHoldDao;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class PlanBaseService {

	@Autowired
	private PlanInvestService planInvestService;

	@Autowired
	private PlanMonthService planMonthService;

	@Autowired
	private PlanListService planListService;

	@Autowired
	private PlanMonthDao planMonthDao;
	@Autowired
	private BankDao bankDao;
	@Autowired
	private PlanInvestDao planInvestDao;
	@Autowired
	private BackPlanInvestDao backPlanInvestDao;
	@Autowired
	private PlanProductDao  planProductDao;
	@Autowired
	private ProductDao productDao;
	@Autowired
	private PublisherHoldDao publisherHoldDao;

//	@Autowired
//	private InvestorStatisticsService investorStatisticsService;

	@Autowired
	private InvestorBaseAccountDao investorBaseAccountDao;
	@Autowired
	private InvestorTradeOrderService investorTradeOrderService;
	
	@Autowired
	private MsgLogDao msgLogDao;
	@PersistenceContext
	private EntityManager em;
	
	
	
	private void oncePlans(String uid, DepositProfit totalDp, List<PlanProfitForm> resultList) {

		List<String> typeL = Arrays.asList(InvestTypeEnum.OnceTourInvest.getCode(),
				InvestTypeEnum.OnceEduInvest.getCode());
		List<String> statusL = Arrays.asList(PlanStatus.SUCCESS.getCode(), PlanStatus.DEPOSITED.getCode(),
				PlanStatus.REDEEMING.getCode());
//		List<String> statusL = Arrays.asList(PlanStatus.SUCCESS.getCode(), PlanStatus.DEPOSITED.getCode(),
//				PlanStatus.REDEEMING.getCode(), PlanStatus.COMPLETE.getCode());
		List<PlanInvestEntity> tourList = planInvestDao.findSuccessByUidTypeStatus(uid, typeL, statusL);
		if (tourList.size() > 0) {
			for (PlanInvestEntity entity : tourList) {
				float depositAmount = entity.getDepositAmount().floatValue();
				float profitAmount = 0;
				if (entity.getStatus().equals(PlanStatus.COMPLETE.getCode())) {
					profitAmount = entity.getBalance().floatValue();
				} else {
					profitAmount = entity.getDepositAmount().floatValue();
				}
				
				PlanProfitForm form = new PlanProfitForm();
				form.setDepositAmount(JJCUtility.keep2Decimal(depositAmount));
				form.setIncomeAmount(JJCUtility.keep2Decimal(profitAmount));
				form.setPlanListName(JJCUtility.plantype2Str(entity.getPlanType()));
//				+ DateUtil.timestamp2FullStr(entity.getCreateTime()));
				form.setPlanType(entity.getPlanType());
				form.setCreateTime(entity.getCreateTime());
				form.setPlanOid(entity.getOid());

				totalDp.setDepositAmount(totalDp.getDepositAmount() + depositAmount);
				totalDp.setProfitAmount(totalDp.getProfitAmount() + profitAmount);

				resultList.add(form);
			}
		}

	}
	/**
	 * Deprecated
	 * @param uid
	 * @param type
	 * @return
	 */
	private DepositProfit oncePlan(String uid, String type) {
		List<String> statusL = Arrays.asList(PlanStatus.COMPLETE.getCode());
		List<PlanInvestEntity> tourList = planInvestService.querySuccessPlanByUidType(uid, type, statusL);

		int depositAmount = 0;
		float profitAmount = 0;
		if (tourList.size() > 0) {
			for (PlanInvestEntity entity : tourList) {
				depositAmount += entity.getDepositAmount().intValue();
				profitAmount += entity.getBalance().floatValue();
			}
		}

		// Not complete
		statusL = Arrays.asList(PlanStatus.SUCCESS.getCode(), PlanStatus.DEPOSITED.getCode(),
				PlanStatus.REDEEMING.getCode());
		tourList = planInvestService.querySuccessPlanByUidType(uid, type, statusL);
		if (tourList.size() > 0) {
			for (PlanInvestEntity entity : tourList) {
				depositAmount += entity.getDepositAmount().intValue();
				profitAmount += entity.getDepositAmount().floatValue();
			}
		}

		DepositProfit dp = new DepositProfit();
		dp.setDepositAmount(depositAmount);
		dp.setProfitAmount(profitAmount);
		return dp;
	}

	public DepositProfit oneMonthPlan(PlanMonthEntity month) {

		List<String> statusL = Arrays.asList(PlanStatus.COMPLETE.getCode(), PlanStatus.SUCCESS.getCode(),
				PlanStatus.DEPOSITED.getCode(), PlanStatus.REDEEMING.getCode());
		List<PlanInvestEntity> tourList = planInvestService.querySuccessPlanByMonth(month.getOid(), statusL);

		float depositAmount = 0;
		float profitAmount = 0;
		if (tourList.size() > 0) {
			for (PlanInvestEntity entity : tourList) {
				depositAmount += entity.getDepositAmount().floatValue();
				profitAmount += caculuateOneInvestIncome(entity, true);
			}
		}
		DepositProfit dp = new DepositProfit();
		dp.setDepositAmount(depositAmount);
		dp.setProfitAmount(profitAmount);
		return dp;

	}

	
	
	private void eduTourSalaryMonthPlans(String uid, DepositProfit totalDp, List<PlanProfitForm> resultList) {
		List<PlanMonthEntity> listMonth = planMonthDao.queryAllSuccessPlanMonth(uid);

		for (PlanMonthEntity month : listMonth) {
			DepositProfit one = oneMonthPlan(month);//已经成功投资的，包括月定投已经完成的
			PlanProfitForm form = new PlanProfitForm();
			form.setDepositAmount(JJCUtility.keep2Decimal(one.getDepositAmount()));
			form.setIncomeAmount(JJCUtility.keep2Decimal(one.getProfitAmount()));
			form.setPlanListName(JJCUtility.plantype2Str(month.getPlanType()));
//					+ DateUtil.timestamp2FullStr(month.getCreateTime()));
			form.setPlanType(month.getPlanType());
			form.setCreateTime(month.getCreateTime());
			form.setPlanOid(month.getOid());

			totalDp.setDepositAmount(totalDp.getDepositAmount() + one.getDepositAmount());
			totalDp.setProfitAmount(totalDp.getProfitAmount() + one.getProfitAmount());

			resultList.add(form);
		}

	}

	private DepositProfit eduTourSalaryMonthPlan(String uid, String type) {
		List<PlanMonthEntity> listMonth = planMonthService.querySuccessPlanMonthByUid(uid, type);
		DepositProfit dp = new DepositProfit();
		for (PlanMonthEntity month : listMonth) {
			DepositProfit one = oneMonthPlan(month);//已经成功投资的，包括月定投已经完成的
			dp.setDepositAmount(dp.getDepositAmount() + one.getDepositAmount());
			dp.setProfitAmount(dp.getProfitAmount() + one.getProfitAmount());
		}

		return dp;
	}

	
	public PlanProfitListForm satisticsPlanList(String uid) {

		PlanProfitListForm genericForm = new PlanProfitListForm();

		if (uid == null) {
			genericForm.setErrorCode(-1);
			genericForm.setErrorMessage(ErrorMessage.USER_NOT_LOGIN);
			return genericForm;
		}
		
		DepositProfit totalDp = new DepositProfit();
		List<PlanProfitForm> resultList = new ArrayList<PlanProfitForm>();
		
		oncePlans(uid, totalDp, resultList);
//
		eduTourSalaryMonthPlans(uid, totalDp, resultList);
		
		if (resultList.size() > 1) {
			Collections.sort(resultList, new Comparator<PlanProfitForm>() {
				@Override
				public int compare(PlanProfitForm o1, PlanProfitForm o2) {
					int flag = o1.getCreateTime().compareTo(o2.getCreateTime());
					return -flag;
				}
			});
		}
		
		/*
		if (resultList.size() > 1) {
			Collections.sort(resultList, (PlanProfitForm o1, PlanProfitForm o2) -> {
					int flag = o1.getCreateTime().compareTo(o2.getCreateTime());
					return -flag;
				}
			);
		*/
			
		genericForm.setProfitList(resultList);
		
		/** 到期的心愿计划会赎回到余额中 */
		
//		PlanProfitListForm  planProfitListForm = calcInvestHoldPlanAmount(genericForm, uid);
		genericForm.setTotalDepositAmount(JJCUtility.keep2Decimal(totalDp.getDepositAmount()));
		genericForm.setTotalExpectedAmount(JJCUtility.keep2Decimal(totalDp.getProfitAmount()));
		
		
		/** 心愿计划的累计投资总资产和累计本息和 */	
		
		completePlans(uid, totalDp);
		
		genericForm.setTotalholdInvestAmount(JJCUtility.keep2Decimal(totalDp.getDepositAmount()));
		genericForm.setTotalholdAmountIncome(JJCUtility.keep2Decimal(totalDp.getProfitAmount()));
		
		genericForm.setErrorCode(0);
		return genericForm;
	}
	/**
	 * Deprecated, 2018-01-29
	 * @param uid
	 * @return
	 */
	public PlanProfitListForm satisticsPlanList0129(String uid) {

		PlanProfitListForm genericForm = new PlanProfitListForm();

		if (uid == null) {
			genericForm.setErrorCode(-1);
			genericForm.setErrorMessage(ErrorMessage.USER_NOT_LOGIN);
			return genericForm;
		}
		DepositProfit totalDp = new DepositProfit();
		List<PlanProfitForm> resultList = new ArrayList<PlanProfitForm>();
		// Once tour
		DepositProfit onceTourDp = oncePlan(uid, InvestTypeEnum.OnceTourInvest.getCode());//计算一次性定投的投资金额和累计收益
		generateProfitForm(totalDp, resultList, onceTourDp, InvestTypeEnum.OnceTourInvest.getCode());
		// Once edu
		DepositProfit onceEduDp = oncePlan(uid, InvestTypeEnum.OnceEduInvest.getCode());
		generateProfitForm(totalDp, resultList, onceEduDp, InvestTypeEnum.OnceEduInvest.getCode());

		// Month tour
		DepositProfit monthTourDp = eduTourSalaryMonthPlan(uid, InvestTypeEnum.MonthTourInvest.getCode());
		generateProfitForm(totalDp, resultList, monthTourDp, InvestTypeEnum.MonthTourInvest.getCode());

		// Month edu
		DepositProfit monthEduDp = eduTourSalaryMonthPlan(uid, InvestTypeEnum.MonthEduInvest.getCode());
		generateProfitForm(totalDp, resultList, monthEduDp, InvestTypeEnum.MonthEduInvest.getCode());

		// Salary
		DepositProfit monthSalaryDp = eduTourSalaryMonthPlan(uid, InvestTypeEnum.MonthSalaryInvest.getCode());
		generateProfitForm(totalDp, resultList, monthSalaryDp, InvestTypeEnum.MonthSalaryInvest.getCode());
		
		
		/** 心愿计划的累计投资总资产和累计本息和 */
		genericForm.setTotalholdInvestAmount(JJCUtility.keep2Decimal(totalDp.getDepositAmount()));
		genericForm.setTotalholdAmountIncome(JJCUtility.keep2Decimal(totalDp.getProfitAmount()));
		
		genericForm.setProfitList(resultList);
		/** 到期的心愿计划会赎回到余额中 */
		PlanProfitListForm  planProfitListForm = calcInvestHoldPlanAmount(genericForm, uid);
		genericForm.setTotalDepositAmount(planProfitListForm.getTotalDepositAmount());
		genericForm.setTotalExpectedAmount(planProfitListForm.getTotalExpectedAmount());
		
		
		genericForm.setErrorCode(0);
		return genericForm;
	}

	private void generateProfitForm(DepositProfit totalDp, List<PlanProfitForm> resultList, DepositProfit monthDp,
			String planType) {
		if (monthDp.getDepositAmount() > 0) {
			PlanProfitForm form = new PlanProfitForm();
			form.setDepositAmount(JJCUtility.keep2Decimal(monthDp.getDepositAmount()));
			form.setIncomeAmount(JJCUtility.keep2Decimal(monthDp.getProfitAmount()));
			form.setPlanListName(planListService.findByPlanType(planType).getName());
			form.setPlanType(planType);

			totalDp.setDepositAmount(totalDp.getDepositAmount() + monthDp.getDepositAmount());
			totalDp.setProfitAmount(totalDp.getProfitAmount() + monthDp.getProfitAmount());

			resultList.add(form);
		}
	}

	public List<PlanFormVO> getOwnerPlanList(String uid) {

		List<PlanInvestEntity> listOnce = planInvestService.queryOncePlanByUid(uid);
		List<PlanMonthEntity> listMoth = planMonthService.queryByUid(uid);
		List<PlanFormVO> resultList = new ArrayList<PlanFormVO>();

		for (PlanMonthEntity entity : listMoth) {
			PlanFormVO form = new PlanFormVO();
			form.setOid(entity.getOid());
			PlanListEntity listEntity = planListService.findByPlanType(entity.getPlanType());
			form.setPlanType(entity.getPlanType());
			form.setPlanListOid(listEntity.getOid());
			form.setPlanListName(listEntity.getName());
			form.setDepositAmount(entity.getTotalDepositAmount());
			form.setStatus(entity.getStatus());
			form.setStartTime(entity.getCreateTime());
			if (entity.getPlanType() != null
					&& entity.getPlanType().equals(InvestTypeEnum.MonthSalaryInvest.getCode())) {
				form.setCategory(PlanFormVO.SALARY_MONTH);
			} else {
				form.setCategory(PlanFormVO.TOUR_EDU_MONTH);
			}
			resultList.add(form);
		}

		for (PlanInvestEntity entity : listOnce) {
			PlanFormVO form = new PlanFormVO();
			form.setOid(entity.getOid());
			PlanListEntity listEntity = planListService.findByPlanType(entity.getPlanType());
			form.setPlanType(InvestTypeEnum.OnceEduInvest.getCode());
			form.setPlanListOid(listEntity.getOid());
			form.setPlanListName(listEntity.getName());
			form.setDepositAmount(entity.getDepositAmount());
			form.setStatus(entity.getStatus());
			form.setStartTime(entity.getCreateTime());
			form.setEndTime(entity.getEndTime());
			form.setCategory(PlanFormVO.TOUR_EDU_ONCE);
			resultList.add(form);
		}
		if (resultList.size() > 1) {
			Collections.sort(resultList, new Comparator<PlanFormVO>() {
				@Override
				public int compare(PlanFormVO o1, PlanFormVO o2) {
					int flag = o1.getStartTime().compareTo(o2.getStartTime());
					return -flag;
				}
			});
		}
		return resultList;
	}
	
	/**
	 * 
	 * @param depositLis
	 * @return
	 */
	private int lastYearCount(List<PlanInvestEntity> depositLis) {
		int count = 0;
		for (PlanInvestEntity pe : depositLis) {
			if (DateUtil.getYearFromDate(pe.getCreateTime()) < Integer.parseInt(DateUtil.getCurrentYear())) {
				++count; 
			}
		}
		return count;
	}
	/**
	 * 按月定投心愿计划详情
	 * 
	 * @param oid
	 * @return
	 */
	public MonthPlanByOidRep getMonthPlanDetail(String oid) {
		PlanMonthEntity entity = planMonthService.queryByOid(oid);
		if (entity == null) {
			throw new AMPException(ErrorMessage.MYPLAN_NOT_EXIST);
		}
		MonthPlanByOidRep rep = new MonthPlanByOidRep();
		// 根据计划类型查找计划实体
		PlanListEntity listEntity = planListService.findByPlanType(entity.getPlanType());
		// 查询月定投记录
		List<PlanInvestEntity> depositList = planInvestService.queryByPlanMonthOid(entity.getOid());
		String pStatus = null;
		if (entity.getStatus().equals("STOP") || entity.getStatus().equals("REDEEMING")) {
			pStatus = "已停止";
		} else if (entity.getStatus().equals("READY") || entity.getStatus().equals("SUCCESS")
				|| entity.getStatus().equals("DEPOSITED") || entity.getStatus().equals("TODEPOSIT")) {
			pStatus = "进行中";
		} else if (entity.getStatus().equals("COMPLETE")) {
			pStatus = "已结清";
		}
		// 计算投资月份
		int planMonthCount = 0;
		if (entity.getPlanType().contentEquals(InvestTypeEnum.MonthSalaryInvest.getCode())) {
			// The next month invest, and no deposit record.
			/*
			if ((depositList.size() == 0) && (Integer.parseInt(DateUtil.getCurrentMonth()) != DateUtil
					.getMonthFromDate(entity.getStartInvestDate()))) {
				planMonthCount = 13 - DateUtil.getMonthFromDate(entity.getStartInvestDate());
			} else {
				*/
				if (entity.getStatus().equals(PlanStatus.READY.getCode())) {
					/*
					planMonthCount = depositList.size() + 12 - Integer.parseInt(DateUtil.getCurrentMonth());
					planMonthCount += lastYearCount(depositList);

					int todayOnMonth = Integer.parseInt(DateUtil.getCurrentDay());
					if (entity.getMonthInvestDate() > todayOnMonth) {
						planMonthCount += 1;
					}
					*/
					int startYear = DateUtil.getYearFromDate(entity.getStartInvestDate());
					int startMonth = DateUtil.getMonthFromDate(entity.getStartInvestDate());
					int endYear = DateUtil.getCurrentYearInt();
//					if (DateUtil.getCurrentMonthInt() == 12) {
//						++endYear;
//					}
					int endMonth = 12;
					planMonthCount = (endYear - startYear) * 12 + endMonth -  startMonth + 1;
					planMonthCount += 12 - endMonth;
				} else {
					int startYear = DateUtil.getYearFromDate(entity.getStartInvestDate());
					int startMonth = DateUtil.getMonthFromDate(entity.getStartInvestDate());
					int endYear = entity.getLastInvestMonth() / 100;
					int endMonth = entity.getLastInvestMonth() % 100;
					if (endYear == 0) {
						endYear = startYear;
						endMonth = 12;
					}
					
					planMonthCount = (endYear - startYear) * 12 + endMonth -  startMonth + 1;
					planMonthCount += 12 - endMonth;
				}
			//}
		} else {
			planMonthCount = entity.getPlanMonthCount();
			if (planMonthCount < depositList.size()) {
				planMonthCount = depositList.size();
			}
		}
		// 查询投资绑定银行卡信息，截取卡号后四位
		BankEntity bEntity = bankDao.getOKBankByInvestorOid(entity.getUid());
		String bankCardInfo = null;
		if (bEntity != null && bEntity.getDebitCard() != null && bEntity.getDebitCard().length() > 4) {
			int cardLen = bEntity.getDebitCard().length();
			bankCardInfo = bEntity.getBankName() + "(尾号" + bEntity.getDebitCard().substring(cardLen - 4, cardLen) + ")";
		} else {
			bankCardInfo = "无银行卡绑定";
		}
		// 获取分期付款年份月份及对应状态
		List<InstalmentRep> instalment = new ArrayList<InstalmentRep>();
		int firstYear = DateUtil.getYearFromDate(entity.getStartInvestDate());
		int firstMoth = DateUtil.getMonthFromDate(entity.getStartInvestDate());
		for (int i = 0; i < planMonthCount; i++) {
			InstalmentRep iRep = new InstalmentRep();
			if (firstMoth > 12) {
				firstYear = firstYear + 1;
				firstMoth = firstMoth - 12;
			}
			iRep.setYear(firstYear);
			iRep.setMonth(firstMoth);
			String status = getMonthPlanStatus(firstYear, firstMoth, depositList);
			//Fixed the bug of stop
			if (status == null) {
				if (entity.getStatus().equals("STOP") || entity.getStatus().equals("REDEEMING") 
						|| entity.getStatus().equals("COMPLETE")) {
					status = "stop";
				} else {
					if ((firstYear * 100 + firstMoth) < entity.getLastInvestMonth()) {
						status = "fail";
					} else {
						status = "wait";
					}
				}
			}
			iRep.setStatus(status);

			instalment.add(iRep);
			firstMoth = firstMoth + 1;
		}
		//遍历获取累计投资金额、本息累计
		float totalAmount = 0;		
		float principalAndInterest = 0;
		for (int i = 0; i < planMonthCount; i++) {
			PlanInvestEntity currentMd = null;
			if (i < depositList.size()) {
				currentMd = depositList.get(i);
			}
			if (currentMd != null) {
				if (PlanStatus.DEPOSITED.getCode().contentEquals(currentMd.getStatus())
						|| PlanStatus.SUCCESS.getCode().contentEquals(currentMd.getStatus())
						|| PlanStatus.REDEEMING.getCode().contentEquals(currentMd.getStatus())
						|| PlanStatus.COMPLETE.getCode().contentEquals(currentMd.getStatus())) {
					principalAndInterest += caculuateOneInvestIncome(currentMd, false);
					totalAmount += currentMd.getDepositAmount().floatValue();
				}
			}
		}
		//获取累计已投期数
		int totalMonths = caculateConter(depositList);
		//获取赎回状态
		int transferBalance = 0;
		if (InvestTypeEnum.MonthSalaryInvest.getCode().equals(entity.getPlanType())) {
			if (entity.getStatus().equals(PlanStatus.REDEEMING.getCode())
				|| entity.getStatus().equals(PlanStatus.COMPLETE.getCode())) {
				transferBalance = 1;
			} if (entity.getStatus().equals(PlanStatus.STOP.getCode()) 
					&& planInvestService.checkTransferBalacen(oid)) {
				transferBalance = 0;
			} else {
				transferBalance = -1;
			}
		}
		PlanInvestEntity onceInvest = this.planInvestDao.findByMonthOidAsc(entity.getOid());
		if (onceInvest != null) {
			InvestorTradeOrderEntity tradeOrder = this.investorTradeOrderService.findBywishplanOid(onceInvest.getOid(),
					"wishInvest");
			if (tradeOrder != null) {
				rep.setOrderCode(tradeOrder.getOrderCode());
			}
		}
		
		
		rep.setPlanOid(entity.getOid());
		rep.setPlanName(listEntity.getName());
		rep.setPlanTarget(entity.getPlanTarget());
		rep.setInvestType("按月定投");
		rep.setInvestDuration(planMonthCount);
		rep.setAddTime(entity.getCreateTime());
		rep.setFinishTime(entity.getEndTime());
		rep.setInstalment(instalment);
		rep.setMonthAmount(entity.getMonthAmount());
		rep.setMonthTime(entity.getMonthInvestDate());
		rep.setBankCardInfo(bankCardInfo);
		rep.setPlanStatus(pStatus);
		rep.setTotalAmount(JJCUtility.keep2Decimal(totalAmount));
		rep.setTotalMonths(totalMonths);
		rep.setPrincipalAndInterest(JJCUtility.keep2Decimal(principalAndInterest));
		rep.setTransferBalance(transferBalance);
		rep.setRedeemStatus(entity.getStatus());

		return rep;
	}

	private float caculuateOneInvestIncome(PlanInvestEntity currentMd, boolean actual) {
		// Salary
		if (currentMd.getPlanType().equals(InvestTypeEnum.MonthSalaryInvest.getCode())) {
			return planInvestService.caculuateOneMonthIncome(currentMd);
		} else {
			if (PlanStatus.COMPLETE.getCode().equals(currentMd.getStatus())) {
				return currentMd.getBalance().floatValue();
			} else {
				if (actual) {
					return currentMd.getDepositAmount().floatValue();
				} else {
					return currentMd.getExpectedAmount().floatValue();
				}

			}
		}

	}

	private int caculateConter(List<PlanInvestEntity> depositList) {
		int i = 0;
		for (PlanInvestEntity entity : depositList) {
			if (PlanStatus.SUCCESS.getCode().equals(entity.getStatus())
					|| PlanStatus.REDEEMING.getCode().equals(entity.getStatus())
					|| PlanStatus.DEPOSITED.getCode().equals(entity.getStatus())
					|| PlanStatus.COMPLETE.getCode().equals(entity.getStatus())) {
				++i;
			}

		}
		return i;
	}

	public PageResp<MonthPlanList> getRootOwnerMonthPlanList(Specification<PlanMonthEntity> spec, Pageable pageable) {
		Page<PlanMonthEntity> cas = planMonthDao.findAll(spec, pageable);
		PageResp<MonthPlanList> pagesRep = new PageResp<MonthPlanList>();
		if (cas != null && cas.getContent() != null && cas.getTotalElements() > 0) {
			for (PlanMonthEntity entity : cas) {
				MonthPlanList form = new MonthPlanList();
				form.setOid(entity.getOid());// 计划的id
				form.setPlanType(entity.getPlanType());
				PlanListEntity listEntity = planListService.findByPlanType(entity.getPlanType());
				form.setPlanListName(listEntity.getName());// 计划名称
				form.setCreateTime(entity.getCreateTime());// 计划加入的时间
				form.setDepositAmount(entity.getTotalDepositAmount());// 月定投累计投资金额
				// 判断月定投的投资期限---如果是薪增长，则无期限
				form.setInvestDuration(Integer.toString(entity.getPlanMonthCount()));// 投资期限
				form.setMonthInvset(entity.getMonthAmount());// 月定投金额
				form.setTransferDay("每月"+entity.getMonthInvestDate()+"日");// 每月几号进行划扣
				//form.setFirstTransferTime(entity.getStartInvestDate());// 第一次划扣日期
				form.setFirstTransferTime(DateUtil.getCurrDate(entity.getStartInvestDate()));// 第一次划扣日期,格式：2017-12-29
				// 预期年化收益率
				if (entity.getExpectedRate() != null && entity.getExpectedRate().compareTo(new BigDecimal("0")) > 0) {
					form.setExpectRate(
							ProductDecimalFormat.format(ProductDecimalFormat.multiply(entity.getExpectedRate())) + "%");
				}
				// 预计到期收益---薪增长无
				if (!entity.getPlanType().equals(InvestTypeEnum.MonthSalaryInvest.getCode())) {
					
					//心愿计划的预期收益
					form.setExpectedInterest(this.getMonthExceptInterest(entity.getOid()));																				
					form.setExpectAmount(entity.getExpectedAmount().toString());// 到期期望的本息和
				}else{
					form.setExpectedInterest("-");
					form.setExpectAmount("-");
					form.setInvestDuration("-");
				}

				InvestorBaseAccountEntity baseAccount = this.investorBaseAccountDao.findByOid(entity.getUid());

				//yesterday Income
				form.setHoldYesterdayIncome(yesterdayIncome(entity));//昨日收益
				
//				InvestorStatisticsEntity investStatictics = this.investorStatisticsService
//						.findByInvestorBaseAccount(baseAccount);
//				form.setRealInterest(investStatictics.getWishplanIncome());// 实时累计收益
				form.setRealInterest(monthplanRealInterest(entity));// 实时累计收益
				form.setRealAmount(form.getRealInterest().add(entity.getTotalDepositAmount()));// 实时累计的本息和
				if (baseAccount.getPhoneNum().length() == 11) {
					form.setAccountType("主账户");
				} else {
					form.setAccountType("子账户");
				}
				form.setStatus(entity.getStatus());
				form.setStatusDesc(this.judgePlanStatus(entity.getStatus()));
				form.setPlanTarget(entity.getPlanTarget());//心愿目标

				pagesRep.getRows().add(form);
			}
		}
		pagesRep.setTotal(cas.getTotalElements());
		return pagesRep;
	}
	
	//获取心愿计划月定投的期望收益
	private String getMonthExceptInterest(String monthOid) {
		float expectAmount = 0;
		float investAmount = 0;
		List<PlanInvestEntity> planMonth = this.planInvestDao.findDeductInfoByOid(monthOid);
		if(planMonth != null && planMonth.size() > 0){
			//遍历
			for(PlanInvestEntity entity : planMonth){
				investAmount += entity.getDepositAmount().floatValue();
				expectAmount += entity.getExpectedAmount().floatValue();				
			}
		}
		return JJCUtility.keep2Decimal(expectAmount - investAmount);
	}

	/**
	 * yesterdayIncome
	 * @param entity
	 * @return
	 */
	private BigDecimal yesterdayIncome(PlanMonthEntity entity) {
		BigDecimal result = BigDecimal.ZERO;
		//The education and tour income
		if(!entity.getPlanType().equals(InvestTypeEnum.MonthSalaryInvest.getCode())) {
			BigDecimal complete = planMonthDao.completeYesterdayIncome(entity.getOid());
			if (complete != null) {
				result = complete;
			}
		} else {
			 List<String> list = planMonthDao.queryInvestOidBySalary(entity.getOid());
			 if (list.size() != 0) {
				 BigDecimal listIncome = BigDecimal.ZERO;
				 for (String investOid : list) {
					 BigDecimal currentIncome = planMonthDao.yesterdayIncome(investOid);
					 if (currentIncome != null) {
						 listIncome = listIncome.add(currentIncome);
					 }
				 }
				 result = listIncome;
			 }
			
		}
		
		return result;
	}
	
	/**
	 * monthplan realInterest
	 * @param month
	 * @return
	 */
	private BigDecimal monthplanRealInterest(PlanMonthEntity month) { 
		DepositProfit one = oneMonthPlan(month);
		if (one != null) {
			return  new BigDecimal(one.getProfitAmount() - one.getDepositAmount());
		} else {
			return BigDecimal.ZERO;
		}
	}
	
	/**
	 * onceplan RealInterest
	 * @param once
	 * @return
	 */
	
	private BigDecimal onceplanRealInterest(BackPlanInvestEntity once) { 
		if (once.getStatus().equals(PlanStatus.COMPLETE.getCode())) {
			return once.getBalance().subtract(once.getDepositAmount());
		} else {
			return BigDecimal.ZERO;
		}
	}
	
	
	/*
	 * public PageResp<QueryOnceInvestInfo>
	 * getRootOwnerMonthPlanList(Specification<BackPlanMonthEntity> spec,
	 * Pageable pageable) { Page<PlanMonthEntity> cas =
	 * planMonthDao.findAll(spec, pageable); PageResp<QueryOnceInvestInfo>
	 * pagesRep = new PageResp<QueryOnceInvestInfo>(); if(cas!=null &&
	 * cas.getContent()!=null &&cas.getTotalElements()>0){
	 * for(BackPlanMonthEntity entity : cas){ QueryOnceInvestInfo queryMonthInfo
	 * = new QueryOnceInvestInfo();
	 * queryMonthInfo.setOid(planMonthEntity.getOid());
	 * queryMonthInfo.setPlanListName(planMonthEntity.get); } }
	 * pagesRep.setTotal(cas.getTotalElements()); return pagesRep; }
	 */

	/*
	 * public PageResp<PlanFormVO>
	 * getRootOwnerOncePlanList(Specification<PlanInvestEntity> spec, Pageable
	 * pageable) { Page<PlanInvestEntity> cas = planInvestDao.findAll(spec,
	 * pageable); PageResp<PlanFormVO> pagesRep = new PageResp<PlanFormVO>(); if
	 * (cas != null && cas.getContent() != null && cas.getTotalElements() > 0) {
	 * for (PlanInvestEntity entity : cas) { PlanFormVO form = new PlanFormVO();
	 * form.setOid(entity.getOid()); PlanListEntity listEntity =
	 * planListService.findByPlanType(entity.getPlanType());
	 * form.setPlanType(entity.getPlanType());
	 * form.setPlanListOid(listEntity.getOid());
	 * form.setPlanListName(listEntity.getName());
	 * form.setDepositAmount(entity.getDepositAmount());
	 * form.setStatus(entity.getStatus());
	 * form.setStartTime(entity.getCreateTime());
	 * form.setEndTime(entity.getEndTime());
	 * form.setCategory(PlanFormVO.TOUR_EDU_ONCE); pagesRep.getRows().add(form);
	 * } } pagesRep.setTotal(cas.getTotalElements()); return pagesRep; }
	 */

	public PageResp<QueryOnceInvestInfo> getRootOwnerOncePlanList(Specification<BackPlanInvestEntity> spec,
			Pageable pageable) {
		Page<BackPlanInvestEntity> cas = backPlanInvestDao.findAll(spec, pageable);
		PageResp<QueryOnceInvestInfo> pagesRep = new PageResp<QueryOnceInvestInfo>();
		if (cas != null && cas.getContent() != null && cas.getTotalElements() > 0) {
			for (BackPlanInvestEntity entity : cas) {
				QueryOnceInvestInfo onceInvest = new QueryOnceInvestInfo();
				onceInvest.setOid(entity.getOid());
				PlanListEntity listEntity = planListService.findByPlanType(entity.getPlanType());
				onceInvest.setPlanListName(listEntity.getName());
				onceInvest.setPlanType(entity.getPlanType());
				onceInvest.setCreateTime(entity.getCreateTime());
				onceInvest.setDepositAmount(entity.getDepositAmount());
				onceInvest.setInvestDuration(this.getMontBetweenTime(entity.getCreateTime(), entity.getEndTime()));

				if (entity.getExpectedRate() != null && entity.getExpectedRate().compareTo(new BigDecimal("0")) > 0) {
					onceInvest.setExpectRate(
							ProductDecimalFormat.format(ProductDecimalFormat.multiply(entity.getExpectedRate())) + "%");
				}

				onceInvest.setExpectedInterest(entity.getExpectedAmount().subtract(entity.getDepositAmount()));// 到期期望收益
				onceInvest.setExpectAmount(entity.getExpectedAmount());// 预计到期本息

				//Once yesterday income
				BigDecimal onceIncome = planInvestDao.onceYesterdayIncome(entity.getOid());
				if (onceIncome != null) {
					onceInvest.setHoldYesterdayIncome(onceIncome);
				} else {
					onceInvest.setHoldYesterdayIncome(BigDecimal.ZERO);
				}

				
				onceInvest.setRealInterest(onceplanRealInterest(entity));
				onceInvest.setRealAmount(entity.getDepositAmount().add(onceInvest.getRealInterest()));// 每日累计本息
				if (entity.getInvestBaseAccount().getPhoneNum().length() == 11) {
					onceInvest.setAccountType("主账户");
				} else {
					onceInvest.setAccountType("子账户");
				}
				onceInvest.setStatus(entity.getStatus());
				onceInvest.setStatusDesc(this.judgePlanStatus(entity.getStatus()));
				onceInvest.setPlanTarget(entity.getPlanTarget());//心愿目标
				pagesRep.getRows().add(onceInvest);

			}
		}
		pagesRep.setTotal(cas.getTotalElements());
		return pagesRep;
	}

	/** 判断心愿计划进行的状态 */
	public String judgePlanStatus(String status) {

		if (status.equals(PlanStatus.READY.getCode()) || status.equals(PlanStatus.SUCCESS.getCode())
				|| status.equals(PlanStatus.DEPOSITED.getCode()) || status.equals(PlanStatus.TODEPOSIT.getCode())) {
			return "进行中";
		}
		if (status.equals(PlanStatus.STOP.getCode()) ||status.equals(PlanStatus.REDEEMING.getCode())) {
			return "已停止";
		}
		if (status.equals(PlanStatus.COMPLETE.getCode())) {
			return "已结清";
		}
		if (status.equals(PlanStatus.FAILURE.getCode())) {
			return "已失败";
		}
		return "进行中";
	}

	/** 判断两个时间之间的月数 */
	public int getMontBetweenTime(Timestamp t1, Timestamp t2) {
		Date createTime = new Date(t1.getTime());
		Date endTime = new Date(t2.getTime());
		return DateUtil.getMonthSpace(createTime, endTime);// 投资期限为月
	}

	/**
	 * 分期付款对应的状态，wait：待转 done：已转 fail:失败 stop:终止
	 * 
	 * @return
	 */
	public String getMonthPlanStatus(int firstYear, int firstMoth, List<PlanInvestEntity> depositList) {
		String status = null;
		String eMoth = null;
		Timestamp stime = null;
		Timestamp etime = null;
		int eYear = 0;
		// 拼装时间
		String sMoth = firstMoth > 9 ? firstMoth + "" : "0" + firstMoth;
		if (firstMoth == 12) {
			eMoth = "01";
			eYear = firstYear + 1;
		} else {
			eYear = firstYear;
			eMoth = (firstMoth + 1) > 9 ? (firstMoth + 1) + "" : "0" + (firstMoth + 1);
		}
		stime = Timestamp.valueOf(firstYear + "-" + sMoth + "-01 00:00:00");
		etime = Timestamp.valueOf(eYear + "-" + eMoth + "-01 00:00:00");
		for (PlanInvestEntity entity : depositList) {
			if (entity.getCreateTime().after(stime) && entity.getCreateTime().before(etime)) {
				if (entity.getStatus().contentEquals(PlanStatus.READY.getCode())
						|| entity.getStatus().contentEquals(PlanStatus.TODEPOSIT.getCode())) {
					return "wait";
				} else if (entity.getStatus().contentEquals(PlanStatus.STOP.getCode())) {
					return "stop";
				} else if (entity.getStatus().contentEquals(PlanStatus.SUCCESS.getCode())
						|| entity.getStatus().contentEquals(PlanStatus.COMPLETE.getCode())
						|| entity.getStatus().contentEquals(PlanStatus.DEPOSITED.getCode())
						|| entity.getStatus().contentEquals(PlanStatus.REDEEMING.getCode())) {
					return "done";
				} else if (entity.getStatus().contentEquals(PlanStatus.FAILURE.getCode())) {
					return "fail";
				}
			}
		}
		return null;
	}

	/** 判断用户持有的心愿计划的累计投资总额和 累计本息和 */
	public PlanProfitListForm calcInvestHoldPlanAmount(PlanProfitListForm form, String uid) {

		form.setTotalDepositAmount((new BigDecimal(0)).toString());
		form.setTotalExpectedAmount((new BigDecimal(0)).toString());

		// 遍历

		Iterator<PlanProfitForm> iterator = form.getProfitList().iterator();
		while (iterator.hasNext()) {
			PlanProfitForm planProfit = iterator.next();
			if (planProfit.getPlanType().equals(InvestTypeEnum.OnceEduInvest.getCode())) {

				this.holdOnceAmount(uid, planProfit);
			} else if (planProfit.getPlanType().equals(InvestTypeEnum.OnceTourInvest.getCode())) {
				this.holdOnceAmount(uid, planProfit);

			} else if (planProfit.getPlanType().equals(InvestTypeEnum.MonthEduInvest.getCode())) {

				this.holdMonthAmount(uid, planProfit);
			} else if (planProfit.getPlanType().equals(InvestTypeEnum.MonthTourInvest.getCode())) {
				this.holdMonthAmount(uid, planProfit);

			} else if (planProfit.getPlanType().equals(InvestTypeEnum.MonthSalaryInvest.getCode())) {
				this.holdMonthAmount(uid, planProfit);

			}
			form.setTotalDepositAmount(
					(new BigDecimal(form.getTotalDepositAmount()).add(new BigDecimal(planProfit.getDepositAmount())))
							.toString());
			form.setTotalExpectedAmount(
					(new BigDecimal(form.getTotalExpectedAmount()).add(new BigDecimal(planProfit.getIncomeAmount())))
							.toString());
			/** 移除已经结清的心愿计划投资金额为0.00的列表 */
			if ((new BigDecimal(planProfit.getDepositAmount())).compareTo(new BigDecimal(0)) == 0) {

				iterator.remove();
			}
			
			
		}
		return form;
	}
	
	/**
	 * 用户月定投计划持有的心愿计划的总资产和总收益。
	 * 
	 * */
	public void holdMonthAmount(String uid,PlanProfitForm planProfit){
		List<PlanMonthEntity> planMonth = this.planMonthDao.findCompleteByUid(uid,
				planProfit.getPlanType());
		if (planMonth != null) {
			for (PlanMonthEntity month : planMonth) {
				planProfit.setDepositAmount((new BigDecimal(planProfit.getDepositAmount())
						.subtract(month.getTotalDepositAmount()).toString()));// 减去已经结清的投资金额
				planProfit.setIncomeAmount(
						(new BigDecimal(planProfit.getIncomeAmount()).subtract(month.getIncome()))
								.toString());// 减去已经结清的本息和
			}
		}
	}
	
	/**
	 * 用户持有的一次性购买的心愿计划的总资产和总收益
	 * 
	 * */
	public void holdOnceAmount(String uid,PlanProfitForm planProfit){
		List<PlanInvestEntity> planOnce = this.planInvestDao.findCompleteByUid(uid,
				planProfit.getPlanType());
		if (planOnce != null) {
			for (PlanInvestEntity once : planOnce) {
				planProfit.setDepositAmount((new BigDecimal(planProfit.getDepositAmount())
						.subtract(once.getDepositAmount()).toString()));// 减去已经结清的投资金额
				planProfit.setIncomeAmount(
						(new BigDecimal(planProfit.getIncomeAmount()).subtract(once.getBalance()))
								.toString());// 减去已经结清的本息和
			}
		}
	}
	
	public MonthPageResp<BackMonthDeductInfo> queryMonthDeductInfo(String planOid,int page,int rows) {
		MonthPageResp<BackMonthDeductInfo> rep = new MonthPageResp<BackMonthDeductInfo>();	
		PlanMonthEntity planMonth = this.planMonthDao.findByOid(planOid);
		if(planMonth !=null) {
			rep.setCreateTime(planMonth.getCreateTime());//计划的加入时间
			//非薪增长的投资期限
			if(!planMonth.getPlanType().equals(InvestTypeEnum.MonthSalaryInvest.getCode())){
				int investDuration = this.getMontBetweenTime(planMonth.getEndTime(),planMonth.getStartInvestDate());
				rep.setInvestDuration(investDuration);
			}
			PlanListEntity  planList =  this.planListService.findByPlanType(planMonth.getPlanType());
			rep.setPlanListName(planList.getName());//计划名称
			rep.setTotalInvestCount(planMonth.getTotalInvestCount());//已划扣次数
			rep.setTotalInvestAmount(planMonth.getTotalDepositAmount());//累计投资次数
			rep.setMonthAmount(planMonth.getMonthAmount());//每月转入金额
			rep.setMonthInvestDay(planMonth.getMonthInvestDate());//每月几日进行划扣
			rep.setStatus(planMonth.getStatus());//计划状态
			rep.setStatusDesc(this.judgePlanStatus(planMonth.getStatus()));//状态描述
			
			
			Specification<PlanInvestEntity> spec = new Specification<PlanInvestEntity>(){

				@Override
				public Predicate toPredicate(Root<PlanInvestEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
					Predicate a = cb.equal(root.get("monthOid").as(String.class), planOid);
					return cb.and(a);
				}
			};
			//排序
			Pageable pageable = new PageRequest(page - 1, rows, new Sort(new Order(Direction.ASC, "createTime")));
			Page<PlanInvestEntity> planInvestEntity =   this.planInvestDao.findAll(spec,pageable);	
			
			if(planInvestEntity != null && planInvestEntity.getTotalElements() > 0){
				//遍历
				for(PlanInvestEntity planInvest : planInvestEntity){
					BackMonthDeductInfo  backMonthDeductInfo= new BackMonthDeductInfo();
					//执行年+月
					Timestamp  deductTime = planInvest.getCreateTime();
					int year = deductTime.getYear()+1900;
					int month = deductTime.getMonth()+1;
					int day = deductTime.getDay();
					backMonthDeductInfo.setDeductMonth(year+"年"+month+"月");
					
					

					if(planInvest.getWithholdCount() ==0){
						backMonthDeductInfo.setDeductStatusDesc("成功");
//						InvestorTradeOrderEntity tradeOrder =  this.investorTradeOrderService.findBywishplanOid(planInvest.getOid());
						//实际执行月+日
//						Timestamp  realTime = tradeOrder.getCreateTime();
						Timestamp realTime = planInvest.getDepositTime();
						if (realTime == null) {
							realTime = planInvest.getCreateTime();
						}
						backMonthDeductInfo.setRealInvestTime(this.getRealDeductTime(realTime));
						backMonthDeductInfo.setMonthInvestTime(this.getRealDeductTime(planInvest.getCreateTime()));
						//The input time
						backMonthDeductInfo.setIsCardDeduct(false);//是否从银行卡划扣
						backMonthDeductInfo.setRealDeductCount("-");//划扣次数
						backMonthDeductInfo.setRealInvestAmountfromBalance(planInvest.getDepositAmount().toString());//月划扣金额
						backMonthDeductInfo.setRealInvestAmountfromCard("-");//月划扣金额
					}else{
						backMonthDeductInfo.setIsCardDeduct(true);//是否从银行卡划扣
						
						backMonthDeductInfo.setRealDeductCount(Integer.toString(planInvest.getWithholdCount()));//划扣次数
							if(planInvest.getStatus().equals(PlanStatus.FAILURE.getCode())){
								backMonthDeductInfo.setDeductStatusDesc("失败");
							}else if(planInvest.getStatus().equals(PlanStatus.TODEPOSIT.getCode())){
								backMonthDeductInfo.setDeductStatusDesc("划扣中");
							}else{
								backMonthDeductInfo.setDeductStatusDesc("成功");
//								InvestorTradeOrderEntity tradeOrder =  this.investorTradeOrderService.findBywishplanOid(planInvest.getOid());
//								//执行月
//								Timestamp  realTime = tradeOrder.getCreateTime();
								Timestamp realTime = planInvest.getDepositTime();
								if (realTime == null) {
									realTime = planInvest.getCreateTime();
								}
								backMonthDeductInfo.setRealInvestTime(this.getRealDeductTime(realTime));
								//The input time
								backMonthDeductInfo.setMonthInvestTime(this.getRealDeductTime(planInvest.getCreateTime()));
								
								backMonthDeductInfo.setRealInvestAmountfromCard(planInvest.getDepositAmount().toString());//月划扣金额
								backMonthDeductInfo.setRealInvestAmountfromBalance("-");//月划扣金额
							}
					}
					//The comments.
					backMonthDeductInfo.setInvestDesc(generateComments(planMonth, planInvest));
					rep.getRows().add(backMonthDeductInfo);
				}
				rep.setTotal(planInvestEntity.getTotalElements());
				
			}
			
			
		}
		return rep;
	}
	
	/**
	 * generateComments
	 * @param planMonth
	 * @param lastInvest
	 * @return
	 */
	private String generateComments(PlanMonthEntity planMonth, PlanInvestEntity lastInvest) {
		String update = generateCommentBytype(planMonth, lastInvest, COMMENT_TYPE_UPADE); 
		String stop = generateCommentBytype(planMonth, lastInvest, COMMENT_TYPE_STOP); 
		String complete = generateCommentBytype(planMonth, lastInvest, COMMENT_TYPE_COMPLETE);
		if (update == null && stop == null && complete == null) {
			return "";
		} else {
			String result = "";
			if (update != null) {
				result = result + update;
			}
			if (stop != null) {
				if (result.length() > 0) {
					result = result + "<br/>";
				}
				result = result + stop;
			}
			if (complete != null) {
				if (result.length() > 0) {
					result = result + "<br/>";
				}
				result = result + complete;
			}
			return result;
		}
		
	}
	/**
	 * generateUpdateComment
	 * @param planMonth
	 * @param lastInvest
	 * @return
	 */
	static final String COMMENT_TYPE_UPADE = "plan_update";
	static final String COMMENT_TYPE_STOP = "plan_stop";
	static final String COMMENT_TYPE_COMPLETE = "plan_complete";
	private String generateCommentBytype(PlanMonthEntity planMonth, PlanInvestEntity lastInvest, String type) {
		List<String> names = null;
		if (COMMENT_TYPE_UPADE.equals(type)) {
			names = Arrays.asList("msgModifyWageIncreaseSuccess", "msgSonModifyWageIncreaseSuccess", 
					"msgModifyPlanByOneMonthSuccess", "msgSonModifyPlanByOneMonthSuccess");
		} else if (COMMENT_TYPE_STOP.equals(type)){
			names = Arrays.asList("msgStopByMonth", "msgSonStopByMonth");
		} else if (COMMENT_TYPE_COMPLETE.equals(type)) {
			names = Arrays.asList("msgMonthPlanReceivedPayments", "msgSonMonthPlanReceivedPayments");
		}
		int year = DateUtil.getYearFromDate(lastInvest.getCreateTime());
		int month = DateUtil.getMonthFromDate(lastInvest.getCreateTime());
		Timestamp sDate = null;
		Timestamp eDate = null;
		try {
			sDate = DateUtil.fetchTimestamp(DateUtil.getFirstDayZeroTimeOfMonth(year, month, "yyyy-MM-dd") + " 00:00:00");
			eDate = DateUtil.fetchTimestamp(DateUtil.getLastDayLastTimeOfMonth(year, month, "yyyy-MM-dd") + " 24:00:00");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<MsgLogEntity> logList = msgLogDao.getMsgLogByObject(planMonth.getOid(), names, sDate, eDate);
		if (logList.size() == 0) {
			return null;
		}
		if (COMMENT_TYPE_UPADE.equals(type)) {
			String before = "";
			String after = "";
			for (MsgLogEntity log : logList) {

				List<String> values = MsgUtil.disAssembleMsgParams(log.getSendObj());
				if (log.getCreateTime().compareTo(lastInvest.getCreateTime()) > 0) {
					if (after.length() > 0) {
						after = after + "<br/>";
					}
					after = after + String.format("%s 用户修改转入金额为%s元，转入日期为每月%s日，下月执行", getRealDeductTime(log.getCreateTime()), values.get(3), values.get(2));
				} else {
					if (before.length() > 0) {
						before = before + "<br/>";
					}
					before = before + String.format("%s 用户修改转入金额为%s元，转入日期为每月%s日，本月执行", getRealDeductTime(log.getCreateTime()), values.get(3), values.get(2));
				}
			}
			
			return before + after;
			
		} else if (COMMENT_TYPE_STOP.equals(type)) {
			String stop = null;
			for (MsgLogEntity log : logList) {
				if(!planMonth.getPlanType().equals(InvestTypeEnum.MonthSalaryInvest.getCode())) {
					stop = "用户停止定投计划，不再进行定时划扣，已转入的本金和收益在到期后自动转出"; 
				} else {
					stop = "用户停止定投计划，不再进行定时划扣，已转入的本金和收益T+2日后可转出到余额，用户手动转出到余额";
				}
			}
			
			return stop;
		} else if (COMMENT_TYPE_COMPLETE.equals(type)) {
			String complete = null;
			for (MsgLogEntity log : logList) {
				complete = "已结清"; 
			}
			return complete;
			
		}
		
		return null;
	}
	
	/**   当划扣成功时，查询出订单中的时间
	 *
	 * 格式如：10月1日
	 */
	public String getRealDeductTime(Timestamp time){
		
		int year = time.getYear()+1900;
		int month = time.getMonth()+1;
		int day = time.getDate();
		return month+"月"+day+"日";
		
	}

	/** 
	 * 查询心愿计划关联的产品列表详情
	 * */
	public PageResp<PlanRelateProductInfo> queryPlanRelateProduct(String planOid,int page,int rows) {
				
		PageResp<PlanRelateProductInfo> pageResp = new PageResp<PlanRelateProductInfo>();
		
		List<PlanProductEntity> list = this.queryRelateProduct(planOid);		
		if(list != null && list.size() > 0){
			//遍历
			for(PlanProductEntity planProduct : list){
				PlanRelateProductInfo relateProduct = new PlanRelateProductInfo();
				Product p = this.productDao.findByOid(planProduct.getProductOid());
				relateProduct.setProductOid(p.getOid());//产品id
				relateProduct.setProductCode(p.getCode());//产品编号
				relateProduct.setProductName(p.getFullName());//产品名称
				relateProduct.setProductType(p.getType().getOid());//产品类型
				relateProduct.setProductTypeDesc(p.getType().getName());//产品类型描述
				relateProduct.setConfirmTime("--");
				
				List<String> investList =  Arrays.asList("invest");
				
				//产品的购买时间
				InvestorTradeOrderEntity investTradeOrder = this.investorTradeOrderService
						.findByWishplanOidAndProductOidAndType(planProduct.getOid(), planProduct.getProductOid(),
								investList);
				if(investTradeOrder != null ){
					relateProduct.setInvestTime(investTradeOrder.getOrderTime());//产品投资时间
					//份额确认时间
					if(investTradeOrder.getCompleteTime()!= null){
						relateProduct.setConfirmTime(DateUtil.getDateTimeFormated(investTradeOrder.getCompleteTime()));
					}				
				}			
				//定期产品成立时间
				Product product = this.productDao.findByOidAndStatus(planProduct.getProductOid());
				
				if (product != null && p.getType().getOid().equals(Product.TYPE_Producttype_01)
						&& product.getSetupDate() != null) {
					relateProduct.setSetupTime(DateUtil.format(p.getSetupDate()));
				} else {
					relateProduct.setSetupTime("--");
				}		
				List<String> redeemList = Arrays.asList("normalRedeem","cash","clearRedeem");
				//产品赎回时间
				InvestorTradeOrderEntity redeemTradeOrder = this.investorTradeOrderService
						.findByWishplanOidAndProductOidAndType(planProduct.getOid(), planProduct.getProductOid(),
								redeemList);
				relateProduct.setRedeemTime("--");//产品的赎回时间
				relateProduct.setRedeemToAccountTime("--");// 产品的赎回到账时间
				String redeemTime = "--";
				if (redeemTradeOrder != null ) {		
					relateProduct.setRedeemTime(DateUtil.getDateTimeFormated(redeemTradeOrder.getOrderTime()));
					redeemTime =  DateUtil.getTimestampFormated(redeemTradeOrder.getOrderTime());
				} 	
				
				if (this.planIsComplete(planOid)
						.equals(redeemTime)) {
					InvestorTradeOrderEntity planRedeemOrder = this.investorTradeOrderService.findBywishplanOid(planOid,
							"wishRedeem");
					if (planRedeemOrder != null) {
						relateProduct
								.setRedeemToAccountTime(DateUtil.getDateTimeFormated(planRedeemOrder.getOrderTime()));
					}
				}else{
						if(redeemTradeOrder != null && redeemTradeOrder.getRedeemToAccountTime() != null){				
							relateProduct.setRedeemToAccountTime(
									DateUtil.getDateTimeFormated(redeemTradeOrder.getRedeemToAccountTime()));// 产品的赎回到账时间
						}					 	
				}
							
				pageResp.getRows().add(relateProduct);
			}
			
			//对产品进行排序---按照创建时间升序、赎回到账时间升序。
			if (pageResp.getRows().size() > 1) {
				Collections.sort(pageResp.getRows(), new Comparator<PlanRelateProductInfo>() {
					@Override
					public int compare(PlanRelateProductInfo o1, PlanRelateProductInfo o2) {
						int flag = 0;
						flag = o1.getRedeemToAccountTime().compareTo(o2.getRedeemToAccountTime());
						if ((o1.getRedeemToAccountTime().equals("--"))
								|| (o2.getRedeemToAccountTime().equals("--"))) {
							if (flag == 0) {							
								flag = o1.getInvestTime().compareTo(o2.getInvestTime());
								return flag;
							}
							return -flag;
						}else{
							if (flag == 0) {							
								flag = o1.getInvestTime().compareTo(o2.getInvestTime());
								return flag;
							}
							return flag;
						}
							
					}
				});
			}
					
			pageResp.setTotal(pageResp.getRows().size());
			//对产品进行分页
			List<PlanRelateProductInfo> newList = new ArrayList<PlanRelateProductInfo>();
			int currIdx = (page > 1 ? (page - 1) * rows : 0);
			for (int j = 0; j < rows && j < pageResp.getRows().size() - currIdx; j++) {
				PlanRelateProductInfo PlanRelateInfo = pageResp.getRows().get(currIdx + j);
				newList.add(PlanRelateInfo);
			}
			pageResp.setRows(newList);
		}
		
		
		return pageResp;
	}

	
	/**
	 * 
	 * 查询计划的结束日期
	 * 
	 * */

	private String planIsComplete(String planOid) {

		PlanInvestEntity planInvest = this.planInvestDao.findByOid(planOid);
		if (planInvest != null && (planInvest.getStatus().equals(PlanStatus.COMPLETE.getCode())
				|| planInvest.getStatus().equals(PlanStatus.REDEEMING.getCode())
				|| planInvest.getStatus().equals(PlanStatus.TOREDEEM.getCode()))) {

			return DateUtil.getTimestampFormated(planInvest.getEndTime());
		}
		PlanMonthEntity planMonth = this.planMonthDao.findByOid(planOid);
		if (planMonth != null && (planMonth.getStatus().equals(PlanStatus.COMPLETE.getCode())
				|| planMonth.getStatus().equals(PlanStatus.REDEEMING.getCode())
				|| planMonth.getStatus().equals(PlanStatus.TOREDEEM.getCode()))) {
			return DateUtil.getTimestampFormated(planMonth.getEndTime());
		}
		return "";
	}

	/**
	 * 后台查询心愿计划的收益明细
	 * 
	 * 
	 * 
	 * */
	public PlanPageResp<PlanIncomeInfo> wishplanIncome(String investorOid, int page, int rows, String planBatch,
			String planName, Timestamp investTimeStart, Timestamp investTimeEnd) {
		PlanPageResp<PlanIncomeInfo> resp = new PlanPageResp<PlanIncomeInfo>();
		
		String sqlBatch = "";
		if(!StringUtil.isEmpty(planBatch)){
			sqlBatch = "%"+planBatch+"%";
		}else{
			sqlBatch = planBatch;
		}
		List<Object[]> list = this.planInvestDao.findPlanByCondition(investorOid, planName, investTimeStart,
				investTimeEnd ,sqlBatch);
		float totalIncome = 0.0f;
		if (list != null && list.size() > 0) {
			// 遍历
			for (Object[] entity : list) {
				PlanIncomeInfo incomeInfo = new PlanIncomeInfo();
				String planOid = (String) entity[0];// 计划id
				incomeInfo.setPlanType((String) entity[1]);// 计划类型
				incomeInfo.setTotalInvestAmount((BigDecimal) entity[2]);// 心愿计划投资金额
				incomeInfo.setInvestTime( (Timestamp) entity[3]);// 计划加入时间
				String status = (String) entity[4];// 计划的状态
				incomeInfo.setPlanBatch((String)entity[5]);//计划的批次
				incomeInfo.setPlanName(this.judgeType(incomeInfo.getPlanType()));//心愿计划名称	
				// 薪增长会每日看到发放收益
				if (incomeInfo.getPlanType().equals(InvestTypeEnum.MonthSalaryInvest.getCode())) {
					// 统计薪增长的每日收益
					List<Object[]> wishplanIncome = this.planProductDao.calcuIncomeByPlanOid(planOid);//心愿计划的id
					if (wishplanIncome != null && wishplanIncome.size() > 0) {
						// 遍历
						for (Object[] arr : wishplanIncome) {
							if((BigDecimal)arr[0] != null && (Date) arr[1]!= null && (Timestamp) arr[2] != null){
								PlanIncomeInfo incomeInfos = new PlanIncomeInfo();		
								incomeInfos.setInvestTime(incomeInfo.getInvestTime());
								incomeInfos.setTotalInvestAmount(incomeInfo.getTotalInvestAmount());
								incomeInfos.setPlanType(incomeInfo.getPlanType());
								incomeInfos.setPlanName(incomeInfo.getPlanName());
								incomeInfos.setPlanBatch(incomeInfo.getPlanBatch());
								incomeInfos.setIncome(((BigDecimal) arr[0]).toString());
								incomeInfos.setConfirmDate((DateUtil.format((Date)arr[1])));
								incomeInfos.setCreateTime(DateUtil.getDateTimeFormated((Timestamp)arr[2]));// 收益发放日
								resp.getRows().add(incomeInfos);
								totalIncome += Float.parseFloat(incomeInfos.getIncome());	
							}										
						}
					}
				} else if ((!(incomeInfo.getPlanType().equals(InvestTypeEnum.MonthSalaryInvest.getCode())))
						&& status.equals(PlanStatus.COMPLETE.getCode())) {
					// 非薪增长的心愿计划在完成后统计其收益
					if (incomeInfo.getPlanType().equals(InvestTypeEnum.OnceEduInvest.getCode())
							|| incomeInfo.getPlanType().equals(InvestTypeEnum.OnceTourInvest.getCode())) {
						// 一次性定投的
						List<Object[]> list2 = this.planProductDao.calcuCompleteTotalIncome(planOid);
						if (list2 != null && list2.size() > 0) {
							for (Object[] arr1 : list2) {
								if((BigDecimal)arr1[0] != null && (Date) arr1[1]!= null && (Timestamp) arr1[2] != null){
									PlanIncomeInfo incomeInfos = new PlanIncomeInfo();		
									incomeInfos.setInvestTime(incomeInfo.getInvestTime());
									incomeInfos.setTotalInvestAmount(incomeInfo.getTotalInvestAmount());
									incomeInfos.setPlanType(incomeInfo.getPlanType());
									incomeInfos.setPlanName(incomeInfo.getPlanName());
									incomeInfos.setPlanBatch(incomeInfo.getPlanBatch());
									incomeInfos.setIncome(((BigDecimal) arr1[0]).toString());
									/*incomeInfos.setConfirmDate(DateUtil.format((Date) arr1[1]));
									incomeInfos.setCreateTime(DateUtil.getDateTimeFormated((Timestamp) arr1[2]));*/
									Timestamp endTime = this.planInvestDao.findPlanEndtime(planOid);
									incomeInfos.setConfirmDate(DateUtil.getTimestampFormated(endTime));
									InvestorTradeOrderEntity tradeOrder = this.investorTradeOrderService
											.findBywishplanOid(planOid, "wishRedeem");
									if(tradeOrder != null && tradeOrder.getOrderAmount() != null){
										incomeInfos.setCreateTime(DateUtil.getDateTimeFormated(tradeOrder.getOrderTime()));
									}
									resp.getRows().add(incomeInfos);
									totalIncome += Float.parseFloat(incomeInfos.getIncome());
								}
								
							}
						} 
					} else if (incomeInfo.getPlanType().equals(InvestTypeEnum.MonthEduInvest.getCode())
							|| incomeInfo.getPlanType().equals(InvestTypeEnum.MonthTourInvest.getCode())) {
						// 教育月定投、旅游月定投
						List<Object[]> monthList = this.planProductDao.calcuCompleteMonthTotalIncome(planOid);
						
						if (monthList != null && list.size() > 0) {
							for (Object[] arr2 : monthList) {
								if((BigDecimal)arr2[0] != null && (Date) arr2[1]!= null ){
									PlanIncomeInfo incomeInfos = new PlanIncomeInfo();		
									incomeInfos.setInvestTime(incomeInfo.getInvestTime());
									incomeInfos.setTotalInvestAmount(incomeInfo.getTotalInvestAmount());
									incomeInfos.setPlanType(incomeInfo.getPlanType());
									incomeInfos.setPlanName(incomeInfo.getPlanName());
									incomeInfos.setPlanBatch(incomeInfo.getPlanBatch());
									incomeInfos.setIncome(((BigDecimal) arr2[0]).toString());						
								/*	incomeInfos.setConfirmDate(DateUtil.format((Date) arr2[1]));								
									incomeInfos.setCreateTime(DateUtil.getDateTimeFormated((Timestamp) arr2[2]));*/	
									Timestamp endTime = this.planMonthDao.findPlanEndtime(planOid);
									incomeInfos.setConfirmDate(DateUtil.getTimestampFormated(endTime));
									InvestorTradeOrderEntity tradeOrder = this.investorTradeOrderService
											.findBywishplanOid(planOid, "wishRedeem");
									if(tradeOrder != null && tradeOrder.getOrderAmount() != null){
										incomeInfos.setCreateTime(DateUtil.getDateTimeFormated(tradeOrder.getOrderTime()));
									}

									resp.getRows().add(incomeInfos);
									totalIncome += Float.parseFloat(incomeInfos.getIncome());
								}
								
							}
						}
					}

				}

			}
			
			resp.setSelectTotalIncome(JJCUtility.keep2Decimal(totalIncome));

			resp.setTotal(resp.getRows().size());

			// 对list集合进行排序
			this.sortList(resp.getRows());
		
			// 对list集合进行分页查询
			List<PlanIncomeInfo> newList = this.pageList(resp.getRows(),page ,rows);
			resp.setRows(newList);	
		}
		
		return resp;
	}

	/** 判断心愿计划类型  */
	public String judgeType(String planType){
		if(planType.equals(InvestTypeEnum.OnceEduInvest.getCode())){
			return "助学成长计划-一次性";
		}
		if(planType.equals(InvestTypeEnum.MonthEduInvest.getCode())){
			return "助学成长计划-月定投";
		}
		if(planType.equals(InvestTypeEnum.OnceTourInvest.getCode())){
			return "家庭旅游计划-一次性";
		}
		if(planType.equals(InvestTypeEnum.MonthTourInvest.getCode())){
			return "家庭旅游计划-月定投";
		}
		if(planType.equals(InvestTypeEnum.MonthSalaryInvest.getCode())){
			return "薪增长计划-月定投";
		}
		return null;
	}
	
	/** 后台对list集合进行排序 */
	public void sortList(List<PlanIncomeInfo> list){
		if (list.size() > 1) {
			Collections.sort(list, new Comparator<PlanIncomeInfo>() {
				@Override
				public int compare(PlanIncomeInfo o1, PlanIncomeInfo o2) {
					int	flag = o1.getConfirmDate().compareTo(o2.getConfirmDate());
						if (flag == 0) {
							flag = o1.getInvestTime().compareTo(o2.getInvestTime());
						}					
					return -flag;
				}
			});
		}
	}
	
	/**  对list集合进行分页查询 */
	public List<PlanIncomeInfo> pageList(List<PlanIncomeInfo> list,int page,int rows){
		List<PlanIncomeInfo> newList = new ArrayList<PlanIncomeInfo>();
		int currIdx = (page > 1 ? (page - 1) * rows : 0);
		for (int j = 0; j < rows && j < list.size() - currIdx; j++) {
			PlanIncomeInfo PlanIncomeInfo = list.get(currIdx + j);
			newList.add(PlanIncomeInfo);
		}
		return newList;
	}
	
	public PlanIncomeInfo copyProperties(PlanIncomeInfo incomeInfo){
		PlanIncomeInfo incomeInfos = new PlanIncomeInfo();		
		incomeInfos.setInvestTime(incomeInfo.getInvestTime());
		incomeInfos.setTotalInvestAmount(incomeInfo.getTotalInvestAmount());
		incomeInfos.setPlanType(incomeInfo.getPlanType());
		incomeInfos.setPlanName(incomeInfo.getPlanName());
		incomeInfos.setPlanBatch(incomeInfo.getPlanBatch());
		incomeInfos.setIncome("--");
		incomeInfos.setConfirmDate("--");
		incomeInfos.setCreateTime("--");// 收益发放日
		return incomeInfos;
	}
	
	/**
	 * 通过planOid返回关联的产品
	 * @param String planOid
	 * return List
	 * 
	 * */
	public List<PlanProductEntity> queryRelateProduct(String planOid) {
		PlanInvestEntity planOnce = this.planInvestDao.findByOid(planOid);
		List<PlanProductEntity> list = null;
		if (planOnce != null) {
			// 计划为一次性购买
			list = this.planProductDao.findRelateProduct(planOid);
		}
		List<PlanInvestEntity> planMonth = this.planInvestDao.findByMonthOid(planOid);
		if (planMonth != null && planMonth.size() > 0) {
			list = new ArrayList<PlanProductEntity>();
			for (PlanInvestEntity pm : planMonth) {
				List<PlanProductEntity> listMonth = this.planProductDao.findRelateProduct(pm.getOid());
				if (planMonth != null && planMonth.size() > 0) {
					for (PlanProductEntity lm : listMonth) {
						list.add(lm);
					}
				}
			}
		}
		return list;
	}
	
	
	/**
	 * complete plans
	 * @param uid
	 * @param totalDp
	 */
	private void completePlans(String uid, DepositProfit totalDp) {

		List<Object[]> planList = planInvestDao.findPlanSByUidByStatusByComplete(uid);

		for (Object[] entity : planList) { // 遍历按月定投数据
			String getOid = (String) entity[0];
			BigDecimal depositAmount = (BigDecimal) entity[1];
			BigDecimal incomeAmount = (BigDecimal) entity[2];

			totalDp.setDepositAmount(totalDp.getDepositAmount() + depositAmount.floatValue());
			totalDp.setProfitAmount(totalDp.getProfitAmount() + incomeAmount.floatValue());
		}

	}
	
	
}
