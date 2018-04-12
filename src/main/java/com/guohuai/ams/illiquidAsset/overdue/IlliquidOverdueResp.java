package com.guohuai.ams.illiquidAsset.overdue;

import com.guohuai.basic.component.ext.web.BaseResp;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class IlliquidOverdueResp extends BaseResp {

	public IlliquidOverdueResp(IlliquidOverdue liquidAsset) {
		super();
		this.data = liquidAsset;
	}

	private IlliquidOverdue data;
}
