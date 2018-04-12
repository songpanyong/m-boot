package com.guohuai.ams.illiquidAsset.overdue;

import java.util.List;

import org.springframework.data.domain.Page;

import com.guohuai.basic.component.ext.web.PageResp;

public class IlliquidOverdueListResp extends PageResp<IlliquidOverdue> {
	public IlliquidOverdueListResp() {
		super();
	}

	public IlliquidOverdueListResp(Page<IlliquidOverdue> liquidAsset) {
		this(liquidAsset.getContent(), liquidAsset.getTotalElements());
	}

	public IlliquidOverdueListResp(List<IlliquidOverdue> liquidAsset) {
		this(liquidAsset, liquidAsset.size());
	}

	public IlliquidOverdueListResp(List<IlliquidOverdue> liquidAsset, long total) {
		this();
		super.setTotal(total);
		super.setRows(liquidAsset);
	}
}
