package com.guohuai.mmp.investor.tradeorder;

import java.sql.Date;
import java.sql.Timestamp;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guohuai.ams.channel.ChannelService;
import com.guohuai.ams.product.Product;
import com.guohuai.ams.product.ProductService;
import com.guohuai.cache.service.CacheHoldService;
import com.guohuai.cache.service.CacheInvestorService;
import com.guohuai.cache.service.CacheProductService;
import com.guohuai.calendar.TradeCalendarService;
import com.guohuai.component.util.DateUtil;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountEntity;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountService;
import com.guohuai.mmp.investor.cashflow.InvestorCashFlowService;
import com.guohuai.mmp.investor.orderlog.OrderLogEntity;
import com.guohuai.mmp.investor.orderlog.OrderLogService;
import com.guohuai.mmp.platform.SeqGeneratorService;
import com.guohuai.mmp.platform.msgment.AbortiveMailReq;
import com.guohuai.mmp.platform.msgment.AbortiveMsgReq;
import com.guohuai.mmp.platform.msgment.MailService;
import com.guohuai.mmp.platform.msgment.MsgService;
import com.guohuai.mmp.platform.msgment.MsgUtil;
import com.guohuai.mmp.platform.msgment.ReceivedPaymentsMailReq;
import com.guohuai.mmp.platform.msgment.ReceivedPaymentsMsgReq;
import com.guohuai.mmp.platform.publisher.offset.OffsetService;
import com.guohuai.mmp.platform.publisher.offset.PublisherOffsetService;
import com.guohuai.mmp.platform.publisher.product.offset.ProductOffsetService;
import com.guohuai.mmp.platform.tulip.TulipService;
import com.guohuai.mmp.publisher.hold.PublisherHoldService;
import com.guohuai.mmp.serialtask.RedeemNotifyParams;
import com.guohuai.mmp.serialtask.SerialTaskEntity;
import com.guohuai.mmp.serialtask.SerialTaskReq;
import com.guohuai.mmp.serialtask.SerialTaskRequireNewService;
import com.guohuai.mmp.serialtask.SerialTaskService;
import com.guohuai.mmp.sys.CodeConstants;

@Service
@Transactional
public class InvestorRedeemTradeOrderService {
	Logger logger = LoggerFactory.getLogger(InvestorRedeemTradeOrderService.class);
	
	@Autowired
	private InvestorBaseAccountService investorBaseAccountService;
	@Autowired
	private ChannelService channelService; 
	@Autowired
	private ProductService productService;
	@Autowired
	private SeqGeneratorService seqGeneratorService;
	@Autowired
	private PublisherHoldService publisherHoldService;
	@Autowired
	private TradeCalendarService tradeCalendarService;
	@Autowired
	private InvestorTradeOrderService investorTradeOrderService;
	@Autowired
	private InvestorCashFlowService investorCashFlowService;
	@Autowired
	private OrderLogService orderLogService;
	@Autowired
	private OffsetService offsetService;
	@Autowired
	private TulipService tulipService;
	@Autowired
	private CacheProductService cacheProductService;
	@Autowired
	private CacheHoldService cacheHoldService;
	@Autowired
	private SerialTaskService serialTaskService;
	@Autowired
	private SerialTaskRequireNewService serialTaskRequireNewService;
	@Autowired
	private MsgService msgService;
	@Autowired
	private MailService mailService;
	@Autowired
	private MsgUtil msgUtil;
	@Autowired
    private  OrderDateService orderDateService;
    @Autowired
    private ProductOffsetService productOffsetService;
    @Autowired
    private PublisherOffsetService publisherOffsetService;
    @Autowired
    private CacheInvestorService cacheInvestorService;
	
	@Transactional(value = TxType.REQUIRES_NEW)
	public TradeOrderRep redeemRequiresNew(String orderCode) {
		InvestorTradeOrderEntity orderEntity = investorTradeOrderService.findByOrderCode(orderCode);
		return this.redeem(orderEntity);
	}
	
	
	public TradeOrderRep redeem(InvestorTradeOrderEntity orderEntity) {
		TradeOrderRep tradeOrderRep = new TradeOrderRep();
		//Not strict the wishplan
		if (orderEntity.getWishplanOid() == null) {
			verification(orderEntity);
		}
		RedeemNotifyParams params = new RedeemNotifyParams();
		params.setOrderCode(orderEntity.getOrderCode());
		SerialTaskReq<RedeemNotifyParams> sreq = new SerialTaskReq<RedeemNotifyParams>();
		sreq.setTaskParams(params);
		sreq.setTaskCode(SerialTaskEntity.TASK_taskCode_redeem);
		serialTaskService.createSerialTask(sreq);
		
		tradeOrderRep.setTradeOrderOid(orderEntity.getOid());
		return tradeOrderRep;
	}
	
	
	
	@Transactional(value = TxType.REQUIRES_NEW)
	public TradeOrderRep redeemDo(String orderCode, String taskOid) {
		
		TradeOrderRep tradeOrderRep = new TradeOrderRep();

		InvestorTradeOrderEntity orderEntity = investorTradeOrderService.findByOrderCode(orderCode);
		
		this.vericationdb(orderEntity);
		
		   /** 记录资金明细 */
        this.cashFlow(orderEntity);
        
        /** 参与轧差 */
        this.offset(orderEntity);
        
		/** 份额确认 */
		this.confirm(orderEntity);
		
		/** 消息通知 */
		//Filter out the wish plan
		if (orderEntity.getWishplanOid() == null) {
			this.sendMsg(orderEntity);
		}
		
		/** 推广平台事件发送 */
		this.tulipService.onRedeem(orderEntity);
		
		this.investorTradeOrderService.saveEntity(orderEntity);
		
		serialTaskRequireNewService.updateTime(taskOid);
		
		return tradeOrderRep;

	}

	private void sendMsg(InvestorTradeOrderEntity orderEntity) {
		if (InvestorTradeOrderEntity.TRADEORDER_orderType_cashFailed.equals(orderEntity.getOrderType())) {
			AbortiveMsgReq msgReq = new AbortiveMsgReq();
			msgReq.setProductName(orderEntity.getProduct().getName());
			msgReq.setHotLine(msgUtil.getHotLine());
			msgReq.setPhone(orderEntity.getInvestorBaseAccount().getPhoneNum());
			msgService.abortive(msgReq);
			
			AbortiveMailReq mailReq = new AbortiveMailReq();
			mailReq.setUserOid(orderEntity.getInvestorBaseAccount().getOid());
			mailReq.setProductName(orderEntity.getProduct().getName());
			mailReq.setHotLine(msgUtil.getHotLine());
			mailService.abortive(mailReq);
			
		} 
		if (InvestorTradeOrderEntity.TRADEORDER_orderType_cash.equals(orderEntity.getOrderType()) ||
				InvestorTradeOrderEntity.TRADEORDER_orderType_cashFailed.equals(orderEntity.getOrderType())) {
			ReceivedPaymentsMsgReq req = new ReceivedPaymentsMsgReq();
			req.setPhone(orderEntity.getInvestorBaseAccount().getPhoneNum());
			req.setProductName(orderEntity.getProduct().getName());
			req.setOrderAmount(orderEntity.getPayAmount());
			msgService.receivedpayments(req);
			
			ReceivedPaymentsMailReq mail = new ReceivedPaymentsMailReq();
			mail.setUserOid(orderEntity.getInvestorBaseAccount().getOid());
			mail.setProductName(orderEntity.getProduct().getName());
			mail.setOrderAmount(orderEntity.getPayAmount());
			mailService.receivedpayments(mail);
		}
	}
	
	
	private InvestorTradeOrderEntity offset(InvestorTradeOrderEntity orderEntity) {
		
		if (0 != orderEntity.getProduct().getRedeemConfirmDays()) {
			//Avoid reduplicative submitting, 2018-03-07
			if (orderEntity.getPublisherOffset() == null) {
				orderEntity.setPublisherOffset(this.publisherOffsetService.getLatestOffset(orderEntity,
						this.orderDateService.getRedeemConfirmDate(orderEntity)));
				productOffsetService.offset(orderEntity.getPublisherBaseAccount(), orderEntity, true);
			}
			orderEntity.setPublisherClearStatus(InvestorTradeOrderEntity.TRADEORDER_publisherClearStatus_toClear);
			orderEntity.setPublisherConfirmStatus(InvestorTradeOrderEntity.TRADEORDER_publisherConfirmStatus_toConfirm);
			orderEntity.setPublisherCloseStatus(InvestorTradeOrderEntity.TRADEORDER_publisherCloseStatus_toClose);
		} else {
			//Avoid reduplicative submitting, 2018-03-07, for t+0 only product offset 
//			if (orderEntity.getPublisherOffset() == null) {
//				orderEntity.setPublisherOffset(publisherOffsetService.getLatestOffset(orderEntity.getPublisherBaseAccount(),
//						this.orderDateService.getRedeemConfirmDate(orderEntity)));
//				productOffsetService.offset(orderEntity.getPublisherBaseAccount(), orderEntity, true);
//			}
			orderEntity.setPublisherClearStatus(InvestorTradeOrderEntity.TRADEORDER_investorClearStatus_cleared);
		}
	
		orderEntity.setOrderStatus(InvestorTradeOrderEntity.TRADEORDER_orderStatus_accepted);
		return orderEntity;
	}
	
	private void cashFlow(InvestorTradeOrderEntity orderEntity) {
		/** 创建<<投资人-资金变动明细>> */
		investorCashFlowService.createCashFlow(orderEntity);
	}

	/**
	 * 赎回确认
	 */
	public void confirm(InvestorTradeOrderEntity orderEntity) {
		if (orderEntity.getProduct().getRedeemConfirmDays() == 0) {
			offsetService.processItem(orderEntity);
			if (InvestorTradeOrderEntity.TRADEORDER_orderType_expGoldRedeem.equals(orderEntity.getOrderType())) {
				orderEntity.setPublisherCloseStatus(InvestorTradeOrderEntity.TRADEORDER_publisherCloseStatus_closed);
			} else {
				orderEntity.setPublisherCloseStatus(InvestorTradeOrderEntity.TRADEORDER_publisherCloseStatus_closing);
			}
		    
		}
	}

	public void verification(InvestorTradeOrderEntity orderEntity) {

		if (InvestorTradeOrderEntity.TRADEORDER_orderType_normalRedeem.equals(orderEntity.getOrderType())) {

			/** 判断用户是否正常 */
			this.cacheInvestorService.isInvestorBaseAccountNormal(orderEntity.getInvestorBaseAccount().getOid());
			/** 产品相关交易约束 **/
			this.cacheProductService.checkProduct4Redeem(orderEntity);
			/** 产品赎回上限约束 **/
			this.cacheProductService.update4Redeem(orderEntity);

			/** 单人单日赎回上限、单人单日赎回次数 **/
			this.cacheHoldService.redeemDayRules(orderEntity);

			/** 个人赎回份额约束 */
			this.cacheHoldService.update4MinRedeem(orderEntity);

			/** 锁仓,判断可赎回份额是否足够 */
			this.cacheHoldService.redeemLock(orderEntity);

		}

	}
	
	private void vericationdb(InvestorTradeOrderEntity orderEntity) {
		/** 产品单日净赎回上限 **/
		this.productService.update4Redeem(orderEntity);
		
		/** 单人单日赎回上限、单人单日赎回次数 **/
		this.publisherHoldService.redeemDayRules(orderEntity);

		 /** 可赎回金额 */
        this.publisherHoldService.redeemLock(orderEntity);
	}

	
	/**
	 * 订单赎回日志记录
	 */
	@Transactional(value = TxType.REQUIRES_NEW)
	public void redeemThen(TradeOrderRep rep, String orderCode) {
		
		InvestorTradeOrderEntity orderEntity = investorTradeOrderService.findByOrderCode(orderCode);
		
		if (-1 == rep.getErrorCode()) {
			orderEntity.setOrderStatus(InvestorTradeOrderEntity.TRADEORDER_orderStatus_refused);
		} else {
			orderEntity.setOrderStatus(InvestorTradeOrderEntity.TRADEORDER_orderStatus_accepted);
		}
		investorTradeOrderService.saveEntity(orderEntity);

		OrderLogEntity orderLog = new OrderLogEntity();
		orderLog.setErrorCode(rep.getErrorCode());
		orderLog.setErrorMessage(rep.getErrorMessage());
		orderLog.setOrderType(orderEntity.getOrderType());
		orderLog.setTradeOrderOid(orderEntity.getOrderCode());
		orderLog.setOrderStatus(orderEntity.getOrderStatus());
		this.orderLogService.create(orderLog);
	}
	
	@Transactional(value = TxType.REQUIRES_NEW)
	public void redeemResubmitThen(TradeOrderRep rep, String orderCode) {
		
		InvestorTradeOrderEntity orderEntity = investorTradeOrderService.findByOrderCode(orderCode);
		
		if (-1 == rep.getErrorCode()) {
			orderEntity.setOrderStatus(InvestorTradeOrderEntity.TRADEORDER_orderStatus_refused);
		} else {
			orderEntity.setOrderStatus(InvestorTradeOrderEntity.TRADEORDER_orderStatus_submitted);
		}
		investorTradeOrderService.saveEntity(orderEntity);

		OrderLogEntity orderLog = new OrderLogEntity();
		orderLog.setErrorCode(rep.getErrorCode());
		orderLog.setErrorMessage(rep.getErrorMessage());
		orderLog.setOrderType(orderEntity.getOrderType());
		orderLog.setTradeOrderOid(orderEntity.getOrderCode());
		orderLog.setOrderStatus(orderEntity.getOrderStatus());
		this.orderLogService.create(orderLog);
	}
	
	/**
	 * 正常赎回单
	 */
	@Transactional(value = TxType.REQUIRES_NEW)
	public InvestorTradeOrderEntity createNormalRedeemTradeOrder(RedeemTradeOrderReq req) {
		InvestorTradeOrderEntity orderEntity = this.createRedeemTradeOrder(req);
		orderEntity.setOrderType(InvestorTradeOrderEntity.TRADEORDER_orderType_normalRedeem);
		orderEntity.setCreateMan(InvestorTradeOrderEntity.TRADEORDER_createMan_investor);
		orderEntity.setChannel(this.channelService.findByCid(req.getCid())); //所属渠道
		orderEntity.setProvince(req.getProvince());
		orderEntity.setCity(req.getCity());
		orderEntity.setPayAmount(orderEntity.getOrderAmount());
		return orderEntity;
	}
	
	/**
	 * 清盘赎回单
	 */
	@Transactional(value = TxType.REQUIRES_NEW)
	public InvestorTradeOrderEntity createClearRedeemTradeOrder(RedeemTradeOrderReq req) {
		InvestorTradeOrderEntity orderEntity = this.createRedeemTradeOrder(req);
		orderEntity.setOrderType(InvestorTradeOrderEntity.TRADEORDER_orderType_clearRedeem);
		orderEntity.setCreateMan(InvestorTradeOrderEntity.TRADEORDER_createMan_platform);
		orderEntity.setPayAmount(orderEntity.getOrderAmount());
		return orderEntity;
	}
	
	/**
	 * 还本付息单
	 */
	public InvestorTradeOrderEntity createCashTradeOrder(RedeemTradeOrderReq req) {
		InvestorTradeOrderEntity orderEntity = this.createRedeemTradeOrder(req);
		orderEntity.setOrderType(InvestorTradeOrderEntity.TRADEORDER_orderType_cash);
		orderEntity.setCreateMan(InvestorTradeOrderEntity.TRADEORDER_createMan_platform);
		orderEntity.setPayAmount(orderEntity.getOrderAmount());
		return orderEntity;
	}
	
	/**
	 * 募集失败退款单
	 */
	public InvestorTradeOrderEntity createCashFailTradeOrder(RedeemTradeOrderReq req) {
		InvestorTradeOrderEntity orderEntity = this.createRedeemTradeOrder(req);
		orderEntity.setOrderType(InvestorTradeOrderEntity.TRADEORDER_orderType_cashFailed);
		orderEntity.setCreateMan(InvestorTradeOrderEntity.TRADEORDER_createMan_platform);
		orderEntity.setPayAmount(req.getPayAmount());
		return orderEntity;
	}
	
	/**
	 * 体验金赎回
	 */
	public InvestorTradeOrderEntity createExpRedeemTradeOrder(RedeemTradeOrderReq req) {
		InvestorTradeOrderEntity orderEntity = this.createRedeemTradeOrder(req);
		orderEntity.setOrderType(InvestorTradeOrderEntity.TRADEORDER_orderType_expGoldRedeem);
		orderEntity.setCreateMan(InvestorTradeOrderEntity.TRADEORDER_createMan_platform);
		orderEntity.setPayAmount(orderEntity.getOrderAmount());
		return orderEntity;
	}
	
	/**
	 * 退款单
	 */
	public InvestorTradeOrderEntity createRefundTradeOrder(RedeemTradeOrderReq req) {
		InvestorTradeOrderEntity orderEntity = this.createRedeemTradeOrder(req);
		orderEntity.setOrderType(InvestorTradeOrderEntity.TRADEORDER_orderType_refund);
		orderEntity.setCreateMan(InvestorTradeOrderEntity.TRADEORDER_createMan_platform);
		orderEntity.setPayAmount(orderEntity.getOrderAmount());
		return orderEntity;
	}
	
	private InvestorTradeOrderEntity createRedeemTradeOrder(RedeemTradeOrderReq req) {
		Product product = this.productService.findByOid(req.getProductOid());
		InvestorTradeOrderEntity orderEntity = new InvestorTradeOrderEntity();
		InvestorBaseAccountEntity baseAccount = this.investorBaseAccountService.findOne(req.getUid());
		orderEntity.setInvestorBaseAccount(baseAccount); //所属投资人
		orderEntity.setPublisherBaseAccount(product.getPublisherBaseAccount()); //所属发行人
		orderEntity.setProduct(product); //所属产品
		orderEntity.setOrderCode(this.seqGeneratorService.getSeqNo(CodeConstants.PAYMENT_redeem));
		orderEntity.setOrderAmount(req.getOrderAmount());
		orderEntity.setOrderVolume(req.getOrderAmount().divide(product.getNetUnitShare()));
		orderEntity.setOrderStatus(InvestorTradeOrderEntity.TRADEORDER_orderStatus_submitted);
		orderEntity.setOrderTime(DateUtil.getSqlCurrentDate());
		orderEntity.setCheckStatus(InvestorTradeOrderEntity.TRADEORDER_checkStatus_no);
		/** The order type of origin branch, the default is plain */
		orderEntity.setWishplanOid(req.getPlanRedeemOid());
		return investorTradeOrderService.saveEntity(orderEntity);
	}
	
	public Date getRedeemDate(Product product, Timestamp orderTime) {
		Date redeemConfirmDate;
		
		boolean isT = DateUtil.isT(orderTime);
		
		//定期还为付息为0则默认按1处理
		int redeemConfirmDays = (0 == product.getRedeemConfirmDays()) ? 1 : product.getRedeemConfirmDays();
		if (!isT) {
			redeemConfirmDays = redeemConfirmDays + 1;
		}
		if (null == product.getRredeemDateType() || Product.Product_dateType_T.equals(product.getRredeemDateType())) {
			boolean isTrade = this.tradeCalendarService.isTrade(new java.sql.Date(orderTime.getTime()));
			if (isTrade) {
				
			} else {
				redeemConfirmDays = redeemConfirmDays + 1;
			}
			redeemConfirmDate = tradeCalendarService
					.nextTrade(new java.sql.Date(orderTime.getTime()), redeemConfirmDays);
		} else {
			redeemConfirmDate = DateUtil.addSQLDays(new java.sql.Date(orderTime.getTime()), redeemConfirmDays);
		}
		return redeemConfirmDate;
	}
	


}
