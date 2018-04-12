package com.guohuai.mmp.platform.finance.check;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
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

import net.kaczmarzyk.spring.data.jpa.domain.DateAfterInclusive;
import net.kaczmarzyk.spring.data.jpa.domain.DateBeforeInclusive;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.Like;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;

@RestController
@RequestMapping(value = "/mimosa/platform/finance/check", produces = "application/json")
public class PlatformFinanceCheckBootController extends BaseController{
	@Autowired
	PlatformFinanceCheckService platformFinanceCheckService;
	
	/**
	 * 查询
	 */ 
	@RequestMapping(value = "mng", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<PageResp<PlatformFinanceCheckRep>> checkDataList(HttpServletRequest request,
			@And({ @Spec(params = "checkCode", path = "checkCode", spec = Like.class),
					@Spec(params = "checkStatus", path = "checkStatus", spec = Equal.class),
					@Spec(params = "beginCheckDate", path = "checkDate", spec = DateAfterInclusive.class),
					@Spec(params = "endCheckDate", path = "checkDate", spec = DateBeforeInclusive.class) }) Specification<PlatformFinanceCheckEntity> spec,
			@RequestParam int page, @RequestParam int rows,
			@RequestParam(required = false, defaultValue = "checkDate") String sort,
			@RequestParam(required = false, defaultValue = "desc") String order) {
		Direction sortDirection = Direction.DESC;
		if (!"desc".equals(order)) {
			sortDirection = Direction.ASC;
		}
		Pageable pageable = new PageRequest(page - 1, rows, new Sort(new Order(sortDirection, sort)));
		PageResp<PlatformFinanceCheckRep> rep = this.platformFinanceCheckService.checkDataList(spec, pageable);

		return new ResponseEntity<PageResp<PlatformFinanceCheckRep>>(rep, HttpStatus.OK);
	}

	@RequestMapping(value = "checkOrder", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<BaseResp> checkOrder(@RequestParam String checkOid, @RequestParam String checkDate) {
		String operator=this.getLoginUser();
		BaseResp rep = this.platformFinanceCheckService.checkOrder(checkOid, checkDate, operator);
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
	@RequestMapping(value = "checkDataConfirm", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<BaseResp> checkDataConfirm(@RequestParam String oid) {
		String operator=this.getLoginUser();
		BaseResp rep = this.platformFinanceCheckService.checkDataConfirm(oid,operator);
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}

}
