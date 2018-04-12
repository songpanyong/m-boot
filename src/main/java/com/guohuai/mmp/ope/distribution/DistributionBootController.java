package com.guohuai.mmp.ope.distribution;

import javax.servlet.http.HttpServletRequest;

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
@RequestMapping(value = "/mimosa/boot/ope/distribution", produces = "application/json;charset=UTF-8")
public class DistributionBootController extends BaseController {

	@Autowired
	private DistributionService distributionService;
	
	@RequestMapping(name = "运营查询-用户分布列表", value = "list", method = { RequestMethod.POST, RequestMethod.GET })
	public @ResponseBody ResponseEntity<DistributionListResp> list(HttpServletRequest request,
			@RequestParam(required = false) String startTime,
			@RequestParam(required = false) String endTime,
			@RequestParam(required = false, defaultValue = "1") int page,
			@RequestParam(required = false, defaultValue = "10") int rows) {
		
		DistributionListResp resp = distributionService.groupList(startTime, endTime, page, rows);
		
		return new ResponseEntity<DistributionListResp>(resp, HttpStatus.OK);
	}
	
	@RequestMapping(name = "运营查询-用户来源统计列表", value = "sourcelist", method = { RequestMethod.POST, RequestMethod.GET })
	public @ResponseBody ResponseEntity<DistributionListResp> sourcelist(HttpServletRequest request,
			@RequestParam(required = false) String startTime,
			@RequestParam(required = false) String endTime,
			@RequestParam(required = true) String source,
			@RequestParam(required = false, defaultValue = "1") int page,
			@RequestParam(required = false, defaultValue = "10") int rows) {
		
		DistributionListResp resp = distributionService.sourcelist(startTime, endTime,source,page,rows);
		
		return new ResponseEntity<DistributionListResp>(resp, HttpStatus.OK);
	}
}
