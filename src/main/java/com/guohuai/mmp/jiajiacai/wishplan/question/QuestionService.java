package com.guohuai.mmp.jiajiacai.wishplan.question;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guohuai.mmp.jiajiacai.wishplan.plan.entity.WishplanProduct;
import com.guohuai.component.util.DateUtil;
import com.guohuai.mmp.jiajiacai.caculate.InvestProfitCaculate;
import com.guohuai.mmp.jiajiacai.caculate.JJCUtility;
import com.guohuai.mmp.jiajiacai.common.constant.InvestTypeEnum;
import com.guohuai.mmp.jiajiacai.common.exception.BaseException;
import com.guohuai.mmp.jiajiacai.wishplan.product.JJCProductService;
import com.guohuai.mmp.jiajiacai.wishplan.product.form.JJCProductRate;
import com.guohuai.mmp.jiajiacai.wishplan.risklevel.RiskLevelDao;

@Service
public class QuestionService {
	
	@Autowired
	private JJCProductService productService ;
	
	@Autowired
	private RiskLevelDao  riskLevelDao;
	/**
	 * 一次性购买时 根据投资收益计算投资本金
	 * @param duration
	 * @param profit
	 * @return
	 */
	InvestMessageForm caculateInvestCapitalByFixed(int duration, int profit, String planOid, boolean onlyOpen, String uid) {
		JJCProductRate productRate = productService.getRateByDuration(duration, onlyOpen, uid);
		float param = 1.0f;
		float capital = InvestProfitCaculate.caculateInvestCapitalByFixed(duration, profit, (float)productRate.getRate(), param);
		int newCapital = ((int)(capital + 99.99) / 100) * 100;
		float benefit = InvestProfitCaculate.caculateInvestProfitByFixed(duration, newCapital, productRate.getRate());
		String newGeneralBenefit = JJCUtility.keep2Decimal(benefit);
		InvestMessageForm form = new InvestMessageForm(planOid, duration, newCapital, newGeneralBenefit, InvestTypeEnum.FixedInvestment.getCode(), productRate.getOid(), "", productRate.getRate(), new BigDecimal(0), null);
		return form;
	}
	
	 /**
	 * 一次性购买时 根据投资收益计算投资本金
	 * @param duration
	 * @param profit
	 * @return
	 */
	List<InvestMessageForm> caculateInvestCapitalByFixedList(int duration, int profit, String planOid, boolean onlyOpen, String uid) {
		String resultLevel = riskLevelDao.selectRiskLevel(uid);
//		String riskLevel = productService.generateRiskLevel(resultLevel);
		List<InvestMessageForm> messageFormList = new ArrayList<InvestMessageForm>();
		List<WishplanProduct> formList =null;
		try {
			formList = productService.findProductRateList(duration, resultLevel, onlyOpen);
		} catch (BaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		float param = 1.0f;
		int capital = 1;
		for (WishplanProduct form: formList) {
			JJCProductRate productRate = new JJCProductRate();
			productRate.setOid(form.getOid());
			productRate.setName(form.getName());
			productRate.setRate(form.getExpAror().floatValue());
			capital = (int)InvestProfitCaculate.caculateInvestCapitalByFixed(duration, profit, form.getExpAror().floatValue(), param);
			capital = (capital / 100) * 100;
			String profitStr = JJCUtility.keep2Decimal(profit);
			InvestMessageForm messageForm = new InvestMessageForm(planOid,duration,capital,profitStr,InvestTypeEnum.FixedInvestment.getCode(), productRate.getOid(), "", productRate.getRate(), new BigDecimal(0), null);
			messageFormList.add(messageForm);
		}
		return messageFormList;
	}
	
	/**
	 * 计算每月定投的投资本金
	 * @param investNumber
	 * @param generalBenefit
	 * @return
	 */
	InvestMessageForm caculateInvestCapitalByMonth(int investNumber, int generalBenefit, String planOid, boolean onlyOpen, String uid) {
		int investDuration = DateUtil.diffDays4Months(investNumber);
		JJCProductRate productRate = productService.getRateByDuration(investDuration, onlyOpen, uid);
		float capital = InvestProfitCaculate.caculateInvestCapitalByMonth(investNumber, generalBenefit, (float)productRate.getRate());
		int newCapital = ((int)(capital + 99.99) / 100) * 100;
		float benefit = InvestProfitCaculate.caculateInvestProfitByMonth(investNumber, newCapital, productRate.getRate());
		String newGeneralBenefit = JJCUtility.keep2Decimal(benefit);
		InvestMessageForm form = new InvestMessageForm(planOid, investDuration, newCapital, newGeneralBenefit, InvestTypeEnum.MonthInvestment.getCode(), productRate.getOid(), "",  productRate.getRate(), new BigDecimal(0), null);
		return form;
	}
	
	/**
	 * 计算每月定投的投资本金
	 * @param investNumber
	 * @param generalBenefit
	 * @return
	 */
	List<InvestMessageForm> caculateInvestCapitalByMonthList(int investNumber, int generalBenefit, String planOid, boolean onlyOpen, String uid) {
		int investDuration = DateUtil.diffDays4Months(investNumber);
		String resultLevel = riskLevelDao.selectRiskLevel(uid);
//		String riskLevel = productService.generateRiskLevel(resultLevel);
		List<InvestMessageForm> messageFormList = new ArrayList<InvestMessageForm>();
		List<WishplanProduct> formList =null;
		try {
			formList = productService.findProductRateList(investDuration, resultLevel, onlyOpen);
		} catch (BaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int capital = 1;
		for (WishplanProduct form : formList) {
			JJCProductRate productRate = new JJCProductRate();
			productRate.setOid(form.getOid());
			productRate.setName(form.getName());
			productRate.setRate(form.getExpAror().floatValue());
			
			capital = (int)InvestProfitCaculate.caculateInvestCapitalByMonth(investNumber, generalBenefit, (float)productRate.getRate());
			capital = (capital / 100) * 100;
			String profitStr = JJCUtility.keep2Decimal(generalBenefit);
			InvestMessageForm messageForm = new InvestMessageForm(planOid,investDuration,capital,profitStr,InvestTypeEnum.MonthSalaryInvest.getCode(),productRate.getOid(),"", productRate.getRate(), new BigDecimal(0), null);
			messageFormList.add(messageForm);
		}
		return messageFormList;
	}
	
}
