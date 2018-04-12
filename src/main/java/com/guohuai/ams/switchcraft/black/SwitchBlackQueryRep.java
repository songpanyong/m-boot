package com.guohuai.ams.switchcraft.black;

import java.sql.Timestamp;

import com.guohuai.basic.component.ext.web.PageResp;

import lombok.EqualsAndHashCode;

@lombok.Data
@EqualsAndHashCode(callSuper = false)
@lombok.Builder
public class SwitchBlackQueryRep{

	private String oid, userAcc, operator, note;	
	
	private Timestamp createTime, updateTime;
}
