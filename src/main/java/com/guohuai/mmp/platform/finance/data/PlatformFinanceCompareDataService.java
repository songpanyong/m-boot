package com.guohuai.mmp.platform.finance.data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.DateUtil;
import com.guohuai.component.web.view.RowsRep;
import com.guohuai.mmp.investor.bankorder.InvestorBankOrderEntity;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderEntity;
import com.guohuai.mmp.platform.accment.AccParam;
import com.guohuai.mmp.platform.accment.Accment;
import com.guohuai.mmp.platform.accment.QueryOrdersRep;
import com.guohuai.mmp.platform.accment.QueryOrdersRequest;
import com.guohuai.mmp.platform.finance.check.PlatformFinanceCheckEntity;
import com.guohuai.mmp.platform.finance.check.PlatformFinanceCheckNewService;
import com.guohuai.mmp.platform.finance.check.PlatformFinanceCheckService;
import com.guohuai.mmp.platform.finance.orders.OrdersEntity;

import lombok.extern.slf4j.Slf4j;
/**
 * 处理结算系统提供的对账数据
 * @author jeffrey
 *
 */
@Service
@Transactional
@Slf4j
public class PlatformFinanceCompareDataService {
	@Autowired
	private PlatformFinanceCompareDataNewService platformFinanceCompareDataNewService;
	@Autowired
	private Accment accmentService;
	@Autowired
	private PlatformFinanceCheckNewService platformFinanceCheckNewService;
	@Autowired
	private PlatformFinanceCheckService platformFinanceCheckService;
	/**
	 * 同步对账数据
	 */
	public BaseResp synRemoteData(String checkOid) {
		BaseResp rep = new BaseResp();

		PlatformFinanceCheckEntity pfcEntity = platformFinanceCheckService.findByOid(checkOid);
		if (null == pfcEntity) {
			throw new AMPException("尚未生成对账批次");
		}
		QueryOrdersRequest ireq = new QueryOrdersRequest();
		ireq.setBeginTime(DateUtil.format(pfcEntity.getBeginTime(), DateUtil.datetimePattern));
		ireq.setEndTime(DateUtil.format(pfcEntity.getEndTime(), DateUtil.datetimePattern));
		ireq.setCountNum(0L);
		RowsRep<QueryOrdersRep> rowsRep = this.accmentService.queryOrders(ireq);
		if (-1 == rowsRep.getErrorCode()) {
			throw new AMPException(rowsRep.getErrorMessage());
		}
		if (rowsRep.getRows().isEmpty()) {
			pfcEntity.setCheckDataSyncStatus(PlatformFinanceCheckEntity.CHECKDATASYNCSTATUS_syncOK);
			pfcEntity.setLdataStatus(PlatformFinanceCheckEntity.CHECK_ldataStatus_prepared);
			pfcEntity.setGaStatus(PlatformFinanceCheckEntity.CHECK_gaStatus_gaOk);
			pfcEntity.setCheckStatus(PlatformFinanceCheckEntity.CHECKSTATUS_CHECKSUCCESS);
			this.platformFinanceCheckService.saveEntity(pfcEntity);
		} else {
			platformFinanceCheckNewService.syncing(pfcEntity.getOid());
			new Thread(new Runnable() {
				@Override
				public void run() {
					synCompareDataDo(checkOid);
				}
			}).start();
		}
		
		return rep;
	}
	/**
	 * 同步数据处理
	 */
	public void synCompareDataDo(String checkOid) {
		PlatformFinanceCheckEntity pfcEntity = platformFinanceCheckService.findByOid(checkOid);
		int totalCount = 0;
		platformFinanceCompareDataNewService.deleteByCheckOid(checkOid);
		
		QueryOrdersRequest ireq = new QueryOrdersRequest();
		ireq.setBeginTime(DateUtil.format(pfcEntity.getBeginTime(), DateUtil.datetimePattern));
		ireq.setEndTime(DateUtil.format(pfcEntity.getEndTime(), DateUtil.datetimePattern));
		ireq.setCountNum(0L);
		while (true) {
			List<PlatformFinanceCompareDataEntity> fcdList = new ArrayList<PlatformFinanceCompareDataEntity>();
			List<QueryOrdersRep> resList = null;
			try {
				RowsRep<QueryOrdersRep> irep = accmentService.queryOrders(ireq);
				resList = irep.getRows();
				if (-1 == irep.getErrorCode()) {
					log.info("synCompareDataRep={}", JSONObject.toJSONString(irep));
				}
				if (null == resList || resList.isEmpty()) {
					break;
				}
				PlatformFinanceCompareDataEntity fcd = null;
				for (QueryOrdersRep entity : resList) {
					fcd = new PlatformFinanceCompareDataEntity();
					fcd.setPlatformFinanceCheck(pfcEntity);
					fcd.setOrderCode(entity.getOrderCode());
					fcd.setOrderType(entity.getOrderType());
					fcd.setBuzzOrderType(entity.getOrderType());
					fcd.setUserType(entity.getUserType());
					fcd.setBuzzUserType(getBuzzUserType(entity.getUserType()));
					fcd.setFee(null != entity.getFee() ? entity.getFee() : BigDecimal.ZERO);
					fcd.setVoucher(null != entity.getVoucher() ? entity.getVoucher() : BigDecimal.ZERO);
					fcd.setTradeAmount(entity.getTradeAmount().add(fcd.getFee()).add(fcd.getVoucher()));
					fcd.setInvestorOid(entity.getInvestorOid());
					fcd.setPhoneNum(entity.getPhoneNum());
					fcd.setRealName(entity.getRealName());
					fcd.setReconciliationStatus(entity.getReconciliationStatus());
					fcd.setOrderTime(DateUtil.fetchTimestamp(entity.getBuzzDate()));
					fcd.setOrderStatus(entity.getOrderStatus());
					if ("deposit".equals(entity.getOrderType()) || "withdraw".equals(entity.getOrderType()) || "redEnvelope".equals(entity.getOrderType())) {
						if ("1".equals(entity.getOrderStatus())) {
							fcd.setBuzzOrderStatus(InvestorBankOrderEntity.BANKORDER_orderStatus_paySuccess);
						} else if ("2".equals(entity.getOrderStatus()) || "3".equals(entity.getOrderStatus())){
							fcd.setBuzzOrderStatus(InvestorBankOrderEntity.BANKORDER_orderStatus_payFailed);
						} else {
							fcd.setBuzzOrderStatus(InvestorBankOrderEntity.BANKORDER_orderStatus_toPay);
						}
					}
					
					if ("invest".equals(entity.getOrderType()) || "redeem".equals(entity.getOrderType())) {
						if ("1".equals(entity.getOrderStatus())) {
							fcd.setBuzzOrderStatus(InvestorTradeOrderEntity.TRADEORDER_orderStatus_paySuccess);
						} else if ("2".equals(entity.getOrderStatus())){
							fcd.setBuzzOrderStatus(InvestorBankOrderEntity.BANKORDER_orderStatus_payFailed);
						} else {
							fcd.setBuzzOrderStatus(InvestorBankOrderEntity.BANKORDER_orderStatus_toPay);
						}
					}
					
					
					fcd.setCheckStatus(PlatformFinanceCompareDataEntity.DATA_checkStatus_no);
					
					ireq.setCountNum(entity.getCountNum());
					fcdList.add(fcd);
				}
				totalCount += fcdList.size();
				this.platformFinanceCompareDataNewService.save(fcdList);
			} catch (Exception e) {
				platformFinanceCheckNewService.syncFailed(pfcEntity.getOid());
				log.info("<=============同步数据异常===============>");
			}
		}
		platformFinanceCheckNewService.syncOK(pfcEntity.getOid(), totalCount);

	}
	private String getBuzzUserType(String userType) {
		if (AccParam.UserType.INVESTOR.toString().equals(userType)) {
			return OrdersEntity.ORDER_userType_investor;
		}
		if (AccParam.UserType.SPV.toString().equals(userType)) {
			return OrdersEntity.ORDER_userType_spv;
		}
		return userType;
	}

}