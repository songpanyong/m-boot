package com.guohuai.mmp.platform.finance.orders;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.mmp.platform.finance.check.PlatformFinanceCheckEntity;
import com.guohuai.mmp.platform.finance.check.PlatformFinanceCheckService;
import com.guohuai.mmp.platform.finance.result.PlatformFinanceCompareDataResultNewService;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class OrdersService {
	@Autowired
	private OrdersDao ordersDao;
	@Autowired
	private PlatformFinanceCheckService platformFinanceCheckService;
	@Autowired
	private PlatformFinanceCompareDataResultNewService platformFinanceCompareDataResultNewService;
	
	/**
	 * 1.1 渠道对账单下载
	 * 1.2 渠道对账单标准化
	 */
	
	
	/**
	 * 1.3 本地交易记录准备
	 */
	public BaseResp performLocalOrders(String checkOid) {
		PlatformFinanceCheckEntity entity = platformFinanceCheckService.findByOid(checkOid);
		
		platformFinanceCheckService.updateLdataStatusPrepareing(checkOid);
		
		ordersDao.deleteByCheckOid(checkOid);
		
		/**
		 * 投资人充值、提现
		 */
		this.ordersDao.performInvestorsDepositOrders(entity.getBeginTime(), entity.getEndTime(), checkOid);
		this.ordersDao.performInvestorsWithdrawOrders(entity.getBeginTime(), entity.getEndTime(), checkOid);
		
		/**
		 * 申购、赎回
		 */
		this.ordersDao.performInvestorInvestAndRedeemOrders(entity.getBeginTime(), entity.getEndTime(), checkOid);
		
		/**
		 * 发行人充值、提现
		 */
		this.ordersDao.performPublishersDepositOrders(entity.getBeginTime(), entity.getEndTime(), checkOid);
		this.ordersDao.performPublishersWithdrawOrders(entity.getBeginTime(), entity.getEndTime(), checkOid);
		
		platformFinanceCheckService.updateLdataStatusPrepared(checkOid);
		
		return new BaseResp();
	}
	
	/**
	 * 1.4 轧帐
	 */
	public BaseResp check(String checkOid) {
		
		this.platformFinanceCheckService.gaing(checkOid);
		
		this.platformFinanceCompareDataResultNewService.deleteByCheckOid(checkOid);
		
		/** abpay', 'abcollect' 删除不参与对账的数据*/
		this.ordersDao.deleteUnfitOrdersInRemote(checkOid);
		this.ordersDao.deleteUnfitOrders(checkOid);
		
		/** 筛选出正常的订单 */
		log.info("======= 筛选出正常的订单");
		this.ordersDao.filterRightOrders(checkOid);
		
		/** 内场订单状态待支付，外场成功，补回调成功，外场失败，补回调失败 */
		log.info("======= 内场订单状态待支付，外场成功，补回调成功，外场失败，补回调失败");
		this.ordersDao.filterDepositNotifyOk(checkOid);
		this.ordersDao.filterDepositNotifyFail(checkOid);
		
		log.info("==========充值成功改失败");
		this.ordersDao.filterDepositOK2Fail(checkOid);
		
		log.info("==========充值失败改成功");
		this.ordersDao.filterDepositFail2OK(checkOid);
		
		this.ordersDao.filterWithdrawNotifyOk(checkOid);
		this.ordersDao.filterWithdrawNotifyFail(checkOid);
		
		log.info("==========提现成功改失败");
		this.ordersDao.filterWithdrawOK2Fail(checkOid);
		
		
		
		/**
		 * 1.充值异常长款--外场充值单
			外场有充值，业务没充值，业务补充值单
			(系统设计上是不会出现的-，但会有订单后续流程中断了导致两边订单状态不匹配，使订单继续走下去即可)
		 */
		/**
		 * 2.提现异常长款--外场提现单
			外场有，业务没有，补提现单
			(系统设计上是不会出现的-，但会有订单后续流程中断了导致两边订单状态不匹配，使订单继续走下去即可)
		 */
		log.info("======= 长款");
		this.ordersDao.filterDepositAndWithdrawAndInvestAndRedeemLong(checkOid);
		
		
		/**
		 * 3.充值异常短款--业务有，外场没有。
充值冲正单或置为失败（较为简单）

4.提现异常短款--内场提现，业务有提现单，外场没有
提现冲正单（置为失败）.

转账(申购、赎回)
内场转账，业务有，外场没有
转账冲正。（申购单作废，赎回单补一条新的支付订单、或赎回冲正）
		 */
		log.info("======= 短款");
		this.ordersDao.filterDepositAndWithdrawAndInvestAndRedeemShort(checkOid);
		
		log.info("======= 合并正常数据");
		this.ordersDao.mergeNormalData(checkOid);
		
		log.info("======= 合并回调数据");
		this.ordersDao.mergeNotifyData(checkOid);
		
		log.info("======= 合并ok2fail数据");
		this.ordersDao.mergeOk2FailData(checkOid);
		
		log.info("======= 合并fail2ok数据");
		this.ordersDao.mergeFail2OkData(checkOid);
		
		log.info("======= 合并长款数据");
		this.ordersDao.mergeLongData(checkOid);
		
		log.info("======= 合并短款数据");
		this.ordersDao.mergeShortData(checkOid);
		
		log.info("======= 合并异常数据");
		this.ordersDao.mergeException(checkOid);
		log.info("======= 错账笔数");
		int wrongCount = this.ordersDao.getWrongCount(checkOid);
		
		this.platformFinanceCheckService.gaed(checkOid, wrongCount);
		
		return new BaseResp();
	}
	
	

	
	public String userTypeEn2Ch(String userType) {
		if (OrdersEntity.ORDER_userType_investor.equals(userType)) {
			return "投资者";
		}
		
		if (OrdersEntity.ORDER_userType_spv.equals(userType)) {
			return "发行人";
		}
		
		return userType;
	}
	
	
	public String checkStatusEn2Ch(String checkStatus) {
		if (OrdersEntity.ORDER_checkStatus_no.equals(checkStatus)) {
			return "尚未轧账";
		}
		if (OrdersEntity.ORDER_checkStatus_notifyOk.equals(checkStatus)) {
			return "状态不一致--待回调成功";
		}
		if (OrdersEntity.ORDER_checkStatus_notifyFail.equals(checkStatus)) {
			return "状态不一致--待回调失败";
		}
		if (OrdersEntity.ORDER_checkStatus_long.equals(checkStatus)) {
			return "补单";
		}
		if (OrdersEntity.ORDER_checkStatus_short.equals(checkStatus)) {
			return "作废";
		}
		if (OrdersEntity.ORDER_checkStatus_equal.equals(checkStatus)) {
			return "一致";
		}
		if (OrdersEntity.ORDER_checkStatus_exception.equals(checkStatus)) {
			return "异常";
		}
		if (OrdersEntity.ORDER_checkStatus_okTofail.equals(checkStatus)) {
			return "成功改失败";
		}
		if (OrdersEntity.ORDER_checkStatus_failToOk.equals(checkStatus)) {
			return "失败改成功";
		}
		return checkStatus;
	}

}