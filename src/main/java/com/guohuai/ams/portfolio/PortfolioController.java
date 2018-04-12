package com.guohuai.ams.portfolio;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

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
import com.guohuai.ams.portfolio.common.ConstantUtil;
import com.guohuai.ams.portfolio.entity.AdjustEntity;
import com.guohuai.ams.portfolio.entity.IncomeEntity;
import com.guohuai.ams.portfolio.entity.NetValueEntity;
import com.guohuai.ams.portfolio.entity.PortfolioEntity;
import com.guohuai.ams.portfolio.form.NetValueForm;
import com.guohuai.ams.portfolio.form.PortfolioForm;
import com.guohuai.ams.portfolio.form.PortfolioListResp;
import com.guohuai.ams.portfolio.form.PortfolioResp;
import com.guohuai.ams.portfolio.form.PortfolioStatisticsResp;
import com.guohuai.ams.portfolio.holdAsset.illiquidAsset.IlliquidHoldEntity;
import com.guohuai.ams.portfolio.holdAsset.illiquidAsset.IlliquidHoldService;
import com.guohuai.ams.portfolio.holdAsset.liquidAsset.LiquidHoldEntity;
import com.guohuai.ams.portfolio.holdAsset.liquidAsset.LiquidHoldService;
import com.guohuai.ams.portfolio.service.AdjustService;
import com.guohuai.ams.portfolio.service.IncomeService;
import com.guohuai.ams.portfolio.service.NetValueService;
import com.guohuai.ams.portfolio.service.PortfolioService;
import com.guohuai.ams.portfolio.trade.AssetTradeEntity;
import com.guohuai.ams.portfolio.trade.AssetTradeForm;
import com.guohuai.ams.portfolio.trade.AssetTradeService;
import com.guohuai.ams.portfolio.trade.illiquidAsset.IlliquidTradeService;
import com.guohuai.ams.portfolio.trade.liquidAsset.LiquidTradeService;
import com.guohuai.basic.component.ext.web.BaseController;
import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.basic.component.ext.web.PageResp;
import com.guohuai.component.util.DateUtil;
import com.guohuai.component.web.view.Response;
import com.guohuai.mmp.publisher.baseaccount.PublisherBaseAccountService;

import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.Like;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;

/**
 * 存续期--投资组合操作入口
 * 
 * @author star.zhu 2016年12月27日
 */
@RestController
@RequestMapping(value = "/mimosa/portfolioManage/portfolio", produces = "application/json;charset=utf-8")
public class PortfolioController extends BaseController {

	@Autowired
	private PortfolioService portfolioService;
	@Autowired
	private AdjustService adjustService;
	@Autowired
	private NetValueService netValueService;
	@Autowired
	private IncomeService incomeService;
	@Autowired
	private PublisherBaseAccountService spvService;
	@Autowired
	private LiquidHoldService liquidHoldService;
	@Autowired
	private IlliquidHoldService illiquidHoldService;
	@Autowired
	private LiquidTradeService liquidTradeService;
	@Autowired
	private IlliquidTradeService illiquidTradeService;
	@Autowired
	private AssetTradeService assetTradeService;

	/**
	 * 获取spv列表
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getAllSPV", name = "投资组合 - 获取spv列表", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<Response> getAllSPV() {
		List<JSONObject> objList = spvService.getAllSPV();
		Response r = new Response();
		r.with("result", objList);
		return new ResponseEntity<Response>(r, HttpStatus.OK);
	}

	/**
	 * 新建投资组合
	 * 
	 * @param form
	 * @return
	 */
	@RequestMapping(value = "/createPortfolio", name = "投资组合 - 新建", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<Response> createPortfolio(PortfolioForm form) {
		portfolioService.createPortfolio(form, super.getLoginUser());
		Response r = new Response();
		r.with("result", "SUCCESSED!");
		return new ResponseEntity<Response>(r, HttpStatus.OK);
	}

	/**
	 * 新建审核
	 * 
	 * @param operation
	 *            操作：yes（通过）；no（不通过）
	 * @param oid
	 * @return
	 */
	@RequestMapping(value = "/auditPortfolio", name = "投资组合 - 新建审核", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<Response> auditPortfolio(@RequestParam String oid, @RequestParam String operate, @RequestParam String auditMark) {
		portfolioService.auditPortfolio(oid, super.getLoginUser(), operate, auditMark);
		Response r = new Response();
		r.with("result", "SUCCESSED!");
		return new ResponseEntity<Response>(r, HttpStatus.OK);
	}

	/**
	 * 获取所有的投资组合列表，支持模糊查询
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getListByParams", name = "投资组合 - 获取所有投资组合列表", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<PortfolioListResp> getListByParams(@And({ @Spec(params = "name", path = "name", spec = Like.class), @Spec(params = "state", path = "state", spec = Equal.class) }) Specification<PortfolioEntity> spec, @RequestParam(required = false, defaultValue = "1") int page, @RequestParam(required = false, defaultValue = "10") int rows, @RequestParam(required = false, defaultValue = "createTime") String sortField,
			@RequestParam(required = false, defaultValue = "desc") String sort) {
		Direction sortDirection = Direction.DESC;
		if (!"desc".equals(sort)) {
			sortDirection = Direction.ASC;
		}
		Pageable pageable = new PageRequest(page - 1, rows, new Sort(new Order(sortDirection, sortField)));
		PortfolioListResp reps = portfolioService.getAuditListByParams(spec, pageable);
		return new ResponseEntity<PortfolioListResp>(reps, HttpStatus.OK);
	}

	/**
	 * 获取所有的投资组合列表，支持模糊查询
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getListByPass", name = "投资组合 - 获取通过投资组合列表", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<PageResp<PortfolioEntity>> getListByPass(
			@And({ @Spec(params = "name", path = "name", spec = Like.class), 
				@Spec(params = "state", path = "state", spec = Equal.class) }) Specification<PortfolioEntity> spec, 
			@RequestParam(required = false, defaultValue = "1") int page, 
			@RequestParam(required = false, defaultValue = "10") int rows, 
			@RequestParam(required = false, defaultValue = "createTime") String sortField,
			@RequestParam(required = false, defaultValue = "desc") String sort) {
		Direction sortDirection = Direction.DESC;
		if (!"desc".equals(sort)) {
			sortDirection = Direction.ASC;
		}
		Specification<PortfolioEntity> stateSpec = new Specification<PortfolioEntity>() {
			@Override
			public Predicate toPredicate(Root<PortfolioEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.equal(root.get("state"), PortfolioEntity.PORTFOLIO_STATE_duration);
			}
		};
		spec = Specifications.where(spec).and(stateSpec);
		Pageable pageable = new PageRequest(page - 1, rows, new Sort(new Order(sortDirection, sortField)));
		PageResp<PortfolioEntity> rep = portfolioService.getListByParams(spec, pageable);
		return new ResponseEntity<PageResp<PortfolioEntity>>(rep, HttpStatus.OK);
	}

	/**
	 * 根据oid获取投资组合的详细信息
	 * 
	 * @param oid
	 * @return
	 */
	@RequestMapping(value = "/getPortfolioByOid", name = "投资组合 - 根据oid查询投资组合", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<Response> getPortfolioByOid(@RequestParam String oid) {
		PortfolioResp resp = portfolioService.getPortfolioByOid(oid);
		Response r = new Response();
		r.with("result", resp);
		return new ResponseEntity<Response>(r, HttpStatus.OK);
	}

	/**
	 * 编辑投资组合
	 * 
	 * @param form
	 * @return
	 */
	@RequestMapping(value = "/editPortfolio", name = "投资组合 - 编辑", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<Response> editPortfolio(PortfolioForm form) {
		portfolioService.editPortfolio(form, super.getLoginUser());
		Response r = new Response();
		r.with("result", "SUCCESSED!");
		return new ResponseEntity<Response>(r, HttpStatus.OK);
	}

	/**
	 * 逻辑删除投资组合
	 * 
	 * @param pid
	 * @return
	 */
	@RequestMapping(value = "/updatePortfolio", name = "投资组合 - 逻辑删除", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<Response> updatePortfolio(@RequestParam String pid) {
		portfolioService.updatePortfolio(pid);
		Response r = new Response();
		r.with("result", "SUCCESSED!");
		return new ResponseEntity<Response>(r, HttpStatus.OK);
	}

	/**
	 * 物理删除投资组合
	 * 
	 * @param pid
	 * @return
	 */
	@RequestMapping(value = "/deletePortfolio", name = "投资组合 - 物理删除", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<Response> deletePortfolio(@RequestParam String pid) {
		portfolioService.deletePortfolio(pid);
		Response r = new Response();
		r.with("result", "SUCCESSED!");
		return new ResponseEntity<Response>(r, HttpStatus.OK);
	}

	/**
	 * 投资组合提交审核
	 * 
	 * @param oid
	 * @return
	 */
	@RequestMapping(name = "投资组合提交审核", value = "/examine", method = { RequestMethod.POST, RequestMethod.GET })
	public @ResponseBody ResponseEntity<BaseResp> examine(@RequestParam(required = true) String oid) {
		String operator = super.getLoginUser();
		PortfolioEntity entity = portfolioService.getByOid(oid);
		if (!PortfolioEntity.PORTFOLIO_STATE_create.equals(entity.getState()) && !PortfolioEntity.PORTFOLIO_STATE_reject.equals(entity.getState())) {
			// 标的状态不是待审核或驳回不能提交预审
			throw new RuntimeException();
		}
		entity.setState(PortfolioEntity.PORTFOLIO_STATE_pretrial);
		entity.setOperator(operator);
		entity.setUpdateTime(DateUtil.getSqlCurrentDate());
		portfolioService.save(entity);
		return new ResponseEntity<BaseResp>(new BaseResp(), HttpStatus.OK);
	}

	/**
	 * 投资组合撤销审核
	 * 
	 * @param oid
	 * @return
	 */
	@RequestMapping(name = "投资组合撤销审核", value = "/cancel", method = { RequestMethod.POST, RequestMethod.GET })
	public @ResponseBody ResponseEntity<BaseResp> cancel(@RequestParam(required = true) String oid) {
		String operator = super.getLoginUser();
		PortfolioEntity entity = portfolioService.getByOid(oid);
		if (!PortfolioEntity.PORTFOLIO_STATE_pretrial.equals(entity.getState())) {
			// 标的状态不是审核中不能撤销审核
			throw new RuntimeException();
		}
		entity.setState(PortfolioEntity.PORTFOLIO_STATE_create);
		entity.setOperator(operator);
		entity.setUpdateTime(DateUtil.getSqlCurrentDate());
		portfolioService.save(entity);
		return new ResponseEntity<BaseResp>(new BaseResp(), HttpStatus.OK);
	}

	/**
	 * 获取所有投资组合的名称列表，包含id
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getAllNameList", name = "投资组合 - 获取所有投资组合json列表，包含id和name", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<Response> getAllNameList() {
		List<JSONObject> jsonList = portfolioService.getAllNameList();
		Response r = new Response();
		r.with("rows", jsonList);
		return new ResponseEntity<Response>(r, HttpStatus.OK);
	}

	/**
	 * 净值校准
	 * 
	 * @param form
	 */
	@RequestMapping(value = "/updateNetValue", name = "投资组合 - 净值校准", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<Response> updateNetValue(NetValueForm form) {
		netValueService.create(form, super.getLoginUser());
		Response r = new Response();
		r.with("result", "SUCCESS");
		return new ResponseEntity<Response>(r, HttpStatus.OK);
	}

	/**
	 * 获取所有的投资组合净值校准列表
	 * 
	 * @param page
	 * @param rows
	 * @param sortField
	 * @param sort
	 * @return
	 */
	@RequestMapping(value = "/getNetValueListByParams", name = "投资组合 - 获取所有投资组合净值校准列表", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<PageResp<NetValueEntity>> getNetValueListByParams(@And({ @Spec(params = "pid", path = "portfolio.portfolioOid", spec = Equal.class) }) Specification<NetValueEntity> spec, @RequestParam(required = false, defaultValue = "1") int page, @RequestParam(required = false, defaultValue = "10") int rows, @RequestParam(required = false, defaultValue = "createTime") String sortField,
			@RequestParam(required = false, defaultValue = "desc") String sort) {
		Direction sortDirection = Direction.DESC;
		if (!"desc".equals(sort)) {
			sortDirection = Direction.ASC;
		}
		Pageable pageable = new PageRequest(page - 1, rows, new Sort(new Order(sortDirection, sortField)));
		PageResp<NetValueEntity> rep = netValueService.getListByParams(spec, pageable);
		return new ResponseEntity<PageResp<NetValueEntity>>(rep, HttpStatus.OK);
	}

	/**
	 * 获取净值校准详情
	 * 
	 * @param oid
	 * @return
	 */
	@RequestMapping(value = "/getNetValueInfo", name = "投资组合 - 获取净值校准详情", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<Response> getNetValueInfo(@RequestParam String oid) {
		NetValueForm form = netValueService.getNetValueInfo(oid);
		Response r = new Response();
		r.with("result", form);
		return new ResponseEntity<Response>(r, HttpStatus.OK);
	}

	/**
	 * 获取所有的投资组合收益分配列表
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getIncomeList", name = "投资组合 - 获取投资组合收益分配列表", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<PageResp<IncomeEntity>> getIncomeList(@And({ @Spec(params = "pid", path = "portfolio.portfolioOid", spec = Equal.class) }) Specification<IncomeEntity> spec, @RequestParam(required = false, defaultValue = "1") int page, @RequestParam(required = false, defaultValue = "10") int rows, @RequestParam(required = false, defaultValue = "createTime") String sortField, @RequestParam(required = false, defaultValue = "desc") String sort) {
		Direction sortDirection = Direction.DESC;
		if (!"desc".equals(sort)) {
			sortDirection = Direction.ASC;
		}
		Pageable pageable = new PageRequest(page - 1, rows, new Sort(new Order(sortDirection, sortField)));
		PageResp<IncomeEntity> rep = incomeService.getListByParams(spec, pageable);
		return new ResponseEntity<PageResp<IncomeEntity>>(rep, HttpStatus.OK);
	}

	/**
	 * 获取所有的投资组合持仓现金类资产列表
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getLiquidHoldList", name = "投资组合 - 获取所有投资组合持仓现金类资产列表", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<PageResp<LiquidHoldEntity>> getLiquidHoldList(@And({ @Spec(params = "oid", path = "portfolioOid", spec = Equal.class) }) Specification<LiquidHoldEntity> spec, @RequestParam(required = false, defaultValue = "1") int page, @RequestParam(required = false, defaultValue = "10") int rows, @RequestParam(required = false, defaultValue = "createTime") String sortField, @RequestParam(required = false, defaultValue = "desc") String sort) {
		Direction sortDirection = Direction.DESC;
		if (!"desc".equals(sort)) {
			sortDirection = Direction.ASC;
		}
		Pageable pageable = new PageRequest(page - 1, rows, new Sort(new Order(sortDirection, sortField)));
		PageResp<LiquidHoldEntity> rep = liquidHoldService.getLiquidHoldList(spec, pageable);
		return new ResponseEntity<PageResp<LiquidHoldEntity>>(rep, HttpStatus.OK);
	}

	/**
	 * 现金类资产净值校准
	 * 
	 * @param form
	 */
	@RequestMapping(value = "/updateLiquidValue", name = "投资组合 - 现金类资产净值校准", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<Response> updateLiquidValue(AssetTradeForm form) {
		liquidTradeService.calibration(form, super.getLoginUser());
		Response r = new Response();
		r.with("result", "SUCCESS");
		return new ResponseEntity<Response>(r, HttpStatus.OK);
	}

	/**
	 * 获取所有的投资组合持仓非现金类资产列表
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getIlliquidHoldList", name = "投资组合 - 获取所有投资组合持仓非现金类资产列表", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<PageResp<IlliquidHoldEntity>> getIlliquidHoldList(@And({ @Spec(params = "portfolioOid", path = "portfolioOid", spec = Equal.class) }) Specification<IlliquidHoldEntity> spec, @RequestParam(required = false, defaultValue = "1") int page, @RequestParam(required = false, defaultValue = "10") int rows, @RequestParam(required = false, defaultValue = "createTime") String sortField,
			@RequestParam(required = false, defaultValue = "desc") String sort) {
		Direction sortDirection = Direction.DESC;
		if (!"desc".equals(sort)) {
			sortDirection = Direction.ASC;
		}
		Pageable pageable = new PageRequest(page - 1, rows, new Sort(new Order(sortDirection, sortField)));
		PageResp<IlliquidHoldEntity> rep = illiquidHoldService.getIlliquidHoldList(spec, pageable);
		return new ResponseEntity<PageResp<IlliquidHoldEntity>>(rep, HttpStatus.OK);
	}

	/**
	 * 非现金类资产净值校准
	 * 
	 * @param form
	 */
	@RequestMapping(value = "/updateIlliquidValue", name = "投资组合 - 非现金类资产净值校准", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<Response> updateIlliquidValue(AssetTradeForm form) {
		illiquidTradeService.calibration(form, super.getLoginUser());
		Response r = new Response();
		r.with("result", "SUCCESS");
		return new ResponseEntity<Response>(r, HttpStatus.OK);
	}

	/**
	 * 更新投资组合的可用现金
	 * 
	 * @param form
	 */
	@RequestMapping(value = "/updateCash", name = "投资组合 - 更新投资组合的可用现金", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<Response> updateCash(@RequestParam String oid, @RequestParam BigDecimal amount) {
		adjustService.adjustCash(oid, super.getLoginUser(), amount);
		Response r = new Response();
		r.with("result", "SUCCESS");
		return new ResponseEntity<Response>(r, HttpStatus.OK);
	}

	/**
	 * 更新投资组合的偏离损益
	 * 
	 * @param form
	 */
	@RequestMapping(value = "/updateDeviationValue", name = "投资组合 - 更新投资组合的偏离损益", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<Response> updateDeviationValue(@RequestParam String oid, @RequestParam BigDecimal amount) {
		adjustService.adjustDeviation(oid, super.getLoginUser(), amount);
		Response r = new Response();
		r.with("result", "SUCCESS");
		return new ResponseEntity<Response>(r, HttpStatus.OK);
	}

	/**
	 * 获取待确认交易列表
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getConfirmList", name = "投资组合 - 获取待确认交易列表", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<PageResp<AssetTradeEntity>> getConfirmList(@And({ @Spec(params = "portfolioOid", path = "portfolio.oid", spec = Equal.class), @Spec(path = "state", constVal = ConstantUtil.trade_audit, spec = Equal.class) }) Specification<AssetTradeEntity> spec, @RequestParam(required = false, defaultValue = "1") int page, @RequestParam(required = false, defaultValue = "10") int rows,
			@RequestParam(required = false, defaultValue = "createTime") String sortField, @RequestParam(required = false, defaultValue = "desc") String sort) {
		Direction sortDirection = Direction.DESC;
		if (!"desc".equals(sort)) {
			sortDirection = Direction.ASC;
		}
		Pageable pageable = new PageRequest(page - 1, rows, new Sort(new Order(sortDirection, sortField)));
		PageResp<AssetTradeEntity> rep = assetTradeService.getConfrimList(spec, pageable);
		return new ResponseEntity<PageResp<AssetTradeEntity>>(rep, HttpStatus.OK);
	}

	/**
	 * 获取资产交易列表
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getTadeist", name = "投资组合 - 获取资产交易列表", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<PageResp<IlliquidHoldEntity>> getTadeist(@And({ @Spec(params = "portfolioOid", path = "portfolio.oid", spec = Equal.class) }) Specification<IlliquidHoldEntity> spec, @RequestParam(required = false, defaultValue = "1") int page, @RequestParam(required = false, defaultValue = "10") int rows, @RequestParam(required = false, defaultValue = "createTime") String sortField, @RequestParam(required = false, defaultValue = "desc") String sort) {
		Direction sortDirection = Direction.DESC;
		if (!"desc".equals(sort)) {
			sortDirection = Direction.ASC;
		}
		Pageable pageable = new PageRequest(page - 1, rows, new Sort(new Order(sortDirection, sortField)));
		PageResp<IlliquidHoldEntity> rep = illiquidHoldService.getIlliquidHoldList(spec, pageable);
		return new ResponseEntity<PageResp<IlliquidHoldEntity>>(rep, HttpStatus.OK);
	}

	/**
	 * 获取投资损益列表
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getDeviationList", name = "投资组合 - 获取投资损益列表", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<PageResp<AssetTradeEntity>> getDeviationList(@RequestParam String portfolioOid, @RequestParam(required = false, defaultValue = "1") int page, @RequestParam(required = false, defaultValue = "10") int rows, @RequestParam(required = false, defaultValue = "createTime") String sortField, @RequestParam(required = false, defaultValue = "desc") String sort) {
		Direction sortDirection = Direction.DESC;
		if (!"desc".equals(sort)) {
			sortDirection = Direction.ASC;
		}
		Specification<AssetTradeEntity> spec = new Specification<AssetTradeEntity>() {

			@Override
			public Predicate toPredicate(Root<AssetTradeEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				// TODO Auto-generated method stub
				return cb.and(cb.equal(root.get("portfolio.oid").as(String.class), portfolioOid), cb.equal(root.get("state"), ConstantUtil.state_duration), cb.or(cb.equal(root.get("tradeType"), ConstantUtil.trade_redeem), cb.equal(root.get("tradeType"), ConstantUtil.trade_transOut)));
			}
		};
		Pageable pageable = new PageRequest(page - 1, rows, new Sort(new Order(sortDirection, sortField)));
		PageResp<AssetTradeEntity> rep = assetTradeService.getConfrimList(spec, pageable);
		return new ResponseEntity<PageResp<AssetTradeEntity>>(rep, HttpStatus.OK);
	}

	/**
	 * 获取现金校准列表
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getAdjustCashList", name = "投资组合 - 获取现金校准列表", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<PageResp<AdjustEntity>> getAdjustCashList(@And({ @Spec(params = "portfolioOid", path = "portfolio.oid", spec = Equal.class), @Spec(path = "type", constVal = ConstantUtil.adjust_cash, spec = Equal.class) }) Specification<AdjustEntity> spec, @RequestParam(required = false, defaultValue = "1") int page, @RequestParam(required = false, defaultValue = "10") int rows,
			@RequestParam(required = false, defaultValue = "createTime") String sortField, @RequestParam(required = false, defaultValue = "desc") String sort) {
		Direction sortDirection = Direction.DESC;
		if (!"desc".equals(sort)) {
			sortDirection = Direction.ASC;
		}
		Pageable pageable = new PageRequest(page - 1, rows, new Sort(new Order(sortDirection, sortField)));
		PageResp<AdjustEntity> rep = adjustService.getListByParams(spec, pageable);
		return new ResponseEntity<PageResp<AdjustEntity>>(rep, HttpStatus.OK);
	}

	/**
	 * 获取现金校准列表
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getAdjustDeviationList", name = "投资组合 - 获取现金校准列表", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<PageResp<AdjustEntity>> getAdjustDeviationList(@And({ @Spec(params = "portfolioOid", path = "portfolio.oid", spec = Equal.class), @Spec(path = "type", constVal = ConstantUtil.adjust_deviation, spec = Equal.class) }) Specification<AdjustEntity> spec, @RequestParam(required = false, defaultValue = "1") int page, @RequestParam(required = false, defaultValue = "10") int rows,
			@RequestParam(required = false, defaultValue = "createTime") String sortField, @RequestParam(required = false, defaultValue = "desc") String sort) {
		Direction sortDirection = Direction.DESC;
		if (!"desc".equals(sort)) {
			sortDirection = Direction.ASC;
		}
		Pageable pageable = new PageRequest(page - 1, rows, new Sort(new Order(sortDirection, sortField)));
		PageResp<AdjustEntity> rep = adjustService.getListByParams(spec, pageable);
		return new ResponseEntity<PageResp<AdjustEntity>>(rep, HttpStatus.OK);
	}

	/**
	 * 资产交易
	 * 
	 * @param form
	 */
	@RequestMapping(value = "/assetTrade", name = "投资组合 - 资产交易", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<Response> assetPurchase(AssetTradeForm form) {
		assetTradeService.assetTrade(form, super.getLoginUser());
		Response r = new Response();
		r.with("result", "SUCCESS");
		return new ResponseEntity<Response>(r, HttpStatus.OK);
	}

	/**
	 * 统计数据
	 * @param oid
	 * @return
	 */
	@RequestMapping(value = "/statistics", name = "投资组合 - 统计数据", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ResponseEntity<PortfolioStatisticsResp> statistics(@RequestParam String oid) {
		PortfolioStatisticsResp r = this.portfolioService.statisticsQuery(oid);
		return new ResponseEntity<PortfolioStatisticsResp>(r, HttpStatus.OK);
	}

}
