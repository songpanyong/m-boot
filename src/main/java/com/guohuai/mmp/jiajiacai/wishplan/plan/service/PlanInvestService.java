package com.guohuai.mmp.jiajiacai.wishplan.plan.service;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guohuai.ams.calendar.TradeCalendarTService;
import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.DateUtil;
import com.guohuai.component.web.view.RowsRep;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountDao;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountService;
import com.guohuai.mmp.investor.tradeorder.InvestorInvestTradeOrderExtService;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderDao;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderEntity;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderService;
import com.guohuai.mmp.investor.tradeorder.TradeOrderRep;
import com.guohuai.mmp.jiajiacai.caculate.InvestProfitCaculate;
import com.guohuai.mmp.jiajiacai.caculate.JJCUtility;
import com.guohuai.mmp.jiajiacai.caculate.StringUtils;
import com.guohuai.mmp.jiajiacai.common.constant.InvestTypeEnum;
import com.guohuai.mmp.jiajiacai.common.constant.PlanStatus;
import com.guohuai.mmp.jiajiacai.common.exception.BaseException;
import com.guohuai.mmp.jiajiacai.common.exception.ErrorMessage;
import com.guohuai.mmp.jiajiacai.wishplan.plan.dao.PlanInvestDao;
import com.guohuai.mmp.jiajiacai.wishplan.plan.dao.PlanProductDao;
import com.guohuai.mmp.jiajiacai.wishplan.plan.entity.PlanInvestEntity;
import com.guohuai.mmp.jiajiacai.wishplan.plan.entity.PlanMonthEntity;
import com.guohuai.mmp.jiajiacai.wishplan.plan.entity.PlanProductEntity;
import com.guohuai.mmp.jiajiacai.wishplan.plan.form.EducationInvestReq;
import com.guohuai.mmp.jiajiacai.wishplan.plan.form.PlanProductForm;
import com.guohuai.mmp.jiajiacai.wishplan.plan.form.TradeOrderForm;
import com.guohuai.mmp.jiajiacai.wishplan.plan.rep.PlanByOidRep;
import com.guohuai.mmp.jiajiacai.wishplan.plan.rep.PlanListRep;
import com.guohuai.mmp.jiajiacai.wishplan.planlist.PlanListDao;
import com.guohuai.mmp.jiajiacai.wishplan.planlist.PlanListEntity;
import com.guohuai.mmp.jiajiacai.wishplan.planlist.PlanListService;
import com.guohuai.mmp.jiajiacai.wishplan.product.JJCProductService;
import com.guohuai.mmp.jiajiacai.wishplan.product.form.JJCProductRate;
import com.guohuai.mmp.jiajiacai.wishplan.question.InvestMessageForm;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PlanInvestService {

	@Autowired
	private PlanInvestDao planInvestDao;

	@Autowired
	private PlanProductService planService;

	@Autowired
	private PlanListService planListService;

	@Autowired
	private PlanMonthService planMonthService;

	@Autowired
	InvestorInvestTradeOrderExtService investorInvestTradeOrderExtService;

	@Autowired
	private JJCProductService productService;

	@Autowired
	private PlanTradeOrderService tradeOrderService;

	@Autowired
	private PlanProductDao planDao;

	@Autowired
	private InvestorBaseAccountService investorBaseAccountService;

	@Autowired
	private InvestorBaseAccountDao investorBaseAccountDao;

	@Autowired
	private TradeCalendarTService tradeCalendarService;

	private int nestCount;

	@Autowired
	InvestorTradeOrderDao tradeOrderDao;

	@Autowired
	private PlanListDao planListDao;
	@Autowired
	private InvestorTradeOrderService investorTradeOrderService;

	@Transactional
	public PlanInvestEntity planInsert(PlanProductForm form, String planType, String monthOid) {
		PlanInvestEntity entity = new PlanInvestEntity();
		entity.setOid(StringUtils.uuid());
		entity.setUid(form.getUid());

		entity.setMonthOid(monthOid);
		// Create time
		entity.setCreateTime(DateUtil.getSqlCurrentDate());

		BigDecimal rate = new BigDecimal(form.getExpectedRate());
		rate = rate.setScale(4, BigDecimal.ROUND_HALF_UP);
		entity.setExpectedRate(rate);
		entity.setDepositAmount(form.getMoneyVolume());

		// The tour info

		// entity.setInvestDuration(form.getInvestDuration() / 30);
		entity.setInvestDuration(form.getInvestDuration());

		entity.setStatus(PlanStatus.READY.getCode());

		entity.setPlanType(planType);

		entity.setCid(form.getCid());
		entity.setCkey(form.getCkey());

		entity.setMonthOid(form.getMonthOid());

		entity.setInvestDuration(form.getInvestDuration());
		if (!InvestTypeEnum.MonthSalaryInvest.getCode().equals(form.getPlanType()) && form.getInvestDuration() > 0) {
			entity.setEndTime(
					new Timestamp(DateUtil.addDays(DateUtil.getCurrDate(), form.getInvestDuration()).getTime()));
			InvestMessageForm pfrofitForm = planService.caculateInvestProfitByFixed(form.getInvestDuration(),
					entity.getDepositAmount().intValue(), entity.getExpectedRate().floatValue());
			entity.setExpectedAmount(new BigDecimal(pfrofitForm.getProfit()));
		}
		entity.setPlanTarget(form.getPlanTarget());
		
		/** 增加心愿批次的字段 */
		String planName = this.planListDao.findPlanNameByType(planType);
		entity.setPlanBatch(planName+""+DateUtil.currentTime());
		
		PlanInvestEntity result = planInvestDao.save(entity);
//		PlanInvestEntity result = planInvestDao.saveAndFlush(entity);
		return result;
	}

	public List<PlanInvestEntity> queryByUid(String uid) {
		List<PlanInvestEntity> list = planInvestDao.findByUidOrderByCreateTimeDesc(uid);
		return list;
	}

	public List<PlanInvestEntity> queryOncePlanByUid(String uid) {
		List<PlanInvestEntity> list = planInvestDao.findOncePlanByUidOrderByCreateTimeDesc(uid);
		return list;
	}

	/**
	 * 根据心愿计划状态获取计划列表
	 * 
	 * @param planStatus
	 * @param page
	 * @param rows
	 * @param uid
	 * @return
	 */
	public RowsRep<PlanListRep> getOwnerPlanList(String planStatus, int page, int rows, String uid) {
		// 根据接口传的计划状态调整sql查询条件
		List<String> sqlWhere = Arrays.asList("STOP", "REDEEMING", "COMPLETE", "READY", "SUCCESS", "DEPOSITED",
				"TODEPOSIT");
		if (planStatus.equals("STOP")) {
			sqlWhere = Arrays.asList("STOP", "REDEEMING");
		} else if (planStatus.equals("COMPLETE")) {
			sqlWhere = Arrays.asList("COMPLETE");
		} else if (planStatus.equals("READY")) {
			sqlWhere = Arrays.asList("READY", "SUCCESS", "DEPOSITED", "TODEPOSIT");
		}
		// 根据页数和每页条数判断偏移量
		Integer offset = (page - 1) * rows;

		List<Object[]> planList = planInvestDao.findPlanSByUidByStatusOrderByCreateTimeDesc(uid, sqlWhere, rows,
				offset); // 获取一次性购买数据
		RowsRep<PlanListRep> res = new RowsRep<PlanListRep>();

		for (Object[] entity : planList) { // 遍历按月定投数据
			String getOid = entity[0].toString();
			String getPlanType = entity[1].toString();
			BigDecimal getDepositAmount = (BigDecimal) entity[2];
			String getStatus = entity[3].toString();
			Timestamp getCreateTime = (Timestamp) entity[4];
			// String getEndTime = entity[5]== null ? "" : entity[5].toString();
			String getPlanTarget = entity[6] == null ? "" : entity[6].toString();
			// 根据计划类别查类别表
			PlanListEntity listEntity = planListService.findByPlanType(getPlanType);
			// 根据数据库查询的计划状态匹配前台显示的状态
			String pStatus = null;

			if (getStatus.equals("STOP") || getStatus.equals("REDEEMING")) {
				pStatus = "已停止";
			} else if (getStatus.equals("READY") || getStatus.equals("SUCCESS") || getStatus.equals("DEPOSITED")
					|| getStatus.equals("TODEPOSIT")) {
				pStatus = "进行中";
			} else if (getStatus.equals("COMPLETE")) {
				pStatus = "已结清";
			}
			String investType = getPlanType.indexOf("ONCE_") != -1 ? "一次性购买" : "按月定投";

			PlanListRep rep = PlanListRep.builder().planOid(getOid).planName(listEntity.getName())
					.planTarget(getPlanTarget).investType(investType).totalAmount(getDepositAmount)
					.addTime(getCreateTime).planStatus(pStatus).build();
			res.add(rep);
		}
		return res;
	}

	public PlanInvestEntity queryByOid(String oid) {
		return planInvestDao.findByOid(oid);
	}

	@Transactional
	public int updateStatus(String oid, String status) {
		return planInvestDao.updateStatus(oid, status);
	}

	@Transactional
	public int completeInvest(String oid, String status) {
		return planInvestDao.complete(oid, status);
	}

	public TradeOrderRep nestBuyProduct(PlanProductForm tradeOrderReq, BigDecimal productValume, PlanInvestEntity pme) {
		if (nestCount++ > 3) {
			return null;
		}
		// Only one buy
		if (productValume.compareTo(tradeOrderReq.getMoneyVolume()) > 0) {
			return buyOne(tradeOrderReq);
			// Can buy part
		} else if (productValume.intValue() > 0) {
			tradeOrderReq.setMoneyVolume(productValume);
			TradeOrderRep rep = buyOne(tradeOrderReq);
			if (rep.getErrorCode() == 0) {
				pme.setBalance(pme.getBalance().subtract(tradeOrderReq.getMoneyVolume()));
				if (pme.getBalance().compareTo(BigDecimal.ONE) < 0) {
					planInvestDao.updateStatus(pme.getOid(), PlanStatus.SUCCESS.getCode());
					return rep;
				}
				tradeOrderReq.setMoneyVolume(JJCUtility.bigKeep2Decimal(pme.getBalance()));
			} 

			List<String> exculuds = Arrays.asList(tradeOrderReq.getProductOid());
			JJCProductRate productRate = getMaxProfitProduct(pme.getEndTime(), exculuds, pme.getUid());
			log.info("部分购买  planOid={}, old-productId={}, new-productId={}", pme.getOid(),
					tradeOrderReq.getProductOid(), productRate.getOid());
			resetOrderReq(tradeOrderReq, productRate);
			return nestBuyProduct(tradeOrderReq, productRate.getProductMoneyValume(), pme);

		} else {
			List<String> exculuds = Arrays.asList("");
			JJCProductRate productRate = getMaxProfitProduct(pme.getEndTime(), exculuds, pme.getUid());
			resetOrderReq(tradeOrderReq, productRate);
			return nestBuyProduct(tradeOrderReq, productRate.getProductMoneyValume(), pme);
		}
	}

	public JJCProductRate getMaxProfitProduct(Timestamp time, List<String> exculuds, String uid) {
		// Get the max profit from product.
		int duration = 0;
		boolean onlyOpen = true;
		if (time != null) {
			duration = DateUtil.getTimeRemainDays(time);
			onlyOpen = false;
		}
		JJCProductRate productRate = productService.getMaxRateByDuration(duration, onlyOpen, exculuds, uid);
		return productRate;
	}

	@Transactional
	public void subtractBalance(BigDecimal balance, String oid) {
		planInvestDao.subtractBalance(balance, oid);
		updateStatusByBalance(oid);
	}

	/**
	 * addBalance, added income to plan-product added balance to plan-invest
	 * 
	 * @param income
	 * @param oid
	 * @return
	 */
	@Transactional
	public int addIncome(BigDecimal income, String oid) {
		int result = planDao.addIncome(income, oid);
		String planOid = planDao.queryInvestOid(oid);
		result += planInvestDao.addBalance(income, planOid);
		return result;
	}

	// Add income and complete.

	public int incomeAndComplete(BigDecimal income, String oid) {
		String planOid = planDao.queryInvestOid(oid);
		String status = PlanStatus.DEPOSITED.getCode();
		try {
			status = needReinvest(planOid) ? PlanStatus.DEPOSITED.getCode() : PlanStatus.REDEEMING.getCode();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return incomeAndCompletedb(oid, planOid, income, status);
	}

	@Transactional
	public int incomeAndCompletedb(String oid, String investOid, BigDecimal income, String investStatus) {
		log.info("计划回款  investOid={}, income={}, investStatus={}", investOid, income, investStatus);
		int result = planDao.addIncomeComplete(income, oid);
		result += planInvestDao.updateBalanceStatus(income, investStatus, investOid);
		log.info("计划回款成功");
		return result;
	}

	public boolean needReinvest(String investOid) {

		String status = planInvestDao.findPlanStatus(investOid);
		if (status.equals(PlanStatus.REDEEMING.getCode())) {
			return false;
		}
		Timestamp ts = planInvestDao.findPlanEndtime(investOid);
		// Salary
		if (ts == null || DateUtil.getTimeRemainDays(ts) > 10) {
			return true;
		}
		Date dt = new Date(ts.getTime());
		Date lastdt = tradeCalendarService.lastTrade(dt, 0);
		Date nextdt = tradeCalendarService.nextTrade(new Date(DateUtil.getCurrDate().getTime()), 1);
		if (lastdt.after(nextdt)) {
			return true;
		} else {
			return false;
		}
	}

	private TradeOrderRep buyOne(PlanProductForm tradeOrderReq) {
		PlanProductEntity planEntity = planService.planInvest(tradeOrderReq);

		tradeOrderReq.setPlanRedeemOid(planEntity.getOid());
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
			planService.updateStatus(planEntity.getOid(), PlanStatus.SUCCESS.getCode(), rep.getTradeOrderOid());
			subtractBalance(tradeOrderReq.getMoneyVolume(), planEntity.getPlanOid());
			productService.update4InvestLockVolume(planEntity.getProductOid(), tradeOrderReq.getMoneyVolume());
			log.info("计划购买成功 uid={}, planId={}, money={}, endTime={}", tradeOrderReq.getUid(), planEntity.getOid(),
					tradeOrderReq.getMoneyVolume(), DateUtil.getSqlCurrentDate());
		} else {
			planService.updateStatus(planEntity.getOid(), PlanStatus.FAILURE.getCode(), rep.getTradeOrderOid());
			log.info("计划购买失败 uid={}, planId={}, money={}, endTime={}", tradeOrderReq.getUid(), planEntity.getOid(),
					tradeOrderReq.getMoneyVolume(), DateUtil.getSqlCurrentDate());
			String error = rep.getErrorMessage().substring(rep.getErrorMessage().length() - 6);
			if (error.equals("(9007)")) {
				// TODO:
				planInvestDao.updateStatus(planEntity.getPlanOid(), PlanStatus.SUCCESS.getCode());
				// planInvestDao.updateBalanceStatus(new
				// BigDecimal(-tradeOrderReq.getMoneyVolume().intValue()),
				// PlanStatus.SUCCESS.getCode(), planEntity.getPlanOid());
				// planInvestDao.updateBalanceStatus(new
				// BigDecimal(-tradeOrderReq.getMoneyVolume().intValue()),
				// PlanStatus.SUCCESS.getCode(), planEntity.getPlanOid());
			}
		}
		return rep;
	}

	private PlanProductForm resetOrderReq(PlanProductForm tradeOrderReq, JJCProductRate productRate) {
		tradeOrderReq.setProductOid(productRate.getOid());
		tradeOrderReq.setExpectedRate((float) productRate.getRate());
		int expectedValume = (int) InvestProfitCaculate.caculateInvestProfitByFixed(tradeOrderReq.getInvestDuration(),
				tradeOrderReq.getMoneyVolume().floatValue(), tradeOrderReq.getExpectedRate());
		tradeOrderReq.setExpectedAmount(new BigDecimal(expectedValume));
		return tradeOrderReq;
	}

	public List<PlanInvestEntity> queryOverdueCurrentProduct() {
		List<PlanInvestEntity> list = planInvestDao.queryOverdueCurrentProduct();
		return list;
	}

	public List<PlanInvestEntity> queryByStatus(String status) {
		List<PlanInvestEntity> list = planInvestDao.findByStatus(status);
		return list;
	}

	@Transactional
	public int setBalanceAndStatus(PlanInvestEntity entity) {

		entity.setBalance(entity.getDepositAmount());
		entity.setStatus(PlanStatus.DEPOSITED.getCode());
		//Record the deposit time
		entity.setDepositTime(DateUtil.getSqlCurrentDate());
		planInvestDao.save(entity);

		return 0;
	}

	public String depositSucess(PlanInvestEntity entity) {
		try {
			setBalanceAndStatus(entity);
			updateBalance(entity.getUid());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.info("计划转账记录余额失败 uid={}, planId={}, money={}, endTime={}", entity.getUid(), entity.getOid(),
					entity.getBalance(), DateUtil.getSqlCurrentDate());
			e.printStackTrace();
			return null;
		}

		InvestorTradeOrderEntity tradeOrder = null;
		try {
			TradeOrderForm form = new TradeOrderForm();

			form.setMoneyVolume(entity.getBalance());
			form.setPlanRedeemOid(entity.getOid());
			form.setUid(entity.getUid());

			tradeOrder = tradeOrderService.recordTradeOrder(form, true);
			tradeOrderService.payCallback(tradeOrder, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.info("计划转账记录交易失败 uid={}, planId={}, money={}, endTime={}", entity.getUid(), entity.getOid(),
					entity.getBalance(), DateUtil.getSqlCurrentDate());
			e.printStackTrace();
			return null;
		}

		if (null != tradeOrder) {
			return tradeOrder.getOid();
		}
		return null;

	}

	@Transactional
	public void updateStatusByBalance(String investOid) {
		BigDecimal balance = planInvestDao.queryBalanceByOid(investOid);
		if (balance.compareTo(BigDecimal.ONE) < 0) {
			planInvestDao.updateStatus(investOid, PlanStatus.SUCCESS.getCode());
		}
	}

	public PlanProductForm generateOrderReq(EducationInvestReq eduReq, JJCProductRate productRate)
			throws BaseException {
		// JJCProductRate productRate =
		// productService.getRateByDuration(eduReq.getDuration(), false);
		if (productRate.getOid() == null) {
			throw new BaseException(ErrorMessage.PRODUCT_NOT_EXIST);
		}
		InvestMessageForm form = planService.caculateInvestProfitByFixed(DateUtil.diffDays4Months(eduReq.getDuration()),
				eduReq.getMoneyVolume().intValue(), productRate.getRate());

		PlanProductForm tradeOrderReq = new PlanProductForm();
		tradeOrderReq.setExpectedAmount(new BigDecimal(form.getProfit()));
		tradeOrderReq.setExpectedRate((float) productRate.getRate());
		// 产品ID
		tradeOrderReq.setProductOid(productRate.getOid());
		// 申购金额
		tradeOrderReq.setMoneyVolume(eduReq.getMoneyVolume());

		// 计划id
		tradeOrderReq.setPlanListOid(eduReq.getPlanListOid());
		tradeOrderReq.setCid(eduReq.getCid());
		tradeOrderReq.setCkey(eduReq.getCkey());
		tradeOrderReq.setInvestDuration(DateUtil.diffDays4Months(eduReq.getDuration()));

		tradeOrderReq.setPlanTarget(eduReq.getPlanTarget());
		return tradeOrderReq;
	}

	public PlanProductForm generateOrderReq(PlanMonthEntity pme, InvestMessageForm investForm) {
		// JJCProductRate productRate =
		// productService.getRateByDuration(eduReq.getDuration(), false);
		// if (investForm.getProductOid() == null) {
		// throw new BaseException(ErrorMessage.PRODUCT_NOT_EXIST);
		// }

		PlanProductForm tradeOrderReq = new PlanProductForm();
		if (!pme.getPlanType().equals(InvestTypeEnum.MonthSalaryInvest.getCode())) {
			tradeOrderReq.setExpectedAmount(new BigDecimal(investForm.getProfit()));
		}
		tradeOrderReq.setExpectedRate((float) investForm.getRate());
		// 产品ID
		tradeOrderReq.setProductOid(investForm.getProductOid());
		// 申购金额
		tradeOrderReq.setMoneyVolume(pme.getMonthAmount());

		// 计划id
		// tradeOrderReq.setPlanListOid(eduReq.getPlanListOid());
		tradeOrderReq.setPlanType(pme.getPlanType());
		tradeOrderReq.setCid(pme.getCid());
		tradeOrderReq.setCkey(pme.getCkey());
		tradeOrderReq.setInvestDuration(investForm.getDuration());
		tradeOrderReq.setPlanTarget(pme.getPlanTarget());
		return tradeOrderReq;
	}

	@Transactional
	public int addWithholdCount(String oid) {
		return planInvestDao.addWithholdCount(oid);

	}

	public List<PlanInvestEntity> findByMonthOidAndStatusList(String id, List<String> statusList) {
		List<PlanInvestEntity> list = planInvestDao.findByMonthOidAndStatusList(id, statusList);
		return list;
	}

	public List<PlanInvestEntity> queryByPlanMonthOidAndStatus(String id, String status) {
		List<PlanInvestEntity> list = planInvestDao.findByMonthOidAndStatus(id, status);
		return list;
	}

	public List<PlanInvestEntity> queryOverdue() {
		// Date nextdt = tradeCalendarService.nextTrade(new
		// Date(DateUtil.getCurrDate().getTime()), 1);
		// List<PlanInvestEntity> list = planInvestDao.findOverdueNettrade(new
		// Timestamp(nextdt.getTime()));
		List<PlanInvestEntity> list = planInvestDao.findOverdueNow();
		return list;
	}

	public List<PlanInvestEntity> queryByPlanMonthOid(String id) {
		List<PlanInvestEntity> list = planInvestDao.findByMonthOid(id);
		return list;
	}

	public List<PlanInvestEntity> querySuccessPlanByUidType(String uid, String type, List<String> statusList) {
		List<PlanInvestEntity> list = planInvestDao.findSuccessByUidTypeDesc(uid, type, statusList);
		return list;
	}

	public List<PlanInvestEntity> querySuccessPlanByMonth(String monthOid, List<String> statusList) {
		List<PlanInvestEntity> list = planInvestDao.findSuccessByMonthDesc(monthOid, statusList);
		return list;
	}

	public boolean checkTransferBalacen(String id) {
		PlanInvestEntity latestPlan = planInvestDao.findLatestDeposit(id);
//		boolean result = true;
		if (latestPlan != null) {
			List<PlanProductEntity> ppelist = planDao.findByPlanOidAndStatus(latestPlan.getOid(),
					PlanStatus.SUCCESS.getCode());

			if (ppelist.size() == 0) {
				return false;
			}

			for (PlanProductEntity ppe : ppelist) {

				int count = tradeOrderDao.findHoldincomeCountByPlanOid(ppe.getOid());
				if (count == 0) {
					return false;
				}
				/*
				Date date = tradeOrderDao.findBeginRedeemDateByOid(ppe.getOrderOid());
				// Check the redeem time
				if (!DateUtil.compare_current(date)) {
					return false;
				}
				*/
			}
		}
		return true;
	}

	private String convertStatus(PlanInvestEntity ie) {
		if (ie.getStatus().equals(PlanStatus.COMPLETE.getCode())
				|| ie.getStatus().equals(PlanStatus.DEPOSITED.getCode())
				|| ie.getStatus().equals(PlanStatus.SUCCESS.getCode())
				|| ie.getStatus().equals(PlanStatus.REDEEMING.getCode())) {
			return "done";
		} else if (ie.getStatus().equals(PlanStatus.FAILURE.getCode())) {
			return "fail";
		} else if (ie.getStatus().equals(PlanStatus.READY.getCode())
				|| ie.getStatus().equals(PlanStatus.TODEPOSIT.getCode())) {
			return "wait";
		} else {
			return ie.getStatus();
		}
	}

	/**
	 * wait：待转 done：已转 fail:失败 stop:终止 none:当前月份没有投资
	 * 
	 * @param entity
	 * @return
	 */
	public String getStatusFromList(List<PlanInvestEntity> list, PlanMonthEntity entity, int id) {
		if (entity.getPlanType().equals(InvestTypeEnum.MonthSalaryInvest.getCode())) {
			if (id < list.size()) {
				return convertStatus(list.get(id));
			} else if (entity.getStatus().contentEquals(PlanStatus.READY.getCode())) {
				return "wait";
			} else if (entity.getStatus().contentEquals(PlanStatus.STOP.getCode())) {
				return "stop";
			} else {
				return "none";
			}
		} else {
			if (id < list.size()) {
				return convertStatus(list.get(id));
			} else if (id < entity.getPlanMonthCount()) {
				if (entity.getStatus().contentEquals(PlanStatus.READY.getCode())) {
					return "wait";
				} else if (entity.getStatus().contentEquals(PlanStatus.STOP.getCode())) {
					return "stop";
				} else {
					return "none";
				}
			} else {
				return "none";
			}
		}
	}

	private void updateBalance(String oid) {
		investorBaseAccountService.updateBalance(investorBaseAccountDao.findByOid(oid));
	}

	public void nestCounter() {
		nestCount = 0;
	}

	public float caculuateOneMonthIncome(PlanInvestEntity currentMd) {
		// Salary
		float balance = 0;
		List<String> listStatuses = Arrays.asList("SUCCESS", "COMPLETE", "TOREDEEM", "REDEEMING");
		List<PlanProductEntity> successList = planDao.queryPlanProductByInvestAndStatusList(currentMd.getOid(),
				listStatuses);

		for (PlanProductEntity p : successList) {

			if (p.getStatus().equals(PlanStatus.COMPLETE.getCode())) {
				balance += p.getIncome().floatValue();
			} else {
				balance += p.getAmount().floatValue();
				if (p.getIncome() != null) {
					balance += p.getIncome().floatValue();
				}
				if (p.getIncomeVolume() != null) {
					balance += p.getIncomeVolume().floatValue();
				}

			}
		}
		if (successList.size() == 0) {
			balance = currentMd.getDepositAmount().floatValue();
		}
		return balance;
	}

	/**
	 * 一次性购买心愿计划详情
	 * 
	 * @param oid
	 * @return
	 */
	public PlanByOidRep getPlanByOid(String oid) {
		// 根据oid查询心愿计划类别
		PlanByOidRep rep = new PlanByOidRep();
		PlanInvestEntity entity = queryByOid(oid);
		if (entity == null) {
			throw new AMPException(ErrorMessage.MYPLAN_NOT_EXIST);
		}
		PlanListEntity listEntity = planListService.findByPlanType(entity.getPlanType());
		String pStatus = null;
		if (entity.getStatus().equals("STOP") || entity.getStatus().equals("REDEEMING")) {
			pStatus = "已停止";
		} else if (entity.getStatus().equals("READY") || entity.getStatus().equals("SUCCESS") || entity.getStatus().equals("DEPOSITED")
				|| entity.getStatus().equals("TODEPOSIT")) {
			pStatus = "进行中";
		} else if (entity.getStatus().equals("COMPLETE")) {
			pStatus = "已结清";
		}
		String investType = entity.getPlanType().indexOf("ONCE_") != -1 ? "一次性购买" : "按月定投";
		
		InvestorTradeOrderEntity tradeOrder =  this.investorTradeOrderService.findBywishplanOid(entity.getOid(), "wishInvest");
		if(tradeOrder != null){
			rep.setOrderCode(tradeOrder.getOrderCode());
		}
		rep.setPlanOid(entity.getOid());
		rep.setPlanName(listEntity.getName());
		rep.setPlanTarget(entity.getPlanTarget());
		rep.setInvestType(investType);
		rep.setTotalAmount(entity.getDepositAmount());
		if (entity.getStatus().equals(PlanStatus.COMPLETE.getCode())) {
			rep.setPrincipalAndInterest(JJCUtility.keep2Decimal(entity.getBalance().floatValue()));
		} else {
			rep.setPrincipalAndInterest(JJCUtility.keep2Decimal(entity.getExpectedAmount().floatValue()));
		}
		rep.setInvestDuration(DateUtil.getMonthSpace(entity.getEndTime(), entity.getCreateTime()));
		rep.setCompletedDays(DateUtil.daysBetween(DateUtil.getSqlDate(),entity.getCreateTime()));
		rep.setTotalDays(DateUtil.daysBetween(entity.getEndTime(),entity.getCreateTime()));
		rep.setAddTime(entity.getCreateTime());
		rep.setFinishTime(entity.getEndTime());
		rep.setPlanStatus(pStatus);

		return rep;
	}
	
	/**
	 * checkDeposited
	 * @param wishplanOid
	 * @return
	 */
	public boolean checkDeposited(String wishplanOid) {
		int count = tradeOrderDao.findTradeOrderByWishplanAndType(wishplanOid, InvestorTradeOrderEntity.TRADEORDER_orderType_wishInvest);
		return count > 0? true : false;
	}
}
