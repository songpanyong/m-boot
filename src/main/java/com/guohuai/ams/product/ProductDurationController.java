package com.guohuai.ams.product;

import java.math.BigDecimal;
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

import com.alibaba.fastjson.JSONObject;
import com.guohuai.basic.component.ext.web.BaseController;
import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.basic.component.ext.web.PageResp;
import com.guohuai.basic.component.ext.web.Response;
import com.guohuai.component.util.StringUtil;

/**
 * 存续期产品操作相关接口
 * @author wangyan
 *
 */
@RestController
@RequestMapping(value = "/mimosa/product/duration", produces = "application/json")
public class ProductDurationController extends BaseController {

	@Autowired
	private ProductDurationService productDurationService;

	/**
	 * 存续期产品列表查询
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
	 * @return {@link ResponseEntity<PagesRep<ProductLogListResp>>} ,如果返回的errCode属性等于0表示成功，否则表示失败，失败原因在errMessage里面体现
	 */
	
	/*public ResponseEntity<PageResp<ProductLogListResp>> list(HttpServletRequest request, @RequestParam final String name, @RequestParam final String type, @RequestParam final String status, @RequestParam int page, @RequestParam int rows,
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
				return cb.and(cb.equal(root.get("isDeleted").as(String.class), Product.NO), cb.equal(root.get("auditState").as(String.class), Product.AUDIT_STATE_Reviewed));
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
		Specification<Product> statusSpec = null;
		if (!StringUtil.isEmpty(status)) {
			statusSpec = new Specification<Product>() {
				@Override
				public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
					return cb.equal(root.get("state").as(String.class), status);
				}
			};
			spec = Specifications.where(spec).and(statusSpec);
		}

		Pageable pageable = new PageRequest(page - 1, rows, new Sort(new Order(sortDirection, sort)));
		PageResp<ProductLogListResp> rep = this.productDurationService.durationList(spec, pageable);
		return new ResponseEntity<PageResp<ProductLogListResp>>(rep, HttpStatus.OK);
	}*/
	/**
	 * 后台产品展示
	 * 
	 * */
	@RequestMapping(value = "/list", name="存续期产品列表查询", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ResponseEntity<PageResp<ProductLogListResp>> list(HttpServletRequest request,
			@RequestParam final String name, @RequestParam final String type,  @RequestParam(required = false) String marketState,@RequestParam(required = false) String status,
			@RequestParam int page, @RequestParam int rows) {
		PageResp<ProductLogListResp> rep = this.productDurationService.toTnProductList(name, marketState, page, rows,type,status,true);
		return new ResponseEntity<PageResp<ProductLogListResp>>(rep, HttpStatus.OK);
	}
	
	
	
	/**
	 * 后台产品的列表查询（活、定期、心愿计划产品的列表查询）
	 * 
	 * 
	 */
	
	@RequestMapping(value = "/totnlist", name="活期、定期产品列表查询", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ResponseEntity<PageResp<ProductLogListResp>> totnlist(HttpServletRequest request,
			@RequestParam(required = false) String name, @RequestParam(required = false) String marketState,
			@RequestParam(required = false) String type,@RequestParam(required = false)String status, @RequestParam(required = true) int page, @RequestParam(required = true) int rows) {
		PageResp<ProductLogListResp> rep = this.productDurationService.toTnProductList(name, marketState, page, rows,type,status,false);
		return new ResponseEntity<PageResp<ProductLogListResp>>(rep, HttpStatus.OK);
	}
	
	/**
	 * 获取存续期产品的名称列表，包含id
	 * @return
	 */
	@RequestMapping(value = "/productNameList", name="获取存续期产品的名称列表", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<Response> getAllNameList() {
		Specification<Product> spec = new Specification<Product>() {
			@Override
			public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.and(cb.equal(root.get("isDeleted").as(String.class), Product.NO), cb.equal(root.get("auditState").as(String.class), Product.AUDIT_STATE_Reviewed));
			}
		};
		spec = Specifications.where(spec);
		
		List<JSONObject> jsonList = productDurationService.productNameList(spec);
		Response r = new Response();
		r.with("rows", jsonList);
		return new ResponseEntity<Response>(r, HttpStatus.OK);
	}
	
	
	/**
	 *  查询存续期产品默认一个产品
	 * @param oid 产品类型的oid
	 * @return {@link ResponseEntity<ProductDetailResp>} ,如果返回的errCode属性等于0表示成功，否则表示失败，失败原因在errMessage里面体现 
	 */
	@RequestMapping(value = "/getProductByOid", name="查询存续期产品默认一个产品", method = {RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResponseEntity<ProductDetailResp> getProductByOid(@RequestParam String oid) {
		ProductDetailResp pr = this.productDurationService.getProductByOid(oid);
		return new ResponseEntity<ProductDetailResp>(pr, HttpStatus.OK);
	}
	
	/**
	 *  查询存续期产品默认一个产品的统计信息
	 * @param oid 产品类型的oid
	 * @return {@link ResponseEntity<ProductDurationResp>} ,如果返回的errCode属性等于0表示成功，否则表示失败，失败原因在errMessage里面体现 
	 */
	@RequestMapping(value = "/getProductDuration", name="查询存续期产品默认一个产品的统计信息", method = {RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResponseEntity<ProductDurationResp> getProductDuration(@RequestParam String oid) {
		ProductDurationResp pdr = this.productDurationService.getProductDuration(oid);
		return new ResponseEntity<ProductDurationResp>(pdr, HttpStatus.OK);
	}
	
	/**
	 * 触发产品清盘
	 * @param oid 产品oid
	 * @param clearingComment 清盘理由
	 * @return
	 * @throws ParseException
	 */
	@RequestMapping(value = "/productClearing", name="触发产品清盘", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ResponseEntity<BaseResp> productClearing(@RequestParam(required = true) String oid,@RequestParam(required = true) String clearingComment) throws ParseException,Exception {
		String operator = super.getLoginUser();
		BaseResp repponse = this.productDurationService.productClearing(oid, operator, clearingComment);
		return new ResponseEntity<BaseResp>(repponse, HttpStatus.OK);
	}
	
	/**
	 * 交易规则设置
	 */
	@RequestMapping(value = "/currentTradingRuleSet", name="交易规则设置", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<BaseResp> currentTradingRuleSet(@Valid CurrentTradingRuleSetForm form) throws ParseException,Exception {
		String operator = super.getLoginUser();
		BaseResp repponse = this.productDurationService.currentTradingRuleSet(form, operator);
		return new ResponseEntity<BaseResp>(repponse, HttpStatus.OK);
	}
     
	/**
 	 * 单人单日赎回限额设置
 	 * 
 	 * @param oid
 	 *            产品oid
 	 * @param singleDailyMaxRedeem           
 	 * @return
 	 * @throws ParseException
 	 */ 
 	@RequestMapping(value = "/updateSingleDailyMaxRedeem", name="单人单日赎回限额设置", method = { RequestMethod.POST, RequestMethod.GET })
 	@ResponseBody
 	public ResponseEntity<BaseResp> updateSingleDailyMaxRedeem(@RequestParam String oid, @RequestParam BigDecimal singleDailyMaxRedeem) throws ParseException,Exception {
 		String operator = super.getLoginUser();
 		BaseResp repponse = this.productDurationService.updateSingleDailyMaxRedeem(oid, singleDailyMaxRedeem, operator);
 		return new ResponseEntity<BaseResp>(repponse, HttpStatus.OK);
 	}

     
// 	/**
// 	 * 激活赎回确认
// 	 * 
// 	 * @param oid
// 	 *            产品oid
// 	 * @return
// 	 * @throws ParseException
// 	 */
// 	@RequestMapping(value = "/openRedeemConfirm", name="激活赎回确认", method = { RequestMethod.POST, RequestMethod.GET })
// 	@ResponseBody
// 	public ResponseEntity<BaseResp> openRedeemConfirm(@RequestParam String oid) throws ParseException {
// 		String operator = super.getLoginUser();
// 		BaseResp repponse = this.productDurationService.openRedeemConfirm(oid, operator);
// 		return new ResponseEntity<BaseResp>(repponse, HttpStatus.OK);
// 	}

// 	/**
// 	 * 屏蔽赎回确认
// 	 * 
// 	 * @param oid
// 	 *            产品oid
// 	 * @return
// 	 * @throws ParseException
// 	 */
// 	@RequestMapping(value = "/closeRedeemConfirm", name="屏蔽赎回确认", method = { RequestMethod.POST, RequestMethod.GET })
// 	@ResponseBody
// 	public ResponseEntity<BaseResp> closeRedeemConfirm(@RequestParam String oid) throws ParseException {
// 		String operator = super.getLoginUser();
// 		BaseResp repponse = this.productDurationService.closeRedeemConfirm(oid, operator);
// 		return new ResponseEntity<BaseResp>(repponse, HttpStatus.OK);
// 	}
 	
 	/**
 	 * 快速赎回设置
 	 * 
 	 * @param oid
 	 *            产品oid
 	 * @param fastRedeemStatus 快速赎回开关 打开：YES 关闭：NO     
 	 * @param fastRedeemMax 快速赎回阀值	
 	 * @return
 	 * @throws ParseException
 	 */ 
// 	@RequestMapping(value = "/updateFastRedeem", name="快速赎回设置", method = { RequestMethod.POST, RequestMethod.GET })
// 	@ResponseBody
// 	public ResponseEntity<BaseResp> updateFastRedeem(@RequestParam String oid, @RequestParam String fastRedeemStatus, @RequestParam BigDecimal fastRedeemMax) throws ParseException,Exception {
// 		String operator = super.getLoginUser();
// 		BaseResp repponse = this.productDurationService.updateFastRedeem(oid, fastRedeemStatus, fastRedeemMax, operator);
// 		return new ResponseEntity<BaseResp>(repponse, HttpStatus.OK);
// 	}
 	
 	/**
 	 * 标签类型设置
 	 * 
 	 * @param oid
 	 *            产品oid
 	 * @param productLabel  '秒杀：seckill，recom:推荐，新手：freshman'
 	 * @return
 	 * @throws ParseException
 	 */ 
 	@RequestMapping(value = "/updateProductLabel", name="标签类型设置", method = { RequestMethod.POST, RequestMethod.GET })
 	@ResponseBody
 	public ResponseEntity<BaseResp> updateProductLabel(@RequestParam String oid, @RequestParam String[] expandProductLabels) throws ParseException,Exception {
 		String operator = super.getLoginUser();
 		BaseResp repponse = this.productDurationService.updateProductExtendLabel(oid, expandProductLabels, operator);
 		return new ResponseEntity<BaseResp>(repponse, HttpStatus.OK);
 	}
 	
 	
 	/**
 	 * 募集失败
 	 */ 
	@RequestMapping(value = "/productRaiseFail", name = "募集失败", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ResponseEntity<BaseResp> productRaiseFail(@RequestParam String oid) {
		String operator = super.getLoginUser();
		BaseResp repponse = this.productDurationService.productRaiseFail(oid, operator);
		return new ResponseEntity<BaseResp>(repponse, HttpStatus.OK);
	}
 	
 	/**
 	 * 募集成功
 	 * 
 	 * @param oid
 	 *            产品oid
 	 * @return
 	 * @throws ParseException
 	 */ 
 	@RequestMapping(value = "/productRaiseSuccess", name="募集成功", method = { RequestMethod.POST, RequestMethod.GET })
 	@ResponseBody
 	public ResponseEntity<BaseResp> productRaiseSuccess(@RequestParam String oid) throws ParseException,Exception {
 		String operator = super.getLoginUser();
 		BaseResp repponse = this.productDurationService.productRaiseSuccess(oid, operator);
 		return new ResponseEntity<BaseResp>(repponse, HttpStatus.OK);
 	}
 	
// 	/**
// 	 * 是否自动派息设置
// 	 * @param oid
// 	 * @param isAutoAssignIncome
// 	 * @return
// 	 * @throws ParseException
// 	 * @throws Exception
// 	 */
// 	@RequestMapping(value = "/isAutoAssignIncomeSet", name="是否自动派息设置", method = RequestMethod.POST)
// 	@ResponseBody
// 		public ResponseEntity<BaseResp> isAutoAssignIncomeSet(@RequestParam String oid, @RequestParam String isAutoAssignIncome) throws ParseException,Exception {	
// 		String operator = super.getLoginUser();
// 		BaseResp repponse = this.productDurationService.isAutoAssignIncomeSet(oid, isAutoAssignIncome, operator);
// 		return new ResponseEntity<BaseResp>(repponse, HttpStatus.OK);
// 	}
 	
 	/**
 	 * 产品的置顶
 	 * 
 	 */
 	/*@RequestMapping(value="totop",method = RequestMethod.POST)
 	public ResponseEntity<ToTopRep> toTop(@RequestParam String productOid){
 		ToTopRep rep = this.productDurationService.toTop(productOid);
 		return new ResponseEntity<ToTopRep>(rep,HttpStatus.OK);
 	}*/
	
 	/**
 	 * 根据权重值进行排序
 	 * 
 	 * */
 	@RequestMapping(value="sortbyvalue",method = RequestMethod.POST)
 	@ResponseBody
 	public ResponseEntity<BaseResp> saveValue(@RequestParam(required = true) String productOid,@RequestParam(required = true) int value){
 		
 		 BaseResp resp = this.productDurationService.savaValue(productOid,value);
 		 return new ResponseEntity<BaseResp>(resp,HttpStatus.OK);
 	}
}
