package com.guohuai;

import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.alibaba.fastjson.JSON;
import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.component.util.DateUtil;
import com.guohuai.mmp.platform.msgment.MsgParam;
import com.guohuai.mmp.platform.msgment.MsgService;
import com.guohuai.mmp.platform.msgment.MsgUtil;
import com.guohuai.mmp.platform.msgment.WithdrawApplyMsgReq;
import com.guohuai.mmp.platform.msgment.WithdrawSuccessMailReq;
import com.guohuai.mmp.sms.SMSUtils;
import com.guohuai.mmp.sys.CodeConstants;

import rop.thirdparty.org.apache.commons.lang3.StringUtils;



@RunWith(SpringRunner.class)
@SpringBootTest(classes = ApplicationBootstrap.class)
public class SMSTest {
	

	@Autowired
	private SMSUtils sMSUtils;
	@Autowired
	private MsgService msgService;
	
	@Autowired
	private MsgUtil msgUtil;
	
	@Test
	public void test() {
		
		String[] value={"3"};
		//BaseResp rep =  this.sMSUtils.sendSMS("15229092991", "registsuccess", value);
		//BaseResp rep = this.sMSUtils.newSendSms("15229092991","bindcard",value);
		//System.out.print(rep);
		
		/*WithdrawApplyMsgReq msgReq = new WithdrawApplyMsgReq();
		msgReq.setPhone("15229092991");
		msgReq.setOrderAmount(new BigDecimal(100));
		msgReq.setOrderTime(DateUtil.getSqlCurrentDate());
		msgReq.setFee(new BigDecimal(2));
		msgReq.setPreAmount(msgReq.getOrderAmount().subtract(msgReq.getFee()));
		msgService.withdrawapply(msgReq);*/
//		orep = this.sMSUtils.sendSMS(mta.getPhone(), MsgParam.msgSonWithdrawApply.toString(), JSON.parseObject(msgUtil.assembleMsgParams(mta.getNickname(),mta.getOrderTime(),mta.getOrderAmount(),mta.getFee(),mta.getPreAmount()), String[].class));
		/*String[] values ={"旅游一次性","中国银行","200"};
		BaseResp rep = this.sMSUtils.sendSMS("15229092991",MsgParam.msgDeductFailForChannelNoSupportCard.toString(),values);
		BaseResp rep2 = this.sMSUtils.sendSMS("15229092991",MsgParam.msgDeductFailForSystemNoSupportCard.toString(),values);
		BaseResp rep3 = this.sMSUtils.sendSMS("15229092991",MsgParam.msgDeductFailForOtherReason.toString(),values);
		
		String[] values2 ={"萝卜","旅游一次性","中国银行","200"};
		BaseResp rep00 = this.sMSUtils.sendSMS("15229092991",MsgParam.msgSonDeductFailForOnlineBanking.toString(),values2);
		BaseResp rep11 = this.sMSUtils.sendSMS("15229092991",MsgParam.msgSonDeductFailForChannelNoSupportCard.toString(),values2);
		BaseResp rep22 = this.sMSUtils.sendSMS("15229092991",MsgParam.msgSonDeductFailForSystemNoSupportCard.toString(),values2);
		BaseResp rep33 = this.sMSUtils.sendSMS("15229092991",MsgParam.msgSonDeductFailForOtherReason.toString(),values2);*/
		String orderCode = "246682018030600000002";
		String code = orderCode.substring(orderCode.length() - 16, orderCode.length());
		System.out.println(code);
		String code2 = StringUtils.substringBefore(orderCode, code);
		System.out.println(code2);
		
		String code3 = code2.substring(0, code2.length() - 2);
		System.out.println(code3);
		String rollInCode = code3 + "" + CodeConstants.PAYMENT_RollIn + "" + code;
		System.out.println(rollInCode);
		
	}
	
}
