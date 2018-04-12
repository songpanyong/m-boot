package com.guohuai.ams.liquidAsset.yield;
import java.util.List;

import org.springframework.data.domain.Page;

import com.guohuai.basic.component.ext.web.PageResp;

public class LiquidAssetYieldListResp extends PageResp<LiquidAssetYield>{
	public LiquidAssetYieldListResp() {
		super();
	}

	public LiquidAssetYieldListResp(Page<LiquidAssetYield> page) {
		this(page.getContent(), page.getTotalElements());
	}

	public LiquidAssetYieldListResp(List<LiquidAssetYield> list) {
		this(list, list.size());
	}

	public LiquidAssetYieldListResp(List<LiquidAssetYield> list, long total) {
		this();
		super.setTotal(total);
		super.setRows(list);
	}
}
