package com.guohuai.ams.portfolio.chargeFee;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.guohuai.ams.portfolio.common.ConstantUtil;
import com.guohuai.basic.component.ext.web.BaseController;
import com.guohuai.basic.component.ext.web.PageResp;
import com.guohuai.component.web.view.Response;

import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.Like;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;

/**
 * 费金接口
 * @author star.zhu
 * 2016年12月26日
 */
@RestController
@RequestMapping(value = "/mimosa/portfolio/chargeFee", produces = "application/json;charset=utf-8")
public class ChargeFeeController extends BaseController {

	@Autowired
	private ChargeFeeService chargeFeeService;
	
	/**
	 * 获取费金累计列表，支持模糊查询
	 * @return
	 */
	@RequestMapping(value = "/getCountList", name = "费金管理 - 获取费金累计列表", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<PageResp<ChargeFeeForm>> getCountList(
			@And({
                @Spec(params = "name", path = "name", spec = Like.class),
                @Spec(params = "state", path = "state", spec = Equal.class),
                @Spec(path = "classify", constVal = ConstantUtil.fee_count, spec = Equal.class)
            })Specification<ChargeFeeEntity> spec,
			@RequestParam(required = false, defaultValue = "1") int page,
			@RequestParam(required = false, defaultValue = "10") int rows,
			@RequestParam(required = false, defaultValue = "createTime") String sortField,
			@RequestParam(required = false, defaultValue = "desc") String sort) {
		Direction sortDirection = Direction.DESC;
		if (!"desc".equals(sort)) {
			sortDirection = Direction.ASC;
		}
		Pageable pageable = new PageRequest(page - 1, rows, new Sort(new Order(sortDirection, sortField)));
		PageResp<ChargeFeeForm> rep = chargeFeeService.getListByParams(spec, pageable);
        return new ResponseEntity<PageResp<ChargeFeeForm>>(rep, HttpStatus.OK);
	}
	
	/**
	 * 获取费金计提列表，支持模糊查询
	 * @return
	 */
	@RequestMapping(value = "/getDrawList", name = "费金管理 - 获取费金计提列表", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<PageResp<ChargeFeeForm>> getDrawList(
			@And({
                @Spec(params = "name", path = "name", spec = Like.class),
                @Spec(params = "state", path = "state", spec = Equal.class),
                @Spec(path = "classify", constVal = ConstantUtil.fee_draw, spec = Equal.class)
            })Specification<ChargeFeeEntity> spec,
			@RequestParam(required = false, defaultValue = "1") int page,
			@RequestParam(required = false, defaultValue = "10") int rows,
			@RequestParam(required = false, defaultValue = "createTime") String sortField,
			@RequestParam(required = false, defaultValue = "desc") String sort) {
		Direction sortDirection = Direction.DESC;
		if (!"desc".equals(sort)) {
			sortDirection = Direction.ASC;
		}
		Pageable pageable = new PageRequest(page - 1, rows, new Sort(new Order(sortDirection, sortField)));
		PageResp<ChargeFeeForm> rep = chargeFeeService.getListByParams(spec, pageable);
        return new ResponseEntity<PageResp<ChargeFeeForm>>(rep, HttpStatus.OK);
	}
	
	/**
	 * 费金计提
	 * @return
	 */
	@RequestMapping(value = "/drawingFee", name = "费金管理 - 费金计提", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<Response> drawingFee(ChargeFeeForm form) {
		String operator = super.getLoginUser();
		chargeFeeService.drawingFee(form, operator);
		Response r = new Response();
        r.with("result", "SUCCESS!");
        return new ResponseEntity<Response>(r, HttpStatus.OK);
	}
}
