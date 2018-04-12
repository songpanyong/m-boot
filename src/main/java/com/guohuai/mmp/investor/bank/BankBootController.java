package com.guohuai.mmp.investor.bank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.guohuai.basic.component.ext.web.BaseController;
import com.guohuai.basic.component.ext.web.BaseResp;

@RestController
@RequestMapping(value="/mimosa/boot/investor/bank",produces="application/json;charset=UTF-8")
public class BankBootController extends BaseController {

	@Autowired
	private BankService bankService;
	

	/**
	 * 解绑银行卡
	 * @param investorOid
	 * @return
	 */
	@RequestMapping(value="/removebank", method=RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<BaseResp> removeBank(@RequestParam(required = true) String investorOid) {
		String operator = super.getLoginUser();
		BaseResp rep = this.bankService.syncRemoveSettleBank(investorOid, operator);
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
	/**
	 * 获取用户绑定的银行卡信息
	 * @param investorOid
	 * @return
	 */
	@RequestMapping(value = "/getbankinfo", method = {RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public ResponseEntity<BankInfoRep> getBankInfo(@RequestParam(required = true) String investorOid) {
		BankInfoRep rep = this.bankService.getBankInfo(investorOid);
		
		return new ResponseEntity<BankInfoRep>(rep, HttpStatus.OK);
	}
	
}
