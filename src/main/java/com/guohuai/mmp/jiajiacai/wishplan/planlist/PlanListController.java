package com.guohuai.mmp.jiajiacai.wishplan.planlist;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.guohuai.mmp.jiajiacai.common.exception.BaseException;
import com.guohuai.mmp.jiajiacai.common.exception.ErrorMessage;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value="心愿计划 - 计划列表")
@RestController
@EnableAutoConfiguration
@RequestMapping(value = "/mimosa/wishplan/planlist", produces = "application/json")
public class PlanListController {
	@Autowired
	private PlanListService planService;
	
	@ApiOperation(value="查询所有的心愿计划列表", notes="查询所有的心愿计划列表 ")
	@RequestMapping(value = "getPlanList", method ={RequestMethod.POST})
	@ResponseBody
	public ResponseEntity<List<PlanListEntity>> getPlanList() {
		List<PlanListEntity> result =planService.findAll(); 
		return new ResponseEntity<List<PlanListEntity>>(result, HttpStatus.OK); 
	}
	
	@ApiOperation(value="根据ID查询心愿计划", notes="根据ID查询心愿计划 ")
	@RequestMapping(value = "getPlanEntity", method ={RequestMethod.POST})
	@ResponseBody
	public ResponseEntity<PlanListEntity> getPlanEntity(@NotNull @RequestParam("oid") String oid) throws BaseException {
		PlanListEntity plan =planService.getEntityByOid(oid);
		if(plan == null) {
			throw new BaseException(ErrorMessage.PLAN_NOT_EXIST);
		}
		return new ResponseEntity<PlanListEntity>(plan, HttpStatus.OK); 
	}
	
}
