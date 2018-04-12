package com.guohuai.mmp.investor.baseaccount.refer.details;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.guohuai.basic.component.ext.web.BaseController;
import com.guohuai.basic.component.ext.web.PageResp;

/**
 * 我的推荐列表查询
 * 
 * @author wanglei
 */
@RestController
@RequestMapping(value = "/mimosa/client/investor/baseaccount/referdetail", produces = "application/json")
public class InvestoRefErDetailsClientController extends BaseController {
	
	
	@Autowired
	InvestoRefErDetailsService investoRefErDetailsService;

	@RequestMapping(value = "referlist", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<PageResp<InvestoRefErDetailsRep>> referlist(@RequestParam int page, @RequestParam int rows) {

		final String uid = this.getLoginUser();
		page = page < 1 ? 1 : page;
		rows = rows < 1 ? 1 : rows;

		Specification<InvestoRefErDetailsEntity> spec = new Specification<InvestoRefErDetailsEntity>() {
			@Override
			public Predicate toPredicate(Root<InvestoRefErDetailsEntity> root, CriteriaQuery<?> query,
					CriteriaBuilder cb) {
				return cb.and(cb
						.equal(root.get("investorRefEree").get("investorBaseAccount").get("oid").as(String.class), uid)); // 投资者ID
			}
		};
		Pageable pageable = new PageRequest(page - 1, rows, new Sort(new Order(Direction.DESC, "createTime")));
		PageResp<InvestoRefErDetailsRep> pages = this.investoRefErDetailsService.referlist(spec,pageable);
		return new ResponseEntity<PageResp<InvestoRefErDetailsRep>>(pages, HttpStatus.OK);
	}
	
	/**
	 * 推荐排名统计，前10名
	 * @return
	 */
	@RequestMapping(value = "recomtop10", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<PageResp<InvestoRefErDetailsRankRep>> recommendRankTOP10() {
		
		PageResp<InvestoRefErDetailsRankRep> pages = this.investoRefErDetailsService.recommendRankTOP10();
		
		return new ResponseEntity<PageResp<InvestoRefErDetailsRankRep>>(pages, HttpStatus.OK);
	}

}
