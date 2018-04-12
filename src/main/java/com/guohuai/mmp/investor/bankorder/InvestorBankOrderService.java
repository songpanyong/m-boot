package com.guohuai.mmp.investor.bankorder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.eclipse.aether.collection.CollectRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.guohuai.basic.common.SeqGenerator;
import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.basic.component.ext.web.PageResp;
import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.BigDecimalUtil;
import com.guohuai.component.util.DateUtil;
import com.guohuai.mmp.investor.bank.BankDao;
import com.guohuai.mmp.investor.bankorder.apply.InvestorDepositApplyDao;
import com.guohuai.mmp.investor.bankorder.apply.InvestorDepositApplyEntity;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountDao;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountEntity;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountService;
import com.guohuai.mmp.investor.baseaccount.RedenvelopeLongReq;
import com.guohuai.mmp.investor.cashflow.InvestorCashFlowService;
import com.guohuai.mmp.investor.orderlog.OrderLogEntity;
import com.guohuai.mmp.investor.orderlog.OrderLogService;
import com.guohuai.mmp.investor.sonoperate.SonOperateDao;
import com.guohuai.mmp.investor.tradeorder.coupon.TradeOrderCouponService;
import com.guohuai.mmp.platform.accment.AccParam;
import com.guohuai.mmp.platform.accment.Accment;
import com.guohuai.mmp.platform.accment.TradeRequest;
import com.guohuai.mmp.platform.finance.result.PlatformFinanceCompareDataResultNewService;
import com.guohuai.mmp.platform.payment.OrderNotifyReq;
import com.guohuai.mmp.platform.payment.PayHtmlRep;
import com.guohuai.mmp.platform.payment.PayParam;
import com.guohuai.mmp.platform.payment.Payment;
import com.guohuai.mmp.platform.payment.log.PayLogEntity;
import com.guohuai.mmp.platform.payment.log.PayLogService;
import com.guohuai.mmp.platform.tulip.TulipService;
import com.guohuai.mmp.sys.CodeConstants;
import com.guohuai.tulip.platform.facade.obj.MyCouponRep;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class InvestorBankOrderService {

	@Autowired
	private InvestorBaseAccountService investorBaseAccountService;
	@Autowired
	private InvestorBankOrderDao investorBankOrderDao;
	@Autowired
	private SonOperateDao sonOperateDao;
	@Autowired
	private InvestorDepositApplyDao investorDepositApplyDao;
	@Autowired
	private InvestorBaseAccountDao investorBaseAccountDao;
	@Autowired
	private SeqGenerator seqGenerator;
	@Autowired
	private OrderLogService orderLogService;
	@Autowired
	private PayLogService payLogService;
	@Autowired
	private Payment payment;
	@Autowired
	private PlatformFinanceCompareDataResultNewService platformFinanceCompareDataResultNewService;
	@Autowired
	private InvestorCashFlowService investorCashFlowService;
	@Autowired
	private Accment accment;
	@Autowired
	private TulipService tulipService;
	@Autowired
	private TradeOrderCouponService tradeOrderCouponService;
	@Autowired
	private BankDao bankDao;
	@Autowired
	private InvestorWithdrawBankOrderService investorWithdrawBankOrderService;

	public InvestorBankOrderEntity saveEntity(InvestorBankOrderEntity bankOrder) {
		return this.investorBankOrderDao.save(bankOrder);
	}

	public InvestorBankOrderEntity findByOrderCodeAndOrderStatusAndOrderType(String orderCode, String orderStatus,
			String orderType) {
		InvestorBankOrderEntity bankOrder = this.investorBankOrderDao
				.findByOrderCodeAndOrderStatusAndOrderType(orderCode, orderStatus, orderType);
		if (null == bankOrder) {
			// error.define[80001]=投资人-银行委托单的订单号不存在!(CODE:80001)
			throw new AMPException(80001);
		}
		return bankOrder;
	}

	public InvestorBankOrderEntity findByOrderCode(String orderCode) {
		InvestorBankOrderEntity bankOrder = this.investorBankOrderDao.findByOrderCode(orderCode);
		if (null == bankOrder) {
			// error.define[80001]=投资人-银行委托单的订单号不存在!(CODE:80001)
			throw new AMPException(80001);
		}
		return bankOrder;
	}

	public InvestorBankOrderEntity findByOrderCodeNoExc(String orderCode) {
		InvestorBankOrderEntity bankOrder = this.investorBankOrderDao.findByOrderCode(orderCode);
		return bankOrder;
	}

	public BaseResp isDone(BankOrderIsDoneReq isDone) {
		BankOrderIsDoneRep rep = new BankOrderIsDoneRep();

		InvestorBankOrderEntity bankOrder = this.investorBankOrderDao.findOne(isDone.getBankOrderOid());
		if (null == bankOrder) {
			rep.setErrorCode(-1);
			rep.setErrorMessage("订单不存在");
			return rep;
		}

		PayLogEntity log = this.payLogService.findByOrderCodeAndHandleType(bankOrder.getOrderCode(),
				PayLogEntity.PAY_handleType_notify);
		if (null == log) {
			rep.setErrorCode(-1);
			rep.setErrorMessage("订单处理中");
			return rep;
		} else {
			if (InvestorBankOrderEntity.BANKORDER_orderStatus_paySuccess.equals(bankOrder.getOrderStatus())) {
				rep.setCompleteTime(bankOrder.getCompleteTime());
			} else {
				rep.setErrorCode(-2);
				rep.setErrorMessage(log.getErrorMessage());
			}
		}

		return rep;
	}

	/**
	 * 创建投资者充值订单
	 */
	@Transactional(value = TxType.REQUIRES_NEW)
	public InvestorBankOrderEntity createDepositBankOrder(DepositBankOrderReq bankOrderReq, String investorOid) {
		InvestorBankOrderEntity bo = this.investorBankOrderDao.findByOrderCode(bankOrderReq.getPayNo());
		if (null != bo) {
			throw new AMPException(80037);
		}
		InvestorBankOrderEntity bankOrder = new InvestorBankOrderEntity();
		bankOrder.setOrderCode(bankOrderReq.getPayNo());
		bankOrder.setFee(BigDecimal.ZERO);
		bankOrder.setFeePayer(InvestorBankOrderEntity.BANKORDER_feePayer_platform);
		bankOrder.setOrderStatus(InvestorBankOrderEntity.BANKORDER_orderStatus_submitted);
		bankOrder.setOrderAmount(bankOrderReq.getOrderAmount());
		bankOrder.setOrderType(InvestorBankOrderEntity.BANKORDER_orderType_deposit);
		bankOrder.setInvestorBaseAccount(investorBaseAccountService.findOne(investorOid));
		bankOrder.setOrderTime(DateUtil.getSqlCurrentDate());
		return this.saveEntity(bankOrder);
	}

	public InvestorBankOrderEntity createDepositLongBankOrder(DepositLongBankOrderReq bankOrderReq) {
		InvestorBankOrderEntity bo = this.investorBankOrderDao.findByOrderCode(bankOrderReq.getOrderCode());
		if (null != bo) {
			throw new AMPException(80037);
		}
		InvestorBankOrderEntity bankOrder = new InvestorBankOrderEntity();
		bankOrder.setOrderCode(bankOrderReq.getOrderCode());
		bankOrder.setFee(BigDecimal.ZERO);
		bankOrder.setFeePayer(InvestorBankOrderEntity.BANKORDER_feePayer_platform);
		bankOrder.setOrderStatus(InvestorBankOrderEntity.BANKORDER_orderStatus_submitted);
		bankOrder.setOrderAmount(bankOrderReq.getOrderAmount());
		bankOrder.setOrderType(InvestorBankOrderEntity.BANKORDER_orderType_depositLong);
		bankOrder.setInvestorBaseAccount(investorBaseAccountService.findOne(bankOrderReq.getInvestorOid()));
		bankOrder.setOrderTime(bankOrderReq.getOrderTime());
		return  this.saveEntity(bankOrder);
	}
	
	@Transactional(value = TxType.REQUIRES_NEW)
	public InvestorBankOrderEntity createDepositBankOrderbf(DepositBankOrderbfReq bankOrderReq, String investorOid) {
		
		InvestorBankOrderEntity bankOrder = new InvestorBankOrderEntity();
		bankOrder.setOrderCode(this.seqGenerator.next(CodeConstants.PAYMENT_deposit));
		bankOrder.setFee(BigDecimal.ZERO);
		bankOrder.setFeePayer(InvestorBankOrderEntity.BANKORDER_feePayer_platform);
		bankOrder.setOrderStatus(InvestorBankOrderEntity.BANKORDER_orderStatus_submitted);
		bankOrder.setOrderAmount(bankOrderReq.getOrderAmount());
		bankOrder.setOrderType(InvestorBankOrderEntity.BANKORDER_orderType_deposit);
		bankOrder.setInvestorBaseAccount(investorBaseAccountService.findOne(investorOid));
		bankOrder.setOrderTime(DateUtil.getSqlCurrentDate());
		//Wish plan
		bankOrder.setWishplanOid(bankOrderReq.getWishplanOid());
		return this.saveEntity(bankOrder);
	}

	@Transactional(value = TxType.REQUIRES_NEW)
	public InvestorBankOrderEntity createDepositBankOrder(ApiDepositBankOrderReq bankOrderReq, String investorOid) {
		InvestorBankOrderEntity bankOrder = new InvestorBankOrderEntity();
		bankOrder.setOrderCode(this.seqGenerator.next(CodeConstants.PAYMENT_deposit));
		bankOrder.setFee(BigDecimal.ZERO);
		bankOrder.setFeePayer(InvestorBankOrderEntity.BANKORDER_feePayer_platform);
		bankOrder.setOrderStatus(InvestorBankOrderEntity.BANKORDER_orderStatus_submitted);
		bankOrder.setOrderAmount(bankOrderReq.getOrderAmount());
		bankOrder.setOrderType(InvestorBankOrderEntity.BANKORDER_orderType_deposit);
		bankOrder.setInvestorBaseAccount(investorBaseAccountService.findOne(investorOid));
		bankOrder.setOrderTime(DateUtil.getSqlCurrentDate());
		return this.saveEntity(bankOrder);
	}

	public InvestorBankOrderEntity createRedEnvelopeLongOrder(RedenvelopeLongReq req) {
		InvestorBankOrderEntity bankOrder = new InvestorBankOrderEntity();
		bankOrder.setOrderCode(this.seqGenerator.next(CodeConstants.INVESTOR_redEnvelope));
		bankOrder.setOrderType(InvestorBankOrderEntity.BNAKORDER_orderType_redEnvelope);
		bankOrder.setInvestorBaseAccount(investorBaseAccountService.findOne(req.getInvestorOid()));
		bankOrder.setOrderAmount(req.getOrderAmount());
		bankOrder.setFee(BigDecimal.ZERO);
		bankOrder.setFeePayer(InvestorBankOrderEntity.BANKORDER_feePayer_platform);
		bankOrder.setOrderStatus(InvestorBankOrderEntity.BANKORDER_orderStatus_paySuccess);
		bankOrder.setOrderTime(req.getOrderTime());
		return this.saveEntity(bankOrder);
	}

	/** 红包订单 */
	public InvestorBankOrderEntity createRedEnvelopeOrder(InvestorBaseAccountEntity account, BigDecimal redEnvelopeAmt,
			String seqNo) {
		InvestorBankOrderEntity bankOrder = new InvestorBankOrderEntity();
		bankOrder.setOrderCode(seqNo);
		bankOrder.setOrderType(InvestorBankOrderEntity.BNAKORDER_orderType_redEnvelope);
		bankOrder.setInvestorBaseAccount(account);
		bankOrder.setOrderAmount(redEnvelopeAmt);
		bankOrder.setUsedCoupons(InvestorBankOrderEntity.BANKORDER_usedCoupons_yes);
		bankOrder.setFee(BigDecimal.ZERO);
		bankOrder.setFeePayer(InvestorBankOrderEntity.BANKORDER_feePayer_platform);
		bankOrder.setOrderStatus(InvestorBankOrderEntity.BANKORDER_orderStatus_paySuccess);
		bankOrder.setOrderTime(DateUtil.getSqlCurrentDate());
		return this.saveEntity(bankOrder);
	}

	/** 转账订单 */
	public InvestorBankOrderEntity createTransferAccountOrder(TransferAccountReq req, String investorOid, String status) {
		InvestorBankOrderEntity bankOrder = new InvestorBankOrderEntity();
		if(status == "rollIn"){
			bankOrder.setOrderCode(this.seqGenerator.next(CodeConstants.PAYMENT_RollIn));
			bankOrder.setOrderType(InvestorBankOrderEntity.BNAKORDER_orderType_ROLLIN);
		}else{
			bankOrder.setOrderCode(this.seqGenerator.next(CodeConstants.PAYMENT_RollOut));
			bankOrder.setOrderType(InvestorBankOrderEntity.BNAKORDER_orderType_ROLLOUT);
		}
		
		String orderCode = status == "rollIn" ? CodeConstants.PAYMENT_RollIn :CodeConstants.PAYMENT_RollOut;
		
		bankOrder.setInvestorBaseAccount(investorBaseAccountService.findOne(investorOid));

		bankOrder.setOrderStatus(InvestorBankOrderEntity.BANKORDER_orderStatus_paySuccess);
		bankOrder.setOrderTime(DateUtil.getSqlCurrentDate());
		bankOrder.setOrderAmount(req.getOrderAmount());
		
		InvestorBankOrderEntity bankOrders = this.saveEntity(bankOrder);
		/**  创建投资人资金变动明细 */
		this.investorCashFlowService.createCashFlow(bankOrders);

		return bankOrders;
	}

	/**
	 * 创建投资者提现订单
	 */
	@Transactional(value = TxType.REQUIRES_NEW)
	public InvestorBankOrderEntity createWithdrawNormalBankOrder(WithdrawBankOrderReq bankOrderReq,
			String investorOid) {
		InvestorBankOrderEntity bankOrder = new InvestorBankOrderEntity();
		bankOrder.setOrderCode(this.seqGenerator.next(CodeConstants.PAYMENT_withdraw));
		bankOrder.setInvestorBaseAccount(investorBaseAccountService.findOne(investorOid));
		bankOrder.setOrderStatus(InvestorBankOrderEntity.BANKORDER_orderStatus_submitted);
		bankOrder.setOrderTime(DateUtil.getSqlCurrentDate());

		bankOrder.setOrderType(InvestorBankOrderEntity.BANKORDER_orderType_withdraw);
		bankOrder.setOrderAmount(bankOrderReq.getOrderAmount());
		log.info("bankOrder:{}",bankOrder);
		return this.saveEntity(bankOrder);
	}

	public InvestorBankOrderEntity createWithdrawLongBankOrder(WithdrawLongBankOrderReq ireq) {
		InvestorBankOrderEntity bankOrder = new InvestorBankOrderEntity();
		bankOrder.setOrderCode(ireq.getOrderCode());
		bankOrder.setInvestorBaseAccount(investorBaseAccountService.findOne(ireq.getInvestorOid()));
		
		bankOrder.setOrderStatus(InvestorBankOrderEntity.BANKORDER_orderStatus_submitted);
		bankOrder.setOrderTime(ireq.getOrderTime());
		bankOrder.setOrderType(InvestorBankOrderEntity.BANKORDER_orderType_withdrawLong);
		bankOrder.setOrderAmount(ireq.getOrderAmount());
		bankOrder.setFee(ireq.getFee());
		bankOrder.setFeePayer(InvestorBankOrderEntity.BANKORDER_feePayer_user);
		bankOrder.setIceOutTime(ireq.getOrderTime());

		return this.saveEntity(bankOrder);
	}

	/**
	 * /** 手续费支付方--平台 * public static final String BANKORDER_feePayer_platform =
	 * "platform"; /** 手续费支付方--用户 * public static final String
	 * BANKORDER_feePayer_user = "user";
	 */
	public String feePayerEn2Ch(String feePayer) {
		if (InvestorBankOrderEntity.BANKORDER_feePayer_user.equals(feePayer)) {
			return "用户";
		}
		if (InvestorBankOrderEntity.BANKORDER_feePayer_platform.equals(feePayer)) {
			return "平台";
		}

		return feePayer;
	}

	/**
	 * /** 交易类型--充值 * public static final String BANKORDER_orderType_deposit =
	 * "deposit"; /** 交易类型--提现 * public static final String
	 * BANKORDER_orderType_withdraw = "withdraw";
	 */
	public String orderTypeEn2Ch(String orderType) {
		if (InvestorBankOrderEntity.BANKORDER_orderType_deposit.equals(orderType)) {
			return "充值";
		}
		if (InvestorBankOrderEntity.BANKORDER_orderType_depositLong.equals(orderType)) {
			return "充值";
		}

		if (InvestorBankOrderEntity.BANKORDER_orderType_withdraw.equals(orderType)) {
			return "提现";
		}
		if (InvestorBankOrderEntity.BANKORDER_orderType_withdrawLong.equals(orderType)) {
			return "提现";
		}
		if (InvestorBankOrderEntity.BNAKORDER_orderType_redEnvelope.equals(orderType)) {
			return "现金红包";
		}
		if (InvestorBankOrderEntity.BNAKORDER_orderType_ROLLIN.equals(orderType)) {
			return "账户转入";
		}
		if (InvestorBankOrderEntity.BNAKORDER_orderType_ROLLOUT.equals(orderType)) {
			return "账户转出";
		}

		return orderType;
	}

	public String orderStatusEn2Ch(String orderStatus) {
		if (InvestorBankOrderEntity.BANKORDER_orderStatus_submitted.equals(orderStatus)) {
			return "已提交";
		}
		if (InvestorBankOrderEntity.BANKORDER_orderStatus_toPay.equals(orderStatus)) {
			return "待支付";
		}
		if (InvestorBankOrderEntity.BANKORDER_orderStatus_submitFailed.equals(orderStatus)) {
			return "交易关闭";
		}
		if (InvestorBankOrderEntity.BANKORDER_orderStatus_payFailed.equals(orderStatus)) {
			return "交易关闭";
		}
		if (InvestorBankOrderEntity.BANKORDER_orderStatus_paySuccess.equals(orderStatus)) {
			return "支付成功";
		}

		if (InvestorBankOrderEntity.BANKORDER_orderStatus_abandoned.equals(orderStatus)) {
			return "已作废";
		}
		

		return orderStatus;
	}
	
	public String frozenStatusEn2Ch(String frozenStatus) {
		if (InvestorBankOrderEntity.BANKORDER_frozenStatus_frozened.equals(frozenStatus)) {
			return "已冻结";
		}
		if (InvestorBankOrderEntity.BANKORDER_frozenStatus_toIceOut.equals(frozenStatus)) {
			return "解冻中";
		}
		if (InvestorBankOrderEntity.BANKORDER_frozenStatus_iceOut.equals(frozenStatus)) {
			return "已解冻";
		}
		
		return frozenStatus;
	}

	/** 我的充值提现记录 */
	public PageResp<MyBankOrderRep> myquery(Specification<InvestorBankOrderEntity> spec, Pageable pageable) {

		PageResp<MyBankOrderRep> pageRep = new PageResp<MyBankOrderRep>();

		Page<InvestorBankOrderEntity> page = this.investorBankOrderDao.findAll(spec, pageable);
		if (page != null && page.getSize() > 0 && page.getTotalElements() > 0) {
			List<MyBankOrderRep> rows = new ArrayList<MyBankOrderRep>();
			for (InvestorBankOrderEntity entity : page) {
				MyBankOrderRep myBankOrderRep = new MyBankOrderRep();
				myBankOrderRep.setOrderCode(entity.getOrderCode());
				myBankOrderRep.setOrderType(entity.getOrderType());
				myBankOrderRep.setOrderTypeDisp(orderTypeEn2Ch(entity.getOrderType()));
				myBankOrderRep.setOrderAmount(entity.getOrderAmount());
				myBankOrderRep.setOrderTime(entity.getCreateTime());
				myBankOrderRep.setOrderStatus(entity.getOrderStatus());
				
				// qi修改
				// myBankOrderRep.setOperator(entity.get);
				String soe = this.sonOperateDao.getOperateName(entity.getOid());
				if (null != soe) {
					myBankOrderRep.setOperator(soe);
				}
				if (InvestorBankOrderEntity.BANKORDER_orderStatus_payFailed.equals(entity.getOrderStatus())) {
					/*PayLogEntity payLog = payLogService.findByOrderCodeAndHandleType(entity.getOrderCode(),
							PayLogEntity.PAY_handleType_notify);
					if (null != payLog) {
						myBankOrderRep.setOrderStatusDisp(payLog.getErrorMessage());
					} else {
						myBankOrderRep.setOrderStatusDisp(orderStatusEn2Ch(entity.getOrderStatus()));
					}*/
					myBankOrderRep.setOrderStatusDisp(orderStatusEn2Ch(entity.getOrderStatus()));
				} else {
					myBankOrderRep.setOrderStatusDisp(orderStatusEn2Ch(entity.getOrderStatus()));
				}
				if (InvestorBankOrderEntity.BANKORDER_feePayer_user.equals(entity.getFeePayer())) {
					myBankOrderRep.setFee(entity.getFee());
				}

				rows.add(myBankOrderRep);
			}
			pageRep.setRows(rows);
			pageRep.setTotal(page.getTotalElements());
		}

		return pageRep;
	}

	/**
	 * 获取用户充值对账金额
	 * 
	 * @param checkTimeStart
	 * @param checkTimeEnd
	 * @param orderType
	 * @param orderStatus
	 * @param investOid
	 * @return
	 */
	public BigDecimal getCheckBankOrder4Acc(String checkTimeStart, String checkTimeEnd, String orderType,
			String orderStatus, String investOid) {
		return this.investorBankOrderDao.getCheckBankOrder4Acc(checkTimeStart, checkTimeEnd, orderType, orderStatus,
				investOid);
	}

	/**
	 * 提现申请中金额
	 * 
	 * @param checkTimeStart
	 * @param checkTimeEnd
	 * @param investOid
	 * @return
	 */
	public BigDecimal getWithdrawApplyAmt4Acc(String checkTimeStart, String checkTimeEnd, String investOid) {
		return this.investorBankOrderDao.getWithdrawApplyAmt4Acc(checkTimeStart, checkTimeEnd, investOid);
	}

	/**
	 * 提现金额，包括申请中和已完成
	 * 
	 * @param checkTimeStart
	 * @param checkTimeEnd
	 * @param investOid
	 * @return
	 */
	public BigDecimal getWithdrawAmt4Acc(String checkTimeStart, String checkTimeEnd, String investOid) {
		return this.investorBankOrderDao.getWithdrawAmt4Acc(checkTimeStart, checkTimeEnd, investOid);
	}

	@Transactional(value = TxType.REQUIRES_NEW)
	public void depositThen(String orderCode, PayHtmlRep rep) {
		InvestorBankOrderEntity orderEntity = this.findByOrderCode(orderCode);
		orderEntity.setOrderStatus(rep.getOrderStatus());
		this.saveEntity(orderEntity);

		OrderLogEntity orderLog = new OrderLogEntity();
		orderLog.setOrderStatus(rep.getOrderStatus());
		orderLog.setTradeOrderOid(orderCode);
		orderLog.setErrorCode(rep.getErrorCode());
		orderLog.setErrorMessage(rep.getErrorMessage());
		orderLog.setOrderType(orderEntity.getOrderType());
		orderLogService.create(orderLog);
	}

	@Transactional(value = TxType.REQUIRES_NEW)
	public void depositThen(String orderCode, BankOrderRep rep) {
		this.bankOrderThen(orderCode, rep);

	}

	@Transactional(value = TxType.REQUIRES_NEW)
	public void withdrawThen(String orderCode, BankOrderRep rep) {
		this.bankOrderThen(orderCode, rep);

	}

	public void bankOrderThen(String orderCode, BankOrderRep rep) {
		InvestorBankOrderEntity orderEntity = this.findByOrderCode(orderCode);
		orderEntity.setOrderStatus(rep.getOrderStatus());
		this.saveEntity(orderEntity);

		OrderLogEntity orderLog = new OrderLogEntity();
		orderLog.setOrderStatus(rep.getOrderStatus());
		orderLog.setTradeOrderOid(orderCode);
		orderLog.setErrorCode(rep.getErrorCode());
		orderLog.setErrorMessage(rep.getErrorMessage());
		orderLog.setOrderType(orderEntity.getOrderType());
		orderLogService.create(orderLog);
	}

	public Page<InvestorBankOrderEntity> findPage(Specification<InvestorBankOrderEntity> spec, Pageable pageable) {
		return this.investorBankOrderDao.findAll(spec, pageable);
	}

	/**
	 * 统计投资人充值总额
	 * 
	 * @return
	 */
	public List<Object[]> getInvestorDepositAmount() {
		return investorBankOrderDao.getInvestorDepositAmount();
	}

	/**
	 * 统计投资人提现总额
	 * 
	 * @return
	 */
	public List<Object[]> getInvestorWithdrawAmount() {
		return investorBankOrderDao.getInvestorWithdrawAmount();
	}
	
	public BaseResp notifyWithdrawOk(NotifyReq notifyReq) {
		log.info("notifyWithdrawOk--noitfyReq:{}", JSONObject.toJSONString(notifyReq));
		int i = platformFinanceCompareDataResultNewService.updateDealStatusDealingByOid(notifyReq.getCrOid());
		log.info("notifyWithdrawOk--orderCode={}, start update={}", notifyReq.getOrderCode(), i == 1);
		
		
		
		OrderNotifyReq ireq = new OrderNotifyReq();
		ireq.setOrderCode(notifyReq.getOrderCode());
		ireq.setReturnCode(PayParam.ReturnCode.RC0000.toString());
		
		investorWithdrawBankOrderService.notifyWithdrawOk(ireq);
		i = platformFinanceCompareDataResultNewService.updateDealStatusDealtByOid(notifyReq.getCrOid());
		log.info("notifyWithdrawOk--orderCode={}, start update={}", notifyReq.getOrderCode(), i == 1);
		return new BaseResp();
	}
	
	
	public BaseResp notifyWithdrawFail(NotifyReq notifyReq) {
		log.info("notifyWithdrawFail--noitfyReq:{}", JSONObject.toJSONString(notifyReq));
		int i = platformFinanceCompareDataResultNewService.updateDealStatusDealingByOid(notifyReq.getCrOid());
		log.info("notifyWithdrawFail--orderCode={}, start update={}", notifyReq.getOrderCode(), i == 1);
		
		
		
		OrderNotifyReq ireq = new OrderNotifyReq();
		ireq.setOrderCode(notifyReq.getOrderCode());
		ireq.setReturnCode(PayParam.ReturnCode.RC9999.toString());
		investorWithdrawBankOrderService.notifyWithdrawFail(ireq);
		
		i = platformFinanceCompareDataResultNewService.updateDealStatusDealtByOid(notifyReq.getCrOid());
		log.info("notifyWithdrawFail--orderCode={}, end update={}", notifyReq.getOrderCode(), i == 1);
		return new BaseResp();
	}

	public BaseResp notifyOk(NotifyReq notifyReq) {
		platformFinanceCompareDataResultNewService.updateDealStatusDealingByOid(notifyReq.getCrOid());

		OrderNotifyReq ireq = new OrderNotifyReq();
		//ireq.setIPayNo(notifyReq.getPayNo());
		ireq.setOrderCode(notifyReq.getOrderCode());
		ireq.setReturnCode(PayParam.ReturnCode.RC0000.toString());
		boolean flag = payment.tradeCallback(ireq);

		platformFinanceCompareDataResultNewService.updateDealStatusDealtByOid(notifyReq.getCrOid());
		return new BaseResp();
	}

	public BaseResp notifyFail(NotifyReq notifyReq) {
		platformFinanceCompareDataResultNewService.updateDealStatusDealingByOid(notifyReq.getCrOid());

		OrderNotifyReq ireq = new OrderNotifyReq();
		//ireq.setIPayNo(notifyReq.getPayNo());
		ireq.setOrderCode(notifyReq.getOrderCode());
		ireq.setReturnCode(PayParam.ReturnCode.RC9999.toString());
		boolean flag = payment.tradeCallback(ireq);

		platformFinanceCompareDataResultNewService.updateDealStatusDealtByOid(notifyReq.getCrOid());
		return new BaseResp();
	}

	/**
	 * 转账
	 */
	@Transactional
	public BaseResp transferAccount(TransferAccountReq req, String uid) {
		InvestorBaseAccountEntity account = this.investorBaseAccountDao.findByMemberId(uid);
		InvestorBaseAccountEntity parentAccount = this.investorBaseAccountDao.findByMemberId(req.getParentAccountId());
		if (null == uid) {
			throw new AMPException("主账户ID不能为空！");
		}
		// 处理子账户转入开始
		BaseResp repIn = new BaseResp();
		BaseResp repOut = new BaseResp();
		TradeRequest trIn = this.rollInRequest(req, uid);
		TradeRequest trOut = this.rollOutRequest(req, req.getParentAccountId());

		try {
			repIn = this.accment.trade(trIn);
			repOut = this.accment.trade(trOut);
			if (0 == repIn.getErrorCode() && 0 == repOut.getErrorCode()) {
				// 同步更新余额
				this.investorBaseAccountService.updateBalance(account); // 更新主账户余额
				this.investorBaseAccountService.updateBalance(parentAccount); // 更新父账户余额
				this.createTransferAccountOrder(req, req.getParentAccountId(), "rollOut");
				this.createTransferAccountOrder(req, uid, "rollIn");
			}
		} catch (AMPException ampException) {
			repIn.setErrorCode(ampException.getCode());
			repIn.setErrorMessage(ampException.getMessage());
		} catch (Exception e) {
			repIn.setErrorCode(-1);
			repIn.setErrorMessage(AMPException.getStacktrace(e));
		}
		return repIn;
	}
	
	/**
	 * 拼装账户之间转入的请求
	 */
	public TradeRequest rollInRequest(TransferAccountReq req, String uid){
		String seqNo = this.seqGenerator.next(CodeConstants.PAYMENT_RollIn);

		TradeRequest trIn = new TradeRequest();
		trIn.setUserOid(uid);
		trIn.setUserType(AccParam.UserType.INVESTOR.toString());
		trIn.setOrderType(AccParam.OrderType.RollIn.toString());
		trIn.setVoucher(BigDecimal.ZERO);
		trIn.setBalance(req.getOrderAmount());
		trIn.setRemark("账户之间转入");
		trIn.setOrderNo(seqNo);
		trIn.setIPayNo(seqNo);
		//trIn.setIPayNo(this.seqGenerator.next(CodeConstants.PAYMENT_RollInPayNo));
		trIn.setOrderTime(DateUtil.format(new java.util.Date(), DateUtil.fullDatePattern));
		return trIn;
	}
	
	/**
	 * 拼装账户之间转出的请求
	 */
	public TradeRequest rollOutRequest(TransferAccountReq req, String uid){
		String seqNo = this.seqGenerator.next(CodeConstants.PAYMENT_RollOut);

		TradeRequest trOut = new TradeRequest();
		trOut.setUserOid(uid);
		trOut.setUserType(AccParam.UserType.INVESTOR.toString());
		trOut.setOrderType(AccParam.OrderType.RollOut.toString());
		trOut.setVoucher(BigDecimal.ZERO);
		trOut.setBalance(req.getOrderAmount());
		trOut.setRemark("账户之间转出");
		trOut.setOrderNo(seqNo);
		trOut.setIPayNo(seqNo);
		//trOut.setIPayNo(this.seqGenerator.next(CodeConstants.PAYMENT_RollOutPayNo));
		trOut.setOrderTime(DateUtil.format(new java.util.Date(), DateUtil.fullDatePattern));
		return trOut;
	}

	@Transactional
	public BaseResp redEnvelope(BankOrderRedReq req) {
		InvestorBaseAccountEntity account = this.investorBaseAccountService.findByPhoneNum(req.getPhoneNum());
		return redEnvelope(req.getCouponId(), account);
	}

	@Transactional
	public BaseResp redEnvelope(String couponId, String investorOid) {
		InvestorBaseAccountEntity account = this.investorBaseAccountService.findOne(investorOid);
		return redEnvelope(couponId, account);
	}

	/**
	 * 使用现金红包
	 */
	@Transactional
	public BaseResp redEnvelope(String couponId, InvestorBaseAccountEntity account) {
		BaseResp rep = new BaseResp();

		MyCouponRep redRep = this.tulipService.useCoupon(couponId);
		if (0 != redRep.getErrorCode()) {
			log.error("运营系统用户：{}使用红包失败，原因：{}", account.getPhoneNum(),
					redRep.getErrorMessage() + "(" + redRep.getErrorCode() + ")");
			throw new AMPException(redRep.getErrorMessage() + "(" + redRep.getErrorCode() + ")");
		}

		if (!BigDecimalUtil.isBigZero(redRep.getAmount())) {
			throw new AMPException("红包金额要大于0！");
		}

		String seqNo = this.seqGenerator.next(CodeConstants.INVESTOR_redEnvelope);

		TradeRequest tradeRequest = new TradeRequest();
		tradeRequest.setUserOid(account.getMemberId());
		tradeRequest.setUserType(AccParam.UserType.INVESTOR.toString());
		tradeRequest.setOrderType(AccParam.OrderType.RED.toString());
		tradeRequest.setVoucher(BigDecimal.ZERO);
		tradeRequest.setBalance(redRep.getAmount());
		tradeRequest.setRemark("红包");
		tradeRequest.setOrderNo(seqNo);
		tradeRequest.setIPayNo(seqNo);
		//tradeRequest.setIPayNo(this.seqGenerator.next(CodeConstants.INVESTOR_redEnvelopePayNo));
		tradeRequest.setOrderTime(DateUtil.format(new java.util.Date(), DateUtil.fullDatePattern));

		try {
			rep = this.accment.trade(tradeRequest);
			if (0 == rep.getErrorCode()) {
				// 同步更新余额
				this.investorBaseAccountService.updateBalance(account);

				InvestorBankOrderEntity bankOrder = this.createRedEnvelopeOrder(account, redRep.getAmount(), seqNo);

				this.tradeOrderCouponService.saveBankOrderCoupon(bankOrder, redRep.getCouponId());

				this.investorCashFlowService.createCashFlow(bankOrder);
			}
		} catch (AMPException ampException) {
			log.info(ampException.getMessage(), ampException);
			rep.setErrorCode(ampException.getCode());
			rep.setErrorMessage(ampException.getMessage());
		} catch (Exception e) {
			log.info(e.getMessage(), e);
			rep.setErrorCode(-1);
			rep.setErrorMessage(AMPException.getStacktrace(e));
		}
		this.orderLogService.redEnvelOrderLog(seqNo, rep);

		return rep;
	}
	
	/**
	 * 重发短信
	 * @param uid
	 * @param req
	 * @return
	 */
	public BaseResp resms(String uid,ResendSmsReq req) {
		InvestorDepositApplyEntity idae = investorDepositApplyDao.findByPayNoAndInvestorOid(req.getPayNo(),uid);
		if (null == idae) {
			throw new AMPException("充值申请表没有此记录！");
		}
		
		BaseResp rep = payment.resendSms(req,uid);
		return rep;
	}
	
}
