
package com.guohuai.ams.illiquidAsset.repaymentPlan;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.guohuai.ams.illiquidAsset.IlliquidAsset;
import com.guohuai.ams.illiquidAsset.IlliquidAssetSetupForm;
import com.guohuai.basic.component.ext.web.BaseController;
import com.guohuai.basic.component.repayment.RepaymentPlan;
import com.guohuai.basic.component.repayment.mode.MonthDaysMode;
import com.guohuai.basic.component.repayment.rate.YearYieldRate;
import com.guohuai.component.web.view.Response;

import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;

@RestController
@RequestMapping(value = "/mimosa/illiquidAsset/plan", produces = "application/json;charset=UTF-8")
public class IlliquidAssetRepaymentPlanController extends BaseController {
	@Autowired
	private IlliquidAssetRepaymentPlanService illiquidAssetRepaymentPlanService;

	@RequestMapping(name = "查询底层项目", value = "planList", method = { RequestMethod.POST, RequestMethod.GET })
	public @ResponseBody ResponseEntity<IlliquidAssetRepaymentPlanListResp> projectlist(HttpServletRequest request,
			@And({ @Spec(params = "targetOid", path = "illiquidAsset.oid", spec = Equal.class)}) 
		Specification<IlliquidAssetRepaymentPlan> spec,
			@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "50") int size,
			@RequestParam(defaultValue = "createTime") String sortField,
			@RequestParam(required = false, defaultValue = "desc") String sort) {
		Direction sortDirection = Direction.DESC;
		if (!"desc".equals(sort)) {
			sortDirection = Direction.ASC;
		}
		if (page < 1) {
			page = 1;
		}
		if (size <= 0) {
			size = 50;
		}
		
		Pageable pageable = new PageRequest(page - 1, size, new Sort(new Order(sortDirection, sortField)));
		
		IlliquidAssetRepaymentPlanListResp resp = illiquidAssetRepaymentPlanService.queryPage(spec, pageable);
		
		return new ResponseEntity<IlliquidAssetRepaymentPlanListResp>(resp, HttpStatus.OK);
	}
	
	@RequestMapping(name = "还款计划", value = "changePlan", method = { RequestMethod.POST, RequestMethod.GET })
	public @ResponseBody ResponseEntity<List<RepaymentPlan.Plan>> planlist(@Validated IlliquidAssetRepaymentPlanForm form) throws Exception{
		
		// List<RepaymentPlan.Plan> plans = RepaymentPlan.aDebtServiceDue(startDate, endDate, principal, rate);
		
		List<RepaymentPlan.Plan> r = null;
		switch (form.getAccrualType()) {
		case IlliquidAsset.PAYMENT_METHOD_A_DEBT_SERVICE_DUE:
			r = RepaymentPlan.aDebtServiceDue(form.getStartDate(), form.getEndDate(), form.getRaiseScope(), new YearYieldRate(form.getExpAror(), form.getContractDays()));
			break;
		case IlliquidAsset.PAYMENT_METHOD_EACH_INTEREST_RINCIPAL_DUE:
			r = RepaymentPlan.eachInterestRincipalDue(form.getStartDate(), form.getEndDate(), form.getRaiseScope(), new YearYieldRate(form.getExpAror(), form.getContractDays()), form.getAccrualDate(), form.getContractDays() == 365 ? MonthDaysMode.NATURAL_DAYS : MonthDaysMode.FIXED_30_DAYS);
			break;
		case IlliquidAsset.PAYMENT_METHOD_FIXED_BASIS_MORTGAGE:
			r=RepaymentPlan.fixedBasisMortgage(form.getStartDate(), form.getEndDate(), form.getRaiseScope(), new YearYieldRate(form.getExpAror(), form.getContractDays()), form.getAccrualDate(), form.getContractDays() == 365 ? MonthDaysMode.NATURAL_DAYS : MonthDaysMode.FIXED_30_DAYS);
			break;
		case IlliquidAsset.PAYMENT_METHOD_FIXED_PAYMENT_MORTGAGE:
			r=RepaymentPlan.fixedPaymentMortgage(form.getStartDate(), form.getEndDate(), form.getRaiseScope(), new YearYieldRate(form.getExpAror(), form.getContractDays()), form.getAccrualDate(), form.getContractDays() == 365 ? MonthDaysMode.NATURAL_DAYS : MonthDaysMode.FIXED_30_DAYS);
			break;
		default:
			r = new ArrayList<RepaymentPlan.Plan>();
			break;
		}
		
		
		return new ResponseEntity<List<RepaymentPlan.Plan>>(r, HttpStatus.OK);
	}
	

	@RequestMapping(name = "还款计划", value = "savePlan", method = { RequestMethod.POST, RequestMethod.GET })
	public @ResponseBody ResponseEntity<Response> savePlan(@RequestBody IlliquidAssetSetupForm form) throws Exception{
	
		this.illiquidAssetRepaymentPlanService.savePlan(form, super.getLoginUser());
		Response r = new Response();
		r.with("result", "SUCCESSED!");
		return new ResponseEntity<Response>(r, HttpStatus.OK);
	}
	
}
