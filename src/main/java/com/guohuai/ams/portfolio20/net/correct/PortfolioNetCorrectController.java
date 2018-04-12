package com.guohuai.ams.portfolio20.net.correct;

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

import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;

/**
 * @author created by Arthur
 * @date 2017年2月20日 - 下午5:13:10
 */
@RestController
@RequestMapping(value = "/mimosa/portfolio/netCorrect", produces = "application/json;charset=utf-8")
public class PortfolioNetCorrectController {

	@Autowired
	private PortfolioNetCorrectService portfolioNetCorrectService;

	@RequestMapping(value = "/prepare", name = "投资组合 - 净值校准 - 状态查询", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<PortfolioNetCorrectPrepareResp> prepare(@RequestParam String portfolioOid) {
		PortfolioNetCorrectPrepareResp p = this.portfolioNetCorrectService.prepare(portfolioOid);
		return new ResponseEntity<PortfolioNetCorrectPrepareResp>(p, HttpStatus.OK);
	}
	/**历史资产净值列表
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getListByHistory", name = "历史资产净值 - 列表", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<PortfolioNetCorrectPrepareListResp> getListByHistory(
			@And({ @Spec(params = "portfolioOid", path = "portfolio.oid", spec = Equal.class), 
                   @Spec(params = "state", path = "state", spec = Equal.class)})Specification<PortfolioNetCorrectEntity> spec,
			@RequestParam(required = false, defaultValue = "1") int page,
			@RequestParam(required = false, defaultValue = "10") int rows,
			@RequestParam(required = false, defaultValue = "netDate") String sortField,
			@RequestParam(required = false, defaultValue = "desc") String sort) {
		Direction sortDirection = Direction.DESC;
		if (!"desc".equals(sort)) {
			sortDirection = Direction.ASC;
		}
		
		Pageable pageable = new PageRequest(page - 1, rows, new Sort(new Order(sortDirection, sortField)));
		Page<PortfolioNetCorrectEntity> entity = portfolioNetCorrectService.getListByHistory(spec, pageable);
		PortfolioNetCorrectPrepareListResp reps = new PortfolioNetCorrectPrepareListResp(entity);
		return new ResponseEntity<PortfolioNetCorrectPrepareListResp>(reps, HttpStatus.OK);
	}

}
