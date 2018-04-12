package com.guohuai.mmp.platform.tulip;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.drools.core.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.guohuai.ams.label.LabelService;
import com.guohuai.basic.component.exception.GHException;
import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.basic.component.ext.web.PageResp;
import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.DateUtil;
import com.guohuai.component.util.StringUtil;
import com.guohuai.component.web.view.Pages;
import com.guohuai.mmp.investor.bankorder.InvestorBankOrderEntity;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountEntity;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderEntity;
import com.guohuai.mmp.investor.tradeorder.TradeOrderReq;
import com.guohuai.mmp.investor.tradeorder.coupon.TradeOrderCouponEntity;
import com.guohuai.mmp.investor.tradeorder.coupon.TradeOrderCouponService;
import com.guohuai.mmp.job.JobEnum;
import com.guohuai.mmp.job.lock.JobLockService;
import com.guohuai.mmp.job.log.JobLogEntity;
import com.guohuai.mmp.job.log.JobLogFactory;
import com.guohuai.mmp.job.log.JobLogService;
import com.guohuai.mmp.platform.reserved.couponcashdetails.CouponCashDetailsService;
import com.guohuai.mmp.platform.tulip.log.TuipLogReq;
import com.guohuai.mmp.platform.tulip.log.TulipLogEntity;
import com.guohuai.mmp.platform.tulip.log.TulipLogService;
import com.guohuai.mmp.platform.tulip.rep.MyRateCouponRep;
import com.guohuai.mmp.platform.tulip.req.VerifyCouponRequest;
import com.guohuai.tulip.platform.coupon.CouponEntity;
import com.guohuai.tulip.platform.coupon.userCoupon.UserCouponService;
import com.guohuai.tulip.platform.event.EventService;
import com.guohuai.tulip.platform.eventAnno.AuthenticationEvent;
import com.guohuai.tulip.platform.eventAnno.BearerEvent;
import com.guohuai.tulip.platform.eventAnno.BindingCardEvent;
import com.guohuai.tulip.platform.eventAnno.CashEvent;
import com.guohuai.tulip.platform.eventAnno.FriendEvent;
import com.guohuai.tulip.platform.eventAnno.InvalidBidsEvent;
import com.guohuai.tulip.platform.eventAnno.InvestEvent;
import com.guohuai.tulip.platform.eventAnno.RechargeEvent;
import com.guohuai.tulip.platform.eventAnno.RedeemEvent;
import com.guohuai.tulip.platform.eventAnno.RefundEvent;
import com.guohuai.tulip.platform.eventAnno.RegisterEvent;
import com.guohuai.tulip.platform.eventAnno.SignEvent;
import com.guohuai.tulip.platform.facade.FacadeService;
import com.guohuai.tulip.platform.facade.obj.CouponInterestRep;
import com.guohuai.tulip.platform.facade.obj.CouponInterestReq;
import com.guohuai.tulip.platform.facade.obj.EventRep;
import com.guohuai.tulip.platform.facade.obj.EventReq;
import com.guohuai.tulip.platform.facade.obj.InvalidBidsReq;
import com.guohuai.tulip.platform.facade.obj.IssuedCouponReq;
import com.guohuai.tulip.platform.facade.obj.MyCouponRep;
import com.guohuai.tulip.platform.facade.obj.MyCouponReq;
import com.guohuai.tulip.schedule.notify.NotifyEventService;

import lombok.extern.slf4j.Slf4j;

/**
 * 推广平台-业务实际处理接口<br/>
 * 
 * 
 * 注意:调用推广平台接口统一使用@see TulipNewService中的接口<br/>
 * mimosa代码中和推广平台接口重发定时器中，都会调用此service中的方法。
 *
 */
@Service
@Slf4j
public class TulipService {

	Logger logger = LoggerFactory.getLogger(TulipService.class);

	@Autowired
	private CouponCashDetailsService couponCashDetailsService;
	@Autowired
	private TulipLogService tulipLogService;
	@Autowired
	private LabelService labelService;
	@Autowired
	private ApplicationEventPublisher eventPublisher;
	@Autowired
	private FacadeService facadeService;
	@Autowired
	private UserCouponService userCouponService;
	@Autowired
	private EventService eventService;
	@Autowired
	private JobLockService jobLockService;
	@Autowired
	private JobLogService jobLogService;
	@Autowired
	private TradeOrderCouponService tradeOrderCouponService;
	@Autowired
	private NotifyEventService notifyEventService;

	/** 是否启用推广平台接口(1启用,其他不启用) */
	@Value("${tulip.mmp.sdkopen:0}")
	private String tulipsdkopen;

	public void validateCouponForInvest(TradeOrderReq tradeOrderReq) {
		VerifyCouponRequest vcReq = new VerifyCouponRequest();
		vcReq.setCouponId(tradeOrderReq.getCouponId());
		vcReq.setCouponType(tradeOrderReq.getCouponType());
		vcReq.setCouponAmount(tradeOrderReq.getCouponAmount());
		vcReq.setCouponDeductibleAmount(tradeOrderReq.getCouponDeductibleAmount());
		vcReq.setOrderAmount(tradeOrderReq.getMoneyVolume());
		vcReq.setPayAmouont(tradeOrderReq.getPayAmouont());
		validateCouponForInvest(vcReq);
	}
	/**
	 * 投资单接口中校验卡券是否可用于购买此产品
	 */
	public void validateCouponForInvest(VerifyCouponRequest tradeOrderReq) {
		
		if (!this.isUseCoupon(tradeOrderReq.getCouponId())) {
			return;
		}
		
		if (!this.isAmountGreatThanZero(tradeOrderReq.getCouponAmount())) {
			throw new AMPException("卡券金额应大于0");
		}

		if (!this.isAmountGreatThanZero(tradeOrderReq.getOrderAmount())) {
			throw new AMPException("订单金额应大于0");
		}

		/**
		 * 体验金 couponDeductibleAmount = couponAmount = moneyVolume, payAmouont =
		 * 0 优惠券 couponDeductibleAmount + payAmount = moneyVolume, payAmount >
		 * 0, couponAmount >= couponDeductibleAmount 加息券 payAmouont =
		 * moneyVolume, couponDeductibleAmount = couponAmount = 2（表示2%加息）
		 */

		if (CouponEntity.COUPON_TYPE_coupon.equals(tradeOrderReq.getCouponType())) {
			/**
			 * 优惠券 couponDeductibleAmount + payAmount = moneyVolume, payAmount >
			 * 0, couponAmount >= couponDeductibleAmount
			 */
			if (!isAmountGreatThanZero(tradeOrderReq.getCouponDeductibleAmount())) {
				throw new AMPException("卡券实际抵扣金额应大于0");
			}
			
			if (!isAmountGreatThanZero(tradeOrderReq.getPayAmouont())) {
				throw new AMPException("实付金额应大于0");
			}

			if (tradeOrderReq.getCouponDeductibleAmount().compareTo(tradeOrderReq.getCouponAmount()) > 0) {
				// error.define[110009]=优惠券实际抵扣金额不能大于优惠券自身金额
				throw new AMPException(110009);
			}

			if (tradeOrderReq.getPayAmouont().add(tradeOrderReq.getCouponDeductibleAmount())
					.compareTo(tradeOrderReq.getOrderAmount()) != 0) {
				// error.define[110010]=优惠券实际抵扣金额与实付金额之和应等于订单金额
				throw new AMPException(110010);
			}

		} else if (CouponEntity.COUPON_TYPE_tasteCoupon.equals(tradeOrderReq.getCouponType())) {
			/**
			 * 体验金 couponDeductibleAmount = couponAmount = moneyVolume,
			 * payAmouont = 0
			 */
			if (!isAmountGreatThanZero(tradeOrderReq.getCouponDeductibleAmount())) {
				throw new AMPException("卡券实际抵扣金额应大于0");
			}
			
			if (!this.isAmountEqualZero(tradeOrderReq.getPayAmouont())) {
				throw new AMPException("实付金额 应为0");
			}

			if (tradeOrderReq.getOrderAmount().compareTo(tradeOrderReq.getCouponDeductibleAmount()) != 0) {
				throw new AMPException("卡券实际抵扣金额不等于订单金额");
			}
			if (tradeOrderReq.getOrderAmount().compareTo(tradeOrderReq.getCouponAmount()) != 0) {
				throw new AMPException("卡券金额不等于订单金额");
			}
			if (tradeOrderReq.getCouponAmount().compareTo(tradeOrderReq.getCouponDeductibleAmount()) != 0) {
				throw new AMPException("卡券实际抵扣金额不等于卡券金额");
			}

		} else if (CouponEntity.COUPON_TYPE_rateCoupon.equals(tradeOrderReq.getCouponType())) {
			/**
			 * 加息券 payAmouont = moneyVolume, 
			 * couponDeductibleAmount =0
			 *  couponAmount = 2（表示2%加息）
			 */
			if (!isAmountGreatThanZero(tradeOrderReq.getCouponAmount())) {
				throw new AMPException("加息率应大于0");
			}
			if (!isAmountGreatThanZero(tradeOrderReq.getPayAmouont())) {
				throw new AMPException("实付金额应大于0");
			}

			if (tradeOrderReq.getOrderAmount().compareTo(tradeOrderReq.getPayAmouont()) != 0) {
				throw new AMPException("实付金额应等于订单金额");
			}

//			if (tradeOrderReq.getCouponDeductibleAmount().compareTo(tradeOrderReq.getCouponAmount()) != 0) {
//				throw new AMPException("卡券实际抵扣金额不等于卡券金额");
//			}

		}  else {
			// error.define[110015]=非法的优惠券类型
			throw new AMPException(110015);
		}
	}
	

	
	/**
	 * 使用卡券
	 * @param couponId
	 * @return
	 */
	public MyCouponRep useCoupon(String couponId) {
		MyCouponRep rep = new MyCouponRep();
		//判断运营推广是否启用
		if(isSdkEnable()){
			if (!this.isUseCoupon(couponId)) {
				return rep;
			}
			MyCouponReq req=new MyCouponReq();
			req.setCouponId(couponId);
			rep=useCoupon(req);
		}
		return rep;
	}
	public MyCouponRep useCoupon(MyCouponReq req){
		MyCouponRep myCouponRep =new MyCouponRep();
		BaseResp rep=new BaseResp();
		try {
			if(StringUtils.isEmpty(req.getCouponId())){
				throw new GHException("卡券ID不能为空!");
			}
			myCouponRep = this.facadeService.useUserCoupon(req);
			log.info(JSONObject.toJSONString(myCouponRep));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			rep.setErrorCode(-1);
			rep.setErrorMessage("使用卡券失败");
		}
		writeLog(req,rep,TulipLogEntity.TULIP_TYPE.TULIP_TYPE_USECOUPON.getInterfaceCode(),TulipLogEntity.TULIP_TYPE.TULIP_TYPE_USECOUPON.getInterfaceName() );
		if(0 != rep.getErrorCode()){
			throw new GHException(rep.getErrorMessage());
		}
		return myCouponRep;
	}
	/**
	 * 重置卡券
	 * 将使用了的卡券设为未使用
	 * @param couponId
	 * @return
	 */
	public MyCouponRep resetCoupon(String couponId){
		MyCouponRep rep =new MyCouponRep();
		//判断运营推广是否启用
		if(isSdkEnable()){
			if (!this.isUseCoupon(couponId)) {
				return rep;
			}
			MyCouponReq req=new MyCouponReq();
			req.setCouponId(couponId);
			rep=resetCoupon(req);
		}
		return rep;
	}
	public MyCouponRep resetCoupon(MyCouponReq req){
		BaseResp rep= new BaseResp();
		MyCouponRep myCouponRep = new MyCouponRep();
		try {
			if(StringUtils.isEmpty(req.getCouponId())){
				throw new GHException("卡券ID不能为空!");
			}
			myCouponRep = this.facadeService.resetUserCoupon(req);
			log.info(JSONObject.toJSONString(myCouponRep));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			rep.setErrorCode(-1);
			rep.setErrorMessage(AMPException.getStacktrace(e));
		}
		writeLog(req,rep,TulipLogEntity.TULIP_TYPE.TULIP_TYPE_RESETCOUPON.getInterfaceCode(),TulipLogEntity.TULIP_TYPE.TULIP_TYPE_RESETCOUPON.getInterfaceName());
		return myCouponRep;
	}
	
	/**
	 * 向推广平台发送申购成功事件
	 */
	public void sendInvestOK(InvestorTradeOrderEntity orderEntity) {
		//判断运营推广是否启用
		if(isSdkEnable()){
			InvestEvent event = new InvestEvent();
			event.setOrderStatus(TulipConstants.STATUS_SUCCESS);
			if (orderEntity.getUsedCoupons().equals(InvestorTradeOrderEntity.TRADEORDER_usedCoupons_yes)){
				TradeOrderCouponEntity coupon = tradeOrderCouponService.findByInvestorTradeOrder(orderEntity);
				event.setCouponId(coupon.getCoupons());// 卡券编号
				event.setDiscount(coupon.getCouponAmount());// 卡券金额
			}else{
				event.setCouponId(null);// 卡券编号
				event.setDiscount(null);// 卡券金额
			}
			event.setOrderType(orderEntity.getOrderType());// 订单类型
			event.setOrderCode(orderEntity.getOrderCode());// 订单流水号
			event.setCreateTime(orderEntity.getCreateTime());// 订单时间
			event.setUserId(orderEntity.getInvestorBaseAccount().getOid());// 用户ID
			event.setProductId(orderEntity.getProduct().getOid());// 产品编号
			event.setUserAmount(orderEntity.getPayAmount());// 实际支付金额
			event.setProductName(orderEntity.getProduct().getName());// 产品名称
			event.setOrderAmount(orderEntity.getOrderAmount());// 订单金额
			//不同的系统类型，处理的申购事件的方法不一样
			event.setSystemType(InvestEvent.systemType_GH);
//			event.setAsync(false);
			this.sendInvest(event);
		}
	}
	/**
	 * 向推广平台发送申购失败事件
	 */
	public void sendInvestFail(InvestorTradeOrderEntity orderEntity) {
		//判断运营推广是否启用
		if(isSdkEnable()){
			InvestEvent event = new InvestEvent();
			event.setOrderStatus(TulipConstants.STATUS_FAIL);
			if (orderEntity.getUsedCoupons().equals(InvestorTradeOrderEntity.TRADEORDER_usedCoupons_yes)){
				TradeOrderCouponEntity coupon = tradeOrderCouponService.findByInvestorTradeOrder(orderEntity);
				event.setCouponId(coupon.getCoupons());// 卡券编号
				event.setDiscount(coupon.getCouponAmount());// 卡券金额
			}else{
				event.setCouponId(null);// 卡券编号
				event.setDiscount(null);// 卡券金额
			}
			event.setOrderType(orderEntity.getOrderType());// 订单类型
			event.setOrderCode(orderEntity.getOrderCode());// 订单流水号
			event.setCreateTime(orderEntity.getCreateTime());// 订单时间
			event.setUserId(orderEntity.getInvestorBaseAccount().getOid());// 用户ID
			event.setProductId(orderEntity.getProduct().getOid());// 产品编号
			event.setUserAmount(orderEntity.getPayAmount());// 实际支付金额
			event.setProductName(orderEntity.getProduct().getName());// 产品名称
			event.setOrderAmount(orderEntity.getOrderAmount());// 订单金额
			//不同的系统类型，处理的申购事件的方法不一样
			event.setSystemType(InvestEvent.systemType_GH);
//			event.setAsync(false);
			this.sendInvest(event);
		}
	}
	
	public void sendInvest(InvestEvent event) {
		BaseResp rep = new BaseResp();
		try {
			if(StringUtils.isEmpty(event.getUserId())){
				throw new GHException("用户ID不能为空!");
			}
			this.eventPublisher.publishEvent(JSONObject.toJSONString(event));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			rep.setErrorCode(-1);
			rep.setErrorMessage("投资事件异常!");
		}
		writeLog(event, rep, TulipLogEntity.TULIP_TYPE.TULIP_TYPE_INVESTMENT.getInterfaceCode(),
				TulipLogEntity.TULIP_TYPE.TULIP_TYPE_INVESTMENT.getInterfaceName());
	}
	

	
	/**
	 * 向推广平台发送注册事件
	 */
	public void onRegister(InvestorBaseAccountEntity investorAccount, InvestorBaseAccountEntity friendAccount) {
		//判断运营推广是否启用
		if(isSdkEnable()){
			RegisterEvent event = new RegisterEvent();
			event.setUserId(investorAccount.getOid());
			event.setPhone(investorAccount.getPhoneNum());
			if (null != friendAccount) {
				event.setFriendId(friendAccount.getOid());
			}
	
			this.onRegister(event);
		}
	}
	public void onRegister(RegisterEvent event) {
		BaseResp rep = new BaseResp();
		try {
			if(StringUtils.isEmpty(event.getUserId())){
				throw new GHException("用户ID不能为空!");
			}
			this.eventPublisher.publishEvent(JSONObject.toJSONString(event));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			rep.setErrorCode(-1);
			rep.setErrorMessage("注册事件异常!");
		}
		writeLog(event, rep, TulipLogEntity.TULIP_TYPE.TULIP_TYPE_REGISTER.getInterfaceCode(),
				TulipLogEntity.TULIP_TYPE.TULIP_TYPE_REGISTER.getInterfaceName());
	}
	
	
	/**
	 * 向推广平台发送实名认证事件
	 */
	public void onSetRealName(InvestorBaseAccountEntity baseAccount) {
		//判断运营推广是否启用
		if(isSdkEnable()){
			AuthenticationEvent event = new AuthenticationEvent();
			event.setUserId(baseAccount.getOid());
			event.setName(baseAccount.getRealName());
			if (null != baseAccount.getIdNum() && baseAccount.getIdNum().length() > 16) {
				String birthStr = baseAccount.getIdNum().substring(6, 14);
				event.setBirthday(DateUtil.formatUtilToSql(DateUtil.parseDate(birthStr, "yyyyMMdd")));
			}
			this.onSetRealName(event);
		}
	}

	public void onSetRealName(AuthenticationEvent event) {
		BaseResp rep = new BaseResp();
		try {
			if(StringUtils.isEmpty(event.getUserId())){
				throw new GHException("用户ID不能为空!");
			}
			this.eventPublisher.publishEvent(JSONObject.toJSONString(event));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			rep.setErrorCode(-1);
			rep.setErrorMessage(AMPException.getStacktrace(e));
		}
		writeLog(event, rep, TulipLogEntity.TULIP_TYPE.TULIP_TYPE_SETREALNAME.getInterfaceCode(),
				TulipLogEntity.TULIP_TYPE.TULIP_TYPE_SETREALNAME.getInterfaceName());
	}
	
	/**
	 * 记录TULIP_LOG日志
	 * @param req
	 * @param rep
	 * @param interfaceCode
	 * @param interfaceName
	 */
	private void writeLog(Object req, BaseResp rep, String interfaceCode, String interfaceName) {
		TuipLogReq lreq = new TuipLogReq();
		lreq.setErrorCode(rep.getErrorCode());
		lreq.setErrorMessage(rep.getErrorMessage());
		lreq.setInterfaceName(interfaceName);
		lreq.setInterfaceCode(interfaceCode);
		lreq.setSendObj(JSONObject.toJSONString(req));
		lreq.setSendedTimes(1);
		this.tulipLogService.createTulipLogEntity(lreq);
	}
	
	/**
	 * 发送赎回事件
	 */
	public void onRedeem(InvestorTradeOrderEntity orderEntity) {
		//判断运营推广是否启用
		if(isSdkEnable()){
			RedeemEvent event = new RedeemEvent();
			event.setUserId(orderEntity.getInvestorBaseAccount().getOid());
			event.setProductId(orderEntity.getProduct().getOid());
			event.setProductName(orderEntity.getProduct().getName());
			event.setOrderCode(orderEntity.getOrderCode());
			event.setOrderType(orderEntity.getOrderType());
			event.setUserAmount(orderEntity.getOrderAmount());
			event.setCreateTime(orderEntity.getOrderTime());
			this.onRedeem(event);
		}
	}

	public void onRedeem(RedeemEvent event) {
		BaseResp rep = new BaseResp();
		try {
			if(StringUtils.isEmpty(event.getUserId())){
				throw new GHException("用户ID不能为空!");
			}
			this.eventPublisher.publishEvent(JSONObject.toJSONString(event));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			rep.setErrorCode(-1);
			rep.setErrorMessage(AMPException.getStacktrace(e));
		}
		writeLog(event, rep, TulipLogEntity.TULIP_TYPE.TULIP_TYPE_REDEEM.getInterfaceCode(),
				TulipLogEntity.TULIP_TYPE.TULIP_TYPE_REDEEM.getInterfaceName());
	}
	
	/**
	 * 向推广平台发送提现事件(从余额直接体现)
	 * 
	 */
	public void onCash(InvestorBankOrderEntity bankOrder) {
		//判断运营推广是否启用
		if(isSdkEnable()){
			CashEvent event = new CashEvent();
			event.setOrderCode(bankOrder.getOrderCode());
			event.setCreateTime(bankOrder.getOrderTime());
			event.setUserId(bankOrder.getInvestorBaseAccount().getOid());
			event.setUserAmount(bankOrder.getOrderAmount());
			event.setOrderStatus(TulipConstants.STATUS_SUCCESS);
			event.setOrderType("cash");
			this.onCash(event);
		}
	}

	public void onCash(CashEvent event) {
		BaseResp rep = new BaseResp();
		try {
			if(StringUtils.isEmpty(event.getUserId())){
				throw new GHException("用户ID不能为空!");
			}
			this.eventPublisher.publishEvent(JSONObject.toJSONString(event));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			rep.setErrorCode(-1);
			rep.setErrorMessage(AMPException.getStacktrace(e));
		}
		writeLog(event, rep, TulipLogEntity.TULIP_TYPE.TULIP_TYPE_CASH.getInterfaceCode(),
				TulipLogEntity.TULIP_TYPE.TULIP_TYPE_CASH.getInterfaceName());
	}
	/**
	 * 充值事件
	 */
	public void onRecharge(InvestorBankOrderEntity bankOrder){
		//判断运营推广是否启用
		if(isSdkEnable()){
			RechargeEvent event = new RechargeEvent();
			event.setOrderCode(bankOrder.getOrderCode());
			event.setCreateTime(bankOrder.getOrderTime());
			event.setUserId(bankOrder.getInvestorBaseAccount().getOid());
			event.setUserAmount(bankOrder.getOrderAmount());
			event.setOrderStatus(TulipConstants.STATUS_SUCCESS);
			event.setOrderType("recharge");
			this.onRecharge(event);
		}
	}
	public void onRecharge(RechargeEvent event){
		BaseResp rep = new BaseResp();
		try {
			if(StringUtils.isEmpty(event.getUserId())){
				throw new GHException("用户ID不能为空!");
			}
			this.eventPublisher.publishEvent(JSONObject.toJSONString(event));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			rep.setErrorCode(-1);
			rep.setErrorMessage(AMPException.getStacktrace(e));
		}
		writeLog(event, rep, TulipLogEntity.TULIP_TYPE.TULIP_TYPE_RECHARGE.getInterfaceCode(),
				TulipLogEntity.TULIP_TYPE.TULIP_TYPE_RECHARGE.getInterfaceName());
	}
	
	public void onInvalidBids(InvalidBidsReq req) {
		//判断运营推广是否启用
		if(isSdkEnable()){
			InvalidBidsEvent event=new InvalidBidsEvent();
			event.setProductId(req.getProductId());
			this.onInvalidBids(event);
		}
	}
	public void onInvalidBids(InvalidBidsEvent event) {
		BaseResp baesRep = new BaseResp();
		try {
			if(StringUtils.isEmpty(event.getProductId())){
				throw new GHException("产品ID不能为空!");
			}
			this.eventPublisher.publishEvent(JSONObject.toJSONString(event));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			baesRep.setErrorCode(-1);
			baesRep.setErrorMessage(AMPException.getStacktrace(e));
		}
		writeLog(event, baesRep, TulipLogEntity.TULIP_TYPE.TULIP_TYPE_INVALIDBIDS.getInterfaceCode() ,TulipLogEntity.TULIP_TYPE.TULIP_TYPE_INVALIDBIDS.getInterfaceName());
	}
	/**
	 * 向推广平台发送推荐人事件
	 * 
	 * @param account
	 *            :注册人
	 * @param recommender
	 *            :推荐人
	 */
	public void onReferee(InvestorBaseAccountEntity account, InvestorBaseAccountEntity recommender) {
		//判断运营推广是否启用
		if(isSdkEnable()){
			FriendEvent event = new FriendEvent();
			event.setUserId(account.getOid());
			event.setPhone(account.getPhoneNum());// 手机号
			if (recommender != null) {
				event.setFriendId(recommender.getOid());// 推荐人
			}
			onReferee(event);
		}
	}
	public void onReferee(FriendEvent event){
		BaseResp rep = new BaseResp();
		try {
			if(StringUtils.isEmpty(event.getFriendId())){
				throw new GHException("推荐人用户ID不能为空!");
			}
			this.eventPublisher.publishEvent(JSONObject.toJSONString(event));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			rep.setErrorCode(-1);
			rep.setErrorMessage(AMPException.getStacktrace(e));
		}
		writeLog(event, rep, TulipLogEntity.TULIP_TYPE.TULIP_TYPE_REFEREE.getInterfaceCode(),
					TulipLogEntity.TULIP_TYPE.TULIP_TYPE_REFEREE.getInterfaceName());
	}
	/**
	 * 向推广平台发送到期兑付事件
	 */
	public void onBearer(InvestorTradeOrderEntity order) {
		//判断运营推广是否启用
		if(isSdkEnable()){
			BearerEvent event = new BearerEvent();
			event.setUserId(order.getInvestorBaseAccount().getOid());// 投资者oid
			event.setOrderCode(order.getOrderCode());// 订单流水号
			event.setOrderType(order.getOrderType());// 订单类型
			event.setUserAmount(order.getOrderAmount());// 订单金额
			event.setCreateTime(order.getCreateTime());// 订单创建时间
			event.setProductId(order.getProduct().getOid());// 产品oid
			event.setProductName(order.getProduct().getName());// 产品名称
			this.onBearer(event);
		}
	}

	public void onBearer(BearerEvent event) {
		BaseResp rep=new BaseResp();
		try {
			this.eventPublisher.publishEvent(JSONObject.toJSONString(event));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			rep.setErrorCode(-1);
			rep.setErrorMessage(AMPException.getStacktrace(e));
		}
		writeLog(event, rep, TulipLogEntity.TULIP_TYPE.TULIP_TYPE_BEARER.getInterfaceCode(),
				TulipLogEntity.TULIP_TYPE.TULIP_TYPE_BEARER.getInterfaceName());
	}

	/**
	 * 向推广平台发送退款事件
	 * @param order
	 */
	public void onRefund(InvestorTradeOrderEntity order) {
		//判断运营推广是否启用
		if(isSdkEnable()){
			RefundEvent event = new RefundEvent();
			event.setOrderCode(order.getOrderCode());
			event.setUserId(order.getInvestorBaseAccount().getOid());
			this.onRefund(event);
		}
	}

	
	public void onRefund(RefundEvent event) {
		BaseResp rep = new BaseResp();
		try {
			if(StringUtils.isEmpty(event.getUserId())){
				throw new GHException("用户ID不能为空!");
			}
			this.eventPublisher.publishEvent(JSONObject.toJSONString(event));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			rep.setErrorCode(-1);
			rep.setErrorMessage(AMPException.getStacktrace(e));
		}
		writeLog(event, rep, TulipLogEntity.TULIP_TYPE.TULIP_TYPE_REFUND.getInterfaceCode(),
				TulipLogEntity.TULIP_TYPE.TULIP_TYPE_REFUND.getInterfaceName());
	}
	/**
	 * 签到方法
	 * @param userId
	 * @return
	 */
	public void onSign(String userId) {
		//判断运营推广是否启用
		if(isSdkEnable()){
			SignEvent event = new SignEvent();
			event.setUserId(userId);
			onSign(event);
		}
	}
	public void onSign(SignEvent event) {
		BaseResp rep = new BaseResp();
		try {
			if(StringUtils.isEmpty(event.getUserId())){
				throw new GHException("用户ID不能为空!");
			}
			this.eventPublisher.publishEvent(JSONObject.toJSONString(event));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			rep.setErrorCode(-1);
			rep.setErrorMessage(AMPException.getStacktrace(e));
		}
		writeLog(event, rep, TulipLogEntity.TULIP_TYPE.TULIP_TYPE_SIGN.getInterfaceCode(),
				TulipLogEntity.TULIP_TYPE.TULIP_TYPE_SIGN.getInterfaceName());
	}
	/**
	 * 绑卡事件
	 * @param account
	 */
	public void onBindingCard(InvestorBaseAccountEntity account){
		//判断运营推广是否启用
		if(isSdkEnable()){
			BindingCardEvent event = new BindingCardEvent();
			event.setUserId(account.getOid());
			this.onBindingCard(event);
		}
	}
	public void onBindingCard(BindingCardEvent event){
		BaseResp rep = new BaseResp();
		try {
			if(StringUtils.isEmpty(event.getUserId())){
				throw new GHException("用户ID不能为空!");
			}
			this.eventPublisher.publishEvent(JSONObject.toJSONString(event));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			rep.setErrorCode(-1);
			rep.setErrorMessage(AMPException.getStacktrace(e));
		}
		writeLog(event, rep, TulipLogEntity.TULIP_TYPE.TULIP_TYPE_BINDINGCARD.getInterfaceCode(),
				TulipLogEntity.TULIP_TYPE.TULIP_TYPE_BINDINGCARD.getInterfaceName());
	}
	/**
	 * 卡券备付金清算时，卡券核销<br/>
	 * (返回码0推广平台返回成功，110002推广平台返回失败,110001请求推广平台异常)
	 * 
	 * @param verifacationsInfos
	 *            :需要核销的卡券信息
	 * 
	 * @return 核销结果(返回码0成功，110002失败,110001请求异常。仅此3个返回码)
	 */
	public BaseResp verificationCouponForLiquidation(MyCouponReq... verifacationsInfos) {
		BaseResp rep=new BaseResp();
		//判断运营推广是否启用
		if(isSdkEnable()){
			List<MyCouponReq> list = new ArrayList<MyCouponReq>();
			if (verifacationsInfos != null && verifacationsInfos.length > 0) {
				for (MyCouponReq mimosaReq : verifacationsInfos) {
	
					MyCouponReq tulipReq = new MyCouponReq();
					// 卡券编号
					tulipReq.setCouponId(mimosaReq.getCouponId());
					// 产品oid
					tulipReq.setProductId(mimosaReq.getProductId());
	
					list.add(tulipReq);
				}
			}
			rep=this.verificationCouponCommon(list, true);
		}
		return rep;

	}

	/**
	 * 卡券核销请求重发时，调用核销接口
	 * 
	 * @param req
	 *            :需要核销的卡券信息
	 * 
	 * @return 核销结果(返回码0成功，110002失败,110001请求异常。仅此3个返回码)
	 */
	public BaseResp verificationCouponForResend(List<MyCouponReq> req) {
		BaseResp rep=new BaseResp();
		//判断运营推广是否启用
		if(isSdkEnable()){
			rep=this.verificationCouponCommon(req, false);
		}
		return rep;
	}

	/**
	 * 卡券核销SDK公共入口<br/>
	 * 1.卡券清算时，需要调用核销接口 <br/>
	 * 2.卡券平台核销接口重发时，也要调用核销接口<br/>
	 * 3.根据核销结果，更新卡券核销明细表的卡券核销状态
	 */
	private BaseResp verificationCouponCommon(List<MyCouponReq> req, boolean saveTulipSdkLog) {

		// 请求推广平台结果
		this.facadeService.verificationCoupon(req);
		// 更新卡券核销明细状态
		BaseResp baseRep = updateVerficationStatus(req);
		// 返回核销结果
		return baseRep;
	}

	/** 更新卡券核销明细状态 */
	private BaseResp updateVerficationStatus(List<MyCouponReq> tulipParam) {
		// 卡券编码列表
		List<String> couponIdList = new ArrayList<String>();
		for (MyCouponReq req : tulipParam) {
			couponIdList.add(req.getCouponId());// 卡券ID
		}
		couponCashDetailsService.cancelDo(couponIdList);
		return new BaseResp();
	}

	/**
	 * 计算加息金额
	 * 
	 * @param userOid
	 *            :投资者编号
	 * @param couponID
	 *            :卡券编号
	 * @param orderCode
	 *            :订单流水号
	 * @param orderAmount
	 *            :订单金额
	 * @param days
	 *            :定期产品存续期天数
	 * @param incomeCalcBasis
	 *            :收益计算基础(360或365计息)
	 * @return
	 */
	protected MyRateCouponRep countRateCoupon(String userOid, String couponID, String orderCode, BigDecimal orderAmount,
			int days, int incomeCalcBasis) {
		MyRateCouponRep rep = new MyRateCouponRep();
		//判断运营推广是否启用
		if(isSdkEnable()){
			CouponInterestReq req = new CouponInterestReq();
			req.setUserId(userOid);
			req.setCouponId(couponID);
			req.setOrderCode(orderCode);
			req.setOrderAmount(orderAmount);
			req.setDays(days);
			req.setYearDay(incomeCalcBasis);
	
			CouponInterestRep tulipResult = this.facadeService.couponInterest(req);
			// 推广平台返回成功时，获取其余参数信息
			if (tulipResult.getErrorCode() == TulipConstants.ERRORCODE_MIMOSA_0) {
				CouponInterestRep tulip = ((CouponInterestRep) tulipResult);
				rep.setRateAmount(tulip.getCouponAmount());// 加息金额
			}
		}
		return rep;

	}

	

	/**
	 * 获取个人的券码列表（分页）
	 * 
	 * @param userOid
	 *            :投资者Oid
	 * @param status
	 *            :(可送空)卡券状态,unused-未使用,use-已使用,writeOff-核销,expired-过期
	 * @param type
	 *            :(可送空)卡券类型,redPackets-红包,coupon-优惠券,rateCoupon-加息券,tasteCoupon-体验金
	 * @param page
	 *            : 页号
	 * @param rows
	 *            :分页大小
	 */
	public PageResp<MyCouponRep> getMyAllCouponList(String userOid, String status, String type, int page,
			int rows) {
		PageResp<MyCouponRep> pageResp  =new PageResp<MyCouponRep>();
		//判断运营推广是否启用
		if(isSdkEnable()){
			MyCouponReq req = new MyCouponReq();
			req.setStatus(status);
			req.setUserId(userOid);
			req.setPage(page);
			req.setRows(rows);
			req.setType(type);
			
			pageResp = this.facadeService.getMyCouponList(req);
		}
		return pageResp;
	}
	
	public MyCouponRep getMyAllCouponNum(String userId){
		MyCouponRep rep =new MyCouponRep();
		//判断运营推广是否启用
		if(isSdkEnable()){
			MyCouponReq req=new MyCouponReq();
			req.setUserId(userId);
			rep=this.facadeService.getMyCouponNum(req);
		}
		return rep;
	}

	/**
	 * 获取个人可用于某产品的券码列表
	 * 
	 * @param userOid
	 *            :投资者Oid
	 * @param proOid
	 *            :(可送空)产品Oid
	 * @param investmentAmount
	 *            :(可送空)申购金额
	 */
	protected PageResp<MyCouponRep> getMyCouponList(String userOid, String proOid) {
		PageResp<MyCouponRep>  pageResp=new PageResp<MyCouponRep>();
		//判断运营推广是否启用
		if(isSdkEnable()){
			MyCouponReq req = new MyCouponReq();
			req.setUserId(userOid);
			List<String> labelCodes=labelService.findLabelCodeByProductId(proOid);
			req.setLabelCodes(labelCodes);
			pageResp = this.facadeService.getCouponList(req);
		}
		return pageResp;
	}

	/**
	 * 获取某个券信息
	 */
	public MyCouponRep getCouponDetail(String couponId) {
		MyCouponRep rep =new MyCouponRep();
		//判断运营推广是否启用
		if(isSdkEnable()){
			MyCouponReq req = new MyCouponReq();
			req.setCouponId(couponId);
			rep = this.facadeService.getCouponDetail(req);
		}
		return rep;
	}

	/**
	 * 下发卡券
	 * 
	 * @param userOid
	 *            :用户oid
	 * @param eventId
	 *            :活动ID
	 */
	public void issuedCoupon(IssuedCouponReq req) {
		//判断运营推广是否启用
		if(isSdkEnable()){
			BaseResp rep = new BaseResp();
			try {
				this.facadeService.issuedCouponByEventId(req.getUserId(), req.getEventId());
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				rep.setErrorCode(-1);
				rep.setErrorMessage(AMPException.getStacktrace(e));
			}
			//日志记录
			writeLog(req, rep, TulipLogEntity.TULIP_TYPE.TULIP_TYPE_ISUEDCOUPON.getInterfaceCode(),
						TulipLogEntity.TULIP_TYPE.TULIP_TYPE_ISUEDCOUPON.getInterfaceName());
		}
	}

	
	
	/**
	 * 判断是否使用了卡券
	 * 
	 * @param couponId
	 *            :卡券编号
	 * @return
	 */
	public boolean isUseCoupon(String couponId) {
		return StringUtil.isEmpty(couponId) ? false : true;
	}

	
	/**
	 * 金额是否大于0,如果 大于0，返回true,否则返回false
	 */
	private boolean isAmountGreatThanZero(BigDecimal amount) {
		if (null == amount) {
			return false;
		}
		if (amount.compareTo(BigDecimal.ZERO) > 0) {
			return true;
		}
		return false;
	}
	
	/**
	 * 金额是否大于等于0,如果 大于等于0，返回true,否则返回false
	 */
	private boolean isAmountGreatThanEqualZero(BigDecimal amount) {
		if (null == amount) {
			return false;
		}
		if (amount.compareTo(BigDecimal.ZERO) >= 0) {
			return true;
		}
		return false;
	}
	/**
	 * 金额是否等于0,如果 等于0，返回true,否则返回false
	 */
	private boolean isAmountEqualZero(BigDecimal amount) {
		if (null == amount) {
			return false;
		}
		if (amount.compareTo(BigDecimal.ZERO) == 0) {
			return true;
		}
		return false;
	}
	/**
	 * 获取活动奖励信息
	 * @return
	 */
	public EventRep getEventInfo(EventReq param) {
		EventRep rep=new EventRep();
		//判断运营推广是否启用
		if(isSdkEnable()){
			rep = facadeService.getEventCouponMoneyInfo(param);
		}
		return rep;
	}
	
	
	/**
	 * 检查用户是否签到
	 * @param userId
	 * @return
	 */
	public BaseResp checkSign(String userId) {
		BaseResp rep=new BaseResp();
		//判断运营推广是否启用
		if(isSdkEnable()){
			Boolean isSign=facadeService.checkSignIn(userId);
			if(isSign){
				rep.setErrorCode(-1);
				rep.setErrorMessage("该用户已签到!");
			}
		}
		return rep;
	}
	
	/**
	 * 获取即将得到的卡券列表（还未下发到用户账户的卡券）
	 * @param eventType 事件
	 * @return
	 */
	public PageResp<MyCouponRep> getCouponWithEventSoon(String eventType) {
		PageResp<MyCouponRep> rep=new PageResp<MyCouponRep>();
		
		//判断运营推广是否启用
		if(isSdkEnable()){
			rep = eventService.findEventCouponSoon(eventType);
		}
		return rep;
	}


	/** 是否启用推广平台(true启用，false不启用) */
	public boolean isSdkEnable() {
		return (TulipConstants.OPENFLAG_TULIPSDK.equals(getSdkOpenConfig())) ? true : false;
	}
	private String getSdkOpenConfig() {
		return this.tulipsdkopen;
	}
	/**
	 * 卡券过期方法
	 */
	public void updateCouponForExpired(){
		//判断运营推广是否启用
		if(isSdkEnable()){
			if (this.jobLockService.getRunPrivilege(JobEnum.JOB_jobId_autoModifyCouponStatus.getJobId())) {
				updateCouponForExpiredLog();
			}
		}
	}
	private void updateCouponForExpiredLog(){
		JobLogEntity jobLog = JobLogFactory.getInstance(JobEnum.JOB_jobId_autoModifyCouponStatus.getJobId());

		try {
			updateCouponForExpiredDo();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			jobLog.setJobMessage(AMPException.getStacktrace(e));
			jobLog.setJobStatus(JobLogEntity.JOB_jobStatus_failed);

		}
		jobLog.setBatchEndTime(DateUtil.getSqlCurrentDate());
		this.jobLogService.saveEntity(jobLog);
		this.jobLockService.resetJob(JobEnum.JOB_jobId_autoModifyCouponStatus.getJobId());
	}
	private void updateCouponForExpiredDo(){
		this.userCouponService.updateCouponForExpired();
	}
	/**
	 * 活动自动上架
	 */
	public void autoOnEvent(){
		//判断运营推广是否启用
		if(isSdkEnable()){
			if (this.jobLockService.getRunPrivilege(JobEnum.JOB_jobId_autoOnEvent.getJobId())) {
				autoOnEventLog();
			}
		}
	}
	private void autoOnEventLog(){
		JobLogEntity jobLog = JobLogFactory.getInstance(JobEnum.JOB_jobId_autoOnEvent.getJobId());

		try {
			autoOnEventDo();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			jobLog.setJobMessage(AMPException.getStacktrace(e));
			jobLog.setJobStatus(JobLogEntity.JOB_jobStatus_failed);

		}
		jobLog.setBatchEndTime(DateUtil.getSqlCurrentDate());
		this.jobLogService.saveEntity(jobLog);
		this.jobLockService.resetJob(JobEnum.JOB_jobId_autoOnEvent.getJobId());
	}
	private void autoOnEventDo(){
		this.eventService.autoOnEvent();
	}
	/**
	 * 自动调度活动
	 */
	public void issuedCouponBySchedule() {
		//判断运营推广是否启用
		if(isSdkEnable()){
			if (this.jobLockService.getRunPrivilege(JobEnum.JOB_jobId_autoIssuedCouponBySchedule.getJobId())) {
				issuedCouponByScheduleLog();
			}
		}
	}
	
	private void issuedCouponByScheduleLog(){
		JobLogEntity jobLog = JobLogFactory.getInstance(JobEnum.JOB_jobId_autoIssuedCouponBySchedule.getJobId());

		try {
			issuedCouponByScheduleDo();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			jobLog.setJobMessage(AMPException.getStacktrace(e));
			jobLog.setJobStatus(JobLogEntity.JOB_jobStatus_failed);

		}
		jobLog.setBatchEndTime(DateUtil.getSqlCurrentDate());
		this.jobLogService.saveEntity(jobLog);
		this.jobLockService.resetJob(JobEnum.JOB_jobId_autoIssuedCouponBySchedule.getJobId());
	}
	private void issuedCouponByScheduleDo(){
		this.facadeService.issuedCouponBySchedule();
	}
	/**
	 * 用户生日活动
	 */
	public void issuedCouponByBirthDay() {
		//判断运营推广是否启用
		if(isSdkEnable()){
			if (this.jobLockService.getRunPrivilege(JobEnum.JOB_jobId_autoIssuedBirthDayCoupon.getJobId())) {
				issuedCouponByBirthDayLog();
			}
		}
	}
	private void issuedCouponByBirthDayLog(){
		JobLogEntity jobLog = JobLogFactory.getInstance(JobEnum.JOB_jobId_autoIssuedBirthDayCoupon.getJobId());

		try {
			issuedCouponByBirthDayDo();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			jobLog.setJobMessage(AMPException.getStacktrace(e));
			jobLog.setJobStatus(JobLogEntity.JOB_jobStatus_failed);

		}
		jobLog.setBatchEndTime(DateUtil.getSqlCurrentDate());
		this.jobLogService.saveEntity(jobLog);
		this.jobLockService.resetJob(JobEnum.JOB_jobId_autoIssuedBirthDayCoupon.getJobId());
	}
	private void issuedCouponByBirthDayDo() {
		this.facadeService.issuedCouponByBirthDay();
	}
	
	public void sendRegisterEvent(){
		//1,判断此job是否可执行
		if (this.jobLockService.getRunPrivilege(JobEnum.JOB_jobId_tulipEventRegister.getJobId())) {
			
			JobLogEntity jobLog = JobLogFactory.getInstance(JobEnum.JOB_jobId_tulipEventRegister.getJobId());
			
			try {
				this.notifyEventService.sendRegisterEvent();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				jobLog.setJobMessage(AMPException.getStacktrace(e));
				jobLog.setJobStatus(JobLogEntity.JOB_jobStatus_failed);
				jobLog.setBatchEndTime(DateUtil.getSqlCurrentDate());
				
				this.jobLogService.saveEntity(jobLog);
			}
			this.jobLockService.resetJob(JobEnum.JOB_jobId_tulipEventRegister.getJobId());
		}
	}
	
	public void sendFriendEvent(){
		if (this.jobLockService.getRunPrivilege(JobEnum.JOB_jobId_tulipEventFriend.getJobId())) {
			
			JobLogEntity jobLog = JobLogFactory.getInstance(JobEnum.JOB_jobId_tulipEventFriend.getJobId());
			
			try {
				this.notifyEventService.sendFriendEvent();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				jobLog.setJobMessage(AMPException.getStacktrace(e));
				jobLog.setJobStatus(JobLogEntity.JOB_jobStatus_failed);
				
				jobLog.setBatchEndTime(DateUtil.getSqlCurrentDate());
				this.jobLogService.saveEntity(jobLog);
			}
			this.jobLockService.resetJob(JobEnum.JOB_jobId_tulipEventFriend.getJobId());
			
		}
	}
	
	public void sendSetRealNameEvent(){
		if (this.jobLockService.getRunPrivilege(JobEnum.JOB_jobId_tulipEventRealname.getJobId())) {
			
			JobLogEntity jobLog = JobLogFactory.getInstance(JobEnum.JOB_jobId_tulipEventRealname.getJobId());
			
			try {
				this.notifyEventService.sendSetRealNameEvent();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				jobLog.setJobMessage(AMPException.getStacktrace(e));
				jobLog.setJobStatus(JobLogEntity.JOB_jobStatus_failed);
				
				jobLog.setBatchEndTime(DateUtil.getSqlCurrentDate());
				this.jobLogService.saveEntity(jobLog);
			}
			this.jobLockService.resetJob(JobEnum.JOB_jobId_tulipEventRealname.getJobId());
		}
	}
	
	public void sendInvestmentEvent(){
		if (this.jobLockService.getRunPrivilege(JobEnum.JOB_jobId_tulipEventInvestment.getJobId())) {
			
			JobLogEntity jobLog = JobLogFactory.getInstance(JobEnum.JOB_jobId_tulipEventInvestment.getJobId());
			
			try {
				this.notifyEventService.sendInvestmentEvent();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				jobLog.setJobMessage(AMPException.getStacktrace(e));
				jobLog.setJobStatus(JobLogEntity.JOB_jobStatus_failed);
				
				jobLog.setBatchEndTime(DateUtil.getSqlCurrentDate());
				this.jobLogService.saveEntity(jobLog);
			}
			this.jobLockService.resetJob(JobEnum.JOB_jobId_tulipEventInvestment.getJobId());
		}
	}
	public void sendRedeemEvent(){
		if (this.jobLockService.getRunPrivilege(JobEnum.JOB_jobId_tulipEventRedeem.getJobId())) {
			
			JobLogEntity jobLog = JobLogFactory.getInstance(JobEnum.JOB_jobId_tulipEventRedeem.getJobId());
			
			try {
				this.notifyEventService.sendRedeemEvent();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				jobLog.setJobMessage(AMPException.getStacktrace(e));
				jobLog.setJobStatus(JobLogEntity.JOB_jobStatus_failed);
				
				jobLog.setBatchEndTime(DateUtil.getSqlCurrentDate());
				this.jobLogService.saveEntity(jobLog);
			}
			this.jobLockService.resetJob(JobEnum.JOB_jobId_tulipEventRedeem.getJobId());
		}
	}
	
	public void sendBearerEvent(){
		if (this.jobLockService.getRunPrivilege(JobEnum.JOB_jobId_tulipEventBeare.getJobId())) {
			
			JobLogEntity jobLog = JobLogFactory.getInstance(JobEnum.JOB_jobId_tulipEventBeare.getJobId());
			
			try {
				this.notifyEventService.sendBearerEvent();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				jobLog.setJobMessage(AMPException.getStacktrace(e));
				jobLog.setJobStatus(JobLogEntity.JOB_jobStatus_failed);
				
				jobLog.setBatchEndTime(DateUtil.getSqlCurrentDate());
				this.jobLogService.saveEntity(jobLog);
			}
			this.jobLockService.resetJob(JobEnum.JOB_jobId_tulipEventBeare.getJobId());
		}
	}
	
	public void sendCashEvent(){
		if (this.jobLockService.getRunPrivilege(JobEnum.JOB_jobId_tulipEventCash.getJobId())) {
			
			JobLogEntity jobLog = JobLogFactory.getInstance(JobEnum.JOB_jobId_tulipEventCash.getJobId());
			
			try {
				this.notifyEventService.sendCashEvent();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				jobLog.setJobMessage(AMPException.getStacktrace(e));
				jobLog.setJobStatus(JobLogEntity.JOB_jobStatus_failed);
				
				jobLog.setBatchEndTime(DateUtil.getSqlCurrentDate());
				this.jobLogService.saveEntity(jobLog);
			}
			this.jobLockService.resetJob(JobEnum.JOB_jobId_tulipEventCash.getJobId());
		}
	}
	
	public void sendRefundEvent(){
		if (this.jobLockService.getRunPrivilege(JobEnum.JOB_jobId_tulipEventRefund.getJobId())) {
			
			JobLogEntity jobLog = JobLogFactory.getInstance(JobEnum.JOB_jobId_tulipEventRefund.getJobId());
			
			try {
				this.notifyEventService.sendRefundEvent();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				jobLog.setJobMessage(AMPException.getStacktrace(e));
				jobLog.setJobStatus(JobLogEntity.JOB_jobStatus_failed);
				
				jobLog.setBatchEndTime(DateUtil.getSqlCurrentDate());
				this.jobLogService.saveEntity(jobLog);
			}
			this.jobLockService.resetJob(JobEnum.JOB_jobId_tulipEventRefund.getJobId());
		}
	}
	
	public void sendBindingCardEvent(){
		if (this.jobLockService.getRunPrivilege(JobEnum.JOB_jobId_tulipEventBingd.getJobId())) {
			
			JobLogEntity jobLog = JobLogFactory.getInstance(JobEnum.JOB_jobId_tulipEventBingd.getJobId());
			
			try {
				this.notifyEventService.sendBindingCardEvent();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				jobLog.setJobMessage(AMPException.getStacktrace(e));
				jobLog.setJobStatus(JobLogEntity.JOB_jobStatus_failed);
				
				jobLog.setBatchEndTime(DateUtil.getSqlCurrentDate());
				this.jobLogService.saveEntity(jobLog);
			}
			this.jobLockService.resetJob(JobEnum.JOB_jobId_tulipEventBingd.getJobId());
		}
	}
	
	public void sendRechargeEvent(){
		if (this.jobLockService.getRunPrivilege(JobEnum.JOB_jobId_tulipEventRecharge.getJobId())) {
			
			JobLogEntity jobLog = JobLogFactory.getInstance(JobEnum.JOB_jobId_tulipEventRecharge.getJobId());
			
			try {
				this.notifyEventService.sendRechargeEvent();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				jobLog.setJobMessage(AMPException.getStacktrace(e));
				jobLog.setJobStatus(JobLogEntity.JOB_jobStatus_failed);
				
				jobLog.setBatchEndTime(DateUtil.getSqlCurrentDate());
				this.jobLogService.saveEntity(jobLog);
			}
			this.jobLockService.resetJob(JobEnum.JOB_jobId_tulipEventRecharge.getJobId());
		}
	}
	
	public void sendSignEvent(){
		if (this.jobLockService.getRunPrivilege(JobEnum.JOB_jobId_tulipEventSign.getJobId())) {
			
			JobLogEntity jobLog = JobLogFactory.getInstance(JobEnum.JOB_jobId_tulipEventSign.getJobId());
			
			try {
				this.notifyEventService.sendSignEvent();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				jobLog.setJobMessage(AMPException.getStacktrace(e));
				jobLog.setJobStatus(JobLogEntity.JOB_jobStatus_failed);
				
				jobLog.setBatchEndTime(DateUtil.getSqlCurrentDate());
				this.jobLogService.saveEntity(jobLog);
			}
			this.jobLockService.resetJob(JobEnum.JOB_jobId_tulipEventSign.getJobId());
		}
	}
	
	public void sendInvalidBidsEvent(){
		if (this.jobLockService.getRunPrivilege(JobEnum.JOB_jobId_tulipEventInvalidb.getJobId())) {
			
			JobLogEntity jobLog = JobLogFactory.getInstance(JobEnum.JOB_jobId_tulipEventInvalidb.getJobId());
			
			try {
				this.notifyEventService.sendInvalidBidsEvent();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				jobLog.setJobMessage(AMPException.getStacktrace(e));
				jobLog.setJobStatus(JobLogEntity.JOB_jobStatus_failed);
				
				jobLog.setBatchEndTime(DateUtil.getSqlCurrentDate());
				this.jobLogService.saveEntity(jobLog);
			}
			this.jobLockService.resetJob(JobEnum.JOB_jobId_tulipEventInvalidb.getJobId());
		}
	}
}
