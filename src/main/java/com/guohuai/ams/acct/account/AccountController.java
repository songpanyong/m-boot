package com.guohuai.ams.acct.account;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.guohuai.basic.component.ext.web.BaseController;

@RestController
@RequestMapping(value = "/mimosa/acct/account", produces = "application/json;charset=utf-8")
public class AccountController extends BaseController {

	@Autowired
	private AccountService accountService;

	@RequestMapping(value = "/search", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ResponseEntity<List<AccountResp>> search() {
		super.getLoginUser();
		List<Account> list = this.accountService.search();
		List<AccountResp> view = new ArrayList<AccountResp>();
		for (Account a : list) {
			view.add(new AccountResp(a));
		}
		return new ResponseEntity<List<AccountResp>>(view, HttpStatus.OK);
	}

	@RequestMapping(value = "/update", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ResponseEntity<AccountResp> update(AccountForm form) {
		super.getLoginUser();
		Account a = this.accountService.update(form);
		AccountResp view = new AccountResp(a);
		return new ResponseEntity<AccountResp>(view, HttpStatus.OK);
	}

}
