package com.guohuai.mmp.platform.payment.log;

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
import com.guohuai.basic.component.ext.web.PageResp;

import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;

@RestController
@RequestMapping(value = "/mimosa/boot/platform/payment/log", produces = "application/json")
public class PayLogBootController extends BaseController {
	
	@Autowired
	private PayLogService payLogService;
	
	@RequestMapping(value = "mng", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<PageResp<PayLogQueryRep>> mng(HttpServletRequest request,
			@And({@Spec(params = "interfaceName", path = "interfaceName", spec = Equal.class),
				@Spec(params = "orderCode", path = "orderCode", spec = Equal.class)}) Specification<PayLogEntity> spec,
			@RequestParam int page, 
			@RequestParam int rows,
			@RequestParam(required = false) String closeStatus, 
			@RequestParam(required = false) String offsetStatus,
			@RequestParam(required = false, defaultValue = "createTime") String sort,
			@RequestParam(required = false, defaultValue = "desc") String order) {
		Direction sortDirection = Direction.DESC;
		if (!"desc".equals(order)) {
			sortDirection = Direction.ASC;
		}
		
		Pageable pageable = new PageRequest(page - 1, rows, new Sort(new Order(sortDirection, sort)));
		PageResp<PayLogQueryRep> rep = this.payLogService.mng(spec, pageable);
		return new ResponseEntity<PageResp<PayLogQueryRep>>(rep, HttpStatus.OK);
	}
	
	
	
}
