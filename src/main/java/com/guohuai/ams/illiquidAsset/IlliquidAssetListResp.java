package com.guohuai.ams.illiquidAsset;

import java.util.List;

import org.springframework.data.domain.Page;

import com.guohuai.basic.component.ext.web.PageResp;

public class IlliquidAssetListResp extends PageResp<IlliquidAsset> {
	public IlliquidAssetListResp() {
		super();
	}

	public IlliquidAssetListResp(Page<IlliquidAsset> liquidAsset) {
		this(liquidAsset.getContent(), liquidAsset.getTotalElements());
	}

	public IlliquidAssetListResp(List<IlliquidAsset> liquidAsset) {
		this(liquidAsset, liquidAsset.size());
	}

	public IlliquidAssetListResp(List<IlliquidAsset> liquidAsset, long total) {
		this();
		super.setTotal(total);
		super.setRows(liquidAsset);
	}
	
	
}
