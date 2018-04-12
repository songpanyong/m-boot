package com.guohuai.mmp.investor.sonaccount.produceMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="/mimosa/produce/produceMessage")
public class ProductMessageController {
		
	@Autowired
	ProductMessageService productMessageService;
	
	@RequestMapping(value="/detail",method=RequestMethod.POST)
	public ResponseEntity<ProductMessageRep> queryProductDetailByProductOid(@RequestParam String productOid){
		
		ProductMessageRep rep = this.productMessageService.queryByProductOid(productOid);
		
		
		return new ResponseEntity<ProductMessageRep>(rep,HttpStatus.OK);
	}
	
}
