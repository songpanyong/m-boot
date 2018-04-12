package com.guohuai.mmp.platform.baseaccount;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.guohuai.basic.component.ext.web.BaseController;
import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.mmp.platform.baseaccount.statistics.PlatformStatisticsScheduleService;

@RestController
@RequestMapping(value = "/mimosa/boot/platform/baseaccount", produces = "application/json")
public class PlatformBaseAccountBootController extends BaseController {
	
	@Autowired
	private PlatformBaseAccountService platformBaseAccountService;
	@Autowired
	private PlatformStatisticsScheduleService platformStatisticsScheduleService;
	
	@RequestMapping(value = "deta", method = {RequestMethod.POST, RequestMethod.GET})
	@ResponseBody
	public ResponseEntity<PlatformBaseAccountRep> deta() {
		this.getLoginUser();
		PlatformBaseAccountRep rep = this.platformBaseAccountService.deta();
		return new ResponseEntity<PlatformBaseAccountRep>(rep, HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "borrow", method = {RequestMethod.POST, RequestMethod.GET})
	@ResponseBody
	public ResponseEntity<BaseResp> borrow(@RequestParam(required = true) BigDecimal amount) {
		this.getLoginUser();
		
		BaseResp rep = this.platformBaseAccountService.borrowMoney(amount);
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
	@RequestMapping(value = "pay", method = {RequestMethod.POST, RequestMethod.GET})
	@ResponseBody
	public ResponseEntity<BaseResp> pay(@RequestParam(required = true) BigDecimal amount) {
		this.getLoginUser();
		
		BaseResp rep = this.platformBaseAccountService.payMoney(amount);
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
	@RequestMapping(value = "refresh", method = {RequestMethod.POST, RequestMethod.GET})
	@ResponseBody
	public ResponseEntity<BaseResp> refresh() {
		BaseResp rep = this.platformStatisticsScheduleService.handStatisticsAllSchedule();
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
}
