package com.guohuai.ams.liquidAsset;

import java.util.List;

import org.springframework.data.domain.Page;

import com.guohuai.basic.component.ext.web.PageResp;

public class LiquidAssetListResp extends PageResp<LiquidAsset> {
	public LiquidAssetListResp() {
		super();
	}

	public LiquidAssetListResp(Page<LiquidAsset> liquidAsset) {
		this(liquidAsset.getContent(), liquidAsset.getTotalElements());
	}

	public LiquidAssetListResp(List<LiquidAsset> liquidAsset) {
		this(liquidAsset, liquidAsset.size());
	}

	public LiquidAssetListResp(List<LiquidAsset> liquidAsset, long total) {
		this();
		super.setTotal(total);
		super.setRows(liquidAsset);
	}
}
