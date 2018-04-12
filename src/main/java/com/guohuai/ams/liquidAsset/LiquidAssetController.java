package com.guohuai.ams.liquidAsset;

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
import org.springframework.data.domain.Page;
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
import com.guohuai.component.util.DateUtil;
import com.guohuai.component.util.Section;

import lombok.extern.slf4j.Slf4j;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.Like;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;

@RestController
@RequestMapping(value = "/mimosa/boot/liquidAsset", produces = "application/json;charset=UTF-8")
@Slf4j
public class LiquidAssetController extends BaseController {

	@Autowired
	private LiquidAssetService liquidAssetService;

	@RequestMapping(name = "新增现金类标的资产", value = "/addFund", method = { RequestMethod.POST, RequestMethod.GET })
	public @ResponseBody ResponseEntity<BaseResp> add(@Valid LiquidAssetForm form) {
		String operator = super.getLoginUser();
		LiquidAsset liquidAsset = liquidAssetService.createInvestment(form);
		liquidAsset.setState(LiquidAsset.LIQUID_STATE_waitPretrial);
		liquidAsset.setOperator(operator);
		liquidAsset.setCreateTime(DateUtil.getSqlCurrentDate());
		liquidAsset.setUpdateTime(DateUtil.getSqlCurrentDate());
//		this.liquidAssetService.findBySnAndState(liquidAsset);
		liquidAsset = liquidAssetService.save(liquidAsset);
		return new ResponseEntity<BaseResp>(new BaseResp(), HttpStatus.OK);
	}

	@RequestMapping(name = "投资标的资产查询", value = "/list", method = { RequestMethod.POST, RequestMethod.GET })
	public @ResponseBody ResponseEntity<LiquidAssetListResp> list(HttpServletRequest request,
			@And({ @Spec(params = "sn", path = "sn", spec = Equal.class),
					@Spec(params = "name", path = "name", spec = Like.class),
					@Spec(params = "type", path = "type", spec = Equal.class),
					@Spec(params = "state", path = "state", spec = Equal.class) }) Specification<LiquidAsset> spec,
			@RequestParam(required = false, defaultValue = "1") int page,
			@RequestParam(required = false, defaultValue = "10") int rows,
			@RequestParam(required = false, defaultValue = "updateTime") String sortField,
			@RequestParam(required = false, defaultValue = "desc") String sort) {

		Direction sortDirection = Direction.DESC;
		if (!"desc".equals(sort)) {
			sortDirection = Direction.ASC;
		}
		Specification<LiquidAsset> stateSpec = new Specification<LiquidAsset>() {
			@Override
			public Predicate toPredicate(Root<LiquidAsset> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.and(cb.notEqual(root.get("state"), LiquidAsset.LIQUID_STATE_invalid),
						cb.notEqual(root.get("state"), LiquidAsset.LIQUID_STATE_collecting));
			}
		};
		spec = Specifications.where(spec).and(stateSpec);
		Pageable pageable = new PageRequest(page - 1, rows, new Sort(new Order(sortDirection, sortField)));
		Page<LiquidAsset> entitys = liquidAssetService.getLiquidAssetList(spec, pageable);
		LiquidAssetListResp resps = new LiquidAssetListResp(entitys);
		return new ResponseEntity<LiquidAssetListResp>(resps, HttpStatus.OK);
	}

	/**
	 * 查看标的详情
	 * 
	 * @param oid
	 * @return
	 */
	@RequestMapping(name = "现金类标的详情", value = "/detail", method = { RequestMethod.POST, RequestMethod.GET })
	public @ResponseBody ResponseEntity<LiquidAssetResp> detail(String oid) {
		LiquidAsset entity = this.liquidAssetService.findByOid(oid);
		LiquidAssetResp resp = new LiquidAssetResp(entity);
		return new ResponseEntity<LiquidAssetResp>(resp, HttpStatus.OK);
	}

	/**
	 * 现金类标的投资选项
	 * @param type
	 * @return
	 */
	@RequestMapping(name = "现金类标的投资选项", value = "/options", method = { RequestMethod.POST, RequestMethod.GET })
	public @ResponseBody ResponseEntity<List<LiquidAssetOptions>> options(@RequestParam String type) {
		List<LiquidAssetOptions> options = this.liquidAssetService.getOptions(type);
		return new ResponseEntity<>(options, HttpStatus.OK);
	}

	/**
	 * 资产作废
	 * 
	 * @param oid
	 * @return
	 */
	@RequestMapping(name = "现金管理类工具作废", value = "/invalid", method = { RequestMethod.POST, RequestMethod.GET })
	public @ResponseBody ResponseEntity<BaseResp> invalid(@RequestParam(required = true) String oid) {
		String operator = super.getLoginUser();
		LiquidAsset entity = liquidAssetService.findByOid(oid);
		// 资产作废
		entity.setState(LiquidAsset.LIQUID_STATE_invalid);
		entity.setUpdateTime(DateUtil.getSqlCurrentDate());
		entity.setOperator(operator);
		liquidAssetService.save(entity);
		return new ResponseEntity<BaseResp>(new BaseResp(), HttpStatus.OK);
	}

	@RequestMapping(name = "现金管理类工具审核列表", value = "/accessList", method = { RequestMethod.POST, RequestMethod.GET })
	public @ResponseBody ResponseEntity<LiquidAssetListResp> accessList(HttpServletRequest request,
			@RequestParam(required = false, defaultValue = "1") int page,
			@RequestParam(required = false, defaultValue = "10") int rows,
			@RequestParam(required = false, defaultValue = "updateTime") String sortField,
			@RequestParam(required = false, defaultValue = "desc") String sort) {
		Direction sortDirection = Direction.DESC;
		if (!"desc".equals(sort)) {
			sortDirection = Direction.ASC;
		}
		Specification<LiquidAsset> spec = new Specification<LiquidAsset>() {
			@Override
			public Predicate toPredicate(Root<LiquidAsset> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.equal(root.get("state").as(String.class), LiquidAsset.LIQUID_STATE_pretrial);
			}
		};
		Pageable pageable = new PageRequest(page - 1, rows, new Sort(new Order(sortDirection, sortField)));
		Page<LiquidAsset> entitys = liquidAssetService.getLiquidAssetList(spec, pageable);
		LiquidAssetListResp resps = new LiquidAssetListResp(entitys);
		return new ResponseEntity<LiquidAssetListResp>(resps, HttpStatus.OK);
	}

	/**
	 * 标的提交审核
	 * 
	 * @param oid
	 * @return
	 */
	@RequestMapping(name = "货币基金提交审核", value = "/examine", method = { RequestMethod.POST, RequestMethod.GET })
	public @ResponseBody ResponseEntity<BaseResp> examine(@RequestParam(required = true) String oid) {
		String operator = super.getLoginUser();
		LiquidAsset entity = liquidAssetService.findByOid(oid);
		if (!LiquidAsset.LIQUID_STATE_waitPretrial.equals(entity.getState())
				&& !LiquidAsset.LIQUID_STATE_reject.equals(entity.getState())) {
			// 标的状态不是待预审或驳回不能提交预审
			throw new RuntimeException();
		}
		entity.setState(LiquidAsset.LIQUID_STATE_pretrial);
		entity.setOperator(operator);
		entity.setUpdateTime(DateUtil.getSqlCurrentDate());
		liquidAssetService.save(entity);
		return new ResponseEntity<BaseResp>(new BaseResp(), HttpStatus.OK);
	}

	/**
	 * 审核通过
	 * 
	 * @param oid
	 * @return
	 */
	@RequestMapping(name = "投资标的审核通过", value = "/checkpass", method = { RequestMethod.POST, RequestMethod.GET })
	public @ResponseBody ResponseEntity<BaseResp> checkPass(@RequestParam(required = true) String oid) {
		String operator = super.getLoginUser();
		liquidAssetService.check(oid, LiquidAsset.LIQUID_STATE_collecting, operator);
		return new ResponseEntity<BaseResp>(new BaseResp(), HttpStatus.OK);
	}

	/**
	 * 预审驳回
	 * 
	 * @param oid
	 * @return
	 */
	@RequestMapping(name = "投资标的驳回", value = "/checkreject", method = { RequestMethod.POST, RequestMethod.GET })
	public @ResponseBody ResponseEntity<BaseResp> checkReject(@RequestParam(required = true) String oid,
			String suggest) {
		String operator = super.getLoginUser();
		liquidAssetService.check(oid, LiquidAsset.LIQUID_STATE_reject, operator);
		return new ResponseEntity<BaseResp>(new BaseResp(), HttpStatus.OK);
	}

	/**
	 * 编辑标的资产
	 * 
	 * @param form
	 * @return
	 */
	@RequestMapping(name = "编辑现金管理工具", value = "/edit", method = { RequestMethod.POST, RequestMethod.GET })
	public @ResponseBody ResponseEntity<BaseResp> edit(@Valid LiquidAssetForm form) {
		String operator = super.getLoginUser();
		LiquidAsset temp = liquidAssetService.editInvestment(form);
		temp.setOperator(operator);
		liquidAssetService.save(temp);
		return new ResponseEntity<BaseResp>(new BaseResp(), HttpStatus.OK);
	}
	
	/**
	 * 现金类标的备选库查询
	 * @param request
	 * @param op
	 * @param spec
	 * @param page
	 * @param rows
	 * @param sortDirection
	 * @param sortField
	 * @return
	 */
	@RequestMapping(name = "现金类标的备选库查询", value = "/storageList", method = { RequestMethod.POST, RequestMethod.GET })
	public @ResponseBody ResponseEntity<LiquidAssetListResp> list(HttpServletRequest request,
			@RequestParam() final String op,
			@And({	
				@Spec(params = "name", path = "name", spec = Like.class),
				@Spec(params = "sn", path = "sn", spec = Like.class), 
				@Spec(params = "type", path = "type", spec = Equal.class) 
			}) Specification<LiquidAsset> spec,
			@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "50") int rows, @RequestParam(defaultValue = "desc") String sortDirection,
			@RequestParam(defaultValue = "updateTime") String sortField) {
		if (page < 1) {
			page = 1;
		}
		if (rows <= 0) {
			rows = 50;
		}
		Order order = new Order(Direction.valueOf(sortDirection.toUpperCase()), sortField);
		Pageable pageable = new PageRequest(page - 1, rows, new Sort(order));

		// 拼接条件
		spec = Specifications.where(spec).and(new Specification<LiquidAsset>() {
			@Override
			public Predicate toPredicate(Root<LiquidAsset> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicate = new ArrayList<>();				
				if (op.equals("storageList")) { // 现金管理工具库列表
					Expression<String> exp = root.get("state");
//					In<String> in = cb.in(exp);
//					in.in(Arrays.asList(CashTool.CASHTOOL_STATE_delete, CashTool.CASHTOOL_STATE_invalid));
//					predicate.add(cb.not(in));
					
					predicate.add(cb.equal(exp,  LiquidAsset.LIQUID_STATE_collecting));
				} else if (op.equals("historyList")) { // 历史列表
					Expression<String> exp = root.get("state");					
					predicate.add(exp.in(new Object[] { LiquidAsset.LIQUID_STATE_invalid }));
				} else{
					throw AMPException.getException("未知的操作类型[" + op + "]"); 
				}
				Predicate[] pre = new Predicate[predicate.size()];
				return query.where(predicate.toArray(pre)).getRestriction();
				
			}
		});

		// 最新流通份额
		final String circulationShares = request.getParameter("circulationShares");
		if (StringUtils.isNotBlank(circulationShares)) {
			spec = Specifications.where(spec).and(new Specification<LiquidAsset>() {
				@Override
				public Predicate toPredicate(Root<LiquidAsset> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
					Section<LiquidAsset> section = new Section<LiquidAsset>(circulationShares);
					return section.build(root, cb, "circulationShares");
				}
			});
		}
		
		// 持有份额
		final String holdAmount = request.getParameter("holdAmount");
		if (StringUtils.isNotBlank(holdAmount)) {
			spec = Specifications.where(spec).and(new Specification<LiquidAsset>() {
				@Override
				public Predicate toPredicate(Root<LiquidAsset> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
					Section<LiquidAsset> section = new Section<LiquidAsset>(holdAmount);
					return section.build(root, cb, "holdAmount");
				}
			});
		}
		
		// 7日年化收益率
		final String weeklyYield = request.getParameter("weeklyYield");
		if (StringUtils.isNotBlank(weeklyYield)) {
			spec = Specifications.where(spec).and(new Specification<LiquidAsset>() {
				@Override
				public Predicate toPredicate(Root<LiquidAsset> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
					Section<LiquidAsset> section = new Section<LiquidAsset>(weeklyYield);
					return section.build(root, cb, "weeklyYield");
				}
			});
		}

		Page<LiquidAsset> pageData = liquidAssetService.getLiquidAssetList(spec, pageable);

		LiquidAssetListResp resp = new LiquidAssetListResp(pageData);
		return new ResponseEntity<LiquidAssetListResp>(resp, HttpStatus.OK);
	}
	
	/**
	 * 现金类标的移除出库
	 * @param oid
	 * @return
	 */
	@RequestMapping(name = "现金类标的移除出库", value = "/remove", method = { RequestMethod.POST, RequestMethod.GET })
	public BaseResp remove(String oid) {
		log.debug("现金类标的移除出库接口!!!");
		String loginId = super.getLoginUser();
		log.debug("获取操作员id:" + loginId);
		this.liquidAssetService.remove(oid, loginId);
		return new BaseResp();
	}

	
}
