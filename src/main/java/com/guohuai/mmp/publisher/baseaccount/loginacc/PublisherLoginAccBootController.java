package com.guohuai.mmp.publisher.baseaccount.loginacc;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.guohuai.basic.component.ext.web.BaseController;
import com.guohuai.basic.component.ext.web.BaseResp;

@RestController
@RequestMapping(value = "/mimosa/boot/publisher/loginacc", produces = "application/json")
public class PublisherLoginAccBootController extends BaseController {
	
	@Autowired
	private PublisherLoginAccService publisherLoginAccService;
	
	@RequestMapping(value = "allacc", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<AllAccRep> allAcc() {
		
		
		AllAccRep rep = this.publisherLoginAccService.allAcc();
		return new ResponseEntity<AllAccRep>(rep, HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "allaccmodify", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<AllAccRep> allAcc4Modify(@RequestParam(required = true) String baseAccountOid) {
		
		
		AllAccRep rep = this.publisherLoginAccService.allAcc4Modify(baseAccountOid);
		return new ResponseEntity<AllAccRep>(rep, HttpStatus.OK);
	}

	@RequestMapping(value = "uploginacc", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<BaseResp> upLoginAcc(@Valid UpLoginAccReq upLoginAccReq) {
		
		
		BaseResp rep = this.publisherLoginAccService.upLoginAcc(upLoginAccReq);
		return new ResponseEntity<BaseResp>(rep, HttpStatus.OK);
	}
	
	
	
}
