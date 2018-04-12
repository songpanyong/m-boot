package com.guohuai.mmp.investor.baseaccount;

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
import com.guohuai.component.util.CheckUtil;
import com.guohuai.component.util.DateUtil;

import lombok.extern.slf4j.Slf4j;
import net.kaczmarzyk.spring.data.jpa.domain.DateAfterInclusive;
import net.kaczmarzyk.spring.data.jpa.domain.DateBeforeInclusive;
import net.kaczmarzyk.spring.data.jpa.domain.In;
import net.kaczmarzyk.spring.data.jpa.domain.Like;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;

@RestController
@RequestMapping(value = "/mimosa/boot/investor/baseaccount", produces = "application/json")
@Slf4j
public class InvestorBaseAccountBootController extends BaseController {

	@Autowired
	private InvestorBaseAccountService investorBaseAccountService;
	

	@RequestMapping(value = "query", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<PageResp<InvestorBaseAccountQueryRep>> channelQuery(HttpServletRequest request,
			@And({ @Spec(params = "phoneNum", path = "phoneNum", spec = Like.class),
					@Spec(params = "status", path = "status", spec = In.class),
					@Spec(params = "owner", path = "owner", spec = In.class),
					@Spec(params = "reqTimeBegin", path = "createTime", spec = DateAfterInclusive.class, config = DateUtil.fullDatePattern),
					@Spec(params = "reqTimeEnd", path = "createTime", spec = DateBeforeInclusive.class, config = DateUtil.fullDatePattern) }) Specification<InvestorBaseAccountEntity> spec,
			@RequestParam int page, @RequestParam int rows) {
		
		Specification<InvestorBaseAccountEntity> specDeleteSon = new Specification<InvestorBaseAccountEntity>(){

			@Override
			public Predicate toPredicate(Root<InvestorBaseAccountEntity> root, CriteriaQuery<?> query,
					CriteriaBuilder cb) {
				
				return cb.equal(cb.length(root.get("phoneNum")),11);
			}};
		spec = Specifications.where(spec).and(specDeleteSon);
			
		Pageable pageable = new PageRequest(page - 1, rows, new Sort(new Order(Direction.DESC, "createTime")));
		PageResp<InvestorBaseAccountQueryRep> rep = this.investorBaseAccountService.accountQuery(spec, pageable);

		return new ResponseEntity<PageResp<InvestorBaseAccountQueryRep>>(rep, HttpStatus.OK);
	}
	
	/**
	 * 注册(后台注册使用)
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "regist", method =  RequestMethod.POST )
	@ResponseBody
	public ResponseEntity<BaseResp> regist(@Valid @RequestBody InvestorBaseAccountAddReq req) {
		CheckUtil.isMobileNO(req.getUserAcc(), false, 0, 80027);
		BaseResp rep = this.investorBaseAccountService.addBaseAccount(req, false);
		// 登陆
		super.setLoginUser(req.getInvestorOid(), new String[]{req.getPlatform()});
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
	/**
	 * SINA资金明细
	 */
//	@RequestMapping(value = "qsinadetails", method = {RequestMethod.POST, RequestMethod.GET})
//	@ResponseBody
//	public ResponseEntity<QueryAccountDetailsResp> queryAccountDetails(@Valid QueryAccountDetailsReq req) {
//		QueryAccountDetailsResp rep = investorBaseAccountService.queryAccountDetails(req);
//
//		return new ResponseEntity<QueryAccountDetailsResp>(rep, HttpStatus.OK);
//	}
	
	/**
	 *超级用户信息 
	 */
	@RequestMapping(value = "sman", method = {RequestMethod.POST, RequestMethod.GET})
	@ResponseBody
	public ResponseEntity<BaseAccountRep> supermanInfo() {
		this.getLoginUser();
		BaseAccountRep rep = this.investorBaseAccountService.supermanInfo();
		return new ResponseEntity<BaseAccountRep>(rep, HttpStatus.OK);
	}
	
	/**
	 * 修改注册手机号
	 * 
	 * @param uid
	 * @param newpn
	 * @return
	 */
	@RequestMapping(value = "changeacc", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<BaseResp> changeAcc(@RequestParam(required = true) String investorOid, 
			@RequestParam(required = true) String oldpn, @RequestParam(required = true) String newpn) {
		log.info("investorOid={}, oldpn={}, newpn={}", investorOid, oldpn, newpn);
		BaseResp rep = this.investorBaseAccountService.changeAcc(investorOid, oldpn, newpn);
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
	/**
	 * 锁定与解锁用户
	 * 
	 * @param uid
	 * @param islock
	 *            is/not
	 * @return
	 */
	@RequestMapping(value = "lockuser", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<BaseResp> lockUser(@RequestParam(required = true) String uoid,
			@RequestParam(required = true) String islock) {

		BaseResp rep = this.investorBaseAccountService.lockUser(uoid, islock);
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}

	/**
	 * 用户信息
	 * 
	 * @param uid
	 * @return
	 */
	@RequestMapping(value = "userinfo", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<InvestorBaseAccountInfoRep> userInfo(@RequestParam(required = true) String uoid) {

		InvestorBaseAccountInfoRep rep = this.investorBaseAccountService.getUserInfo(uoid);
		return new ResponseEntity<InvestorBaseAccountInfoRep>(rep, HttpStatus.OK);
	}

	/**
	 * 解锁登录锁定
	 * @param investorOid 用户OID
	 * @return
	 */
	@RequestMapping(value = "/cancelloginlock", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<BaseResp> cancelLoginLock(@RequestParam(required = true) String investorOid) {
		
		this.investorBaseAccountService.cancelLoginLock(investorOid);
		
		return new ResponseEntity<BaseResp>(new BaseResp(), HttpStatus.OK);
	}
	
	/**
	 * 获取用户资金信息
	 * 
	 * @param uoid
	 * @return
	 */
	@RequestMapping(value = "cashuserinfo", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<BaseAccountRep> getCashUserinfo(@RequestParam(required = true) String uoid) {

		BaseAccountRep rep = this.investorBaseAccountService.userInfoPc(uoid);
		return new ResponseEntity<BaseAccountRep>(rep, HttpStatus.OK);
	}
	
	/**
	 * 更改用户手机号(UUID)，仅测试使用
	 * 
	 * @param phoneNum
	 * @return
	 */
	@RequestMapping(value = "chanphone", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<BaseResp> changePhone(@RequestParam(required = true) String phoneNum) {

		this.investorBaseAccountService.changeAccPhoneNum(phoneNum);
		return new ResponseEntity<BaseResp>(new BaseResp(), HttpStatus.OK);
	}
	
	/**
	 * 投资者同步余额
	 */
	@RequestMapping(value = "ub", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<BaseResp> updateBalance(@RequestParam(required = true) String investorOid) {
		
		BaseResp rep = this.investorBaseAccountService.updateBalance(this.investorBaseAccountService.findOne(investorOid));
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
	/**
	 * 删除账号，只删除注册时候产生的数据（充值，提现不删除，谨慎操作），仅测试使用
	 * 
	 * @param phoneNum
	 * @return
	 */
	@RequestMapping(value = "delacc", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<BaseResp> delAccount(@RequestParam(required = true) String phoneNum) {

		BaseResp rep = this.investorBaseAccountService.delAccount(phoneNum);
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
}
