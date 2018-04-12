package com.guohuai.mmp.investor.sonaccount;

import java.io.Serializable;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@lombok.Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class RegistSonAccountReq implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**账户id*/
	private String id;

	@NotBlank(message = "姓名不能为空！")
	@Length(min = 2, max = 15, message = "姓名位数不对！")
	private String realName;

	@NotBlank(message = "身份证号不能为空！")
	@Length(min = 18, max = 18, message = "身份证号位数不对！")
	private String idCard; 
	
}
