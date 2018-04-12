package com.guohuai.mmp.city;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.guohuai.basic.component.ext.web.BaseController;
import com.guohuai.basic.component.ext.web.PageResp;

@RestController
@RequestMapping(value = "/mimosa/client/city", produces = "application/json")
public class CityController extends BaseController {

	@Autowired
	private CityService cityService;
	
	@RequestMapping(value = "query", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<PageResp<CityQueryRep>> cityQuery(@RequestParam boolean province, 
			@RequestParam(required = false) String parentCode) {
		PageResp<CityQueryRep> rep = this.cityService.findByCityParentCode(province, parentCode);

		return new ResponseEntity<PageResp<CityQueryRep>>(rep, HttpStatus.OK);
	}
	
	@RequestMapping(value = "all", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<PageResp<CityQueryAllRep>> cityAllQuery() {
		PageResp<CityQueryAllRep> rep = this.cityService.getAllCitys();

		return new ResponseEntity<PageResp<CityQueryAllRep>>(rep, HttpStatus.OK);
	}
}
