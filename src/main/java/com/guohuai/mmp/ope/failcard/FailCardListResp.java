package com.guohuai.mmp.ope.failcard;


import java.util.List;

import org.springframework.data.domain.Page;

import com.guohuai.basic.component.ext.web.PageResp;

public class FailCardListResp extends PageResp<FailCard> {
	public FailCardListResp() {
		super();
	}

	public FailCardListResp(Page<FailCard> liquidAsset) {
		this(liquidAsset.getContent(), liquidAsset.getTotalElements());
	}

	public FailCardListResp(List<FailCard> liquidAsset) {
		this(liquidAsset, liquidAsset.size());
	}

	public FailCardListResp(List<FailCard> liquidAsset, long total) {
		this();
		super.setTotal(total);
		super.setRows(liquidAsset);
	}
}
