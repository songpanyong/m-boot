package com.guohuai.ams.duration.fact.income;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;

import com.guohuai.ams.acct.books.AccountBook;
import com.guohuai.ams.acct.books.AccountBookService;
import com.guohuai.ams.acct.books.document.SPVDocumentService;
import com.guohuai.ams.portfolio.entity.PortfolioEntity;
import com.guohuai.ams.portfolio.service.PortfolioService;
import com.guohuai.ams.product.Product;
import com.guohuai.ams.product.ProductDao;
import com.guohuai.ams.product.ProductDecimalFormat;
import com.guohuai.ams.product.ProductService;
import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.basic.component.ext.web.PageResp;
import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.DateUtil;
import com.guohuai.component.util.DecimalUtil;
import com.guohuai.component.util.StringUtil;
import com.guohuai.mmp.jiajiacai.caculate.JJCUtility;
import com.guohuai.mmp.publisher.investor.InterestRateMethodService;
import com.guohuai.mmp.publisher.investor.interest.result.InterestResultEntity;
import com.guohuai.mmp.publisher.product.rewardincomepractice.PracticeService;
import com.guohuai.mmp.publisher.product.rewardincomepractice.RewardIsNullRep;
import com.guohuai.operate.api.AdminSdk;
import com.guohuai.operate.api.objs.admin.AdminObj;

/**
 * 收益分配
 * 
 * @author wangyan
 *
 */
@Service
public class IncomeDistributionService {
	private static final Logger logger = LoggerFactory.getLogger(IncomeDistributionService.class);
	@Autowired
	private IncomeEventDao incomeEventDao;
	@Autowired
	private IncomeAllocateDao incomeAllocateDao;
	@Autowired
	private AccountBookService accountBookService;
	@Autowired
	private SPVDocumentService spvDocumentService;
	@Autowired
	private ProductDao productDao;
	@Autowired
	private ProductService productService;
	@Autowired
	private PortfolioService portfolioService;
	@Autowired
	private AdminSdk adminSdk;
	@Autowired
	private PracticeService practiceService;
	@Autowired
	private InterestRateMethodService interestRateMethodService;
	

	public IncomeAllocateCalcResp getIncomeAdjustData(String portfolioOid) {
		IncomeAllocateCalcResp resp = new IncomeAllocateCalcResp();

		BigDecimal undisIncome = new BigDecimal("0");// 未分配收益
		BigDecimal productTotalScale = new BigDecimal("0");// 产品总规模 王国处获取
		BigDecimal productRewardBenefit = new BigDecimal("0");// 奖励收益 王国处获取
		BigDecimal productCouponBenefit = new BigDecimal("0");// 加息收益 王国处获取
		BigDecimal productDistributionIncome = new BigDecimal("0");// 分配收益
		BigDecimal productAnnualYield = new BigDecimal("0");// 年化收益率

		Product p = null;
		List<Product> ps = productService.getProductListByPortfolioOid(portfolioOid);
		if (ps != null && ps.size() > 0) {
			p = ps.get(0);

//			if (Product.YES.equals(p.getIsAutoAssignIncome())) {
//				throw AMPException.getException("自动派息，不能手动派息!");
//			}

			resp.setProductOid(p.getOid());
			resp.setIncomeCalcBasis(p.getIncomeCalcBasis());
		} else {
			resp.setIncomeCalcBasis("365");
		}

		IncomeEvent lastIncomeEvent = this.findLastValidIncomeEvent(portfolioOid);// 查询该资产池最近一天的收益分配日 非 IncomeEvent.STATUS_Fail 和 非 IncomeEvent.STATUS_Delete

		if (lastIncomeEvent == null) {// 首次分配收益
			resp.setIncomeDate("");// 收益分配日
			resp.setLastIncomeDate("");// 上一收益分配日
		} else {// 非首次分配收益
			if (IncomeEvent.STATUS_Create.equals(lastIncomeEvent.getStatus())) {
				// error.define[60004]=请先审核上一次的收益分配!(CODE:60004)
				throw AMPException.getException(60004);
			} else if (IncomeEvent.STATUS_Allocating.equals(lastIncomeEvent.getStatus())) {
				// error.define[60005]=请先等待上一次的收益分配完成!(CODE:60005)
				throw AMPException.getException(60005);
			} else if (IncomeEvent.STATUS_AllocateFail.equals(lastIncomeEvent.getStatus())) {
				// error.define[60006]=请先完成上一次的收益分配!(CODE:60006)
				throw AMPException.getException(60006);
			} else {
				if (lastIncomeEvent.getBaseDate().getTime() == DateUtil.getBeforeDate().getTime()) {
					// error.define[60010]=今日已经申请过昨日收益分配!(CODE:60010)
					throw AMPException.getException(60010);
				} else {
					// 上一收益分配日
					resp.setLastIncomeDate(DateUtil.format(lastIncomeEvent.getBaseDate()));
					Date incomeDate = DateUtil.addSQLDays(lastIncomeEvent.getBaseDate(), 1);

					Date today = DateUtil.formatUtilToSql(DateUtil.getCurrDate());
					if (incomeDate.getTime() >= today.getTime()) {
						// error.define[60011]=今日只能申请昨日以及昨日之前的收益分配!(CODE:60011)
						throw AMPException.getException(60011);
					}
					resp.setIncomeDate(DateUtil.format(incomeDate));// 收益分配日
					// 计提费用
					BigDecimal feeValue = BigDecimal.ZERO;
					if (feeValue != null) {
						resp.setFeeValue(ProductDecimalFormat.format(feeValue, "0.##"));
						if (feeValue.compareTo(new BigDecimal("0")) > 0) {
							resp.setFeeValueStr(resp.getFeeValue() + "元");
						}
					}
					if (p != null) {
						RewardIsNullRep practice = practiceService.rewardIsNullRep(p, incomeDate);// 王国
						if (practice != null) {
							if (practice.getTotalHoldVolume() != null) {// 持有人总份额
								productTotalScale = practice.getTotalHoldVolume();
							}
							if (practice.getTotalRewardIncome() != null) {// 奖励收益
								productRewardBenefit = practice.getTotalRewardIncome();
							}
							if (practice.getTotalCouponIncome() != null) {// 奖励收益
								productCouponBenefit = practice.getTotalCouponIncome();
							}
							
						}
					}
				}
			}
		}

		Map<String, AccountBook> accountBookMap = accountBookService.find(portfolioOid, "1111", "1201", "2201");
		if (accountBookMap != null && accountBookMap.size() > 0) {
			// 资产池
			AccountBook investmentAssets = accountBookMap.get("1111");// 资产池 投资资产
			if (investmentAssets != null) {
				resp.setInvestmentAssets(ProductDecimalFormat.format(investmentAssets.getBalance(), "0.##"));
				if (investmentAssets.getBalance().compareTo(new BigDecimal("0")) > 0) {
					resp.setInvestmentAssetsStr(resp.getInvestmentAssets() + "元");// 投资资产
				}
			}
			AccountBook apUndisIncome = accountBookMap.get("2201");// 资产池 未分配收益
			if (apUndisIncome != null) {
				resp.setApUndisIncome(ProductDecimalFormat.format(apUndisIncome.getBalance(), "0.##"));
				if (apUndisIncome.getBalance().compareTo(new BigDecimal("0")) > 0) {
					resp.setApUndisIncomeStr(resp.getApUndisIncome() + "元");// 资产池未分配收益
				}
			}
			AccountBook apReceiveIncome = accountBookMap.get("1201");// 资产池 应收投资收益
			if (apReceiveIncome != null) {
				resp.setApReceiveIncome(ProductDecimalFormat.format(apReceiveIncome.getBalance(), "0.##"));
				if (apReceiveIncome.getBalance().compareTo(new BigDecimal("0")) > 0) {
					resp.setApReceiveIncomeStr(resp.getApReceiveIncome() + "元");// 应收投资收益
				}
				undisIncome = apReceiveIncome.getBalance().subtract(productRewardBenefit).subtract(productDistributionIncome).subtract(productCouponBenefit);
			}
		}

		resp.setProductTotalScale(ProductDecimalFormat.format(productTotalScale, "0.##"));// 产品总规模 王国处获取
		if (productTotalScale.compareTo(new BigDecimal("0")) > 0) {
			resp.setProductTotalScaleStr(productTotalScale + "元");// 产品总规模
		}
		resp.setProductRewardBenefit(ProductDecimalFormat.format(productRewardBenefit, "0.##"));// 奖励收益 王国处获取
		if (productRewardBenefit.compareTo(new BigDecimal("0")) > 0) {
			resp.setProductRewardBenefitStr(productRewardBenefit + "元");// 奖励收益
		}
		resp.setProductCouponBenefit(ProductDecimalFormat.format(productCouponBenefit, "0.##"));// 奖励收益 王国处获取
		if (productCouponBenefit.compareTo(new BigDecimal("0")) > 0) {
			resp.setProductCouponBenefitStr(productCouponBenefit + "元");// 奖励收益
		}
		// 年化收益率=分配收益/产品总规模*365
		if (productTotalScale.compareTo(new BigDecimal("0")) != 0) {
			productAnnualYield = productDistributionIncome.multiply(new BigDecimal(resp.getIncomeCalcBasis())).divide(productTotalScale, 4, RoundingMode.HALF_UP);// 分配收益/产品总规模*365
		}

		resp.setProductDistributionIncome(ProductDecimalFormat.format(productDistributionIncome, "0.##"));// 分配收益
		resp.setProductAnnualYield(ProductDecimalFormat.format(ProductDecimalFormat.multiply(productAnnualYield), "0.##"));// 年化收益率 单位%
		resp.setAssetpoolOid(portfolioOid);
		resp.setUndisIncome(ProductDecimalFormat.format(undisIncome, "0.##"));// 未分配收益

		BigDecimal receiveIncome = new BigDecimal("0");// 应收投资收益
		if (undisIncome.compareTo(new BigDecimal("0")) < 0) {
			receiveIncome = undisIncome.negate();
		}
		resp.setUndisIncome(ProductDecimalFormat.format(receiveIncome, "0.##"));// 应收投资收益

		BigDecimal totalScale = productTotalScale.add(productRewardBenefit).add(productDistributionIncome).add(productCouponBenefit);
		resp.setTotalScale(ProductDecimalFormat.format(totalScale, "0.##"));// 产品总规模
		resp.setAnnualYield(ProductDecimalFormat.format(productAnnualYield, "0.##"));// 产品年化收益率

		// 年化收益率=分配收益/产品总规模*365
		// 万份收益=年化收益率/365*10000
		if (productTotalScale.compareTo(new BigDecimal("0")) != 0) {
			resp.setMillionCopiesIncome(ProductDecimalFormat.format(productDistributionIncome.multiply(new BigDecimal("10000")).divide(productTotalScale, 4, RoundingMode.HALF_UP), "0.####"));// 万份收益
		} else {
			resp.setMillionCopiesIncome(ProductDecimalFormat.format(new BigDecimal("0"), "0.####"));// 万份收益
		}

		return resp;
	}

	/**
	 * 根据资产池和收益分配日获取 产品总规模和奖励收益
	 * 
	 * @param assetPoolOid
	 * @param incomeDate
	 * @return
	 */
	public IncomeAllocateCalcResp getTotalScaleRewardBenefit(String assetPoolOid, String incomeDate) {
		IncomeAllocateCalcResp resp = new IncomeAllocateCalcResp();

		BigDecimal productTotalScale = new BigDecimal("0");// 产品总规模 王国处获取
		BigDecimal productRewardBenefit = new BigDecimal("0");// 奖励收益 王国处获取
		BigDecimal productCouponBenefit = new BigDecimal("0");// 加息收益 王国处获取

		BigDecimal feeValue = new BigDecimal("0");// 计提费用

		List<Product> ps = productService.getProductListByPortfolioOid(assetPoolOid);
		if (ps != null && ps.size() > 0) {
			Date incomeSqlDate = DateUtil.parseToSqlDate(incomeDate);

			resp.setFeeValue(ProductDecimalFormat.format(feeValue, "0.##"));
			if (feeValue.compareTo(new BigDecimal("0")) > 0) {
				resp.setFeeValueStr(resp.getFeeValue() + "元");
			}

			RewardIsNullRep practice = practiceService.rewardIsNullRep(ps.get(0), incomeSqlDate);// 王国
			if (practice != null) {
				if (practice.getTotalHoldVolume() != null) {// 持有人总份额
					productTotalScale = practice.getTotalHoldVolume();
				}
				if (practice.getTotalRewardIncome() != null) {// 奖励收益
					productRewardBenefit = practice.getTotalRewardIncome();
				}
				if (practice.getTotalCouponIncome() != null) {// 加息收益
					productCouponBenefit = practice.getTotalCouponIncome();
				}
			}

		}

//		resp.setProductTotalScale(ProductDecimalFormat.format(productTotalScale, "0.##"));// 产品总规模 王国处获取
		resp.setProductTotalScale(productTotalScale.toPlainString());// 产品总规模 王国处获取
		if (productTotalScale.compareTo(new BigDecimal("0")) > 0) {
			resp.setProductTotalScaleStr(productTotalScale + "元");// 产品总规模
		}
		resp.setProductRewardBenefit(ProductDecimalFormat.format(productRewardBenefit, "0.##"));// 奖励收益 王国处获取
		if (productRewardBenefit.compareTo(new BigDecimal("0")) > 0) {
			resp.setProductRewardBenefitStr(productRewardBenefit + "元");// 奖励收益
		}
		resp.setProductCouponBenefit(ProductDecimalFormat.format(productCouponBenefit, "0.##"));// 加息收益 王国处获取
		if (productCouponBenefit.compareTo(new BigDecimal("0")) > 0) {
			resp.setProductCouponBenefitStr(productCouponBenefit + "元");// 加息收益
		}
		return resp;
	}

	/**
	 * 查询该资产池最近一天的收益分配日 非 IncomeEvent.STATUS_Fail 和 非 IncomeEvent.STATUS_Delete
	 * 
	 * @param assetPoolOid
	 * @return
	 */
	private IncomeEvent findLastValidIncomeEvent(final String assetPoolOid) {
		Specification<IncomeEvent> spec = new Specification<IncomeEvent>() {
			@Override
			public Predicate toPredicate(Root<IncomeEvent> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.equal(root.get("portfolio").get("oid").as(String.class), assetPoolOid);
			}
		};

		Specification<IncomeEvent> statusSpec = new Specification<IncomeEvent>() {
			@Override
			public Predicate toPredicate(Root<IncomeEvent> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.and(cb.notEqual(root.get("status").as(String.class), IncomeEvent.STATUS_Fail), 
						cb.notEqual(root.get("status").as(String.class), IncomeEvent.STATUS_Delete));
			}
		};
		spec = Specifications.where(spec).and(statusSpec);

		List<IncomeEvent> ims = incomeEventDao.findAll(spec, new Sort(new Order(Direction.DESC, "baseDate")));
		if (ims != null && ims.size() > 0) {
			return ims.get(0);
		}
		return null;

	}

	@Transactional
	public IncomeAllocateBaseResp saveIncomeAdjust(IncomeAllocateForm form, String operator) throws ParseException {
		IncomeAllocateBaseResp response = new IncomeAllocateBaseResp();

		PortfolioEntity portfolio = this.portfolioService.getByOid(form.getAssetpoolOid());
		if (portfolio == null) {
			throw AMPException.getException(60000);
		}
		List<Product> ps = productService.getProductListByPortfolioOid(form.getAssetpoolOid());
		Product product = null;
		if (ps != null && ps.size() > 0) {
			product = ps.get(0);
		}

		if (practiceService.isPractice(product, DateUtil.parseToSqlDate(form.getIncomeDistrDate()))) {

			IncomeEvent lastIncomeEvent = this.findLastValidIncomeEvent(form.getAssetpoolOid());// 查询该资产池最近一天的收益分配日 非 IncomeEvent.STATUS_Fail 和 非 IncomeEvent.STATUS_Delete
			if (lastIncomeEvent != null) {// 非首次分配收益
				if (IncomeEvent.STATUS_Create.equals(lastIncomeEvent.getStatus())) {
					response.setErrorCode(-1);
					response.setErrorMessage("请先审核" + DateUtil.formatDate(lastIncomeEvent.getBaseDate().getTime()) + "的收益分配!");
					throw AMPException.getException(60004);
				} else if (IncomeEvent.STATUS_Allocating.equals(lastIncomeEvent.getStatus())) {
					response.setErrorCode(-1);
					response.setErrorMessage("请先等待" + DateUtil.formatDate(lastIncomeEvent.getBaseDate().getTime()) + "的收益分配完成!");
					throw AMPException.getException(60005);
				} else if (IncomeEvent.STATUS_AllocateFail.equals(lastIncomeEvent.getStatus())) {
					response.setErrorCode(-1);
					response.setErrorMessage("请先完成" + DateUtil.formatDate(lastIncomeEvent.getBaseDate().getTime()) + "的收益分配!");
					throw AMPException.getException(60006);
				} else if (lastIncomeEvent.getBaseDate().getTime() == DateUtil.getBeforeDate().getTime()) {
					response.setErrorCode(-1);
					response.setErrorMessage("今日已经申请过昨日收益分配");
					throw AMPException.getException(60010);
				} else if (lastIncomeEvent.getBaseDate().getTime() >= DateUtil.formatUtilToSql(DateUtil.getCurrDate()).getTime()) {
					response.setErrorCode(-1);
					response.setErrorMessage("今日只能申请昨日以及昨日之前的收益分配");
					throw AMPException.getException(60011);
				}
			}

			Timestamp now = new Timestamp(System.currentTimeMillis());

			IncomeEvent incomeEvent = new IncomeEvent();
			incomeEvent.setOid(StringUtil.uuid());
			incomeEvent.setPortfolio(portfolio);
			incomeEvent.setBaseDate(DateUtil.parseToSqlDate(form.getIncomeDistrDate()));
			incomeEvent.setAllocateIncome(new BigDecimal(form.getProductRewardBenefit()).add(new BigDecimal(form.getProductDistributionIncome())).add(new BigDecimal(form.getProductCouponBenefit())));// 总分配收益
			//Truncated
			incomeEvent.setAllocateIncome(JJCUtility.bigKeep4Decimal(incomeEvent.getAllocateIncome()));
			incomeEvent.setCreator(operator);
			incomeEvent.setCreateTime(now);
			incomeEvent.setDays(1);
			incomeEvent.setStatus(IncomeEvent.STATUS_Create);
			incomeEventDao.save(incomeEvent);

			IncomeAllocate incomeAllocate = new IncomeAllocate();
			incomeAllocate.setOid(StringUtil.uuid());

			if (product != null && Product.TYPE_Producttype_02.equals(product.getType().getOid())) {//活期产品
				incomeAllocate.setAllocateIncomeType(IncomeAllocate.ALLOCATE_INCOME_TYPE_durationIncome);
			} else if (product != null && Product.TYPE_Producttype_01.equals(product.getType().getOid())) {
				incomeAllocate.setAllocateIncomeType(IncomeAllocate.ALLOCATE_INCOME_TYPE_raiseIncome);
			}

			incomeAllocate.setIncomeEvent(incomeEvent);
			incomeAllocate.setProduct(product);
			incomeAllocate.setBaseDate(incomeEvent.getBaseDate());
			incomeAllocate.setCapital(new BigDecimal(form.getProductTotalScale()));// 产品总规模
			incomeAllocate.setAllocateIncome(new BigDecimal(form.getProductDistributionIncome()));// 分配基础收益
			//Truncated
			incomeAllocate.setAllocateIncome(JJCUtility.bigKeep4Decimal(incomeAllocate.getAllocateIncome()));
			incomeAllocate.setRewardIncome(new BigDecimal(form.getProductRewardBenefit()));// 分配奖励收益
			incomeAllocate.setRatio(ProductDecimalFormat.divide(new BigDecimal(form.getProductAnnualYield())));// 收益率(年化) form.getProductAnnualYield()单位为%
			incomeAllocate.setCouponIncome(new BigDecimal(form.getProductCouponBenefit()));	// 加息收益
			/**
			 * 万份收益=年化收益率/365*10000
			 */
			BigDecimal productAnnualYield = ProductDecimalFormat.divide(new BigDecimal(form.getProductAnnualYield())); // 产品范畴 年化收益率
			//		BigDecimal incomeCalcBasis = new BigDecimal(product.getIncomeCalcBasis());// 计算基础
			//		BigDecimal millionCopiesIncome = productAnnualYield.multiply(new BigDecimal("10000"))
			//				.divide(incomeCalcBasis, 4, RoundingMode.HALF_UP);// 万份收益

			incomeAllocate.setWincome(getWinIncome(productAnnualYield, product));

			incomeAllocate.setDays(1);// 收益分配天数
			incomeAllocate.setSuccessAllocateIncome(new BigDecimal("0"));// 成功分配基础收益金额
			incomeAllocate.setSuccessAllocateRewardIncome(new BigDecimal("0"));// 成功分配奖励收益金额
			incomeAllocate.setSuccessAllocateCouponIncome(new BigDecimal("0"));// 成功分配加息收益金额
			incomeAllocate.setLeftAllocateIncome(new BigDecimal(form.getProductRewardBenefit()).add(new BigDecimal(form.getProductDistributionIncome())).add(new BigDecimal(form.getProductCouponBenefit())));// 剩余总分配收益金额
			//Truncated
			incomeAllocate.setLeftAllocateIncome(JJCUtility.bigKeep4Decimal(incomeAllocate.getLeftAllocateIncome()));
			incomeAllocate.setLeftAllocateBaseIncome(new BigDecimal(form.getProductDistributionIncome()));//剩余分配基础金额
			incomeAllocate.setLeftAllocateRewardIncome(new BigDecimal(form.getProductRewardBenefit()));//剩余分配奖励金额
			incomeAllocate.setLeftAllocateCouponIncome(new BigDecimal(form.getProductCouponBenefit()));//剩余分配加息金额
			incomeAllocate.setSuccessAllocateInvestors(0);// 成功分配投资者数
			incomeAllocate.setFailAllocateInvestors(0);// 失败分配投资者数
			incomeAllocate = incomeAllocateDao.save(incomeAllocate);
			response.setOid(incomeAllocate.getOid());
		} else {
			response.setErrorCode(-1);
			response.setErrorMessage("请先进行该资产池对应的产品收益试算，才能进行收益分配");
		}

		return response;
	}

	public BigDecimal getWinIncome(BigDecimal annualInterest, Product product) {
		return DecimalUtil.setScaleDown(new BigDecimal("10000").multiply(new BigDecimal(Math.pow(1 + annualInterest.doubleValue(), 1 / Double.parseDouble(product.getIncomeCalcBasis()))).subtract(BigDecimal.ONE).setScale(7, BigDecimal.ROUND_HALF_UP)));
	}

	public IncomeDistributionResp getIncomeAdjust(String oid) {
		IncomeAllocate incomeAllocate = incomeAllocateDao.findOne(oid);
		IncomeDistributionResp idr = new IncomeDistributionResp(incomeAllocate);

		Map<String, AdminObj> adminObjMap = new HashMap<String, AdminObj>();
		AdminObj adminObj = null;
		if (!StringUtil.isEmpty(idr.getCreator())) {
			if (adminObjMap.get(idr.getCreator()) == null) {
				try {
					adminObj = adminSdk.getAdmin(idr.getCreator());
					adminObjMap.put(idr.getCreator(), adminObj);
				} catch (Exception e) {
				}
			}
			if (adminObjMap.get(idr.getCreator()) != null) {
				idr.setCreator(adminObjMap.get(idr.getCreator()).getName());
			}
		}
		if (!StringUtil.isEmpty(idr.getAuditor())) {
			if (adminObjMap.get(idr.getAuditor()) == null) {
				try {
					adminObj = adminSdk.getAdmin(idr.getAuditor());
					adminObjMap.put(idr.getAuditor(), adminObj);
				} catch (Exception e) {
				}

			}
			if (adminObjMap.get(idr.getAuditor()) != null) {
				idr.setAuditor(adminObjMap.get(idr.getAuditor()).getName());
			}
		}

		return idr;
	}

	/**
	 * 资产池 收益分配列表
	 * 
	 * @param spec
	 * @param pageable
	 * @return
	 */
	public PageResp<IncomeDistributionResp> getIncomeAdjustList(Specification<IncomeAllocate> spec, Pageable pageable) {
		Page<IncomeAllocate> cas = this.incomeAllocateDao.findAll(spec, pageable);

		PageResp<IncomeDistributionResp> pagesRep = new PageResp<IncomeDistributionResp>();

		if (cas != null && cas.getContent() != null && cas.getTotalElements() > 0) {
			List<IncomeDistributionResp> rows = new ArrayList<IncomeDistributionResp>();

			Map<String, AdminObj> adminObjMap = new HashMap<String, AdminObj>();
			AdminObj adminObj = null;

			for (IncomeAllocate ia : cas) {
				IncomeDistributionResp idr = new IncomeDistributionResp(ia);

				if (!StringUtil.isEmpty(idr.getCreator())) {
					if (adminObjMap.get(idr.getCreator()) == null) {
						try {
							adminObj = adminSdk.getAdmin(idr.getCreator());
							adminObjMap.put(idr.getCreator(), adminObj);
						} catch (Exception e) {
						}
					}
					if (adminObjMap.get(idr.getCreator()) != null) {
						idr.setCreator(adminObjMap.get(idr.getCreator()).getName());
					}
				}
				if (!StringUtil.isEmpty(idr.getAuditor())) {
					if (adminObjMap.get(idr.getAuditor()) == null) {
						try {
							adminObj = adminSdk.getAdmin(idr.getAuditor());
							adminObjMap.put(idr.getAuditor(), adminObj);
						} catch (Exception e) {
						}

					}
					if (adminObjMap.get(idr.getAuditor()) != null) {
						idr.setAuditor(adminObjMap.get(idr.getAuditor()).getName());
					}
				}

				rows.add(idr);
			}
			pagesRep.setRows(rows);
		}
		pagesRep.setTotal(cas.getTotalElements());

		return pagesRep;
	}

	@Transactional
	public BaseResp auditPassIncomeAdjust(String oid, String operator) {
		BaseResp response = new BaseResp();

		IncomeAllocate incomeAllocate = incomeAllocateDao.findOne(oid);
		IncomeEvent ie = this.incomeEventDao.findOne(incomeAllocate.getIncomeEvent().getOid());

		if (ie != null && !IncomeEvent.STATUS_Create.equals(ie.getStatus())) {
			response.setErrorCode(-1);
			response.setErrorMessage("待审核状态才能审核!");
			throw AMPException.getException(60007);
		}

		Timestamp now = new Timestamp(System.currentTimeMillis());

		ie.setAuditor(operator);
		ie.setAuditTime(now);
		ie.setStatus(IncomeEvent.STATUS_Allocating);// 发放中
		incomeEventDao.saveAndFlush(ie);

		this.interestRateMethodService.interest(incomeAllocate.getOid(), incomeAllocate.getProduct().getOid());

		return response;
	}

	/**
	 * 发放收益 乐超收益分配完成后调用
	 * 
	 * @param allocateIncomeReturn
	 */
	@Transactional
	public void allocateIncome(InterestResultEntity allocateIncomeReturn) throws Exception {
		Product p = productDao.findOne(allocateIncomeReturn.getProduct().getOid());
		IncomeAllocate im = incomeAllocateDao.findOne(allocateIncomeReturn.getIncomeAllocate().getOid());
		IncomeEvent ie = incomeEventDao.findOne(im.getIncomeEvent().getOid());

		/**
		 * 成功分配基础收益金额
		 */
		BigDecimal successAllocateBaseIncome = BigDecimal.ZERO;
		if (allocateIncomeReturn.getSuccessAllocateBaseIncome() != null) {
			successAllocateBaseIncome = allocateIncomeReturn.getSuccessAllocateBaseIncome();
		}

		/**
		 * 成功分配奖励收益金额
		 */
		BigDecimal successAllocateRewardIncome = BigDecimal.ZERO;
		if (allocateIncomeReturn.getSuccessAllocateRewardIncome() != null) {
			successAllocateRewardIncome = allocateIncomeReturn.getSuccessAllocateRewardIncome();
		}
		
		/**
		 * 成功分配加息收益金额
		 */
		BigDecimal successAllocateCouponIncome = BigDecimal.ZERO;
		if (allocateIncomeReturn.getSuccessAllocateCouponIncome() != null) {
			successAllocateCouponIncome = allocateIncomeReturn.getSuccessAllocateCouponIncome();
		}

		/**
		 * 成功分配投资者数
		 */
		Integer successAllocateInvestors = 0;
		if (allocateIncomeReturn.getSuccessAllocateInvestors() != null) {
			successAllocateInvestors = allocateIncomeReturn.getSuccessAllocateInvestors();
		}

		im.setSuccessAllocateIncome(im.getSuccessAllocateIncome().add(successAllocateBaseIncome));// 成功分配基础收益金额
		im.setSuccessAllocateRewardIncome(im.getSuccessAllocateRewardIncome().add(successAllocateRewardIncome));// 成功分配奖励收益金额
		im.setSuccessAllocateCouponIncome(im.getSuccessAllocateCouponIncome().add(successAllocateCouponIncome));// 成功分配加息收益金额
		im.setLeftAllocateIncome(allocateIncomeReturn.getLeftAllocateIncome());// 未分配总金额
		//Truncated
		im.setLeftAllocateIncome(JJCUtility.bigKeep4Decimal(im.getLeftAllocateIncome()));

		im.setLeftAllocateBaseIncome(allocateIncomeReturn.getLeftAllocateBaseIncome());// 剩余分配基础金额
		im.setLeftAllocateRewardIncome(allocateIncomeReturn.getLeftAllocateRewardIncome());//剩余分配奖励金额
		im.setLeftAllocateCouponIncome(allocateIncomeReturn.getLeftAllocateCouponIncome());//剩余分配加息金额

		im.setSuccessAllocateInvestors(im.getSuccessAllocateInvestors() + successAllocateInvestors);// 成功分配投资者数
		im.setFailAllocateInvestors(allocateIncomeReturn.getFailAllocateInvestors());// 失败分配投资者数
		incomeAllocateDao.saveAndFlush(im);

		ie.setStatus(allocateIncomeReturn.getStatus());// 分配状态
		incomeEventDao.saveAndFlush(ie);

		/**
		 * 成功分配基础收益金额+成功分配奖励收益金额+成功分配加息收益金额
		 */
		BigDecimal successAllIncome = successAllocateBaseIncome.add(successAllocateRewardIncome).add(successAllocateCouponIncome);
		logger.info("successAllIncome={}", successAllIncome);
		if (successAllIncome.compareTo(BigDecimal.ZERO) != 0) {// 成功分配收益金额
			// 资产池收益分配成功发送更新产品currentVolume
			logger.info("successAllIncome={}, productOid={}", successAllIncome, p.getOid());
			if (!p.getIncomeDealType().equals(Product.PRODUCT_incomeDealType_cash)) {
				this.productDao.incomeAllocateAdjustCurrentVolume(p.getOid(), successAllIncome, im.getRatio());
			} else {
				this.productDao.incomeAllocateAdjustCurrentVolume(p.getOid(), im.getRatio());
			}
			
			// 会计分录接口1
			spvDocumentService.incomeAllocate(ie.getPortfolio().getOid(), ie.getOid(), successAllIncome);

		}
		if (im.getAllocateIncomeType().equals(IncomeAllocate.ALLOCATE_INCOME_TYPE_durationIncome) && Product.TYPE_Producttype_01.equals(p.getType().getOid())) {
			this.productService.repayInterestOk(p.getOid());
		}

	}

	@Transactional
	public BaseResp auditFailIncomeAdjust(String oid, String operator) {
		BaseResp response = new BaseResp();

		IncomeAllocate incomeAllocate = incomeAllocateDao.findOne(oid);
		IncomeEvent ie = this.incomeEventDao.findOne(incomeAllocate.getIncomeEvent().getOid());

		if (ie != null && !IncomeEvent.STATUS_Create.equals(ie.getStatus())) {
			response.setErrorCode(-1);
			response.setErrorMessage("待审核状态才能审核!");
			throw AMPException.getException(60007);
		}

		Timestamp now = new Timestamp(System.currentTimeMillis());

		ie.setAuditor(operator);
		ie.setAuditTime(now);
		ie.setStatus(IncomeEvent.STATUS_Fail);
		incomeEventDao.saveAndFlush(ie);

		return response;
	}

	@Transactional
	public IncomeAllocate deleteIncomeAdjust(String oid, String operator) {
		IncomeAllocate incomeAllocate = incomeAllocateDao.findOne(oid);
		IncomeEvent ie = this.incomeEventDao.findOne(incomeAllocate.getIncomeEvent().getOid());

		if (ie != null && !IncomeEvent.STATUS_Create.equals(ie.getStatus()) && !IncomeEvent.STATUS_Fail.equals(ie.getStatus())) {
			throw AMPException.getException(60008);
		}

		Timestamp now = new Timestamp(System.currentTimeMillis());
		ie.setAuditor(operator);
		ie.setAuditTime(now);
		ie.setStatus(IncomeEvent.STATUS_Delete);
		incomeEventDao.saveAndFlush(ie);
		return incomeAllocate;
	}

	@Transactional
	public BaseResp allocateIncomeAgain(String oid, String operator) {
		BaseResp response = new BaseResp();

		IncomeAllocate incomeAllocate = incomeAllocateDao.findOne(oid);
		IncomeEvent ie = this.incomeEventDao.findOne(incomeAllocate.getIncomeEvent().getOid());

		if (ie != null && !IncomeEvent.STATUS_AllocateFail.equals(ie.getStatus())) {
			response.setErrorCode(-1);
			response.setErrorMessage("只有分配失败的收益分配才可以再次发送!");
			throw AMPException.getException(60009);
		}

		ie.setStatus(IncomeEvent.STATUS_Allocating);// 发放中
		incomeEventDao.saveAndFlush(ie);

		this.interestRateMethodService.interest(incomeAllocate.getOid(), incomeAllocate.getProduct().getOid());
		return response;
	}

	/**
	 * @author yuechao 获取最近的收益发放日期
	 * @param productOid
	 * @return
	 */
	public Date getLatestIncomeDate(String productOid) {
		Date incomeDate = this.incomeEventDao.getLatestIncomeDate(productOid);
		return incomeDate;
	}

	public boolean isIncomeAllocated(final String productOid, final Date baseDate) {
		Specification<IncomeAllocate> spec = new Specification<IncomeAllocate>() {
			@Override
			public Predicate toPredicate(Root<IncomeAllocate> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.and(cb.equal(root.get("product").get("oid").as(String.class), productOid), cb.equal(root.get("baseDate").as(Date.class), baseDate), cb.equal(root.get("incomeEvent").get("status").as(String.class), IncomeEvent.STATUS_Allocated));
			}
		};

		spec = Specifications.where(spec);

		List<IncomeAllocate> ims = incomeAllocateDao.findAll(spec);
		if (ims != null && ims.size() > 0) {
			return true;
		}
		return false;
	}

}
