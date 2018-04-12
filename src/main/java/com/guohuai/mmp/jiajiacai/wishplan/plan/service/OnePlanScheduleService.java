package com.guohuai.mmp.jiajiacai.wishplan.plan.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guohuai.component.util.DateUtil;
import com.guohuai.mmp.jiajiacai.caculate.JJCUtility;
import com.guohuai.mmp.jiajiacai.common.constant.PlanStatus;
import com.guohuai.mmp.jiajiacai.common.exception.BaseException;
import com.guohuai.mmp.jiajiacai.wishplan.plan.dao.PlanInvestDao;
import com.guohuai.mmp.jiajiacai.wishplan.plan.entity.PlanInvestEntity;
import com.guohuai.mmp.jiajiacai.wishplan.plan.form.PlanProductForm;
import com.guohuai.mmp.jiajiacai.wishplan.product.form.JJCProductRate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OnePlanScheduleService {

	@Autowired
	private PlanInvestDao planInvestDao;

	@Autowired
	private PlanInvestService planInvestService;
	/**
	 * Buy or redeem
	 * @throws BaseException
	 */
	public void scheduleProcess() throws BaseException {
		List<PlanInvestEntity> listTour = planInvestDao.findStatusesLimit(PlanStatus.DEPOSITED.getCode(), 5);
		for (PlanInvestEntity pme : listTour) {
			if (planInvestService.needReinvest(pme.getOid())) {
				processOnePlan(pme);
			} else {
				planInvestService.updateStatus(pme.getOid(), PlanStatus.REDEEMING.getCode());
			}
		}

	}

//	private InvestMessageForm getMaxProfitProduct(BigDecimal moneyVolum, Timestamp time) throws BaseException {
//		// Get the max profit from product.
//		int duration = 0;
//		boolean onlyOpen = true;
//		if (time != null){
//			duration =	DateUtil.getTimeRemainDays(time);
//			onlyOpen = false;
//		}
//
//		JJCProductRate productRate = productService.getRateByDuration(duration, onlyOpen);
////		if (productRate.getOid() == null) {
////			throw new BaseException(ErrorMessage.PRODUCT_NOT_EXIST);
////		}
//		InvestMessageForm form = planProductService.caculateInvestProfitByFixed(duration, moneyVolum.intValue(),
//				productRate.getRate());
//		form.setRate(productRate.getRate());
//		form.setProductOid(productRate.getOid());
//		form.setProductMoneyValume(productRate.getProductMoneyValume());
//		return form;
//	}

	public void processOnePlan(PlanInvestEntity pme) throws BaseException {

		try {
			//Test
//			pme.setBalance(new BigDecimal(250));
			
			if (pme.getBalance().compareTo(BigDecimal.ONE) < 0) {
				planInvestDao.updateStatus(pme.getOid(), PlanStatus.SUCCESS.getCode());
				return ;
			}
//			BigDecimal balance = new BigDecimal(pme.getBalance().intValue());
//			InvestMessageForm investForm = getMaxProfitProduct(balance, pme.getEndTime());
			List<String> exculuds =  Arrays.asList("");
			
			JJCProductRate rate = planInvestService.getMaxProfitProduct(pme.getEndTime(), exculuds, pme.getUid());
			
			PlanProductForm tradeOrderReq = initPlanProductFrom(pme, rate);
			tradeOrderReq.setMoneyVolume(JJCUtility.bigKeep2Decimal(pme.getBalance()));
			//Test
//			rate.setProductMoneyValume(new BigDecimal(100));
			
			planInvestService.nestCounter();
			planInvestService.nestBuyProduct(tradeOrderReq, rate.getProductMoneyValume(), pme);
//			planInvestService.updateStatusByBalance(pme.getOid());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			log.info("计划产品购买失败 oid={}, type={}", pme.getOid(), pme.getPlanType());
		}
	}

	private static PlanProductForm initPlanProductFrom(PlanInvestEntity pme, JJCProductRate rate)
			throws BaseException {
		PlanProductForm tradeOrderReq = new PlanProductForm();
		tradeOrderReq.setUid(pme.getUid());
//		tradeOrderReq.setMoneyVolume(pme.getDepositAmount());
//		tradeOrderReq.setMoneyVolume(pme.getBalance());
		
		log.info("计划购买开始 uid={}, money={}, productid={}, startTime={}", tradeOrderReq.getUid(),
				tradeOrderReq.getMoneyVolume(), tradeOrderReq.getProductOid(), DateUtil.getSqlCurrentDate());
		tradeOrderReq.setProductOid(rate.getOid());
		tradeOrderReq.setPlanOid(pme.getOid());
		tradeOrderReq.setPlanType(pme.getPlanType());
		// hard code
		tradeOrderReq.setCid(pme.getCid());
		tradeOrderReq.setCkey(pme.getCkey());
//		tradeOrderReq.setPlanRedeemOid(rate.getPlanRedeemOid());
		tradeOrderReq.setOriginBranch(PlanProductForm.TRADEORDER_originBranch_whishMiddle);
		return tradeOrderReq;
	}

}
