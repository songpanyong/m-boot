package com.guohuai.mmp.ope.nocard;

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
@RequestMapping(value = "/mimosa/boot/ope/nocard", produces = "application/json;charset=UTF-8")
public class NoCardBootController extends BaseController {

	@Autowired
	private NoCardService noCardService;
	
	@RequestMapping(name = "运营查询-未绑卡列表", value = "list", method = { RequestMethod.POST, RequestMethod.GET })
	public @ResponseBody ResponseEntity<NoCardListResp> list(HttpServletRequest request,
			@And({ @Spec(params = "name", path = "name", spec = Like.class),
					@Spec(params = "phone", path = "phone", spec = Like.class),
					@Spec(params = "isFeedback", path = "isFeedback", spec = Equal.class),
					@Spec(params = "isBind", path = "isBind", spec = Equal.class),
					@Spec(params = "registerTimeBegin", path = "registerTime", spec = DateAfterInclusive.class, config = "yyyy-MM-dd HH:mm:ss"),
					@Spec(params = "registerTimeEnd", path = "registerTime", spec = DateBeforeInclusive.class, config = "yyyy-MM-dd HH:mm:ss"),
					@Spec(params = "bindSuccessTimeBegin", path = "bindSuccessTime", spec = DateAfterInclusive.class, config = "yyyy-MM-dd HH:mm:ss"),
					@Spec(params = "bindSuccessTimeEnd", path = "bindSuccessTime", spec = DateBeforeInclusive.class, config = "yyyy-MM-dd HH:mm:ss")}) Specification<NoCard> spec,
			@RequestParam(required = false, defaultValue = "1") int page,
			@RequestParam(required = false, defaultValue = "10") int rows,
			@RequestParam(required = false, defaultValue = "no") String isBind) {
		
		Pageable pageable = new PageRequest(page - 1, rows, new Sort(new Order(Direction.DESC, "isFeedback"),new Order(Direction.DESC, "registerTime")));
		
		if (isBind!=null&&isBind.equals(NoCard.NOCARD_COMMON_IS)){
			pageable = new PageRequest(page - 1, rows, new Sort(new Order(Direction.DESC, "bindSuccessTime")));
		}
		
		Page<NoCard> enchs = noCardService.queryPage(spec, pageable);
		NoCardListResp pageResp = new NoCardListResp(enchs);
		return new ResponseEntity<NoCardListResp>(pageResp, HttpStatus.OK);
	}
	
	@RequestMapping(name = "运营查询-未绑卡详情", value = "detail", method = { RequestMethod.POST, RequestMethod.GET })
	public @ResponseBody ResponseEntity<NoCardResp> detail(@RequestParam(required = true) String oid) {
		NoCard entity = noCardService.findByOid(oid);
		NoCardResp resp = new NoCardResp(entity);
		return new ResponseEntity<NoCardResp>(resp, HttpStatus.OK);
	}
	
	@RequestMapping(name = "运营查询-未绑卡反馈", value = "feedback", method = { RequestMethod.POST, RequestMethod.GET })
	public @ResponseBody ResponseEntity<BaseResp> feedback(@RequestParam(required = true) String aoid, @RequestParam(required = true) String lastFeedback) {
		noCardService.feedback(aoid, this.getLoginUser(), lastFeedback);
		return new ResponseEntity<BaseResp>(new BaseResp(), HttpStatus.OK);
	}
}
