package com.guohuai.mmp.platform.publisher.offset;

import java.math.BigDecimal;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guohuai.ams.product.Product;
import com.guohuai.ams.product.ProductService;
import com.guohuai.cache.service.CacheProductService;
import com.guohuai.component.util.DateUtil;
import com.guohuai.component.util.DecimalUtil;
import com.guohuai.mmp.investor.baseaccount.statistics.InvestorStatisticsService;
import com.guohuai.mmp.investor.tradeorder.FlatWareTotalRep;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderEntity;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderService;
import com.guohuai.mmp.platform.baseaccount.statistics.PlatformStatisticsService;
import com.guohuai.mmp.publisher.baseaccount.statistics.PublisherStatisticsService;
import com.guohuai.mmp.publisher.hold.PublisherHoldEntity;
import com.guohuai.mmp.publisher.hold.PublisherHoldService;
import com.guohuai.mmp.sys.SysConstant;

@Service
public class OffsetService {
	
	@Autowired
	private InvestorTradeOrderService investorTradeOrderService;
	@Autowired
	private PublisherHoldService publisherHoldService;
	@Autowired
	private ProductService productService;
	@Autowired
	private InvestorStatisticsService investorStatisticsService;
	@Autowired
	private PlatformStatisticsService platformStatisticsService;
	@Autowired
	private PublisherStatisticsService publisherStatisticsService;
	@Autowired
	private CacheProductService cacheProductService;
	
	@Transactional(value = TxType.REQUIRES_NEW)
	public void processItemRequireNew(String orderCode, VolumeConfirmRep iRep) {
		InvestorTradeOrderEntity orderEntity = investorTradeOrderService.findByOrderCode(orderCode);
		this.processItem(orderEntity, iRep);
		
	}
	
	public void processItem(InvestorTradeOrderEntity orderEntity) {
		this.processItem(orderEntity, new VolumeConfirmRep());
		
	}
	
	private void processItem(InvestorTradeOrderEntity orderEntity, VolumeConfirmRep iRep) {
		
		/**
		 * 赎回、清盘赎回、还本付息、募集失败退款
		 */
		if (InvestorTradeOrderEntity.TRADEORDER_orderType_normalRedeem.equals(orderEntity.getOrderType()) 
				|| InvestorTradeOrderEntity.TRADEORDER_orderType_clearRedeem.equals(orderEntity.getOrderType())
				|| InvestorTradeOrderEntity.TRADEORDER_orderType_cash.equals(orderEntity.getOrderType())
				|| InvestorTradeOrderEntity.TRADEORDER_orderType_cashFailed.equals(orderEntity.getOrderType())
				|| InvestorTradeOrderEntity.TRADEORDER_orderType_expGoldRedeem.equals(orderEntity.getOrderType())) {
			redeemConfirm(orderEntity, iRep);
			if (0 != orderEntity.getProduct().getRedeemConfirmDays()) {
				orderEntity.setPublisherConfirmStatus(InvestorTradeOrderEntity.TRADEORDER_publisherConfirmStatus_confirmed);
			}
		}
		
		/**
		 * 体验金投资(expGoldInvest)、冲销单(writeOff)、投资单(invest)
		 * 定转活投资(noPayInvest)、补投资单(reInvest)
		 */
		if (InvestorTradeOrderEntity.TRADEORDER_orderType_invest.equals(orderEntity.getOrderType()) ||
				InvestorTradeOrderEntity.TRADEORDER_orderType_writeOff.equals(orderEntity.getOrderType()) ||
				InvestorTradeOrderEntity.TRADEORDER_orderType_expGoldInvest.equals(orderEntity.getOrderType())) {
			investConfirm(iRep, orderEntity);
			orderEntity.setHoldStatus(InvestorTradeOrderEntity.TRADEORDER_holdStatus_holding);
			if (0 != orderEntity.getProduct().getPurchaseConfirmDays()) {
				if (InvestorTradeOrderEntity.TRADEORDER_orderType_invest.equals(orderEntity.getOrderType()) ||
				InvestorTradeOrderEntity.TRADEORDER_orderType_expGoldInvest.equals(orderEntity.getOrderType())) {
					orderEntity.setPublisherConfirmStatus(InvestorTradeOrderEntity.TRADEORDER_publisherConfirmStatus_confirmed);
				}
			}
		}
		
		orderEntity.setCompleteTime(DateUtil.getSqlCurrentDate());
		orderEntity.setOrderStatus(InvestorTradeOrderEntity.TRADEORDER_orderStatus_confirmed);
		
		this.investorTradeOrderService.saveEntity(orderEntity);
	}

	private void redeemConfirm(InvestorTradeOrderEntity orderEntity, VolumeConfirmRep iRep) {
		/** 更新合仓、分仓 */
		FlatWareTotalRep rep = this.publisherHoldService.normalRedeem(orderEntity);
		
		/** 还本付息 */  /** 产品募集失败--退款处理 */
		if ((orderEntity.getWishplanOid() == null) 
				&& (InvestorTradeOrderEntity.TRADEORDER_orderType_cash.equals(orderEntity.getOrderType())
				|| InvestorTradeOrderEntity.TRADEORDER_orderType_cashFailed.equals(orderEntity.getOrderType()))) {
			investorStatisticsService.repayStatistics(orderEntity.getInvestorBaseAccount(), orderEntity.getOrderAmount());
		} 
		
		if ((orderEntity.getWishplanOid() == null) 
				&& (InvestorTradeOrderEntity.TRADEORDER_orderType_normalRedeem.equals(orderEntity.getOrderType())
				|| InvestorTradeOrderEntity.TRADEORDER_orderType_clearRedeem.equals(orderEntity.getOrderType())
				|| InvestorTradeOrderEntity.TRADEORDER_orderType_expGoldRedeem.equals(orderEntity.getOrderType()))) {
			/** 投资人数据统计 */
			investorStatisticsService.redeemStatistics(orderEntity);
		}
		
		/** 发行人统计--还款*/
//		this.publisherStatisticsService.increaseTotalReturnAmount(orderEntity);
		
		/** 平台统计--累计交易额、累计还款额 */
//		this.platformStatisticsService.updateStatistics4RedeemConfirm(orderEntity.getOrderAmount());
		
		/** 产品 */
		this.productService.update4RedeemConfirm(orderEntity.getProduct(), orderEntity.getOrderVolume());
		
		/** SPV */
		this.publisherHoldService.update4RedeemConfirm(orderEntity.getProduct(), orderEntity.getOrderVolume());
		
		iRep.setRedeemAmount(orderEntity.getOrderAmount());
	}
	
	
	
	public void investConfirm(VolumeConfirmRep iRep, InvestorTradeOrderEntity orderEntity) {
		String productType = orderEntity.getProduct().getType().getOid();
		
		BigDecimal lockRedeemHoldVolume = SysConstant.BIGDECIMAL_defaultValue, redeemableHoldVolume = SysConstant.BIGDECIMAL_defaultValue;
		String redeemStatus = InvestorTradeOrderEntity.TRADEORDER_redeemStatus_no, accrualStatus = InvestorTradeOrderEntity.TRADEORDER_accrualStatus_no;
		BigDecimal accruableHoldVolume = SysConstant.BIGDECIMAL_defaultValue;
		if (Product.TYPE_Producttype_02.equals(productType)) { // 活期
			if (DateUtil.isLessThanOrEqualToday(orderEntity.getBeginRedeemDate())) {
				redeemableHoldVolume = orderEntity.getOrderVolume(); //可赎回份额增加
				redeemStatus = InvestorTradeOrderEntity.TRADEORDER_redeemStatus_yes;
			} else {
				lockRedeemHoldVolume = orderEntity.getOrderVolume(); //锁定赎回份额减少
				redeemStatus = InvestorTradeOrderEntity.TRADEORDER_redeemStatus_no;
			}
		} else {
			lockRedeemHoldVolume = orderEntity.getOrderVolume();
		}
		
		if (DateUtil.isLessThanOrEqualToday(orderEntity.getBeginAccuralDate())) {
			accruableHoldVolume = orderEntity.getOrderAmount();
			accrualStatus = InvestorTradeOrderEntity.TRADEORDER_accrualStatus_yes;
		}
		
		/** 正常申购单、冲销单、活转定--活期投资、定转活--活期投资*/
		if (InvestorTradeOrderEntity.TRADEORDER_orderType_invest.equals(orderEntity.getOrderType())
				|| InvestorTradeOrderEntity.TRADEORDER_orderType_writeOff.equals(orderEntity.getOrderType())) {
			PublisherHoldEntity publisherHold = orderEntity.getPublisherHold();
			/** 更新订单状态 */
			if (Product.TYPE_Producttype_01.equals(orderEntity.getProduct().getType().getOid())) {
				publisherHold.setProductAlias(this.cacheProductService.getProductAlias(orderEntity.getProduct()));
			}
			publisherHold.setLockRedeemHoldVolume(publisherHold.getLockRedeemHoldVolume().add(lockRedeemHoldVolume));
			publisherHold.setRedeemableHoldVolume(publisherHold.getRedeemableHoldVolume().add(redeemableHoldVolume));
			publisherHold.setAccruableHoldVolume(publisherHold.getAccruableHoldVolume().add(accruableHoldVolume));
			publisherHold.setHoldVolume(publisherHold.getHoldVolume().add(orderEntity.getOrderVolume()));
			publisherHold.setToConfirmInvestVolume(publisherHold.getToConfirmInvestVolume().subtract(orderEntity.getOrderVolume()));
			// error.define[30080]=份额确认异常(CODE:30080)
			DecimalUtil.isValOutGreatThanOrEqualZero(publisherHold.getToConfirmInvestVolume(), 30080);
			if (publisherHold.getHoldStatus().equals(PublisherHoldEntity.PUBLISHER_HOLD_HOLD_STATUS_toConfirm)) {
				publisherHold.setHoldStatus(PublisherHoldEntity.PUBLISHER_HOLD_HOLD_STATUS_holding);
			}
			this.publisherHoldService.saveEntity(publisherHold);
			
		}
		/** 体验金投资 */
		if (InvestorTradeOrderEntity.TRADEORDER_orderType_expGoldInvest.equals(orderEntity.getOrderType())) {
			PublisherHoldEntity hold = orderEntity.getPublisherHold();
				hold.setExpGoldVolume(hold.getExpGoldVolume().add(orderEntity.getOrderVolume()));
				hold.setAccruableHoldVolume(accruableHoldVolume);
				hold.setHoldVolume(hold.getHoldVolume().add(orderEntity.getOrderVolume()));
				hold.setToConfirmInvestVolume(hold.getToConfirmInvestVolume().subtract(orderEntity.getOrderAmount()));
				if (hold.getHoldStatus().equals(PublisherHoldEntity.PUBLISHER_HOLD_HOLD_STATUS_toConfirm)) {
					hold.setHoldStatus(PublisherHoldEntity.PUBLISHER_HOLD_HOLD_STATUS_holding);
				}
				this.publisherHoldService.saveEntity(hold);
				
		}
		
		orderEntity.setAccrualStatus(accrualStatus);
		orderEntity.setRedeemStatus(redeemStatus);
		
		/** 更新产品 */
		this.productService.update4InvestConfirm(orderEntity.getProduct(), orderEntity.getOrderVolume());
		/** 更新SPV持仓 */
		this.publisherHoldService.update4InvestConfirm(orderEntity.getProduct(), orderEntity.getOrderVolume());
		
		/** 投资人数据统计 */
		if (orderEntity.getWishplanOid() == null) {
			investorStatisticsService.investStatistics(orderEntity, orderEntity.getInvestorBaseAccount());
		}
		
		/** 平台统计--投资单份额确认:累计交易额、累计借款额 */
//		this.platformStatisticsService.updateStatistics4InvestConfirm(orderEntity.getOrderAmount());
		
		/** 发行人统计--借款 */
//		this.publisherStatisticsService.increaseTotalLoanAmount(orderEntity);
		
		iRep.setInvestAmount(orderEntity.getOrderAmount());
		
	
	}

}
