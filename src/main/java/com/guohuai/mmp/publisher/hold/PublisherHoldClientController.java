package com.guohuai.mmp.publisher.hold;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.guohuai.basic.component.ext.web.BaseController;
import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.basic.component.ext.web.PageResp;

import net.kaczmarzyk.spring.data.jpa.domain.In;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;


@RestController
@RequestMapping(value = "/mimosa/client/holdconfirm", produces = "application/json")
public class PublisherHoldClientController extends BaseController {

	@Autowired
	private PublisherHoldService  publisherHoldService;
	
	
	
	
	@RequestMapping(value = "pmng", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<PageResp<HoldQueryRep>> mng(HttpServletRequest request,
			@And({@Spec(params = "holdStatus", path = "holdStatus", spec = In.class),
				@Spec(params = "productType", path = "product.type", spec = In.class)}) Specification<PublisherHoldEntity> spec,
			@RequestParam int page, 
			@RequestParam int rows,
			@RequestParam(required = false, defaultValue = "createTime") String sort,
			@RequestParam(required = false, defaultValue = "desc") String order) {
		Direction sortDirection = Direction.DESC;
		if (!"desc".equals(order)) {
			sortDirection = Direction.ASC;
		}
		final String uid = "";
		Specification<PublisherHoldEntity> uidSpec = new Specification<PublisherHoldEntity>() {
			@Override
			public Predicate toPredicate(Root<PublisherHoldEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

				return cb.equal(root.get("investorBaseAccount").get("oid").as(String.class), uid);
			}

		};
		spec = Specifications.where(spec).and(uidSpec);
		Pageable pageable = new PageRequest(page - 1, rows, new Sort(new Order(sortDirection, sort)));
		PageResp<HoldQueryRep> rep = this.publisherHoldService.holdMng(spec, pageable);
		return new ResponseEntity<PageResp<HoldQueryRep>>(rep, HttpStatus.OK);
	}
	
	
	/**
	 * 我的活期---基线   (目前使用这个版本) 
	 */
	@RequestMapping(value = "t0hold", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<MyHoldT0QueryRep> queryMyT0HoldProList() {

		String userOid = this.getLoginUser();
		MyHoldT0QueryRep rep = this.publisherHoldService.queryMyT0HoldProList(userOid);
		return new ResponseEntity<MyHoldT0QueryRep>(rep, HttpStatus.OK);
	}
	
	/**
	 * 我的活期---基线
	 */
	@RequestMapping(value = "newt0hold", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<MyHoldT0QueryRep> queryMyT0HoldProLists() {

		String userOid = this.getLoginUser();
		MyHoldT0QueryRep rep = this.publisherHoldService.queryMyT0HoldProLists(userOid);
		return new ResponseEntity<MyHoldT0QueryRep>(rep, HttpStatus.OK);
	}
	

	/** 
	 * 我的活期  持有中 详情 
	 */
	@RequestMapping(value = "mycurrdetail", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<BaseResp> queryT0HoldingDetail(@RequestParam String productOid) {
		String userOid = this.getLoginUser();
		MyHoldQueryRep rep = this.publisherHoldService.queryT0HoldingDetail(userOid, productOid);
		
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
	
	/**
	 * 我的定期
	 */
	@RequestMapping(value = "tnhold", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<MyHoldTnQueryRep> queryMyTnHoldProList() {

		String userOid = this.getLoginUser();
		MyHoldTnQueryRep rep = this.publisherHoldService.queryMyTnHoldProList(userOid);
		return new ResponseEntity<MyHoldTnQueryRep>(rep, HttpStatus.OK);
	}
	
	/**
	 * 我的定期的每条记录
	 */
	@RequestMapping(value = "newtnhold", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<MyHoldTnQueryRep> queryMyTnHoldProLists() {

		String userOid = this.getLoginUser();
		MyHoldTnQueryRep rep = this.publisherHoldService.queryMyTnHoldProLists(userOid);
		return new ResponseEntity<MyHoldTnQueryRep>(rep, HttpStatus.OK);
	}
	
	
	
	
	/**
	 * 查询 我的定期  持有中  产品详情
	 * version 1
	 */
	@RequestMapping(value = "tningdetail", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<TnHoldingDetail> queryTnHoldingDetail(@RequestParam String productOid) {

		String userOid = this.getLoginUser();
		TnHoldingDetail rep = this.publisherHoldService.queryTnHoldingDetail(userOid, productOid);
		return new ResponseEntity<TnHoldingDetail>(rep, HttpStatus.OK);
	}
	
	/**
	 * 查询 我的定期  持有中  产品详情
	 * version 2 20170310
	 */
	@RequestMapping(value = "tningdetailer", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<TnHoldingDetail> queryTnHoldingDetailer(@RequestParam String productOid) {

		String userOid = this.getLoginUser();
		TnHoldingDetail rep = this.publisherHoldService.queryTnHoldingDetailer(userOid, productOid);
		return new ResponseEntity<TnHoldingDetail>(rep, HttpStatus.OK);
	}
	
	/**
	 * 查询我的已结清定期产品详情
	 * 
	 * @param proOid
	 * @return
	 * 
	 */
	@RequestMapping(value = "closedregularinfo", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<MyClosedRegularInfoQueryRep> closedregularinfo(@RequestParam String proOid) {

		String userOid = this.getLoginUser();
		MyClosedRegularInfoQueryRep rep = this.publisherHoldService.closedregularinfo(userOid, proOid);
		return new ResponseEntity<MyClosedRegularInfoQueryRep>(rep, HttpStatus.OK);
	}
	
	@RequestMapping(value = "mholdvol", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<BaseResp> getMaxHoldVol(@RequestParam String productOid) {
		String investorOid = this.getLoginUser();
		BaseResp rep = this.publisherHoldService.getMaxHoldVol(investorOid, productOid);
		
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
}
