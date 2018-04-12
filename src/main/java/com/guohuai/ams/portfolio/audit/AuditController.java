package com.guohuai.ams.portfolio.audit;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

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

import com.guohuai.ams.portfolio.chargeFee.ChargeFeeService;
import com.guohuai.ams.portfolio.entity.NetValueEntity;
import com.guohuai.ams.portfolio.entity.PortfolioEntity;
import com.guohuai.ams.portfolio.form.PortfolioListResp;
import com.guohuai.ams.portfolio.service.AdjustService;
import com.guohuai.ams.portfolio.service.IncomeService;
import com.guohuai.ams.portfolio.service.NetValueService;
import com.guohuai.ams.portfolio.service.PortfolioService;
import com.guohuai.basic.component.ext.web.BaseController;
import com.guohuai.basic.component.ext.web.PageResp;
import com.guohuai.component.web.view.Response;

import net.kaczmarzyk.spring.data.jpa.domain.DateAfterInclusive;
import net.kaczmarzyk.spring.data.jpa.domain.DateBeforeInclusive;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.Like;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;

/**
 * 审核接口
 * 
 * @author star.zhu 2016年12月26日
 */
@RestController
@RequestMapping(value = "/mimosa/portfolio/audit", produces = "application/json;charset=utf-8")
public class AuditController extends BaseController {

	@Autowired
	private PortfolioService portfolioService;
	@Autowired
	private NetValueService netValueService;
	@Autowired
	private IncomeService incomeService;
	@Autowired
	private ChargeFeeService chargeFeeService;
	@Autowired
	private AdjustService adjustService;
	@Autowired
	private AuditService auditService;

	/**
	 * 新建投资组合审核
	 * 
	 * @return
	 */
	@RequestMapping(value = "/auditToPorfolio", name = "投资组合 - 新建投资组合审核", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<Response> auditToPorfolio(@RequestParam String oid, @RequestParam String operate, @RequestParam String auditMark) {
		String operator = super.getLoginUser();
		portfolioService.auditPortfolio(oid, operator, operate, auditMark);
		Response r = new Response();
		r.with("result", "SUCCESS!");
		return new ResponseEntity<Response>(r, HttpStatus.OK);
	}

	/**
	 * 获取所有的投资组合待审核列表，支持模糊查询
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getAuditListByParams", name = "投资组合 - 获取所有投资组合审核列表", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<PortfolioListResp> getAuditListByParams(@And({ @Spec(params = "name", path = "name", spec = Like.class), @Spec(params = "createTimeStart", path = "createTime", spec = DateAfterInclusive.class),@Spec(params = "createTimEnd", path = "createTime", spec =DateBeforeInclusive.class)}) Specification<PortfolioEntity> spec, @RequestParam(required = false, defaultValue = "1") int page, @RequestParam(required = false, defaultValue = "10") int rows, @RequestParam(required = false, defaultValue = "createTime") String sortField,
			@RequestParam(required = false, defaultValue = "desc") String sort) {
		Direction sortDirection = Direction.DESC;
		if (!"desc".equals(sort)) {
			sortDirection = Direction.ASC;
		}
		Specification<PortfolioEntity> stateSpec = new Specification<PortfolioEntity>() {
			@Override
			public Predicate toPredicate(Root<PortfolioEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.equal(root.get("state"), PortfolioEntity.PORTFOLIO_STATE_pretrial);
			}
		};
		spec = Specifications.where(spec).and(stateSpec);
		Pageable pageable = new PageRequest(page - 1, rows, new Sort(new Order(sortDirection, sortField)));
		PortfolioListResp resps = portfolioService.getAuditListByParams(spec, pageable);
		return new ResponseEntity<PortfolioListResp>(resps, HttpStatus.OK);
	}

	/**
	 * 获取所有的投资组合净值校准审核列表
	 * 
	 * @param page
	 * @param rows
	 * @param sortField
	 * @param sort
	 * @return
	 */
	@RequestMapping(value = "/getNetValueAuditListByParams", name = "投资组合 - 获取所有投资组合净值校准审核列表", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<PageResp<NetValueEntity>> getNetValueListByParams(@And({ @Spec(params = "pname", path = "portfolio.name", spec = Like.class) }) Specification<NetValueEntity> spec, @RequestParam(required = false, defaultValue = "1") int page, @RequestParam(required = false, defaultValue = "10") int rows, @RequestParam(required = false, defaultValue = "createTime") String sortField, @RequestParam(required = false, defaultValue = "desc") String sort) {
		Direction sortDirection = Direction.DESC;
		if (!"desc".equals(sort)) {
			sortDirection = Direction.ASC;
		}
		Specification<NetValueEntity> stateSpec = new Specification<NetValueEntity>() {
			@Override
			public Predicate toPredicate(Root<NetValueEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.equal(root.get("state"), NetValueEntity.PORTFOLIO_NETVALUE_STATE_duration);
			}
		};
		spec = Specifications.where(spec).and(stateSpec);
		Pageable pageable = new PageRequest(page - 1, rows, new Sort(new Order(sortDirection, sortField)));
		PageResp<NetValueEntity> rep = netValueService.getListByParams(spec, pageable);
		return new ResponseEntity<PageResp<NetValueEntity>>(rep, HttpStatus.OK);
	}

	/**
	 * 净值校准审核
	 * 
	 * @return
	 */
	@RequestMapping(value = "/auditToNetValue", name = "投资组合 - 净值校准审核", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<Response> auditToNetValue(@RequestParam String oid, @RequestParam String operate, @RequestParam String auditMark) {
		String operator = super.getLoginUser();
		netValueService.auditNetValue(oid, operator, operate, auditMark);
		Response r = new Response();
		r.with("result", "SUCCESS!");
		return new ResponseEntity<Response>(r, HttpStatus.OK);
	}

	/**
	 * 资产交易审核
	 * 
	 * @return
	 */
	@RequestMapping(value = "/auditToOrder", name = "投资组合 - 资产交易审核", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<Response> auditToTrade(@RequestParam String oid, @RequestParam String operate, @RequestParam String auditMark) {
		// String operator = super.getLoginUser();
		// marketOrderService.auditToOrder(oid, operator, operate, auditMark);
		Response r = new Response();
		r.with("result", "SUCCESS!");
		return new ResponseEntity<Response>(r, HttpStatus.OK);
	}

	/**
	 * 收益分配审核
	 * 
	 * @return
	 */
	@RequestMapping(value = "/auditToIncome", name = "投资组合 - 收益分配审核", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<Response> auditToIncome(@RequestParam String oid, @RequestParam String operate, @RequestParam String auditMark) {
		String operator = super.getLoginUser();
		incomeService.auditIncome(oid, operator, operate, auditMark);
		Response r = new Response();
		r.with("result", "SUCCESS!");
		return new ResponseEntity<Response>(r, HttpStatus.OK);
	}

	/**
	 * 费金提取审核
	 * 
	 * @return
	 */
	@RequestMapping(value = "/auditToChargeFee", name = "投资组合 - 费金提取审核", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<Response> auditToChargeFee(@RequestParam String oid, @RequestParam String operate, @RequestParam String auditMark) {
		String operator = super.getLoginUser();
		chargeFeeService.auditDraw(oid, operator, operate, auditMark);
		Response r = new Response();
		r.with("result", "SUCCESS!");
		return new ResponseEntity<Response>(r, HttpStatus.OK);
	}

	/**
	 * 现金校准审核
	 * 
	 * @return
	 */
	@RequestMapping(value = "/auditToCash", name = "投资组合 - 现金校准审核", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<Response> auditToCash(@RequestParam String oid, @RequestParam String operate, @RequestParam String auditMark) {
		String operator = super.getLoginUser();
		adjustService.auditAdjust(oid, operator, operate, auditMark);
		Response r = new Response();
		r.with("result", "SUCCESS!");
		return new ResponseEntity<Response>(r, HttpStatus.OK);
	}

	/**
	 * 损益校准审核
	 * 
	 * @return
	 */
	@RequestMapping(value = "/auditToDeviation", name = "投资组合 - 损益校准审核", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<Response> auditToDeviation(@RequestParam String oid, @RequestParam String operate, @RequestParam String auditMark) {
		String operator = super.getLoginUser();
		adjustService.auditAdjust(oid, operator, operate, auditMark);
		Response r = new Response();
		r.with("result", "SUCCESS!");
		return new ResponseEntity<Response>(r, HttpStatus.OK);
	}

	/**
	 * 审核记录
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getListByParams", name = "投资组合 - 审核记录", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<PageResp<AuditEntity>> getListByParams(@And({ @Spec(params = "portfolioName", path = "portfolio.name", spec = Like.class), @Spec(params = "auditType", path = "auditType", spec = Equal.class) }) Specification<AuditEntity> spec, @RequestParam(required = false, defaultValue = "1") int page, @RequestParam(required = false, defaultValue = "10") int rows, @RequestParam(required = false, defaultValue = "createTime") String sortField,
			@RequestParam(required = false, defaultValue = "desc") String sort) {
		Direction sortDirection = Direction.DESC;
		if (!"desc".equals(sort)) {
			sortDirection = Direction.ASC;
		}
		Pageable pageable = new PageRequest(page - 1, rows, new Sort(new Order(sortDirection, sortField)));
		PageResp<AuditEntity> rep = auditService.getListByParams(spec, pageable);
		return new ResponseEntity<PageResp<AuditEntity>>(rep, HttpStatus.OK);
	}

	/**
	 * 根据oid，获取审核记录详情
	 * 
	 * @param oid
	 * @return
	 */
	@RequestMapping(value = "/getAuditRecordByOid", name = "投资组合 - 审核记录详情", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<Response> getAuditRecordByOid(@RequestParam String oid) {
		AuditEntity resp = this.auditService.findByOid(oid);
		Response r = new Response();
		r.with("result", resp);
		return new ResponseEntity<Response>(r, HttpStatus.OK);
	}

}
