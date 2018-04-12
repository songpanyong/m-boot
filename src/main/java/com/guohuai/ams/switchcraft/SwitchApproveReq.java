package com.guohuai.ams.switchcraft;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import com.guohuai.basic.component.ext.web.parameter.validation.Enumerations;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@lombok.Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class SwitchApproveReq {
	
	@NotNull(message = "系统开关不能为空！")
	private String oid;
	
	@NotNull(message = "审核结果不能为空！")	
	@Enumerations(values = {"pass","refused"}, message = "审核结果参数有误！")
	private String approveStatus;
	
	@NotNull(message = "审核意见不能为空！")
	@Length(max = 250, message = "审核意见长度不能超过250（包含）！")
	private String remark;
}
