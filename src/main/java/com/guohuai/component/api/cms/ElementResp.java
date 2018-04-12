package com.guohuai.component.api.cms;

import com.guohuai.basic.component.ext.web.BaseResp;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
public class ElementResp extends BaseResp{

	public ElementResp(ElementEntity entity){
		super();
		this.data = entity;
	}
	
	private ElementEntity data;
}
