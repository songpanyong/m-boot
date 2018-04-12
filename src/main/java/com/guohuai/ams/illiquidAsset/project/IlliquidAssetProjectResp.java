package com.guohuai.ams.illiquidAsset.project;

import com.guohuai.basic.component.ext.web.BaseResp;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class IlliquidAssetProjectResp extends BaseResp {

	public IlliquidAssetProjectResp(IlliquidAssetProject liquidAsset) {
		super();
		this.data = liquidAsset;
	}

	private IlliquidAssetProject data;
}
