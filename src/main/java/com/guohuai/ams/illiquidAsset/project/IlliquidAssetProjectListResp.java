package com.guohuai.ams.illiquidAsset.project;

import java.util.List;

import org.springframework.data.domain.Page;

import com.guohuai.basic.component.ext.web.PageResp;

public class IlliquidAssetProjectListResp extends PageResp<IlliquidAssetProject> {
	public IlliquidAssetProjectListResp() {
		super();
	}

	public IlliquidAssetProjectListResp(Page<IlliquidAssetProject> liquidAsset) {
		this(liquidAsset.getContent(), liquidAsset.getTotalElements());
	}

	public IlliquidAssetProjectListResp(List<IlliquidAssetProject> liquidAsset) {
		this(liquidAsset, liquidAsset.size());
	}

	public IlliquidAssetProjectListResp(List<IlliquidAssetProject> liquidAsset, long total) {
		this();
		super.setTotal(total);
		super.setRows(liquidAsset);
	}
}
