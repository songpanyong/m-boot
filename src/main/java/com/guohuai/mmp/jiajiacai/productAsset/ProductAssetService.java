package com.guohuai.mmp.jiajiacai.productAsset;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guohuai.ams.illiquidAsset.IlliquidAsset;
import com.guohuai.ams.liquidAsset.LiquidAsset;
import com.guohuai.ams.portfolio.dao.PortfolioDao;
import com.guohuai.ams.portfolio.entity.PortfolioEntity;
import com.guohuai.ams.portfolio20.illiquid.hold.PortfolioIlliquidHoldEntity;
import com.guohuai.ams.portfolio20.illiquid.hold.PortfolioIlliquidHoldService;
import com.guohuai.ams.portfolio20.liquid.hold.PortfolioLiquidHoldEntity;
import com.guohuai.ams.portfolio20.liquid.hold.PortfolioLiquidHoldService;
import com.guohuai.basic.common.StringUtil;
import com.guohuai.cache.entity.HoldCacheEntity;
import com.guohuai.cache.entity.ProductCacheEntity;
import com.guohuai.cache.service.CacheHoldService;
import com.guohuai.cache.service.CacheProductService;
import com.guohuai.component.util.DateUtil;
import com.guohuai.component.web.view.RowsRep;
import com.guohuai.mmp.jiajiacai.caculate.JJCUtility;
import com.guohuai.mmp.jiajiacai.common.constant.InvestTypeEnum;
import com.guohuai.mmp.jiajiacai.common.constant.PlanStatus;
import com.guohuai.mmp.jiajiacai.wishplan.plan.dao.PlanInvestDao;
import com.guohuai.mmp.jiajiacai.wishplan.plan.dao.PlanMonthDao;
import com.guohuai.mmp.jiajiacai.wishplan.plan.dao.PlanProductDao;
import com.guohuai.mmp.jiajiacai.wishplan.plan.entity.PlanInvestEntity;
import com.guohuai.mmp.jiajiacai.wishplan.plan.entity.PlanMonthEntity;
import com.guohuai.mmp.jiajiacai.wishplan.plan.entity.PlanProductEntity;
import com.guohuai.mmp.jiajiacai.wishplan.plan.form.DepositProfit;
import com.guohuai.mmp.jiajiacai.wishplan.plan.form.PlanProfitForm;
import com.guohuai.mmp.jiajiacai.wishplan.plan.service.PlanBaseService;
import com.guohuai.mmp.publisher.hold.PublisherHoldEntity;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProductAssetService {

	@Autowired
	private PortfolioDao portfolioDao;

	@Autowired
	private CacheHoldService cacheHoldService;

	@Autowired
	private CacheProductService cacheProductService;

	@Autowired
	private PortfolioLiquidHoldService portfolioLiquidHoldService;

	@Autowired
	private PortfolioIlliquidHoldService portfolioIlliquidHoldService;

	@Autowired
	private PlanMonthDao planMonthDao;

	@Autowired
	private PlanInvestDao planInvestDao;

	@Autowired
	private PlanBaseService planBaseService;

	@Autowired
	private PlanProductDao planDao;

	/**
	 * levelSecond
	 * 
	 * @param productOid
	 * @return
	 */
	public RowsRep<AssetLevelSecond> levelSecond(String productOid, BigDecimal volume) {

		PortfolioEntity pe = portfolioDao.queryByProductOid(productOid);
		return generateOnePortfolio(pe, volume);
	}

	/**
	 * processSecondRate
	 * 
	 * @param listSecond
	 * @param totalAmount
	 */
	private void processSecondRate(List<AssetLevelSecond> listSecond, BigDecimal totalAmount) {
		BigDecimal totalHold = BigDecimal.ZERO;
		for (AssetLevelSecond second : listSecond) {
			totalHold = totalHold.add(second.getAmount());
		}
		
		BigDecimal offsetRate = BigDecimal.ZERO;
		BigDecimal offsetAmount = BigDecimal.ZERO;
		AssetLevelSecond maxAmountLevel = listSecond.get(0);
		
		
		for (AssetLevelSecond second : listSecond) {
			
			BigDecimal rate = second.getAmount().divide(totalHold, 4, RoundingMode.HALF_UP);
			second.setAmount(JJCUtility.bigKeep2Decimal(totalAmount.multiply(rate)));
			rate = rate.multiply(new BigDecimal(100));
			second.setAssetRate(JJCUtility.bigKeep2Decimal(rate));
			
			if (second.getAmount().compareTo(maxAmountLevel.getAmount()) > 0) {
				maxAmountLevel = second;
			} 
			
			offsetRate = offsetRate.add(second.getAssetRate());
			offsetAmount = offsetAmount.add(second.getAmount());
			
//			updateOneSecondRate(second, totalAmount, totalHold);
		}
		
		offsetRate = offsetRate.subtract(new BigDecimal(100));
		offsetAmount = offsetAmount.subtract(totalAmount);
		
		if (offsetAmount.compareTo(BigDecimal.ZERO) > 0) { 
			maxAmountLevel.setAmount(maxAmountLevel.getAmount().subtract(offsetAmount));
		} else if (offsetRate.compareTo(BigDecimal.ZERO) < 0) { 
			maxAmountLevel.setAmount(maxAmountLevel.getAmount().subtract(offsetAmount));
		}
		
		if (offsetRate.compareTo(BigDecimal.ZERO) > 0) { 
			maxAmountLevel.setAssetRate(maxAmountLevel.getAssetRate().subtract(offsetRate));
		} else if (offsetRate.compareTo(BigDecimal.ZERO) < 0) { 
			maxAmountLevel.setAssetRate(maxAmountLevel.getAssetRate().subtract(offsetRate));
		}
		
		for (AssetLevelSecond second : listSecond) {
//			updateOneSecondRate(second, totalAmount);
			updateOneSecondRate(second);
		}
	}

	/**
	 * updateOneSecondRate
	 * 
	 * @param second
	 * @param totalAccount
	 */
	private void updateOneSecondRate(AssetLevelSecond second) {
//		BigDecimal rate = second.getAmount().divide(totalHold, 4, RoundingMode.HALF_UP);
//		second.setAmount(JJCUtility.bigKeep2Decimal(totalAccount.multiply(rate)));
//		rate = rate.multiply(new BigDecimal(100));
//		second.setAssetRate(JJCUtility.bigKeep2Decimal(rate));
		BigDecimal totalAmount = second.getAmount();
		BigDecimal thirdHold = BigDecimal.ZERO;
		for (AssetLevelThird third : second.getListThird()) {
			// updateOneThirdRate(third, second.getAmount());
			thirdHold = thirdHold.add(third.getAmount());
		}
		BigDecimal offsetRate = BigDecimal.ZERO;
		BigDecimal offsetAmount = BigDecimal.ZERO;
		AssetLevelThird maxAmountLevel = second.getListThird().get(0);
		
		for (AssetLevelThird third : second.getListThird()) {
			BigDecimal rate = third.getAmount().divide(thirdHold, 4, RoundingMode.HALF_UP);

			third.setAmount(JJCUtility.bigKeep2Decimal(totalAmount.multiply(rate)));

			rate = rate.multiply(new BigDecimal(100));

			third.setAssetRate(JJCUtility.bigKeep2Decimal(rate));
			
			if (third.getAmount().compareTo(maxAmountLevel.getAmount()) > 0) {
				maxAmountLevel = third;
			} 
			
			offsetRate = offsetRate.add(third.getAssetRate());
			offsetAmount = offsetAmount.add(third.getAmount());
		}
		offsetRate = offsetRate.subtract(new BigDecimal(100));
		offsetAmount = offsetAmount.subtract(totalAmount);
		
		if (offsetAmount.compareTo(BigDecimal.ZERO) > 0) { 
			maxAmountLevel.setAmount(maxAmountLevel.getAmount().subtract(offsetAmount));
		} else if (offsetRate.compareTo(BigDecimal.ZERO) < 0) { 
			maxAmountLevel.setAmount(maxAmountLevel.getAmount().subtract(offsetAmount));
		}
		
		if (offsetRate.compareTo(BigDecimal.ZERO) > 0) { 
			maxAmountLevel.setAssetRate(maxAmountLevel.getAssetRate().subtract(offsetRate));
		} else if (offsetRate.compareTo(BigDecimal.ZERO) < 0) { 
			maxAmountLevel.setAssetRate(maxAmountLevel.getAssetRate().subtract(offsetRate));
		}
		
	}

	/**
	 * updateOneThirdRate
	 * 
	 * @param third
	 * @param totalAccount
	 */
//	private void updateOneThirdRate(AssetLevelThird third, BigDecimal totalAccount, BigDecimal totalHold) {
//		BigDecimal rate = third.getAmount().divide(totalHold, 4, RoundingMode.HALF_UP);
//
//		third.setAmount(JJCUtility.bigKeep2Decimal(totalAccount.multiply(rate)));
//
//		rate = rate.multiply(new BigDecimal(100));
//
//		third.setAssetRate(JJCUtility.bigKeep2Decimal(rate));
//	}

	/**
	 * processAsset
	 * 
	 * @param mapSecond
	 * @param asset
	 * @param holdAccount
	 */
	private <T> void processAsset(Map<String, AssetLevelSecond> mapSecond, T asset, BigDecimal holdAccount) {
		String type = null;
		String assetName = null;
		String oid = null;
		if (asset instanceof IlliquidAsset) {
			type = ((IlliquidAsset) asset).getType();
			assetName = ((IlliquidAsset) asset).getName();
			oid = ((IlliquidAsset) asset).getOid();
		} else if (asset instanceof LiquidAsset) {
			type = ((LiquidAsset) asset).getType();
			assetName = ((LiquidAsset) asset).getName();
			oid = ((LiquidAsset) asset).getOid();
		}
		String genearalType = generateType(type);
		AssetLevelSecond second = null;
		if (mapSecond.containsKey(genearalType)) {
			second = mapSecond.get(genearalType);
		} else {
			second = new AssetLevelSecond();
			mapSecond.put(genearalType, second);

			second.setAssetType(genearalType);
			try {
				second.setTypeName(getNameByType(second.getAssetType()));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		second.setAmount(second.getAmount().add(holdAccount));
		// updateSecondRate(second, totalAccount);
		processThirdLevel(second, assetName, holdAccount, oid);
	}

	/**
	 * processThirdLevel
	 * 
	 * @param second
	 * @param assetName
	 * @param holdAccount
	 */
	private void processThirdLevel(AssetLevelSecond second, String assetName, BigDecimal holdAccount, String oid) {
		//Avoid the same asset.
		for (AssetLevelThird third : second.getListThird()) {
			if (third.getOid().equals(oid)) {
				return;
			}
		}

		AssetLevelThird third = new AssetLevelThird();
		third.setAssetName(assetName);
		third.setAmount(holdAccount);
		third.setOid(oid);
		second.getListThird().add(third);
	}

	static final public String TARGET_XINTUO = "信托类";
	static final public String TARGET_PIAOJU = "票据";
	static final public String TARGET_XIAOFEI_JINRONG = "消费金融类";
	static final public String TARGET_QITA = "其他";

	static final public String TYPE_CASH_FUND = "货币基金";
	public static final String TYPE_AGREEMENT_DEPOSIT = "协定存款";

	/**
	 * general type
	 * 
	 * @param accType
	 * @return
	 */
	private String generateType(String accType) {

		if (StringUtil.in(accType, "TARGETTYPE_05", "TARGETTYPE_06", "TARGETTYPE_07", "TARGETTYPE_04", "TARGETTYPE_03",
				"TARGETTYPE_12", "TARGETTYPE_13", "TARGETTYPE_01", "TARGETTYPE_02", "TARGETTYPE_14")) {
			return "TARGET_XINTUO";// 信托类
		} else if (StringUtil.in(accType, "TARGETTYPE_16", "TARGETTYPE_15")) {
			return "TARGET_PIAOJU";// 票据类
		} else if (StringUtil.in(accType, "TARGETTYPE_17", "TARGETTYPE_18")) {
			return "TARGET_XIAOFEI_JINRONG";// 消费金融类
		} else if (StringUtil.in(accType, "TARGETTYPE_19", "TARGETTYPE_20", "TARGETTYPE_08")) {
			return "TARGET_QITA"; // 其他
		} else if (StringUtil.in(accType, "CASHTOOLTYPE_01")) {
			return "TYPE_CASH_FUND";// 货币基金
		} else if (StringUtil.in(accType, "CASHTOOLTYPE_02")) {
			return "TYPE_AGREEMENT_DEPOSIT";// 协定存款
		}
		return null;
	}

	/**
	 * convert type to name
	 * 
	 * @param accType
	 * @return
	 * @throws Exception
	 */
	private String getNameByType(final String accType) throws Exception {
		Class<?> class1 = null;
		try {
			class1 = Class.forName("com.guohuai.mmp.jiajiacai.productAsset.ProductAssetService");
			Field field1 = class1.getField(accType);
			if (null != field1) {
				String name = (String) field1.get(null);
				return name;
			} else {
				return null;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * second level onePortfolio
	 * 
	 * @param pe
	 * @return
	 */
	// @Transactional
	private RowsRep<AssetLevelSecond> generateOnePortfolio(PortfolioEntity pe, BigDecimal totalAmount) {

		// 取合仓数据
		List<PortfolioIlliquidHoldEntity> listIlliquid = portfolioIlliquidHoldService.findHoldingHold(pe);

		List<PortfolioLiquidHoldEntity> listLiquid = portfolioLiquidHoldService.findHoldingHold(pe);
		Map<String, AssetLevelSecond> mapSecond = new HashMap<String, AssetLevelSecond>();
		if (listIlliquid.size() > 0) {
			for (PortfolioIlliquidHoldEntity he : listIlliquid) {
				IlliquidAsset asset = he.getIlliquidAsset();
				processAsset(mapSecond, asset, he.getHoldShare());

			}
		}

		if (listLiquid.size() > 0) {
			for (PortfolioLiquidHoldEntity he : listLiquid) {
				LiquidAsset asset = he.getLiquidAsset();
				processAsset(mapSecond, asset, he.getHoldShare());

			}
		}

		List<AssetLevelSecond> listSecond = new ArrayList<AssetLevelSecond>(mapSecond.values());
		
		if (listSecond.size() != 0) {

			processSecondRate(listSecond, totalAmount);

			removeZero(listSecond);
		}

		RowsRep<AssetLevelSecond> levelSecond = new RowsRep<AssetLevelSecond>();
		levelSecond.setRows(listSecond);

		return levelSecond;
	}

	/**
	 * removed Zero
	 * 
	 * @param listSecond
	 */
	private void removeZero(List<AssetLevelSecond> listSecond) {
//		List<AssetLevelSecond> removeSecond = new ArrayList<AssetLevelSecond>();
		for (int i = listSecond.size() - 1; i >= 0; i--) {
			AssetLevelSecond second = listSecond.get(i);
			if (second.getAmount().compareTo(BigDecimal.ZERO) == 0) {
				listSecond.remove(second);
//				removeSecond.add(second);
			}
		}
		//Removed.
//		for (AssetLevelSecond second : removeSecond) {
//			listSecond.remove(second);
//		}

		
		for (AssetLevelSecond second : listSecond) {
//			List<AssetLevelThird> removeThird = new ArrayList<AssetLevelThird>();
			for (int i = second.getListThird().size() - 1; i >= 0; i--) {
				AssetLevelThird third = second.getListThird().get(i);
				if (third.getAmount().compareTo(BigDecimal.ZERO) == 0) {
					second.getListThird().remove(third);
//					removeThird.add(third);
				}
			}
			
			//Removed.
//			for (AssetLevelThird third : removeThird) {
//				second.getListThird().remove(third);
//			}
		}

	}

	/**
	 * level First
	 * 
	 * @param uid
	 * @param productType
	 * @return
	 */
	public FirstLevelRep<AssetLevelFirst> levelFirst(String uid, String productType) {
		List<HoldCacheEntity> holds = cacheHoldService.findByInvestorOid(uid);
		// List<AssetLevelFirst> applyDetails = new ArrayList<AssetLevelFirst>();
		FirstLevelRep<AssetLevelFirst> applyDetails = new FirstLevelRep<AssetLevelFirst>();
		BigDecimal totalVolume = BigDecimal.ZERO;
		for (HoldCacheEntity hold : holds) {
			if (PublisherHoldEntity.PUBLISHER_HOLD_HOLD_STATUS_closed.equals(hold.getHoldStatus())
					|| PublisherHoldEntity.PUBLISHER_HOLD_HOLD_STATUS_refunded.equals(hold.getHoldStatus())) {
				continue;
			}
			ProductCacheEntity product = cacheProductService.getProductCacheEntityById(hold.getProductOid());

			if (productType.equals(product.getType())) {
				AssetLevelFirst level = new AssetLevelFirst();
				level.setAmount(hold.getHoldVolume());
				totalVolume = totalVolume.add(hold.getHoldVolume());
				level.setProductName(product.getName());
				level.setProductOid(product.getProductOid());
				applyDetails.add(level);
			}
		}

		if (applyDetails.getRows().size() > 0) {
			for (AssetLevelFirst level : applyDetails.getRows()) {
				BigDecimal rate = level.getAmount().multiply(new BigDecimal(100));
				rate = rate.divide(totalVolume, 2, RoundingMode.HALF_UP);
				level.setProductRate(JJCUtility.bigKeep2Decimal(rate));
			}
		}
		applyDetails.setTotalVolume(totalVolume);
		return applyDetails;
	}

	/**
	 * wish plan level First
	 * 
	 * @param uid
	 * @param productType
	 * @return
	 */
	public FirstLevelRep<AssetLevelFirstWishplan> wpLevelFirst(String uid) {

		List<String> typeL = Arrays.asList(InvestTypeEnum.OnceTourInvest.getCode(),
				InvestTypeEnum.OnceEduInvest.getCode());
		List<String> statusL = Arrays.asList(PlanStatus.SUCCESS.getCode(), PlanStatus.DEPOSITED.getCode(),
				PlanStatus.REDEEMING.getCode());
		List<PlanInvestEntity> tourList = planInvestDao.findSuccessByUidTypeStatus(uid, typeL, statusL);
		BigDecimal totalVolume = BigDecimal.ZERO;
		FirstLevelRep<AssetLevelFirstWishplan> applyDetails = new FirstLevelRep<AssetLevelFirstWishplan>();
		if (tourList.size() > 0) {
			for (PlanInvestEntity entity : tourList) {
				AssetLevelFirstWishplan level = new AssetLevelFirstWishplan();
				level.setAmount(JJCUtility.bigKeep2Decimal(entity.getDepositAmount()));
				totalVolume = totalVolume.add(entity.getDepositAmount());
				level.setWishplanName(JJCUtility.plantype2Str(entity.getPlanType()));
//						+ DateUtil.timestamp2FullStr(entity.getCreateTime()));
				level.setWishplanOid(entity.getOid());
				level.setWishplanType(entity.getPlanType());
				applyDetails.add(level);
			}
		}

		List<PlanMonthEntity> listMonth = planMonthDao.queryAllSuccessPlanMonth(uid);

		for (PlanMonthEntity month : listMonth) {

			AssetLevelFirstWishplan level = new AssetLevelFirstWishplan();
			DepositProfit one = planBaseService.oneMonthPlan(month);// 已经成功投资的，包括月定投已经完成的
			level.setAmount(JJCUtility.bigKeep2Decimal(new BigDecimal(one.getProfitAmount())));
//			level.setAmount(new BigDecimal(one.getDepositAmount()));
			totalVolume = totalVolume.add(level.getAmount());
			level.setWishplanName(JJCUtility.plantype2Str(month.getPlanType()));
//					+ DateUtil.timestamp2FullStr(month.getCreateTime()));
			level.setWishplanOid(month.getOid());
			level.setWishplanType(month.getPlanType());
			applyDetails.add(level);

		}

		if (applyDetails.getRows().size() > 0) {
			BigDecimal offset = BigDecimal.ZERO;
			AssetLevelFirstWishplan maxRateLevel = applyDetails.getRows().get(0);
//			AssetLevelFirstWishplan minRateLevel = applyDetails.getRows().get(0);
			for (AssetLevelFirstWishplan level : applyDetails.getRows()) {
				BigDecimal rate = level.getAmount().multiply(new BigDecimal(100));
				rate = rate.divide(totalVolume, 2, RoundingMode.HALF_UP);
				level.setWishplanRate(JJCUtility.bigKeep2Decimal(rate));
				//offset
				offset = offset.add(level.getWishplanRate());
				if (level.getWishplanRate().compareTo(maxRateLevel.getWishplanRate()) > 0) {
					maxRateLevel = level;
				} 
//				else if (level.getWishplanRate().compareTo(minRateLevel.getWishplanRate()) < 0) {
//					minRateLevel = level;
//				}
			}
			//offset
			offset = offset.subtract(new BigDecimal(100));
			if (offset.compareTo(BigDecimal.ZERO) > 0) { 
				maxRateLevel.setWishplanRate(maxRateLevel.getWishplanRate().subtract(offset));
			} else if (offset.compareTo(BigDecimal.ZERO) < 0) { 
				maxRateLevel.setWishplanRate(maxRateLevel.getWishplanRate().subtract(offset));
			}
			
		}
		applyDetails.setTotalVolume(JJCUtility.bigKeep2Decimal(totalVolume));
		return applyDetails;
	}

	/**
	 * wish plan levelSecond
	 * 
	 * @param productOid
	 * @return
	 */
	public RowsRep<AssetLevelSecond> wplevelSecond(String wishplanOid, String planType, BigDecimal amount) {
		List<PlanProductEntity> successList = null;
		if (InvestTypeEnum.OnceTourInvest.getCode().equals(planType)
				|| InvestTypeEnum.OnceEduInvest.getCode().equals(planType)) {

			List<String> listStatuses = Arrays.asList("SUCCESS", "TOREDEEM", "REDEEMING");
			successList = planDao.queryPlanProductByInvestAndStatusList(wishplanOid, listStatuses);

		} else if (InvestTypeEnum.MonthEduInvest.getCode().equals(planType)
				|| InvestTypeEnum.MonthTourInvest.getCode().equals(planType)
				|| InvestTypeEnum.MonthSalaryInvest.getCode().equals(planType)) {

			List<String> statusL = Arrays.asList(PlanStatus.SUCCESS.getCode(), PlanStatus.DEPOSITED.getCode(),
					PlanStatus.REDEEMING.getCode());
			List<PlanInvestEntity> investList = planInvestDao.findSuccessByMonthDesc(wishplanOid, statusL);

			List<String> listStatuses = Arrays.asList("SUCCESS", "TOREDEEM", "REDEEMING");
			List<String> investOids = new ArrayList<String>();
			for (PlanInvestEntity pe : investList) {
				investOids.add(pe.getOid());
			}
			successList = planDao.queryByInvestAndStatus(investOids, listStatuses);

		} else {
			return null;
		}

		BigDecimal realAmount = BigDecimal.ZERO;
		List<AssetLevelFirst> listFirst = new ArrayList<AssetLevelFirst>();
		for (PlanProductEntity pe : successList) {
			AssetLevelFirst first = new AssetLevelFirst();
			first.setAmount(pe.getAmount());
			first.setProductOid(pe.getProductOid());
			realAmount = realAmount.add(first.getAmount());
			listFirst.add(first);
		}

		// Salary
		BigDecimal incomeAmount = BigDecimal.ZERO;
		if (InvestTypeEnum.MonthSalaryInvest.getCode().equals(planType)) {

			for (PlanProductEntity pe : successList) {
				incomeAmount = incomeAmount.add(pe.getIncome());
				incomeAmount = incomeAmount.add(pe.getIncomeVolume());
			}
			amount = amount.subtract(incomeAmount);
//			amount.setScale(4, RoundingMode.HALF_UP);
//			amount = JJCUtility.bigKeep2Decimal(amount);
		}

		if (realAmount.compareTo(BigDecimal.ZERO) > 0 && amount.compareTo(realAmount) != 0) {
			BigDecimal rate = amount.divide(realAmount, 4, RoundingMode.HALF_UP);
			BigDecimal offset = BigDecimal.ZERO;
			AssetLevelFirst maxAmountLevel = listFirst.get(0);
			for (AssetLevelFirst lf : listFirst) {
				lf.setAmount(lf.getAmount().multiply(rate));
				//offset
				offset = offset.add(lf.getAmount());
				if (lf.getAmount().compareTo(maxAmountLevel.getAmount()) > 0) {
					maxAmountLevel = lf;
				} 
			}
			
			//offset
			offset = offset.subtract(amount);
			if (offset.compareTo(BigDecimal.ZERO) > 0) { 
				maxAmountLevel.setAmount(maxAmountLevel.getAmount().subtract(offset));
			} else if (offset.compareTo(BigDecimal.ZERO) < 0) { 
				maxAmountLevel.setAmount(maxAmountLevel.getAmount().subtract(offset));
			}
		}

		RowsRep<AssetLevelSecond> rowsRep = generateWPPortfolio(listFirst, amount);

		if (incomeAmount.compareTo(BigDecimal.ZERO) > 0) {
			AssetLevelSecond income = new AssetLevelSecond();
			income.setAmount(incomeAmount);
//			income.getAmount().setScale(2, RoundingMode.HALF_UP);
			income.setTypeName("收益");
			rowsRep.getRows().add(income);
		}

		return rowsRep;
	}

	/**
	 * second level onePortfolio
	 * 
	 * @param pe
	 * @return
	 */
	// @Transactional
	private RowsRep<AssetLevelSecond> generateWPPortfolio(List<AssetLevelFirst> listFirst, BigDecimal totalAmount) {
		// RowsRep<AssetLevelSecond> levelSecond = new RowsRep<AssetLevelSecond>();
		Map<String, AssetLevelSecond> mapSecond = new HashMap<String, AssetLevelSecond>();
		for (AssetLevelFirst af : listFirst) {
			PortfolioEntity pe = portfolioDao.queryByProductOid(af.getProductOid());
			oneWPPortfolio(pe, af.getAmount(), mapSecond);
		}

		List<AssetLevelSecond> listSecond = new ArrayList<AssetLevelSecond>(mapSecond.values());

		if (listSecond.size() != 0) {
			processSecondRate(listSecond, totalAmount);
			removeZero(listSecond);
		}

		RowsRep<AssetLevelSecond> levelSecond = new RowsRep<AssetLevelSecond>();
		levelSecond.setRows(listSecond);

		return levelSecond;

	}

	private void oneWPPortfolio(PortfolioEntity pe, BigDecimal totalAmount, Map<String, AssetLevelSecond> mapSecond) {

		// 取合仓数据
		List<PortfolioIlliquidHoldEntity> listIlliquid = portfolioIlliquidHoldService.findHoldingHold(pe);

		List<PortfolioLiquidHoldEntity> listLiquid = portfolioLiquidHoldService.findHoldingHold(pe);

		if (listIlliquid.size() > 0) {
			for (PortfolioIlliquidHoldEntity he : listIlliquid) {
				IlliquidAsset asset = he.getIlliquidAsset();
				processAsset(mapSecond, asset, he.getHoldShare());

			}
		}

		if (listLiquid.size() > 0) {
			for (PortfolioLiquidHoldEntity he : listLiquid) {
				LiquidAsset asset = he.getLiquidAsset();
				processAsset(mapSecond, asset, he.getHoldShare());

			}
		}

	}

}
