package com.guohuai.mmp.investor.bankorder;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.guohuai.basic.component.ext.web.BaseController;
import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.basic.component.ext.web.PageResp;
import com.guohuai.component.util.DateUtil;

import net.kaczmarzyk.spring.data.jpa.domain.DateAfterInclusive;
import net.kaczmarzyk.spring.data.jpa.domain.DateBeforeInclusive;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.In;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;

@RestController
@RequestMapping(value = "/mimosa/boot/investor/bankorder")
public class InvestorBankOrderBootController extends BaseController {
	
	@Autowired
	private InvestorDepositBankOrderService investorDepositBankOrderService;
	@Autowired
	private InvestorWithdrawBankOrderService investorWithdrawBankOrderService;
	@Autowired
	private InvestorBankOrderService investorBankOrderService;

	
	
	@RequestMapping(value = "depositlong", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntity<BaseResp> depositLong(@RequestBody @Valid DepositLongBankOrderReq bankOrderReq) {
		this.getLoginUser();
	
		BaseResp rep = this.investorDepositBankOrderService.depositLong(bankOrderReq);
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
	@RequestMapping(value = "depositshort", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntity<BaseResp> depositShort(@RequestBody @Valid DepositShortBankOrderReq bankOrderReq) {
		this.getLoginUser();
	
		BaseResp rep = this.investorDepositBankOrderService.depositShort(bankOrderReq);
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
	@RequestMapping(value = "notifydepositok", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntity<BaseResp> notifyDepositOk(@RequestBody @Valid NotifyReq notifyReq) {
		this.getLoginUser();
	
		BaseResp rep = this.investorDepositBankOrderService.notifyDepositOk(notifyReq);
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
	@RequestMapping(value = "notifydepositfail", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntity<BaseResp> notifyDepositFail(@RequestBody @Valid NotifyReq notifyReq) {
		this.getLoginUser();
	
		BaseResp rep = this.investorDepositBankOrderService.notifyDepositFail(notifyReq);
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
	@RequestMapping(value = "withdrawlong", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntity<BaseResp> withdrawLong(@RequestBody @Valid WithdrawLongBankOrderReq bankOrderReq) {
		this.getLoginUser();
	
		BaseResp rep = this.investorWithdrawBankOrderService.withdrawLong(bankOrderReq);
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "withdrawshort", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntity<BaseResp> withdrawShort(@RequestBody @Valid WithdrawShortBankOrderReq bankOrderReq) {
		this.getLoginUser();
	
		BaseResp rep = this.investorWithdrawBankOrderService.withdrawShort(bankOrderReq);
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
	@RequestMapping(value = "notifyok", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntity<BaseResp> notifyOk(@RequestBody @Valid NotifyReq notifyReq) {
		this.getLoginUser();
	
		BaseResp rep = this.investorBankOrderService.notifyOk(notifyReq);
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
	@RequestMapping(value = "notifyfail", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntity<BaseResp> notifyFail(@RequestBody @Valid NotifyReq notifyReq) {
		this.getLoginUser();
	
		BaseResp rep = this.investorBankOrderService.notifyFail(notifyReq);
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
	@RequestMapping(value = "notifywithdrawok", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntity<BaseResp> notifyWithdrawOk(@RequestBody @Valid NotifyReq notifyReq) {
		this.getLoginUser();
	
		BaseResp rep = this.investorBankOrderService.notifyWithdrawOk(notifyReq);
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
	@RequestMapping(value = "notifywithdrawfail", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntity<BaseResp> notifyWithdrawFail(@RequestBody @Valid NotifyReq notifyReq) {
		this.getLoginUser();
	
		BaseResp rep = this.investorBankOrderService.notifyWithdrawFail(notifyReq);
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
	/**
	 * 实时对账:充值订单状态由失败改为成功
	 */
	@RequestMapping(value = "depositfail2ok", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntity<BaseResp> depositFail2Ok(@RequestParam String orderCode) {
		this.getLoginUser();
	
		BaseResp rep = this.investorDepositBankOrderService.depositFail2Ok(orderCode);
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
	
	/**
	 * 业务结算对账--投资人充值 由失败改成功
	 */
	@RequestMapping(value = "crdepositfail2ok", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntity<BaseResp> crDepositFail2Ok(@RequestBody @Valid NotifyReq notifyReq) {
		this.getLoginUser();
	
		BaseResp rep = this.investorDepositBankOrderService.crDepositFail2Ok(notifyReq);
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
	/**
	 * 业务结算对账--投资人充值  由成功改为失败
	 * 
	 */
	@RequestMapping(value = "depositok2fail", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntity<BaseResp> depositOk2Fail(@RequestBody @Valid NotifyReq notifyReq) {
		this.getLoginUser();
	
		BaseResp rep = this.investorDepositBankOrderService.depositOk2Fail(notifyReq);
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
	
	/**
	 * 提现修改订单状态
	 * 由成功改为失败
	 */
	@RequestMapping(value = "withdrawok2fail", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntity<BaseResp> withdrawOk2Fail(@RequestBody @Valid NotifyReq notifyReq) {
		this.getLoginUser();
	
		BaseResp rep = this.investorWithdrawBankOrderService.withdrawOk2Fail(notifyReq);
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
	/**
	 * 提现结算
	 */
	@RequestMapping(value = "withdrawpass", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntity<BaseResp> withdrawPass(@RequestParam String orderCode) {
		this.getLoginUser();
	
		BaseResp rep = this.investorWithdrawBankOrderService.withdrawPass(orderCode);
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
	/**
	 * 批量提现结算
	 */
	@RequestMapping(value = "withdrawbtpass", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntity<BaseResp> withdrawBatchPass(@RequestParam List<String> orderCodes) {
		this.getLoginUser();
	
		BaseResp rep = this.investorWithdrawBankOrderService.withdrawBatchPass(orderCodes);
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
	/**
	 * 提现失败
	 */
	@RequestMapping(value = "withdrawreject", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntity<BaseResp> withdrawReject(@RequestParam String orderCode) {
		this.getLoginUser();
	
		BaseResp rep = this.investorWithdrawBankOrderService.withdrawReject(orderCode);
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
	/**
	 * 批量提现失败
	 */
	@RequestMapping(value = "withdrawbtreject", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntity<BaseResp> withdrawBatchReject(@RequestParam List<String> orderCodes) {
		this.getLoginUser();
	
		BaseResp rep = this.investorWithdrawBankOrderService.withdrawBatchReject(orderCodes);
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
	@RequestMapping(value = "cright", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntity<BaseResp> withdrawShort(@RequestParam String orderCode) {
		this.getLoginUser();
	
		BaseResp rep = this.investorDepositBankOrderService.correctDepositBankOrder(orderCode);
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
	@RequestMapping(value = "mng", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<PageResp<MyBankOrderRep>> mng(HttpServletRequest request,
			@And({ @Spec(params = "orderType", path = "orderType", spec = In.class),
					@Spec(params = "investorOid", path = "investorBaseAccount.oid", spec = Equal.class),
					@Spec(params = "orderCode", path = "orderCode", spec = Equal.class),
					@Spec(params = "orderTimeBegin", path = "orderTime", spec = DateAfterInclusive.class, config = DateUtil.fullDatePattern),
					@Spec(params = "orderTimeEnd", path = "orderTime", spec = DateBeforeInclusive.class, config = DateUtil.fullDatePattern) }) Specification<InvestorBankOrderEntity> spec,
			@RequestParam int page, @RequestParam int rows,
			@RequestParam(required = false, defaultValue = "createTime") String sort,
			@RequestParam(required = false, defaultValue = "desc") String order) {
		Direction sortDirection = Direction.DESC;
		if (!"desc".equals(order)) {
			sortDirection = Direction.ASC;
		}
	
		Pageable pageable = new PageRequest(page - 1, rows, new Sort(new Order(sortDirection, sort)));
		PageResp<MyBankOrderRep> rep = this.investorBankOrderService.myquery(spec, pageable);
		return new ResponseEntity<PageResp<MyBankOrderRep>>(rep, HttpStatus.OK);
	}
	
	@RequestMapping(value = "redenvelopeshort", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntity<BaseResp> redEnvelopeShort(@RequestBody @Valid RedEnvelopeShortBankOrderReq bankOrderReq) {
		this.getLoginUser();
	
		BaseResp rep = this.investorDepositBankOrderService.redEnvelopeShort(bankOrderReq);
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}


}
