package com.guohuai.mmp.publisher.baseaccount;

import org.hibernate.validator.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@lombok.Data
@NoArgsConstructor
@AllArgsConstructor
public class PublisherBaseAccountBindApplyReq {
	
	@NotEmpty(message = "发行人OID不能为空")
	private String baseAccountOid;
	/**
	 * 姓名
	 */
	@NotEmpty(message = "姓名不能为空")
	private String realName;
	
	/**
	 * 证件号
	 */
	@NotEmpty(message = "证件号不能为空")
	private String certificateNo;
	
	/**
	 * 银行名称
	 */
	@NotEmpty(message = "银行名称不能为空")
	private String bankName;
	
	/**
	 * 银行账号
	 */
	@NotEmpty(message = "银行账号不能为空")
	private String cardNo;
	
	
	
	
}
