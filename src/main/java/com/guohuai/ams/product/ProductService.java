package com.guohuai.ams.product;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.guohuai.ams.dict.Dict;
import com.guohuai.ams.dict.DictService;
import com.guohuai.ams.label.LabelEntity;
import com.guohuai.ams.label.LabelEnum;
import com.guohuai.ams.portfolio.entity.PortfolioEntity;
import com.guohuai.ams.portfolio.service.PortfolioService;
import com.guohuai.ams.product.productChannel.ProductChannel;
import com.guohuai.ams.product.productChannel.ProductChannelService;
import com.guohuai.ams.product.reward.ProductIncomeReward;
import com.guohuai.ams.product.reward.ProductIncomeRewardService;
import com.guohuai.ams.product.reward.ProductRewardResp;
import com.guohuai.ams.productLabel.ProductLabel;
import com.guohuai.ams.productLabel.ProductLabelService;
import com.guohuai.basic.component.collectinfo.CollectInfoSdk;
import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.basic.component.ext.web.PageResp;
import com.guohuai.calendar.TradeCalendarService;
import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.DateUtil;
import com.guohuai.component.util.DecimalUtil;
import com.guohuai.component.util.StringUtil;
import com.guohuai.file.File;
import com.guohuai.file.FileResp;
import com.guohuai.file.FileService;
import com.guohuai.file.SaveFileForm;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountEntity;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountService;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderEntity;
import com.guohuai.mmp.publisher.baseaccount.PublisherBaseAccountDao;
import com.guohuai.mmp.publisher.baseaccount.PublisherBaseAccountEntity;
import com.guohuai.mmp.publisher.baseaccount.PublisherBaseAccountEntityDao;
import com.guohuai.mmp.publisher.baseaccount.PublisherBaseAccountService;
import com.guohuai.mmp.publisher.hold.PublisherHoldDao;
import com.guohuai.mmp.publisher.hold.PublisherHoldEntity;
import com.guohuai.mmp.publisher.hold.PublisherHoldService;
import com.guohuai.operate.api.AdminSdk;
import com.guohuai.operate.api.objs.admin.AdminObj;

@Service
@Transactional
public class ProductService {
	Logger logger = LoggerFactory.getLogger(ProductService.class);
	@Autowired
	private ProductDao productDao;
	@Autowired
	private ProductLogDao productLogDao;
	@Autowired
	private ProductLogService productLogService;
	@Autowired
	private DictService dictService;
	@Autowired
	private FileService fileService;
	@Autowired
	private ProductChannelService productChannelService;
	@Autowired
	private AdminSdk adminSdk;
	@Autowired
	private PublisherBaseAccountEntityDao publisherDao;
	@Autowired
	private PublisherHoldDao publisherHoldDao;
	@Autowired
	private ProductIncomeRewardService productRewardService;
	@Autowired
	private PortfolioService portfolioService;
	@Autowired
	private ProductIncomeRewardService incomeRewardService;
	@Autowired
	private PublisherHoldService publisherHoldService;
	@Autowired
	private PublisherBaseAccountDao publisherBaseAccountDao;
	@Autowired
	TradeCalendarService tradeCalendarService;
	@Autowired
	private ProductLabelService productLabelService;
	@Autowired
	private InvestorBaseAccountService investorBaseAccountService;
	@Autowired
	private PublisherBaseAccountService publisherBaseAccountService;
	
	
	/**
	 * 获取prodictOid对应产品所有可以选择的资产池的名称列表
	 * 
	 * @param productOid
	 * @return
	 */
	@Transactional
	public List<JSONObject> getOptionalPortfolioNameList(String productOid) {

		List<JSONObject> jsonObjList = this.portfolioService.getAllNameList();

		Specification<Product> spec = new Specification<Product>() {
			@Override
			public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.equal(root.get("isDeleted").as(String.class), Product.NO);
			}
		};
		List<Product> products = this.productDao.findAll(Specifications.where(spec));

		if (products != null && products.size() > 0) {
			Set<String> choosedPortfolioOids = new HashSet<String>();

			String productAssetOid = null;// productOid该产品对应的资产池

			if (StringUtil.isEmpty(productOid)) {
				for (Product p : products) {
					if (p.getPortfolio() != null) {
						choosedPortfolioOids.add(p.getPortfolio().getOid());
					}
				}
			} else {
				for (Product p : products) {
					if (p.getPortfolio() != null) {
						if (productOid.equals(p.getOid())) {
							productAssetOid = p.getPortfolio().getOid();
						} else {
							choosedPortfolioOids.add(p.getPortfolio().getOid());
						}
					}
				}
			}

			List<JSONObject> validPortfolioNames = new ArrayList<JSONObject>();
			for (JSONObject portfolioName : jsonObjList) {
				if (productAssetOid != null && productAssetOid.equals(portfolioName.get("oid"))) {
					validPortfolioNames.add(portfolioName);
				} else if (!choosedPortfolioOids.contains(portfolioName.get("oid"))) {
					validPortfolioNames.add(portfolioName);
				}
			}
			return validPortfolioNames;

		} else {
			return jsonObjList;
		}

	}

	@Transactional
	public BaseResp savePeriodic(SavePeriodicProductForm form, String operator) {
		BaseResp response = new BaseResp();
		
		checkPeriodic(form);	// 定期产品新建修改验证

		Product product = newProduct();
		product.setOid(StringUtil.uuid());
		product.setCode(form.getCode()); //产品编号
		product.setName(form.getName()); //产品简称
		product.setFullName(form.getFullName()); //产品全称
		product.setAdministrator(form.getAdministrator()); //产品管理人
		product.setState(Product.STATE_Create);
		product.setIsDeleted(Product.NO);
		product.setRepayInterestStatus(Product.PRODUCT_repayInterestStatus_toRepay);//付息状态
		product.setRepayLoanStatus(Product.PRODUCT_repayLoanStatus_toRepay);//还本状态

		// 产品类型
		Dict assetType = this.dictService.get(form.getTypeOid());
		product.setType(assetType);

		PortfolioEntity portfolio = this.portfolioService.getByOid(form.getAssetPoolOid());
		if (null == portfolio) {
			throw new AMPException("投资组合不存在");
		}
		product.setPortfolio(portfolio);
		product.setPublisherBaseAccount(portfolio.getSpvEntity());
		product.setReveal(form.getReveal());
		product.setRevealComment(form.getRevealComment());
		product.setCurrency(form.getCurrency());
		product.setIncomeCalcBasis(form.getIncomeCalcBasis());
		product.setOperationRate(DecimalUtil.zoomIn(form.getOperationRate(), 100));

		// 年化收益
		product.setExpAror(DecimalUtil.zoomIn(form.getExpAror(), 100));
		if (null == form.getExpArorSec() || form.getExpArorSec().compareTo(BigDecimal.ZERO) == 0) {
			product.setExpArorSec(product.getExpAror());
		} else {
			product.setExpArorSec(DecimalUtil.zoomIn(form.getExpArorSec(), 100));
		}
		if (null != form.getRewardInterest()) {
			product.setRewardInterest(form.getRewardInterest());
		}

		// 募集开始时间类型;募集期天数:()个自然日;起息日天数:成立后()个自然日;存续期天数:()个自然日;还本付息日 存续期结束后第()个自然日
		if (Product.DATE_TYPE_ManualInput.equals(form.getRaiseStartDateType()) && null == form.getRaiseStartDate()) {
			// error.define[90009]=请填写募集开始时间
			throw AMPException.getException(90009);
		}
		product.setRaiseStartDateType(form.getRaiseStartDateType());

		product.setRaisePeriodDays(form.getRaisePeriod()); //募集期

		product.setInterestsFirstDays(form.getSubscribeConfirmDays()); //募集期起息期（申购确认即起息）

		product.setDurationPeriodDays(form.getDurationPeriod()); // 存续期(成立后)

		product.setAccrualRepayDays(form.getAccrualDate()); // 还本付息日

		//募集期预期年化收益
		product.setRecPeriodExpAnYield(DecimalUtil.zoomIn(form.getRecPeriodExpAnYield(), 100));

		//认购确认日:认购订单提交后()个日内 
		product.setPurchaseConfirmDays(form.getSubscribeConfirmDays());
		product.setRedeemConfirmDays(form.getRedeemConfirmDays());

		//募集满额后是否自动触发成立
		if (Product.RAISE_FULL_FOUND_TYPE_AUTO.equals(form.getRaiseFullFoundType())) {
			if (null == form.getAutoFoundDays()) {
				throw new AMPException("选择自动成立，请填写募集满额后第几个自然日自动成立");
			}
			product.setAutoFoundDays(form.getAutoFoundDays());
		}
		product.setRaiseFullFoundType(form.getRaiseFullFoundType());
	
		//募集期满后最晚成立日
		product.setFoundDays(form.getFoundDays());

		// 募集开始日期,募集结束日期
		if (Product.DATE_TYPE_ManualInput.equals(form.getRaiseStartDateType())) {
			product.setRaiseStartDate(form.getRaiseStartDate());

			Date raiseEndDate = DateUtil.addSQLDays(product.getRaiseStartDate(), form.getRaisePeriod() - 1);
			product.setRaiseEndDate(raiseEndDate);// 募集结束时间

			Date setupDate = DateUtil.addSQLDays(raiseEndDate, form.getFoundDays());
			product.setSetupDate(setupDate);// 最晚产品成立时间

			Date durationPeriodEndDate = DateUtil.addSQLDays(setupDate, form.getDurationPeriod() - 1);
			product.setDurationPeriodEndDate(durationPeriodEndDate);// 存续期结束时间

			// 到期最晚还本付息日 指存续期结束后的还本付息最迟发生在存续期后的第X个自然日的23:59:59为止
			Date repayDate = DateUtil.addSQLDays(durationPeriodEndDate, form.getAccrualDate());
			// 到期最晚还本付息日 指存续期结束后的还本付息最迟发生在存续期后的第X个自然日的23:59:59为止
			product.setRepayDate(repayDate);// 到期还款时间

		} else {
			product.setRaiseStartDate(null);
		}

		product.setRaisedTotalNumber(form.getRaisedTotalNumber());
		product.setMaxSaleVolume(form.getRaisedTotalNumber());

		product.setInvestMin(form.getInvestMin());
		product.setInvestMax(form.getInvestMax());
		product.setInvestAdditional(form.getInvestAdditional());
		product.setInvestDateType(form.getInvestDateType());
		product.setMaxHold(form.getMaxHold());

		product.setNetUnitShare(form.getNetUnitShare());

		product.setDealStartTime(form.getDealStartTime()==null?null:form.getDealStartTime().replaceAll(":", ""));
		product.setDealEndTime(form.getDealEndTime()==null?null:form.getDealEndTime().replaceAll(":", ""));

		product.setIsOpenPurchase(Product.YES);
		product.setIsOpenRemeed(Product.NO);
		product.setInvestComment(form.getInvestComment());

		product.setInstruction(form.getInstruction());
		product.setRiskLevel(form.getRiskLevel());
		product.setInvestorLevel(form.getInvestorLevel());
//		product.setStems(Product.STEMS_Userdefine);
		product.setAuditState(Product.AUDIT_STATE_Nocommit);
		// 其他字段 初始化默认值s
		product.setOperator(operator);
		product.setUpdateTime(DateUtil.getSqlCurrentDate());
		product.setCreateTime(DateUtil.getSqlCurrentDate());

		
		// 附件文件
		List<SaveFileForm> fileForms = null;
		String fkey = StringUtil.uuid();
		if (StringUtil.isEmpty(form.getFiles())) {
			product.setFileKeys(StringUtil.EMPTY);
		} else {
			product.setFileKeys(fkey);
			fileForms = JSON.parseArray(form.getFiles(), SaveFileForm.class);
		}
		// 投资协议书
		List<SaveFileForm> investFileForm = null;
		String investFileKey = StringUtil.uuid();
		if (StringUtil.isEmpty(form.getInvestFile())) {
			product.setInvestFileKey(StringUtil.EMPTY);
		} else {
			product.setInvestFileKey(investFileKey);
			investFileForm = JSON.parseArray(form.getInvestFile(), SaveFileForm.class);
		}

		// 信息服务协议
		List<SaveFileForm> serviceFileForm = null;

		String serviceFileKey = StringUtil.uuid();
		if (StringUtil.isEmpty(form.getServiceFile())) {
			product.setServiceFileKey(StringUtil.EMPTY);
		} else {
			product.setServiceFileKey(serviceFileKey);
			serviceFileForm = JSON.parseArray(form.getServiceFile(), SaveFileForm.class);
		}

		
		product.setPurchaseApplyStatus(Product.APPLY_STATUS_None);
		product.setRedeemApplyStatus(Product.APPLY_STATUS_None);
		product.setBasicRatio(BigDecimal.ZERO);
//		product.setFastRedeemStatus(Product.NO);
//		product.setFastRedeemMax(BigDecimal.ZERO);
//		product.setFastRedeemLeft(BigDecimal.ZERO);


		product = this.productDao.save(product);

		// 附件文件
		this.fileService.save(fileForms, fkey, File.CATE_User, operator);
		// 投资协议书
		this.fileService.save(investFileForm, investFileKey, File.CATE_User, operator);
		// 信息服务协议
		this.fileService.save(serviceFileForm, serviceFileKey, File.CATE_User, operator);

		List<String> labelOids = new ArrayList<String>();
		if (!StringUtil.isEmpty(form.getBasicProductLabel())) {//基础标签
			labelOids.add(form.getBasicProductLabel());
		}
		if (form.getExpandProductLabels() != null && form.getExpandProductLabels().length > 0) {//扩展标签
			for (String ex : form.getExpandProductLabels()) {
				labelOids.add(ex);
			}
		}
		this.productLabelService.saveAndFlush(product, labelOids);

		return response;
	}

	/**
	 * 定期新建修改验证
	 * @param form
	 */
	private void checkPeriodic(SavePeriodicProductForm form) {
		// 如果募集期满后最晚成立日 < 认购确认日
		if (form.getFoundDays() != null && form.getSubscribeConfirmDays() != null && (form.getFoundDays()<form.getSubscribeConfirmDays())){
			// “募集期满后最晚成立日”必须大于等于“认购确认日”
			throw AMPException.getException(30081);
		}
	}

	private Product newProduct() {
		Product p = new Product();
//		p.setManageRate(new BigDecimal(0));
//		p.setFixedManageRate(new BigDecimal(0));
		p.setBasicRatio(new BigDecimal(0));
		p.setOperationRate(new BigDecimal(0));
//		p.setPayModeDay(0);
		p.setRaisePeriodDays(0);
		p.setLockPeriodDays(0);
		p.setInterestsFirstDays(0);
		p.setDurationPeriodDays(0);
		p.setExpAror(new BigDecimal(0));
		p.setExpArorSec(new BigDecimal(0));
		p.setRaisedTotalNumber(new BigDecimal(0));
		p.setNetUnitShare(new BigDecimal(0));
		p.setInvestMin(new BigDecimal(0));
		p.setInvestAdditional(new BigDecimal(0));
		p.setInvestMax(new BigDecimal(0));
		p.setMinRredeem(new BigDecimal(0));
		p.setNetMaxRredeemDay(new BigDecimal(0));
		p.setDailyNetMaxRredeem(new BigDecimal(0));
		p.setAccrualRepayDays(0);
		p.setPurchaseConfirmDays(0);
		p.setRedeemConfirmDays(0);
//		p.setRedeemTimingTaskDays(0);
		p.setPurchaseNum(0);
		p.setCurrentVolume(new BigDecimal(0));
		p.setCollectedVolume(new BigDecimal(0));
		p.setLockCollectedVolume(new BigDecimal(0));
		p.setMaxSaleVolume(new BigDecimal(0));
//		p.setIsAutoAssignIncome(Product.NO);
		return p;

	}

	@Transactional
	public BaseResp saveCurrent(SaveCurrentProductForm form, String operator) {
		BaseResp response = new BaseResp();

		Product product = this.newProduct();
		product.setOid(StringUtil.uuid());
		PortfolioEntity portfolio = this.portfolioService.getByOid(form.getAssetPoolOid());
		if (null == portfolio) {
			throw new AMPException("所属投资组合不能为空");
		}
		product.setPortfolio(portfolio); // 所属资产池
		product.setPublisherBaseAccount(this.publisherBaseAccountService.findOne(portfolio.getSpvEntity().getOid())); // 所属发行人
		product.setCode(form.getCode()); // 产品编号
		product.setName(form.getName()); // 产品简称
		product.setFullName(form.getFullName()); // 产品全称
		product.setAdministrator(form.getAdministrator()); // 产品管理人
		Dict type = this.dictService.get(form.getTypeOid());
		product.setType(type); // 产品类型
		product.setReveal(form.getReveal()); // 额外增信
		product.setRevealComment(form.getRevealComment()); // 增信备注
		product.setCurrency(form.getCurrency()); // 币种
		product.setIncomeCalcBasis(form.getIncomeCalcBasis()); // 收益计算基础
		product.setOperationRate(DecimalUtil.zoomIn(form.getOperationRate(), 100)); // 平台运营费率
		product.setAccrualCycleOid(form.getAccrualCycleOid()); // 收益结转周期

		product.setState(Product.STATE_Create); // 产品状态
		product.setIsDeleted(Product.NO); // 是否已删除

		product.setSetupDateType(form.getSetupDateType()); // 产品成立时间类型
		product.setInterestsFirstDays(form.getInterestsDate()); // 起息日
		product.setLockPeriodDays(form.getLockPeriod());// 锁定期
		product.setPurchaseConfirmDays(form.getPurchaseConfirmDate()); // 申购确认日
		product.setRedeemConfirmDays(form.getRedeemConfirmDate()); // 赎回确认日
		// 产品成立时间（存续期开始时间）
		if (Product.DATE_TYPE_ManualInput.equals(form.getSetupDateType()) && null == form.getSetupDate()) {
			// error.define[90010]=请填写成立时间
			throw AMPException.getException(90010);
		}
		if (Product.DATE_TYPE_ManualInput.equals(form.getSetupDateType())) {
			product.setSetupDate(form.getSetupDate());
		} else {
			product.setSetupDate(null);
		}

		// 年化收益
		product.setExpAror(DecimalUtil.zoomIn(form.getExpAror(), 100));
		if (null == form.getExpArorSec() || form.getExpArorSec().compareTo(BigDecimal.ZERO) == 0) {
			product.setExpArorSec(product.getExpAror());
		} else {
			product.setExpArorSec(DecimalUtil.zoomIn(form.getExpArorSec(), 100));
		}
		if (null != form.getRewardInterest()) {
			product.setRewardInterest(form.getRewardInterest());
		}

		product.setInvestMin(form.getInvestMin()); // 单笔投资最低份额
		product.setInvestMax(form.getInvestMax()); // 单笔投资追加份额
		product.setInvestAdditional(form.getInvestAdditional()); // 单笔投资最高份额
		product.setInvestDateType(form.getInvestDateType()); // 有效投资日类型
		product.setIsOpenPurchase(Product.YES); // 申购开关

		product.setNetMaxRredeemDay(form.getNetMaxRredeemDay()); //// 单日净赎回上限
		product.setDailyNetMaxRredeem(form.getNetMaxRredeemDay());
		product.setMinRredeem(form.getMinRredeem()); // 单笔净赎回下限
		product.setMaxRredeem(form.getMaxRredeem()); // 单笔净赎回上限
		product.setAdditionalRredeem(form.getAdditionalRredeem()); //// 单笔赎回递增份额
		product.setRredeemDateType(form.getRredeemDateType()); // 有效赎回日类型
//		product.setIsOpenRedeemConfirm(Product.YES);// 是否屏蔽赎回确认
		product.setSingleDailyMaxRedeem(form.getSingleDailyMaxRedeem());// 单人单日赎回上限
		product.setIsOpenRemeed(Product.YES); // 赎回开关
//		product.setFastRedeemStatus(Product.NO); // 快赎开关
//		product.setFastRedeemMax(BigDecimal.ZERO); // 快赎阈值
//		product.setFastRedeemLeft(BigDecimal.ZERO); // 快赎剩余
		product.setSingleDayRedeemCount(form.getSingleDayRedeemCount());
		product.setIsPreviousCurVolume(form.getIsPreviousCurVolume()); // 赎回占比开关
		product.setRedeemWithoutInterest(form.getRedeemWithoutInterest()); // 赎回立刻不计息	
		if (Product.YES.equals(product.getIsPreviousCurVolume())) {
			// 赎回占上一交易日规模百分比
			product.setPreviousCurVolumePercent(form.getPreviousCurVolumePercent());
		} else {
			product.setPreviousCurVolumePercent(BigDecimal.ZERO);
		}

		product.setNetUnitShare(form.getNetUnitShare());
		product.setMaxHold(form.getMaxHold()); // 单人持有份额上限
		product.setDealStartTime(form.getDealStartTime()==null?null:form.getDealStartTime().replaceAll(":", ""));
		product.setDealEndTime(form.getDealEndTime()==null?null:form.getDealEndTime().replaceAll(":", ""));
		product.setInvestComment(form.getInvestComment()); // 投资标的
		product.setInstruction(form.getInstruction()); // 产品说明
		product.setRiskLevel(form.getRiskLevel()); // 风险等级
		product.setInvestorLevel(form.getInvestorLevel()); // 投资者类型
//		product.setStems(Product.STEMS_Userdefine);
		product.setAuditState(Product.AUDIT_STATE_Nocommit); // 未提交审核
		// 其他字段 初始化默认值s
		product.setOperator(operator); // 操作人
		product.setUpdateTime(DateUtil.getSqlCurrentDate()); // 更新时间
		product.setCreateTime(DateUtil.getSqlCurrentDate()); // 创建时间
		// 附件文件
		List<SaveFileForm> fileForms = null;
		String fkey = StringUtil.uuid();
		if (StringUtil.isEmpty(form.getFiles())) {
			product.setFileKeys(StringUtil.EMPTY);
		} else {
			product.setFileKeys(fkey);
			fileForms = JSON.parseArray(form.getFiles(), SaveFileForm.class);
		}

		// 投资协议书
		List<SaveFileForm> investFileForm = null;
		String investFileKey = StringUtil.uuid();
		if (StringUtil.isEmpty(form.getInvestFile())) {
			product.setInvestFileKey(StringUtil.EMPTY);
		} else {
			product.setInvestFileKey(investFileKey);
			investFileForm = JSON.parseArray(form.getInvestFile(), SaveFileForm.class);
		}

		// 信息服务协议
		List<SaveFileForm> serviceFileForm = null;
		String serviceFileKey = StringUtil.uuid();
		if (StringUtil.isEmpty(form.getServiceFile())) {
			product.setServiceFileKey(StringUtil.EMPTY);
		} else {
			product.setServiceFileKey(serviceFileKey);
			serviceFileForm = JSON.parseArray(form.getServiceFile(), SaveFileForm.class);
		}

		product.setPurchaseApplyStatus(Product.APPLY_STATUS_None);
		product.setRedeemApplyStatus(Product.APPLY_STATUS_None);
		product.setMaxSaleVolume(BigDecimal.ZERO);
		product.setBasicRatio(BigDecimal.ZERO);
		
		product.setIncomeDealType(form.getIncomeDealType());	// 收益处理方式
		
		product = this.productDao.save(product);

		// 附件文件
		this.fileService.save(fileForms, fkey, File.CATE_User, operator);
		// 投资协议书
		this.fileService.save(investFileForm, investFileKey, File.CATE_User, operator);
		// 信息服务协议
		this.fileService.save(serviceFileForm, serviceFileKey, File.CATE_User, operator);

		List<String> labelOids = new ArrayList<String>();
		if (!StringUtil.isEmpty(form.getBasicProductLabel())) {// 基础标签
			labelOids.add(form.getBasicProductLabel());
		}
		if (form.getExpandProductLabels() != null && form.getExpandProductLabels().length > 0) {// 扩展标签
			for (String ex : form.getExpandProductLabels()) {
				labelOids.add(ex);
			}
		}
		this.productLabelService.saveAndFlush(product, labelOids);

		return response;
	}

	/**
	 * 获取指定id的产品对象
	 * 
	 * @param pid
	 *            产品对象id
	 * @return {@link Product}
	 */
	public Product getProductByOid(String pid) {
		if (StringUtil.isEmpty(pid)) {
			return null;
		}
		Product product = this.productDao.findOne(pid);
		if (product == null || Product.YES.equals(product.getIsDeleted())) {
			throw AMPException.getException(90000);
		}
		return product;

	}

	@Transactional
	public Product delete(String oid, String operator) {
		Product product = this.getProductByOid(oid);

		// 面向渠道的属性，同一产品在不同渠道可以为不同状态，同一产品，只要有一个渠道销售状态为“渠道X待上架”，该产品已录入详情字段就不可修改。
		List<ProductChannel> pcs = this.productChannelService.queryProductChannels(oid);
		if (pcs != null && pcs.size() > 0) {
			for (ProductChannel pc : pcs) {
				if (ProductChannel.MARKET_STATE_Shelfing.equals(pc.getMarketState())
						|| ProductChannel.MARKET_STATE_Onshelf.equals(pc.getMarketState())) {
					// error.define[90007]=已经上架不可以作废
					throw AMPException.getException(90007);
				}
			}
		}

		product.setIsDeleted(Product.YES);
		// 其它：修改时间、操作人
		product.setOperator(operator);
		product.setUpdateTime(new Timestamp(System.currentTimeMillis()));

		product = this.productDao.saveAndFlush(product);

		return product;
	}

	/**
	 * 进行更新产品信息的操作，同时记录并存储日志
	 */
	@Transactional
	public BaseResp updatePeriodic(SavePeriodicProductForm form, String operator) {

		BaseResp response = new BaseResp();
		checkPeriodic(form);// 定期产品新建修改验证
		
		// 根据form中的productOid，从数据库得到相应对象，之后进行为对象进行审核操作
		Product product = this.getProductByOid(form.getOid());
		// 当前时间
		Timestamp now = new Timestamp(System.currentTimeMillis());

		// 未提交审核的可修改
		if (!Product.AUDIT_STATE_Nocommit.equals(product.getAuditState())) {
			throw AMPException.getException(90008);
		}
		// 判断是否可以修改 名称类型不变
		PortfolioEntity portfolio = this.portfolioService.getByOid(form.getAssetPoolOid());
		if (null == portfolio) {
			throw new AMPException("投资组合不存在");
		}

		product.setPublisherBaseAccount(publisherDao.findOne(portfolio.getSpvEntity().getOid()));

		product.setPortfolio(portfolio);

		product.setCode(form.getCode());
		product.setName(form.getName());
		product.setFullName(form.getFullName());
		product.setAdministrator(form.getAdministrator());
		product.setState(Product.STATE_Update);
		product.setReveal(form.getReveal());
		product.setRevealComment(form.getRevealComment());
		product.setCurrency(form.getCurrency());
		product.setIncomeCalcBasis(form.getIncomeCalcBasis());
		product.setOperationRate(DecimalUtil.zoomIn(form.getOperationRate(), 100));

		// 年化收益
		product.setExpAror(DecimalUtil.zoomIn(form.getExpAror(), 100));
		if (null == form.getExpArorSec() || form.getExpArorSec().compareTo(BigDecimal.ZERO) == 0) {
			product.setExpArorSec(product.getExpAror());
		} else {
			product.setExpArorSec(DecimalUtil.zoomIn(form.getExpArorSec(), 100));
		}
		product.setRewardInterest(form.getRewardInterest());
		// 募集开始时间类型;募集期:()个自然日;起息日:募集满额后()个自然日;存续期:()个自然日
		if (Product.DATE_TYPE_ManualInput.equals(form.getRaiseStartDateType()) && null == form.getRaiseStartDate()) {
			// error.define[90009]=请填写募集开始时间
			throw AMPException.getException(90009);
		}
		product.setRaiseStartDateType(form.getRaiseStartDateType());

		product.setRaisePeriodDays(form.getRaisePeriod());

//		product.setInterestsFirstDays(form.getInterestsFirstDate());

		product.setDurationPeriodDays(form.getDurationPeriod());

		product.setAccrualRepayDays(form.getAccrualDate());

		//募集期预期年化收益
		product.setRecPeriodExpAnYield(DecimalUtil.zoomIn(form.getRecPeriodExpAnYield(), 100));

		// 认购确认日:认购订单提交后()个日内
		product.setPurchaseConfirmDays(form.getSubscribeConfirmDays());

		// 募集满额后是否自动触发成立
		//募集满额后是否自动触发成立
		if (Product.RAISE_FULL_FOUND_TYPE_AUTO.equals(form.getRaiseFullFoundType())) {
			if (null == form.getAutoFoundDays()) {
				throw new AMPException("选择自动成立，请填写募集满额后第几个自然日自动成立");
			}
			product.setAutoFoundDays(form.getAutoFoundDays());
		}
		product.setRaiseFullFoundType(form.getRaiseFullFoundType());

		// 募集期满后最晚成立日
		product.setFoundDays(Integer.valueOf(form.getFoundDays()));

		// 募集开始日期,募集结束日期
		if (Product.DATE_TYPE_ManualInput.equals(form.getRaiseStartDateType())) {
			product.setRaiseStartDate(form.getRaiseStartDate());

			Date raiseEndDate = DateUtil.addSQLDays(product.getRaiseStartDate(), form.getRaisePeriod() - 1);
			product.setRaiseEndDate(raiseEndDate);// 募集结束时间

			Date setupDate = DateUtil.addSQLDays(raiseEndDate, form.getFoundDays());
			product.setSetupDate(setupDate);// 最晚产品成立时间

			Date durationPeriodEndDate = DateUtil.addSQLDays(setupDate, form.getDurationPeriod() - 1);
			product.setDurationPeriodEndDate(durationPeriodEndDate);// 存续期结束时间

			// 到期最晚还本付息日 指存续期结束后的还本付息最迟发生在存续期后的第X个自然日的23:59:59为止
			Date repayDate = DateUtil.addSQLDays(durationPeriodEndDate, form.getAccrualDate());
			// 到期最晚还本付息日 指存续期结束后的还本付息最迟发生在存续期后的第X个自然日的23:59:59为止
			product.setRepayDate(repayDate);// 到期还款时间
		} else {
			product.setRaiseStartDate(null);
		}

		product.setRaisedTotalNumber(form.getRaisedTotalNumber());
		product.setMaxSaleVolume(form.getRaisedTotalNumber());

		product.setInvestMin(form.getInvestMin());

		product.setInvestMax(form.getInvestMax());

		product.setInvestAdditional(form.getInvestAdditional());

		product.setInvestDateType(form.getInvestDateType());

		product.setNetUnitShare(form.getNetUnitShare());

		product.setInvestComment(form.getInvestComment());
		product.setInstruction(form.getInstruction());
		product.setRiskLevel(form.getRiskLevel());
		product.setInvestorLevel(form.getInvestorLevel());
		// 其它：修改时间、操作人
		product.setOperator(operator);
		product.setUpdateTime(now);

		product.setDealStartTime(form.getDealStartTime()==null?null:form.getDealStartTime().replaceAll(":", ""));
		product.setDealEndTime(form.getDealEndTime()==null?null:form.getDealEndTime().replaceAll(":", ""));

		// 附件文件
		List<SaveFileForm> fileForms = null;
		if (!StringUtil.isEmpty(form.getFiles())) {
			fileForms = JSON.parseArray(form.getFiles(), SaveFileForm.class);
		}
		String fkey = null;
		if (StringUtil.isEmpty(product.getFileKeys())) {
			fkey = StringUtil.uuid();
			if (fileForms != null && fileForms.size() > 0) {
				product.setFileKeys(fkey);
			}
		} else {
			fkey = product.getFileKeys();
			if (fileForms == null || fileForms.size() == 0) {
				product.setFileKeys(StringUtil.EMPTY);
			}
		}

		// 投资协议书
		List<SaveFileForm> investFileForm = null;
		if (!StringUtil.isEmpty(form.getInvestFile())) {
			investFileForm = JSON.parseArray(form.getInvestFile(), SaveFileForm.class);
		}
		String investFileKey = null;
		if (StringUtil.isEmpty(product.getInvestFileKey())) {
			investFileKey = StringUtil.uuid();
			if (investFileForm != null && investFileForm.size() > 0) {
				product.setInvestFileKey(investFileKey);
			}
		} else {
			investFileKey = product.getInvestFileKey();
			if (investFileForm == null || investFileForm.size() == 0) {
				product.setInvestFileKey(StringUtil.EMPTY);
			}
		}
		// 信息服务协议
		List<SaveFileForm> serviceFileForm = null;
		if (!StringUtil.isEmpty(form.getServiceFile())) {
			serviceFileForm = JSON.parseArray(form.getServiceFile(), SaveFileForm.class);
		}
		String serviceFileKey = null;
		if (StringUtil.isEmpty(product.getServiceFileKey())) {
			serviceFileKey = StringUtil.uuid();
			if (serviceFileForm != null && serviceFileForm.size() > 0) {
				product.setServiceFileKey(serviceFileKey);
			}
		} else {
			serviceFileKey = product.getServiceFileKey();
			if (serviceFileForm == null || serviceFileForm.size() == 0) {
				product.setServiceFileKey(StringUtil.EMPTY);
			}
		}

		// 更新产品
		product = this.productDao.saveAndFlush(product);
		{
			// 附件文件
			this.fileService.save(fileForms, fkey, File.CATE_User, operator);
			// 投资协议书
			this.fileService.save(investFileForm, investFileKey, File.CATE_User, operator);
			// 信息服务协议
			this.fileService.save(serviceFileForm, serviceFileKey, File.CATE_User, operator);
		}

		List<String> labelOids = new ArrayList<String>();
		if (!StringUtil.isEmpty(form.getBasicProductLabel())) {// 基础标签
			labelOids.add(form.getBasicProductLabel());
		}
		if (form.getExpandProductLabels() != null && form.getExpandProductLabels().length > 0) {// 扩展标签
			for (String ex : form.getExpandProductLabels()) {
				labelOids.add(ex);
			}
		}
		this.productLabelService.saveAndFlush(product, labelOids);

		return response;
	}

	/**
	 * 进行更新产品信息的操作，同时记录并存储日志
	 * 
	 * @param form
	 * @param operator
	 * @return
	 */
	@Transactional
	public BaseResp updateCurrent(SaveCurrentProductForm form, String operator) throws ParseException, Exception {
		// 根据form中的productOid，从数据库得到相应对象，之后进行为对象进行审核操作
		BaseResp response = new BaseResp();

		Product product = this.getProductByOid(form.getOid());

		// 当前时间
		Timestamp now = new Timestamp(System.currentTimeMillis());
		// 未提交审核的可修改
		if (!Product.AUDIT_STATE_Nocommit.equals(product.getAuditState())) {
			throw AMPException.getException(90008);
		}
		// 判断是否可以修改 名称类型不变

		PortfolioEntity portfolio = this.portfolioService.getByOid(form.getAssetPoolOid());
		if (null == portfolio) {
			throw new AMPException("投资组合不存在");
		}
		product.setPortfolio(portfolio);
		product.setPublisherBaseAccount(this.publisherBaseAccountService.findOne(portfolio.getSpvEntity().getOid()));

		product.setCode(form.getCode());
		product.setName(form.getName());
		product.setFullName(form.getFullName());
		product.setAdministrator(form.getAdministrator());
		product.setState(Product.STATE_Update);
		product.setReveal(form.getReveal());
		product.setRevealComment(form.getRevealComment());
		product.setCurrency(form.getCurrency());
		product.setIncomeCalcBasis(form.getIncomeCalcBasis());

		// 收益结转周期
		product.setAccrualCycleOid(form.getAccrualCycleOid());

		product.setOperationRate(DecimalUtil.zoomIn(form.getOperationRate(), 100));

		// 年化收益
		product.setExpAror(DecimalUtil.zoomIn(form.getExpAror(), 100));
		if (null == form.getExpArorSec() || form.getExpArorSec().compareTo(BigDecimal.ZERO) == 0) {
			product.setExpArorSec(product.getExpAror());
		} else {
			product.setExpArorSec(DecimalUtil.zoomIn(form.getExpArorSec(), 100));
		}
		product.setRewardInterest(form.getRewardInterest());

		if (Product.DATE_TYPE_ManualInput.equals(form.getSetupDateType()) && null == form.getSetupDate()) {
			// error.define[90010]=请填写成立时间
			throw AMPException.getException(90010);
		}
		if (Product.DATE_TYPE_ManualInput.equals(form.getSetupDateType())) {
			product.setSetupDate(form.getSetupDate());
		} else {
			product.setSetupDate(null);
		}

		product.setSetupDateType(form.getSetupDateType());

		product.setInterestsFirstDays(Integer.valueOf(form.getInterestsDate()));

		product.setLockPeriodDays(Integer.valueOf(form.getLockPeriod()));

		product.setPurchaseConfirmDays(Integer.valueOf(form.getPurchaseConfirmDate()));

		product.setRedeemConfirmDays(form.getRedeemConfirmDate());

		product.setInvestMin(form.getInvestMin());
		product.setInvestMax(form.getInvestMax());
		product.setInvestAdditional(form.getInvestAdditional());
		product.setInvestDateType(form.getInvestDateType());
		product.setNetUnitShare(form.getNetUnitShare());

		product.setNetMaxRredeemDay(form.getNetMaxRredeemDay());
		product.setDailyNetMaxRredeem(form.getNetMaxRredeemDay());
		product.setMinRredeem(form.getMinRredeem());// 单笔赎回下限
		product.setMaxRredeem(form.getMaxRredeem());
		product.setAdditionalRredeem(form.getAdditionalRredeem());
		product.setRredeemDateType(form.getRredeemDateType());
		product.setSingleDailyMaxRedeem(form.getSingleDailyMaxRedeem());// 单人单日赎回上限
		product.setSingleDayRedeemCount(form.getSingleDayRedeemCount()); //单人单日赎回次数
		product.setIsPreviousCurVolume(form.getIsPreviousCurVolume()); // 赎回占比开关
		product.setRedeemWithoutInterest(form.getRedeemWithoutInterest()); // 赎回立刻不计息	
		if (Product.YES.equals(product.getIsPreviousCurVolume())) {
			// 赎回占上一交易日规模百分比
			product.setPreviousCurVolumePercent(form.getPreviousCurVolumePercent());
		} else {
			product.setPreviousCurVolumePercent(BigDecimal.ZERO);
		}

		product.setDealStartTime(form.getDealStartTime()==null?null:form.getDealStartTime().replaceAll(":", ""));
		product.setDealEndTime(form.getDealEndTime()==null?null:form.getDealEndTime().replaceAll(":", ""));
		product.setMaxHold(form.getMaxHold());
		product.setInvestComment(form.getInvestComment());
		product.setInstruction(form.getInstruction());
		product.setRiskLevel(form.getRiskLevel());
		product.setInvestorLevel(form.getInvestorLevel());
		// 其它：修改时间、操作人
		product.setOperator(operator);
		product.setUpdateTime(now);

		product.setIncomeDealType(form.getIncomeDealType());	// 收益处理方式
		
		// 附件文件
		List<SaveFileForm> fileForms = null;
		if (!StringUtil.isEmpty(form.getFiles())) {
			fileForms = JSON.parseArray(form.getFiles(), SaveFileForm.class);
		}
		String fkey = null;
		if (StringUtil.isEmpty(product.getFileKeys())) {
			fkey = StringUtil.uuid();
			if (fileForms != null && fileForms.size() > 0) {
				product.setFileKeys(fkey);
			}
		} else {
			fkey = product.getFileKeys();
			if (fileForms == null || fileForms.size() == 0) {
				product.setFileKeys(StringUtil.EMPTY);
			}
		}
		// 投资协议书
		List<SaveFileForm> investFileForm = null;
		if (!StringUtil.isEmpty(form.getInvestFile())) {
			investFileForm = JSON.parseArray(form.getInvestFile(), SaveFileForm.class);
		}
		String investFileKey = null;
		if (StringUtil.isEmpty(product.getInvestFileKey())) {
			investFileKey = StringUtil.uuid();
			if (investFileForm != null && investFileForm.size() > 0) {
				product.setInvestFileKey(investFileKey);
			}
		} else {
			investFileKey = product.getInvestFileKey();
			if (investFileForm == null || investFileForm.size() == 0) {
				product.setInvestFileKey(StringUtil.EMPTY);
			}
		}

		// 信息服务协议
		List<SaveFileForm> serviceFileForm = null;
		if (!StringUtil.isEmpty(form.getServiceFile())) {
			serviceFileForm = JSON.parseArray(form.getServiceFile(), SaveFileForm.class);
		}
		String serviceFileKey = null;
		if (StringUtil.isEmpty(product.getServiceFileKey())) {
			serviceFileKey = StringUtil.uuid();
			if (serviceFileForm != null && serviceFileForm.size() > 0) {
				product.setServiceFileKey(serviceFileKey);
			}
		} else {
			serviceFileKey = product.getServiceFileKey();
			if (serviceFileForm == null || serviceFileForm.size() == 0) {
				product.setServiceFileKey(StringUtil.EMPTY);
			}
		}

		// 更新产品
		product = this.productDao.saveAndFlush(product);

		{
			// 附件文件
			this.fileService.save(fileForms, fkey, File.CATE_User, operator);
			// 投资协议书
			this.fileService.save(investFileForm, investFileKey, File.CATE_User, operator);
			// 信息服务协议
			this.fileService.save(serviceFileForm, serviceFileKey, File.CATE_User, operator);
		}

		List<String> labelOids = new ArrayList<String>();
		if (!StringUtil.isEmpty(form.getBasicProductLabel())) {// 基础标签
			labelOids.add(form.getBasicProductLabel());
		}
		if (form.getExpandProductLabels() != null && form.getExpandProductLabels().length > 0) {// 扩展标签
			for (String ex : form.getExpandProductLabels()) {
				labelOids.add(ex);
			}
		}
		this.productLabelService.saveAndFlush(product, labelOids);
		return response;
	}

	/**
	 * 产品详情
	 * 
	 * @param oid
	 * @return
	 */
	@Transactional
	public ProductDetailResp read(String oid) {
		Product product = this.getProductByOid(oid);
		ProductDetailResp pr = new ProductDetailResp(product);

		if (product.getPortfolio() != null && product.getPortfolio().getSpvEntity() != null) {
			pr.setSpvName(product.getPortfolio().getSpvEntity() .getRealName());
		}
		Map<String, AdminObj> adminObjMap = new HashMap<String, AdminObj>();

		FileResp fr = null;
		AdminObj adminObj = null;
		if (!StringUtil.isEmpty(product.getFileKeys())) {
			List<File> files = this.fileService.list(product.getFileKeys(), File.STATE_Valid);
			if (files.size() > 0) {
				pr.setFiles(new ArrayList<FileResp>());

				for (File file : files) {
					fr = new FileResp(file);
					if (adminObjMap.get(file.getOperator()) == null) {
						try {
							adminObj = adminSdk.getAdmin(file.getOperator());
							adminObjMap.put(file.getOperator(), adminObj);
						} catch (Exception e) {
						}
					}
					if (adminObjMap.get(file.getOperator()) != null) {
						fr.setOperator(adminObjMap.get(file.getOperator()).getName());
					}
					pr.getFiles().add(fr);
				}
			}
		}

		if (!StringUtil.isEmpty(product.getInvestFileKey())) {
			List<File> files = this.fileService.list(product.getInvestFileKey(), File.STATE_Valid);
			if (files.size() > 0) {
				pr.setInvestFiles(new ArrayList<FileResp>());

				for (File file : files) {
					fr = new FileResp(file);
					if (adminObjMap.get(file.getOperator()) == null) {
						try {
							adminObj = adminSdk.getAdmin(file.getOperator());
							adminObjMap.put(file.getOperator(), adminObj);
						} catch (Exception e) {
						}
					}
					if (adminObjMap.get(file.getOperator()) != null) {
						fr.setOperator(adminObjMap.get(file.getOperator()).getName());
					}
					pr.getInvestFiles().add(fr);
				}
			}
		}

		if (!StringUtil.isEmpty(product.getServiceFileKey())) {
			List<File> files = this.fileService.list(product.getServiceFileKey(), File.STATE_Valid);
			if (files.size() > 0) {
				pr.setServiceFiles(new ArrayList<FileResp>());

				for (File file : files) {
					fr = new FileResp(file);
					if (adminObjMap.get(file.getOperator()) == null) {
						try {
							adminObj = adminSdk.getAdmin(file.getOperator());
							adminObjMap.put(file.getOperator(), adminObj);
						} catch (Exception e) {
						}
					}
					if (adminObjMap.get(file.getOperator()) != null) {
						fr.setOperator(adminObjMap.get(file.getOperator()).getName());
					}
					pr.getServiceFiles().add(fr);
				}
			}
		}

		List<ProductChannel> pcs = productChannelService.queryProductChannels(oid);
		if (pcs != null && pcs.size() > 0) {
			StringBuilder channelNames = new StringBuilder();
			List<String> channelOids = new ArrayList<String>();
			for (ProductChannel pc : pcs) {
				channelNames.append(pc.getChannel().getChannelName()).append(",");
				channelOids.add(pc.getChannel().getOid());
			}
			pr.setChannelNames(channelNames.substring(0, channelNames.length() - 1));
			pr.setChannelOids(channelOids);
		}

		List<ProductIncomeReward> pirs = productRewardService.productRewardList(oid);
		if (pirs != null && pirs.size() > 0) {
			List<ProductRewardResp> rewards = new ArrayList<ProductRewardResp>();
			for (ProductIncomeReward pir : pirs) {
				ProductRewardResp pirRep = new ProductRewardResp(pir);
				rewards.add(pirRep);
			}
			pr.setRewards(rewards);
		}

		List<ProductLabel> productLabels = productLabelService.findProductLabelsByProduct(product);
		if (productLabels != null && productLabels.size() > 0) {
			List<String> exOids = new ArrayList<String>();
			List<String> exNames = new ArrayList<String>();
			List<String> exCodes = new ArrayList<String>();
			for (ProductLabel pl : productLabels) {
				if (LabelEntity.labelType_general.equals(pl.getLabel().getLabelType())) {
					pr.setBasicProductLabelOid(pl.getLabel().getOid());
					pr.setBasicProductLabelName(pl.getLabel().getLabelName());
					pr.setBasicProductLabelCode(pl.getLabel().getLabelCode());
				} else if (LabelEntity.labelType_extend.equals(pl.getLabel().getLabelType())) {
					exOids.add(pl.getLabel().getOid());
					exNames.add(pl.getLabel().getLabelName());
					exCodes.add(pl.getLabel().getLabelCode());
				}
			}
			if (exOids.size() > 0) {
				pr.setExpandProductLabelOids(exOids.toArray(new String[exOids.size()]));
				pr.setExpandProductLabelNames(exNames.toArray(new String[exOids.size()]));
				pr.setExpandProductLabelCodes(exCodes.toArray(new String[exOids.size()]));
			}

		}
		
		List<ProductLog> logs = productLogService.findByProduct(product);
		List<ProductLogPojo> productLog = new ArrayList<ProductLogPojo>();
		for (ProductLog log : logs) {
			ProductLogPojo pojo = new ProductLogPojo();
			pojo.setAuditTime(log.getAuditTime());
			pojo.setAuditComment(log.getAuditComment());
			pojo.setAuditStateDisp(this.productLogService.auditStateEn2Ch(log.getAuditState()));
			pojo.setAuditTypeDisp(this.productLogService.auditTypeEn2Ch(log.getAuditType()));
			productLog.add(pojo);
		}
		pr.setProductLog(productLog);
		
		return pr;
	}

	/**
	 * 查询
	 * 
	 * @param spec
	 * @param pageable
	 * @return {@link PagesRep<ProductResp>}
	 *         ,如果返回的errCode属性等于0表示成功，否则表示失败，失败原因在errMessage里面体现
	 */
	public PageResp<ProductResp> list(Specification<Product> spec, Pageable pageable) {
		Page<Product> cas = this.productDao.findAll(spec, pageable);
		PageResp<ProductResp> pagesRep = new PageResp<ProductResp>();
		if (cas != null && cas.getContent() != null && cas.getTotalElements() > 0) {
			List<ProductResp> rows = new ArrayList<ProductResp>();

			Map<String, List<ProductIncomeReward>> incomeRewardNum = this.getProductRewards(cas.getContent());

			Map<String, PublisherBaseAccountEntity> aoprateObjMap = new HashMap<String, PublisherBaseAccountEntity>();
			
			for (Product p : cas) {
				ProductResp queryRep = new ProductResp(p);
				if (p.getPortfolio() != null && p.getPortfolio().getSpvEntity() != null) {
					if (aoprateObjMap.get(p.getPortfolio().getSpvEntity().getOid()) == null) {
						try {
							
							aoprateObjMap.put(p.getPortfolio().getSpvEntity().getOid(), p.getPortfolio().getSpvEntity());
							
						} catch (Exception e) {
						}
					}
					if (aoprateObjMap.get(p.getPortfolio().getSpvEntity().getOid()) != null) {
						queryRep.setSpvName(aoprateObjMap.get(p.getPortfolio().getSpvEntity().getOid()).getRealName());
					}
				}
				if (incomeRewardNum.get(p.getOid()) != null) {
					queryRep.setRewardNum(incomeRewardNum.get(p.getOid()).size());
				} else {
					queryRep.setRewardNum(0);
				}

				rows.add(queryRep);
			}
			pagesRep.setRows(rows);
		}
		pagesRep.setTotal(cas.getTotalElements());
		return pagesRep;
	}

	private Map<String, List<ProductIncomeReward>> getProductRewards(List<Product> ps) {
		List<String> productOids = new ArrayList<String>();
		for (Product p : ps) {
			productOids.add(p.getOid());
		}
		Map<String, List<ProductIncomeReward>> incomeRewardNum = new HashMap<String, List<ProductIncomeReward>>();

		List<ProductIncomeReward> list = incomeRewardService.productsRewardList(productOids);

		if (list != null && list.size() > 0) {
			for (ProductIncomeReward reward : list) {
				if (incomeRewardNum.get(reward.getProduct().getOid()) == null) {
					incomeRewardNum.put(reward.getProduct().getOid(), new ArrayList<ProductIncomeReward>());
				}
				incomeRewardNum.get(reward.getProduct().getOid()).add(reward);
			}
		}
		return incomeRewardNum;
	}

	/**
	 * 查询审核中
	 * 
	 * @param spec
	 * @param pageable
	 * @return
	 */
	public PageResp<ProductLogListResp> auditList(Specification<Product> spec, Pageable pageable) {
		Page<Product> cas = this.productDao.findAll(spec, pageable);
		PageResp<ProductLogListResp> pagesRep = new PageResp<ProductLogListResp>();
		if (cas != null && cas.getContent() != null && cas.getTotalElements() > 0) {
			List<ProductLogListResp> rows = new ArrayList<ProductLogListResp>();

			Map<String, AdminObj> adminObjMap = new HashMap<String, AdminObj>();
			Map<String, PublisherBaseAccountEntity> aoprateObjMap = new HashMap<String, PublisherBaseAccountEntity>();

			List<String> oids = new ArrayList<String>();
			for (Product p : cas) {
				oids.add(p.getOid());
			}
			Map<String, ProductLog> plMap = this.getProductLogs(oids, ProductLog.AUDIT_TYPE_Auditing, ProductLog.AUDIT_STATE_Commited);
			ProductLog pl = null;

			AdminObj adminObj = null;
			for (Product p : cas) {
				ProductLogListResp queryRep = new ProductLogListResp(p);
				if (p.getPortfolio() != null && p.getPortfolio().getSpvEntity() != null) {
					if (aoprateObjMap.get(p.getPortfolio().getSpvEntity().getOid()) == null) {
						try {
							
							aoprateObjMap.put(p.getPortfolio().getSpvEntity().getOid(), p.getPortfolio().getSpvEntity());
							
						} catch (Exception e) {
						}
					}
					if (aoprateObjMap.get(p.getPortfolio().getSpvEntity().getOid()) != null) {
						queryRep.setSpvName(aoprateObjMap.get(p.getPortfolio().getSpvEntity().getOid()).getRealName());
					}
				}
				// 申请人和申请时间
				pl = plMap.get(p.getOid());

				if (pl != null) {
					if (adminObjMap.get(pl.getAuditor()) == null) {
						try {
							adminObj = adminSdk.getAdmin(pl.getAuditor());
							adminObjMap.put(pl.getAuditor(), adminObj);
						} catch (Exception e) {
						}

					}
					if (adminObjMap.get(pl.getAuditor()) != null) {
						queryRep.setApplicant(adminObjMap.get(pl.getAuditor()).getName());
					}
					queryRep.setApplyTime(DateUtil.formatDatetime(pl.getAuditTime().getTime()));
				}
				rows.add(queryRep);
			}
			pagesRep.setRows(rows);
		}
		pagesRep.setTotal(cas.getTotalElements());

		return pagesRep;
	}

	/**
	 * 查询复核中
	 * 
	 * @param spec
	 * @param pageable
	 * @return
	 */
	public PageResp<ProductLogListResp> checkList(Specification<Product> spec, Pageable pageable) {
		PageResp<ProductLogListResp> pagesRep = new PageResp<ProductLogListResp>();

		Page<Product> cas = this.productDao.findAll(spec, pageable);

		if (cas != null && cas.getContent() != null && cas.getTotalElements() > 0) {
			List<ProductLogListResp> rows = new ArrayList<ProductLogListResp>();

			List<String> oids = new ArrayList<String>();
			for (Product p : cas) {
				oids.add(p.getOid());
			}
			Map<String, ProductLog> plMap1 = this.getProductLogs(oids, ProductLog.AUDIT_TYPE_Auditing, ProductLog.AUDIT_STATE_Commited);// 提交记录
			Map<String, ProductLog> plMap2 = this.getProductLogs(oids, ProductLog.AUDIT_TYPE_Auditing, ProductLog.AUDIT_STATE_Approval);// 审核记录

			Map<String, AdminObj> adminObjMap = new HashMap<String, AdminObj>();
			AdminObj adminObj = null;
			Map<String, PublisherBaseAccountEntity> aoprateObjMap = new HashMap<String, PublisherBaseAccountEntity>();
			ProductLog pl1 = null;
			ProductLog pl2 = null;
			for (Product p : cas) {
				ProductLogListResp queryRep = new ProductLogListResp(p);
				if (p.getPortfolio() != null && p.getPortfolio().getSpvEntity() != null) {
					if (aoprateObjMap.get(p.getPortfolio().getSpvEntity().getOid()) == null) {
						try {
							
							aoprateObjMap.put(p.getPortfolio().getSpvEntity().getOid(), p.getPortfolio().getSpvEntity());
							
						} catch (Exception e) {
						}
					}
					if (aoprateObjMap.get(p.getPortfolio().getSpvEntity().getOid()) != null) {
						queryRep.setSpvName(aoprateObjMap.get(p.getPortfolio().getSpvEntity().getOid()).getRealName());
					}
				}
				// 申请人和申请时间
				pl1 = plMap1.get(p.getOid());
				if (pl1 != null) {
					if (adminObjMap.get(pl1.getAuditor()) == null) {
						try {
							adminObj = adminSdk.getAdmin(pl1.getAuditor());
							adminObjMap.put(pl1.getAuditor(), adminObj);
						} catch (Exception e) {
						}

					}
					if (adminObjMap.get(pl1.getAuditor()) != null) {
						queryRep.setApplicant(adminObjMap.get(pl1.getAuditor()).getName());
					}
					queryRep.setApplyTime(DateUtil.formatDatetime(pl1.getAuditTime().getTime()));
				}

				pl2 = plMap2.get(p.getOid());
				if (pl2 != null) {
					// 审核人 审核时间
					if (adminObjMap.get(pl2.getAuditor()) == null) {
						try {
							adminObj = adminSdk.getAdmin(pl2.getAuditor());
							adminObjMap.put(pl2.getAuditor(), adminObj);
						} catch (Exception e) {
						}
					}
					if (adminObjMap.get(pl2.getAuditor()) != null) {
						queryRep.setAuditor(adminObjMap.get(pl2.getAuditor()).getName());
					}
					queryRep.setAuditTime(DateUtil.formatDatetime(pl2.getAuditTime().getTime()));
				}
				rows.add(queryRep);
			}
			pagesRep.setRows(rows);
		}
		pagesRep.setTotal(cas.getTotalElements());

		return pagesRep;
	}

	/**
	 * 查询productlogs
	 * 
	 * @param oids
	 * @param auditType
	 * @param auditState
	 * @return
	 */
	public Map<String, ProductLog> getProductLogs(final List<String> oids, final String auditType, final String auditState) {
		Direction sortDirection = Direction.DESC;
		Sort sort = new Sort(new Order(sortDirection, "auditTime"));

		Specification<ProductLog> spec = new Specification<ProductLog>() {
			@Override
			public Predicate toPredicate(Root<ProductLog> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.and(cb.equal(root.get("auditType").as(String.class), auditType), cb.equal(root.get("auditState").as(String.class), auditState));
			}
		};

		spec = Specifications.where(spec).and(new Specification<ProductLog>() {
			@Override
			public Predicate toPredicate(Root<ProductLog> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Expression<String> exp = root.get("product").get("oid").as(String.class);
				In<String> in = cb.in(exp);
				for (String oid : oids) {
					in.value(oid);
				}
				return in;
			}
		});

		Map<String, ProductLog> map = new HashMap<String, ProductLog>();
		List<ProductLog> pls = this.productLogDao.findAll(spec, sort);
		if (null != pls && pls.size() > 0) {
			for (ProductLog pl : pls) {
				if (map.get(pl.getProduct().getOid()) == null) {
					map.put(pl.getProduct().getOid(), pl);
				}
			}
		}
		return map;
	}

	@Transactional
	public BaseResp aduitApply(List<String> oids, String operator) {
		BaseResp response = new BaseResp();

		List<Product> ps = productDao.findByOidIn(oids);
		if (ps == null || ps.size() == 0) {
			// error.define[90000]=产品不存在!(CODE:90000)
			throw AMPException.getException(90000);
		}

		for (Product product : ps) {
			if (product == null || Product.YES.equals(product.getIsDeleted())) {
				// error.define[90000]=产品不存在!(CODE:90000)
				throw AMPException.getException(90000);
			}
			if (product.getPortfolio() == null) {
				// error.define[90011]=请先选择资产池配置
				throw AMPException.getException(90011);
			}
			// 必须有协议书
			if (!isExistFiles(product)) {
				// error.define[90033]=必须上传投资协议书、服务协议书等文件
				throw AMPException.getException(90033);
			}
			if (!Product.AUDIT_STATE_Nocommit.equals(product.getAuditState())) {
				// error.define[90015]=不能提交审核
				throw AMPException.getException(90015);
			}
			product.setState(Product.STATE_Auditing);
			product.setAuditState(Product.AUDIT_STATE_Auditing);
			product.setOperator(operator);
			product.setUpdateTime(DateUtil.getSqlCurrentDate());

			ProductLog plb = new ProductLog();
			plb.setOid(StringUtil.uuid());

			plb.setProduct(product);
			plb.setAuditType(ProductLog.AUDIT_TYPE_Auditing);
			plb.setAuditState(ProductLog.AUDIT_STATE_Commited);
			plb.setAuditor(operator);
			plb.setAuditTime(DateUtil.getSqlCurrentDate());

			this.productDao.saveAndFlush(product);
			this.productLogDao.save(plb);

		}

		return response;
	}

	private boolean isExistFiles(Product product) {
		/*
		 * if (StringUtil.isEmpty(product.getFileKeys())) {// 附件
		 * return false;
		 * } else {// 附件
		 * List<File> files = this.fileService.list(product.getFileKeys(), File.STATE_Valid);
		 * if (files == null || files.size() == 0) {
		 * return false;
		 * }
		 * }
		 */
		if (StringUtil.isEmpty(product.getInvestFileKey())) {// 投资协议
			return false;
		} else {// 投资协议
			List<File> infiles = this.fileService.list(product.getInvestFileKey(), File.STATE_Valid);
			if (infiles == null || infiles.size() == 0) {
				return false;
			}
		}
		if (StringUtil.isEmpty(product.getServiceFileKey())) {// 服务协议
			return false;
		} else {// 服务协议
			List<File> sfiles = this.fileService.list(product.getServiceFileKey(), File.STATE_Valid);
			if (sfiles == null || sfiles.size() == 0) {
				return false;
			}
		}
		return true;
	}

	@Transactional
	public BaseResp aduitApprove(String oid, String operator, String auditComment) throws ParseException {
		BaseResp response = new BaseResp();

		Product product = this.getProductByOid(oid);

		if (!Product.AUDIT_STATE_Auditing.equals(product.getAuditState())) {
			// error.define[90013]=不能审核操作
			throw AMPException.getException(90013);
		}

		Timestamp now = new Timestamp(System.currentTimeMillis());
		product.setState(Product.STATE_Auditpass);
		product.setAuditState(Product.AUDIT_STATE_Reviewing);
		product.setOperator(operator);
		product.setUpdateTime(now);

		ProductLog.ProductLogBuilder plb = ProductLog.builder().oid(StringUtil.uuid());
		{
			plb.product(product).auditType(ProductLog.AUDIT_TYPE_Auditing)
			.auditState(ProductLog.AUDIT_STATE_Approval).auditor(operator).auditTime(now).auditComment(auditComment);
		}

		this.productDao.saveAndFlush(product);
		this.productLogDao.save(plb.build());

		return response;
	}

	@Transactional
	public BaseResp aduitReject(String oid, String auditComment, String operator) throws ParseException {
		BaseResp response = new BaseResp();

		Product product = this.getProductByOid(oid);
		if (!Product.AUDIT_STATE_Auditing.equals(product.getAuditState())) {
			throw AMPException.getException(90013);
		}
		Timestamp now = new Timestamp(System.currentTimeMillis());
		product.setState(Product.STATE_Auditfail);
		product.setAuditState(Product.AUDIT_STATE_Nocommit);
		product.setOperator(operator);
		product.setUpdateTime(now);

		ProductLog.ProductLogBuilder plb = ProductLog.builder().oid(StringUtil.uuid());
		{
			plb.product(product).auditType(ProductLog.AUDIT_TYPE_Auditing)
			.auditState(ProductLog.AUDIT_STATE_Reject).auditor(operator).auditTime(now).auditComment(auditComment);
		}

		this.productDao.saveAndFlush(product);
		this.productLogDao.save(plb.build());

		return response;
	}

	@Transactional
	public BaseResp reviewApprove(String oid, String operator, String auditComment) throws ParseException {
		BaseResp response = new BaseResp();

		Product product = this.getProductByOid(oid);
		if (!Product.AUDIT_STATE_Reviewing.equals(product.getAuditState())) {
			throw AMPException.getException(90014);
		}
		Timestamp now = new Timestamp(System.currentTimeMillis());
		product.setState(Product.STATE_Reviewpass);
		product.setAuditState(Product.AUDIT_STATE_Reviewed);
		product.setOperator(operator);
		product.setUpdateTime(now);

		ProductLog.ProductLogBuilder plb = ProductLog.builder().oid(StringUtil.uuid());
		{
			plb.product(product).auditType(ProductLog.AUDIT_TYPE_Reviewing)
			.auditState(ProductLog.AUDIT_STATE_Approval).auditor(operator).auditTime(now).auditComment(auditComment);
		}

		PortfolioEntity portfolio = this.portfolioService.getByOid(product.getPortfolio().getOid());
		if (portfolio == null) {
			throw AMPException.getException(30001);
		}

		PublisherBaseAccountEntity spv = this.publisherBaseAccountDao.findOne(portfolio.getSpvEntity().getOid());

		PublisherHoldEntity hold = this.publisherHoldService.getPortfolioSpvHold(portfolio, spv);
		if (hold != null) {
			if (product.getType().getOid().equals(Product.TYPE_Producttype_02)) {
				product.setRaisedTotalNumber(hold.getTotalVolume());// 本金余额(持有总份额) totalHoldVolume decimal(16,4)
			}
			if (hold.getProduct() == null) {
				hold.setProduct(product);
				this.publisherHoldDao.saveAndFlush(hold);
			}
		}

		this.productDao.saveAndFlush(product);
		this.productLogDao.save(plb.build());

		return response;
	}

	@Transactional
	public BaseResp reviewReject(String oid, String auditComment, String operator) throws ParseException {
		BaseResp response = new BaseResp();

		Product product = this.getProductByOid(oid);
		if (!Product.AUDIT_STATE_Reviewing.equals(product.getAuditState())) {
			throw AMPException.getException(90014);
		}
		Timestamp now = new Timestamp(System.currentTimeMillis());
		product.setState(Product.STATE_Reviewfail);
		product.setAuditState(Product.AUDIT_STATE_Auditing);
		product.setOperator(operator);
		product.setUpdateTime(now);

		ProductLog.ProductLogBuilder plb = ProductLog.builder().oid(StringUtil.uuid());
		{
			plb.product(product).auditType(ProductLog.AUDIT_TYPE_Reviewing)
			.auditState(ProductLog.AUDIT_STATE_Reject).auditor(operator).auditTime(now).auditComment(auditComment);
		}

		this.productDao.saveAndFlush(product);
		this.productLogDao.save(plb.build());

		return response;
	}

	@Transactional
	public long validateSingle(final String attrName, final String value, final String oid) {

		Specification<Product> spec = new Specification<Product>() {
			@Override
			public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				if (StringUtil.isEmpty(oid)) {
					return cb.and(cb.equal(root.get("isDeleted").as(String.class), Product.NO), cb.equal(root.get(attrName).as(String.class), value));
				} else {
					return cb.and(cb.equal(root.get("isDeleted").as(String.class), Product.NO), cb.equal(root.get(attrName).as(String.class), value), cb.notEqual(root.get("oid").as(String.class), oid));
				}
			}
		};
		spec = Specifications.where(spec);

		return this.productDao.count(spec);
	}

	/**
	 * 查询某个资产下的未删除的产品
	 * 
	 * @param portfolioOid
	 * @return
	 */
	public List<Product> getProductListByPortfolioOid(final String portfolioOid) {

		Specification<Product> spec = new Specification<Product>() {
			@Override
			public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.and(cb.equal(root.get("portfolio").get("oid").as(String.class), portfolioOid), cb.equal(root.get("isDeleted").as(String.class), Product.NO));
			}
		};
		List<Product> products = productDao.findAll(spec);

		return products;
	}

	public List<Product> findByState(String state) {
		return this.productDao.findByState(state);
	}

	/**
	 * 投资校验产品
	 * 
	 * @param tradeOrder
	 */
	public void checkProduct4Invest(InvestorTradeOrderEntity tradeOrder) {
		Product product = tradeOrder.getProduct();

		if (Product.NO.equals(product.getIsOpenPurchase())) {
			// error.define[30020]=申购开关已关闭(CODE:30020)
			throw new AMPException(30020);
		}

		if (Product.TYPE_Producttype_01.equals(product.getType().getOid())) {
			if (!Product.STATE_Raising.equals(product.getState())) {
				// error.define[30017]=定期产品非募集期不能投资(CODE:30017)
				throw new AMPException(30017);
			}
		}

		if (Product.TYPE_Producttype_02.equals(product.getType().getOid())) {
			if (!Product.STATE_Durationing.equals(product.getState())) {
				// error.define[30055]=活期产品非存续期不能投资(CODE:30055)
				throw new AMPException(30055);
			}
		}

		if (Product.YES.equals(product.getIsDeleted())) {
			// error.define[30018]=产品已删除(CODE:30018)
			throw new AMPException(30018);
		}

		// 投资份额需要大于0
		if (tradeOrder.getOrderVolume().compareTo(BigDecimal.ZERO) <= 0) {
			// error.define[30040]=金额不能小于等于0(CODE:30040)
			throw new AMPException(30040);
		}

		if (null != product.getInvestMin() && product.getInvestMin().compareTo(BigDecimal.ZERO) != 0) {
			if (tradeOrder.getOrderVolume().compareTo(product.getInvestMin()) < 0) {
				// error.define[30008]=不能小于产品投资最低金额(CODE:30008)
				throw new AMPException(30008);
			}
		}
		if (null != product.getInvestMax() && product.getInvestMax().compareTo(BigDecimal.ZERO) != 0) {
			if (tradeOrder.getOrderVolume().compareTo(product.getInvestMax()) > 0) {
				// error.define[30009]=已超过产品投资最高金额(CODE:30009)
				throw new AMPException(30009);
			}
		}
		if (null != product.getInvestAdditional() && product.getInvestAdditional().compareTo(BigDecimal.ZERO) != 0) {
			if (null != product.getInvestMin() && product.getInvestMin().compareTo(BigDecimal.ZERO) != 0) {
				if (tradeOrder.getOrderVolume().subtract(product.getInvestMin()).remainder(product.getInvestAdditional()).compareTo(BigDecimal.ZERO) != 0) {
					// error.define[30010]=不满足产品投资追加金额(CODE:30010)
					throw new AMPException(30010);
				}
			} else {
				if (tradeOrder.getOrderVolume().remainder(product.getInvestAdditional()).compareTo(BigDecimal.ZERO) != 0) {
					// error.define[30010]=不满足产品投资追加金额(CODE:30010)
					throw new AMPException(30010);
				}
			}

		}

	}

	public void updateProduct4LockCollectedVolume(InvestorTradeOrderEntity tradeOrder) {
		this.updateProduct4LockCollectedVolume(tradeOrder, false);
	}

	/**
	 * 检验产品可售份额
	 * 
	 * @param tradeOrder
	 */
	public void updateProduct4LockCollectedVolume(InvestorTradeOrderEntity tradeOrder, boolean isRecovery) {
		BigDecimal orderVolume = tradeOrder.getOrderVolume();
		if (isRecovery) {
			orderVolume = orderVolume.negate();
		}
		int i = this.productDao.update4Invest(tradeOrder.getProduct().getOid(), orderVolume);
		if (i < 1) {
			// error.define[30011]=产品可投金额不足(CODE:30011)
			throw new AMPException(30011);
		}
	}

	/**
	 * 份额确认之后解除锁定份额
	 * 
	 * @param tradeOrder
	 */
	public int update4InvestConfirm(Product product, BigDecimal orderVolume) {
		int i = this.productDao.update4InvestConfirm(product.getOid(), orderVolume);
		if (i < 1) {
			// error.define[30012]=解除产品锁定份额异常(CODE:30012)
			throw new AMPException(30012);
		}
		return i;
	}

	public void checkProduct4Redeem(InvestorTradeOrderEntity tradeOrder) {
		Product product = tradeOrder.getProduct();

		if (Product.TYPE_Producttype_01.equals(product.getType().getOid())) {
			// error.define[30060]=非活期产品不能赎回(CODE:30060)
			throw new AMPException(30060);
		}

		// 投资份额需要大于0
		if (tradeOrder.getOrderVolume().compareTo(BigDecimal.ZERO) <= 0) {
			// error.define[30040]=份额不能小于等于0(CODE:30040)
			throw new AMPException(30040);
		}

		if (null != product.getMaxRredeem() && product.getMaxRredeem().compareTo(BigDecimal.ZERO) != 0) {
			if (tradeOrder.getOrderVolume().compareTo(product.getMaxRredeem()) > 0) {
				// error.define[30038]=不满足赎回最高份额条件(CODE:30038)
				throw new AMPException(30038);
			}
		}

		if (Product.NO.equals(product.getIsOpenRemeed())) {
			// error.define[30021]=赎回开关已关闭(CODE:30021)
			throw new AMPException(30021);
		}

//		if (Product.NO.equals(product.getIsOpenRedeemConfirm()) && InvestorTradeOrderEntity.TRADEORDER_orderType_normalRedeem.equals(tradeOrder.getOrderType())) {
//			// error.define[30033]=屏蔽赎回确认处于打开状态(CODE:30033)
//			throw new AMPException(30033);
//		}

	}

	public void update4Redeem(InvestorTradeOrderEntity orderEntity) {
		
		if (InvestorTradeOrderEntity.TRADEORDER_orderType_normalRedeem.equals(orderEntity.getOrderType())) {
			Product product = orderEntity.getProduct();
			if (DecimalUtil.isGoRules(product.getNetMaxRredeemDay())) { // 产品单日赎回上限为零或null，则表示无上限
				int i = this.productDao.update4Redeem(product.getOid(), orderEntity.getOrderVolume());
				if (i < 1) {
					// error.define[30014]=赎回超出产品单日净赎回上限(CODE:30014)
					throw new AMPException(30014);
				}
			}
			
			if (Product.YES.equals(orderEntity.getProduct().getIsPreviousCurVolume())) {

				int i = this.productDao.updatePreviousCurVolume(orderEntity.getProduct().getOid(),
						orderEntity.getOrderVolume());
				if (i < 1) {
					
					throw new AMPException("赎回超出上一个交易日产品规模(基于占比算)");
				}
			}
		}
		
		

	}

	public int update4RedeemConfirm(Product product, BigDecimal orderVolume) {
		int i = this.productDao.update4RedeemConfirm(product.getOid(), orderVolume);
		if (i < 1) {
			// error.define[30019]=赎回确认份额异常(CODE:30019)
			throw new AMPException(30019);
		}
		if (Product.STATE_Clearing.equals(product.getState()) 
				|| Product.STATE_Durationend.equals(product.getState()) 
				|| Product.STATE_Raiseend.equals(product.getState())) {
			i = this.productDao.update4Liquidation(product.getOid());
			if (i > 0) {
				

			}
		}
		return i;
	}

	/**
	 * 更新投资次数
	 */
	public int updatePurchaseNum(String productOid) {
		return this.productDao.updatePurchaseNum(productOid);
	}

	/**
	 * 更新购买人数
	 */
	public int updatePurchasePeopleNumAndPurchaseNum(String productOid) {
		return this.productDao.updatePurchasePeopleNumAndPurchaseNum(productOid);
	}

	public List<Product> findAll() {
		return this.productDao.findAll();
	}

	public List<Product> findAll(Specification<Product> spec) {
		return this.productDao.findAll(spec);
	}

	public Product findByOid(String productOid) {
		Product product = this.productDao.findOne(productOid);
		if (null == product) {
			throw new AMPException("产品不存在");
		}
		return product;
	}

	public List<Product> needIncomeBeforeOffset(String spvOid) {
		return this.productDao.needIncomeBeforeOffset(spvOid);
	}

	/**
	 * 活期产品 存续期、清盘中 发放收益
	 */
	public List<Product> findProductT04Snapshot() {
		return this.productDao.findProductT04Snapshot();
	}

	/**
	 * 定期产品 募集期 发放收益
	 */
	public List<Product> findProductTn4Snapshot(Date incomeDate) {
		return this.productDao.findProductTn4Snapshot(incomeDate);
	}
//
//	public List<Product> findProductTn4Interest() {
//		return this.productDao.findProductTn4Interest();
//	}

	/**
	 * 活期产品新建轧差批次
	 */
	public List<Product> findProductT04NewOffset(PublisherBaseAccountEntity spv) {
		return this.productDao.findProductT04NewOffset(spv.getOid());
	}

	/**
	 * 定期产品新建轧差批次
	 */
	public List<Product> findProductTn4NewOffset(PublisherBaseAccountEntity spv) {
		return this.productDao.findProductTn4NewOffset(spv.getOid());
	}

	/**
	 * 废单：解锁产品锁定已募份额
	 */
	public int update4InvestAbandon(InvestorTradeOrderEntity orderEntity) {
		int i = this.productDao.update4InvestAbandon(orderEntity.getProduct().getOid(), orderEntity.getOrderVolume());
		if (i < 1) {
			// error.define[30034]=废申购单时产品锁定份额异常(CODE:30034)
			throw new AMPException(30034);
		}
		return i;
	}
	/**
	 * 废单：扣除定期产品
	 * 增加最高可售份额
	 * 减少当前份额
	 * 减少已募份额
	 */
	public int update4InvestConfirmAbandon(InvestorTradeOrderEntity orderEntity) {
		int i = this.productDao.update4InvestConfirmAbandon(orderEntity.getProduct().getOid(), orderEntity.getOrderVolume());
		if (i < 1) {
			// error.define[30034]=废申购单时产品锁定份额异常(CODE:30034)
			throw new AMPException("废单定期产品增加最高可售份额异常");
		}
		return i;
	}
	public void update4RedeemRefuse(Product product, BigDecimal orderVolume) {
		if (null != product.getDailyNetMaxRredeem() && product.getDailyNetMaxRredeem().compareTo(BigDecimal.ZERO) != 0) { // 产品单日赎回上限为0，表示无限制
			this.productDao.update4RedeemRefuse(product.getOid(), orderVolume);
		}
	}

	/**
	 * 查找生成协议的产品
	 * 
	 * @return
	 */
	public List<Product> findByProduct4Contract() {

		return this.productDao.findByProduct4Contract();
	}

	/**
	 * 获取需要还本的产品
	 * 
	 * @return
	 */
	public List<Product> getRepayLoanProduct() {
		return this.productDao.getRepayLoanProduct(DateUtil.getSqlDate());
	}

	/**
	 * 获取需要付息的产品
	 * 
	 * @return
	 */
	public List<Product> getRepayInterestProduct() {
		return this.productDao.getRepayInterestProduct(DateUtil.getSqlDate());
	}


//	public int updateFastRedeemLeft(String productOid, BigDecimal orderVolume) {
//		int i = this.productDao.updateFastRedeemLeft(productOid, orderVolume);
//		return i;
//	}
//
//	/**
//	 * @author yuechao
//	 * @return
//	 */
//	public int resetFastRedeemLeft() {
//		int i = this.productDao.resetFastRedeemLeft();
//		return i;
//	}

	/**
	 * @author yuechao
	 */
	public Product saveEntity(Product product) {
		return this.productDao.save(product);
	}

	/**
	 * 发行人下的产品列表
	 * 
	 * @param spec
	 * @param pageable
	 * @return {@link PagesRep<ProductResp>}
	 *         ,如果返回的errCode属性等于0表示成功，否则表示失败，失败原因在errMessage里面体现
	 */
	public PageResp<ProductResp> accproducts(final String publisherBaseAccountOid, Specification<Product> spec, int page, int number) {
		PageResp<ProductResp> pagesRep = new PageResp<ProductResp>();

		Specification<PublisherBaseAccountEntity> pbaspec = new Specification<PublisherBaseAccountEntity>() {
			@Override
			public Predicate toPredicate(Root<PublisherBaseAccountEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.equal(root.get("oid").as(String.class), publisherBaseAccountOid);
			}
		};

		final List<PublisherBaseAccountEntity> pbas = this.publisherBaseAccountDao.findAll(pbaspec);
		if (pbas != null && pbas.size() > 0) {
			if (pbas.size() > 1) {
				spec = Specifications.where(spec).and(new Specification<Product>() {
					@Override
					public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
						Expression<String> exp = root.get("publisherBaseAccount").get("oid").as(String.class);
						In<String> in = cb.in(exp);
						for (PublisherBaseAccountEntity pba : pbas) {
							in.value(pba.getOid());
						}
						return in;
					}
				});
			} else {
				spec = Specifications.where(spec).and(new Specification<Product>() {
					@Override
					public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
						return cb.equal(root.get("publisherBaseAccount").get("oid").as(String.class), pbas.get(0).getOid());
					}
				});
			}
			Pageable pageable = new PageRequest(page - 1, number, new Sort(new Order(Direction.DESC, "createTime")));

			Page<Product> cas = this.productDao.findAll(spec, pageable);

			if (cas != null && cas.getContent() != null && cas.getTotalElements() > 0) {
				List<ProductResp> rows = new ArrayList<ProductResp>();

				Map<String, List<ProductIncomeReward>> incomeRewardNum = this.getProductRewards(cas.getContent());

				Map<String, PublisherBaseAccountEntity> aoprateObjMap = new HashMap<String, PublisherBaseAccountEntity>();
				for (Product p : cas) {
					ProductResp queryRep = new ProductResp(p);
					if (p.getPortfolio() != null && p.getPortfolio().getSpvEntity() != null) {
						if (aoprateObjMap.get(p.getPortfolio().getSpvEntity().getOid()) == null) {
							try {
								
								aoprateObjMap.put(p.getPortfolio().getSpvEntity().getOid(), p.getPortfolio().getSpvEntity());
								
							} catch (Exception e) {
							}
						}
						if (aoprateObjMap.get(p.getPortfolio().getSpvEntity().getOid()) != null) {
							queryRep.setSpvName(aoprateObjMap.get(p.getPortfolio().getSpvEntity().getOid()).getRealName());
						}
					}
					if (incomeRewardNum.get(p.getOid()) != null) {
						queryRep.setRewardNum(incomeRewardNum.get(p.getOid()).size());
					} else {
						queryRep.setRewardNum(0);
					}

					rows.add(queryRep);
				}
				pagesRep.setRows(rows);
			}
			pagesRep.setTotal(cas.getTotalElements());
		} else {
			pagesRep.setRows(null);
			pagesRep.setTotal(0);
		}
		return pagesRep;
	}

	/** 根据发行人和产品状态查询产品 */
	public List<Product> getProductByPublisherOidAndState(final String publisherOid, final String state) {
		List<Product> list = this.productDao.findAll(new Specification<Product>() {
			@Override
			public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.and(cb.equal(root.get("publisherBaseAccount").get("oid").as(String.class), publisherOid), //发行人ID
						cb.equal(root.get("state").as(String.class), state)//产品状态
				);
			}
		});

		return list;
	}

	public Product findOne(String oid) {
		return this.productDao.findOne(oid);
	}

	public int updateRepayStatus(Product product, String repayLoanStatus, String repayInterestStatus) {
		return this.productDao.updateRepayStatus(product.getOid(), repayLoanStatus, repayInterestStatus);

	}

	/**
	 * @author yuechao 还本付息逾期产品
	 */
	public List<Product> getOverdueProduct(Date curDate) {
		return this.productDao.getOverdueProduct(curDate);
	}

	public void batchUpdate(List<Product> pList) {
		this.productDao.save(pList);

	}

	/**
	 * 还本付息锁
	 */
	@Transactional(TxType.REQUIRES_NEW)
	public void repayLock(String productOid) {
		int i = this.productDao.repayLock(productOid);
		if (i < 1) {
			// error.define[30066]=已还本付息或还本付息中(CODE:30066)
			throw AMPException.getException(30066);
		}
	}

	/** 校验产品交易时间 */
	public void isInDealTime(String productOid) {
		Product product = this.findByOid(productOid);
		//交易时间
		if (!StringUtil.isEmpty(product.getDealStartTime()) && !StringUtil.isEmpty(product.getDealEndTime())) {
			if (!DateUtil.isIn(DateUtil.getSqlCurrentDate(), product.getDealStartTime(), product.getDealEndTime())) {
				// error.define[30048]=非交易时间不接收订单(CODE:30048)
				throw AMPException.getException(30048);
			}
		}
	}

	/**
	 * 获取当前在售的产品列表
	 * 
	 * @return
	 */
	public Product findOnSaleTyjProducts() {
		return this.productDao.findOnSaleTyjProducts(LabelEnum.tiyanjin.toString());
	}
	
	@Transactional(TxType.REQUIRES_NEW)
	public int lockProduct(String productOid) {
		int i = this.productDao.lockProduct(productOid);
		if (i < 1) {
			throw new AMPException("产品正在处理中,请稍侯");
		}
		return i;
		
	}
	
	@Transactional(TxType.REQUIRES_NEW)
	public int unLockProduct(String productOid) {
		int i = this.productDao.unLockProduct(productOid);
		if (i < 1) {
			logger.error("产品解锁异常");
		}
		return i;
	}
	
	// @Transactional(TxType.REQUIRES_NEW)
	public int repayInterestOk(String productOid) {
		int i = this.productDao.repayInterestOk(productOid);
		if (i < 1) {
			logger.error("{}产品正在派息状态更新失败", productOid);
		}
		return i;
	}
	
	@Transactional(TxType.REQUIRES_NEW)
	public int repayInterestLock(String productOid) {
		int i = this.productDao.repayInterestLock(productOid);
		if (i < 1) {
			throw new AMPException("产品正在派息中或已派息");
		}
		return i;
	}
	
	@Transactional(TxType.REQUIRES_NEW)
	public int repayLoanLock(String productOid) {
		int i = this.productDao.repayLoanLock(productOid);
		if (i < 1) {
			throw new AMPException("产品正在还本或尚未派息");
		}
		return i;
	}
	
	@Transactional(TxType.REQUIRES_NEW)
	public int repayLoanEnd(String productOid, String repayLoanStatus) {
		int i = this.productDao.repayLoanEnd(productOid, repayLoanStatus);
		if (i < 1) {
			logger.error("{}产品正在派息状态更新失败", productOid);
		}
		return i;
	}
	
	
	public Map<String, Integer> productAmount(PublisherBaseAccountEntity ba) {
		Map<String, Integer> map = new HashMap<String, Integer>();
		List<Product> list = this.productDao.productAmount(ba);
		for (Product product : list) {
			if (map.get(product.getState()) == null) {
				map.put(product.getState(), 1);
			} else {
				map.put(product.getState(), map.get(product.getState()) + 1);
			}
		}
		return map;
	}
	public int closedProductAmount(String publisherOid) {
		return this.productDao.closedProductAmount(publisherOid);
	}
	
	
	public Map<String, Integer> productAmount() {
		Map<String, Integer> map = new HashMap<String, Integer>();
		List<Product> list = this.productDao.productAmount();
		for (Product product : list) {
			if (map.get(product.getState()) == null) {
				map.put(product.getState(), 1);
			} else {
				map.put(product.getState(), map.get(product.getState()) + 1);
			}
		}
		
		return map;
	}
	public int closedProductAmount() {
		return this.productDao.closedProductAmount();
	}
	
//	@Scheduled(cron = "1 1 * * * ?")
//	@Transactional(value = TxType.REQUIRES_NEW)
//	public void getLatestTen() {
//		List<Product> tmp = this.productDao.getLatestTen();
//		CollectInfoSdk.collectInfo(JSONObject.toJSONString(tmp));
//	}
}
