package com.guohuai.mmp.investor.bank;

import javax.validation.Valid;

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
/**
 * User and debit card relationship management controller
 * @author Jeffrey.Wong
 * 2015年7月8日下午5:13:40
 */
@RestController
@RequestMapping(value="/mimosa/client/investor/bank",produces="application/json;charset=UTF-8")
public class BankClientController extends BaseController {
	
	@Autowired
	private BankService bankService;
	
	/**
	 * 绑卡申请
	 * @param req
	 * @return
	 */
	@RequestMapping(value="/bindcardapply", method=RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<BaseResp> binkBankApply(@Valid @RequestBody BindBankCardApplyReq req){
		BaseResp rep = this.bankService.bindApply(req, super.getLoginUser());
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
	/**
	 * 新增银行卡
	 * @param req
	 * @return
	 */
	@RequestMapping(value="/add", method=RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<BaseResp> add(@Valid @RequestBody BankAddReq req){
		BaseResp rep = this.bankService.add(req, super.getLoginUser());
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
	/**
	 * 绑定银行卡
	 * @author wq
	 * @param req
	 * @return
	 */
	@RequestMapping(value="/bindBankCard", method=RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<BaseResp> bindBankCard(@Valid @RequestBody BindBankCardReq req){
		BaseResp rep = this.bankService.bindBankCard(req);
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
}
