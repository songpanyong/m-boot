package com.guohuai.mmp.investor.cashflow;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.guohuai.basic.component.ext.web.BaseController;
import com.guohuai.basic.component.ext.web.PageResp;
import com.guohuai.component.util.DateUtil;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountService;
import com.guohuai.tulip.util.StringUtil;

import net.kaczmarzyk.spring.data.jpa.domain.DateAfterInclusive;
import net.kaczmarzyk.spring.data.jpa.domain.DateBeforeInclusive;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.In;
import net.kaczmarzyk.spring.data.jpa.domain.Like;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;

@RestController
@RequestMapping(value = "/mimosa/boot/platform/investor/cashflow", produces = "application/json")
public class InvestorCashFlowBootController extends BaseController {
	
	@Autowired
	InvestorCashFlowService investorCashFlowService;
	@Autowired
	private InvestorBaseAccountService investorBaseAccountService;
	
	/**
	 * 查询
	 */
	@RequestMapping(value = "query", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<PageResp<InvestorCashFlowQueryRep>> query(HttpServletRequest request,
			//@And({@Spec(params = "tradeType", path = "tradeType", spec = In.class),
				//@Spec(params = "investorBaseAccountOid", path = "investorBaseAccount.oid", spec = Equal.class),
			@And({@Spec(params = "createTimeBegin", path = "createTime", spec = DateAfterInclusive.class, config = DateUtil.fullDatePattern),
					@Spec(params = "createTimeEnd", path = "createTime", spec = DateBeforeInclusive.class, config = DateUtil.fullDatePattern)}) 
		Specification<InvestorCashFlowEntity> spec,
		@RequestParam int page, 
		@RequestParam int rows,
		@RequestParam(required = false, defaultValue = "createTime") String sort,
		@RequestParam(required = false, defaultValue = "desc") String order,
		@RequestParam(required =true) String investorBaseAccountOid,
		@RequestParam String tradeType) {
		Direction sortDirection = Direction.DESC;
		if (!"desc".equals(order)) {
			sortDirection = Direction.ASC;
		}
		
		/** 判断用户交易类型 中的转账*/
		if(!StringUtil.isEmpty(tradeType)){
			if(tradeType.equals("transfer")){
				//判断传递过来的id是主账户还是子账户
				if(this.investorBaseAccountService.judgeByOid(investorBaseAccountOid)){
					//主账户
					Specification<InvestorCashFlowEntity> specType = new Specification<InvestorCashFlowEntity>(){

						@Override
						public Predicate toPredicate(Root<InvestorCashFlowEntity> root, CriteriaQuery<?> query,
								CriteriaBuilder cb) {						
							return cb.equal(root.get("tradeType").as(String.class), "rollOut");
						}};
						spec = Specifications.where(spec).and(specType);
				}else{
					//子账户
					Specification<InvestorCashFlowEntity> specType = new Specification<InvestorCashFlowEntity>(){

						@Override
						public Predicate toPredicate(Root<InvestorCashFlowEntity> root, CriteriaQuery<?> query,
								CriteriaBuilder cb) {						
							return cb.equal(root.get("tradeType").as(String.class), "rollIn");
						}};
						spec = Specifications.where(spec).and(specType);
				}				
			}else{		
				Specification<InvestorCashFlowEntity> specType = new Specification<InvestorCashFlowEntity>(){

					@Override
					public Predicate toPredicate(Root<InvestorCashFlowEntity> root, CriteriaQuery<?> query,
							CriteriaBuilder cb) {				
						return cb.equal(root.get("tradeType").as(String.class), tradeType);
					}};
					spec = Specifications.where(spec).and(specType);					
			}			
		}
		
		Specification<InvestorCashFlowEntity> specOid = new Specification<InvestorCashFlowEntity>(){

			@Override
			public Predicate toPredicate(Root<InvestorCashFlowEntity> root, CriteriaQuery<?> query,
					CriteriaBuilder cb) {
				// TODO Auto-generated method stub
				return cb.equal(root.get("investorBaseAccount").get("oid").as(String.class), investorBaseAccountOid);
			}};
		spec = Specifications.where(spec).and(specOid);
		
		Pageable pageable = new PageRequest(page - 1, rows, new Sort(new Order(sortDirection, sort)));
		PageResp<InvestorCashFlowQueryRep> rep = this.investorCashFlowService.query(spec, pageable);
		
		return new ResponseEntity<PageResp<InvestorCashFlowQueryRep>>(rep, HttpStatus.OK);
	}
	
	/**
	 * 对账查询
	 * @param request
	 * @param spec
	 * @param page
	 * @param rows
	 * @param sort
	 * @param order
	 * @return
	 */
	@RequestMapping(value = "query4recorrect", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<PageResp<InvestorCashFlow4IncorrectQueryRep>> recorrectQuery(HttpServletRequest request,
			@And({@Spec(params = "investOid", path = "investorBaseAccount.oid", spec = Equal.class),
//				@Spec(params = "direction", path = "tradeType", spec = Like.class),
				@Spec(params = "tradeType", path = "tradeType", spec = Like.class),
//				@Spec(params = "doCheckType", path = "tradeType", spec = Like.class),
					@Spec(params = "createTimeBegin", path = "createTime", spec = DateAfterInclusive.class, config = DateUtil.fullDatePattern),
					@Spec(params = "createTimeEnd", path = "createTime", spec = DateBeforeInclusive.class, config = DateUtil.fullDatePattern)}) 
		Specification<InvestorCashFlowEntity> spec,
		@RequestParam int page, 
		@RequestParam int rows,
		@RequestParam(required = false, defaultValue = "createTime") String sort,
		@RequestParam(required = false, defaultValue = "desc") String order) {
		Direction sortDirection = Direction.DESC;
		if (!"desc".equals(order)) {
			sortDirection = Direction.ASC;
		}
		Pageable pageable = new PageRequest(page - 1, rows, new Sort(new Order(sortDirection, sort)));
		PageResp<InvestorCashFlow4IncorrectQueryRep> rep = this.investorCashFlowService.query4Incorrect(spec, pageable);
		
		return new ResponseEntity<PageResp<InvestorCashFlow4IncorrectQueryRep>>(rep, HttpStatus.OK);
	}
	
	
}
