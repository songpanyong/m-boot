package com.guohuai.mmp.jiajiacai.wishplan.plan;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.guohuai.account.api.request.RedeemToBasicRequest;
import com.guohuai.basic.component.ext.web.BaseController;
import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.DateUtil;
import com.guohuai.component.util.StringUtil;
import com.guohuai.component.web.view.RowsRep;
import com.guohuai.mmp.investor.tradeorder.InvestorInvestTradeOrderExtService;
import com.guohuai.mmp.investor.tradeorder.TradeOrderRep;
import com.guohuai.mmp.jiajiacai.caculate.JJCUtility;
import com.guohuai.mmp.jiajiacai.common.constant.InvestTypeEnum;
import com.guohuai.mmp.jiajiacai.common.constant.PlanStatus;
import com.guohuai.mmp.jiajiacai.common.exception.BaseException;
import com.guohuai.mmp.jiajiacai.common.exception.ErrorMessage;
import com.guohuai.mmp.jiajiacai.common.service.ThirdNotifyService;
import com.guohuai.mmp.jiajiacai.ebaoquan.EbaoquanRecord;
import com.guohuai.mmp.jiajiacai.ebaoquan.EbaoquanRecordService;
import com.guohuai.mmp.jiajiacai.rep.JudgeJoinSalaryPlanRep;
import com.guohuai.mmp.jiajiacai.rep.MonthLogRep;
import com.guohuai.mmp.jiajiacai.rep.StopMonthInvestRep;
import com.guohuai.mmp.jiajiacai.rep.UpdateMonthInvestRep;
import com.guohuai.mmp.jiajiacai.wishplan.plan.entity.PlanInvestEntity;
import com.guohuai.mmp.jiajiacai.wishplan.plan.entity.PlanMonthEntity;
import com.guohuai.mmp.jiajiacai.wishplan.plan.entity.PlanProductEntity;
import com.guohuai.mmp.jiajiacai.wishplan.plan.form.EducationInvestReq;
import com.guohuai.mmp.jiajiacai.wishplan.plan.form.FixedPlanForm;
import com.guohuai.mmp.jiajiacai.wishplan.plan.form.MonthlyInvestForm;
import com.guohuai.mmp.jiajiacai.wishplan.plan.form.PlanDepositForm;
import com.guohuai.mmp.jiajiacai.wishplan.plan.form.PlanProductForm;
import com.guohuai.mmp.jiajiacai.wishplan.plan.form.PlanProfitListForm;
import com.guohuai.mmp.jiajiacai.wishplan.plan.rep.MonthPlanByOidRep;
import com.guohuai.mmp.jiajiacai.wishplan.plan.rep.PlanByOidRep;
import com.guohuai.mmp.jiajiacai.wishplan.plan.rep.PlanListRep;
import com.guohuai.mmp.jiajiacai.wishplan.plan.service.IncomeBalanceService;
import com.guohuai.mmp.jiajiacai.wishplan.plan.service.OnePlanScheduleService;
import com.guohuai.mmp.jiajiacai.wishplan.plan.service.PlanBaseService;
import com.guohuai.mmp.jiajiacai.wishplan.plan.service.PlanInvestService;
import com.guohuai.mmp.jiajiacai.wishplan.plan.service.PlanMonthScheduleService;
import com.guohuai.mmp.jiajiacai.wishplan.plan.service.PlanMonthService;
import com.guohuai.mmp.jiajiacai.wishplan.plan.service.PlanProductRedeemService;
import com.guohuai.mmp.jiajiacai.wishplan.plan.service.PlanProductService;
import com.guohuai.mmp.jiajiacai.wishplan.planlist.PlanListEntity;
import com.guohuai.mmp.jiajiacai.wishplan.planlist.PlanListService;
import com.guohuai.mmp.jiajiacai.wishplan.product.JJCProductService;
import com.guohuai.mmp.jiajiacai.wishplan.product.form.JJCProductRate;
import com.guohuai.mmp.jiajiacai.wishplan.question.InvestMessageForm;
import com.guohuai.mmp.platform.accment.Accment;
import com.guohuai.mmp.platform.msgment.MsgParam;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Api(value = "心愿计划 - 支付")
@RestController
@EnableAutoConfiguration
@RequestMapping(value = "/mimosa/wishplan/invest", produces = "application/json")
@Slf4j
public class PlanController extends BaseController {

	@Autowired
	private PlanProductService planService;

	@Autowired
	private PlanListService planListService;

	@Autowired
	private JJCProductService productService;

	@Autowired
	InvestorInvestTradeOrderExtService investorInvestTradeOrderExtService;

	@Autowired
	private PlanMonthService planMonthService;

	@Autowired
	private PlanInvestService planInvestService;

	@Autowired
	PlanMonthScheduleService scheduleService;

	@Autowired
	private PlanProductRedeemService planCompleteRedeemService;

	@Autowired
	private Accment accmentService;

	@Autowired
	private OnePlanScheduleService eduTourScheduleOnce;

	@Autowired
	private PlanMonthScheduleService planMothScheduleService;

	@Autowired
	private ThirdNotifyService thirdNotifyService;

	@Autowired
	private PlanBaseService planBaseService;

	@Autowired
	private IncomeBalanceService incomeBalanceService;

	@Autowired
	private PlanProductRedeemService planRedeemService;
	
	@Autowired
	private EbaoquanRecordService baoquanService;
	@ApiOperation(value = "一次性支付", notes = "一次性支付 ")
	@RequestMapping(value = "fixInvest", method = { RequestMethod.POST })
	@ResponseBody
	public ResponseEntity<String> fixedInvest(@Valid @RequestBody FixedPlanForm form) throws BaseException {
		PlanListEntity plan = planListService.getEntityByOid(form.getPlanOid());
		if (plan == null) {
			throw new BaseException(ErrorMessage.PLAN_NOT_EXIST);
		}
		PlanProductEntity rep = planService.investFixed(form);
		String result = "{result : success}";
		return new ResponseEntity<String>(result, HttpStatus.OK);
	}

	@ApiOperation(value = "旅游计划实施", notes = "一次性支付 ")
	@RequestMapping(value = "planInvest", method = { RequestMethod.POST })
	@ResponseBody
	public ResponseEntity<TradeOrderRep> planInvest(@RequestBody @Valid PlanProductForm tradeOrderReq)
			throws BaseException {
		String uid = this.getLoginUser();
		if (uid != null) {
			tradeOrderReq.setUid(uid);
		}
		if (tradeOrderReq.getUid() == null) {
			throw new BaseException(ErrorMessage.USER_NOT_LOGIN);
		}
		log.info("计划购买开始 uid={}, money={}, productid={}, startTime={}", tradeOrderReq.getUid(),
				tradeOrderReq.getMoneyVolume(), tradeOrderReq.getProductOid(), DateUtil.getSqlCurrentDate());
		PlanListEntity plan = planListService.getEntityByOid(tradeOrderReq.getPlanListOid());
		if (plan == null) {
			throw new BaseException(ErrorMessage.PLAN_NOT_EXIST);
		}
		tradeOrderReq.setInvestDuration(DateUtil.diffDays4Months(tradeOrderReq.getInvestDuration()));
		PlanInvestEntity tourPlan = planInvestService.planInsert(tradeOrderReq, InvestTypeEnum.OnceTourInvest.getCode(),
				null);
		if (tourPlan == null) {
			throw new BaseException(ErrorMessage.PRAMETER_ERROR);
		}
		RedeemToBasicRequest req = new RedeemToBasicRequest();
		req.setBalance(tradeOrderReq.getMoneyVolume());
		req.setUserOid(uid);
		req.setOpposition(true);
		int result = accmentService.redeem2basic(req);
		TradeOrderRep rep = new TradeOrderRep();
		if (result == 0) {
			rep.setExpectedAmount(JJCUtility.keep2Decimal(tourPlan.getExpectedAmount().floatValue()));
			// rep.setExpectedAmount(tourPlan.getExpectedAmount().floatValue());
			rep.setErrorCode(0);
			String tradeOrderOid = planInvestService.depositSucess(tourPlan);
			rep.setTradeOrderOid(tradeOrderOid);
			rep.setStartTime(tourPlan.getCreateTime());
			rep.setEndTime(tourPlan.getEndTime());
			// Call third msg and mail.
			thirdNotifyService.callMsgMail(MsgParam.msgsuccessjoinplanonetime, tourPlan, tourPlan.getUid());
			log.info("旅游计划购买成功 uid={}, planId={}, money={}, endTime={}", tradeOrderReq.getUid(), tourPlan.getOid(),
					tradeOrderReq.getMoneyVolume(), DateUtil.getSqlCurrentDate());
			//Record ebaoquan
			baoquanService.eBaoquanRecord(EbaoquanRecord.EBAOQUAN_TYPE_WISHPLAN_ONCE, tourPlan);
		} else {
			rep.setErrorCode(-1);
			planInvestService.updateStatus(tourPlan.getOid(), PlanStatus.FAILURE.getCode());
			log.info("旅游计划购买失败 uid={}, planId={}, money={}, endTime={}", tradeOrderReq.getUid(), tourPlan.getOid(),
					tradeOrderReq.getMoneyVolume(), DateUtil.getSqlCurrentDate());
		}
		return new ResponseEntity<TradeOrderRep>(rep, HttpStatus.OK);
	}

	@ApiOperation(value = "修改我的心愿计划", notes = "根据计划ID修改我的心愿计划 --（按月定投）")
	@RequestMapping(value = "updatePlan", method = { RequestMethod.POST })
	@ResponseBody
	public ResponseEntity<UpdateMonthInvestRep/* String */> updatePlan(@NotNull @RequestParam("planOid") String planOid,
			@RequestParam("dateNumber") int dateNumber, @RequestParam("amount") BigDecimal amount) {
		UpdateMonthInvestRep rep = new UpdateMonthInvestRep();
		String uid = this.getLoginUser();
		if (uid == null) {
			// throw new BaseException(ErrorMessage.USER_NOT_LOGIN);
			throw new AMPException("当前用户未登录或会话已超时");
		}
		int flag = planMonthService.alterPlan(planOid, dateNumber, amount);
		PlanMonthEntity planMoth = planMonthService.queryByOid(planOid);
		if (flag < 1) {
			rep.setIsUpdate(false);
		} else {

			if (planMoth.getPlanType().equals(InvestTypeEnum.MonthSalaryInvest.getCode())) {
				thirdNotifyService.callMsgMail(MsgParam.msgmodifywageincreasesuccess, planMoth, planMoth.getUid());
			} else {
				thirdNotifyService.callMsgMail(MsgParam.msgmodifyplanbyonemonthsuccess, planMoth, planMoth.getUid());
			}
			rep.setIsUpdate(true);
		}
		// return new ResponseEntity<String>("" + flag, HttpStatus.OK);
		return new ResponseEntity<UpdateMonthInvestRep>(rep, HttpStatus.OK);
	}

	@ApiOperation(value = "终止我的心愿计划", notes = "根据计划ID终止我的心愿计划 --（按月定投） ")
	@RequestMapping(value = "stopPlan", method = { RequestMethod.POST })
	@ResponseBody
	public ResponseEntity<StopMonthInvestRep> stopPlan(@NotNull @RequestParam("planOid") String planOid) {
		StopMonthInvestRep rep = new StopMonthInvestRep();
		int flag = planMonthService.stopPlan(planOid);
		if (flag < 1) {
			rep.setIsStop(false);
		} else {
			PlanMonthEntity planMonth = planMonthService.queryByOid(planOid);

			thirdNotifyService.callMsgMail(MsgParam.msgstopbymonth, planMonth, planMonth.getUid());

			rep.setIsStop(true);
		}

		// return new ResponseEntity<String>("" + flag, HttpStatus.OK);
		return new ResponseEntity<StopMonthInvestRep>(rep, HttpStatus.OK);
	}

	@ApiOperation(value = "查询个人的心愿计划列表", notes = "查询个人心愿计划列表 ")
	@RequestMapping(value = "getOwnerPlanList", method = { RequestMethod.POST })
	@ResponseBody
	public RowsRep<PlanListRep> getOwnerPlanList(@NotNull @RequestParam("planStatus") String planStatus,
			@RequestParam("page") int page, @RequestParam("rows") int rows) throws BaseException {
		String uid = this.getLoginUser();
		if (uid == null) {
			throw new BaseException(ErrorMessage.USER_NOT_LOGIN);
		}

		RowsRep<PlanListRep> rep = planInvestService.getOwnerPlanList(planStatus,page,rows,uid);

		return rep;
	}

	@ApiOperation(value = "根据id查询我的计划", notes = "根据id查询我的计划 ")
	@RequestMapping(value = "getPlanByOid", method = { RequestMethod.POST })
	@ResponseBody
	public PlanByOidRep getPlanByOid(@NotNull @RequestParam("planOid") String planOid) {
		
		PlanByOidRep rep = planInvestService.getPlanByOid(planOid);

		return rep;
	}

	@ApiOperation(value = "保存计划名称", notes = "根据UID保存计划名称 ")
	@RequestMapping(value = "savePlanName", method = { RequestMethod.POST })
	@ResponseBody
	public String savePlanName(@NotNull @RequestParam("uid") String uid, @NotNull @RequestParam("name") String name) {

		String key = getSessionKey(uid);
		session.setAttribute(key, name);
		String result = "{\"status\": \"success\"}";
		return result;
	}

	@ApiOperation(value = "获取计划名称", notes = "根据UID获取计划名称 ")
	@RequestMapping(value = "getPlanName", method = { RequestMethod.POST })
	@ResponseBody
	public String getPlanName(@NotNull @RequestParam("uid") String uid) throws BaseException {

		StringBuffer result = new StringBuffer();
		String key = getSessionKey(uid);
		Object obj = session.getAttribute(key);
		if (obj == null) {
			throw new BaseException(ErrorMessage.PLAN_NOT_EXIST);
		}
		result.append("{ \"name\" : \"").append(obj.toString()).append("\"}");
		return result.toString();
	}

	private String getSessionKey(String uid) {
		return uid + "_Name";
	}

	@ApiOperation(value = "查询是否加入了薪增长计划", notes = "根据UID查询是否加入了薪增长计划 ")
	@RequestMapping(value = "checkSalaryPlan", method = { RequestMethod.POST })
	@ResponseBody
	// public String checkSalaryPlan(@NotNull @RequestParam("uid") String uid)
	// throws BaseException {
	// public ResponseEntity<JudgeJoinSalaryPlanRep> checkSalaryPlan(@NotNull
	// @RequestParam("uid") String uid) {
	public ResponseEntity<JudgeJoinSalaryPlanRep> checkSalaryPlan() {
		JudgeJoinSalaryPlanRep rep = new JudgeJoinSalaryPlanRep();
		String uid = super.isLogin();
		if (!StringUtil.isEmpty(uid)) {
			List<String> inculuds = Arrays.asList(PlanStatus.READY.getCode());
			List<String> list = planMonthService.findPlanByUidTypeStatuses(uid, inculuds,
					InvestTypeEnum.MonthSalaryInvest.getCode());
			// String result = "";

			if (list == null || list.size() == 0) {
				// result = "{\"status\": \"false\"}";
				rep.setIsJoin(false);
			} else {
				// result = "{\"status\": \"true\"}";
				rep.setPlanOid(list.get(0));
				rep.setIsJoin(true);
			}
		}

		return new ResponseEntity<JudgeJoinSalaryPlanRep>(rep, HttpStatus.OK);
	}

	@ApiOperation(value = "计算收益", notes = "根据期限本金计算收益 ")
	@RequestMapping(value = "caculateProfit", method = { RequestMethod.POST })
	@ResponseBody
	public ResponseEntity<InvestMessageForm> caculateProfit(@NotNull @RequestParam("duration") int duration,
			@NotNull @RequestParam("capital") int capital, @NotNull @RequestParam("planOid") String planOid,
			@NotNull @RequestParam("type") String type) throws BaseException {
		type = type.toUpperCase();
		String uid = this.getLoginUser();
		if (uid == null) {
			throw new BaseException(ErrorMessage.USER_NOT_LOGIN);
		}
		JJCProductRate productRate = productService.getRateByDuration(duration, false, uid);
		InvestMessageForm form = null;
//		form = planService.caculateInvestProfitByFixed(duration, capital, productRate.getRate());
		
		if (type.equals(InvestTypeEnum.FixedInvestment.getCode())) {
			form = planService.caculateInvestProfitByFixed(duration, capital, productRate.getRate());
		} else if (type.equals(InvestTypeEnum.MonthInvestment.getCode())) {
//			int month = JJCUtility.days2month(duration);
			int months = duration / 30;
			form = planService.caculateInvestProfitByMonth(months, capital, productRate.getRate());
		}
		
		form.setPlanOid(planOid);
		form.setProductOid(productRate.getOid());
		form.setProductName(productRate.getName());
		form.setRate(productRate.getRate());
		return new ResponseEntity<InvestMessageForm>(form, HttpStatus.OK);
	}

	@ApiOperation(value = "助学计划实施", notes = "助学成长计划 ")
	@RequestMapping(value = "educationInvest", method = { RequestMethod.POST })
	@ResponseBody
	public ResponseEntity<TradeOrderRep> educationInvest(@RequestBody @Valid EducationInvestReq eduReq)
			throws BaseException {
		// Get the max profit from product.
		String uid = this.getLoginUser();
		if (uid == null) {
			throw new BaseException(ErrorMessage.USER_NOT_LOGIN);
		}
		JJCProductRate productRate = productService.getRateByDuration(DateUtil.diffDays4Months(eduReq.getDuration()), false, uid);
		PlanProductForm tradeOrderReq = planInvestService.generateOrderReq(eduReq, productRate);
		tradeOrderReq.setUid(uid);
		log.info("助学成长计划购买开始 uid={}, money={}, productid={}, startTime={}", tradeOrderReq.getUid(),
				tradeOrderReq.getMoneyVolume(), tradeOrderReq.getProductOid(), DateUtil.getSqlCurrentDate());
		PlanInvestEntity investPlan = planInvestService.planInsert(tradeOrderReq,
				InvestTypeEnum.OnceEduInvest.getCode(), null);
		if (investPlan == null) {
			throw new BaseException(ErrorMessage.PRAMETER_ERROR);
		}
		RedeemToBasicRequest req = new RedeemToBasicRequest();
		req.setBalance(tradeOrderReq.getMoneyVolume());
		req.setUserOid(uid);
		req.setOpposition(true);
		int result = accmentService.redeem2basic(req);
		TradeOrderRep rep = new TradeOrderRep();
		if (result == 0) {
			rep.setExpectedAmount(JJCUtility.keep2Decimal(investPlan.getExpectedAmount().floatValue()));
			// rep.setExpectedAmount(investPlan.getExpectedAmount().floatValue());
			rep.setErrorCode(0);
			String tradeOrderOid = planInvestService.depositSucess(investPlan);
			rep.setTradeOrderOid(tradeOrderOid);
			rep.setStartTime(investPlan.getCreateTime());
			rep.setEndTime(investPlan.getEndTime());
			// Call third msg and mail.
			thirdNotifyService.callMsgMail(MsgParam.msgsuccessjoinplanonetime, investPlan, investPlan.getUid());
			log.info("助学成长计划购买成功 uid={}, planId={}, money={}, endTime={}", tradeOrderReq.getUid(), investPlan.getOid(),
					tradeOrderReq.getMoneyVolume(), DateUtil.getSqlCurrentDate());
			//Record ebaoquan
			baoquanService.eBaoquanRecord(EbaoquanRecord.EBAOQUAN_TYPE_WISHPLAN_ONCE, investPlan);
		} else {
			rep.setErrorCode(-1);
			planInvestService.updateStatus(investPlan.getOid(), PlanStatus.FAILURE.getCode());
			log.info("助学成长计划购买失败 uid={}, planId={}, money={}, endTime={}", tradeOrderReq.getUid(), investPlan.getOid(),
					tradeOrderReq.getMoneyVolume(), DateUtil.getSqlCurrentDate());
		}

		return new ResponseEntity<TradeOrderRep>(rep, HttpStatus.OK);
	}
	/*
	 * @ApiOperation(value = "助学计划实施", notes = "助学成长计划 ")
	 * 
	 * @RequestMapping(value = "educationInvest", method = { RequestMethod.POST
	 * })
	 * 
	 * @ResponseBody public ResponseEntity<TradeOrderRep>
	 * educationInvest(@RequestBody @Valid EducationInvestReq eduReq) throws
	 * BaseException { // Get the max profit from product.
	 * 
	 * String uid = this.getLoginUser(); if (uid == null) { throw new
	 * BaseException(ErrorMessage.USER_NOT_LOGIN); } JJCProductRate productRate
	 * = productService.getRateByDuration(eduReq.getDuration() * 30, false);
	 * PlanProductForm tradeOrderReq =
	 * planEduOnceService.generateOrderReq(eduReq, productRate);
	 * tradeOrderReq.setUid(uid);
	 * log.info("助学成长计划购买开始 uid={}, money={}, productid={}, startTime={}",
	 * tradeOrderReq.getUid(), tradeOrderReq.getMoneyVolume(),
	 * tradeOrderReq.getProductOid(), DateUtil.getSqlCurrentDate());
	 * 
	 * PlanEduOnceEntity eduPlan = planEduOnceService.planInsert(tradeOrderReq);
	 * if (eduPlan == null) { throw new
	 * BaseException(ErrorMessage.PRAMETER_ERROR); } else {
	 * tradeOrderReq.setPlanOid(eduPlan.getOid());
	 * tradeOrderReq.setPlanType(InvestTypeEnum.OnceEduInvest.getCode()); }
	 * tradeOrderReq.setEndTime(eduPlan.getEndTime()); // Record the wish plan
	 * redeem PlanRedeemBalanceEntity redeem =
	 * planRedeemService.recordRedeem(tradeOrderReq); if (null == redeem) {
	 * throw new BaseException(ErrorMessage.PRAMETER_ERROR); }
	 * tradeOrderReq.setPlanRedeemOid(redeem.getOid());
	 * tradeOrderReq.setOriginBranch(PlanProductForm.
	 * TRADEORDER_originBranch_whishMiddle);
	 * 
	 * TradeOrderRep rep = planEduOnceService.nestBuyProduct(tradeOrderReq,
	 * productRate.getMoneyValume());
	 * 
	 * if (rep.getErrorCode() == 0) {
	 * planEduOnceService.updateStatus(eduPlan.getOid(),
	 * PlanStatus.SUCCESS.getCode());
	 * log.info("助学成长计划购买成功 uid={}, planId={}, money={}, endTime={}",
	 * tradeOrderReq.getUid(), eduPlan.getOid(), tradeOrderReq.getMoneyVolume(),
	 * DateUtil.getSqlCurrentDate()); } else {
	 * planEduOnceService.updateStatus(eduPlan.getOid(),
	 * PlanStatus.FAILURE.getCode());
	 * log.info("助学成长计划购买失败 uid={}, planId={}, money={}, endTime={}",
	 * tradeOrderReq.getUid(), eduPlan.getOid(), tradeOrderReq.getMoneyVolume(),
	 * DateUtil.getSqlCurrentDate()); }
	 * 
	 * // planService.updateStatus(planEntity.getOid(),
	 * PlanStatus.SUCCESS.getCode(), // rep.getTradeOrderOid());
	 * 
	 * return new ResponseEntity<TradeOrderRep>(rep, HttpStatus.OK); }
	 */

	@ApiOperation(value = "按月定投", notes = "薪增长、旅游分期、教育分期 ")
	@RequestMapping(value = "monthlyInvest", method = { RequestMethod.POST })
	@ResponseBody
	public ResponseEntity<BaseResp> monthlyInvest(@Valid @RequestBody MonthlyInvestForm form) throws BaseException {

		PlanListEntity plan = planListService.findByPlanType(form.getPlanType());
		if (plan == null) {
			throw new BaseException(ErrorMessage.PLAN_NOT_EXIST);
		}
		form.setPlanType(plan.getPlanType());
		String uid = this.getLoginUser();
		if (uid == null) {
			throw new BaseException(ErrorMessage.USER_NOT_LOGIN);
		} else {
			form.setUid(uid);
		}

		if (form.getPlanType().equals(InvestTypeEnum.MonthEduInvest.getCode())
				|| form.getPlanType().equals(InvestTypeEnum.MonthTourInvest.getCode())) {
			JJCProductRate productRate = productService.getRateByDuration(DateUtil.diffDays4Months(form.getPlanMonthCount()), false, uid);
			if (productRate.getOid() == null) {
				throw new BaseException(ErrorMessage.PRODUCT_NOT_EXIST);
			}
			InvestMessageForm investForm = planService.caculateInvestProfitByMonth(form.getPlanMonthCount(),
					form.getMothAmount().intValue(), productRate.getRate());
			form.setExpectedAmount(new BigDecimal(investForm.getProfit()));
			BigDecimal rate = new BigDecimal(productRate.getRate());
			rate = rate.setScale(4, BigDecimal.ROUND_HALF_UP);
			form.setExpectedRate(rate);
		} else if (form.getPlanType().equals(InvestTypeEnum.MonthSalaryInvest.getCode())) {
			JJCProductRate productRate = productService.getRateByDuration(DateUtil.diffDays4Months(form.getPlanMonthCount()), true, uid);
			if (productRate.getOid() == null) {
				throw new BaseException(ErrorMessage.PRODUCT_NOT_EXIST);
			}
			BigDecimal rate = new BigDecimal(productRate.getRate());
			rate = rate.setScale(4, BigDecimal.ROUND_HALF_UP);
			form.setExpectedRate(rate);
		}

		log.info("每月定投计划购买开始 uid={}, monthmoney={}, planLabel={}", uid, form.getMothAmount(), plan.getName());
		PlanMonthEntity rep = planMonthService.investMonth(form);

		// Record the wish plan redeem
		// PlanRedeemBalanceEntity redeem =
		// planRedeemService.recordMonthRedeem(rep);
		// if (null == redeem) {
		// throw new BaseException(ErrorMessage.PRAMETER_ERROR);
		// }

		BaseResp result = new BaseResp();
		result.setErrorCode(0);
		result.setErrorMessage(null);
		if (rep.getOid() != null) {
			// Current with hold
			planMothScheduleService.withholdCurrent(rep);
			log.info("设置定投计划成功 uid={}, oid={}, monthmoney={}", uid, rep.getOid(), rep.getMonthAmount());
			if (rep.getPlanType().equals(InvestTypeEnum.MonthSalaryInvest.getCode())) {
				thirdNotifyService.callMsgMail(MsgParam.msgsetwageincreasesuccess, rep, rep.getUid());
			} else {
				thirdNotifyService.callMsgMail(MsgParam.msgsuccessjoinplanbymonth, rep, rep.getUid());
			}
		} else {
			log.info("设置定投计划失败 uid={}, oid={}, monthmoney={}", uid, rep.getOid(), rep.getMonthAmount());
		}

		return new ResponseEntity<BaseResp>(result, HttpStatus.OK);
	}

	@ApiOperation(value = "schedule buy", notes = "month schedule")
	@RequestMapping(value = "monthSchedule", method = { RequestMethod.POST })
	@ResponseBody
	public ResponseEntity<String> monthSchedule() throws BaseException {
		String result = "success!";
		try {
			scheduleService.scheduleProcess();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage(), e);
			e.printStackTrace();
		}
		return new ResponseEntity<String>(result, HttpStatus.OK);
	}

	@ApiOperation(value = "根据id查询我的计划详情", notes = "根据id查询我的计划详情 ")
	@RequestMapping(value = "getMonthPlanDetail", method = { RequestMethod.POST })
	@ResponseBody
	public ResponseEntity<MonthPlanByOidRep> getMonthPlanDetail(@NotNull @RequestParam("planOid") String oid) {
		MonthPlanByOidRep rep = planBaseService.getMonthPlanDetail(oid);
		return new ResponseEntity<MonthPlanByOidRep>(rep, HttpStatus.OK);
	}

	@ApiOperation(value = "统计个人的心愿计划列表", notes = "统计个人心愿计划列表 ")
	@RequestMapping(value = "satisticsPlanList", method = { RequestMethod.POST })
	@ResponseBody
	public ResponseEntity<PlanProfitListForm> satisticsPlanList() throws BaseException {
		String uid = this.getLoginUser();
		PlanProfitListForm genericForm = planBaseService.satisticsPlanList(uid);
		return new ResponseEntity<PlanProfitListForm>(genericForm, HttpStatus.OK);
	}

	@ApiOperation(value = "薪增长计划完成", notes = "薪增长转出到余额 ")
	@RequestMapping(value = "salaryplanComplete", method = { RequestMethod.POST })
	@ResponseBody
	public ResponseEntity<String> salaryplanComplete(@NotNull @RequestParam("planOid") String planOid)
			throws BaseException {
		String uid = this.getLoginUser();
		if (uid == null) {
			throw new BaseException(ErrorMessage.USER_NOT_LOGIN);
		}
		// int flag = planMonthService.complete(planOid);
		PlanMonthEntity entity = planMonthService.queryByOid(planOid);
		if (entity.getStatus().equals(PlanStatus.COMPLETE.getCode())
				|| entity.getStatus().equals(PlanStatus.REDEEMING.getCode())) {
			throw new BaseException(ErrorMessage.TRANSFER_BALANCE_ERROR);
		}
		int flag = planCompleteRedeemService.processSalaryRedeem(entity);

		return new ResponseEntity<String>("" + flag, HttpStatus.OK);
	}

	@ApiOperation(value = "定投记录", notes = "每月定投记录 ")
	@RequestMapping(value = "monthDepositRecord", method = { RequestMethod.POST })
	@ResponseBody
	public ResponseEntity<MonthLogRep> monthDepositRecord(@NotNull @RequestParam("planOid") String planOid) {
		MonthLogRep rep = new MonthLogRep();
		List<PlanDepositForm> listForm = new ArrayList<PlanDepositForm>();
		List<PlanInvestEntity> depositList = planInvestService.queryByPlanMonthOid(planOid);
		for (PlanInvestEntity month : depositList) {
			PlanDepositForm form = new PlanDepositForm();
			form.setDepositAmount(month.getDepositAmount());
			form.setDepositTime(month.getCreateTime());
			if (PlanStatus.DEPOSITED.getCode().equals(month.getStatus())
					|| PlanStatus.SUCCESS.getCode().equals(month.getStatus())
					|| PlanStatus.REDEEMING.getCode().equals(month.getStatus())
					|| PlanStatus.COMPLETE.getCode().equals(month.getStatus())) {
				form.setDepositStatus("1");
			} else if (PlanStatus.STOP.getCode().equals(month.getStatus())) {
				form.setDepositStatus("4");
			} else if (PlanStatus.FAILURE.getCode().equals(month.getStatus())) {
				form.setDepositStatus("3");
			} else if (PlanStatus.TODEPOSIT.getCode().equals(month.getStatus())) {
				form.setDepositStatus("2");
			} else {
				form.setDepositStatus("0");
			}

			listForm.add(form);
			rep.setList(listForm);
		}
		if (rep.getList() != null && rep.getList().size() > 1) {
			List<PlanDepositForm> listSorted =
					
			rep.getList().stream()

			.sorted((form1, form2) ->

			form1.getDepositTime().compareTo(form2.getDepositTime()))
			
			.collect(Collectors.toList());
			
			rep.setList(listSorted);
			
		}
		return new ResponseEntity<MonthLogRep>(rep, HttpStatus.OK);
	}

	@ApiOperation(value = "eduTourSchedule buy", notes = "eduTourSchedule schedule")
	@RequestMapping(value = "eduTourSchedule", method = { RequestMethod.POST })
	@ResponseBody
	public ResponseEntity<String> eduTourSchedule() throws BaseException {
		String result = "success!";
		eduTourScheduleOnce.scheduleProcess();
		return new ResponseEntity<String>(result, HttpStatus.OK);
	}

	
	
	@ApiOperation(value = "scheduleProcess", notes = "scheduleProcess")
	@RequestMapping(value = "scheduleProcess", method = { RequestMethod.POST })
	@ResponseBody
	public ResponseEntity<String> scheduleProcess() throws BaseException {
		String result = "success!";
		planMothScheduleService.scheduleProcess();;
		return new ResponseEntity<String>(result, HttpStatus.OK);
	}
	
	@ApiOperation(value = "overdueProcess", notes = "overdueProcess schedule")
	@RequestMapping(value = "overdueProcess", method = { RequestMethod.POST })
	@ResponseBody
	public ResponseEntity<String> overdueProcess() throws BaseException {
		String result = "success!";
		planMothScheduleService.scheduleOverdueProcess();
		return new ResponseEntity<String>(result, HttpStatus.OK);
	}

	@ApiOperation(value = "redeem2basic", notes = "redeem2basic schedule")
	@RequestMapping(value = "redeem2basic", method = { RequestMethod.POST })
	@ResponseBody
	public ResponseEntity<String> redeem2basic() throws BaseException {
		String result = "success!";
		incomeBalanceService.redeem2basic();
		return new ResponseEntity<String>(result, HttpStatus.OK);
	}

	@ApiOperation(value = "planRedeemAgain", notes = "planRedeemAgain schedule")
	@RequestMapping(value = "planRedeemAgain", method = { RequestMethod.POST })
	@ResponseBody
	public ResponseEntity<String> planRedeemAgain() throws BaseException {
		String result = "success!";
		planRedeemService.planRedeemAgain();
		return new ResponseEntity<String>(result, HttpStatus.OK);
	}

	@ApiOperation(value = "planRedeem", notes = "planRedeem schedule")
	@RequestMapping(value = "planRedeem", method = { RequestMethod.POST })
	@ResponseBody
	public ResponseEntity<String> planRedeem() throws BaseException {
		String result = "success!";
		planRedeemService.planRedeem();
		return new ResponseEntity<String>(result, HttpStatus.OK);
	}

}
