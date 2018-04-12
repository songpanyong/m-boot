package com.guohuai.mmp.investor.tradeorder;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.guohuai.ams.label.LabelEnum;
import com.guohuai.ams.label.LabelService;
import com.guohuai.ams.product.Product;
import com.guohuai.ams.product.ProductDao;
import com.guohuai.ams.product.reward.ProductIncomeReward;
import com.guohuai.ams.product.reward.ProductIncomeRewardCacheService;
import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.basic.component.ext.web.PageResp;
import com.guohuai.cache.entity.HoldCacheEntity;
import com.guohuai.cache.entity.ProductCacheEntity;
import com.guohuai.cache.service.CacheHoldService;
import com.guohuai.cache.service.CacheProductService;
import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.DateUtil;
import com.guohuai.component.util.DecimalUtil;
import com.guohuai.component.util.StringUtil;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountEntity;
import com.guohuai.mmp.investor.orderlog.OrderLogService;
import com.guohuai.mmp.investor.sonaccount.SonAccountDao;
import com.guohuai.mmp.investor.sonaccount.SonAccountEntity;
import com.guohuai.mmp.investor.tradeorder.coupon.TradeOrderCouponEntity;
import com.guohuai.mmp.investor.tradeorder.coupon.TradeOrderCouponService;
import com.guohuai.mmp.jiajiacai.wishplan.plan.dao.PlanInvestDao;
import com.guohuai.mmp.jiajiacai.wishplan.plan.dao.PlanMonthDao;
import com.guohuai.mmp.jiajiacai.wishplan.plan.dao.PlanProductDao;
import com.guohuai.mmp.jiajiacai.wishplan.plan.entity.PlanInvestEntity;
import com.guohuai.mmp.jiajiacai.wishplan.plan.entity.PlanMonthEntity;
import com.guohuai.mmp.jiajiacai.wishplan.planlist.PlanListDao;
import com.guohuai.mmp.job.JobEnum;
import com.guohuai.mmp.job.lock.JobLockService;
import com.guohuai.mmp.job.log.JobLogEntity;
import com.guohuai.mmp.job.log.JobLogFactory;
import com.guohuai.mmp.job.log.JobLogService;
import com.guohuai.mmp.platform.investor.offset.InvestorOffsetEntity;
import com.guohuai.mmp.platform.payment.log.PayLogEntity;
import com.guohuai.mmp.platform.payment.log.PayLogService;
import com.guohuai.mmp.platform.publisher.dividend.offset.DividendOffsetEntity;
import com.guohuai.mmp.platform.publisher.offset.PublisherOffsetEntity;
import com.guohuai.mmp.publisher.baseaccount.PublisherBaseAccountEntity;
import com.guohuai.mmp.publisher.hold.PublisherHoldEntity;
import com.guohuai.mmp.publisher.holdapart.closedetails.CloseDetailsEntity;
import com.guohuai.mmp.publisher.holdapart.closedetails.CloseDetailsService;
import com.guohuai.mmp.publisher.product.agreement.ProductAgreementEntity;
import com.guohuai.mmp.publisher.product.agreement.ProductAgreementService;
import com.guohuai.mmp.sys.SysConstant;

@Service
@Transactional
public class InvestorTradeOrderService {
	
	Logger logger = LoggerFactory.getLogger(InvestorTradeOrderService.class);
	@Value("${investor.info.kickstar:true}")
	private boolean kickstar = true;
	@Autowired
	private InvestorTradeOrderDao investorTradeOrderDao;
	@Autowired
	private CloseDetailsService closeDetailsService;
	@Autowired
	private OrderLogService orderLogService;
	@Autowired
	private ProductIncomeRewardCacheService productIncomeRewardCacheService;
	@Autowired
	private PayLogService payLogService;
	@Autowired
	private JobLockService jobLockService;
	@Autowired
	private JobLogService jobLogService;
	@Autowired
	private OrderDateService orderDateService;
	@Autowired
	private InvestorTradeOrderRequireNewService investorTradeOrderRequireNewService;
	@Autowired
	private ProductAgreementService productAgreementService;
	@Autowired
	private TradeOrderCouponService tradeOrderCouponService;
	@Autowired
	private InvestorTradeOrderBatchPayService investorTradeOrderBatchPayService;
	@Autowired
	private LabelService labelService;
	@Autowired
	private CacheHoldService cacheHoldService;
	@Autowired
	private CacheProductService cacheProductService;
	@Autowired
	private SonAccountDao sonAccountDao;
	
	@Autowired
	private PlanInvestDao planInvestDao;
	
	@Autowired
	private PlanProductDao planProductDao;
	
	@Autowired
	private ProductDao productDao;
	
	@Autowired
	private PlanListDao planListDao;
	
	@Autowired
	private PlanMonthDao planMonthDao;

	
	/**
	 * 第三方支付购买成功回调
	 */
	public static final String PAYMENT_trade_finished  = "TRADE_FINISHED";
	public static final String PAYMENT_trade_failed = "TRADE_FAILED";
	
	/**
	 * 第三方支付购买成功回调
	 */
	public static final String PAYMENT_pay_finished  = "PAY_FINISHED";
	
	public static final String PAYMENT_apply_success = "APPLY_SUCCESS";
	
	
	/**
	 * 一次批量赎回量不能超过300
	 */
	public static final int MAX_REDEMPTION_TIMES = 300;
	
	public InvestorTradeOrderEntity saveEntity(InvestorTradeOrderEntity orderEntity) {
		return investorTradeOrderDao.save(orderEntity);
	}
	
	@Transactional(value = TxType.REQUIRES_NEW)
	public InvestorTradeOrderEntity saveEntityNewTrans(InvestorTradeOrderEntity orderEntity) {
		return investorTradeOrderDao.save(orderEntity);
		
	}

	public List<InvestorTradeOrderEntity> findAll(Specification<InvestorTradeOrderEntity> spec){
		List<InvestorTradeOrderEntity> list = this.investorTradeOrderDao.findAll(spec);	
		return list;
	}
	
	public Page<InvestorTradeOrderEntity> findPage(Specification<InvestorTradeOrderEntity> spec, Pageable pageable){
		Page<InvestorTradeOrderEntity> page = this.investorTradeOrderDao.findAll(spec, pageable);	
		return page;
	}
	
	public InvestorTradeOrderEntity findOne(String oid) {
		InvestorTradeOrderEntity orderEntity = this.investorTradeOrderDao.findOne(oid);
		if (null == orderEntity) {
			//error.define[80008]=投资人-交易委托单：订单号不存在!(CODE:80008)
			throw new AMPException(80008);
		}
		return orderEntity;
	}

	
	public InvestorTradeOrderEntity findByOrderCode(String orderCode) {
		InvestorTradeOrderEntity tradeOrder = this.investorTradeOrderDao.findByOrderCode(orderCode);
		if (null == tradeOrder) {
			//error.define[80008]=投资人-交易委托单：订单号不存在!(CODE:80008)
			throw new AMPException(80008);
		}
		return tradeOrder;
	}
	
//	/**
//	 * 获取待结算订单
//	 */
//	public List<InvestorTradeOrderEntity> findToCloseOrders(InvestorOffsetEntity offset, String investorOid, String lastOid){
//		return this.investorTradeOrderDao.findToCloseOrders(offset.getOid(), investorOid, lastOid);
//	}
	
	/**
	 * 获取待结算订单
	 */
	public List<InvestorTradeOrderEntity> findToCloseOrders(PublisherOffsetEntity offset, String lastOid){
		return this.investorTradeOrderDao.findToCloseOrders(offset.getOid(), lastOid);
	}
	
//	/**
//	 * 获取待结算平台订单
//	 */
//	public List<InvestorTradeOrderEntity> findToClosePlatformOrders(InvestorOffsetEntity offset, String investorOid){
//		return this.investorTradeOrderDao.findToClosePlatformOrders(offset.getOid(), investorOid);
//	}
//	
	
	/**
	 * 订单查询 
	 */
	public PageResp<TradeOrderQueryRep> investorTradeOrderMng(Specification<InvestorTradeOrderEntity> spec, Pageable pageable, boolean isCms) {
		Page<InvestorTradeOrderEntity> cas = this.investorTradeOrderDao.findAll(spec, pageable);
		PageResp<TradeOrderQueryRep> pagesRep = new PageResp<TradeOrderQueryRep>();
		if (cas != null && cas.getContent() != null && cas.getTotalElements() > 0) {
			for (InvestorTradeOrderEntity tradeOrder : cas) {
				if (!isCms && (InvestorTradeOrderEntity.TRADEORDER_orderType_wishInvest.equals(tradeOrder.getOrderType())
					|| InvestorTradeOrderEntity.TRADEORDER_orderType_wishRedeem.equals(tradeOrder.getOrderType()))) {
					renderWishOrder(pagesRep, tradeOrder);
				} else {
					renderPlainOrder(pagesRep, tradeOrder);
				}
			}
		}
		pagesRep.setTotal(cas.getTotalElements());
		return pagesRep;
	}
	
	private void renderWishOrder(PageResp<TradeOrderQueryRep> pagesRep, InvestorTradeOrderEntity tradeOrder) {
		TradeOrderQueryRep queryRep = new TradeOrderQueryRep();
		queryRep.setTradeOrderOid(tradeOrder.getOid()); // OID
		PlanInvestEntity planEntity = planInvestDao.findByOid(tradeOrder.getWishplanOid());
		String planName = null;
		if (null != planEntity) {
			planName = planListDao.findPlanNameByType(planEntity.getPlanType());
		} else {
			PlanMonthEntity planMonth = planMonthDao.findByOid(tradeOrder.getWishplanOid());
			planName = planListDao.findPlanNameByType(planMonth.getPlanType());
		}
		queryRep.setProductOid(tradeOrder.getWishplanOid()); // 产品UUID
		queryRep.setProductName(planName); // 产品名称
		if (null != tradeOrder.getChannel()) {
			queryRep.setChannelOid(tradeOrder.getChannel().getOid());
			queryRep.setChannelName(tradeOrder.getChannel().getChannelName());
		}
		/** 原国槐的代码 */
		// queryRep.setPhoneNum(this.kickstar ?
		// StringUtil.kickstarOnPhoneNum(tradeOrder.getInvestorBaseAccount().getPhoneNum())
		// : tradeOrder.getInvestorBaseAccount().getPhoneNum());
		/**
		 * 家加财新增代码
		 * 
		 * 将投资记录子账户的手机号变为昵称
		 */
		if (tradeOrder.getInvestorBaseAccount().getPhoneNum().length() != 11) {
			// 子账户

			SonAccountEntity sonAccountEntity = this.sonAccountDao
					.findBySid(tradeOrder.getInvestorBaseAccount().getOid());
			if (sonAccountEntity != null) {
				queryRep.setPhoneNum(this.kickstarOnNickName(sonAccountEntity.getNickname()));
			}
		} else {
			// 主账户
			queryRep.setPhoneNum(
					this.kickstar ? StringUtil.kickstarOnPhoneNum(tradeOrder.getInvestorBaseAccount().getPhoneNum())
							: tradeOrder.getInvestorBaseAccount().getPhoneNum());
		}

		queryRep.setOrderCode(tradeOrder.getOrderCode()); // 订单号
		queryRep.setOrderType(tradeOrder.getOrderType()); // 订单类型
		queryRep.setOrderTypeDisp(TradeUtil.orderTypeEn2Ch(tradeOrder.getOrderType()));
		queryRep.setOrderAmount(tradeOrder.getOrderAmount()); // 订单金额
		queryRep.setOrderVolume(tradeOrder.getOrderVolume()); // 订单份额
		queryRep.setOrderStatus(tradeOrder.getOrderStatus());
		queryRep.setOrderStatusDisp(TradeUtil.orderStatusEn2Ch(tradeOrder.getOrderStatus())); // 订单状态
		queryRep.setContractStatus(tradeOrder.getContractStatus());
		queryRep.setContractStatusDisp(TradeUtil.contractStatusEn2Ch(tradeOrder.getContractStatus()));
		queryRep.setCreateMan(tradeOrder.getCreateMan()); // 订单创建人
		queryRep.setCreateManDisp(TradeUtil.createManEn2Ch(tradeOrder.getCreateMan())); // 订单创建人disp
		queryRep.setOrderTime(tradeOrder.getOrderTime());
		queryRep.setCompleteTime(tradeOrder.getCompleteTime()); // 订单完成时间
		queryRep.setPublisherClearStatus(tradeOrder.getPublisherClearStatus());
		queryRep.setPublisherClearStatusDisp(TradeUtil.publisherClearStatusEn2Ch(tradeOrder.getPublisherClearStatus()));
		queryRep.setPublisherConfirmStatus(tradeOrder.getPublisherConfirmStatus());
		queryRep.setPublisherConfirmStatusDisp(
				TradeUtil.publisherConfirmStatusEn2Ch(tradeOrder.getPublisherConfirmStatus()));
		queryRep.setPublisherCloseStatus(tradeOrder.getPublisherCloseStatus());
		queryRep.setPublisherCloseStatusDisp(TradeUtil.publisherCloseStatusEn2Ch(tradeOrder.getPublisherCloseStatus()));

		if (InvestorTradeOrderEntity.TRADEORDER_usedCoupons_yes.equals(tradeOrder.getUsedCoupons())) {
			TradeOrderCouponEntity coupon = tradeOrderCouponService.findByInvestorTradeOrder(tradeOrder);
			if (coupon != null) {
				queryRep.setCouponType(coupon.getCouponType());
				queryRep.setCouponTypeDisp(tradeOrderCouponService.couponTypeEn2Ch(coupon.getCouponType()));

				if (coupon.getCouponType().equals(TradeOrderCouponEntity.TRADEORDERCOUPON_type_rateCoupon)) {
					queryRep.setCouponAmount(coupon.getAdditionalInterestRate() + "%"); // 卡券加息率
				} else {
					queryRep.setCouponAmount(coupon.getCouponAmount().toString() + "元"); // 卡券面值
				}
			}
		}

		queryRep.setPayAmount(tradeOrder.getPayAmount());
		queryRep.setHoldVolume(tradeOrder.getHoldVolume()); // 持有份额
		queryRep.setRedeemStatus(tradeOrder.getRedeemStatus()); // 可赎回状态
		queryRep.setRedeemStatusDisp(TradeUtil.redeemStatusEn2Ch(tradeOrder.getRedeemStatus()));
		queryRep.setAccrualStatus(tradeOrder.getAccrualStatus()); // 可计息状态
		queryRep.setAccrualStatusDisp(TradeUtil.accrualStatusEn2Ch(tradeOrder.getAccrualStatus()));
		queryRep.setBeginAccuralDate(tradeOrder.getBeginAccuralDate()); // 起息日
		queryRep.setBeginRedeemDate(tradeOrder.getBeginRedeemDate()); // 起始赎回日
		queryRep.setTotalIncome(tradeOrder.getTotalIncome()); // 累计收益
		queryRep.setTotalBaseIncome(tradeOrder.getTotalBaseIncome()); // 累计基础收益
		queryRep.setTotalRewardIncome(tradeOrder.getTotalRewardIncome()); // 累计奖励收益
		queryRep.setYesterdayBaseIncome(tradeOrder.getYesterdayBaseIncome());
		queryRep.setYesterdayRewardIncome(tradeOrder.getYesterdayRewardIncome());
		queryRep.setYesterdayIncome(tradeOrder.getYesterdayIncome());
		queryRep.setIncomeAmount(tradeOrder.getIncomeAmount()); // 定期收益
		queryRep.setExpectIncome(tradeOrder.getExpectIncome());
		queryRep.setExpectIncomeExt(tradeOrder.getExpectIncomeExt());
		queryRep.setValue(tradeOrder.getValue());
		queryRep.setHoldStatus(tradeOrder.getHoldStatus());
		queryRep.setHoldStatusDisp(TradeUtil.holdStatusEn2Ch(tradeOrder.getHoldStatus()));
		queryRep.setConfirmDate(tradeOrder.getConfirmDate());
//		queryRep.setBaseIncomeRatio(tradeOrder.getProduct().getBasicRatio()); // 基础收益率
		if (InvestorTradeOrderEntity.TRADEORDER_contractStatus_pdfOK.equals(tradeOrder.getContractStatus())) {
			queryRep.setInvestContractAddr(productAgreementService.findByInvestorTradeOrderAndAgreementType(tradeOrder,
					ProductAgreementEntity.Agreement_agreementType_investing).getAgreementUrl());
			queryRep.setServiceContractAddr(productAgreementService.findByInvestorTradeOrderAndAgreementType(tradeOrder,
					ProductAgreementEntity.Agreement_agreementType_service).getAgreementUrl());
		}
		if (null != tradeOrder.getBeginAccuralDate()) {
			long holdDays = DateUtil.daysBetween(DateUtil.getSqlDate(), tradeOrder.getBeginAccuralDate()) + 1;
			if (holdDays > 0) {
				queryRep.setHoldDays(holdDays);
				ProductIncomeReward w = this.productIncomeRewardCacheService
						.getRewardEntity(tradeOrder.getProduct().getOid(), (int) holdDays);
				if (null != w) {
					queryRep.setRewardIncomeRatio(w.getRatio()); // 奖励收益率
					queryRep.setRewardIncomeLevel(w.getLevel()); // 奖励阶梯
				}
			}
		}

		queryRep.setUpdateTime(tradeOrder.getUpdateTime());
		queryRep.setCreateTime(tradeOrder.getCreateTime()); // 订单创建时间

		pagesRep.getRows().add(queryRep);

	}
	
	private void renderPlainOrder(PageResp<TradeOrderQueryRep> pagesRep, InvestorTradeOrderEntity tradeOrder) {
		TradeOrderQueryRep queryRep = new TradeOrderQueryRep();
		queryRep.setTradeOrderOid(tradeOrder.getOid()); // OID
		queryRep.setProductOid(tradeOrder.getProduct().getOid()); // 产品UUID
		
		if (tradeOrder.getWishplanOid() != null) {
			String planName = planProductDao.findPlanName(tradeOrder.getWishplanOid());
			queryRep.setProductName(planName + ":" + tradeOrder.getProduct().getName()); // 产品名称
		} else {
			queryRep.setProductName(tradeOrder.getProduct().getName()); // 产品名称
		}
		
		if (null != tradeOrder.getChannel()) {
			queryRep.setChannelOid(tradeOrder.getChannel().getOid());
			queryRep.setChannelName(tradeOrder.getChannel().getChannelName());
		}
		/** 原国槐的代码 */
		// queryRep.setPhoneNum(this.kickstar ?
		// StringUtil.kickstarOnPhoneNum(tradeOrder.getInvestorBaseAccount().getPhoneNum())
		// : tradeOrder.getInvestorBaseAccount().getPhoneNum());
		/**
		 * 家加财新增代码
		 * 
		 * 将投资记录子账户的手机号变为昵称
		 */
		if (tradeOrder.getInvestorBaseAccount().getPhoneNum().length() != 11) {
			// 子账户

			SonAccountEntity sonAccountEntity = this.sonAccountDao
					.findBySid(tradeOrder.getInvestorBaseAccount().getOid());
			if (sonAccountEntity != null) {
				queryRep.setPhoneNum(this.kickstarOnNickName(sonAccountEntity.getNickname()));
			}
		} else {
			// 主账户
			queryRep.setPhoneNum(
					this.kickstar ? StringUtil.kickstarOnPhoneNum(tradeOrder.getInvestorBaseAccount().getPhoneNum())
							: tradeOrder.getInvestorBaseAccount().getPhoneNum());
		}

		queryRep.setOrderCode(tradeOrder.getOrderCode()); // 订单号
		queryRep.setOrderType(tradeOrder.getOrderType()); // 订单类型
		queryRep.setOrderTypeDisp(TradeUtil.orderTypeEn2Ch(tradeOrder.getOrderType()));
		queryRep.setOrderAmount(tradeOrder.getOrderAmount()); // 订单金额
		queryRep.setOrderVolume(tradeOrder.getOrderVolume()); // 订单份额
		queryRep.setOrderStatus(tradeOrder.getOrderStatus());
		queryRep.setOrderStatusDisp(TradeUtil.orderStatusEn2Ch(tradeOrder.getOrderStatus())); // 订单状态
		queryRep.setContractStatus(tradeOrder.getContractStatus());
		queryRep.setContractStatusDisp(TradeUtil.contractStatusEn2Ch(tradeOrder.getContractStatus()));
		queryRep.setCreateMan(tradeOrder.getCreateMan()); // 订单创建人
		queryRep.setCreateManDisp(TradeUtil.createManEn2Ch(tradeOrder.getCreateMan())); // 订单创建人disp
		queryRep.setOrderTime(tradeOrder.getOrderTime());
		queryRep.setCompleteTime(tradeOrder.getCompleteTime()); // 订单完成时间
		queryRep.setPublisherClearStatus(tradeOrder.getPublisherClearStatus());
		queryRep.setPublisherClearStatusDisp(TradeUtil.publisherClearStatusEn2Ch(tradeOrder.getPublisherClearStatus()));
		queryRep.setPublisherConfirmStatus(tradeOrder.getPublisherConfirmStatus());
		queryRep.setPublisherConfirmStatusDisp(
				TradeUtil.publisherConfirmStatusEn2Ch(tradeOrder.getPublisherConfirmStatus()));
		queryRep.setPublisherCloseStatus(tradeOrder.getPublisherCloseStatus());
		queryRep.setPublisherCloseStatusDisp(TradeUtil.publisherCloseStatusEn2Ch(tradeOrder.getPublisherCloseStatus()));

		if (InvestorTradeOrderEntity.TRADEORDER_usedCoupons_yes.equals(tradeOrder.getUsedCoupons())) {
			TradeOrderCouponEntity coupon = tradeOrderCouponService.findByInvestorTradeOrder(tradeOrder);
			if (coupon != null) {
				queryRep.setCouponType(coupon.getCouponType());
				queryRep.setCouponTypeDisp(tradeOrderCouponService.couponTypeEn2Ch(coupon.getCouponType()));

				if (coupon.getCouponType().equals(TradeOrderCouponEntity.TRADEORDERCOUPON_type_rateCoupon)) {
					queryRep.setCouponAmount(coupon.getAdditionalInterestRate() + "%"); // 卡券加息率
				} else {
					queryRep.setCouponAmount(coupon.getCouponAmount().toString() + "元"); // 卡券面值
				}
			}
		}

		queryRep.setPayAmount(tradeOrder.getPayAmount());
		queryRep.setHoldVolume(tradeOrder.getHoldVolume()); // 持有份额
		queryRep.setRedeemStatus(tradeOrder.getRedeemStatus()); // 可赎回状态
		queryRep.setRedeemStatusDisp(TradeUtil.redeemStatusEn2Ch(tradeOrder.getRedeemStatus()));
		queryRep.setAccrualStatus(tradeOrder.getAccrualStatus()); // 可计息状态
		queryRep.setAccrualStatusDisp(TradeUtil.accrualStatusEn2Ch(tradeOrder.getAccrualStatus()));
		queryRep.setBeginAccuralDate(tradeOrder.getBeginAccuralDate()); // 起息日
		queryRep.setBeginRedeemDate(tradeOrder.getBeginRedeemDate()); // 起始赎回日
		queryRep.setTotalIncome(tradeOrder.getTotalIncome()); // 累计收益
		queryRep.setTotalBaseIncome(tradeOrder.getTotalBaseIncome()); // 累计基础收益
		queryRep.setTotalRewardIncome(tradeOrder.getTotalRewardIncome()); // 累计奖励收益
		queryRep.setYesterdayBaseIncome(tradeOrder.getYesterdayBaseIncome());
		queryRep.setYesterdayRewardIncome(tradeOrder.getYesterdayRewardIncome());
		queryRep.setYesterdayIncome(tradeOrder.getYesterdayIncome());
		queryRep.setIncomeAmount(tradeOrder.getIncomeAmount()); // 定期收益
		queryRep.setExpectIncome(tradeOrder.getExpectIncome());
		queryRep.setExpectIncomeExt(tradeOrder.getExpectIncomeExt());
		queryRep.setValue(tradeOrder.getValue());
		queryRep.setHoldStatus(tradeOrder.getHoldStatus());
		queryRep.setHoldStatusDisp(TradeUtil.holdStatusEn2Ch(tradeOrder.getHoldStatus()));
		queryRep.setConfirmDate(tradeOrder.getConfirmDate());
		queryRep.setBaseIncomeRatio(tradeOrder.getProduct().getBasicRatio()); // 基础收益率
		//TODO:Should use e baoquan to replace.
		/*
		if (InvestorTradeOrderEntity.TRADEORDER_contractStatus_pdfOK.equals(tradeOrder.getContractStatus())) {
			queryRep.setInvestContractAddr(productAgreementService.findByInvestorTradeOrderAndAgreementType(tradeOrder,
					ProductAgreementEntity.Agreement_agreementType_investing).getAgreementUrl());
			queryRep.setServiceContractAddr(productAgreementService.findByInvestorTradeOrderAndAgreementType(tradeOrder,
					ProductAgreementEntity.Agreement_agreementType_service).getAgreementUrl());
		}*/
		if (null != tradeOrder.getBeginAccuralDate()) {
			long holdDays = DateUtil.daysBetween(DateUtil.getSqlDate(), tradeOrder.getBeginAccuralDate()) + 1;
			if (holdDays > 0) {
				queryRep.setHoldDays(holdDays);
				ProductIncomeReward w = this.productIncomeRewardCacheService
						.getRewardEntity(tradeOrder.getProduct().getOid(), (int) holdDays);
				if (null != w) {
					queryRep.setRewardIncomeRatio(w.getRatio()); // 奖励收益率
					queryRep.setRewardIncomeLevel(w.getLevel()); // 奖励阶梯
				}
			}
		}
		queryRep.setUpdateTime(tradeOrder.getUpdateTime());
		queryRep.setCreateTime(tradeOrder.getCreateTime()); // 订单创建时间

		pagesRep.getRows().add(queryRep);
	}
	/**
	 * 
	 * 
	 * */
	public  String kickstarOnNickName(String nickName) {
		if (null == nickName) {
			return StringUtil.EMPTY;
		}
		nickName = nickName.replaceAll("([\u4e00-\u9fa5 a-z A-Z 1-9]{1})[\u4e00-\u9fa5 a-z A-Z 1-9]*", "$1*");
		return nickName;
	}
	

	/**
	 * 判断 订单是否已完成
	 */
	public BaseResp isDone(TradeOrderIsDoneReq isDone) {
		TradeOrderIsDoneRep rep = new TradeOrderIsDoneRep();

		InvestorTradeOrderEntity orderEntity = this.investorTradeOrderDao.findOne(isDone.getTradeOrderOid());
		if (null == orderEntity) {
			// error.define[80001]=投资人-银行委托单的订单号不存在!(CODE:80001)
			throw new AMPException(80001);
		}
		if (InvestorTradeOrderEntity.TRADEORDER_orderType_invest.equals(orderEntity.getOrderType())
				|| InvestorTradeOrderEntity.TRADEORDER_orderType_expGoldInvest.equals(orderEntity.getOrderType())) {
			
			if (InvestorTradeOrderEntity.TRADEORDER_orderStatus_paySuccess.equals(orderEntity.getOrderStatus())
					|| InvestorTradeOrderEntity.TRADEORDER_orderStatus_accepted.equals(orderEntity.getOrderStatus())
					|| InvestorTradeOrderEntity.TRADEORDER_orderStatus_confirmed.equals(orderEntity.getOrderStatus())) {
				if (Product.TYPE_Producttype_01.equals(orderEntity.getProduct().getType().getOid())) {
					//rep.setBeginInterestDate(orderDateService.getBeginAccuralDate(orderEntity));
					//rep.setInterestArrivedDate(orderEntity.getProduct().getRepayDate());
					/**  修改产品的开始计息日和还款日 */
					Product product =  this.productDao.findOne(orderEntity.getProduct().getOid());
					if(product!=null){
						rep.setBeginInterestDate(product.getSetupDate());//开始计息日
						rep.setInterestArrivedDate(product.getRepayDate());//开始还款日
					}
				} else {
					rep.setBeginInterestDate(orderDateService.getBeginAccuralDate(orderEntity));
					rep.setInterestArrivedDate(DateUtil.addSQLDays(rep.getBeginInterestDate(), 1));
				}
			} else if (InvestorTradeOrderEntity.TRADEORDER_orderStatus_payFailed.equals(orderEntity.getOrderStatus())) {
				PayLogEntity payLog = payLogService.findByOrderCodeAndHandleType(orderEntity.getOrderCode(), PayLogEntity.PAY_handleType_notify);
				rep.setErrorCode(-2);
				rep.setErrorMessage(payLog.getErrorMessage());
			} else {
				rep.setErrorCode(-1);
				rep.setErrorMessage("订单处理中");
				return rep;
			}
			
		} else if (InvestorTradeOrderEntity.TRADEORDER_orderType_normalRedeem.equals(orderEntity.getOrderType())) {
			if (InvestorTradeOrderEntity.TRADEORDER_orderStatus_confirmed.equals(orderEntity.getOrderStatus())
					|| InvestorTradeOrderEntity.TRADEORDER_orderStatus_accepted.equals(orderEntity.getOrderStatus())) {
				rep.setRedeemArrivedDate(orderDateService.getRedeemConfirmDate(orderEntity));
			} else {
				rep.setErrorCode(-1);
				rep.setErrorMessage("订单处理中");
				return rep;
			}
		}

		return rep;
	}

	
	
	
	
	/**
	 *  订单详情
	 */
	public TradeOrderDetailRep detail(String tradeOrderOid) {
		TradeOrderDetailRep rep = new TradeOrderDetailRep();
		InvestorTradeOrderEntity order = this.investorTradeOrderDao.findOne(tradeOrderOid);
		rep.setOrderCode(order.getOrderCode()); //订单号
		rep.setOrderType(order.getOrderType()); //交易类型
		rep.setOrderTypeDisp(TradeUtil.orderTypeEn2Ch(order.getOrderType())); //交易类型Disp
		rep.setOrderAmount(order.getOrderAmount()); //订单金额
		rep.setOrderVolume(order.getOrderVolume()); //订单份额
		rep.setOrderStatus(order.getOrderStatus()); //订单状态
		rep.setOrderStatusDisp(TradeUtil.orderStatusEn2Ch(order.getOrderStatus())); //订单状态Disp
		rep.setCreateMan(order.getCreateMan()); //订单创建人
		rep.setCreateManDisp(TradeUtil.createManEn2Ch(order.getCreateMan())); //订单创建人Disp
		rep.setCreateTime(order.getCreateTime()); //订单创建时间
		rep.setCompleteTime(order.getCompleteTime()); //订单完成时间
		return rep;
	}
	
	/**
	 * 更新投资人清算状态
	 */
	@Transactional(value = TxType.REQUIRES_NEW)
	public int updateInvestorClearStatus(InvestorOffsetEntity offset,String investorClearStatus){
		return this.investorTradeOrderDao.updateInvestorClearStatus(offset, investorClearStatus);
	}
	
	/**
	 * 更新投资人结算状态
	 */
	public int updateInvestorCloseStatus(InvestorOffsetEntity offset,String investorCloseStatus){
		return this.investorTradeOrderDao.updateInvestorCloseStatus(offset, investorCloseStatus);
	}
	
	public int updateInvestorCloseStatusDirectly(InvestorOffsetEntity offset,String investorCloseStatus){
		return this.investorTradeOrderDao.updateInvestorCloseStatusDirectly(offset, investorCloseStatus);
	}
	
	
	
	public int updatePlatformInvestorCloseStatus(InvestorOffsetEntity offset, String investorOid, String investorCloseStatus){
		return this.investorTradeOrderDao.updatePlatformInvestorCloseStatus(offset, investorOid, investorCloseStatus);
	}
	
	
	/**
	 * 查询用于份额确认 
	 */
	public List<InvestorTradeOrderEntity> findByOffsetOid(String offsetOid, String lastOid) {
		return this.investorTradeOrderDao.findByOffsetOidAndOid(offsetOid, lastOid);
	}
	
	/**
	 * 批量更新
	 */
	public void batchUpdate(List<InvestorTradeOrderEntity> orderList) {
		this.investorTradeOrderDao.save(orderList);

	}
	
	/**
	 * 查询订单用于生成PDF协议 HTML
	 */
	public List<InvestorTradeOrderEntity> findByProductOid4Contract(String productOid, String lastOid) {
		return this.investorTradeOrderDao.findByProductOid4Contract(productOid, lastOid);
	}
	
	/**
	 * 查询订单用于生成PDF协议  PDF
	 */
	public List<InvestorTradeOrderEntity> findByProductOid4PDF(String productOid, String lastOid) {
		return this.investorTradeOrderDao.findByProductOid4PDF(productOid, lastOid);
	}
	

	public int updatePublisherClearStatus(String offsetOid, String clearStatus) {
		int i = this.investorTradeOrderDao.updatePublisherClearStatus(offsetOid, clearStatus);
//		if (i < 1) {
//			// error.define[20021]=清算状态异常(CODE:20021)
//			throw new AMPException(20021);
//		}
		return i;
	}

	/**
	 * 发行人结算
	 */
	public int updateCloseStatus4Redeem(PublisherOffsetEntity offset, String closeStatus) {
		int i = investorTradeOrderDao.updateCloseStatus4Redeem(offset.getOid(), closeStatus);
		return i;
		
	}
	
	/**
	 * 发行人结算
	 */
	public int updateCloseStatus4Invest(PublisherOffsetEntity offset) {
		int i = investorTradeOrderDao.updateCloseStatus4Invest(offset.getOid(), PublisherOffsetEntity.OFFSET_closeStatus_closed);
		return i;
		
	}
	
	/**
	 * 发行人现金分红结算
	 */
	public int updateCloseStatus4Dividend(DividendOffsetEntity offset, String closeStatus4TradeOrder) {
		int i = investorTradeOrderDao.updateCloseStatus4Dividend(offset.getOid(), closeStatus4TradeOrder);
		return i;
		
	}
	
	/**
	 * 结算--支付回调
	 */
	public int updateCloseStatus4CloseBack(PublisherOffsetEntity offset, String closeStatus) {
		int i = investorTradeOrderDao.updateCloseStatus4CloseBack(offset.getOid(), closeStatus);
		return i;
		
	}
	

	/**
	 * 份额确认
	 */
	public int update4Confirm(String oid) {
		int i = this.investorTradeOrderDao.update4Confirm(oid);
		if (i < 1) {
			// error.define[30068]=份额确认订单更新异常(CODE:30068)
			throw new AMPException(30068);
		}
		return i;

	}

	
//	/**
//	 * 查询投资状态、赎回状态的订单时间和订单金额
//	 * 
//	 * @param investorOid
//	 * @param productOid
//	 * @param orderType:
//	 *            invest投资订单，redeem赎回订单
//	 * @return
//	 */
//	public PageResp<TradeOrderDetailsRep> queryHoldApartDetailsByHoldStatus(
//			Specification<InvestorTradeOrderEntity> spec, Pageable pageable) {
//
//		Page<InvestorTradeOrderEntity> page = this.investorTradeOrderDao.findAll(spec, pageable);
//	
//		PageResp<TradeOrderDetailsRep> pagesRep = new PageResp<TradeOrderDetailsRep>();
//		if (page != null && page.getContent() != null && page.getTotalElements() > 0) {
//			List<TradeOrderDetailsRep> rows = new ArrayList<TradeOrderDetailsRep>();
//			for (InvestorTradeOrderEntity p : page) {
//				TradeOrderDetailsRep queryRep = new TradeOrderDetailsRep(p.getOrderTime(), // 订单时间
//						p.getOrderAmount(), // 订单金额
//						getOrderStatusName(p.getOrderStatus())// 订单状态
//				);
//				rows.add(queryRep);
//			}
//		
//			pagesRep.setRows(rows);
//
//			pagesRep.setTotal(page.getTotalElements());
//		}
//
//		return pagesRep;
//	}
	
//	/**
//	 * 我的交易明细
//	 */
//	public PageResp<MyTradeOrderDetailRep> queryMyTradeDetail(Specification<InvestorTradeOrderEntity> spec,
//			Pageable pageable) {
//
//		Page<InvestorTradeOrderEntity> page = this.investorTradeOrderDao.findAll(spec, pageable);
//		PageResp<MyTradeOrderDetailRep> pagesRep = new PageResp<MyTradeOrderDetailRep>();
//		if (page != null && page.getContent() != null && page.getTotalElements() > 0) {
//			List<MyTradeOrderDetailRep> rows = new ArrayList<MyTradeOrderDetailRep>();
//			for (InvestorTradeOrderEntity p : page) {
//				MyTradeOrderDetailRep queryRep = new MyTradeOrderDetailRep(getOrderTypeName(p.getOrderType()), // 订单类型
//						p.getOrderCode(), // 订单号
//						p.getOrderAmount(), // 订单金额
//						p.getOrderTime(), // 订单时间
//						getOrderStatusName(p.getOrderStatus())// 订单状态
//				);
//				rows.add(queryRep);
//			}
//			pagesRep.setRows(rows);
//
//			pagesRep.setTotal(page.getTotalElements());
//		}
//
//		return pagesRep;
//	}
	
	
	
	public void refundAll() {
		String lastOid = "0";
		while (true) {
			List<InvestorTradeOrderEntity> orderList = this.investorTradeOrderDao.findPage4Refund(lastOid);
			if (orderList.isEmpty()) {
				break;
			}
			for (InvestorTradeOrderEntity entity : orderList) {
//				if (!InvestorTradeOrderEntity.TRADEORDER_orderStatus_toRefund.equals(entity.getOrderStatus())) {
//					throw new AMPException("非待退款订单");
//				}
//				if (0 == this.refundPay(entity).getErrorCode()) {
//					entity.setOrderStatus(InvestorTradeOrderEntity.TRADEORDER_orderStatus_refunding);
//				}
				lastOid = entity.getOid();
			}
			this.investorTradeOrderDao.save(orderList);
			
		}
	}
	
	public void refundPart(List<String> orderOidList) {
		
		List<InvestorTradeOrderEntity> orderList = new ArrayList<InvestorTradeOrderEntity>();
		for (String tradeOrderOid : orderOidList) {
			InvestorTradeOrderEntity entity = this.findOne(tradeOrderOid);
			if (null == entity) {
				throw new AMPException("订单号不存在");
			}
//			if (!InvestorTradeOrderEntity.TRADEORDER_orderStatus_toRefund.equals(entity.getOrderStatus())) {
//				throw new AMPException("非待退款订单");
//			}
//			if (0 == this.refundPay(entity).getErrorCode()) {
//				entity.setOrderStatus(InvestorTradeOrderEntity.TRADEORDER_orderStatus_refunding);
//				orderList.add(entity);
//			}
		}
	
		this.investorTradeOrderDao.save(orderList);
	}
	
//	public BaseResp refundPay(InvestorTradeOrderEntity order) {
//		
//		BigDecimal platformBalance = this.platformBalanceService.getMiddleAccount4InvestCollect();
//		if (order.getOrderAmount().compareTo(platformBalance) > 0) {
//			this.logger.warn("balance({}) of platform middle account(投资专用) is not enough", platformBalance);
//			throw AMPException.getException(20014);
//		}
//		RefundRequest req = RefundRequestBuilder.n()
//				.refundAmount(order.getOrderAmount())
//				.origOuterTradeNo(order.getOrderCode())
//				.outTradeNo(this.seqGenerator.next(CodeConstants.Investor_batch_refund))
//				.summary("refund")
//				.build();
//		BaseResp baseRep = this.paymentServiceImpl.refund(req);
//		return baseRep;
//	}
	
//	public String refundPayCallBack(RefundStatusSync refundStatus) {
//		String status = PaymentLogEntity.PaymentLog_paymentStatus_success;
//		InvestorTradeOrderEntity order = this.findByOrderCode(refundStatus.getOrig_outer_trade_no());
//		if (PaymentLogEntity.PaymentLog_paymentStatus_success.equals(refundStatus.getRefund_status())) {
//			
//			order.setOrderStatus(InvestorTradeOrderEntity.TRADEORDER_orderStatus_refunded);
//			
//			investorInvestTradeOrderService.back(order);
//			
//			this.tulipNewService.tulipEventDeal(order);
//		}
//		
//		if (PaymentLogEntity.PaymentLog_refund_failed.equals(refundStatus.getRefund_status())) {
//			//order.setOrderStatus(InvestorTradeOrderEntity.TRADEORDER_orderStatus_refundFailed);
//			status = PaymentLogEntity.PaymentLog_paymentStatus_failure;
//		}
//		order.setCompleteTime(DateUtil.getSqlCurrentDate());
//		this.saveEntity(order);
//		return status;
//	}

	




//	public InvestorTradeOrderEntity findByCoupons(String coupons) {
//		
//		return this.investorTradeOrderDao.findByCoupons(coupons);
//	}


	/** 
	 * 份额确认（分仓）
	 */
	public int update4Confirm(InvestorTradeOrderEntity orderEntity, String redeemStatus, String accrualStatus) {
		return this.investorTradeOrderDao.update4Confirm(orderEntity.getOid(), redeemStatus, accrualStatus);
		
	}
	
	/**
	 * 根据订单平仓
	 */
	
	/**
	 * 根据订单平仓
	 */
	
	
	
	public FlatWareTotalRep wishplanflatWare(InvestorTradeOrderEntity redeemOrder) {
		FlatWareTotalRep totalRep = new FlatWareTotalRep();
		BigDecimal volume = redeemOrder.getOrderVolume();
		if (volume.compareTo(SysConstant.BIGDECIMAL_defaultValue) <= 0) {
			throw AMPException.getException("订单金额小于等于0");
		}
		// List<InvestorTradeOrderEntity> orderList =
		// this.findApart(redeemOrder.getInvestorBaseAccount(),
		// redeemOrder.getProduct());
		// for (InvestorTradeOrderEntity entity : orderList) {
		InvestorTradeOrderEntity entity = investorTradeOrderDao.findTradeOrderByWishplan(redeemOrder.getWishplanOid());

		/** 定期统计本金、利息 */
		if (Product.TYPE_Producttype_01.equals(redeemOrder.getProduct().getType().getOid())) {
			totalRep.setTHoldVolume(totalRep.getTHoldVolume().add(entity.getHoldVolume()));
			totalRep.setTIncomeAmount(totalRep.getTIncomeAmount().add(entity.getTotalIncome()));
		}

		entity.setHoldVolume(SysConstant.BIGDECIMAL_defaultValue);
		entity.setValue(entity.getHoldVolume());
		entity.setHoldStatus(getFlatWareHoldStatus(redeemOrder));

		this.closeDetailsService.createCloseDetails(entity, volume, redeemOrder);
		this.orderLogService.createRedeemCloseLog(entity, redeemOrder);
		setFlatWareRep(volume, entity, totalRep);
		volume = SysConstant.BIGDECIMAL_defaultValue;

		// }
		if (volume.compareTo(SysConstant.BIGDECIMAL_defaultValue) != 0) {
			logger.info("flatWare orderCode:{}", redeemOrder.getOrderCode());
			// error.define[20020]=赎回时分仓份额异常(CODE:20020)
			throw AMPException.getException(20020);
		}
		investorTradeOrderDao.save(entity);
		return totalRep;
	}
	
	public FlatWareTotalRep flatWare(InvestorTradeOrderEntity redeemOrder) {
		FlatWareTotalRep totalRep = new FlatWareTotalRep();
		BigDecimal volume = redeemOrder.getOrderVolume();
		if (volume.compareTo(SysConstant.BIGDECIMAL_defaultValue) <= 0) {
			throw AMPException.getException("订单金额小于等于0");
		}
		List<InvestorTradeOrderEntity> orderList = this.findApart(redeemOrder.getInvestorBaseAccount(), redeemOrder.getProduct());
		for (InvestorTradeOrderEntity entity : orderList) {
			if (volume.compareTo(SysConstant.BIGDECIMAL_defaultValue) <= 0) {
				break;
			}
			
			/** 定期统计本金、利息*/
			if (Product.TYPE_Producttype_01.equals(redeemOrder.getProduct().getType().getOid())) {
				totalRep.setTHoldVolume(totalRep.getTHoldVolume().add(entity.getHoldVolume()));
				totalRep.setTIncomeAmount(totalRep.getTIncomeAmount().add(entity.getTotalIncome()));
			}
			
			if (entity.getHoldVolume().compareTo(volume) < 0) {
				BigDecimal holdVolume = entity.getHoldVolume();
				volume = volume.subtract(holdVolume);
				
				entity.setHoldVolume(SysConstant.BIGDECIMAL_defaultValue);
				entity.setValue(entity.getHoldVolume());
				entity.setHoldStatus(getFlatWareHoldStatus(redeemOrder));
				
				if (holdVolume.compareTo(BigDecimal.ZERO) > 0) {
					this.closeDetailsService.createCloseDetails(entity, holdVolume, redeemOrder);
					setFlatWareRep(holdVolume, entity, totalRep);
				}
				
				this.orderLogService.createRedeemCloseLog(entity, redeemOrder);
				continue;
			}

			if (entity.getHoldVolume().compareTo(volume) == 0) {
				entity.setHoldVolume(SysConstant.BIGDECIMAL_defaultValue);
				entity.setValue(entity.getHoldVolume());
				entity.setHoldStatus(getFlatWareHoldStatus(redeemOrder));
				
				this.closeDetailsService.createCloseDetails(entity, volume, redeemOrder);
				this.orderLogService.createRedeemCloseLog(entity, redeemOrder);
				setFlatWareRep(volume, entity, totalRep);
				volume = SysConstant.BIGDECIMAL_defaultValue;
				
				break;
			}

			if (entity.getHoldVolume().compareTo(volume) > 0) {
				entity.setHoldVolume(entity.getHoldVolume().subtract(volume));
				entity.setValue(entity.getHoldVolume());
				entity.setHoldStatus(InvestorTradeOrderEntity.TRADEORDER_holdStatus_partHolding);
			
				this.closeDetailsService.createCloseDetails(entity, volume, redeemOrder);
				this.orderLogService.createRedeemCloseLog(entity, redeemOrder);
				
				setFlatWareRep(volume,  entity, totalRep);
				volume = SysConstant.BIGDECIMAL_defaultValue;
				break;
			}
		}
		if (volume.compareTo(SysConstant.BIGDECIMAL_defaultValue) != 0) {
			logger.info("flatWare orderCode:{}", redeemOrder.getOrderCode());
			// error.define[20020]=赎回时分仓份额异常(CODE:20020)
			throw AMPException.getException(20020);
		}
		this.batchUpdate(orderList);
		return totalRep;
	}
	
	private String getFlatWareHoldStatus(InvestorTradeOrderEntity orderEntity) {
		if (InvestorTradeOrderEntity.TRADEORDER_orderType_cash.equals(orderEntity.getOrderType())) {
			return InvestorTradeOrderEntity.TRADEORDER_holdStatus_closed;
		}
		if (InvestorTradeOrderEntity.TRADEORDER_orderType_expGoldRedeem.equals(orderEntity.getOrderType())) {
			return InvestorTradeOrderEntity.TRADEORDER_holdStatus_partHolding;
		}
		if (InvestorTradeOrderEntity.TRADEORDER_orderType_cashFailed.equals(orderEntity.getOrderType())) {
			return InvestorTradeOrderEntity.TRADEORDER_holdStatus_refunded;
		}
		if (InvestorTradeOrderEntity.TRADEORDER_orderType_normalRedeem.equals(orderEntity.getOrderType())) {
			return InvestorTradeOrderEntity.TRADEORDER_holdStatus_partHolding;
		}
		if (InvestorTradeOrderEntity.TRADEORDER_orderType_clearRedeem.equals(orderEntity.getOrderType())) {
			return InvestorTradeOrderEntity.TRADEORDER_holdStatus_partHolding;
		}
		if (InvestorTradeOrderEntity.TRADEORDER_orderType_reRedeem.equals(orderEntity.getOrderType())) {
			return InvestorTradeOrderEntity.TRADEORDER_holdStatus_partHolding;
		}
		throw new AMPException("订单类型异常");
		
	}
	
	private void setFlatWareRep(BigDecimal holdVolume, InvestorTradeOrderEntity investOrder, FlatWareTotalRep totalRep) {

		FlatWareRep flatWareRep = new FlatWareRep();
		flatWareRep.setBeginAccuralDate(investOrder.getBeginAccuralDate());
		flatWareRep.setHoldVolume(holdVolume);
		flatWareRep.setAccrualStatus(investOrder.getAccrualStatus());
		
		flatWareRep.setCompleteTime(investOrder.getCompleteTime());
		totalRep.getFlatWareRepList().add(flatWareRep);
		if (InvestorTradeOrderEntity.TRADEORDER_accrualStatus_yes.equals(investOrder.getAccrualStatus())) {
			totalRep.setAccruableHoldVolume(totalRep.getAccruableHoldVolume().add(holdVolume));
		}
	}
	
	/**
	 * 查询（分仓）
	 */
	public List<InvestorTradeOrderEntity> findApart(InvestorBaseAccountEntity investorBaseAccount, Product product) {
		if (product.getClosingRule() != null && product.getClosingRule().equals(Product.PRODUCT_closingRule_LIFO)){
			return this.investorTradeOrderDao.findApartDesc(investorBaseAccount, product);
		}
		return this.investorTradeOrderDao.findApart(investorBaseAccount, product);
	}
	
	/**
	 * 按产品维度进行计息份额份额快照， 活期
	 */
	@Transactional(TxType.REQUIRES_NEW)
	public void snapshotT0Volume(Product product, Date incomeDate){
		this.investorTradeOrderDao.snapshotT0Volume(product.getOid(), incomeDate);
	}
	
	@Transactional(TxType.REQUIRES_NEW)
	public void snapshotT0CashVolume(Product product, Date incomeDate){
		this.investorTradeOrderDao.snapshotT0CashVolume(product.getOid(), incomeDate);
	}
	
	/**
	 * 按产品维度进行计息份额份额快照 , 定期
	 */
	@Transactional(TxType.REQUIRES_NEW)
	public void snapshotTnVolume(Product product, Date incomeDate){
		this.investorTradeOrderDao.snapshotTnVolume(product.getOid(), incomeDate);
	}
	
	/**
	 * 体验金产品快照
	 */
	@Transactional(TxType.REQUIRES_NEW)
	public void snapshotTasteCouponVolume(Product product, Date incomeDate){
		this.investorTradeOrderDao.snapshotTasteCouponVolume(product.getOid(), incomeDate);
	}
	
	public List<InvestorTradeOrderEntity> findByBeforeBeginRedeemDateInclusive(Date date, String oid) {
		return this.investorTradeOrderDao.findByBeforerBeginRedeemDateInclusive(date, oid);
	}
	
	public List<InvestorTradeOrderEntity> findByBeforeBeginAccuralDateInclusive(Date date, String oid) {
		return this.investorTradeOrderDao.findByBeforeBeginAccuralDateInclusive(date, oid);
	}
	
	
	/**
	 * 解锁赎回订单
	 */
	public int unlockRedeem(String orderOid) {
		return this.investorTradeOrderDao.unlockRedeem(orderOid);
	}
	
	/**
	 * 解锁计息订单
	 */
	public int unlockAccrual(String orderOid) {
		return this.investorTradeOrderDao.unlockAccrual(orderOid);
		
	}
	
	public List<InvestorTradeOrderEntity> findInterestableApart(PublisherHoldEntity hold, Date incomeDate) {
		return this.investorTradeOrderDao.findInterestableApart(hold, incomeDate);
	}
	
	public boolean isConfirm(String productOid) {
		int cc = this.investorTradeOrderDao.getToConfirmCountByProduct(productOid);
		if (cc == 0) {
			return true;
		}
		return false;
	}
	
	public int updateHoldApart4Interest(String apartOid, BigDecimal incomeVolume, BigDecimal incomeAmount, 
			BigDecimal netUnitAmount, Date incomeDate, BigDecimal baseAmount, BigDecimal rewardAmount) {
		
		int i = this.investorTradeOrderDao.updateHoldApart4Interest(apartOid, incomeVolume, incomeAmount,
				netUnitAmount, incomeDate, baseAmount, rewardAmount);
		if (i < 1) {
			throw new AMPException("计息失败");
		}
		return i;
	}
	
	public int updateHoldApart4InterestTn(String apartOid, BigDecimal incomeAmount, Date incomeDate) {
		
		int i = investorTradeOrderDao.updateHoldApart4InterestTn(apartOid, incomeAmount, incomeDate);
		if (i < 1) {
			throw new AMPException("计息失败");
		}
		return i;
	}
	
	/** 统计渠道截止昨日投资信息 */
	public List<Object[]> statInvestAmountByChannel(Timestamp startTime, Timestamp endTime) {

		// 统计各渠道昨日投资额
		return this.investorTradeOrderDao.statInvestAmountByChannel(startTime, endTime);
	}

	/** 统计各渠道截止到昨日累计投资额 */
	public List<Object[]> statInvestTotalAmountByChannel(Timestamp endTime) {

		return this.investorTradeOrderDao.statInvestTotalAmountByChannel(endTime);
	}



	public List<InvestorTradeOrderEntity> findByPublisherOffset(PublisherOffsetEntity offset) {
		return this.investorTradeOrderDao.findByPublisherOffset(offset);
	}


	public void flatExpGold() {
		
		if (this.jobLockService.getRunPrivilege(JobEnum.JOB_jobId_flatExpGold.getJobId())) {
			flatExpGoldLog();
		}
	}

	private void flatExpGoldLog() {
		JobLogEntity jobLog = JobLogFactory.getInstance(JobEnum.JOB_jobId_flatExpGold.getJobId());

		try {

			flatExpGoldDo();

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			jobLog.setJobMessage(AMPException.getStacktrace(e));
			jobLog.setJobStatus(JobLogEntity.JOB_jobStatus_failed);

		}
		jobLog.setBatchEndTime(DateUtil.getSqlCurrentDate());
		this.jobLogService.saveEntity(jobLog);
		this.jobLockService.resetJob(JobEnum.JOB_jobId_flatExpGold.getJobId());
	}

	private void flatExpGoldDo() {
		String lastOid = "0";
		Date baseDate = DateUtil.getSqlDate();
		while (true) {
			List<InvestorTradeOrderEntity> orderList = this.investorTradeOrderDao.queryFlatExpGold(lastOid, baseDate);
			if (orderList.isEmpty()) {
				break;
			}
			
			for (InvestorTradeOrderEntity orderEntity : orderList) {
				try {
					logger.info("flatExpGoldDo start ,orderCode={}", orderEntity.getOrderCode());
					investorTradeOrderRequireNewService.processOneItem(orderEntity);
					logger.info("flatExpGoldDo success ,orderCode={}", orderEntity.getOrderCode());
				} catch (Exception e) {
					logger.info("flatExpGoldDo failure ,orderCode={}", orderEntity.getOrderCode());
				}
				
				lastOid = orderEntity.getOid();
			}
		}
		
	}


	public int updateOrderStatus4Abandon(String orderCode) {
		int i = this.investorTradeOrderDao.updateOrderStatus4Abandon(orderCode);
		if (i < 1) {
			throw new AMPException("废单订单状态异常");
		}
		return i;
		
	}

	public int updateOrderStatus4Refund(String orderCode) {
		int i = this.investorTradeOrderDao.updateOrderStatus4Refund(orderCode);
		if (i < 1) {
			throw new AMPException("废单订单状态异常");
		}
		return i;
		
	}

	public List<InvestorTradeOrderEntity> findByPublisherHold(PublisherHoldEntity hold) {
		return this.investorTradeOrderDao.findByPublisherHold(hold);
	}

	public int unlockRedeemByHold(PublisherHoldEntity hold) {
		int i = this.investorTradeOrderDao.unlockRedeemByHold(hold);
		if (i < 1) {
			throw new AMPException("分仓解锁赎回异常");
		}
		return i;
	}
	
	/**
	 * 赎回，还本付息，退款已结算，confirmed：订单与异常
	 * @param checkTimeStart
	 * @param checkTimeEnd
	 * @param investorOid
	 * @return
	 */
	public BigDecimal getAmtTradeOrder4Acc(String checkTimeStart, String checkTimeEnd, String investorOid) {
		return this.investorTradeOrderDao.getAmtTradeOrder4Acc(checkTimeStart, checkTimeEnd, investorOid);
	}
	
	/**
	 * 用户持有份额
	 * @param checkTimeStart
	 * @param checkTimeEnd
	 * @param orderType
	 * @param investorOid
	 * @param productType
	 * @return
	 */
	public BigDecimal getHoldingAmtTradeOrder4Acc(String checkTimeStart, String checkTimeEnd, String orderType, String investorOid, String productType) {
		return this.investorTradeOrderDao.getHoldingAmtTradeOrder4Acc(checkTimeStart, checkTimeEnd, orderType, investorOid, productType);
	}

	/**
	 * 用户持有状态对应金额
	 * @param checkTimeStart
	 * @param checkTimeEnd
	 * @param orderType
	 * @param holdStatus
	 * @param investorOid
	 * @param productType
	 * @return
	 */
	public BigDecimal getHoldStatusAmtTradeOrder4Acc(String checkTimeStart, String checkTimeEnd, String orderType, String holdStatus, String investorOid, String productType) {
		return this.investorTradeOrderDao.getHoldStatusAmtTradeOrder4Acc(checkTimeStart, checkTimeEnd, orderType, holdStatus, investorOid, productType);
	}
	
	/**
	 * 用户红包卡券金额
	 * @param checkTimeStart
	 * @param checkTimeEnd
	 * @param orderType
	 * @param investorOid
	 * @return
	 */
	public BigDecimal getCouponAmtTradeOrder4Acc(String checkTimeStart, String checkTimeEnd, String orderType, String investorOid) {
		return this.investorTradeOrderDao.getCouponAmtTradeOrder4Acc(checkTimeStart, checkTimeEnd, orderType, investorOid);
	}
	
	/**
	 * 申购申请中金额
	 * @param checkTimeStart
	 * @param checkTimeEnd
	 * @param investorOid
	 * @return
	 */
	public BigDecimal getApplyingTradeOrder4Acc(String checkTimeStart, String checkTimeEnd, String investorOid) {
		return this.investorTradeOrderDao.getApplyingTradeOrder4Acc(checkTimeStart, checkTimeEnd, investorOid);
	}
	
	
	
	
	/**
	 * 金猪-订单查询 
	 */
	public TradeOrderListRep investorTradeOrderList(Specification<InvestorTradeOrderEntity> spec, Pageable pageable, String investorOid) {
		
		TradeOrderListRep rep = new TradeOrderListRep();
		
		List<HoldCacheEntity> holds = this.cacheHoldService.findByInvestorOid(investorOid);
		
		for (HoldCacheEntity hold : holds) {
			ProductCacheEntity product = this.cacheProductService.getProductCacheEntityById(hold.getProductOid());
			if (Product.TYPE_Producttype_02.equals(product.getType())) {
				if (this.labelService.isProductLabelHasAppointLabel(product.getProductLabel(), LabelEnum.tiyanjin.toString())) {
					rep.setExpGoldAmount(rep.getExpGoldAmount().add(hold.getTotalVolume()));
				} else {
					rep.setAllValue(rep.getAllValue().add(hold.getTotalVolume())); // 总资产
				}
				
				rep.setYesterdayAllIncome(rep.getYesterdayAllIncome().add(hold.getHoldYesterdayIncome())); // 昨日收益
				
				if (!this.labelService.isProductLabelHasAppointLabel(product.getProductLabel(), LabelEnum.tiyanjin.toString())) {
					rep.getRedeemableProductOids().add(product.getProductOid());
				}
			}
		}
		
		
		Page<InvestorTradeOrderEntity> cas = this.investorTradeOrderDao.findAll(spec, pageable);
		PageResp<MyOrdersRep> pagesRep = new PageResp<MyOrdersRep>();
		if (cas != null && cas.getContent() != null && cas.getTotalElements() > 0) {
			for (InvestorTradeOrderEntity tradeOrder : cas) {
				MyOrdersRep queryRep = new MyOrdersRep();
				queryRep.setTradeOrderOid(tradeOrder.getOid());
				queryRep.setProductName(tradeOrder.getProduct().getName()); //产品名称
				queryRep.setOrderType(tradeOrder.getOrderType()); // 订单类型
				queryRep.setOrderTime(tradeOrder.getOrderTime());
				queryRep.setYesterdayIncome(tradeOrder.getYesterdayIncome());
				queryRep.setValue(tradeOrder.getValue());
				queryRep.setBaseIncomeRatio(null == tradeOrder.getProduct().getBasicRatio() 
						? BigDecimal.ZERO : DecimalUtil.zoomOut(tradeOrder.getProduct().getBasicRatio(), 100, DecimalUtil.scale)); //基础收益率
				
				long holdDays = DateUtil.daysBetween(DateUtil.getSqlDate(), tradeOrder.getBeginAccuralDate());
				if (holdDays >= 0l) {
					ProductIncomeReward w = this.productIncomeRewardCacheService
							.getRewardEntity(tradeOrder.getProduct().getOid(), (int) holdDays);
					if (null != w) {
						queryRep.setRewardIncomeRatio(DecimalUtil.zoomOut(w.getRatio(), 100, DecimalUtil.scale)); // 昨日奖励收益率
					} else {
						queryRep.setRewardIncomeRatio(BigDecimal.ZERO);
					}
				} else {
					queryRep.setRewardIncomeRatio(BigDecimal.ZERO);
				}
			
				queryRep.setIncomeRatio(DecimalUtil.setScaleDown(queryRep.getBaseIncomeRatio().add(queryRep.getRewardIncomeRatio())).toPlainString());
				
				holdDays = DateUtil.daysBetween(DateUtil.getSqlDate(), tradeOrder.getBeginAccuralDate()) + 1;
				if (tradeOrder.getOrderStatus().equals(InvestorTradeOrderEntity.TRADEORDER_orderStatus_confirmed)) {
					if (tradeOrder.getHoldStatus().equals(InvestorTradeOrderEntity.TRADEORDER_holdStatus_holding)
							|| tradeOrder.getHoldStatus()
									.equals(InvestorTradeOrderEntity.TRADEORDER_holdStatus_partHolding)) {
						if (holdDays == 1) {
							queryRep.setViewStatus(TradeOrderQueryJZRep.TRADEORDER_viewStatus_work);
						} else {
							queryRep.setViewStatus(TradeOrderQueryJZRep.TRADEORDER_viewStatus_holding);
						}
					} else {
						queryRep.setViewStatus(TradeOrderQueryJZRep.TRADEORDER_viewStatus_over);
					}
				} else if (tradeOrder.getOrderStatus().equals(InvestorTradeOrderEntity.TRADEORDER_orderStatus_submitted)
						|| tradeOrder.getOrderStatus().equals(InvestorTradeOrderEntity.TRADEORDER_orderStatus_toPay)
						|| tradeOrder.getOrderStatus()
								.equals(InvestorTradeOrderEntity.TRADEORDER_orderStatus_paySuccess)
						|| tradeOrder.getOrderStatus()
								.equals(InvestorTradeOrderEntity.TRADEORDER_orderStatus_accepted)) {
					queryRep.setViewStatus(TradeOrderQueryJZRep.TRADEORDER_viewStatus_accepted);
				} else {
					queryRep.setViewStatus(TradeOrderQueryJZRep.TRADEORDER_viewStatus_fail);
				}
				
				queryRep.setViewStatusDisp(TradeUtil.viewStatusEn2Ch(queryRep.getViewStatus(), holdDays));
				
				pagesRep.getRows().add(queryRep);
			}
		}
		pagesRep.setTotal(cas.getTotalElements());
		
		rep.setPageResp(pagesRep);
		return rep;
	}
	/**
	 * 金猪-订单详情 
	 */
	public MyOrderDetailRep investorTradeOrderDetail(Specification<InvestorTradeOrderEntity> spec) {
		MyOrderDetailRep queryRep = new MyOrderDetailRep();
		List<InvestorTradeOrderEntity> list = this.investorTradeOrderDao.findAll(spec);
		if (list != null && !list.isEmpty()) {
			InvestorTradeOrderEntity tradeOrder = list.get(0);

			queryRep.setProductName(tradeOrder.getProduct().getName()); // 产品名称
			queryRep.setOrderTime(tradeOrder.getOrderTime());
			queryRep.setTotalIncome(tradeOrder.getTotalIncome()); // 累计收益
			queryRep.setYesterdayIncome(tradeOrder.getYesterdayIncome());
			queryRep.setValue(tradeOrder.getValue());
			queryRep.setBaseIncomeRatio(null == tradeOrder.getProduct().getBasicRatio() 
					? BigDecimal.ZERO : DecimalUtil.zoomOut(tradeOrder.getProduct().getBasicRatio(), 100, DecimalUtil.scale)); // 基础收益率
			long holdDays = 0L;
			queryRep.setHoldDays(holdDays);

			holdDays = DateUtil.daysBetween(DateUtil.getSqlDate(), tradeOrder.getBeginAccuralDate()) + 1;
			if (holdDays > 0) {
				queryRep.setHoldDays(holdDays);
				ProductIncomeReward w = this.productIncomeRewardCacheService.getRewardEntity(tradeOrder.getProduct().getOid(), (int) holdDays);
				if (null != w) {
					queryRep.setRewardIncomeRatio(DecimalUtil.zoomOut(w.getRatio(), 100, DecimalUtil.scale)); // 奖励收益率
					ProductIncomeReward ws = this.productIncomeRewardCacheService
							.getRewardEntity(tradeOrder.getProduct().getOid(), w.getEndDate() + 1);
					if (ws != null) {
						queryRep.setRewardNestDays(w.getEndDate() - holdDays);
					}
				} else {
					queryRep.setRewardIncomeRatio(BigDecimal.ZERO);
				}
			} else {
				ProductIncomeReward ws = this.productIncomeRewardCacheService.getRewardEntity(tradeOrder.getProduct().getOid(), 1);
				if (ws != null) {
					queryRep.setRewardNestDays(ws.getStartDate() - holdDays);
				}
				queryRep.setRewardIncomeRatio(BigDecimal.ZERO);
			}

			queryRep.setIncomeRatio(DecimalUtil.setScaleDown(queryRep.getBaseIncomeRatio().add(queryRep.getRewardIncomeRatio())).toPlainString());
		}
		return queryRep;
	}
	
	/**
	 * 金猪-赎回订单详情 
	 * @param pageable 
	 */
	public PageResp<TradeOrderCloseQueryRep> investorTradeOrderCloseList(Specification<CloseDetailsEntity> spec, Pageable pageable) {
		Page<CloseDetailsEntity> list = closeDetailsService.findAll(spec, pageable);
		PageResp<TradeOrderCloseQueryRep> pagesRep = new PageResp<TradeOrderCloseQueryRep>();
		if (list != null && list.getContent() != null && list.getTotalElements() > 0) {
			for (CloseDetailsEntity close : list){
				TradeOrderCloseQueryRep en = new TradeOrderCloseQueryRep();
				en.setCloseOrderOid(close.getRedeemOrder().getOid());
				en.setTradeOrderOid(close.getInvestOrder().getOid());
				en.setProductOid(close.getProduct().getOid());
				en.setProductName(close.getProduct().getName());
				en.setPhoneNum(this.kickstar ? StringUtil.kickstarOnPhoneNum(close.getInvestorBaseAccount().getPhoneNum()) : close.getInvestorBaseAccount().getPhoneNum());
				en.setOrderCode(close.getRedeemOrder().getOrderCode());
				en.setOrderVolume(close.getChangeVolume());
				en.setOrderTime(close.getCreateTime());
				en.setBaseIncomeRatio(close.getBasicRatio());
				en.setBaseIncomeRatioDisp(DecimalUtil.zoomOut(en.getBaseIncomeRatio(), 100)+"%");
				en.setRewardIncomeRatio(close.getRewardIncomeRatio());
				en.setRewardIncomeRatioDisp(DecimalUtil.zoomOut(close.getRewardIncomeRatio(), 100)+"%");
				en.setIncomeRatio(close.getIncomeRatio());
				en.setIncomeRatioDisp(DecimalUtil.zoomOut(close.getIncomeRatio(), 100)+"%");
				en.setHoldDays(close.getHoldDays());
				pagesRep.getRows().add(en);
			}
		}
		pagesRep.setTotal(list.getTotalElements());
		return pagesRep;
	}
	
	/**
	 * 待确认赎回订单 oids
	 */
	public List<String> findByProductOid(String productOid, String lastOid) {
		return this.investorTradeOrderDao.findByProductOid(productOid, lastOid);
	}

	/**
	 * 订单重新结算
	 */
	public BaseResp repayment(List<String> oidlist, String operator) {
		BaseResp resp = new BaseResp();
		Map<String, List<InvestorTradeOrderEntity>> map = new HashMap<>();
		List<PublisherBaseAccountEntity> pubList = new ArrayList<PublisherBaseAccountEntity>();
		if (oidlist != null && !oidlist.isEmpty()){
			
			for (String oid : oidlist) {
				InvestorTradeOrderEntity entity = this.findOne(oid);
				if (!entity.getPublisherCloseStatus().equals(InvestorTradeOrderEntity.TRADEORDER_investorCloseStatus_closePayFailed) &&
						!entity.getPublisherCloseStatus().equals(InvestorTradeOrderEntity.TRADEORDER_investorCloseStatus_closeSubmitFailed)){
					throw new AMPException(entity.getOrderCode() + "结算状态错误!");
				}
				PublisherBaseAccountEntity pub = entity.getPublisherBaseAccount();
				if (map.containsKey(pub.getOid())){
					List<InvestorTradeOrderEntity> list = map.get(pub.getOid());
					list.add(entity);
					map.put(pub.getOid(), list);
				}else{
					List<InvestorTradeOrderEntity> list = new ArrayList<InvestorTradeOrderEntity>();
					list.add(entity);
					map.put(pub.getOid(), list);
					pubList.add(pub);
				}
			}
			
			for (PublisherBaseAccountEntity account : pubList){
				List<InvestorTradeOrderEntity> list = map.get(account.getOid());
				investorTradeOrderBatchPayService.transferAndBatchPay(account, list);
			}
		}
		
		return resp;
	}

	/**
	 * 累计借款总额统计
	 */
	public List<Object[]> getTotalLoanAmount() {
		return investorTradeOrderDao.getTotalLoanAmount();
	}
	
	/**
	 * 累计还款总额统计
	 */
	public List<Object[]> getTotalReturnAmount() {
		return investorTradeOrderDao.getTotalReturnAmount();
	}

	/**
	 * 今日借款统计
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public List<Object[]> getTotalTLoanAmount(String startTime, String endTime) {
		return investorTradeOrderDao.getTotalTLoanAmount(startTime, endTime);
	}
	
	/**
	 * 获取订单的卡券金额
	 */
	public BigDecimal getVoucherAmount(InvestorTradeOrderEntity orderEntity) {
		TradeOrderCouponEntity en = tradeOrderCouponService.findByInvestorTradeOrder(orderEntity);
		
		if (null != en && en.getCouponType().equals(TradeOrderCouponEntity.TRADEORDERCOUPON_type_coupon)) {
			return en.getCouponAmount();
		} else {
			return BigDecimal.ZERO;
		}
	}

	public InvestorTradeOrderEntity findBywishplanOid(String wishplanOid,String orderType) {
		
		return investorTradeOrderDao.findByWishplan(wishplanOid,orderType);
	}

	public InvestorTradeOrderEntity findByWishplanOidAndProductOidAndType(String wishplanOid ,String  productOid ,List<String> Type){
		return this.investorTradeOrderDao.findByWishplanOidAndProductOidAndType(wishplanOid ,productOid,Type);
	}

	public List<InvestorTradeOrderEntity> findByInvestorOid(String userOid,String type) {
		
		return this.investorTradeOrderDao.findByInvestorOid(userOid,type);
	}
	
}
