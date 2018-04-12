package com.guohuai.mmp.investor.tradeorder;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;

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

import com.alibaba.fastjson.JSON;
import com.guohuai.ams.channel.channelapprove.ChannelApprove;
import com.guohuai.ams.product.Product;
import com.guohuai.basic.component.ext.web.BaseController;
import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.basic.component.ext.web.PageResp;
import com.guohuai.component.util.DateUtil;
import com.guohuai.mmp.platform.publisher.offset.PublisherOffsetEntity;
import com.guohuai.mmp.publisher.baseaccount.PublisherBaseAccountEntity;
import com.guohuai.mmp.publisher.baseaccount.loginacc.PublisherLoginAccService;

import net.kaczmarzyk.spring.data.jpa.domain.DateAfterInclusive;
import net.kaczmarzyk.spring.data.jpa.domain.DateBeforeInclusive;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.In;
import net.kaczmarzyk.spring.data.jpa.domain.Null;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;

@RestController
@RequestMapping(value = "/mimosa/boot/tradeorder", produces = "application/json")
public class InvestorTradeOrderBootController extends BaseController {
	
	@Autowired
	private InvestorTradeOrderService investorTradeOrderService;
	@Autowired
	private PublisherLoginAccService publisherLoginAccService;
	
	@Autowired
	private ResubmitTradeOrderService resubmitTradeOrderService;
	
	
	@RequestMapping(value = "deta", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ResponseEntity<TradeOrderDetailRep> detail(@RequestParam(required = true) String tradeOrderOid) {
		TradeOrderDetailRep detailRep = this.investorTradeOrderService.detail(tradeOrderOid);
		return new ResponseEntity<TradeOrderDetailRep>(detailRep, HttpStatus.OK);
	}
	

	
	@RequestMapping(value = "mng", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<PageResp<TradeOrderQueryRep>> mng(HttpServletRequest request,
			@And({@Spec(params = "orderType", path = "orderType", spec = In.class),
				  @Spec(params = "orderStatus", path = "orderStatus", spec = In.class),
				  @Spec(params = "channelOid", path = "channel.oid", spec = Equal.class),
				  @Spec(params = "channelName", path = "channel.channelName", spec = Equal.class),
				  @Spec(params = "orderCode", path = "orderCode", spec = Equal.class),
				  @Spec(params = "createTimeBegin", path = "createTime", spec = DateAfterInclusive.class, config = DateUtil.fullDatePattern),
				  @Spec(params = "createTimeEnd", path = "createTime", spec = DateBeforeInclusive.class, config = DateUtil.fullDatePattern),
				  @Spec(params = "createMan", path = "createMan", spec = Equal.class),
				  @Spec(params = "productName", path = "product.name", spec = Equal.class),
				  @Spec(params = "productType", path = "product.type.oid", spec = Equal.class),
				  @Spec(params = "productOid", path = "product.oid", spec = Equal.class),
				  @Spec(params = "investorOid", path = "investorBaseAccount.oid", spec = Equal.class),
				  @Spec(params = "publisherClearStatus", path = "publisherClearStatus", spec = In.class),
				  @Spec(params = "publisherCloseStatus", path = "publisherCloseStatus", spec = In.class),
				  @Spec(params = "investorOffsetOid", path = "investorOffset.oid", spec = Equal.class),
				  //Filter out of the wishplan trade order
				  //Spec(path="wishplanOid", params="includeWishplan", spec=NotNull.class)
				  @Spec(path = "wishplanOid", spec= Null.class, constVal="true"),
				  @Spec(params = "publisherOffsetOid", path = "publisherOffset.oid", spec = Equal.class)}) Specification<InvestorTradeOrderEntity> spec,
			@RequestParam int page, 
			@RequestParam int rows,
			@RequestParam(required = false) String isPubAcc,
			@RequestParam(required = false, defaultValue = "createTime") String sort,
			@RequestParam(required = false, defaultValue = "desc") String order) {
		Direction sortDirection = Direction.DESC;
		if (!"desc".equals(order)) {
			sortDirection = Direction.ASC;
		}
		
		if (isPubAcc != null && isPubAcc.equals("yes")){
			final String uid = this.getLoginUser();
			final PublisherBaseAccountEntity baseAccount = publisherLoginAccService.findByLoginAcc(uid);
			Specification<InvestorTradeOrderEntity> uidSpec = new Specification<InvestorTradeOrderEntity>(){
				@Override
				public Predicate toPredicate(Root<InvestorTradeOrderEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
					return cb.equal(root.get("publisherBaseAccount"), baseAccount);
				}
				
			};
		
			spec = Specifications.where(spec).and(uidSpec);
		}
		
		Pageable pageable = new PageRequest(page - 1, rows, new Sort(new Order(sortDirection, sort)));
		PageResp<TradeOrderQueryRep> rep = this.investorTradeOrderService.investorTradeOrderMng(spec, pageable, false);
		return new ResponseEntity<PageResp<TradeOrderQueryRep>>(rep, HttpStatus.OK);
	}
	
	//For background
	@RequestMapping(value = "cmsMng", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<PageResp<TradeOrderQueryRep>> cmsMng(HttpServletRequest request,
			@And({@Spec(params = "orderType", path = "orderType", spec = In.class),
				  @Spec(params = "orderStatus", path = "orderStatus", spec = In.class),
				  @Spec(params = "channelOid", path = "channel.oid", spec = Equal.class),
				  @Spec(params = "channelName", path = "channel.channelName", spec = Equal.class),
				  @Spec(params = "orderCode", path = "orderCode", spec = Equal.class),
				  @Spec(params = "createTimeBegin", path = "createTime", spec = DateAfterInclusive.class, config = DateUtil.fullDatePattern),
				  @Spec(params = "createTimeEnd", path = "createTime", spec = DateBeforeInclusive.class, config = DateUtil.fullDatePattern),
				  @Spec(params = "createMan", path = "createMan", spec = Equal.class),
				  @Spec(params = "productName", path = "product.name", spec = Equal.class),
				  @Spec(params = "productType", path = "product.type.oid", spec = Equal.class),
				  @Spec(params = "productOid", path = "product.oid", spec = Equal.class),
				  @Spec(params = "investorOid", path = "investorBaseAccount.oid", spec = Equal.class),
				  @Spec(params = "publisherClearStatus", path = "publisherClearStatus", spec = In.class),
				  @Spec(params = "publisherCloseStatus", path = "publisherCloseStatus", spec = In.class),
				  @Spec(params = "investorOffsetOid", path = "investorOffset.oid", spec = Equal.class),
//				  @Spec(path = "productOid", spec = Null.class, constVal="false"),
				  @Spec(params = "publisherOffsetOid", path = "publisherOffset.oid", spec = Equal.class)}) Specification<InvestorTradeOrderEntity> spec,
			@RequestParam int page, 
			@RequestParam int rows,
			@RequestParam(required = false) String isPubAcc,
			@RequestParam(required = false, defaultValue = "createTime") String sort,
			@RequestParam(required = false, defaultValue = "desc") String order) {
		Direction sortDirection = Direction.DESC;
		if (!"desc".equals(order)) {
			sortDirection = Direction.ASC;
		}
		
		if (isPubAcc != null && isPubAcc.equals("yes")){
			final String uid = this.getLoginUser();
			final PublisherBaseAccountEntity baseAccount = publisherLoginAccService.findByLoginAcc(uid);
			Specification<InvestorTradeOrderEntity> uidSpec = new Specification<InvestorTradeOrderEntity>(){
				@Override
				public Predicate toPredicate(Root<InvestorTradeOrderEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
					return cb.equal(root.get("publisherBaseAccount"), baseAccount);
				}
				
			};
		
			spec = Specifications.where(spec).and(uidSpec);
		}
		
		final Specification<InvestorTradeOrderEntity> typeSpec = new Specification<InvestorTradeOrderEntity>() {
			@Override
			public Predicate toPredicate(Root<InvestorTradeOrderEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Expression<String> exp = root.get("orderType").as(String.class);
				javax.persistence.criteria.CriteriaBuilder.In<String> in = cb.in(exp);
				in.value(InvestorTradeOrderEntity.TRADEORDER_orderType_wishInvest);
				in.value(InvestorTradeOrderEntity.TRADEORDER_orderType_wishRedeem);
				return in;
			}
		};
		
		spec = Specifications.not(typeSpec).and(spec);
		
		Pageable pageable = new PageRequest(page - 1, rows, new Sort(new Order(sortDirection, sort)));
		PageResp<TradeOrderQueryRep> rep = this.investorTradeOrderService.investorTradeOrderMng(spec, pageable, true);
		return new ResponseEntity<PageResp<TradeOrderQueryRep>>(rep, HttpStatus.OK);
	}
	
	@RequestMapping(value = "refundpart", method = { RequestMethod.POST, RequestMethod.POST })
	public @ResponseBody ResponseEntity<BaseResp> refundPart(@RequestBody List<String> tradeOrderList) {
		
			investorTradeOrderService.refundPart(tradeOrderList);
		
		BaseResp rep = new BaseResp();
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
	@RequestMapping(value = "refundall", method = { RequestMethod.POST, RequestMethod.POST })
	public @ResponseBody ResponseEntity<BaseResp> refundAll() {
		
			investorTradeOrderService.refundAll();
		
		BaseResp rep = new BaseResp();
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/repayment", name = "重新结算", method = { RequestMethod.POST, RequestMethod.GET })
	public @ResponseBody ResponseEntity<BaseResp> repayment(@RequestParam String oids) {
		String operator = super.getLoginUser();
		List<String> oidlist = JSON.parseArray(oids, String.class);
		BaseResp repponse = this.investorTradeOrderService.repayment(oidlist, operator);
		return new ResponseEntity<BaseResp>(repponse, HttpStatus.OK);
	}
	
	/**
	 * Added on 2018-02-08
	 * @param oderCode
	 * @return
	 */
	@RequestMapping(value = "/resubmit", name = "订单重新提交", method = { RequestMethod.POST, RequestMethod.GET })
	public @ResponseBody ResponseEntity<BaseResp> resubmit(@RequestParam String orderCode) {
		BaseResp repponse = resubmitTradeOrderService.resubmit(orderCode);
		return new ResponseEntity<BaseResp>(repponse, HttpStatus.OK);
	}
	
}
