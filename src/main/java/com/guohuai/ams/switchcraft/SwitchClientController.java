package com.guohuai.ams.switchcraft;

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
@RequestMapping(value = "/mimosa/client/switch", produces = "application/json")
public class SwitchClientController extends BaseController{

	@Autowired
	private SwitchService switchService;
	
	/**
	 * 客户端调用code开关是否开启，过滤白名单
	 * @param oid
	 * @return
	 */
	@RequestMapping(value = "/find", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public ResponseEntity<SwitchQueryCTRep> find(@RequestParam String code, @RequestParam(required=false) String phone) {
		SwitchQueryCTRep rep = null;
		if (phone == null || phone.isEmpty()){
			rep = this.switchService.findCode(code, null, this.getLoginUser());
		}else{
			rep = this.switchService.findCode(code, phone, null);
		}
		return new ResponseEntity<SwitchQueryCTRep>(rep, HttpStatus.OK);
	}
}
