package com.guohuai.mmp.investor.tradeorder;

import com.guohuai.basic.component.ext.web.BaseResp;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@lombok.Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@lombok.Builder
@AllArgsConstructor
public class RefuseRep extends BaseResp {
	boolean success = true;
	
}
