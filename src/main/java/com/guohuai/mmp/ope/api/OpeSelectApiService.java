package com.guohuai.mmp.ope.api;


import java.sql.Timestamp;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guohuai.component.util.DateUtil;
import com.guohuai.mmp.ope.failcard.FailCard;
import com.guohuai.mmp.ope.failcard.FailCardService;
import com.guohuai.mmp.ope.failrecharge.FailRecharge;
import com.guohuai.mmp.ope.failrecharge.FailRechargeService;
import com.guohuai.mmp.ope.nobuy.NoBuy;
import com.guohuai.mmp.ope.nobuy.NoBuyService;
import com.guohuai.mmp.ope.nocard.NoCard;
import com.guohuai.mmp.ope.nocard.NoCardService;
import com.guohuai.mmp.ope.norecharge.NoRecharge;
import com.guohuai.mmp.ope.norecharge.NoRechargeService;

@Service
@Transactional
public class OpeSelectApiService {

	@Autowired
	private NoCardService noCardService;
	@Autowired
	private FailCardService failCardService;
	@Autowired
	private NoRechargeService noRechargeService;
	@Autowired
	private FailRechargeService failRechargeService;
	@Autowired
	private NoBuyService noBuyService;
	
	// 新建未绑卡
	public void createNoCard(String userOid, String phone, String source, Long registerTime) {
		NoCard en = noCardService.findByUserOid(userOid);
		if (en == null){
			en = new NoCard();
			en.setUserOid(userOid);
			en.setPhone(phone);
			en.setSource(source);
			en.setRegisterTime(new Timestamp(registerTime));
			en.setIsFeedback(NoCard.NOCARD_COMMON_NO);
			en.setIsBind(NoCard.NOCARD_COMMON_NO);
			
			noCardService.saveEntity(en);
		}
	}

	// 新建绑卡未成功
	public void createFailCard(String userOid, String phone, String source, String systemReason) {
		FailCard failCard = failCardService.findByUserOid(userOid);
		if (failCard == null){
			failCard = new FailCard();
			failCard.setUserOid(userOid);
			failCard.setPhone(phone);
			failCard.setSource(source);
			failCard.setBindTime(DateUtil.getSqlCurrentDate());
			failCard.setSystemReason(systemReason);
			failCard.setIsFeedback(FailCard.FAILCARD_COMMON_NO);
			failCard.setIsBind(FailCard.FAILCARD_COMMON_NO);
			
			failCardService.saveEntity(failCard);
		}
	}
	
	// 新建绑卡未成功完整
	public void createFullFailCard(String userOid, String phone, String source, String name, Long bindSuccessTime) {
		FailCard failCard = failCardService.findByUserOid(userOid);
		if (failCard == null){
			failCard = new FailCard();
			failCard.setUserOid(userOid);
			failCard.setPhone(phone);
			failCard.setSource(source);
			failCard.setName(name);
			failCard.setBindTime(new Timestamp(bindSuccessTime));
			failCard.setBindSuccessTime(new Timestamp(bindSuccessTime));
			failCard.setIsFeedback(FailCard.FAILCARD_COMMON_NO);
			failCard.setIsBind(FailCard.FAILCARD_COMMON_IS);
			
			failCardService.saveEntity(failCard);
		}else{
			if (failCard.getIsBind().equals(FailCard.FAILCARD_COMMON_NO)){
				failCard.setName(name);
				failCard.setIsBind(FailCard.FAILCARD_COMMON_IS);
				failCard.setBindSuccessTime(new Timestamp(bindSuccessTime));
				
				failCardService.updateEntity(failCard);
			}else if (failCard.getName() == null || failCard.getName().isEmpty() || !failCard.getName().equals(name)){
				failCard.setName(name);
				
				failCardService.updateEntity(failCard);
			}
		}
		
		// 更新未绑卡表
		NoCard noCard = noCardService.findByUserOid(userOid);
		if (noCard != null){
			if (noCard.getIsBind().equals(FailCard.FAILCARD_COMMON_NO)){
				noCard.setName(name);
				noCard.setBindSuccessTime(new Timestamp(bindSuccessTime));
				noCard.setIsBind(NoCard.NOCARD_COMMON_IS);
				
				noCard = noCardService.updateEntity(noCard);
			}else if (noCard.getName() == null || noCard.getName().isEmpty() || !noCard.getName().equals(name)){
				noCard.setName(name);
				noCard = noCardService.updateEntity(noCard);
			}
			
		}
		
		// 生成未充值表
		NoRecharge noRecharge = noRechargeService.findByUserOid(userOid);
		if (failCard != null){
			if (noRecharge == null){
				noRecharge = new NoRecharge();
				
				noRecharge.setUserOid(userOid);
				noRecharge.setName(failCard.getName());
				noRecharge.setPhone(failCard.getPhone());
				noRecharge.setSource(failCard.getSource());
				noRecharge.setBindTime(failCard.getBindSuccessTime());
				noRecharge.setIsFeedback(NoRecharge.NORECHARGE_COMMON_NO);
				noRecharge.setIsCharge(NoRecharge.NORECHARGE_COMMON_NO);
				
				noRechargeService.saveEntity(noRecharge);
			}else if (noRecharge.getName() == null || noRecharge.getName().isEmpty() || !noRecharge.getName().equals(name)){
				noRecharge.setName(failCard.getName());
				noRechargeService.updateEntity(noRecharge);
			}
		}
	}
	
	// 绑卡失败
	public void failCard(String userOid, String systemReason) {
		FailCard failCard = failCardService.findByUserOid(userOid);
		if (failCard != null && failCard.getIsBind().equals(FailCard.FAILCARD_COMMON_NO)){
			failCard.setSystemReason(systemReason);
			failCard.setBindTime(DateUtil.getSqlCurrentDate());
			
			failCardService.saveEntity(failCard);
		}
	}

	//新建充值未成功
	public void createFailRecharge(String userOid, String phone, String name, String source, String systemReason) {
		FailRecharge en = failRechargeService.findByUserOid(userOid);
		if (en == null){
			en = new FailRecharge();
			en.setUserOid(userOid);
			en.setPhone(phone);
			en.setName(name);
			en.setSource(source);
			en.setRechargeTime(DateUtil.getSqlCurrentDate());
			en.setSystemReason(systemReason);
			en.setIsCharge(FailRecharge.FAILRECHARGE_COMMON_NO);
			en.setIsFeedback(FailRecharge.FAILRECHARGE_COMMON_NO);
			
			failRechargeService.saveEntity(en);
		}else if (en != null && en.getIsCharge().equals(FailRecharge.FAILRECHARGE_COMMON_NO)){
			en.setSystemReason(systemReason);
			en.setRechargeTime(DateUtil.getSqlCurrentDate());
			
			failRechargeService.updateEntity(en);
		}
		
	}

	// 充值失败
	public void failRecharge(String userOid, String phone, String name, String source, String systemReason, Long rechargeTime) {
		FailRecharge en = failRechargeService.findByUserOid(userOid);
		if (en == null){
			en = new FailRecharge();
			en.setUserOid(userOid);
			en.setPhone(phone);
			en.setName(name);
			en.setSource(source);
			en.setRechargeTime(new Timestamp(rechargeTime));
			en.setSystemReason(systemReason);
			en.setIsCharge(FailRecharge.FAILRECHARGE_COMMON_NO);
			en.setIsFeedback(FailRecharge.FAILRECHARGE_COMMON_NO);
			
			failRechargeService.saveEntity(en);
		}else if (en != null && en.getIsCharge().equals(FailRecharge.FAILRECHARGE_COMMON_NO)){
			en.setSystemReason(systemReason);
			
			failRechargeService.updateEntity(en);
		}
	}

	// 充值成功
	public void successRecharge(String userOid, String phone, String name, String source, Long rechargeTime, Long rechargeSuccessTime) {
		FailRecharge en = failRechargeService.findByUserOid(userOid);
		if (en == null){
			en = new FailRecharge();
			en.setUserOid(userOid);
			en.setPhone(phone);
			en.setName(name);
			en.setSource(source);
			en.setRechargeTime(new Timestamp(rechargeTime));
			en.setSystemReason("");
			en.setRechargeSuccessTime(new Timestamp(rechargeSuccessTime));
			en.setIsCharge(FailRecharge.FAILRECHARGE_COMMON_IS);
			en.setIsFeedback(FailRecharge.FAILRECHARGE_COMMON_NO);
			
			failRechargeService.saveEntity(en);
		}else{
			if (en.getIsCharge().equals(FailRecharge.FAILRECHARGE_COMMON_NO)){
				en.setRechargeSuccessTime(new Timestamp(rechargeSuccessTime));
				en.setIsCharge(FailRecharge.FAILRECHARGE_COMMON_IS);
				en.setName(name);
				
				failRechargeService.updateEntity(en);
			}else if (en.getName() == null || en.getName().isEmpty() || !en.getName().equals(name)){
				en.setName(name);
				
				failRechargeService.updateEntity(en);
			}
		}
		
		NoRecharge noRecharge = noRechargeService.findByUserOid(userOid);
		if (noRecharge != null){
			if (noRecharge.getIsCharge().equals(NoRecharge.NORECHARGE_COMMON_NO)){
				noRecharge.setRechargeSuccessTime(new Timestamp(rechargeSuccessTime));
				noRecharge.setIsCharge(NoRecharge.NORECHARGE_COMMON_IS);
				noRecharge.setName(name);
				
				noRechargeService.updateEntity(noRecharge);
			}else if(noRecharge.getName() == null || noRecharge.getName().isEmpty() || !noRecharge.getName().equals(name)){
				noRecharge.setName(name);
				noRechargeService.updateEntity(noRecharge);
			}
			
		}
		
		NoBuy noBuy = noBuyService.findByUserOid(userOid);
		if (en != null){
			if (noBuy == null){
				noBuy = new NoBuy();
				
				noBuy.setUserOid(userOid);
				noBuy.setName(en.getName());
				noBuy.setPhone(en.getPhone());
				noBuy.setSource(en.getSource());
				noBuy.setRechargeTime(en.getRechargeSuccessTime());
				noBuy.setIsBuy(NoBuy.NOBUY_COMMON_NO);
				noBuy.setIsFeedback(NoBuy.NOBUY_COMMON_NO);
				
				noBuyService.saveEntity(noBuy);
			}else if (noBuy.getName() == null || noBuy.getName().isEmpty() || !noBuy.getName().equals(name)){
				noBuy.setName(en.getName());
				
				noBuyService.updateEntity(noBuy);
			}
		}
	}

	//购买了
	public void successbuy(String userOid, Long buyTime) {
		NoBuy noBuy = noBuyService.findByUserOid(userOid);
		if (noBuy != null && noBuy.getIsBuy().equals(NoBuy.NOBUY_COMMON_NO)){
			noBuy.setBuyTime(new Timestamp(buyTime));
			noBuy.setIsBuy(NoBuy.NOBUY_COMMON_IS);
			
			noBuyService.updateEntity(noBuy);
		}
	}

}
