package com.guohuai.mmp.ope.nocard;

import com.guohuai.basic.component.ext.web.BaseResp;

import lombok.Data;
import lombok.EqualsAndHashCode;
@Data
@EqualsAndHashCode(callSuper=false)
public class NoCardResp extends BaseResp{

	public NoCardResp(NoCard entity){
		super();
		this.data = entity;
	}
	
	private NoCard data;
}
