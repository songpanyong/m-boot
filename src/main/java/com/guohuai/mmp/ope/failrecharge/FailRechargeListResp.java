package com.guohuai.mmp.ope.failrecharge;


import java.util.List;

import org.springframework.data.domain.Page;

import com.guohuai.basic.component.ext.web.PageResp;

public class FailRechargeListResp extends PageResp<FailRecharge> {
	public FailRechargeListResp() {
		super();
	}

	public FailRechargeListResp(Page<FailRecharge> liquidAsset) {
		this(liquidAsset.getContent(), liquidAsset.getTotalElements());
	}

	public FailRechargeListResp(List<FailRecharge> liquidAsset) {
		this(liquidAsset, liquidAsset.size());
	}

	public FailRechargeListResp(List<FailRecharge> liquidAsset, long total) {
		this();
		super.setTotal(total);
		super.setRows(liquidAsset);
	}
}
