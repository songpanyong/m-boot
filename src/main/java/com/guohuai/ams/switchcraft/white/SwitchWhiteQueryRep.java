package com.guohuai.ams.switchcraft.white;

import java.sql.Timestamp;

import com.guohuai.basic.component.ext.web.PageResp;

import lombok.EqualsAndHashCode;

@lombok.Data
@EqualsAndHashCode(callSuper = false)
@lombok.Builder
public class SwitchWhiteQueryRep{

	private String oid, userAcc, operator, note;	
	
	private Timestamp createTime, updateTime;
}
