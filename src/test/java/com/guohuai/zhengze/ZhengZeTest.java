package com.guohuai.zhengze;

import static org.junit.Assert.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.alibaba.druid.sql.visitor.functions.Char;
import com.guohuai.ApplicationBootstrap;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=ApplicationBootstrap.class)
public class ZhengZeTest {

	@Test
	public void test() {
		
		String a = "113355";
		//正则规则---不能包含3位及以上相同字符的重复
		String rule = "^.*(.)\\1{2,}+.*$";
		//正则规则--不能包含2位及以上字符组合的重复
		
		String rule2 = "^.*(.{2})(.*)\\1+.*$";
		//正则规则---数字全是个或者是两个以上重复的
		String rule3 = "^(?:(\\d)\\1)+$";
		
		Pattern pattern3 = Pattern.compile(rule3);
		Matcher matcher3 = pattern3.matcher(a);
		boolean flag3 = !matcher3.matches();
		//System.out.println(flag3);
		
		//将正则规则进行编译
		Pattern pattern = Pattern.compile(rule);
		Matcher matcher = pattern.matcher(a);
		boolean flag = !matcher.matches();
		//System.out.println(flag);
		Pattern pattern2 = Pattern.compile(rule2);
		Matcher matcher2 = pattern2.matcher(a);
		boolean flag2 = !matcher2.matches();
		//System.out.println(flag2);
		
		//System.out.println(this.ruled(a));;
		//System.out.println(flag &&flag2 &&this.ruled(a));

	}
	
	//正则规则--不能包含3位及以上的正序或者逆序的连续字符
			public boolean ruled(String s){
				char[] cc = s.toCharArray();
				for(int i=0;i<cc.length-2;i++){
					if(Math.abs(cc[i] - cc[i+1]) == 1 && (cc[i] - cc[i+1]) == (cc[i+1] - cc[i+2])){
						return false;
					}
			}
				return true;
			
			}

}
