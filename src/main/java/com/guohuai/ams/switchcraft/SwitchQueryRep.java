package com.guohuai.ams.switchcraft;

import java.sql.Timestamp;

import com.guohuai.basic.component.ext.web.PageResp;

import lombok.EqualsAndHashCode;

@lombok.Data
@EqualsAndHashCode(callSuper = false)
@lombok.Builder
public class SwitchQueryRep{

	private String oid, code, name, status, whiteStatus, requester, approver, approveRemark, type, content;	
	
	private Timestamp  createTime, updateTime;
}
