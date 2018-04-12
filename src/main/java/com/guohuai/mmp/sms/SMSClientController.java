package com.guohuai.mmp.sms;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.guohuai.ams.switchcraft.white.SwitchWhiteEntity;
import com.guohuai.ams.switchcraft.white.SwitchWhiteService;
import com.guohuai.basic.component.ext.web.BaseController;
import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.CheckUtil;
import com.guohuai.component.util.StringUtil;
import com.guohuai.mmp.captcha.CaptchaService;

@RestController
@RequestMapping(value = "/mimosa/client/sms", produces = "application/json")
public class SMSClientController extends BaseController {

	@Autowired
	private SMSUtils sMSUtils;
	@Autowired
	private CaptchaService captchaService;
	
	@Autowired
	private SwitchWhiteService switchWhiteService;
	
	/**
	 * 原国槐代码
	 * 发送短信
	 * @param phone
	 * @param smsType
	 * @return
	 */
	
	/*@RequestMapping(value = "sendvc", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<BaseResp> sendVc(@Valid @RequestBody SMSReq req) {
		// 校验图形验证码
		if (!StringUtil.isEmpty(req.getImgvc())) {
			this.captchaService.validImgVc(req.getImgvc(), super.session.getId());
		}
		CheckUtil.isMobileNO(req.getPhone(), false, 0, 80027);
		this.sMSUtils.sendSMS(req.getPhone(), req.getSmsType(), req.getValues());

		return new ResponseEntity<BaseResp>(new BaseResp(), HttpStatus.OK);
	}*/
	
	/**
	 * 家加财新代码
	 * 
	 *
	 * 发送短信（限制短信验证码类的发送次数）
	 * @param phone
	 * @param smsType
	 * @return
	 */
	@RequestMapping(value = "sendvc", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<SendSMSRep> sendVc(@Valid @RequestBody SMSReq req) {
		
		//暂时加入白名单功能，只有白名单的用户才能进行注册
		if(req.getSmsType().equals("regist")){
			SwitchWhiteEntity whiteEntity = this.switchWhiteService.findBySwitchOidAndUserAcc("4", req.getPhone());
			if(whiteEntity == null){
				throw new AMPException("您的手机号暂时无法注册");
			}
		}
		
		// 校验图形验证码
		if (!StringUtil.isEmpty(req.getImgvc())) {
			this.captchaService.validImgVc(req.getImgvc(), super.session.getId());
		}
		CheckUtil.isMobileNO(req.getPhone(), false, 0, 80027);
		SendSMSRep rep = this.sMSUtils.newSendSms(req.getPhone(), req.getSmsType(), req.getValues());

		return new ResponseEntity<SendSMSRep>(rep, HttpStatus.OK);
	}
	
	/**
	 * 校验短信
	 * @param phone
	 * @param smsType
	 * @param veriCode
	 * @return
	 */
	@RequestMapping(value = "checkvc", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<BaseResp> checkVc(@Valid @RequestBody SMSReq req) {
		CheckUtil.isMobileNO(req.getPhone(), false, 0, 80027);
		this.sMSUtils.checkVeriCode(req.getPhone(), req.getSmsType(), req.getVeriCode());

		return new ResponseEntity<BaseResp>(new BaseResp(), HttpStatus.OK);
	}
}
