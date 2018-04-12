package com.guohuai.mmp.investor.orderlog;

import lombok.Data;

@Data
public class OrderLogReq {
	
	private int errorCode;
	private String errorMessage;
	private String orderCode;
	private String orderStatus;
	
	private String orderType;
}
