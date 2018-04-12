package com.guohuai.mmp.jiajiacai.wishplan.plan.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guohuai.account.api.request.RedeemToBasicRequest;
import com.guohuai.component.util.DateUtil;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountDao;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountService;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderEntity;
import com.guohuai.mmp.jiajiacai.common.constant.InvestTypeEnum;
import com.guohuai.mmp.jiajiacai.common.constant.PlanStatus;
import com.guohuai.mmp.jiajiacai.common.service.ThirdNotifyService;
import com.guohuai.mmp.jiajiacai.wishplan.plan.dao.PlanInvestDao;
import com.guohuai.mmp.jiajiacai.wishplan.plan.dao.PlanMonthDao;
import com.guohuai.mmp.jiajiacai.wishplan.plan.entity.PlanInvestEntity;
import com.guohuai.mmp.jiajiacai.wishplan.plan.entity.PlanMonthEntity;
import com.guohuai.mmp.jiajiacai.wishplan.plan.form.TradeOrderForm;
import com.guohuai.mmp.platform.accment.Accment;
import com.guohuai.mmp.platform.msgment.MsgParam;
import com.guohuai.mmp.publisher.investor.holdincome.InvestorIncomeDao;
import com.guohuai.mmp.publisher.investor.holdincome.InvestorIncomeEntity;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class IncomeBalanceService {

	@Autowired
	private PlanInvestDao planInvestDao;

	@Autowired
	private Accment accmentService;

	@Autowired
	private InvestorBaseAccountService investorBaseAccountService;

	@Autowired
	private InvestorBaseAccountDao investorBaseAccountDao;

	@Autowired
	private PlanProductService planProductService;

	@Autowired
	private PlanInvestService planInvestService;

	@Autowired
	private PlanMonthDao planMonthDao;

	@Autowired
	private PlanTradeOrderService tradeOrderService;

	@Autowired
	private InvestorIncomeDao investorIncomeDao;

	@Autowired
	private ThirdNotifyService thirdNotifyService;

	public void redeem2basic() {

		List<PlanMonthEntity> listMonth = planMonthDao.findByStatusLimit(PlanStatus.REDEEMING.getCode(), 10);
		for (PlanMonthEntity pme : listMonth) {
			processMonthRedeem(pme);
		}

		List<PlanInvestEntity> listInvest = planInvestDao.findOncePlanByStatus(PlanStatus.REDEEMING.getCode(), 10);
		for (PlanInvestEntity pie : listInvest) {
			processInvestRedeem(pie);
		}
	}

	private void processInvestRedeem(PlanInvestEntity pie) {
		List<String> listStatuses = Arrays.asList(PlanStatus.SUCCESS.getCode(), PlanStatus.TOREDEEM.getCode(),
				PlanStatus.REDEEMING.getCode());
		/*
		 * List<PlanProductEntity> planEntityList =
		 * planProductService.queryPlanProductByInvestAndStatusList(pie.getOid(),
		 * listStatuses);
		 */
		int planEntityCount = planProductService.countPlanProductByInvestAndStatusList(pie.getOid(), listStatuses);
		if (planEntityCount == 0) {
			processRedeemBalance(pie);
		}
	}

	private void processRedeemBalance(PlanInvestEntity pmd) {
		RedeemToBasicRequest req = new RedeemToBasicRequest();
		req.setBalance(pmd.getBalance());
		req.setUserOid(pmd.getUid());
		int result = accmentService.redeem2basic(req);
		if (result == 0) {
			planInvestDao.updateStatus(pmd.getOid(), PlanStatus.COMPLETE.getCode());
			tradeorder(req, pmd.getOid());
			// Notify
			thirdNotifyService.callMsgMail(MsgParam.msgwishplanreceivedpayments, pmd, pmd.getUid());

			try {
				staticticsOnceIncome(pmd);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			try {
				updateBalance(req.getUserOid());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// processInvestorBalance(pmd);

		}
	}

	/*
	 * @Transactional private void processInvestorBalance(PlanInvestEntity pre) {
	 * InvestorBaseAccountEntity investor =
	 * investorBaseAccountDao.findByOid(pre.getUid());
	 * investor.setBalance(investor.getBalance().add(pre.getBalance()));
	 * investorBaseAccountDao.save(investor);
	 * 
	 * pre.setBalance(BigDecimal.ZERO);
	 * pre.setStatus(PlanStatus.COMPLETE.getCode()); planInvestDao.save(pre); }
	 */
	private void updateBalance(String oid) {
		investorBaseAccountService.updateBalance(investorBaseAccountDao.findByOid(oid));
	}

	public void processMonthRedeem(PlanMonthEntity pme) {
		List<PlanInvestEntity> depositList = planInvestService.queryByPlanMonthOidAndStatus(pme.getOid(),
				PlanStatus.REDEEMING.getCode());
		boolean complete = true;
		for (PlanInvestEntity monthDep : depositList) {
			List<String> listStatuses = Arrays.asList(PlanStatus.SUCCESS.getCode(), PlanStatus.TOREDEEM.getCode(),
					PlanStatus.REDEEMING.getCode());
			/*
			 * List<PlanProductEntity> planEntityList = planProductService
			 * .queryPlanProductByInvestAndStatusList(monthDep.getOid(), listStatuses);
			 */
			int planEntityCount = planProductService.countPlanProductByInvestAndStatusList(monthDep.getOid(),
					listStatuses);
			if (planEntityCount == 0) {
				planMonthDao.addIncome(monthDep.getBalance(), pme.getOid());
				planInvestService.updateStatus(monthDep.getOid(), PlanStatus.COMPLETE.getCode());
			} else {
				complete = false;
			}
		}

		if (complete) {
			processMonthRedeemBalance(pme.getOid());
		}
	}

	private void processMonthRedeemBalance(String planMonthOid) {

		PlanMonthEntity pme = planMonthDao.findByOid(planMonthOid);

		RedeemToBasicRequest req = new RedeemToBasicRequest();
		req.setBalance(pme.getIncome());
		req.setUserOid(pme.getUid());
		int result = accmentService.redeem2basic(req);
		if (result == 0) {
			log.info("心愿中途户转入到余额开始 uid={}, oid={}, monthmoney={}", pme.getOid(), pme.getOid(), pme.getIncome());
			planMonthDao.updateStatus(pme.getOid(), PlanStatus.COMPLETE.getCode());
			tradeorder(req, pme.getOid());
			// Notify
			thirdNotifyService.callMsgMail(MsgParam.msgmonthplanreceivedpayments, pme, pme.getUid());
			// TODO:
			if (!pme.getPlanType().equals(InvestTypeEnum.MonthSalaryInvest.getCode())) {
				try {
					staticticsMonthIncome(pme);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			try {
				updateBalance(req.getUserOid());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			log.info("心愿中途户转入到余额结束 uid={}, oid={}, monthmoney={}", pme.getOid(), pme.getOid(), pme.getIncome());
		}
	}

	private void tradeorder(RedeemToBasicRequest req, String oid) {

		TradeOrderForm form = new TradeOrderForm();

		form.setMoneyVolume(req.getBalance());
		form.setPlanRedeemOid(oid);
		form.setUid(req.getUserOid());

		InvestorTradeOrderEntity tradeOrder = tradeOrderService.recordTradeOrder(form, false);

	}

	@Transactional
	public void staticticsOnceIncome(PlanInvestEntity pmd) {
		// 创建<<发行人-投资人-合仓收益明细>>
		InvestorIncomeEntity investorIncomeEntity = new InvestorIncomeEntity();
		// investorIncomeEntity.setPublisherHold(hold); //所属持有人手册
		// investorIncomeEntity.setIncomeAllocate(incomeAllocate);
		// investorIncomeEntity.setProduct(product); //所属产品
		// investorIncomeEntity.setInvestorBaseAccount(hold.getInvestorBaseAccount());
		// //所属投资人
		// investorIncomeEntity.setIncomeAmount(holdIncomeAmount.add(holdLockIncomeAmount));
		// investorIncomeEntity.setBaseAmount(holdBaseAmount);
		// investorIncomeEntity.setRewardAmount(holdRewardAmount);
		// investorIncomeEntity.setAccureVolume(holdAccuralVolume);
		// investorIncomeEntity.setConfirmDate(incomeDate);
		if (pmd.getBalance().compareTo(pmd.getDepositAmount()) > 0) {
			investorIncomeEntity.setIncomeAmount(pmd.getBalance().subtract(pmd.getDepositAmount()));
		} else {
			investorIncomeEntity.setIncomeAmount(BigDecimal.ZERO);
			log.info("心愿计划收益错误 uid={}, oid={}, monthmoney={}", pmd.getOid(), pmd.getOid(), pmd.getBalance());
		}
		investorIncomeEntity.setWishplanOid(pmd.getOid());
		investorIncomeEntity.setInvestorBaseAccount(investorBaseAccountDao.findByOid(pmd.getUid()));
		investorIncomeEntity.setConfirmDate(DateUtil.getSqlDate());

		investorIncomeDao.save(investorIncomeEntity);
		planInvestDao.updateWishplanAmountByInvestorOid(pmd.getUid(), investorIncomeEntity.getIncomeAmount());
	}

	@Transactional
	public void staticticsMonthIncome(PlanMonthEntity pmd) {
		// 创建<<发行人-投资人-合仓收益明细>>
		InvestorIncomeEntity investorIncomeEntity = new InvestorIncomeEntity();
		// investorIncomeEntity.setPublisherHold(hold); //所属持有人手册
		// investorIncomeEntity.setIncomeAllocate(incomeAllocate);
		// investorIncomeEntity.setProduct(product); //所属产品
		// investorIncomeEntity.setInvestorBaseAccount(hold.getInvestorBaseAccount());
		// //所属投资人
		// investorIncomeEntity.setIncomeAmount(holdIncomeAmount.add(holdLockIncomeAmount));
		// investorIncomeEntity.setBaseAmount(holdBaseAmount);
		// investorIncomeEntity.setRewardAmount(holdRewardAmount);
		// investorIncomeEntity.setAccureVolume(holdAccuralVolume);
		// investorIncomeEntity.setConfirmDate(incomeDate);
		if (pmd.getIncome().compareTo(pmd.getTotalDepositAmount()) > 0) {
			investorIncomeEntity.setIncomeAmount(pmd.getIncome().subtract(pmd.getTotalDepositAmount()));
		} else {
			investorIncomeEntity.setIncomeAmount(BigDecimal.ZERO);
			log.info("心愿计划收益错误 uid={}, oid={}, monthmoney={}", pmd.getOid(), pmd.getOid(), pmd.getIncome());
		}
		investorIncomeEntity.setWishplanOid(pmd.getOid());
		investorIncomeEntity.setInvestorBaseAccount(investorBaseAccountDao.findByOid(pmd.getUid()));
		investorIncomeEntity.setConfirmDate(DateUtil.getSqlDate());

		investorIncomeDao.save(investorIncomeEntity);

		planInvestDao.updateWishplanAmountByInvestorOid(pmd.getUid(), investorIncomeEntity.getIncomeAmount());
	}

}
