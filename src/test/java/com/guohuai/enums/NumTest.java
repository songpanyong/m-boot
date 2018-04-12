package com.guohuai.enums;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.guohuai.ApplicationBootstrap;
import com.guohuai.mmp.investor.baseaccount.EasyPayPwd;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = ApplicationBootstrap.class)
public class NumTest {

	@Test
	public void test() {
		//System.out.println(Num.test);
		//System.out.println("a");
		/*System.out.println(Num.num1);
		System.out.println(Num.values());
		System.out.println(Num.values().length);
		for(Num e : Num.values()){
			System.out.println(e.getName());
		}*/
		/*for(EasyPayPwd e : EasyPayPwd.values()){
			System.out.println(e.getNum());
		}*/
		/*for(SmsType  s:SmsType.values()){
			System.out.println(s);
		}*/
		String smsType="forgetpaypwd";
		boolean b = smsType.equals(SmsType.forgetlogin)||smsType.equals(SmsType.forgetpaypwd)||smsType.equals(SmsType.regist);
			System.out.println(b);
		
	}

}
