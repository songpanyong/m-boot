package com.guohuai.mmp.platform.publisher.offset;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.guohuai.basic.component.ext.web.BaseController;
import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.basic.component.ext.web.PageResp;
import com.guohuai.mmp.publisher.baseaccount.PublisherBaseAccountEntity;
import com.guohuai.mmp.publisher.baseaccount.loginacc.PublisherLoginAccService;

import net.kaczmarzyk.spring.data.jpa.domain.DateAfterInclusive;
import net.kaczmarzyk.spring.data.jpa.domain.DateBeforeInclusive;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.In;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;


@RestController
@RequestMapping(value = "/mimosa/boot/publisheroffset", produces = "application/json")
public class PublisherOffsetBootController extends BaseController {

	@Autowired
	private PublisherOffsetService  publisherOffsetService;
	@Autowired
	private PublisherLoginAccService publisherLoginAccService;
	
	
	
	@RequestMapping(value = "mng", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<PageResp<PublisherOffsetQueryRep>> mng(HttpServletRequest request,
			@And({@Spec(params = "offsetCode", path = "offsetCode", spec = Equal.class),
				@Spec(params = "offsetDateBegin", path = "offsetDate", spec = DateAfterInclusive.class),
				@Spec(params = "offsetDateEnd", path = "offsetDate", spec = DateBeforeInclusive.class),
				@Spec(params = "closeStatus", path = "closeStatus", spec = In.class),
				@Spec(params = "clearStatus", path = "clearStatus", spec = In.class),
				@Spec(params = "phone", path = "publisherBaseAccount.phone", spec = Equal.class),
				@Spec(params = "realName", path = "publisherBaseAccount.realName", spec = Equal.class),
				@Spec(params = "confirmStatus", path = "confirmStatus", spec = In.class)}) Specification<PublisherOffsetEntity> spec,
			@RequestParam int page, 
			@RequestParam int rows,
			@RequestParam(required = false) String closeStatus, 
			@RequestParam(required = false) String offsetStatus,
			@RequestParam(required = false, defaultValue = "offsetDate") String sort,
			@RequestParam(required = false, defaultValue = "desc") String order) {
		Direction sortDirection = Direction.DESC;
		if (!"desc".equals(order)) {
			sortDirection = Direction.ASC;
		}
		
		Pageable pageable = new PageRequest(page - 1, rows, new Sort(new Order(sortDirection, sort), 
				new Order(Direction.DESC, "createTime"), 
				new Order(Direction.DESC, "oid")));
		PageResp<PublisherOffsetQueryRep> rep = this.publisherOffsetService.mng(spec, pageable);
		return new ResponseEntity<PageResp<PublisherOffsetQueryRep>>(rep, HttpStatus.OK);
	}
	
	@RequestMapping(value = "deta", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<PublisherOffsetDetailRep> detail(@RequestParam(required = true) String offsetOid){
		PublisherOffsetDetailRep detailRep = this.publisherOffsetService.detail(offsetOid);
		return new ResponseEntity<PublisherOffsetDetailRep>(detailRep, HttpStatus.OK);
	}
	
	@RequestMapping(value = "clear", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<BaseResp> clear(@RequestParam String offsetOid){
		
		BaseResp rep = this.publisherOffsetService.clear(offsetOid);
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
	@RequestMapping(value = "close", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<BaseResp> close(@RequestParam String offsetOid){
		
		BaseResp rep = this.publisherOffsetService.close(offsetOid);
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
	@RequestMapping(value = "offsetmoney", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<BaseResp> offsetMoney(@RequestBody OffsetMoneyReq moneyReq){
		BaseResp rep = new BaseResp();
		rep = this.publisherOffsetService.offsetMoney(moneyReq);
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
	@RequestMapping(value = "volconfirm", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<BaseResp> confirm(@RequestParam String offsetOid){
		BaseResp rep = this.publisherOffsetService.confirm(offsetOid);
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
	
	/**
	 * 平台-发行人-轧差查询
	 * @param request
	 * @param spec
	 * @param page
	 * @param rows
	 * @param sort
	 * @param order
	 * @return {@link ResponseEntity<PageResp<PublisherOffsetQueryRep>>}
	 */
	@RequestMapping(value = "mnguid", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<PageResp<PublisherOffsetQueryRep>> mngUid(HttpServletRequest request,
			@And({@Spec(params = "offsetCode", path = "offsetCode", spec = Equal.class),
				@Spec(params = "offsetDateBegin", path = "offsetDate", spec = DateAfterInclusive.class),
				@Spec(params = "offsetDateEnd", path = "offsetDate", spec = DateBeforeInclusive.class)}) Specification<PublisherOffsetEntity> spec,
			@RequestParam int page, 
			@RequestParam int rows,
			@RequestParam(required = false, defaultValue = "createTime") String sort,
			@RequestParam(required = false, defaultValue = "desc") String order) {
		Direction sortDirection = Direction.DESC;
		if (!"desc".equals(order)) {
			sortDirection = Direction.ASC;
		}
		final String uid = this.getLoginUser();
		final PublisherBaseAccountEntity baseAccount = publisherLoginAccService.findByLoginAcc(uid);
		Specification<PublisherOffsetEntity> uidSpec = new Specification<PublisherOffsetEntity>(){
			@Override
			public Predicate toPredicate(Root<PublisherOffsetEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.equal(root.get("publisherBaseAccount"), baseAccount);
			}
			
		};
	
		spec = Specifications.where(spec).and(uidSpec);
		
		Pageable pageable = new PageRequest(page - 1, rows, new Sort(new Order(sortDirection, sort), 
				new Order(Direction.DESC, "createTime"), 
				new Order(Direction.DESC, "oid")));
		PageResp<PublisherOffsetQueryRep> rep = this.publisherOffsetService.mng(spec, pageable);
		return new ResponseEntity<PageResp<PublisherOffsetQueryRep>>(rep, HttpStatus.OK);
	}

}
