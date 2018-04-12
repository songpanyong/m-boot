package com.guohuai.mmp.publisher.bankorder;

import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.guohuai.basic.common.SeqGenerator;
import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.basic.component.ext.web.PageResp;
import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.DateUtil;
import com.guohuai.mmp.investor.bankorder.DepositLongBankOrderReq;
import com.guohuai.mmp.investor.bankorder.WithdrawLongBankOrderReq;
import com.guohuai.mmp.platform.finance.result.PlatformFinanceCompareDataResultNewService;
import com.guohuai.mmp.platform.payment.OrderNotifyReq;
import com.guohuai.mmp.platform.payment.PayParam;
import com.guohuai.mmp.platform.payment.Payment;
import com.guohuai.mmp.platform.payment.log.PayLogEntity;
import com.guohuai.mmp.platform.payment.log.PayLogService;
import com.guohuai.mmp.publisher.baseaccount.PublisherBaseAccountEntity;
import com.guohuai.mmp.publisher.baseaccount.PublisherBaseAccountService;
import com.guohuai.mmp.sys.CodeConstants;


@Service
@Transactional
public class PublisherBankOrderService {

	Logger logger = LoggerFactory.getLogger(PublisherBankOrderService.class);

	@Autowired
	private PublisherBankOrderDao publisherBankOrderDao;
	@Autowired
	private SeqGenerator seqGenerator;
	@Autowired
	private PayLogService payLogService;
	@Autowired
	private PublisherBaseAccountService publisherBaseAccountService;
	@Autowired
	private PlatformFinanceCompareDataResultNewService platformFinanceCompareDataResultNewService;
	@Autowired
	private Payment payment;


	public PublisherBankOrderEntity saveEntity(PublisherBankOrderEntity bankOrder) {
		
		return this.publisherBankOrderDao.save(bankOrder);
	}
	
	public PublisherBankOrderEntity findByOrderCodeAndOrderStatusAndOrderType(String orderCode, String orderStatus, String orderType) {
		PublisherBankOrderEntity bankOrder = this.publisherBankOrderDao.findByOrderCodeAndOrderStatusAndOrderType(orderCode, orderStatus, orderType);
		if (null == bankOrder) {
			//error.define[80001]=投资人-银行委托单的订单号不存在!(CODE:80001)
			throw new AMPException(80001);
		}
		return bankOrder;
	}
	
	public PublisherBankOrderEntity findByOidAndOrderStatusAndOrderType(String oid, String orderStatus, String orderType) {
		PublisherBankOrderEntity bankOrder = this.publisherBankOrderDao.findByOidAndOrderStatusAndOrderType(oid, orderStatus, orderType);
		if (null == bankOrder) {
			//error.define[80001]=投资人-银行委托单的订单号不存在!(CODE:80001)
			throw new AMPException(80001);
		}
		return bankOrder;
	}
	
	public PublisherBankOrderEntity findByOrderCode(String orderCode) {
		PublisherBankOrderEntity bankOrder = this.publisherBankOrderDao.findByOrderCode(orderCode);
		if (null == bankOrder) {
			//error.define[80001]=投资人-银行委托单的订单号不存在!(CODE:80001)
			throw new AMPException(80001);
		}
		return bankOrder;
	}
	
	
	/**
	 * 判断SPV银行委托是否回调完成
	 * @param {@link BankOrderIsDoneReq isDone}
	 * @return {@link BaseResp rep}
	 */
	public BaseResp isDone(BankOrderIsDoneReq isDone) {
		BankOrderIsDoneRep rep = new BankOrderIsDoneRep();
		
		PublisherBankOrderEntity bankOrder = this.publisherBankOrderDao.findOne(isDone.getBankOrderOid());
	
		if (null == bankOrder) {
			rep.setErrorCode(-1);
			rep.setErrorMessage("订单不存在");
			return rep;
		}
		
		PayLogEntity log = this.payLogService.findByOrderCodeAndHandleType(bankOrder.getOrderCode(), PayLogEntity.PAY_handleType_notify);
		if (null == log) {
			rep.setErrorCode(-1);
			rep.setErrorMessage("订单处理中");
			return rep;
		} else {
			if (PublisherBankOrderEntity.BANKORDER_orderStatus_done.equals(bankOrder.getOrderStatus())) {
				rep.setCompleteTime(bankOrder.getCompleteTime());
			} else {
				rep.setErrorCode(-2);
				rep.setErrorMessage(log.getErrorMessage());
			}
		}
		
		return rep;
	}
	
	
	/**
	 * 创建SPV放款订单
	 */
	public PublisherBankOrderEntity createPayOrder(BankOrderPayReq bankOrderReq, PublisherBaseAccountEntity baseAccount) {
		PublisherBankOrderEntity bankOrder = new PublisherBankOrderEntity();
		bankOrder.setOrderCode(this.seqGenerator.next(CodeConstants.PAYMENT_spvPay));
		bankOrder.setOrderType(PublisherBankOrderEntity.BANK_ORDER_ORDER_TYPE_pay);
		bankOrder.setPublisherBaseAccount(baseAccount);
		bankOrder.setOrderAmount(bankOrderReq.getOrderAmount());
		bankOrder.setOrderStatus(PublisherBankOrderEntity.BANKORDER_orderStatus_submitted);
		bankOrder.setOrderTime(DateUtil.getSqlCurrentDate());
		return  this.saveEntity(bankOrder);
	}
	
	
	/**
	 * 创建SPV收款订单
	 */
	public PublisherBankOrderEntity createCollectOrder(BankOrderCollectReq bankOrderReq, PublisherBaseAccountEntity baseAccount) {
		PublisherBankOrderEntity bankOrder = new PublisherBankOrderEntity();
		bankOrder.setOrderCode(this.seqGenerator.next(CodeConstants.PAYMENT_spvCollect));
		bankOrder.setOrderType(PublisherBankOrderEntity.BANK_ORDER_ORDER_TYPE_collect);
		bankOrder.setPublisherBaseAccount(baseAccount);
		bankOrder.setOrderAmount(bankOrderReq.getOrderAmount());
		bankOrder.setOrderStatus(PublisherBankOrderEntity.BANKORDER_orderStatus_submitted);
		bankOrder.setOrderTime(DateUtil.getSqlCurrentDate());
		return  this.saveEntity(bankOrder);
	}
	
	/**
	 * 创建SPV充值订单
	 */
	public PublisherBankOrderEntity createDepostBankOrder(BankOrderReq bankOrderReq, PublisherBaseAccountEntity baseAccount) {
		PublisherBankOrderEntity bankOrder = new PublisherBankOrderEntity();
		bankOrder.setOrderCode(this.seqGenerator.next(CodeConstants.PAYMENT_debitDeposit));
		bankOrder.setOrderType(PublisherBankOrderEntity.BANK_ORDER_ORDER_TYPE_deposit);
		bankOrder.setPublisherBaseAccount(baseAccount);
		bankOrder.setOrderAmount(bankOrderReq.getOrderAmount());
		bankOrder.setOrderStatus(PublisherBankOrderEntity.BANKORDER_orderStatus_submitted);
		bankOrder.setOrderTime(DateUtil.getSqlCurrentDate());
		return  this.saveEntity(bankOrder);
	}
	
	public PublisherBankOrderEntity createDepositLongBankOrder(DepositLongBankOrderReq ireq) {
		PublisherBankOrderEntity bankOrder = new PublisherBankOrderEntity();
		bankOrder.setOrderCode(this.seqGenerator.next(CodeConstants.PAYMENT_debitDeposit));
		bankOrder.setOrderType(PublisherBankOrderEntity.BANK_ORDER_ORDER_TYPE_deposit);
		bankOrder.setPublisherBaseAccount(publisherBaseAccountService.findByOid(ireq.getInvestorOid()));
		bankOrder.setOrderAmount(ireq.getOrderAmount());
		bankOrder.setOrderStatus(PublisherBankOrderEntity.BANKORDER_orderStatus_submitted);
		bankOrder.setOrderTime(DateUtil.getSqlCurrentDate());
		return  this.saveEntity(bankOrder);
	}
	
	/**
	 * 创建SPV提现订单
	 */
	public PublisherBankOrderEntity createWithdrawBankOrder(BankOrderWithdrawReq bankOrderReq, PublisherBaseAccountEntity baseAccount) {
		PublisherBankOrderEntity bankOrder = new PublisherBankOrderEntity();
		bankOrder.setOrderCode(this.seqGenerator.next(CodeConstants.PAYMENT_debitWithdraw));
		bankOrder.setOrderType(PublisherBankOrderEntity.BANK_ORDER_ORDER_TYPE_withdraw);
		bankOrder.setPublisherBaseAccount(baseAccount);
		bankOrder.setOrderAmount(bankOrderReq.getOrderAmount());
		bankOrder.setOrderStatus(PublisherBankOrderEntity.BANKORDER_orderStatus_submitted);
		bankOrder.setOrderTime(DateUtil.getSqlCurrentDate());
		return  this.saveEntity(bankOrder);
		
	}
	
	public PublisherBankOrderEntity createWithdrawLongBankOrder(WithdrawLongBankOrderReq ireq) {
		PublisherBankOrderEntity bankOrder = new PublisherBankOrderEntity();
		bankOrder.setOrderCode(this.seqGenerator.next(CodeConstants.PAYMENT_debitWithdraw));
		bankOrder.setOrderType(PublisherBankOrderEntity.BANK_ORDER_ORDER_TYPE_withdrawLong);
		bankOrder.setPublisherBaseAccount(this.publisherBaseAccountService.findByOid(ireq.getInvestorOid()));
		bankOrder.setOrderAmount(ireq.getOrderAmount());
		bankOrder.setOrderStatus(PublisherBankOrderEntity.BANKORDER_orderStatus_submitted);
		bankOrder.setOrderTime(DateUtil.getSqlCurrentDate());
		return  this.saveEntity(bankOrder);
		
	}
	
	/**
	 * SPV银行委托查询
	 * @param {@link Specification<PublisherBankOrderEntity> spec}
	 * @param {@link Pageable pageable}
	 * @return {@link PageResp<PublisherBankOrderQueryRep> cas}
	 */
	public PageResp<PublisherBankOrderQueryRep> publisherBankOrderMng(Specification<PublisherBankOrderEntity> spec, Pageable pageable) {
		Page<PublisherBankOrderEntity> cas = this.publisherBankOrderDao.findAll(spec, pageable);
		PageResp<PublisherBankOrderQueryRep> pagesRep = new PageResp<PublisherBankOrderQueryRep>();
		if (cas != null && cas.getContent() != null && cas.getTotalElements() > 0) {
			for (PublisherBankOrderEntity entity : cas) {
				PublisherBankOrderQueryRep queryRep = new PublisherBankOrderQueryRep();
				queryRep.setOrderCode(entity.getOrderCode()); //订单号
				queryRep.setOrderType(entity.getOrderType()); //交易类型
				queryRep.setOrderTypeDisp(orderTypeEn2Ch(entity.getOrderType())); //交易类型disp
				queryRep.setFeePayer(entity.getFeePayer()); //手续费支付方
				queryRep.setFeePayerDisp(feePayerEn2Ch(entity.getFeePayer())); //手续费支付方disp
				queryRep.setFee(entity.getFee()); //手续费
				queryRep.setOrderAmount(entity.getOrderAmount()); //订单金额
				queryRep.setOrderStatus(entity.getOrderStatus()); //订单状态
				queryRep.setOrderStatusDisp(orderStatusEn2Ch(entity.getOrderStatus())); //订单状态disp
				queryRep.setCompleteTime(entity.getCompleteTime()); //订单完成时间
				queryRep.setOrderTime(entity.getOrderTime()); //订单创建时间
				
				pagesRep.getRows().add(queryRep);
			}
		}
		pagesRep.setTotal(cas.getTotalElements());
		return pagesRep;
	}
	
	private String orderStatusEn2Ch(String orderStatus) {
		String orderStatusDisp = null;
		switch (orderStatus) {
		case PublisherBankOrderEntity.BANKORDER_orderStatus_submitFailed:
			orderStatusDisp = "申请失败";
			break;
		case PublisherBankOrderEntity.BANKORDER_orderStatus_toPay:
			orderStatusDisp = "待支付";
			break;
		case PublisherBankOrderEntity.BANKORDER_orderStatus_payFailed:
			orderStatusDisp = "支付失败";
			break;
		case PublisherBankOrderEntity.BANKORDER_orderStatus_done:
			orderStatusDisp = "成交";
			break;
		case PublisherBankOrderEntity.BANKORDER_orderStatus_abandoned:
			orderStatusDisp = "已作废";
			break;

		default:
			orderStatusDisp = orderStatus;
			break;
		}
		return orderStatusDisp;
	}

	private String feePayerEn2Ch(String feePayer) {
		if (PublisherBankOrderEntity.BANK_ORDER_FEE_PAYER_platform.equals(feePayer)) {
			return "平台";
		} else if (PublisherBankOrderEntity.BANK_ORDER_FEE_PAYER_user.equals(feePayer)) {
			return "用户";
		}
		return feePayer;
	}

	private String orderTypeEn2Ch(String orderType) {
		if (PublisherBankOrderEntity.BANK_ORDER_ORDER_TYPE_deposit.equals(orderType)) {
			return "充值";
		} else if (PublisherBankOrderEntity.BANK_ORDER_ORDER_TYPE_withdraw.equals(orderType)) {
			return "提现";
		} else if (PublisherBankOrderEntity.BANK_ORDER_ORDER_TYPE_collect.equals(orderType)) {
			return "收款";
		} else if (PublisherBankOrderEntity.BANK_ORDER_ORDER_TYPE_pay.equals(orderType)) {
			return "放款";
		}
		return orderType;
	}

	public Page<PublisherBankOrderEntity> findPage(Specification<PublisherBankOrderEntity> spec, Pageable pageable) {
		
		return this.publisherBankOrderDao.findAll(spec, pageable);
	}

	/**
	 * 统计发行人充值总额
	 * @return
	 */
	public List<Object[]> getPublisherDepositAmount() {
		return publisherBankOrderDao.getPublisherDepositAmount();
	}
	/**
	 * 统计发行人提现总额
	 * @return
	 */
	public List<Object[]> getPublisherWithdrawAmount() {
		return publisherBankOrderDao.getPublisherWithdrawAmount();
	}

	
	public BaseResp notifyOk(NotifyReq notifyReq) {
		platformFinanceCompareDataResultNewService.updateDealStatusDealingByOid(notifyReq.getCrOid());
		
		
		
		OrderNotifyReq ireq = new OrderNotifyReq();
		ireq.setIPayNo(notifyReq.getPayNo());
		ireq.setOrderCode(notifyReq.getPayNo());
		ireq.setReturnCode(PayParam.ReturnCode.RC0000.toString());
		boolean flag = payment.tradeCallback(ireq);
		
		platformFinanceCompareDataResultNewService.updateDealStatusDealtByOid(notifyReq.getCrOid());
		return new BaseResp();
	}
	
	
	public BaseResp notifyFail(NotifyReq notifyReq) {
		platformFinanceCompareDataResultNewService.updateDealStatusDealingByOid(notifyReq.getCrOid());
		
		
		
		OrderNotifyReq ireq = new OrderNotifyReq();
		ireq.setIPayNo(notifyReq.getPayNo());
		ireq.setOrderCode(notifyReq.getPayNo());
		ireq.setReturnCode(PayParam.ReturnCode.RC9999.toString());
		boolean flag = payment.tradeCallback(ireq);
		
		platformFinanceCompareDataResultNewService.updateDealStatusDealtByOid(notifyReq.getCrOid());
		return new BaseResp();
	}
	


}
