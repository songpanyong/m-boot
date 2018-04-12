package com.guohuai.mmp.investor.orderlog;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.mmp.investor.bankorder.InvestorBankOrderEntity;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderEntity;
import com.guohuai.mmp.platform.notify.NotifyService;

@Service
@Transactional
public class OrderLogService {
	Logger logger = LoggerFactory.getLogger(OrderLogService.class);
	@Autowired
	OrderLogDao orderLogDao;

	@Autowired
	NotifyService notifyService;

	public OrderLogEntity save(OrderLogEntity orderLog) {
		return this.orderLogDao.save(orderLog);
	}

	@Transactional(value = TxType.REQUIRES_NEW)
	public OrderLogEntity create(OrderLogEntity orderLog) {
		orderLog = save(orderLog);

		this.notifyService.create(orderLog.getOrderStatus(), JSONObject.toJSONString(orderLog));

		return orderLog;
	}

	/**
	 * 创建赎回结算日志
	 * 
	 * @param orderCode
	 * @param orderStatus
	 * @param orderType
	 * @param e
	 * @return
	 */
	public OrderLogEntity createRedeemCloseLog(InvestorTradeOrderEntity investOrder,
			InvestorTradeOrderEntity redeemOrder) {
		OrderLogEntity orderLog = new OrderLogEntity();
		orderLog.setTradeOrderOid(investOrder.getOrderCode());
		orderLog.setOrderStatus(investOrder.getHoldStatus());
		orderLog.setReferredOrderCode(redeemOrder.getOrderCode());
		orderLog.setReferredOrderAmount(redeemOrder.getOrderAmount());
		orderLog.setOrderType(OrderLogEntity.ORDERLOG_orderType_investClose);
		orderLog = save(orderLog);
		this.notifyService.create(investOrder.getHoldStatus(), JSONObject.toJSONString(orderLog));
		return orderLog;
	}
	
	@Transactional(value = TxType.REQUIRES_NEW)
	public void createOrderLog(OrderLogReq ireq) {
		OrderLogEntity orderLog = new OrderLogEntity();
		orderLog.setTradeOrderOid(ireq.getOrderCode());
		orderLog.setOrderType(ireq.getOrderType());
		orderLog.setOrderStatus(ireq.getOrderStatus());
		orderLog.setErrorCode(ireq.getErrorCode());
		orderLog.setErrorMessage(ireq.getErrorMessage());
		this.save(orderLog);
		
	}
	
	public void redEnvelOrderLog(String orderCode, BaseResp rep) {
		OrderLogEntity orderLog = new OrderLogEntity();
		orderLog.setOrderStatus(rep.getErrorCode() == 0 ? InvestorBankOrderEntity.BANKORDER_orderStatus_paySuccess : InvestorBankOrderEntity.BANKORDER_orderStatus_payFailed);
		orderLog.setTradeOrderOid(orderCode);
		orderLog.setErrorCode(rep.getErrorCode());
		orderLog.setErrorMessage(rep.getErrorMessage());
		orderLog.setOrderType(InvestorBankOrderEntity.BNAKORDER_orderType_redEnvelope);
		this.create(orderLog);
	}

}
