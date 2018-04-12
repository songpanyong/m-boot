package com.guohuai.mmp.investor.tradeorder;

import java.math.BigDecimal;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guohuai.ams.channel.ChannelService;
import com.guohuai.ams.product.Product;
import com.guohuai.ams.product.ProductService;
import com.guohuai.basic.common.SeqGenerator;
import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.cache.service.CacheHoldService;
import com.guohuai.cache.service.CacheInvestorService;
import com.guohuai.cache.service.CacheProductService;
import com.guohuai.cache.service.CacheSPVHoldService;
import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.DateUtil;
import com.guohuai.component.util.DecimalUtil;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountService;
import com.guohuai.mmp.investor.cashflow.InvestorCashFlowService;
import com.guohuai.mmp.investor.orderlog.OrderLogEntity;
import com.guohuai.mmp.investor.orderlog.OrderLogService;
import com.guohuai.mmp.investor.tradeorder.coupon.TradeOrderCouponEntity;
import com.guohuai.mmp.investor.tradeorder.coupon.TradeOrderCouponService;
import com.guohuai.mmp.jiajiacai.ebaoquan.EbaoquanRecord;
import com.guohuai.mmp.jiajiacai.ebaoquan.EbaoquanRecordService;
import com.guohuai.mmp.platform.accment.AccParam;
import com.guohuai.mmp.platform.accment.Accment;
import com.guohuai.mmp.platform.accment.TradeRequest;
import com.guohuai.mmp.platform.accment.TransferRequest;
import com.guohuai.mmp.platform.msgment.BuySuccessMailReq;
import com.guohuai.mmp.platform.msgment.BuySuccessMsgReq;
import com.guohuai.mmp.platform.msgment.MailService;
import com.guohuai.mmp.platform.msgment.MsgService;
import com.guohuai.mmp.platform.payment.PayParam;
import com.guohuai.mmp.platform.publisher.offset.OffsetService;
import com.guohuai.mmp.platform.publisher.offset.PublisherOffsetService;
import com.guohuai.mmp.platform.publisher.product.offset.ProductOffsetService;
import com.guohuai.mmp.platform.tulip.TulipService;
import com.guohuai.mmp.publisher.baseaccount.PublisherBaseAccountService;
import com.guohuai.mmp.publisher.hold.PublisherHoldEntity;
import com.guohuai.mmp.publisher.hold.PublisherHoldService;
import com.guohuai.mmp.publisher.investor.InterestFormula;
import com.guohuai.mmp.serialtask.InvestNotifyParams;
import com.guohuai.mmp.serialtask.SerialTaskEntity;
import com.guohuai.mmp.serialtask.SerialTaskReq;
import com.guohuai.mmp.serialtask.SerialTaskService;
import com.guohuai.mmp.sys.CodeConstants;
import com.guohuai.tulip.platform.facade.obj.MyCouponRep;
import com.guohuai.tulip.util.StringUtil;

@Service
@Transactional
public class InvestorInvestTradeOrderService  {
	
	
	Logger logger = LoggerFactory.getLogger(InvestorInvestTradeOrderService.class);
	@Autowired
	private InvestorBaseAccountService investorBaseAccountService;
	@Autowired
	private ChannelService channelService;
	@Autowired
	private ProductService productService;
	@Autowired
	private SeqGenerator seqGenerator;
	@Autowired
	private InvestorCashFlowService investorCashFlowService;
	@Autowired
	private PublisherHoldService publisherHoldService;
	@Autowired
	private ProductOffsetService productOffsetService;
	@Autowired
	private PublisherOffsetService publisherOffsetService;
	@Autowired
	private TulipService tulipService;
	@Autowired
	private OrderLogService orderLogService;
	@Autowired
	private InvestorTradeOrderService investorTradeOrderService;
	@Autowired
	private CacheProductService cacheProductService;
	@Autowired
	private CacheSPVHoldService cacheSPVHoldService;
	@Autowired
	private OffsetService offsetService;
	@Autowired
	private CacheHoldService cacheHoldService;
	@Autowired
	private SerialTaskService serialTaskService;
	@Autowired
	private OrderDateService orderDateService;
	@Autowired
	private CacheInvestorService cacheInvestorService;
	@Autowired
	private MsgService msgService;
	@Autowired
	private MailService mailService;
	@Autowired
	private TradeOrderCouponService tradeOrderCouponService;
	@Autowired
	private Accment accmentService;
	@Autowired
	private PublisherBaseAccountService publisherBaseAccountService;
	
	@Autowired
	private EbaoquanRecordService baoquanService;
	/** 记录投资订单日志 */
	@Transactional(value = TxType.REQUIRES_NEW)
	public void investThen(TradeOrderRep tradeOrderRep,
			String orderCode) {
		
		InvestorTradeOrderEntity orderEntity = investorTradeOrderService.findByOrderCode(orderCode);
		/** 订单失败 */
		if (0 != tradeOrderRep.getErrorCode()) {
			orderEntity.setOrderStatus(InvestorTradeOrderEntity.TRADEORDER_orderStatus_refused);
			investorTradeOrderService.saveEntity(orderEntity);
			//eBaoquan record
		} else if (orderEntity.getWishplanOid() == null) {
			int baoquanType = 0;
			if(Product.TYPE_Producttype_02.equals(orderEntity.getProduct().getType().getOid())) {
				baoquanType = EbaoquanRecord.EBAOQUAN_TYPE_INVEST_OPEN;
			} else {
				baoquanType = EbaoquanRecord.EBAOQUAN_TYPE_INVEST_CLOSE;
			}
			baoquanService.eBaoquanRecord(baoquanType, orderEntity);
		}
		
		OrderLogEntity orderLog = new OrderLogEntity();
		orderLog.setErrorCode(tradeOrderRep.getErrorCode());
		orderLog.setErrorMessage(tradeOrderRep.getErrorMessage());
		orderLog.setOrderType(orderEntity.getOrderType());
		orderLog.setTradeOrderOid(orderEntity.getOrderCode());
		orderLog.setOrderStatus(orderEntity.getOrderStatus());
		this.orderLogService.create(orderLog);
	}

	@Transactional(value = TxType.REQUIRES_NEW)
	public TradeOrderRep invest(String orderCode, TradeOrderReq tradeOrderReq) {
	
		InvestorTradeOrderEntity orderEntity = investorTradeOrderService.findByOrderCode(orderCode);
		
		TradeOrderRep tradeOrderRep = new TradeOrderRep();
		
		/** 锁定卡券 */
		this.tulipService.useCoupon(tradeOrderReq.getCouponId());
		
		verification(orderEntity);
		
		sendAccSysInvestEvent(orderEntity);
		
		serialCallBack(orderEntity);
		
		tradeOrderRep.setTradeOrderOid(orderEntity.getOid());
		return tradeOrderRep;
	}
	
	private void sendAccSysInvestEvent(InvestorTradeOrderEntity orderEntity) {
		if (InvestorTradeOrderEntity.TRADEORDER_orderType_invest.equals(orderEntity.getOrderType())) {
			if (0 == orderEntity.getProduct().getPurchaseConfirmDays()) {
				TransferRequest ireq = new TransferRequest();
				ireq.setPublisherOid(orderEntity.getPublisherBaseAccount().getOid());
				ireq.setRequestNo(StringUtil.uuid());
				ireq.setSystemSource(AccParam.SystemSource.MIMOSA.toString());
				ireq.setOrderCode(orderEntity.getOrderCode());
				ireq.setIPayNo(this.seqGenerator.next(CodeConstants.PAYMENT_investPayNo));
				ireq.setInvestorOid(orderEntity.getInvestorBaseAccount().getOid());
				ireq.setOrderType(AccParam.OrderType.INVEST.toString());
				ireq.setOrderAmount(orderEntity.getPayAmount());
				ireq.setVoucher(orderEntity.getOrderAmount().subtract(orderEntity.getPayAmount()));
				ireq.setOrderTime(DateUtil.format(orderEntity.getOrderTime(), DateUtil.fullDatePattern));
				ireq.setUserType(AccParam.UserType.INVESTOR.toString());
				ireq.setRemark("remark:投资人投资");
				ireq.setOrderDesc("orderDesc:订单描述");
				//Distiguish
				if (null != orderEntity.getWishplanOid()) {
					ireq.setOriginBranch(InvestorTradeOrderEntity.TRADEORDER_originBranch_whishMiddle);
				}
				BaseResp irep = accmentService.transfer(ireq);
				if (0 != irep.getErrorCode()) {
					throw new AMPException(irep.getErrorMessage());
				} else {
					this.investorBaseAccountService.updateBalance(orderEntity.getInvestorBaseAccount());
					publisherBaseAccountService.updateBalance(orderEntity.getPublisherBaseAccount());
					
				}
			} else {
				TradeRequest ireq = new TradeRequest();
				ireq.setUserOid(orderEntity.getInvestorBaseAccount().getMemberId());
				ireq.setPublisherMemeberId(orderEntity.getPublisherBaseAccount().getMemberId());
				ireq.setUserType(AccParam.UserType.INVESTOR.toString());
				ireq.setOrderType(AccParam.OrderType.INVEST.toString());
				ireq.setBalance(orderEntity.getPayAmount());
				ireq.setVoucher(orderEntity.getOrderAmount().subtract(orderEntity.getPayAmount()));
				ireq.setRemark("投资人投资");
				ireq.setOrderNo(orderEntity.getOrderCode());
				ireq.setIPayNo(this.seqGenerator.next(CodeConstants.PAYMENT_investPayNo));
				ireq.setOrderTime(DateUtil.format(orderEntity.getOrderTime(), DateUtil.fullDatePattern));
				//Distiguish
				if (null != orderEntity.getWishplanOid()) {
					ireq.setOriginBranch(InvestorTradeOrderEntity.TRADEORDER_originBranch_whishMiddle);
				}
				BaseResp irep = accmentService.trade(ireq);
				if (0 != irep.getErrorCode()) {
					throw new AMPException(irep.getErrorMessage());
				} else {
					this.investorBaseAccountService.updateBalance(orderEntity.getInvestorBaseAccount());
					publisherBaseAccountService.updateBalance(orderEntity.getPublisherBaseAccount());
				}
			}
		}
	}

	private void serialCallBack(InvestorTradeOrderEntity orderEntity) {
		if (InvestorTradeOrderEntity.TRADEORDER_orderType_expGoldInvest.equals(orderEntity.getOrderType())
				|| InvestorTradeOrderEntity.TRADEORDER_orderType_writeOff.equals(orderEntity.getOrderType())
			|| InvestorTradeOrderEntity.TRADEORDER_orderType_reInvest.equals(orderEntity.getOrderType())
			|| InvestorTradeOrderEntity.TRADEORDER_orderType_invest.equals(orderEntity.getOrderType())) {
			InvestNotifyParams params = new InvestNotifyParams();
			params.setOrderCode(orderEntity.getOrderCode());
			params.setReturnCode(PayParam.ReturnCode.RC0000.toString());
			SerialTaskReq<InvestNotifyParams> req = new SerialTaskReq<InvestNotifyParams>();
			req.setTaskParams(params);
			req.setTaskCode(SerialTaskEntity.TASK_taskCode_invest);
			serialTaskService.createSerialTask(req);
		}
	}


	/**
	 * 校验
	 */
	public void verification(InvestorTradeOrderEntity orderEntity) {
		
		//Filter out of the wishplan
		if (orderEntity.getWishplanOid() == null) {
			this.investorBaseAccountService.balanceEnoughInvest(orderEntity.getInvestorBaseAccount(), orderEntity.getPayAmount());
		}
		
//		/** 判断用户是否正常 */
//		this.cacheInvestorService.isInvestorBaseAccountNormal(orderEntity.getInvestorBaseAccount().getOid());
		
		/** 校验<<产品>> */
		this.cacheProductService.checkProduct4Invest(orderEntity);
		
		/** 检验用户是否新手 */
		this.cacheInvestorService.isNewbie(orderEntity);

		/** 产品可售份额 */
		this.cacheProductService.updateProduct4LockCollectedVolume(orderEntity);

		/** 校验SPV持仓 */
		this.cacheSPVHoldService.checkSpvHold4Invest(orderEntity);

		/** 校验产品最大持仓 */
		cacheHoldService.checkMaxHold4Invest(orderEntity);
	}
	
	@Transactional(value = TxType.REQUIRES_NEW)
	public void investCallBack(String orderCode) {
		InvestorTradeOrderEntity orderEntity = investorTradeOrderService.findByOrderCode(orderCode);
		this.payCallback(orderEntity);
	}
	
	
	/**
	 * 更新支付状态
	 */
	private InvestorTradeOrderEntity payCallback(InvestorTradeOrderEntity orderEntity) {
		
		
		vericationdb(orderEntity);
		
		/** 记录资金明细 */
		this.cashFlow(orderEntity);

		/** 参与轧差 */
		this.offset(orderEntity);
		
		/** 入仓 */
		this.writeHold(orderEntity);
		
		/** 确认 */
		confirm(orderEntity);
		
		this.tulipService.sendInvestOK(orderEntity);
		
		this.investorTradeOrderService.saveEntity(orderEntity);
		//Filter out the wish plan
		//if (orderEntity.getWishplanOid() == null && Product.TYPE_Producttype_01.equals(orderEntity.getProduct().getType().getOid())) {
		if (orderEntity.getWishplanOid() == null) {
			BuySuccessMsgReq msgReq = new BuySuccessMsgReq();
			msgReq.setPhone(orderEntity.getInvestorBaseAccount().getPhoneNum());
			msgReq.setProductName(orderEntity.getProduct().getName());
			msgService.buysuccess(msgReq);
			
			BuySuccessMailReq mailReq = new BuySuccessMailReq();
			mailReq.setProductName(orderEntity.getProduct().getName());
			mailReq.setUserOid(orderEntity.getInvestorBaseAccount().getOid());
			mailService.buysuccess(mailReq);
		}
		
		return orderEntity;
	}
	
	

	public void vericationdb(InvestorTradeOrderEntity orderEntity) {

		/** 产品可售份额 */
		if (orderEntity.getWishplanOid() == null) {
			this.productService.updateProduct4LockCollectedVolume(orderEntity);
		}
		/** 校验SPV持仓 */
		this.publisherHoldService.checkSpvHold4Invest(orderEntity);
		/** 校验用户最大持仓 */
		this.publisherHoldService.checkMaxHold4Invest(orderEntity);
	}

	private void cashFlow(InvestorTradeOrderEntity orderEntity) {
		/** 创建<<投资人-资金变动明细>> */
		investorCashFlowService.createCashFlow(orderEntity);

	}

	private InvestorTradeOrderEntity offset(InvestorTradeOrderEntity orderEntity) {
		
		/** 参与轧差 : 订单类型是投资并或体验金投资 且投资确认日不等于0 */
		if (0 != orderEntity.getProduct().getPurchaseConfirmDays()) {
			if (InvestorTradeOrderEntity.TRADEORDER_orderType_invest.equals(orderEntity.getOrderType()) ||
					InvestorTradeOrderEntity.TRADEORDER_orderType_expGoldInvest.equals(orderEntity.getOrderType())) {
				orderEntity.setPublisherOffset(this.publisherOffsetService.getLatestOffset(orderEntity,
						this.orderDateService.getConfirmDate(orderEntity)));
				productOffsetService.offset(orderEntity.getPublisherBaseAccount(), orderEntity, true);
				orderEntity.setPublisherClearStatus(InvestorTradeOrderEntity.TRADEORDER_publisherClearStatus_toClear);
				orderEntity.setPublisherConfirmStatus(InvestorTradeOrderEntity.TRADEORDER_publisherConfirmStatus_toConfirm);
				orderEntity.setPublisherCloseStatus(InvestorTradeOrderEntity.TRADEORDER_publisherCloseStatus_toClose);
			}
			
		} else {
			//2018-03-07, for t+0 only product offset 
//			if (InvestorTradeOrderEntity.TRADEORDER_orderType_invest.equals(orderEntity.getOrderType()) ||
//					InvestorTradeOrderEntity.TRADEORDER_orderType_expGoldInvest.equals(orderEntity.getOrderType())) {
//				orderEntity.setPublisherOffset(publisherOffsetService.getLatestOffset(orderEntity.getPublisherBaseAccount(),
//						this.orderDateService.getConfirmDate(orderEntity)));
//				productOffsetService.offset(orderEntity.getPublisherBaseAccount(), orderEntity, true);
//			}
		}
		orderEntity.setOrderStatus(InvestorTradeOrderEntity.TRADEORDER_orderStatus_accepted);

		return orderEntity;
	}

	/**
	 * 确认份额
	 */
	private void writeHold(InvestorTradeOrderEntity orderEntity) {

		// 分仓
		
		
		
		/** 创建或更新 <<发行人-持有人手册>> */
		orderEntity.setHoldVolume(orderEntity.getOrderVolume()); // 持有份额
		orderEntity.setValue(orderEntity.getOrderVolume());
		orderEntity.setBeginAccuralDate(
				this.orderDateService.getBeginAccuralDate(orderEntity));
		orderEntity.setBeginRedeemDate(this.orderDateService.getBeginRedeemDate(orderEntity));
		orderEntity.setCorpusAccrualEndDate(this.orderDateService.getCorpusAccrualEndDate(orderEntity));

		orderEntity.setRedeemStatus(InvestorTradeOrderEntity.TRADEORDER_redeemStatus_no);
		orderEntity.setAccrualStatus(InvestorTradeOrderEntity.TRADEORDER_accrualStatus_no);
		orderEntity.setHoldStatus(InvestorTradeOrderEntity.TRADEORDER_holdStatus_toConfirm); // 待确认状态
		
		BigDecimal expectIncome = getExpectIncome(orderEntity, orderEntity.getProduct().getExpAror());
		BigDecimal expectIncomeExt = getExpectIncome(orderEntity, orderEntity.getProduct().getExpArorSec());
		orderEntity.setExpectIncome(expectIncome);
		orderEntity.setExpectIncomeExt(expectIncomeExt);
		
		
		PublisherHoldEntity hold = publisherHoldService.invest(orderEntity);
		orderEntity.setPublisherHold(hold); // 所属持有人手册
	}

	public BigDecimal getExpectIncome(InvestorTradeOrderEntity orderEntity, BigDecimal annualInterestRate) {
		BigDecimal expectIncome = BigDecimal.ZERO;
		
		if (Product.TYPE_Producttype_01.equals(orderEntity.getProduct().getType().getOid())) {
			
			if (null != orderEntity.getProduct().getRewardInterest()) {
				annualInterestRate = annualInterestRate.add(DecimalUtil.zoomIn(orderEntity.getProduct().getRewardInterest(), 100));
			}
			
			/** 保留4位 */
			expectIncome = InterestFormula.simple(orderEntity.getHoldVolume(), 
					annualInterestRate, orderEntity.getProduct().getIncomeCalcBasis(), orderEntity.getProduct().getDurationPeriodDays());
			
			/** 保留2位*/
			expectIncome = expectIncome.add(orderEntity.getRemainderBaseIncome()).setScale(DecimalUtil.scale, DecimalUtil.roundMode);
			if (InvestorTradeOrderEntity.TRADEORDER_usedCoupons_yes.equals(orderEntity.getUsedCoupons())) {
				TradeOrderCouponEntity couponEntity = this.tradeOrderCouponService.findByInvestorTradeOrder(orderEntity);
				if (TradeOrderCouponEntity.TRADEORDERCOUPON_type_rateCoupon.equals(couponEntity.getCouponType())) {
					int effectiveDays = Math.min(orderEntity.getProduct().getDurationPeriodDays(), couponEntity.getAffectiveDays());
					BigDecimal couponIncome = InterestFormula.simple(orderEntity.getHoldVolume(), 
							DecimalUtil.zoomIn(couponEntity.getAdditionalInterestRate(), 100), orderEntity.getProduct().getIncomeCalcBasis(), effectiveDays)
							 .setScale(DecimalUtil.scale, DecimalUtil.roundMode);
					expectIncome = expectIncome.add(couponIncome);
				}
			}
		}
		return expectIncome;
	}
	
	/**
	 * 体验金投资单、投资单并且申购确认日等于0
	 *  冲销单份额直接确认 
	 */
	private void confirm(InvestorTradeOrderEntity orderEntity) {
		if (InvestorTradeOrderEntity.TRADEORDER_orderType_writeOff.equals(orderEntity.getOrderType())) {
			offsetService.processItem(orderEntity);
			return;
		}
		
		if (0 == orderEntity.getProduct().getPurchaseConfirmDays()) {
			if ((InvestorTradeOrderEntity.TRADEORDER_orderType_expGoldInvest.equals(orderEntity.getOrderType())
					|| InvestorTradeOrderEntity.TRADEORDER_orderType_invest.equals(orderEntity.getOrderType()))) {
				offsetService.processItem(orderEntity);
			}
		}
	}
	
	
	/**
	 * 体验金投资
	 */
	@Transactional(value = TxType.REQUIRES_NEW)
	public InvestorTradeOrderEntity createExpGoldInvestTradeOrder(TradeOrderReq tradeOrderReq) {
		InvestorTradeOrderEntity orderEntity = createInvestTradeOrder(tradeOrderReq);
		orderEntity.setOrderType(InvestorTradeOrderEntity.TRADEORDER_orderType_expGoldInvest);
		orderEntity.setCreateMan(InvestorTradeOrderEntity.TRADEORDER_createMan_platform);
		return orderEntity;
	}
	
	/**
	 * 正常投资
	 */
	@Transactional(value = TxType.REQUIRES_NEW)
	public InvestorTradeOrderEntity createNormalInvestTradeOrder(TradeOrderReq tradeOrderReq) {
		InvestorTradeOrderEntity orderEntity = createInvestTradeOrder(tradeOrderReq);
		orderEntity.setOrderType(InvestorTradeOrderEntity.TRADEORDER_orderType_invest);
		orderEntity.setCreateMan(InvestorTradeOrderEntity.TRADEORDER_createMan_investor);
		orderEntity.setChannel(this.channelService.findByCid(tradeOrderReq.getCid()));
		return orderEntity;
	}
	
	/**
	 * 创建冲销单
	 */
	@Transactional(value = TxType.REQUIRES_NEW)
	public InvestorTradeOrderEntity createWriteOffTradeOrder(TradeOrderReq tradeOrderReq) {
		InvestorTradeOrderEntity orderEntity = createInvestTradeOrder(tradeOrderReq);
		orderEntity.setOrderType(InvestorTradeOrderEntity.TRADEORDER_orderType_writeOff);
		orderEntity.setCreateMan(InvestorTradeOrderEntity.TRADEORDER_createMan_platform); // 订单创建人
		return orderEntity;
	}
	
	
	/**
	 * 设置卡券相关属性
	 */
	private void setCouponProperties(InvestorTradeOrderEntity order, TradeOrderReq tradeOrderReq) {
		// 没有使用卡券时,实付金额等于订单金额
		if (this.tulipService.isSdkEnable() && this.tulipService.isUseCoupon(tradeOrderReq.getCouponId())) {
//			orderEntity.setCoupons(tradeOrderReq.getCouponId());// 卡券编号
//			orderEntity.setCouponType(tradeOrderReq.getCouponType());// 卡券类型
//			orderEntity.setCouponAmount(tradeOrderReq.getCouponDeductibleAmount());// 卡券实际抵扣金额
//			 加息券 payAmouont = moneyVolume, 
//			couponDeductibleAmount = couponAmount = 2（表示2%加息）
//			优惠券 couponDeductibleAmount + payAmount = moneyVolume, payAmount > 0, 
//			 couponAmount >= couponDeductibleAmount
//			体验金 couponDeductibleAmount = couponAmount = moneyVolume,
//			payAmouont = 0
					 
			// 投资人-交易委托单-卡券
			TradeOrderCouponEntity coupon = new TradeOrderCouponEntity();
			coupon.setInvestorBaseAccount(order.getInvestorBaseAccount());
			coupon.setInvestorTradeOrder(order);
			coupon.setCouponAmount(tradeOrderReq.getCouponDeductibleAmount());	// 卡券金额    是实际卡券抵扣金额
			coupon.setCoupons(tradeOrderReq.getCouponId());
			coupon.setCouponType(tradeOrderReq.getCouponType());
			if (tradeOrderReq.getCouponType() != null && tradeOrderReq.getCouponType().equals(TradeOrderCouponEntity.TRADEORDERCOUPON_type_rateCoupon)){
				coupon.setAdditionalInterestRate(tradeOrderReq.getCouponAmount());	// 加息收益率，如果是加息券才有
				
				if (order.getProduct().getType().getOid().equals(Product.TYPE_Producttype_02)){
					coupon.setDAdditionalInterestRate(InterestFormula.caclDayInterest(coupon.getAdditionalInterestRate().divide(new BigDecimal(100)),
						Integer.parseInt(order.getProduct().getIncomeCalcBasis())));
				}else{
					coupon.setDAdditionalInterestRate(coupon.getAdditionalInterestRate().divide(new BigDecimal(100)).divide(new BigDecimal(order.getProduct().getIncomeCalcBasis()),15, BigDecimal.ROUND_HALF_UP));
				}
			}
			
			MyCouponRep couponRep = tulipService.getCouponDetail(tradeOrderReq.getCouponId());
			
			coupon.setAffectiveDays(couponRep.getValidPeriod());
			
			tradeOrderCouponService.saveEntity(coupon);
			
			order.setUsedCoupons(InvestorTradeOrderEntity.TRADEORDER_usedCoupons_yes);
			order.setPayAmount(tradeOrderReq.getPayAmouont());// 实际支付金额
		} else {
			order.setUsedCoupons(InvestorTradeOrderEntity.TRADEORDER_usedCoupons_no);
			order.setPayAmount(order.getOrderAmount());// 实际支付金额
		}
	}

	/**
	 * 创建投资者投资订单
	 */
	private InvestorTradeOrderEntity createInvestTradeOrder(TradeOrderReq tradeOrderReq) {

		Product product = this.productService.findByOid(tradeOrderReq.getProductOid());

		InvestorTradeOrderEntity orderEntity = new InvestorTradeOrderEntity();
		orderEntity.setPublisherBaseAccount(product.getPublisherBaseAccount());
		orderEntity.setInvestorBaseAccount(this.investorBaseAccountService.findOne(tradeOrderReq.getUid()));
		orderEntity.setProduct(product);
		orderEntity.setOrderCode(this.seqGenerator.next(CodeConstants.PAYMENT_invest));
		orderEntity.setOrderAmount(tradeOrderReq.getMoneyVolume());
		orderEntity.setOrderVolume(tradeOrderReq.getMoneyVolume().divide(product.getNetUnitShare()));
		orderEntity.setOrderStatus(InvestorTradeOrderEntity.TRADEORDER_orderStatus_submitted);
		orderEntity.setContractStatus(InvestorTradeOrderEntity.TRADEORDER_contractStatus_toHtml);
		orderEntity.setCheckStatus(InvestorTradeOrderEntity.TRADEORDER_checkStatus_no);
		orderEntity.setOrderTime(DateUtil.getSqlCurrentDate()); // 订单时间
		//Distinguish the whishpaln with the plain oder
//		if(tradeOrderReq.getClass().equals(PlanProductForm.class)){
		orderEntity.setWishplanOid(tradeOrderReq.getPlanRedeemOid());
//		orderEntity.setOriginBranch(tradeOrderReq.getOriginBranch());
//		} 
		orderEntity = investorTradeOrderService.saveEntity(orderEntity);
		
		setCouponProperties(orderEntity, tradeOrderReq);
		
		return investorTradeOrderService.saveEntity(orderEntity);
	}

}