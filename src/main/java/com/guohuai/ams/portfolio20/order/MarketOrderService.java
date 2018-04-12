package com.guohuai.ams.portfolio20.order;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.guohuai.ams.illiquidAsset.IlliquidAsset;
import com.guohuai.ams.illiquidAsset.IlliquidAssetService;
import com.guohuai.ams.liquidAsset.LiquidAsset;
import com.guohuai.ams.liquidAsset.LiquidAssetService;
import com.guohuai.ams.portfolio.entity.PortfolioEntity;
import com.guohuai.ams.portfolio.service.PortfolioService;
import com.guohuai.ams.portfolio20.illiquid.hold.PortfolioIlliquidHoldDao;
import com.guohuai.ams.portfolio20.illiquid.hold.PortfolioIlliquidHoldEntity;
import com.guohuai.ams.portfolio20.illiquid.hold.PortfolioIlliquidHoldService;
import com.guohuai.ams.portfolio20.illiquid.hold.part.PortfolioIlliquidHoldPartEntity;
import com.guohuai.ams.portfolio20.illiquid.hold.part.PortfolioIlliquidHoldPartService;
import com.guohuai.ams.portfolio20.illiquid.hold.repayment.PortfolioIlliquidHoldRepaymentEntity;
import com.guohuai.ams.portfolio20.illiquid.hold.repayment.PortfolioIlliquidHoldRepaymentService;
import com.guohuai.ams.portfolio20.invest.losses.PortfolioInvestLossesService;
import com.guohuai.ams.portfolio20.liquid.hold.PortfolioLiquidHoldDao;
import com.guohuai.ams.portfolio20.liquid.hold.PortfolioLiquidHoldEntity;
import com.guohuai.ams.portfolio20.liquid.hold.PortfolioLiquidHoldService;
import com.guohuai.ams.portfolio20.liquid.hold.part.PortfolioLiquidHoldPartEntity;
import com.guohuai.ams.portfolio20.liquid.hold.part.PortfolioLiquidHoldPartService;
import com.guohuai.basic.component.exception.GHException;
import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.DateUtil;
import com.guohuai.component.util.StringUtil;

/**
 * 存续期--订单服务接口
 * 
 * 
 */
@Service
public class MarketOrderService {
	@Autowired
	private PortfolioService portfolioService;
	@Autowired
	private LiquidAssetService liquidAssetService;
	@Autowired
	private IlliquidAssetService illiquidAssetService;
	@Autowired
	private PortfolioLiquidHoldService portfolioLiquidHoldService;
	@Autowired
	private PortfolioIlliquidHoldService portfolioIlliquidHoldService;
	@Autowired
	private PortfolioLiquidHoldPartService portfolioLiquidHoldPartService;
	@Autowired
	private PortfolioIlliquidHoldPartService portfolioIlliquidHoldPartService;
	@Autowired
	private PortfolioIlliquidHoldRepaymentService portfolioIlliquidHoldRepaymentService;
	@Autowired
	private PortfolioInvestLossesService portfolioInvestLossesService;
	@Autowired
	private MarketOrderDao marketOrderdao;
	@Autowired
	private PortfolioLiquidHoldDao portfolioLiquidHoldDao;
	@Autowired
	private PortfolioIlliquidHoldDao portfolioIlliquidHoldDao;
	@Autowired
	private EntityManager entityManager;
	/**
	 * 现金类申购
	 * 
	 * @param from
	 * @param uid
	 * @param type
	 *            order（订单）
	 */
	@Transactional
	public void purchaseForLiquid(MarketOrderForm form, String operator) {

		LiquidAsset asset = this.liquidAssetService.findByOid(form.getLiquidAssetOid());
		if (null == asset) {
			throw new GHException("未知的资产ID");
		}
		PortfolioEntity portfolio = this.portfolioService.getByOid(form.getPortfolioOid());
		if (null == portfolio) {
			throw new GHException("未知的投资组合ID");
		}
		MarketOrderEntity order = new MarketOrderEntity();
		order.setOid(StringUtil.uuid());
		order.setType(MarketOrderEntity.TYPE_LIQUID);
		order.setPortfolio(portfolio);
		order.setLiquidAsset(asset);
		order.setOrderState(MarketOrderEntity.ORDER_STATE_SUBMIT);
		order.setDealType(MarketOrderEntity.DEALTYPE_PURCHASE);
		order.setOrderAmount(form.getOrderAmount().multiply(new BigDecimal(10000)).setScale(2, RoundingMode.HALF_UP));
		order.setTradeShare(form.getOrderAmount().multiply(new BigDecimal(10000)).setScale(2, RoundingMode.HALF_UP));
		order.setOrderDate(DateUtil.getSqlDate());
		order.setForceClose("NO");

		this.liquidAssetService.applyForPurchase(form.getLiquidAssetOid(), form.getOrderAmount().multiply(new BigDecimal(10000)).setScale(2, RoundingMode.HALF_UP));

		this.marketOrderdao.save(order);

		this.portfolioService.freezeCash(portfolio.getOid(), order.getOrderAmount());
	}

	/**
	 * 现金类 申购审核 确认
	 * 
	 * @param form
	 * @param uid
	 */
	@Transactional
	public void passForPurchaseLiquid(String oid, String operator, String auditMark) {

		int xd = this.marketOrderdao.pass(oid);
		if (xd == 0) {
			throw new AMPException("订单ID错误, 或订单状态已失效.");
		}

		MarketOrderEntity order = marketOrderdao.findOne(oid);
		order.setOrderState(MarketOrderEntity.ORDER_STATE_PASS);
		order.setOrderDate(DateUtil.getSqlDate());
		order.setAuditTime(new Timestamp(System.currentTimeMillis()));
		order.setAuditor(operator);
		order.setAuditMark(auditMark);

		// 取合仓数据
		PortfolioLiquidHoldEntity hold = this.portfolioLiquidHoldService.findHoldingHold(order.getPortfolio(), order.getLiquidAsset());
		if (null == hold) {
			hold = this.portfolioLiquidHoldService.newHold(order.getPortfolio(), order.getLiquidAsset(), operator);
		}

		// 建立分仓
		PortfolioLiquidHoldPartEntity holdPart = this.portfolioLiquidHoldPartService.newHoldPart(hold, order);

		// 更新合仓数据
		hold = this.portfolioLiquidHoldService.mergeHold(hold, holdPart);

		this.liquidAssetService.passForPurchase(order.getLiquidAsset().getOid(), order.getOrderAmount());

		this.marketOrderdao.save(order);

		this.portfolioService.liquidPurchase(order.getPortfolio().getOid(), order.getOrderAmount(), order.getTradeShare());

	}

	/**
	 * 现金类 申购驳回
	 * 
	 * @param form
	 * @param uid
	 */
	@Transactional
	public void failForPurchaseLiquid(String oid, String operator, String auditMark) {

		int xd = this.marketOrderdao.fail(oid);
		if (xd == 0) {
			throw new AMPException("订单ID错误, 或订单状态已失效.");
		}

		MarketOrderEntity order = this.marketOrderdao.findOne(oid);
		order.setOrderState(MarketOrderEntity.ORDER_STATE_FAIL);
		order.setAuditMark(auditMark);
		this.liquidAssetService.failForPurchase(order.getLiquidAsset().getOid(), order.getOrderAmount());
		this.marketOrderdao.save(order);
		this.portfolioService.relaxCash(order.getPortfolio().getOid(), order.getOrderAmount());
	}

	/**
	 * 现金类 赎回
	 * 
	 * @param from
	 * @param uid
	 */
	@Transactional
	public void redeemForLiquid(MarketOrderForm form, String operator) {

		// 投资订单表插入数据
		LiquidAsset asset = this.liquidAssetService.findByOid(form.getLiquidAssetOid());
		if (null == asset) {
			throw new GHException("未知的资产ID");
		}
		PortfolioEntity portfolio = this.portfolioService.getByOid(form.getPortfolioOid());
		if (null == portfolio) {
			throw new GHException("未知的投资组合ID");
		}

		PortfolioLiquidHoldEntity hold = this.portfolioLiquidHoldService.findHoldingHold(portfolio, asset);

		MarketOrderEntity order = new MarketOrderEntity();
		order.setOid(StringUtil.uuid());
		order.setType(MarketOrderEntity.TYPE_LIQUID);
		order.setPortfolio(portfolio);
		order.setLiquidAsset(asset);
		order.setOrderState(MarketOrderEntity.ORDER_STATE_SUBMIT);
		order.setDealType(MarketOrderEntity.DEALTYPE_REDEEM);

		BigDecimal amount = BigDecimal.ZERO;

		if ("YES".equalsIgnoreCase(form.getForceClose())) {
			order.setOrderAmount(hold.getHoldAmount());
			order.setTradeShare(hold.getHoldAmount());
			order.setForceClose("YES");
			amount = hold.getHoldAmount();
		} else {
			order.setOrderAmount(form.getOrderAmount().multiply(new BigDecimal(10000)).setScale(2, RoundingMode.HALF_UP));
			order.setTradeShare(form.getOrderAmount().multiply(new BigDecimal(10000)).setScale(2, RoundingMode.HALF_UP));
			order.setForceClose("NO");
			amount = form.getOrderAmount().multiply(new BigDecimal(10000)).setScale(2, RoundingMode.HALF_UP);
		}
		order.setOrderDate(DateUtil.getSqlDate());
		this.marketOrderdao.save(order);

		// 冻结合仓持仓份额
		this.portfolioLiquidHoldService.freeze(order, hold);

		this.liquidAssetService.applyForRedeem(form.getLiquidAssetOid(), amount);
	}

	/**
	 * 现金类 赎回驳回
	 * 
	 * @param form
	 * @param uid
	 */
	@Transactional
	public void failRedeemForLiquid(String oid, String operator, String auditMark) {

		int xd = this.marketOrderdao.fail(oid);
		if (xd == 0) {
			throw new AMPException("订单ID错误, 或订单状态已失效.");
		}

		MarketOrderEntity order = this.marketOrderdao.findOne(oid);
		order.setOrderState(MarketOrderEntity.ORDER_STATE_FAIL);
		order.setOrderDate(DateUtil.getSqlDate());
		order.setAuditMark(auditMark);
		this.marketOrderdao.save(order);
		PortfolioLiquidHoldEntity hold = this.portfolioLiquidHoldService.findHoldingHold(order.getPortfolio(), order.getLiquidAsset());
		hold = this.portfolioLiquidHoldService.failRedeem(hold, order);

		this.liquidAssetService.failForRedeem(order.getLiquidAsset().getOid(), order.getTradeShare());
	}

	/**
	 * 现金类 赎回审核 确认
	 * 
	 * @param form
	 * @param uid
	 */
	@Transactional
	public void passRedeemForLiquid(String oid, String operator, String auditMark) {

		int xd = this.marketOrderdao.pass(oid);
		if (xd == 0) {
			throw new AMPException("订单ID错误, 或订单状态已失效.");
		}

		MarketOrderEntity order = marketOrderdao.findOne(oid);
		order.setOrderState(MarketOrderEntity.ORDER_STATE_PASS);
		order.setAuditTime(new Timestamp(System.currentTimeMillis()));
		order.setAuditor(operator);
		order.setOrderDate(DateUtil.getSqlDate());
		order.setAuditMark(auditMark);
		// 取合仓数据
		PortfolioLiquidHoldEntity hold = this.portfolioLiquidHoldService.findHoldingHold(order.getPortfolio(), order.getLiquidAsset());

		BigDecimal investCapital = hold.getInvestAmount();
		BigDecimal investIncome = hold.getInvestCome();
		BigDecimal holdShare = hold.getHoldShare();

		// 更新合仓数据
		Map<String, BigDecimal> map = this.portfolioLiquidHoldService.subHold(hold, order);

		BigDecimal selloutShare = map.get("CAPITAL").add(map.get("LEFT_OFFSET_CAPITAL"));
		BigDecimal selloutPrice = order.getOrderAmount();

		BigDecimal losses = selloutPrice.subtract(selloutShare);

		this.marketOrderdao.save(order);

		this.liquidAssetService.passForRedeem(order.getLiquidAsset().getOid(), order.getTradeShare());

		this.portfolioInvestLossesService.save(order, investCapital, investIncome, holdShare, selloutShare, selloutPrice, losses);

		this.portfolioService.liquidRedeem(order.getPortfolio().getOid(), order.getOrderAmount(), order.getTradeShare());
	}

	// 非现金类申购(下订单)
	public void purchaseForIlliquid(MarketOrderForm form, String type, String operator) {
		IlliquidAsset asset = this.illiquidAssetService.findByOid(form.getIlliquidAssetOid());
		if (null == asset) {
			throw new GHException("未知的资产ID");
		}
		PortfolioEntity portfolio = this.portfolioService.getByOid(form.getPortfolioOid());
		if (null == portfolio) {
			throw new GHException("未知的投资组合ID");
		}

		MarketOrderEntity order = new MarketOrderEntity();
		order.setOid(StringUtil.uuid());
		order.setType(MarketOrderEntity.TYPE_ILLIQUID);
		order.setPortfolio(portfolio);
		order.setIlliquidAsset(asset);
		order.setOrderState(MarketOrderEntity.ORDER_STATE_SUBMIT);
		order.setDealType(type);
		order.setOrderAmount(form.getOrderAmount().multiply(new BigDecimal(10000)).setScale(2, RoundingMode.HALF_UP));
		if (MarketOrderEntity.DEALTYPE_PURCHASE.equals(type)) {
			order.setTradeShare(form.getOrderShare().multiply(new BigDecimal(10000)).setScale(2, RoundingMode.HALF_UP));
		} else if (MarketOrderEntity.DEALTYPE_SUBSCRIPE.equals(type)) {
			order.setTradeShare(form.getOrderAmount().multiply(new BigDecimal(10000)).setScale(2, RoundingMode.HALF_UP));
		} else {
			throw new GHException("未知的交易类型: " + type);
		}
		order.setOrderDate(DateUtil.getSqlDate());
		order.setExceptWay(form.getExceptWay());
		order.setForceClose("NO");

		this.marketOrderdao.save(order);

		this.illiquidAssetService.applyForPurchase(asset.getOid(), order.getTradeShare());

		this.portfolioService.freezeCash(order.getPortfolio().getOid(), order.getOrderAmount());
	}

	// 非现金类申购驳回
	@Transactional
	public void failForPurchaseIlliquid(String oid, String operator, String auditMark) {

		int xd = this.marketOrderdao.fail(oid);
		if (xd == 0) {
			throw new AMPException("订单ID错误, 或订单状态已失效.");
		}

		MarketOrderEntity order = this.marketOrderdao.findOne(oid);
		order.setOrderState(MarketOrderEntity.ORDER_STATE_FAIL);
		order.setAuditMark(auditMark);
		order.setAuditTime(new Timestamp(System.currentTimeMillis()));
		order.setAuditor(operator);
		this.marketOrderdao.save(order);

		this.illiquidAssetService.failForPurchase(order.getIlliquidAsset().getOid(), order.getTradeShare());

		this.portfolioService.relaxCash(order.getPortfolio().getOid(), order.getOrderAmount());
	}

	// 非现金类申购确认
	@Transactional
	public void passForPurchaseIlliquid(String oid, String operator, String auditMark) {

		int xd = this.marketOrderdao.pass(oid);
		if (xd == 0) {
			throw new AMPException("订单ID错误, 或订单状态已失效");
		}

		MarketOrderEntity order = marketOrderdao.findOne(oid);
		order.setOrderState(MarketOrderEntity.ORDER_STATE_PASS);
		order.setOrderDate(DateUtil.getSqlDate()); 
		order.setAuditTime(new Timestamp(System.currentTimeMillis()));
		order.setAuditor(operator);
		order.setAuditMark(auditMark);

		// 取合仓数据
		PortfolioIlliquidHoldEntity hold = this.portfolioIlliquidHoldService.findHoldingHold(order.getPortfolio(), order.getIlliquidAsset());
		if (null == hold) {
			hold = this.portfolioIlliquidHoldService.newHold(order.getPortfolio(), order.getIlliquidAsset(), order.getExceptWay(), operator);
		}

		// 建立分倉
		PortfolioIlliquidHoldPartEntity holdPart = this.portfolioIlliquidHoldPartService.newHoldPart(hold, order);

		// 更新合仓数据
		hold = this.portfolioIlliquidHoldService.mergeHold(hold, holdPart);

		// 已成立或已起息的标的, 要生成还款计划
		if (order.getIlliquidAsset().getLifeState().equals(IlliquidAsset.ILLIQUIDASSET_LIFESTATE_SETUP) || order.getIlliquidAsset().getLifeState().equals(IlliquidAsset.ILLIQUIDASSET_LIFESTATE_VALUEDATE)) {
			this.portfolioIlliquidHoldRepaymentService.mergeRepayment(hold, order, operator);
		}
		this.marketOrderdao.save(order);

		// TODO 偏离损益 (申购时, 有此业务)
		// BigDecimal offet =
		// order.getOrderAmount().subtract(order.getTradeShare());

		this.illiquidAssetService.passForPurchase(order.getIlliquidAsset().getOid(), order.getTradeShare());

		this.portfolioService.illiquidPurchase(order.getPortfolio().getOid(), order.getOrderAmount(), order.getTradeShare());
	}

	// 非现金类退款(下订单)
	@Transactional
	public void refundForIlliquid(MarketOrderForm form, String operator) {
		// 投资订单表插入数据
		IlliquidAsset asset = this.illiquidAssetService.findByOid(form.getIlliquidAssetOid());
		if (null == asset) {
			throw new GHException("未知的资产ID.");
		}
		PortfolioEntity portfolio = this.portfolioService.getByOid(form.getPortfolioOid());
		if (null == portfolio) {
			throw new GHException("未知的投资组合ID.");
		}

		PortfolioIlliquidHoldEntity hold = this.portfolioIlliquidHoldService.findHoldingHold(portfolio, asset);

		MarketOrderEntity order = new MarketOrderEntity();
		order.setOid(StringUtil.uuid());
		order.setType(MarketOrderEntity.TYPE_ILLIQUID);
		order.setPortfolio(portfolio);
		order.setIlliquidAsset(asset);
		order.setOrderState(MarketOrderEntity.ORDER_STATE_SUBMIT);
		order.setDealType(MarketOrderEntity.DEALTYPE_REFUND);
		order.setOrderAmount(form.getOrderAmount().multiply(new BigDecimal(10000)).setScale(2, RoundingMode.HALF_UP));
		order.setTradeShare(hold.getHoldShare());
		order.setCapital(hold.getHoldShare());
		order.setIncome(hold.getHoldIncome());
		order.setForceClose("YES");
		order.setOrderDate(DateUtil.getSqlDate());
		order = this.marketOrderdao.save(order);

		Map<String, BigDecimal> map = this.portfolioIlliquidHoldService.freeze(order, hold);

		this.illiquidAssetService.applyForRepayment(asset.getOid(), map.get("ACTUAL_FREEZE_CAPITAL"), map.get("ACTUAL_FREEZE_INCOME"));

	}

	// 非现金类退款审核驳回
	@Transactional
	public void failRefundForIlliquid(String oid, String operator, String auditMark) {
		int xd = this.marketOrderdao.fail(oid);
		if (xd == 0) {
			throw new AMPException("订单ID错误, 或订单状态已失效.");
		}

		MarketOrderEntity order = this.marketOrderdao.findOne(oid);
		order.setOrderState(MarketOrderEntity.ORDER_STATE_FAIL);
		order.setOrderDate(DateUtil.getSqlDate());
		order.setAuditTime(new Timestamp(System.currentTimeMillis()));
		order.setAuditor(operator);
		order.setAuditMark(auditMark);
		this.marketOrderdao.save(order);

		PortfolioIlliquidHoldEntity hold = this.portfolioIlliquidHoldService.findHoldingHold(order.getPortfolio(), order.getIlliquidAsset());

		Map<String, BigDecimal> map = this.portfolioIlliquidHoldService.failRepayment(hold, order);

		this.illiquidAssetService.failForRepayment(order.getIlliquidAsset().getOid(), map.get("ACTUAL_UNFREEZE_CAPITAL"), map.get("ACTUAL_UNFREEZE_INCOME"));

	}

	// 非现金类退款审核通过
	@Transactional
	public void passRefundForIlliquid(String oid, String operator, String auditMark) {

		int xd = this.marketOrderdao.pass(oid);
		if (xd == 0) {
			throw new AMPException("订单ID错误, 或订单状态已失效.");
		}

		MarketOrderEntity order = this.marketOrderdao.findOne(oid);
		order.setOrderState(MarketOrderEntity.ORDER_STATE_PASS);
		order.setAuditTime(new Timestamp(System.currentTimeMillis()));
		order.setAuditor(operator);
		order.setOrderDate(DateUtil.getSqlDate());
		order.setAuditMark(auditMark);

		// 取合仓数据
		PortfolioIlliquidHoldEntity hold = this.portfolioIlliquidHoldService.findHoldingHold(order.getPortfolio(), order.getIlliquidAsset());

		BigDecimal investCapital = hold.getHoldShare();
		BigDecimal investIncome = hold.getHoldIncome();
		BigDecimal holdShare = hold.getHoldShare();

		// 更新合仓
		Map<String, BigDecimal> map = this.portfolioIlliquidHoldService.subHold(hold, order);

		BigDecimal selloutShare = map.get("ACTUAL_REPAYMENT_CAPITAL").add(map.get("LEFT_HOLD_SHARE"));

		BigDecimal selloutPrice = order.getOrderAmount();

		BigDecimal losses = selloutPrice.subtract(selloutShare);

		// 偏离损益
		/*
		 * BigDecimal offset =
		 * order.getCapital().add(order.getIncome()).subtract(map.get(
		 * "ACTUAL_REPAYMENT_CAPITAL")).subtract(map.get(
		 * "ACTUAL_REPAYMENT_INCOME")).add(map.get("LEFT_HOLD_SHARE")).add(map.
		 * get("LEFT_HOLD_INCOME"));
		 */

		this.illiquidAssetService.passForRepayment(order.getIlliquidAsset().getOid(), map.get("ACTUAL_REPAYMENT_CAPITAL").add(map.get("LEFT_HOLD_SHARE")), map.get("ACTUAL_REPAYMENT_INCOME").add(map.get("LEFT_HOLD_INCOME")));

		this.portfolioInvestLossesService.save(order, investCapital, investIncome, holdShare, selloutShare, selloutPrice, losses);

		BigDecimal cash = order.getOrderAmount();
		BigDecimal estimate = map.get("ACTUAL_REPAYMENT_CAPITAL").add(map.get("ACTUAL_REPAYMENT_INCOME")).add(map.get("LEFT_HOLD_SHARE")).add(map.get("LEFT_HOLD_INCOME"));
		this.portfolioService.illiquidRepayment(order.getPortfolio().getOid(), cash, estimate);
	}

	// 非现金类还款(下订单)
	@Transactional
	public void repaymentForIlliquid(MarketOrderForm form, String operator) {
		// 投资订单表插入数据
		IlliquidAsset asset = this.illiquidAssetService.findByOid(form.getIlliquidAssetOid());
		if (null == asset) {
			throw new GHException("未知的资产ID.");
		}
		PortfolioEntity portfolio = this.portfolioService.getByOid(form.getPortfolioOid());
		if (null == portfolio) {
			throw new GHException("未知的投资组合ID.");
		}
		PortfolioIlliquidHoldRepaymentEntity repayment = this.portfolioIlliquidHoldRepaymentService.findByOid(form.getIlliquidAssetRepaymentOid());
		if (null == repayment) {
			throw new GHException("未知的还款计划ID.");
		}

		PortfolioIlliquidHoldEntity hold = this.portfolioIlliquidHoldService.findHoldingHold(portfolio, asset);

		MarketOrderEntity order = new MarketOrderEntity();
		order.setOid(StringUtil.uuid());
		order.setType(MarketOrderEntity.TYPE_ILLIQUID);
		order.setPortfolio(portfolio);
		order.setIlliquidAsset(asset);
		order.setIlliquidAssetRepayment(repayment);
		order.setOrderState(MarketOrderEntity.ORDER_STATE_SUBMIT);
		order.setDealType(MarketOrderEntity.DEALTYPE_REPAYMENT);
		order.setCapital(form.getOrderCapital().multiply(new BigDecimal(10000)).setScale(2, RoundingMode.HALF_UP));
		order.setIncome(form.getOrderIncome().multiply(new BigDecimal(10000)).setScale(2, RoundingMode.HALF_UP));
		order.setOrderDate(DateUtil.getSqlDate());
		if (PortfolioIlliquidHoldRepaymentEntity.LAST_ISSUE_YES.equals(repayment.getLastIssue())) {
			order.setForceClose("YES");
		} else {
			order.setForceClose("NO");
		}
		order = this.marketOrderdao.save(order);

		Map<String, BigDecimal> map = this.portfolioIlliquidHoldService.freeze(order, hold);

		this.portfolioIlliquidHoldRepaymentService.repaymenting(repayment, order, operator);

		this.illiquidAssetService.applyForRepayment(asset.getOid(), map.get("ACTUAL_FREEZE_CAPITAL"), map.get("ACTUAL_FREEZE_INCOME"));

	}

	// 非现金类还款确认
	@Transactional
	public void passRepaymentForIlliquid(String oid, String operator, String auditMark) {

		int xd = this.marketOrderdao.pass(oid);
		if (xd == 0) {
			throw new AMPException("订单ID错误, 或订单状态已失效.");
		}

		MarketOrderEntity order = this.marketOrderdao.findOne(oid);
		order.setOrderState(MarketOrderEntity.ORDER_STATE_PASS);
		order.setAuditTime(new Timestamp(System.currentTimeMillis()));
		order.setAuditor(operator);
		order.setOrderDate(DateUtil.getSqlDate());
		order.setAuditMark(auditMark);

		// 取合仓数据
		PortfolioIlliquidHoldEntity hold = this.portfolioIlliquidHoldService.findHoldingHold(order.getPortfolio(), order.getIlliquidAsset());

		BigDecimal investCapital = hold.getHoldShare();
		BigDecimal investIncome = hold.getHoldIncome();
		BigDecimal holdShare = hold.getHoldShare();
		if(PortfolioIlliquidHoldEntity.EXCEPT_WAY_BOOK_VALUE.equals(hold.getExceptWay())){
			if(PortfolioIlliquidHoldEntity.HOLDSTATE_HOLDING.equals(hold.getHoldState())){
				hold.setTotalPfofit(hold.getTotalPfofit().add(order.getIncome()));
			}
		}
		// 更新合仓
		Map<String, BigDecimal> map = this.portfolioIlliquidHoldService.subHold(hold, order);

		// 偏离损益
		/*
		 * BigDecimal offset =
		 * order.getCapital().add(order.getIncome()).subtract(map.get(
		 * "ACTUAL_REPAYMENT_CAPITAL")).subtract(map.get(
		 * "ACTUAL_REPAYMENT_INCOME")).add(map.get("LEFT_HOLD_SHARE")).add(map.
		 * get("LEFT_HOLD_INCOME"));
		 */

		BigDecimal selloutShare = map.get("ACTUAL_REPAYMENT_CAPITAL").add(map.get("LEFT_HOLD_SHARE"));
		BigDecimal selloutPrice = order.getCapital().add(order.getIncome());
		BigDecimal losses = selloutPrice.subtract(selloutShare);

		this.portfolioIlliquidHoldRepaymentService.passRepayment(order.getIlliquidAssetRepayment(), operator);

		this.illiquidAssetService.passForRepayment(order.getIlliquidAsset().getOid(), map.get("ACTUAL_REPAYMENT_CAPITAL").add(map.get("LEFT_HOLD_SHARE")), map.get("ACTUAL_REPAYMENT_INCOME").add(map.get("LEFT_HOLD_INCOME")));

		this.portfolioInvestLossesService.save(order, investCapital, investIncome, holdShare, selloutShare, selloutPrice, losses);

		BigDecimal cash = order.getCapital().add(order.getIncome());
		BigDecimal estimate = map.get("ACTUAL_REPAYMENT_CAPITAL").add(map.get("ACTUAL_REPAYMENT_INCOME")).add(map.get("LEFT_HOLD_SHARE")).add(map.get("LEFT_HOLD_INCOME"));
		this.portfolioService.illiquidRepayment(order.getPortfolio().getOid(), cash, estimate);
	}

	// 非现金类还款驳回
	@Transactional
	public void failRepaymentForIlliquid(String oid, String operator, String auditMark) {

		int xd = this.marketOrderdao.fail(oid);
		if (xd == 0) {
			throw new AMPException("订单ID错误, 或订单状态已失效.");
		}

		MarketOrderEntity order = this.marketOrderdao.findOne(oid);
		order.setOrderState(MarketOrderEntity.ORDER_STATE_FAIL);
		order.setOrderDate(DateUtil.getSqlDate());
		order.setAuditTime(new Timestamp(System.currentTimeMillis()));
		order.setAuditor(operator);
		order.setAuditMark(auditMark);
		this.marketOrderdao.save(order);

		PortfolioIlliquidHoldEntity hold = this.portfolioIlliquidHoldService.findHoldingHold(order.getPortfolio(), order.getIlliquidAsset());

		Map<String, BigDecimal> map = this.portfolioIlliquidHoldService.failRepayment(hold, order);

		this.portfolioIlliquidHoldRepaymentService.failRepayment(order.getIlliquidAssetRepayment(), operator);

		this.illiquidAssetService.failForRepayment(order.getIlliquidAsset().getOid(), map.get("ACTUAL_UNFREEZE_CAPITAL"), map.get("ACTUAL_UNFREEZE_INCOME"));
	}

	// 非现金类资产转让
	@Transactional
	public void transForIlliquid(MarketOrderForm form, String dealType, String operator) {
		// 投资订单表插入数据
		IlliquidAsset asset = this.illiquidAssetService.findByOid(form.getIlliquidAssetOid());
		if (null == asset) {
			throw new GHException("未知的资产ID.");
		}
		PortfolioEntity portfolio = this.portfolioService.getByOid(form.getPortfolioOid());
		if (null == portfolio) {
			throw new GHException("未知的投资组合ID.");
		}

		PortfolioIlliquidHoldEntity hold = this.portfolioIlliquidHoldService.findHoldingHold(portfolio, asset);

		MarketOrderEntity order = new MarketOrderEntity();
		order.setOid(StringUtil.uuid());
		order.setType(MarketOrderEntity.TYPE_ILLIQUID);
		order.setPortfolio(portfolio);
		order.setIlliquidAsset(asset);
		order.setOrderState(MarketOrderEntity.ORDER_STATE_SUBMIT);
		order.setDealType(dealType);
		order.setOrderAmount(form.getOrderAmount().multiply(new BigDecimal(10000)).setScale(2, RoundingMode.HALF_UP));
		order.setTradeShare(hold.getHoldShare());
		order.setCapital(hold.getHoldShare());
		order.setIncome(hold.getHoldIncome());
		order.setOrderDate(DateUtil.getSqlDate());
		order.setForceClose("YES");
		order = this.marketOrderdao.save(order);

		Map<String, BigDecimal> map = this.portfolioIlliquidHoldService.freeze(order, hold);

		this.illiquidAssetService.applyForRepayment(asset.getOid(), map.get("ACTUAL_FREEZE_CAPITAL"), map.get("ACTUAL_FREEZE_INCOME"));

	}

	// 非现金类资产转让审核驳回
	@Transactional
	public void failTransForIlliquid(String oid, String operator, String auditMark) {

		int xd = this.marketOrderdao.fail(oid);
		if (xd == 0) {
			throw new AMPException("订单ID错误, 或订单状态已失效.");
		}

		MarketOrderEntity order = this.marketOrderdao.findOne(oid);
		order.setOrderState(MarketOrderEntity.ORDER_STATE_FAIL);
		order.setOrderDate(DateUtil.getSqlDate());
		order.setAuditTime(new Timestamp(System.currentTimeMillis()));
		order.setAuditor(operator);
		order.setAuditMark(auditMark);
		this.marketOrderdao.save(order);

		PortfolioIlliquidHoldEntity hold = this.portfolioIlliquidHoldService.findHoldingHold(order.getPortfolio(), order.getIlliquidAsset());

		Map<String, BigDecimal> map = this.portfolioIlliquidHoldService.failRepayment(hold, order);

		this.illiquidAssetService.failForRepayment(order.getIlliquidAsset().getOid(), map.get("ACTUAL_UNFREEZE_CAPITAL"), map.get("ACTUAL_UNFREEZE_INCOME"));
	}

	// 非现金类资产转让审核通过
	@Transactional
	public void passTransForIlliquid(String oid, String operator, String auditMark) {

		int xd = this.marketOrderdao.pass(oid);
		if (xd == 0) {
			throw new AMPException("订单ID错误, 或订单状态已失效.");
		}

		MarketOrderEntity order = this.marketOrderdao.findOne(oid);
		order.setOrderState(MarketOrderEntity.ORDER_STATE_PASS);
		order.setAuditTime(new Timestamp(System.currentTimeMillis()));
		order.setAuditor(operator);
		order.setOrderDate(DateUtil.getSqlDate());
		order.setAuditMark(auditMark);

		// 取合仓数据
		PortfolioIlliquidHoldEntity hold = this.portfolioIlliquidHoldService.findHoldingHold(order.getPortfolio(), order.getIlliquidAsset());
		BigDecimal investCapital = hold.getHoldShare();
		BigDecimal investIncome = hold.getHoldIncome();
		BigDecimal holdShare = hold.getHoldShare();

		// 更新合仓
		Map<String, BigDecimal> map = this.portfolioIlliquidHoldService.subHold(hold, order, true);

		BigDecimal selloutShare = map.get("ACTUAL_REPAYMENT_CAPITAL").add(map.get("LEFT_HOLD_SHARE"));
		BigDecimal selloutPrice = order.getOrderAmount();

		BigDecimal losses = selloutPrice.subtract(selloutShare);

		// 偏离损益
		/*
		 * BigDecimal offset =
		 * order.getCapital().add(order.getIncome()).subtract(map.get(
		 * "ACTUAL_REPAYMENT_CAPITAL")).subtract(map.get(
		 * "ACTUAL_REPAYMENT_INCOME")).add(map.get("LEFT_HOLD_SHARE")).add(map.
		 * get("LEFT_HOLD_INCOME"));
		 */

		this.illiquidAssetService.passForRepayment(order.getIlliquidAsset().getOid(), map.get("ACTUAL_REPAYMENT_CAPITAL").add(map.get("LEFT_HOLD_SHARE")), map.get("ACTUAL_REPAYMENT_INCOME").add(map.get("LEFT_HOLD_INCOME")));
		this.portfolioInvestLossesService.save(order, investCapital, investIncome, holdShare, selloutShare, selloutPrice, losses);

		BigDecimal cash = order.getOrderAmount();
		BigDecimal estimate = map.get("ACTUAL_REPAYMENT_CAPITAL").add(map.get("ACTUAL_REPAYMENT_INCOME")).add(map.get("LEFT_HOLD_SHARE")).add(map.get("LEFT_HOLD_INCOME"));
		this.portfolioService.illiquidRepayment(order.getPortfolio().getOid(), cash, estimate);
	}

	// 非现金类资产转出
	@Transactional
	public void selloutForIlliquid(MarketOrderForm form, String operator) {
		// 投资订单表插入数据
		IlliquidAsset asset = this.illiquidAssetService.findByOid(form.getIlliquidAssetOid());
		if (null == asset) {
			throw new GHException("未知的资产ID.");
		}
		PortfolioEntity portfolio = this.portfolioService.getByOid(form.getPortfolioOid());
		if (null == portfolio) {
			throw new GHException("未知的投资组合ID.");
		}

		PortfolioIlliquidHoldEntity hold = this.portfolioIlliquidHoldService.findHoldingHold(portfolio, asset);

		MarketOrderEntity order = new MarketOrderEntity();
		order.setOid(StringUtil.uuid());
		order.setType(MarketOrderEntity.TYPE_ILLIQUID);
		order.setPortfolio(portfolio);
		order.setIlliquidAsset(asset);
		order.setOrderState(MarketOrderEntity.ORDER_STATE_SUBMIT);
		order.setDealType(MarketOrderEntity.DEALTYPE_SELLOUT);
		order.setOrderAmount(form.getOrderAmount().multiply(new BigDecimal(10000)).setScale(2, RoundingMode.HALF_UP));
		if ("YES".equalsIgnoreCase(form.getForceClose())) {
			order.setTradeShare(hold.getHoldShare());
			order.setCapital(hold.getHoldShare());
			order.setIncome(hold.getHoldIncome());
			order.setForceClose("YES");
		} else {
			order.setTradeShare(form.getOrderShare().multiply(new BigDecimal(10000)).setScale(2, RoundingMode.HALF_UP));
			order.setCapital(form.getOrderShare().multiply(new BigDecimal(10000)).setScale(2, RoundingMode.HALF_UP));
			order.setIncome(BigDecimal.ZERO);
			order.setForceClose("NO");
		}
		order.setOrderDate(DateUtil.getSqlDate());
		order = this.marketOrderdao.save(order);

		Map<String, BigDecimal> map = this.portfolioIlliquidHoldService.freeze(order, hold);

		this.illiquidAssetService.applyForRepayment(asset.getOid(), map.get("ACTUAL_FREEZE_CAPITAL"), map.get("ACTUAL_FREEZE_INCOME"));

	}

	// 非现金类资产转出审核驳回
	@Transactional
	public void failSelloutForIlliquid(String oid, String operator, String auditMark) {

		int xd = this.marketOrderdao.fail(oid);
		if (xd == 0) {
			throw new AMPException("订单ID错误, 或订单状态已失效.");
		}

		MarketOrderEntity order = this.marketOrderdao.findOne(oid);
		order.setOrderState(MarketOrderEntity.ORDER_STATE_FAIL);
		order.setAuditTime(new Timestamp(System.currentTimeMillis()));
		order.setAuditor(operator);
		order.setOrderDate(DateUtil.getSqlDate());
		order.setAuditMark(auditMark);
		this.marketOrderdao.save(order);

		PortfolioIlliquidHoldEntity hold = this.portfolioIlliquidHoldService.findHoldingHold(order.getPortfolio(), order.getIlliquidAsset());

		Map<String, BigDecimal> map = this.portfolioIlliquidHoldService.failRepayment(hold, order);

		this.illiquidAssetService.failForRepayment(order.getIlliquidAsset().getOid(), map.get("ACTUAL_UNFREEZE_CAPITAL"), map.get("ACTUAL_UNFREEZE_INCOME"));
	}

	// 非现金类资产转出审核通过
	@Transactional
	public void passSelloutForIlliquid(String oid, String operator, String auditMark) {

		int xd = this.marketOrderdao.pass(oid);
		if (xd == 0) {
			throw new AMPException("订单ID错误, 或订单状态已失效.");
		}

		MarketOrderEntity order = this.marketOrderdao.findOne(oid);
		order.setOrderState(MarketOrderEntity.ORDER_STATE_PASS);
		order.setAuditTime(new Timestamp(System.currentTimeMillis()));
		order.setAuditor(operator);
		order.setOrderDate(DateUtil.getSqlDate());
		order.setAuditMark(auditMark);

		// 取合仓数据
		PortfolioIlliquidHoldEntity hold = this.portfolioIlliquidHoldService.findHoldingHold(order.getPortfolio(), order.getIlliquidAsset());

		BigDecimal investCapital = hold.getHoldShare();
		BigDecimal investIncome = hold.getHoldIncome();
		BigDecimal holdShare = hold.getHoldShare();

		// 更新合仓
		Map<String, BigDecimal> map = this.portfolioIlliquidHoldService.subHold(hold, order);

		BigDecimal selloutShare = map.get("ACTUAL_REPAYMENT_CAPITAL").add(map.get("LEFT_HOLD_SHARE"));

		BigDecimal selloutPrice = order.getOrderAmount();

		BigDecimal losses = selloutPrice.subtract(selloutShare);

		// 偏离损益
		/*
		 * BigDecimal offset =
		 * order.getCapital().add(order.getIncome()).subtract(map.get(
		 * "ACTUAL_REPAYMENT_CAPITAL")).subtract(map.get(
		 * "ACTUAL_REPAYMENT_INCOME")).add(map.get("LEFT_HOLD_SHARE")).add(map.
		 * get("LEFT_HOLD_INCOME"));
		 */

		this.illiquidAssetService.passForRepayment(order.getIlliquidAsset().getOid(), map.get("ACTUAL_REPAYMENT_CAPITAL").add(map.get("LEFT_HOLD_SHARE")), map.get("ACTUAL_REPAYMENT_INCOME").add(map.get("LEFT_HOLD_INCOME")));

		this.portfolioInvestLossesService.save(order, investCapital, investIncome, holdShare, selloutShare, selloutPrice, losses);

		BigDecimal cash = order.getOrderAmount();
		BigDecimal estimate = map.get("ACTUAL_REPAYMENT_CAPITAL").add(map.get("ACTUAL_REPAYMENT_INCOME")).add(map.get("LEFT_HOLD_SHARE")).add(map.get("LEFT_HOLD_INCOME"));
		this.portfolioService.illiquidRepayment(order.getPortfolio().getOid(), cash, estimate);
	}

	// 非现金类资产核销
	@Transactional
	public void cancellateForIlliquid(MarketOrderForm form, String dealType, String operator) {
		// 投资订单表插入数据
		IlliquidAsset asset = this.illiquidAssetService.findByOid(form.getIlliquidAssetOid());
		if (null == asset) {
			throw new GHException("未知的资产ID.");
		}
		PortfolioEntity portfolio = this.portfolioService.getByOid(form.getPortfolioOid());
		if (null == portfolio) {
			throw new GHException("未知的投资组合ID.");
		}

		PortfolioIlliquidHoldEntity hold = this.portfolioIlliquidHoldService.findHoldingHold(portfolio, asset);

		MarketOrderEntity order = new MarketOrderEntity();
		order.setOid(StringUtil.uuid());
		order.setType(MarketOrderEntity.TYPE_ILLIQUID);
		order.setPortfolio(portfolio);
		order.setIlliquidAsset(asset);
		order.setOrderState(MarketOrderEntity.ORDER_STATE_SUBMIT);
		order.setDealType(dealType);
		order.setOrderAmount(BigDecimal.ZERO);
		order.setTradeShare(hold.getHoldShare());
		order.setCapital(hold.getHoldShare());
		order.setIncome(hold.getHoldIncome());
		order.setOrderDate(DateUtil.getSqlDate());
		order.setForceClose("YES");
		order = this.marketOrderdao.save(order);

		Map<String, BigDecimal> map = this.portfolioIlliquidHoldService.freeze(order, hold);

		this.illiquidAssetService.applyForRepayment(asset.getOid(), map.get("ACTUAL_FREEZE_CAPITAL"), map.get("ACTUAL_FREEZE_INCOME"));
	}

	// 非现金类资产核销审核驳回
	@Transactional
	public void failCancellateForIlliquid(String oid, String operator, String auditMark) {

		int xd = this.marketOrderdao.fail(oid);
		if (xd == 0) {
			throw new AMPException("订单ID错误, 或订单状态已失效.");
		}

		MarketOrderEntity order = this.marketOrderdao.findOne(oid);
		order.setOrderState(MarketOrderEntity.ORDER_STATE_FAIL);
		order.setOrderDate(DateUtil.getSqlDate());
		order.setAuditTime(new Timestamp(System.currentTimeMillis()));
		order.setAuditor(operator);
		order.setAuditMark(auditMark);
		this.marketOrderdao.save(order);

		PortfolioIlliquidHoldEntity hold = this.portfolioIlliquidHoldService.findHoldingHold(order.getPortfolio(), order.getIlliquidAsset());

		Map<String, BigDecimal> map = this.portfolioIlliquidHoldService.failRepayment(hold, order);

		this.illiquidAssetService.failForRepayment(order.getIlliquidAsset().getOid(), map.get("ACTUAL_UNFREEZE_CAPITAL"), map.get("ACTUAL_UNFREEZE_INCOME"));
	}

	// 非现金类资产核销审核通过
	@Transactional
	public void passCancellateForIlliquid(String oid, String operator, String auditMark) {

		int xd = this.marketOrderdao.pass(oid);
		if (xd == 0) {
			throw new AMPException("订单ID错误, 或订单状态已失效.");
		}

		MarketOrderEntity order = this.marketOrderdao.findOne(oid);
		order.setOrderState(MarketOrderEntity.ORDER_STATE_PASS);
		order.setAuditTime(new Timestamp(System.currentTimeMillis()));
		order.setAuditor(operator);
		order.setOrderDate(DateUtil.getSqlDate());
		order.setAuditMark(auditMark);

		// 取合仓数据
		PortfolioIlliquidHoldEntity hold = this.portfolioIlliquidHoldService.findHoldingHold(order.getPortfolio(), order.getIlliquidAsset());

		BigDecimal investCapital = hold.getHoldShare();
		BigDecimal investIncome = hold.getHoldIncome();
		BigDecimal holdShare = hold.getHoldShare();

		// 更新合仓
		Map<String, BigDecimal> map = this.portfolioIlliquidHoldService.subHold(hold, order, true);

		BigDecimal selloutShare = map.get("ACTUAL_REPAYMENT_CAPITAL").add(map.get("LEFT_HOLD_SHARE"));
		BigDecimal selloutIncome = map.get("ACTUAL_REPAYMENT_INCOME").add(map.get("LEFT_HOLD_INCOME"));
		BigDecimal selloutPrice = BigDecimal.ZERO;
		BigDecimal losses = selloutPrice.subtract(selloutShare);

		// 偏离损益
		/*
		 * BigDecimal offset =
		 * order.getCapital().add(order.getIncome()).subtract(map.get(
		 * "ACTUAL_REPAYMENT_CAPITAL")).subtract(map.get(
		 * "ACTUAL_REPAYMENT_INCOME")).add(map.get("LEFT_HOLD_SHARE")).add(map.
		 * get("LEFT_HOLD_INCOME"));
		 */

		this.illiquidAssetService.passForRepayment(order.getIlliquidAsset().getOid(), selloutShare, selloutIncome);

		this.portfolioInvestLossesService.save(order, investCapital, investIncome, holdShare, selloutShare, selloutPrice, losses);

		BigDecimal cash = BigDecimal.ZERO;
		BigDecimal estimate = map.get("ACTUAL_REPAYMENT_CAPITAL").add(map.get("ACTUAL_REPAYMENT_INCOME")).add(map.get("LEFT_HOLD_SHARE")).add(map.get("LEFT_HOLD_INCOME"));
		this.portfolioService.illiquidRepayment(order.getPortfolio().getOid(), cash, estimate);
	}

	/**
	 * 获取资产交易列表
	 * 
	 * @param spec
	 * @param pageable
	 * @return
	 */
	public Page<MarketOrderEntity> getMarketOrderList(Specification<MarketOrderEntity> spec, Pageable pageable) {
		return this.marketOrderdao.findAll(spec, pageable);
	}
	/**
	 * 查询 资产交易列表 wangzhixin
	 * @param spec
	 * @param pageable
	 * @return
	 */
	public MarketOrderListResp getMarketOrderListSeach(String pname, String aname, int page, int rows) {
		Sort sort = new Sort(new Order(Direction.DESC, "createTime"));
		Pageable pageable = new PageRequest(page, rows, sort);
		String where = "  ";
		String order = "  order by t.orderDate DESC";
		if (!pname.equals("") && null != pname) {
			where += "  and p.name like '%"+pname+"%' ";
		}
		if (!aname.equals("") && null != aname) {
			where+=" and illiquidas1_.name like '%"+aname+"%' ";
		}

		String sql = "select t.* from (SELECT marketorde0_.* FROM T_GAM_ASSET_ORDER marketorde0_,T_GAM_ILLIQUID_ASSET illiquidas1_,T_GAM_PORTFOLIO p  WHERE marketorde0_.illiquidAssetOid = illiquidas1_.oid and marketorde0_.portfolioOid=p.oid and marketorde0_.orderState='SUBMIT' "+where +" "
+" UNION " 
+" SELECT marketorde0_.* FROM T_GAM_ASSET_ORDER marketorde0_,T_GAM_LIQUID_ASSET illiquidas1_,T_GAM_PORTFOLIO p WHERE marketorde0_.liquidAssetOid = illiquidas1_.oid and marketorde0_.portfolioOid=p.oid and marketorde0_.orderState='SUBMIT' "+where+" )t "+order;

		Query query = entityManager.createNativeQuery(sql, MarketOrderEntity.class);
		List<MarketOrderEntity> entityList = query.getResultList();
		long size = entityList.size();
		Query limit = entityManager.createNativeQuery(sql, MarketOrderEntity.class).setFirstResult(page * rows).setMaxResults(rows);
		List<MarketOrderEntity> limitList = limit.getResultList();
		MarketOrderListResp marketOrderListResp = new MarketOrderListResp(limitList, size);
		return marketOrderListResp;
	}

	/**
	 * 获取现金资产管理类交易列表
	 * 
	 * @param spec
	 * @param pageable
	 * @return
	 */
	public Page<PortfolioLiquidHoldEntity> getLiquidManageOrderList(Specification<PortfolioLiquidHoldEntity> spec, Pageable pageable) {
		return this.portfolioLiquidHoldDao.findAll(spec, pageable);
	}

	/**
	 * 获取非现金资产管理类交易列表
	 * 
	 * @param spec
	 * @param pageable
	 * @return
	 */
	public Page<PortfolioIlliquidHoldEntity> getIlliquidManageOrderList(Specification<PortfolioIlliquidHoldEntity> spec, Pageable pageable) {
		return this.portfolioIlliquidHoldDao.findAll(spec, pageable);
	}

	@Transactional
	public MarketOrderResp getMarketOrderByOid(String oid) {
		MarketOrderEntity entity = new MarketOrderEntity();
		if (null == oid || "".equals(oid)) {
			return null;
		} else {
			entity = this.marketOrderdao.findOne(oid);
		}
		return new MarketOrderResp(entity);
	}
	@Transactional
	public MarketOrderEntity getMarketOrderByOidSeach(String oid) {
		MarketOrderEntity entity = new MarketOrderEntity();
		if (null == oid || "".equals(oid)) {
			return null;
		} else {
			entity = this.marketOrderdao.findOne(oid);
		}
		return entity;
	}
}
