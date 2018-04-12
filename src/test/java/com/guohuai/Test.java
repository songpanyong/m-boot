package com.guohuai;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;

import com.guohuai.account.api.AccountSdk;
import com.guohuai.account.api.request.CreateUserRequest;
import com.guohuai.account.api.response.CreateUserResponse;
import com.guohuai.component.util.DateUtil;
import com.guohuai.mmp.jiajiacai.caculate.JJCUtility;
import com.guohuai.mmp.platform.accment.AccParam;

public class Test {

	public static void main(String[] args) throws IOException {
		/*
		BigDecimal a = new BigDecimal("0.00000000");
		System.out.println(a.toPlainString());
		
//		timeTest();
//		new Test().accountQueryList();
//		dataTest();
		BigDecimal b = new BigDecimal(10);
		BigDecimal aa = new BigDecimal(20);
		int result2 = aa.compareTo(b); 
		System.out.print(result2);
		*/
		testFunction();
	}
	
	private static void testFunction() {
		
		//Truncated
		BigDecimal bd = new BigDecimal(100.123456);
		
		BigDecimal ad = new BigDecimal(-0.2);
		bd = bd.subtract(ad);
//		bd.setScale(4, BigDecimal.ROUND_HALF_UP);
		System.out.print(bd.floatValue());
		System.out.print(JJCUtility.bigKeep4Decimal(bd));
		
		/*
		String father = "512528199501011234";
		String child = "512528201412301234";
		boolean result = DateUtil.yearsByValve(father, child, 20);
		
		father = "512528199501011234";
		child = "512528201512301234";
		boolean result2 = DateUtil.yearsByValve(father, child, 20);
		
		father = "512528199512311234";
		child = "512528201512301234";
		boolean result3 = DateUtil.yearsByValve(father, child, 20);
		*/
	}
	public void accountQueryList() {
			// UID2016112200000007
//		UserQueryRequest req = new UserQueryRequest();
//		req.setSystemSource(AccParam.SystemSource.MIMOSA.toString());
//		req.setUserType(AccParam.UserType.PLATFORM.toString());
//		req.setSystemUid(null);
//		UserListResponse	orep = new AccountSdk("http://115.28.58.108:80").userQueryList(req);
//		/**
//		 * {"errorCode":0,"errorMessage":null,"total":1,"rows":[{"returnCode":null,"errorMess
//age":null,"oid":"402880ec588a9fb001588aa876a40001","userType":"T3","systemUid":null,"userOid":"UID2016112200000007","systemSource":"mimosa","createTim
//e":"2016-11-22 14:10:46"}]}
//		 */
//		orep.getRows().get(0).getUserOid();
//			System.out.println(orep);
		//UID2016112200000007
//		AccountQueryRequest req = new AccountQueryRequest();
//		req.setUserOid("UID2016112200000007");
//		req.setUserType(AccParam.UserType.PLATFORM.toString());
//		req.setAccountType(AccParam.AccountType.SUPERACCOUNT.toString());
//		AccountListResponse resp = new AccountSdk("http://115.28.58.108:80").accountQueryList(req);
		
		CreateUserRequest oreq = new CreateUserRequest();
		oreq.setSystemUid("111");
		oreq.setRemark("创建发行人");
		oreq.setUserType(AccParam.UserType.SPV.toString());
		oreq.setSystemSource(AccParam.SystemSource.MIMOSA.toString());
		
		try {
			
			CreateUserResponse orep = new AccountSdk("http://115.28.58.108:80").addUser(oreq);
		} catch (Exception e) {
			
		}
		
	}
	
	static void  timeTest() {
		Date day = DateUtil.addDays(DateUtil.parse("2017-2-28"), DateUtil.diffDays4Months(1));
		Date day2 = DateUtil.addDays(DateUtil.parse("2017-1-29"), 30);
		Date day3 = DateUtil.addDays(DateUtil.parse("2017-1-31"), DateUtil.diffDays4Months(1));
		System.out.print(day);
		System.out.print(day2);
		System.out.print(day3);
	}
	
	static void dataTest() {  
		
		BigDecimal big1 = JJCUtility.bigKeep2Decimal(new BigDecimal(100.7123));
			
		BigDecimal big2 = JJCUtility.bigKeep2Decimal(new BigDecimal(100.0023));
		
		BigDecimal big3 = JJCUtility.bigKeep2Decimal(new BigDecimal(100.7023));
		
		BigDecimal big4 = JJCUtility.bigKeep2Decimal(new BigDecimal(100.0123));
		
		System.out.print(big1);
		System.out.print(big2);
		System.out.print(big3);
		System.out.print(big4);
	}
	

}
