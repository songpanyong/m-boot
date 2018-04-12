package com.guohuai.mmp.jiajiacai.wishplan.plan.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guohuai.basic.common.SeqGenerator;
import com.guohuai.component.util.DateUtil;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountService;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderDao;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderEntity;
import com.guohuai.mmp.jiajiacai.wishplan.plan.dao.PlanInvestDao;
import com.guohuai.mmp.jiajiacai.wishplan.plan.form.TradeOrderForm;
import com.guohuai.mmp.platform.msgment.BuySuccessMailReq;
import com.guohuai.mmp.platform.msgment.BuySuccessMsgReq;
import com.guohuai.mmp.sys.CodeConstants;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PlanTradeOrderService {

	@Autowired
	private SeqGenerator seqGenerator;
	
	@Autowired
	private InvestorTradeOrderDao investorTradeOrderDao;
	
	@Autowired
	private InvestorBaseAccountService investorBaseAccountService;
	
	@Autowired
	private PlanInvestDao mPlanInvestDao;
	@Transactional
	public InvestorTradeOrderEntity recordTradeOrder(TradeOrderForm form, boolean isInvest) {
		InvestorTradeOrderEntity orderEntity = new InvestorTradeOrderEntity();
		/*
		 * orderEntity.setPublisherBaseAccount(product.getPublisherBaseAccount());
		 * orderEntity.setInvestorBaseAccount(this.investorBaseAccountService.findOne(
		 * tradeOrderReq.getUid())); orderEntity.setProduct(product);
		 */
		if (isInvest) {
			orderEntity.setOrderCode(seqGenerator.next(CodeConstants.PAYMENT_wishInvest));
			orderEntity.setOrderType(InvestorTradeOrderEntity.TRADEORDER_orderType_wishInvest);
		} else {
			orderEntity.setOrderCode(seqGenerator.next(CodeConstants.PAYMENT_wishRedeem));
			orderEntity.setOrderType(InvestorTradeOrderEntity.TRADEORDER_orderType_wishRedeem);
		}
		orderEntity.setInvestorBaseAccount(investorBaseAccountService.findOne(form.getUid()));
		orderEntity.setOrderAmount(form.getMoneyVolume());
		orderEntity.setOrderVolume(form.getMoneyVolume());
		
		orderEntity.setOrderStatus(InvestorTradeOrderEntity.TRADEORDER_orderStatus_confirmed);
		orderEntity.setContractStatus(InvestorTradeOrderEntity.TRADEORDER_contractStatus_toHtml);
		orderEntity.setCheckStatus(InvestorTradeOrderEntity.TRADEORDER_checkStatus_no);

		orderEntity.setOrderTime(DateUtil.getSqlCurrentDate()); // 订单时间
		
		orderEntity.setWishplanOid(form.getPlanRedeemOid());
//		orderEntity.setOriginBranch(InvestorTradeOrderEntity.TRADEORDER_originBranch_whishStartEnd);
		
		orderEntity = investorTradeOrderDao.save(orderEntity);
		
		return orderEntity;

	}
	

	/**
	 * sms and mail
	 */
	public void payCallback(InvestorTradeOrderEntity orderEntity, String type) {
		String wishName = null;
		if (orderEntity.getWishplanOid() != null ) {
			wishName = mPlanInvestDao.findPlanName(orderEntity.getWishplanOid());
		}
		BuySuccessMsgReq msgReq = new BuySuccessMsgReq();
		msgReq.setPhone(orderEntity.getInvestorBaseAccount().getPhoneNum());
		msgReq.setProductName(wishName);
//		msgService.buysuccess(msgReq, true);

		BuySuccessMailReq mailReq = new BuySuccessMailReq();
		mailReq.setProductName(wishName);
		mailReq.setUserOid(orderEntity.getInvestorBaseAccount().getOid());
//		mailService.buysuccess(mailReq, true);
	}


	public List<Object[]> queryWishplanRedeem(String investorOid) {
		return this.investorTradeOrderDao.queryWishplanRedeem(investorOid);
		
	}


	public List<Object[]> queryWishplanTodayRedeem(String investorOid) {
		
		return this.investorTradeOrderDao.queryWishplanTodayRedeem(investorOid);
	}


	public List<InvestorTradeOrderEntity> queryWishplanOid(String investorOid) {
		
		return this.investorTradeOrderDao.findTradeOrderByInvestorOid(investorOid);
	}


	public int queryTotalInvestCount(String investorOid) {
		
		return investorTradeOrderDao.queryTotalInvestCountByOid(investorOid);
		
	}


	public List<Object[]> queryTodayInvestInfo(String investorOid) {
		
		return investorTradeOrderDao.queryTodayInvestInfo(investorOid);
	}
	
	/**
	 * find TradeOrder By Wishplan And Type
	 * @param wishplanOid
	 * @param type
	 * @return
	 */
    public int findTradeOrderByWishplanAndType(String wishplanOid, String type) {	
		return investorTradeOrderDao.findTradeOrderByWishplanAndType(wishplanOid, type);
	}
	
}
