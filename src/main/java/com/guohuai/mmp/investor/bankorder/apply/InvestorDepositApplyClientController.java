package com.guohuai.mmp.investor.bankorder.apply;

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

@RestController
@RequestMapping(value = "/mimosa/client/investor/bankorder/apply", produces = "application/json")
public class InvestorDepositApplyClientController extends BaseController {
	
	
	@Autowired
	private InvestorDepositApplyService investorDepositApplyService;
	
	
	@RequestMapping(value = "dapply", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<BaseResp> depositApply(@RequestBody @Valid ApplyReq req) {
		String uid = this.getLoginUser();
		
		BaseResp rep = this.investorDepositApplyService.depositApply(req, uid);
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
}
