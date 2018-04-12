package com.guohuai.ams.illiquidAsset;

import com.guohuai.basic.component.ext.web.BaseResp;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class IlliquidAssetResp extends BaseResp {

	public IlliquidAssetResp(IlliquidAsset liquidAsset) {
		super();
		this.data = liquidAsset;
	}

	private IlliquidAsset data;
}
