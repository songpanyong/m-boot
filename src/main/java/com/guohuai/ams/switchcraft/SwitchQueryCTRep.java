package com.guohuai.ams.switchcraft;


import com.guohuai.basic.component.ext.web.BaseResp;

import lombok.EqualsAndHashCode;

@lombok.Data
@EqualsAndHashCode(callSuper = false)
@lombok.Builder
public class SwitchQueryCTRep extends BaseResp {

	private String code, status, type, content;
}
