package com.guohuai.ams.product.productChannel;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;

import com.guohuai.ams.channel.Channel;
import com.guohuai.ams.channel.ChannelDao;
import com.guohuai.ams.label.LabelService;
import com.guohuai.ams.product.Product;
import com.guohuai.ams.product.ProductDao;
import com.guohuai.ams.product.ProductService;
import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.basic.component.ext.web.PageResp;
import com.guohuai.cache.service.CacheChannelService;
import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.DateUtil;
import com.guohuai.mmp.publisher.hold.PublisherHoldDao;
import com.guohuai.mmp.publisher.hold.PublisherHoldEntity;

@Service
@Transactional
public class ProductChannelService {

	@Autowired
	private ProductDao productDao;
	@Autowired
	private ProductChannelDao productChannelDao;
	@Autowired
	private ChannelDao channelDao;
	@Autowired
	private PublisherHoldDao publisherHoldDao;
	@Autowired
	private ProductService productService;
	@Autowired
	private CacheChannelService cacheChannelService;
	@Autowired
	private LabelService labelService;
	
	@Transactional
	public List<ProductChannel> queryProductChannels(final String productOid) {

		Specification<ProductChannel> spec = new Specification<ProductChannel>() {
			@Override
			public Predicate toPredicate(Root<ProductChannel> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.equal(root.get("product").get("oid").as(String.class), productOid);
			}
		};
		spec = Specifications.where(spec);

		List<ProductChannel> pcs = productChannelDao.findAll(spec);

		return pcs;
	}

	@Transactional
	public List<ProductChannel> queryProductChannels(final List<String> productOids) {

		Specification<ProductChannel> spec = new Specification<ProductChannel>() {
			@Override
			public Predicate toPredicate(Root<ProductChannel> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Expression<String> exp = root.get("product").get("oid").as(String.class);
				return exp.in(productOids);
			}
		};
		spec = Specifications.where(spec);

		List<ProductChannel> pcs = productChannelDao.findAll(spec);

		return pcs;
	}

	@Transactional
	public PageResp<ProductChannelResp> list(Specification<ProductChannel> spec, Pageable pageable) {
		PageResp<ProductChannelResp> pagesRep = new PageResp<ProductChannelResp>();

		Page<ProductChannel> productChannels = productChannelDao.findAll(spec, pageable);

		List<ProductChannelResp> list = new ArrayList<ProductChannelResp>();
		for (ProductChannel pc : productChannels) {
			ProductChannelResp rep = new ProductChannelResp(pc);
			list.add(rep);
		}
		pagesRep.setTotal(productChannels.getTotalElements());
		pagesRep.setRows(list);
		return pagesRep;
	}

	@Transactional
	public boolean isPublish(String cid, String ckey, String productOid) {
		int c = this.productChannelDao.countForPublish(cid, ckey, productOid);
		if (c > 0) {
			return true;
		}
		throw new AMPException("该产品在该渠道尚未发行或发行已被退回");
	} 

	@Transactional
	public BaseResp upshelf(String oid, String operator) {
		BaseResp response = new BaseResp();
		ProductChannel productChannel = this.productChannelDao.findOne(oid);
		if (productChannel == null) {
			throw AMPException.getException(90017);// 不能上架
		}
		Product product = this.productService.getProductByOid(productChannel.getProduct().getOid());
		Channel channel = this.channelDao.findOne(productChannel.getChannel().getOid());
		if (Channel.CHANNEL_STATUS_ON.equals(channel.getChannelStatus()) && Channel.CHANNEL_DELESTATUS_NO.equals(channel.getDeleteStatus())) {
			if (!ProductChannel.MARKET_STATE_Noshelf.equals(productChannel.getMarketState())
					&& !ProductChannel.MARKET_STATE_Offshelf.equals(productChannel.getMarketState())) {
				// error.define[90017]=不能上下架操作
				throw AMPException.getException(90017);
			}
			if(Product.TYPE_Producttype_02.equals(product.getType().getOid())){
				// 根据产品ID获取基本标签--体验金(>0),非体验金(=0)
				Integer labelCount = this.labelService.findLabelByProductId(product.getOid());
				//去掉体验金
				if (labelCount > 0) {
					// 获取已上架体验金活期产品，如果有则报错
					Integer channelLabelCount = labelService.findLabel4ProductChannel(channel.getOid());
					if (channelLabelCount > 0){
						//存在已上架的体验金活期产品！体验金活期产品限上架一个！(CODE:90038)
						throw AMPException.getException(90038);
					}
				}
			}
			
			productChannel.setMarketState(ProductChannel.MARKET_STATE_Onshelf);
			productChannel.setRackTime(new Timestamp(System.currentTimeMillis()));
			this.productChannelDao.saveAndFlush(productChannel);
			
			if(Product.TYPE_Producttype_02.equals(product.getType().getOid())) { //活期产品
				if (Product.DATE_TYPE_FirstRackTime.equals(product.getSetupDateType())
						&& product.getSetupDate() == null) { //与渠道首次上架同时开始募集
					product.setSetupDate(DateUtil.getSqlDate());
					product.setState(Product.STATE_Durationing);
					product.setUpdateTime(DateUtil.getSqlCurrentDate());
					productDao.saveAndFlush(product);
//					/**
//					 * 定期产品进入募集期时，增加产品发行数量
//					 * 活期产品进入存续期时
//					 * @author yuechao
//					 */
//					publisherStatisticsService.increaseReleasedProductAmount(product.getPublisherBaseAccount());
//					platformStatisticsService.increaseReleasedProductAmount();
//					/**
//					 * 定期产品进入募集期时，增加在售产品数量
//					 * 活期产品进入存续期时
//					 */
//					publisherStatisticsService.increaseOnSaleProductAmount(product.getPublisherBaseAccount());
//					platformStatisticsService.increaseOnSaleProductAmount();
				}
			} else if (Product.TYPE_Producttype_01.equals(product.getType().getOid())) {// 定期
				if (Product.DATE_TYPE_FirstRackTime.equals(product.getRaiseStartDateType())
						&& product.getRaiseStartDate() == null) {

					product.setRaiseStartDate(DateUtil.getSqlDate());
					product.setRaiseEndDate(
							DateUtil.addSQLDays(product.getRaiseStartDate(), product.getRaisePeriodDays() - 1));// 募集结束时间

					product.setSetupDate(DateUtil.addSQLDays(product.getRaiseEndDate(), product.getFoundDays()));// 最晚产品成立时间
					product.setDurationPeriodEndDate(
							DateUtil.addSQLDays(product.getSetupDate(), product.getDurationPeriodDays() - 1));// 存续期结束时间
					if (product.getAccrualRepayDays() != null && product.getAccrualRepayDays() > 0) {
						// 到期最晚还本付息日 指存续期结束后的还本付息最迟发生在存续期后的第X个自然日的23:59:59为止
						product.setRepayDate(
								DateUtil.addSQLDays(product.getDurationPeriodEndDate(), product.getAccrualRepayDays()));// 到期还款时间
					}

					product.setState(Product.STATE_Raising);
					product.setUpdateTime(DateUtil.getSqlCurrentDate());
					productDao.saveAndFlush(product);
//					/**
//					 * 定期产品进入募集期时，增加产品发行数量 活期产品进入存续期时
//					 * 
//					 * @author yuechao
//					 */
//					publisherStatisticsService.increaseReleasedProductAmount(product.getPublisherBaseAccount());
//					platformStatisticsService.increaseReleasedProductAmount();
//					/**
//					 * 定期产品进入募集期时，增加在售产品数量 活期产品进入存续期时
//					 */
//					publisherStatisticsService.increaseOnSaleProductAmount(product.getPublisherBaseAccount());
//					platformStatisticsService.increaseOnSaleProductAmount();
				}
			}
		} else {
			// error.define[90017]=不能上下架操作
			throw AMPException.getException(90017);
		}
		
		List<PublisherHoldEntity> list = this.publisherHoldDao.findSpvHoldByProduct(product);
		if(list==null || list.size()==0) {
			response.setErrorMessage("SPV仓位不足，请前往补仓位，以免影响该产品被购买!");
		} else {
			PublisherHoldEntity spvHold = list.get(0);
			if(spvHold.getTotalVolume().subtract(spvHold.getLockRedeemHoldVolume()).compareTo(BigDecimal.ZERO) > 0) {
				response.setErrorMessage("上架成功!");
			} else {
				if (Product.TYPE_Producttype_01.equals(product.getType().getOid())) {
					throw AMPException.getException("SPV仓位不足，不能上架");
				}
				response.setErrorMessage("SPV仓位不足，请前往补仓位，以免影响该产品被购买!");
			}
		}
		
		return response;
	}

	@Transactional
	public BaseResp donwshelf(String oid, String operator) {
		BaseResp response = new BaseResp();
		ProductChannel productChannel = this.productChannelDao.findOne(oid);
		if (productChannel == null) {
			throw AMPException.getException(90017);// 不能下架
		}
		if (!ProductChannel.MARKET_STATE_Onshelf.equals(productChannel.getMarketState())) {
			throw AMPException.getException(90017);
		}
		productChannel.setMarketState(ProductChannel.MARKET_STATE_Offshelf);
		productChannel.setDownTime(new Timestamp(System.currentTimeMillis()));
		this.productChannelDao.saveAndFlush(productChannel);
		
		return response;
	}

	// 渠道详情中的产品列表查询
	@Transactional
	public ProductChannelViewPage channelQuery(Specification<ProductChannel> spec, Pageable pageable) {
		Page<ProductChannel> rs = this.productChannelDao.findAll(spec, pageable);

		ProductChannelViewPage r = new ProductChannelViewPage();
		r.setTotal(rs.getTotalElements());

		if (null != rs.getContent() && rs.getContent().size() > 0) {
			for (ProductChannel c : rs.getContent()) {
				r.getRows().add(new ProductChannelView(c));
			}
		}

		return r;
	}

	/**
	 * 
	 * @param productOid
	 *            产品id
	 * @param channelOid
	 *            渠道id
	 */
	public ProductChannel getProductChannel(final String productOid, final String channelOid) {
		Specification<ProductChannel> spec = new Specification<ProductChannel>() {
			@Override
			public Predicate toPredicate(Root<ProductChannel> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.and(cb.equal(root.get("product").get("oid").as(String.class), productOid),
						cb.equal(root.get("channel").get("oid").as(String.class), channelOid));
			}
		};
		return this.productChannelDao.findOne(spec);
	}

	/**
	 * 根据产品查<<产品--渠道>>
	 * 
	 * @param product
	 * @return
	 */
	public List<ProductChannel> getChannelByProduct(Product product) {
		List<ProductChannel> list = this.productChannelDao.findByProductOid(product.getOid());
		return list;
	}
	/**
	 * 查询可用渠道
	 * @param lastOid
	 * @return
	 */
	public List<Object[]> getChannelByBatch(){
		return this.productChannelDao.getChannelByBatch();
	}
	
	/**
	 * 根据Oid查询可用渠道
	 * @param oid
	 * @return
	 */
	public List<ProductChannel> getChannelByProductOid(String productOid){
		return this.productChannelDao.findByProductOid(productOid);
	}

	public List<ProductChannel> getChannelByChannelOid(String channelOid) {
		return this.productChannelDao.findByChannelOid(channelOid);
	}
}
