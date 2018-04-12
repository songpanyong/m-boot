package com.guohuai.mmp.publisher.bankorder;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
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
import com.guohuai.mmp.investor.bankorder.DepositLongBankOrderReq;
import com.guohuai.mmp.investor.bankorder.DepositShortBankOrderReq;
import com.guohuai.mmp.investor.bankorder.WithdrawLongBankOrderReq;
import com.guohuai.mmp.investor.bankorder.WithdrawShortBankOrderReq;
import com.guohuai.mmp.platform.payment.OrderNotifyReq;
import com.guohuai.mmp.platform.payment.Payment;
import com.guohuai.mmp.publisher.baseaccount.PublisherBaseAccountEntity;
import com.guohuai.mmp.publisher.baseaccount.loginacc.PublisherLoginAccService;

import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.In;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;

@RestController
@RequestMapping(value = "/mimosa/boot/publisher/bankorder", produces = "application/json")
public class PublisherBankOrderBootController extends BaseController {
	
	@Autowired
	private PublisherDepositBankOrderService publisherDepositBankOrderService;
	
	@Autowired
	private PublisherWithdrawBankOrderService publisherWithdrawBankOrderService;
	
	@Autowired
	private PublisherBankOrderService publisherBankOrderService;
	
	@Autowired
	private PublisherLoginAccService publisherLoginAccService;
	@Autowired
	private Payment paymentServiceImpl;
	@Autowired
	private PublisherCollectOrderService publisherCollectOrderService;
	@Autowired
	private PublisherPayOrderService publisherPayOrderService;
	
	/**
	 * 发行人充值
	 */
	@RequestMapping(value = "deposit", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<BaseResp> deposit(@Valid BankOrderReq bankOrderReq) {
		String uid = this.getLoginUser();
		
		BaseResp rep = this.publisherDepositBankOrderService.deposit(bankOrderReq, uid);
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
	@RequestMapping(value = "depositdup", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Boolean> depositDressUp(@RequestParam(required = true) String orderCode, @RequestParam(required = true) String returnCode) {
		
		OrderNotifyReq rep = new OrderNotifyReq();
		rep.setOrderCode(orderCode);
		rep.setReturnCode(returnCode);
		boolean flag = this.paymentServiceImpl.tradeCallback(rep);
		return new ResponseEntity<Boolean>(flag, HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "depositlong", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntity<BaseResp> depositLong(@RequestBody @Valid DepositLongBankOrderReq bankOrderReq) {
		this.getLoginUser();
	
		BaseResp rep = this.publisherDepositBankOrderService.depositLong(bankOrderReq);
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
	@RequestMapping(value = "depositshort", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntity<BaseResp> depositShort(@RequestBody @Valid DepositShortBankOrderReq bankOrderReq) {
		this.getLoginUser();
	
		BaseResp rep = this.publisherDepositBankOrderService.depositShort(bankOrderReq);
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
	@RequestMapping(value = "withdrawlong", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntity<BaseResp> depositLong(@RequestBody @Valid WithdrawLongBankOrderReq bankOrderReq) {
		this.getLoginUser();
	
		BaseResp rep = this.publisherWithdrawBankOrderService.withdrawLong(bankOrderReq);
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "withdrawshort", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntity<BaseResp> withdrawShort(@RequestBody @Valid WithdrawShortBankOrderReq bankOrderReq) {
		this.getLoginUser();
	
		BaseResp rep = this.publisherWithdrawBankOrderService.withdrawShort(bankOrderReq);
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
	/**
	 * 发行人收款
	 */
	@RequestMapping(value = "collect", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<BaseResp> collect(@Valid BankOrderCollectReq bankOrderReq) {
		String uid = this.getLoginUser();
		
		BaseResp rep = this.publisherCollectOrderService.collect(bankOrderReq, uid);
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
	
	/**
	 * 发行人放款
	 */
	@RequestMapping(value = "pay", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<BaseResp> pay(@Valid BankOrderPayReq bankOrderReq) {
		String uid = this.getLoginUser();
		
		BaseResp rep = this.publisherPayOrderService.pay(bankOrderReq, uid);
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "withdraw", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<BaseResp> withdraw(@Valid BankOrderWithdrawReq bankOrderReq) {
		String uid = this.getLoginUser();
		
		BaseResp rep = this.publisherWithdrawBankOrderService.withdraw(bankOrderReq, uid);
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
	@RequestMapping(value = "withdrawdup", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Boolean> withdrawDressUp(@RequestParam(required = true) String orderCode, @RequestParam(required = true) String returnCode) {
		OrderNotifyReq rep = new OrderNotifyReq();
		//rep.setIPayNo(orderCode);
		rep.setOrderCode(orderCode);
		rep.setReturnCode(returnCode);
		Boolean flag = this.paymentServiceImpl.tradeCallback(rep);
		return new ResponseEntity<Boolean>(flag, HttpStatus.OK);
	}
	
	@RequestMapping(value = "isdone", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<BaseResp> isDone(@Valid BankOrderIsDoneReq bankOrderReq) {
		
		BaseResp rep = this.publisherBankOrderService.isDone(bankOrderReq);
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
	/**
	 * 发行人个人
	 */
	@RequestMapping(value = "mng", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<PageResp<PublisherBankOrderQueryRep>> mng(HttpServletRequest request,
			@And({@Spec(params = "orderType", path = "orderType", spec = In.class),
				  @Spec(params = "orderStatus", path = "orderStatus", spec = In.class)}) Specification<PublisherBankOrderEntity> spec,
			@RequestParam int page, 
			@RequestParam int rows,
			@RequestParam(required = false, defaultValue = "createTime") String sort,
			@RequestParam(required = false, defaultValue = "desc") String order) {
		Direction sortDirection = Direction.DESC;
		if (!"desc".equals(order)) {
			sortDirection = Direction.ASC;
		}
		final String uid = this.getLoginUser();
		final PublisherBaseAccountEntity baseAccount = publisherLoginAccService.findByLoginAcc(uid);
		Specification<PublisherBankOrderEntity> speci = new Specification<PublisherBankOrderEntity>() {
			@Override
			public Predicate toPredicate(Root<PublisherBankOrderEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate tmp = cb.equal(root.get("publisherBaseAccount"), baseAccount);
				return tmp;
			}
		};
		spec = Specifications.where(spec).and(speci);
		Pageable pageable = new PageRequest(page - 1, rows, new Sort(new Order(sortDirection, sort)));
		PageResp<PublisherBankOrderQueryRep> rep = this.publisherBankOrderService.publisherBankOrderMng(spec, pageable);
		return new ResponseEntity<PageResp<PublisherBankOrderQueryRep>>(rep, HttpStatus.OK);
	}
	
	/**
	 * 发行人管理
	 */
	@RequestMapping(value = "smng", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<PageResp<PublisherBankOrderQueryRep>> smng(HttpServletRequest request,
			@And({@Spec(params = "orderType", path = "orderType", spec = In.class),
				  @Spec(params = "orderStatus", path = "orderStatus", spec = In.class),
				  @Spec(params = "publisherOid", path = "publisherBaseAccount.oid", spec = Equal.class)}) Specification<PublisherBankOrderEntity> spec,
			@RequestParam int page, 
			@RequestParam int rows,
			@RequestParam(required = false, defaultValue = "createTime") String sort,
			@RequestParam(required = false, defaultValue = "desc") String order) {
		Direction sortDirection = Direction.DESC;
		if (!"desc".equals(order)) {
			sortDirection = Direction.ASC;
		}
	
		Pageable pageable = new PageRequest(page - 1, rows, new Sort(new Order(sortDirection, sort)));
		PageResp<PublisherBankOrderQueryRep> rep = this.publisherBankOrderService.publisherBankOrderMng(spec, pageable);
		return new ResponseEntity<PageResp<PublisherBankOrderQueryRep>>(rep, HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "cright", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntity<BaseResp> cRight(@RequestParam String orderCode) {
		this.getLoginUser();
	
		BaseResp rep = this.publisherDepositBankOrderService.correctDepositBankOrder(orderCode);
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
	@RequestMapping(value = "notifyok", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntity<BaseResp> notifyOk(@RequestBody @Valid NotifyReq notifyReq) {
		this.getLoginUser();
	
		BaseResp rep = this.publisherBankOrderService.notifyOk(notifyReq);
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
	@RequestMapping(value = "notifyfail", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntity<BaseResp> notifyFail(@RequestBody @Valid NotifyReq notifyReq) {
		this.getLoginUser();
	
		BaseResp rep = this.publisherBankOrderService.notifyFail(notifyReq);
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
}
