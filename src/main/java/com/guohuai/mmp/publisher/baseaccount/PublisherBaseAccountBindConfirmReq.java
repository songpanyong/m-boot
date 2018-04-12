package com.guohuai.mmp.publisher.baseaccount;

import org.hibernate.validator.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@lombok.Data
@NoArgsConstructor
@AllArgsConstructor
public class PublisherBaseAccountBindConfirmReq {
	
	@NotEmpty(message = "发行人OID不能为空")
	private String baseAccountOid;
	
	private String cardOrderId;
	
	@NotEmpty(message = "短信码不能为空")
	private String smsCode;
	
	
	
	
}
