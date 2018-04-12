package com.guohuai.mmp.investor.sonaccount;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.HashMapChangeSet;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.guohuai.basic.common.StringUtil;
import com.guohuai.basic.component.ext.web.BaseController;
import com.guohuai.basic.component.ext.web.BaseResp;

import com.guohuai.component.web.view.RowsRep;
import com.guohuai.mmp.investor.bank.BindBankCardApplyReq;
import com.guohuai.mmp.investor.baseaccount.BaseAccountInfoRep;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountAddReq;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountClientController;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountEntity;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountIsLoginRep;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountLoginReq;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(value = "/mimosa/client/investor/sonaccount", produces = "application/json")
@Slf4j
public class SonAccountController extends BaseController {

	@Autowired
	private SonAccountService sonAccountService;
	
	@Autowired
	private InvestorBaseAccountService investorBaseAccountService;
	
	@Autowired
	private SonAccountBindCardService sonAccountBindCardService;
	/*
	*//**
	 * 检查子账户昵称是否存在
	 * @return
	 *//*
	@RequestMapping(value = "checknickname", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<CheckNicknameRep> checkickname(@RequestParam String nickname) {
		String pid = this.getLoginUser();
		CheckNicknameRep rep = this.sonAccountService.checkNickname(pid,nickname);
		
		return new ResponseEntity<CheckNicknameRep>(rep, HttpStatus.OK);
	}*/
	/*
	*//**
	 *为子账户创建账户名
	 *@param  req
	 *@return rep
	 * 
	 *//*
	@RequestMapping(value="regist",method=RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<RegistSonAccountRep> registSonAccount(@RequestBody @Validated RegistSonAccountReq req){
		
		RegistSonAccountRep rep =  this.sonAccountService.registSonAccount(req);
		
		return new ResponseEntity<RegistSonAccountRep>(rep,HttpStatus.OK) ;
		
	}*/
	/**
	 * 主账号绑定子账户
	 * @param req
	 * @return 
	 * 
	 * */
	@RequestMapping(value="sonbind",method=RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<BaseResp> sonBind(@RequestBody SonBindReq req){
		this.sonAccountService.sonBind(req);		
		return new ResponseEntity<BaseResp>(new BaseResp(),HttpStatus.OK);
		
	}
	
	/**
	 * 显示子账户的信息
	 * @param id
	 * @return
	 * @throws ParseException 
	 * 
	 * */
	@RequestMapping(value="soninfo",method=RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<SonInfoRep> sonInfo(@RequestParam String sMemberId) throws ParseException{
		SonInfoRep rep  =   this.sonAccountService.SonInfo(sMemberId);	
		return new ResponseEntity<SonInfoRep>(rep,HttpStatus.OK);
		
	}
	
	/**
	 * 解除账户绑定
	 * @param id
	 * @return
	 * 
	 * */
	@RequestMapping(value="/sonunbind",method=RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<BaseResp> sonUnbind(@RequestParam String sMemberId){		
		 this.sonAccountService.sonUnbind(sMemberId);	
		return new ResponseEntity<BaseResp>(new BaseResp(),HttpStatus.OK);
		
	}
	
	/**
	 * 通过手机号获取用户信息
	 */
	@RequestMapping(value = "telgetaccountinfo", method =  RequestMethod.POST )
	@ResponseBody
	public ResponseEntity<BaseAccountInfoRep> telGetAccountInfo(@RequestParam String phoneNum){
		InvestorBaseAccountEntity account = this.investorBaseAccountService.findByPhone(phoneNum);
		BaseAccountInfoRep rep = this.investorBaseAccountService.getAccountInfo(account.getOid());
		return new ResponseEntity<BaseAccountInfoRep>(rep, HttpStatus.OK);
		
	}
	
	/**
	 *创建子账户 
	 * 
	 */
	@RequestMapping(value="/createsonaccount",method=RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<CheckNicknameRep> addNickName(@RequestBody AddNickNameReq req ){
		
		
		CheckNicknameRep rep = this.sonAccountService.checkNicknames(req);
		return new ResponseEntity<CheckNicknameRep>(rep,HttpStatus.OK);
		
	}
	
	/**
	 * 为账户完善姓名和身份证号
	 * @throws ParseException 
	 * 
	 * */
	@RequestMapping(value="/addrealname",method=RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<RegistSonAccountRep> createSonAccount(@RequestBody RegistSonAccountReq req) {
		
		RegistSonAccountRep rep = this.sonAccountService.createSonAccount(req);
		
		return new ResponseEntity<RegistSonAccountRep>(rep,HttpStatus.OK);
	}
	
	/**
	 * 主账号切换到子账户
	 * @return
	 * @throws ParseException 
	 *//*
	@RequestMapping(value = "/change", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<BaseResp> change(@RequestBody ChangeAccountReq req)  {		
		ChangeToSonRep rep = this.sonAccountService.change(req);
		super.setLoginUser(req.getUserId(), new String[]{req.getPlatform()});
		return new ResponseEntity<BaseResp>(rep , HttpStatus.OK);
	}*/
	
	/**
	 * 主账号切换到子账户
	 * @return
	 * @throws ParseException 
	 * 
	 * 目前使用这个
	 */
	@RequestMapping(value = "/change", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<PAccountChangeToSonRep> change(@RequestBody ChangeAccountReq req)  {		
		PAccountChangeToSonRep rep = this.sonAccountService.change(req);
		if(rep.getErrorCode()==0){
			super.setLoginUser(req.getUserId(), new String[]{req.getPlatform()});
		}
		
		return new ResponseEntity<PAccountChangeToSonRep>(rep , HttpStatus.OK);
	}
	
	/**
	 * 
	 * 子账户切换到主账户
	 * 
	 * */
	@RequestMapping(value="/changetobasicaccount", method=RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<ChangeToBasicRep> changeTOBasicAccount(@RequestParam String platform){
		String investorOid = super.getLoginUser();
		ChangeToBasicRep rep =  this.sonAccountService.changeTOBasicAccount(investorOid);
		super.setLoginUser(rep.getPid(), new String[]{platform});
		
		return new ResponseEntity<ChangeToBasicRep>(rep,HttpStatus.OK);
	}
	
	
	
	/**
	 * 判断主、子登录和主账号下是否有子账户
	 * @throws ParseException 
	 * 
	 * */
	@RequestMapping(value="/judge", method=RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<JudgeRep> checkLogin()  {
		JudgeRep rep = null;
		String investorOid = super.isLogin();
		if (StringUtil.isEmpty(investorOid)) {
			 rep = new JudgeRep();
			rep.setIslogin(false);
			log.info("当前用户未登录，或会话超时");
		} else {
			rep = this.sonAccountService.judge(investorOid);
		}
		return new ResponseEntity<JudgeRep>(rep,HttpStatus.OK);
	}
	
	/**
	 * 主子账号列表查询
	 * 
	 * */
	@RequestMapping(value="/accountlist", method=RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<AccountListRep> accountLists(){
		String investor = super.getLoginUser();			
		Map<String,Object> map=  this.sonAccountService.accountLists(investor);
		AccountListRep rep = new AccountListRep();
		rep.setInfo(map);		
		return new ResponseEntity<>(rep,HttpStatus.OK);
	}
	
	/** 通过id获取主账户的信息  */
	@RequestMapping(value="/mainaccountinfo", method=RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<MainAccountInfoRep> accountInfo(@RequestParam String investorOid){
		
		MainAccountInfoRep rep =  this.sonAccountService.accountInfo(investorOid);
		return new ResponseEntity<MainAccountInfoRep>(rep,HttpStatus.OK);
	}
	
	
	/** 查看点击头像查看当前账户的信息 
	 * @throws ParseException */
	@RequestMapping(value="/accountinfo", method=RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<SonAccountInfoRep> sonAccountInfo() {
		SonAccountInfoRep rep =null;
		String investorOid = super.isLogin();
		if(StringUtil.isEmpty(investorOid)){
			rep = new SonAccountInfoRep();
			rep.setIslogin(false);
		}else{
			rep =  this.sonAccountService.sonAccountInfo(investorOid);
		}
		
		return new ResponseEntity<SonAccountInfoRep>(rep,HttpStatus.OK);
	}
	
	/**
	 * 
	 * 账户的绑卡申请 
	 * 
	 **/
	@RequestMapping(value="/sonbindapply", method=RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<BaseResp> sonBindApply(@RequestBody SonBindBankCardApplyReq req) {
		
		 BaseResp rep = this.sonAccountBindCardService.sonBindApply(req);
		return new ResponseEntity<BaseResp>(rep,HttpStatus.OK);
	}
	

	/**
	 * 为子账户绑定银行卡
	 * @param req
	 * @return
	 */
	@RequestMapping(value="/sonbindconfirm", method=RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<BaseResp> sonAddCard(@RequestBody SonAddCardReq req) {
		
		 BaseResp rep = this.sonAccountBindCardService.sonAddCard(req);
		return new ResponseEntity<BaseResp>(rep,HttpStatus.OK);
	}
	
	/**
	 * 账户的解绑卡
	 * @param req
	 * @return
	 */
	@RequestMapping(value="/removebank", method=RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<BaseResp>removeBank () {
		
		BaseResp rep = this.sonAccountBindCardService.removeBank();
		
		return new ResponseEntity<BaseResp>(rep,HttpStatus.OK);
	}
	
	/**
	 * 在子账户中获取主账户的余额和id
	 * @param req
	 * @return
	 */
	@RequestMapping(value="/showmaininson",method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<GetMasterInfoRep> getMasterInfo () {		
		GetMasterInfoRep rep = this.sonAccountService.getMasterInfo();		
		return new ResponseEntity<GetMasterInfoRep>(rep,HttpStatus.OK);
	}
	
	/**
	 * 主子账号资产列表查询
	 * 
	 * */
	@RequestMapping(value="/accountamountlist", method=RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<AccountListRep> accountAmountList(){
		String investor = super.getLoginUser();			
		Map<String,Object> map=  this.sonAccountService.accountAmountList(investor);
		AccountListRep rep = new AccountListRep();
		rep.setInfo(map);		
		return new ResponseEntity<>(rep,HttpStatus.OK);
	}
}
