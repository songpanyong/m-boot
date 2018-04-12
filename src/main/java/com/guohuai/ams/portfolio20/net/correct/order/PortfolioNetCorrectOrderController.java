package com.guohuai.ams.portfolio20.net.correct.order;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import com.guohuai.component.web.view.Response;

import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.Like;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;

/**
 * @author created by Arthur
 * @date 2017年2月20日 - 下午5:04:41
 */
@RestController
@RequestMapping(value = "/mimosa/portfolio/netCorrect/order", produces = "application/json;charset=utf-8")
public class PortfolioNetCorrectOrderController extends BaseController {

	@Autowired
	private PortfolioNetCorrectOrderService portfolioNetCorrectOrderService;
	
	@RequestMapping(value = "/submit", name = "投资组合 - 净值校准 - 下单", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<Response> submit(PortfolioNetCorrectOrderForm form) {
		this.portfolioNetCorrectOrderService.createOrder(form, super.getLoginUser());
		Response r = new Response();
		r.with("result", "SUCCESSED!");
		return new ResponseEntity<Response>(r, HttpStatus.OK);
	}

	@RequestMapping(value = "/pass", name = "投资组合 - 净值校准 - 审核通过", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<Response> pass(@RequestParam String oid, @RequestParam String auditMark) {
		this.portfolioNetCorrectOrderService.passOrder(oid, auditMark, super.getLoginUser());
		Response r = new Response();
		r.with("result", "SUCCESSED!");
		return new ResponseEntity<Response>(r, HttpStatus.OK);
	}

	@RequestMapping(value = "/fail", name = "投资组合 - 净值校准 - 审核驳回", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<Response> fail(@RequestParam String oid, @RequestParam String auditMark) {
		this.portfolioNetCorrectOrderService.failOrder(oid, auditMark, super.getLoginUser());
		Response r = new Response();
		r.with("result", "SUCCESSED!");
		return new ResponseEntity<Response>(r, HttpStatus.OK);
	}

	@RequestMapping(value = "/delete", name = "投资组合 - 净值校准 - 删除订单", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<Response> delete(@RequestParam String oid) {
		this.portfolioNetCorrectOrderService.deleteOrder(oid, super.getLoginUser());
		Response r = new Response();
		r.with("result", "SUCCESSED!");
		return new ResponseEntity<Response>(r, HttpStatus.OK);
	}

	/**净值校准审核记录列表
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getListByAuditing", name = "净值校准 - 记录列表", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<PortfolioNetCorrectOrderListResp> getListByAuditing(
			@And({ @Spec(params = "name", path = "portfolio.name", spec = Like.class), 
                   @Spec(params = "state", path = "state", spec = Equal.class)})Specification<PortfolioNetCorrectOrderEntity> spec,
			@RequestParam(required = false, defaultValue = "1") int page,
			@RequestParam(required = false, defaultValue = "10") int rows,
			@RequestParam(required = false, defaultValue = "createTime") String sortField,
			@RequestParam(required = false, defaultValue = "desc") String sort) {
		Direction sortDirection = Direction.DESC;
		if (!"desc".equals(sort)) {
			sortDirection = Direction.ASC;
		}
		Specification<PortfolioNetCorrectOrderEntity> stateSpec = new Specification<PortfolioNetCorrectOrderEntity>() {
			@Override
			public Predicate toPredicate(Root<PortfolioNetCorrectOrderEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.notEqual(root.get("orderState"), PortfolioNetCorrectOrderEntity.ORDER_STATE_DELETE);
			}
		};
		spec = Specifications.where(spec).and(stateSpec);
		Pageable pageable = new PageRequest(page - 1, rows, new Sort(new Order(sortDirection, sortField)));
		Page<PortfolioNetCorrectOrderEntity> rep = portfolioNetCorrectOrderService.getListByRecording(spec, pageable);
		PortfolioNetCorrectOrderListResp resps = new PortfolioNetCorrectOrderListResp(rep);
        return new ResponseEntity<PortfolioNetCorrectOrderListResp>(resps, HttpStatus.OK);
	}
	/**净值校准审核记录列表
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getListByRecording", name = "净值校准 - 记录列表", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<PortfolioNetCorrectOrderListResp> getListByRecording(
			@And({ @Spec(params = "portfolioOid", path = "portfolio.oid", spec = Equal.class), 
                   @Spec(params = "state", path = "state", spec = Equal.class)})Specification<PortfolioNetCorrectOrderEntity> spec,
			@RequestParam(required = false, defaultValue = "1") int page,
			@RequestParam(required = false, defaultValue = "10") int rows,
			@RequestParam(required = false, defaultValue = "createTime") String sortField,
			@RequestParam(required = false, defaultValue = "desc") String sort) {
		Direction sortDirection = Direction.DESC;
		if (!"desc".equals(sort)) {
			sortDirection = Direction.ASC;
		}
		Pageable pageable = new PageRequest(page - 1, rows, new Sort(new Order(sortDirection, sortField)));
		Page<PortfolioNetCorrectOrderEntity> rep = portfolioNetCorrectOrderService.getListByRecording(spec, pageable);
		PortfolioNetCorrectOrderListResp resps = new PortfolioNetCorrectOrderListResp(rep);
        return new ResponseEntity<PortfolioNetCorrectOrderListResp>(resps, HttpStatus.OK);
	}
}
