package com.guohuai.mmp.ope.norecharge;


import java.util.List;

import org.springframework.data.domain.Page;

import com.guohuai.basic.component.ext.web.PageResp;

public class NoRechargeListResp extends PageResp<NoRecharge> {
	public NoRechargeListResp() {
		super();
	}

	public NoRechargeListResp(Page<NoRecharge> liquidAsset) {
		this(liquidAsset.getContent(), liquidAsset.getTotalElements());
	}

	public NoRechargeListResp(List<NoRecharge> liquidAsset) {
		this(liquidAsset, liquidAsset.size());
	}

	public NoRechargeListResp(List<NoRecharge> liquidAsset, long total) {
		this();
		super.setTotal(total);
		super.setRows(liquidAsset);
	}
}
