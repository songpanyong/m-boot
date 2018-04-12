package com.guohuai.mmp.jiajiacai.wishplan.plan.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guohuai.cache.entity.ProductCacheEntity;
import com.guohuai.cache.service.CacheProductService;
import com.guohuai.component.exception.AMPException;
import com.guohuai.mmp.investor.tradeorder.InvestorInvestTradeOrderExtService;
import com.guohuai.mmp.investor.tradeorder.RedeemTradeOrderReq;
import com.guohuai.mmp.investor.tradeorder.TradeOrderRep;
import com.guohuai.mmp.jiajiacai.common.constant.InvestTypeEnum;
import com.guohuai.mmp.jiajiacai.common.constant.PlanStatus;
import com.guohuai.mmp.jiajiacai.wishplan.plan.dao.WishplanProductDao;
import com.guohuai.mmp.jiajiacai.wishplan.plan.entity.PlanInvestEntity;
import com.guohuai.mmp.jiajiacai.wishplan.plan.entity.PlanMonthEntity;
import com.guohuai.mmp.jiajiacai.wishplan.plan.entity.PlanProductEntity;
import com.guohuai.mmp.jiajiacai.wishplan.plan.entity.WishplanProduct;
import com.guohuai.mmp.publisher.hold.PublisherHoldDao;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PlanProductRedeemService {

	@Autowired
	private PlanMonthService planMonthService;

	@Autowired
	private PlanProductService planProductService;

	@Autowired
	private InvestorInvestTradeOrderExtService investorInvestTradeOrderExtService;

	@Autowired
	private PlanInvestService planInvestService;

	@Autowired
	private WishplanProductDao productDao;
	
	@Autowired
	private PublisherHoldDao publisherHoldDao;

	@Autowired
	private CacheProductService cacheProductService;
	
	public void planRedeem() {
		List<PlanMonthEntity> listMonth = planMonthService.queryOverdueCurrentProduct();
		for (PlanMonthEntity pme : listMonth) {
			try {
				processMonthRedeem(pme);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		List<PlanInvestEntity> listInvest = planInvestService.queryOverdue();
		for (PlanInvestEntity pme : listInvest) {
			try {
				processInvestRedeem(pme);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public void planRedeemAgain() {
		List<PlanProductEntity> planEntityList = planProductService.queryInvestByStatus(PlanStatus.TOREDEEM.getCode());
		for (PlanProductEntity p : planEntityList) {
			WishplanProduct product = productDao.findByOid(p.getProductOid());
			if (WishplanProduct.TYPE_Producttype_02.equals(product.getType())) {
				PlanInvestEntity pme = planInvestService.queryByOid(p.getPlanOid());
				RedeemTradeOrderReq redeemTradeOrderReq = createTradeOrder(p, pme);
				
				if (redeemTradeOrderReq == null) {
					continue;
				}
				
				TradeOrderRep rep = null;
				try {
					rep = investorInvestTradeOrderExtService.redeem(redeemTradeOrderReq);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (rep.getErrorCode() == 0) {
					planProductService.updateStatus(p.getOid(), PlanStatus.REDEEMING.getCode());
					log.info("心愿产品赎回申请成功 oid={}, monthmoney={}, planoid={}", p.getOid(), p.getAmount(), p.getPlanOid());
				} else {
					planProductService.updateStatus(p.getOid(), PlanStatus.TOREDEEM.getCode());
					log.info("心愿产品赎回申请失败 oid={}, monthmoney={}, planoid={}", p.getOid(), p.getAmount(), p.getPlanOid());
				}
				
			}
		}
	}
	/**
	 * processMonthRedeem
	 * @param pme
	 * @return
	 */
	public int processMonthRedeem(PlanMonthEntity pme) {
		List<String> listStatuses = Arrays.asList(PlanStatus.SUCCESS.getCode(), PlanStatus.REDEEMING.getCode());
		List<PlanInvestEntity> depositList = planInvestService.findByMonthOidAndStatusList(pme.getOid(),
				listStatuses);
		for (PlanInvestEntity monthDep : depositList) {
			List<PlanProductEntity> planEntityList = planProductService.findByPlanOidAndStatus(monthDep.getOid(),
					PlanStatus.SUCCESS.getCode());
			for (PlanProductEntity p : planEntityList) {
				WishplanProduct product = productDao.findByOid(p.getProductOid());
				if (WishplanProduct.TYPE_Producttype_02.equals(product.getType())) {
					RedeemTradeOrderReq redeemTradeOrderReq = createTradeOrder(p, monthDep);
					TradeOrderRep rep = null;
					try {
						rep = investorInvestTradeOrderExtService.redeem(redeemTradeOrderReq);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (rep.getErrorCode() == 0) {
						planProductService.updateStatus(p.getOid(), PlanStatus.REDEEMING.getCode());
						log.info("心愿产品赎回申请成功 oid={}, monthmoney={}, planoid={}", p.getOid(), p.getAmount(),
								p.getPlanOid());
					} else {
						planProductService.updateStatus(p.getOid(), PlanStatus.TOREDEEM.getCode());
						log.info("心愿产品赎申请回失败 oid={}, monthmoney={}, planoid={}", p.getOid(), p.getAmount(),
								p.getPlanOid());
					}
				} else {
					log.info("心愿产品赎回 TYPE_Producttype_02: oid={}, monthmoney={}, productOid={}",  p.getOid(), p.getAmount(), p.getProductOid());
				}
			}
			planInvestService.updateStatus(monthDep.getOid(), PlanStatus.REDEEMING.getCode());
		}
		return planMonthService.updateStatus(pme.getOid(), PlanStatus.REDEEMING.getCode());
		/*
		if (InvestTypeEnum.MonthSalaryInvest.getCode().equals(pme.getPlanType())) {
			planMonthService.updateStatus(pme.getOid(), PlanStatus.REDEEMING.getCode());
			return planMonthService.transferBalance(pme.getOid());
		} else {
			return planMonthService.updateStatus(pme.getOid(), PlanStatus.REDEEMING.getCode());
		}
		*/
	}

	/**
	 * processSalaryRedeem
	 * @param pme
	 * @return
	 */
	public int processSalaryRedeem(PlanMonthEntity pme) {
		List<PlanInvestEntity> depositList = planInvestService.queryByPlanMonthOidAndStatus(pme.getOid(),
				PlanStatus.SUCCESS.getCode());
//		
//		if (depositList.size() == 0) {
//			return -1;
//		}
		
		for (PlanInvestEntity monthDep : depositList) {
			List<PlanProductEntity> planEntityList = planProductService.findByPlanOidAndStatus(monthDep.getOid(),
					PlanStatus.SUCCESS.getCode());
			for (PlanProductEntity p : planEntityList) {
				RedeemTradeOrderReq redeemTradeOrderReq = createTradeOrder(p, monthDep);
				TradeOrderRep rep = investorInvestTradeOrderExtService.redeem(redeemTradeOrderReq);
				if (rep.getErrorCode() == 0) {
					planProductService.updateStatus(p.getOid(), PlanStatus.REDEEMING.getCode());
					log.info("心愿产品赎回申请成功 oid={}, monthmoney={}, planoid={}", p.getOid(), p.getAmount(), p.getPlanOid());
				} else {
					planProductService.updateStatus(p.getOid(), PlanStatus.TOREDEEM.getCode());
					log.info("心愿产品赎申请回失败 oid={}, monthmoney={}, planoid={}", p.getOid(), p.getAmount(), p.getPlanOid());
				}
			}
			planInvestService.completeInvest(monthDep.getOid(), PlanStatus.REDEEMING.getCode());
		}
		if (InvestTypeEnum.MonthSalaryInvest.getCode().equals(pme.getPlanType())) {
//			planMonthService.updateStatus(pme.getOid(), PlanStatus.REDEEMING.getCode());
			return planMonthService.redeemSalaryplan(pme.getOid());
		} else {
			return planMonthService.updateStatus(pme.getOid(), PlanStatus.REDEEMING.getCode());
		}
	}

	private void processInvestRedeem(PlanInvestEntity pme) {
		List<PlanProductEntity> planEntityList = planProductService.findByPlanOidAndStatus(pme.getOid(),
				PlanStatus.SUCCESS.getCode());
		for (PlanProductEntity p : planEntityList) {
			WishplanProduct product = productDao.findByOid(p.getProductOid());
			if (product != null && WishplanProduct.TYPE_Producttype_02.equals(product.getType())) {
				log.info("心愿产品赎回申请oid={}, monthmoney={}, planoid={}", p.getOid(), p.getAmount(), p.getPlanOid());
				RedeemTradeOrderReq redeemTradeOrderReq = createTradeOrder(p, pme);
				TradeOrderRep rep = null;
				try {
					rep = investorInvestTradeOrderExtService.redeem(redeemTradeOrderReq);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (rep.getErrorCode() == 0) {
					planProductService.updateStatus(p.getOid(), PlanStatus.REDEEMING.getCode());
					log.info("心愿产品赎回申请成功 oid={}, monthmoney={}, planoid={}", p.getOid(), p.getAmount(), p.getPlanOid());
				} else {
					planProductService.updateStatus(p.getOid(), PlanStatus.TOREDEEM.getCode());
					log.info("心愿产品赎回申请失败 oid={}, monthmoney={}, planoid={}", p.getOid(), p.getAmount(), p.getPlanOid());
				}
			} else {
				log.info("心愿产品赎回 TYPE_Producttype_02: oid={}, monthmoney={}, productOid={}",  p.getOid(), p.getAmount(), p.getProductOid());
			}
			planInvestService.updateStatus(pme.getOid(), PlanStatus.REDEEMING.getCode());
		}
	}

	public RedeemTradeOrderReq createTradeOrder(PlanProductEntity pp, PlanInvestEntity pi) {
		RedeemTradeOrderReq tradeOrder = new RedeemTradeOrderReq();
		tradeOrder.setCid(pi.getCid());
		tradeOrder.setCkey(pi.getCkey());
		
		//tradeOrder.setOrderAmount(new BigDecimal(pp.getAmount()));
//		tradeOrder.setOrderAmount(publisherHoldDao.findRedeemableHoldVolume(pp.getOid()));
		//tradeOrder.setOrderAmount(new BigDecimal(pp.getAmount()));
		BigDecimal redeemAmount = publisherHoldDao.findRedeemableHoldVolume(pp.getOid());
		
		if (redeemAmount == null  || redeemAmount.floatValue() <= 0) {
			throw new AMPException("redeemAmount 不足)");
		}
		
		ProductCacheEntity product = cacheProductService.getProductCacheEntityById(pp.getProductOid());
		if (product == null) {
			throw new AMPException("product 不存在)");
		}
		
		BigDecimal redeemAbleAmount = null;
		/*
		if (product.getMinRredeem() != null && product.getMinRredeem().floatValue() > 0) { 
			BigDecimal remainder = redeemAmount.remainder(product.getMinRredeem());
			redeemAbleAmount = redeemAmount.subtract(remainder);
		} else {
			redeemAbleAmount = redeemAmount;
		}
		*/
		redeemAbleAmount = redeemAmount;
		log.info("心愿产品赎回份额 holdVolume={}, minRredeem={}, redeemAbleAmount={}", redeemAmount, product.getMinRredeem(), redeemAbleAmount);
		
		tradeOrder.setOrderAmount(redeemAbleAmount);
		
		tradeOrder.setProductOid(pp.getProductOid());

//		tradeOrder.setPlanRedeemOid(pi.getOid());
//		tradeOrder.setUid(pi.getUid());
		
		tradeOrder.setPlanRedeemOid(pp.getOid());
		tradeOrder.setUid(pp.getUid());

		return tradeOrder;
	}
	
}
