package com.guohuai.ams.switchcraft;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import com.guohuai.basic.component.ext.web.parameter.validation.Enumerations;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@lombok.Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class SwitchAddReq {
	
	private String oid;
	
	@NotNull(message = "编码不能为空！")
	@Length(max = 30, message = "编码长度不能超过30（包含）！")
	private String code;
	
	@NotNull(message = "名称不能为空！")
	@Length(max = 30, message = "名称长度不能超过30（包含）！")
	private String name;
	
	@NotNull(message = "类型不能为空！")
	@Enumerations(values = {"switch","configure"}, message = "类型参数有误！")
	private String type;
	
	@NotNull(message = "内容不能为空！")
	@Length(max = 250, message = "内容不能超过250（包含）！")
	private String content;
}
