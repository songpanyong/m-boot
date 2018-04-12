package com.guohuai.mmp.platform.accment;

import com.guohuai.basic.component.ext.web.BaseResp;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class UserBindCardApplyRep extends BaseResp {
	/**
	 * 绑卡申请 响应
	cardOrderId		String	绑卡返回的编号,可空
	 */
	
	private String cardOrderId;
}
