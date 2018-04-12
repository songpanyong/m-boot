package com.guohuai.mmp.publisher.baseaccount;

import com.guohuai.basic.component.ext.web.BaseResp;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class BaseAccountAddRep extends BaseResp {
	private String baseAccountOid;
}
