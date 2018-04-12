package com.guohuai.mmp.investor.tradeorder.check;

import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guohuai.ams.product.Product;
import com.guohuai.ams.product.ProductService;
import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.DateUtil;
import com.guohuai.mmp.investor.orderlog.OrderLogEntity;
import com.guohuai.mmp.investor.orderlog.OrderLogService;
import com.guohuai.mmp.investor.tradeorder.InvestorRedeemTradeOrderService;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderDao;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderEntity;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderService;
import com.guohuai.mmp.investor.tradeorder.RefuseRep;
import com.guohuai.mmp.platform.investor.offset.InvestorOffsetService;
import com.guohuai.mmp.platform.publisher.offset.PublisherOffsetService;
import com.guohuai.mmp.platform.publisher.product.offset.ProductOffsetService;
import com.guohuai.mmp.publisher.hold.PublisherHoldService;
import com.guohuai.mmp.serialtask.AbandonParams;
import com.guohuai.mmp.serialtask.SerialTaskEntity;
import com.guohuai.mmp.serialtask.SerialTaskReq;
import com.guohuai.mmp.serialtask.SerialTaskRequireNewService;
import com.guohuai.mmp.serialtask.SerialTaskService;

@Service
@Transactional
public class InvestorAbandonTradeOrderService  {
	Logger logger = LoggerFactory.getLogger(InvestorAbandonTradeOrderService.class);
	@Autowired
	private InvestorTradeOrderDao investorTradeOrderDao;
	@Autowired
	private PublisherHoldService publisherHoldService;
	@Autowired
	private OrderLogService orderLogService;
	@Autowired
	private PublisherOffsetService publisherOffsetService;
	@Autowired
	private ProductOffsetService productOffsetService;
	@Autowired
	private ProductService productService;
	@Autowired
	private InvestorTradeOrderService investorTradeOrderService;
	@Autowired
	private InvestorOffsetService investorOffsetService;
	@Autowired
	private InvestorRedeemTradeOrderService investorRedeemTradeOrderService;
	@Autowired
	private InvestorAbandonTradeOrderRequireNewService investorAbandonTradeOrderRequireNewService;
	@Autowired
	private SerialTaskService serialTaskService;
	@Autowired
	private SerialTaskRequireNewService serialTaskRequireNewService;
	
	
	public BaseResp abandon(AbandonReq req) {

		this.investorAbandonTradeOrderRequireNewService.abandonOrder(req.getOrderCode());

		AbandonParams params = new AbandonParams();
		params.setOrderCode(req.getOrderCode());
		params.setOrderAmount(req.getOrderAmount());
		SerialTaskReq<AbandonParams> sreq = new SerialTaskReq<AbandonParams>();
		sreq.setTaskCode(SerialTaskEntity.TASK_taskCode_abandon);
		sreq.setTaskParams(params);

		serialTaskService.createSerialTask(sreq);

		return new BaseResp();
	}
	
	public void abandonDo(AbandonParams params, String taskOid) {
		BaseResp rep = new BaseResp();

		InvestorTradeOrderEntity orderEntity = investorTradeOrderService.findByOrderCode(params.getOrderCode());
		
		try {
			investorAbandonTradeOrderRequireNewService.abandon(params);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			rep.setErrorCode(-1);
			rep.setErrorMessage(AMPException.getStacktrace(e));
		
		} 
		OrderLogEntity orderLog = new OrderLogEntity();
		orderLog.setOrderType(orderEntity.getOrderType());
		orderLog.setTradeOrderOid(orderEntity.getOrderCode());
		orderLog.setOrderStatus(InvestorTradeOrderEntity.TRADEORDER_orderStatus_abandoned);
		orderLog.setErrorCode(rep.getErrorCode());
		orderLog.setErrorMessage(rep.getErrorMessage());
		this.orderLogService.create(orderLog);
		
		serialTaskRequireNewService.updateTime(taskOid);
	}
	
	/*
	public RefuseRep refuse(String tradeOrderOid) {
		logger.info("refuse tradeOrderOid:{}", tradeOrderOid);
		RefuseRep rep = new RefuseRep();
		
		try {
			
			InvestorTradeOrderEntity tradeOrder = investorTradeOrderService.findByOrderCode(tradeOrderOid);
			
			int i = this.investorTradeOrderDao.refuseOrder(tradeOrderOid);
			if (i > 0) {
				// 产品单日赎回上限 产品处理(非当日的订单，不扣份额)
				if (DateUtil.isEqualDay(tradeOrder.getOrderTime())) {
					this.productService.update4RedeemRefuse(tradeOrder.getProduct(), tradeOrder.getOrderVolume());
				}
				// 仓位处理
				this.publisherHoldService.redeem4Refuse(tradeOrder);
				// 当日赎回累计
				if (DateUtil.isEqualDay(tradeOrder.getOrderTime())) {
					this.publisherHoldService.redeem4RefuseOfDayRedeemVolume(tradeOrder);
				}

				publisherOffsetService.getLatestOffset(tradeOrder,
						investorRedeemTradeOrderService.getRedeemDate(tradeOrder.getProduct(), tradeOrder.getOrderTime()), false);
				productOffsetService.offset(tradeOrder.getPublisherBaseAccount(), tradeOrder, false);
				this.investorOffsetService.getLatestNormalOffset(tradeOrder, false);
				rep.setSuccess(true);
			} 
			
		} catch (AMPException e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			rep.setSuccess(false);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			rep.setSuccess(false);
		}
		InvestorTradeOrderEntity tradeOrder = investorTradeOrderService.findByOrderCode(tradeOrderOid);
		if (rep.isSuccess()) {
			OrderLogEntity orderLog = new OrderLogEntity();
			orderLog.setOrderType(tradeOrder.getOrderType());
			orderLog.setTradeOrderOid(tradeOrder.getOrderCode());
			orderLog.setOrderStatus(InvestorTradeOrderEntity.TRADEORDER_orderStatus_refused);
			this.orderLogService.create(orderLog);
		} else {
			// error.define[20018]=废单状态异常(CODE:20018)
			throw AMPException.getException(20018);
		}
		return rep;
	}
*/	
//	public void isOpenRedeemConfirm(Product product) {
//		logger.info("isOpenRedeemConfirm productOid:{}", product.getOid());
//		if (Product.NO.equals(product.getIsOpenRedeemConfirm())) {
//			String lastOid = "0";
//			while (true) {
//				List<InvestorTradeOrderEntity> orderList = this.investorTradeOrderDao.findByProduct(product.getOid(), lastOid);
//				if (orderList.isEmpty()) {
//					break;
//				}
//				for (InvestorTradeOrderEntity order : orderList) {
//					
//					refuse(order.getOrderCode());
//					lastOid = order.getOid();
//				}
//			}
//		} else {
//			logger.info("打开赎回确认，不作任何处理.");
//		}
//		
//		
//	}


	
}
