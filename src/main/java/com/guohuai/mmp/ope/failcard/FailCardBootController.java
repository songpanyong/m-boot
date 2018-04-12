package com.guohuai.mmp.ope.failcard;

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
@RequestMapping(value = "/mimosa/boot/ope/failcard", produces = "application/json;charset=UTF-8")
public class FailCardBootController extends BaseController {

	@Autowired
	private FailCardService failCardService;
	
	@RequestMapping(name = "运营查询-绑卡失败列表", value = "list", method = { RequestMethod.POST, RequestMethod.GET })
	public @ResponseBody ResponseEntity<FailCardListResp> list(HttpServletRequest request,
			@And({ @Spec(params = "name", path = "name", spec = Like.class),
					@Spec(params = "phone", path = "phone", spec = Like.class),
					@Spec(params = "isFeedback", path = "isFeedback", spec = Equal.class),
					@Spec(params = "isBind", path = "isBind", spec = Equal.class),
					@Spec(params = "bindTimeBegin", path = "bindTime", spec = DateAfterInclusive.class, config = "yyyy-MM-dd HH:mm:ss"),
					@Spec(params = "bindTimeEnd", path = "bindTime", spec = DateBeforeInclusive.class, config = "yyyy-MM-dd HH:mm:ss"),
					@Spec(params = "bindSuccessTimeBegin", path = "bindSuccessTime", spec = DateAfterInclusive.class, config = "yyyy-MM-dd HH:mm:ss"),
					@Spec(params = "bindSuccessTimeEnd", path = "bindSuccessTime", spec = DateBeforeInclusive.class, config = "yyyy-MM-dd HH:mm:ss")}) Specification<FailCard> spec,
			@RequestParam(required = false, defaultValue = "1") int page,
			@RequestParam(required = false, defaultValue = "10") int rows,
			@RequestParam(required = false, defaultValue = "no") String isBind) {
		
		Pageable pageable = new PageRequest(page - 1, rows, new Sort(new Order(Direction.DESC, "isFeedback"),new Order(Direction.DESC, "bindTime")));
		
		if (isBind!=null&&isBind.equals(FailCard.FAILCARD_COMMON_IS)){
			pageable = new PageRequest(page - 1, rows, new Sort(new Order(Direction.DESC, "bindSuccessTime")));
		}
		
		Page<FailCard> enchs = failCardService.queryPage(spec, pageable);
		FailCardListResp pageResp = new FailCardListResp(enchs);
		return new ResponseEntity<FailCardListResp>(pageResp, HttpStatus.OK);
	}
	
	@RequestMapping(name = "运营查询-绑卡失败详情", value = "detail", method = { RequestMethod.POST, RequestMethod.GET })
	public @ResponseBody ResponseEntity<FailCardResp> detail(@RequestParam(required = true) String oid) {
		FailCard entity = failCardService.findByOid(oid);
		FailCardResp resp = new FailCardResp(entity);
		return new ResponseEntity<FailCardResp>(resp, HttpStatus.OK);
	}
	
	@RequestMapping(name = "运营查询-绑卡失败反馈", value = "feedback", method = { RequestMethod.POST, RequestMethod.GET })
	public @ResponseBody ResponseEntity<BaseResp> feedback(@RequestParam(required = true) String aoid, @RequestParam(required = true) String lastFeedback) {
		failCardService.feedback(aoid, this.getLoginUser(), lastFeedback);
		return new ResponseEntity<BaseResp>(new BaseResp(), HttpStatus.OK);
	}
}
