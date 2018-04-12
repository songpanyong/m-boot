package com.guohuai.mmp.ope.nocard;


import java.util.List;

import org.springframework.data.domain.Page;

import com.guohuai.basic.component.ext.web.PageResp;

public class NoCardListResp extends PageResp<NoCard> {
	public NoCardListResp() {
		super();
	}

	public NoCardListResp(Page<NoCard> liquidAsset) {
		this(liquidAsset.getContent(), liquidAsset.getTotalElements());
	}

	public NoCardListResp(List<NoCard> liquidAsset) {
		this(liquidAsset, liquidAsset.size());
	}

	public NoCardListResp(List<NoCard> liquidAsset, long total) {
		this();
		super.setTotal(total);
		super.setRows(liquidAsset);
	}
}
