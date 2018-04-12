package com.guohuai.mmp.backstage.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.guohuai.basic.component.ext.web.PageResp;
import com.guohuai.component.util.DateUtil;
import com.guohuai.mmp.backstage.SonAccountRelateEntity;
import com.guohuai.mmp.backstage.rep.SonAccountUserInfoRep;
import com.guohuai.mmp.backstage.rep.TransferMoneyRep;
import com.guohuai.mmp.backstage.req.SonAccountUserInfoReq;
import com.guohuai.mmp.backstage.service.BackStageRelateSonAccountService;
import com.guohuai.mmp.investor.bankorder.InvestorBankOrderEntity;
import net.kaczmarzyk.spring.data.jpa.domain.DateAfterInclusive;
import net.kaczmarzyk.spring.data.jpa.domain.DateBeforeInclusive;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.GreaterThanOrEqual;
import net.kaczmarzyk.spring.data.jpa.domain.LessThanOrEqual;
import net.kaczmarzyk.spring.data.jpa.domain.Like;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;

@RestController
@RequestMapping(value="/mimosa/backstage")
public class BackStageRelateSonAccountController {
	
	@Autowired
	BackStageRelateSonAccountService backStageRelateSonAccountService;
	
	
	/**
	 * 后台关于主子账户之间的转账记录查询
	 * 
	 * */
	@RequestMapping(value="/query",method=RequestMethod.POST)
	public ResponseEntity<PageResp<TransferMoneyRep>> query(HttpServletRequest request,
			@And({@Spec(params = "tradeType", path = "orderType", spec = Equal.class),
				  @Spec(params = "phone",path="investorBaseAccount.phoneNum",spec = Equal.class),
				  @Spec(params = "pid",path="investorBaseAccount.oid",spec = Equal.class),		 
				  @Spec(params = "tradeNum",path="orderCode",spec =Equal.class),
				  @Spec(params = "orderStatus",path="orderStatus",spec =Equal.class),
				  @Spec(params = "transferTimeBegin",path="orderTime",spec = DateAfterInclusive.class, config = DateUtil.fullDatePattern),
				  @Spec(params = "transferTimeEnd",path="orderTime",spec = DateBeforeInclusive.class, config = DateUtil.fullDatePattern),
			  	  @Spec(params = "transferMoneyEnd",path = "orderAmount",spec =LessThanOrEqual.class),
			  	@Spec(params = "transferMoneyBegin",path="orderAmount",spec=GreaterThanOrEqual.class)  
			})Specification<InvestorBankOrderEntity> spec,
			@RequestParam int rows, @RequestParam int page){
		
		Pageable pages = new PageRequest(page - 1, rows, new Sort(Direction.DESC,"createTime"));
		PageResp<TransferMoneyRep> rep = this.backStageRelateSonAccountService.query(spec,pages);
		
		return new ResponseEntity<PageResp<TransferMoneyRep>>(rep,HttpStatus.OK);
	}
	
	
	/**
	 * 后台关于主子账户的相关关联 信息查询
	 * 
	 * */
	@RequestMapping(value="/sonAccountquery",method=RequestMethod.POST)
	public ResponseEntity<PageResp<SonAccountUserInfoRep>> querySonAccount(HttpServletRequest request,
			@And({@Spec(params = "sid",path="sonBaseAccount.oid",spec = Equal.class),
				  @Spec(params = "pid",path = "investorBaseAccount.oid",spec = Equal.class),
				  @Spec(params = "relationstatus",path = "status",spec = Equal.class),
				  @Spec(params = "phoneNum",path="investorBaseAccount.phoneNum",spec = Like.class),
				  @Spec(params = "status" ,path = "sonBaseAccount.status",spec = Equal.class),
				  @Spec(params ="createTimeStartTime",path="sonBaseAccount.createTime",spec= DateAfterInclusive.class, config = DateUtil.fullDatePattern),	
				  @Spec(params ="createTimeEndTime",path="sonBaseAccount.createTime",spec= DateBeforeInclusive.class, config = DateUtil.fullDatePattern),
			})Specification<SonAccountRelateEntity> spec,@RequestParam int rows,@RequestParam int page){
		Pageable pages =new PageRequest(page-1,rows,new Sort(Direction.DESC,"sonBaseAccount.createTime"));
		PageResp<SonAccountUserInfoRep> rep = this.backStageRelateSonAccountService.querySonAccount(spec,pages);
		return new ResponseEntity<PageResp<SonAccountUserInfoRep>>(rep,HttpStatus.OK);
	}
}

	
