package com.guohuai.mmp.sms;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.guohuai.basic.component.ext.web.BaseController;
import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.component.util.CheckUtil;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(value = "/mimosa/boot/sms", produces = "application/json")
@Slf4j
public class SMSBootController extends BaseController {

	@Autowired
	private SMSUtils sMSUtils;
	
	/**
	 * 获取短信验证码
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "getvc", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<SMSVeriCodeRep> getVc(@Valid @RequestBody SMSReq req) {
		CheckUtil.isMobileNO(req.getPhone(), false, 0, 80027);
		SMSVeriCodeRep rep = this.sMSUtils.getVeriCode(req);
		log.info("后台获取手机号：{}的验证码。", req.getPhone());
		return new ResponseEntity<SMSVeriCodeRep>(rep, HttpStatus.OK);
	}
	
	/**
	 * 后台调用发短信
	 * @param request
	 * @param phone
	 * @param mesTempCode
	 * @param mesParam
	 * @return
	 */
	@RequestMapping(value = "sendSms", method = RequestMethod.POST)
	public ResponseEntity<BaseResp> sendVc(HttpServletRequest request,
			@RequestParam(required = true) String phone, 
			@RequestParam(required = true) String mesTempCode, 
			@RequestParam(required = false) String mesParam) {
		
		BaseResp resp = this.sMSUtils.sendSMS(phone, mesTempCode, JSON.parseObject(mesParam, String[].class));

		return new ResponseEntity<BaseResp>(resp, HttpStatus.OK);
	}
}
