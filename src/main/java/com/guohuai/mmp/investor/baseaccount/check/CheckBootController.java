package com.guohuai.mmp.investor.baseaccount.check;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.guohuai.basic.component.ext.web.BaseController;
import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.basic.component.ext.web.PageResp;
import com.guohuai.component.util.DateUtil;

import lombok.extern.slf4j.Slf4j;
import net.kaczmarzyk.spring.data.jpa.domain.Like;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;

@RestController
@RequestMapping(value = "/mimosa/boot/investor/baseaccount/check", produces = "application/json")
@Slf4j
public class CheckBootController extends BaseController {

	@Autowired
	private CheckService checkService;

	@RequestMapping(value = "query", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<PageResp<CheckQueryRep>> channelQuery(HttpServletRequest request,
			@And({ @Spec(params = "phone", path = "investorBaseAccount.phoneNum", spec = Like.class),
				@Spec(params = "checkStatus", path = "checkStatus", spec = Like.class),
				@Spec(params = "userStatus", path = "userStatus", spec = Like.class)
			}) Specification<CheckEntity> spec,
			@RequestParam int page, 
			@RequestParam int rows) {
		
		final String checkTime = this.checkService.getMaxCheckTime();
		
		Specification<CheckEntity> sc = new Specification<CheckEntity>() {
			@Override
			public Predicate toPredicate(Root<CheckEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.equal(root.get("checkTime").as(String.class), checkTime);
			}
		};
		spec = Specifications.where(spec).and(sc);
		
		Pageable pageable = new PageRequest(page - 1, rows);
		PageResp<CheckQueryRep> rep = this.checkService.checkQuery(spec, pageable);

		return new ResponseEntity<PageResp<CheckQueryRep>>(rep, HttpStatus.OK);
	}
	
	@RequestMapping(value = "generate", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<BaseResp> generateCheckOrders() {
		String currentCheckTime = DateUtil.getCurrStrDate();
		log.info("账户总额对账记录，生成日期：{}。", currentCheckTime);
		BaseResp rep =  this.checkService.generateCheckOrders(currentCheckTime);

		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
	@RequestMapping(value = "singlegenerate", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<BaseResp> singleCheck(@RequestParam(required = true) String checkOid) {
		String currentCheckTime = DateUtil.getCurrStrDate();
		log.info("重新账户总额对账，对账记录：{}，生成日期：{}。", checkOid, currentCheckTime);
		BaseResp rep =  this.checkService.singleGenerate(checkOid);

		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
	/**
	 * 获取平台累计金额
	 * @param checkTimeEnd
	 * @return
	 */
	@RequestMapping(value = "getplatsumamt", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<CheckSumAmtRep> getPlatformSumAmt() {
		
		String checkTime = this.checkService.getMaxCheckTime();
		
		CheckSumAmtRep rep = this.checkService.getPlatformSumAmt(checkTime);

		return new ResponseEntity<CheckSumAmtRep>(rep, HttpStatus.OK);
	}

	@RequestMapping(value = "unlock", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<BaseResp> unlockUser(@RequestParam(required = true) String checkOid) {
		
		this.checkService.uptCheckUserStatus(checkOid);		
		return new ResponseEntity<BaseResp>(new BaseResp(), HttpStatus.OK);
	}
	
}
