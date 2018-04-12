package com.guohuai.mmp.ope.failcard;

import com.guohuai.basic.component.ext.web.BaseResp;

import lombok.Data;
import lombok.EqualsAndHashCode;
@Data
@EqualsAndHashCode(callSuper=false)
public class FailCardResp extends BaseResp{

	public FailCardResp(FailCard entity){
		super();
		this.data = entity;
	}
	
	private FailCard data;
}
