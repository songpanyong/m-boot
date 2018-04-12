package com.guohuai.mmp.platform.payment;

import com.guohuai.basic.component.ext.web.BaseResp;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PayHtmlRep extends BaseResp {
	private String retHtml;
	private String orderStatus;
	
}
