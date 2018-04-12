package com.guohuai.mmp.sms;

import com.guohuai.basic.component.ext.web.BaseResp;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
@lombok.Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class SMSVeriCodeRep extends BaseResp {

	String veriCode;
}
