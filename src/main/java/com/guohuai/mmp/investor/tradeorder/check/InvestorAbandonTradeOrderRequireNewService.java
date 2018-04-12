package com.guohuai.mmp.investor.tradeorder.check;

import java.math.BigDecimal;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guohuai.ams.product.Product;
import com.guohuai.ams.product.ProductService;
import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.DateUtil;
import com.guohuai.mmp.investor.abandonlog.AbandonLogService;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderDao;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderEntity;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderService;
import com.guohuai.mmp.investor.tradeorder.OrderDateService;
import com.guohuai.mmp.platform.publisher.offset.PublisherOffsetEntity;
import com.guohuai.mmp.platform.publisher.offset.PublisherOffsetService;
import com.guohuai.mmp.platform.publisher.product.offset.ProductOffsetService;
import com.guohuai.mmp.publisher.hold.PublisherHoldService;
import com.guohuai.mmp.serialtask.AbandonParams;

@Service
@Transactional
public class InvestorAbandonTradeOrderRequireNewService  {
	Logger logger = LoggerFactory.getLogger(InvestorAbandonTradeOrderRequireNewService.class);
	@Autowired
	private AbandonLogService abandonLogService;
	@Autowired
	private PublisherHoldService publisherHoldService;
	@Autowired
	private PublisherOffsetService publisherOffsetService;
	@Autowired
	private ProductOffsetService productOffsetService;
	@Autowired
	private ProductService productService;
	@Autowired
	private InvestorTradeOrderService investorTradeOrderService;
	@Autowired
	private OrderDateService orderDateService;
	@Autowired
	private InvestorTradeOrderDao investorTradeOrderDao;
	
	@Transactional(value = TxType.REQUIRES_NEW)
	public void abandonOrder(String orderCode) {
		int i = this.investorTradeOrderDao.abandonOrder(orderCode);
		if (i < 1) {
			// error.define[30070]=废单失败(CODE:30070)
			throw new AMPException(30070);
		}
	}
	
	
	
	
	@Transactional(value = TxType.REQUIRES_NEW)
	public void abandon(AbandonParams req) {
		InvestorTradeOrderEntity orderEntity = investorTradeOrderService.findByOrderCode(req.getOrderCode());
		//活期订单
		if(Product.TYPE_Producttype_02.equals(orderEntity.getProduct().getType().getOid())){
			/** 解锁产品锁定已募份额 */
			this.productService.update4InvestAbandon(orderEntity);

			/** 解锁SPV锁定份额 */
			this.publisherHoldService.updateSpvHold4InvestAbandon(orderEntity);

			/** 扣除投资人最大持仓份额 */
			this.publisherHoldService.updateMaxHold4InvestAbandon(orderEntity.getInvestorBaseAccount(),
					orderEntity.getProduct(), orderEntity.getOrderVolume());

			// SPV轧差
			publisherOffsetService.getLatestOffset(orderEntity,
					this.orderDateService.getConfirmDate(orderEntity),
					false);
			// 产品轧差
			productOffsetService.offset(orderEntity.getPublisherBaseAccount(), orderEntity, false);

			// 合仓处理
			this.publisherHoldService.abandonInvestOrder(orderEntity);

			// 当日投资累计
			if (DateUtil.isEqualDay(orderEntity.getOrderTime())) {
				this.publisherHoldService.invest4AbandonOfDayInvestVolume(orderEntity);
				
			}
		} else {
			//产品轧差, for t+0, added on 2018-03-07
//			orderEntity.setPublisherOffset(publisherOffsetService.getLatestOffset(orderEntity.getPublisherBaseAccount(),this.orderDateService.getConfirmDate(orderEntity)));
//			productOffsetService.offset(orderEntity.getPublisherBaseAccount(), orderEntity, false);
			
			/** 定期产品==增加可售份额,减少当前份额,少已募份额*/
			this.productService.update4InvestConfirmAbandon(orderEntity);
			/** 定期产品==增加SPV总份额*/
			this.publisherHoldService.update4InvestConfirmAbandon(orderEntity);
			BigDecimal accruableHoldVolume = BigDecimal.ZERO;
			if(InvestorTradeOrderEntity.TRADEORDER_accrualStatus_yes.equals(orderEntity.getAcceptStatus())){
				accruableHoldVolume=orderEntity.getOrderVolume();
			}
			this.publisherHoldService.updateHold4ConfirmAbandon(orderEntity.getPublisherHold(), orderEntity.getOrderVolume(), accruableHoldVolume, orderEntity.getOrderVolume());
		}
		this.abandonLogService.create(orderEntity);
		orderEntity.setOrderStatus(InvestorTradeOrderEntity.TRADEORDER_orderStatus_abandoned);
		investorTradeOrderService.saveEntity(orderEntity);
		
	}
	
	
	
	
	
}
