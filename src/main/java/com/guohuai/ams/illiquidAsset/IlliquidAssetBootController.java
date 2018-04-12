package com.guohuai.ams.illiquidAsset;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
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

import com.guohuai.basic.component.ext.web.BaseController;
import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.Section;
import com.guohuai.component.web.view.Response;

import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.Like;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;

/**
 * 投资 管理
 * 
 * @author lirong
 *
 */
@RestController
@RequestMapping(value = "/mimosa/illiquidAsset/main", produces = "application/json;charset=UTF-8")
public class IlliquidAssetBootController extends BaseController {

	@Autowired
	private IlliquidAssetService illiquidAssetService;

	/**
	 * 投资 列表
	 * 
	 * @param request
	 * @param spec
	 * @param page
	 * @param rows
	 * @param sortField
	 * @param sort
	 * @return
	 */
	@RequestMapping(name = " 管理-投资标的列表", value = "/list", method = { RequestMethod.POST, RequestMethod.GET })
	public @ResponseBody ResponseEntity<IlliquidAssetListResp> list(HttpServletRequest request, @And({ @Spec(params = "name", path = "name", spec = Like.class), @Spec(params = "type", path = "type", spec = Equal.class), @Spec(params = "state", path = "state", spec = Equal.class) }) Specification<IlliquidAsset> spec, @RequestParam(required = false, defaultValue = "1") int page, @RequestParam(required = false, defaultValue = "10") int rows,
			@RequestParam(required = false, defaultValue = "updateTime") String sortField, @RequestParam(required = false, defaultValue = "desc") String sort) {
		Direction sortDirection = Direction.DESC;
		if (!"desc".equals(sort)) {
			sortDirection = Direction.ASC;
		}
		Specification<IlliquidAsset> lifeStateSpec = new Specification<IlliquidAsset>() {
			@Override
			public Predicate toPredicate(Root<IlliquidAsset> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				//Predicate a = cb.equal(root.get("lifeState").as(String.class), IlliquidAsset.ILLIQUIDASSET_STATE_CREATE);
				Expression<String> exp_state = root.get("state").as(String.class);
				return cb.and(exp_state.in(new Object[] { IlliquidAsset.ILLIQUIDASSET_STATE_CREATE, IlliquidAsset.ILLIQUIDASSET_STATE_AUDITING, IlliquidAsset.ILLIQUIDASSET_STATE_PASS, IlliquidAsset.ILLIQUIDASSET_STATE_REJECT }));
			}
		};
		spec = Specifications.where(spec).and(lifeStateSpec);

		Pageable pageable = new PageRequest(page - 1, rows, new Sort(new Order(sortDirection, sortField)));

		IlliquidAssetListResp resp = illiquidAssetService.queryPage(spec, pageable);

		return new ResponseEntity<IlliquidAssetListResp>(resp, HttpStatus.OK);
	}

	/**
	 * 投资标的详情
	 * 
	 * @param oid
	 * @return
	 */
	@RequestMapping(name = "标的管理-投资标的详情", value = "detail", method = { RequestMethod.POST, RequestMethod.GET })
	public @ResponseBody ResponseEntity<IlliquidAssetResp> detail(@RequestParam(required = true) String oid) {
		IlliquidAsset entity = illiquidAssetService.findByOid(oid);
		IlliquidAssetResp resp = new IlliquidAssetResp(entity);
		return new ResponseEntity<IlliquidAssetResp>(resp, HttpStatus.OK);
	}

	/**
	 * 新建投资标的
	 * 
	 * @param investment
	 * @return
	 */
	@RequestMapping(name = "标的管理-新建投资标的", value = "add", method = { RequestMethod.POST, RequestMethod.GET })
	public @ResponseBody ResponseEntity<BaseResp> add(@Valid IlliquidAssetForm form) {
		String operator = super.getLoginUser();
		illiquidAssetService.saveIlliquidAsset(form, operator);
		return new ResponseEntity<BaseResp>(new BaseResp(), HttpStatus.OK);
	}

	/**
	 * 编辑投资标的
	 * 
	 * @param investment
	 * @return
	 */
	@RequestMapping(name = "标的管理-编辑投资标的", value = "edit", method = { RequestMethod.POST, RequestMethod.GET })
	public @ResponseBody ResponseEntity<BaseResp> edit(@Valid IlliquidAssetForm form) {
		String operator = super.getLoginUser();
		illiquidAssetService.updateIlliquidAsset(form, operator);
		return new ResponseEntity<BaseResp>(new BaseResp(), HttpStatus.OK);
	}

	/**
	 * 提交预审
	 * 
	 * @param investment
	 * @return
	 */
	/**
	 * @param oid
	 * @return
	 */
	@RequestMapping(name = "标的管理-投资标的提交预审", value = "examine", method = { RequestMethod.POST, RequestMethod.GET })
	public @ResponseBody ResponseEntity<BaseResp> examine(@RequestParam(required = true) String oid) {
		String operator = super.getLoginUser();
		illiquidAssetService.comitCheck(oid, operator);
		return new ResponseEntity<BaseResp>(new BaseResp(), HttpStatus.OK);
	}

	/**
	 * 作废
	 * 
	 * @param investment
	 * @return
	 */
	@RequestMapping(name = "标的管理-投资标的作废", value = "invalid", method = { RequestMethod.POST, RequestMethod.GET })
	public @ResponseBody ResponseEntity<BaseResp> invalid(@RequestParam(required = true) String oid) {
		String operator = super.getLoginUser();
		illiquidAssetService.invalid(oid, operator);
		return new ResponseEntity<BaseResp>(new BaseResp(), HttpStatus.OK);
	}

	/**
	 * 投资标的预审列表
	 * 
	 * @param request
	 * @param page
	 * @param rows
	 * @param sortField
	 * @param sort
	 * @return
	 */
	@RequestMapping(name = "投资标的预审列表", value = "checklist", method = { RequestMethod.POST, RequestMethod.GET })
	public @ResponseBody ResponseEntity<IlliquidAssetListResp> list(HttpServletRequest request, @RequestParam(required = false, defaultValue = "1") int page, @RequestParam(required = false, defaultValue = "10") int rows, @RequestParam(required = false, defaultValue = "updateTime") String sortField, @RequestParam(required = false, defaultValue = "desc") String sort) {
		Direction sortDirection = Direction.DESC;
		if (!"desc".equals(sort)) {
			sortDirection = Direction.ASC;
		}
		Specification<IlliquidAsset> spec = new Specification<IlliquidAsset>() {
			@Override
			public Predicate toPredicate(Root<IlliquidAsset> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.equal(root.get("state").as(String.class), IlliquidAsset.ILLIQUIDASSET_STATE_AUDITING);
			}
		};
		Pageable pageable = new PageRequest(page - 1, rows, new Sort(new Order(sortDirection, sortField)));

		IlliquidAssetListResp resp = illiquidAssetService.queryPage(spec, pageable);

		return new ResponseEntity<IlliquidAssetListResp>(resp, HttpStatus.OK);
	}

	/**
	 * 预审通过
	 * 
	 * @param oid
	 * @return
	 */
	@RequestMapping(name = "投资标的预审通过", value = "checkpass", method = { RequestMethod.POST, RequestMethod.GET })
	public @ResponseBody ResponseEntity<BaseResp> checkPass(@RequestParam(required = true) String oid, @RequestParam(required = true) BigDecimal trustAmount) {
		String operator = super.getLoginUser();
		illiquidAssetService.precheck(oid, IlliquidAsset.ILLIQUIDASSET_STATE_PASS, operator, trustAmount, null);
		return new ResponseEntity<BaseResp>(new BaseResp(), HttpStatus.OK);
	}

	/**
	 * 预审驳回
	 * 
	 * @param oid
	 * @return
	 */
	@RequestMapping(name = "投资标的预审驳回", value = "checkreject", method = { RequestMethod.POST, RequestMethod.GET })
	public @ResponseBody ResponseEntity<BaseResp> checkReject(@RequestParam(required = true) String oid, @RequestParam(required = false, defaultValue = "0") BigDecimal trustAmount, @RequestParam String suggest) {
		String operator = super.getLoginUser();
		illiquidAssetService.precheck(oid, IlliquidAsset.ILLIQUIDASSET_STATE_REJECT, operator, trustAmount, suggest);
		return new ResponseEntity<BaseResp>(new BaseResp(), HttpStatus.OK);
	}

	/**
	 * 预审通过（非信托）
	 * 
	 * @param oid
	 * @return
	 */
	@RequestMapping(name = "投资标的预审通过", value = "checkpassY", method = { RequestMethod.POST, RequestMethod.GET })
	public @ResponseBody ResponseEntity<BaseResp> checkpassY(@RequestParam(required = true) String oid) {
		String operator = super.getLoginUser();
		illiquidAssetService.precheckN(oid, IlliquidAsset.ILLIQUIDASSET_STATE_PASS, operator);
		return new ResponseEntity<BaseResp>(new BaseResp(), HttpStatus.OK);
	}

	/**
	 * 预审驳回（非信托）
	 * 
	 * @param oid
	 * @return
	 */
	@RequestMapping(name = "投资标的预审驳回", value = "checkpassN", method = { RequestMethod.POST, RequestMethod.GET })
	public @ResponseBody ResponseEntity<BaseResp> checkpassN(@RequestParam(required = true) String oid) {
		String operator = super.getLoginUser();
		illiquidAssetService.precheckN(oid, IlliquidAsset.ILLIQUIDASSET_STATE_REJECT, operator);
		return new ResponseEntity<BaseResp>(new BaseResp(), HttpStatus.OK);
	}

	/**
	 * 标的确认
	 * 
	 * @param oid
	 * @return
	 */
	@RequestMapping(name = "标的管理-标的确认", value = "enter", method = { RequestMethod.POST, RequestMethod.GET })
	public @ResponseBody ResponseEntity<BaseResp> investmentEntity(@RequestParam(required = true) String oid) {
		String operator = super.getLoginUser();
		illiquidAssetService.enter(oid, operator);
		return new ResponseEntity<BaseResp>(new BaseResp(), HttpStatus.OK);
	}

	/**
	 * 投资标的库管理列表
	 * 
	 * @Title: investmentPoolList
	 * @author vania
	 * @version 1.0
	 * @see:
	 * @param request
	 * @param spec
	 * @param page
	 * @param size
	 * @param sortDirection
	 * @param sortField
	 * @return ResponseEntity<InvestmentListResp> 返回类型
	 */
	@RequestMapping(name = "标的库列表", value = "poolList", method = { RequestMethod.POST, RequestMethod.GET })
	public @ResponseBody ResponseEntity<IlliquidAssetListResp> list(HttpServletRequest request, @RequestParam() final String op, @And({ @Spec(params = "name", path = "name", spec = Like.class), @Spec(params = "type", path = "type", spec = Equal.class) }) Specification<IlliquidAsset> spec, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "50") int rows, @RequestParam(defaultValue = "desc") String sortDirection,
			@RequestParam(defaultValue = "updateTime") String sortField) {

		if (page < 1) {
			page = 1;
		}
		if (rows <= 0) {
			rows = 50;
		}
		Order order = new Order(Direction.valueOf(sortDirection.toUpperCase()), sortField);
		Pageable pageable = new PageRequest(page - 1, rows, new Sort(order));

		// 根据不同的操作拼接条件
		spec = Specifications.where(spec).and(new Specification<IlliquidAsset>() {
			@Override
			public Predicate toPredicate(Root<IlliquidAsset> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicate = new ArrayList<>();
				if (op.equals("storageList")) { // 投资标的备选库
					Expression<String> exp_state = root.get("state").as(String.class);
					predicate.add(exp_state.in(new Object[] { IlliquidAsset.ILLIQUIDASSET_STATE_DURATION }));
					//					Expression<String> exp_lifeState = root.get("lifeState").as(String.class);					
					//					predicate.add(exp_lifeState.in(new Object[] { IlliquidAsset.ILLIQUIDASSET_LIFESTATE_COLLECTING}));
				} else if (op.equals("holdList")) { // 已持有列表
					Expression<String> exp_state = root.get("state").as(String.class);
					predicate.add(exp_state.in(new Object[] { IlliquidAsset.ILLIQUIDASSET_STATE_DURATION }));
					//Expression<String> exp_lifeState = root.get("lifeState").as(String.class);
					//predicate.add(exp_lifeState.in(new Object[] { IlliquidAsset.ILLIQUIDASSET_LIFESTATE_COLLECTING, IlliquidAsset.ILLIQUIDASSET_LIFESTATE_OVER_VALUEDATE, IlliquidAsset.ILLIQUIDASSET_LIFESTATE_OVERDUE }));

					Expression<BigDecimal> expHa = root.get("holdShare").as(BigDecimal.class);
					Predicate p = cb.gt(expHa, new BigDecimal(0)); //持有金额大于0: holdShare > 0 		
					predicate.add(p);
				} else if (op.equals("notHoldList")) { // 未持有列表
					Expression<String> exp_state = root.get("state").as(String.class);
					predicate.add(exp_state.in(new Object[] { IlliquidAsset.ILLIQUIDASSET_STATE_DURATION }));
					//Expression<String> exp_lifeState = root.get("lifeState").as(String.class);
					//predicate.add(exp_lifeState.in(new Object[] { IlliquidAsset.ILLIQUIDASSET_LIFESTATE_COLLECTING, IlliquidAsset.ILLIQUIDASSET_LIFESTATE_OVER_VALUEDATE, IlliquidAsset.ILLIQUIDASSET_LIFESTATE_OVERDUE }));

					Expression<BigDecimal> exp = root.get("holdShare").as(BigDecimal.class);
					Predicate p = cb.or(cb.isNull(exp), cb.le(exp, new BigDecimal(0))); //持有金额为空或者大于0: holdShare is null or holdShare < 0
					predicate.add(p);
				} else if (op.equals("historyList")) { // 历史列表
					Expression<String> exp_state = root.get("state").as(String.class);
					//作废状态
					predicate.add(exp_state.in(new Object[] { IlliquidAsset.ILLIQUIDASSET_STATE_INCALID }));
					Expression<String> exp_lifeState = root.get("lifeState").as(String.class);
					//已过期
					predicate.add(exp_lifeState.in(new Object[] {IlliquidAsset.ILLIQUIDASSET_LIFESTATE_OVER_VALUEDATE }));// 已到期
				} else {
					throw AMPException.getException("未知的操作类型[" + op + "]");
				}
				Predicate[] pre = new Predicate[predicate.size()];
				return query.where(predicate.toArray(pre)).getRestriction();
			}
		});

		String raiseScope = request.getParameter("raiseScope");
		spec = this.buildSpec(spec, "raiseScope", raiseScope);

		String holdShare = request.getParameter("holdShare");
		spec = this.buildSpec(spec, "holdShare", holdShare);

		String applyAmount = request.getParameter("applyAmount");
		spec = this.buildSpec(spec, "applyAmount", applyAmount);

		String lifed = request.getParameter("lifed");
		spec = this.buildSpec(spec, "lifed", lifed);

		String expAror = request.getParameter("expAror");
		spec = this.buildSpec(spec, "expAror", expAror);

		IlliquidAssetListResp resp = illiquidAssetService.queryPage(spec, pageable);

		return new ResponseEntity<IlliquidAssetListResp>(resp, HttpStatus.OK);
	}

	/**
	 * 构建范围查询条件
	 * 
	 * @Title: buildSpec
	 * @author vania
	 * @version 1.0
	 * @see:
	 * @param spec
	 * @param attr
	 * @param value
	 * @return Specification<Investment> 返回类型
	 */
	private Specification<IlliquidAsset> buildSpec(Specification<IlliquidAsset> spec, final String attr, final String value) {
		if (StringUtils.isNotBlank(value)) {
			spec = Specifications.where(spec).and(new Specification<IlliquidAsset>() {
				@Override
				public Predicate toPredicate(Root<IlliquidAsset> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
					Section<IlliquidAsset> section = new Section<IlliquidAsset>(value);
					return section.build(root, cb, attr);
				}
			});
		}
		return spec;
	}

	//	/**
	//	 * 标的成立
	//	 * @param form
	//	 * @return ResponseEntity<BaseResp>    返回类型
	//	 */
	//	@RequestMapping(name = "标的成立", value = "establish", method = { RequestMethod.POST, RequestMethod.GET })
	//	public @ResponseBody ResponseEntity<BaseResp> establish(@Valid EstablishForm form) {
	//		String loginId = null; 
	//		try {
	//			loginId = super.getLoginUser();
	//		} catch (Exception e) {
	//			
	//		}
	//		form.setOperator(loginId);
	//		this.illiquidAssetService.establish(form);
	//		return new ResponseEntity<BaseResp>(new BaseResp(), HttpStatus.OK);
	//	}

	/**
	 * 标的不成立
	 * 
	 * @Title: unEstablish
	 * @see: @return CommonResp 返回类型 @throws
	 */
	@RequestMapping(name = "标的成立失败", value = "unEstablish", method = { RequestMethod.POST, RequestMethod.GET })
	public @ResponseBody ResponseEntity<BaseResp> unEstablish(@Valid UnEstablishForm form) {
		String loginId = null;
		try {
			loginId = super.getLoginUser();
		} catch (Exception e) {

		}
		form.setOperator(loginId);
		this.illiquidAssetService.unEstablish(form);
		return new ResponseEntity<BaseResp>(new BaseResp(), HttpStatus.OK);
	}

	/**
	 * 逾期
	 * 
	 * @param oid
	 * @return
	 */
	@RequestMapping(name = " 逾期", value = "overdueN", method = { RequestMethod.POST, RequestMethod.GET })
	public @ResponseBody ResponseEntity<Response> overdue(@Valid String oid) {
		this.illiquidAssetService.overdue(oid, super.getLoginUser());
		Response r = new Response();
		r.with("result", "SUCCESSED!");
		return new ResponseEntity<Response>(r, HttpStatus.OK);
	}

	/**
	 * 投资 正常还款
	 * 
	 * @param oid
	 * @return
	 */
	@RequestMapping(name = "本息兑付", value = "incomeSaveN", method = { RequestMethod.POST, RequestMethod.GET })
	public @ResponseBody ResponseEntity<Response> incomeSaveN(@Valid String oid) {
		illiquidAssetService.targetIncomeN(oid, super.getLoginUser());
		Response r = new Response();
		r.with("result", "SUCCESSED!");
		return new ResponseEntity<Response>(r, HttpStatus.OK);
	}

	/**
	 * 投资 逾期还款
	 * 
	 * @param oid
	 * @return
	 */
	@RequestMapping(name = "逾期兑付", value = "incomeSaveD", method = { RequestMethod.POST, RequestMethod.GET })
	public @ResponseBody ResponseEntity<Response> incomeSaveD(@Valid String oid) {
		illiquidAssetService.targetIncomeD(oid, super.getLoginUser());
		Response r = new Response();
		r.with("result", "SUCCESSED!");
		return new ResponseEntity<Response>(r, HttpStatus.OK);
	}
	
	/**
	 * 投资 逾期转让
	 * 
	 * @param oid
	 * @return
	 */
	@RequestMapping(name = "逾期转让", value = "overdueTransfer", method = { RequestMethod.POST, RequestMethod.GET })
	public @ResponseBody ResponseEntity<Response> overdueTransfer(@Valid String oid) {
		illiquidAssetService.overdueTransfer(oid, super.getLoginUser());
		Response r = new Response();
		r.with("result", "SUCCESSED!");
		return new ResponseEntity<Response>(r, HttpStatus.OK);
	}
	/**
	 *  转让
	 * 
	 * @param oid
	 * @return
	 */
	@RequestMapping(name = "转让", value = "transfer", method = { RequestMethod.POST, RequestMethod.GET })
	public @ResponseBody ResponseEntity<Response> transfer(@Valid String oid) {
		illiquidAssetService.transfer(oid, super.getLoginUser());
		Response r = new Response();
		r.with("result", "SUCCESSED!");
		return new ResponseEntity<Response>(r, HttpStatus.OK);
	}

	/**
	 * 投资 坏账核销
	 * 
	 * @param oid
	 * @return
	 */
	@RequestMapping(name = " 坏账核销", value = "targetCancel", method = { RequestMethod.POST, RequestMethod.GET })
	public @ResponseBody ResponseEntity<Response> targetCancel(@Valid String oid) {
		illiquidAssetService.targetCancel(oid, super.getLoginUser());
		Response r = new Response();
		r.with("result", "SUCCESSED!");
		return new ResponseEntity<Response>(r, HttpStatus.OK);
	}

	/**
	 * 结束
	 * 
	 * @param oid
	 * @return ResponseEntity<BaseResp> 返回类型
	 */
	@RequestMapping(name = " 结束", value = "close", method = { RequestMethod.POST, RequestMethod.GET })
	public @ResponseBody ResponseEntity<BaseResp> close(@RequestParam() String oid) {
		String loginId = null;
		try {
			loginId = super.getLoginUser();
		} catch (Exception e) {
		}
		this.illiquidAssetService.close(oid, loginId);
		return new ResponseEntity<BaseResp>(new BaseResp(), HttpStatus.OK);
	}
	
	
	/**
	 * 获取可认购的标的类型
	 * 
	 * @param type
	 *            order（订单）
	 * @return
	 */
	@RequestMapping(value = "/subscripeOptions", name = "投资组合 - 非现金类资产 - 認購列表", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<List<IlliquidAssetOptions>> subscripeOptions(@RequestParam String portfolioOid, @RequestParam(required = false, defaultValue="") String type) {
		List<IlliquidAssetOptions> names = this.illiquidAssetService.subscripeQuery(portfolioOid, type);
		return new ResponseEntity<List<IlliquidAssetOptions>>(names, HttpStatus.OK);
	}

	/**
	 * 获取可申购的标的类型
	 * 
	 * @param type
	 *            order（订单）
	 * @return
	 */
	@RequestMapping(value = "/purchaseOptions", name = "投资组合 - 非现金类资产 - 申购列表", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<List<IlliquidAssetOptions>> purchaseOptions(@RequestParam String portfolioOid, @RequestParam(required=false, defaultValue="") String type) {
		List<IlliquidAssetOptions> names = this.illiquidAssetService.purchaseQuery(portfolioOid, type);
		return new ResponseEntity<List<IlliquidAssetOptions>>(names, HttpStatus.OK);
	}

}
