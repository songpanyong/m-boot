package com.guohuai.ams.switchcraft;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

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

import com.guohuai.ams.switchcraft.black.SwitchBlackEntity;
import com.guohuai.ams.switchcraft.black.SwitchBlackQueryRep;
import com.guohuai.ams.switchcraft.black.SwitchBlackService;
import com.guohuai.ams.switchcraft.white.SwitchWhiteEntity;
import com.guohuai.ams.switchcraft.white.SwitchWhiteQueryRep;
import com.guohuai.ams.switchcraft.white.SwitchWhiteService;
import com.guohuai.basic.component.ext.web.BaseController;
import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.basic.component.ext.web.PageResp;

import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.In;
import net.kaczmarzyk.spring.data.jpa.domain.Like;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;

@RestController
@RequestMapping(value = "/mimosa/boot/switch", produces = "application/json")
public class SwitchBootController extends BaseController{

	@Autowired
	private SwitchService switchService;
	@Autowired
	private SwitchWhiteService switchWhiteService;
	@Autowired
	private SwitchBlackService switchBlackService;
	
	@RequestMapping(value = "/query", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<PageResp<SwitchQueryRep>> query(HttpServletRequest request,
		@And({  @Spec(params = "code", path = "code", spec = Like.class),
				@Spec(params = "name", path = "name", spec = Like.class),
			    @Spec(params = "status", path = "status", spec = In.class),
			    @Spec(params = "whiteStatus", path = "whiteStatus", spec = In.class)}) 
         		Specification<SwitchEntity> spec,
 		@RequestParam int page, 
		@RequestParam int rows) {
		
		Pageable pageable = new PageRequest(page - 1, rows, new Sort(new Order(Direction.DESC, "createTime")));		
		PageResp<SwitchQueryRep> rep = this.switchService.query(spec, pageable);
		
		return new ResponseEntity<PageResp<SwitchQueryRep>>(rep, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/querywhite", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<PageResp<SwitchWhiteQueryRep>> queryWhite(HttpServletRequest request,
		@And({@Spec(params = "switchOid", path = "switchOid", spec = Equal.class),
				@Spec(params = "userAcc", path = "userAcc", spec = Like.class)}) 
         		Specification<SwitchWhiteEntity> spec,
 		@RequestParam int page, 
		@RequestParam int rows) {
		
		Pageable pageable = new PageRequest(page - 1, rows, new Sort(new Order(Direction.DESC, "createTime")));		
		PageResp<SwitchWhiteQueryRep> rep = this.switchWhiteService.query(spec, pageable);
		
		return new ResponseEntity<PageResp<SwitchWhiteQueryRep>>(rep, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/queryblack", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<PageResp<SwitchBlackQueryRep>> queryBlack(HttpServletRequest request,
		@And({@Spec(params = "switchOid", path = "switchOid", spec = Equal.class),
				@Spec(params = "userAcc", path = "userAcc", spec = Like.class)}) 
         		Specification<SwitchBlackEntity> spec,
 		@RequestParam int page, 
		@RequestParam int rows) {
		
		Pageable pageable = new PageRequest(page - 1, rows, new Sort(new Order(Direction.DESC, "createTime")));		
		PageResp<SwitchBlackQueryRep> rep = this.switchBlackService.query(spec, pageable);
		
		return new ResponseEntity<PageResp<SwitchBlackQueryRep>>(rep, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<BaseResp> add(@Valid SwitchAddReq req) {
		String operator = super.getLoginUser();
		BaseResp rep = this.switchService.add(req, operator);
		
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<BaseResp> update(@Valid SwitchAddReq req) {
		String operator = super.getLoginUser();
		BaseResp rep = this.switchService.add(req, operator);
		
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}

	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<BaseResp> delete(@RequestParam String oid) {
		BaseResp rep = new BaseResp();
		this.switchService.del(oid);
		
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/delwhite", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<BaseResp> delwhite(@RequestParam String oid) {
		BaseResp rep = new BaseResp();
		this.switchWhiteService.del(oid);
		
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/delblack", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<BaseResp> delblack(@RequestParam String oid) {
		BaseResp rep = new BaseResp();
		this.switchBlackService.del(oid);
		
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}

	/**
	 * 审核Banner 通过/驳回
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/dealapprove", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<BaseResp> dealApprove(@Valid SwitchApproveReq req) {
		String operator = super.getLoginUser();
		BaseResp rep = this.switchService.dealApprove(req, operator);
				
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
	/**
	 * 停用
	 * @param oid
	 * @return
	 */
	@RequestMapping(value = "/disable", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<BaseResp> disable(@RequestParam String oid) {
		BaseResp rep = new BaseResp();
		this.switchService.enable(oid, SwitchEntity.SWITCH_Status_disable);
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
	/**
	 * 启用
	 * @param oid
	 * @return
	 */
	@RequestMapping(value = "/enable", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<BaseResp> enable(@RequestParam String oid) {
		BaseResp rep = new BaseResp();
		this.switchService.enable(oid, SwitchEntity.SWITCH_Status_enable);
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
	/**
	 * 添加白名单
	 * @return
	 */
	@RequestMapping(value = "/addwhite", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<BaseResp> addwhite(@Valid SwitchAddWhiteReq req) {
		BaseResp rep = new BaseResp();
		this.switchService.addWhite(req, this.getLoginUser());
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
	/**
	 * 添加黑名单
	 * @return
	 */
	@RequestMapping(value = "/addblack", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<BaseResp> addblack(@Valid SwitchAddBlackReq req) {
		BaseResp rep = new BaseResp();
		this.switchService.addBlack(req, this.getLoginUser());
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
	/**
	 * 黑白名单停用
	 * @param oid
	 * @return
	 */
	@RequestMapping(value = "/whitedisable", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<BaseResp> whitedisable(@RequestParam String oid) {
		BaseResp rep = new BaseResp();
		this.switchService.whiteEnable(oid, SwitchEntity.SWITCH_WhiteStatus_no);
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
	/**
	 * 白名单启用
	 * @param oid
	 * @return
	 */
	@RequestMapping(value = "/whiteenable", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<BaseResp> whiteenable(@RequestParam String oid) {
		BaseResp rep = new BaseResp();
		this.switchService.whiteEnable(oid, SwitchEntity.SWITCH_WhiteStatus_white);
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
	/**
	 * 黑名单启用
	 * @param oid
	 * @return
	 */
	@RequestMapping(value = "/blackenable", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<BaseResp> blackenable(@RequestParam String oid) {
		BaseResp rep = new BaseResp();
		this.switchService.whiteEnable(oid, SwitchEntity.SWITCH_WhiteStatus_black);
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
}
