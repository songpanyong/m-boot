package com.guohuai.ams.portfolio20.order;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.guohuai.ams.portfolio20.illiquid.hold.PortfolioIlliquidHoldEntity;
import com.guohuai.ams.portfolio20.illiquid.hold.PortfolioIlliquidHoldListResp;
import com.guohuai.ams.portfolio20.illiquid.hold.PortfolioIlliquidHoldResp;
import com.guohuai.ams.portfolio20.illiquid.hold.PortfolioIlliquidHoldService;
import com.guohuai.ams.portfolio20.liquid.hold.PortfolioLiquidHoldEntity;
import com.guohuai.ams.portfolio20.liquid.hold.PortfolioLiquidHoldListResp;
import com.guohuai.ams.portfolio20.liquid.hold.PortfolioLiquidHoldResp;
import com.guohuai.ams.portfolio20.liquid.hold.PortfolioLiquidHoldService;
import com.guohuai.basic.component.ext.web.BaseController;
import com.guohuai.component.web.view.Response;

import antlr.collections.List;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.Like;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;

/**
 * 现金类申购赎回
 * 
 * @author star.zhang zhiqiang 2017年02月08日
 */
@RestController
@RequestMapping(value = "/mimosa/market/order", produces = "application/json;charset=utf-8")
public class MarketOrderController extends BaseController {

	@Autowired
	private MarketOrderService marketOrderservice;
	@Autowired
	private PortfolioLiquidHoldService portfolioLiquidHoldService;
	@Autowired
	private PortfolioIlliquidHoldService portfolioIlliquidHoldService;

	/**
	 * 现金类资产申购
	 * 
	 * @param form
	 * @param type
	 *            order（订单）
	 * @return
	 */
	@RequestMapping(value = "/purchaseForLiquid", name = "投资组合 - 现金类资产 - 申购", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<Response> purchaseForLiquid(MarketOrderForm form) {
		marketOrderservice.purchaseForLiquid(form, super.getLoginUser());
		Response r = new Response();
		r.with("result", "SUCCESSED!");
		return new ResponseEntity<Response>(r, HttpStatus.OK);
	}

	/**
	 * 现金类资产申购审核通过
	 * 
	 * @param form
	 */

	@RequestMapping(value = "/passForPurchaseLiquid", name = "投资组合 - 现金类资产 - 申购审核", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<Response> passForPurchaseLiquid(@RequestParam String oid, @RequestParam String auditMark) {
		marketOrderservice.passForPurchaseLiquid(oid, super.getLoginUser(), auditMark);
		Response r = new Response();
		r.with("result", "SUCCESSED!");
		return new ResponseEntity<Response>(r, HttpStatus.OK);
	}

	/**
	 * 现金类资产申购审核驳回
	 * 
	 * @param form
	 */
	@RequestMapping(value = "/failForPurchaseLiquid", name = "投资组合 - 现金类资产 - 申购审核", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<Response> failForPurchaseLiquid(@RequestParam String oid, @RequestParam String auditMark) {
		marketOrderservice.failForPurchaseLiquid(oid, super.getLoginUser(), auditMark);
		Response r = new Response();
		r.with("result", "SUCCESSED!");
		return new ResponseEntity<Response>(r, HttpStatus.OK);
	}

	/**
	 * 资产交易列表--通过
	 * 
	 * @param portfolioOid
	 * @return
	 */
	@RequestMapping(value = "/getMarketOrderListByPortfolioOid", name = "投资组合 - 资产交易列表", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<MarketOrderListResp> list(HttpServletRequest request, @And({ @Spec(params = "portfolioOid", path = "portfolio.oid", spec = Equal.class), @Spec(params = "orderState", path = "orderState", spec = Equal.class) }) Specification<MarketOrderEntity> spec, @RequestParam(required = false, defaultValue = "1") int page, @RequestParam(required = false, defaultValue = "10") int rows,
			@RequestParam(required = false, defaultValue = "orderDate") String sortField, @RequestParam(required = false, defaultValue = "desc") String sort) {

		Direction sortDirection = Direction.DESC;
		if (!"desc".equals(sort)) {
			sortDirection = Direction.ASC;
		}
		Specification<MarketOrderEntity> stateSpec = new Specification<MarketOrderEntity>() {
			@Override
			public Predicate toPredicate(Root<MarketOrderEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.equal(root.get("orderState"), MarketOrderEntity.ORDER_STATE_PASS);
			}
		};
		spec = Specifications.where(spec).and(stateSpec);
		Pageable pageable = new PageRequest(page - 1, rows, new Sort(new Order(sortDirection, sortField)));
		Page<MarketOrderEntity> entitys = marketOrderservice.getMarketOrderList(spec, pageable);
		MarketOrderListResp resps = new MarketOrderListResp(entitys);
		return new ResponseEntity<MarketOrderListResp>(resps, HttpStatus.OK);
	}

	/**
	 * 资产交易审核列表--新建
	 * 
	 * @param portfolioOid
	 * @return
	 */
	@RequestMapping(value = "/getToAuditMarketOrderListByPortfolioOid", name = "投资组合 - 资产交易列表", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<MarketOrderListResp> toAuditList(HttpServletRequest request,@RequestParam String portfolioName,@RequestParam String assetName, @RequestParam(required = false, defaultValue = "1") int page, @RequestParam(required = false, defaultValue = "10") int rows) {
		MarketOrderListResp resps=marketOrderservice.getMarketOrderListSeach(portfolioName,assetName,page-1,rows);
		return new ResponseEntity<MarketOrderListResp>(resps, HttpStatus.OK);
	}

	/**
	 * 根据oid获取资产交易的详细信息
	 * 
	 * @param oid
	 * @return
	 */
	@RequestMapping(value = "/getMarketOrderByOid", name = "资产交易 - 根据oid查询资产交易", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<Response> getMarketOrderByOid(@RequestParam String oid) {
		MarketOrderResp resp = marketOrderservice.getMarketOrderByOid(oid);
		Response r = new Response();
		r.with("result", resp);
		return new ResponseEntity<Response>(r, HttpStatus.OK);
	}

	/**
	 * 资产交易记录列表--新建 通过 驳回
	 * 
	 * @param portfolioOid
	 * @return
	 */
	@RequestMapping(value = "/getMarketOrderRecordList", name = "投资组合 - 资产交易列表", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<MarketOrderListResp> recordList(HttpServletRequest request, @And({ @Spec(params = "portfolioOid", path = "portfolio.oid", spec = Like.class), @Spec(params = "assetName", path = "liquidAsset.name", spec = Like.class), @Spec(params = "assetName", path = "illiquidAsset.name", spec = Like.class), @Spec(params = "orderState", path = "orderState", spec = Equal.class) }) Specification<MarketOrderEntity> spec,
			@RequestParam(required = false, defaultValue = "1") int page, @RequestParam(required = false, defaultValue = "10") int rows, @RequestParam(required = false, defaultValue = "orderDate") String sortField, @RequestParam(required = false, defaultValue = "desc") String sort) {

		Direction sortDirection = Direction.DESC;
		if (!"desc".equals(sort)) {
			sortDirection = Direction.ASC;
		}
		Pageable pageable = new PageRequest(page - 1, rows, new Sort(new Order(sortDirection, sortField)));
		Page<MarketOrderEntity> entitys = marketOrderservice.getMarketOrderList(spec, pageable);
		MarketOrderListResp resps = new MarketOrderListResp(entitys);
		System.out.println(resps);
		return new ResponseEntity<MarketOrderListResp>(resps, HttpStatus.OK);
	}

	/**
	 * 赎回（货币基金）
	 * 
	 * @param from
	 */
	@RequestMapping(value = "/redeemForLiquid", name = "投资组合 - 货币基金 - 赎回驳回", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<Response> redeemForLiquid(MarketOrderForm form) {
		marketOrderservice.redeemForLiquid(form, super.getLoginUser());
		Response r = new Response();
		r.with("result", "SUCCESSED!");
		return new ResponseEntity<Response>(r, HttpStatus.OK);
	}

	/**
	 * 赎回驳回（货币基金）
	 * 
	 * @param from
	 */
	@RequestMapping(value = "/failRedeemForLiquid", name = "投资组合 - 货币基金 - 赎回驳回", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<Response> failRedeemForLiquid(@RequestParam String oid, @RequestParam String auditMark) {
		marketOrderservice.failRedeemForLiquid(oid, super.getLoginUser(), auditMark);
		Response r = new Response();
		r.with("result", "SUCCESSED!");
		return new ResponseEntity<Response>(r, HttpStatus.OK);
	}

	/**
	 * 赎回通过（货币基金）
	 * 
	 * @param from
	 */
	@RequestMapping(value = "/passRedeemForLiquid", name = "投资组合 - 货币基金 - 赎回审核通过", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<Response> passRedeemForLiquid(@RequestParam String oid, @RequestParam String auditMark) {
		marketOrderservice.passRedeemForLiquid(oid, super.getLoginUser(), auditMark);
		Response r = new Response();
		r.with("result", "SUCCESSED!");
		return new ResponseEntity<Response>(r, HttpStatus.OK);
	}

	/**
	 * 获取现金资产管理类交易列表
	 * 
	 * @param spec
	 * @param pageable
	 * @return
	 */
	@RequestMapping(value = "/getLiquidManageOrderList", name = "投资组合 - 资产交易列表", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<PortfolioLiquidHoldListResp> getLiquidManageOrderList(HttpServletRequest request, @RequestParam String oid, @RequestParam(required = false, defaultValue = "1") int page, @RequestParam(required = false, defaultValue = "10") int rows, @RequestParam(required = false, defaultValue = "createTime") String sortField, @RequestParam(required = false, defaultValue = "desc") String sort) {

		Direction sortDirection = Direction.DESC;
		if (!"desc".equals(sort)) {
			sortDirection = Direction.ASC;
		}
		Specification<PortfolioLiquidHoldEntity> spec = new Specification<PortfolioLiquidHoldEntity>() {
			@Override
			public Predicate toPredicate(Root<PortfolioLiquidHoldEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate state = cb.equal(root.get("holdState"), PortfolioLiquidHoldEntity.HOLD_STATE_HOLDING);
				Predicate portfolio = cb.equal(root.get("portfolio").get("oid").as(String.class), oid);
				return cb.and(state, portfolio);
			}
		};
		Pageable pageable = new PageRequest(page - 1, rows, new Sort(new Order(sortDirection, sortField)));
		Page<PortfolioLiquidHoldEntity> entitys = marketOrderservice.getLiquidManageOrderList(spec, pageable);
		PortfolioLiquidHoldListResp resps = new PortfolioLiquidHoldListResp(entitys);
		return new ResponseEntity<PortfolioLiquidHoldListResp>(resps, HttpStatus.OK);
	}

	/**
	 * 获取非现金资产管理类交易列表
	 * 
	 * @param spec
	 * @param pageable
	 * @return
	 */
	@RequestMapping(value = "/getIlliquidManageOrderList", name = "投资组合 - 资产交易列表", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<PortfolioIlliquidHoldListResp> getIlliquidManageOrderList(HttpServletRequest request, @RequestParam String oid, @RequestParam(required = false, defaultValue = "1") int page, @RequestParam(required = false, defaultValue = "10") int rows, @RequestParam(required = false, defaultValue = "createTime") String sortField, @RequestParam(required = false, defaultValue = "desc") String sort) {

		Direction sortDirection = Direction.DESC;
		if (!"desc".equals(sort)) {
			sortDirection = Direction.ASC;
		}
		Specification<PortfolioIlliquidHoldEntity> spec = new Specification<PortfolioIlliquidHoldEntity>() {
			@Override
			public Predicate toPredicate(Root<PortfolioIlliquidHoldEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate state = cb.equal(root.get("holdState"), PortfolioIlliquidHoldEntity.HOLDSTATE_HOLDING);
				Predicate portfolio = cb.equal(root.get("portfolio").get("oid").as(String.class), oid);
				return cb.and(state, portfolio);
			}
		};
		Pageable pageable = new PageRequest(page - 1, rows, new Sort(new Order(sortDirection, sortField)));
		Page<PortfolioIlliquidHoldEntity> entitys = marketOrderservice.getIlliquidManageOrderList(spec, pageable);
		PortfolioIlliquidHoldListResp resps = new PortfolioIlliquidHoldListResp(entitys);
		return new ResponseEntity<PortfolioIlliquidHoldListResp>(resps, HttpStatus.OK);
	}

	/**
	 * 获得现金赎回详情
	 * 
	 * @param investment
	 * @return
	 */
	@RequestMapping(value = "/getLiquidRedeemOrderList", name = "赎回按钮详情展示", method = { RequestMethod.POST, RequestMethod.GET })
	public @ResponseBody ResponseEntity<PortfolioLiquidHoldResp> getLiquidRedeemOrderList(@RequestParam String oid) {
		PortfolioLiquidHoldEntity entitys = portfolioLiquidHoldService.findByOid(oid);
		PortfolioLiquidHoldResp resp = new PortfolioLiquidHoldResp(entitys);
		return new ResponseEntity<PortfolioLiquidHoldResp>(resp, HttpStatus.OK);
	}

	/**
	 * 获得非现金赎回详情
	 * 
	 * @param investment
	 * @return
	 */
	@RequestMapping(value = "/getIlliquidRedeemOrderList", name = "赎回按钮详情展示", method = { RequestMethod.POST, RequestMethod.GET })
	public @ResponseBody ResponseEntity<PortfolioIlliquidHoldResp> getIlliquidRedeemOrderList(@RequestParam String oid) {
		PortfolioIlliquidHoldEntity entitys = portfolioIlliquidHoldService.findByOid(oid);
		PortfolioIlliquidHoldResp resp = new PortfolioIlliquidHoldResp(entitys);
		return new ResponseEntity<PortfolioIlliquidHoldResp>(resp, HttpStatus.OK);
	}

	/**
	 * 非现金类资产 申购
	 * 
	 * @param portfolioOid
	 *            投资组合Id
	 * @param liquidAssetOid
	 *            非现金类资产Id
	 * @param orderAmount
	 *            申购价格
	 * @param orderShare
	 *            申购份额
	 * @param exceptWay
	 *            估值方式 账面价值法: BOOK_VALUE; 摊余成本法: AMORTISED_COST;
	 * @return
	 */
	@RequestMapping(value = "/purchaseForIlliquid", name = "投资组合 - 非现金类资产 - 申购", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<Response> purchaseForIlliquid(MarketOrderForm form) {
		this.marketOrderservice.purchaseForIlliquid(form, MarketOrderEntity.DEALTYPE_PURCHASE, super.getLoginUser());
		Response r = new Response();
		r.with("result", "SUCCESSED!");
		return new ResponseEntity<Response>(r, HttpStatus.OK);
	}

	/**
	 * 非现金类资产 申购审核通过
	 * 
	 * @param oid
	 *            订单Id
	 * @param auditMark
	 *            审核意见
	 * @return
	 */
	@RequestMapping(value = "/passForPurchaseIlliquid", name = "投资组合 - 非现金类资产 - 申购审核通过", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<Response> passForPurchaseIlliquid(@RequestParam String oid, @RequestParam String auditMark) {
		this.marketOrderservice.passForPurchaseIlliquid(oid, super.getLoginUser(), auditMark);
		Response r = new Response();
		r.with("result", "SUCCESSED!");
		return new ResponseEntity<Response>(r, HttpStatus.OK);
	}

	/**
	 * 非现金类资产 申购审核驳回
	 * 
	 * @param oid
	 *            订单Id
	 * @param auditMark
	 *            审核意见
	 * @return
	 */
	@RequestMapping(value = "/failForPurchaseIlliquid", name = "投资组合 - 非现金类资产 - 申购审核驳回", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<Response> failForPurchaseIlliquid(@RequestParam String oid, @RequestParam String auditMark) {
		this.marketOrderservice.failForPurchaseIlliquid(oid, super.getLoginUser(), auditMark);
		Response r = new Response();
		r.with("result", "SUCCESSED!");
		return new ResponseEntity<Response>(r, HttpStatus.OK);
	}

	/**
	 * 非现金类资产 认购
	 * 
	 * @param portfolioOid
	 *            投资组合Id
	 * @param liquidAssetOid
	 *            非现金类资产Id
	 * @param orderAmount
	 *            成交价格
	 * @param orderShare
	 *            买入份额
	 * @param exceptWay
	 *            估值方式 账面价值法: BOOK_VALUE; 摊余成本法: AMORTISED_COST;
	 * @return
	 */
	@RequestMapping(value = "/subscripeForIlliquid", name = "投资组合 - 非现金类资产 - 认购", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<Response> subscripeForIlliquid(MarketOrderForm form) {
		this.marketOrderservice.purchaseForIlliquid(form, MarketOrderEntity.DEALTYPE_SUBSCRIPE, super.getLoginUser());
		Response r = new Response();
		r.with("result", "SUCCESSED!");
		return new ResponseEntity<Response>(r, HttpStatus.OK);
	}

	/**
	 * 非现金类资产 认购审核通过
	 * 
	 * @param oid
	 *            订单Id
	 * @param auditMark
	 *            审核意见
	 * @return
	 */
	@RequestMapping(value = "/passForSubscripeIlliquid", name = "投资组合 - 非现金类资产 - 认购审核通过", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<Response> passForSubscripeIlliquid(@RequestParam String oid, @RequestParam String auditMark) {
		this.marketOrderservice.passForPurchaseIlliquid(oid, super.getLoginUser(), auditMark);
		Response r = new Response();
		r.with("result", "SUCCESSED!");
		return new ResponseEntity<Response>(r, HttpStatus.OK);
	}

	/**
	 * 非现金类资产 认购审核驳回
	 * 
	 * @param oid
	 *            订单Id
	 * @param auditMark
	 *            审核意见
	 * @return
	 */
	@RequestMapping(value = "/failForubscripeIlliquid", name = "投资组合 - 非现金类资产 - 认购审核驳回", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<Response> failForubscripeIlliquid(@RequestParam String oid, @RequestParam String auditMark) {
		this.marketOrderservice.failForPurchaseIlliquid(oid, super.getLoginUser(), auditMark);
		Response r = new Response();
		r.with("result", "SUCCESSED!");
		return new ResponseEntity<Response>(r, HttpStatus.OK);
	}

	/**
	 * 非现金类标的 还款
	 * 
	 * @param portfolioOid
	 *            投资组合Id
	 * @param liquidAssetOid
	 *            非现金类资产Id
	 * @param illiquidAssetRepaymentOid
	 *            还款计划Id
	 * @param orderCapital
	 *            还款本金
	 * @param orderIncome
	 *            还款利息
	 * @return
	 */
	@RequestMapping(value = "/repaymentForIlliquid", name = "投资组合 - 非现金类资产 - 还款", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<Response> repaymentForIlliquid(MarketOrderForm form) {
		this.marketOrderservice.repaymentForIlliquid(form, super.getLoginUser());
		Response r = new Response();
		r.with("result", "SUCCESSED!");
		return new ResponseEntity<Response>(r, HttpStatus.OK);
	}

	/**
	 * 非现金类标的 还款审核失败
	 * 
	 * @param oid
	 *            订单Id
	 * @param auditMark
	 *            审核意见
	 * @return
	 */
	@RequestMapping(value = "/failRepaymentForIlliquid", name = "投资组合 - 非现金类资产 - 还款审核失败", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<Response> failRepaymentForIlliquid(@RequestParam String oid, @RequestParam String auditMark) {
		this.marketOrderservice.failRepaymentForIlliquid(oid, super.getLoginUser(), auditMark);
		Response r = new Response();
		r.with("result", "SUCCESSED!");
		return new ResponseEntity<Response>(r, HttpStatus.OK);
	}

	/**
	 * 非现金类资产 还款审核通过
	 * 
	 * @param oid
	 *            订单Id
	 * @param auditMark
	 *            审核意见
	 * @return
	 */
	@RequestMapping(value = "/passRepaymentForIlliquid", name = "投资组合 - 非现金类资产 - 还款审核通过", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<Response> passRepaymentForIlliquid(@RequestParam String oid, @RequestParam String auditMark) {
		this.marketOrderservice.passRepaymentForIlliquid(oid, super.getLoginUser(), auditMark);
		Response r = new Response();
		r.with("result", "SUCCESSED!");
		return new ResponseEntity<Response>(r, HttpStatus.OK);
	}

	/**
	 * 非现金类标的 转让
	 * 
	 * @param portfolioOid
	 *            投资组合Id
	 * @param liquidAssetOid
	 *            非现金类资产Id
	 * @param orderAmount
	 *            转让价格
	 * @return
	 */
	@RequestMapping(value = "/transferForIlliquid", name = "投资组合 - 非现金类资产 - 转让", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<Response> transferForIlliquid(MarketOrderForm form) {
		this.marketOrderservice.transForIlliquid(form, MarketOrderEntity.DEALTYPE_TRANSFER, super.getLoginUser());
		Response r = new Response();
		r.with("result", "SUCCESSED!");
		return new ResponseEntity<Response>(r, HttpStatus.OK);
	}

	/**
	 * 非现金类标的 转让审核失败
	 * 
	 * @param oid
	 *            订单Id
	 * @param auditMark
	 *            审核意见
	 * @return
	 */
	@RequestMapping(value = "/failTransferForIlliquid", name = "投资组合 - 非现金类资产 - 转让审核失败", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<Response> failTransferForIlliquid(@RequestParam String oid, @RequestParam String auditMark) {
		this.marketOrderservice.failTransForIlliquid(oid, super.getLoginUser(), auditMark);
		Response r = new Response();
		r.with("result", "SUCCESSED!");
		return new ResponseEntity<Response>(r, HttpStatus.OK);
	}

	/**
	 * 非现金类资产 转让审核通过
	 * 
	 * @param oid
	 *            订单Id
	 * @param auditMark
	 *            审核意见
	 * @return
	 */
	@RequestMapping(value = "/passTransferForIlliquid", name = "投资组合 - 非现金类资产 - 转让审核通过", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<Response> passTransferForIlliquid(@RequestParam String oid, @RequestParam String auditMark) {
		this.marketOrderservice.passTransForIlliquid(oid, super.getLoginUser(), auditMark);
		Response r = new Response();
		r.with("result", "SUCCESSED!");
		return new ResponseEntity<Response>(r, HttpStatus.OK);
	}

	/**
	 * 非现金类标的 退款
	 * 
	 * @param portfolioOid
	 *            投资组合Id
	 * @param liquidAssetOid
	 *            非现金类资产Id
	 * @param orderAmount
	 *            退款金额
	 * @return
	 */
	@RequestMapping(value = "/refundForIlliquid", name = "投资组合 - 非现金类资产 - 转让", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<Response> refundForIlliquid(MarketOrderForm form) {
		this.marketOrderservice.refundForIlliquid(form, super.getLoginUser());
		Response r = new Response();
		r.with("result", "SUCCESSED!");
		return new ResponseEntity<Response>(r, HttpStatus.OK);
	}

	/**
	 * 非现金类标的 退款审核失败
	 * 
	 * @param oid
	 *            订单Id
	 * @param auditMark
	 *            审核意见
	 * @return
	 */
	@RequestMapping(value = "/failRefundForIlliquid", name = "投资组合 - 非现金类资产 - 转让审核失败", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<Response> failRefundForIlliquid(@RequestParam String oid, @RequestParam String auditMark) {
		this.marketOrderservice.failRefundForIlliquid(oid, super.getLoginUser(), auditMark);
		Response r = new Response();
		r.with("result", "SUCCESSED!");
		return new ResponseEntity<Response>(r, HttpStatus.OK);
	}

	/**
	 * 非现金类资产 转让审核通过
	 * 
	 * @param oid
	 *            订单Id
	 * @param auditMark
	 *            审核意见
	 * @return
	 */
	@RequestMapping(value = "/passRefundForIlliquid", name = "投资组合 - 非现金类资产 - 转让审核通过", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<Response> passRefundForIlliquid(@RequestParam String oid, @RequestParam String auditMark) {
		this.marketOrderservice.passRefundForIlliquid(oid, super.getLoginUser(), auditMark);
		Response r = new Response();
		r.with("result", "SUCCESSED!");
		return new ResponseEntity<Response>(r, HttpStatus.OK);
	}

	/**
	 * 非现金类标的 逾期转让
	 * 
	 * @param portfolioOid
	 *            投资组合Id
	 * @param liquidAssetOid
	 *            非现金类资产Id
	 * @param orderAmount
	 *            转让价格
	 * @return
	 */
	@RequestMapping(value = "/overduetransForIlliquid", name = "投资组合 - 非现金类资产 - 逾期转让", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<Response> overduetransForIlliquid(MarketOrderForm form) {
		this.marketOrderservice.transForIlliquid(form, MarketOrderEntity.DEALTYPE_OVERDUETRANS, super.getLoginUser());
		Response r = new Response();
		r.with("result", "SUCCESSED!");
		return new ResponseEntity<Response>(r, HttpStatus.OK);
	}

	/**
	 * 非现金类标的 逾期转让审核失败
	 * 
	 * @param oid
	 *            订单Id
	 * @param auditMark
	 *            审核意见
	 * @return
	 */
	@RequestMapping(value = "/failOverduetransForIlliquid", name = "投资组合 - 非现金类资产 - 逾期转让审核失败", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<Response> failOverduetransForIlliquid(@RequestParam String oid, @RequestParam String auditMark) {
		this.marketOrderservice.failTransForIlliquid(oid, super.getLoginUser(), auditMark);
		Response r = new Response();
		r.with("result", "SUCCESSED!");
		return new ResponseEntity<Response>(r, HttpStatus.OK);
	}

	/**
	 * 非现金类资产 逾期转让审核通过
	 * 
	 * @param oid
	 *            订单Id
	 * @param auditMark
	 *            审核意见
	 * @return
	 */
	@RequestMapping(value = "/passOverduetransForIlliquid", name = "投资组合 - 非现金类资产 - 逾期转让审核通过", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<Response> passOverduetransForIlliquid(@RequestParam String oid, @RequestParam String auditMark) {
		this.marketOrderservice.passTransForIlliquid(oid, super.getLoginUser(), auditMark);
		Response r = new Response();
		r.with("result", "SUCCESSED!");
		return new ResponseEntity<Response>(r, HttpStatus.OK);
	}

	/**
	 * 非现金类标的 转出
	 * 
	 * @param portfolioOid
	 *            投资组合Id
	 * @param liquidAssetOid
	 *            非现金类资产Id
	 * @param orderAmount
	 *            转让价格
	 * @param orderShare
	 *            转出份额
	 * @param forceClose
	 *            是否全部转出{是: YES} {否: NO}
	 * @return
	 */
	@RequestMapping(value = "/selloutForIlliquid", name = "投资组合 - 非现金类资产 - 转出", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<Response> selloutForIlliquid(MarketOrderForm form) {
		this.marketOrderservice.selloutForIlliquid(form, super.getLoginUser());
		Response r = new Response();
		r.with("result", "SUCCESSED!");
		return new ResponseEntity<Response>(r, HttpStatus.OK);
	}

	/**
	 * 非现金类标的 转出审核失败
	 * 
	 * @param oid
	 *            订单Id
	 * @param auditMark
	 *            审核意见
	 * @return
	 */
	@RequestMapping(value = "/failSelloutForIlliquid", name = "投资组合 - 非现金类资产 - 转出审核失败", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<Response> failSelloutForIlliquid(@RequestParam String oid, @RequestParam String auditMark) {
		this.marketOrderservice.failSelloutForIlliquid(oid, super.getLoginUser(), auditMark);
		Response r = new Response();
		r.with("result", "SUCCESSED!");
		return new ResponseEntity<Response>(r, HttpStatus.OK);
	}

	/**
	 * 非现金类资产 转出审核通过
	 * 
	 * @param oid
	 *            订单Id
	 * @param auditMark
	 *            审核意见
	 * @return
	 */
	@RequestMapping(value = "/passSelloutForIlliquid", name = "投资组合 - 非现金类资产 - 转出审核通过", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<Response> passSelloutForIlliquid(@RequestParam String oid, @RequestParam String auditMark) {
		this.marketOrderservice.passSelloutForIlliquid(oid, super.getLoginUser(), auditMark);
		Response r = new Response();
		r.with("result", "SUCCESSED!");
		return new ResponseEntity<Response>(r, HttpStatus.OK);
	}

	/**
	 * 非现金类标的 坏账核销
	 * 
	 * @param portfolioOid
	 *            投资组合Id
	 * @param liquidAssetOid
	 *            非现金类资产Id
	 * @return
	 */
	@RequestMapping(value = "/cancellateForIlliquid", name = "投资组合 - 非现金类资产 - 坏账核销", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<Response> cancellateForIlliquid(MarketOrderForm form) {
		this.marketOrderservice.cancellateForIlliquid(form, MarketOrderEntity.DEALTYPE_CANCELLATE, super.getLoginUser());
		Response r = new Response();
		r.with("result", "SUCCESSED!");
		return new ResponseEntity<Response>(r, HttpStatus.OK);
	}

	/**
	 * 非现金类标的 坏账核销审核失败
	 * 
	 * @param oid
	 *            订单Id
	 * @param auditMark
	 *            审核意见
	 * @return
	 */
	@RequestMapping(value = "/failCancellateForIlliquid", name = "投资组合 - 非现金类资产 - 坏账核销审核失败", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<Response> failCancellateForIlliquid(@RequestParam String oid, @RequestParam String auditMark) {
		this.marketOrderservice.failCancellateForIlliquid(oid, super.getLoginUser(), auditMark);
		Response r = new Response();
		r.with("result", "SUCCESSED!");
		return new ResponseEntity<Response>(r, HttpStatus.OK);
	}

	/**
	 * 非现金类资产 坏账核销审核通过
	 * 
	 * @param oid
	 *            订单Id
	 * @param auditMark
	 *            审核意见
	 * @return
	 */
	@RequestMapping(value = "/passCancellateForIlliquid", name = "投资组合 - 非现金类资产 - 坏账核销审核通过", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<Response> passCancellateForIlliquid(@RequestParam String oid, @RequestParam String auditMark) {
		this.marketOrderservice.passCancellateForIlliquid(oid, super.getLoginUser(), auditMark);
		Response r = new Response();
		r.with("result", "SUCCESSED!");
		return new ResponseEntity<Response>(r, HttpStatus.OK);
	}

	/**
	 * 非现金类标的 逾期坏账核销
	 * 
	 * @param portfolioOid
	 *            投资组合Id
	 * @param liquidAssetOid
	 *            非现金类资产Id
	 * @return
	 */
	@RequestMapping(value = "/overdueCancellateForIlliquid", name = "投资组合 - 非现金类资产 - 逾期坏账核销", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<Response> overdueCancellateForIlliquid(MarketOrderForm form) {
		this.marketOrderservice.cancellateForIlliquid(form, MarketOrderEntity.DEALTYPE_OVERDUECANCELLATE, super.getLoginUser());
		Response r = new Response();
		r.with("result", "SUCCESSED!");
		return new ResponseEntity<Response>(r, HttpStatus.OK);
	}

	/**
	 * 非现金类标的 逾期坏账核销审核失败
	 * 
	 * @param oid
	 *            订单Id
	 * @param auditMark
	 *            审核意见
	 * @return
	 */
	@RequestMapping(value = "/failOverdueCancellateForIlliquid", name = "投资组合 - 非现金类资产 - 逾期坏账核销审核失败", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<Response> failOverdueCancellateForIlliquid(@RequestParam String oid, @RequestParam String auditMark) {
		this.marketOrderservice.failCancellateForIlliquid(oid, super.getLoginUser(), auditMark);
		Response r = new Response();
		r.with("result", "SUCCESSED!");
		return new ResponseEntity<Response>(r, HttpStatus.OK);
	}

	/**
	 * 非现金类资产 逾期坏账核销审核通过
	 * 
	 * @param oid
	 *            订单Id
	 * @param auditMark
	 *            审核意见
	 * @return
	 */
	@RequestMapping(value = "/passOverdueCancellateForIlliquid", name = "投资组合 - 非现金类资产 - 逾期坏账核销审核通过", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<Response> passOverdueCancellateForIlliquid(@RequestParam String oid, @RequestParam String auditMark) {
		this.marketOrderservice.passCancellateForIlliquid(oid, super.getLoginUser(), auditMark);
		Response r = new Response();
		r.with("result", "SUCCESSED!");
		return new ResponseEntity<Response>(r, HttpStatus.OK);
	}
}
