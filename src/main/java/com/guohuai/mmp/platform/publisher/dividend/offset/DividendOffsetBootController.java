package com.guohuai.mmp.platform.publisher.dividend.offset;

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
import com.guohuai.mmp.publisher.baseaccount.PublisherBaseAccountEntity;
import com.guohuai.mmp.publisher.baseaccount.loginacc.PublisherLoginAccService;

import net.kaczmarzyk.spring.data.jpa.domain.DateAfterInclusive;
import net.kaczmarzyk.spring.data.jpa.domain.DateBeforeInclusive;
import net.kaczmarzyk.spring.data.jpa.domain.In;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;


@RestController
@RequestMapping(value = "/mimosa/boot/dividendoffset", produces = "application/json")
public class DividendOffsetBootController extends BaseController {

	@Autowired
	private DividendOffsetService dividendOffsetService;
	@Autowired
	private PublisherLoginAccService publisherLoginAccService;
	
	@RequestMapping(value = "mng", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<PageResp<DividendOffsetQueryRep>> mng(HttpServletRequest request,
			@And({
				@Spec(params = "dividendDateBegin", path = "dividendDate", spec = DateAfterInclusive.class),
				@Spec(params = "dividendDateEnd", path = "dividendDate", spec = DateBeforeInclusive.class),
				@Spec(params = "dividendCloseStatus", path = "dividendCloseStatus", spec = In.class)
				}) Specification<DividendOffsetEntity> spec,
			@RequestParam int page, 
			@RequestParam int rows,
			@RequestParam(required = false) String closeStatus, 
			@RequestParam(required = false) String offsetStatus,
			@RequestParam(required = false, defaultValue = "dividendDate") String sort,
			@RequestParam(required = false, defaultValue = "desc") String order) {
		Direction sortDirection = Direction.DESC;
		if (!"desc".equals(order)) {
			sortDirection = Direction.ASC;
		}
		
		Pageable pageable = new PageRequest(page - 1, rows, new Sort(new Order(sortDirection, sort)));
		PageResp<DividendOffsetQueryRep> rep = this.dividendOffsetService.mng(spec, pageable);
		return new ResponseEntity<PageResp<DividendOffsetQueryRep>>(rep, HttpStatus.OK);
		
		
	}
	
	@RequestMapping(value = "mnguid", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<PageResp<DividendOffsetQueryRep>> mngUid(HttpServletRequest request,
			@And({
				@Spec(params = "dividendDateBegin", path = "dividendDate", spec = DateAfterInclusive.class),
				@Spec(params = "dividendDateEnd", path = "dividendDate", spec = DateBeforeInclusive.class),
				@Spec(params = "dividendCloseStatus", path = "dividendCloseStatus", spec = In.class)
				}) Specification<DividendOffsetEntity> spec,
			@RequestParam int page, 
			@RequestParam int rows,
			@RequestParam(required = false) String closeStatus, 
			@RequestParam(required = false) String offsetStatus,
			@RequestParam(required = false, defaultValue = "dividendDate") String sort,
			@RequestParam(required = false, defaultValue = "desc") String order) {
		Direction sortDirection = Direction.DESC;
		if (!"desc".equals(order)) {
			sortDirection = Direction.ASC;
		}
		
		final String uid = this.getLoginUser();
		final PublisherBaseAccountEntity baseAccount = publisherLoginAccService.findByLoginAcc(uid);
		Specification<DividendOffsetEntity> uidSpec = new Specification<DividendOffsetEntity>(){
			@Override
			public Predicate toPredicate(Root<DividendOffsetEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.equal(root.get("product").get("publisherBaseAccount"), baseAccount);
			}
			
		};
	
		spec = Specifications.where(spec).and(uidSpec);
		
		Pageable pageable = new PageRequest(page - 1, rows, new Sort(new Order(sortDirection, sort)));
		PageResp<DividendOffsetQueryRep> rep = this.dividendOffsetService.mng(spec, pageable);
		return new ResponseEntity<PageResp<DividendOffsetQueryRep>>(rep, HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "close", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<BaseResp> close(@RequestParam String dividendOffsetOid){
		
		BaseResp rep = this.dividendOffsetService.close(dividendOffsetOid);
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "schividend", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<BaseResp> schividend(){
		
		BaseResp rep = new BaseResp();
		this.dividendOffsetService.dividend();
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
	

}
