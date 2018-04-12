package com.guohuai.ams.portfolio20.illiquid.hold.repayment;

import java.sql.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.guohuai.basic.common.DateUtil;
import com.guohuai.basic.common.StringUtil;
import com.guohuai.basic.component.ext.web.BaseController;
import com.guohuai.basic.component.ext.web.Response;

@RestController
@RequestMapping(value = "/mimosa/illiquid/hold/repayment/schedule", produces = "application/json;charset=utf-8")
public class PortfolioIlliquidHoldRepaymentScheduleController extends BaseController {

	@Autowired
	private PortfolioIlliquidHoldRepaymentScheduleService portfolioIlliquidHoldRepaymentScheduleService;

	@RequestMapping(value = "/updateState", name = "非现金类标的 - 更新还款计划状态", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ResponseEntity<Response> updateState(@RequestParam(defaultValue = "", required = false) String baseDate) {
		Date base = null;
		if (StringUtil.isEmpty(baseDate)) {
			base = new Date(System.currentTimeMillis());
		} else {
			base = new Date(DateUtil.parseDate(baseDate, "yyyy-MM-dd").getTime());
		}

		this.portfolioIlliquidHoldRepaymentScheduleService.updateState(base);

		Response r = new Response();
		r.with("result", "SUCCESSED!");
		return new ResponseEntity<Response>(r, HttpStatus.OK);
	}

}
