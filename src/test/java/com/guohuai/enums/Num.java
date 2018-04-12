package com.guohuai.enums;


public enum Num {
	//test
	num1("000000"),
	num2("111111"),
	num3("222222"),
	num4("333333"),
	num5("444444"),
	num6("555555"),
	num7("666666"),
	num8("7777777"),
	num9("111111"),
	num10("111111"),
	num11("111111"),
	num12("111111"),
	num13("111111");
	
	private String name;
	
	private Num(String name){
		this.name = name;
	}
	
	/**生成set、get方法*/
	
	public String getName(){
		
		return name;
	}
	

	
}
