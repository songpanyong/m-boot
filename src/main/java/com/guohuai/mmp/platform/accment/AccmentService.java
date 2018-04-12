package com.guohuai.mmp.platform.accment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.guohuai.account.api.AccountSdk;
import com.guohuai.account.api.request.AccountBatchRedeemRequest;
import com.guohuai.account.api.request.AccountSettlementRequest;
import com.guohuai.account.api.request.AccountTransRequest;
import com.guohuai.account.api.request.CreateUserRequest;
import com.guohuai.account.api.request.PublisherAccountQueryRequest;
import com.guohuai.account.api.request.RedeemToBasicRequest;
import com.guohuai.account.api.request.entity.AccountOrderDto;
import com.guohuai.account.api.response.AccountBalanceResponse;
import com.guohuai.account.api.response.AccountBatchRedeemResponse;
import com.guohuai.account.api.response.AccountReconciliationDataResponse;
import com.guohuai.account.api.response.AccountSettlementResponse;
import com.guohuai.account.api.response.AccountTransResponse;
import com.guohuai.account.api.response.BaseResponse;
import com.guohuai.account.api.response.CreateUserResponse;
import com.guohuai.account.api.response.PublisherAccountBalanceResponse;
import com.guohuai.basic.common.StringUtil;
import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.component.exception.AMPException;
import com.guohuai.component.web.view.RowsRep;
import com.guohuai.mmp.platform.accment.log.AccLogReq;
import com.guohuai.mmp.platform.accment.log.AccLogService;
import com.guohuai.mmp.platform.payment.log.PayInterface;
import com.guohuai.mmp.platform.payment.log.PayLogEntity;
import com.guohuai.mmp.platform.payment.log.PayLogReq;
import com.guohuai.mmp.platform.payment.log.PayLogService;
import com.guohuai.settlement.api.request.InteractiveRequest;
import com.guohuai.settlement.api.request.OrderAccountRequest;
import com.guohuai.settlement.api.response.OrderAccountResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AccmentService  implements Accment {
	@Autowired
	private AccountSdk accountSdk;
	@Autowired
	private AccLogService accLogService;
	@Autowired
	private PayLogService payLogService;
	
	
	/**
	 * 新增用户
	 */
	@Override
	public void addUser(CreateUserReq ireq) {
		log.info("add user：" + JSON.toJSONString(ireq));
		CreateUserRequest oreq = new CreateUserRequest();
		oreq.setSystemUid(ireq.getSystemUid());
		oreq.setUserType(ireq.getUserType());
		oreq.setPhone(ireq.getPhone());
		oreq.setRemark(ireq.getRemark());
		oreq.setSystemSource(AccParam.SystemSource.MIMOSA.toString());
		
		CreateUserResponse orep = this.accountSdk.addUser(oreq);
		Assert.notNull(orep, "账户系统返回为空");
		
		if (!AccParam.ReturnCode.RC0000.toString().equals(orep.getReturnCode())) {
			throw new AMPException(orep.getErrorMessage());
		}
		
		BaseResp irep = new BaseResp();
		irep.setErrorMessage(JSONObject.toJSONString(orep));
		this.writeLog(irep, oreq, AccInterface.addUser.getInterfaceName());
		                               
	}
	
	
	/**
	 * 会员账户交易	trade
	 */
	@Override
	public BaseResp trade(TradeRequest ireq) {
		return this.trade(ireq, true);
	}
	
	@Override
	public BaseResp trade(TradeRequest ireq, boolean isLog) {
		BaseResp irep = new BaseResp();
		AccountTransRequest oreq  = new AccountTransRequest();
		oreq.setUserOid(ireq.getUserOid());
		oreq.setPublisherUserOid(ireq.getPublisherMemeberId());
		oreq.setUserType(ireq.getUserType());
		oreq.setOrderType(ireq.getOrderType());
		oreq.setBalance(ireq.getBalance());
		oreq.setVoucher(ireq.getVoucher());
		oreq.setRemark(ireq.getRemark());
		oreq.setOrderNo(ireq.getIPayNo());
		oreq.setSystemSource(AccParam.SystemSource.MIMOSA.toString());
		oreq.setRequestNo(StringUtil.uuid());
		oreq.setOrderCreatTime(ireq.getOrderTime());
		//private String originBranch;
		oreq.setOriginBranch(ireq.getOriginBranch());
		AccountTransResponse orep = new AccountTransResponse();
		try {
			orep = accountSdk.trade(oreq);
			setIrep(irep, orep);
		} catch (Exception e) {
			irep.setErrorCode(-1);
			irep.setErrorMessage(AMPException.getStacktrace(e));
		}
		
		if (isLog) {
//			writeLog(irep, ireq, AccInterface.trade.getInterfaceName());
			this.writeLog(JSONObject.toJSONString(ireq), ireq.getOrderNo(), ireq.getIPayNo(), PayLogEntity.PAY_handleType_transfer, irep, PayInterface.transfer.getInterfaceName());
		}
		return irep;
	}
	
	@Override
	public BaseResp transfer(TransferRequest ireq) {
		BaseResp irep = new BaseResp();
		AccountTransRequest oreq  = new AccountTransRequest();
		
		oreq.setPublisherUserOid(ireq.getPublisherOid());
		oreq.setRequestNo(ireq.getRequestNo());
		oreq.setSystemSource(ireq.getSystemSource());
		oreq.setOrderNo(ireq.getIPayNo());
		oreq.setUserOid(ireq.getInvestorOid());
		oreq.setOrderType(ireq.getOrderType());
		oreq.setBalance(ireq.getOrderAmount());
		oreq.setVoucher(ireq.getVoucher());
		oreq.setOrderCreatTime(ireq.getOrderTime());
		oreq.setUserType(ireq.getUserType());
		oreq.setRemark(ireq.getRemark());
		oreq.setOrderDesc(ireq.getOrderDesc());
		//private String originBranch;
		oreq.setOriginBranch(ireq.getOriginBranch());
		AccountTransResponse orep = new AccountTransResponse();
		try {
			orep = accountSdk.transfer(oreq);
			setIrep(irep, orep);
		} catch (Exception e) {
			irep.setErrorCode(-1);
			irep.setErrorMessage(AMPException.getStacktrace(e));
		}
		
//		writeLog(irep, ireq, AccInterface.trade.getInterfaceName());
		this.writeLog(JSONObject.toJSONString(ireq), ireq.getOrderCode(), ireq.getIPayNo(), PayLogEntity.PAY_handleType_transfer, irep, PayInterface.transfer.getInterfaceName());
		
		return irep;
	}
	
	

	private void setIrep(BaseResp irep, BaseResponse orep) {

		/** orep == null, 当接口返回为NULL时 */
		if (null == orep) {
			irep.setErrorCode(-1);
			irep.setErrorMessage("返回为空");
			return;
		}

		if (AccParam.ReturnCode.RC0000.toString().equals(orep.getReturnCode())) {
			irep.setErrorMessage(orep.getErrorMessage());
		} else {
			irep.setErrorCode(-1);
			irep.setErrorMessage(orep.getErrorMessage());
		}
		return;
	}
	
	
	private <T> void writeLog(BaseResp irep, T sendObj, String interfaceName) {
		
		AccLogReq accLogReq =  new AccLogReq();
		accLogReq.setInterfaceName(interfaceName);
		accLogReq.setSendedTimes(1);
		accLogReq.setSendObj(JSONObject.toJSONString(sendObj));
		accLogReq.setErrorCode(irep.getErrorCode());
		accLogReq.setErrorMessage(irep.getErrorMessage());
		accLogService.createEntity(accLogReq);
	}
	

	
	/**
	 * 结算
	 */
	@Override
	public BaseResp close(CloseRequest ireq) {
		BaseResp irep = new BaseResp();
		
		AccountSettlementRequest oreq = new AccountSettlementRequest();
		oreq.setPublisherUserOid(ireq.getPublisherUserOid());
		oreq.setNettingBalance(ireq.getNettingBalance());
		oreq.setRequestNo(ireq.getRequestNo());
		oreq.setSystemSource(ireq.getSystemSource());
		oreq.setOrderNo(ireq.getOrderCode());
		oreq.setOrderType(ireq.getOrderType());
		oreq.setOrderCreatTime(ireq.getOrderTime());
		oreq.setUserType(ireq.getUserType());
		oreq.setRemark(ireq.getRemark());
		oreq.setOrderDesc(ireq.getOrderDesc());
		oreq.setApplyBalance(ireq.getInvestAmount());
		oreq.setRedeemBalance(ireq.getRedeemAmount());

		AccountSettlementResponse orep = new AccountSettlementResponse();
		try {
			orep = this.accountSdk.nettingSettlement(oreq);
			this.setIrep(irep, orep);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			irep.setErrorCode(-1);
			irep.setErrorMessage(AMPException.getStacktrace(e));
		}
		
		
		this.writeLog(irep, ireq, AccInterface.nettingSettlement.getInterfaceName());
	
		return irep;
	}

	

	@Override
	public UserBalanceRep queryBalance(String memberId) {
		UserBalanceRep irep = new UserBalanceRep();
		
		InteractiveRequest ireq = new InteractiveRequest();
		ireq.setUserOid(memberId);
		
		AccountBalanceResponse orep = this.accountSdk.getAccountBalanceByUserOid(ireq);
		log.info("memberId={},queryBalance={}", memberId, JSONObject.toJSONString(orep));
		if (null == orep) {
			irep.setErrorCode(-1);
			irep.setErrorMessage("用户余额为null");
			return irep;
		}
		if (!AccParam.ReturnCode.RC0000.toString().equals(orep.getReturnCode())) {
			irep.setErrorCode(-1);
			irep.setErrorMessage(orep.getErrorMessage());
			return irep;
		}
		irep.setBalance(orep.getBalance());
		irep.setRechargeFrozenBalance(orep.getRechargeFrozenBalance());
		irep.setWithdrawFrozenBalance(orep.getWithdrawFrozenBalance());
		irep.setApplyAvailableBalance(orep.getApplyAvailableBalance());
		irep.setWithdrawAvailableBalance(orep.getWithdrawAvailableBalance());
		return irep;
	}
	
	@Override
	public PublisherBalanceRep queryPublisherBalance(String memberId) {
		PublisherBalanceRep irep = new PublisherBalanceRep();
		
		PublisherAccountQueryRequest ireq = new PublisherAccountQueryRequest();
		ireq.setUserOid(memberId);
		
		PublisherAccountBalanceResponse orep = this.accountSdk.getPublisherAccountBalanceByUserOid(ireq);
		log.info("memberId={},queryBalance={}", memberId, JSONObject.toJSONString(orep));
		if (null == orep) {
			irep.setErrorCode(-1);
			irep.setErrorMessage("用户余额为null");
			return irep;
		}
		if (!AccParam.ReturnCode.RC0000.toString().equals(orep.getReturnCode())) {
			irep.setErrorCode(-1);
			irep.setErrorMessage(orep.getErrorMessage() );
			return irep;
		}
		irep.setBasicBalance(orep.getBasicBalance());
		irep.setCollectionSettlementBalance(orep.getCollectionSettlementBalance());
		irep.setAvailableAmountBalance(orep.getAvailableAmountBalance());
		irep.setFrozenAmountBalance(orep.getFrozenAmountBalance());
		irep.setWithdrawAvailableAmountBalance(orep.getWithdrawAvailableAmountBalance());
		return irep;
	}


	/**
	 * 发行人收款、付款
	 */
	@Override
	public BaseResp publisherTrade(PublisherTradeRequest ireq) {
		BaseResp irep = new BaseResp();
		AccountTransRequest oreq  = new AccountTransRequest();
		oreq.setUserOid(ireq.getMemeberId());
		oreq.setUserType(ireq.getUserType());
		oreq.setOrderType(ireq.getOrderType());
		oreq.setBalance(ireq.getBalance());
		oreq.setRemark(ireq.getRemark());
		oreq.setOrderNo(ireq.getOrderCode());
		oreq.setSystemSource(AccParam.SystemSource.MIMOSA.toString());
		oreq.setRequestNo(StringUtil.uuid());
		oreq.setRemark(ireq.getRemark());
		
		AccountTransResponse orep = new AccountTransResponse();
		try {
			orep = accountSdk.publisherTrans(oreq);
			setIrep(irep, orep);
		} catch (Exception e) {
			irep.setErrorCode(-1);
			irep.setErrorMessage(AMPException.getStacktrace(e));
		}
		
		this.writeLog(irep, ireq, AccInterface.publisherTrans.getInterfaceName());
		
		return irep;
	}
	
	
	private void writeLog(String content, String orderCode, String iPayNo, String handleType, BaseResp irep, String interfaceName) {
		PayLogReq logReq = new PayLogReq();
		logReq.setErrorCode(irep.getErrorCode());
		logReq.setErrorMessage(irep.getErrorMessage());
		logReq.setInterfaceName(interfaceName);
		logReq.setSendedTimes(1);
		logReq.setContent(content);
		logReq.setOrderCode(orderCode);
		logReq.setIPayNo(iPayNo);
		logReq.setHandleType(handleType);
		this.payLogService.createEntity(logReq);
	}
	
	

	@Override
	public BaseResp batchPay(BatchPayRequest ireq) {
		BaseResp irep = new BaseResp();
		
		AccountBatchRedeemRequest oreq  = new AccountBatchRedeemRequest();
		oreq.setPublisherUserOid(ireq.getMemberId());
		oreq.setSystemSource(ireq.getSystemSource());
		oreq.setRequestNo(ireq.getRequestNo());
		
		List<AccountOrderDto> orderList = new ArrayList<AccountOrderDto>();
		
		for (BatchPayDto iDTO : ireq.getOrders()) {
			AccountOrderDto oDTO = new AccountOrderDto();
			oDTO.setOrderNo(iDTO.getIPayNo());
			oDTO.setUserOid(iDTO.getMemberId());
			oDTO.setOrderType(iDTO.getOrderType());
			oDTO.setBalance(iDTO.getOrderAmount());
			oDTO.setOrderDesc(iDTO.getOrderDesc());
			oDTO.setSubmitTime(iDTO.getOrderTime());
			oDTO.setRemark(iDTO.getRemark());
			//Wishplan
			oDTO.setOriginBranch(iDTO.getOriginBranch());
			orderList.add(oDTO);
		}
		oreq.setOrderList(orderList);
		
		AccountBatchRedeemResponse orep = new AccountBatchRedeemResponse();
		try {
			orep = accountSdk.batchRedeem(oreq);
			setIrep(irep, orep);
		} catch (Exception e) {
			irep.setErrorCode(-1);
			irep.setErrorMessage(AMPException.getStacktrace(e));
		}
		
		
		for (BatchPayDto iDTO : ireq.getOrders()) {
			this.writeLog(JSONObject.toJSONString(iDTO), iDTO.getOrderCode(), iDTO.getIPayNo(), PayLogEntity.PAY_handleType_applyCall, irep, PayInterface.redeem.getInterfaceName());
		}
		
		
		return irep;
	}


	/**
	 * 获取对账数据
	 */
	public RowsRep<QueryOrdersRep> queryOrders(QueryOrdersRequest ireq) {
		RowsRep<QueryOrdersRep> irep = new RowsRep<QueryOrdersRep>();
		OrderAccountRequest oreq = new  OrderAccountRequest();
		oreq.setBeginTime(ireq.getBeginTime());
		oreq.setEndTime(ireq.getEndTime());
		oreq.setCountNum(ireq.getCountNum());
		//List<OrderAccountResponse> orep = this.accountSdk.getAccountReconciliationData(oreq);
		
		try {
			//orep = this.accountSdk.getAccountReconciliationData(oreq);
			AccountReconciliationDataResponse orep = this.accountSdk.getAccountAlreadyReconciliationData(oreq);
			if (null == orep) {
				irep.setErrorCode(-1);
				irep.setErrorMessage("返回为空");
				return irep;
			}
			if (!AccParam.ReturnCode.RC0000.toString().equals(orep.getReturnCode())) {
				irep.setErrorCode(-1);
				irep.setErrorMessage(orep.getErrorMessage());
				return irep;
			}
			List<OrderAccountResponse> list = orep.getOrderList();
			if (null == list) {
				irep.setErrorCode(-1);
				irep.setErrorMessage("返回为空");
				return irep;
			}
			for (OrderAccountResponse t : list) {
				QueryOrdersRep i = new QueryOrdersRep();
				i.setOrderCode(t.getOrderCode());
				i.setOrderType(t.getOrderType());
				i.setUserType(t.getUserType());
				i.setTradeAmount(t.getOrderAmount());
				
				i.setInvestorOid(t.getInvestorOid());
				i.setPhoneNum(t.getUserPhone());
				i.setRealName(t.getUserName());
				i.setReconciliationStatus(t.getReconlicationStatus());
				i.setBuzzDate(t.getBuzzDate());
				i.setCountNum(t.getCountNum());
				i.setOrderStatus(t.getOrderStatus());
				i.setFee(t.getFee());
				i.setVoucher(t.getVoucher());
				irep.add(i);
			}
		} catch (Exception e) {
			irep.setErrorCode(-1);
			irep.setErrorMessage(AMPException.getStacktrace(e));
		}
		return irep;
	}
	
	/**
	 * redeem2basic
	 */
	@Override
	public int redeem2basic(RedeemToBasicRequest req) {
		int result = -1;
		try {
			result = accountSdk.redeem2basic(req);
		} catch (Exception e) {
			e.printStackTrace();
			result = -1;
		}
		return result;
	}
	
}
