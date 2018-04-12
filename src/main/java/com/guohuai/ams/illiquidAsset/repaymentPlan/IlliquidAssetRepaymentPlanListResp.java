package com.guohuai.ams.illiquidAsset.repaymentPlan;

import java.util.List;

import org.springframework.data.domain.Page;

import com.guohuai.basic.component.ext.web.PageResp;

public class IlliquidAssetRepaymentPlanListResp extends PageResp<IlliquidAssetRepaymentPlan> {
	public IlliquidAssetRepaymentPlanListResp() {
		super();
	}

	public IlliquidAssetRepaymentPlanListResp(Page<IlliquidAssetRepaymentPlan> liquidAsset) {
		this(liquidAsset.getContent(), liquidAsset.getTotalElements());
	}

	public IlliquidAssetRepaymentPlanListResp(List<IlliquidAssetRepaymentPlan> liquidAsset) {
		this(liquidAsset, liquidAsset.size());
	}

	public IlliquidAssetRepaymentPlanListResp(List<IlliquidAssetRepaymentPlan> liquidAsset, long total) {
		this();
		super.setTotal(total);
		super.setRows(liquidAsset);
	}
}
