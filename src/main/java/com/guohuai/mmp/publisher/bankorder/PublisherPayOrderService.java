package com.guohuai.mmp.publisher.bankorder;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.component.util.DateUtil;
import com.guohuai.component.util.StringUtil;
import com.guohuai.mmp.platform.accment.AccParam;
import com.guohuai.mmp.platform.accment.Accment;
import com.guohuai.mmp.platform.accment.PublisherTradeRequest;
import com.guohuai.mmp.publisher.baseaccount.PublisherBaseAccountEntity;
import com.guohuai.mmp.publisher.baseaccount.PublisherBaseAccountService;
import com.guohuai.mmp.publisher.cashflow.PublisherCashFlowService;

@Service
@Transactional
public class PublisherPayOrderService {
	
	@Autowired
	private Accment accmentService;
	@Autowired
	private PublisherBankOrderService publisherBankOrderService;
	@Autowired
	private PublisherBaseAccountService publisherBaseAccountService;
	@Autowired
	private PublisherCashFlowService publisherCashFlowService;

	/**
	 *  收款--发行人
	 */
	@Transactional
	public BaseResp pay(BankOrderPayReq bankOrderReq, String investorOid) {
		
		
		BankOrderRep bankOrderRep = new BankOrderRep();
		
		PublisherBaseAccountEntity baseAccount = publisherBaseAccountService.findByLoginAcc(investorOid);
		/** 创建订单 */
		PublisherBankOrderEntity bankOrder = publisherBankOrderService.createPayOrder(bankOrderReq, baseAccount);
		
		PublisherTradeRequest ireq = new PublisherTradeRequest();
		ireq.setMemeberId(baseAccount.getMemberId());
		ireq.setUserType(AccParam.UserType.SPV.toString());
		ireq.setOrderType(AccParam.OrderType.SPVPAY.toString());
		ireq.setBalance(bankOrder.getOrderAmount());
		ireq.setRemark("备注:发行人放款");
		ireq.setOrderCode(bankOrder.getOrderCode());
		ireq.setSystemSource(AccParam.SystemSource.MIMOSA.toString());
		ireq.setRequestNo(StringUtil.uuid());
		ireq.setOrderDesc("订单描述:发行人放款");
		
		BaseResp baseRep = this.accmentService.publisherTrade(ireq);
		
		if (0 != baseRep.getErrorCode()) {
			bankOrderRep.setErrorCode(baseRep.getErrorCode());
			bankOrderRep.setErrorMessage(baseRep.getErrorMessage());
			bankOrder.setOrderStatus(PublisherBankOrderEntity.BANKORDER_orderStatus_submitFailed);
			bankOrder.setCompleteTime(DateUtil.getSqlCurrentDate());
			this.publisherBankOrderService.saveEntity(bankOrder);
		} else {
			bankOrder.setOrderStatus(PublisherBankOrderEntity.BANKORDER_orderStatus_toPay);
			this.publisherBankOrderService.saveEntity(bankOrder);
			this.payCallback(bankOrder);
		}
		
		
		bankOrderRep.setBankOrderOid(bankOrder.getOid());
		bankOrderRep.setErrorCode(baseRep.getErrorCode());
		bankOrderRep.setErrorMessage(baseRep.getErrorMessage());
		return baseRep;
	}
	
	
	public boolean payCallback(PublisherBankOrderEntity bankOrder) {
//		PublisherBankOrderEntity bankOrder = publisherBankOrderService.findByOrderCode(ireq.getOrderCode());
		String orderStatus = PublisherBankOrderEntity.BANKORDER_orderStatus_done;
//		if (PayParam.ReturnCode.RC0000.toString().equals(ireq.getReturnCode())) {
			/** 创建<<发行人-资金变动明细>> */
			publisherCashFlowService.createCashFlow(bankOrder);
			/** 更新<<发行人-基本账户>>.<<账户余额>> */
			this.publisherBaseAccountService.updateBalance(bankOrder.getPublisherBaseAccount());
//		} else {
//			orderStatus = PublisherBankOrderEntity.BANKORDER_orderStatus_payFailed;
//		}
		
		bankOrder.setOrderStatus(orderStatus);
		bankOrder.setCompleteTime(DateUtil.getSqlCurrentDate());
		publisherBankOrderService.saveEntity(bankOrder);
		
		return true;
	}
	
	
	
}
