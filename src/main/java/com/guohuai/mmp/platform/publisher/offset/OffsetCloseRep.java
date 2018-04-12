package com.guohuai.mmp.platform.publisher.offset;

import com.guohuai.basic.component.ext.web.BaseResp;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@lombok.Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class OffsetCloseRep extends BaseResp {
	private String retHtml;
	private String offsetOid;
	private String type;
}
