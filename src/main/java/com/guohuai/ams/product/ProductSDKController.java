package com.guohuai.ams.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.guohuai.basic.component.ext.web.BaseController;
import com.guohuai.basic.component.ext.web.PageResp;

/**
 * mimosa提供的SDK接口
 * 
 * @author wanglei
 *
 */
@RestController
@RequestMapping(value = "/mimosa/sdk/product", produces = "application/json")
public class ProductSDKController extends BaseController {

	@Autowired
	private ProductSDKService productSDKService;

	/**
	 * 所有产品列表查询
	 */
	@RequestMapping(value = "/queryall", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<PageResp<ProductSDKRep>> queryProductList(@RequestBody ProductSDKReq req) {

		return new ResponseEntity<PageResp<ProductSDKRep>>(this.productSDKService.queryProductList(req), HttpStatus.OK);
	}

	/**
	 * 产品标签查询
	 */
	@RequestMapping(value = "/proLabel", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<PageResp<ProductLabelRep>> proLabel() {

		return new ResponseEntity<PageResp<ProductLabelRep>>(this.productSDKService.queryProductLabelList(),
				HttpStatus.OK);
	}
	
	/**
	 * 根据产品标签类型查询产品标签
	 */
	@RequestMapping(value = "/getProductLabelNames", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<PageResp<ProductLabelRep>> getProductLabelNames(@RequestBody ProductLabelReq param) {
		return new ResponseEntity<PageResp<ProductLabelRep>>(this.productSDKService.getProductLabelNames(param),
				HttpStatus.OK);
	}
}
