package com.guohuai.ams.switchcraft;

import javax.validation.constraints.NotNull;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@lombok.Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class SwitchAddWhiteReq {
	
	
	@NotNull(message = "系统级开关不能为空！")
	private String oid;
	
	@NotNull(message = "手机号不能为空！")
	private String phone;
	
	private String note;
}
