package com.guohuai.mmp.ope.nobuy;

import com.guohuai.basic.component.ext.web.BaseResp;

import lombok.Data;
import lombok.EqualsAndHashCode;
@Data
@EqualsAndHashCode(callSuper=false)
public class NoBuyResp extends BaseResp{

	public NoBuyResp(NoBuy entity){
		super();
		this.data = entity;
	}
	
	private NoBuy data;
}
