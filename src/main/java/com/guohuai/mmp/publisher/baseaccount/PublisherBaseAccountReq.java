package com.guohuai.mmp.publisher.baseaccount;

import org.hibernate.validator.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@lombok.Data
@NoArgsConstructor
@AllArgsConstructor
public class PublisherBaseAccountReq {
	
	/**
	 * 手机号
	 */
	@NotEmpty(message = "手机号不能为空")
	private String phone;
	
	/**
	 * 所属管理员
	 */
	@NotEmpty(message = "所属管理员不能为空")
	private String[] adminInvestorOids;
	
	
	
}
