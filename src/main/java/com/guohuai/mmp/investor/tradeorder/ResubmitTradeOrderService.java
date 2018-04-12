package com.guohuai.mmp.investor.tradeorder;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.guohuai.basic.component.ext.web.BaseResp;

@Service
@Transactional
public class ResubmitTradeOrderService {

	Logger logger = LoggerFactory.getLogger(ResubmitTradeOrderService.class);
	@Value("${investor.info.kickstar:true}")
	private boolean kickstar = true;
	@Autowired
	private InvestorTradeOrderDao investorTradeOrderDao;

	@Autowired
	private InvestorRedeemTradeOrderService investorRedeemTradeOrderService;
	/**
	 * 订单重新提交
	 */
	public BaseResp resubmit(String orderCode) {
		TradeOrderRep tradeOrderRep = null;
		InvestorTradeOrderEntity orderEntity = investorTradeOrderDao.findByOrderCode(orderCode);
		if (orderEntity.getOrderType().equals(InvestorTradeOrderEntity.TRADEORDER_orderType_normalRedeem)) {
			if (orderEntity.getWishplanOid() != null && orderEntity.getOrderStatus().equals(InvestorTradeOrderEntity.TRADEORDER_orderStatus_refused)) {
				try {
					tradeOrderRep = investorRedeemTradeOrderService.redeemRequiresNew(orderEntity.getOrderCode());
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(e.getMessage(), e);
					tradeOrderRep.setErrorCode(-1);
					tradeOrderRep.setErrorMessage(e.getMessage());
				}
				// 赎回日志记录
				investorRedeemTradeOrderService.redeemResubmitThen(tradeOrderRep, orderEntity.getOrderCode());
				return tradeOrderRep;
			}
		}
		return tradeOrderRep;
	}

}
