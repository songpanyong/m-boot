package com.guohuai.mmp.investor.baseaccount;

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

import com.guohuai.basic.common.StringUtil;
import com.guohuai.basic.component.exception.GHException;
import com.guohuai.basic.component.ext.web.BaseController;
import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.component.util.CheckUtil;
import com.guohuai.mmp.captcha.CaptchaService;
import com.guohuai.mmp.investor.baseaccount.statistics.InvestorStatisticsService;
import com.guohuai.mmp.investor.baseaccount.statistics.MyCaptialQueryRep;
import com.guohuai.mmp.investor.baseaccount.statistics.MyHomeQueryRep;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(value = "/mimosa/client/investor/baseaccount", produces = "application/json")
@Slf4j
public class InvestorBaseAccountClientController extends BaseController {
	
	@Autowired
	InvestorBaseAccountService investorBaseAccountService;
	@Autowired
	InvestorStatisticsService investorStatisticsService;
	@Autowired
	private CaptchaService captchaService;
	
	/**
	 * 登录（新）
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "login", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<BaseResp> login(@Valid @RequestBody InvestorBaseAccountLoginReq req) {
		
		CheckUtil.isMobileNO(req.getUserAcc(), false, 0, 80027);
		
		String accountOid = StringUtil.EMPTY;
		if (!StringUtil.isEmpty(req.getUserPwd())) {
			accountOid = this.investorBaseAccountService.login(req);
		}/* else if (!StringUtil.isEmpty(req.getVericode())) {
			// 快速登录
			accountOid = this.investorBaseAccountService.fastLogin(req);
		}*/ else {
			throw new GHException("请输入密码！");
		}

		if (!StringUtil.isEmpty(accountOid)) {
			//将用户的id保存在session中
			super.setLoginUser(accountOid, new String[]{req.getPlatform()});
		}
		return new ResponseEntity<BaseResp>(new BaseResp(), HttpStatus.OK);
	}
	
	
	
	@RequestMapping(value = "logout", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<BaseResp> logout() {
		this.setLogoutUser();
		return new ResponseEntity<BaseResp>(new BaseResp(), HttpStatus.OK);
	}
	
	/**
	 * 判断用户的锁定状态
	 * @param userAcc 手机号/账号
	 * @return
	 */
	@RequestMapping(value = "/checklock", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<BaseResp> checkLockState(@RequestParam(required = true) String userAcc) {
		CheckUtil.isMobileNO(userAcc, true, 80026, 80027);
		
		this.investorBaseAccountService.checkLockState(userAcc);
		
		return new ResponseEntity<BaseResp>(new BaseResp(), HttpStatus.OK);
	}
	
	/**
	 * 是否登录状态
	 * @return
	 */
	@RequestMapping(value = "islogin", method ={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public ResponseEntity<InvestorBaseAccountIsLoginRep> checkLogin() {
		InvestorBaseAccountIsLoginRep rep = new InvestorBaseAccountIsLoginRep();
		String investorOid = super.isLogin();
		if (StringUtil.isEmpty(investorOid)) {
			rep.setIslogin(false);
			log.info("当前用户未登录，或会话超时。");
		} else {
			rep.setIslogin(true);
			rep.setInvestorOid(investorOid);
		}
		return new ResponseEntity<InvestorBaseAccountIsLoginRep>(rep, HttpStatus.OK);
	}
	
	/**
	 * 用户信息
	 * @return
	 */
	@RequestMapping(value = "userinfo", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<BaseAccountRep> userinfo() {
		String uid = this.getLoginUser();//返回当前的用户id
		BaseAccountRep rep = this.investorBaseAccountService.userInfo(uid, true);
		return new ResponseEntity<BaseAccountRep>(rep, HttpStatus.OK);
	}
	
	
	/**
	 * 我的
	 * @author yuechao
	 */
	@RequestMapping(value = "myhome", method = RequestMethod.POST )
	@ResponseBody
	public ResponseEntity<MyHomeQueryRep> myHome() {
		String uid = this.getLoginUser();
		MyHomeQueryRep rep = this.investorStatisticsService.myHome(uid);
		return new ResponseEntity<MyHomeQueryRep>(rep, HttpStatus.OK);
	}
	
	/**
	 * 我的资产
	 * @author yuechao
	 */
	@RequestMapping(value = "mycaptial", method =  RequestMethod.POST )
	@ResponseBody
	public ResponseEntity<MyCaptialQueryRep> myCaptialQry() {
		String uid = this.getLoginUser();
		MyCaptialQueryRep rep = this.investorStatisticsService.myCaptial(uid);
		return new ResponseEntity<MyCaptialQueryRep>(rep, HttpStatus.OK);
	}
	
	/**
	 * 判断手机号是否已经注册
	 */
	@RequestMapping(value = "isregist", method =  RequestMethod.POST )
	@ResponseBody
	public ResponseEntity<InvestorBaseAccountIsRegistRep> isRegist(@RequestParam String phoneNum) {
		CheckUtil.isMobileNO(phoneNum, true, 80026, 80027);
		InvestorBaseAccountIsRegistRep rep = this.investorBaseAccountService.isRegist(phoneNum);
		return new ResponseEntity<InvestorBaseAccountIsRegistRep>(rep, HttpStatus.OK);
	}
	
	/**
	 * 注册（新）
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "regist", method =  RequestMethod.POST )
	@ResponseBody
	public ResponseEntity<BaseResp> regist(@Valid @RequestBody InvestorBaseAccountAddReq req) {
		// 校验图形验证码
		if (!StringUtil.isEmpty(req.getImgvc())) {
			this.captchaService.validImgVc(req.getImgvc(), super.session.getId());
		}
		CheckUtil.isMobileNO(req.getUserAcc(), false, 0, 80027);
		BaseResp rep = this.investorBaseAccountService.addBaseAccount(req, true);
		//BaseResp rep = this.investorBaseAccountService.addBaseAccount(req, false);
		// 登陆
		super.setLoginUser(req.getInvestorOid(), new String[]{req.getPlatform()});
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
	/**
	 * 校验输入的原登录密码是否正确（新）
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "checkloginpwd", method =  RequestMethod.POST )
	@ResponseBody
	public ResponseEntity<BaseResp> checkLoginPassword(@Valid @RequestBody InvestorBaseAccountPasswordReq req) {
		req.setInvestorOid(super.getLoginUser());
		BaseResp rep = this.investorBaseAccountService.checkLoginPassword(req);
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
	/**
	 * 设置/修改登录密码（新）
	 */
	@RequestMapping(value = "editloginpwd", method =  RequestMethod.POST )
	@ResponseBody
	public ResponseEntity<InvestorBaseAccountPasswordRep> editLoginPassword(@Valid @RequestBody InvestorBaseAccountPasswordReq req) {
		CheckUtil.checkloginPwd(req.getUserPwd(), 6, 16, false, 0, 80028);
		req.setInvestorOid(super.getLoginUser());
		InvestorBaseAccountPasswordRep rep = this.investorBaseAccountService.editLoginPassword(req);
		this.investorBaseAccountService.logoutOtherPlatform(req, rep, super.session.getId());
		return new ResponseEntity<InvestorBaseAccountPasswordRep>(rep, HttpStatus.OK);
	}
	
	/**
	 * 修改登录密码，包括验证旧密码（新）
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "modifyloginpwd", method =  RequestMethod.POST )
	@ResponseBody
	public ResponseEntity<InvestorBaseAccountPasswordRep> modifyLoginPassword(@Valid @RequestBody InvestorBaseAccountPasswordReq req) {
		CheckUtil.checkloginPwd(req.getOldUserPwd(), 6, 16, true, 80029, 80028);
		CheckUtil.checkloginPwd(req.getUserPwd(), 6, 16, false, 0, 80028);
		req.setInvestorOid(super.getLoginUser());
		InvestorBaseAccountPasswordRep rep = this.investorBaseAccountService.modifyLoginPassword(req);
		this.investorBaseAccountService.logoutOtherPlatform(req, rep, super.session.getId());
		return new ResponseEntity<InvestorBaseAccountPasswordRep>(rep, HttpStatus.OK);
	}
	
	/**
	 * 忘记登录密码（新）
	 */
	@RequestMapping(value = "forgetloginpwd", method =  RequestMethod.POST )
	@ResponseBody
	public ResponseEntity<InvestorBaseAccountPasswordRep> forgetLoginPassword(@Valid @RequestBody InvestorBaseAccountPasswordReq req) {
		CheckUtil.isMobileNO(req.getUserAcc(), true, 80026, 80027);
		CheckUtil.checkloginPwd(req.getUserPwd(), 6, 16, false, 0, 80028);
		CheckUtil.checkNum(req.getVericode(), 6, 6, true, 80030, 80031);
		InvestorBaseAccountPasswordRep rep = this.investorBaseAccountService.forgetLoginPassword(req);
		this.investorBaseAccountService.logoutOtherPlatform(req, rep, super.session.getId());
		return new ResponseEntity<InvestorBaseAccountPasswordRep>(rep, HttpStatus.OK);
	}
	
	/**
	 * 校验交易密码是否正确（新）
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "checkpaypwd", method =  RequestMethod.POST )
	@ResponseBody
	public ResponseEntity<BaseResp> checkPayPwd(@Valid @RequestBody InvestorBaseAccountPayPwdReq req) {
		req.setInvestorOid(super.getLoginUser());
		BaseResp rep = this.investorBaseAccountService.checkPayPwd(req);
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
	/**
	 * 设置/修改交易密码（新）
	 */
	@RequestMapping(value = "editpaypwd", method =  RequestMethod.POST )
	@ResponseBody
	public ResponseEntity<BaseResp> editPayPwd(@Valid @RequestBody InvestorBaseAccountPayPwdReq req) {
		CheckUtil.checkNum(req.getPayPwd(), 6, 6, false, 0, 80032);
		req.setInvestorOid(super.getLoginUser());
		BaseResp rep = this.investorBaseAccountService.editPayPwd(req);
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
	/**
	 * 修改交易密码，包括验证旧密码（新）
	 */
	@RequestMapping(value = "modifypaypwd", method =  RequestMethod.POST )
	@ResponseBody
	public ResponseEntity<BaseResp> modifyPayPwd(@Valid @RequestBody InvestorBaseAccountPayPwdReq req) {
		CheckUtil.checkNum(req.getOldPayPwd(), 6, 6, true, 80033, 80032);
		CheckUtil.checkNum(req.getPayPwd(), 6, 6, false, 0, 80032);
		req.setInvestorOid(super.getLoginUser());
		BaseResp rep = this.investorBaseAccountService.modifyPayPwd(req);
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
	/**
	 * 重置交易密码（新）
	 */
	@RequestMapping(value = "forgetpaypwd", method =  RequestMethod.POST )
	@ResponseBody
	public ResponseEntity<BaseResp> forgetPayPwd(@Valid @RequestBody InvestorBaseAccountPayPwdReq req) {
		CheckUtil.isMobileNO(req.getUserAcc(), true, 80026, 80027);
		CheckUtil.checkNum(req.getPayPwd(), 6, 6, false, 0, 80032);
		CheckUtil.checkNum(req.getVericode(), 6, 6, true, 80030, 80031);
		req.setInvestorOid(super.getLoginUser());
		BaseResp rep = this.investorBaseAccountService.forgetPayPwd(req);
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
	/**
	 * 用户信息（新）
	 */
	@RequestMapping(value = "accountinfo", method =  RequestMethod.POST )
	@ResponseBody
	public ResponseEntity<BaseAccountInfoRep> getAccountInfo() {
		BaseAccountInfoRep rep = this.investorBaseAccountService.getAccountInfo(super.getLoginUser());
		return new ResponseEntity<BaseAccountInfoRep>(rep, HttpStatus.OK);
	}
	
	
	/**
	 * 用户重置交易密码前的校验
	 * 
	 * */
	@RequestMapping(value = "checkforgetpaypwd",method = RequestMethod.POST)
	public ResponseEntity<BaseResp> checkBeforeresetPayPassword(@RequestBody CheckBeforeResetPassReq req){
		String investorOid = super.getLoginUser();
		req.setInvestorOid(investorOid);	
		BaseResp rep = this.investorBaseAccountService.checkBeforeresetPayPassword(req);
		return new ResponseEntity<BaseResp>(rep,HttpStatus.OK); 
	}
}
