package com.guohuai.mmp.ope.nobuy;


import java.util.List;

import org.springframework.data.domain.Page;

import com.guohuai.basic.component.ext.web.PageResp;

public class NoBuyListResp extends PageResp<NoBuy> {
	public NoBuyListResp() {
		super();
	}

	public NoBuyListResp(Page<NoBuy> liquidAsset) {
		this(liquidAsset.getContent(), liquidAsset.getTotalElements());
	}

	public NoBuyListResp(List<NoBuy> liquidAsset) {
		this(liquidAsset, liquidAsset.size());
	}

	public NoBuyListResp(List<NoBuy> liquidAsset, long total) {
		this();
		super.setTotal(total);
		super.setRows(liquidAsset);
	}
}
