package com.guohuai.mmp.investor.tradeorder.check;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.cache.service.CacheHoldService;
import com.guohuai.cache.service.CacheProductService;
import com.guohuai.cache.service.CacheSPVHoldService;
import com.guohuai.cache.service.RedisExecuteLogExtService;
import com.guohuai.component.exception.AMPException;
import com.guohuai.mmp.investor.abandonlog.AbandonLogService;
import com.guohuai.mmp.investor.tradeorder.InvestorRedeemTradeOrderService;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderEntity;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderService;
import com.guohuai.mmp.investor.tradeorder.RedeemTradeOrderReq;
import com.guohuai.mmp.platform.finance.modifyorder.ModifyOrderNewService;
import com.guohuai.mmp.platform.finance.result.PlatformFinanceCompareDataResultNewService;

@Service
@Transactional
public class InvestorRefundTradeOrderService {
	Logger logger = LoggerFactory.getLogger(InvestorRefundTradeOrderService.class);
	@Autowired
	private InvestorTradeOrderService investorTradeOrderService;
	@Autowired
	private CacheHoldService cacheHoldService;
	@Autowired
	private CacheSPVHoldService cacheSPVHoldService;
	@Autowired
	private CacheProductService cacheProductService;
	@Autowired
	private InvestorRedeemTradeOrderService investorRedeemTradeOrderService;
	@Autowired
	private RedisExecuteLogExtService redisExecuteLogExtService;
	@Autowired
	private PlatformFinanceCompareDataResultNewService platformFinanceCompareDataResultNewService;
	@Autowired
	private AbandonLogService abandonLogService;
	@Autowired
	private InvestorRefundTradeOrderRequireNewService investorRefundTradeOrderRequireNewService;
	@Autowired
	private ModifyOrderNewService modifyOrderNewService;
	public void refundPay(InvestorTradeOrderEntity orderEntity) {

//		RedeemPayRequest req = new RedeemPayRequest();
//		req.setUserOid(orderEntity.getInvestorBaseAccount().getMemberId());
//		req.setOrderNo(orderEntity.getOrderCode());
//		req.setAmount(orderEntity.getOrderAmount());
//		req.setFee(BigDecimal.ZERO);
//		req.setProvince(orderEntity.getProvince());
//		req.setCity(orderEntity.getCity());
//		req.setOrderTime(DateUtil.format(orderEntity.getOrderTime(), DateUtil.fullDatePattern));
//		// 支付
//		this.paymentServiceImpl.redeemPay(req);
	}


	public BaseResp refund(RefundTradeOrderReq req) {
		BaseResp rep = new BaseResp();
		
		InvestorTradeOrderEntity orderEntity = this.investorTradeOrderService.findByOrderCode(req.getOrderCode());
		
		
		
		investorRefundTradeOrderRequireNewService.refundOrder(req.getOrderCode());
		
		
		try {
			
			RedeemTradeOrderReq refundReq = new RedeemTradeOrderReq();
			refundReq.setUid(orderEntity.getInvestorBaseAccount().getOid());
			refundReq.setProductOid(orderEntity.getProduct().getOid());
			refundReq.setOrderAmount(req.getOrderAmount());
			
			InvestorTradeOrderEntity refundOrderEntity = investorRedeemTradeOrderService.createRefundTradeOrder(refundReq);
			refundOrderEntity.setOrderStatus(InvestorTradeOrderEntity.TRADEORDER_orderStatus_refunding);
			this.investorTradeOrderService.saveEntity(refundOrderEntity);
			
			this.refundPay(refundOrderEntity);
			
			this.abandonLogService.create(orderEntity, refundOrderEntity);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			rep.setErrorCode(-1);
			rep.setErrorMessage(AMPException.getStacktrace(e));
		}
		
		return rep;
	}

}
