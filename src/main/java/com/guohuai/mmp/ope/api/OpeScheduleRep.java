package com.guohuai.mmp.ope.api;

import com.guohuai.basic.component.ext.web.BaseResp;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class OpeScheduleRep extends BaseResp{
		private String userOid;
		private String phone;
		private String source;
		private String name;
		private Long createTime;
}
