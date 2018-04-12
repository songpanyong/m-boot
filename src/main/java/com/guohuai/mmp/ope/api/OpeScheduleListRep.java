package com.guohuai.mmp.ope.api;

import java.util.List;

import com.guohuai.basic.component.ext.web.BaseResp;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class OpeScheduleListRep extends BaseResp{
	  
	  private List<OpeScheduleRep> list;
}
