package com.guohuai.mmp.jiajiacai.caculate;

import com.guohuai.component.util.DateUtil;
import com.guohuai.mmp.jiajiacai.common.constant.Constant;

public class InvestProfitCaculate {
	
	
	/**
	 * 一次性购买计算投资本金
	 * @param investDuration 投资期限
	 * @param generalBenefit 到期收益
	 * @param rate 利率
	 * @param investType 投资方式 按月定投、一次性购买
	 * @param param 收益的系数 默认值是1 <br>如计划收益是10000 ，系数是1.05 则预期收益 = 10500，这样反推本金时需要多付一点本金 保证到期收益达到预期
	 * @return
	 */
	public static float caculateInvestCapitalByFixed(int investDuration, float generalBenefit, float rate, float param) {
		//计划投资（应投本金）=（到期可用*360）/（利率*期限+360）
		float investment = (generalBenefit * param *  Constant.YEAR_DAYS) / (rate * investDuration +  Constant.YEAR_DAYS);
		return investment;
	}
	
	/**
	 * 一次性购买计算投资收益 
	 * @param investDuration 投资期限
	 * @param investCapital 计划投资（应投本金）
	 * @param rate 利率
	 * @param investType 投资方式 按月定投、一次性购买
	 * @return
	 */
	public static float caculateInvestProfitByFixed(int investDuration,float investCapital,float rate) {
		//计划投资（应投本金） *（利率*期限+360） /360 = 到期可用
		float generalBenefit = ((float) (investCapital * (rate * investDuration +  Constant.YEAR_DAYS)) / Constant.YEAR_DAYS);
		return generalBenefit;
	}
	
	/**
	 * 计算定投的收益
	 * @param investNumber 定投期数
	 * @param everyCapital 每期定投金额
	 * @param rate 收益率
	 * @return
	 */
	public static float caculateInvestProfitByMonth(int investNumber, float everyCapital, float rate) {
	//	M=a(1+x)[-1+(1+x)^n]/x
	//	M：预期收益
	//	a：每期定投金额
	//	x：收益率
	//	n：定投期数(公式中为n次方)
	// 到期可用=修正后每月定投*（y*n（n+1）/2+12*n）/12
//		rate = rate / 12;
//		double generalBenefit = 0f;
//		generalBenefit = everyCapital*(1+rate)*(-1+Math.pow((1+rate),investNumber))/rate; 
//		float generalBenefit = (float) ((everyCapital * (rate * investNumber * (investNumber + 1) / 2 + 12 * investNumber)) / 12.00); 
		float generalBenefit = 0.f;
		int continueDays = DateUtil.diffDays4Months(investNumber);
		for (int i = 0; i < investNumber; i++) {
			int startDays = i > 0 ? DateUtil.diffDays4Months(i) : 0;
			float currentBenefit = caculateInvestProfitByFixed(continueDays - startDays, everyCapital, rate);
			generalBenefit += currentBenefit;
		}
		return generalBenefit;
	}
	
	/**
	 * 计算每月定投的投资本金
	 * @param investNumber
	 * @param generalBenefit
	 * @param rate
	 * @return
	 */
	public static float caculateInvestCapitalByMonth(int investNumber,float generalBenefit, float rate) {
		// a = M*x/ ( (1+x)[-1+(1+x)^n] )
		/*
		rate = rate / 12;
		double everyCapital = 0d;
		everyCapital = generalBenefit * rate /( (1+rate) *(-1 + Math.pow((1+rate),investNumber))  ) ;
		*/
		//每月定投=12*到期可用/{y*n（n+1）/2+12*n}
		float everyCapital = 12 * generalBenefit / (rate * investNumber * (investNumber + 1) / 2 + 12 * investNumber);
		return (float)everyCapital;
	}
	
}
