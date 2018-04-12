package com.guohuai.ams.product;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.guohuai.ams.acct.books.AccountBook;
import com.guohuai.ams.acct.books.AccountBookService;
import com.guohuai.ams.label.LabelDao;
import com.guohuai.ams.label.LabelEntity;
import com.guohuai.ams.label.LabelResp;
import com.guohuai.ams.portfolio.entity.PortfolioEntity;
import com.guohuai.ams.product.productChannel.ProductChannel;
import com.guohuai.ams.product.productChannel.ProductChannelDao;
import com.guohuai.ams.productLabel.ProductLabel;
import com.guohuai.ams.productLabel.ProductLabelDao;
import com.guohuai.ams.productLabel.ProductLabelService;
import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.basic.component.ext.web.PageResp;
import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.DateUtil;
import com.guohuai.component.util.DecimalUtil;
import com.guohuai.component.util.StringUtil;
import com.guohuai.mmp.investor.tradeorder.InvestorRepayCashTradeOrderService;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderService;
import com.guohuai.mmp.investor.tradeorder.check.InvestorAbandonTradeOrderService;
import com.guohuai.mmp.platform.publisher.product.offset.OffsetConstantRep;
import com.guohuai.mmp.platform.publisher.product.offset.ProductOffsetService;
import com.guohuai.mmp.publisher.baseaccount.PublisherBaseAccountDao;
import com.guohuai.mmp.publisher.baseaccount.PublisherBaseAccountEntity;
import com.guohuai.mmp.publisher.hold.PlanIncomeInfo;
import com.guohuai.mmp.publisher.hold.PublisherHoldEntity;
import com.guohuai.mmp.publisher.hold.PublisherHoldService;

@Transactional
@Service
public class ProductDurationService {

	@Autowired
	private ProductDao productDao;
	@Autowired
	private ProductService productService;
	@Autowired
	private PublisherBaseAccountDao publisherBaseAccountDao;
	@Autowired
	private PublisherHoldService publisherHoldService;
	@Autowired
	private AccountBookService accountBookService;
	@Autowired
	private ProductOffsetService productOffsetService;
	@Autowired
	private InvestorRepayCashTradeOrderService investorCashTradeOrderService;
	@Autowired
	private InvestorAbandonTradeOrderService investorAbandonTradeOrderService;
	@Autowired
	private ProductLabelService productLabelService;
	@Autowired
	private InvestorTradeOrderService investorTradeOrderService; 
	
	@Autowired
	private ProductChannelDao productChannelDao;
	@Autowired
	private ProductLabelDao productLabelDao;
	@Autowired
	private LabelDao labelDao;
	@PersistenceContext
	private EntityManager em;

	public PageResp<ProductLogListResp> durationList(Specification<Product> spec, Pageable pageable) {
		PageResp<ProductLogListResp> pagesRep = new PageResp<ProductLogListResp>();

		Page<Product> cas = this.productDao.findAll(spec, pageable);

		if (cas != null && cas.getContent() != null && cas.getTotalElements() > 0) {
			List<ProductLogListResp> rows = new ArrayList<ProductLogListResp>();
			Map<String, PublisherBaseAccountEntity> aoprateObjMap = new HashMap<String, PublisherBaseAccountEntity>();
		
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
				rows.add(queryRep);
			}
			pagesRep.setRows(rows);
		}

		pagesRep.setTotal(cas.getTotalElements());

		return pagesRep;
	}
	
	
	/**
	 * 获取存续期产品的名称列表，包含id
	 * 
	 * @return
	 */
	@Transactional
	public List<JSONObject> productNameList(Specification<Product> spec) {
		List<JSONObject> jsonObjList = Lists.newArrayList();
		List<Product> ps = productDao.findAll(spec, new Sort(new Order(Direction.DESC, "updateTime")));
		if (ps != null && ps.size() > 0) {
			JSONObject jsonObj = null;
			for (Product p : ps) {
				jsonObj = new JSONObject();
				jsonObj.put("oid", p.getOid());
				jsonObj.put("name", p.getName());
				jsonObjList.add(jsonObj);
			}
		}

		return jsonObjList;
	}

	public ProductDetailResp getProductByOid(String oid) {
		ProductDetailResp pr = null;
		if (StringUtil.isEmpty(oid)) {
			Specification<Product> spec = new Specification<Product>() {
				@Override
				public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
					return cb.and(cb.equal(root.get("isDeleted").as(String.class), Product.NO), cb.equal(root.get("auditState").as(String.class), Product.AUDIT_STATE_Reviewed));
				}
			};
			spec = Specifications.where(spec);
			List<Product> ps = productDao.findAll(spec, new Sort(new Order(Direction.DESC, "updateTime")));
			if (ps != null && ps.size() > 0) {
				Product p = ps.get(0);
				pr = new ProductDetailResp(p);
				if (p.getPortfolio() != null && p.getPortfolio().getSpvEntity() != null) {
					pr.setSpvName(p.getPortfolio().getSpvEntity().getRealName());
				}
			}
		} else {
			pr = this.productService.read(oid);
		}

		return pr;
	}

	/**
	 * 投资赎回确认接口
	 * 
	 * @param productOid
	 *            产品oid
	 * @param investAmount
	 *            投资金额
	 * @param remeedAmount
	 *            赎回金额
	 * @throws Exception
	 */
	public void comfirmInvest(String productOid, BigDecimal investAmount, BigDecimal remeedAmount) throws Exception {
		if (!StringUtil.isEmpty(productOid)) {
			Product p = productService.getProductByOid(productOid);

			if (investAmount == null) {
				investAmount = BigDecimal.ZERO;
			}
			if (remeedAmount == null) {
				remeedAmount = BigDecimal.ZERO;
			}
			if (p.getPortfolio() == null || p.getPortfolio().getSpvEntity() == null) {
				throw AMPException.getException("没有找到产品对应的资产池和SPV");
			}
			if (p.getPortfolio() != null && p.getPortfolio().getSpvEntity() != null) {

				PortfolioEntity portfolio = p.getPortfolio();

				PublisherBaseAccountEntity spv = this.publisherBaseAccountDao.findOne(portfolio.getSpvEntity().getOid());

				PublisherHoldEntity hold = this.publisherHoldService.getPortfolioSpvHold(portfolio, spv);
				if (hold == null) {
					throw AMPException.getException("没有找到产品对应的持有人名册");
				}

				// 其他事件: 判断产品状态是否是清盘中, 如果是清盘中, 调用 清盘完成接口
				if (Product.STATE_Clearing.equals(p.getState())) {
					this.productCleared(productOid);
				}

			}

		}

	}

	/**
	 * 产品清盘接口: 清盘中的产品, 不可开启申购/开启赎回, 并在该事件中自动关闭申购/赎回, 在产品表中记录清盘操作人, 清盘操作时间
	 * 产品状态调整为清盘中: clearing 其他事件: 调用dubbo接口 LEOrderService.productClear()
	 * 
	 * @param productOid
	 * @param operator
	 * @param remark
	 *            备注
	 * @return
	 * @throws Exception
	 */
	public BaseResp productClearing(String productOid, String operator, String remark) throws Exception {
		BaseResp resp = new BaseResp();
		if (!StringUtil.isEmpty(productOid)) {
			Product p = productService.getProductByOid(productOid);
			if (p.getLockCollectedVolume().compareTo(BigDecimal.ZERO) != 0) {
				throw new AMPException("清盘前，请先确认当前产品所有份额");
			}
			p.setState(Product.STATE_Clearing);
			p.setIsOpenPurchase(Product.NO);
			p.setIsOpenRemeed(Product.NO);
			p.setUpdateTime(DateUtil.getSqlCurrentDate());
			p.setClearingTime(DateUtil.getSqlCurrentDate());
			p.setClearingOperator(operator);
			this.productDao.saveAndFlush(p);
		}
		return resp;
	}

	/**
	 * 清盘完成接口 判断产品 currentVolume 是否为0, 如果为0, 则认为产品清盘完成, 修改产品装填为 cleared 其他事件:
	 * @param productOid
	 */
	public void productCleared(String productOid) throws Exception {
		if (!StringUtil.isEmpty(productOid)) {
			Product p = productService.getProductByOid(productOid);
			if (p.getCurrentVolume().compareTo(BigDecimal.ZERO) == 0) {// 判断产品 currentVolume 是否为0, 如果为0, 则认为产品清盘完成, 修改产品装填为 cleared
				p.setState(Product.STATE_Cleared);
				p.setUpdateTime(DateUtil.getSqlCurrentDate());
				p.setClearedTime(DateUtil.getSqlCurrentDate());
				this.productDao.saveAndFlush(p);
			}
		}
	}

	public ProductDurationResp getProductDuration(String oid) {
		ProductDurationResp pdr = new ProductDurationResp();

		Product p = null;
		if (StringUtil.isEmpty(oid)) {
			Specification<Product> spec = new Specification<Product>() {
				@Override
				public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
					return cb.and(cb.equal(root.get("isDeleted").as(String.class), Product.NO), cb.equal(root.get("auditState").as(String.class), Product.AUDIT_STATE_Reviewed));
				}
			};
			spec = Specifications.where(spec);
			List<Product> ps = productDao.findAll(spec, new Sort(new Order(Direction.DESC, "updateTime")));
			if (ps != null && ps.size() > 0) {
				p = ps.get(0);
			}
		} else {
			p = this.productService.getProductByOid(oid);
		}

		if (p != null) {
			pdr.setOid(p.getOid());
			pdr.setCurrentVolume(p.getCurrentVolume()); // 持有人总份额
			// realNetting 产品实时轧差结果(通过王国接口取)
			OffsetConstantRep offsets = this.productOffsetService.findByProductOid(p.getOid());

			pdr.setRealNetting(offsets.getNetPosition());

			if (p.getPortfolio() != null) {
				// SPV预付费金 1401 SPV应收费金 2301
				Map<String, AccountBook> accountBookMap = accountBookService.find(p.getPortfolio().getOid(), "1401", "2301");
				if (accountBookMap != null && accountBookMap.size() > 0) {
					// 资产池
					AccountBook prepaidFee = accountBookMap.get("1401");// SPV预付费金 1401
					if (prepaidFee != null) {
						pdr.setPrepaidFee(prepaidFee.getBalance());
					}
					AccountBook payFee = accountBookMap.get("2301");// SPV应收费金 2301
					if (payFee != null) {
						pdr.setPayFee(payFee.getBalance());
					}
				}
				pdr.setShares(p.getPortfolio().getShares());// SPV基子份额
				pdr.setMarketValue(p.getPortfolio().getNetValue());// SPV基子市值
				pdr.setDrawedChargefee(p.getPortfolio().getDrawedChargefee());// SPV累计已提取费金
				pdr.setCountintChargefee(p.getPortfolio().getCountintChargefee());// SPV累计已计提费金
			}
			if (null != p.getPortfolio()) {
				PortfolioEntity ap = p.getPortfolio();
				BigDecimal cashPosition = ap.getCashPosition() == null ? BigDecimal.ZERO : ap.getCashPosition();
				BigDecimal liquidDimensions = ap.getLiquidDimensions() == null ? BigDecimal.ZERO : ap.getLiquidDimensions();
				pdr.setHqla(cashPosition.add(liquidDimensions).setScale(2, RoundingMode.HALF_UP));
			}
		}

		return pdr;
	}

	public BaseResp currentTradingRuleSet(CurrentTradingRuleSetForm form, String operator) throws ParseException, Exception {
		BaseResp response = new BaseResp();
		Product product = this.productService.getProductByOid(form.getOid());
		Timestamp now = new Timestamp(System.currentTimeMillis());

		if (Product.TYPE_Producttype_02.equals(product.getType().getOid())) {
			if (!StringUtil.isEmpty(form.getPurchaseConfirmDate())) {
				product.setPurchaseConfirmDays(Integer.valueOf(form.getPurchaseConfirmDate()));
			} else {
				product.setPurchaseConfirmDays(null);
			}
//			product.setPurchaseConfirmDaysType(form.getPurchaseConfirmDateType());

			if (!StringUtil.isEmpty(form.getInterestsDate())) {
				product.setInterestsFirstDays(Integer.valueOf(form.getInterestsDate()));
			} else {
				product.setInterestsFirstDays(null);
			}

			if (!StringUtil.isEmpty(form.getRedeemConfirmDate())) {
				product.setRedeemConfirmDays(Integer.valueOf(form.getRedeemConfirmDate()));
			} else {
				product.setRedeemConfirmDays(null);
			}
//			product.setRedeemConfirmDaysType(form.getRedeemConfirmDateType());

			if (!StringUtil.isEmpty(form.getNetUnitShare())) {
				product.setNetUnitShare(new BigDecimal(form.getNetUnitShare()));
			} else {
				product.setNetUnitShare(null);
			}

			if (!StringUtil.isEmpty(form.getNetMaxRredeemDay())) {
				product.setNetMaxRredeemDay(new BigDecimal(form.getNetMaxRredeemDay()));
			} else {
				product.setNetMaxRredeemDay(null);
			}

			if (!StringUtil.isEmpty(form.getMaxHold())) {
				product.setMaxHold(new BigDecimal(form.getMaxHold()));
			} else {
				product.setMaxHold(null);
			}
			if (!StringUtil.isEmpty(form.getSingleDailyMaxRedeem())) {
				product.setSingleDailyMaxRedeem(new BigDecimal(form.getSingleDailyMaxRedeem()));
			} else {
				product.setSingleDailyMaxRedeem(null);
			}
			if (!StringUtil.isEmpty(form.getMinRredeem())) {
				product.setMinRredeem(new BigDecimal(form.getMinRredeem()));
			} else {
				product.setMinRredeem(null);
			}

			if (!StringUtil.isEmpty(form.getAdditionalRredeem())) {
				product.setAdditionalRredeem(new BigDecimal(form.getAdditionalRredeem()));
			} else {
				product.setAdditionalRredeem(null);
			}

			if (!StringUtil.isEmpty(form.getMaxRredeem())) {
				product.setMaxRredeem(new BigDecimal(form.getMaxRredeem()));
			} else {
				product.setMaxRredeem(null);
			}
			if (!StringUtil.isEmpty(form.getRedeemWithoutInterest())) {
				product.setRedeemWithoutInterest(form.getRedeemWithoutInterest());
			}
			product.setRredeemDateType(form.getRredeemDateType());
			
			product.setIsPreviousCurVolume(form.getIsPreviousCurVolume()); // 赎回占比开关
			if (Product.YES.equals(product.getIsPreviousCurVolume())) {
				product.setPreviousCurVolumePercent(form.getPreviousCurVolumePercent());  // 赎回占上一交易日规模百分比
				product.setPreviousCurVolume(DecimalUtil.zoomIn(product.getCurrentVolume().multiply(product.getPreviousCurVolumePercent()), 100)); // 上一个交易日产品当前规模(基于占比算) 
			} else {
				product.setPreviousCurVolumePercent(BigDecimal.ZERO);  // 赎回占上一交易日规模百分比
				product.setPreviousCurVolume(BigDecimal.ZERO); // 上一个交易日产品当前规模(基于占比算) 
			}
			
			
		}

		if (!StringUtil.isEmpty(form.getInvestMin())) {
			product.setInvestMin(new BigDecimal(form.getInvestMin()));
		} else {
			product.setInvestMin(null);
		}
		if (!StringUtil.isEmpty(form.getInvestAdditional())) {
			product.setInvestAdditional(new BigDecimal(form.getInvestAdditional()));
		} else {
			product.setInvestAdditional(null);
		}

		if (!StringUtil.isEmpty(form.getInvestMax())) {
			product.setInvestMax(new BigDecimal(form.getInvestMax()));
		} else {
			product.setInvestMax(null);
		}
		product.setDealStartTime(form.getDealStartTime());
		product.setDealEndTime(form.getDealEndTime());
		product.setInvestDateType(form.getInvestDateType());
		product.setSingleDayRedeemCount(form.getSingleDayRedeemCount()); // 单人单日赎回次数
		product.setRewardInterest(form.getRewardInterest()); // 平台奖励收益

		product.setOperator(operator);
		product.setUpdateTime(now);

		product = this.productDao.saveAndFlush(product);
		
		return response;
	}

	/*
	 * 单人单日赎回限额设置
	 */
	public BaseResp updateSingleDailyMaxRedeem(final String oid, BigDecimal singleDailyMaxRedeem, String operator) throws ParseException, Exception {
		BaseResp response = new BaseResp();

		Product p = productService.getProductByOid(oid);

		p.setSingleDailyMaxRedeem(singleDailyMaxRedeem);
		p.setUpdateTime(new Timestamp(System.currentTimeMillis()));
		productDao.saveAndFlush(p);

		return response;
	}

//	/**
//	 * 激活赎回确认
//	 */
//	public BaseResp openRedeemConfirm(final String oid, String operator) throws ParseException {
//		BaseResp response = new BaseResp();
//		Product p = productService.getProductByOid(oid);
//		if (Product.NO.equals(p.getIsOpenRedeemConfirm())) {
//			p.setIsOpenRedeemConfirm(Product.YES);
//			p.setUpdateTime(new Timestamp(System.currentTimeMillis()));
//			productDao.saveAndFlush(p);
//
//
//			investorAbandonTradeOrderService.isOpenRedeemConfirm(p);
//		}
//		return response;
//	}
//
//	/*
//	 * 屏蔽赎回确认
//	 */
//	public BaseResp closeRedeemConfirm(final String oid, String operator) throws ParseException {
//		BaseResp response = new BaseResp();
//		Product p = productService.getProductByOid(oid);
//		if (Product.YES.equals(p.getIsOpenRedeemConfirm())) {
//			p.setIsOpenRedeemConfirm(Product.NO);
//			p.setUpdateTime(new Timestamp(System.currentTimeMillis()));
//			productDao.saveAndFlush(p);
//
//
//			investorAbandonTradeOrderService.isOpenRedeemConfirm(p);
//		}
//		return response;
//	}

	/*
	 * 快速赎回设置
	 */
//	public BaseResp updateFastRedeem(final String oid, String fastRedeemStatus, BigDecimal fastRedeemMax, String operator) throws ParseException, Exception {
//		BaseResp response = new BaseResp();
//
//		Product p = productService.getProductByOid(oid);
//
//		if (Product.YES.equals(p.getFastRedeemStatus())) {
//			if (Product.YES.equals(fastRedeemStatus)) {
//				productDao.updateFastRedeemMax(oid, fastRedeemMax, fastRedeemStatus, operator);
//			} else {
//				p.setFastRedeemStatus(fastRedeemStatus);
//				p.setOperator(operator);
//				p.setUpdateTime(new Timestamp(System.currentTimeMillis()));
//				productDao.saveAndFlush(p);
//			}
//		} else if (Product.YES.equals(fastRedeemStatus)) {
//			p.setFastRedeemStatus(fastRedeemStatus);
//			p.setFastRedeemMax(fastRedeemMax);
//			p.setFastRedeemLeft(fastRedeemMax);
//			p.setOperator(operator);
//			p.setUpdateTime(new Timestamp(System.currentTimeMillis()));
//			productDao.saveAndFlush(p);
//		}
//		return response;
//	}

	/*
	 * 扩展标签设置
	 */
	public BaseResp updateProductExtendLabel(final String oid, String[] expandProductLabels, String operator) throws ParseException, Exception {
		BaseResp response = new BaseResp();

		Product product = productService.getProductByOid(oid);

		List<String> labelOids = new ArrayList<String>();

		List<ProductLabel> productLabels = productLabelService.findProductLabelsByProduct(product);
		if (productLabels != null && productLabels.size() > 0) {
			for (ProductLabel pl : productLabels) {
				if (LabelEntity.labelType_general.equals(pl.getLabel().getLabelType())) {//基础标签
					labelOids.add(pl.getLabel().getOid());
				}
			}
		}
		if (expandProductLabels != null && expandProductLabels.length > 0) {//扩展标签
			for (String ex : expandProductLabels) {
				labelOids.add(ex);
			}
		}
		this.productLabelService.saveAndFlush(product, labelOids);

		return response;
	}

	/**
	 * 募集失败
	 */
	public BaseResp productRaiseFail(String productOid, String operator) {

		Product product = productService.getProductByOid(productOid);
		if (!Product.STATE_Raiseend.equals(product.getState())) {
			// error.define[30076]=非募集期结束,不可募集失败(CODE:30076)
			throw new AMPException(30076);
		}
		boolean isConfirm = this.investorTradeOrderService.isConfirm(productOid);
		if (!isConfirm) {
			// error.define[30064]=请先确认份额(CODE:30064)
			throw new AMPException(30064);
		}
		
		product.setUpdateTime(DateUtil.getSqlCurrentDate());
		product.setOperator(operator);
		product.setState(Product.STATE_RaiseFail);
		product.setRaiseFailDate(DateUtil.getSqlDate());
		productDao.save(product);
		
		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				investorCashTradeOrderService.isEstablish(productOid, false);
			}
		});
		t.start();

		return new BaseResp();
	}

	/**
	 * 募集成功
	 */
	public BaseResp productRaiseSuccess(final String oid, String operator) throws ParseException, Exception {
		BaseResp response = new BaseResp();

		Product product = productService.getProductByOid(oid);
		if (!Product.STATE_Raiseend.equals(product.getState())) {
			// error.define[30076]=非募集期结束,不可募集成立(CODE:30076)
			throw new AMPException(30076);
		}
		boolean isConfirm = this.investorTradeOrderService.isConfirm(product.getOid());
		if (!isConfirm) {
			// error.define[30064]=请先确认份额(CODE:30064)
			throw new AMPException(30064);
		}
		
		

		investorCashTradeOrderService.isEstablish(oid, true);

		Timestamp now = new Timestamp(System.currentTimeMillis());
		product.setUpdateTime(now);
		product.setOperator(operator);
		product.setState(Product.STATE_Durationing);

		product.setSetupDate(DateUtil.getSqlDate());// 产品成立时间（存续期开始时间）
		product.setDurationPeriodEndDate(DateUtil.addSQLDays(product.getSetupDate(), product.getDurationPeriodDays() - 1)); // 存续期结束时间
		product.setRepayDate(DateUtil.addSQLDays(product.getDurationPeriodEndDate(), product.getAccrualRepayDays()));// 到期还款时间

		productDao.saveAndFlush(product);
	

		return response;
	}
	
	/**
	 * 筛选产品活、定期、心愿计划产品的列表
	 * 
	 * */
	
	public PageResp<ProductLogListResp> toTnProductList(String name, String marketState, int page, int rows,
			String type,String status,Boolean isAll) {
		PageResp<ProductLogListResp> resp = new PageResp<ProductLogListResp>();
		StringBuffer query = this.query(name, marketState, type,status,isAll);		
		@SuppressWarnings("unchecked")
		List<Object[]> list = em.createNativeQuery(query.toString()).getResultList();	
		if (list != null && list.size() > 0) {
			List<ProductLogListResp> productList = new ArrayList<ProductLogListResp>();
			
			for (Object[] arr : list) {				
				Product p = this.productService.findByOid((String)arr[0]);				
				if (p != null) {
					/** 筛选产品的上下架状态和渠道 */
					List<ProductChannel> listChannel = this.productChannelDao.queryChannel(p.getOid());
					if(listChannel != null && listChannel.size() > 0){
						//遍历
						for(ProductChannel productChannel : listChannel){
							//筛选产品在不同渠道上的状态
							if(StringUtil.isEmpty(marketState) || productChannel.getMarketState().equals(marketState) ){
								ProductLogListResp queryRep = this.queryLabel(p);
								queryRep.setMarketState(productChannel.getMarketState());//上架状态
								queryRep.setMarketStateDesc(this.descMarketState(queryRep.getMarketState()));//上下架状态描述
								queryRep.setCid(productChannel.getChannel().getCid());//渠道cid
								queryRep.setCkey(productChannel.getChannel().getCkey());//渠道ckey
								queryRep.setChannelName(productChannel.getChannel().getChannelName());//渠道名称
								productList.add(queryRep);
							}													
						}
					}else{
						ProductLogListResp queryRep = this.queryLabel(p);
						queryRep.setMarketStateDesc("未匹配渠道");
						queryRep.setCid("--");//渠道cid
						queryRep.setCkey("--");//渠道ckey
						queryRep.setChannelName("--");//渠道名称
						productList.add(queryRep);
					}				
				}
			}
			
			/** 循环遍历剔除产品中产品和渠道都相同的 */
			Set set = new  HashSet(); 
	         List<ProductLogListResp> newList1 = new  ArrayList<ProductLogListResp>(); 
	         for (ProductLogListResp cd:productList) {
	            if(set.add(cd)){
	            	newList1.add(cd);
	            }
	        }
			
			resp.setTotal(productList.size());
			/**对产品进行分页*/
			List<ProductLogListResp> newList = new ArrayList<ProductLogListResp>();
			int currIdx = (page > 1 ? (page - 1) * rows : 0);
			for (int j = 0; j < rows && j < productList.size() - currIdx; j++) {
				ProductLogListResp productResp = productList.get(currIdx + j);
				newList.add(productResp);
			}
			resp.setRows(newList);
		}
		return resp;
	}


	public BaseResp savaValue(String productOid,int value) {
		Product p =this.productDao.findByOid(productOid);
		if(p != null){
			p.setWeightValue(value);
			this.productDao.save(p);
		}else{
			throw new AMPException("未找到对应的产品");
		}
		return new BaseResp();
	}

	/** 将产品置顶的操作  *//*
	public ToTopRep toTop(String productOid) {
		// TODO Auto-generated method stub
		ToTopRep rep = new ToTopRep();
		Product product = this.productDao.findByOid(productOid);
		rep.setIsTop(false);
		if(product !=null){
			if(product.getTop()==2){
				product.setTop(1);
				this.productDao.save(product);
				rep.setIsTop(true);
			}
		}else{
			throw new AMPException("该产品不存在");
		}
		return rep;
	}*/

	/*
	 * 是否自动派息设置
	 */
//	public BaseResp isAutoAssignIncomeSet(final String oid, String isAutoAssignIncome, String operator) throws ParseException, Exception {
//		BaseResp response = new BaseResp();
//		Product p = productService.getProductByOid(oid);
//		if (!isAutoAssignIncome.equals(p.getIsAutoAssignIncome())) {
//			p.setUpdateTime(new Timestamp(System.currentTimeMillis()));
//			p.setOperator(operator);
//			p.setIsAutoAssignIncome(isAutoAssignIncome);
//			productDao.saveAndFlush(p);
//		}
//		return response;
//	}
	
	/**  
	 * 编写查询语句
	 * 
	 * */
	public StringBuffer query(String name, String marketState,String type,String status,Boolean isAll){
		
		StringBuffer sele = new StringBuffer();
		StringBuffer from = new StringBuffer();
		StringBuffer where = new StringBuffer();
		StringBuffer order = new StringBuffer();
		sele.append("select t1.oid,t1.name");
		from.append(" from t_gam_product t1");	
		if(StringUtil.isEmpty(marketState) ){
			where.append(" where 0=0");
		}else if(marketState.equals("NOCHANNEL")) {
			where.append(" WHERE t1.oid NOT IN(SELECT t2.productOid FROM  t_gam_product_channel t2)");
		}else {
			from.append(" , t_gam_product_channel t3 ");
			where.append(" where t3.productOid = t1.oid and t3.marketState ='").append(marketState).append("'");
		}		
		if(!StringUtil.isEmpty(type)){
			if (type.equals("wishproduct")) {
				where.append(
						" and t1.oid in (SELECT t4.oid FROM t_gam_product t4,t_money_platform_label t5,t_money_platform_label_product t6 WHERE t4.oid = t6.productOid AND   t6.labelOid = t5.oid AND t5.labelCode='11')");
			} else {
				if(!isAll){
					where.append(
							" and t1.oid not in (SELECT t4.oid FROM t_gam_product t4,t_money_platform_label t5,t_money_platform_label_product t6 WHERE t4.oid = t6.productOid AND   t6.labelOid = t5.oid AND t5.labelCode='11')");
				}
				where.append(" and t1.type='").append(type).append("'");
			}							
		}
			where.append(" and t1.isDeleted='no' AND t1.auditState='reviewed'");
			
			if(!StringUtil.isEmpty(name)){
				 where.append(" AND t1.fullName like '%").append(name).append("%'");
			}
			if(!StringUtil.isEmpty(status)){
				where.append(" and t1.state = '").append(status).append("'");
			}
			
			if(!isAll){
				order.append(" order by t1.weightValue asc,t1.createTime desc");
			}else{
				order.append(" order by t1.createTime desc");
			}
					
			//拼接sql语句
			return sele.append(from.toString()).append(where.toString()).append(order.toString());
	}
	
	/** 上下架状态描述 */
	public String descMarketState(String marketState){
		String	marketStateDesc = "";
		if(marketState.equals("NOSHELF")){
			marketStateDesc = "已匹配渠道未上架";
		}else if(marketState.equals("ONSHELF")){
			marketStateDesc = "已上架";
		}else if(marketState.equals("OFFSHELF")){
			marketStateDesc = "已下架";
		}else{
			marketStateDesc = "未匹配渠道";
		}
		return marketStateDesc;
	}
	/** 
	 * 后台查询产品的标签
	 *  */
	public ProductLogListResp queryLabel(Product p){
		ProductLogListResp queryRep = new ProductLogListResp(p);
		Map<String, PublisherBaseAccountEntity> aoprateObjMap = new HashMap<String, PublisherBaseAccountEntity>();
		if (p.getPortfolio() != null && p.getPortfolio().getSpvEntity() != null) {
			if (aoprateObjMap.get(p.getPortfolio().getSpvEntity().getOid()) == null) {
				try {
					aoprateObjMap.put(p.getPortfolio().getSpvEntity().getOid(),
							p.getPortfolio().getSpvEntity());
				} catch (Exception e) {
				}
			}
			if (aoprateObjMap.get(p.getPortfolio().getSpvEntity().getOid()) != null) {
				queryRep.setSpvName(
						aoprateObjMap.get(p.getPortfolio().getSpvEntity().getOid()).getRealName());
			}
		}
		/** 添加产品的标签 */
		List<ProductLabel> productLabel = this.productLabelDao.findByProduct(p);
		List<LabelInfo>  labelList = new ArrayList<LabelInfo>();
		if (productLabel != null && productLabel.size() > 0) {
			for (ProductLabel prolab : productLabel) {
				LabelInfo labelInfo = new LabelInfo(prolab.getLabel());			
				labelList.add(labelInfo);							
			}
			queryRep.setLabel(labelList);
		}
		
		return queryRep;
	}
}
