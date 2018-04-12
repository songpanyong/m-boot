package com.guohuai.mmp.publisher.product.client;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.guohuai.ams.duration.fact.income.IncomeAllocate;
import com.guohuai.ams.duration.fact.income.IncomeAllocateDao;
import com.guohuai.ams.duration.fact.income.IncomeEvent;
import com.guohuai.ams.label.LabelRep;
import com.guohuai.ams.label.LabelResp;
import com.guohuai.ams.product.Product;
import com.guohuai.ams.product.ProductDao;
import com.guohuai.ams.product.ProductDecimalFormat;
import com.guohuai.ams.product.ProductPojo;
import com.guohuai.ams.product.ProductService;
import com.guohuai.ams.product.productChannel.ProductChannel;
import com.guohuai.ams.product.productChannel.ProductChannelDao;
import com.guohuai.ams.product.reward.ProductIncomeReward;
import com.guohuai.ams.product.reward.ProductIncomeRewardService;
import com.guohuai.ams.productLabel.ProductLabel;
import com.guohuai.ams.productLabel.ProductLabelDao;
import com.guohuai.ams.productLabel.ProductLabelService;
import com.guohuai.basic.component.ext.web.PageResp;
import com.guohuai.cache.entity.HoldCacheEntity;
import com.guohuai.cache.service.CacheHoldService;
import com.guohuai.cache.service.CacheProductService;
import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.DateUtil;
import com.guohuai.component.util.DecimalUtil;
import com.guohuai.component.util.StringUtil;
import com.guohuai.component.web.view.RowsRep;
import com.guohuai.file.File;
import com.guohuai.file.FileResp;
import com.guohuai.file.FileService;
import com.guohuai.mmp.investor.tradeorder.OrderDateService;
import com.guohuai.mmp.jiajiacai.wishplan.product.JJCProductService;
import com.guohuai.mmp.jiajiacai.wishplan.risklevel.RiskLevelDao;
import com.guohuai.mmp.publisher.investor.InterestFormula;
import com.guohuai.operate.api.AdminSdk;
import com.guohuai.operate.api.objs.admin.AdminObj;

import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class ProductClientService {

	@Autowired
	private IncomeAllocateDao incomeAllocateDao;
	@Autowired
	private ProductService productService;
	@Autowired
	private FileService fileService;
	@Autowired
	private ProductChannelDao productChannelDao;
	@Autowired
	private AdminSdk adminSdk;
	@Autowired
	private ProductIncomeRewardService rewardService;
	@Autowired
	private ProductDao productDao;
	@Autowired
	private ProductLabelDao productLabelDao;
	@Autowired
	private ProductLabelService productLabelService;
	@Autowired
	private OrderDateService orderDateService;
	@Autowired
	private CacheHoldService cacheHoldService;
	@PersistenceContext
	private EntityManager em;
	@Autowired
	CacheProductService cacheProductService;
	@Autowired
	private JJCProductService jJCProductService;
	@Autowired
	private RiskLevelDao riskLevelDao;

	//起购金额 sort
	Comparator<ProductChannel> investMinSort(final String order) {
		return new Comparator<ProductChannel>() {
			public int compare(ProductChannel o1, ProductChannel o2) {
				//这里写比较方法
				if (o1.getProduct() != null && o2.getProduct() != null && o1.getProduct().getInvestMin() != null && o2.getProduct().getInvestMin() != null) {
					if (!"desc".equals(order)) {
						int result = o1.getProduct().getInvestMin().compareTo(o2.getProduct().getInvestMin());
						return result;
					} else {
						int result = o2.getProduct().getInvestMin().compareTo(o1.getProduct().getInvestMin());
						return result;
					}
				}
				return 0;//然后return一个int型的值
			}
		};
	}

	//投资期限sort
	Comparator<ProductChannel> durationPeriodDaysSort(final String order) {
		return new Comparator<ProductChannel>() {
			public int compare(ProductChannel o1, ProductChannel o2) {
				//这里写比较方法
				if (o1.getProduct() != null && o2.getProduct() != null && o1.getProduct().getDurationPeriodDays() != null && o2.getProduct().getDurationPeriodDays() != null) {
					if (!"desc".equals(order)) {
						int result = o1.getProduct().getDurationPeriodDays().compareTo(o2.getProduct().getDurationPeriodDays());
						return result;
					} else {
						int result = o2.getProduct().getDurationPeriodDays().compareTo(o1.getProduct().getDurationPeriodDays());
						return result;
					}
				}
				return 0;//然后return一个int型的值
			}
		};
	}

	//预期年化收益率 sort
	Comparator<ProductChannel> expArorSort(final String order, final Map<String, Map<String, BigDecimal>> productExpArorMap) {
		return new Comparator<ProductChannel>() {
			public int compare(ProductChannel o1, ProductChannel o2) {
				//这里写比较方法
				if (o1.getProduct() != null && o2.getProduct() != null && o1.getProduct().getExpAror() != null && o2.getProduct().getExpAror() != null) {
					BigDecimal o1expAror = o1.getProduct().getExpAror();
					BigDecimal o2expAror = o2.getProduct().getExpAror();
					if (productExpArorMap.get(o1.getProduct().getOid()) != null) {
						Map<String, BigDecimal> minMaxReward = productExpArorMap.get(o1.getProduct().getOid());
						o1expAror = o1expAror.add(minMaxReward.get("minReward"));
					}
					if (productExpArorMap.get(o2.getProduct().getOid()) != null) {
						Map<String, BigDecimal> minMaxReward = productExpArorMap.get(o2.getProduct().getOid());
						o2expAror = o2expAror.add(minMaxReward.get("minReward"));
					}
					if (!"desc".equals(order)) {
						int result = o1expAror.compareTo(o2expAror);
						return result;
					} else {
						int result = o2expAror.compareTo(o1expAror);
						return result;
					}
				}
				return 0;//然后return一个int型的值
			}
		};
	}

	//活期产品maxSaleVolume-lockCollectedVolume sort
	Comparator<ProductChannel> maxSalelockCollectedVolumeSort(final String order) {
		return new Comparator<ProductChannel>() {
			public int compare(ProductChannel o1, ProductChannel o2) {
				// 这里写比较方法
				if (o1.getProduct() != null && o2.getProduct() != null && o1.getProduct().getMaxSaleVolume() != null && o2.getProduct().getMaxSaleVolume() != null) {
					if (!"desc".equals(order)) {
						int result = o1.getProduct().getMaxSaleVolume().subtract(o1.getProduct().getLockCollectedVolume()).compareTo(o2.getProduct().getMaxSaleVolume().subtract(o2.getProduct().getLockCollectedVolume()));
						return result;
					} else {
						int result = o2.getProduct().getMaxSaleVolume().subtract(o2.getProduct().getLockCollectedVolume()).compareTo(o1.getProduct().getMaxSaleVolume().subtract(o1.getProduct().getLockCollectedVolume()));
						return result;
					}
				}
				return 0;// 然后return一个int型的值
			}
		};
	}

	//产品A的预期年化收益率是 1%-5% 奖励收益率最小是0.1% 最大是0.9% 那么展示的时候就展示 （1%+0.1%）-（5%+0.9%）即展示的是 1.1%-5.9%；
	//产品B预期年化收益率是4% 奖励收益率最小是3% 最大是9%  那么展示的时候就展示 （4%+3%）-（4%+9%）即展示的是 7%-13%；
	/**
	 * 获取产品的最小和最大奖励收益率
	 * 
	 * @param pcs
	 * @return
	 */
	private Map<String, Map<String, BigDecimal>> getProductsMinMaxRewards(List<String> productOids) {
		Map<String, Map<String, BigDecimal>> productExpArorMap = new HashMap<String, Map<String, BigDecimal>>();

		if (productOids != null && productOids.size() > 0) {
			/** * 阶梯收益率 */
			Map<String, List<ProductIncomeReward>> productRewardMap = new HashMap<String, List<ProductIncomeReward>>();//<productOid,List<ProductIncomeReward>>
			List<ProductIncomeReward> rewards = rewardService.productsRewardList(productOids);
			if (rewards != null && rewards.size() > 0) {
				for (ProductIncomeReward reward : rewards) {
					if (productRewardMap.get(reward.getProduct().getOid()) == null) {
						productRewardMap.put(reward.getProduct().getOid(), new ArrayList<ProductIncomeReward>());
					}
					productRewardMap.get(reward.getProduct().getOid()).add(reward);
				}
			}
			if (productRewardMap.size() > 0) {
				for (String productOid : productOids) {
					List<ProductIncomeReward> prewards = productRewardMap.get(productOid);
					if (prewards != null && prewards.size() > 0) {//算上奖励收益
						BigDecimal minReward = prewards.get(0).getRatio();
						BigDecimal maxReward = prewards.get(0).getRatio();
						for (ProductIncomeReward preward : prewards) {
							if (preward.getRatio().compareTo(minReward) < 0) {
								minReward = preward.getRatio();
							}
							if (preward.getRatio().compareTo(maxReward) > 0) {
								maxReward = preward.getRatio();
							}
						}
						Map<String, BigDecimal> minMaxReward = new HashMap<String, BigDecimal>();
						minMaxReward.put("minReward", minReward);
						minMaxReward.put("maxReward", maxReward);
						productExpArorMap.put(productOid, minMaxReward);
					}
				}
			}
		}

		return productExpArorMap;
	}

	/**
	 * app查询可以申购的定期活期产品推荐列表
	 * 
	 * @param spec
	 * @param pageable
	 * @return {@link PageResp<ProductListResp>}
	 *         ,如果返回的errCode属性等于0表示成功，否则表示失败，失败原因在errMessage里面体现
	 */
	public PageResp<ProductListResp> recommends(Specification<ProductChannel> spec, Pageable pageable) {
		Page<ProductChannel> cas = this.productChannelDao.findAll(spec, pageable);
		PageResp<ProductListResp> pagesRep = new PageResp<ProductListResp>();
		if (cas != null && cas.getContent() != null && cas.getTotalElements() > 0) {

			List<String> productOids = new ArrayList<String>();
			for (ProductChannel p : cas) {
				productOids.add(p.getProduct().getOid());
			}

			Map<String, List<LabelResp>> labelMap = this.findProductLabels(productOids);

			Map<String, Map<String, BigDecimal>> productExpArorMap = this.getProductsMinMaxRewards(productOids);
			List<ProductListResp> rows = new ArrayList<ProductListResp>();
			Page<IncomeAllocate> pcas = null;
			for (ProductChannel p : cas) {
				if (Product.TYPE_Producttype_02.equals(p.getProduct().getType().getOid())) {
					pcas = getProductIncomeAllocate(p.getProduct().getPortfolio().getOid(), 1);
				} else {
					pcas = null;
				}
				ProductListResp queryRep = new ProductListResp(p, productExpArorMap, pcas);
				queryRep.setProductLabels(labelMap.get(p.getProduct().getOid()));
				rows.add(queryRep);
			}
			pagesRep.setRows(rows);
		}
		pagesRep.setTotal(cas.getTotalElements());
		return pagesRep;
	}

	/**
	 * app查询全部产品列表
	 * 
	 * @param spec
	 * 
	 * @param
	 * @return {@link PageResp<ProductListResp>}
	 *         ,如果返回的errCode属性等于0表示成功，否则表示失败，失败原因在errMessage里面体现
	 */
	public PageResp<ProductListResp> list(Specification<ProductChannel> spec, int page, int rows) {
		PageResp<ProductListResp> pagesRep = new PageResp<ProductListResp>();

		Direction sortDirection = Direction.DESC;
		Sort rackTimeSort = new Sort(new Order(sortDirection, "rackTime"));

		List<ProductChannel> productChannels = this.productChannelDao.findAll(spec, rackTimeSort);
		if (productChannels != null && productChannels.size() > 0) {
			List<ProductChannel> currnets = new ArrayList<ProductChannel>();//活期募集中
			List<ProductChannel> raisings = new ArrayList<ProductChannel>();//定期募集中
			List<ProductChannel> raiseends = new ArrayList<ProductChannel>();//定期募集結束

			for (ProductChannel pc : productChannels) {
				if (Product.TYPE_Producttype_01.equals(pc.getProduct().getType().getOid())) {
					if (Product.STATE_Raising.equals(pc.getProduct().getState())) {
						raisings.add(pc);
					} else if (Product.STATE_Raiseend.equals(pc.getProduct().getState())) {
						raiseends.add(pc);
					}
				} else {
					currnets.add(pc);
				}
			}

			List<ProductChannel> pcs = new ArrayList<ProductChannel>();
			for (ProductChannel pc : currnets) {
				pcs.add(pc);
			}
			for (ProductChannel pc : raisings) {
				pcs.add(pc);
			}
			for (ProductChannel pc : raiseends) {
				pcs.add(pc);
			}

			int i = 0;
			List<ProductChannel> pcrow = new ArrayList<ProductChannel>();//满足条件后的指定页数的列数的数据
			for (ProductChannel p : pcs) {
				if (i >= (page - 1) * rows && i < page * rows) {
					pcrow.add(p);
				}
				i++;
			}
			if (pcrow.size() > 0) {
				List<String> productOids = new ArrayList<String>();
				for (ProductChannel p : pcrow) {
					productOids.add(p.getProduct().getOid());
				}
				Map<String, List<LabelResp>> labelMap = this.findProductLabels(productOids);
				Map<String, Map<String, BigDecimal>> productExpArorMap = this.getProductsMinMaxRewards(productOids);

				List<ProductListResp> productRows = new ArrayList<ProductListResp>();
				for (ProductChannel p : pcrow) {
					ProductListResp queryRep = new ProductListResp(p, productExpArorMap, null);
					queryRep.setProductLabels(labelMap.get(p.getProduct().getOid()));
					productRows.add(queryRep);
				}
				pagesRep.setRows(productRows);
			}
			pagesRep.setTotal(pcs.size());
		}

		return pagesRep;
	}

	/**
	 * app定期查询可购买产品列表
	 * 
	 * @param spec
	 * @param expArorStartStr
	 * @param expArorEndStr
	 * @param page
	 * @param rows
	 * @param sort
	 * @param order
	 * @return {@link PageResp<ProductPeriodicResp>}
	 *         ,如果返回的errCode属性等于0表示成功，否则表示失败，失败原因在errMessage里面体现
	 */
	public PageResp<ProductPeriodicResp> periodicList(Specification<ProductChannel> spec, String expArorStartStr, String expArorEndStr, int page, int rows, String sort, final String order) {
		PageResp<ProductPeriodicResp> pagesRep = new PageResp<ProductPeriodicResp>();

		Direction sortDirection = Direction.DESC;
		Sort rackTimeSort = new Sort(new Order(sortDirection, "rackTime"));

		if ("rackTime".equals(sort)) {
			if (!"desc".equals(order)) {
				sortDirection = Direction.ASC;
			}
		}
		List<ProductChannel> productChannelList = this.productChannelDao.findAll(spec, rackTimeSort);
		if (productChannelList != null && productChannelList.size() > 0) {
			List<String> productOids = new ArrayList<String>();
			for (ProductChannel p : productChannelList) {
				productOids.add(p.getProduct().getOid());
			}

			List<ProductChannel> productChannels = null;

			if (!StringUtil.isEmpty(expArorStartStr) || !StringUtil.isEmpty(expArorEndStr)) {
				productChannels = new ArrayList<ProductChannel>();

				Map<String, Map<String, BigDecimal>> productExpArorMap = this.getProductsMinMaxRewards(productOids);

				if (!StringUtil.isEmpty(expArorStartStr) && !StringUtil.isEmpty(expArorEndStr)) {
					BigDecimal expArorStart = new BigDecimal(expArorStartStr);
					BigDecimal expArorEnd = new BigDecimal(expArorEndStr);
					for (ProductChannel pc : productChannelList) {
						BigDecimal expAror = pc.getProduct().getExpAror();
						BigDecimal expArorSec = pc.getProduct().getExpArorSec();
						if (productExpArorMap.get(pc.getProduct().getOid()) != null) {
							Map<String, BigDecimal> minMaxReward = productExpArorMap.get(pc.getProduct().getOid());
							expAror = expAror.add(minMaxReward.get("minReward"));
							expArorSec = expArorSec.add(minMaxReward.get("maxReward"));
						}
						if ((expArorStart.compareTo(expAror) <= 0 && expArorEnd.compareTo(expAror) >= 0) || (expArorStart.compareTo(expArorSec) <= 0 && expArorEnd.compareTo(expArorSec) >= 0)) {
							productChannels.add(pc);
						}
					}
				} else if (!StringUtil.isEmpty(expArorStartStr)) {
					BigDecimal expArorStart = new BigDecimal(expArorStartStr);
					for (ProductChannel pc : productChannelList) {
						BigDecimal expAror = pc.getProduct().getExpAror();
						BigDecimal expArorSec = pc.getProduct().getExpArorSec();
						if (productExpArorMap.get(pc.getProduct().getOid()) != null) {
							Map<String, BigDecimal> minMaxReward = productExpArorMap.get(pc.getProduct().getOid());
							expAror = expAror.add(minMaxReward.get("minReward"));
							expArorSec = expArorSec.add(minMaxReward.get("maxReward"));
						}
						if (expArorStart.compareTo(expAror) <= 0 || expArorStart.compareTo(expArorSec) <= 0) {
							productChannels.add(pc);
						}
					}

				} else if (!StringUtil.isEmpty(expArorEndStr)) {
					BigDecimal expArorEnd = new BigDecimal(expArorEndStr);
					for (ProductChannel pc : productChannelList) {
						BigDecimal expAror = pc.getProduct().getExpAror();
						BigDecimal expArorSec = pc.getProduct().getExpArorSec();
						if (productExpArorMap.get(pc.getProduct().getOid()) != null) {
							Map<String, BigDecimal> minMaxReward = productExpArorMap.get(pc.getProduct().getOid());
							expAror = expAror.add(minMaxReward.get("minReward"));
							expArorSec = expArorSec.add(minMaxReward.get("maxReward"));
						}
						if (expArorEnd.compareTo(expAror) >= 0 || expArorEnd.compareTo(expArorSec) >= 0) {
							productChannels.add(pc);
						}
					}
				}

				List<ProductChannel> raisings = new ArrayList<ProductChannel>();//募集中
				List<ProductChannel> raiseends = new ArrayList<ProductChannel>();//募集結束

				//设置成基础收益率加上奖励收益率 便于按收益里排序的正确性
				for (ProductChannel pc : productChannels) {
					if (Product.STATE_Raising.equals(pc.getProduct().getState())) {
						raisings.add(pc);
					} else if (Product.STATE_Raiseend.equals(pc.getProduct().getState())) {
						raiseends.add(pc);
					}
				}

				if ("expAror".equals(sort)) {//预期年化收益率 
					Collections.sort(raisings, expArorSort(order, productExpArorMap));
					Collections.sort(raiseends, expArorSort(order, productExpArorMap));
				} else if ("investMin".equals(sort)) {//起投金额
					Collections.sort(raisings, investMinSort(order));
					Collections.sort(raiseends, investMinSort(order));
				} else if ("durationPeriodDays".equals(sort)) {//投资期限
					Collections.sort(raisings, durationPeriodDaysSort(order));
					Collections.sort(raiseends, durationPeriodDaysSort(order));
				}

				List<ProductChannel> pcs = new ArrayList<ProductChannel>();
				for (ProductChannel pc : raisings) {
					pcs.add(pc);
				}
				for (ProductChannel pc : raiseends) {
					pcs.add(pc);
				}
				int i = 0;
				List<ProductPeriodicResp> productRows = new ArrayList<ProductPeriodicResp>();

				List<String> lproductOids = new ArrayList<String>();
				for (ProductChannel p : pcs) {
					lproductOids.add(p.getProduct().getOid());
				}
				Map<String, List<LabelResp>> labelMap = new HashMap<String, List<LabelResp>>();
				if (!lproductOids.isEmpty()) {
					labelMap = this.findProductLabels(lproductOids);
				}
				for (ProductChannel p : pcs) {
					if (i >= (page - 1) * rows && i < page * rows) {
						ProductPeriodicResp queryRep = new ProductPeriodicResp(p, productExpArorMap);
						queryRep.setProductLabels(labelMap.get(p.getProduct().getOid()));
						productRows.add(queryRep);
					}
					i++;
				}
				if (productRows.size() > 0) {
					pagesRep.setRows(productRows);
				}
				pagesRep.setTotal(pcs.size());
			} else {
				productChannels = productChannelList;

				List<ProductChannel> raisings = new ArrayList<ProductChannel>();//募集中
				List<ProductChannel> raiseends = new ArrayList<ProductChannel>();//募集結束

				if ("expAror".equals(sort)) {//预期年化收益率 

					Map<String, Map<String, BigDecimal>> productExpArorMap = this.getProductsMinMaxRewards(productOids);
					//设置成基础收益率加上奖励收益率 便于按收益里排序的正确性
					for (ProductChannel pc : productChannels) {
						if (Product.STATE_Raising.equals(pc.getProduct().getState())) {
							raisings.add(pc);
						} else if (Product.STATE_Raiseend.equals(pc.getProduct().getState())) {
							raiseends.add(pc);
						}
					}

					Collections.sort(raisings, expArorSort(order, productExpArorMap));
					Collections.sort(raiseends, expArorSort(order, productExpArorMap));

					List<ProductChannel> pcs = new ArrayList<ProductChannel>();
					for (ProductChannel pc : raisings) {
						pcs.add(pc);
					}
					for (ProductChannel pc : raiseends) {
						pcs.add(pc);
					}

					int i = 0;
					List<ProductPeriodicResp> productRows = new ArrayList<ProductPeriodicResp>();

					List<String> lproductOids = new ArrayList<String>();
					for (ProductChannel p : pcs) {
						lproductOids.add(p.getProduct().getOid());
					}
					Map<String, List<LabelResp>> labelMap = this.findProductLabels(lproductOids);

					for (ProductChannel p : pcs) {
						if (i >= (page - 1) * rows && i < page * rows) {
							ProductPeriodicResp queryRep = new ProductPeriodicResp(p, productExpArorMap);
							queryRep.setProductLabels(labelMap.get(p.getProduct().getOid()));
							productRows.add(queryRep);
						}
						i++;
					}
					if (productRows.size() > 0) {
						pagesRep.setRows(productRows);
					}
					pagesRep.setTotal(pcs.size());
				} else {
					for (ProductChannel pc : productChannels) {
						if (Product.STATE_Raising.equals(pc.getProduct().getState())) {
							raisings.add(pc);
						} else if (Product.STATE_Raiseend.equals(pc.getProduct().getState())) {
							raiseends.add(pc);
						}
					}
					if ("investMin".equals(sort)) {//起投金额
						Collections.sort(raisings, investMinSort(order));
						Collections.sort(raiseends, investMinSort(order));
					} else if ("durationPeriodDays".equals(sort)) {//投资期限
						Collections.sort(raisings, durationPeriodDaysSort(order));
						Collections.sort(raiseends, durationPeriodDaysSort(order));
					}

					List<ProductChannel> pcs = new ArrayList<ProductChannel>();
					for (ProductChannel pc : raisings) {
						pcs.add(pc);
					}
					for (ProductChannel pc : raiseends) {
						pcs.add(pc);
					}

					int i = 0;
					List<ProductChannel> pcrow = new ArrayList<ProductChannel>();//满足条件后的指定页数的列数的数据
					for (ProductChannel p : pcs) {
						if (i >= (page - 1) * rows && i < page * rows) {
							pcrow.add(p);
						}
						i++;
					}

					if (pcrow.size() > 0) {
						productOids = new ArrayList<String>();
						for (ProductChannel p : pcrow) {
							productOids.add(p.getProduct().getOid());
						}

						Map<String, List<LabelResp>> labelMap = this.findProductLabels(productOids);
						Map<String, Map<String, BigDecimal>> productExpArorMap = this.getProductsMinMaxRewards(productOids);

						List<ProductPeriodicResp> productRows = new ArrayList<ProductPeriodicResp>();
						for (ProductChannel p : pcrow) {
							ProductPeriodicResp queryRep = new ProductPeriodicResp(p, productExpArorMap);
							queryRep.setProductLabels(labelMap.get(p.getProduct().getOid()));
							productRows.add(queryRep);
						}
						pagesRep.setRows(productRows);
					}
					pagesRep.setTotal(pcs.size());

				}
			}

		}
		return pagesRep;
	}

	/**
	 * app活期查询可购买产品列表
	 * 
	 * @param spec
	 * @param pageable
	 * @return {@link PageResp<ProductCurrentResp>}
	 *         ,如果返回的errCode属性等于0表示成功，否则表示失败，失败原因在errMessage里面体现
	 */
	public PageResp<ProductCurrentResp> currentList(Specification<ProductChannel> spec, int page, int rows, String sort, final String order) {
		PageResp<ProductCurrentResp> pagesRep = new PageResp<ProductCurrentResp>();
		Direction sortDirection = Direction.DESC;
		if (!"desc".equals(order)) {
			sortDirection = Direction.ASC;
		}
		Sort rackTimeSort = new Sort(new Order(sortDirection, "rackTime"));

		List<ProductChannel> productChannelList = this.productChannelDao.findAll(spec, rackTimeSort);
		if (productChannelList != null && productChannelList.size() > 0) {
			Collections.sort(productChannelList, maxSalelockCollectedVolumeSort(order));
			int i = 0;
			List<ProductChannel> pcrow = new ArrayList<ProductChannel>();//满足条件后的指定页数的列数的数据
			for (ProductChannel p : productChannelList) {
				if (i >= (page - 1) * rows && i < page * rows) {
					pcrow.add(p);
				}
				i++;
			}
			if (pcrow.size() > 0) {
				List<String> productOids = new ArrayList<String>();
				for (ProductChannel p : pcrow) {
					productOids.add(p.getProduct().getOid());
				}
				Map<String, List<LabelResp>> labelMap = this.findProductLabels(productOids);
				Map<String, Map<String, BigDecimal>> productExpArorMap = this.getProductsMinMaxRewards(productOids);
				List<ProductCurrentResp> productRows = new ArrayList<ProductCurrentResp>();
				for (ProductChannel cp : pcrow) {
					ProductCurrentResp queryRep = new ProductCurrentResp();
					Product product = cp.getProduct();
					queryRep.setOid(product.getOid());
					queryRep.setChannelOid(cp.getChannel().getOid());
					queryRep.setProductCode(product.getCode());
					queryRep.setProductName(product.getName());
					queryRep.setProductFullName(product.getFullName());//产品全称
					queryRep.setCurrentVolume(product.getCurrentVolume());//当前金额
					queryRep.setCollectedVolume(product.getCollectedVolume());//已集总金额
					queryRep.setLockCollectedVolume(product.getLockCollectedVolume());//锁定已募份额
					queryRep.setRaisedTotalNumber(product.getRaisedTotalNumber());//募集总份额
					queryRep.setMaxSaleVolume(product.getMaxSaleVolume());//最高可售份额
					queryRep.setPurchaseNum(product.getPurchaseNum());
					queryRep.setInvestMin(product.getInvestMin());//单笔投资最低金额
					queryRep.setLockPeriodDays(product.getLockPeriodDays());//锁定期
					queryRep.setNetUnitShare(product.getNetUnitShare());

					String incomeCalcBasis = product.getIncomeCalcBasis();//产品计算基础

					if (productExpArorMap.get(product.getOid()) != null) {
						Map<String, BigDecimal> minMaxReward = productExpArorMap.get(product.getOid());
						BigDecimal minReward = minMaxReward.get("minReward");
						BigDecimal maxReward = minMaxReward.get("maxReward");

						String minRewardStr = ProductDecimalFormat.format(ProductDecimalFormat.multiply(minReward)) + "%";
						String maxRewardStr = ProductDecimalFormat.format(ProductDecimalFormat.multiply(maxReward)) + "%";
						if (minRewardStr.equals(maxRewardStr)) {
							queryRep.setRewardYieldRange(minRewardStr);
						} else {
							queryRep.setRewardYieldRange(minRewardStr + "-" + maxRewardStr);
						}

						String rewardTenThsProfitFst = ProductDecimalFormat.format(minReward.multiply(new BigDecimal(10000)).divide(new BigDecimal(incomeCalcBasis), 4, RoundingMode.HALF_UP), "0.0000");
						String rewardTenThsProfitSec = ProductDecimalFormat.format(maxReward.multiply(new BigDecimal(10000)).divide(new BigDecimal(incomeCalcBasis), 4, RoundingMode.HALF_UP), "0.0000");
						if (rewardTenThsProfitFst.equals(rewardTenThsProfitSec)) {
							queryRep.setRewardTenThsProfit(rewardTenThsProfitFst);
						} else {
							queryRep.setRewardTenThsProfit(rewardTenThsProfitFst + "-" + rewardTenThsProfitSec);
						}
					}

					if ((product.getExpAror() != null && product.getExpAror().compareTo(new BigDecimal("0")) > 0) || (product.getExpArorSec() != null && product.getExpArorSec().compareTo(new BigDecimal("0")) > 0)) {
						BigDecimal expAror = product.getExpAror();//预期年化收益率
						BigDecimal expArorSec = product.getExpArorSec();//预期年化收益率
						if (null != product.getRewardInterest()) {
							expAror = expAror.add(DecimalUtil.zoomIn(product.getRewardInterest(), 100));
							expArorSec = expArorSec.add(DecimalUtil.zoomIn(product.getRewardInterest(), 100));
						}

						String expArorStr = ProductDecimalFormat.format(ProductDecimalFormat.multiply(expAror)) + "%";
						String expArorSecStr = ProductDecimalFormat.format(ProductDecimalFormat.multiply(expArorSec)) + "%";
						if (expArorStr.equals(expArorSecStr)) {
							queryRep.setAnnualInterestSec(expArorStr);
						} else {
							queryRep.setAnnualInterestSec(expArorStr + "-" + expArorSecStr);
						}

						String tenThsPerDayProfitFst = InterestFormula.compound(new BigDecimal(10000), expAror, incomeCalcBasis).toString();
						String tenThsPerDayProfitSec = InterestFormula.compound(new BigDecimal(10000), expArorSec, incomeCalcBasis).toString();
						if (tenThsPerDayProfitFst.equals(tenThsPerDayProfitSec)) {
							queryRep.setTenThsPerDayProfit(tenThsPerDayProfitFst);
						} else {
							queryRep.setTenThsPerDayProfit(tenThsPerDayProfitFst + "-" + tenThsPerDayProfitSec);
						}

						if (expArorStr.equals(expArorSecStr)) {//固定预期收益率 
							if (productExpArorMap.get(product.getOid()) != null) {
								queryRep.setShowType(ProductCurrentResp.SHOW_TYPE_5);
							} else {
								queryRep.setShowType(ProductCurrentResp.SHOW_TYPE_2);
							}
						} else {
							if (productExpArorMap.get(product.getOid()) != null) {
								queryRep.setShowType(ProductCurrentResp.SHOW_TYPE_4);
							} else {
								queryRep.setShowType(ProductCurrentResp.SHOW_TYPE_1);
							}
						}
					} else {
						if (productExpArorMap.get(product.getOid()) != null) {
							Page<IncomeAllocate> pcas = getProductIncomeAllocate(product.getPortfolio().getOid(), 1);
							if (pcas != null && pcas.getContent() != null && pcas.getTotalElements() > 0) {
								BigDecimal yesterdayYieldExp = pcas.getContent().get(0).getRatio();//昨日年化收益率
								queryRep.setYesterdayYield(ProductDecimalFormat.format(ProductDecimalFormat.multiply(yesterdayYieldExp)) + "%");//昨日年化收益率
								queryRep.setTenThsPerDayProfit(ProductDecimalFormat.format(yesterdayYieldExp.multiply(new BigDecimal(10000)).divide(new BigDecimal(incomeCalcBasis), 4, RoundingMode.HALF_UP), "0.0000"));
								queryRep.setShowType(ProductCurrentResp.SHOW_TYPE_6);
							} else {
								queryRep.setShowType(ProductCurrentResp.SHOW_TYPE_7);
							}
						} else {
							Page<IncomeAllocate> pcas = getProductIncomeAllocate(product.getPortfolio().getOid(), 7);
							if (pcas != null && pcas.getContent() != null && pcas.getTotalElements() > 0) {
								BigDecimal sevenDayYieldRatio = new BigDecimal("0");
								for (IncomeAllocate ia : pcas.getContent()) {
									sevenDayYieldRatio = sevenDayYieldRatio.add(ia.getRatio());
								}
								BigDecimal sevenDayYieldExp = sevenDayYieldRatio.multiply(new BigDecimal("100")).divide(new BigDecimal("" + new Long(pcas.getTotalElements()).intValue()), 4, RoundingMode.HALF_UP);//七日年化收益率单位（%）
								queryRep.setSevenDayYield(ProductDecimalFormat.format(sevenDayYieldExp, "0.00") + "%");//七日年化收益率:最新7条取平均值
								queryRep.setTenThsPerDayProfit(ProductDecimalFormat.format(sevenDayYieldExp.divide(new BigDecimal("100")).multiply(new BigDecimal("10000")).divide(new BigDecimal(incomeCalcBasis), 4, RoundingMode.HALF_UP), "0.0000"));
							}
							queryRep.setShowType(ProductCurrentResp.SHOW_TYPE_3);
						}
					}
					queryRep.setProductLabels(labelMap.get(product.getOid()));
					productRows.add(queryRep);
				}
				pagesRep.setRows(productRows);
			}
		}

		pagesRep.setTotal(productChannelList.size());

		return pagesRep;
	}

	/**
	 * 获取某个产品的最新收益分配
	 * 
	 * @param assetPoolOid
	 * @return
	 */
	private Page<IncomeAllocate> getProductIncomeAllocate(final String portfolioOid, int rows) {
		//收益分配
		Specification<IncomeAllocate> pspec = new Specification<IncomeAllocate>() {
			@Override
			public Predicate toPredicate(Root<IncomeAllocate> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.and(
						cb.equal(root.get("incomeEvent").get("status").as(String.class), IncomeEvent.STATUS_Allocated),
						cb.equal(root.get("incomeEvent").get("portfolio").get("oid").as(String.class), portfolioOid));

			}
		};
		pspec = Specifications.where(pspec);
		Page<IncomeAllocate> pcas = this.incomeAllocateDao.findAll(pspec, new PageRequest(0, rows, new Sort(new Order(Direction.DESC, "baseDate"))));
		return pcas;
	}

	/**
	 * 活期产品详情
	 * 
	 * @param oid
	 * @return
	 */

	public ProductCurrentDetailResp currentDetail(String oid) {
		Product product = productService.getProductByOid(oid);

		String incomeCalcBasis = product.getIncomeCalcBasis();//产品计算基础
		BigDecimal expAror = product.getExpAror();//预期年化收益率
		BigDecimal expArorSec = product.getExpArorSec();//预期年化收益率
		
		String expArorStr = null;//产品预期年化收益率开始值
		String expArorSecStr = null;//产品预期年化收益率结束值
		if (product.getExpAror() != null && product.getExpAror().compareTo(new BigDecimal("0")) > 0) {
			expArorStr = ProductDecimalFormat.format(ProductDecimalFormat.multiply(expAror)) + "%";
		}
		if (product.getExpArorSec() != null && product.getExpArorSec().compareTo(new BigDecimal("0")) > 0) {
			expArorSecStr = ProductDecimalFormat.format(ProductDecimalFormat.multiply(expArorSec)) + "%";
		}

		List<ProductIncomeReward> prewards = rewardService.productRewardList(oid);//阶梯收益
		Page<IncomeAllocate> pcas = getProductIncomeAllocate(product.getPortfolio().getOid(), 30);//实际收益分配的产品收益率
		
		ProductCurrentDetailResp pr = new ProductCurrentDetailResp(product);
		pr.setCompanyName(product.getPublisherBaseAccount().getRealName());
		pr.setInvestTime(DateUtil.getSqlCurrentDate()); //投资时间
		//根据当天时间计算起息日期
		java.sql.Date beginAccuralDate = this.orderDateService.getBeginAccuralDate(product);
		pr.setInterestsFirstDate(beginAccuralDate);//起息日期
		pr.setLockPeriodDays(product.getLockPeriodDays());//锁定期
		pr.setPurchasePeopleNum(product.getPurchasePeopleNum());//已购人数
		pr.setInvestMin(product.getInvestMin());//起投金额
		pr.setType(product.getType().getOid());//产品类型
		pr.setProductName(product.getName());//产品名称
		pr.setCurrentVolume(product.getCurrentVolume());//当前金额
		pr.setMaxSaleVolume(product.getMaxSaleVolume());//最高可售份额
		pr.setPurchaseNum(product.getPurchaseNum());//已投次数
		pr.setPreviousCurVolume(product.getPreviousCurVolume());//上一个交易日产品当前规模(基于占比算)
		pr.setPreviousCurVolumePercent(product.getPreviousCurVolumePercent());//赎回占上一交易日规模百分比
		pr.setIsPreviousCurVolume(product.getIsPreviousCurVolume());//赎回占比开关
		pr.setMaxHold(product.getMaxHold());//单人持有上限
		
		/** 判断是否可在购买的交易区间内  */
		pr.setIsInvest(this.judgeInvestByTime(product));
		
		
		pr.setIncomeDealType(product.getIncomeDealType());
		if(pr.getIncomeDealType().equals(Product.PRODUCT_incomeDealType_cash)){
			pr.setIncomeDealTypeDesc("现金分红");
		}else if(pr.getIncomeDealType().equals(Product.PRODUCT_incomeDealType_reinvest)){
			pr.setIncomeDealTypeDesc("复利收益");
		}
		/** 查看收益日*/
		pr.setCheckInterestDate(DateUtil.addSQLDays(pr.getInterestsFirstDate(), 1));
		
		
		
		/******************* 有阶梯收益start *************/
		BigDecimal avg=BigDecimal.ZERO;
		if (prewards != null && prewards.size() > 0) {//有阶梯收益
			BigDecimal minReward = prewards.get(0).getRatio();
			BigDecimal maxReward = prewards.get(0).getRatio();

			/******************* 昨日年化收益率柱状图start *************/
			BigDecimal yesterdayYieldExp = new BigDecimal("0");
			List<ProductDetailIncomeRewardProfit> rewardYields = new ArrayList<ProductDetailIncomeRewardProfit>();//奖励收益率 单位（%）
			ProductDetailIncomeRewardProfit rewardYield = null;
			
			for (ProductIncomeReward preward : prewards) {
				if (preward.getRatio().compareTo(minReward) < 0) {
					minReward = preward.getRatio();
				}
				if (preward.getRatio().compareTo(maxReward) > 0) {
					maxReward = preward.getRatio();
				}
				//基础收益率为0(未派发收益)
				if(product.getBasicRatio().compareTo(BigDecimal.ZERO)==0){
					if (!StringUtil.isEmpty(expArorStr) || !StringUtil.isEmpty(expArorSecStr)) {
						if (expArorStr.equals(expArorSecStr)) {
							yesterdayYieldExp = product.getExpAror().add(DecimalUtil.zoomIn(product.getRewardInterest(), 100));
						} else {
							if (!StringUtil.isEmpty(expArorStr) && !StringUtil.isEmpty(expArorSecStr)) {
								yesterdayYieldExp = product.getExpAror().add(product.getExpArorSec()).divide(new BigDecimal("2")).add(DecimalUtil.zoomIn(product.getRewardInterest(), 100));
							} else if (!StringUtil.isEmpty(expArorStr)) {
								yesterdayYieldExp = product.getExpAror().add(DecimalUtil.zoomIn(product.getRewardInterest(), 100));
							} else {
								yesterdayYieldExp = product.getExpArorSec().add(DecimalUtil.zoomIn(product.getRewardInterest(), 100));
							}
						}
					}
				}else{
					yesterdayYieldExp=product.getBasicRatio();
				}
				rewardYield = new ProductDetailIncomeRewardProfit();
				rewardYield.setProfit(ProductDecimalFormat.format(ProductDecimalFormat.multiply(yesterdayYieldExp.add(preward.getRatio()))));
				rewardYield.setWithoutLadderProfit(ProductDecimalFormat.format(ProductDecimalFormat.multiply(yesterdayYieldExp)));
				if (preward.getEndDate() != null && preward.getEndDate().intValue() > 0) {
					rewardYield.setStandard(preward.getStartDate() + "天-" + preward.getEndDate() + "天");
					rewardYield.setEndDate(preward.getEndDate().toString());//截止天数
				} else {
					rewardYield.setStandard("大于等于" + preward.getStartDate() + "天");
				}
				rewardYield.setLevel(preward.getLevel());//阶梯名称
				rewardYield.setStartDate(preward.getStartDate().toString());//起始天数	
				rewardYields.add(rewardYield);
			}
			pr.setRewardYields(rewardYields);
			/******************* 昨日年化收益率柱状图end *************/

			/**************** 奖励年化收益率start ****************/
			String minRewardStr = ProductDecimalFormat.format(ProductDecimalFormat.multiply(minReward)) + "%";
			String maxRewardStr = ProductDecimalFormat.format(ProductDecimalFormat.multiply(maxReward)) + "%";
			if (minRewardStr.equals(maxRewardStr)) {
				pr.setRewardYieldRange(minRewardStr);
			} else {
				pr.setRewardYieldRange(minRewardStr + "-" + maxRewardStr);
			}
			avg=maxReward.divide(new BigDecimal("2")); 
			String rewardTenThsProfitFst = ProductDecimalFormat.format(minReward.multiply(new BigDecimal(10000)).divide(new BigDecimal(incomeCalcBasis), 4, RoundingMode.HALF_UP), "0.0000");
			String rewardTenThsProfitSec = ProductDecimalFormat.format(maxReward.multiply(new BigDecimal(10000)).divide(new BigDecimal(incomeCalcBasis), 4, RoundingMode.HALF_UP), "0.0000");
			if (rewardTenThsProfitFst.equals(rewardTenThsProfitSec)) {
				pr.setRewardTenThsProfit(rewardTenThsProfitFst);
			} else {
				//阶梯收益的奖励区间
				pr.setRewardTenThsProfit(rewardTenThsProfitFst + "-" + rewardTenThsProfitSec);
			}
			/**************** 奖励年化收益率end ****************/
			pr.setHasLadderProfit(Product.YES);
			pr.setLadderDesc("阶梯收益");
		}else{//无阶梯收益
			pr.setHasLadderProfit(Product.NO);
		}
		/******************* 有阶梯收益end *************/

		/**************** 基准年化收益率走势图start ****************/
		CurrentProductDetailProfit[] annualYields = null;//基准年化收益率走势  单位（%）
		CurrentProductDetailProfit[] perMillionIncomes = null;//基准万份收益走势 单位（元）
		CurrentProductDetailProfit annualYield = null;
		CurrentProductDetailProfit perMillionIncome = null;

		if ((product.getExpAror() != null && product.getExpAror().compareTo(new BigDecimal("0")) > 0) || (product.getExpArorSec() != null && product.getExpArorSec().compareTo(new BigDecimal("0")) > 0)) {
			//确保有30条
			annualYields = new CurrentProductDetailProfit[30];//基准年化收益率走势  单位（%）
			perMillionIncomes = new CurrentProductDetailProfit[30];//基准万份收益走势 单位（元）

			//基础收益率为0(未派发收益)
			if(product.getBasicRatio().compareTo(BigDecimal.ZERO)==0){
				//用产品预期年化收率补齐前面的数据start
				for (int i = annualYields.length - 1; i >= 0; i--) {
					annualYield = new CurrentProductDetailProfit();
					if (expArorStr.equals(expArorSecStr)) {
						BigDecimal profit1=product.getExpAror().add(DecimalUtil.zoomIn(product.getRewardInterest(), 100));
						annualYield.setProfit(ProductDecimalFormat.format(ProductDecimalFormat.multiply(profit1)));
					} else {
						BigDecimal profit2=product.getExpAror().add(product.getExpArorSec()).divide(new BigDecimal("2")).add(DecimalUtil.zoomIn(product.getRewardInterest(), 100));
						annualYield.setProfit(ProductDecimalFormat.format(ProductDecimalFormat.multiply(profit2)));
					}
					annualYield.setStandard(DateUtil.format(DateUtil.addDay(new Date(), i - annualYields.length)));
					annualYields[i] = annualYield;

					perMillionIncome = new CurrentProductDetailProfit();
					perMillionIncome.setProfit(getTenThousandIncome2Start(product, incomeCalcBasis));
					perMillionIncome.setStandard(DateUtil.format(DateUtil.addDay(new Date(), i - annualYields.length)));
					perMillionIncomes[i] = perMillionIncome;
				}
				//用产品预期年化收率补齐前面的数据end
			}
		} else {
				annualYields = new CurrentProductDetailProfit[new Long(pcas.getTotalElements()).intValue()];//基准年化收益率走势  单位（%）
				perMillionIncomes = new CurrentProductDetailProfit[new Long(pcas.getTotalElements()).intValue()];//基准万份收益走势 单位（元）
		}
		

		List<BigDecimal> ratios = new ArrayList<BigDecimal>();
		//用最新的实际收益分配 替换最后面的数据 start
		if (pcas != null && pcas.getContent() != null && pcas.getTotalElements() > 0) {
			annualYields = new CurrentProductDetailProfit[30];//基准年化收益率走势  单位（%）
			perMillionIncomes = new CurrentProductDetailProfit[30];//基准万份收益走势 单位（元）
			
			//用产实际年化收率补齐前面的数据end
			int j = annualYields.length - 1;
			int k=annualYields.length - pcas.getContent().size()-1;
			for (IncomeAllocate ia : pcas) {
				if (ratios.size() <= 7) {
					ratios.add(ia.getRatio());
				}
				annualYield = new CurrentProductDetailProfit();
				annualYield.setProfit(ProductDecimalFormat.format(ProductDecimalFormat.multiply(ia.getRatio())));
				annualYield.setStandard(DateUtil.format(ia.getBaseDate()));
				annualYields[j] = annualYield;

				perMillionIncome = new CurrentProductDetailProfit();
				perMillionIncome.setProfit(ProductDecimalFormat.format(ia.getWincome(), "0.0000"));
				perMillionIncome.setStandard(DateUtil.format(ia.getBaseDate()));
				perMillionIncomes[j] = perMillionIncome;
				j--;
				if(j < 0){
					break;
				}
			}
			
			for(int i=k; i >= 0; i--){
				annualYield = new CurrentProductDetailProfit();
				if (expArorStr.equals(expArorSecStr)) {
					BigDecimal profit1=product.getExpAror().add(DecimalUtil.zoomIn(product.getRewardInterest(), 100));
					annualYield.setProfit(ProductDecimalFormat.format(ProductDecimalFormat.multiply(profit1)));
				} else {
					BigDecimal profit2=product.getExpAror().add(product.getExpArorSec()).divide(new BigDecimal("2")).add(DecimalUtil.zoomIn(product.getRewardInterest(), 100));
					annualYield.setProfit(ProductDecimalFormat.format(ProductDecimalFormat.multiply(profit2)));
				}
//				annualYield.setStandard(DateUtil.format(DateUtil.addDay(new Date(), k - annualYields.length)));
				annualYield.setStandard(DateUtil.format(DateUtil.addDay(DateUtil.parseToSqlDate(annualYields[i+1].getStandard()), -1)));
				
				annualYields[i] = annualYield;
				perMillionIncome = new CurrentProductDetailProfit();
				perMillionIncome.setProfit(getTenThousandIncome2Start(product, incomeCalcBasis));
				perMillionIncome.setStandard(DateUtil.format(DateUtil.addDay(DateUtil.parseToSqlDate(annualYields[i+1].getStandard()), -1)));
				perMillionIncomes[i] = perMillionIncome;
			}
			pr.setAnnualYields(Arrays.asList(annualYields));
			pr.setPerMillionIncomes(Arrays.asList(perMillionIncomes));
		}else{
			//没有发放收益
			//确保有10条
			annualYields = new CurrentProductDetailProfit[30];//基准年化收益率走势  单位（%）
			perMillionIncomes = new CurrentProductDetailProfit[30];//基准万份收益走势 单位（元）
			    
			for (int i = annualYields.length - 1; i >= 0; i--) {
				annualYield = new CurrentProductDetailProfit();
				if (expArorStr.equals(expArorSecStr)) {
					BigDecimal profit1=product.getExpAror().add(DecimalUtil.zoomIn(product.getRewardInterest(), 100));
					annualYield.setProfit(ProductDecimalFormat.format(ProductDecimalFormat.multiply(profit1)));
				} else {
					BigDecimal profit2=product.getExpAror().add(product.getExpArorSec()).divide(new BigDecimal("2")).add(DecimalUtil.zoomIn(product.getRewardInterest(), 100));
					annualYield.setProfit(ProductDecimalFormat.format(ProductDecimalFormat.multiply(profit2)));
				}
				annualYield.setStandard(DateUtil.format(DateUtil.addDay(new Date(), i - annualYields.length)));
				annualYields[i] = annualYield;

				perMillionIncome = new CurrentProductDetailProfit();
				perMillionIncome.setProfit(getTenThousandIncome2Start(product, incomeCalcBasis));
				perMillionIncome.setStandard(DateUtil.format(DateUtil.addDay(new Date(), i - annualYields.length)));
				perMillionIncomes[i] = perMillionIncome;
			}
		}
		
		
		//用最新的实际收益分配 替换最后面的数据 end
		pr.setAnnualYields(Arrays.asList(annualYields));
		pr.setPerMillionIncomes(Arrays.asList(perMillionIncomes));
		/******************* 基准年化收益率走势图 end *************/

		if (!StringUtil.isEmpty(expArorStr) || !StringUtil.isEmpty(expArorSecStr)) {//有预期年化收益率
			/**************** 预期年化收益率start ****************/

			if (expArorStr.equals(expArorSecStr)) {
				pr.setAnnualInterestSec(expArorStr);
				pr.setTenThsPerDayProfit(getTenThousandIncome(product, incomeCalcBasis));
				
			} else {
				if (!StringUtil.isEmpty(expArorStr) && !StringUtil.isEmpty(expArorSecStr)) {
					pr.setAnnualInterestSec(expArorStr + "-" + expArorSecStr);
					pr.setTenThsPerDayProfit(getTenThousandIncome(product, incomeCalcBasis) + "-" + getTenThousandIncome2End(product, incomeCalcBasis));
				} else if (!StringUtil.isEmpty(expArorStr)) {
					pr.setAnnualInterestSec(expArorStr);
					pr.setTenThsPerDayProfit(getTenThousandIncome(product, incomeCalcBasis));
				} else {
					pr.setAnnualInterestSec(expArorSecStr);
					pr.setTenThsPerDayProfit(getTenThousandIncome(product, incomeCalcBasis));
				}
			}
			/**************** 预期年化收益率end ****************/

			if (expArorStr.equals(expArorSecStr)) {//固定预期收益率
				if (prewards != null && prewards.size() > 0) {//有奖励收益
					pr.setShowType(ProductCurrentResp.SHOW_TYPE_5);
				} else {
					pr.setShowType(ProductCurrentResp.SHOW_TYPE_2);
				}
			} else {
				if (prewards != null && prewards.size() > 0) {//有奖励收益
					pr.setShowType(ProductCurrentResp.SHOW_TYPE_4);
				} else {
					pr.setShowType(ProductCurrentResp.SHOW_TYPE_1);
				}
			}
		} else {
			if (prewards != null && prewards.size() > 0) {//有奖励收益
				if (pcas != null && pcas.getContent() != null && pcas.getTotalElements() > 0) {
					pr.setShowType(ProductCurrentResp.SHOW_TYPE_6);

					BigDecimal yesterdayYieldExp = pcas.getContent().get(0).getRatio();//昨日年化收益率
					pr.setYesterdayYield(ProductDecimalFormat.format(ProductDecimalFormat.multiply(yesterdayYieldExp)) + "%");//昨日年化收益率
					pr.setTenThsPerDayProfit(ProductDecimalFormat.format(yesterdayYieldExp.multiply(new BigDecimal(10000)).divide(new BigDecimal(incomeCalcBasis), 4, RoundingMode.HALF_UP), "0.0000"));
				} else {
					pr.setShowType(ProductCurrentResp.SHOW_TYPE_7);
				}
			} else {
				if (pcas != null && pcas.getContent() != null && pcas.getTotalElements() > 0) {
					pr.setShowType(ProductCurrentResp.SHOW_TYPE_3);

					BigDecimal sevenDayYieldRatio = new BigDecimal("0");

					for (BigDecimal ratio : ratios) {
						sevenDayYieldRatio = sevenDayYieldRatio.add(ratio);
					}
					BigDecimal sevenDayYieldExp = sevenDayYieldRatio.multiply(new BigDecimal("100")).divide(new BigDecimal("" + ratios.size()), 4, RoundingMode.HALF_UP);//七日年化收益率单位（%）
					pr.setSevenDayYield(ProductDecimalFormat.format(sevenDayYieldExp, "0.00") + "%");//七日年化收益率:最新7条取平均值
					pr.setTenThsPerDayProfit(ProductDecimalFormat.format(sevenDayYieldExp.divide(new BigDecimal("100")).multiply(new BigDecimal("10000")).divide(new BigDecimal(incomeCalcBasis), 4, RoundingMode.HALF_UP), "0.0000"));
				}
			}
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

		List<ProductLabel> productLabels = productLabelService.findProductLabelsByProduct(product);
		if (productLabels != null && productLabels.size() > 0) {
			List<LabelResp> labelResps = new ArrayList<LabelResp>();
			for (ProductLabel pl : productLabels) {
				labelResps.add(new LabelResp(pl.getLabel()));
			}
			pr.setProductLabels(labelResps);
		}
		return pr;
	}

	public String getTenThousandIncome(Product product, String incomeCalcBasis) {
		
		if (null != product.getRewardInterest()) {
			return InterestFormula.compound(new BigDecimal(10000),
					product.getExpAror().add(DecimalUtil.zoomIn(product.getRewardInterest(), 100)), product.getIncomeCalcBasis()).toString();
		} else {
			return InterestFormula.compound(new BigDecimal(10000), product.getExpAror(), product.getIncomeCalcBasis()).toString();
		}
		

	}
	
	public String getTenThousandIncome2Start(Product product, String incomeCalcBasis) {
		if (null != product.getRewardInterest()) {
			return InterestFormula.compound(new BigDecimal(10000),
					((product.getExpAror().add(product.getExpArorSec()).divide(new BigDecimal("2.0")))).add(DecimalUtil.zoomIn(product.getRewardInterest(), 100)), product.getIncomeCalcBasis()).toString();
		} else {
			return InterestFormula.compound(new BigDecimal(10000), ((product.getExpAror().add(product.getExpArorSec()).divide(new BigDecimal("2.0")))), product.getIncomeCalcBasis()).toString();
		}
	}
	
	private String getTenThousandIncome2End(Product product, String incomeCalcBasis) {
		
		if (null != product.getRewardInterest()) {
			return InterestFormula.compound(new BigDecimal(10000),
					product.getExpArorSec().add(DecimalUtil.zoomIn(product.getRewardInterest(), 100)), product.getIncomeCalcBasis()).toString();
		} else {
			return InterestFormula.compound(new BigDecimal(10000), product.getExpArorSec(), product.getIncomeCalcBasis()).toString();
		}
		

	}
	

	/**
	 * 定期产品详情
	 * 
	 * @param oid
	 * @return
	 */

	public ProductPeriodicDetailResp periodicDdetail(String oid, String uid) {
		Product product = productService.getProductByOid(oid);

		List<ProductIncomeReward> prewards = rewardService.productRewardList(oid);
		if (prewards != null && prewards.size() > 0) {//算上奖励收益
			BigDecimal minReward = prewards.get(0).getRatio();
			BigDecimal maxReward = prewards.get(0).getRatio();
			for (ProductIncomeReward preward : prewards) {
				if (preward.getRatio().compareTo(minReward) < 0) {
					minReward = preward.getRatio();
				}
				if (preward.getRatio().compareTo(maxReward) > 0) {
					maxReward = preward.getRatio();
				}
			}
		}
		ProductPeriodicDetailResp pr = new ProductPeriodicDetailResp(product, prewards);
		
		pr.setIncomeDealType(product.getIncomeDealType());
		if(pr.getIncomeDealType().equals(Product.PRODUCT_incomeDealType_cash)){
			pr.setIncomeDealTypeDesc("现金红包");
		}else if(pr.getIncomeDealType().equals(Product.PRODUCT_incomeDealType_reinvest)){
			pr.setIncomeDealTypeDesc("复利收益");
		}
		
		if (Product.STATE_Raising.equals(product.getState())) {
			// 投资时间
			pr.setInvestTime(DateUtil.getSqlCurrentDate());
		} else {
			if (uid != null) {
				HoldCacheEntity hold = cacheHoldService.getHoldCacheEntityByUidAndProductId(uid, oid);
				if (null == hold) {
					throw new AMPException("未投资用户不可查看详情");
				}
				pr.setInvestTime(hold.getLatestOrderTime());
			}
			
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
		List<ProductLabel> productLabels = productLabelService.findProductLabelsByProduct(product);
		if (productLabels != null && productLabels.size() > 0) {
			List<LabelResp> labelResps = new ArrayList<LabelResp>();
			for (ProductLabel pl : productLabels) {
				labelResps.add(new LabelResp(pl.getLabel()));
			}
			pr.setProductLabels(labelResps);
		}
		
		/** 判断当前的时间是否在交易区间内  */
		pr.setIsInvest(this.judgeInvestByTime(product));
		return pr;
	}

	/**
	 * app查询可以申购的定期活期产品推荐列表
	 * 
	 * @param spec
	 * @param pageable
	 * @return {@link PageResp<ProductListResp>}
	 *         ,如果返回的errCode属性等于0表示成功，否则表示失败，失败原因在errMessage里面体现
	 */
	public PageResp<ProductListResp> labelProducts(Specification<ProductChannel> spec, Sort sort) {
		List<ProductChannel> pcs = this.productChannelDao.findAll(spec, sort);
		PageResp<ProductListResp> pagesRep = new PageResp<ProductListResp>();
		if (pcs != null && pcs.size() > 0) {
			List<String> productOids = new ArrayList<String>();
			for (ProductChannel p : pcs) {
				productOids.add(p.getProduct().getOid());
			}

			Map<String, Map<String, BigDecimal>> productExpArorMap = this.getProductsMinMaxRewards(productOids);
			List<ProductListResp> rows = new ArrayList<ProductListResp>();
			Page<IncomeAllocate> pcas = null;
			for (ProductChannel p : pcs) {
				if (Product.TYPE_Producttype_02.equals(p.getProduct().getType().getOid())) {
					pcas = getProductIncomeAllocate(p.getProduct().getPortfolio().getOid(), 1);
				} else {
					pcas = null;
				}
				ProductListResp queryRep = new ProductListResp(p, productExpArorMap, pcas);
				rows.add(queryRep);
			}
			pagesRep.setRows(rows);
			pagesRep.setTotal(pcs.size());
		}
		return pagesRep;
	}

	public PageResp<ProductListResp> labelProducts(String channeOid, String labelCode) {
		System.out.println(DateUtil.getSqlDate());
		List<Object[]> pcs = this.productDao.queryLabelProducts(channeOid, labelCode, DateUtil.getSqlDate());

		PageResp<ProductListResp> pagesRep = new PageResp<ProductListResp>();
		if (pcs != null && pcs.size() > 0) {

			List<String> productOids = new ArrayList<String>();
			for (Object[] p : pcs) {
				productOids.add((String) p[0]);
			}
			Map<String, List<LabelResp>> labelMap = this.findProductLabels(productOids);

			Map<String, Map<String, BigDecimal>> productExpArorMap = this.getProductsMinMaxRewards(productOids);
			List<ProductListResp> rows = new ArrayList<ProductListResp>();
			Page<IncomeAllocate> pcas = null;
			Product p = null;
			for (Object[] o : pcs) {
				p = this.productDao.findOne((String) o[0]);
				if (Product.TYPE_Producttype_02.equals((String) o[2])) {
					pcas = getProductIncomeAllocate(p.getPortfolio().getOid(), 1);
				} else {
					pcas = null;
				}
				ProductListResp queryRep = new ProductListResp(o, productExpArorMap, pcas);
				queryRep.setProductLabels(labelMap.get(p.getOid()));
				rows.add(queryRep);
			}
			pagesRep.setRows(rows);
			pagesRep.setTotal(pcs.size());
		}
		return pagesRep;
	}

	private Map<String, List<LabelResp>> findProductLabels(final List<String> productOids) {
		//查询标签
		Specification<ProductLabel> spec = new Specification<ProductLabel>() {
			@Override
			public Predicate toPredicate(Root<ProductLabel> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				In<String> inProductOids = cb.in(root.get("product").get("oid").as(String.class));
				for (String productOid : productOids) {
					inProductOids.value(productOid);
				}
				return inProductOids;
			}
		};
		spec = Specifications.where(spec);
		List<ProductLabel> ls = productLabelDao.findAll(spec);
		Map<String, List<LabelResp>> labelMap = new HashMap<String, List<LabelResp>>();
		if (ls != null && ls.size() > 0) {
			for (ProductLabel l : ls) {
				if (labelMap.get(l.getProduct().getOid()) == null) {
					labelMap.put(l.getProduct().getOid(), new ArrayList<LabelResp>());
				}
				labelMap.get(l.getProduct().getOid()).add(new LabelResp(l.getLabel()));
			}
		}
		return labelMap;
	}
	/**    家加财修改------显示所有已经非售罄的定期产品       */
	public PageResp<ProductPojo> getTnProducts(int page, int rows, BigDecimal expArorStart, BigDecimal expArorEnd, 
			Integer durationPeriodDaysStart, Integer durationPeriodDaysEnd, String channelOid, String sort,
			String order, String uid) {
		PageResp<ProductPojo> pagesRep = new PageResp<ProductPojo>();
		if (null == expArorStart) {
			expArorStart = BigDecimal.ZERO;
		}
		if (null == expArorEnd) {
			expArorEnd = new BigDecimal(10000);
		}
		if (null == durationPeriodDaysStart) {
			durationPeriodDaysStart = 0;
		}
		if (null == durationPeriodDaysEnd) {
			durationPeriodDaysEnd = 10000;
		}
		StringBuilder sb = new StringBuilder();
		StringBuilder count = new StringBuilder();
		StringBuilder where = new StringBuilder();
		StringBuilder sborder = new StringBuilder();
		StringBuilder pages = new StringBuilder();

		sb.append("SELECT t1.oid, t1.name, t1.investMin, t1.expAror, t1.expArorSec, t1.rewardInterest,");
		sb.append(" t1.durationPeriodDays, t1.raisedTotalNumber, t1.collectedVolume, t1.lockCollectedVolume, ");
		sb.append(" CASE  WHEN t1.state = 'RAISING' THEN 1 ELSE 2 END AS stateOrder, ");
		sb.append(" t1.state, t1.type, ");
		sb.append(
				" CASE WHEN t1.raisedTotalNumber - t1.collectedVolume - t1.lockCollectedVolume = 0 THEN 1 ELSE 0 END AS investableVolume, t1.purchaseNum, t1.maxSaleVolume,t1.dealStartTime,t1.dealEndTime ,t1.weightValue");
		sb.append(
				" FROM T_GAM_PRODUCT t1, T_GAM_PRODUCT_CHANNEL t2, T_MONEY_PLATFORM_CHANNEL t3 ,T_MONEY_PLATFORM_LABEL t4, T_MONEY_PLATFORM_LABEL_PRODUCT t5");
		sb.append(
				" WHERE t1.oid = t2.productOid AND t2.channelOid = t3.oid AND t1.type = 'PRODUCTTYPE_01' AND t2.marketState = 'ONSHELF' AND t1.state in ('RAISING', 'RAISEEND', 'DURATIONING', 'DURATIONEND') AND t1.oid=t5.productOid AND t5.labelOid = t4.oid AND t4.isOK = 'yes'");

		if (!StringUtil.isEmpty(uid) && (!StringUtil.isEmpty(this.riskLevelDao.selectRiskLevel(uid)))) {
			String riskString = this.getBuyProductRiskLevel(uid);
			where.append(" AND t1.riskLevel in").append(riskString);
		} else {
			where.append(" AND t1.riskLevel = 'R1'");

		}

		where.append(" AND t4.labelCode <> '11'");
		where.append(" AND t1.expAror >= ").append(expArorStart);
		where.append(" AND t1.expAror <= ").append(expArorEnd);
		where.append(" AND t1.durationPeriodDays >= ").append(durationPeriodDaysStart);
		where.append(" AND t1.durationPeriodDays <= ").append(durationPeriodDaysEnd);
		where.append(" AND t3.oid = '").append(channelOid).append("' ");

		if (!StringUtil.isEmpty(sort) && !StringUtil.isEmpty(order)) {
			if ("expAror".equalsIgnoreCase(sort)) {
				sborder.append(" order by  (expAror*100) ").append(order)
						.append(",");
				sborder.append(" t1.weightValue,t2.rackTime desc ");
			} else {
				sborder.append(" order by t1. ").append(sort).append(" ").append(order)
						.append(",");
				sborder.append(" t1.weightValue asc , (expAror*100) desc, t2.rackTime desc ");
			}
		} else {
			// sborder.append(" order by investableVolume, stateOrder,
			// t2.rackTime desc ");
			sborder.append(
					" order by t1.weightValue,(expAror*100) desc, t2.rackTime desc ");
		}
		// page.append(" limit ").append(start).append(",").append(end);

		sb.append(where.toString()).append(sborder.toString()).append(pages.toString());
		@SuppressWarnings("unchecked")
		List<Object[]> list = em.createNativeQuery(sb.toString()).getResultList();
		int i = 0;
		for (Object[] arr : list) {
			// arr表示每一个对象
			int a = ((BigDecimal) arr[15]).compareTo((BigDecimal) arr[9]);
			// System.out.println(a);
			int b = ((BigDecimal) arr[15]).subtract((BigDecimal) arr[9]).divide(new BigDecimal(1))
					.subtract((BigDecimal) arr[2]).compareTo(new BigDecimal(0));
			// 筛选满足条件下定期产品
			if (((String) arr[11]).equals(Product.STATE_Raising) && (a != 0) && (b >= 0)) {
				i++;
				ProductPojo pojo = new ProductPojo();
				pojo.setProductOid((String) arr[0]);
				pojo.setName((String) arr[1]);
				pojo.setInvestMin((BigDecimal) arr[2]);
				pojo.setExpAror((BigDecimal) arr[3]);
				pojo.setExpArorSec((BigDecimal) arr[4]);
				pojo.setRewardInterest((BigDecimal) arr[5]);
				pojo.setDurationPeriodDays(((Integer) arr[6]));
				pojo.setRaisedTotalNumber((BigDecimal) arr[7]);
				pojo.setCollectedVolume((BigDecimal) arr[8]);
				pojo.setLockCollectedVolume((BigDecimal) arr[9]);
				pojo.setStateOrder((arr[10]).toString());
				pojo.setState((String) arr[11]);
				pojo.setType((String) arr[12]);
				pojo.setPurchaseNum((Integer) arr[14]);
				pojo.setMaxSaleVolume((BigDecimal) arr[15]);
				pojo.setDealStartTime((String)arr[16]);
				pojo.setDealEndTime((String)arr[17]);
				pojo.setWeightValue((Integer)arr[18]);
				setExpArrorDisp(pojo);
				List<LabelRep> labelList = this.productLabelService.getLabelRepByProduct(pojo.getProductOid());
				pojo.setLabelList(labelList);
				/**判断当前时间是否在可购买时间内*/
				Product product = this.productDao.findByOid(pojo.getProductOid());
				pojo.setIsInvest(this.judgeInvestByTime(product));
				pagesRep.getRows().add(pojo);
			}
		}

		pagesRep.setTotal(i);/**对list集合进行分页*/
		
		List<ProductPojo> newList = new ArrayList<ProductPojo>();
		 int currIdx = (page > 1 ? (page -1) * rows : 0);
	        for (int j = 0; j < rows && j < pagesRep.getRows().size() - currIdx; j++) {
	        	ProductPojo productPojo  = pagesRep.getRows().get(currIdx + j);
	            newList.add(productPojo);
	        }

		pagesRep.setRows(newList);

		return pagesRep;
	}
	
	
	/**  获得定期产品列表   */
	/*public PageResp<ProductPojo> getTnProducts(int start, int end, BigDecimal expArorStart, BigDecimal expArorEnd, 
			Integer durationPeriodDaysStart, Integer durationPeriodDaysEnd, String channelOid, String sort, String order) {
		PageResp<ProductPojo> pagesRep = new PageResp<ProductPojo>();
		if (null == expArorStart) {
			expArorStart = BigDecimal.ZERO;
		}
		if (null == expArorEnd) {
			expArorEnd = new BigDecimal(10000);
		}
		if (null == durationPeriodDaysStart) {
			durationPeriodDaysStart = 0;
		}
		if (null == durationPeriodDaysEnd) {
			durationPeriodDaysEnd = 10000;
		}
		StringBuilder sb = new StringBuilder();
		StringBuilder count = new StringBuilder();
		StringBuilder where = new StringBuilder();
		StringBuilder sborder = new StringBuilder();
		StringBuilder page = new StringBuilder();
		count.append("SELECT count(*) FROM T_GAM_PRODUCT t1, T_GAM_PRODUCT_CHANNEL t2, T_MONEY_PLATFORM_CHANNEL t3 ");
		count.append(" WHERE t1.oid = t2.productOid AND t2.channelOid = t3.oid  AND t1.type = 'PRODUCTTYPE_01' AND t2.marketState = 'ONSHELF' AND t1.state in ('RAISING', 'RAISEEND', 'DURATIONING', 'DURATIONEND') ");
		
		sb.append("SELECT t1.oid, t1.name, t1.investMin, t1.expAror, t1.expArorSec, t1.rewardInterest,");
		sb.append(" t1.durationPeriodDays, t1.raisedTotalNumber, t1.collectedVolume, t1.lockCollectedVolume, ");
		sb.append(" CASE  WHEN t1.state = 'RAISING' THEN 1 ELSE 2 END AS stateOrder, ");
		sb.append(" t1.state, t1.type, ");
		sb.append(" CASE WHEN t1.raisedTotalNumber - t1.collectedVolume - t1.lockCollectedVolume = 0 THEN 1 ELSE 0 END AS investableVolume, t1.purchaseNum, t1.maxSaleVolume ");
		sb.append(" FROM T_GAM_PRODUCT t1, T_GAM_PRODUCT_CHANNEL t2, T_MONEY_PLATFORM_CHANNEL t3 ");
		sb.append(" WHERE t1.oid = t2.productOid AND t2.channelOid = t3.oid AND t1.type = 'PRODUCTTYPE_01' AND t2.marketState = 'ONSHELF' AND t1.state in ('RAISING', 'RAISEEND', 'DURATIONING', 'DURATIONEND') ");
		
		where.append(" AND (t1.expAror + t1.rewardInterest) >= ").append(expArorStart);
		where.append(" AND (t1.expArorSec + t1.rewardInterest) <= ").append(expArorEnd);
		where.append(" AND t1.durationPeriodDays >= ").append(durationPeriodDaysStart);
		where.append(" AND t1.durationPeriodDays <= ").append(durationPeriodDaysEnd);
		where.append(" AND t3.oid = '").append(channelOid).append("' ");
		if (!StringUtil.isEmpty(sort) && !StringUtil.isEmpty(order)) {
			if("expAror".equalsIgnoreCase(sort)){
				sborder.append(" order by investableVolume,stateOrder, (expArorSec*100+rewardInterest) ").append(order).append(",");
				sborder.append("  t2.rackTime desc ");
			}else{
				sborder.append(" order by investableVolume,stateOrder, t1. ").append(sort).append(" ").append(order).append(",");
				sborder.append("  (expArorSec*100+rewardInterest) desc, t2.rackTime desc ");
			}
		} else {
//			sborder.append(" order by investableVolume, stateOrder, t2.rackTime desc ");
			sborder.append(" order by investableVolume,stateOrder,(expArorSec*100+rewardInterest) desc, t2.rackTime desc ");
		}
		page.append(" limit ").append(start).append(",").append(end);
		
		count.append(where.toString());
		
		Integer total = ((BigInteger)em.createNativeQuery(count.toString()).getSingleResult()).intValue();
		
		sb.append(where.toString()).append(sborder.toString()).append(page.toString());
		@SuppressWarnings("unchecked")
		List<Object[]> list = em.createNativeQuery(sb.toString()).getResultList();
		for (Object[] arr : list) {
			ProductPojo pojo = new ProductPojo();
			pojo.setProductOid((String)arr[0]);
			pojo.setName((String)arr[1]);
			pojo.setInvestMin((BigDecimal)arr[2]);
			pojo.setExpAror((BigDecimal)arr[3]);
			pojo.setExpArorSec((BigDecimal)arr[4]);
			pojo.setRewardInterest((BigDecimal)arr[5]);
			pojo.setDurationPeriodDays(((Integer)arr[6]));
			pojo.setRaisedTotalNumber((BigDecimal)arr[7]);
			pojo.setCollectedVolume((BigDecimal)arr[8]);
			pojo.setLockCollectedVolume((BigDecimal)arr[9]);
			pojo.setStateOrder((arr[10]).toString());
			pojo.setState((String)arr[11]);
			pojo.setType((String)arr[12]);
			pojo.setPurchaseNum((Integer)arr[14]);
			pojo.setMaxSaleVolume((BigDecimal)arr[15]);
			setExpArrorDisp(pojo);
			List<LabelRep> labelList = this.productLabelService.getLabelRepByProduct(pojo.getProductOid());
			pojo.setLabelList(labelList);
			pagesRep.getRows().add(pojo);
		}
		pagesRep.setTotal(total);
		return pagesRep;
	}
	*/
	
	public RowsRep<ProductPojo> getPCHomeProducts(String channelOid) {
		RowsRep<ProductPojo> rowsRep = new RowsRep<ProductPojo>();
		StringBuilder sb = new StringBuilder();
		StringBuilder where = new StringBuilder();
		sb.append("SELECT t1.oid, t1.name, t1.investMin, t1.expAror, t1.expArorSec, t1.rewardInterest,");
		sb.append(" t1.durationPeriodDays, t1.raisedTotalNumber, t1.collectedVolume, t1.lockCollectedVolume, ");
		sb.append(" CASE  WHEN t1.state = 'RAISING' THEN 1 WHEN t1.state = 'RAISEEND' THEN 2 ");
		sb.append(" WHEN t1.state = 'DURATIONING' THEN 3 WHEN t1.state = 'DURATIONEND' THEN 4 WHEN t1.state = 'CLEARED' THEN 5 ELSE 6 END AS stateOrder, ");
		sb.append(" t1.state, t1.type, ");
		sb.append(" CASE WHEN t1.raisedTotalNumber - t1.collectedVolume - t1.lockCollectedVolume = 0 THEN 1 ELSE 0 END AS investableVolume, t1.purchaseNum ");
		
		sb.append(" FROM T_GAM_PRODUCT t1, T_GAM_PRODUCT_CHANNEL t2, T_MONEY_PLATFORM_CHANNEL t3 ");
		sb.append(" WHERE t1.oid = t2.productOid AND t2.channelOid = t3.oid AND t1.type = 'PRODUCTTYPE_01' AND t2.marketState = 'ONSHELF' ");
		sb.append(" AND t1.state in ('RAISING', 'RAISEEND', 'DURATIONING', 'DURATIONEND') ");
		sb.append(" AND t3.oid = '").append(channelOid).append("' ");
		where.append(" AND (t1.expAror + t1.rewardInterest) >= 0 ");
		where.append(" AND (t1.expArorSec + t1.rewardInterest) <= 1000 ");
		where.append(" AND t1.durationPeriodDays >= 0 ");
		where.append(" AND t1.durationPeriodDays <= 10000");
		where.append(" order by investableVolume, stateOrder, t2.rackTime desc ");
		where.append(" limit 6");
		
		
		
//		Product t0Product = this.productDao.getT0Product(channelOid);
		List<Product> t0Product = this.productDao.getT0ProductByChannelOidExceptTyj4APPAndPC(channelOid);
		if (t0Product != null && !t0Product.isEmpty()){
			for (Product t0pro : t0Product){
				addProductPojo(rowsRep, t0pro);
			}
		}
		
		Product newBie = this.productDao.getNewBieProduct(channelOid);
		addProductPojo(rowsRep, newBie);
		
		sb.append(where.toString());
		@SuppressWarnings("unchecked")
		List<Object[]> list = em.createNativeQuery(sb.toString()).getResultList();
		int tnCount = 1;
		for (Object[] arr : list) {
			if (tnCount > 2) {
				break;
			}
			ProductPojo pojo = new ProductPojo();
			pojo.setProductOid((String)arr[0]);
			if (null != newBie && pojo.getProductOid().equals(newBie.getOid())) {
				continue;
			}
			pojo.setName((String)arr[1]);
			pojo.setInvestMin((BigDecimal)arr[2]);
			pojo.setExpAror((BigDecimal)arr[3]);
			pojo.setExpArorSec((BigDecimal)arr[4]);
			pojo.setRewardInterest((BigDecimal)arr[5]);
			pojo.setDurationPeriodDays(((Integer)arr[6]));
			pojo.setRaisedTotalNumber((BigDecimal)arr[7]);
			pojo.setCollectedVolume((BigDecimal)arr[8]);
			pojo.setLockCollectedVolume((BigDecimal)arr[9]);
			pojo.setStateOrder((arr[10]).toString());
			pojo.setState((String)arr[11]);
			pojo.setType((String)arr[12]);
			pojo.setPurchaseNum((Integer)arr[14]);
			setExpArrorDisp(pojo);
			List<LabelRep> labelList = this.productLabelService.getLabelRepByProduct(pojo.getProductOid());
			pojo.setLabelList(labelList);
			rowsRep.add(pojo);
			tnCount++;
		}
		
		return rowsRep;
	}
	

	public RowsRep<ProductPojo> getAppHomeProducts(String channelOid) {
		RowsRep<ProductPojo> rowsRep = new RowsRep<ProductPojo>();
		StringBuilder sb = new StringBuilder();
		StringBuilder where = new StringBuilder();
		sb.append("SELECT t1.oid, t1.name, t1.investMin, t1.expAror, t1.expArorSec, t1.rewardInterest,");
		sb.append(" t1.durationPeriodDays, t1.raisedTotalNumber, t1.collectedVolume, t1.lockCollectedVolume, ");
		sb.append(" CASE  WHEN t1.state = 'RAISING' THEN 1 WHEN t1.state = 'RAISEEND' THEN 2 ");
		sb.append(" WHEN t1.state = 'DURATIONING' THEN 3 WHEN t1.state = 'DURATIONEND' THEN 4 WHEN t1.state = 'CLEARED' THEN 5 ELSE 6 END AS stateOrder, ");
		sb.append(" t1.state, t1.type, ");
		sb.append(" CASE WHEN t1.raisedTotalNumber - t1.collectedVolume - t1.lockCollectedVolume = 0 THEN 1 ELSE 0 END AS investableVolume, t1.purchaseNum,t1.incomeCalcBasis ");
		sb.append(" FROM T_GAM_PRODUCT t1, T_GAM_PRODUCT_CHANNEL t2, T_MONEY_PLATFORM_CHANNEL t3 ");
		sb.append(" WHERE t1.oid = t2.productOid AND t2.channelOid = t3.oid AND t1.type = 'PRODUCTTYPE_01' AND t2.marketState = 'ONSHELF' AND t1.state in ('RAISING', 'RAISEEND', 'DURATIONING', 'DURATIONEND') ");
		sb.append(" AND t3.oid = '").append(channelOid).append("' ");
		where.append(" AND (t1.expAror + t1.rewardInterest) >= 0 ");
		where.append(" AND (t1.expArorSec + t1.rewardInterest) <= 1000 ");
		where.append(" AND t1.durationPeriodDays >= 0 ");
		where.append(" AND t1.durationPeriodDays <= 10000");
		where.append(" order by investableVolume, stateOrder, t2.rackTime desc ");
		where.append(" limit 6");
		
		Product newBie = this.productDao.getNewBieProduct(channelOid);
		addProductPojo(rowsRep, newBie);
		
		List<Product> t0Product = this.productDao.getT0ProductByChannelOidExceptTyj4APPAndPC(channelOid);
		if (t0Product != null && !t0Product.isEmpty()){
			for (Product t0pro : t0Product){
				addProductPojo(rowsRep, t0pro);
			}
		}
		
		sb.append(where.toString());
		@SuppressWarnings("unchecked")
		List<Object[]> list = em.createNativeQuery(sb.toString()).getResultList();
		int tnCount = 1;
		for (Object[] arr : list) {
			if (tnCount > 4) {
				break;
			}
			ProductPojo pojo = new ProductPojo();
			pojo.setProductOid((String)arr[0]);
			
			if (null != newBie && pojo.getProductOid().equals(newBie.getOid())) {
				continue;
			}
			pojo.setName((String)arr[1]);
			pojo.setInvestMin((BigDecimal)arr[2]);
			pojo.setExpAror((BigDecimal)arr[3]);
			pojo.setExpArorSec((BigDecimal)arr[4]);
			pojo.setRewardInterest((BigDecimal)arr[5]);
			pojo.setDurationPeriodDays(((Integer)arr[6]));
			pojo.setRaisedTotalNumber((BigDecimal)arr[7]);
			pojo.setCollectedVolume((BigDecimal)arr[8]);
			pojo.setLockCollectedVolume((BigDecimal)arr[9]);
			pojo.setStateOrder((arr[10]).toString());
			pojo.setState((String)arr[11]);
			pojo.setType((String)arr[12]);
			pojo.setPurchaseNum((Integer)arr[14]);
			pojo.setIncomeCalcBasis(arr[15].toString());
			
			setExpArrorDisp(pojo);
			List<LabelRep> labelList = this.productLabelService.getLabelRepByProduct(pojo.getProductOid());
			pojo.setLabelList(labelList);
			rowsRep.add(pojo);
			tnCount++;
		}
		return rowsRep;
	}
	
	private void setExpArrorDisp(ProductPojo pojo) {
		setExpArrorDispWithLevelRatio(pojo, null);
	}

	private void setExpArrorDisp(ProductPojo pojo, Product product) {
		if (null != pojo.getExpAror() && null != pojo.getExpArorSec()) {
			pojo.setShowType(ProductPojo.ProductPojo_showType_double);
		} else {
			pojo.setShowType(ProductPojo.ProductPojo_showType_single);
		}
		if (pojo.getExpAror().compareTo(pojo.getExpArorSec()) == 0) {
//			System.out.println(DecimalUtil.zoomOut(pojo.getExpAror(), 100));
//			System.out.println(DecimalUtil.setScaleDown(DecimalUtil.zoomOut(pojo.getExpAror(), 100)));
			pojo.setExpArrorDisp(DecimalUtil.zoomOut(pojo.getExpAror(), 100, 2) + "%");
		} else {
			pojo.setExpArrorDisp(DecimalUtil.zoomOut(pojo.getExpAror(), 100, 2) 
					+ "%~" + DecimalUtil.zoomOut(pojo.getExpArorSec(), 100, 2) + "%");
		}
		if (null != product && product.getType().getOid().equals(Product.TYPE_Producttype_02)) {
			if (null != product.getRewardInterest()) {
				pojo.setTenThousandIncome(InterestFormula.compound(new BigDecimal(10000),
						product.getExpAror().add(DecimalUtil.zoomIn(product.getRewardInterest(), 100)), product.getIncomeCalcBasis()));
			} else {
				pojo.setTenThousandIncome(InterestFormula.compound(new BigDecimal(10000), product.getExpAror(), product.getIncomeCalcBasis()));
			}
			
		}
		
	}
	
	
	/**
	 * 说明：有阶梯收益
	 * @param pojo
	 * @param product
	 */
	private void setExpArrorDispWithLevelRatio(ProductPojo pojo, Product product) {
		if (null != pojo.getExpAror() && null != pojo.getExpArorSec()) {
			pojo.setShowType(ProductPojo.ProductPojo_showType_double);
		} else {
			pojo.setShowType(ProductPojo.ProductPojo_showType_single);
		}
		if (pojo.getExpAror().compareTo(pojo.getExpArorSec()) == 0) {
//			System.out.println(DecimalUtil.zoomOut(pojo.getExpAror(), 100));
//			System.out.println(DecimalUtil.setScaleDown(DecimalUtil.zoomOut(pojo.getExpAror(), 100)));
			pojo.setExpArrorDisp(DecimalUtil.zoomOut(pojo.getExpAror(), 100, 2)
					+ "%");
		} else {
			pojo.setExpArrorDisp(DecimalUtil.zoomOut(pojo.getExpAror(), 100, 2) 
					+ "%~" + DecimalUtil.zoomOut(pojo.getExpArorSec(), 100, 2) + "%");
		}
		if (null != product && product.getType().getOid().equals(Product.TYPE_Producttype_02)) {
			if (null != product.getRewardInterest()) {
				pojo.setTenThousandIncome(InterestFormula.compound(new BigDecimal(10000),
						product.getExpAror().add(DecimalUtil.zoomIn(product.getRewardInterest(), 100)), product.getIncomeCalcBasis()));
			} else {
				pojo.setTenThousandIncome(InterestFormula.compound(new BigDecimal(10000), product.getExpAror(), product.getIncomeCalcBasis()));
			}
			
		}
		
	}

	private void addProductPojo(RowsRep<ProductPojo> rowsRep, Product product) {
		if (null == product) {
			return;
		}
		ProductPojo pojo = new ProductPojo();
		pojo.setProductOid(product.getOid());
		pojo.setName(product.getName());
		pojo.setInvestMin(product.getInvestMin());
		pojo.setExpAror(product.getExpAror());
		pojo.setExpArorSec(product.getExpArorSec());
		pojo.setRewardInterest(product.getRewardInterest());
		pojo.setDurationPeriodDays(product.getDurationPeriodDays());
		pojo.setRaisedTotalNumber(product.getRaisedTotalNumber());
		pojo.setMaxSaleVolume(product.getMaxSaleVolume());
		pojo.setCollectedVolume(product.getCollectedVolume());
		pojo.setLockCollectedVolume(product.getLockCollectedVolume());
		pojo.setStateOrder("0");
		pojo.setState(product.getState());
		pojo.setType(product.getType().getOid());
		pojo.setPurchaseNum(product.getPurchaseNum());
		/******************* 阶梯收益率start *************/
		List<ProductIncomeReward> prewards = rewardService.productRewardList(product.getOid());//奖励收益率
		if (prewards != null && prewards.size() > 0) {//有奖励收益
			BigDecimal minReward = prewards.get(0).getRatio();
			BigDecimal maxReward = prewards.get(0).getRatio();
			for (ProductIncomeReward preward : prewards) {
				if (preward.getRatio().compareTo(minReward) < 0) {
					minReward = preward.getRatio();
				}
				if (preward.getRatio().compareTo(maxReward) > 0) {
					maxReward = preward.getRatio();
				}
			}
			String minRewardStr = ProductDecimalFormat.format(ProductDecimalFormat.multiply(minReward)) + "%";
			String maxRewardStr = ProductDecimalFormat.format(ProductDecimalFormat.multiply(maxReward)) + "%";
			if (minRewardStr.equals(maxRewardStr)) {
				pojo.setRewardYieldRange(minRewardStr);
			} else {
				pojo.setRewardYieldRange(minRewardStr + "-" + maxRewardStr);
			}
		}
		/******************* 阶梯收益率end *************/
		setExpArrorDisp(pojo, product);
		List<LabelRep> labelList = this.productLabelService.getLabelRepByProduct(pojo.getProductOid());
		pojo.setLabelList(labelList);
		rowsRep.add(pojo);
	}

	/**
	 * 说明：筛选出非售罄中的活期产品列表
	 * 
	 * */
	public PageResp<ProductPojo> getT0Products(String channelOid,String uid,int page ,int rows ) {
		PageResp<ProductPojo> pageRep = new PageResp<ProductPojo>();
		
		List<String> resultLevel = null;
		if(!StringUtil.isEmpty(uid) && (!StringUtil.isEmpty(this.riskLevelDao.selectRiskLevel(uid)))){
			String riskLevel =this.riskLevelDao.selectRiskLevel(uid);
			resultLevel = this.jJCProductService.generateRiskLevel(riskLevel);
		}else{
			resultLevel = new ArrayList<String>();
			resultLevel.add("R1");
		}
		List<Product> productList = this.productDao.getT0ProductByChannelOidExceptTyj(channelOid,resultLevel);
		
		int i = 0;
		if (!CollectionUtils.isEmpty(productList)){
			
			for (Product product : productList) {
				int a = product.getMaxSaleVolume().compareTo(product.getLockCollectedVolume());
				int b = (product.getMaxSaleVolume().subtract(product.getLockCollectedVolume()))
						.divide(new BigDecimal(1)).subtract(product.getInvestMin()).compareTo(new BigDecimal(0));
				if((product.getState().equals(Product.STATE_Durationing))&&(a!=0)&&(b>=0)){
					i++;
					ProductPojo productPojo=new ProductPojo();
					BeanUtils.copyProperties(product, productPojo);
					productPojo.setProductOid(product.getOid());
					productPojo.setType(product.getType().getOid());
					productPojo.setStateOrder("0");
					
					/** 增加收益方式 */
					if (productPojo.getIncomeDealType().equals(Product.PRODUCT_incomeDealType_reinvest)) {
						productPojo.setIncomeDealTypeDesc("复利收益");
					} else if (productPojo.getIncomeDealType().equals(Product.PRODUCT_incomeDealType_cash)) {
						productPojo.setIncomeDealTypeDesc("现金分红");
					}
					
					/** 判断当前是否可用购买 */
					productPojo.setIsInvest(this.judgeInvestByTime(product));
					
					setExpArrorDisp(productPojo, product);
					List<LabelRep> labelList = this.productLabelService.getLabelRepByProduct(productPojo.getProductOid());
					productPojo.setLabelList(labelList);
					
					//******************* 阶梯收益率start ************
					List<ProductIncomeReward> prewards = rewardService.productRewardList(product.getOid());//奖励收益率
					if (prewards != null && prewards.size() > 0) {//有阶梯收益
						BigDecimal minReward = prewards.get(0).getRatio();
						BigDecimal maxReward = prewards.get(0).getRatio();
						for (ProductIncomeReward preward : prewards) {
							if (preward.getRatio().compareTo(minReward) < 0) {
								minReward = preward.getRatio();
							}
							if (preward.getRatio().compareTo(maxReward) > 0) {
								maxReward = preward.getRatio();
							}
						}
						String minRewardStr = ProductDecimalFormat.format(ProductDecimalFormat.multiply(minReward)) + "%";
						String maxRewardStr = ProductDecimalFormat.format(ProductDecimalFormat.multiply(maxReward)) + "%";
						if (minRewardStr.equals(maxRewardStr)) {
							productPojo.setRewardYieldRange(minRewardStr);
						} else {
							productPojo.setRewardYieldRange(minRewardStr + "-" + maxRewardStr);
						}
					}
				
					pageRep.getRows().add(productPojo);
				}
				
				
			}
			pageRep.setTotal(i);/**对list集合进行分页*/
			/*List<ProductPojo> newList=pageRep.getRows().subList(rows*(page-1), ((rows*page)>pageRep.getTotal()? (int)(pageRep.getTotal()):(page*rows)));
           */
			List<ProductPojo> newList = new ArrayList<ProductPojo>();
			 int currIdx = (page > 1 ? (page -1) * rows : 0);
		        for (int j = 0; j < rows && j < pageRep.getRows().size() - currIdx; j++) {
		        	ProductPojo productPojo  = pageRep.getRows().get(currIdx + j);
		            newList.add(productPojo);
		        }
			
            pageRep.setRows(newList);
		}
		return pageRep;
	}
	
	/**
	 * 说明：活期列表----基线
	 */
	/*public PageResp<ProductPojo> getT0Products(String channelOid) {
		PageResp<ProductPojo> pageRep = new PageResp<ProductPojo>();
		List<Product> productList = this.productDao.getT0ProductByChannelOidExceptTyj(channelOid);
		if (!CollectionUtils.isEmpty(productList)){
			for (Product product : productList) {
				ProductPojo productPojo=new ProductPojo();
				BeanUtils.copyProperties(product, productPojo);
				productPojo.setProductOid(product.getOid());
				productPojo.setType(product.getType().getOid());
				productPojo.setStateOrder("0");
				setExpArrorDisp(productPojo, product);
				List<LabelRep> labelList = this.productLabelService.getLabelRepByProduct(productPojo.getProductOid());
				productPojo.setLabelList(labelList);
				
				*//******************* 阶梯收益率start *************//*
				List<ProductIncomeReward> prewards = rewardService.productRewardList(product.getOid());//奖励收益率
				if (prewards != null && prewards.size() > 0) {//有阶梯收益
					BigDecimal minReward = prewards.get(0).getRatio();
					BigDecimal maxReward = prewards.get(0).getRatio();
					for (ProductIncomeReward preward : prewards) {
						if (preward.getRatio().compareTo(minReward) < 0) {
							minReward = preward.getRatio();
						}
						if (preward.getRatio().compareTo(maxReward) > 0) {
							maxReward = preward.getRatio();
						}
					}
					String minRewardStr = ProductDecimalFormat.format(ProductDecimalFormat.multiply(minReward)) + "%";
					String maxRewardStr = ProductDecimalFormat.format(ProductDecimalFormat.multiply(maxReward)) + "%";
					if (minRewardStr.equals(maxRewardStr)) {
						productPojo.setRewardYieldRange(minRewardStr);
					} else {
						productPojo.setRewardYieldRange(minRewardStr + "-" + maxRewardStr);
					}
				}
			
				pageRep.getRows().add(productPojo);
			}
			pageRep.setTotal(productList.size());
		}
		return pageRep;
	}*/

	public ProductCurrentDetailResp currentTyjDetail(String channelOid) {
		ProductCurrentDetailResp pr = new ProductCurrentDetailResp();
		Product product = this.productDao.getTyjProduct(channelOid);
		if (null == product) {
			log.error("暂无体验金产品！");
			pr.setErrorCode(-1);
			pr.setErrorMessage("暂无体验金产品！");
		}else{
			pr.setOid(product.getOid());
		}
		return pr;
	}
	
	/**
	 * 
	 * 判断当前的时间是否在交易时间内
	 * return boolean
	 * */
	public boolean judgeInvestByTime(Product product){
		
		//判断当前的时间是否在交易时间内
		String dealStartTime = product.getDealStartTime();
		String dealEndTime = product.getDealEndTime();
		if(product.getDealStartTime() == null||product.getDealStartTime().equals("")){
			dealStartTime = "000000";
		}
		if(product.getDealEndTime() == null||product.getDealEndTime().equals("")){
			dealEndTime = "235959";
		}				
		Timestamp currentTime = DateUtil.getSqlCurrentDate();		
		boolean flag = DateUtil.isIn(currentTime, dealStartTime, dealEndTime);
		return flag;
		
	}
	
	/**
	 * 通过用户的风险评测结果来筛选出所有的可购买的范围内的评测结果
	 * */
	public String getBuyProductRiskLevel(String uid){
		String riskLevel = this.riskLevelDao.selectRiskLevel(uid);
		List<String> resultLevel = this.jJCProductService.generateRiskLevel(riskLevel);
		String riskString = "";
		if(resultLevel !=null){
			for(int i = 0;i<resultLevel.size();i++){
				if(i == resultLevel.size()-1){
					riskString += "'"+resultLevel.get(i)+"'";
				}else{
					riskString += "'"+resultLevel.get(i)+"'"+",";
				}
			}
			riskString = "("+riskString +")";
		}
		return riskString;
	}
}
