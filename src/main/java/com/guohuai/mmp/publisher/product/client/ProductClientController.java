package com.guohuai.mmp.publisher.product.client;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.guohuai.ams.channel.Channel;
import com.guohuai.ams.product.Product;
import com.guohuai.ams.product.ProductPojo;
import com.guohuai.ams.product.ProductService;
import com.guohuai.ams.product.productChannel.ProductChannel;
import com.guohuai.basic.component.ext.web.BaseController;
import com.guohuai.basic.component.ext.web.PageResp;
import com.guohuai.component.util.DateUtil;
import com.guohuai.component.web.view.RowsRep;

import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.GreaterThanOrEqual;
import net.kaczmarzyk.spring.data.jpa.domain.LessThanOrEqual;
import net.kaczmarzyk.spring.data.jpa.domain.Like;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;

@RestController
@RequestMapping(value = "/mimosa/product/client", produces = "application/json")
public class ProductClientController extends BaseController {
	
	@Autowired
	private ProductClientService productClientService;
	@Autowired
	private ProductService productService;
	
	
	/**
	 * app查询可以申购的定期活期产品推荐列表
	 * @param request
	 * @param channeOid 渠道oid
	 * @return {@link ResponseEntity<PageResp<ProductListResp>>} ,如果返回的errCode属性等于0表示成功，否则表示失败，失败原因在errMessage里面体现 
	 */
	@RequestMapping(value = "/recommends", name="app查询可以申购的定期活期产品推荐列表", method = {RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResponseEntity<PageResp<ProductListResp>> recommends(HttpServletRequest request,
			@RequestParam final String channeOid) {
		
		int page = 1;
		int rows = 5;
		
		Specification<ProductChannel> spec = new Specification<ProductChannel>() {
			@Override
			public Predicate toPredicate(Root<ProductChannel> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				
				return cb.and(
						cb.equal(root.get("marketState").as(String.class), ProductChannel.MARKET_STATE_Onshelf),//产品在渠道上上架状态
						cb.equal(root.get("channel").get("oid").as(String.class), channeOid),//产品选择某个渠道
						cb.equal(root.get("channel").get("channelStatus").as(String.class), Channel.CHANNEL_STATUS_ON),//渠道启用
						cb.equal(root.get("channel").get("deleteStatus").as(String.class), Channel.CHANNEL_DELESTATUS_NO),//渠道未删除
						cb.equal(root.get("product").get("isDeleted").as(String.class), Product.NO),//产品未删除
						cb.equal(root.get("product").get("isOpenPurchase").as(String.class), Product.YES)//可以申购
					);
			}
		};
		
		Specification<ProductChannel> typeSpec = new Specification<ProductChannel>() {
			@Override
			public Predicate toPredicate(Root<ProductChannel> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.or(
						cb.and(cb.equal(root.get("product").get("type").get("oid").as(String.class), Product.TYPE_Producttype_02),//产品类型--活期
								cb.equal(root.get("product").get("state").as(String.class), Product.STATE_Durationing),//存续期(活期)
								cb.lessThanOrEqualTo(root.get("product").get("setupDate").as(Date.class), DateUtil.getSqlDate())//募集期内
								),
						cb.and(cb.equal(root.get("product").get("type").get("oid").as(String.class), Product.TYPE_Producttype_01),//产品类型--定期
								cb.equal(root.get("product").get("state").as(String.class), Product.STATE_Raising),//募集中(募集期)
								cb.lessThanOrEqualTo(root.get("product").get("raiseStartDate").as(Date.class), DateUtil.getSqlDate()),//募集期内
								cb.greaterThanOrEqualTo(root.get("product").get("raiseEndDate").as(Date.class), DateUtil.getSqlDate())//募集期内
								)
						);
			}
		};
		spec = Specifications.where(spec).and(typeSpec);
		
		Pageable pageable = new PageRequest(page - 1, rows, new Sort(new Order(Direction.DESC, "rackTime")));
		PageResp<ProductListResp> rep = this.productClientService.recommends(spec, pageable);
		return new ResponseEntity<PageResp<ProductListResp>>(rep, HttpStatus.OK);
	}
	
	
	/**
	 * app查询可以申购的定期活期产品全部列表
	 * @param request
	 * @param channeOid 渠道oid
	 * @param page 第几页
	 * @param rows 每页显示多少记录数
	 * @param sort 排序字段 rackTime
	 * @param order 排序规则：升序还是降序 desc
	 * @return {@link ResponseEntity<PageResp<ProductListResp>>} ,如果返回的errCode属性等于0表示成功，否则表示失败，失败原因在errMessage里面体现 
	 */
	@RequestMapping(value = "/list", name="app查询可以申购的定期活期产品全部列表", method = {RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResponseEntity<PageResp<ProductListResp>> list(HttpServletRequest request,
			@RequestParam final String channeOid,
			@RequestParam int page, 
			@RequestParam int rows) {
		
		if (page < 1) {
			page = 1;
		}
		if (rows < 1) {
			rows = 1;
		}
		
		Specification<ProductChannel> spec = new Specification<ProductChannel>() {
			@Override
			public Predicate toPredicate(Root<ProductChannel> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				
				return cb.and(
						cb.equal(root.get("marketState").as(String.class), ProductChannel.MARKET_STATE_Onshelf),//产品在渠道上上架状态
						cb.equal(root.get("channel").get("oid").as(String.class), channeOid),//产品选择某个渠道
						cb.equal(root.get("channel").get("channelStatus").as(String.class), Channel.CHANNEL_STATUS_ON),//渠道启用
						cb.equal(root.get("channel").get("deleteStatus").as(String.class), Channel.CHANNEL_DELESTATUS_NO),//渠道未删除
						cb.equal(root.get("product").get("isDeleted").as(String.class), Product.NO),//产品未删除
						cb.equal(root.get("product").get("isOpenPurchase").as(String.class), Product.YES)//可以申购
					);
			}
		};
		
		Specification<ProductChannel> typeSpec = new Specification<ProductChannel>() {
			@Override
			public Predicate toPredicate(Root<ProductChannel> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.or(
						cb.and(cb.equal(root.get("product").get("type").get("oid").as(String.class), Product.TYPE_Producttype_02),//产品类型--活期
								cb.equal(root.get("product").get("state").as(String.class), Product.STATE_Durationing),//存续期(活期)
								cb.lessThanOrEqualTo(root.get("product").get("setupDate").as(Date.class), DateUtil.getSqlDate())//募集期内
								),
						cb.and(cb.equal(root.get("product").get("type").get("oid").as(String.class), Product.TYPE_Producttype_01),//产品类型--定期
								cb.equal(root.get("product").get("state").as(String.class), Product.STATE_Raiseend)//募集結束
								),
						cb.and(cb.equal(root.get("product").get("type").get("oid").as(String.class), Product.TYPE_Producttype_01),//产品类型--定期
								cb.equal(root.get("product").get("state").as(String.class), Product.STATE_Raising),//募集中(募集期)
								cb.lessThanOrEqualTo(root.get("product").get("raiseStartDate").as(Date.class), DateUtil.getSqlDate()),//募集期内
								cb.greaterThanOrEqualTo(root.get("product").get("raiseEndDate").as(Date.class), DateUtil.getSqlDate())//募集期内
							)
						);
			}
		};
		spec = Specifications.where(spec).and(typeSpec);
		
		PageResp<ProductListResp> rep = this.productClientService.list(spec, page, rows);
		return new ResponseEntity<PageResp<ProductListResp>>(rep, HttpStatus.OK);
	}
	

	/**
	 * app查询可以申购的定期产品产品
	 * @param request
	 * @param channeOid 渠道oid
	 * @param reveal 保障方式（额外增信 YES NO）
	 * @param investMinStart 起头金额开始范围 元单位
	 * @param investMinEnd 起头金额结束范围 元单位
	 * @param durationPeriodDaysStart 投资期限开始范围 天单位
	 * @param durationPeriodDaysEnd 投资期限结束范围 天单位
	 * @param expArorStart 年化收益率开始范围 5%就传0.05
	 * @param expArorEnd 年化收益率结束范围 5%就传0.05
	 * @param page 第几页
	 * @param rows 每页显示多少记录数
	 * @param sort 排序字段 rackTime
	 * @param order 排序规则：升序还是降序 desc
	 * @return {@link ResponseEntity<PageResp<ProductPeriodicResp>>} ,如果返回的errCode属性等于0表示成功，否则表示失败，失败原因在errMessage里面体现 
	 */
	@RequestMapping(value = "/periodics", name="app查询可以申购的定期产品产品", method = {RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResponseEntity<PageResp<ProductPeriodicResp>> periodicList(HttpServletRequest request,
			@RequestParam int page, 
			@RequestParam int rows,
			@RequestParam(required = false, defaultValue = "rackTime") String sort,
			@RequestParam(required = false, defaultValue = "desc") String order,
			@RequestParam(required = false) String expArorStart,
			@RequestParam(required = false) String expArorEnd,
			@And({ @Spec(params = "channeOid", path = "channel.oid", spec = Equal.class),
					@Spec(params = "reveal", path = "product.reveal", spec = Like.class),
					@Spec(path = "product.investMin", params = { "investMinStart" }, spec = GreaterThanOrEqual.class),
					@Spec(path = "product.investMin", params = { "investMinEnd" }, spec = LessThanOrEqual.class),
					@Spec(path="product.durationPeriodDays", params={"durationPeriodDaysStart"}, spec=GreaterThanOrEqual.class),
					@Spec(path = "product.durationPeriodDays", params = {"durationPeriodDaysEnd" }, spec = LessThanOrEqual.class)}) 
			Specification<ProductChannel> customerSpec1) {
		if (page < 1) {
			page = 1;
		}
		if (rows < 1) {
			rows = 1;
		}
		
		Specification<ProductChannel> spec = new Specification<ProductChannel>() {
			@Override
			public Predicate toPredicate(Root<ProductChannel> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				
				return cb.and(
						cb.equal(root.get("marketState").as(String.class), ProductChannel.MARKET_STATE_Onshelf),//产品在渠道上上架状态
						cb.equal(root.get("channel").get("channelStatus").as(String.class), Channel.CHANNEL_STATUS_ON),//渠道启用
						cb.equal(root.get("channel").get("deleteStatus").as(String.class), Channel.CHANNEL_DELESTATUS_NO),//渠道未删除
						cb.equal(root.get("product").get("isDeleted").as(String.class), Product.NO),//产品未删除
						cb.equal(root.get("product").get("type").get("oid").as(String.class), Product.TYPE_Producttype_01),//产品类型--定期
						cb.equal(root.get("product").get("isOpenPurchase").as(String.class), Product.YES)//可以申购
					);
			}
		};
		
		Specification<ProductChannel> stateSpec = new Specification<ProductChannel>() {
			@Override
			public Predicate toPredicate(Root<ProductChannel> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.or(
						cb.and(cb.equal(root.get("product").get("state").as(String.class), Product.STATE_Raising),//募集中(募集期)
								cb.lessThanOrEqualTo(root.get("product").get("raiseStartDate").as(Date.class), DateUtil.getSqlDate()),//募集期内
								cb.greaterThanOrEqualTo(root.get("product").get("raiseEndDate").as(Date.class), DateUtil.getSqlDate())//募集期内
								),
						cb.equal(root.get("product").get("state").as(String.class), Product.STATE_Raiseend)//募集結束
						);
			}
		};
		spec = Specifications.where(spec).and(customerSpec1).and(stateSpec);
		
		PageResp<ProductPeriodicResp> rep = this.productClientService.periodicList(spec, expArorStart, expArorEnd, page, rows, sort, order);
		return new ResponseEntity<PageResp<ProductPeriodicResp>>(rep, HttpStatus.OK);
	}
	
	/**
	 * 活期列表--基线
	 */
	@RequestMapping(value = "/t0products", method = {RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResponseEntity<PageResp<ProductPojo>> getT0Products(HttpServletRequest request, 
			@RequestParam(required = true) String channelOid,@RequestParam(required = true) int page ,@RequestParam(required = true) int rows) {
		/** 增加当期登录用户的Id */
		String uid = super.isLogin();
		PageResp<ProductPojo> rep = this.productClientService.getT0Products(channelOid,uid,page,rows);
		return new ResponseEntity<PageResp<ProductPojo>>(rep, HttpStatus.OK);
	}
	
	
	/**
	 * APP端定期列表
	 */
	@RequestMapping(value = "/tnproducts", method = {RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResponseEntity<PageResp<ProductPojo>> getTnProducts(HttpServletRequest request,
			@RequestParam int page, @RequestParam int rows, @RequestParam(required = false) String sort, @RequestParam(required = false) String order,
			@RequestParam(required = false) BigDecimal expArorStart, @RequestParam(required = false) BigDecimal expArorEnd,
			@RequestParam(required = false) Integer durationPeriodDaysStart,
			@RequestParam(required = false) Integer durationPeriodDaysEnd,
			@RequestParam(required = true) String channelOid) {
	/*	if (page < 1) {
			page = 1;
		}
		if (rows < 1) {
			rows = 1;
		}
		int start = 0;
		int end = rows;
		if (page == 1) {
			start = 0;
		} else {
			start = (page - 1) * rows;
		}*/
		
		/** 增加当前登录的用户的id  */
		String uid = super.isLogin();
		PageResp<ProductPojo> rep = this.productClientService.getTnProducts(page, rows, expArorStart, expArorEnd,
				durationPeriodDaysStart, durationPeriodDaysEnd, channelOid, sort, order,uid);
		
			
		return new ResponseEntity<PageResp<ProductPojo>>(rep, HttpStatus.OK);
	}
	
	/**
	 * APP端首页
	 */
	@RequestMapping(value = "/apphome", name="app查询可以申购的定期产品产品", method = {RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResponseEntity<RowsRep<ProductPojo>> getAppHomeProducts(@RequestParam(required = true) String channelOid) {
		
		RowsRep<ProductPojo> rep = this.productClientService.getAppHomeProducts(channelOid);
		
		return new ResponseEntity<RowsRep<ProductPojo>>(rep, HttpStatus.OK);
	}
	
	
	 /**
	  * PC端首页
	  */
	@RequestMapping(value = "/pchome", method = {RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResponseEntity<RowsRep<ProductPojo>> getPCHomeProducts(@RequestParam(required = true) String channelOid) {
		
		RowsRep<ProductPojo> rep = this.productClientService.getPCHomeProducts(channelOid);
		
		return new ResponseEntity<RowsRep<ProductPojo>>(rep, HttpStatus.OK);
	}
	
	/**
	 * app查询可以申购的活期产品产品
	 * @param request
	 * @param channeOid
	 * @param page 第几页
	 * @param rows 每页显示多少记录数
	 * @param sort 排序字段 rackTime
	 * @param order 排序规则：升序还是降序 desc
	 * @return {@link ResponseEntity<PageResp<ProductCurrentResp>>} ,如果返回的errCode属性等于0表示成功，否则表示失败，失败原因在errMessage里面体现 
	 */
	@RequestMapping(value = "/currents", name="app查询可以申购的活期产品产品", method = {RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResponseEntity<PageResp<ProductCurrentResp>> currentList(HttpServletRequest request,
			@RequestParam final String channeOid,
			@RequestParam int page, 
			@RequestParam int rows) {
		
		if (page < 1) {
			page = 1;
		}
		if (rows < 1) {
			rows = 1;
		}
		
		Specification<ProductChannel> spec = new Specification<ProductChannel>() {
			@Override
			public Predicate toPredicate(Root<ProductChannel> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				
				return cb.and(
						cb.equal(root.get("marketState").as(String.class), ProductChannel.MARKET_STATE_Onshelf),//产品在渠道上上架状态
						cb.equal(root.get("channel").get("oid").as(String.class), channeOid),//产品选择某个渠道
						cb.equal(root.get("channel").get("channelStatus").as(String.class), Channel.CHANNEL_STATUS_ON),//渠道启用
						cb.equal(root.get("channel").get("deleteStatus").as(String.class), Channel.CHANNEL_DELESTATUS_NO),//渠道未删除
						cb.equal(root.get("product").get("isDeleted").as(String.class), Product.NO),//产品未删除
						cb.equal(root.get("product").get("type").get("oid").as(String.class), Product.TYPE_Producttype_02),//产品类型--活期
						cb.equal(root.get("product").get("state").as(String.class), Product.STATE_Durationing),//存续期(活期)
						cb.lessThanOrEqualTo(root.get("product").get("setupDate").as(Date.class), DateUtil.getSqlDate()),//募集期内
						cb.equal(root.get("product").get("isOpenPurchase").as(String.class), Product.YES)//可以申购
					);
			}
		};
		spec = Specifications.where(spec);

		final String sort = "rackTime";
		final String order = "desc";
		
		PageResp<ProductCurrentResp> rep = this.productClientService.currentList(spec, page, rows, sort, order);
		return new ResponseEntity<PageResp<ProductCurrentResp>>(rep, HttpStatus.OK);
	}
	
	/**
	 * 产品明细(活期)
	 * @param oid 产品类型的oid
	 * @return {@link ResponseEntity<ProductCurrentDetailResp>} ,如果返回的errCode属性等于0表示成功，否则表示失败，失败原因在errMessage里面体现 
	 */
	@RequestMapping(value = "/cdetail", name="app查询活期产品明细", method = {RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResponseEntity<ProductCurrentDetailResp> currentDetail(@RequestParam(required = true) String oid) {
		ProductCurrentDetailResp pr = this.productClientService.currentDetail(oid);
		return new ResponseEntity<ProductCurrentDetailResp>(pr, HttpStatus.OK);
	}
	
	
	/**
	 * 产品明细(获取体验金oid)
	 * @param oid 产品类型的oid
	 * @return {@link ResponseEntity<ProductCurrentDetailResp>} ,如果返回的errCode属性等于0表示成功，否则表示失败，失败原因在errMessage里面体现 
	 */
	@RequestMapping(value = "/tdetail", name="app查询体验金产品oid", method = {RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResponseEntity<ProductCurrentDetailResp> currentTyjDetail(@RequestParam(required = true) String channelOid) {
		ProductCurrentDetailResp pr = this.productClientService.currentTyjDetail(channelOid);
		return new ResponseEntity<ProductCurrentDetailResp>(pr, HttpStatus.OK);
	}
	
	/**
	 * 产品明细
	 * @param oid 产品类型的oid
	 * @return {@link ResponseEntity<ProductPeriodicDetailResp>} ,如果返回的errCode属性等于0表示成功，否则表示失败，失败原因在errMessage里面体现 
	 */
	@RequestMapping(value = "/pdetail", name="app查询定期产品明细", method = {RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResponseEntity<ProductPeriodicDetailResp> periodicDdetail(@RequestParam(required = true) String oid) {
		String uid = null;
		Product product = productService.findByOid(oid);
		if (Product.STATE_Raiseend.equals(product.getState()) ||
				Product.STATE_Durationing.equals(product.getState()) ||
				Product.STATE_Durationend.equals(product.getState()) ||
				Product.STATE_Cleared.equals(product.getState())) {
			uid = this.getLoginUser();
		}
		ProductPeriodicDetailResp pr = this.productClientService.periodicDdetail(oid, uid);
		return new ResponseEntity<ProductPeriodicDetailResp>(pr, HttpStatus.OK);
	}
	
	/**
	 * app根据产品标签查询产品列表
	 * @param request
	 * @param channeOid 渠道oid
	 * @param productLabel 产品标签 '秒杀：seckill，recom:推荐，新手：freshman'
	 * @return {@link ResponseEntity<PageResp<ProductListResp>>} ,如果返回的errCode属性等于0表示成功，否则表示失败，失败原因在errMessage里面体现 
	 */
	@RequestMapping(value = "/labelProducts", name="app根据产品标签查询产品列表", method = {RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResponseEntity<PageResp<ProductListResp>> labelProducts(HttpServletRequest request,
			@RequestParam final String channeOid,
			@RequestParam final String productLabel) {
//		Specification<ProductChannel> spec = new Specification<ProductChannel>() {
//			@Override
//			public Predicate toPredicate(Root<ProductChannel> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
//				return cb.and(
//						cb.equal(root.get("marketState").as(String.class), ProductChannel.MARKET_STATE_Onshelf),//产品在渠道上上架状态
//						cb.equal(root.get("channel").get("oid").as(String.class), channeOid),//产品选择某个渠道
//						cb.equal(root.get("channel").get("channelStatus").as(String.class), Channel.CHANNEL_STATUS_ON),//渠道启用
//						cb.equal(root.get("channel").get("deleteStatus").as(String.class), Channel.CHANNEL_DELESTATUS_NO),//渠道未删除
//						cb.equal(root.get("product").get("isDeleted").as(String.class), Product.NO),//产品未删除
//						cb.equal(root.get("product").get("isOpenPurchase").as(String.class), Product.YES),//可以申购
//						cb.equal(root.get("product").get("productLabel").as(String.class), productLabel)
//					);
//			}
//		};
//		
//		Specification<ProductChannel> typeSpec = new Specification<ProductChannel>() {
//			@Override
//			public Predicate toPredicate(Root<ProductChannel> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
//				return cb.or(
//						cb.and(cb.equal(root.get("product").get("type").get("oid").as(String.class), Product.TYPE_Producttype_02),//产品类型--活期
//								cb.equal(root.get("product").get("state").as(String.class), Product.STATE_Durationing),//存续期(活期)
//								cb.lessThanOrEqualTo(root.get("product").get("setupDate").as(Date.class), DateUtil.getSqlDate())//募集期内
//								),
//						cb.and(cb.equal(root.get("product").get("type").get("oid").as(String.class), Product.TYPE_Producttype_01),//产品类型--定期
//								cb.equal(root.get("product").get("state").as(String.class), Product.STATE_Raising),//募集中(募集期)
//								cb.lessThanOrEqualTo(root.get("product").get("raiseStartDate").as(Date.class), DateUtil.getSqlDate()),//募集期内
//								cb.greaterThanOrEqualTo(root.get("product").get("raiseEndDate").as(Date.class), DateUtil.getSqlDate())//募集期内
//								)
//						);
//			}
//		};
//		spec = Specifications.where(spec).and(typeSpec);
//		PageResp<ProductListResp> rep = this.productService.labelProducts(spec, new Sort(new Order(Direction.DESC, "rackTime")));
		PageResp<ProductListResp> rep = this.productClientService.labelProducts(channeOid, productLabel);
		return new ResponseEntity<PageResp<ProductListResp>>(rep, HttpStatus.OK);
	}
	
}
