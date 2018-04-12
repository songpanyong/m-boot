package com.guohuai.mmp.ope.norecharge;

import com.guohuai.basic.component.ext.web.BaseResp;

import lombok.Data;
import lombok.EqualsAndHashCode;
@Data
@EqualsAndHashCode(callSuper=false)
public class NoRechargeResp extends BaseResp{

	public NoRechargeResp(NoRecharge entity){
		super();
		this.data = entity;
	}
	
	private NoRecharge data;
}
