package com.guohuai.mmp.investor.tradeorder;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Expression;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.guohuai.ams.product.Product;
import com.guohuai.basic.component.ext.web.BaseController;
import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.basic.component.ext.web.PageResp;
import com.guohuai.component.util.DateUtil;
import com.guohuai.file.legal.LegalService;
import com.guohuai.file.legal.LegalType;
import com.guohuai.mmp.platform.payment.Payment;
import com.guohuai.mmp.publisher.holdapart.closedetails.CloseDetailsEntity;
import com.guohuai.tulip.platform.coupon.CouponEntity;

import lombok.extern.slf4j.Slf4j;
import net.kaczmarzyk.spring.data.jpa.domain.DateAfterInclusive;
import net.kaczmarzyk.spring.data.jpa.domain.DateBeforeInclusive;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.In;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;

@RestController
@RequestMapping(value = "/mimosa/client/tradeorder", produces = "application/json")
@Slf4j
public class InvestorTradeOrderClientController extends BaseController {
	
	@Autowired
	InvestorInvestTradeOrderService investorInvestTradeOrderService;
	@Autowired
	InvestorInvestTradeOrderExtService investorInvestTradeOrderExtService;
	@Autowired
	InvestorRedeemTradeOrderService investorRedeemTradeOrderService;
	
	@Autowired
	InvestorTradeOrderService investorTradeOrderService;
	
	@Autowired
	Payment paymentServiceImpl;
	@Autowired
	LegalService legalService;
	
	@RequestMapping(value = "invest", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<TradeOrderRep> invest(@RequestBody @Valid TradeOrderReq tradeOrderReq) {
		String uid = this.getLoginUser();
		tradeOrderReq.setUid(uid);
		log.info("uid={}, startTime={}", uid, DateUtil.getSqlCurrentDate());
		TradeOrderRep rep =null;
		//体验金申购
		if(CouponEntity.COUPON_TYPE_tasteCoupon.equals(tradeOrderReq.getCouponType())){
			rep = this.investorInvestTradeOrderExtService.expGoldInvest(tradeOrderReq);
		}else{
			rep = this.investorInvestTradeOrderExtService.normalInvest(tradeOrderReq);
		}
		log.info("uid={}, endTime={}", uid, DateUtil.getSqlCurrentDate());
		return new ResponseEntity<TradeOrderRep>(rep, HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "redeem", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<TradeOrderRep> redeem(@RequestBody @Valid RedeemTradeOrderReq redeemTradeOrderReq) {
		String uid = this.getLoginUser();
		
		redeemTradeOrderReq.setUid(uid);
		TradeOrderRep rep = this.investorInvestTradeOrderExtService.redeem(redeemTradeOrderReq);
		return new ResponseEntity<TradeOrderRep>(rep, HttpStatus.OK);
	}
	
	@RequestMapping(value = "isdone", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<BaseResp> isDone(@RequestBody @Valid TradeOrderIsDoneReq isDone) {
		this.getLoginUser();
		
		BaseResp rep = this.investorTradeOrderService.isDone(isDone);
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
	@RequestMapping(value = "mng", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<PageResp<TradeOrderQueryRep>> mng(HttpServletRequest request, @And({
			@Spec(params = "orderType", path = "orderType", spec = In.class),
			@Spec(params = "orderTimeBegin", path = "orderTime", spec = DateAfterInclusive.class, config = DateUtil.defaultDatePattern),
			@Spec(params = "orderTimeEnd", path = "orderTime", spec = DateBeforeInclusive.class, config = DateUtil.defaultDatePattern),
			@Spec(params = "productType", path = "product.type.oid", spec = Equal.class),
			@Spec(params = "productOid", path = "product.oid", spec = Equal.class) }) Specification<InvestorTradeOrderEntity> spec,
			@RequestParam int page, @RequestParam int rows,
			@RequestParam(required = false, defaultValue = "createTime") String sort,
			@RequestParam(required = false, defaultValue = "desc") String order) {
		Direction sortDirection = Direction.DESC;
		if (!"desc".equals(order)) {
			sortDirection = Direction.ASC;
		}
		//筛选当前的用户
		final String uid = this.getLoginUser();
		Specification<InvestorTradeOrderEntity> uidspec = new Specification<InvestorTradeOrderEntity>() {
			@Override
			public Predicate toPredicate(Root<InvestorTradeOrderEntity> root, CriteriaQuery<?> query,
					CriteriaBuilder cb) {

				return cb.equal(root.get("investorBaseAccount").get("oid").as(String.class), uid);
			}
		};

		// To filter the wish middle, include wish invest, redeem
		Specification<InvestorTradeOrderEntity> typeSpec = new Specification<InvestorTradeOrderEntity>() {
			@Override
			public Predicate toPredicate(Root<InvestorTradeOrderEntity> root, CriteriaQuery<?> query,
					CriteriaBuilder cb) {

				Expression<String> exp_state = root.get("orderType").as(String.class);
				Predicate type = exp_state.in(new Object[] { InvestorTradeOrderEntity.TRADEORDER_orderType_wishInvest,
						InvestorTradeOrderEntity.TRADEORDER_orderType_wishRedeem });
				Predicate plan = cb.isNull(root.get("wishplanOid").as(String.class));
				return cb.or(type, plan);
			}
		};

		spec = Specifications.where(uidspec).and(spec).and(typeSpec);
		Pageable pageable = new PageRequest(page - 1, rows, new Sort(new Order(sortDirection, sort)));
		PageResp<TradeOrderQueryRep> rep = this.investorTradeOrderService.investorTradeOrderMng(spec, pageable, false);
		return new ResponseEntity<PageResp<TradeOrderQueryRep>>(rep, HttpStatus.OK);
	}

	/**
	 * 金猪-活期订单持有列表
	 */
	@RequestMapping(value = "currentOrderList", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<TradeOrderListRep> currentOrder(HttpServletRequest request,
			@And({
				  @Spec(params = "productOid", path = "product.oid", spec = Equal.class)
				 }) Specification<InvestorTradeOrderEntity> spec,
			@RequestParam int page, 
			@RequestParam int rows,
			@RequestParam(required = false, defaultValue = "createTime") String sort,
			@RequestParam(required = false, defaultValue = "desc") String order) {
		Direction sortDirection = Direction.DESC;
		if (!"desc".equals(order)) {
			sortDirection = Direction.ASC;
		}
		
		final String uid = this.getLoginUser();
		Specification<InvestorTradeOrderEntity> uidspec = new Specification<InvestorTradeOrderEntity>() {
			@Override
			public Predicate toPredicate(Root<InvestorTradeOrderEntity> root, CriteriaQuery<?> query,
					CriteriaBuilder cb) {
				Predicate a = cb.equal(root.get("investorBaseAccount").get("oid").as(String.class), uid);
				Predicate b = cb.equal(root.get("product").get("type").get("oid").as(String.class), Product.TYPE_Producttype_02);
				javax.persistence.criteria.CriteriaBuilder.In<String> in = cb.in(root.get("orderType").as(String.class));
				in.value(InvestorTradeOrderEntity.TRADEORDER_orderType_invest);
				in.value(InvestorTradeOrderEntity.TRADEORDER_orderType_expGoldInvest);
				return cb.and(a,b, in);
			}
		};
		spec = Specifications.where(uidspec).and(spec);
		
		Pageable pageable = new PageRequest(page - 1, rows, new Sort(new Order(sortDirection, sort)));
		TradeOrderListRep rep = this.investorTradeOrderService.investorTradeOrderList(spec, pageable, uid);
		return new ResponseEntity<TradeOrderListRep>(rep, HttpStatus.OK);
	}
	
	/**
	 * 金猪-活期订单详情
	 */
	@RequestMapping(value = "currentOrderDetail", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<MyOrderDetailRep> currentOrderDetail(HttpServletRequest request,
			@RequestParam(required = true) String oid) {
		
		final String uid = this.getLoginUser();
		Specification<InvestorTradeOrderEntity> spec = new Specification<InvestorTradeOrderEntity>() {
			@Override
			public Predicate toPredicate(Root<InvestorTradeOrderEntity> root, CriteriaQuery<?> query,
					CriteriaBuilder cb) {
				Predicate a = cb.equal(root.get("oid").as(String.class), oid);
				Predicate b = cb.equal(root.get("investorBaseAccount").get("oid").as(String.class), uid);
				return cb.and(a,b);
				
			}
		};
		
		MyOrderDetailRep rep = this.investorTradeOrderService.investorTradeOrderDetail(spec);
		return new ResponseEntity<MyOrderDetailRep>(rep, HttpStatus.OK);
	}
	
	/**
	 * 金猪-活期分仓赎回列表
	 * @return
	 */
	@RequestMapping(value = "currentOrderClose", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<PageResp<TradeOrderCloseQueryRep>> currentOrderClose(HttpServletRequest request,
			@And({
				  @Spec(params = "productOid", path = "product.oid", spec = Equal.class)}) Specification<CloseDetailsEntity> spec,
			@RequestParam int page, 
			@RequestParam int rows,
			@RequestParam(required = false, defaultValue = "createTime") String sort,
			@RequestParam(required = false, defaultValue = "desc") String order) {
		Direction sortDirection = Direction.DESC;
		if (!"desc".equals(order)) {
			sortDirection = Direction.ASC;
		}
		
		final String uid = this.getLoginUser();
		Specification<CloseDetailsEntity> uidspec = new Specification<CloseDetailsEntity>() {
			@Override
			public Predicate toPredicate(Root<CloseDetailsEntity> root, CriteriaQuery<?> query,
					CriteriaBuilder cb) {
				Predicate a = cb.equal(root.get("investorBaseAccount").get("oid").as(String.class), uid);
				Predicate b = cb.equal(root.get("product").get("type").get("oid").as(String.class), Product.TYPE_Producttype_02);
				return cb.and(a,b);
			}
		};
		spec = Specifications.where(uidspec).and(spec);
		
		Pageable pageable = new PageRequest(page - 1, rows, new Sort(new Order(sortDirection, sort)));
		PageResp<TradeOrderCloseQueryRep> rep = this.investorTradeOrderService.investorTradeOrderCloseList(spec, pageable);
		return new ResponseEntity<PageResp<TradeOrderCloseQueryRep>>(rep, HttpStatus.OK);
	}
	
	/**
	 * 
	 * 前端传递所有协议的类型和code码
	 * */
	@RequestMapping(value = "/querylegaltype", method = RequestMethod.POST)
	public ResponseEntity<LegalTypeResp<LegalType>> queryAllLegal() {
		LegalTypeResp<LegalType> resp = this.legalService.find();
		return new ResponseEntity<LegalTypeResp<LegalType>>(resp, HttpStatus.OK);
	}
	
	/**
	 * 
	 * 根据订单号和合同类型，返回给用户相应的合同
	 * 
	 * */
	@RequestMapping(value="/querylegalfile",method = RequestMethod.POST)
	public ResponseEntity<legalFileResp> queryInvestorFile(@RequestParam(required = true) String orderCode,@RequestParam(required = true) String typeCode){
		String investorOid = super.isLogin();
		legalFileResp resp = this.investorInvestTradeOrderExtService.queryInvestorFile(orderCode,typeCode,investorOid);
		return new ResponseEntity<legalFileResp>(resp,HttpStatus.OK);
		}
}
