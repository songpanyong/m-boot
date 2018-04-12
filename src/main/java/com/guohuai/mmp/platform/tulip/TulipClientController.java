package com.guohuai.mmp.platform.tulip;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.guohuai.basic.component.ext.web.BaseController;
import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.basic.component.ext.web.PageResp;
import com.guohuai.component.web.view.Pages;
import com.guohuai.tulip.platform.facade.obj.EventRep;
import com.guohuai.tulip.platform.facade.obj.EventReq;
import com.guohuai.tulip.platform.facade.obj.IssuedCouponReq;
import com.guohuai.tulip.platform.facade.obj.MyCouponRep;

@RestController
@RequestMapping(value = "/mimosa/client/tulip", produces = "application/json")
public class TulipClientController extends BaseController {

	@Autowired
	private TulipService tulipService;

	/**
	 * 我的所有卡券
	 * 
	 * @param status:(可送空)卡券状态(卡券状态notUsed未使用;used已使用;expired过期)
	 * @param type:(可送空)卡券类型(redPackets红包;coupon优惠券;3折扣券;体验金tasteCoupon;加息券rateCoupon)
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping(value = "myallcoupon", method = {RequestMethod.POST, RequestMethod.GET})
	@ResponseBody
	public ResponseEntity<PageResp<MyCouponRep>> myAllCoupon(@RequestParam(required = false) String status,
			@RequestParam(required = false) String type, @RequestParam int page, @RequestParam int rows) {

		String userOid = this.getLoginUser();

		return new ResponseEntity<PageResp<MyCouponRep>>(
				this.tulipService.getMyAllCouponList(userOid, status, type, page, rows), HttpStatus.OK);
	}
	
	/**
	 * 我的所有卡券数量
	 * 
	 * @return notUsedNum未使用数量;usedNum:已使用数量;expiredNum:过期数量)
	 */
	@RequestMapping(value = "getMyAllCouponNum", method = {RequestMethod.POST, RequestMethod.GET})
	@ResponseBody
	public ResponseEntity<MyCouponRep> getMyAllCouponNum() {

		String userOid = this.getLoginUser();

		return new ResponseEntity<MyCouponRep>(
				this.tulipService.getMyAllCouponNum(userOid), HttpStatus.OK);
	}

	/**
	 * 我的可购买某产品的卡券列表
	 * 
	 * @param proOid：产品oid
	 * @return
	 */
	@RequestMapping(value = "mycouponofpro", method = {RequestMethod.POST, RequestMethod.GET})
	@ResponseBody
	public ResponseEntity<PageResp<MyCouponRep>> getMyCouponList(@RequestParam String proOid) {

		String userOid = this.getLoginUser();

		return new ResponseEntity<PageResp<MyCouponRep>>(this.tulipService.getMyCouponList(userOid, proOid),
				HttpStatus.OK);
	}

	/**
	 * 卡券金额
	 * 
	 * @param couponId:卡券编码
	 * @return
	 */
	@RequestMapping(value = "coupondetail", method = {RequestMethod.POST, RequestMethod.GET})
	@ResponseBody
	public ResponseEntity<MyCouponRep> getCouponAmount(@RequestParam(required = false) String userOid,
			@RequestParam String couponId) {
		if (StringUtils.isEmpty(userOid)) {
			userOid = this.getLoginUser();
		}
		return new ResponseEntity<MyCouponRep>(this.tulipService.getCouponDetail(couponId), HttpStatus.OK);
	}
	
	/**
	 * 获取活动奖励信息
	 * @return
	 */
	@RequestMapping(value = "getEventInfo",method = {RequestMethod.POST, RequestMethod.GET})
	@ResponseBody
	public ResponseEntity<EventRep> getEventInfo(@RequestBody EventReq param) {
		return new ResponseEntity<EventRep>(this.tulipService.getEventInfo(param), HttpStatus.OK);
	}
	/**
	 * 签到方法
	 * @return
	 */
	@RequestMapping(value = "signIn",method = {RequestMethod.POST, RequestMethod.GET})
	@ResponseBody
	public ResponseEntity<BaseResp> signIn(){
		String userId = this.getLoginUser();
		this.tulipService.onSign(userId);
		return new ResponseEntity<BaseResp>(new BaseResp(),HttpStatus.OK);
	}
	
	/**
	 * 检查用户是否签到
	 * @return
	 */
	@RequestMapping(value="checkSign",method = {RequestMethod.POST, RequestMethod.GET})
	@ResponseBody
	public ResponseEntity<BaseResp> checkSign(){
		String userId = this.getLoginUser();
		return new ResponseEntity<BaseResp>(this.tulipService.checkSign(userId),HttpStatus.OK);
	}
	/**
	 * 主动下发卡券
	 * @param userOid
	 * @param eventId
	 * @return
	 */
	@RequestMapping(value="issuedCoupon",method = {RequestMethod.POST, RequestMethod.GET} )
	@ResponseBody
	public ResponseEntity<BaseResp> issuedCoupon(@RequestParam String userOid,@RequestParam String eventId){
		IssuedCouponReq req = new IssuedCouponReq();
		req.setUserId(userOid);// 用户oid
		req.setEventId(eventId);// 活动ID
		this.tulipService.issuedCoupon(req);
		
		return new ResponseEntity<BaseResp>(new BaseResp(),HttpStatus.OK);
	}
	
	/**
	 * 获取执行事件之后即将获取的卡券列表
	 * <p> 触发事件（如注册）后，前端弹出预计下发的卡券列表（异步下发，即展现的是还未下发的卡券）
	 * @param eventType
	 * @return
	 */
	@RequestMapping(value="couponSoonList",method = {RequestMethod.POST, RequestMethod.GET} )
	@ResponseBody
	public ResponseEntity<PageResp<MyCouponRep>> couponSoonList(@RequestParam String eventType){
		
		PageResp<MyCouponRep> pages = this.tulipService.getCouponWithEventSoon(eventType);
		
		return new ResponseEntity<PageResp<MyCouponRep>>(pages,HttpStatus.OK);
	}
}
