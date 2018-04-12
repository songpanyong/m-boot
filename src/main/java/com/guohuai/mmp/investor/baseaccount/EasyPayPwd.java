package com.guohuai.mmp.investor.baseaccount;

public enum EasyPayPwd {

	num0("000000"),num1("111111"),num2("222222"),num3("333333"),num4("444444"),num5("555555"),
	num6("666666"),num7("777777"),num8("888888"),num9("999999"),num10("123456"),num11("654321"),num12("123123");
	
	private String num;
	
	private EasyPayPwd(String num){
		this.num = num;
	}
	
	/**get方法*/
	public String getNum(){
		return num;
	}
	/** 重写toString方法  */
	public String toString(){
		return  "num = "+ num;
	}
}
