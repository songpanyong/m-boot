package com.guohuai.ams.portfolio20.estimate;

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

import com.alibaba.fastjson.JSONObject;
import com.guohuai.basic.component.ext.web.Response;

import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;

/**
 * @author created by Arthur
 * @date 2017年2月27日 - 上午11:25:52
 */
@RestController
@RequestMapping(value = "/mimosa/portfolio/estimate", produces = "application/json;charset=utf-8")
public class PortfolioEstimateController {

	@Autowired
	private PortfolioEstimateService portfolioEstimateService;

	@RequestMapping(name = "投资组合 - 估值计算", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ResponseEntity<Response> estimate() {
		this.portfolioEstimateService.batchEstimate();
		Response r = new Response();
		r.with("result", "SUCCESSED!");
		return new ResponseEntity<Response>(r, HttpStatus.OK);
	}
	
	/**
	 * 计提费用明细列表
	 * 
	 * @param portfolioOid
	 * @return
	 */
	@RequestMapping(value = "/chargefeeList", name = "投资组合 - 计提费用明细列表", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<PortfolioEstimateListResp> list(HttpServletRequest request, 
			@And({ @Spec(params = "portfolioOid", path = "portfolio.oid", spec = Equal.class)}) Specification<PortfolioEstimateEntity> spec, 
			@RequestParam(required = false, defaultValue = "1") int page,
			@RequestParam(required = false, defaultValue = "10") int rows,
			@RequestParam(required = false, defaultValue = "estimateDate") String sortField, @RequestParam(required = false, defaultValue = "desc") String sort) {

		Direction sortDirection = Direction.DESC;
		if (!"desc".equals(sort)) {
			sortDirection = Direction.ASC;
		}
		Pageable pageable = new PageRequest(page - 1, rows, new Sort(new Order(sortDirection, sortField)));
		Page<PortfolioEstimateEntity> entitys = this.portfolioEstimateService.getChargefeeList(spec, pageable);
		PortfolioEstimateListResp resps = new PortfolioEstimateListResp(entitys);
		return new ResponseEntity<PortfolioEstimateListResp>(resps, HttpStatus.OK);
	}
	
	/**
	 * 最新估值日和累计计提费用
	 * @param oid
	 * @return
	 */
	@RequestMapping(name = "最新估值日和累计计提费用", value = "/theNew", method = { RequestMethod.POST, RequestMethod.GET })
	public @ResponseBody JSONObject theNew(@RequestParam String portfolioOid) {
		return this.portfolioEstimateService.theNew(portfolioOid);
	}

}
