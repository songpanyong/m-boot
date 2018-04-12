package com.guohuai.mmp.platform.baseaccount.statistics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.guohuai.basic.component.ext.web.BaseController;
import com.guohuai.basic.component.ext.web.BaseResp;

@RestController
@RequestMapping(value = "/mimosa/boot/platform/baseaccount/statistics", produces = "application/json")
public class PlatformStatisticsController extends BaseController {

	@Autowired
	PlatformStatisticsService patformStatisticsService;

	@RequestMapping(value = "home", method = { RequestMethod.POST })
	@ResponseBody
	public ResponseEntity<BaseResp> home() {
		this.getLoginUser();
		BaseResp rep = this.patformStatisticsService.qryHome();
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
	

}
