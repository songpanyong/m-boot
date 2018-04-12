package com.guohuai.mmp.ope.nobuy;

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
@RequestMapping(value = "/mimosa/boot/ope/nobuy", produces = "application/json;charset=UTF-8")
public class NoBuyBootController extends BaseController {

	@Autowired
	private NoBuyService noBuyService;
	
	@RequestMapping(name = "运营查询-未购买列表", value = "list", method = { RequestMethod.POST, RequestMethod.GET })
	public @ResponseBody ResponseEntity<NoBuyListResp> list(HttpServletRequest request,
			@And({ @Spec(params = "name", path = "name", spec = Like.class),
					@Spec(params = "phone", path = "phone", spec = Like.class),
					@Spec(params = "isFeedback", path = "isFeedback", spec = Equal.class),
					@Spec(params = "isBuy", path = "isBuy", spec = Equal.class),
					@Spec(params = "rechargeTimeBegin", path = "rechargeTime", spec = DateAfterInclusive.class, config = "yyyy-MM-dd HH:mm:ss"),
					@Spec(params = "rechargeTimeEnd", path = "rechargeTime", spec = DateBeforeInclusive.class, config = "yyyy-MM-dd HH:mm:ss"),
					@Spec(params = "buyTimeBegin", path = "buyTime", spec = DateAfterInclusive.class, config = "yyyy-MM-dd HH:mm:ss"),
					@Spec(params = "buyTimeEnd", path = "buyTime", spec = DateBeforeInclusive.class, config = "yyyy-MM-dd HH:mm:ss")}) Specification<NoBuy> spec,
			@RequestParam(required = false, defaultValue = "1") int page,
			@RequestParam(required = false, defaultValue = "10") int rows,
			@RequestParam(required = false, defaultValue = "no") String isBuy) {
		
		Pageable pageable = new PageRequest(page - 1, rows, new Sort(new Order(Direction.DESC, "isFeedback"),new Order(Direction.DESC, "rechargeTime")));
		
		if (isBuy!=null&&isBuy.equals(NoBuy.NOBUY_COMMON_IS)){
			pageable = new PageRequest(page - 1, rows, new Sort(new Order(Direction.DESC, "buyTime")));
		}
		
		Page<NoBuy> enchs = noBuyService.queryPage(spec, pageable);
		NoBuyListResp pageResp = new NoBuyListResp(enchs);
		return new ResponseEntity<NoBuyListResp>(pageResp, HttpStatus.OK);
	}
	
	@RequestMapping(name = "运营查询-未购买详情", value = "detail", method = { RequestMethod.POST, RequestMethod.GET })
	public @ResponseBody ResponseEntity<NoBuyResp> detail(@RequestParam(required = true) String oid) {
		NoBuy entity = noBuyService.findByOid(oid);
		NoBuyResp resp = new NoBuyResp(entity);
		return new ResponseEntity<NoBuyResp>(resp, HttpStatus.OK);
	}
	
	@RequestMapping(name = "运营查询-未购买反馈", value = "feedback", method = { RequestMethod.POST, RequestMethod.GET })
	public @ResponseBody ResponseEntity<BaseResp> feedback(@RequestParam(required = true) String aoid, @RequestParam(required = true) String lastFeedback) {
		noBuyService.feedback(aoid, this.getLoginUser(), lastFeedback);
		return new ResponseEntity<BaseResp>(new BaseResp(), HttpStatus.OK);
	}
}
