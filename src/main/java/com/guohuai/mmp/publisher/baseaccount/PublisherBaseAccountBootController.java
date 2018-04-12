package com.guohuai.mmp.publisher.baseaccount;

import java.util.List;

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

import com.alibaba.fastjson.JSONObject;
import com.guohuai.basic.component.ext.web.BaseController;
import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.basic.component.ext.web.PageResp;
import com.guohuai.basic.component.ext.web.Response;
import com.guohuai.mmp.platform.accment.UserBindCardApplyRep;

import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.Like;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;

@RestController
@RequestMapping(value = "/mimosa/boot/publisher/baseaccount", produces = "application/json")
public class PublisherBaseAccountBootController extends BaseController {

	@Autowired
	private PublisherBaseAccountService publisherBaseAccountService;
	
	
	
	
	@RequestMapping(value = "mng", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<PageResp<PublisherQueryRep>> superMng(HttpServletRequest request,
			@And({@Spec(params = "phone", path = "phone", spec = Like.class)}) Specification<PublisherBaseAccountEntity> spec,
			@RequestParam int page, 
			@RequestParam int rows,
			@RequestParam(required = false, defaultValue = "createTime") String sort,
			@RequestParam(required = false, defaultValue = "desc") String order) {
		Direction sortDirection = Direction.DESC;
		if (!"desc".equals(order)) {
			sortDirection = Direction.ASC;
		}
		
		Pageable pageable = new PageRequest(page - 1, rows, new Sort(new Order(sortDirection, sort)));
		PageResp<PublisherQueryRep> rep = this.publisherBaseAccountService.mng(spec, pageable);
		return new ResponseEntity<PageResp<PublisherQueryRep>>(rep, HttpStatus.OK);
	}
	
	
	/**
	 * 发行人账户管理(个人)--发行人详情
	 */
	@RequestMapping(value = "userinfo", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<PublisherBaseAccountRep> userinfo() {
		String uid = this.getLoginUser();

		PublisherBaseAccountRep rep = this.publisherBaseAccountService.userInfo(uid);
		return new ResponseEntity<PublisherBaseAccountRep>(rep, HttpStatus.OK);
	}
	
	/**
	 * 发行人账户管理(平台)--发行人详情
	 */
	@RequestMapping(value = "suserinfo", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<PublisherBaseAccountRep> userinfo(@RequestParam(required = true) String publisherOid) {

		PublisherBaseAccountRep rep = this.publisherBaseAccountService.suserInfo(publisherOid);
		return new ResponseEntity<PublisherBaseAccountRep>(rep, HttpStatus.OK);
	}
	
	@RequestMapping(value = "ub", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<PublisherBaseAccountRep> updateBalance() {
		String uid = this.getLoginUser();
		
		this.publisherBaseAccountService.updateBalance(publisherBaseAccountService.findByLoginAcc(uid));
		PublisherBaseAccountRep rep = new PublisherBaseAccountRep();
		return new ResponseEntity<PublisherBaseAccountRep>(rep, HttpStatus.OK);
	}
	
	/**
	 * 创建发行人
	 */
	@RequestMapping(value = "add", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<BaseAccountAddRep> add(@Valid PublisherBaseAccountReq req) {
		this.getLoginUser();

		BaseAccountAddRep rep = this.publisherBaseAccountService.add(req);
		return new ResponseEntity<BaseAccountAddRep>(rep, HttpStatus.OK);
	}
	
	
	/**
	 * 绑卡申请
	 */
	@RequestMapping(value = "bapply", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<UserBindCardApplyRep> bindCardApply(@Valid PublisherBaseAccountBindApplyReq req) {
		this.getLoginUser();

		UserBindCardApplyRep rep = this.publisherBaseAccountService.bindCardApply(req);
		return new ResponseEntity<UserBindCardApplyRep>(rep, HttpStatus.OK);
	}
	
	/**
	 * 绑卡确认
	 */
	@RequestMapping(value = "bconfirm", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<BaseResp> bindCardConfirm(@Valid PublisherBaseAccountBindConfirmReq req) {
		this.getLoginUser();

		BaseResp rep = this.publisherBaseAccountService.bindCardConfirm(req);
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
	/**
	 * 解绑
	 */
	@RequestMapping(value = "unbind", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<BaseResp> unbindCard(@RequestParam String baseAccountOid) {
		this.getLoginUser();

		BaseResp rep = this.publisherBaseAccountService.unbindCard(baseAccountOid);
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
	

	/**
	 * 获取spv列表
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getAllSPV", name = "获取spv列表", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<Response> getAllSPV() {
		List<JSONObject> objList = this.publisherBaseAccountService.getAllSPV();
		Response r = new Response();
		r.with("result", objList);
		return new ResponseEntity<Response>(r, HttpStatus.OK);
	}
	
	/**
	 * 发行人绑卡
	 * 
	 * @return
	 * */
	/**
	 * 
	 */
	@RequestMapping(value = "spbindcard", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<UserBindCardApplyRep> bindCard(@Valid PublisherBaseAccountBindApplyReq req) {
		UserBindCardApplyRep rep = this.publisherBaseAccountService.bindCard(req);
		return new ResponseEntity<UserBindCardApplyRep>(rep, HttpStatus.OK);
	}

}
