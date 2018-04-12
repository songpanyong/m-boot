package com.guohuai.ams.liquidAsset.yield;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.guohuai.ams.liquidAsset.LiquidAssetDao;
import com.guohuai.ams.liquidAsset.LiquidAssetService;
import com.guohuai.ams.liquidAsset.log.LiquidAssetLogService;
import com.guohuai.basic.component.ext.web.BaseController;
import com.guohuai.basic.component.ext.web.BaseResp;

import lombok.extern.slf4j.Slf4j;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;

/**
 * 收益采集接口
 * @author zudafu
 *
 */
@RestController
@RequestMapping(value = "/mimosa/boot/liquidAssetYield", produces = "application/json;charset=UTF-8")
@Slf4j
public class LiquidAssetYieldController extends BaseController{
	@Autowired
	LiquidAssetYieldDao liquidAssetYieldDao;
	@Autowired
	LiquidAssetYieldService liquidAssetYieldService;
	@Autowired
	LiquidAssetService liquidAssetService;
	@Autowired
	LiquidAssetDao liquidAssetDao;
	@Autowired
	LiquidAssetLogService liquidAssetLogService;
	
	/**
	 * 现金标的收益管理
	 * @param request
	 * @param spec
	 * @param page
	 * @param rows
	 * @param sortDirection
	 * @param sortField
	 * @return
	 */
	@RequestMapping(name = "现金标的收益管理", value = "/yieldlist", method = { RequestMethod.POST, RequestMethod.GET })
	public @ResponseBody ResponseEntity<LiquidAssetYieldListResp> listLiquidAssetYield(HttpServletRequest request,
			@And({	
				@Spec(params = "liquidAssetOid", path = "liquidAsset.oid", spec = Equal.class) 
			}) Specification<LiquidAssetYield> spec,
			@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "50") int rows, @RequestParam(defaultValue = "desc") String sortDirection,
			@RequestParam(defaultValue = "dailyProfitDate") String sortField) {
		if (page < 1) {
			page = 1;
		}
		if (rows <= 0) {
			rows = 50;
		}
		Order order = new Order(Direction.valueOf(sortDirection.toUpperCase()), sortField);
		Pageable pageable = new PageRequest(page - 1, rows, new Sort(order));

		Page<LiquidAssetYield> pageData = liquidAssetYieldService.getLiquidAssetList(spec, pageable);

		LiquidAssetYieldListResp resp = new LiquidAssetYieldListResp(pageData);
		return new ResponseEntity<LiquidAssetYieldListResp>(resp, HttpStatus.OK);
	}
	
	/**
	 * 现金类资产收益采集
	 * @param form
	 * @return
	 */
	@RequestMapping(name = "现金类资产收益采集", value = "/yieldSave", method = { RequestMethod.POST, RequestMethod.GET })
	public BaseResp yieldSave(@Valid @RequestBody LiquidAssetYieldForm form) {
		String loginId = null;
		try {
			loginId = super.getLoginUser();
		} catch (Exception e) {
			log.error("获取操作员失败, 原因: " + e.getMessage());
		}
		liquidAssetYieldService.liquidAssetYield(form, loginId);
		return new BaseResp();
	}
	
	/**
	 * 现金类管理收益列表
	 * @param request
	 * @param spec
	 * @param page
	 * @param rows
	 * @param sortDirection
	 * @param sortField
	 * @return
	 */
	@RequestMapping(name = "现金类标的收益列表", value = "/listYield", method = { RequestMethod.POST, RequestMethod.GET })
	public @ResponseBody ResponseEntity<LiquidAssetYieldListResp> liquidAssetYieldList(HttpServletRequest request,
			@And({	
				@Spec(params = "liquidAssetOid", path = "liquidAsset.oid", spec = Equal.class) 
			}) Specification<LiquidAssetYield> spec,
			@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "50") int rows, @RequestParam(defaultValue = "desc") String sortDirection,
			@RequestParam(defaultValue = "profitDate") String sortField) {
		if (page < 1) {
			page = 1;
		}
		if (rows <= 0) {
			rows = 50;
		}
		Order order = new Order(Direction.valueOf(sortDirection.toUpperCase()), sortField);
		Pageable pageable = new PageRequest(page - 1, rows, new Sort(order));

		Page<LiquidAssetYield> pageData = liquidAssetYieldService.getLiquidAssetYieldList(spec, pageable);

		LiquidAssetYieldListResp resp = new LiquidAssetYieldListResp(pageData);
		return new ResponseEntity<LiquidAssetYieldListResp>(resp, HttpStatus.OK);
	}
	
	/**
	 * 货币基金收益采集日期校验
	 * @param oid
	 * @return
	 */
	@RequestMapping(name = "收益开始和截止日期校验", value = "/dateVerify", method = { RequestMethod.POST, RequestMethod.GET })
	public @ResponseBody JSONObject dateVerify(@RequestParam String oid) {
		return this.liquidAssetYieldService.dateVerify(oid);
	}
}
