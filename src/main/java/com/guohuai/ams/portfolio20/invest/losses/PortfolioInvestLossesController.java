package com.guohuai.ams.portfolio20.invest.losses;

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

import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;

/**
 * @author created by Arthur
 * @date 2017年2月20日 - 下午5:04:41
 */
@RestController
@RequestMapping(value = "/mimosa/portfolio/invest/Losses", produces = "application/json;charset=utf-8")
public class PortfolioInvestLossesController extends BaseController {
	@Autowired
	private PortfolioInvestLossesService portfolioInvestLossesService;

	/**投资损益列表
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getListByLosses", name = "投资损益 - 列表", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<PortfolioInvestLossesListResp> getListByLosses(
			@And({ @Spec(params = "portfolioOid", path = "portfolio.oid", spec = Equal.class)})Specification<PortfolioInvestLossesEntity> spec,
			@RequestParam(required = false, defaultValue = "1") int page,
			@RequestParam(required = false, defaultValue = "10") int rows,
			@RequestParam(required = false, defaultValue = "orderDate") String sortField,
			@RequestParam(required = false, defaultValue = "desc") String sort) {
		Direction sortDirection = Direction.DESC;
		if (!"desc".equals(sort)) {
			sortDirection = Direction.ASC;
		}
		Pageable pageable = new PageRequest(page - 1, rows, new Sort(new Order(sortDirection, sortField)));
		Page<PortfolioInvestLossesEntity> rep = portfolioInvestLossesService.getListByLosses(spec, pageable);
		PortfolioInvestLossesListResp resps = new PortfolioInvestLossesListResp(rep);
        return new ResponseEntity<PortfolioInvestLossesListResp>(resps, HttpStatus.OK);
	}
}
