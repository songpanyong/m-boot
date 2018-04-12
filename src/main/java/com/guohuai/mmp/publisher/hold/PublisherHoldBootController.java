package com.guohuai.mmp.publisher.hold;

import java.sql.Timestamp;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
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
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountEntity;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderEntity;
import com.guohuai.mmp.jiajiacai.common.constant.PlanStatus;
import com.guohuai.mmp.jiajiacai.rep.MonthPlanList;
import com.guohuai.mmp.jiajiacai.rep.QueryOnceInvestInfo;
import com.guohuai.mmp.jiajiacai.wishplan.plan.entity.BackPlanInvestEntity;
import com.guohuai.mmp.jiajiacai.wishplan.plan.entity.PlanInvestEntity;
import com.guohuai.mmp.jiajiacai.wishplan.plan.entity.PlanMonthEntity;
import com.guohuai.mmp.jiajiacai.wishplan.plan.entity.PlanProductEntity;
import com.guohuai.mmp.jiajiacai.wishplan.plan.form.PlanFormVO;
import com.guohuai.mmp.jiajiacai.wishplan.plan.service.PlanBaseService;
import com.guohuai.tulip.util.StringUtil;

import net.kaczmarzyk.spring.data.jpa.domain.DateAfterInclusive;
import net.kaczmarzyk.spring.data.jpa.domain.DateBeforeInclusive;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.In;
import net.kaczmarzyk.spring.data.jpa.domain.Null;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;


@RestController
@RequestMapping(value = "/mimosa/boot/holdconfirm", produces = "application/json")
public class PublisherHoldBootController extends BaseController {

	@Autowired
	PublisherHoldService  publisherHoldService;
	
	@Autowired 
	PlanBaseService planService;
	
	@RequestMapping(value = "superMng", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<PageResp<HoldQueryRep>> superMng(HttpServletRequest request,
			@And({@Spec(params = "holdStatus", path = "holdStatus", spec = In.class),
				@Spec(params = "createTimeBegin", path = "createTime", spec = DateAfterInclusive.class),
				@Spec(params = "createTimeEnd", path = "createTime", spec = DateBeforeInclusive.class)}) Specification<PublisherHoldEntity> spec,
			@RequestParam int page, 
			@RequestParam int rows,
			@RequestParam(required = false, defaultValue = "createTime") String sort,
			@RequestParam(required = false, defaultValue = "desc") String order) {
		Direction sortDirection = Direction.DESC;
		if (!"desc".equals(order)) {
			sortDirection = Direction.ASC;
		}
		
		Specification<PublisherHoldEntity> ownerSpec = new Specification<PublisherHoldEntity>() {
			@Override
			public Predicate toPredicate(Root<PublisherHoldEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

				return cb.equal(root.get("investorBaseAccount").get("owner").as(String.class),
						InvestorBaseAccountEntity.BASEACCOUNT_owner_platform);
			}

		};
		spec = Specifications.where(spec).and(ownerSpec);
		
		
		Pageable pageable = new PageRequest(page - 1, rows, new Sort(new Order(sortDirection, sort)));
		PageResp<HoldQueryRep> rep = this.publisherHoldService.holdMng(spec, pageable);
		return new ResponseEntity<PageResp<HoldQueryRep>>(rep, HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "pmng", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<PageResp<HoldQueryRep>> mng(HttpServletRequest request,
			@And({@Spec(params = "holdStatus", path = "holdStatus", spec = Equal.class),
				@Spec(params = "productCode", path = "product.code", spec = Equal.class),
				@Spec(params = "productName", path = "product.name", spec = Equal.class),
				@Spec(params = "productType", path = "product.type.oid", spec = In.class),
				@Spec(params = "accountType", path = "accountType", spec = In.class),
				  @Spec(params = "investorBaseAccountOid", path = "investorBaseAccount.oid", spec = Equal.class),
			     @Spec(path = "wishplanOid" ,spec = Null.class,constVal = "true")
			}) Specification<PublisherHoldEntity> spec,
			@RequestParam(required = false) String phoneNum, 
			@RequestParam int page, 
			@RequestParam int rows,
			@RequestParam(required = false, defaultValue = "createTime") String sort,
			@RequestParam(required = false, defaultValue = "desc") String order) {
		Direction sortDirection = Direction.DESC;
		if (!"desc".equals(order)) {
			sortDirection = Direction.ASC;
		}
		
		if (!StringUtil.isEmpty(phoneNum)) {
			Specification<PublisherHoldEntity> investorPhoneSpec = new Specification<PublisherHoldEntity>() {
				@Override
				public Predicate toPredicate(Root<PublisherHoldEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

					Predicate a = cb.equal(root.get("investorBaseAccount").get("phoneNum").as(String.class),
							phoneNum);
					Predicate b = cb.equal(root.get("publisherBaseAccount").get("phone").as(String.class),
							phoneNum);
					return cb.or(a, b);
				}

			};
			spec = Specifications.where(spec).and(investorPhoneSpec);
		}
		
		
		Pageable pageable = new PageRequest(page - 1, rows, new Sort(new Order(sortDirection, sort)));
		PageResp<HoldQueryRep> rep = this.publisherHoldService.holdMng(spec, pageable);
		return new ResponseEntity<PageResp<HoldQueryRep>>(rep, HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "deta", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<HoldDetailRep> close(@RequestParam(required = true) String holdOid){
		HoldDetailRep detailRep = this.publisherHoldService.detail(holdOid);
		return new ResponseEntity<HoldDetailRep>(detailRep, HttpStatus.OK);
	}
	
	/**
	 * 根据资产池和资产池对应的hold
	 * @param assetPoolOid
	 * @return
	 */
	@RequestMapping(value = "getHoldByAssetPoolOid", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<HoldDetailRep> getHoldByAssetPoolOid(@RequestParam(required = true) String assetPoolOid){
		HoldDetailRep detailRep = this.publisherHoldService.getHoldByAssetPoolOid(assetPoolOid);
		return new ResponseEntity<HoldDetailRep>(detailRep, HttpStatus.OK);
	}
	
	/**
	 * wishplanMonth
	 */
	@RequestMapping(value = "wishplanMonth", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<PageResp<MonthPlanList>> wishplanMonth(HttpServletRequest request,
			@And({@Spec(params = "investorBaseAccountOid", path = "uid", spec = Equal.class),
				  @Spec(params = "type",path = "planType",spec = Equal.class)
//				  @Spec(params = "investStatus",path = "status",spec = Equal.class)
			}) Specification<PlanMonthEntity> spec,
			@RequestParam int page, 
			@RequestParam int rows,
			@RequestParam(required = false) String status, 
			@RequestParam(required = false, defaultValue = "createTime") String sort,
			@RequestParam(required = false, defaultValue = "desc") String order) {
		Direction sortDirection = Direction.DESC;
		if (!"desc".equals(order)) {
			sortDirection = Direction.ASC;
		}
		
		
		/** 判断status的状态  */
		Specification<PlanMonthEntity> statusSpec = new Specification<PlanMonthEntity>() {

					@Override
					public Predicate toPredicate(Root<PlanMonthEntity> root, CriteriaQuery<?> query,
							CriteriaBuilder cb) {
						// TODO Auto-generated method stub
						Expression<String> statusExp = root.get("status").as(String.class);
						Predicate t =null;
						if(!StringUtil.isEmpty(status) ){
							if(status.equals("PROCESS")){
								t = statusExp.in(new Object[] { PlanStatus.READY.getCode(), PlanStatus.SUCCESS.getCode(),PlanStatus.DEPOSITED.getCode(),PlanStatus.TODEPOSIT.getCode()});
							}else if(status.equals("STOP")){
								t = statusExp.in(new Object[] { PlanStatus.STOP.getCode(),PlanStatus.REDEEMING.getCode()});
							}else if(status.equals("COMPLETE")){
								t = statusExp.in(new Object[]{PlanStatus.COMPLETE.getCode()});
							}
							return cb.or(t);
						}
						return null;
						
					}
				};
			if(!StringUtil.isEmpty(status)){
				spec = Specifications.where(spec).and(statusSpec);
			}
		
		
		Pageable pageable = new PageRequest(page - 1, rows, new Sort(new Order(sortDirection, sort)));
		PageResp<MonthPlanList> rep = planService.getRootOwnerMonthPlanList(spec, pageable);
		return new ResponseEntity<PageResp<MonthPlanList>>(rep, HttpStatus.OK);
	}
	
	/**
	 * wishplanOnce
	 */
	@RequestMapping(value = "wishplanOnce", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<PageResp<QueryOnceInvestInfo>> wishplanOnce(HttpServletRequest request,
			@And({
			  @Spec(params = "investorBaseAccountOid", path = "investBaseAccount.oid", spec = Equal.class),
			  @Spec(path = "monthOid", spec = Null.class, constVal="true"),
			  @Spec(params = "type",path = "planType",spec = Equal.class)
//			  @Spec(params = "status", path = "status",spec = Equal.class)
			}) Specification<BackPlanInvestEntity> spec,
			@RequestParam int page, 
			@RequestParam int rows,
			@RequestParam(required = false) String status, 
			@RequestParam(required = false, defaultValue = "createTime") String sort,
			@RequestParam(required = false, defaultValue = "desc") String order) {
		Direction sortDirection = Direction.DESC;
		if (!"desc".equals(order)) {
			sortDirection = Direction.ASC;
		}
		
		/** 用Specification来增加筛选条件 */
		Specification<BackPlanInvestEntity> statusSpe  = null;
		if (!StringUtil.isEmpty(status)) {
			if (status.equals("PROCESS")) {
				statusSpe = new Specification<BackPlanInvestEntity>() {

					@Override
					public Predicate toPredicate(Root<BackPlanInvestEntity> root, CriteriaQuery<?> query,
							CriteriaBuilder cb) {
						// TODO Auto-generated method stub
						Expression<String> statusExp = root.get("status").as(String.class);
						Predicate t = statusExp.in(new Object[] { PlanStatus.SUCCESS.getCode(), PlanStatus.READY.getCode(),PlanStatus.DEPOSITED.getCode(),PlanStatus.TODEPOSIT.getCode() });

						return cb.or(t);
					}
				};
			} else if (status.equals("COMPLETE")) { 
			statusSpe = new Specification<BackPlanInvestEntity>(){

				@Override
				public Predicate toPredicate(Root<BackPlanInvestEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
					// TODO Auto-generated method stub
					Expression<String> statusExp = root.get("status").as(String.class);
					Predicate t = statusExp.in(new Object[]{PlanStatus.COMPLETE.getCode()});
					return cb.or(t);
				}};
			}else if(status.equals("STOP")){
				statusSpe = new Specification<BackPlanInvestEntity>(){

					@Override
					public Predicate toPredicate(Root<BackPlanInvestEntity> root, CriteriaQuery<?> query,
							CriteriaBuilder cb) {
						Expression<String> statusExp = root.get("status").as(String.class);
						Predicate t = statusExp.in(new Object[]{PlanStatus.STOP.getCode(),PlanStatus.REDEEMING.getCode()});
						return cb.or(t) ;
					}
					
				};
			}
			
		}
		
		if (statusSpe != null) {
			spec = Specifications.where(spec).and(statusSpe);
		}

		Pageable pageable = new PageRequest(page - 1, rows, new Sort(new Order(sortDirection, sort)));
		PageResp<QueryOnceInvestInfo> rep = planService.getRootOwnerOncePlanList(spec, pageable);
		return new ResponseEntity<PageResp<QueryOnceInvestInfo>>(rep, HttpStatus.OK);
	}
	
	/**
	 * 后台接口记录月定投的划扣详情
	 * 
	 * */
	@RequestMapping(value = "querymonthdeductinfo" ,method =RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<MonthPageResp<BackMonthDeductInfo>> queryMonthDedcutInfo(@RequestParam(required = true) String planOid,@RequestParam int page, 
			@RequestParam int rows){
		
		MonthPageResp<BackMonthDeductInfo> rep = this.planService.queryMonthDeductInfo(planOid,page,rows);
		return new ResponseEntity<MonthPageResp<BackMonthDeductInfo>>(rep , HttpStatus.OK);
	}
	
	/**
	 * 后台展示心愿计划关联产品的详情展示
	 * 
	 * */
	@RequestMapping(value="planrelateproduct" , method =RequestMethod.POST)
	public ResponseEntity<PageResp<PlanRelateProductInfo>> queryPlanRelateProduct(@RequestParam(required = true) String planOid,@RequestParam int page,@RequestParam int rows){

			PageResp<PlanRelateProductInfo> rep = this.planService.queryPlanRelateProduct(planOid,page,rows);
		return new ResponseEntity<PageResp<PlanRelateProductInfo>>(rep,HttpStatus.OK);
	}
	
	/**
	 * 后台展示心愿计划的收益明细
	 * 
	 * */
	@RequestMapping(value = "planincome", method = RequestMethod.POST)
	public ResponseEntity<PlanPageResp<PlanIncomeInfo>> wishplanIncome(
			@RequestParam(required = true) String investorOid, @RequestParam(required = true) int page,
			@RequestParam(required = true) int rows, @RequestParam(required = false) String planBatch,
			@RequestParam(required = false) String planName, @RequestParam(required = false) Timestamp investTimeStart,
			@RequestParam(required = false) Timestamp investTimeEnd) {
		PlanPageResp<PlanIncomeInfo> resp = this.planService.wishplanIncome(investorOid, page, rows, planBatch,
				planName, investTimeStart, investTimeEnd);
		return new ResponseEntity<>(resp, HttpStatus.OK);
	}
	
}
