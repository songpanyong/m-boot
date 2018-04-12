package com.guohuai;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.component.api.cms.CmsApi;
import com.guohuai.component.util.DateUtil;
import com.guohuai.mmp.platform.msgment.AbortiveMailReq;
import com.guohuai.mmp.platform.msgment.MailReq;
import com.guohuai.mmp.platform.msgment.MailService;
import com.guohuai.mmp.platform.msgment.MsgParam;
import com.guohuai.mmp.platform.msgment.MsgService;
import com.guohuai.mmp.platform.msgment.MsgUtil;
import com.guohuai.mmp.platform.msgment.ReceivedPaymentsMailReq;
import com.guohuai.mmp.platform.msgment.ReceivedPaymentsMsgReq;
import com.guohuai.mmp.platform.msgment.WithdrawApplyMailReq;
import com.guohuai.mmp.platform.msgment.WithdrawSuccessMailReq;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=ApplicationBootstrap.class)
public class MailTest {
	
	@Autowired
	MailService mailService;
	@Autowired
	MsgService msgService;
	
	@Autowired
	private MsgUtil msgUtil;
	@Autowired
	private CmsApi cmsApi;

	@Test
	public void test() {
		
		WithdrawApplyMailReq req = new WithdrawApplyMailReq();
		req.setUserOid("26f7510a991142508f243c80b158f07e");
		req.setOrderAmount(new BigDecimal(100));
		req.setOrderTime(DateUtil.getSqlCurrentDate());	
		req.setFee(new BigDecimal(2));
		req.setPreAmount(req.getOrderAmount().subtract(req.getFee()));
		//mailService.withdrawapply(req);
		
		/*AbortiveMailReq ireq = new AbortiveMailReq();
		ireq.setHotLine("400-8258");
		ireq.setProductName("haha");
		ireq.setUserOid("26f7510a991142508f243c80b158f07e");
		mailService.abortive(ireq);*/
	/*	//提现到账
		WithdrawSuccessMailReq ireq = new WithdrawSuccessMailReq();
		ireq.setCompleteTime(null);
		ireq.setUserOid("26f7510a991142508f243c80b158f07e");
		
		ireq.setOrderAmount(new BigDecimal(100));
		mailService.withdrawsuccess(ireq);
		ReceivedPaymentsMailReq ireq = new ReceivedPaymentsMailReq();
		ireq.setOrderAmount(new BigDecimal(100));
		ireq.setProductName("haha");
		ireq.setUserOid("14e3ede9bb2e49a28d2aaa32178b7be8");
		mailService.receivedpayments(ireq);
		ReceivedPaymentsMsgReq req = new ReceivedPaymentsMsgReq();
		req.setPhone("ba5fe1543f724300bc17e21dd56f4d3d");
		req.setProductName("haha");
		req.setOrderAmount(new BigDecimal(100));
		msgService.receivedpayments(req);
		*/
		WithdrawSuccessMailReq reqs = new WithdrawSuccessMailReq();
		reqs.setCompleteTime(DateUtil.getSqlCurrentDate());
		reqs.setRealAmount(new BigDecimal(98));
		reqs.setUserOid("2614c548154749c89b8199d95223fe65");
		
		//mailService.withdrawsuccess(reqs);
		String values = msgUtil.assembleMsgParams("旅游一次性","中国银行","200");
		BaseResp orep = this.cmsApi.sendMail("6c7edb657a52469c9ab43fd5c3a405ba", MsgParam.mailDeductFailForOnlineBanking.toString(), values);
		BaseResp orep1 = this.cmsApi.sendMail("6c7edb657a52469c9ab43fd5c3a405ba", MsgParam.mailDeductFailForChannelNoSupportCard.toString(), values);
		BaseResp orep2 = this.cmsApi.sendMail("6c7edb657a52469c9ab43fd5c3a405ba", MsgParam.mailDeductFailForSystemNoSupportCard.toString(), values);

		BaseResp orep3 = this.cmsApi.sendMail("6c7edb657a52469c9ab43fd5c3a405ba", MsgParam.mailDeductFailForOtherReason.toString(), values);

		String values2 =msgUtil.assembleMsgParams("萝卜","旅游一次性","中国银行","200");
		BaseResp orep11 = this.cmsApi.sendMail("6c7edb657a52469c9ab43fd5c3a405ba", MsgParam.mailsonDeductFailForOnlineBanking.toString(), values2);
		BaseResp orep22 = this.cmsApi.sendMail("6c7edb657a52469c9ab43fd5c3a405ba", MsgParam.mailsonDeductFailForChannelNoSupportCard.toString(), values2);
		BaseResp orep23 = this.cmsApi.sendMail("6c7edb657a52469c9ab43fd5c3a405ba", MsgParam.mailsonDeductFailForSystemNoSupportCard.toString(), values2);

		BaseResp orep33 = this.cmsApi.sendMail("6c7edb657a52469c9ab43fd5c3a405ba", MsgParam.mailsonDeductFailForOtherReason.toString(), values2);


		
		
	}

}
