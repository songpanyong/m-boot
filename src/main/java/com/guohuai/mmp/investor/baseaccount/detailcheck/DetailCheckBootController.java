package com.guohuai.mmp.investor.baseaccount.detailcheck;

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
@RequestMapping(value = "/mimosa/boot/investor/baseaccount/detailcheck", produces = "application/json")
@Slf4j
public class DetailCheckBootController extends BaseController {

	@Autowired
	private DetailCheckService detailCheckService;
	

	@RequestMapping(value = "query", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<PageResp<DetailCheckQueryRep>> channelQuery(HttpServletRequest request,
			@And({ @Spec(params = "phone", path = "investorBaseAccount.phoneNum", spec = Like.class),
				@Spec(params = "checkStatus", path = "checkStatus", spec = Like.class)
			}) Specification<DetailCheckEntity> spec,
			@RequestParam int page,
			@RequestParam int rows) {
		
		final String checkTime = this.detailCheckService.getMaxCheckTime();
		
		Specification<DetailCheckEntity> sc = new Specification<DetailCheckEntity>() {
			@Override
			public Predicate toPredicate(Root<DetailCheckEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.equal(root.get("checkTime").as(String.class), checkTime);
			}
		};
		spec = Specifications.where(spec).and(sc);
		
		Pageable pageable = new PageRequest(page - 1, rows);
		PageResp<DetailCheckQueryRep> rep = this.detailCheckService.detailCheckQuery(spec, pageable);

		return new ResponseEntity<PageResp<DetailCheckQueryRep>>(rep, HttpStatus.OK);
	}

	@RequestMapping(value = "generate", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<BaseResp> generateDetailCheck() {
		String checkTime = DateUtil.getCurrStrDate();
		
		log.info("账户明细对账，生成日期：{}", checkTime);
		BaseResp rep =  this.detailCheckService.generateDetailCheck(checkTime);

		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
	/**
	 * 单个重置对账
	 * @param detailOid
	 * @return
	 */
	@RequestMapping(value = "singlegenerate", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<BaseResp> singleGenerateDetailCheck(@RequestParam(required = true) String detailOid) {
		
		BaseResp rep =  this.detailCheckService.singleCheck(detailOid);

		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
}
