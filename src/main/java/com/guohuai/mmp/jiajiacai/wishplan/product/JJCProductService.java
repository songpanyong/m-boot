package com.guohuai.mmp.jiajiacai.wishplan.product;

import java.math.BigDecimal;
import java.sql.Date;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.guohuai.component.exception.AMPException;
import com.guohuai.mmp.jiajiacai.common.constant.Constant;
import com.guohuai.mmp.jiajiacai.common.exception.BaseException;
import com.guohuai.mmp.jiajiacai.common.exception.ErrorMessage;
import com.guohuai.mmp.jiajiacai.wishplan.plan.entity.WishplanProduct;
import com.guohuai.mmp.jiajiacai.wishplan.product.entity.JJCPortfolioEntity;
import com.guohuai.mmp.jiajiacai.wishplan.product.form.JJCPortfolioForm;
import com.guohuai.mmp.jiajiacai.wishplan.product.form.JJCProductRate;
import com.guohuai.mmp.jiajiacai.wishplan.risklevel.RiskLevelDao;
import com.guohuai.tulip.util.DateUtil;

@Service
public class JJCProductService {

	@Autowired
	private JJCProductDao productDao;

	@Autowired
	private JJCPortfolioDao portfolioDao;
	
	@Autowired
	private RiskLevelDao  riskLevelDao;
	
	private DecimalFormat df = new DecimalFormat("######0.00");   

	/**
	 * 根据投资期限获得利率
	 * 
	 * @param investDuration
	 * @return
	 */
	public JJCProductRate getRateByDuration(int investDuration, boolean onlyOpen, String uid) {
		// 构造模拟数据
//		ProductRate productRate = new ProductRate("001", 0.08d);
//		String riskLevel = "R1";
		String resultLevel = riskLevelDao.selectRiskLevel(uid);
//		List<String> riskLevel = generateRiskLevel(resultLevel);
		JJCProductRate productRate = new JJCProductRate();
		List<WishplanProduct> forms = null;
		try {
			forms = this.findProductRateList(investDuration, resultLevel, onlyOpen);
		} catch (BaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		productRate.setOid(forms.get(0).getOid());
		productRate.setName(forms.get(0).getName());
		productRate.setProductMoneyValume(forms.get(0).getMaxSaleVolume().subtract(forms.get(0).getLockCollectedVolume()));
		float rate = complexRateByDuration(investDuration, forms);
		productRate.setRate(rate);
		return productRate;
	}
	
	public JJCProductRate getMaxRateByDuration(int investDuration, boolean onlyOpen, List<String> excludes, String uid) {

//		String resultLevel = "R1";		
		String resultLevel = riskLevelDao.selectRiskLevel(uid);
		List<String> riskLevel = generateRiskLevel(resultLevel);
		
		JJCProductRate productRate = new JJCProductRate();
		WishplanProduct p = null;
		 
		if (onlyOpen) {
			 p = productDao.findOpenProductRateListExclude(riskLevel, excludes);
		} else {
			 Date repayDate = DateUtil.addSQLDays(investDuration);
			 p = productDao.findMaxProductRateListByDate(repayDate, riskLevel, excludes);
		}
		if (p == null) {
			throw new AMPException(ErrorMessage.PRODUCT_NOT_EXIST);
		}
		productRate.setOid(p.getOid());
		productRate.setName(p.getName());
		productRate.setProductMoneyValume(p.getMaxSaleVolume().subtract(p.getLockCollectedVolume()));
		productRate.setRate(p.getExpAror().floatValue());
		
		return productRate;
	}
   /*
	public float complexRateByDuration(int investDuration, List<WishplanProduct> forms) {
//		float profit = 100.f;
		float profit = 0.f;
		if (forms.size() == 1) {
			return forms.get(0).getExpAror().floatValue();
		} else {
			for (WishplanProduct form : forms) {
//				profit += profit * form.getExpAror().floatValue() * form.getDurationPeriodDays() / Constant.YEAR_DAYS;
				profit += 100.f * form.getExpAror().floatValue() * form.getDurationPeriodDays() / Constant.YEAR_DAYS;
			}
			 return profit *  Constant.YEAR_DAYS / (100.f * investDuration);
//			return (profit - 100) / 100.f;
		}
	}
	*/
	public float complexRateByDuration(int investDuration, List<WishplanProduct> forms) {
		float profit = 100.f;
		if (forms.size() == 1) {
			return forms.get(0).getExpAror().floatValue();
		} else {
			for (WishplanProduct form : forms) {
				profit += profit * form.getExpAror().floatValue() * form.getDurationPeriodDays() / Constant.YEAR_DAYS;
			}
			 return (profit - 100) *  Constant.YEAR_DAYS / (100.f * investDuration);
//			return (profit - 100) / 100.f;
		}
	}
	/**
	 * 根据产品ID获取产品的资产配比信息
	 * 
	 * @param productOid
	 * @return
	 * @throws BaseException 
	 */
	public JJCPortfolioForm getPortfolioByProductOid(String productOid) throws BaseException {
		// 构造模拟数据
//		PortfolioForm form = new PortfolioForm(productOid, "TestProduct", "0.50", "0.25", "0.15", "0.10");
		JJCPortfolioForm form = new JJCPortfolioForm();
		// 实际查询数据
		// 根据 productOid 查询资产组合ID ，查询资产组合的比例
		WishplanProduct product = productDao.findOne(productOid);
		if (product == null) {
			throw new BaseException(ErrorMessage.PRODUCT_NOT_EXIST);
		}
		String portfolioOid = product.getAssetPoolOid();
		if (StringUtils.isEmpty(portfolioOid)) {
			throw new BaseException(ErrorMessage.PORTFOLIO_NOT_EXIST);
		}
		JJCPortfolioEntity portfolioEntity = portfolioDao.findOne(portfolioOid);
		form.setOid(productOid);
		form.setName(product.getName());
		form.setCashRate(df.format(portfolioEntity.getCashRate()));
		form.setIlliquidRate(df.format(portfolioEntity.getIlliquidRate()));
		form.setLiquidRate(df.format( portfolioEntity.getLiquidRate()));
		String other = df.format(1 - portfolioEntity.getCashRate().doubleValue() - portfolioEntity.getIlliquidRate().doubleValue() - portfolioEntity.getLiquidRate().doubleValue());
		form.setOther(other);
		return form;
	}
	/**
	 * Convert list to str
	 * @param list
	 * @return
	 */
	public String list2Str(List<String> list) {
		String resultStr = "";
		for (String eachL:list) {
			resultStr += "'" + eachL + "'" + ",";
		}
		resultStr = resultStr.substring(0, resultStr.length() - 1); 
		return resultStr;
	}
	
	/*
	public String generateRiskLevel(String riskLevel) {
		if (riskLevel == null) { 
			riskLevel = "R1";
		}
			
		List<String> level = new ArrayList<String>();
		if(riskLevel.equals("R1")) {
			level.add("R1");
		}else if(riskLevel.equals("R2")) {
			level.add("R1");
			level.add("R2");
		}else if(riskLevel.equals("R3")) {
			level.add("R1");
			level.add("R2");
			level.add("R3");
		}else if(riskLevel.equals("R4")) {
			level.add("R1");
			level.add("R2");
			level.add("R3");
			level.add("R4");
		}else if(riskLevel.equals("R5")) {
			level.add("R1");
			level.add("R2");
			level.add("R3");
			level.add("R4");
			level.add("R5");
		}else {
			//Default
			level.add("R1");
		}
//		List<JJCProductRateForm> forms = new ArrayList<JJCProductRateForm>();
		String resultLevel = list2Str(level);
//		for (String eachL:level) {
//			resultLevel += eachL + ",";
//		}
//		resultLevel = resultLevel.substring(0, resultLevel.length() - 1); 
		
		return resultLevel;
	}
	*/
	public List<String> generateRiskLevel(String riskLevel) {
		if (riskLevel == null) { 
			riskLevel = "R1";
		}
			
		List<String> level = new ArrayList<String>();
		if(riskLevel.equals("R1")) {
			level.add("R1");
		}else if(riskLevel.equals("R2")) {
			level.add("R1");
			level.add("R2");
		}else if(riskLevel.equals("R3")) {
			level.add("R1");
			level.add("R2");
			level.add("R3");
		}else if(riskLevel.equals("R4")) {
			level.add("R1");
			level.add("R2");
			level.add("R3");
			level.add("R4");
		}else if(riskLevel.equals("R5")) {
			level.add("R1");
			level.add("R2");
			level.add("R3");
			level.add("R4");
			level.add("R5");
		}else {
			//Default
			level.add("R1");
		}
//		List<JJCProductRateForm> forms = new ArrayList<JJCProductRateForm>();
//		String resultLevel = list2Str(level);
//		for (String eachL:level) {
//			resultLevel += eachL + ",";
//		}
//		resultLevel = resultLevel.substring(0, resultLevel.length() - 1); 
		
		return level;
	}
	/**
	 * 根据投资期限和风险等级 查询产品利率
	 * @param duration
	 * @param riskLevel
	 * @return
	 * @throws BaseException 
	 */
	public List<WishplanProduct> findProductRateList(int duration, String riskLevel, boolean onlyOpen) throws BaseException {
		/*
		List<String> level = new ArrayList<String>();
		if(riskLevel.equals("R1")) {
			level.add("R1");
		}else if(riskLevel.equals("R2")) {
			level.add("R1");
			level.add("R2");
		}else if(riskLevel.equals("R3")) {
			level.add("R1");
			level.add("R2");
			level.add("R3");
		}else if(riskLevel.equals("R4")) {
			level.add("R1");
			level.add("R2");
			level.add("R3");
			level.add("R4");
		}else if(riskLevel.equals("R5")) {
			level.add("R1");
			level.add("R2");
			level.add("R3");
			level.add("R4");
			level.add("R5");
		}else {
			
		}
//		List<JJCProductRateForm> forms = new ArrayList<JJCProductRateForm>();
		String resultLevel = "";
		for (String eachL:level) {
			resultLevel += eachL + ",";
		}
		resultLevel = resultLevel.substring(0, resultLevel.length() - 1); 
		*/
		List<String> resultLevel = generateRiskLevel(riskLevel);
		List<WishplanProduct> list = new ArrayList<WishplanProduct>();
		if (onlyOpen) {
			List<String> exculuds =  Arrays.asList("");
			WishplanProduct p = productDao.findOpenProductRateListExclude(resultLevel, exculuds);
			list.add(p);
		} else {
//			list = productDao.findMaxProductRateList(duration, resultLevel);
			selectMaxProfitProduct(list, duration, resultLevel);
		}
		if(list==null || list.size() < 1) {
			throw new BaseException(ErrorMessage.PRODUCT_NOT_EXIST);
		}
		/*
		for(int i=0 ;i < list.size() ; i++) {
			JJCProductRateForm form = new JJCProductRateForm();
			form.setOid(list.get(i).getOid());
			form.setName(list.get(i).getName());
			form.setType(list.get(i).getType());
			form.setExpAror(list.get(i).getExpAror().doubleValue());
			if(list.get(i).getInvestMin() !=null ) {
				form.setInvestMin(list.get(i).getInvestMin().doubleValue());
			}
			if(list.get(i).getInvestMax() !=null) {
				form.setInvestMax(list.get(i).getInvestMax().doubleValue());
			}
			form.setDurationPeriodDays(list.get(i).getDurationPeriodDays());
			form.setRiskLevel(list.get(i).getRiskLevel());
			form.setPurchaseApplyStatus(list.get(i).getPurchaseApplyStatus());
			form.setDayRate(list.get(i).getExpAror().doubleValue()/365);
			forms.add(form);
		}
		*/
		// The max expAror
//		if (forms.size() > 1) {
//			Collections.sort(forms, new Comparator<JJCProductRateForm>() {
//				@Override
//				public int compare(JJCProductRateForm o1, JJCProductRateForm o2) {
//					int flag = o1.getExpAror() > o2.getExpAror() ? -1 : 1;
//					return flag;
//				}
//			});
//		}
		       
		return list;
	}
	
	public void selectMaxProfitProduct(List<WishplanProduct> baseList, int duration, List<String> riskLevel) {
		int leftDuration = computeDuraiton(baseList, duration);
		if (leftDuration <= 0) {
			return;
		}
		List<String> excludes = computeExcludes(baseList);
		Date repayDate = DateUtil.addSQLDays(leftDuration);
		WishplanProduct p = productDao.findMaxProductRateListByDate(repayDate, riskLevel, excludes);
		if (p == null) {
			throw new AMPException(ErrorMessage.PRODUCT_NOT_EXIST);
		}
		baseList.add(p);
		selectMaxProfitProduct(baseList, duration, riskLevel);
	}
	
	public int computeDuraiton(List<WishplanProduct> baseList, int originDuration) {
		int consumeDuration = 0;
	    for (WishplanProduct jjcp : baseList) {
	    	if (WishplanProduct.TYPE_Producttype_02.equals(jjcp.getType())) {
//	    	if (jjcp.getType().getOid().equals(Product.TYPE_Producttype_02)) {
	    		jjcp.setDurationPeriodDays(originDuration - consumeDuration);
	    		consumeDuration = originDuration;
	    		break;
	    	} else {
	    		consumeDuration += jjcp.getDurationPeriodDays();
	    	}
	    }
	    return originDuration - consumeDuration;
	}
	
	public String computeExclude(List<WishplanProduct> baseList) {
		String exclude = "";
	    for (WishplanProduct jjcp : baseList) {
	    	exclude += jjcp.getOid() + ",";
	    }
	    if (exclude.length()> 0) {
	    	exclude = exclude.substring(0, exclude.length() -1);
	    }
	    return exclude;
	}
	
	public List<String> computeExcludes(List<WishplanProduct> baseList) {
		List<String>  excludes = new ArrayList<String>();
	    for (WishplanProduct jjcp : baseList) {
	    	excludes.add(jjcp.getOid());
	    }
	    if (excludes.size() == 0) {
	    	excludes.add("");
	    }
	    return excludes;
	}
	
	
	public BigDecimal queryMaxSaleVolume(String oid) {
		return productDao.queryMaxSaleVolume(oid);
	}
	
	@Transactional
	public int update4InvestLockVolume(String productOid, BigDecimal orderVolume) {
		return productDao.update4InvestLockVolume(productOid, orderVolume);
	}
	
}
