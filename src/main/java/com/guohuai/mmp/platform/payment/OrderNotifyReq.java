package com.guohuai.mmp.platform.payment;

@lombok.Data
public class OrderNotifyReq {
	private String returnCode;
	private String iPayNo;
	private  String errorMessage;
	
	private String orderCode;
}
