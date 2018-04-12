package com.guohuai.mmp.jiajiacai.productAsset;

import java.math.BigDecimal;

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

import com.guohuai.basic.component.ext.web.BaseController;
import com.guohuai.component.exception.AMPException;
import com.guohuai.component.web.view.RowsRep;
import com.guohuai.mmp.jiajiacai.common.exception.BaseException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Api(value = "产品标的 - 展示")
@RestController
@EnableAutoConfiguration
@RequestMapping(value = "/mimosa/productasset/client", produces = "application/json")
@Slf4j

public class ProductAssetController extends BaseController {

	@Autowired
	private ProductAssetService paService;

	@ApiOperation(value = "披露第一层", notes = "投资组合 ")
	@RequestMapping(value = "revealLevelFirst", method = { RequestMethod.POST })
	@ResponseBody
	public ResponseEntity<FirstLevelRep<AssetLevelFirst>> revealLevelFirst(
			@NotNull @RequestParam("userId") String userId, @NotNull @RequestParam("type") String type)
			throws BaseException {
		String loginUid = this.getLoginUser();
		if (loginUid == null) {
			throw new AMPException("当前用户未登录或会话已超时");
		}
		FirstLevelRep<AssetLevelFirst> levels = paService.levelFirst(userId, type);
		levels.setErrorCode(0);
		levels.setErrorMessage(null);

		return new ResponseEntity<FirstLevelRep<AssetLevelFirst>>(levels, HttpStatus.OK);
	}

	@ApiOperation(value = "披露第二层", notes = "投资组合 ")
	@RequestMapping(value = "revealLevelSecond", method = { RequestMethod.POST })
	@ResponseBody
	public ResponseEntity<RowsRep<AssetLevelSecond>> revealLevelSecond(
			@NotNull @RequestParam("productOid") String productOid, @NotNull @RequestParam("amount") BigDecimal amount)
			throws BaseException {
		String loginUid = this.getLoginUser();
		if (loginUid == null) {
			throw new AMPException("当前用户未登录或会话已超时");
		}
		RowsRep<AssetLevelSecond> levels = paService.levelSecond(productOid, amount);
		levels.setErrorCode(0);
		levels.setErrorMessage(null);

		return new ResponseEntity<RowsRep<AssetLevelSecond>>(levels, HttpStatus.OK);
	}

	@ApiOperation(value = "披露心愿第一层", notes = "投资组合 ")
	@RequestMapping(value = "wishplanRevealFirst", method = { RequestMethod.POST })
	@ResponseBody
	public ResponseEntity<FirstLevelRep<AssetLevelFirstWishplan>> wishplanRevealFirst(
			@NotNull @RequestParam("userId") String userId) throws BaseException {
		String loginUid = this.getLoginUser();
		if (loginUid == null) {
			throw new AMPException("当前用户未登录或会话已超时");
		}
		FirstLevelRep<AssetLevelFirstWishplan> levels = paService.wpLevelFirst(userId);
		levels.setErrorCode(0);
		levels.setErrorMessage(null);

		return new ResponseEntity<FirstLevelRep<AssetLevelFirstWishplan>>(levels, HttpStatus.OK);
	}

	@ApiOperation(value = "披露心愿第二层", notes = "投资组合 ")
	@RequestMapping(value = "wishplanRevealSecond", method = { RequestMethod.POST })
	@ResponseBody
	public ResponseEntity<RowsRep<AssetLevelSecond>> wishplanRevealSecond(
			@NotNull @RequestParam("wishplanOid") String wishplanOid,
			@NotNull @RequestParam("planType") String planType, @NotNull @RequestParam("amount") BigDecimal amount)
			throws BaseException {
		String loginUid = this.getLoginUser();
		if (loginUid == null) {
			throw new AMPException("当前用户未登录或会话已超时");
		}
		RowsRep<AssetLevelSecond> levels = paService.wplevelSecond(wishplanOid, planType, amount);
		levels.setErrorCode(0);
		levels.setErrorMessage(null);

		return new ResponseEntity<RowsRep<AssetLevelSecond>>(levels, HttpStatus.OK);
	}

}
