package com.guohuai.mmp.ope.norecharge;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

import net.kaczmarzyk.spring.data.jpa.domain.DateAfterInclusive;
import net.kaczmarzyk.spring.data.jpa.domain.DateBeforeInclusive;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.Like;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;

@RestController
@RequestMapping(value = "/mimosa/boot/ope/norecharge", produces = "application/json;charset=UTF-8")
public class NoRechargeBootController extends BaseController {

	@Autowired
	private NoRechargeService noRechargeService;
	
	@RequestMapping(name = "运营查询-未充值列表", value = "list", method = { RequestMethod.POST, RequestMethod.GET })
	public @ResponseBody ResponseEntity<NoRechargeListResp> list(HttpServletRequest request,
			@And({ @Spec(params = "name", path = "name", spec = Like.class),
					@Spec(params = "phone", path = "phone", spec = Like.class),
					@Spec(params = "isFeedback", path = "isFeedback", spec = Equal.class),
					@Spec(params = "isCharge", path = "isCharge", spec = Equal.class),
					@Spec(params = "bindTimeBegin", path = "bindTime", spec = DateAfterInclusive.class, config = "yyyy-MM-dd HH:mm:ss"),
					@Spec(params = "bindTimeEnd", path = "bindTime", spec = DateBeforeInclusive.class, config = "yyyy-MM-dd HH:mm:ss"),
					@Spec(params = "rechargeSuccessTimeBegin", path = "rechargeSuccessTime", spec = DateAfterInclusive.class, config = "yyyy-MM-dd HH:mm:ss"),
					@Spec(params = "rechargeSuccessTimeEnd", path = "rechargeSuccessTime", spec = DateBeforeInclusive.class, config = "yyyy-MM-dd HH:mm:ss")}) Specification<NoRecharge> spec,
			@RequestParam(required = false, defaultValue = "1") int page,
			@RequestParam(required = false, defaultValue = "10") int rows,
			@RequestParam(required = false, defaultValue = "no") String isCharge) {
		
		Pageable pageable = new PageRequest(page - 1, rows, new Sort(new Order(Direction.DESC, "isFeedback"),new Order(Direction.DESC, "bindTime")));
		
		if (isCharge!=null&&isCharge.equals(NoRecharge.NORECHARGE_COMMON_IS)){
			pageable = new PageRequest(page - 1, rows, new Sort(new Order(Direction.DESC, "rechargeSuccessTime")));
		}
		
		Page<NoRecharge> enchs = noRechargeService.queryPage(spec, pageable);
		NoRechargeListResp pageResp = new NoRechargeListResp(enchs);
		return new ResponseEntity<NoRechargeListResp>(pageResp, HttpStatus.OK);
	}
	
	@RequestMapping(name = "运营查询-未充值详情", value = "detail", method = { RequestMethod.POST, RequestMethod.GET })
	public @ResponseBody ResponseEntity<NoRechargeResp> detail(@RequestParam(required = true) String oid) {
		NoRecharge entity = noRechargeService.findByOid(oid);
		NoRechargeResp resp = new NoRechargeResp(entity);
		return new ResponseEntity<NoRechargeResp>(resp, HttpStatus.OK);
	}
	
	@RequestMapping(name = "运营查询-未充值反馈", value = "feedback", method = { RequestMethod.POST, RequestMethod.GET })
	public @ResponseBody ResponseEntity<BaseResp> feedback(@RequestParam(required = true) String aoid, @RequestParam(required = true) String lastFeedback) {
		noRechargeService.feedback(aoid, this.getLoginUser(), lastFeedback);
		return new ResponseEntity<BaseResp>(new BaseResp(), HttpStatus.OK);
	}
}
