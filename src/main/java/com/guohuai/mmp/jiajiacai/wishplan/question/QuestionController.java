package com.guohuai.mmp.jiajiacai.wishplan.question;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.guohuai.basic.component.ext.web.BaseController;
import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.DateUtil;
import com.guohuai.mmp.jiajiacai.common.exception.BaseException;
import com.guohuai.mmp.jiajiacai.common.exception.ErrorMessage;
import com.guohuai.mmp.jiajiacai.rep.CalcuRateRep;
import com.guohuai.mmp.jiajiacai.wishplan.planlist.PlanListEntity;
import com.guohuai.mmp.jiajiacai.wishplan.planlist.PlanListService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value="心愿计划 - 问题")
@RestController
@EnableAutoConfiguration
@RequestMapping(value = "/mimosa/wishplan/question", produces = "application/json")
public class QuestionController extends BaseController {
	
	@Autowired
	private QuestionService questionService;
	
	@Autowired
	private PlanListService planService;
	
	/**
	 * 根据问题确定投资金额
	 * @param answer1 多久之后
	 * @param answer2 目的地
	 * @param answer3 几日游
	 * @param answer4 几人行
	 * @param answer5 几星级
	 * @return
	 * @throws BaseException 
	 */
	@ApiOperation(value="家庭旅游计划问题", notes="根据问题确定投资金额 ")
	@RequestMapping(value = "familyTravelPlan", method ={RequestMethod.POST})
	@ResponseBody
	public ResponseEntity<CalcuRateRep> familyTravelPlan(@NotNull @RequestParam("planOid") String planOid
			, @NotNull @RequestParam("duration") String duration
			, @NotNull @RequestParam("destination") String destination
			, @NotNull @RequestParam("dayNumber") String dayNumber
			, @NotNull @RequestParam("personNumber") String personNumber
			, @NotNull @RequestParam("hotelType") String hotelType)  {
		
		
		String uid = this.getLoginUser();
		if (uid == null) {
			throw new AMPException(ErrorMessage.USER_NOT_LOGIN);
		}
		//check planid
		PlanListEntity plan = planService.getEntityByOid(planOid);
		if(plan == null) {
			throw new AMPException(ErrorMessage.PLAN_NOT_EXIST);
		}
		CalcuRateRep rep = new CalcuRateRep();
		int investDuration = Integer.parseInt(duration);
		int profit = Integer.parseInt(dayNumber)*Integer.parseInt(personNumber);
		int hotelCost =0;
		switch (hotelType) {
			case "A" : hotelCost=500 ; break;  //	快捷连锁      一天成本500元
			case "B" : hotelCost=800 ; break; //	经济适用型   一天成本800元
			case "C" : hotelCost=1200 ; break; //	三星/舒适     一天成本1200
			case "D" : hotelCost=2000 ; break; //	四星/高档     一天成本2000
			case "E" : hotelCost=3000 ; break; //	五星/豪华    一天成本3000
			default : hotelCost=0;
		}
		profit = profit* hotelCost;
		int months = investDuration / 30;
		InvestMessageForm fixedInvest = questionService.caculateInvestCapitalByFixed(DateUtil.diffDays4Months(months), profit, planOid, false, uid);
		InvestMessageForm monthInvest = questionService.caculateInvestCapitalByMonth(months, profit, planOid, false, uid);
//		List<InvestMessageForm> fixedInvestList =questionService.caculateInvestCapitalByFixedList(investDuration, profit,planOid);
//		List<InvestMessageForm> monthInvestList = questionService.caculateInvestCapitalByMonthList(investDuration/30, profit,planOid);
		List<InvestMessageForm> invest = new ArrayList<InvestMessageForm>();
//		fixedInvestList.addAll(monthInvestList);
//		invest.add(monthInvestList);
		fixedInvest.setDuration(investDuration);
		monthInvest.setDuration(investDuration);
		invest.add(fixedInvest);
		invest.add(monthInvest);
		rep.setList(invest);
		return new ResponseEntity<CalcuRateRep>(rep, HttpStatus.OK); 
	}
	
	@ApiOperation(value="一次性购买根据到期收益推算投资额", notes="一次性购买根据到期收益推算投资额")
	@RequestMapping(value = "caculateCapitalByFixed", method ={RequestMethod.POST})
	@ResponseBody
	public ResponseEntity<String> caculateCapitalByFixed(@NotNull @RequestParam("duration") int duration
			,@NotNull @RequestParam("profit") int profit) throws BaseException {
		String uid = this.getLoginUser();
		if (uid == null) {
			throw new BaseException(ErrorMessage.USER_NOT_LOGIN);
		}
		InvestMessageForm form =questionService.caculateInvestCapitalByFixed(duration, profit, "", false, uid);
		StringBuffer buffer=new StringBuffer();
		buffer.append("{\"capital\" :");
		buffer.append(form.getCapital());
		buffer.append("}");
		return new ResponseEntity<String>(buffer.toString(), HttpStatus.OK);
	}
	
	@ApiOperation(value="按月定投根据到期收益推算投资额", notes="按月定投根据到期收益推算投资额 ")
	@RequestMapping(value = "caculateCapitalByMonth", method ={RequestMethod.POST})
	@ResponseBody
	public ResponseEntity<String> caculateCapitalByMonth(@NotNull @RequestParam("duration") int duration,
			@NotNull @RequestParam("profit") int profit) throws BaseException {
		String uid = this.getLoginUser();
		if (uid == null) {
			throw new BaseException(ErrorMessage.USER_NOT_LOGIN);
		}
		InvestMessageForm form = questionService.caculateInvestCapitalByMonth(duration/30, profit,"", false, uid);
		StringBuffer buffer=new StringBuffer();
		buffer.append("{\"capital\" :");
		buffer.append(form.getCapital());
		buffer.append("}");
		return new ResponseEntity<String>(buffer.toString(), HttpStatus.OK);
	}
	
}
