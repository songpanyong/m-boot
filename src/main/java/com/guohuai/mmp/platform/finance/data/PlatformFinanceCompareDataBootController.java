package com.guohuai.mmp.platform.finance.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.guohuai.basic.component.ext.web.BaseController;
import com.guohuai.basic.component.ext.web.BaseResp;

@RestController
@RequestMapping(value = "/mimosa/platform/finance/data", produces = "application/json")
public class PlatformFinanceCompareDataBootController extends BaseController {

	@Autowired
	private PlatformFinanceCompareDataService financeCompareDataService;

	/**
	 * 同步数据
	 */
	@RequestMapping(value = "rdata")
	@ResponseBody
	public ResponseEntity<BaseResp> synRemoteData(@RequestParam String checkOid) {
		BaseResp rep = financeCompareDataService.synRemoteData(checkOid);
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
}
