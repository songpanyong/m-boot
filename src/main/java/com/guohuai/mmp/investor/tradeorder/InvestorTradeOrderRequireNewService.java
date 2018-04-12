package com.guohuai.mmp.investor.tradeorder;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
//@Slf4j
public class InvestorTradeOrderRequireNewService {
	
	
	@Autowired
	private InvestorTradeOrderService investorTradeOrderService;
	@Autowired
	private InvestorInvestTradeOrderExtService investorInvestTradeOrderExtService;
	
	
	@Transactional(value = TxType.REQUIRES_NEW)
	public void processOneItem(InvestorTradeOrderEntity orderInEntity) {
		InvestorTradeOrderEntity orderEntity = investorTradeOrderService.findByOrderCode(orderInEntity.getOrderCode());
		/** 记录赎回单 */
		RedeemTradeOrderReq rtoRep = new RedeemTradeOrderReq();
		rtoRep.setProductOid(orderEntity.getProduct().getOid());
		rtoRep.setOrderAmount(orderEntity.getOrderAmount());
		rtoRep.setUid(orderEntity.getInvestorBaseAccount().getOid());
		
		investorInvestTradeOrderExtService.expGoldRedeem(rtoRep);
		
		
		
		
		
	}
	

}
