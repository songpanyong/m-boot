package com.guohuai.mmp.ope.failrecharge;

import com.guohuai.basic.component.ext.web.BaseResp;

import lombok.Data;
import lombok.EqualsAndHashCode;
@Data
@EqualsAndHashCode(callSuper=false)
public class FailRechargeResp extends BaseResp{

	public FailRechargeResp(FailRecharge entity){
		super();
		this.data = entity;
	}
	
	private FailRecharge data;
}
