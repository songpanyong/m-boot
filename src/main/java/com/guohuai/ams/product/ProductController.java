package com.guohuai.ams.product;

import java.math.BigDecimal;
import java.sql.Date;
import java.text.ParseException;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.guohuai.basic.component.ext.web.BaseController;
import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.basic.component.ext.web.PageResp;
import com.guohuai.component.util.DateUtil;
import com.guohuai.component.util.StringUtil;
import com.guohuai.component.web.view.Response;
import com.guohuai.mmp.investor.tradeorder.InvestorRepayCashTradeOrderService;

import net.kaczmarzyk.spring.data.jpa.domain.Like;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Conjunction;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Or;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;

/**
 * 产品操作相关接口
 * @author wangyan
 *
 */
@RestController
@RequestMapping(value = "/mimosa/product", produces = "application/json")
public class ProductController extends BaseController {

	@Autowired
	private ProductService productService;
	@Autowired
	private InvestorRepayCashTradeOrderService investorCashTradeOrderService;
	
	
	/**
	 * 获取productOid对应产品所有可以选择的资产池的名称列表
	 * @return
	 */
	@RequestMapping(value = "/getOptionalAssetPoolNameList", name="新加编辑产品可以选择的资产池名称列表", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<Response> getAllNameList(@RequestParam(required = false) String productOid) {
		List<JSONObject> jsonList = productService.getOptionalPortfolioNameList(productOid);
		Response r = new Response();
		r.with("rows", jsonList);
		return new ResponseEntity<Response>(r, HttpStatus.OK);
	}
	
	/**
	 * 新加定期产品
	 */
	@RequestMapping(value = "/save/periodic", name = "新加定期产品", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<BaseResp> savePeriodic(@Valid SavePeriodicProductForm form) throws ParseException, Exception {
		String operator = super.getLoginUser();
		
		BaseResp repponse = this.productService.savePeriodic(form, operator);
		return new ResponseEntity<BaseResp>(repponse, HttpStatus.OK);
	}

	/**
	 * 新加活期产品
	 */
	@RequestMapping(value = "/save/current", name="新加活期产品", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<BaseResp> saveCurrent(@Valid SaveCurrentProductForm form)  {
		String operator = super.getLoginUser();
		
		BaseResp repponse = this.productService.saveCurrent(form, operator);
		return new ResponseEntity<BaseResp>(repponse, HttpStatus.OK);
	}

	@RequestMapping(value = "/delete", name="作废产品", method = { RequestMethod.POST, RequestMethod.DELETE })
	@ResponseBody
	public ResponseEntity<ProductResp> delete(@RequestParam(required = true) String oid) {
		String operator = super.getLoginUser();
		Product product = this.productService.delete(oid, operator);
		return new ResponseEntity<ProductResp>(new ProductResp(product), HttpStatus.OK);
	}

	/**
	 * 更新定期产品
	 */
	@RequestMapping(value = "/update/periodic", name="编辑定期产品", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<BaseResp> updatePeriodic(@Valid SavePeriodicProductForm form) throws ParseException,Exception {
		String operator = super.getLoginUser();

		BaseResp repponse = this.productService.updatePeriodic(form, operator);
		return new ResponseEntity<BaseResp>(repponse, HttpStatus.OK);
	}

	/**
	 * 更新活期产品
	 * 
	 * @param form
	 * @return
	 */
	@RequestMapping(value = "/update/current", name="编辑活期产品", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<BaseResp> updateCurrent(@Valid SaveCurrentProductForm form) throws ParseException,Exception {
		String operator = super.getLoginUser();
		BaseResp repponse = this.productService.updateCurrent(form, operator);
		return new ResponseEntity<BaseResp>(repponse, HttpStatus.OK);
	}

	/**
	 * 产品明细
	 * 
	 * @param oid
	 *            产品类型的oid
	 * @return {@link ResponseEntity<ProductDetailResp>} ,如果返回的errCode属性等于0表示成功，否则表示失败，失败原因在errMessage里面体现
	 */
	@RequestMapping(value = "/detail", name="产品明细", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ResponseEntity<ProductDetailResp> detail(@RequestParam(required = true) String oid) {
		ProductDetailResp pr = this.productService.read(oid);
		return new ResponseEntity<ProductDetailResp>(pr, HttpStatus.OK);
	}

	/**
	 * 产品申请列表查询
	 * 
	 * @param request
	 * @param spec
	 * @param page
	 *            第几页
	 * @param rows
	 *            每页显示多少记录数
	 * @param sort
	 *            排序字段 update
	 * @param order
	 *            排序规则：升序还是降序 desc
	 * @return {@link ResponseEntity<PagesRep<ProductQueryRep>>} ,如果返回的errCode属性等于0表示成功，否则表示失败，失败原因在errMessage里面体现
	 */
	@RequestMapping(value = "/apply/list",name="产品申请列表查询", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ResponseEntity<PageResp<ProductResp>> applyList(HttpServletRequest request, @RequestParam final String name, @RequestParam final String type, @RequestParam int page, @RequestParam int rows,
			@RequestParam(required = false, defaultValue = "updateTime") String sort, @RequestParam(required = false, defaultValue = "desc") String order) {

		if (page < 1) {
			page = 1;
		}
		if (rows < 1) {
			rows = 1;
		}

		Direction sortDirection = Direction.DESC;
		if (!"desc".equals(order)) {
			sortDirection = Direction.ASC;
		}
		Specification<Product> spec = new Specification<Product>() {
			@Override
			public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.and(cb.equal(root.get("isDeleted").as(String.class), Product.NO), cb.equal(root.get("auditState").as(String.class), Product.AUDIT_STATE_Nocommit));
			}
		};
		spec = Specifications.where(spec);

		Specification<Product> nameSpec = null;
		if (!StringUtil.isEmpty(name)) {
			nameSpec = new Specification<Product>() {
				@Override
				public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
					return cb.or(cb.like(root.get("name").as(String.class), "%" + name + "%"), cb.like(root.get("fullName").as(String.class), "%" + name + "%"));
				}
			};
			spec = Specifications.where(spec).and(nameSpec);
		}
		Specification<Product> typeSpec = null;
		if (!StringUtil.isEmpty(type)) {
			typeSpec = new Specification<Product>() {
				@Override
				public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
					return cb.equal(root.get("type").get("oid").as(String.class), type);
				}
			};
			spec = Specifications.where(spec).and(typeSpec);
		}

		Pageable pageable = new PageRequest(page - 1, rows, new Sort(new Order(sortDirection, sort)));
		PageResp<ProductResp> rep = this.productService.list(spec, pageable);
		return new ResponseEntity<PageResp<ProductResp>>(rep, HttpStatus.OK);
	}

	/**
	 * 产品审核列表查询
	 * 
	 * @param request
	 * @param spec
	 * @param page
	 *            第几页
	 * @param rows
	 *            每页显示多少记录数
	 * @param sort
	 *            排序字段 update
	 * @param order
	 *            排序规则：升序还是降序 desc
	 * @return {@link ResponseEntity<PagesRep<ProductQueryRep>>} ,如果返回的errCode属性等于0表示成功，否则表示失败，失败原因在errMessage里面体现
	 */
	@RequestMapping(value = "/audit/list", name="产品审核列表查询", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ResponseEntity<PageResp<ProductLogListResp>> auditList(HttpServletRequest request, @RequestParam final String name, @RequestParam final String type, @RequestParam int page, @RequestParam int rows,
			@RequestParam(required = false, defaultValue = "updateTime") String sort, @RequestParam(required = false, defaultValue = "desc") String order) {

		if (page < 1) {
			page = 1;
		}
		if (rows < 1) {
			rows = 1;
		}
		Direction sortDirection = Direction.DESC;
		if (!"desc".equals(order)) {
			sortDirection = Direction.ASC;
		}
		Specification<Product> spec = new Specification<Product>() {
			@Override
			public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.and(cb.equal(root.get("isDeleted").as(String.class), Product.NO), cb.equal(root.get("auditState").as(String.class), Product.AUDIT_STATE_Auditing));
			}
		};
		spec = Specifications.where(spec);

		Specification<Product> nameSpec = null;
		if (!StringUtil.isEmpty(name)) {
			nameSpec = new Specification<Product>() {
				@Override
				public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
					return cb.or(cb.like(root.get("name").as(String.class), "%" + name + "%"), cb.like(root.get("fullName").as(String.class), "%" + name + "%"));
				}
			};
			spec = Specifications.where(spec).and(nameSpec);
		}
		Specification<Product> typeSpec = null;
		if (!StringUtil.isEmpty(type)) {
			typeSpec = new Specification<Product>() {
				@Override
				public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
					return cb.equal(root.get("type").get("oid").as(String.class), type);
				}
			};
			spec = Specifications.where(spec).and(typeSpec);
		}

		Pageable pageable = new PageRequest(page - 1, rows, new Sort(new Order(sortDirection, sort)));
		PageResp<ProductLogListResp> rep = this.productService.auditList(spec, pageable);
		return new ResponseEntity<PageResp<ProductLogListResp>>(rep, HttpStatus.OK);
	}

	/**
	 * 产品查询复核列表
	 * 
	 * @param request
	 * @param spec
	 * @param page
	 *            第几页
	 * @param rows
	 *            每页显示多少记录数
	 * @param sort
	 *            排序字段 update
	 * @param order
	 *            排序规则：升序还是降序 desc
	 * @return {@link ResponseEntity<PagesRep<ProductQueryRep>>} ,如果返回的errCode属性等于0表示成功，否则表示失败，失败原因在errMessage里面体现
	 */
	@RequestMapping(value = "/check/list", name="产品查询复核列表", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ResponseEntity<PageResp<ProductLogListResp>> checkList(HttpServletRequest request, @RequestParam final String name, @RequestParam final String type, @RequestParam int page, @RequestParam int rows,
			@RequestParam(required = false, defaultValue = "updateTime") String sort, @RequestParam(required = false, defaultValue = "desc") String order) {

		if (page < 1) {
			page = 1;
		}
		if (rows < 1) {
			rows = 1;
		}
		Direction sortDirection = Direction.DESC;
		if (!"desc".equals(order)) {
			sortDirection = Direction.ASC;
		}
		Specification<Product> spec = new Specification<Product>() {
			@Override
			public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.and(cb.equal(root.get("isDeleted").as(String.class), Product.NO), cb.equal(root.get("auditState").as(String.class), Product.AUDIT_STATE_Reviewing));
			}
		};
		spec = Specifications.where(spec);

		Specification<Product> nameSpec = null;
		if (!StringUtil.isEmpty(name)) {
			nameSpec = new Specification<Product>() {
				@Override
				public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
					return cb.or(cb.like(root.get("name").as(String.class), "%" + name + "%"), cb.like(root.get("fullName").as(String.class), "%" + name + "%"));
				}
			};
			spec = Specifications.where(spec).and(nameSpec);
		}
		Specification<Product> typeSpec = null;
		if (!StringUtil.isEmpty(type)) {
			typeSpec = new Specification<Product>() {
				@Override
				public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
					return cb.equal(root.get("type").get("oid").as(String.class), type);
				}
			};
			spec = Specifications.where(spec).and(typeSpec);
		}

		Pageable pageable = new PageRequest(page - 1, rows, new Sort(new Order(sortDirection, sort)));
		PageResp<ProductLogListResp> rep = this.productService.checkList(spec, pageable);
		return new ResponseEntity<PageResp<ProductLogListResp>>(rep, HttpStatus.OK);
	}

	/**
	 * 产品提交审核
	 * 
	 * @param oids
	 *            产品oids
	 * @return {@link ResponseEntity<BaseResp>} ,如果返回的errCode属性等于0表示成功，否则表示失败，失败原因在errMessage里面体现
	 */
	@RequestMapping(value = "/aduit/apply", name = "产品提交审核", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ResponseEntity<BaseResp> aduitApply(@RequestParam String oids) {
		String operator = super.getLoginUser();
		List<String> oidlist = JSON.parseArray(oids, String.class);
		BaseResp repponse = this.productService.aduitApply(oidlist, operator);
		return new ResponseEntity<BaseResp>(repponse, HttpStatus.OK);
	}

	/**
	 * 产品审核通过
	 * 
	 * @param oids
	 *            产品oids
	 * @return {@link ResponseEntity<BaseResp>} ,如果返回的errCode属性等于0表示成功，否则表示失败，失败原因在errMessage里面体现
	 */
	@RequestMapping(value = "/aduit/approve", name="产品审核通过", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ResponseEntity<BaseResp> aduitApprove(@RequestParam(required = true) String oid, @RequestParam(required = false) String auditComment) throws ParseException {
		String operator = super.getLoginUser();
		BaseResp repponse = this.productService.aduitApprove(oid, operator, auditComment);
		return new ResponseEntity<BaseResp>(repponse, HttpStatus.OK);
	}

	/**
	 * 产品审核不通过
	 * 
	 * @param oid
	 *            产品oid
	 * @param auditComment
	 *            审核不通过备注
	 * @return {@link ResponseEntity<BaseResp>} ,如果返回的errCode属性等于0表示成功，否则表示失败，失败原因在errMessage里面体现
	 * @throws ParseException
	 */
	@RequestMapping(value = "/aduit/reject", name="产品审核不通过", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ResponseEntity<BaseResp> aduitReject(@RequestParam(required = true) String oid, @RequestParam(required = true) String auditComment) throws ParseException {
		String operator = super.getLoginUser();
		BaseResp repponse = this.productService.aduitReject(oid, auditComment, operator);
		return new ResponseEntity<BaseResp>(repponse, HttpStatus.OK);
	}

	/**
	 * 产品复核通过
	 * 
	 * @param oids
	 *            产品oid
	 * @return {@link ResponseEntity<BaseResp>} ,如果返回的errCode属性等于0表示成功，否则表示失败，失败原因在errMessage里面体现
	 */
	@RequestMapping(value = "/review/approve", name="产品复核通过", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ResponseEntity<BaseResp> reviewApprove(@RequestParam(required = true) String oid, @RequestParam(required = false) String auditComment) throws ParseException {
		String operator = super.getLoginUser();
		BaseResp repponse = this.productService.reviewApprove(oid, operator, auditComment);
		return new ResponseEntity<BaseResp>(repponse, HttpStatus.OK);
	}

	/**
	 * 产品复核不通过
	 * 
	 * @param oid
	 *            产品oid
	 * @param auditComment
	 *            审核不通过备注
	 * @return {@link ResponseEntity<BaseResp>} ,如果返回的errCode属性等于0表示成功，否则表示失败，失败原因在errMessage里面体现
	 * @throws ParseException
	 */
	@RequestMapping(value = "/review/reject", name="产品复核不通过", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ResponseEntity<BaseResp> reviewReject(@RequestParam(required = true) String oid, @RequestParam(required = true) String auditComment) throws ParseException {
		String operator = super.getLoginUser();
		BaseResp repponse = this.productService.reviewReject(oid, auditComment, operator);
		return new ResponseEntity<BaseResp>(repponse, HttpStatus.OK);
	}

	/**
	 * 验证产品名称是否唯一
	 */
	@RequestMapping(value = "/validateName", name="验证名称是否唯一", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<BaseResp> validateName(@RequestParam String name, @RequestParam(required = false) String id) {
		BaseResp pr = new BaseResp();
		long single = this.productService.validateSingle("name", name, id);
		return new ResponseEntity<BaseResp>(pr, single > 0 ? HttpStatus.CONFLICT : HttpStatus.OK);
	}

	/**
	 * 验证产品全称是否唯一
	 */
	@RequestMapping(value = "/validateFullName", name="验证全称是否唯一", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<BaseResp> validateFullName(@RequestParam String fullName, @RequestParam(required = false) String id) {
		BaseResp pr = new BaseResp();
		long single = this.productService.validateSingle("fullName", fullName, id);
		return new ResponseEntity<BaseResp>(pr, single > 0 ? HttpStatus.CONFLICT : HttpStatus.OK);
	}

	/**
	 * 验证产品编码是否唯一
	 */
	@RequestMapping(value = "/validateCode", name = "验证编码是否唯一", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<BaseResp> validateCode(@RequestParam String code, @RequestParam(required = false) String id) {
		BaseResp pr = new BaseResp();
		long single = this.productService.validateSingle("code", code, id);
		return new ResponseEntity<BaseResp>(pr, single > 0 ? HttpStatus.CONFLICT : HttpStatus.OK);
	}
	
	/**
	 * 派发存续期收益
	 */
	@RequestMapping(value = "/allocateIncome", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public ResponseEntity<BaseResp> allocateIncome(@RequestParam String productOid, @RequestParam BigDecimal incomeAmount, @RequestParam BigDecimal couponAmount, @RequestParam BigDecimal fpRate) {
		BaseResp pr = new BaseResp();
		investorCashTradeOrderService.allocateIncome(productOid, incomeAmount, couponAmount, fpRate);
		return new ResponseEntity<BaseResp>(pr, HttpStatus.OK);
	}
	
	/**
	 * 还本付息
	 */
	@RequestMapping(value = "/cash", method = {RequestMethod.GET, RequestMethod.POST} )
	@ResponseBody
	public ResponseEntity<BaseResp> cash(@RequestParam String productOid) {
		BaseResp pr = new BaseResp();
		investorCashTradeOrderService.repayCash(productOid);
		return new ResponseEntity<BaseResp>(pr, HttpStatus.OK);
	}
	
	/**
	 * 发行人下的标的列表
	 * @param request
	 * @param spec
	 * @param productNameSpec
	 * @param corporateOid
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping(value = "/accproducts", name="发行人下的标的列表", method = {RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResponseEntity<PageResp<ProductResp>> approvesQuery(HttpServletRequest request,
			@Conjunction(value = {
		            @Or({@Spec(path="name", params="name", spec=Like.class),
		                @Spec(path="fullName", params="name", spec=Like.class)})
		        }, and = @Spec(path="code", params="code", spec=Like.class)) Specification<Product> spec,
			@RequestParam String publisherBaseAccountOid,	
			@RequestParam(required = false, defaultValue = "") final String creTimeBegin,
			@RequestParam(required = false, defaultValue = "") final String creTimeEnd,
		@RequestParam int page, 
		@RequestParam int rows) {
		if(!StringUtil.isEmpty(creTimeBegin) || !StringUtil.isEmpty(creTimeEnd)) {
			if(!StringUtil.isEmpty(creTimeBegin)) {
				spec = Specifications.where(spec).and(new Specification<Product>() {
				@Override
				public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
					return cb.greaterThanOrEqualTo(root.get("createTime").as(Date.class), new Date(DateUtil.parse(creTimeBegin, DateUtil.datetimePattern).getTime()));
				}
			});
			}
			if(!StringUtil.isEmpty(creTimeEnd)) {
				spec = Specifications.where(spec).and(new Specification<Product>() {
				@Override
				public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
					return cb.lessThanOrEqualTo(root.get("createTime").as(Date.class), new Date(DateUtil.parse(creTimeEnd, DateUtil.datetimePattern).getTime()));
				}
			});
			}
				
		}
		
		PageResp<ProductResp> rep = this.productService.accproducts(publisherBaseAccountOid, spec, page, rows);
		
		return new ResponseEntity<PageResp<ProductResp>>(rep, HttpStatus.OK);
	}
	
}
