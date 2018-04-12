package com.guohuai.mmp.platform.finance.orders;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.guohuai.basic.component.ext.web.BaseController;
import com.guohuai.basic.component.ext.web.BaseResp;

@RestController
@RequestMapping(value = "/mimosa/platform/finance/orders", produces = "application/json")
public class OrdersBootController extends BaseController{

	@Autowired
	private OrdersService ordersService;
	
	
	/**
	 * 1.3 本地交易记录准备
	 */
	@RequestMapping(value = "lorders")
	@ResponseBody
	public ResponseEntity<BaseResp> lorders(@RequestParam String checkOid) {
		BaseResp rep= ordersService.performLocalOrders(checkOid);
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
	
	/**
	 *  1.4 轧帐
	 */
	@RequestMapping(value = "check")
	@ResponseBody
	public ResponseEntity<BaseResp> check(@RequestParam String checkOid) {
		BaseResp rep= ordersService.check(checkOid);
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
	
}
