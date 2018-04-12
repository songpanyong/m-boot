package com.guohuai.mmp.platform.publisher.product.offset;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.guohuai.ams.order.SPVOrder;
import com.guohuai.ams.order.SPVOrderDao;
import com.guohuai.ams.portfolio.dao.PortfolioDao;
import com.guohuai.ams.portfolio20.estimate.PortfolioEstimateDao;
import com.guohuai.ams.portfolio20.estimate.PortfolioEstimateEntity;
import com.guohuai.ams.portfolio20.order.MarketOrderDao;
import com.guohuai.ams.portfolio20.order.MarketOrderEntity;
import com.guohuai.ams.product.Product;
import com.guohuai.ams.product.ProductService;
import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.DateUtil;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderEntity;
import com.guohuai.mmp.platform.publisher.offset.PublisherOffsetEntity;
import com.guohuai.mmp.platform.publisher.offset.PublisherOffsetService;
import com.guohuai.mmp.publisher.baseaccount.PublisherBaseAccountEntity;

@Service
@Transactional
public class ProductOffsetService {

	Logger logger = LoggerFactory.getLogger(ProductOffsetService.class);

	@Autowired
	ProductOffsetDao productOffsetDao;
	@Autowired
	PortfolioDao portfolioDao;
	@Autowired
	private SPVOrderDao spvOrderDao;
	@Autowired
	PortfolioEstimateDao portfolioEstimateDao;
	@Autowired
	MarketOrderDao marketOrderDao;
	@Autowired
	ProductService productService;
	@Autowired
	PublisherOffsetService publisherOffsetService;
	@Autowired
	ProductOffsetServiceRequiresNew productOffsetServiceRequiresNew;

	public ProductOffsetEntity updateEntity(ProductOffsetEntity offset) {
		return this.productOffsetDao.save(offset);
	}

	/**
	 * 获取最新的轧差批次
	 * 
	 * @return
	 */
	public ProductOffsetEntity offset(PublisherBaseAccountEntity publisherBaseAccount,
			InvestorTradeOrderEntity tradeOrder, boolean isPositive) {
		BigDecimal orderAmount = tradeOrder.getOrderAmount();
		if (!isPositive) {
			orderAmount = orderAmount.negate();
		}
		ProductOffsetEntity offset = this.getLatestOffset(tradeOrder.getProduct(), tradeOrder.getPublisherOffset());
		if (tradeOrder.getOrderType().equals(InvestorTradeOrderEntity.TRADEORDER_orderType_invest)
				|| tradeOrder.getOrderType().equals(InvestorTradeOrderEntity.TRADEORDER_orderType_reInvest)) {
			this.increaseInvest(offset, orderAmount);
		} else {
			this.increaseRedeem(offset, orderAmount);
		}
		return offset;
	}
	
	/**
	 * 获取T+0最新的轧差批次
	 * 
	 * @return
	 */
	public ProductOffsetEntity offsetT0(PublisherBaseAccountEntity publisherBaseAccount,
			InvestorTradeOrderEntity tradeOrder, boolean isPositive) {
		BigDecimal orderAmount = tradeOrder.getOrderAmount();
		if (!isPositive) {
			orderAmount = orderAmount.negate();
		}
		ProductOffsetEntity offset = this.getLatestOffset(tradeOrder.getProduct(), tradeOrder.getPublisherOffset());
		if (tradeOrder.getOrderType().equals(InvestorTradeOrderEntity.TRADEORDER_orderType_invest)
				|| tradeOrder.getOrderType().equals(InvestorTradeOrderEntity.TRADEORDER_orderType_reInvest)) {
			this.increaseInvest(offset, orderAmount);
		} else {
			this.increaseRedeem(offset, orderAmount);
		}
		return offset;
	}

	/**
	 * 增加待清算投资
	 * 
	 * @param offset
	 * @param investAmount
	 */
	public void increaseInvest(ProductOffsetEntity offset, BigDecimal investAmount) {
		this.productOffsetDao.increaseInvest(offset.getOid(), investAmount);
	}

	/**
	 * 增加待清算赎回
	 * 
	 * @param offset
	 * @param investAmount
	 */
	public void increaseRedeem(ProductOffsetEntity offset, BigDecimal investAmount) {
		this.productOffsetDao.increaseRedeem(offset.getOid(), investAmount);
	}

	private ProductOffsetEntity getLatestOffset(Product product, PublisherOffsetEntity pOffset) {

		ProductOffsetEntity offset = this.productOffsetDao.getLatestOffset(product, pOffset.getOffsetCode());
		if (offset == null) {
			try {
				return this.productOffsetServiceRequiresNew.createEntity(product, pOffset);
			} catch (Exception e) {
				throw new AMPException("新建轧差异常");
				// return this.getLatestOffset(product, pOffset);
			}

		}
		return offset;
	}

	public int updateOffsetStatus2closed(String spvOid) {
		return this.productOffsetDao.updateOffsetStatus2closed(spvOid);
	}

	/**
	 * 根据产品查找<<非已结算>>状态产品轧差
	 * 
	 * @param productOid
	 * @return
	 */
	public OffsetConstantRep findByProductOid(String productOid) {
		List<ProductOffsetEntity> list = this.productOffsetDao.findByProductConstantly(productOid);

		OffsetConstantRep rep = new OffsetConstantRep();
		if (null != list && !list.isEmpty()) {
			for (ProductOffsetEntity entity : list) {
				rep.setNetPosition(rep.getNetPosition().add(entity.getNetPosition()));
			}
		}
		return rep;
	}

	public FindsOidRep<ProductOffsetMoneyRep> findByOffsetOid(String offsetOid) {
		List<ProductOffsetEntity> list = this.productOffsetDao
				.findByPublisherOffset(this.publisherOffsetService.findByOid(offsetOid));
		FindsOidRep<ProductOffsetMoneyRep> rows = new FindsOidRep<ProductOffsetMoneyRep>();
		BigDecimal totalAvailableAmount = BigDecimal.ZERO;// 初始化总可用金
		BigDecimal totalCashCategory = BigDecimal.ZERO;// 初始化总现金类
		BigDecimal totalNonCashCategory = BigDecimal.ZERO;// 初始化总非现金类
		BigDecimal totalSubtotal = BigDecimal.ZERO;// 初始化总计
		for (ProductOffsetEntity entity : list) {
			String portfolioRatio = "可用金"
					+ entity.getProduct().getPortfolio().getCashRate().multiply(new BigDecimal("100")).intValue() + "%"
					+ "现金类"
					+ entity.getProduct().getPortfolio().getLiquidRate().multiply(new BigDecimal("100")).intValue()
					+ "%" + "非现金类"
					+ entity.getProduct().getPortfolio().getIlliquidRate().multiply(new BigDecimal("100")).intValue()
					+ "%";
			Date beforeTreeday = DateUtil.formatUtilToSql(DateUtil.beforeTreeday(entity.getOffsetDate())); // 上个自然日(前天)，因为估值表里标记时间少一天
			// 获取可用金余额，如果为空则置为0，可用金公式：昨天15：00 的投资组合的可用金+当日的净头寸
			PortfolioEstimateEntity peEntity = portfolioEstimateDao.getCashEstimate(entity.getProduct().getPortfolio().getOid(),beforeTreeday);
			if(peEntity == null){
				logger.info(entity.getProduct().getPortfolio().getOid());
			}
			BigDecimal yesterdayCashEstimate = peEntity == null || peEntity.getCashEstimate() == null ? BigDecimal.ZERO : peEntity.getCashEstimate(); //修复空指针问题
			
			BigDecimal availableAmount = yesterdayCashEstimate.add(entity.getNetPosition());
			
			//开始计算当日资金各项流水
			java.util.Date offsetDate = DateUtil.lastDate(entity.getOffsetDate()); //如果当前是10号，需要找8号15：00以后到9号15：00以前的数据
			Timestamp startTime = DateUtil.parseToSqlDateTime(DateUtil.getDaySysBeginTime(offsetDate));//当日起始时间
			Timestamp endTime = DateUtil.parseToSqlDateTime(DateUtil.getDaySysEndTime(offsetDate)); //当前时间
			//当日充值提现记录
			BigDecimal moneyFlow = BigDecimal.ZERO;
			List<SPVOrder> spvOrderList = spvOrderDao.getPortfolioRecord(entity.getProduct().getPortfolio().getOid(), startTime, endTime);
			for(SPVOrder spvEntity : spvOrderList){
				if(spvEntity.getOrderType().equals("INVEST")){
					moneyFlow = moneyFlow.add(spvEntity.getOrderAmount());
				} else if(spvEntity.getOrderType().equals("REDEEM")){
					moneyFlow = moneyFlow.subtract(spvEntity.getOrderAmount());
				}
			}
			// 获取当日现金类净头寸
			BigDecimal cashCategory = BigDecimal.ZERO;
			//logger.info("offset{} start{}  end{}",entity.getOffsetDate(),startTime,endTime);
			List<MarketOrderEntity> moEntitys = marketOrderDao.getPortfolioTypeRecord("LIQUID",entity.getProduct().getPortfolio().getOid(), startTime,endTime);
			for (MarketOrderEntity moEntity : moEntitys) {
				if(moEntity.getDealType().equals("PURCHASE")){
					cashCategory = cashCategory.add(moEntity.getOrderAmount());
				}else if(moEntity.getDealType().equals("REDEEM")){
					cashCategory = cashCategory.subtract(moEntity.getOrderAmount());
				}
			}
			// 获取当日非现金类净头寸
			BigDecimal nonCashCategory = BigDecimal.ZERO;
			List<MarketOrderEntity> imoEntitys = marketOrderDao.getPortfolioTypeRecord("ILLIQUID",entity.getProduct().getPortfolio().getOid(), startTime,endTime);
			for (MarketOrderEntity imoEntity : imoEntitys) {
				if(imoEntity.getDealType().equals("SUBSCRIPE") || imoEntity.getDealType().equals("PURCHASE")){
					nonCashCategory = nonCashCategory.add(imoEntity.getOrderAmount());
				}else if(imoEntity.getDealType().equals("REPAYMENT")){
					nonCashCategory = nonCashCategory.subtract(imoEntity.getOrderAmount());
				}
			}
			//投资组合昨日可用金额:前天15:00的可用金额-昨日的申购+赎回+充值-提现
			yesterdayCashEstimate = yesterdayCashEstimate.add(moneyFlow).subtract(cashCategory).subtract(nonCashCategory);
			//计算小计
			BigDecimal subtotal = BigDecimal.ZERO;
			subtotal = availableAmount.subtract(cashCategory).subtract(nonCashCategory);
			ProductOffsetMoneyRep rep = ProductOffsetMoneyRep.builder().productOid(entity.getProduct().getOid())
					.productCode(entity.getProduct().getCode()).productName(entity.getProduct().getName())
					.netPosition(entity.getNetPosition()).investAmount(entity.getInvestAmount())
					.redeemAmount(entity.getRedeemAmount()).portfolioOid(entity.getProduct().getPortfolio().getOid())
					.portfolioName(entity.getProduct().getPortfolio().getName()).portfolioRatio(portfolioRatio)
					.yesterdayCashEstimate(yesterdayCashEstimate).availableAmount(availableAmount).cashCategory(cashCategory).nonCashCategory(nonCashCategory)
					.subtotal(subtotal).build();
			rows.add(rep);
			totalAvailableAmount = totalAvailableAmount.add(availableAmount);
			totalCashCategory = totalCashCategory.add(cashCategory);
			totalNonCashCategory = totalNonCashCategory.add(nonCashCategory);
			totalSubtotal = totalSubtotal.add(subtotal);
		}
		rows.setTotalAvailableAmount(totalAvailableAmount);
		rows.setTotalCashCategory(totalCashCategory);
		rows.setTotalNonCashCategory(totalNonCashCategory);
		rows.setTotalSubtotal(totalSubtotal);

		return rows;
	}

	public int updateConfirmStatus(String pOffsetOid, String confirmStatus) {
		return this.productOffsetDao.updateConfirmStatus(pOffsetOid, confirmStatus);

	}

	public int updateConfirmStatus4Lock(String pOffsetOid, String confirmStatus) {
		return this.productOffsetDao.updateConfirmStatus4Lock(pOffsetOid, confirmStatus);

	}

	public int updateClearStatus(String offsetOid, String clearStatus) {
		int i = this.productOffsetDao.updateClearStatus(offsetOid, clearStatus);
		if (i < 1) {
			// error.define[20021]=清算状态异常(CODE:20021)
			throw new AMPException(20021);
		}
		return i;

	}

	public int updateCloseStatus4Close(String offsetOid, String closeStatus) {
		int i = this.productOffsetDao.updateCloseStatus4Close(offsetOid, closeStatus);
		if (i < 1) {
			// error.define[20022]=结算状态异常(CODE:20022)
			throw AMPException.getException(20022);
		}
		return i;
	}

	public int updateCloseStatus4CloseBack(String offsetOid, String closeStatus) {
		int i = this.productOffsetDao.updateCloseStatus4CloseBack(offsetOid, closeStatus);
		if (i < 1) {
			// error.define[20022]=结算状态异常(CODE:20022)
			throw AMPException.getException(20022);
		}
		return i;
	}

	public List<ProductOffsetEntity> findByPublisherOffset(PublisherOffsetEntity offset) {
		return this.productOffsetDao.findByPublisherOffset(offset);
	}

	public ProductOffsetEntity findByProductAndOffsetCode(Product product, String offsetCode) {

		return this.productOffsetDao.findByProductAndOffsetCode(product, offsetCode);
	}

}
