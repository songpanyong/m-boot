package com.guohuai.mmp.investor.bankorder;

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

import com.alibaba.fastjson.JSONObject;
import com.guohuai.basic.component.ext.web.BaseController;
import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.basic.component.ext.web.PageResp;
import com.guohuai.component.util.DateUtil;
import com.guohuai.mmp.investor.bank.BankService;
import com.guohuai.mmp.investor.bank.BindBankCardApplyReq;
import com.guohuai.mmp.investor.bankorder.apply.ApplyReq;
import com.guohuai.mmp.investor.bankorder.apply.InvestorDepositApplyService;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountEntity;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountService;
import com.guohuai.mmp.investor.sonaccount.SonAccountService;
import com.guohuai.mmp.platform.payment.PayHtmlRep;

import lombok.extern.slf4j.Slf4j;
import net.kaczmarzyk.spring.data.jpa.domain.DateAfterInclusive;
import net.kaczmarzyk.spring.data.jpa.domain.DateBeforeInclusive;
import net.kaczmarzyk.spring.data.jpa.domain.In;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;

@Slf4j
@RestController
@RequestMapping(value = "/mimosa/client/investor/bankorder")
public class InvestorBankOrderClientController extends BaseController {
	
	@Autowired
	private InvestorBankOrderExtService investorBankOrderExtService;
	@Autowired
	private InvestorBankOrderService investorBankOrderService;
	@Autowired
	private SonAccountService sonAccountService;
	@Autowired
	private InvestorBaseAccountService baseAccountService;
	@Autowired
	private BankService bankService;
	@Autowired
	private InvestorDepositApplyService investorDepositApplyService;

	/**
	 * 充值(认证支付)
	 * @param bankOrderReq
	 * @return
	 */
	@RequestMapping(value = "deposit", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntity<BankOrderRep> deposit(@RequestBody @Valid DepositBankOrderReq bankOrderReq) {
		String investorOid = null;
		if(bankOrderReq.getInvestorOid() == null || bankOrderReq.getInvestorOid().length()<=0){
			investorOid = this.getLoginUser();
		}else{
			investorOid = bankOrderReq.getInvestorOid();
		}
		
		BankOrderRep rep = this.investorBankOrderExtService.deposit(bankOrderReq, investorOid);
		return new ResponseEntity<BankOrderRep>(rep, HttpStatus.OK);
	}
	
	/**
	 * 充值(代扣)
	 * @param bankOrderReq
	 * @return
	 */
	@RequestMapping(value = "depositbf", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntity<BankOrderRep> depositbf(@RequestBody @Valid DepositBankOrderbfReq bankOrderReq) {
		String investorOid = this.getLoginUser();
		
		BankOrderRep rep = this.investorBankOrderExtService.depositbf(bankOrderReq, investorOid);
		return new ResponseEntity<BankOrderRep>(rep, HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "apideposit", method = {RequestMethod.POST, RequestMethod.GET}, produces = "text/html; charset=utf-8")
	public String apiDeposit(@Valid ApiDepositBankOrderReq bankOrderReq) {
		String uid = this.getLoginUser();
	
		PayHtmlRep rep = this.investorBankOrderExtService.apiDeposit(bankOrderReq, uid);
		this.response.setContentType("text/html; charset=utf-8");
		return rep.getRetHtml();
	}
	
	/**
	 * 提现
	 * @param bankOrderReq
	 * @return
	 */
	@RequestMapping(value = "withdraw", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntity<BankOrderRep> withdraw(@RequestBody @Valid WithdrawBankOrderReq bankOrderReq) {
		String uid = this.getLoginUser();
		
		BankOrderRep rep = this.investorBankOrderExtService.withdraw(bankOrderReq, uid);
		return new ResponseEntity<BankOrderRep>(rep, HttpStatus.OK);
	}
	
	/**
	 * 用户之间转账
	 * 子账户发起转账请求，从主账户里面扣钱
	 * @param bankOrderReq
	 * @return
	 */
	@RequestMapping(value = "transferAccount", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntity<BaseResp> transferAccount(@RequestBody @Valid TransferAccountReq req) {
		String uid = this.getLoginUser();
		//验证传过来的要转账的子账户ID是否与主账户存在关联关系
		this.sonAccountService.accountInfo(req.getParentAccountId());
		BaseResp rep = this.investorBankOrderService.transferAccount(req,uid);
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
	/**
	 * 检测充值订单是否完成
	 * @param isDone
	 * @return
	 */
	@RequestMapping(value = "isdone", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntity<BaseResp> isDone(@RequestBody @Valid BankOrderIsDoneReq isDone) {
		this.getLoginUser();
		
		BaseResp rep = this.investorBankOrderService.isDone(isDone);
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
	/**
	 * 交易明细
	 * @param request
	 * @param spec
	 * @param page
	 * @param rows
	 * @param sort
	 * @param order
	 * @return
	 */
	@RequestMapping(value = "mng", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<PageResp<MyBankOrderRep>> mng(HttpServletRequest request,
			@And({ @Spec(params = "orderType", path = "orderType", spec = In.class),
					@Spec(params = "orderTimeBegin", path = "orderTime", spec = DateAfterInclusive.class, config = DateUtil.fullDatePattern),
					@Spec(params = "orderTimeEnd", path = "orderTime", spec = DateBeforeInclusive.class, config = DateUtil.fullDatePattern) }) Specification<InvestorBankOrderEntity> spec,
			@RequestParam int page, @RequestParam int rows,
			@RequestParam(required = false, defaultValue = "createTime") String sort,
			@RequestParam(required = false, defaultValue = "desc") String order) {
		Direction sortDirection = Direction.DESC;
		if (!"desc".equals(order)) {
			sortDirection = Direction.ASC;
		}
		final String userOid = this.getLoginUser();
		Specification<InvestorBankOrderEntity> specUserOid = new Specification<InvestorBankOrderEntity>() {
		@Override
		public Predicate toPredicate(Root<InvestorBankOrderEntity> root, CriteriaQuery<?> query,
				CriteriaBuilder cb) {
			return cb.and(cb.equal(root.get("investorBaseAccount").get("oid").as(String.class), userOid));
		}
		};
		spec = Specifications.where(spec).and(specUserOid);
		Pageable pageable = new PageRequest(page - 1, rows, new Sort(new Order(sortDirection, sort)));
		PageResp<MyBankOrderRep> rep = this.investorBankOrderService.myquery(spec, pageable);
		return new ResponseEntity<PageResp<MyBankOrderRep>>(rep, HttpStatus.OK);
	}

	/**
	 * 已现金给用户发红包
	 * 
	 * @param uoid
	 * @return
	 */
	@RequestMapping(value = "redenvelope", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<BaseResp> redEnvelope(@Valid @RequestBody BankOrderRedReq req) {

		BaseResp rep = this.investorBankOrderService.redEnvelope(req);
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
	
	/**
	 * 用户使用红包
	 * 
	 * @param uoid
	 * @return
	 */
	@RequestMapping(value = "receiveredenvelope", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<BaseResp> receiveRedEnvelope(@RequestParam(required = true) String couponId) {

		BaseResp rep = this.investorBankOrderService.redEnvelope(couponId, this.getLoginUser());
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
	/**
	 * 充值+绑卡(认证支付)
	 * @param bankOrderReq
	 * @return
	 */
	@RequestMapping(value = "depositapply", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntity<BaseResp> depositapply(@RequestBody @Valid DepositApplyReq depositApplyReq) {
		String uid = depositApplyReq.getInvestorOid(); //获取当前用户uid
		InvestorBaseAccountEntity ibae = baseAccountService.getBaseAccountEntity(uid);//根据uid获取姓名和身份证号
		log.info("ibea:{}",ibae);
		//绑卡申请请求包组装
		BindBankCardApplyReq bbcaReq = new BindBankCardApplyReq();
		bbcaReq.setRealName(ibae.getRealName());
		bbcaReq.setCertificateNo(ibae.getIdNum());
		bbcaReq.setBankName(depositApplyReq.getBankName());
		bbcaReq.setCardNo(depositApplyReq.getCardNo());
		bbcaReq.setPhone(depositApplyReq.getPhone());
		log.info("bbcaReq:{}",bbcaReq);
		bankService.bindApply(bbcaReq, uid);//绑卡申请
		//预支付请求包组装
		ApplyReq aReq = new ApplyReq();
		aReq.setOrderAmount(depositApplyReq.getOrderAmount());
		log.info("aReq:{}",aReq);
		BaseResp rep = investorDepositApplyService.depositApply(aReq, uid);
		log.info("rep:{}",rep);
		if(rep.getErrorCode() == 0){
			bankService.payAndAdd(uid);
		}
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
	/**
	 * 先锋支付(认证支付)重发短信验证码
	 * @param bankOrderReq
	 * @return
	 */
	@RequestMapping(value = "resendsms", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntity<BaseResp> resendSms(@RequestBody @Valid ResendSmsReq req) {
		/*String uid = this.getLoginUser();
		BaseResp rep = investorBankOrderService.resms(uid,req);*/
		BaseResp rep =  investorBankOrderService.resms(req.getInvestorOid(),req);
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
}
