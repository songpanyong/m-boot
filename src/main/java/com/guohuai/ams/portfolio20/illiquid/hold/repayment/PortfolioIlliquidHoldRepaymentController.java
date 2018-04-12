package com.guohuai.ams.portfolio20.illiquid.hold.repayment;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.guohuai.basic.component.ext.web.BaseController;

@RestController
@RequestMapping(value = "/mimosa/illiquid/hold/repayment", produces = "application/json;charset=utf-8")
public class PortfolioIlliquidHoldRepaymentController extends BaseController {

	@Autowired
	private PortfolioIlliquidHoldRepaymentService portfolioIlliquidHoldRepaymentService;

	@RequestMapping(value = "/list", name = "投资组合 - 非现金类标的 - 还款计划列表", method = { RequestMethod.POST, RequestMethod.GET })
	public @ResponseBody ResponseEntity<PortfolioIlliquidHoldRepaymentListResp> list(@RequestParam String holdOid) {

		List<PortfolioIlliquidHoldRepaymentEntity> list = this.portfolioIlliquidHoldRepaymentService.findByHold(holdOid);
		PortfolioIlliquidHoldRepaymentListResp resp = new PortfolioIlliquidHoldRepaymentListResp(list);
		return new ResponseEntity<PortfolioIlliquidHoldRepaymentListResp>(resp, HttpStatus.OK);
	}

}
