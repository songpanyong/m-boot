package com.guohuai;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.alibaba.fastjson.JSON;
import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.component.util.DateUtil;
import com.guohuai.mmp.investor.sonaccount.RegistSonAccountReq;
import com.guohuai.mmp.investor.sonaccount.SonAccountService;
import com.guohuai.mmp.investor.tradeorder.RegistFile;
import com.guohuai.mmp.jiajiacai.ebaoquan.EbaoquanRecord;
import com.guohuai.mmp.jiajiacai.ebaoquan.EbaoquanRecordDao;
import com.guohuai.mmp.jiajiacai.ebaoquan.EbaoquanRecordService;
import com.guohuai.mmp.jiajiacai.wishplan.plan.dao.PlanProductDao;
import com.guohuai.mmp.sms.SMSUtils;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=ApplicationBootstrap.class)
public class IdentifyTest {

	@Autowired
	private SonAccountService sonAccountService;
	@Autowired
	private PlanProductDao planProductDao;
	@Autowired
	private EbaoquanRecordService recordService;
	@Autowired
	private EbaoquanRecordDao recordDao;
	
	@Test
	public void test() {
		
		
		RegistSonAccountReq  req = new RegistSonAccountReq();
		req.setRealName("王日武");
		req.setIdCard("140221199107105318");
		System.out.println("req = "+ req);
		
		//Boolean flag = this.sonAccountService.checkCardId(req);
		//assertEquals(true, flag);
		//System.out.println("Boolean = "+ flag);
		
		/*List<Object[]> monthList = this.planProductDao.calcuCompleteMonthTotalIncome("60421e8558de42d09e7a819550fe3623");
		System.out.println(monthList.size());
		boolean flags = (!(monthList.size()==1&&"[null]".equals(monthList.get(0))))&&(monthList.size()!=0);
		System.out.println(flags);
		System.out.println("[null]".equals(monthList.get(0)));
		System.out.println(monthList.get(0));*/
		EbaoquanRecord e =  this.recordDao.findByCodeId("GENDXWT97126620180209133030");
		
		RegistFile registFile = new RegistFile();
		registFile.setConstractNum(e.getCodeId());
		registFile.setFirstParty("aaa");
		registFile.setSecondParty("bbb");
		registFile.setTradeTime(DateUtil.currentTime());
		String jsonParam = JSON.toJSONString(registFile);
		e.setFileParam(jsonParam);
		//recordService.save(e);
	}

}
