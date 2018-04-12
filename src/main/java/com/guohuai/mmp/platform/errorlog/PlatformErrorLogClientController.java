package com.guohuai.mmp.platform.errorlog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.guohuai.basic.component.ext.web.BaseController;
import com.guohuai.basic.component.ext.web.BaseResp;

@RestController
@RequestMapping(value = "/mimosa/client/platform/errorlog", produces = "application/json")
public class PlatformErrorLogClientController extends BaseController {
	
	@Autowired
	private PlatformErrorLogService platformErrorLogService;
	
	@RequestMapping(value = "slog", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<BaseResp> saveErrorLog(@RequestBody ErrorLogReq req) {
		String uid = this.getLoginUser();
		BaseResp rep = this.platformErrorLogService.saveErrorLog(req, uid);
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
	
	
}
