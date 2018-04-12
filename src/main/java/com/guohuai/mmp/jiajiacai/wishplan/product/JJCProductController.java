package com.guohuai.mmp.jiajiacai.wishplan.product;

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
import com.guohuai.mmp.jiajiacai.wishplan.plan.entity.WishplanProduct;
import com.guohuai.mmp.jiajiacai.wishplan.product.form.JJCPortfolioForm;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value="心愿计划 - 获取产品的信息")
@RestController
@EnableAutoConfiguration
@RequestMapping(value = "/mimosa/wishplan/product", produces = "application/json")
public class JJCProductController {
	
	@Autowired
	private JJCProductService productService;
	
	@ApiOperation(value="获取产品的资产配比信息", notes="根据产品ID获取产品的资产配比信息")
	@RequestMapping(value = "getProductAssetRatioById", method ={RequestMethod.POST})
	@ResponseBody
	public ResponseEntity<JJCPortfolioForm> getProductAssetRatioById(@NotNull @RequestParam("productOid") String productOid) throws BaseException {
		JJCPortfolioForm form = productService.getPortfolioByProductOid(productOid);
		if(form == null) {
			throw new BaseException(ErrorMessage.PORTFOLIO_NOT_EXIST);
		}
		return new ResponseEntity<JJCPortfolioForm>(form, HttpStatus.OK);
	}
	
	@ApiOperation(value="获取最优的产品利率列表", notes="获取最优的产品利率列表")
	@RequestMapping(value = "getProductRateList", method ={RequestMethod.POST})
	@ResponseBody
	public ResponseEntity<List<WishplanProduct>> getProductRateList(@NotNull @RequestParam("duration") int duration
			,@NotNull @RequestParam("riskLevel") String riskLevel
			) throws BaseException {
		List<WishplanProduct> formList = productService.findProductRateList(duration, riskLevel, false);
		return new ResponseEntity<List<WishplanProduct>>(formList, HttpStatus.OK);
	}
	
}
